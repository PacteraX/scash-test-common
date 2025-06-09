package db.rdb.dbcp.delegation;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executor;

import db.DBErrorCodes;
import db.LogConstants;
import db.rdb.DuplicateKeyException;
import db.rdb.dbcp.DBCP;
import db.rdb.dbcp.IPool;
import db.rdb.dbcp.IPool.CONN_SHUTDOWN_EVENT;
import db.rdb.dbcp.IConnection;
import db.rdb.dbcp.IScheduleManager.SCHEDULER_TYPE;
import db.rdb.dbcp.IScheduler;
import db.rdb.dbcp.schedule.tasks.QueryCancelTimeoutChecker;
import db.rdb.dbcp.schedule.tasks.UpdateCancelTimeoutChecker;
import db.rdb.dbcp.schedule.tasks.ValidationQueryTimeoutChecker;
import db.rdb.tx.IPreparedStatement;
import system.core.UnchkedExecption;

/**
 * AtomicConnection、LazyConnectionとDelegationConnectionまとめて、
 * コネクション一本化作成、Connection作成
 *
 * @author cfw-so
 */
public class Connection extends AbstractConnection {
    protected final IPool pool;
    private final Random random = new Random();
    private String groupName;
    public final java.sql.Connection connection;
    protected final String validationQuery;
    long queryTimeout;
    long commitTimeout;
    long cancelTimeout;
    long rollbackTimeout;
    long validationQueryTimeout;
    private long rollbackSleep;
    private long createtime;
    private long originalCreateTime;
    private boolean isInsidePool = false;
    private boolean isScashClosed = false;
    protected SQLException error;
    protected RollbackTimeoutChecker timeoutRollbackChecker;
    protected CommitTimeoutChecker timeoutCommitChecker;
    protected QueryCancelTimeoutChecker timeoutQChecker;
    protected UpdateCancelTimeoutChecker timeoutUChecker;
    protected ValidationQueryTimeoutChecker timeoutVQChecker;
    protected IScheduler scheduler = DBCP.getSchedulemgr().getScheduler(SCHEDULER_TYPE.SQL_TIMEOUT_CHKER);
    protected List<Object> lobs = new ArrayList<>();
    public enum CONTINUE_STATE {
        SUCCESS_CONTINUE_OK, FAILURE_CONTINUE_OK, FAILURE_CONTINUE_NG,NULL
    }

    protected Connection(final IPool ipool, final java.sql.Connection iconnection) throws SQLException {
        this.pool = ipool;
        this.connection = iconnection;
        this.groupName = ipool.getGroupName();

        this.connection.setAutoCommit(false);
        this.connection.setTransactionIsolation(java.sql.Connection.TRANSACTION_READ_COMMITTED);

        this.createtime = System.currentTimeMillis() + pool.getSource().getWatchInterval() * random.nextInt(pool.getSource().getCreateTimeMultiple());
        this.originalCreateTime = System.currentTimeMillis();
        this.queryTimeout = pool.getSource().getQueryTimeout();
        this.commitTimeout = pool.getSource().getCommitTimeout();
        this.cancelTimeout = pool.getSource().getCancelTimeout();
        this.rollbackTimeout = pool.getSource().getRollbackTimeout();
        this.validationQueryTimeout = pool.getSource().getValidationQueryTimeout();
        this.rollbackSleep = pool.getSource().getRollbackSleep();
        this.validationQuery = this.pool.getSource().getValidationQuery();
    }
    public IPool getPool() {
        return pool;
    }
    // ----------- Implementing Connection interface ------------- //
    @Override
    public final boolean isValid(final int iTimeout) throws SQLException {
        return this.connection.isValid(iTimeout);
    }

    @Override
    public void setAutoCommit(boolean flag) throws SQLException {
        this.connection.setAutoCommit(flag);
    }

    @Override
    public PreparedStatement prepareStatement(final String sql) throws SQLException {
        return new ScashPreparedStatement(this.connection, sql);
    }

    public void setRollbackTimeoutSchedule() {
        this.timeoutRollbackChecker = new RollbackTimeoutChecker(this);
        this.scheduler.addTask(this.timeoutRollbackChecker, this.rollbackTimeout);
    }
    public void setCommitTimeoutSchedule() {
        this.timeoutCommitChecker = new CommitTimeoutChecker(this);
        this.scheduler.addTask(this.timeoutCommitChecker, this.commitTimeout);
    }
    public void setUpdateCancelTimeoutSchedule() {
        this.timeoutUChecker = new UpdateCancelTimeoutChecker();
        this.scheduler.addTask(this.timeoutUChecker, this.cancelTimeout);
    }
    public void setQueryCancelTimeoutSchedule() {
        this.timeoutQChecker = new QueryCancelTimeoutChecker();
        this.scheduler.addTask(this.timeoutQChecker, this.cancelTimeout);
    }

    public void setValidationQueryTimeoutSchedule() {
        this.timeoutVQChecker = new ValidationQueryTimeoutChecker(this);
        this.scheduler.addTask(this.timeoutVQChecker, this.validationQueryTimeout);
    }

    @Override
    public void commit() throws SQLException {
        setCommitTimeoutSchedule();
        try {
            this.connection.commit();
        } finally {
            if (this.timeoutCommitChecker != null) {
                checkCommitError();
            }
        }
    }

    @Override
    public void rollback() throws SQLException {
        SQLException sqlEx = null;
        while (true) {
            ContinueState rollbackState = _rollback();
            if (rollbackState.getState() == CONTINUE_STATE.SUCCESS_CONTINUE_OK) {
                break;
            }else if(rollbackState.getState() == CONTINUE_STATE.FAILURE_CONTINUE_OK){
                sqlEx = rollbackState.getSqlEx();
                break;
            }
            try {
                Thread.sleep(this.rollbackSleep);
            } catch (InterruptedException e) {
            }
        }
        if (sqlEx!=null) {
            throw sqlEx;
        }
    }

    public ContinueState _rollback() {
        ContinueState continueState = new ContinueState();
        setRollbackTimeoutSchedule();
        try {
            this.connection.rollback();
            continueState.setState(CONTINUE_STATE.SUCCESS_CONTINUE_OK);
        } catch (SQLException ex1) {
            continueState.setSqlEx(ex1);
            continueState.setState(CONTINUE_STATE.FAILURE_CONTINUE_OK);
            if (!checkAndshutdown()) {
                continueState.setState(CONTINUE_STATE.FAILURE_CONTINUE_NG);
            }
        } finally {
            if (this.timeoutRollbackChecker != null) {
                this.scheduler.discardTask(this.timeoutRollbackChecker);
                this.error = this.timeoutRollbackChecker.getError();
                if (this.error != null) {
                    continueState.setState(CONTINUE_STATE.FAILURE_CONTINUE_OK);
                    continueState.setSqlEx(this.error);
                }
            }
        }
        return continueState;
    }

    protected void connectionCloseCheck() {
        if(isScashClosed())
            return;

        boolean closed = false;
        try{
            closed = isClosed();
        } catch (SQLException ex) {
            closed = true;
        } finally {
            if(closed){
                checkAndshutdown();
            }
        }
    }

    /*
     * 各DB毎にこのクラスを継承したクラス内で定義する。
     * Oracle：ORCLConnection
     * PostgreSQL：PSQLConnection
     */
    @Override
    public synchronized void close() throws SQLException {
    }

    /*
     * 各DB毎にこのクラスを継承したクラス内で定義する。
     * Oracle：ORCLConnection
     * PostgreSQL：PSQLConnection
     */
    public void processConfirmConnectionCondition(java.sql.Connection conn) {
    }

    @Override
    public final boolean isClosed() throws SQLException {
        return this.connection.isClosed();
    }

    @Override
    public final boolean isScashClosed() {
        return isScashClosed;
    }

    @Override
    public final void insidePool() {
        isInsidePool = true;
    }

    @Override
    public final void outsidePool() {
        isInsidePool = false;
    }

    @Override
    public final boolean isInside() {
        return isInsidePool == true;
    }

    @Override
    public void shutdown() throws SQLException {
        finalizeC();
    }

    @Override
    public synchronized boolean checkAndshutdown(CONN_SHUTDOWN_EVENT event) {

        if(isScashClosed())
            return true;
        boolean canClose = false;
        int ownConnSize = -1;

        synchronized(this.pool){
            ownConnSize = this.pool.getPoolCount().get();
            // 最低限接続数以外の場合
            if (ownConnSize > this.pool.getSource().getMinimumPoolSize()) {
                ownConnSize = this.pool.getPoolCount().decrementAndGet();
                canClose = true;
            }
        }
        if(canClose){
            // クローズ処理
            try {
                shutdown();
            } catch (SQLException ex) {
            }
        }
        return canClose;
    }

    @Override
    public synchronized boolean checkAndshutdown() {
        return checkAndshutdown(CONN_SHUTDOWN_EVENT.APL);
    }

    protected void finalizeC() throws SQLException {
        try {
            if (!isClosed()) {
                this.connection.close();
            }
        } finally {
            isScashClosed = true;
        }
    }

    @Override
    public final DatabaseMetaData getMetaData() throws SQLException {
        return this.connection.getMetaData();
    }

    @Override
    public final String getDbName() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.pool.getDbName());
        sb.append(",");
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    @Override
    public final String getGroupName() {
        return this.pool.getGroupName();
    }


    @Override
    public long getCreateTime() {
        return this.createtime;
    }

    public void checkRollbackError() throws SQLException {
        if (this.timeoutRollbackChecker != null) {
            this.scheduler.discardTask(this.timeoutRollbackChecker);
            this.error = this.timeoutRollbackChecker.getError();
            if (this.error != null) {
                throw error;
            }
        }
    }

    public void checkCommitError() throws SQLException {
        if (this.timeoutCommitChecker != null) {
            this.scheduler.discardTask(this.timeoutCommitChecker);
            this.error = this.timeoutCommitChecker.getError();
            if (this.error != null) {
                throw error;
            }
        }
    }

    public void checkQueryCancelError() throws SQLException {
        if (this.timeoutQChecker != null) {
            this.scheduler.discardTask(this.timeoutQChecker);
            this.error = this.timeoutQChecker.getError();
            if (this.error != null) {
                throw error;
            }
        }
    }

    public void checkUpdateCancelError() throws SQLException {
        if (this.timeoutUChecker != null) {
            this.scheduler.discardTask(this.timeoutUChecker);
            this.error = this.timeoutUChecker.getError();
            if (this.error != null) {
                throw error;
            }
        }
    }

    public void checkValidationQueryError() throws SQLException {
        if (this.timeoutVQChecker != null) {
            this.scheduler.discardTask(this.timeoutVQChecker);
            this.error = this.timeoutVQChecker.getError();
            if (this.error != null) {
                throw error;
            }
        }
    }

    public static class ContinueState {
        private CONTINUE_STATE state = CONTINUE_STATE.NULL;
        private SQLException sqlEx = null;

        public CONTINUE_STATE getState() {
            return state;
        }

        public void setState(CONTINUE_STATE state) {
            this.state = state;
        }

        public SQLException getSqlEx() {
            return sqlEx;
        }

        public void setSqlEx(SQLException sqlEx) {
            this.sqlEx = sqlEx;
        }
    }

    public class ScashPreparedStatement extends AbstractPreparedStatement implements IPreparedStatement {
        private IScheduler schdr = DBCP.getSchedulemgr().getScheduler(SCHEDULER_TYPE.CONNECTION_TIMEOUT_CHKER);
        private UpdateTimeoutChecker timeoutUpdateChecker;
        private QueryTimeoutChecker timeoutQueryChecker;
        private SQLException err;
        private PreparedStatement pstmt;
        private final java.sql.Connection conn;
        private List<Object> paramList = new ArrayList<>();

        protected ScashPreparedStatement(java.sql.Connection con) throws SQLException{
            this(con, null);
        }

        protected ScashPreparedStatement(java.sql.Connection con, String sql)throws SQLException {
            this.conn = con;
            if (sql == null || sql.equals("")) {
                this.pstmt = null;
            } else {
                this.pstmt = this.conn.prepareStatement(sql);
                this.pstmt.setFetchSize(this.getPool().getSource().getFetchSize());
            }
            this.groupNm = getGroupName();
            this.sql = sql;
        }

        @Override
        public PreparedStatement prepareStatement(String groupName)throws SQLException {
            throw new SQLException(IConnection.NOT_SUPPORTED_MESSAGE);
        }
        /**
         * 実行前batch argument.実行後に削除．
         */
        private final List<BatchArgument> unExecutedBatchArguments = new LinkedList<>();

        private final String groupNm;

        private String sql;

        private EXECUTE_BATCH_RESULT executeBatchResult = EXECUTE_BATCH_RESULT.NOT_EXECUTED;

        @Override
        public java.sql.Connection getConnection() {
            return Connection.this;
        }

        public IPool getPool() {
            return Connection.this.getPool();
        }
        public String getSql() {
            return sql;
        }
        public void setTimeoutUpdateSchedule(PreparedStatement ptmt) {
            this.timeoutUpdateChecker = new UpdateTimeoutChecker(Connection.this,ptmt);
            this.schdr.addTask(this.timeoutUpdateChecker, queryTimeout);
        }

        public void setTimeoutQuerySchedule(PreparedStatement ptmt) {
            this.timeoutQueryChecker = new QueryTimeoutChecker(Connection.this,ptmt);
            this.schdr.addTask(this.timeoutQueryChecker, queryTimeout);
        }

        @Override
        public void addBatch(final String sql, final List<Object> params, final String tableName, final String wherePhrase,
                final List<List<Object>> keysList, final OPERATION operation) throws SQLException {
            this.addBatchArgument(sql, params);

        }

        private BatchArgument addBatchArgument(final String sql, final List<Object> params) {
            BatchArgument batchArgument = new BatchArgument(sql, params);
            unExecutedBatchArguments.add(batchArgument);
            return batchArgument;
        }

        @Override
        public void clearBatch() throws SQLException {
            unExecutedBatchArguments.clear();
        }

        /**
         * バッチ処理実行．
         */
        @Override
        public int[] executeBatch() throws SQLException {
            int[] ret = new int[this.unExecutedBatchArguments.size()];
            if (unExecutedBatchArguments.size() == 0) {
                return ret;
            }
            try {
                for (int i = 0; i < unExecutedBatchArguments.size(); i++) {
                    ret[i] = executeUpdate(unExecutedBatchArguments.get(i));
                }
                this.executeBatchResult = EXECUTE_BATCH_RESULT.SUCCESS;
            } catch (Exception e) {
                throw new UnchkedExecption(e);
            } finally {
                this.clearBatch();
            }
            return ret;
        }

        private int executeUpdate(final BatchArgument batchArgument) throws Exception {
            pstmt = null;
            int rs = 0;

            try {
                pstmt = Connection.this.prepareStatement(batchArgument.getSql());
                if (batchArgument.getParams() != null) {
                    for (int i = 0; i < batchArgument.getParams().size(); i++) {
                        pstmt.setObject(i + 1, batchArgument.getParams().get(i));
                    }
                }
                rs = pstmt.executeUpdate();
            } catch (SQLTimeoutException ex) {
                this.executeBatchResult = EXECUTE_BATCH_RESULT.TIMEOUT_ERROR;
                throw ex;
            } catch (SQLException ex) {
                String code = "";
                if(DBCP.isOracle()) {
                    int errorCode = ex.getErrorCode();
                    code = Integer.toString(errorCode);
                } else {
                    code = ex.getSQLState();
                }

                String msgCode = null;
                if (DBErrorCodes.UNIQUE_VIOLATION_CODE.equals(code)) {
                    this.executeBatchResult = EXECUTE_BATCH_RESULT.LOGICAL_ERROR;
                    msgCode = LogConstants.ERROR_DBE1033;
                    ex = new DuplicateKeyException(ex);
                } else if(DBErrorCodes.DEAD_LOCK_CODE.equals(code)
                        || DBErrorCodes.NOTNULL_VIOLATION_CODE.equals(code)
                        || DBErrorCodes.CHECK_VIOLATION_CODE.equals(code)
                        || DBErrorCodes.REFERENCE_VIOLATION_CODE1.equals(code)
                        || DBErrorCodes.REFERENCE_VIOLATION_CODE2.equals(code)) {
                    this.executeBatchResult = EXECUTE_BATCH_RESULT.LOGICAL_ERROR;
                    msgCode = LogConstants.ERROR_DBE1033;
                } else if(DBErrorCodes.READ_ONLY_CODE.equals(code)) {
                    checkAndshutdown();
                    this.executeBatchResult = EXECUTE_BATCH_RESULT.FAILOVER_ERROR;
                    msgCode = LogConstants.ERROR_DBE1067;
                } else {
                    this.executeBatchResult = EXECUTE_BATCH_RESULT.SYSTEM_ERROR;
                    msgCode = LogConstants.ERROR_DBE1034;
                }

                throw ex;

            } catch (Exception ex) {
                // その他の例外も含むが発生
                this.executeBatchResult = EXECUTE_BATCH_RESULT.SYSTEM_ERROR;
                throw ex;
            } finally {
                if (pstmt != null) {
                    pstmt.close();
                }
            }
            return rs;
        }

        @Override
        public int executeUpdate() throws SQLException {
            int rs = 0;
            setTimeoutUpdateSchedule(pstmt);
            try {
                if (pstmt != null) {
                    try {
                        rs = pstmt.executeUpdate();
                    } catch (SQLException e) {
                        String code = "";
                        if(DBCP.isOracle()) {
                            int errorCode = e.getErrorCode();
                            code = Integer.toString(errorCode);
                        } else {
                            code = e.getSQLState();
                        }
                        throw e;
                    }
                }
            } finally {
                // タイムアウトチェック処理
                checkUpdateError();
            }
            return rs;
        }

        @Override
        public boolean execute() throws SQLException {
            try {
                pstmt.execute();
            } catch (Exception ex) {
                throw ex;
            } finally {
                if (pstmt != null) {
                    pstmt.close();
                }
            }
            return true;
        }

        @Override
        public ResultSet executeQuery() throws SQLException {
            ResultSet rs = null;
            setTimeoutQuerySchedule(pstmt);
            try {
                rs = pstmt.executeQuery();
            } catch (SQLException ex) {
                String code = "";
                if(DBCP.isOracle()) {
                    int errorCode = ex.getErrorCode();
                    code = Integer.toString(errorCode);
                } else {
                    code = ex.getSQLState();
                }
                // DB生存確認
                if (!conn.isClosed()) {
                    confirmConnectionCondition();
                }
                throw ex;
            } finally {
                // タイムアウトチェック処理
                checkQueryError();
            }
            return rs;
        }

        private void confirmConnectionCondition() {
            processConfirmConnectionCondition(conn);
        }

        public void checkUpdateError() throws SQLException {
            if (this.timeoutUpdateChecker != null) {
                long time = System.nanoTime() - this.timeoutUpdateChecker.getStartTime();
                this.schdr.discardTask(this.timeoutUpdateChecker);
                this.err = this.timeoutUpdateChecker.getError();
                if (this.err != null) {
                    throw err;
                }
            }
        }

        public void checkQueryError() throws SQLException {
            if (this.timeoutQueryChecker != null) {
                long time = System.nanoTime() - this.timeoutQueryChecker.getStartTime();
                this.schdr.discardTask(this.timeoutQueryChecker);
                this.err = this.timeoutQueryChecker.getError();
                if (this.err != null) {
                    throw err;
                }
            }
        }

        @Override
        public EXECUTE_BATCH_RESULT getExecuteBatchResult() {
            return this.executeBatchResult;
        }

        @Override
        public void rollback() throws SQLException {
            setRollbackTimeoutSchedule();
            try {
                Connection.this.connection.rollback();
            } finally {
                checkRollbackError();
            }
            clearBatch();
            executeBatchResult = EXECUTE_BATCH_RESULT.ROLLBACK;
        }

        private class BatchArgument {
            private String sqlBat;
            private List<Object> params;

            BatchArgument(final String sql, final List<Object> params) {
                this.sqlBat = sql;
                this.params = params;
            }

            String getSql() {
                return this.sqlBat;
            }

            List<Object> getParams() {
                return this.params;
            }

            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder();
                sb.append("BatchArgument(");
                sb.append("sql=");
                sb.append(this.sqlBat);
                sb.append(", params=");
                if (this.params == null) {
                    sb.append("null");
                } else {
                    sb.append("[");
                    for (int i = 0; i < params.size(); i++) {
                        if (i != 0) {
                            sb.append(", ");
                        }
                        sb.append(params.get(i));
                    }
                    sb.append("]");
                }
                sb.append(")");
                return sb.toString();
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (sql != null) {
                int count = 0;
                int start = 0;
                while (true) {
                    int index = sql.indexOf('?', start);
                    if (index < 0) {
                        index = sql.length();
                    }
                    sb.append(sql.substring(start, index));
                    start = index + 1;
                    if (index == sql.length()) {
                        break;
                    }
                    if (count < paramList.size()) {
                        try {
                            Object o = paramList.get(count++);
                            String sep = ((o instanceof String) || (o instanceof byte [])) ? "'" : "";
                            sb.append(sep);
                            sb.append(sep);
                            continue;
                        }
                        catch (Exception e) {
                            // 例外は無視
                        }
                    }
                    sb.append('?');
                }
            }
            return sb.toString();
        }

        @Override
        public void addBatch() throws SQLException {
            pstmt.addBatch();
        }

        @Override
        public void addBatch(final String sql) throws SQLException {
            pstmt.addBatch(sql);
        }

        @Override
        public void clearParameters() throws SQLException {
            pstmt.clearParameters();
        }

        @Override
        public void clearWarnings() throws SQLException {
            pstmt.clearWarnings();
        }

        @Override
        public void close() throws SQLException {
            pstmt.close();
        }

        @Override
        public int getFetchDirection() throws SQLException {
            return pstmt.getFetchDirection();
        }

        @Override
        public int getFetchSize() throws SQLException {
            return pstmt.getFetchSize();
        }

        @Override
        public ResultSet getGeneratedKeys() throws SQLException {
            return pstmt.getGeneratedKeys();
        }

        @Override
        public int getMaxFieldSize() throws SQLException {
            return pstmt.getMaxFieldSize();
        }

        @Override
        public int getMaxRows() throws SQLException {
            return pstmt.getMaxRows();
        }

        @Override
        public boolean getMoreResults() throws SQLException {
            return pstmt.getMoreResults();
        }

        @Override
        public boolean getMoreResults(final int current) throws SQLException {
            return pstmt.getMoreResults(current);
        }

        @Override
        public ParameterMetaData getParameterMetaData() throws SQLException {
            return pstmt.getParameterMetaData();
        }

        @Override
        public int getQueryTimeout() throws SQLException {
            return pstmt.getQueryTimeout();
        }

        @Override
        public ResultSet getResultSet() throws SQLException {
            return pstmt.getResultSet();
        }

        @Override
        public int getResultSetConcurrency() throws SQLException {
            return pstmt.getResultSetConcurrency();
        }

        @Override
        public int getResultSetHoldability() throws SQLException {
            return pstmt.getResultSetHoldability();
        }

        @Override
        public int getResultSetType() throws SQLException {
            return pstmt.getResultSetType();
        }

        @Override
        public int getUpdateCount() throws SQLException {
            return pstmt.getUpdateCount();
        }

        @Override
        public SQLWarning getWarnings() throws SQLException {
            return pstmt.getWarnings();
        }

        @Override
        public boolean isClosed() throws SQLException {
            return pstmt.isClosed();
        }

        @Override
        public boolean isPoolable() throws SQLException {
            return pstmt.isPoolable();
        }

        @Override
        public boolean isWrapperFor(final Class<?> iface) throws SQLException {
            return pstmt.isWrapperFor(iface);
        }

        @Override
        public void setArray(final int parameterIndex, final Array x) throws SQLException {
            paramList.add(parameterIndex - 1, x);
            pstmt.setArray(parameterIndex, x);
        }

        @Override
        public void setAsciiStream(final int parameterIndex, final InputStream x, final int length) throws SQLException {
            pstmt.setAsciiStream(parameterIndex, x, length);
        }

        @Override
        public void setAsciiStream(final int parameterIndex, final InputStream x, final long length) throws SQLException {
            pstmt.setAsciiStream(parameterIndex, x, length);
        }

        @Override
        public void setAsciiStream(final int parameterIndex, final InputStream x) throws SQLException {
            pstmt.setAsciiStream(parameterIndex, x);
        }

        @Override
        public void setBigDecimal(final int parameterIndex, final BigDecimal x) throws SQLException {
            paramList.add(parameterIndex - 1, x);
            pstmt.setBigDecimal(parameterIndex, x);
        }

        @Override
        public void setBinaryStream(final int parameterIndex, final InputStream x, final int length) throws SQLException {
            pstmt.setBinaryStream(parameterIndex, x, length);
        }

        @Override
        public void setBinaryStream(final int parameterIndex, final InputStream x, final long length) throws SQLException {
            pstmt.setBinaryStream(parameterIndex, x, length);
        }

        @Override
        public void setBinaryStream(final int parameterIndex, final InputStream x) throws SQLException {
            pstmt.setBinaryStream(parameterIndex, x);
        }

        @Override
        public void setBlob(final int parameterIndex, final Blob x) throws SQLException {
            paramList.add(parameterIndex - 1, x);
            pstmt.setBlob(parameterIndex, x);
        }

        @Override
        public void setBlob(final int parameterIndex, final InputStream inputStream, final long length) throws SQLException {
            pstmt.setBlob(parameterIndex, inputStream, length);
        }

        @Override
        public void setBlob(final int parameterIndex, final InputStream inputStream) throws SQLException {
            pstmt.setBlob(parameterIndex, inputStream);
        }

        @Override
        public void setBoolean(final int parameterIndex, final boolean x) throws SQLException {
            paramList.add(parameterIndex - 1, x);
            pstmt.setBoolean(parameterIndex, x);
        }

        @Override
        public void setByte(final int parameterIndex, final byte x) throws SQLException {
            paramList.add(parameterIndex - 1, x);
            pstmt.setByte(parameterIndex, x);
        }

        @Override
        public void setBytes(final int parameterIndex, final byte[] x) throws SQLException {
            paramList.add(parameterIndex - 1, x);
            pstmt.setBytes(parameterIndex, x);
        }

        @Override
        public void setCharacterStream(final int parameterIndex, final Reader reader, final int length) throws SQLException {
            pstmt.setCharacterStream(parameterIndex, reader, length);
        }

        @Override
        public void setCharacterStream(final int parameterIndex, final Reader reader, final long length) throws SQLException {
            pstmt.setCharacterStream(parameterIndex, reader, length);
        }

        @Override
        public void setCharacterStream(final int parameterIndex, final Reader reader) throws SQLException {
            pstmt.setCharacterStream(parameterIndex, reader);
        }

        @Override
        public void setClob(final int parameterIndex, final Clob x) throws SQLException {
            paramList.add(parameterIndex - 1, x);
            pstmt.setClob(parameterIndex, x);
        }

        @Override
        public void setClob(final int parameterIndex, final Reader reader, final long length) throws SQLException {
            pstmt.setClob(parameterIndex, reader, length);
        }

        @Override
        public void setClob(final int parameterIndex, final Reader reader) throws SQLException {
            pstmt.setClob(parameterIndex, reader);
        }

        @Override
        public void setCursorName(final String name) throws SQLException {
            pstmt.setCursorName(name);
        }

        @Override
        public void setDate(final int parameterIndex, final Date x, final Calendar cal) throws SQLException {
            pstmt.setDate(parameterIndex, x, cal);
        }

        @Override
        public void setDate(final int parameterIndex, final Date x) throws SQLException {
            paramList.add(parameterIndex - 1, x);
            pstmt.setDate(parameterIndex, x);
        }

        @Override
        public void setDouble(final int parameterIndex, final double x) throws SQLException {
            paramList.add(parameterIndex - 1, x);
            pstmt.setDouble(parameterIndex, x);
        }

        @Override
        public void setEscapeProcessing(final boolean enable) throws SQLException {
            pstmt.setEscapeProcessing(enable);
        }

        @Override
        public void setFetchDirection(final int direction) throws SQLException {
            pstmt.setFetchDirection(direction);
        }

        @Override
        public void setFetchSize(final int rows) throws SQLException {
            pstmt.setFetchSize(rows);
        }

        @Override
        public void setFloat(final int parameterIndex, final float x) throws SQLException {
            paramList.add(parameterIndex - 1, x);
            pstmt.setFloat(parameterIndex, x);
        }

        @Override
        public void setInt(final int parameterIndex, final int x) throws SQLException {
            paramList.add(parameterIndex - 1, x);
            pstmt.setInt(parameterIndex, x);
        }

        @Override
        public void setLong(final int parameterIndex, final long x) throws SQLException {
            paramList.add(parameterIndex - 1, x);
            pstmt.setLong(parameterIndex, x);
        }

        @Override
        public void setString(final int parameterIndex, final String s) throws SQLException {
            paramList.add(parameterIndex - 1, s);
            pstmt.setString(parameterIndex, s);
        }

        @Override
        public void setMaxFieldSize(final int max) throws SQLException {
            pstmt.setMaxFieldSize(max);
        }

        @Override
        public void setMaxRows(final int max) throws SQLException {
            pstmt.setMaxRows(max);
        }

        @Override
        public void setNCharacterStream(final int parameterIndex, final Reader value, final long length) throws SQLException {
            pstmt.setNCharacterStream(parameterIndex, value, length);
        }

        @Override
        public void setNCharacterStream(final int parameterIndex, final Reader value) throws SQLException {
            pstmt.setNCharacterStream(parameterIndex, value);
        }

        @Override
        public void setNClob(final int parameterIndex, final NClob value) throws SQLException {
            pstmt.setNClob(parameterIndex, value);
        }

        @Override
        public void setNClob(final int parameterIndex, final Reader reader, final long length) throws SQLException {
            pstmt.setNClob(parameterIndex, reader, length);
        }

        @Override
        public void setNClob(final int parameterIndex, final Reader reader) throws SQLException {
            pstmt.setNClob(parameterIndex, reader);
        }

        @Override
        public void setNString(final int parameterIndex, final String value) throws SQLException {
            pstmt.setNString(parameterIndex, value);
        }

        @Override
        public void setNull(final int parameterIndex, final int sqlType, final String typeName) throws SQLException {
            paramList.add(parameterIndex - 1, null);
            pstmt.setNull(parameterIndex, sqlType, typeName);
        }

        @Override
        public void setNull(final int parameterIndex, final int sqlType) throws SQLException {
            paramList.add(parameterIndex - 1, null);
            pstmt.setNull(parameterIndex, sqlType);
        }

        @Override
        public void setObject(final int parameterIndex, Object x, final int targetSqlType, final int scaleOrLength) throws SQLException {
            pstmt.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
        }

        @Override
        public void setObject(final int parameterIndex, final Object x, final int targetSqlType) throws SQLException {
            pstmt.setObject(parameterIndex, x, targetSqlType);
        }

        @Override
        public void setObject(final int parameterIndex, final Object x) throws SQLException {
            paramList.add(parameterIndex - 1, x);
            pstmt.setObject(parameterIndex, x);
        }

        @Override
        public void setPoolable(final boolean poolable) throws SQLException {
            pstmt.setPoolable(poolable);
        }

        @Override
        public void setQueryTimeout(final int seconds) throws SQLException {
            pstmt.setQueryTimeout(seconds);
        }

        @Override
        public void setRef(final int parameterIndex, final Ref x) throws SQLException {
            pstmt.setRef(parameterIndex, x);
        }

        @Override
        public void setRowId(final int parameterIndex, final RowId x) throws SQLException {
            pstmt.setRowId(parameterIndex, x);
        }

        @Override
        public void setShort(final int parameterIndex, final short x) throws SQLException {
            paramList.add(parameterIndex - 1, x);
            pstmt.setShort(parameterIndex, x);
        }

        @Override
        public void setSQLXML(final int parameterIndex, final SQLXML xmlObject) throws SQLException {
            pstmt.setSQLXML(parameterIndex, xmlObject);
        }

        @Override
        public void setTime(final int parameterIndex, final Time x, final Calendar cal) throws SQLException {
            pstmt.setTime(parameterIndex, x, cal);
        }

        @Override
        public void setTime(final int parameterIndex, final Time x) throws SQLException {
            paramList.add(parameterIndex - 1, x);
            pstmt.setTime(parameterIndex, x);
        }

        @Override
        public void setTimestamp(final int parameterIndex, final Timestamp x, final Calendar cal) throws SQLException {
            pstmt.setTimestamp(parameterIndex, x, cal);
        }

        @Override
        public void setTimestamp(final int parameterIndex, final Timestamp x) throws SQLException {
            paramList.add(parameterIndex - 1, x);
            pstmt.setTimestamp(parameterIndex, x);
        }

        @Override
        public void setURL(final int parameterIndex, final URL x) throws SQLException {
            pstmt.setURL(parameterIndex, x);
        }

        @Override
        public <T> T unwrap(final Class<T> iface) throws SQLException {
            return pstmt.unwrap(iface);
        }

        @Override
        public void closeOnCompletion() throws SQLException {
            pstmt.closeOnCompletion();
        }

        @Override
        public boolean isCloseOnCompletion() throws SQLException {
            return pstmt.isCloseOnCompletion();
        }
    }

    @Override
    public void abort(Executor arg0) throws SQLException {
        this.connection.abort(arg0);
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return this.connection.getNetworkTimeout();
    }

    @Override
    public String getSchema() throws SQLException {
        return this.connection.getSchema();
    }

    @Override
    public void setNetworkTimeout(Executor arg0, int arg1) throws SQLException {
        this.connection.setNetworkTimeout(arg0,arg1);
    }

    @Override
    public void setSchema(String arg0) throws SQLException {
       this.connection.setSchema(arg0);
    }

    @Override
    public long getOriginalCreateTime() {
        return originalCreateTime;
    }

    @Override
    public Blob createBlob() throws SQLException {
        Blob blob = this.connection.createBlob();
        lobs.add(blob);
        return blob;
    }

    @Override
    public Clob createClob() throws SQLException {
        Clob clob = this.connection.createClob();
        lobs.add(clob);
        return clob;
    }
}
