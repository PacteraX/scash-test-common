package db.rdb.dbcp.schedule;

import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

import db.rdb.dbcp.IScheduleManager;
import db.rdb.dbcp.IScheduler;
import system.finalize.IFinalize;

/**
 * スケジューラ管理.
 * @author satoh
 */
public class ScheduleManager implements IScheduleManager, IFinalize {

    private Map<Enum<SCHEDULER_TYPE>, IScheduler> scheduleres = new ConcurrentHashMap<>();

    @Override
    public final IScheduler getScheduler(final SCHEDULER_TYPE type) {
        return scheduleres.get(type);
    }

    @Override
    public final void addScheduler(final SCHEDULER_TYPE type) {
        this.scheduleres.put(type, new Scheduler());
    }

    @Override
    synchronized public void execute() {
        Iterator<IScheduler> iterator = this.scheduleres.values().iterator();
        while (iterator.hasNext()) {
            IScheduler s = iterator.next();
            if ( s instanceof Timer) {
                ((Timer)s).cancel();
            }
        }
        this.scheduleres.clear();
    }
}
