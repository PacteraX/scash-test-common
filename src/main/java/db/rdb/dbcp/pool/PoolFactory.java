package db.rdb.dbcp.pool;

import db.rdb.dbcp.IPool;
import db.rdb.dbcp.IPoolFactory;

public class PoolFactory implements IPoolFactory {
    @Override
    public IPool create(String dbName) {
        return new Pool(dbName);
    }
}