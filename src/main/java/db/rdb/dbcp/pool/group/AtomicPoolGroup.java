package db.rdb.dbcp.pool.group;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import db.rdb.dbcp.IPool;
import db.rdb.dbcp.IPoolGroup;

/**
 * AtomicIntegerでリストを取得 最低1件以上から始まる前提
 * @author Administrator
 */
class AtomicPoolGroup implements IPoolGroup {

    private final AtomicInteger index = new AtomicInteger();

    private List<IPool> poolList;

    private final String groupName;

    private boolean alias = false;

    AtomicPoolGroup(final String name, final IPool pool, final boolean alias) {
        this.groupName = name;
        this.index.set(0);
        this.poolList = new ArrayList<IPool>();
        addPool(pool);
        this.alias = alias;
    }

    AtomicPoolGroup(final String name, final List<IPool> poolList, final boolean alias) {
        this.groupName = name;
        this.index.set(0);
        this.poolList = new ArrayList<IPool>();
        for (IPool pool : poolList) {
            addPool(pool);
        }
        this.alias = alias;
    }

    public String getGroupName() {
        return this.groupName;
    }

    @Override
    public void addPool(final IPool pool) {
        poolList.add(pool);
    }

    @Override
    public boolean isAlias() {
        return this.alias;
    }

    @Override
    public Collection<IPool> getPoolList() {
        return Collections.unmodifiableCollection(poolList);
    }
}
