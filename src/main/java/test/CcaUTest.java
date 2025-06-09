package test;

import java.io.FileInputStream;
import java.util.Properties;

import boot.Initializer;
import core.util.UFile;

public class CcaUTest {
    private static final String CONF_PATH = "conf/";
    public static final Properties DEFAULT = new Properties();
    public static void init(){
        system.boot.Initializer.init(UFile.getAbsoluteDirectoryPath() + CONF_PATH);
        System.out.println("Initializer Path = " + UFile.getAbsoluteDirectoryPath() + CONF_PATH);
        Initializer.Initialized(UFile.getAbsoluteDirectoryPath() + CONF_PATH);
        System.out.println("Initializer2 Path = " + UFile.getAbsoluteDirectoryPath() + CONF_PATH);
    }

    public static Properties readProperties(String fileName) {
        if (fileName == null)
            return DEFAULT;

        try {
            System.out.println("target:" + fileName);
            Properties values = new Properties();
            FileInputStream stream = new FileInputStream(fileName);
            values.load(stream);
            stream.close();
            return values;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
