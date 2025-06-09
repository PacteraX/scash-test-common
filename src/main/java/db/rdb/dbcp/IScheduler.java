package db.rdb.dbcp;

import java.util.TimerTask;

/**
 * スケジューラのインターフェース.
 * @author HSK Satoh
 */
public interface IScheduler {
    /**
     * スケジューラにタスクをセットします。
     * @param task
     *            実行するタスク
     * @param time
     *            実行までのアイドル時間
     */
    void addTask(TimerTask task, long time);
    void addTask(TimerTask task, long delay, final long period);

    /**
     * スケジューラに登録されているタスクをキャンセルします。
     * @param task
     *            対象のタスク
     */
    void discardTask(TimerTask task);
}
