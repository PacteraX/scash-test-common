package system.boot;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;

import system.assembler.Assembler;
import system.assembler.IAssembler;
import system.config.Configure;

public class Initializer {
    protected static AtomicBoolean initialized = new AtomicBoolean(false);
    protected static AtomicBoolean success  = new AtomicBoolean(false);
    protected static String ASSEMBLER_KEY = "assembler.early";
    private static String INCLUDE_KEY = "include.file";
    protected static String DEFAULT_PROPERY_NAME = "application.properties";
    protected static String path;

    synchronized static public void init(String path) {
        if (initialized.getAndSet(true)) {
            return;
        }
        try {
            Initializer.path = path;
            loadConfig(path);
            assembleAll();
            success.set(true);
        } catch (Throwable e) {
            String msg = "initialize error.";
            throw new RuntimeException(msg, e);
        }
    }

    static void loadConfig(String path) {
        try {
            String filepath = path + DEFAULT_PROPERY_NAME;
            
            Parameters params = new Parameters();
            FileBasedConfigurationBuilder<PropertiesConfiguration> builder = new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class)
            .configure(params.properties()
            .setFileName(filepath)
            .setListDelimiterHandler(new DefaultListDelimiterHandler(',')));
            
            Configuration conf = builder.getConfiguration();
            
            Configure.addConfiguration(conf);
            Iterator<String> keys = Configure.getKeys();
            while (keys.hasNext()) {
                String key = keys.next();
                if (INCLUDE_KEY.equalsIgnoreCase(key)) {
                    String[] inclist = Configure.getStringArray(key);
                    for (int i = 0; i < inclist.length; i++) {
                        PropertiesConfiguration _config = new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class)
                        .configure(new Parameters().properties().setFileName(inclist[i])).getConfiguration();
                        Configure.addConfiguration(_config);
                    }
                }
            }
        } catch (ConfigurationException e) {
            throw new RuntimeException("Failed to load configuration: " + e.getMessage(), e);
        }
    }

    static void assembleAll() {
        Assembler assembler = new Assembler();
        Iterator<String> keys = Configure.getKeys();
        while (keys.hasNext()) {
            String key = keys.next();
            if (key.startsWith(ASSEMBLER_KEY)) {
                String[] classNames = Configure.getStringArray(key);
                for (String className : classNames) {
                    try {
                        Object obj = Class.forName(className).newInstance();
                        assembler.register((IAssembler) obj);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        try {
            assembler.doAssembleAll();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
