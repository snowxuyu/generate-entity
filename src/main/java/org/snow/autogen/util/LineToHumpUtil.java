package org.snow.autogen.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: snowxuyu
 * Date: 2016/8/3
 * Time: 20:48
 */
public class LineToHumpUtil {

    private static Pattern linePattern = Pattern.compile("_(\\w)");
    private static Pattern humpPattern = Pattern.compile("[A-Z]");

    /**首字母转大写*/
    public static String toUpperCaseFirstOne(String str)
    {
        if(Character.isUpperCase(str.charAt(0))) {
            return str;
        } else {
            return (new StringBuilder()).append(Character.toUpperCase(str.charAt(0))).append(str.substring(1)).toString();
        }
    }

    /**下划线转驼峰*/
    public static String lineToHump(String str){
        str = str.toLowerCase();
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while(matcher.find()){
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**驼峰转下划线,效率比上面高*/
    public static String humpToLine(String str){
        Matcher matcher = humpPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while(matcher.find()){
            matcher.appendReplacement(sb, "_"+matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
