package lab.dataBaseTools;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesProvider {

    /**
     * The type Properties provider.
     */
    private static final Properties appProperties = new Properties();

    static {

        try {
            InputStream inputStream = PropertiesProvider.class.getClassLoader().getResourceAsStream("application.properties");
            appProperties.load(inputStream);
        } catch (IOException e) {
            System.out.println(" упс ");
            throw new RuntimeException();
        }
    }

    /**
     * Gets app properties.
     *
     * @return the app properties
     */
    public static Properties getAppProperties() {
        return new Properties(appProperties);
    }
}


