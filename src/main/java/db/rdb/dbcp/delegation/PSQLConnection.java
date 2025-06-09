package db.rdb.dbcp.delegation;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import db.rdb.dbcp.DBCP;
import db.rdb.dbcp.IPool;

public class PSQLConnection extends Connection {

    protected PSQLConnection(IPool ipool, java.sql.Connection iconnection) throws SQLException {
        super(ipool, iconnection);
    }

    public void checkError() throws SQLException {
        if (this.timeoutRollbackChecker != null) {
            this.scheduler.discardTask(this.timeoutRollbackChecker);
            this.error = this.timeoutRollbackChecker.getError();
            if (this.error != null) {
                try {
                    shutdown();
                } catch (Throwable e) {
                    e.getMessage();
                }
                throw error;
            }
        }
    }

    /*
     * 利用済みコネクションをプールに返却する際、コネクションをロールバックしてから返却する。
     * ロールバック処理で、例外が発生した場合、コネクションを切断する。
     */
    public void con_rollback() throws SQLException {
        setRollbackTimeoutSchedule();
        try {
            this.connection.rollback();
        } finally {
            checkError();
        }
    }

    @Override
    public synchronized void close() throws SQLException {

        connectionCloseCheck();

        if (!isScashClosed()) {
            try {
                con_rollback();
                this.pool.returnTo(this);
            } catch (SQLException ex) {
                try {
                    this.shutdown();
                } catch (Throwable th) {
                    throw new RuntimeException(th);
                }
            }
        }
    }

    @Override
    public void processConfirmConnectionCondition(java.sql.Connection conn) {
        setValidationQueryTimeoutSchedule();
        try (Connection con = (Connection)DBCP.getConnection(this.getGroupName());
             PreparedStatement pstmt = con.connection.prepareStatement(validationQuery)) {
            pstmt.executeQuery();
        } catch (Exception ex) {
            checkAndshutdown();
        } finally {
            try {
                checkValidationQueryError();
            } catch (SQLException e1) {
            }
        }
    }
}
