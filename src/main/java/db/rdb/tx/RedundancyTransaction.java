package db.rdb.tx;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import db.rdb.dbcp.IConnection;
import db.rdb.dbcp.delegation.Connection;
import db.rdb.dbcp.delegation.Connection.ScashPreparedStatement;
import db.rdb.tx.IPreparedStatement.EXECUTE_BATCH_RESULT;
import system.core.UnchkedExecption;

public class RedundancyTransaction implements ITransaction {

    @Override
    public final void submit(final ITxProc txProc) {
        Map<String, TransactionGroupPreparedStatement> stmts = new HashMap<>();
        Map<String, ScashPreparedStatement> pstmtMap = new HashMap<>();

        EXECUTE_BATCH_RESULT executeBatchResult;
        Exception occurredException = null;

        try {
            //クエリ実行処理
            txProc.execute(new TransactionGroupPreparedStatement(stmts, pstmtMap));
        } catch (Exception e) {
            checkNotSupportedSQLException(e);
            occurredException = e;
        }

        ScashPreparedStatement[] pstmts = pstmtMap.values().toArray(new ScashPreparedStatement[0]);
        if (pstmts.length == 0) {
            if (occurredException==null) {
                return;
            }
            throw new UnchkedExecption("ExecuteUpdate() unexecuted.",occurredException);
        }
        ScashPreparedStatement pstmt = pstmts[0];
        executeBatchResult = pstmt.getExecuteBatchResult();

        Connection con = (Connection) pstmt.getConnection();
        if (occurredException == null && executeBatchResult == EXECUTE_BATCH_RESULT.SUCCESS) {
            try {
                con.commit();
            } catch (Exception e) {
                occurredException = e;
            }
        }

        try {
            pstmt.close();
        } catch (SQLException e) {
        }

        returnConnection(con, occurredException);

        if (occurredException != null) {
            throw new UnchkedExecption(occurredException);
        }
    }

    private void returnConnection(Connection con, Exception ex) {
        try {
            if (con.connection != null && !con.isScashClosed()) {
                if (ex != null) {
                    new Thread(new ConnectionReturnToTask(con)).start();
                } else {
                    con.close();
                }
            }
        } catch (SQLException e) {
        }
    }

    private static void checkNotSupportedSQLException(final Exception e) {
        boolean hasNotSupportedSQLException = false;
        Throwable th = e;
        while (true) {
            if ((th instanceof SQLException) && IConnection.NOT_SUPPORTED_MESSAGE.equals(th.getMessage())) {
                hasNotSupportedSQLException = true;
                break;
            }
            if (th.getCause() == null) {
                hasNotSupportedSQLException = false;
                break;
            }
            th = th.getCause();
        }

        if (!hasNotSupportedSQLException) {
            return;
        }
    }

    public static class ConnectionReturnToTask extends Thread {
        private Connection conn;

        public ConnectionReturnToTask(Connection con) {
            this.conn = con;
        }

        @Override
        public void run() {
            try {
                conn.rollback();
            } catch (Exception e) {
            }
            try {
                if (conn.connection != null && !conn.isInside()) {
                    conn.close();
                }
            } catch (Exception e) {
            }
        }
    }
}
