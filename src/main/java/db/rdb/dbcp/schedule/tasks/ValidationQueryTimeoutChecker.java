package db.rdb.dbcp.schedule.tasks;

import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.TimerTask;

import db.rdb.dbcp.IConnection;

/**
 * @author so
 */
public class ValidationQueryTimeoutChecker extends TimerTask {
    private SQLException error;
    private IConnection con;
    private long startTime;

    public ValidationQueryTimeoutChecker(IConnection con) {
        super();
        this.con = con;
        this.startTime = System.nanoTime();
    }

    public long getStartTime() {
        return startTime;
    }

    @Override
    public synchronized final void run() {
        error = new SQLTimeoutException("ValidationQuery timeout error.");
        con.checkAndshutdown();
    }

    /**
     * @return the error
     */
    public synchronized SQLException getError() {
        SQLException ex = error;
        return ex;
    }
}

