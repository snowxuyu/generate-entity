package org.snow.autogen.system;

/**
 * Created by snow on 2015/7/12.
 * 常量接口
 */
public interface Constants {

    /**
     * 系统级常量
     */
    public interface System {
        public static final String SUCCESSS = "1" ;  //1表示成功
        public static final String ERROR = "-1";   //-1表示失败

        public static final String OK_CODE = "200";
    }

    /**
     * 业务常量 普通常量等
     */
    public interface Common {

        public static final String URL_EXT = "?createDatabaseIfNotExist=true&zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=utf-8";
        public static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        public static final String DEFAULT_PATH = "autogenerator"; //子文件夹名称
        public static final String TEMPLATE_PATH = "static/demoTemplate/"; //模板路径
    }
}
