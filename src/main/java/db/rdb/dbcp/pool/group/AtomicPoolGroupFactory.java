package db.rdb.dbcp.pool.group;

import db.rdb.dbcp.IPool;
import db.rdb.dbcp.IPoolGroup;
import db.rdb.dbcp.IPoolGroupFactory;

public class AtomicPoolGroupFactory implements IPoolGroupFactory {
    @Override
    public final IPoolGroup create(final String groupName, final IPool pool) {
        return new AtomicPoolGroup(groupName, pool, false);
    }
    @Override
    public final IPoolGroup create(final String groupName, final IPool pool, final boolean alias) {
        return new AtomicPoolGroup(groupName, pool, alias);
    }

}
