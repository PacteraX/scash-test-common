package test.tool;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import db.rdb.dbcp.DBCPConfig;

/**
 * @author nakamaru
 *
 */
public class DBInitializer {

    public enum DB_TYPE {
        POSTGRES,
        SIMDB,
    }

//    protected static final String SEQ_COMMAND;
//    static {
//        try {
////            Properties props = new Properties();
////            props.load(new FileInputStream("./resources/test-conf/test.properties"));
////            SEQ_COMMAND = props.getProperty("test.seq.command");
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

    private static final String DB_URL;
    private static final String DB_USER;
    private static final String DB_PASS;

    private static final String SIMDB_URL;
    private static final String SIMDB_USER;
    private static final String SIMDB_PASS;
    static {
        try {
            System.out.println("[CCDB] pool.factory.localDB01.url loaded [" + DBCPConfig.getProperty("pool.factory.localDB01.url") + "]" );
            DB_URL = DBCPConfig.getProperty("pool.factory.localDB01.url");
            DB_USER = DBCPConfig.getProperty("pool.factory.localDB01.user");
            DB_PASS = DBCPConfig.getProperty("pool.factory.localDB01.password");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        boolean simDBUrlFlg= true;
        try{
            DBCPConfig.getProperty("simdb.url");
        }catch (Exception e) {
            simDBUrlFlg = false;
        }

        try {
            if (simDBUrlFlg){
                System.out.println("[SIMDB] simdb.url loaded [" + DBCPConfig.getProperty("simdb.url") + "]" );
                SIMDB_URL  = DBCPConfig.getProperty("simdb.url");
                SIMDB_USER = DBCPConfig.getProperty("simdb.user");
                SIMDB_PASS = DBCPConfig.getProperty("simdb.pass");
            }else{
                System.out.println("[SIMDB] pool.factory.localDB01.url loaded [" + DBCPConfig.getProperty("pool.factory.localDB01.url") + "]" );
                SIMDB_URL = DBCPConfig.getProperty("pool.factory.localDB01.url");
                SIMDB_USER = DBCPConfig.getProperty("pool.factory.localDB01.user");
                SIMDB_PASS = DBCPConfig.getProperty("pool.factory.localDB01.password");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void initDb(String scriptPath) throws Exception {

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(scriptPath), "UTF-8"));

        String sql;
        List<String> sqls = new ArrayList<String>();
        try {
            while((sql = reader.readLine())!=null){
                if (!sql.equals("")){

                    // SEQ_NUM_LONG_CYCLICの場合は、seq-serverを初期化
                    if(sql.toUpperCase().indexOf("SEQ_NUM_LONG_CYCLIC") > -1){
                        if(sql.toUpperCase().indexOf("INSERT INTO") > -1){
                            String valueStr = sql.substring(sql.toUpperCase().indexOf("VALUES") + 8, sql.length() - 1);
                            String[] values = valueStr.replaceAll("'", "").split(",");
                            String key = values[0];
                            String[] insertCmd = { "CREATE SEQUENCE " + " " + key
                                    + " MINVALUE " + Double.valueOf(values[3]).longValue()
                                    + " MAXVALUE " + Double.valueOf(values[4]).longValue()
                                    + " INCREMENT BY " + Double.valueOf(values[2]).longValue()
                                    + " START WITH " + (Double.valueOf(values[1]).longValue() + 1)
                                    + " NOCACHE "
                                    + " CYCLE ORDER" + "\n" };
//							new InsertSeqEx().start(insertCmd);
                        }
                    }
                    else{
                        sql = sql.replace("##LF##", "\n");
                        sqls.add(sql);
                    }
                }
            }
            executeSQL(sqls, DB_TYPE.POSTGRES);
        }catch (Exception e) {
            throw e;
        }finally {
            reader.close();
        }
    }

    public void initSimDb(String scriptPath) throws Exception {

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(scriptPath), "UTF-8"));

        String line, sql;
        List<String> sqls = new ArrayList<String>();
        sql = "";
        try {
            while((line = reader.readLine())!=null){
                line = line.trim();
                if (!line.equals("") && !line.startsWith("--")) {
                    sql = sql + " " + line;
                    sqls.add(sql);
                    sql = "";
                }
            }
            executeSQL(sqls, DB_TYPE.SIMDB);
        }catch (Exception e) {
            throw e;
        }finally {
            reader.close();
        }
    }

    private void executeSQL(final List<String> sqls, DB_TYPE dbType) throws SQLException{
        Connection conn = null;
        Statement stmt = null;

        try {
            switch (dbType) {
                case POSTGRES :
                    conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                    break;
                case SIMDB :
                    conn = DriverManager.getConnection(SIMDB_URL, SIMDB_USER, SIMDB_PASS);
                    break;
                default :
                    throw new SQLException("getConnection障害");
            }
            conn.setAutoCommit(true);
            stmt = conn.createStatement();

            if (sqls.isEmpty()){
                return;
            }

            String tmpTableName = getTableName(sqls.get(0));
            for (String sql : sqls){
                String tableName = getTableName(sql);
                if(!tmpTableName.equals(tableName)){
                    System.out.println("BATCH TABLE NAME : " + tmpTableName);
                    stmt.executeBatch();
                    tmpTableName = tableName;
                }
                stmt.addBatch(sql);
            }
            System.out.println("LAST BATCH TABLE NAME : " + tmpTableName);
            stmt.executeBatch();
        } catch (SQLException e) {
            throw e;
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    throw e;
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    throw e;
                }
            }
        }
    }

    private static final String[] REG    = {"TRUNCATE TABLE ([a-zA-Z0-9_]+).*$",
            "DELETE FROM ([a-zA-Z0-9_]+).*$",
            "INSERT INTO ([a-zA-Z0-9_]+).*$"};

    private String getTableName(String sql) {
        for (String regex : REG){
            Pattern p = Pattern.compile(regex);

            Matcher m = p.matcher(sql);
            if (m.find()) {
                String matchstr = m.group(1);
                return matchstr;
            }
        }
        return sql;
    }
}
