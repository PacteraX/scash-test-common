package db.rdb.dbcp.schedule;

import java.util.Timer;
import java.util.TimerTask;

import db.rdb.dbcp.IScheduler;

/**
 * スケジューラ
 */

public class Scheduler extends Timer implements IScheduler {

    /**
     * @stereotype use
     */

    /* #db.rdb.dbcp.schedule.SchedulerConfig lnkSchedulerConfig */

    /**
     * タスクの追加.
     */

    @Override
    public final void addTask(final TimerTask task, final long delay) {
        super.schedule(task, delay);
    }

    @Override
    public final void addTask(final TimerTask task, final long delay, final long period) {
        super.schedule(task, delay, period);
    }

    /**
     * タスクの取り消し.
     */

    @Override
    public final void discardTask(final TimerTask task) {
        task.cancel();
        super.purge();
    }

}
