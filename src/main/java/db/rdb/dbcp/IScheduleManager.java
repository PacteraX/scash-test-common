package db.rdb.dbcp;


/**
 * スケジュール管理のインターフェースです.
 * @author HSK Satoh
 */
public interface IScheduleManager {
    /**
     * スケジュールタイプの列挙.
     * @author HSK Satoh
     */
    public enum SCHEDULER_TYPE {
        SQL_TIMEOUT_CHKER, CONNECTION_TIMEOUT_CHKER, CONNECTION_REFRESHING, STATISTICAL, AUTO_CONNECTION_REFRESHING
    }

    /**
     * 登録されているスケジューラを返します.
     * @param type
     *            スケジュールタイプ
     * @return 対象のスケジューラ
     */
    IScheduler getScheduler(SCHEDULER_TYPE type);

    /**
     * スケジューラを追加します.
     * @param type
     *            スケジュールタイプ
     */
    void addScheduler(SCHEDULER_TYPE type);
}
