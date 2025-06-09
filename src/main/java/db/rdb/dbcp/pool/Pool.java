package db.rdb.dbcp.pool;

import static db.DBConstants.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.configuration2.CompositeConfiguration;

import db.LogConstants;
import db.rdb.ConnectionBiasCheckFailureException;
import db.rdb.DBConnectionBlockCtrl;
import db.rdb.DBConnectionBlockException;
import db.rdb.dbcp.BaseDBCP;
import db.rdb.dbcp.DBCP;
import db.rdb.dbcp.IPool;
import db.rdb.dbcp.IPoolSource;
import db.rdb.dbcp.IConnection;
import db.rdb.dbcp.IScheduleManager.SCHEDULER_TYPE;
import db.rdb.dbcp.IScheduler;
import db.rdb.dbcp.schedule.tasks.AutoConnectionRefreshTask;
import db.rdb.dbcp.schedule.tasks.ConnectionCreateTimeoutChecker;
import system.boot.Finalizer;
import system.config.Configure;
import system.core.UnchkedExecption;
import system.finalize.IFinalize;

class Pool implements IPool {
    private final AtomicInteger poolCount;

    private final AtomicInteger reservations;

    private final PoolQueue queue;

    private PoolSource source ;

    private String dbName;

    private static int retry_max_count = 10;

    private static int shutdown_wait_time = 1000;

    private static int poll_wait_time = 1000;

    private static int refresh_wait_time = 100;

    private enum RAC_MODE {ON,OFF}

    private boolean isRefreshMode = false ;

    private enum REFRESH_MODE {ON,OFF}

    private enum AUTO_REFRESH_MODE {ON,OFF}

    private IScheduler scheduler = DBCP.getSchedulemgr().getScheduler(SCHEDULER_TYPE.CONNECTION_TIMEOUT_CHKER);
    private ConnectionCreateTimeoutChecker timeoutChecker;

    private IScheduler autoRefreshScheduler = DBCP.getSchedulemgr().getScheduler(SCHEDULER_TYPE.AUTO_CONNECTION_REFRESHING);
    private AutoConnectionRefreshTask autoRefreshTask;

    private final Object connectionRefreshLock;

    private enum BIAS_CHECK_MODE {ON,OFF}

    private static final String NODE_COUNT = "NODE_COUNT";

    public Pool(final String dbName) {
        source = PoolSourceFactory.newInstance(dbName);
        this.queue = new PoolQueue();
        this.poolCount = new AtomicInteger(0);
        this.reservations = new AtomicInteger(0);
        this.dbName = dbName;
        if (Configure.containsKey(DBCP_DEFAULT + DBCP_POOL_FACTORY_KEY + DBCP_POOL_CLOSE_RETRYCOUNT)) {
            retry_max_count = Configure.getInt(DBCP_DEFAULT + DBCP_POOL_FACTORY_KEY + DBCP_POOL_CLOSE_RETRYCOUNT);
        }
        if (Configure.containsKey(DBCP_DEFAULT + DBCP_POOL_FACTORY_KEY + DBCP_POOL_CLOSE_TIMEOUT)) {
            shutdown_wait_time = Configure.getInt(DBCP_DEFAULT + DBCP_POOL_FACTORY_KEY + DBCP_POOL_CLOSE_TIMEOUT);
        }
        if (Configure.containsKey(DBCP_PREFIX_KEY + DBCP_POOL_FACTORY_KEY + DBCP_DEFAULT + DBCP_POOL_REFRESH_MODE)) {
            isRefreshMode = REFRESH_MODE.ON.name().equals(
                    Configure.getString(DBCP_PREFIX_KEY + DBCP_POOL_FACTORY_KEY + DBCP_DEFAULT + DBCP_POOL_REFRESH_MODE));
        }
        connectionRefreshLock = new Object();
    }

    @Override
    public IConnection getConnection() {
        if (DBConnectionBlockCtrl.isBlockage()) {
            throw new DBConnectionBlockException();
        }
        try {
            reservations.incrementAndGet();
            return borrow();
        } catch (Exception e) {
            throw new UnchkedExecption(e);
        } finally {
            reservations.decrementAndGet();
        }
    }

    @Override
    public void returnTo(final IConnection con) {
        if (isRefreshMode) {
            checkAndReturn(con, source.getRefreshTime() + (source.getWatchInterval() * source.getReturnCheckMultiple()));
        } else {
            checkAndReturn(con);
        }
    }

    public void checkAndReturn(final IConnection con) {
        this.queue.add(con);
    }

    public void checkAndReturn(final IConnection con, long refreshTime) {
        if ((System.currentTimeMillis() > (con.getCreateTime() + refreshTime))) {
            try {
                if (con.checkAndshutdown(CONN_SHUTDOWN_EVENT.SCHEDULE)) {
                    this.queue.add(create());
                } else {
                    this.queue.add(con);
                }
                return;
            } catch (Throwable e) {
                System.out.println("Connection create error. " + e);
            }
        } else {
            this.queue.add(con);
        }
    }

    @Override
    public IConnection create() throws Exception {
        this.setTimeoutSchedule();
        IConnection con;
        try {
             con = BaseDBCP.getDBConnectionFactory().create(this, this.source.createConnection());
             synchronized (this) {
                 this.poolCount.incrementAndGet();
            }
        } finally {
            this.checkError();
        }
        return con;
    }

    IConnection borrow() {
        IConnection con = null;
        con = this.queue.take();
        if (con == null) {
            String msg = MessageFormat.format(LogConstants.WARN_DBW1023,this.getPoolCount(),this.dbName);
            throw new RuntimeException(msg);
        }
        return con;
    }

    /**
     * increase connection to minimum.
     */
    @Override
    public void increaseConnection() {

        Throwable ex = null;

        int poolSize = 0;
        int racNodeSize = 1;
        int nodeConnectionSize = this.source.getNodeConnectionSize();
        int currentRacNodeSize = 1;
        try {
            if (isRacMode()) {
                currentRacNodeSize = getCurrentRacNodeSize();
                racNodeSize = this.source.getNodeSize();

                //生存DBノード数 ＝ 全ノード数
                if (currentRacNodeSize == racNodeSize) {
                    poolSize = currentRacNodeSize * nodeConnectionSize;

                //生存DBノード数 ＜ 全ノード数
                } else if (currentRacNodeSize < racNodeSize) {
                    poolSize = currentRacNodeSize * nodeConnectionSize;

                //生存DBノード数 ＞ 全ノード数
                } else {
                    poolSize = racNodeSize * nodeConnectionSize;
                }
            } else {
                poolSize = racNodeSize * nodeConnectionSize;
            }
        } catch (Exception e) {
            throw e;
        }

        for (int i = this.poolCount.intValue(); i < poolSize; i++) {
            try {
                this.queue.add(create());
            } catch (Throwable e) {
                ex = e;
            }
        }
        if (ex != null) {
            try {
                suspendAllConnection();
            } catch (Throwable e) {
            }
            throw new UnchkedExecption("Connection pool suspended. -> database : " + this.dbName, ex);
        }

        // 偏りチェック
        BIAS_CHECK_RESULT bias_check_result = checkConnectionBias();
        if(bias_check_result == BIAS_CHECK_RESULT.BIASED) {
            int i;
            int refresh_retry_count = this.source.getRefreshRetryCount();
            for (i = 0; i < refresh_retry_count; i++) {
                try {
                    refreshAllConnection(this.source.getRefreshComandTimeout());
                    break;
                } catch(Throwable e) {
                }
                if (i == (refresh_retry_count - 1)) {
                }
            }
        }
    }

    /**
     * 生存DBノード数を取得
     */
    public int getCurrentRacNodeSize() {
        if (!this.isRacMode()) {
            return 1;
        }

        int nodeSize = 0;
        try {
            try (Connection connection = this.source.createConnection();
                    PreparedStatement pstm = connection.prepareStatement(this.source.getNodeSizeCheckQuery());) {
                try (ResultSet rset = pstm.executeQuery()) {
                    while (rset.next()) {
                        nodeSize = rset.getInt(1);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve RAC node size from the database.",e);
        }
        return nodeSize;
    }


    /**
     * 再接続すべきコネクション数を取得
     */
    public int getTargetConSize(int actNodeSize){

        int allNode = this.source.getNodeSize();

        int conRefSize = 0;
        if(actNodeSize <= allNode) {
            conRefSize = actNodeSize * this.source.getNodeConnectionSize();
        }else {
            conRefSize = this.source.getNodeSize() * this.source.getNodeConnectionSize();
        }
        return conRefSize;
    }

    /**
     * @throws Throwable
     */
    public void suspendAllConnection() throws Throwable {
        synchronized (this.queue) {
            String dbName = null;
            while (0 < this.queue.size()) {
                IConnection con = this.queue.take(poll_wait_time);
                if (con==null) {
                    continue;
                }
                try {
                    dbName = con.getDbName();
                    con.shutdown();
                }catch (Throwable ex) {
                }
            }
        }
    }

    /**
     * シャットダウン処理. コネクションプールのステータスを切離しに設定後、すべてのコネクションをクローズします。<BR/>
     * 使用中のコネクションがある場合は、タイムアウト設定値msウェイトしてから、再度クローズを試みます。<BR/>
     * リトライアウトした場合はログ出力して、システムを終了します。
     */
    @Override
    public void shutdown(final String groupName) {
        StringBuilder msg;
        int retryCount = 0;
        while (0 < this.poolCount.get() && !(retry_max_count < retryCount)) {
            while (0 < this.queue.size()) {
                IConnection con = this.queue.take(poll_wait_time);
                if (con==null) {
                    continue;
                }
                try {
                    con.shutdown();
                } catch (Throwable ex) {
                }
                this.poolCount.decrementAndGet();
            }
            if (0 < this.poolCount.get()) {
                synchronized (Pool.class) {
                    try {
                        Pool.class.wait(shutdown_wait_time);
                        retryCount++;
                    } catch (InterruptedException ex) {
                        msg = new StringBuilder();
                        msg.append(LogConstants.ERROR_DBE2001);
                    }
                }
            }
        }
        if (retry_max_count < retryCount) {
            msg = new StringBuilder();
            msg.append(MessageFormat.format(LogConstants.INFO_DBI2002, groupName, this.dbName));
        }
    }

    @Override
    public String refreshAllConnection(long timeout) throws Throwable{
        synchronized (connectionRefreshLock) {
            long start = System.currentTimeMillis();

            String rst    = null;
            int poolSize  = this.getPoolCount().intValue();         //全コネクション数
            int bfActNode = this.getCurrentRacNodeSize();           //生存DBノード数

            if(bfActNode == 0){
                rst = MessageFormat.format(LogConstants.ERROR_DBE1025,this.source.getGroup());
                throw new Throwable(rst);
            }

            int refreshSize = this.getTargetConSize(bfActNode);     //再接続予定コネクション数

            //「 実際接続数 > プール内の接続数」の場合、
            if (refreshSize > poolSize) {
                refreshSize = poolSize;
                rst = MessageFormat.format(LogConstants.ERROR_DBE1015, refreshSize,poolSize);
            }

            int closeSize = poolSize - refreshSize;                 //クローズ予定コネクション数

            //リフレッシュ処理
            AtomicInteger refreshd = new AtomicInteger(0);
            AtomicInteger closed = new AtomicInteger(0);
            try {
                //リフレッシュ処理
                refresh(start,timeout,refreshd,refreshSize,true);
                //切断処理
                refresh(start,timeout,closed,closeSize,false);
            } catch (Throwable th) {
                rst = MessageFormat.format(th.getMessage(), refreshd, closed, refreshSize, this.getPoolCount().intValue(), System.currentTimeMillis()-start);
                throw new Throwable(rst);
            }

            // プール内のコネクション数 < 接続数最低限以下
            int afActNode = this.getCurrentRacNodeSize();
            //生存DBノード数取得失敗
            if(afActNode == 0) {
                rst = MessageFormat.format(LogConstants.ERROR_DBE1025, this.source.getGroup());
                throw new Throwable(rst);
            }

            if(bfActNode != afActNode){
                rst = MessageFormat.format(LogConstants.ERROR_DBE1026, this.source.getGroup());
                throw new Throwable(rst);
            }

            // 偏りチェック
            if(checkConnectionBias() == BIAS_CHECK_RESULT.BIASED) {
                rst = MessageFormat.format(LogConstants.ERROR_DBE1063, this.source.getGroup());
                throw new ConnectionBiasCheckFailureException(rst);
            }

            // 終了ログ
            rst = MessageFormat.format(LogConstants.INFO_DBI1003, bfActNode, afActNode, refreshd, closed, refreshSize, this.getPoolCount().intValue(), System.currentTimeMillis()-start);
        return rst;
        }
    }

    private void refresh(long startTime, long timeout, AtomicInteger from, int to, boolean isRefresh) throws Throwable {
        while (from.get()<to) {
            if (System.currentTimeMillis() >= startTime+timeout) {
                throw new Throwable(LogConstants.ERROR_DBE1014);
            }

            IConnection con = this.queue.take(poll_wait_time);
            try {
                if (con == null ) {
                    continue;
                }

                if(con .getOriginalCreateTime()<=startTime){
                    disConnection(con);
                    if (isRefresh) {
                        Thread.sleep(refresh_wait_time);
                        this.queue.add(create());
                    }
                    from.incrementAndGet();
                } else {
                    con .close();
                }
            } catch(Throwable e) {
                throw new Throwable(LogConstants.ERROR_DBE1013);
            }
        }
    }

    /**
     * コネクション数回復
     */
    @Override
    public String connectionIncrement()throws Throwable {
        synchronized (connectionRefreshLock) {

            int poolSize    = this.getPoolCount().intValue();                // 全コネクション数
            int bfActNode   = this.getCurrentRacNodeSize();                  // 生存DBノード数
            String msg      = null;

            //生存DBノード数取得失敗
            if(bfActNode == 0) {
                msg = MessageFormat.format(LogConstants.ERROR_DBE1025, this.source.getGroup());
                throw new Throwable(msg);
            }

            int increseSize = this.getTargetConSize(bfActNode);              // 接続するコネクション数

            if(increseSize < poolSize){
                msg = MessageFormat.format(LogConstants.ERROR_DBE1028,increseSize,Integer.valueOf(poolSize));
                throw new Throwable(msg);
            }

            long start  = System.currentTimeMillis();

            int cnt = 0;
            for (int i=0; i < increseSize-poolSize; i++) {
                try{
                    this.queue.add(create());
                    cnt++;
                }catch(Throwable e) {
                    msg = LogConstants.ERROR_DBE1040;
                    throw new Throwable(msg);
                }
            }

            int afActNode = this.getCurrentRacNodeSize();

            //生存DBノード数取得失敗
            if(afActNode == 0) {
                msg = MessageFormat.format(LogConstants.ERROR_DBE1025, this.source.getGroup());
                throw new Throwable(msg);

            }

            if(bfActNode != afActNode) {
                msg = MessageFormat.format(LogConstants.ERROR_DBE1026, this.source.getGroup());
                throw new Throwable(msg);
            }
            msg = MessageFormat.format(LogConstants.INFO_DBI1006, bfActNode, afActNode, cnt, increseSize, this.getPoolCount().intValue(), System.currentTimeMillis()-start);
            return msg;
        }
    }

    @Override
    public String connectionFaultDetection() throws Throwable{
        synchronized (connectionRefreshLock) {

            PreparedStatement pstmt = null;
            IConnection con      = null;
            String msg              = null;
            SQLException err        = null;
            int queueSize           = this.queue.size();           // 待機中コネクション数
            long time               = System.currentTimeMillis();

            int closed = 0;

            for(int i=0; i<queueSize; i++){
                err = null;
                try {
                    con = this.queue.poll();
                    if(con == null){
                        break;
                    }
                    pstmt = con.prepareStatement(this.source.getValidationQuery());
                    pstmt.execute();
                }catch(SQLException sqlEx){
                    err = sqlEx;
                } finally {
                    try {
                        if (pstmt != null) {
                            pstmt.close();
                        }
                    } catch (SQLException e) {
                    }
                    try {
                        if(con != null) {
                            if (err == null) {
                                con.close();
                            } else {
                                closed++;
                                disConnection(con);
                            }
                        }
                    } catch (Throwable e) {
                    }
                }
            }

            msg = MessageFormat.format(LogConstants.INFO_DBI1012, closed, this.getPoolCount().intValue() ,System.currentTimeMillis()-time);
            return msg;
        }
    }

    /**
     * コネクション切断
     * @param con
     * @throws Throwable
     */
    public void disConnection(final IConnection con) throws Throwable {

        try {
            // コネクション切断
            con.shutdown();
        } finally {
            synchronized (this) {
                this.poolCount.decrementAndGet();
            }
        }
    }

    @Override
    public void refreshWaitConnection() {
        int max = this.queue.size();
        for (int i = 0; i < max; i++) {
            IConnection con = this.queue.poll();
            try {
                if (con == null) {
                    break;
                }
                checkAndReturn(con, source.getRefreshTime());
            }catch(Exception e){
            }
        }
    }

    @Override
    public int getConnectionSize() {
        int ret = queue.size() - reservations.get();
        return ret >= 0 ? ret : 0;
    }

    public boolean isRacMode() {
        return this.source.getRacMode().equals(RAC_MODE.ON.name());
    }

    public boolean isRefreshMode() {
        return isRefreshMode;
    }

    public void setTimeoutSchedule() {
        this.timeoutChecker = new ConnectionCreateTimeoutChecker();
        this.scheduler.addTask(this.timeoutChecker, this.source.getQueryTimeout());
    }

    public void checkError() {
        if (this.timeoutChecker != null) {
            this.scheduler.discardTask(this.timeoutChecker);
        }
    }

    @Override
    public void reSetSource(CompositeConfiguration conf) {
        String prefix = DBCP_PREFIX_KEY + DBCP_POOL_FACTORY_KEY + this.dbName;
        this.source.setNodeSize(conf.getInt(prefix+DBCP_POOL_NODE_SIZE));
        this.source.setNodeConnectionSize(conf.getInt(prefix+DBCP_POOL_NODE_CONN_SIZE));
    }

    @Override
    public boolean eqMinimumPoolSize() {
        return (this.getPoolCount().intValue() == this.getSource().getMinimumPoolSize());
    }

    @Override
    public boolean isMinimumPoolSize() {
        return (this.getPoolCount().intValue() <= this.getSource().getMinimumPoolSize());
    }

    @Override
    public boolean isRiskPoolSize() {
        int ownConnSize = this.getPoolCount().intValue() ;
        return (ownConnSize > this.getSource().getMinimumPoolSize())
                && (ownConnSize <= this.getSource().getRiskPoolSize());
    }

    @Override
    public IPoolSource getSource() {
        return source;
    }

    @Override
    public String getGroupName() {
        return source.getGroup();
    }

    @Override
    public AtomicInteger getPoolCount() {
        return this.poolCount;
    }

    @Override
    public int getQueueSize() {
        return queue.size();
    }

    @Override
    public String toString() {
        return source.toString();
    }

    @Override
    public String getUrl() {
        return source.getUrl();
    }

    @Override
    public String getDbName() {
        return this.dbName;
    }

    @Override
    public boolean isBiasCheckMode() {
        return BIAS_CHECK_MODE.ON.name().equalsIgnoreCase(this.source.getBiasCheckMode());
    }

    @Override
    public boolean isAutoRefreshThreshold() {
        return (this.getPoolCount().intValue() <= this.getSource().getAutoRefreshThreshold());
    }

    @Override
    public boolean isAutoRefreshMode() {
        return AUTO_REFRESH_MODE.ON.name().equalsIgnoreCase(this.source.getAutoRefreshMode());
    }

    @Override
    public void addAutoRefreshSchedule() {
        this.autoRefreshTask = new AutoConnectionRefreshTask(this);
        this.autoRefreshScheduler.addTask(this.autoRefreshTask, this.source.getAutoRefreshInterval(), this.source.getAutoRefreshInterval());
        Finalizer.register(new IFinalize() {
            @Override
            public void execute() {
                terminateAutoRefresh();
            }
        });
    }

    public void terminateAutoRefresh() {
        if (this.autoRefreshTask != null) {
            this.autoRefreshScheduler.discardTask(this.autoRefreshTask);
        }
    }

    @Override
    public BIAS_CHECK_RESULT checkConnectionBias() {
        BIAS_CHECK_RESULT connection_bias = BIAS_CHECK_RESULT.NO_BIAS;

        if (!this.isRacMode()) {
            return connection_bias;
        }

        if(!isBiasCheckMode()) {
            return connection_bias;
        }

        int threshold = this.source.getConnectNodeThreshold(); // 閾値
        int nodeSize = 0;
        try (Connection connection = this.source.createConnection();
                PreparedStatement pstm = connection.prepareStatement(this.source.getConnectNodeCheckQuery());) {
            try (ResultSet rset = pstm.executeQuery()) {
                while (rset.next()) {
                    nodeSize = rset.getInt(NODE_COUNT);
                    break;
                }
            }
            if (threshold >= nodeSize) {
                connection_bias = BIAS_CHECK_RESULT.BIASED;
            }
        } catch (Exception e) {
            connection_bias = BIAS_CHECK_RESULT.ERROR;
        }

        return connection_bias;
    }

    @Override
    public boolean refreshAutoConnection() {
        boolean refresh_result = false;

        synchronized (connectionRefreshLock) {

            // プール内のコネクション数がコネクション自動リフレッシュ実行の閾値より大きい場合
            if(!isAutoRefreshThreshold()) {
                return true;
            }

            for (int i = 0; i < this.source.getRefreshRetryCount(); i++) {

                try {
                    // コネクション回復処理
                    connectionIncrement();

                    if(!this.isRacMode()) {
                    // コネクション再接続処理
                        refreshAllConnection(this.source.getRefreshComandTimeout());
                    }
                    refresh_result = true;
                    break;
                } catch(Throwable e) {
                }

                try {
                    Thread.sleep(this.source.getRefreshRetryWaitTime());
                } catch (InterruptedException e) {
                }
            }
        }
        return refresh_result;
    }
}
