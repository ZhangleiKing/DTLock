package buaazl.lock.util;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Vincent on 2018/4/22.
 */
public class Config {
    private final static Logger log = LoggerFactory.getLogger(Config.class);

    private static Configuration config;

    static {
        try {
            config = new PropertiesConfiguration("dtLock.properties");
        } catch (ConfigurationException e) {
            log.error(e.getMessage());
        }
    }

    public static String getStringConfig(String key) {
        return config.getString(key);
    }

    public static int getIntConfig(String key) {
        return config.getInt(key);
    }
}
