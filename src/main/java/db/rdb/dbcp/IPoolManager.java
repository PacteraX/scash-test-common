package db.rdb.dbcp;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author HSK Satoh
 */
public interface IPoolManager {

    /**
     * すべてのプールグループが格納されたMapを返します.
     * @return すべてのプールグループが格納された　Map&lt;グループ名,Poolグループ&gt;
     */
    Map<String, IPoolGroup> getPoolGroups();

    /**
     * コネクションPoolの検証結果を表す列挙です.
     */
    public enum VALIDATION_RESULT {
        SUCCESS, FAILURE
    }

    /**
     *
     * 認証分離状態
     *
     */
    public enum AUTH_SEPARATION_MODE {ON,OFF}

    /**
     *
     * コネクション取得ブロック状態
     *
     */
    public enum BLOCKAGE_MODE {ON,OFF}

    /**
     * 対象のプールグループを返します.
     * @param groupName 対象グループ名
     * @return 対象のプールグループ
     */
    IPoolGroup getPoolGroup(String groupName);

    /**
     * 対象グループの指定したDBのコネクションプールを返します.
     * @param groupName 対象グループ名
     * @param dbName DB名
     * @return 対象グループの指定したDBのコネクションプール
     */
    IPool getPool(String groupName, String dbName);

    /**
     * 対象グループから任意のDBコネクション(参照用)を返します.
     * @param groupName 対象グループ名
     * @return 対象のプールグループの任意のDBコネクション
     */
    IConnection getConnection(String groupName);

    /**
     * 対象DB負荷分散グループから任意のDBコネクション(参照用)を返します.
     * @param groupName 対象グループ名
     * @return 対象のプールグループの任意のDBコネクション
     */
    IConnection getConnectionDBOffLoad(String groupName);

    /**
     * デフォルトのPoolグループを返します.
     * @return デフォルトのPoolグループ
     */
    IPoolGroup getPoolGroup();

    /**
     * デフォルトのPoolグループから任意のDBコネクション(参照用)を返します.
     * @return デフォルトPoolグループの任意のDBコネクション
     */
    IConnection getConnection();

    /**
     * デフォルトのDB負荷分散用Poolグループから任意のDBコネクション(参照用)を返します.
     * @return デフォルトPoolグループの任意のDBコネクション
     */
    IConnection getConnectionDBOffLoad();

    /**
     * @see #getPoolGroup(String)
     */
    IPoolGroup getPoolGroupByForce(String groupName);

    /**
     * @see #getConnection(String)
     */
    IConnection getConnectionByForce(String groupName);

    /**
     * 指定されたグループに新しいPoolを追加します.
     * @param groupName グループ名
     * @param pool 追加するPool
     * @return 追加後のグループのプール数
     */
    int putPoolGroups(String poolGroupKey, IPool pool, boolean alias);

    /**
     * すべてのPoolグループ名を返します.
     * @return すべてのグループ名のSet
     */
    Set<String> getPoolNames();

    /**
     * デフォルトPoolグループの名前をセットします.
     * デフォルトのグループ名はプロパティで指定しておく必要があります.
     */
    void setDefaultPoolGroupKey();

    /**
     * JDBCコネクション初期化処理
     */
    void init();

    /**
     * JDBCコネクションプールリフレッシュモード.
     */
    boolean isRefreshMode();

    /**
     *
     * @param dbgroup DBグループ名
     * @return 認証分離状態
     */
    AUTH_SEPARATION_MODE getAuthMode(String dbgroup);

    /**
     *
     * @param dbgroup DBグループ名
     * @param mode DB認証分離状態
     */
    void setAuthMode(String dbgroup, AUTH_SEPARATION_MODE mode);

    /**
     *
     * @param warId War識別ID
     */
    boolean isExistenceDBOffLoad(String warId);

    /**
     * 負荷分散モードを返す
     * @param warId War識別ID
     */
    boolean isDBOffLoad(String warId);

    /**
     * 負荷分散モード全てを返す
     */
    Map<String,AtomicBoolean> getDBOffLoad();

    /**
     *
     * 負荷分散モードを設定する
     * @param dbgroup War識別ID
     */
    void resistDBOffLoad(String warId);

    /**
     *
     * 負荷分散モードを設定する
     * @param dbgroup War識別ID
     * @param offloadType 負荷分散状態
     */
    void setDBOffLoad(String warId, boolean offloadType);

    /**
     *
     * 負荷分散モード用プールグループリストを設定する
     */
    void createPoolGroupDBOffLoad();

    /**
     *
     * 負荷分散モード用プールグループを設定する
     * @param poolGroupKey DBグループ名
     * @param poolGroup プールグループ
     */
    void setPoolGroupDBOffLoad(String poolGroupKey, IPoolGroup poolGroup);
}
