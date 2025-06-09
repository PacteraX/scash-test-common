package db.rdb.dbcp.delegation;

import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.TimerTask;

/**
 * @author satoh
 */
public class CommitTimeoutChecker extends TimerTask {
    private SQLException error;
    private Connection connection;
    private long startTime;

    public CommitTimeoutChecker(Connection con) {
        super();
        this.connection = con;
        this.startTime = System.nanoTime();
    }

    public long getStartTime() {
        return startTime;
    }

    @Override
    public synchronized final void run() {
        this.error = new SQLTimeoutException("Commit timeout error.");
        try {
            connection.checkAndshutdown();
        } catch (Throwable e) {
        }
    }

    /**
     * @return the error
     */
    public synchronized SQLException getError() {
        SQLException ex = this.error;
        return ex;
    }
}

