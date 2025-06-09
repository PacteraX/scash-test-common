package test;

import static test.CcaUTest.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Properties;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import test.tool.DBDiffer;
import test.tool.DBInitializer;

import core.util.UString;
import db.rdb.dbcp.DBCPConfig;

public abstract class BaseTest{

    protected static final String SIM_SEPERATOR = "\\[c\\]";
    protected static Properties testProps;
    protected static DBDiffer  dbDiffer = null;
    protected static Calendar adjust = null;
    protected static int sleepMSec=0;
    protected String testCaseId;


    private static final PrintStream DEFAULT_SYSTEM_OUT = System.out;
    private static final PrintStream NULL_SYSTEM_OUT;
    static{
        try {
            NULL_SYSTEM_OUT    = new PrintStream(new FileOutputStream("nul:"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    protected static final int TEST_BLOCK_SIZE = 100;
    protected static long suiteStartTime;
    protected static long blockStartTime;
    protected static long testStartTime;
    protected static int  testCount;

    /**
     * テスト用プロパティ呼出
     */
    @Parameters({"testProperties"})
    @BeforeSuite
    public void doInit(String testPropertiesPath) {
        System.out.println("----- Config Init Start --------------------------------");
        System.setOut(NULL_SYSTEM_OUT);
        CcaUTest.init();
        testProps   = readProperties(testPropertiesPath);
        System.setOut(DEFAULT_SYSTEM_OUT);
        System.out.println("----- Config Init End ----------------------------------");

        Connection con = null;
        Statement stmt = null;

        String DB_URL = DBCPConfig.getProperty("pool.factory.localDB01.url");
        String DB_USER = DBCPConfig.getProperty("pool.factory.localDB01.user");
        String DB_PASS = DBCPConfig.getProperty("pool.factory.localDB01.password");

        try {
            con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            stmt = con.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Parameters( { "param" })
    @BeforeMethod
    public void beforeTest(String testCaseId) {
        this.testCaseId = testCaseId;
        testStartTime  = System.currentTimeMillis();
        testCount++;
    }

    //@Deprecated
    protected Properties getRequestProps() {
        //oracleがno waitでDBアクセスが続くとエラーになるため、ケース毎にsleep時間を設定値で持つ
        try {
            Thread.sleep(sleepMSec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return readProperties(testProps.getProperty("data.request") + "In_" + this.testCaseId + ".dat");
    }

    //@Deprecated
    protected Properties getResponseProps() {
        return readProperties(testProps.getProperty("data.response") + "Out_" + this.testCaseId + ".dat");
    }

    @Parameters({"param"})
    @Test
    public void doBefore(String preDataFile) throws Exception{
        if (!UString.isNull(preDataFile))
            new DBInitializer().initDb(preDataFile);
        dbDiffer = new DBDiffer();
    }

    @Parameters({"param", "simsql"})
    @Test
    public void doBeforeSimDate(String preDataFile, String simsqlFile) throws Exception{

        System.out.println("----- doBefore Start --------------------------------");
        if (!UString.isNull(preDataFile)){
            new DBInitializer().initDb(preDataFile);
        }
        if (!UString.isNull(simsqlFile)){
            new DBInitializer().initSimDb(simsqlFile);
        }
        dbDiffer = new DBDiffer();

        System.out.println("----- doBefore End ----------------------------------");
    }

    @Parameters({"param"})
    @Test
    public void doResult(String resultDataDir) {
        if (dbDiffer == null)
            dbDiffer = new DBDiffer(1L);
        if (adjust != null)
            dbDiffer.setDate(adjust);
        if (!UString.isNull(resultDataDir))
            Assert.assertEquals(dbDiffer.compare(resultDataDir),DBDiffer.RESULT.OK);
    }

    @AfterMethod
    public void afterTest() {
        if (testCount % TEST_BLOCK_SIZE == 0) {
            blockStartTime = System.currentTimeMillis();
        }
    }

    /**
     * valueがブランク行かでassertをかける
     *
     */
    protected static void assertBlank(String value) {
        Assert.assertTrue(value.equals(""));
    }

    /**
     * valueがブランク行でないかでassertをかける
     *
     */
    protected static void assertNonBlank(String value) {
        Assert.assertTrue(!value.equals(""));
    }

    /**
     * 期待値と入力値が一致しているかでassertをかける
     *
     */
    protected static void assertEquals(String result, String expect) {
        try {
            System.out.println("RESULT=" + result + " EXPECT=" + expect);
            Assert.assertEquals(result, expect);
        } catch (AssertionError e) {
            throw e;
        }
    }

    /**
     * 期待値と入力値が一致しているかでassertをかける
     *
     * @param name
     * @param result
     * @param expect
     */
    protected static void assertEquals(String name, String result, String expect) {
        try {
            System.out.println("NAME=" + name + " RESULT=" + result + " EXPECT=" + expect);
            Assert.assertEquals(result, expect);
        } catch (AssertionError e) {
            throw e;
        }
    }

    /**
     * 期待値と入力値が一致しているかでassertをかける
     *
     */
    protected static void assertEquals(int result, int expect) {
        try {
            System.out.println("RESULT=" + result + " EXPECT=" + expect);
            Assert.assertEquals(result, expect);
        } catch (AssertionError e) {
            throw e;
        }
    }

    /**
     *  期待値と入力値が一致しているかでassertをかける
     *
     * @param name
     * @param result
     * @param expect
     */
    protected static void assertEquals(String name, int result, int expect) {
        try {
            System.out.println("NAME=" + name + " RESULT=" + result + " EXPECT=" + expect);
            Assert.assertEquals(result, expect);
        } catch (AssertionError e) {
            throw e;
        }
    }

    /**
     * 期待値と入力値が一致しているかでassertをかける
     *
     */
    protected static void assertEquals(boolean result, boolean expect) {
        try {
            System.out.println("RESULT=" + result + " EXPECT=" + expect);
            Assert.assertEquals(result, expect);
        } catch (AssertionError e) {
            throw e;
        }
    }

    /**
     * 期待値と入力値が一致しているかでassertをかける
     *
     */
    protected static void assertEquals(long result, long expect) {
        try {
            System.out.println("RESULT=" + result + " EXPECT=" + expect);
            Assert.assertEquals(result, expect);
        } catch (AssertionError e) {
            throw e;
        }
    }

    /**
     * 期待値と入力値が一致していないかでassertをかける
     *
     */
    protected static void assertNotEquals(String result, String expect) {
        try {
            System.out.println("RESULT=" + result + " EXPECT=" + expect);
            Assert.assertTrue(!expect.equals(result));
        } catch (AssertionError e) {
            throw e;
        }
    }

    protected static void assertNull(Object result) {
        Assert.assertNull(result);
    }


    /**
     * このメソッドは内部でtrimを使っているので、非推奨<br>
     * 必要であればCcaUTest.nullToBlank(map.getProperty(key))あたりで置き換えてください。
     * @param map
     * @param key
     * @return
     */
    @Deprecated
    protected String getProperty(Properties map, String key) {
        String result = map.getProperty(key);
        return (result == null ? "" : result.trim());
    }

    protected interface IRunner{
        void run();
    }
    protected static void checkSyncTimeout(IRunner runner){
        try{
            runner.run();
        }catch(Exception e){
            Throwable t = e;
            while (t != null){
                System.out.println(t);
                t = t.getCause();
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * 期待値と入力値が一致しているかでassertをかける
     *
     */
    protected static void assertFalse(boolean result, String msg) {
        try {
            System.out.println("RESULT=" + result + " EXPECT=FALSE");
            Assert.assertTrue(!result);
        } catch (AssertionError e) {
            System.out.println("msg=" + msg);
            throw e;
        }
    }
    /**
     * 期待値と入力値が一致しているかでassertをかける
     *
     */
    protected static void assertTrue(boolean result, String msg) {
        try {
            System.out.println("RESULT=" + result + " EXPECT=TRUE");
            Assert.assertTrue(result);
        } catch (AssertionError e) {
            System.out.println("msg=" + msg);
            throw e;
        }
    }

}
