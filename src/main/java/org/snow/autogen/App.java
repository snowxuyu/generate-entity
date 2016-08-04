package org.snow.autogen;

import org.snow.autogen.domain.BaseDomain;
import org.snow.autogen.util.LineToHumpUtil;
import org.snow.autogen.util.PropertyUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

/**
 * Hello world!
 */
public class App {

    String rootDir = null;
    String entityPackageName = null;
    String daoPackageName = null;
    String serPackageName = null;
    String implPackageName = null;
    /**
     * 初始化数据库信息
     *
     * @return
     */
    private Connection initParam() {
        //加载数据库配置
        String url = PropertyUtil.getValue("jdbc.url");
        String driver = PropertyUtil.getValue("jdbc.driver");
        String username = PropertyUtil.getValue("jdbc.username");
        String password = PropertyUtil.getValue("jdbc.password");
        rootDir = PropertyUtil.getValue("root.dir");
        entityPackageName = PropertyUtil.getValue("entityPackageName");
        daoPackageName = PropertyUtil.getValue("daoPackageName");
        serPackageName = PropertyUtil.getValue("serPackageName");
        implPackageName = PropertyUtil.getValue("implPackageName");
        Connection conn;
        
        //删除生成目录
        File file = new File(rootDir.toUpperCase()+":"+File.separator+"autogenerate");
        file.mkdirs();
        if (file.exists()) {
            deleteDir(file);
        }

        try {
            //加载驱动
            Class.forName(driver);
            //获取数据库连接
            conn = DriverManager.getConnection(url, username, password);

            printInitInfo(conn);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("没有发现数据库驱动... " + e.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException("数据库连接失败..." + e.getMessage());
        }
        return conn;
    }


    /**
     * 打印初始化信息
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

            getTableStruct(conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    /**
     * 获取库中所有表结构(字段名称、字段类型、注释)
     *
     * @param conn
     * @return
     */
    private void getTableStruct(Connection conn) {
        ArrayList<BaseDomain> domainList = new ArrayList<BaseDomain>();
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
                autoGenerate(domainList, tableName);
                domainList.clear();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成实体对象相关文件
     *
     * @param domainList
     * @param tableName
     */
    private void autoGenerate(ArrayList<BaseDomain> domainList, String tableName) {
        if (rootDir == null || rootDir.length() <= 0) {
            System.out.println("root.dir 未指定..");
            return;
        }

        if (entityPackageName == null || entityPackageName.length() <= 0) {
            System.out.println("entityPackageName 未指定..");
            return;
        }

        if (daoPackageName == null || daoPackageName.length() <= 0) {
            daoPackageName = entityPackageName.substring(0, entityPackageName.lastIndexOf(".")).concat(".dao");
        }

        if (serPackageName == null || serPackageName.length() <= 0) {
            serPackageName = entityPackageName.substring(0, entityPackageName.lastIndexOf(".")).concat(".service");
        }

        if (implPackageName == null || implPackageName.length() <= 0) {
            implPackageName = entityPackageName.substring(0, entityPackageName.lastIndexOf(".")).concat(".service.impl");
        }

        //创建文件夹
        File directory = new File(rootDir.toUpperCase() + ":" + File.separator + "autogenerate");
        if(!directory.exists()) {
            directory.mkdirs();
        }

        //把tableName转换成EntityName
        String className = tableName2EntityName(tableName);


        //生成java 实体
        autoGenerateJavaEntity(domainList, tableName, directory, entityPackageName);

        //生成dao文件
        autoGenerateJavaDao(className, directory, entityPackageName, daoPackageName);

        //生成Mapper文件 xml
        autoGenerateMapperXml(className, directory, entityPackageName, daoPackageName);

        //生成Service文件
        autoGenerateJavaSer(className, directory, entityPackageName, serPackageName);

        //生成impl
        autoGenerateJavaImpl(className, directory, entityPackageName, serPackageName, implPackageName);

    }

    /**
     * 生成java实体对象
     * @param domainList java属性字段
     * @param tableName javabena对应的表名称
     * @param directory 文件路径
     */
    private void autoGenerateJavaEntity(ArrayList<BaseDomain> domainList, String tableName, File directory, String entityPackageName) {
        Boolean dataFlag = true;
        Boolean bigdFlag = true;

        StringBuilder javaSb = new StringBuilder();

        try {
            //把tableName转换成EntityName
            String className = tableName2EntityName(tableName);

            //创建java文件
            String  filePath = directory + File.separator + "entity";
            File dir = new File(filePath);
            dir.mkdirs();
            String javaPath = filePath + File.separator + className;
            File file = new File(javaPath + ".java");
            file.createNewFile();

            //拼装java
            javaSb.append("package ").append(entityPackageName + ";\n");
            javaSb.append("\n");
            javaSb.append("import lombok.Data;\n");
            javaSb.append("import javax.persistence.Table;\n");
            javaSb.append("import org.framework.basic.entity.BaseEntity;\n");

            //判断类型 是否需要import
            for (int i=0; i<domainList.size(); i++) {
                BaseDomain domain = domainList.get(i);
                String columType = domain.getColumType();
                String javaType = sqlType2JavaType(columType);
                if ("Date".equals(javaType) && dataFlag) {
                    javaSb.append("import java.util.Date;\n");
                    dataFlag = false;
                } else if ("BigDecimal".equals(javaType) && bigdFlag) {
                    javaSb.append("import java.math.BigDecimal;\n");
                    bigdFlag = false;
                }
            }

            javaSb.append("\n");

            javaSb.append("@Data\n");
            javaSb.append("@Table(name=\""+ tableName +"\")\n");
            javaSb.append("public class " + className + " extends BaseEntity {\n");


            for (int i = 0; i < domainList.size(); i++) {
                BaseDomain domain = domainList.get(i);
                String remark = domain.getRemark();
                String javaType = sqlType2JavaType(domain.getColumType());
                String fieldName = LineToHumpUtil.lineToHump(domain.getColumName());
                javaSb.append("\t/** " + remark + " */\n");
                javaSb.append("\tprivate " + javaType + " " + fieldName + ";\n");
            }
            javaSb.append("\n}");

            dataWriteFile(javaSb, file);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 生成dao文件
     * @param className  类名
     * @param directory 文件路径
     * @param entityPackageName 实体包名称
     * @param daoPackageName dao包名称
     */
    private void autoGenerateJavaDao(String className, File directory, String entityPackageName, String daoPackageName) {
        StringBuilder javaDaoSb = new StringBuilder();

        //创建dao文件
        try {
            String dirPath = directory + File.separator + "dao";
            File dir = new File(dirPath);
            dir.mkdirs();
            String  daoPath = dirPath + File.separator + className;
            File file = new File(daoPath + "Dao.java");
            file.createNewFile();

            //拼装dao
            javaDaoSb.append("package " + daoPackageName + ";\n");
            javaDaoSb.append("\n");
            javaDaoSb.append("import " + entityPackageName + "." + className + ";\n");
            javaDaoSb.append("import org.framework.basic.dao.BaseDao;\n");
            javaDaoSb.append("\n");
            javaDaoSb.append("public interface " + className + "Dao extends BaseDao<" + className + "> {");
            javaDaoSb.append("\n");
            javaDaoSb.append("\n}");

            dataWriteFile(javaDaoSb, file);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 生成Service
     * @param className
     * @param directory
     * @param entityPackageName
     */
    private void autoGenerateJavaSer(String className, File directory, String entityPackageName, String serPackageName) {
        StringBuilder javaSerSb = new StringBuilder();

        try {
            String dirPath = directory + File.separator + "ser";
            File dir = new File(dirPath);
            dir.mkdirs();
            String  serPath = dirPath + File.separator + className;
            File file = new File(serPath + "Service.java");
            file.createNewFile();

            //拼装service
            javaSerSb.append("package " + serPackageName + ";\n");
            javaSerSb.append("\n");
            javaSerSb.append("import " + entityPackageName + "." + className + ";\n");
            javaSerSb.append("import org.framework.basic.service.BaseService;\n");
            javaSerSb.append("\n");
            javaSerSb.append("public interface " + className + "Service extends BaseService<" + className + "> {\n");
            javaSerSb.append("\n");
            javaSerSb.append("\n}");

            dataWriteFile(javaSerSb, file);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     *   生成impl
     * @param className
     * @param directory
     * @param entityPackageName
     * @param serPackageName
     * @param implPackageName
     */
    private void autoGenerateJavaImpl(String className, File directory, String entityPackageName, String serPackageName, String implPackageName) {
        StringBuilder javaImplSb = new StringBuilder();

        try {
            String dirPath = directory + File.separator + "impl";
            File dir = new File(dirPath);
            dir.mkdirs();
            String  serPath = dirPath + File.separator + className;
            File file = new File(serPath + "ServiceImpl.java");
            file.createNewFile();

            javaImplSb.append("package " + implPackageName + ";\n");
            javaImplSb.append("\n");
            javaImplSb.append("import org.springframework.stereotype.Service;\n");
            javaImplSb.append("import org.framework.basic.service.impl.BaseServiceImpl;\n");
            javaImplSb.append("import " + serPackageName + "." + className + "Service;\n");
            javaImplSb.append("import " + entityPackageName + "." + className + ";\n");
            javaImplSb.append("\n");
            javaImplSb.append("@Service\n");
            javaImplSb.append("public class " + className + "ServiceImpl extends BaseServiceImpl<" + className + "> implements " + className + "Service {\n");
            javaImplSb.append("\n");
            javaImplSb.append("\n}");

            dataWriteFile(javaImplSb, file);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 生成Mapper文件 xml
     * @param className 实体类名称
     * @param directory 文件路径
     * @param entityPackageName 实体类包名
     * @param daoPackageName dao包名
     */
    private void autoGenerateMapperXml(String className, File directory, String entityPackageName, String daoPackageName) {
        StringBuilder xmlSb = new StringBuilder();

        //创建xml文件
        try {
            String dirPath = directory + File.separator + "xml";
            File dir = new File(dirPath);
            dir.mkdirs();
            String  xmlPath = dirPath + File.separator + className;
            File file = new File(xmlPath + "Mapper.xml");
            file.createNewFile();

            xmlSb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
            xmlSb.append("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >\n");
            xmlSb.append("<mapper namespace=\"" + daoPackageName + "." + className + "Dao" + "\">\n");
            xmlSb.append("\t<resultMap id=\"getMap\" type=\"" + entityPackageName + "." + className + "\">\n");
            xmlSb.append("\t</resultMap>\n");
            xmlSb.append("\n");
            xmlSb.append("\n");
            xmlSb.append("</mapper>");

            dataWriteFile(xmlSb, file);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 把数据写入文件
     * @param sb 数据
     * @param file 文件
     */
    private void dataWriteFile(StringBuilder sb, File file) {
        try {
            String data = new String(sb);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(data.getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
        } else if (sqlType.equalsIgnoreCase("datetime")) {
            return "Date";
        } else if (sqlType.equalsIgnoreCase("image")) {
            return "Blod";
        }
        return "String";
    }


     /**
     * 递归删除目录下的所有文件及子目录下所有文件
     * @param dir 将要删除的文件目录
     * @return
     */
    private boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }
    
    
    public static void main(String[] args) throws SQLException {
        //初始化信息
        Connection conn = new App().initParam();

        conn.close();

        System.out.println("文件生成完毕...");
        System.out.println("success!!");
    }

}
