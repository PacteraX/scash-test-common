package db.rdb.dbcp.schedule.tasks;

import java.util.Map;
import java.util.TimerTask;

import db.rdb.dbcp.DBCP;
import db.rdb.dbcp.IPool;
import db.rdb.dbcp.IPoolGroup;
import db.rdb.dbcp.IPoolSource;
import db.rdb.dbcp.IScheduleManager.SCHEDULER_TYPE;

public class ConnectionRefreshTask extends TimerTask {

    final protected Map<String, IPoolGroup> poolGroups;
    final protected IPoolSource poolsource;

    public ConnectionRefreshTask(Map<String, IPoolGroup> poolGroups) {
        this.poolGroups = poolGroups;
        poolsource = this.poolGroups.values().iterator().next().getPoolList().iterator().next().getSource();
        DBCP.getSchedulemgr().getScheduler(SCHEDULER_TYPE.CONNECTION_REFRESHING).addTask(this, poolsource.getWatchInterval(), poolsource.getWatchInterval());
    }

    @Override
    synchronized public void run() {
        for (IPoolGroup pg : poolGroups.values()) {
            if (!pg.isAlias()) {
                for (IPool pool : pg.getPoolList()) {
                    pool.refreshWaitConnection();
                }
            }
        }
    }

}
