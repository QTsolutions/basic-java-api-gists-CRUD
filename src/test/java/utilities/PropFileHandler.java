package utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropFileHandler {
	
	static Properties properties = new Properties();
	static String filePath = "Config.properties";
	
	public static String readProperty(String property) {
        InputStream inPropFile = null;
        try {
            inPropFile = new FileInputStream(filePath);
            properties.load(inPropFile);
        } catch (IOException e) {
            System.out.println("There was Exception reading the Test data");
        }
        String value = properties.getProperty(property);
        return value;
    }

}
