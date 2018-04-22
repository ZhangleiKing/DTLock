package buaazl.lock.config;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Test;

/**
 * Created by Vincent on 2018/4/22.
 */
public class propertiesLoaderTest {

    @Test
    public void testPropertiesString() {
        Configuration config = null;
        try {
            config = new PropertiesConfiguration("dtLock.properties");
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
        String type = config.getString("type");
        assert type.equals("zookeeper");
    }
}
