package boot;

import static core.config.Configure.*;
import static core.constants.PathFilenamePropkey.*;

import java.util.Iterator;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;

import core.assembler.Assembler;
import core.config.Configure;
import core.constants.PathFilenamePropkey;

/**
 * Initial process
 * Configuration load to Configure class
 * @author Ksf-Wada
 */
public class ConfigurationLoader {

    static volatile boolean initialized        = false;
    static volatile Thread  initializingThread = null;

    static synchronized public void initialized(String realConfigFilePath) {
        if (initialized) {
            // do nothing
            return;
        }

        initializingThread = Thread.currentThread();

        // --------------load propertie files-------------------
        try {
            // ap properties
            Configuration apConf =new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class)
                    .configure(new Parameters().properties()
                    .setFileName(realConfigFilePath + AP_CONFIG_FILE)
                    .setListDelimiterHandler(new DefaultListDelimiterHandler(','))).getConfiguration();

            addConfiguration(apConf);

            // Assembler file
            Iterator<String> itr = Configure.getKeys();
            while (itr.hasNext()) {
                String key = itr.next();
                if (key.startsWith(PathFilenamePropkey.ASSEMBLER)){
                    String[] value = Configure.getStringArray(key);
                    Assembler.put(key, value);
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }

        initialized = true;
    }
}
