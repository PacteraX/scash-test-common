package db.rdb.dbcp.pool;

import static db.DBConstants.DBCP_AUTO_REFRESH_INTERVAL;
import static db.DBConstants.DBCP_AUTO_REFRESH_MODE;
import static db.DBConstants.DBCP_AUTO_REFRESH_THRESHOLD;
import static db.DBConstants.DBCP_BIAS_CHECK_MODE;
import static db.DBConstants.DBCP_CANCEL_TIMEOUT;
import static db.DBConstants.DBCP_REFRESH_TIMEOUT;
import static db.DBConstants.DBCP_COMMIT_TIMEOUT;
import static db.DBConstants.DBCP_CREATETIME_MULTIPLE;
import static db.DBConstants.DBCP_DB_OFF_LOAD;
import static db.DBConstants.DBCP_DEFAULT;
import static db.DBConstants.DBCP_FETCHSIZE;
import static db.DBConstants.DBCP_GROUP;
import static db.DBConstants.DBCP_NODE_CHECK_QUERY;
import static db.DBConstants.DBCP_NODE_THRESHOLD;
import static db.DBConstants.DBCP_PASSWORD;
import static db.DBConstants.DBCP_POOL_FACTORY_KEY;
import static db.DBConstants.DBCP_POOL_MINIMUM_SIZE;
import static db.DBConstants.DBCP_POOL_NODE_CONN_SIZE;
import static db.DBConstants.DBCP_POOL_NODE_SIZE;
import static db.DBConstants.DBCP_POOL_NODE_SIZE_CHKSQL;
import static db.DBConstants.DBCP_POOL_RAC_MODE;
import static db.DBConstants.DBCP_POOL_REFRESH_MODE;
import static db.DBConstants.DBCP_POOL_RISK_SIZE;
import static db.DBConstants.DBCP_QUERY_TIMEOUT;
import static db.DBConstants.DBCP_REFRESH_RETRYCOUNT;
import static db.DBConstants.DBCP_REFRESH_RETRYWAITTIME;
import static db.DBConstants.DBCP_REFRESH_TIME;
import static db.DBConstants.DBCP_RETURNCHECK_MULTIPLE;
import static db.DBConstants.DBCP_ROLLBACKTIMEOUT;
import static db.DBConstants.DBCP_ROLLBACK_SLEEP;
import static db.DBConstants.DBCP_STATISTICS_INTERVAL;
import static db.DBConstants.DBCP_STATISTICS_LEVEL;
import static db.DBConstants.DBCP_URL;
import static db.DBConstants.DBCP_USER;
import static db.DBConstants.DBCP_VALIDATION_QUERY;
import static db.DBConstants.DBCP_VALIDATION_TIMEOUT;
import static db.DBConstants.DBCP_WATCH_INTERVAL;

import db.rdb.dbcp.DBCPConfig;

class PoolSourceFactory {
    private static final long DEFULT_REFRESH_TIME = 15*60*1000;
    private static final long DEFULT_WATCH_INTERVAL = 60*1000;
    private static final long DEFULT_STATISTICS_INTERVAL = 10*1000;
    private static final int DEFULT_STATISTICS_LEVEL = 0;
    private static final int DEFULT_CREATETIME_MULTIPLE = 5;
    private static final int DEFULT_RETURNCHECK_MULTIPLE = 5;
    private static final long DEFULT_AUTO_REFRESH_INTERVAL = 10*1000;

    // ----- static -----
    private static PoolSource defaultSource = null;

    public static PoolSource newInstance(String dbName) {
        if (defaultSource == null)
            defaultSource = newInstance();

        String prefix = DBCP_POOL_FACTORY_KEY + dbName;
        PoolSource source = new PoolSource();
        // 必須
        source.setUrl(DBCPConfig.getProperty(prefix + DBCP_URL));
        source.setUser(DBCPConfig.getProperty(prefix + DBCP_USER));
        source.setPassword(DBCPConfig.getProperty(prefix + DBCP_PASSWORD));

        // 拡張(デフォルト値有り)
        source.setGroup(DBCPConfig.getProperty(prefix + DBCP_GROUP, defaultSource.getGroup()));
        source.setValidationQuery(DBCPConfig.getProperty(prefix + DBCP_VALIDATION_QUERY, defaultSource.getValidationQuery()));
        source.setFetchSize(DBCPConfig.getIntProperty(prefix + DBCP_FETCHSIZE, defaultSource.getFetchSize()));

        source.setRacMode(DBCPConfig.getProperty(prefix + DBCP_POOL_RAC_MODE, defaultSource.getRacMode()));
        source.setNodeSize(DBCPConfig.getIntProperty(prefix + DBCP_POOL_NODE_SIZE, defaultSource.getNodeSize()));
        source.setNodeConnectionSize(DBCPConfig.getIntProperty(prefix + DBCP_POOL_NODE_CONN_SIZE, defaultSource.getNodeConnectionSize()));
        source.setNodeSizeCheckQuery(DBCPConfig.getProperty(prefix + DBCP_POOL_NODE_SIZE_CHKSQL, defaultSource.getNodeSizeCheckQuery()));

        source.setRiskPoolSize(DBCPConfig.getIntProperty(prefix + DBCP_POOL_RISK_SIZE, defaultSource.getRiskPoolSize()));
        source.setMinimumPoolSize(DBCPConfig.getIntProperty(prefix + DBCP_POOL_MINIMUM_SIZE, defaultSource.getMinimumPoolSize()));
        source.setDBOffLoad(DBCPConfig.getProperty(prefix + DBCP_DB_OFF_LOAD, defaultSource.getDBOffLoad()));

        source.setRefreshMode(defaultSource.getRefreshMode());
        source.setRefreshComandTimeout(defaultSource.getRefreshComandTimeout());
        source.setRefreshTime(defaultSource.getRefreshTime());
        source.setWatchInterval(defaultSource.getWatchInterval());
        source.setStatisticsInterval(defaultSource.getStatisticsInterval());
        source.setStatisticsLevel(defaultSource.getStatisticsLevel());
        source.setCreateTimeMultiple(defaultSource.getCreateTimeMultiple());
        source.setReturnCheckMultiple(defaultSource.getReturnCheckMultiple());
        source.setQueryTimeout(defaultSource.getQueryTimeout());
        source.setCommitTimeout(defaultSource.getCommitTimeout());
        source.setRollbackTimeout(defaultSource.getRollbackTimeout());
        source.setCancelTimeout(defaultSource.getCancelTimeout());
        source.setValidationQueryTimeout(defaultSource.getValidationQueryTimeout());
        source.setRollbackSleep(defaultSource.getRollbackSleep());

        source.setConnectNodeCheckQuery(DBCPConfig.getProperty(prefix + DBCP_NODE_CHECK_QUERY, defaultSource.getConnectNodeCheckQuery()));
        source.setConnectNodeThreshold(DBCPConfig.getIntProperty(prefix + DBCP_NODE_THRESHOLD, defaultSource.getConnectNodeThreshold()));
        source.setBiasCheckMode(DBCPConfig.getProperty(prefix +  DBCP_BIAS_CHECK_MODE, defaultSource.getBiasCheckMode()));

        source.setRefreshRetryCount(DBCPConfig.getIntProperty(prefix + DBCP_REFRESH_RETRYCOUNT, defaultSource.getRefreshRetryCount()));
        source.setRefreshRetryWaitTime(DBCPConfig.getLongProperty(prefix + DBCP_REFRESH_RETRYWAITTIME, defaultSource.getRefreshRetryWaitTime()));
        source.setAutoRefreshMode(DBCPConfig.getProperty(prefix +  DBCP_AUTO_REFRESH_MODE, defaultSource.getAutoRefreshMode()));
        source.setAutoRefreshThreshold(DBCPConfig.getIntProperty(prefix + DBCP_AUTO_REFRESH_THRESHOLD, defaultSource.getAutoRefreshThreshold()));
        source.setAutoRefreshInterval(DBCPConfig.getLongProperty(prefix + DBCP_AUTO_REFRESH_INTERVAL, defaultSource.getAutoRefreshInterval()));

        return source;
    }

    // ----------- private methods --------- //
    private static PoolSource newInstance() {
        String prefix = DBCP_POOL_FACTORY_KEY + DBCP_DEFAULT;
        PoolSource source = new PoolSource();
        source.setGroup(DBCPConfig.getProperty(prefix + DBCP_GROUP));
        source.setValidationQuery(DBCPConfig.getProperty(prefix + DBCP_VALIDATION_QUERY));
        source.setFetchSize(DBCPConfig.getIntProperty(prefix + DBCP_FETCHSIZE));

        source.setRacMode(DBCPConfig.getProperty(prefix + DBCP_POOL_RAC_MODE));
        source.setNodeSize(DBCPConfig.getIntProperty(prefix + DBCP_POOL_NODE_SIZE));
        source.setNodeConnectionSize(DBCPConfig.getIntProperty(prefix + DBCP_POOL_NODE_CONN_SIZE));
        source.setNodeSizeCheckQuery(DBCPConfig.getProperty(prefix + DBCP_POOL_NODE_SIZE_CHKSQL));

        source.setRiskPoolSize(DBCPConfig.getIntProperty(prefix + DBCP_POOL_RISK_SIZE));
        source.setMinimumPoolSize(DBCPConfig.getIntProperty(prefix + DBCP_POOL_MINIMUM_SIZE));

        source.setRefreshMode(DBCPConfig.getProperty(prefix + DBCP_POOL_REFRESH_MODE, "OFF"));
        source.setRefreshComandTimeout(DBCPConfig.getIntProperty(prefix + DBCP_REFRESH_TIMEOUT, 5000));
        source.setDBOffLoad(DBCPConfig.getProperty(prefix + DBCP_DB_OFF_LOAD, ""));

        source.setRefreshTime(DBCPConfig.getLongProperty(prefix + DBCP_REFRESH_TIME, DEFULT_REFRESH_TIME));
        source.setWatchInterval(DBCPConfig.getLongProperty(prefix + DBCP_WATCH_INTERVAL, DEFULT_WATCH_INTERVAL));
        source.setStatisticsInterval(DBCPConfig.getLongProperty(prefix + DBCP_STATISTICS_INTERVAL, DEFULT_STATISTICS_INTERVAL));
        source.setStatisticsLevel(DBCPConfig.getIntProperty(prefix + DBCP_STATISTICS_LEVEL, DEFULT_STATISTICS_LEVEL));
        source.setCreateTimeMultiple(DBCPConfig.getIntProperty(prefix + DBCP_CREATETIME_MULTIPLE, DEFULT_CREATETIME_MULTIPLE));
        source.setReturnCheckMultiple(DBCPConfig.getIntProperty(prefix + DBCP_RETURNCHECK_MULTIPLE, DEFULT_RETURNCHECK_MULTIPLE));
        source.setQueryTimeout(DBCPConfig.getLongProperty(prefix + DBCP_QUERY_TIMEOUT));
        source.setCommitTimeout(DBCPConfig.getLongProperty(prefix + DBCP_COMMIT_TIMEOUT));
        source.setRollbackTimeout(DBCPConfig.getLongProperty(prefix + DBCP_ROLLBACKTIMEOUT));
        source.setCancelTimeout(DBCPConfig.getLongProperty(prefix + DBCP_CANCEL_TIMEOUT));
        source.setValidationQueryTimeout(DBCPConfig.getLongProperty(prefix + DBCP_VALIDATION_TIMEOUT));
        source.setRollbackSleep(DBCPConfig.getLongProperty(prefix + DBCP_ROLLBACK_SLEEP));

        source.setConnectNodeCheckQuery(DBCPConfig.getProperty(prefix + DBCP_NODE_CHECK_QUERY));
        source.setConnectNodeThreshold(DBCPConfig.getIntProperty(prefix +  DBCP_NODE_THRESHOLD));
        source.setBiasCheckMode(DBCPConfig.getProperty(prefix +  DBCP_BIAS_CHECK_MODE, "OFF"));

        source.setRefreshRetryCount(DBCPConfig.getIntProperty(prefix + DBCP_REFRESH_RETRYCOUNT));
        source.setRefreshRetryWaitTime(DBCPConfig.getLongProperty(prefix + DBCP_REFRESH_RETRYWAITTIME));
        source.setAutoRefreshMode(DBCPConfig.getProperty(prefix +  DBCP_AUTO_REFRESH_MODE, "OFF"));
        source.setAutoRefreshThreshold(DBCPConfig.getIntProperty(prefix + DBCP_AUTO_REFRESH_THRESHOLD));
        source.setAutoRefreshInterval(DBCPConfig.getLongProperty(prefix + DBCP_AUTO_REFRESH_INTERVAL, DEFULT_AUTO_REFRESH_INTERVAL));

        return source;
    }

}
