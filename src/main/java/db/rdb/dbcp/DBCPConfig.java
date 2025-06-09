package db.rdb.dbcp;

import system.config.Configure;
import system.util.UString;
import db.DBConstants;

public class DBCPConfig {

    public static String[] getProperties(final String name) {
        String value = Configure.getString(DBConstants.DBCP_PREFIX_KEY + name);
        if (UString.isNull(value)) {
            return new String[0];
        }
        String[] results = Configure.getStringArray(DBConstants.DBCP_PREFIX_KEY + name);
        return (results == null) ? new String[0] : results;
    }

    public static String getProperty(final String name) {
        String value = Configure.getString(DBConstants.DBCP_PREFIX_KEY + name);
        if (UString.isNull(value)) {
            throw new IllegalArgumentException("Not existed property : " + name);
        }
        return value;
    }

    public static String getProperty(final String name, final String defaultValue) {
        String value = Configure.getString(DBConstants.DBCP_PREFIX_KEY + name);
        return (value == null) ? defaultValue : value;
        // return Configure.getString(name, defaultValue);
    }

    public static int getIntProperty(final String name) {
        return Integer.parseInt(getProperty(name));
    }

    public static int getIntProperty(final String name, final int defaultValue) {
        return Integer.parseInt(getProperty(name, Integer.toString(defaultValue)));
    }

    public static long getLongProperty(final String name) {
        return Long.parseLong(getProperty(name));
    }

    public static long getLongProperty(final String name, final long defaultValue) {
        return Long.parseLong(getProperty(name, Long.toString(defaultValue)));
    }
}
