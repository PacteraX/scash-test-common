package db.rdb.dbcp.schedule.tasks;

import java.util.TimerTask;

import db.LogConstants;
import db.rdb.dbcp.IPool;

public class AutoConnectionRefreshTask extends TimerTask {

    protected final IPool pool;

    public AutoConnectionRefreshTask(IPool pool) {
        super();
        this.pool = pool;
    }

    @Override
    public synchronized void run() {
        if(pool.isAutoRefreshThreshold()) {
            pool.refreshAutoConnection();
        }
    }

}
