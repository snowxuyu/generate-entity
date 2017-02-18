package org.snow.autogen.config;

import org.snow.autogen.domain.BaseDomain;
import org.snow.autogen.util.FileTemplateUtil;
import org.snow.autogen.util.LineToHumpUtil;
import org.snow.autogen.util.PropertyUtil;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Hello world!
 */
public class GeneratorConfig {

    private String url;  //数据库链接
    private String driver; //数据库驱动
    private String username;
    private String password;
    private String rootDir; //根目录
    private String packageName; //基础包名称
    private String entityPackageName;
    private String daoPackageName;
    private String serPackageName;
    private String implPackageName;
    private String controllerPackageName;
    private String dtoPackageName;
    private static String genPath = "";  //文件目录
    private final String DEFAULT_PATH = "autogenerator"; //子文件夹名称

    //私有构造方法
    private GeneratorConfig() {

    }

    //单例模式
    private static class AutoGenerateHolder {
        public static final GeneratorConfig instance = new GeneratorConfig();
    }


    public static void getInstanceInit() {
        Connection connection = null;
        try {
            //初始化配置信息
            AutoGenerateHolder.instance.initConfigParam();

            //获取数据库连接
            connection = AutoGenerateHolder.instance.connDataBase();

            //打印数据库链接信息
            AutoGenerateHolder.instance.printInitInfo(connection);

            //解析表结构
            AutoGenerateHolder.instance.tableStructtResovler(connection);

            System.out.println("文件生成完毕...");
            System.out.println("<<<-------文件所在路径为------->>>: " + genPath);
            System.out.println("success!!");
        } catch (Exception e) {
            System.out.println("文件生成失败..." + e);
            System.out.println("error!!");
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.out.println("数据库链接关闭异常" + e);
                }
            }
        }
    }

    /**
     * 初始化项目配置 数据库信息
     *
     * @return
     */
    private void initConfigParam() {
        //加载数据库配置
        url = PropertyUtil.getValue("jdbc.url");
        driver = PropertyUtil.getValue("jdbc.driver");
        username = PropertyUtil.getValue("jdbc.username");
        password = PropertyUtil.getValue("jdbc.password");
        rootDir = PropertyUtil.getValue("root.dir");
        packageName = PropertyUtil.getValue("packageName");
        entityPackageName = PropertyUtil.getValue("entityPackageName");
        serPackageName = PropertyUtil.getValue("servicePackageName");
        implPackageName = PropertyUtil.getValue("implPackageName");
        daoPackageName = PropertyUtil.getValue("daoPackageName");
        controllerPackageName = PropertyUtil.getValue("controllerPackageName");
        dtoPackageName = PropertyUtil.getValue("dtoPackageName");

        //删除生成目录
        File file = new File(rootDir.toUpperCase() + ":" + File.separator + DEFAULT_PATH);
        file.mkdirs();
        if (file.exists()) {
            deleteDir(file);
        }
    }

    /**
     * 连接数据库
     *
     * @return
     */
    private Connection connDataBase() {

        Connection conn;
        //获取数据库连接
        try {
            //加载驱动
            Class.forName(driver);
            //获取数据库连接
            conn = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("没有发现数据库驱动... " + e.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException("数据库连接失败..." + e.getMessage());
        }
        return conn;
    }


    /**
     * 打印数据库链接信息
     *
     * @param conn
     */

    private void printInitInfo(Connection conn) {
        try {
            //获取解析数据库对象 DatabaseMetaData
            DatabaseMetaData dbmd = conn.getMetaData();
            //打印数据库基本信息
            System.out.println("==================================================");
            System.out.println("当前连接数据库为: " + dbmd.getDatabaseProductName()
                    + "\n数据库版本: " + dbmd.getDatabaseProductVersion()
                    + "\n驱动版本: " + dbmd.getDriverVersion()
                    + "\n数据库名称: " + conn.getCatalog());
            System.out.println("==================================================");
        } catch (SQLException e) {
            System.out.println(e);
        }

    }


    /**
     * 解析库中表结构(字段名称、字段类型、注释)
     *
     * @param conn
     * @return
     */
    private void tableStructtResovler(Connection conn) {
        List<BaseDomain> domainList = new ArrayList<BaseDomain>();
        BaseDomain domain;

        try {
            DatabaseMetaData dbmd = conn.getMetaData();
            //获取所有表名称
            ResultSet tableSet = dbmd.getTables(null, null, "%", new String[]{"TABLE"});
            while (tableSet.next()) {
                //获取表名称
                String tableName = tableSet.getString("TABLE_NAME");
                //获取每个表的表结构
                ResultSet columSet = dbmd.getColumns(null, "%", tableName, "%");
                while (columSet.next()) {
                    domain = new BaseDomain();
                    domain.setColumName(columSet.getString("COLUMN_NAME"));
                    domain.setColumType(columSet.getString("TYPE_NAME"));
                    domain.setRemark(columSet.getString("REMARKS"));
                    domainList.add(domain);
                }
                //生成一个实体对象的相关文件
                autoGeneratorCode(domainList, tableName);
                domainList.clear();
            }
        } catch (SQLException e) {
            System.out.println("解析表结构出错" + e);
        }
    }

    /**
     * 生成实体对象相关文件
     *
     * @param domainList
     * @param tableName
     */
    private void autoGeneratorCode(List<BaseDomain> domainList, String tableName) {
        if (rootDir == null || rootDir.length() <= 0) {
            throw new RuntimeException("root.dir 未指定..");
        }

        if (packageName == null || packageName.length() <= 0) {
            throw new RuntimeException("packageName 未指定..");
        }

        if (entityPackageName == null || entityPackageName.length() <= 0) {
            entityPackageName = packageName.concat(".entity");
        }

        if (daoPackageName == null || daoPackageName.length() <= 0) {
            daoPackageName = packageName.concat(".dao");
        }

        if (serPackageName == null || serPackageName.length() <= 0) {
            serPackageName = packageName.concat(".service");
        }

        if (implPackageName == null || implPackageName.length() <= 0) {
            implPackageName = packageName.concat(".service.impl");
        }

        if (controllerPackageName == null || controllerPackageName.length() <= 0) {
            controllerPackageName = packageName.concat(".controller");
        }

        if (dtoPackageName == null || dtoPackageName.length() <= 0) {
            dtoPackageName = packageName.concat(".dto");
        }

        //创建文件夹
        genPath = rootDir.toUpperCase() + ":" + File.separator + DEFAULT_PATH;
        File directory = new File(genPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        //把tableName转换成EntityName
        String entityName = tableName2EntityName(tableName);

        //首字母转小写
        String lowEntityName = LineToHumpUtil.toLowerCaseFirstOne(entityName);


        //生成java 实体
        autoGenerateJavaEntity(entityName, domainList, tableName, directory, entityPackageName);

        //生成java Dto
        autoGenerateJavaDto(entityName,  directory, entityPackageName, dtoPackageName);

        //生成dao文件
        autoGenerateJavaDao(entityName, directory, entityPackageName, daoPackageName);

        //生成Service文件
        autoGenerateJavaSer(entityName, directory, entityPackageName, dtoPackageName, lowEntityName, serPackageName);

        //生成impl
        autoGenerateJavaImpl(entityName, directory, lowEntityName, entityPackageName, dtoPackageName, daoPackageName, serPackageName, implPackageName, domainList);

        //生成controller
        autoGenerateJavaController(entityName, directory, lowEntityName, entityPackageName, serPackageName, controllerPackageName);

        //生成Mapper文件 xml
        autoGenerateMapperXml(entityName, directory, entityPackageName, daoPackageName);

    }


    /**
     * 生成java实体对象
     *
     * @param domainList java属性字段
     * @param tableName  javabena对应的表名称
     * @param directory  文件路径
     */
    private void autoGenerateJavaEntity(String entityName, List<BaseDomain> domainList, String tableName, File directory, String entityPackageName) {
        Boolean dataFlag = true; //是否引入 import java.util.Date
        Boolean bigdFlag = true; //是否引入 java.math.BigDecimal
        Map<String, Object> map = new HashMap<String, Object>();
        StringBuilder sb = new StringBuilder();
        StringBuilder importSb = new StringBuilder();
        map.put("tableName", tableName);
        map.put("entityName", entityName);
        map.put("entityPackageName", entityPackageName);
        for (BaseDomain domain : domainList) {
            if ("id".equals(domain.getColumName()) || "create_time".equals(domain.getColumName())
                    || "create_name".equals(domain.getColumName()) || "update_time".equals(domain.getColumName())
                    || "update_name".equals(domain.getColumName())) {
                continue;
            }
            if ("Date".equals(sqlType2JavaType(domain.getColumType())) && dataFlag) {
                importSb.append("import java.util.Date;\n");
                dataFlag = false;
            } else if ("BigDecimal".equals(sqlType2JavaType(domain.getColumType())) && bigdFlag) {
                importSb.append("import java.math.BigDecimal;\n");
                bigdFlag = false;
            }
            sb.append("\t/** " + domain.getRemark() + " */\n" + "\tprivate " + sqlType2JavaType(domain.getColumType()) + " " + LineToHumpUtil.lineToHump(domain.getColumName()) + ";\n");
        }
        map.put("javaTypeField", sb.toString());
        map.put("requeireImport", importSb.toString());

        String filePath = directory + File.separator + entityPackageName.replace(".", File.separator) + File.separator;
        FileTemplateUtil.replaceTemplateFile("DemoEntity", entityName, filePath, map);
    }


    /**
     * 生成DTO文件
     * @param entityName
     * @param directory
     * @param entityPackageName
     * @param dtoPackageName
     */
    private void autoGenerateJavaDto(String entityName, File directory, String entityPackageName, String dtoPackageName) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("entityPackageName", entityPackageName);
        map.put("dtoPackageName", dtoPackageName);
        map.put("entityName", entityName);
        String filePath = directory + File.separator + dtoPackageName.replace(".", File.separator) + File.separator;
        FileTemplateUtil.replaceTemplateFile("DemoDto", entityName + "Dto", filePath, map);
    }


    /**
     * 生成dao文件
     *
     * @param entityName 实体类名
     * @param directory  文件路径
     */
    private void autoGenerateJavaDao(String entityName, File directory, String entityPackageName, String daoPackageName) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("entityPackageName", entityPackageName);
        map.put("daoPackageName", daoPackageName);
        map.put("entityName", entityName);
        String filePath = directory + File.separator + daoPackageName.replace(".", File.separator) + File.separator;
        FileTemplateUtil.replaceTemplateFile("DemoDao", entityName + "Dao", filePath, map);
    }


    /**
     * 生成Service
     *
     * @param entityName
     * @param directory
     */
    private void autoGenerateJavaSer(String entityName, File directory, String entityPackageName, String dtoPackageName, String lowEntityName, String serPackageName) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("entityPackageName", entityPackageName);
        map.put("dtoPackageName", dtoPackageName);
        map.put("low_entityName", lowEntityName);
        map.put("serPackageName", serPackageName);
        map.put("entityName", entityName);
        String filePath = directory + File.separator + serPackageName.replace(".", File.separator) + File.separator;
        FileTemplateUtil.replaceTemplateFile("DemoService", entityName + "Service", filePath, map);
    }


    /**
     * 生成impl
     *
     * @param entityName
     * @param directory
     */
    private void autoGenerateJavaImpl(String entityName, File directory, String lowEntityName,
                                      String entityPackageName, String dtoPackageName, String daoPackageName, String serPackageName, String implPackageName, List<BaseDomain> domainList) {
        StringBuilder sb = new StringBuilder();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("entityPackageName", entityPackageName);
        map.put("dtoPackageName", dtoPackageName);
        map.put("daoPackageName", daoPackageName);
        map.put("serPackageName", serPackageName);
        map.put("implPackageName", implPackageName);
        map.put("entityName", entityName);
        map.put("low_entityName", lowEntityName);
        for (BaseDomain domain : domainList) {
            if ("id".equals(domain.getColumName()) || "create_time".equals(domain.getColumName())
                    || "create_name".equals(domain.getColumName()) || "update_time".equals(domain.getColumName())
                    || "update_name".equals(domain.getColumName())) {
                continue;
            }

            sb.append("\t\t" +lowEntityName + ".set" + LineToHumpUtil.toUpperCaseFirstOne(LineToHumpUtil.lineToHump(domain.getColumName())) + "(" + lowEntityName + "Dto.get" + LineToHumpUtil.toUpperCaseFirstOne(LineToHumpUtil.lineToHump(domain.getColumName())) + "());\n");
        }
        map.put("setPropertiesField", sb.toString());
        String filePath = directory + File.separator + implPackageName.replace(".", File.separator) + File.separator;
        FileTemplateUtil.replaceTemplateFile("DemoServiceImpl", entityName + "ServiceImpl", filePath, map);
    }

    /**
     * 生成Controller文件
     *
     * @param entityName 实体类名称
     * @param directory  文件跟路径
     */
    private void autoGenerateJavaController(String entityName, File directory, String lowEntityName, String entityPackageName,
                                            String serPackageName, String controllerPackageName) {

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("dtoPackageName", dtoPackageName);
        map.put("entityPackageName", entityPackageName);
        map.put("serPackageName", serPackageName);
        map.put("controllerPackageName", controllerPackageName);
        map.put("entityName", entityName);
        map.put("low_entityName", lowEntityName);
        String filePath = directory + File.separator + controllerPackageName.replace(".", File.separator) + File.separator;
        FileTemplateUtil.replaceTemplateFile("DemoController", entityName + "Controller", filePath, map);
    }


    /**
     * 生成Mapper文件 xml
     *
     * @param entityName 实体类名称
     * @param directory  文件路径
     */
    private void autoGenerateMapperXml(String entityName, File directory, String entityPackageName, String daoPackageName) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("entityPackageName", entityPackageName);
        map.put("daoPackageName", daoPackageName);
        map.put("entityName", entityName);
        String filePath = directory + File.separator + "xml" + File.separator;
        FileTemplateUtil.replaceTemplateFile("DemoMapper", entityName + "Mapper", filePath, map);
    }

    /**
     * 表名转换成类名
     *
     * @param tableName
     * @return
     */
    private String tableName2EntityName(String tableName) {
        //截取第一个下划线字符串的字符串 为 类名称
        String entity_table_name = tableName.substring(tableName.indexOf("_") + 1);
        //下划线转驼峰
        String toHump = LineToHumpUtil.lineToHump(entity_table_name);
        //首字母大写
        return LineToHumpUtil.toUpperCaseFirstOne(toHump);
    }


    /**
     * 功能：获得列的数据类型
     *
     * @param sqlType
     * @return
     */
    private String sqlType2JavaType(String sqlType) {

        if (sqlType.equalsIgnoreCase("bit")) {
            return "Boolean";
        } else if (sqlType.equalsIgnoreCase("tinyint") || sqlType.equalsIgnoreCase("smallint")
                || sqlType.equalsIgnoreCase("int")) {
            return "Integer";
        } else if (sqlType.equalsIgnoreCase("bigint")) {
            return "Long";
        } else if (sqlType.equalsIgnoreCase("float")) {
            return "Double";
        } else if (sqlType.equalsIgnoreCase("decimal") || sqlType.equalsIgnoreCase("numeric")
                || sqlType.equalsIgnoreCase("real") || sqlType.equalsIgnoreCase("money")
                || sqlType.equalsIgnoreCase("smallmoney")) {
            return "BigDecimal";
        } else if (sqlType.equalsIgnoreCase("varchar") || sqlType.equalsIgnoreCase("char")
                || sqlType.equalsIgnoreCase("nvarchar") || sqlType.equalsIgnoreCase("nchar")
                || sqlType.equalsIgnoreCase("text") || sqlType.equalsIgnoreCase("varchar2")) {
            return "String";
        } else if (sqlType.equalsIgnoreCase("datetime") || sqlType.equalsIgnoreCase("date")) {
            return "Date";
        } else if (sqlType.equalsIgnoreCase("image")) {
            return "Blod";
        }
        return "String";
    }


    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     *
     * @param dir 将要删除的文件目录
     * @return
     */
    private void deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++) {
                deleteDir(new File(dir, children[i]));
            }
        }
        // 目录此时为空，可以删除
        dir.delete();
    }
}
