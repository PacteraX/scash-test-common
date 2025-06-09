package db.rdb.dbcp.delegation;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.TimerTask;

/**
 * @author so
 */
public class QueryTimeoutChecker extends TimerTask {
    private Connection con;
    private SQLTimeoutException error;
    private PreparedStatement pstmt;
    private long startTime;

    public QueryTimeoutChecker(Connection con, PreparedStatement pstmt) {
        super();
        this.con = con;
        this.pstmt = pstmt;
        this.startTime = System.nanoTime();
    }

    public long getStartTime() {
        return startTime;
    }

    @Override
    public synchronized final void run() {
        error = new SQLTimeoutException("ExecuteQuery timeout error.");
        con.setQueryCancelTimeoutSchedule();
        try {
            if (pstmt != null) {
                pstmt.cancel();
            }
        } catch (SQLException e) {
            try {
                pstmt.close();
            } catch (Exception ex) {
            }

            con.checkAndshutdown();
        } finally {
            try {
                con.checkQueryCancelError();
            } catch (SQLException e) {
            }
            try {
                if (pstmt != null && !pstmt.isClosed()) {
                    pstmt.close();
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * @return the error
     */
    public synchronized SQLTimeoutException getError() {
        SQLTimeoutException ex = error;
        pstmt = null;
        return ex;
    }
}

