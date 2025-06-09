package db.rdb.dbcp;

/**
 * Poolグループを作成します.
 * @author satoh
 */
public interface IPoolGroupFactory {
    /**
     * Poolグループを作成します
     * @param pool 初期プール
     * @return Poolグループ
     */
    IPoolGroup create(final String groupName, final IPool pool);
    IPoolGroup create(final String groupName, final IPool pool, final boolean alias);
}
