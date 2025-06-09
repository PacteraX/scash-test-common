package db.rdb.dbcp.schedule.tasks;

import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.TimerTask;

import db.LogConstants;

/**
 * @author satoh
 */
public class UpdateCancelTimeoutChecker extends TimerTask {
    private SQLException error;
    private long startTime;

    public UpdateCancelTimeoutChecker() {
        super();
        this.startTime = System.nanoTime();
    }

    public long getStartTime() {
        return startTime;
    }

    @Override
    public synchronized final void run() {
        this.error = new SQLTimeoutException("Update cancel timeout error.");
    }

    /**
     * @return the error
     */
    public synchronized SQLException getError() {
        SQLException ex = this.error;
        return ex;
    }
}

