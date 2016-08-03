package org.snow.autogen.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: snowxuyu
 * Date: 2016/8/3
 * Time: 20:48
 */

public class PropertyUtil {
    private PropertyUtil () {}

    private static Properties prop = new Properties();

    static {
        InputStream in = null;
        try {
            in = PropertyUtil.class.getResourceAsStream("/jdbc.properties");
            prop.load(in);
            in.close();
        } catch (IOException e) {
            throw new RuntimeException("读取 sql.properties 文件失败..." + e.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static String getValue(String key) {
        String property = prop.getProperty(key);
        if (property != null && property.length() > 0) {
            return property;
        } else {
            return null;
        }
    }
}
