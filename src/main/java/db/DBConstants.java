package db;

/**
 * DB constants
 *
 */
public interface DBConstants {

    // DB config parameters prefix
    String DBCP_PREFIX_KEY              = "dbcp.";

    // DB setting keys
    String DBCP_POOL_FACTORY_KEY        = "pool.factory.";
    String DBCP_DEFAULT                 = "default";

    // JDBC setting keys
    String JDBC_KEY                     = "jdbc.";
    String JDBC_DRIVER                  = "driver.";
    String JDBC_TIMEOUT                 = "login.timeout";

    // DB接続関連
    String DBCP_URL                     = ".url";
    String DBCP_USER                    = ".user";
    String DBCP_PASSWORD                = ".password";
    String DBCP_GROUP                   = ".group";
    String DBCP_VALIDATION_QUERY        = ".validationQuery";
    String DBCP_FETCHSIZE               = ".fetchsize";
    String DBCP_ALIAS                   = "alias.";
    String DBCP_TIMEOUT                 = ".timeout";
    String DBCP_QUERY_TIMEOUT           = ".query.timeout";
    String DBCP_COMMIT_TIMEOUT          = ".commit.timeout";
    String DBCP_CANCEL_TIMEOUT          = ".cancel.timeout";
    String DBCP_ROLLBACKTIMEOUT         = ".rollback.timeout";
    String DBCP_VALIDATION_TIMEOUT      = ".validationQuery.timeout";
    String DBCP_ROLLBACK_SLEEP          = ".rollback.sleep";
    String DBCP_DB_OFF_LOAD             = ".DBOffLoad";

    // RACノード関連
    String DBCP_POOL_RAC_MODE           = ".racMode";
    String DBCP_POOL_NODE_SIZE          = ".nodeSize";
    String DBCP_POOL_NODE_CONN_SIZE     = ".nodeConnectionSize";
    String DBCP_POOL_NODE_SIZE_CHKSQL   = ".nodeSizeCheckQuery";

    // JDBCコネクション数関連
    String DBCP_POOL_RISK_SIZE          = ".riskPoolSize";
    String DBCP_POOL_MINIMUM_SIZE       = ".minimumPoolSize";

    // JDBCコネクションリフレッシュ関連
    String DBCP_POOL_REFRESH_MODE       = ".refreshMode";
    String DBCP_MAX_IDLE                = ".maxIdle";
    String DBCP_REFRESH_TIME            = ".refreshTime";
    String DBCP_WATCH_INTERVAL          = ".watchInterval";
    String DBCP_CREATETIME_MULTIPLE     = ".createTimeMultiple";
    String DBCP_RETURNCHECK_MULTIPLE    = ".returnCheckMultiple";

    // 稼働統計関連
    String DBCP_STATISTICS_INTERVAL     = ".statisticsInterval";
    String DBCP_STATISTICS_LEVEL        = ".statisticsLevel";

    // 停止時設定
    String DBCP_POOL_CLOSE_TIMEOUT      = "close.timeout";
    String DBCP_POOL_CLOSE_RETRYCOUNT   = "close.retrycount";

    // コネクション再接続
    String DBCP_REFRESH_TIMEOUT         = ".refresh.timeout";

    // コネクション取得ブロック関連
    String DBCP_BLOCK_MODE              = "dbcp.jmx.command.dbConnectionBlockMode";

    // コネクション偏りチェック関連
    String DBCP_NODE_CHECK_QUERY         = ".refresh.connectNodeCheckQuery";
    String DBCP_NODE_THRESHOLD           = ".refresh.connectNodeThreshold";
    String DBCP_BIAS_CHECK_MODE          = ".refresh.biasCheckMode";

    // コネクション自動リフレッシュ関連
    String DBCP_REFRESH_RETRYCOUNT        = ".refresh.retryCount";
    String DBCP_REFRESH_RETRYWAITTIME    = ".refresh.retryWaitTime";
    String DBCP_AUTO_REFRESH_MODE         = ".refresh.autoRefreshMode";
    String DBCP_AUTO_REFRESH_THRESHOLD   = ".refresh.autoRefreshThreshold";
    String DBCP_AUTO_REFRESH_INTERVAL    = ".refresh.autoRefreshInterval";

}
