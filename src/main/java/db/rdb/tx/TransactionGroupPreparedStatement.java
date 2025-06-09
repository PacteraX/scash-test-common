package db.rdb.tx;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import system.core.UnchkedExecption;
import db.LogConstants;
import db.rdb.dbcp.DBCP;
import db.rdb.dbcp.IPool;
import db.rdb.dbcp.IPoolGroup;
import db.rdb.dbcp.delegation.AbstractPreparedStatement;
import db.rdb.dbcp.delegation.Connection.ScashPreparedStatement;

public class TransactionGroupPreparedStatement extends AbstractPreparedStatement implements IPreparedStatement {
    protected Map<String, TransactionGroupPreparedStatement> stmts;
    protected Map<String, ScashPreparedStatement> lazyMap;
    protected String physicalName;
    protected String logicalName;
    protected ScashPreparedStatement pstmt;

    @Override
    public void addBatch(String sql, List<Object> params, String tableName, String wherePhrase, List<List<Object>> keysList, OPERATION operation) throws SQLException {
        getDBPreparedStatement().addBatch(sql, params, tableName, wherePhrase, keysList, operation);
    }

    @Override
    public EXECUTE_BATCH_RESULT getExecuteBatchResult() {
        return getDBPreparedStatement().getExecuteBatchResult();
    }

    @Override
    public void clearBatch() throws SQLException {
        getDBPreparedStatement().clearBatch();
    }

    @Override
    public void close() throws SQLException {
        getDBPreparedStatement().close();
    }

    @Override
    public int[] executeBatch() throws SQLException {
        return getDBPreparedStatement().executeBatch();
    }

    @Override
    public void rollback() throws SQLException {
        SQLException t = null;
        TransactionGroupPreparedStatement[] cs = stmts.values().toArray(new TransactionGroupPreparedStatement[stmts.values().size()]);
        for (TransactionGroupPreparedStatement stmt : cs) {
            try {
                stmt.getDBPreparedStatement().rollback();
            } catch (SQLException e) {
                t = e;
            }
        }
        if (t != null) {
            throw t;
        }
    }

    public TransactionGroupPreparedStatement(Map<String, TransactionGroupPreparedStatement> stmts, Map<String, ScashPreparedStatement> conns) {
        this.stmts = stmts;
        this.lazyMap = conns;
    }

    public TransactionGroupPreparedStatement(String physicalName, String logicalName, Map<String, TransactionGroupPreparedStatement> stmts, Map<String, ScashPreparedStatement> conns) {
        this(stmts, conns);
        this.physicalName = physicalName;
        this.logicalName = logicalName;
    }

    public String appendGroupName(TransactionGroupPreparedStatement gname1, TransactionGroupPreparedStatement gname2) {
        StringBuilder sb = new StringBuilder(LogConstants.ERROR_DBE1011);
        String g1 = gname1.logicalName;
        String g2 = gname2.logicalName;
        if (g1.compareTo(g2) > 0) {
            g1 = gname2.logicalName;
            g2 = gname1.logicalName;
        }
        sb.append("[");
        sb.append(g1);
        sb.append(":");
        sb.append(g2);
        sb.append("]");
        return sb.toString();
    }

    @Override
    public PreparedStatement prepareStatement(String logName) throws SQLException {
        TransactionGroupPreparedStatement ret;
        if (logName.equals(this.logicalName)) {
            ret = this;
        }
        else {
            try {
                ret = stmts.get(logName);
                if (ret == null) {
                    String phyName = DBCP.getPhysicalGroupName(logName);
                    ret = new TransactionGroupPreparedStatement(phyName, logName, stmts, lazyMap);
                    stmts.put(logName, ret);
                }
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return ret;
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        getDBPreparedStatement();
        return super.executeQuery();
    }

    @Override
    public int executeUpdate() throws SQLException {
        getDBPreparedStatement();
        return super.executeUpdate();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return getDBPreparedStatement().getConnection();
    }

    public ScashPreparedStatement getDBPreparedStatement() {
        try {
            if (pstmt == null) {
                pstmt = lazyMap.get(this.physicalName);
                if (pstmt == null) {
                    if (lazyMap.size() != 0) {
                        String mes = appendGroupName(this, findOther());
                        throw new SQLException(mes);
                    }
                    IPoolGroup pg = DBCP.getPoolMgr().getPoolGroup(this.physicalName);
                    List<IPool> poolList= new ArrayList<>(pg.getPoolList());
                    pstmt = (ScashPreparedStatement )poolList.get(0).getConnection().prepareStatement("");
                    lazyMap.put(this.physicalName, pstmt);
                }
            }
            return pstmt;
        }
        catch (Exception e) {
            throw new UnchkedExecption(e);
        }
    }

    private TransactionGroupPreparedStatement findOther() {
        for (TransactionGroupPreparedStatement stmt : stmts.values()) {
            if (((stmt.pstmt == pstmt) || (pstmt == null)) && (stmt != this)) {
                return stmt;
            }
        }
        return null;
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
