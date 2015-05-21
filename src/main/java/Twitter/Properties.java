package twitter;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * Created by mahmoud on 2/26/15.
 */
public class Properties {

    public String consumerKey, consumerSecret, authKey, authSecret;

    public Properties(String configName)  {

        java.util.Properties prop = new java.util.Properties();

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(configName);

        if (inputStream != null) {
            try {
                prop.load(inputStream);
                consumerKey = prop.getProperty("consumer_key");
                consumerSecret = prop.getProperty("consumer_secret");
                authKey = prop.getProperty("auth_key");
                authSecret = prop.getProperty("auth_secret");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args){
            Properties properties = new Properties("config.lababidi");
            System.out.println(properties.consumerKey);
    }
}
