package org.snow.autogen.filegenerate;

import org.snow.autogen.domain.BaseDomain;
import org.snow.autogen.dto.RequestDto;
import org.snow.autogen.system.Constants;
import org.snow.autogen.util.FileTemplateUtil;
import org.snow.autogen.util.LineToHumpUtil;
import org.snow.autogen.util.ParamsValidatorUtil;
import org.snow.autogen.util.StringUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created with IntelliJ IDEA.
 * User: snowxuyu
 * Date: 2017-04-28
 * Time: 21:38
 */
@Service("generateFile")
public class GenerateFileImpl implements GenerateFile {

    @Override
    public String autoGenerator(RequestDto requestDto) throws RuntimeException, IllegalAccessException {
        ParamsValidatorUtil.validateParam(requestDto,
                new String[]{"jdbcDriver", "entityPackageName",
                        "dtoPackageName", "daoPackageName", "servicePackageName",
                        "serviceImplPackageName", "controllerPackageName"});
        Connection connection = null;
        String genPath = "";  //文件目录
        String jdbcUrl = requestDto.getJdbcUrl();
        String jdbcDriver = requestDto.getJdbcDriver();
        String jdbcUsername = requestDto.getJdbcUsername();
        String jdbcPassword = requestDto.getJdbcPassword();
        String rootDir = requestDto.getRootDir();
        String packageName = requestDto.getPackageName();
        String entityPackageName = requestDto.getEntityPackageName();
        String dtoPackageName = requestDto.getDtoPackageName();
        String daoPackageName = requestDto.getDaoPackageName();
        String servicePackageName = requestDto.getServicePackageName();
        String serviceImplPackageName = requestDto.getServiceImplPackageName();
        String controllerPackageName = requestDto.getControllerPackageName();

        //初始化路径名称
        //判断url是否包含 ?createDatabaseIfNotExist=true&zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=utf-8
        if ((StringUtils.isEmpty(jdbcUrl) || Constants.Common.JDBC_DRIVER.equals(jdbcUrl)) && !jdbcUrl.contains(Constants.Common.URL_EXT)) {
            jdbcUrl += Constants.Common.URL_EXT;
        }
        if (StringUtils.isEmpty(jdbcDriver)) {
            jdbcDriver = Constants.Common.JDBC_DRIVER;
        }


        if (StringUtils.isEmpty(entityPackageName)) {
            entityPackageName = packageName.concat(".entity");
        }

        if (StringUtils.isEmpty(dtoPackageName)) {
            dtoPackageName = packageName.concat(".dto");
        }

        if (StringUtils.isEmpty(daoPackageName)) {
            daoPackageName = packageName.concat(".dao");
        }

        if (StringUtils.isEmpty(servicePackageName)) {
            servicePackageName = packageName.concat(".business");
        }

        if (StringUtils.isEmpty(serviceImplPackageName)) {
            serviceImplPackageName = packageName.concat(".business.impl");
        }

        if (StringUtils.isEmpty(controllerPackageName)) {
            controllerPackageName = packageName.concat(".controller");
        }

        genPath = rootDir.toUpperCase() + ":" + File.separator + Constants.Common.DEFAULT_PATH;
        //删除生成目录
        File file = new File(genPath);
        file.mkdirs();
        if (file.exists()) {
            deleteDir(file);
        }

        try {
            //连接数据库
            connection = connDataBase(jdbcUrl, jdbcUsername, jdbcPassword, jdbcDriver);
            //打印数据库连接信息
            printDataBaseInfo(connection);
            //解析表结构
            tableStructtResovler(connection, entityPackageName, dtoPackageName, daoPackageName, servicePackageName, serviceImplPackageName, controllerPackageName, genPath);
            System.out.println("文件生成完毕...");
            System.out.println("<<<-------文件所在路径为------->>>: " + genPath);
            System.out.println("success!!");
        } catch (Exception e) {
            System.out.println("文件生成失败..." + e);
            throw new RuntimeException("文件生成失败..." + e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.out.println("数据库链接关闭异常" + e);
                }
            }
        }

        return genPath;
    }


    /**
     * 连接数据库
     *
     * @return
     */
    private Connection connDataBase(String url, String username, String password, String driver) {

        Connection conn;
        //获取数据库连接
        try {
            //加载驱动
            Class.forName(driver);
            //获取数据库连接
            conn = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("没有发现数据库驱动... " + e);
        } catch (SQLException e) {
            throw new RuntimeException("数据库连接失败..." + e);
        }
        return conn;
    }

    /**
     * 打印数据库链接信息
     *
     * @param conn
     */

    private void printDataBaseInfo(Connection conn) {
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
            throw new RuntimeException(e);
        }

    }

    /**
     * 解析库中表结构(字段名称、字段类型、注释)
     *
     * @param conn
     * @return
     */
    private void tableStructtResovler(Connection conn, String entityPackageName, String dtoPackageName, String daoPackageName, String servicePackageName, String serviceImplPackageName, String controllerPackageName, String genPath) {
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
                autoGeneratorCode(domainList, tableName, entityPackageName, dtoPackageName, daoPackageName, servicePackageName, serviceImplPackageName, controllerPackageName, genPath);
                domainList.clear();
            }
        } catch (SQLException e) {
            throw new RuntimeException("解析表结构出错" + e);
        }
    }

    /**
     * 生成实体对象相关文件
     *
     * @param domainList
     * @param tableName
     */
    private void autoGeneratorCode(List<BaseDomain> domainList, String tableName, String entityPackageName, String dtoPackageName, String daoPackageName, String servicePackageName, String serviceImplPackageName, String controllerPackageName, String genPath) {

        //创建文件夹
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
        autoGenerateJavaDto(entityName, directory, entityPackageName, dtoPackageName);

        //生成dao文件
        autoGenerateJavaDao(entityName, directory, entityPackageName, daoPackageName);

        //生成Service文件
        autoGenerateJavaSer(entityName, directory, entityPackageName, dtoPackageName, lowEntityName, servicePackageName);

        //生成impl
        autoGenerateJavaImpl(entityName, directory, lowEntityName, entityPackageName, dtoPackageName, daoPackageName, servicePackageName, serviceImplPackageName, domainList);

        //生成controller
        autoGenerateJavaController(entityName, directory, lowEntityName, entityPackageName, servicePackageName, controllerPackageName, dtoPackageName);

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
                    || "create_user".equals(domain.getColumName()) || "update_time".equals(domain.getColumName())
                    || "update_user".equals(domain.getColumName())) {
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
     *
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
        FileTemplateUtil.replaceTemplateFile("DemoBusiness", entityName + "Business", filePath, map);
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

            sb.append("\t\t" + lowEntityName + ".set" + LineToHumpUtil.toUpperCaseFirstOne(LineToHumpUtil.lineToHump(domain.getColumName())) + "(" + lowEntityName + "Dto.get" + LineToHumpUtil.toUpperCaseFirstOne(LineToHumpUtil.lineToHump(domain.getColumName())) + "());\n");
        }
        map.put("setPropertiesField", sb.toString());
        String filePath = directory + File.separator + implPackageName.replace(".", File.separator) + File.separator;
        FileTemplateUtil.replaceTemplateFile("DemoBusinessImpl", entityName + "BusinessImpl", filePath, map);
    }

    /**
     * 生成Controller文件
     *
     * @param entityName 实体类名称
     * @param directory  文件跟路径
     */
    private void autoGenerateJavaController(String entityName, File directory, String lowEntityName, String entityPackageName,
                                            String serPackageName, String controllerPackageName, String dtoPackageName) {

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
