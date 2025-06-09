package test.tool;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import core.config.Configure;
import db.rdb.dbcp.DBCPConfig;

/**
 * 日時チェック処理はマルチスレッドでは使用できない。
 *
 * @author Administrator
 *
 */
public class DBDiffer {

    public enum RESULT{
        OK,
        ERR_NOT_FOUND_DIR,
        ERR_ILLEGAL_DB_ACCESS,
        ERR_DIFF_NUM,
        ERR_DIFF_DATA
    }

    /**
     * チェックスキップ
     * @author Administrator
     *
     */
    private enum EXCLUDE_VALUE{
        AUTO_ID,TIMESTAMP,TIME,ANY,CMAILBODY,EMAILBODY ,NULL, TIMESTAMPEVA;
        public boolean isExclude(String value){
            return this.name().equalsIgnoreCase(value);
        }
    }
    private static EXCLUDE_VALUE[] EXCLUDE_VALUES = EXCLUDE_VALUE.values();

    private static final boolean NOTHING_DIFF = false;
    private static final boolean HAS_DIFF     = true;

    private static final String REGEX_YYYYYMMDD
        = "(?!([02468][1235679]|[13579][01345789])000229)(([0-9]{4}(01|03|05|07|08|10|12)(0[1-9]|[12][0-9]|3[01]))|([0-9]{4}(04|06|09|11)(0[1-9]|[12][0-9]|30))|([0-9]{4}02(0[1-9]|1[0-9]|2[0-8]))|([0-9]{2}(([02468])[048]|[13579][26])0229))";
    private static final String REGEX_HHMMSS
        = "([0-1][0-9]|[2][0-3])[0-5][0-9][0-5][0-9]";
    private static final String REGEX_sss
        = "[0-9][0-9][0-9]";

    private static final String  CMAIL_BODY = "cmail.body";
    private static final String  CMAIL_BODY_PROP = Configure.getString(CMAIL_BODY, "cmail.body_PROP is null");
    private static final Pattern CMAIL_BODY_PATTERN = Pattern.compile(CMAIL_BODY_PROP);
    private static final String  EMAIL_BODY = "email.body";
    private static final String  EMAIL_BODY_PROP = Configure.getString(EMAIL_BODY, "email.body_PROP is null");
    private static final Pattern EMAIL_BODY_PATTERN = Pattern.compile(EMAIL_BODY_PROP);

    private Date now;
    private HashMap<String, Long> seqNum = new HashMap<String, Long>();

    public DBDiffer(){
        // 日時取得
        this.now = Calendar.getInstance().getTime();
        // ミリ秒切り捨て
        this.now.setTime(this.now.getTime() - (this.now.getTime()%1000));

    }

    public DBDiffer(long n){
        // 日時取得
        this.now = Calendar.getInstance().getTime();
        // ミリ秒切り捨て
        this.now.setTime(this.now.getTime() - (this.now.getTime()%1000));
        // n時間マイナス
        this.now.setTime(this.now.getTime() - (n * 1000 * 60 * 60));
    }

    private RESULT initSeqNum(){
        RESULT result = RESULT.OK;

        CurrentSeqEx cs = new CurrentSeqEx();
        List<String> seqList = cs.getMessages();
        for(String seqStr : seqList){
            String[] seqParts = seqStr.split(",");

            seqNum.put(seqParts[0], Long.valueOf(seqParts[1]));
        }
        
        return result;
    }

    /**
     * 引数パス内のファイル内容(ファイル名=テーブル名)とDBの差分をチェック.
     *
     * @param dirPath 期待値ファイルのディレクトリパス(絶対パス)。
     * @return 差分ありの場合true返却.
     */
    public RESULT compare(String dirPath) {

        File targetDir = new File(dirPath);
        if (!targetDir.exists()) {
            return RESULT.ERR_NOT_FOUND_DIR;
        }

        File[] files = targetDir.listFiles();
        Connection con = null;
        Statement stmt = null;

        RESULT result = RESULT.OK;

        String DB_URL = DBCPConfig.getProperty("pool.factory.localDB01.url");
        String DB_USER = DBCPConfig.getProperty("pool.factory.localDB01.user");
        String DB_PASS = DBCPConfig.getProperty("pool.factory.localDB01.password");
        try{
            con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            stmt = con.createStatement();
            for (File file : files){
                RESULT fileResult;
                if (file.isFile()){
                    if ((fileResult = parseFile(con,stmt,file)) != RESULT.OK){
                        result = fileResult;
                    }
                }
            }
        } catch (SQLException e) {
            return RESULT.ERR_ILLEGAL_DB_ACCESS;
        }finally{
            try{
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    return RESULT.ERR_ILLEGAL_DB_ACCESS;
                }
            }
            }finally{
                if (con != null) {
                    try {
                        con.close();
                    } catch (SQLException e) {
                        return RESULT.ERR_ILLEGAL_DB_ACCESS;
                    }
                }
            }
        }
        return result;
    }

    public void setDate(Calendar cal){
        this.now = cal.getTime();
    }

    private RESULT parseFile(Connection con,Statement stmt,File file){
        String tableName = file.getName().substring(0, file.getName().lastIndexOf("."));

        // TableがSEQ_NUM_LONG_CYCLICの場合はスキップ
        if(tableName.equalsIgnoreCase("SEQ_NUM_LONG_CYCLIC")){
            return RESULT.OK;
        }

        if (checkCount(con,stmt,tableName,file) == HAS_DIFF)
            return RESULT.ERR_DIFF_NUM;
        return checkLine(con,stmt,tableName,file) ? RESULT.ERR_DIFF_DATA : RESULT.OK;
    }

    private boolean checkCount(Connection con,Statement stmt,String tableName,File file){
        String query = getQueryCount(tableName);

        int expect = -1;
        LineNumberReader reader = null;

        try{
            reader = new LineNumberReader(
                    new InputStreamReader(
                    new FileInputStream(file), "UTF-8"));
            while(null!=reader.readLine()){
                ;
            }

            expect = reader.getLineNumber() - 1;
        }catch(IOException e){
            return HAS_DIFF;
        } finally{
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    return HAS_DIFF;
                }
        }

        int real   = -1;
        ResultSet resultSet = null;
        try{
            resultSet = stmt.executeQuery(query);
            while (resultSet.next())
                real = resultSet.getInt(1);
        }catch(SQLException e){
            return HAS_DIFF;
        } finally{
            if (resultSet != null)
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    return HAS_DIFF;
                }
        }

        if (expect == real) {
            return NOTHING_DIFF;
        } else {
            return HAS_DIFF;
        }
    }

    private boolean checkLine(Connection con,Statement stmt,String tableName,File file){
        String query = getQueryCount(tableName);

        LineNumberReader reader = null;
        boolean hasDiff = NOTHING_DIFF;
        try{
            reader = new LineNumberReader(
                    new InputStreamReader(
                    new FileInputStream(file), "UTF-8"));

            String[] columns = reader.readLine().split(",");
            String line;
            HashMap<String, String> columnsMap = new HashMap<String, String>();
            try {
                stmt.executeQuery("SELECT * FROM (SELECT *, ROW_NUMBER() OVER () AS rownum FROM " + tableName + " ) t WHERE rownum = 0 ");

                //ColumnがCLOB型で有るかを判断するために、Columnのデータ型を取得する
                ResultSet columnsResultSet = stmt.executeQuery("Select COLUMN_NAME ,DATA_TYPE from information_schema.columns where table_name='" + tableName.toUpperCase() + "'");
                while(columnsResultSet.next()) {
                    columnsMap.put(columnsResultSet.getString("COLUMN_NAME"), columnsResultSet.getString("DATA_TYPE"));
                }

            } catch (SQLException e1) {
                e1.printStackTrace();
            }

            while(null!=(line =reader.readLine())){
                String[] values = line.split(",",-1);
                for (int i = 0; i  < values.length; i++) {
                    values[i] = values[i].replace("##LF##", "\n");
                }
                if (values.length != columns.length) {
                    hasDiff = HAS_DIFF;
                    continue;
                }
                List<String[]> excludeColumnList = new ArrayList<String[]>();
                query = getQuery(values, tableName, columns, columnsMap, excludeColumnList);
                ResultSet resultSet = null;
                try{
                    resultSet = stmt.executeQuery(query);

                    if(!resultSet.next()){
                        hasDiff = HAS_DIFF;
                        continue;
                    }
                    for (String[] excludeColVal : excludeColumnList) {
                        final String col = excludeColVal[0];
                        final String val = excludeColVal[1];

                        EXCLUDE_VALUE exVal  = EXCLUDE_VALUE.valueOf(val);
                        String  dbVal = resultSet.getString(col);
                        switch(exVal){
                        case AUTO_ID :
                            break;
                        case TIMESTAMP :
                            if (!checkDate(dbVal)) {
                                hasDiff = HAS_DIFF;
                            }
                            break;
                        case TIMESTAMPEVA :
                            if (!checkTimeStampEVA(dbVal)) {
                                hasDiff = HAS_DIFF;
                            }
                            break;
                        case TIME :
                            if (!isTimeFormat(dbVal)) {
                                hasDiff = HAS_DIFF;
                            }
                            break;
                        case CMAILBODY :
                            if (!isCmailBodyFormat(dbVal)) {
                                hasDiff = HAS_DIFF;
                            }
                            break;
                        case EMAILBODY :
                            if (!isEmailBodyFormat(dbVal)) {
                                hasDiff = HAS_DIFF;
                            }
                            break;
                        case NULL :
                            if (dbVal != null) {
                                hasDiff = HAS_DIFF;
                            }
                        }
                    }
                }catch(SQLException e){
                    hasDiff = HAS_DIFF;
                    continue;
                } finally{
                    if (resultSet != null)
                        try {
                            resultSet.close();
                        } catch (SQLException e) {
                            hasDiff = HAS_DIFF;
                            continue;
                        }
                }
            }

        }catch(IOException e){
            return HAS_DIFF;
        } finally{
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    return HAS_DIFF;
                }
        }
        return hasDiff;
    }

    private String getQueryCount(String tableName) {

        StringBuilder query = new StringBuilder();
        query.append("SELECT count(*) FROM ").append(tableName);

        return query.toString();
    }

    private String getQuery(String[] values, String tableName, String[] columns, Map columnsMap,
            final List<String[]> excludeColumnList) {

        StringBuilder query = new StringBuilder();
        query.append("SELECT * FROM ").append(tableName);

        boolean isFirst = true;
        COL : for (int i = 0; i < columns.length; i++) {
            for (EXCLUDE_VALUE exclude : EXCLUDE_VALUES)
                if (exclude.isExclude(values[i])){
                    excludeColumnList.add(new String[]{columns[i],values[i]});
                    continue COL;
                }
            if (isFirst) {
                query.append(" WHERE ");
                isFirst = false;
            } else {
                query.append(" AND ");
            }

            // SEQNUMの場合
            if(values[i].matches("%.*%[0-9]*")){
                String[] seqParts = values[i].split("%");
                Long seqLong = seqNum.get(seqParts[1]) + Long.parseLong(seqParts[2]);
                query.append(columns[i]).append("='").append(seqLong.toString()).append("'");
            }
            // SEQNUM以外
            else {
                if (columnsMap.get(columns[i].toUpperCase()) != null && columnsMap.get(columns[i].toUpperCase()).toString().equals("CLOB")) {
                    query.append("dbms_lob.substr( " + columns[i] +", length(" + columns[i] + "), 1 )").append("='").append(values[i].equals("") ? "`" : values[i]).append("'");
                } else {
                    if (isNull(values[i])) {
                        query.append("(").append(columns[i]).append(" IS NULL ").append(")");
                    } else {
                        query.append(columns[i]).append("='").append(values[i].replace("'", "''")).append("'");
                    }
                }
            }
        }
        return query.toString();
    }

    private boolean checkDate(String val) {
        if (val.equals("00000000000000")) {
            return true;
        }
        return isDateFormat(val);
    }

    private boolean isDateFormat(String val) {
        if (val.length() != 8 && val.length() != 14){
            return false;
        }
        if (val.length() == 8){
            val += "235959";
        }
        String pattern;
        SimpleDateFormat format;
        pattern = REGEX_YYYYYMMDD + REGEX_HHMMSS;
        format = new SimpleDateFormat("yyyyMMddHHmmss");

        boolean ret = Pattern.matches(pattern, val);
        if(ret){
            try {
                ret = val.compareTo(format.format(now)) >= 0;
            } catch (Exception e) {
                ret = false;
            }
        }

        return ret;
    }

    private boolean isTimeFormat(String val) {
        if (val.length() != 6){
            return false;
        }
        return Pattern.matches(REGEX_HHMMSS, val);
    }

    private boolean isCmailBodyFormat(String val) {
        return CMAIL_BODY_PATTERN.matcher(val).matches();
    }

    private boolean isEmailBodyFormat(String val) {
        return EMAIL_BODY_PATTERN.matcher(val).matches();
    }

    private boolean checkTimeStampEVA(String val) {
        if (val.equals("00000000000000") || val.equals("00000000000000000")) {
            return true;
        }

        if (val.length() != 8 && val.length() != 14 && val.length() != 17){
            return false;
        }
        if (val.length() == 8){
            val += "235959999";
        }
        if (val.length() == 14){
            val += "999";
        }
        String pattern;
        pattern = REGEX_YYYYYMMDD + REGEX_HHMMSS + REGEX_sss;
        boolean ret = Pattern.matches(pattern, val);
        if(ret){
            val = val.substring(0, 14);
            SimpleDateFormat format;
            format = new SimpleDateFormat("yyyyMMddHHmmss");
            try {
                ret = val.compareTo(format.format(now)) >= 0;
            } catch (Exception e) {
                ret = false;
            }
        }

        return ret;

    }
    
    public static boolean isNull(Object o) {
        if (o == null) return true;
        String str = o.toString();
        if(str == null || str.length()==0 )return true;
        return false;
    }
}
