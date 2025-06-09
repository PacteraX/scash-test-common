package db.rdb.dbcp;

/**
 * コネクションプールに必要な情報を保持します.
 * @author satoh
 */
public interface IPoolSource {

    String getUrl();
    String getUser();
    String getPassword();
    String getGroup();

    public String getRefreshMode();
    long getRefreshTime();
    long getWatchInterval();
    int getCreateTimeMultiple();
    int getReturnCheckMultiple();

    long getStatisticsInterval();
    int getStatisticsLevel();

    long getQueryTimeout();
    long getCommitTimeout();
    long getCancelTimeout();
    long getRollbackTimeout();
    long getValidationQueryTimeout();
    int getFetchSize();
    long getRollbackSleep();
    public String getValidationQuery();
    public void setValidationQuery(String validationQuery);

    public String getRacMode();
    public int getNodeSize() ;
    public int getNodeConnectionSize();
    public String getNodeSizeCheckQuery();

    public int getRiskPoolSize();
    public int getMinimumPoolSize();
    public String getDBOffLoad();

    String getConnectNodeCheckQuery();
    int getConnectNodeThreshold();
    String getBiasCheckMode();

    int getRefreshRetryCount();
    long getRefreshRetryWaitTime();
    String getAutoRefreshMode();
    int getAutoRefreshThreshold();
    long getAutoRefreshInterval();
}
