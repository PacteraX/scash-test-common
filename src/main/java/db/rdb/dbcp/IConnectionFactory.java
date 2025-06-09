package db.rdb.dbcp;

import java.sql.Connection;

/**
 * 委譲型コネクションを作成します.
 * @author HSK Satoh
 */
public interface IConnectionFactory {
    /**
     * 委譲型コネクションを作成します.
     * @param pool
     *            コネクションプール
     * @param con
     *            委譲対象のコネクション
     * @return 作成された委譲型コネクション
     */
    IConnection create(IPool pool, Connection con);
}
