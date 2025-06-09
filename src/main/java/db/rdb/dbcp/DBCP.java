package db.rdb.dbcp;

//Core Java
import static db.DBConstants.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import db.LogConstants;
import db.rdb.dbcp.IScheduleManager.SCHEDULER_TYPE;
import db.rdb.dbcp.schedule.tasks.ConnectionRefreshTask;
import db.rdb.tx.ITransaction;
import db.rdb.tx.RedundancyTransaction;
import db.rdb.tx.TransactionMgr;
import system.config.Configure;
import system.core.UnchkedExecption;
import system.util.UString;

public class DBCP {

    public static final int INLIMIT = 1000;

    private static volatile boolean isInitialized = false;

    private static final IPoolManager pmgr = BaseDBCP.getPoolManager();

    private static final IScheduleManager scheduleMgr = BaseDBCP.getScheduleManager();

    private static final Map<String, String> aliaseMap = new HashMap<>();

    private static DBMS_TYPE dbmsType = DBMS_TYPE.ORACLE;

    private static boolean oracleDbms = false;

    private static boolean postgresDbms = false;

    public enum DBMS_TYPE {
        ORACLE, POSTGRES, OTHER
    }

    /**
     * DB初期化処理
     */
    public static synchronized void init() {

        try {
            if (isInitialized) {
                return;
            }

            pmgr.setDefaultPoolGroupKey();

            //プール作成及び初期化処理
            setConfig();
            pmgr.init();

            if (pmgr.isRefreshMode()) {
                scheduleMgr.addScheduler(SCHEDULER_TYPE.CONNECTION_REFRESHING);
                new ConnectionRefreshTask(pmgr.getPoolGroups());
            }

            synchronized (DBCP.class) {
                DBCP.class.notifyAll();
            }

            isInitialized = true;
        } catch (Exception e) {
            throw new UnchkedExecption(e);
        } finally {
        }
    }

    public static boolean isInitialized() {
        return isInitialized;
    }

    public static void waitForInitialization() {
        if (isInitialized) {
            return;
        }
        synchronized (DBCP.class) {
            try {
                while (!isInitialized) {
                    DBCP.class.wait(1000);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static Connection getConnection(final String groupName) {
        return (groupName == null) ? pmgr.getConnection() : pmgr.getConnection(groupName);
    }

    public static Connection getConnection() {
        return pmgr.getConnection();
    }

    public static Connection getConnectionDBOffLoad(final String groupName) {
        return (groupName == null) ? pmgr.getConnectionDBOffLoad() : pmgr.getConnectionDBOffLoad(groupName);
    }

    private static void setConfig() {
        DriverManager.setLoginTimeout(DBCPConfig.getIntProperty(JDBC_KEY + JDBC_TIMEOUT, 10));

        Iterator<?> keys = Configure.getKeys();
        while (keys.hasNext()) {
            String key = keys.next().toString();
            if (key.startsWith(DBCP_PREFIX_KEY + JDBC_KEY + JDBC_DRIVER)) {
                setDbmsType(Configure.getString(key));
            }

            if (matchs(key, DBCP_POOL_FACTORY_KEY, DBCP_URL)) {
                String dbName = subString(key, DBCP_POOL_FACTORY_KEY, DBCP_URL);
                if (DBCP_DEFAULT.equals(dbName)) {
                    continue;
                }
                IPool pool = BaseDBCP.getPoolFactory().create(dbName);
                String groupName = pool.getGroupName();
                pmgr.putPoolGroups(groupName, pool, false);
                // alias
                String[] aliases = Configure.getStringArray(DBCP_PREFIX_KEY + DBCP_POOL_FACTORY_KEY + DBCP_ALIAS + groupName);
                if (aliases != null && 0 < aliases.length) {
                    for (int i = 0; i < aliases.length; i++) {
                        aliaseMap.put(aliases[i], groupName);
                        pmgr.putPoolGroups(aliases[i], pool, true);
                    }
                }
            }
        }

        createPoolGroupForDBOffLoad();

        TransactionMgr.setDefaultTransaction(new RedundancyTransaction());
    }

    private static boolean matchs(final String src, final String prifix, final String suffix) {
        String regex = DBCP_PREFIX_KEY + prifix + "(\\p{ASCII})+" + suffix;
        return matchs(src, regex);
    }

    private static boolean matchs(final String src, final String regex) {
        if (UString.isNull(src)) {
            return false;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(src);
        return matcher.matches();
    }

    private static String subString(final String src, final String prifix, final String suffix) {
        return src.substring(DBCP_PREFIX_KEY.length() + prifix.length(), src.length() - suffix.length());
    }

    public static IPoolManager getPoolMgr() {
        return pmgr;
    }

    public static String getPhysicalGroupName(String aliaseName) {
        String name = aliaseMap.get(aliaseName);
        return name != null ? name : aliaseName;
    }

    public static ITransaction getDefaultTransaction() {
        return TransactionMgr.getDefaultTransaction();
    }

    public static IScheduleManager getSchedulemgr() {
        return scheduleMgr;
    }

    private static void createPoolGroupForDBOffLoad() {
        pmgr.createPoolGroupDBOffLoad();
        for (IPoolGroup poolGroup : pmgr.getPoolGroups().values()) {
            if (poolGroup.isAlias()) {
                continue;
            }
            setAliasPoolForDBOffLoad(poolGroup);
        }
    }

    private static void setAliasPoolForDBOffLoad(IPoolGroup poolGroup) {
        for (IPool pool : poolGroup.getPoolList()) {
            String offload = pool.getSource().getDBOffLoad();
            if (!UString.isNull(offload)) {
                if (offload.equals(pool.getDbName())) {
                    throw new UnchkedExecption("A configuration file has an unset up item. [DBOffLoad=" + offload + "]");
                }
                String poolGroupKey = Configure.getString(DBCP_PREFIX_KEY + DBCP_POOL_FACTORY_KEY + offload + DBCP_GROUP, "");
                if (UString.isNull(poolGroupKey)) {
                    throw new UnchkedExecption("A configuration file has an unset up item. [DBOffLoad=" + offload + "]");
                }
                String[] aliases = Configure.getStringArray(DBCP_PREFIX_KEY + DBCP_POOL_FACTORY_KEY + DBCP_ALIAS + poolGroupKey);
                if (aliases.length > 0) {
                    throw new UnchkedExecption("A configuration file has an unset up item. [" + poolGroupKey + "=" + StringUtils.join(aliases, ",") + "]");
                }
                IPool targetPool = pmgr.getPool(poolGroupKey, offload);
                String targetOffload = targetPool.getSource().getDBOffLoad();
                if (!UString.isNull(targetOffload)) {
                    throw new UnchkedExecption("A configuration file has an unset up item. [DBOffLoad=" + targetOffload + "]");
                }
                aliases = Configure.getStringArray(DBCP_PREFIX_KEY + DBCP_POOL_FACTORY_KEY + DBCP_ALIAS + pool.getGroupName());
                IPoolGroup targetPoolGroup = pmgr.getPoolGroup(targetPool.getSource().getGroup());
                for (String aliase : aliases) {
                    pmgr.setPoolGroupDBOffLoad(aliase, targetPoolGroup);
                }
            }
        }
    }

    public static DBMS_TYPE getDbmsType() {
        return dbmsType;
    }

    private static void setDbmsType(String dbtype) {
        if(dbtype.toLowerCase().contains("oracle")) {
            dbmsType = DBMS_TYPE.ORACLE;
            oracleDbms = true;
        } else if (dbtype.toLowerCase().contains("postgresql")) {
            dbmsType = DBMS_TYPE.POSTGRES;
            postgresDbms = true;
        } else {
            dbmsType = DBMS_TYPE.OTHER;
        }
    }

    public static boolean isOracle() {
        return oracleDbms;
    }

    public static boolean isPostgres() {
        return postgresDbms;
    }
}
