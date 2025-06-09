package db.rdb.dbcp;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.configuration2.CompositeConfiguration;

import system.core.UnchkedExecption;

/**
 * connection pool manager.
 * @author KSF M.Sugawara
 * @author modify 2009/12 HSK satoh
 */
public interface IPool {


    public enum CONN_SHUTDOWN_EVENT {APL,COMMAND,SCHEDULE,OTHER}

    public enum BIAS_CHECK_RESULT {BIASED, NO_BIAS, ERROR}

    /**
     * プーリングされたコネクションを返却します.
     * @return コネクション
     */
    IConnection getConnection();

    /**
     * 引数で渡されたコネクションをプールに格納します.
     * @param con
     *            コネクション
     */
    void returnTo(IConnection con);

    /**
     * このプールに使用されているリソースを返します.
     * @return プールに使用されているリソース
     */
    IPoolSource getSource();

    /**
     * このプールが参加しているグループ名を返します.
     * @return グループ名
     */
    String getGroupName();

    /**
     * このプールの接続数をminimumまで増加させます.
     * @throws UnchkedExecption
     *             接続エラー
     */
    void increaseConnection();

    /**
     * このプールが接続しているDB名を返します.
     * @return 接続しているDB
     */
    String getDbName();

    void shutdown(String groupName);

    IConnection create() throws Exception;

    String getUrl();

    public int getConnectionSize();

    public void refreshWaitConnection() ;

    /**
     * Pool内のコネクション数を返却する。
     * @return コネクション数
     */
    AtomicInteger getPoolCount();

    /**
     * Pool内の待機中コネクション数を返却する。
     * @return 待機中コネクション数
     */
    public int getQueueSize();

    /**
     * コネクションリフレッシュ
     * @param timeout
     * @return 実行結果メッセージ
     */
    String refreshAllConnection(long timeout) throws Throwable;

    /**
     * コネクション数回復
     * @return 実行結果メッセージ
     */
    String connectionIncrement() throws Throwable;

    /**
     * コネクション障害強制検知
     * @return 実行結果メッセージ
     */
    String connectionFaultDetection() throws Throwable;

    void reSetSource(CompositeConfiguration conf);

    /**
     * コネクション数 ＝最低限数の場合、True
     * @return boolean
     */
    boolean eqMinimumPoolSize();

    /**
     * コネクション数は最低限数の場合、True
     * @return boolean
     */
    boolean isMinimumPoolSize();

    /**
     * コネクション数は警告数の場合、True
     * @return boolean
     */
    boolean isRiskPoolSize();

    /**
     * コネクション偏りチェック、偏りありの場合True
     * @return boolean
     */
    BIAS_CHECK_RESULT checkConnectionBias();

    /**
     * コネクション自動リフレッシュ
     * @return boolean
     */
    boolean refreshAutoConnection();

    /**
     * コネクション偏りチェックを行う場合、True
     * @return boolean
     */
    boolean isBiasCheckMode();

    /**
     * コネクション数が、コネクション自動リフレッシュ実行閾値以下の場合、True
     * @return boolean
     */
    boolean isAutoRefreshThreshold();

    /**
     * コネクション自動リフレッシュモード
     */
    boolean isAutoRefreshMode();


    /**
     * コネクション自動リフレッシュスケジューラ登録
     */
    public void addAutoRefreshSchedule();
}
