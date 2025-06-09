package db.rdb.dbcp.schedule;

import db.rdb.dbcp.IScheduleManager;
import db.rdb.dbcp.IScheduleManagerFactory;
import db.rdb.dbcp.IScheduleManager.SCHEDULER_TYPE;

/**
 * @author satoh
 */
public class ScheduleManagerFactory implements IScheduleManagerFactory {

    /*
     * (non-Javadoc)
     * @see db.rdb.dbcp.IScheduleManagerFactory#create()
     */
    @Override
    public final IScheduleManager create() {
        IScheduleManager scheduleManager = new ScheduleManager();
        scheduleManager.addScheduler(SCHEDULER_TYPE.SQL_TIMEOUT_CHKER);
        scheduleManager.addScheduler(SCHEDULER_TYPE.CONNECTION_TIMEOUT_CHKER);
        scheduleManager.addScheduler(SCHEDULER_TYPE.AUTO_CONNECTION_REFRESHING);
        return scheduleManager;
    }
}
