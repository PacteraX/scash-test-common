package db.rdb.dbcp.schedule.tasks;

import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.TimerTask;

import db.LogConstants;

public class ConnectionCreateTimeoutChecker extends TimerTask {
    private SQLException error;
    private long startTime;

    public ConnectionCreateTimeoutChecker() {
        super();
        this.startTime = System.nanoTime();
    }

    public long getStartTime() {
        return startTime;
    }

    @Override
    public synchronized final void run() {
        this.error = new SQLTimeoutException("Connection create timeout error.");
    }

    public synchronized SQLException getError() {
        SQLException ex = this.error;
        return ex;
    }
}

