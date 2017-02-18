package org.snow.autogen.util;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: snowxuyu
 * Date: 2017/2/18
 * Time: 0:40
 */
public abstract class FileTemplateUtil {

    /**
     *
     * @param teplateName 模板文件名称
     * @param fileOutPath 输出路径
     * @param map 替换数据
     */
    public final static void replaceTemplateFile(String teplateName, String entityName, String fileOutPath, Map<String, Object> map) {
        Configuration configuration = new Configuration();
        Writer out = null;
        try {
            File file = new File(fileOutPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            String fileNamePath = fileOutPath + entityName;
            File fileName = new File(teplateName.contains("Mapper") ? fileNamePath + ".xml" : fileNamePath + ".java");
            fileName.createNewFile();

            configuration.setDirectoryForTemplateLoading(new File(FileTemplateUtil.class.getResource("/").getPath() +"templates/"));
            //获取模板（template）
            Template template = configuration.getTemplate(teplateName + ".ftl");
            out = new OutputStreamWriter(new FileOutputStream(teplateName.contains("Mapper") ? fileNamePath + ".xml" : fileNamePath + ".java"), "UTF-8");
            template.process(map, out);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
