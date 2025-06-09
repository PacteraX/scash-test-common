package db.rdb.dbcp;

/**
 * コネクションPoolを作成します.
 * @author HSK Satoh
 */
public interface IPoolFactory {
    /**
     * 指定されたDBのコネクションプールを作成します.
     * @param dbName
     *            DB名
     * @return 作成されたコネクションプール
     */
    IPool create(String dbName);
}
