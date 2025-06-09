package db.rdb.dbcp;

/**
 * スケジュール管理を作成します。
 * @author satoh
 */
public interface IScheduleManagerFactory {
    /**
     * スケジュール管理を作成します。
     * @return スケジュール管理
     */
    IScheduleManager create();
}
