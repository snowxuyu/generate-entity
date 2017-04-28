package org.snow.autogen.util;

import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Copyright @ 2016QIANLONG.
 * All right reserved.
 * Class Name : org.framework.common.util
 * Description :
 * Author : gaoguoxiang
 * Date : 2016/12/5
 */

public abstract class ParamsValidatorUtil {
    /**
     * 字符串长度验证
     *
     * @param param
     * @param limit
     * @return
     */
    public static boolean maxLength(String param, Integer limit) {
        if (param.length() > limit) {
            return false;
        }
        return true;
    }

    /**
     * 判断是否为整数
     *
     * @param str 传入的字符串
     * @return 是整数返回true, 否则返回false
     */
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    /**
     * 验证是否为浮点数或者整数
     *
     * @param obj     验证对象
     * @param strings 排除的字段
     * @return
     */
    public static void isNumeric(Object obj, String[] strings) throws IllegalAccessException {

        ArrayList<String> list = new ArrayList<>();
        for (String str : strings) {
            list.add(str);
        }
        isNumeric(obj, list);
    }


    /**
     * 验证是否为浮点数或者整数
     *
     * @param obj, list
     * @return
     */
    public static void isNumeric(Object obj, List<String> list) throws IllegalAccessException {

        Class<? extends Object> objClass = obj.getClass();
        Field[] fields = objClass.getDeclaredFields();//获取对象所有属性
        for (Field field : fields) {
            if (list.contains(field.getName()) || CollectionUtils.isEmpty(list)) {
                continue;
            }
            field.setAccessible(true);
            Object o = field.get(obj);
            if (o != null) {
                String param = o.toString();
                Pattern pattern = Pattern.compile("^(-?\\d+)(\\.\\d+)?$");
                if (!StringUtils.isEmpty(param)) {
                    Matcher isNum = pattern.matcher(param);
                    if (!isNum.matches()) {
                        throw new RuntimeException(field.getName() + "不是数字");
                    }
                }
            }
        }
    }

    /**
     * 对object的所有属性进行非空校验
     */
    public static void validateParam(Object obj) throws IllegalArgumentException, IllegalAccessException {
        validateParam(obj, new String[]{});
    }

    /**
     * 对object的属性进行非空校验，排除字符串数组中的字段
     *
     * @param obj
     * @param strings
     */
    public static void validateParam(Object obj, String[] strings) throws IllegalArgumentException, IllegalAccessException {
        List<String> list = new ArrayList<String>();
        for (String foo : strings) {
            list.add(foo);
        }
        validateParam(obj, list);
    }

    /**
     * 对object的属性进行非空校验，排除list中的字段
     *
     * @param obj  验证对象
     * @param list 排除参数
     */
    public static void validateParam(Object obj, List<String> list) throws IllegalArgumentException, IllegalAccessException {
        Class<? extends Object> objClass = obj.getClass();
        Field[] declaredFields = objClass.getDeclaredFields();//获取该对象的所有字段
        for (Field field : declaredFields) {
            if ((list.contains(field.getName())) || list == null) {
                continue;
            }
            field.setAccessible(true);
            Object o = field.get(obj);
            if (o == null || o.toString().length() == 0) {
                throw new IllegalArgumentException(field.getName() + "不能为空！");
            }
        }

    }
}
