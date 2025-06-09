package core.util;
//Core Java
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import core.UnchkedExecption;
//apache
import org.apache.commons.io.FileUtils;

public class UFile {

    /**
     * プロパティーファイルの情報取得
     *
     * @param fileName
     * @param property
     * @return
     * @throws Exception
     */
    @Deprecated
    static public String getProperty(String fileName, String property)
            throws Exception {
        Properties prop = new Properties();
        // プロパティファイルからキーと値のリストを読み込みます
        prop.load(new FileInputStream(fileName));
        return prop.getProperty(property);
    }

    /**
     * カレント絶対パス取得
     *
     * @return
     */
    static public String getAbsoluteDirectoryPath() {
        return AbsoluteDirePath;
    }

    /**
     * カレント絶対パス取得
     *
     * @return
     */
    static private String setAbsoluteDirectoryPath() {
        File file = new File("");
        return file.getAbsolutePath() + "/";
    }

    static public String setAbsoluteDirectoryPath(String absoluteDirePath) {
        AbsoluteDirePath = absoluteDirePath;
        return AbsoluteDirePath;
    }

    public static void delDir(String path) {
        File dir = new File(path);
        if (dir.exists() && dir.isDirectory()) {
            try {
                FileUtils.cleanDirectory(dir);
            } catch (IOException e) {
            	throw new UnchkedExecption(e);
            }
        }else if (dir.exists() && dir.isFile())
            dir.delete();
    }

    public static void delFile(String filename) {
        delDir(filename);
    }

    /**
     * CSVファイル読み込み（全読み込み）
     *
     * @param fileName
     * @return
     * @throws Exception
     */
    public static List<List<String>> readCSV(String fileName)
    throws Exception {
        File csv = new File(fileName); // CSVデータファイル
        List<List<String>> csvDatas = new ArrayList<List<String>>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(csv));
            // 最終行まで読み込む
            while (br.ready()) {
                String line = br.readLine();
                // 1行をデータの要素に分割
                List<String> strs = new ArrayList<String>();
                StringTokenizer st = new StringTokenizer(line, ",");
                while (st.hasMoreTokens()) {
                    // 1行の各要素をタブ区切りで表示
                    strs.add(st.nextToken());
                }
                csvDatas.add(strs);
            }
            br.close();

        } catch (FileNotFoundException e) {
            // Fileオブジェクト生成時の例外捕捉
            throw new UnchkedExecption(e);
        } catch (IOException e) {
            // BufferedReaderオブジェクトのクローズ時の例外捕捉
            throw new UnchkedExecption(e);
        }
        return csvDatas;
    }

    /**
     * 全書き込み（新規）
     *
     * @param fileName
     * @param datas
     */
    synchronized public static void writeCSV(String fileName,
            List<List<String>> datas) {
        try {
            File csv = new File(fileName); // CSVデータファイル
            csv.createNewFile();
            BufferedWriter bw =
                new BufferedWriter(new FileWriter(csv, false)); // 新規モード
            for (List<String> line : datas) {
                String tokenLine = "";
                for (String data : line) {
                    if (tokenLine.length() < 1)
                        tokenLine = data;
                    else
                        tokenLine += UString.COMMA + data;
                }
                bw.write(tokenLine);
                bw.newLine();
            }
            bw.close();

        } catch (FileNotFoundException e) {
            // Fileオブジェクト生成時の例外捕捉
            throw new UnchkedExecption(e + ":" + fileName);
        } catch (IOException e) {
            // BufferedWriterオブジェクトのクローズ時の例外捕捉
            throw new UnchkedExecption(e + ":" + fileName);
        }
    }

    private static String AbsoluteDirePath = setAbsoluteDirectoryPath();
}
