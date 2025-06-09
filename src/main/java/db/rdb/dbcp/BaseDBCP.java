package db.rdb.dbcp;

import db.rdb.tx.ITransaction;

public class BaseDBCP {

    private static IPoolManager poolManager;

    private static IConnectionFactory dbConnectionFactory;

    private static IPoolFactory poolFactory;

    private static IPoolGroupFactory PoolGroupFactory;

    private static IScheduleManager scheduleManager;

    private static ITransaction tx;

    public static void register(IPoolManager f) {
        poolManager = f;
    }

    public static IPoolManager getPoolManager() {
        return poolManager;
    }

    public static void registerFactory(IConnectionFactory f) {
        dbConnectionFactory = f;
    }

    public static IConnectionFactory getDBConnectionFactory() {
        return dbConnectionFactory;
    }

    public static void registerFactory(IPoolFactory f) {
        poolFactory = f;
    }

    public static IPoolFactory getPoolFactory() {
        return poolFactory;
    }

    public static void registerFactory(IPoolGroupFactory f) {
        PoolGroupFactory = f;
    }

    public static IPoolGroupFactory getPoolGroupFactory() {
        return PoolGroupFactory;
    }

    public static void registerFactory(IScheduleManager f) {
    	scheduleManager = f;
    }

    public static void register(ITransaction traMgr) {
        tx = traMgr;
    }

    public static ITransaction getTransaction() {
        return tx;
    }

    public static final IScheduleManager getScheduleManager() {
        return scheduleManager;
    }
}
