package db.rdb.dbcp.pool;

import static db.DBConstants.DBCP_DEFAULT;
import static db.DBConstants.DBCP_GROUP;
import static db.DBConstants.DBCP_POOL_FACTORY_KEY;
import static db.DBConstants.DBCP_POOL_REFRESH_MODE;
import static db.DBConstants.DBCP_PREFIX_KEY;
import static db.DBConstants.JDBC_DRIVER;
import static db.DBConstants.JDBC_KEY;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import system.core.UnchkedExecption;
import db.LogConstants;
import db.rdb.dbcp.BaseDBCP;
import db.rdb.dbcp.DBCPConfig;
import db.rdb.dbcp.IPool;
import db.rdb.dbcp.IPoolGroup;
import db.rdb.dbcp.IPoolManager;
import db.rdb.dbcp.IConnection;
import system.config.Configure;
import system.finalize.IFinalize;

/**
 * @author Administrator
 * @author modify 2009/12 HSK satoh
 */
public class PoolManager implements IFinalize, IPoolManager, Runnable {

    private String defaultPoolGroupKey;

    private final Map<String, IPoolGroup> poolGroups = new TreeMap<>();

    private final ConcurrentHashMap<String, IPoolGroup> poolGroupsDBOffLoad = new ConcurrentHashMap<>();

    private AtomicBoolean pmgrLock = new AtomicBoolean(false);

    private enum REFRESH_MODE {ON,OFF}

    private Map<String,AUTH_SEPARATION_MODE> authMode;

    private final Map<String,AtomicBoolean> offloads;

    public PoolManager() {
        authMode = new HashMap<>();
        offloads = new HashMap<>();
    }

    @Override
    public AUTH_SEPARATION_MODE getAuthMode(String dbgroup) {
        if (!authMode.containsKey(dbgroup)) {
            return AUTH_SEPARATION_MODE.OFF;
        }
        return authMode.get(dbgroup);
    }

    @Override
    public void setAuthMode(String dbgroup, AUTH_SEPARATION_MODE mode) {
        authMode.put(dbgroup, mode);
    }

    @Override
    public void init() {
        // check driver.
        try {
            for (Iterator<String> ite = Configure.getKeys(); ite.hasNext();) {
                String key = ite.next();
                if (key.startsWith(DBCP_PREFIX_KEY + JDBC_KEY + JDBC_DRIVER)) {
                    Class.forName(Configure.getString(key));
                }
            }
        } catch (ClassNotFoundException e) {
            throw new UnchkedExecption(e);
        }

        for (IPoolGroup group : this.poolGroups.values()) {
            if (!group.isAlias()) {
                Collection<IPool> pools = group.getPoolList();
                for (IPool pool : pools) {
                    try {
                        pool.increaseConnection();
                        if(pool.isAutoRefreshMode()) {
                            pool.addAutoRefreshSchedule();
                        }
                    } catch (UnchkedExecption e) {
                        String logMessage = MessageFormat.format(LogConstants.ERROR_DBE1007,pool.getGroupName(),pool.getDbName());
                        throw e;
                    }
                }
            }
        }
    }

    @Override
    public final Map<String, IPoolGroup> getPoolGroups() {
        if (pmgrLock.get()) {
            throw new RuntimeException("This manager is locked. Can't get Connection ");
        }
        return poolGroups;
    }

    @Override
    public final IPoolGroup getPoolGroup(final String poolGroupKey) {
        String key = defaultPoolGroupKey;
        if (poolGroupKey != null) {
            key = poolGroupKey;
        }

        if (!this.poolGroups.containsKey(key)) {
            throw new RuntimeException("KEY:'" + key + "' is not find. Can't get Connection ");
        }
        return this.poolGroups.get(key);
    }

    @Override
    public final IPool getPool(final String poolGroupKey, final String dbName) {
        for (IPool pool : getPoolGroup(poolGroupKey).getPoolList()) {
            if (pool.getDbName().equals(dbName)) {
                return pool;
            }
        }
        throw new UnchkedExecption(new IllegalArgumentException("Not found GroupName :" + poolGroupKey + " DatabaseName :" + dbName));
    }

    @Override
    public final IConnection getConnection(final String poolGroupKey) {
        IPoolGroup group = this.poolGroups.get(poolGroupKey);
        if (group == null) {
            throw new NullPointerException(poolGroupKey + " group is null.");
        }

        List<IPool> pools = new ArrayList<>();
        for (IPool pool : group.getPoolList()) {
            pools.add(pool);
        }
        return  pools.get(0).getConnection();
    }

    @Override
    public final IPoolGroup getPoolGroup() {
        return getPoolGroup(defaultPoolGroupKey);
    }

    @Override
    public final IConnection getConnection() {
        return getConnection(defaultPoolGroupKey);
    }

    @Override
    public final IConnection getConnectionDBOffLoad() {
        return getConnectionDBOffLoad(defaultPoolGroupKey);
    }

    @Override
    public final IConnection getConnectionDBOffLoad(final String poolGroupKey) {
        IPoolGroup group = this.poolGroupsDBOffLoad.get(poolGroupKey);
        if (group == null) {
            throw new NullPointerException(poolGroupKey + " group is null.");
        }

        List<IPool> pools = new ArrayList<>();
        for (IPool pool : group.getPoolList()) {
            pools.add(pool);
        }
        return  pools.get(0).getConnection();

    }

    @Override
    public final IPoolGroup getPoolGroupByForce(final String poolGroupKey) {
        if (!this.poolGroups.containsKey(poolGroupKey)) {
            return null;
        }
        return this.poolGroups.get(poolGroupKey);
    }

    @Override
    public final IConnection getConnectionByForce(final String poolGroupKey) {
        if (!this.poolGroups.containsKey(poolGroupKey)) {
            return null;
        }
        return getConnection(poolGroupKey);
    }

    @Override
    public final int putPoolGroups(final String poolGroupKey, final IPool pool, final boolean alias) {
        IPoolGroup poolGroup = poolGroups.get(poolGroupKey);
        if (poolGroup == null) {
            poolGroup = BaseDBCP.getPoolGroupFactory().create(poolGroupKey, pool, alias);
            poolGroups.put(poolGroupKey, poolGroup);
        } else {
            poolGroup.addPool(pool);
        }
        return poolGroup.getPoolList().size();
    }

    @Override
    public final Set<String> getPoolNames() {
        return this.poolGroups.keySet();
    }

    @Override
    public final void setDefaultPoolGroupKey() {
        this.defaultPoolGroupKey = DBCPConfig.getProperty(DBCP_POOL_FACTORY_KEY + DBCP_DEFAULT + DBCP_GROUP);
    }

    @Override
    public boolean isRefreshMode() {
        String refreshMode = DBCPConfig.getProperty(
                 DBCP_POOL_FACTORY_KEY + DBCP_DEFAULT + DBCP_POOL_REFRESH_MODE , REFRESH_MODE.OFF.name());

        return refreshMode.equals(REFRESH_MODE.ON.name());
    }

    @Override
    public final void run() {
        for (IPoolGroup poolGroup : this.poolGroups.values()) {
            if (!poolGroup.isAlias()) {
                synchronized (this.poolGroups) {
                    for (IPool pool : poolGroup.getPoolList()) {
                        pool.shutdown(poolGroup.getGroupName());
                    }
                }
            }
        }
    }

    @Override
    public void execute() {
        run();
    }

    @Override
    public boolean isExistenceDBOffLoad(String warId) {
        return offloads.containsKey(warId);
    }

    @Override
    public boolean isDBOffLoad(String warId) {
        try {
            return offloads.get(warId).get();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Map<String,AtomicBoolean> getDBOffLoad() {
        return offloads;
    }

    @Override
    public void resistDBOffLoad(String warId) {
        if (!isExistenceDBOffLoad(warId)) {
            offloads.put(warId, new AtomicBoolean(false));
        }
    }

    @Override
    public void setDBOffLoad(String warId, boolean offLoadType) {
        if (isExistenceDBOffLoad(warId)) {
            boolean oldType = isDBOffLoad(warId);
            offloads.get(warId).compareAndSet(oldType, offLoadType);
        }
    }

    @Override
    public void createPoolGroupDBOffLoad() {
        for (Map.Entry<String, IPoolGroup> entry : poolGroups.entrySet()) {
            setPoolGroupDBOffLoad(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void setPoolGroupDBOffLoad(String poolGroupKey, IPoolGroup poolGroup) {
        poolGroupsDBOffLoad.put(poolGroupKey, poolGroup);
    }
}
