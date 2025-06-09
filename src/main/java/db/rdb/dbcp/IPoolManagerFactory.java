package db.rdb.dbcp;

/**
 * プール管理を作成します.
 * @author HSK Satoh
 */
public interface IPoolManagerFactory {
    /**
     * プール管理を作成します.
     * @return 作成されたプール管理
     */
    IPoolManager create();
}
