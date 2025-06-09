package db.rdb.dbcp.schedule.tasks;

import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.TimerTask;

import db.LogConstants;

/**
 * @author so
 */
public class QueryCancelTimeoutChecker extends TimerTask {
    private SQLException error;
    private long startTime;

    public QueryCancelTimeoutChecker() {
        super();
        this.startTime = System.nanoTime();
    }

    public long getStartTime() {
        return startTime;
    }

    @Override
    public synchronized final void run() {
        this.error = new SQLTimeoutException("Query cancel timeout error.");
    }

    /**
     * @return the error
     */
    public synchronized SQLException getError() {
        SQLException ex = error;
        return ex;
    }
}

