package db;

public interface LogConstants {

    //****************************** JMX COMMAND ***********************************************/
    //----------------------------------------------------------------------- ERROR -----------------------------------------------------------------------//

    /**
     * コネクションリフレッシュコマンド失敗
     */
    static final String ERROR_DBE1013 = "[DBE1013]Connection Refresh Failure.RefreshSucceed={0},Closed={1},RefreshAll={2},poolSize={3},Time={4}ms";

    /**
     * コネクションリフレッシュコマンドタイムアウト
     */
    static final String ERROR_DBE1014 = "[DBE1014]Connection Refresh Timeout.RefreshSucceed={0},Closed={1},RefreshAll={2},poolSize={3},Time={4}ms";

    /**
     * コネクションリフレッシュコマンド。 実際接続数＞プール内の接続数の場合
     */
    static final String ERROR_DBE1015 = "[DBE1015]Connection Refresh Count is more than PooSize.[WillRefresh={0}],[PoolSize={1}]";

    /**
     * 生存DBノード数の取得失敗
     */
    static final String ERROR_DBE1025 = "[DBE1025]The amount of active DB node in DBGroup confirmed failure.[GroupName={0}] ";

    /**
     * 再接続開始時の生存DBノード数 ！= 再接続終了後の生存DBノード数
     */
    static final String ERROR_DBE1026 = "[DBE1026]The amount of active DB node in DBGroup Changed.[GroupName={0}]";

    /**
     * コネクション回復コマンド、再接続数＜＝プール内の接続数の場合
     */
    static final String ERROR_DBE1028 = "[DBE1028]Connection Increase Target is less than PoolSize.[IncreaseTarget={0}],[PoolSize={1}]";


//----------------------------------------------------------------------- WARN -----------------------------------------------------------------------//

    /**
     * コネクションリフレッシュコマンド実行成功
     */
    static final String INFO_DBI1003 = "[DBI1003]Connection Refresh Success.NodeSize={0}/{1},RefreshSucceed={2},Closed={3},RefreshAll={4},poolSize={5},Time={6}ms";

    /**
     * コネクション数回復コマンド実行成功
     */
    static final String INFO_DBI1006 = "[DBI1006]Connection Increment Success.NodeSize={0}/{1},IncreaseSucceed={2},IncreaseAll={3},PoolSize={4},Time={5}ms";

    /**
     * コネクション障害強制検知コマンド実行成功
     */
    static final String INFO_DBI1012 = "[DBI1012]Connection Fault Detection Success.Closed={0},Poolsize={1},Time={2}ms";
    static final String ERROR_DBE1007 = "[DBE1007]Failed to increase connection.[groupName={0}, dbName={1}]";
    static final String ERROR_DBE1011 = "[DBE1011]different logical DB group in transaction.";
    static final String ERROR_DBE1033 = "[DBE1033]Logical error occurred.[GroupName={0}, DbName={1}, BatchArgument={2}, BatchResult={3}]";
    static final String ERROR_DBE1034 = "[DBE1034]System error occurred.[GroupName={0}, DbName={1}, BatchArgument={2}, BatchResult={3}]";
    static final String ERROR_DBE2001 = "[DBE2001]Connection close interrupted.";
    static final String ERROR_DBE1040 = "[DBE1040]Connection create error";
    static final String ERROR_DBE1063 = "[DBE1063]Bias occurs in connection.[GroupName={0}]";
    static final String ERROR_DBE1067 = "[DBE1067]Failover error occurred.[GroupName={0}, DbName={1}, BatchArgument={2}, BatchResult={3}]";

    static final String INFO_DBI2002 = "[DBI2002]Group : {0} / Databese : {1}  Connection close retry out.";
    static final String WARN_DBW1023 = "[DBW1023]Pool exhausted max={0} DB name={1}";

}
