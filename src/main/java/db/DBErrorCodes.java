package db;

import db.rdb.dbcp.DBCP;

public class DBErrorCodes {
    public static final String UNIQUE_VIOLATION_CODE;        // 一意制約違反
    public static final String DEAD_LOCK_CODE;                // デッドロック
    public static final String NOTNULL_VIOLATION_CODE;       // NotNull制約違反
    public static final String CHECK_VIOLATION_CODE;         // チェック違反
    public static final String REFERENCE_VIOLATION_CODE1;   // 外部参照制約違反
    public static final String REFERENCE_VIOLATION_CODE2;   // 外部参照制約違反
    public static final String CANCEL_SUCCESS_CODE;          // PrepareStaementキャンセル成功
    public static final String READ_ONLY_CODE;                // READ_ONLYトランザクション

    static {
        if(DBCP.isPostgres()) {
            // PSQL sqlstate番号
            UNIQUE_VIOLATION_CODE            = "23505"; // 一意制約違反
            DEAD_LOCK_CODE                    = "40P01"; // デッドロック
            NOTNULL_VIOLATION_CODE           = "23502"; // NotNull制約違反
            CHECK_VIOLATION_CODE             = "23514"; // チェック違反
            REFERENCE_VIOLATION_CODE1       = "23503"; // 外部参照制約違反
            REFERENCE_VIOLATION_CODE2       = "23000"; // 外部参照制約違反
            CANCEL_SUCCESS_CODE              = "57014"; // PrepareStaementキャンセル成功
            READ_ONLY_CODE                    = "25006"; // READ_ONLYトランザクション
        } else {
            // default値：ORA sqlstate番号
            UNIQUE_VIOLATION_CODE            = "1";    // 一意制約違反
            DEAD_LOCK_CODE                    = "60";   // デッドロック
            NOTNULL_VIOLATION_CODE           = "1400"; // NotNull制約違反
            CHECK_VIOLATION_CODE             = "2290"; // チェック違反
            REFERENCE_VIOLATION_CODE1       = "2291"; // 外部参照制約違反
            REFERENCE_VIOLATION_CODE2       = "2292"; // 外部参照制約違反
            CANCEL_SUCCESS_CODE              = "1013"; // PrepareStaementキャンセル成功
            READ_ONLY_CODE                    = "-";    // READ_ONLYトランザクション
        }
    }
}
