package system.config;

import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.configuration2.CompositeConfiguration;
import org.apache.commons.configuration2.Configuration;

/**
 * depend -> ConfigurationLoader
 *
 * @author Ksf-Wada
 */
public class Configure {

    private static String ENCODING   = "encoding";
    private static String ENC_DEFAULT = "UTF-8";
    private static String encoding;

    private static CompositeConfiguration configs = new CompositeConfiguration();

    public static void addConfiguration(Configuration configuration) {
        configs.addConfiguration(configuration);
        encoding = getEncoding();
    }

    /**
     * Resource file load message resource file and field resource file.
     *
     */
    public static void addResource(Properties resource){
        Set<Object> keys = resource.keySet();
        for (Object key : keys)
            configs.addProperty((String) key, resource.get(key));
        encoding = getEncoding();
    }

    public static boolean containsKey(String name) {
        return configs.containsKey(name);
    }

    public static String getString(String name) {
        return configs.getString(name);
    }

    public static String getString(String name, String defaultValue) {
        return (configs.containsKey(name)) ? configs.getString(name) : defaultValue;
    }

    public static String[] getStringArray(String name) {
        return configs.getStringArray(name);
    }

    public static int getInt(String name) {
        return configs.getInt(name);
    }

    public static int getInt(String name, int defaultValue) {
        return (configs.containsKey(name)) ? configs.getInt(name) : defaultValue;
    }

    public static String getString(Enum<?> name) {
        return getString(name.name());
    }

    public static String[] getStringArray(Enum<?> name) {
        return getStringArray(name.name());
    }

    public static int getInt(Enum<?> name) {
        return getInt(name.name());
    }

    public static String getEncording(){
        try{
            return encoding.toString();
        }catch(NullPointerException e){
            encoding = getEncoding();
            return encoding;
        }
    }

    public static Iterator<String> getKeys() {
        return configs.getKeys();
    }

    public static boolean getBoolean(String name) {
        return Boolean.parseBoolean(getString(name));
    }

    private static String getEncoding(){
        return configs.containsKey(ENCODING) ? configs.getString(ENCODING) : ENC_DEFAULT ;
    }

}
