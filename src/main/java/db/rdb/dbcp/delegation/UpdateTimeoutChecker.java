package db.rdb.dbcp.delegation;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.TimerTask;

/**
 * @author so
 */
public class UpdateTimeoutChecker extends TimerTask {
    private Connection con;
    private SQLTimeoutException error;
    private PreparedStatement pstmt;
    private long startTime;

    public UpdateTimeoutChecker(Connection con, PreparedStatement pstmt) {
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
        this.error = new SQLTimeoutException("ExecuteUpdate timeout error.");
        con.setUpdateCancelTimeoutSchedule();
        try {
            if (pstmt != null) {
                pstmt.cancel();
            }
        } catch (SQLException e) {

        } finally {
            try {
                con.checkUpdateCancelError();
            } catch (SQLException e) {
            }
        }
    }

    /**
     * @return the error
     */
    public synchronized SQLTimeoutException getError() {
        SQLTimeoutException ex = this.error;
        this.pstmt = null;
        return ex;
    }
}

