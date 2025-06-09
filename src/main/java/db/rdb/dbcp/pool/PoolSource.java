package db.rdb.dbcp.pool;

import java.sql.Connection;
import java.sql.DriverManager;

import db.rdb.dbcp.IPoolSource;
import system.util.UString;

class PoolSource implements IPoolSource{

    private String url;
    private String user;
    private String password;

    private String group;

    private String validationQuery;
    private int fetchSize;

    private String racMode;
    private int nodeSize;
    private int nodeConnectionSize;
    private String nodeSizeCheckQuery;

    private int riskPoolSize;
    private int minimumPoolSize;

    private String refreshMode;
    private int refreshComandTimeout;
    private long refreshTime;
    private long watchInterval;
    private int createTimeMultiple;
    private int returnCheckMultiple;

    private long statisticsInterval;
    private int statisticsLevel;

    private long queryTimeout;
    private long commitTimeout;
    private long cancelTimeout;
    private long rollbackTimeout;
    private long validationQueryTimeout;
    private long rollbackSleep;

    private String offLoad;

    private String connectNodeCheckQuery;
    private int connectNodeThreshold;
    private String biasCheckMode;

    private int refreshRetryCount;
    private long refreshRetryWaitTime;
    private String autoRefreshMode;
    private int autoRefreshThreshold;
    private long autoRefreshInterval;

    public Connection createConnection() throws Exception {
        return DriverManager.getConnection(url, user, password);
    }

    public boolean isValid() {
        if (UString.isNull(url))
            return false;
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PoolSource == false)
            return false;

        if (((PoolSource)obj).url.equals(this.url) == false)
            return false;
        if (((PoolSource)obj).user.equals(this.user) == false)
            return false;
        if (((PoolSource)obj).password.equals(this.password) == false)
            return false;

        return true;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("url-").append(url).append("\r\n");
        sb.append("user-").append(user).append("\r\n");
        sb.append("password-").append(password).append("\r\n");
        sb.append("group-").append(group).append("\r\n");
        sb.append("validationQuery-").append(validationQuery).append("\r\n");
        sb.append("fetchSize-").append(fetchSize).append("\r\n");
        sb.append("racMode-").append(racMode).append("\r\n");
        sb.append("nodeSize-").append(nodeSize).append("\r\n");
        sb.append("nodeConnectionSize-").append(nodeConnectionSize).append("\r\n");
        sb.append("nodeSizeCheckQuery-").append(nodeSizeCheckQuery).append("\r\n");
        sb.append("riskPoolSize-").append(riskPoolSize).append("\r\n");
        sb.append("minimumPoolSize-").append(minimumPoolSize).append("\r\n");
        sb.append("refreshMode-").append(refreshMode).append("\r\n");
        sb.append("queryTimeout-").append(queryTimeout).append("\r\n");
        sb.append("commitTimeout-").append(commitTimeout).append("\r\n");
        sb.append("cancelTimeout-").append(cancelTimeout).append("\r\n");
        sb.append("rollbackTimeout-").append(rollbackTimeout).append("\r\n");
        sb.append("validationQueryTimeout-").append(validationQueryTimeout).append("\r\n");
        sb.append("rollbackSleep-").append(rollbackSleep).append("\r\n");
        sb.append("DBOffLoad-").append(offLoad).append("\r\n");
        sb.append("connectNodeCheckQuery-").append(connectNodeCheckQuery).append("\r\n");
        sb.append("connectNodeThreshold-").append(connectNodeThreshold).append("\r\n");
        sb.append("biasCheckMode-").append(biasCheckMode).append("\r\n");
        sb.append("refreshRetryCount-").append(refreshRetryCount).append("\r\n");
        sb.append("refreshRetryWaitTime-").append(refreshRetryWaitTime).append("\r\n");
        sb.append("autoRefreshMode-").append(autoRefreshMode).append("\r\n");
        sb.append("autoRefreshThreshold-").append(autoRefreshThreshold).append("\r\n");
        sb.append("autoRefreshInterval-").append(autoRefreshInterval).append("\r\n");
        return sb.toString();
    }

    @Override
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public String getValidationQuery() {
        return validationQuery;
    }

    @Override
    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
    }

    @Override
    public int getFetchSize() {
        return fetchSize;
    }

    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    @Override
    public String getRacMode() {
        return racMode;
    }

    public void setRacMode(String racMode) {
        this.racMode = racMode;
    }

    @Override
    public int getNodeSize() {
        return nodeSize;
    }

    public void setNodeSize(int nodeSize) {
        this.nodeSize = nodeSize;
    }

    @Override
    public int getNodeConnectionSize() {
        return nodeConnectionSize;
    }

    public void setNodeConnectionSize(int nodeConnectionSize) {
        this.nodeConnectionSize = nodeConnectionSize;
    }

    @Override
    public String getNodeSizeCheckQuery() {
        return nodeSizeCheckQuery;
    }

    public void setNodeSizeCheckQuery(String nodeSizeCheckQuery) {
        this.nodeSizeCheckQuery = nodeSizeCheckQuery;
    }

    @Override
    public int getRiskPoolSize() {
        return riskPoolSize;
    }

    public void setRiskPoolSize(int riskPoolSize) {
        this.riskPoolSize = riskPoolSize;
    }

    @Override
    public int getMinimumPoolSize() {
        return minimumPoolSize;
    }

    public void setMinimumPoolSize(int minimumPoolSize) {
        this.minimumPoolSize = minimumPoolSize;
    }

    @Override
    public long getRefreshTime() {
        return refreshTime;
    }

    public void setRefreshTime(long refreshTime) {
        this.refreshTime = refreshTime;
    }

    @Override
    public long getWatchInterval() {
        return watchInterval;
    }

    public void setWatchInterval(long watchInterval) {
        this.watchInterval = watchInterval;
    }

    @Override
    public int getCreateTimeMultiple() {
        return createTimeMultiple;
    }

    public void setCreateTimeMultiple(int createTimeMultiple) {
        this.createTimeMultiple = createTimeMultiple;
    }

    @Override
    public int getReturnCheckMultiple() {
        return returnCheckMultiple;
    }

    public void setReturnCheckMultiple(int returnCheckMultiple) {
        this.returnCheckMultiple = returnCheckMultiple;
    }

    @Override
    public long getStatisticsInterval() {
        return statisticsInterval;
    }

    public void setStatisticsInterval(long statisticsInterval) {
        this.statisticsInterval = statisticsInterval;
    }

    @Override
    public int getStatisticsLevel() {
        return statisticsLevel;
    }

    public void setStatisticsLevel(int statisticsLevel) {
        this.statisticsLevel = statisticsLevel;
    }

    @Override
    public String getRefreshMode() {
        return refreshMode;
    }

    public void setRefreshMode(String refreshMode) {
        this.refreshMode = refreshMode;
    }

    public int getRefreshComandTimeout() {
        return refreshComandTimeout;
    }

    public void setRefreshComandTimeout(int refreshComandTimeout) {
        this.refreshComandTimeout = refreshComandTimeout;
    }

    @Override
    public long getQueryTimeout() {
        return queryTimeout;
    }

    public void setQueryTimeout(long queryTimeout) {
        this.queryTimeout = queryTimeout;
    }

    @Override
    public long getCommitTimeout() {
        return commitTimeout;
    }

    public void setCommitTimeout(long commitTimeout) {
        this.commitTimeout = commitTimeout;
    }

    @Override
    public long getCancelTimeout() {
        return cancelTimeout;
    }

    public void setCancelTimeout(long cancelTimeout) {
        this.cancelTimeout = cancelTimeout;
    }

    @Override
    public long getRollbackTimeout() {
        return rollbackTimeout;
    }

    public void setRollbackTimeout(long rollbackTimeout) {
        this.rollbackTimeout = rollbackTimeout;
    }

    @Override
    public long getValidationQueryTimeout() {
        return validationQueryTimeout;
    }

    public void setValidationQueryTimeout(long validationQueryTimeout) {
        this.validationQueryTimeout = validationQueryTimeout;
    }

    @Override
    public long getRollbackSleep() {
        return rollbackSleep;
    }

    public void setRollbackSleep(long rollbackSleep) {
        this.rollbackSleep = rollbackSleep;
    }

    @Override
    public String getConnectNodeCheckQuery() {
        return connectNodeCheckQuery;
    }

    public void setConnectNodeCheckQuery(String connectNodeCheckQuery) {
        this.connectNodeCheckQuery = connectNodeCheckQuery;
    }

    @Override
    public int getConnectNodeThreshold() {
        return connectNodeThreshold;
    }

    public void setConnectNodeThreshold(int connectNodeThreshold) {
        this.connectNodeThreshold = connectNodeThreshold;
    }

    @Override
    public String getBiasCheckMode() {
        return biasCheckMode;
    }

    public void setBiasCheckMode(String biasCheckMode) {
        this.biasCheckMode = biasCheckMode;
    }

    @Override
    public int getRefreshRetryCount() {
        return refreshRetryCount;
    }

    public void setRefreshRetryCount(int refreshRetryCount) {
        this.refreshRetryCount = refreshRetryCount;
    }

    @Override
    public long getRefreshRetryWaitTime() {
        return refreshRetryWaitTime;
    }

    public void setRefreshRetryWaitTime(long refreshRetryWaitTime) {
        this.refreshRetryWaitTime = refreshRetryWaitTime;
    }

    @Override
    public String getAutoRefreshMode() {
        return autoRefreshMode;
    }

    public void setAutoRefreshMode(String autoRefreshMode) {
        this.autoRefreshMode = autoRefreshMode;
    }

    @Override
    public int getAutoRefreshThreshold() {
        return autoRefreshThreshold;
    }

    public void setAutoRefreshThreshold(int autoRefreshThreshold) {
        this.autoRefreshThreshold = autoRefreshThreshold;
    }

    @Override
    public long getAutoRefreshInterval() {
        return autoRefreshInterval;
    }

    public void setAutoRefreshInterval(long autoRefreshInterval) {
        this.autoRefreshInterval = autoRefreshInterval;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String getDBOffLoad() {
        return offLoad;
    }

    public void setDBOffLoad(String offload) {
        this.offLoad = offload;
    }

    // ----- getter and setter -----

}

