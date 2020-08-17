package cn.smallyoung.websiteadmin.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author smallyoung
 * @date 2020/8/3
 */
public class ConvertBean {

    public static Map<String, Object> bean2Map(Object bean, String... args) {
        Map<String, Object> map = new HashMap<>(args.length);
        if (bean == null) {
            return null;
        }
        try {
            Class<?> type = bean.getClass();
            BeanInfo beanInfo = Introspector.getBeanInfo(type);
            PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();

            for (PropertyDescriptor descriptor : descriptors) {
                String propertyName = descriptor.getName();
                if ("class".equals(propertyName)) {
                    continue;
                }
                if (Arrays.stream(args).anyMatch(a -> check(a, propertyName))) {
                    Method readMethod = descriptor.getReadMethod();
                    Object result = readMethod.invoke(bean);
                    if(Arrays.stream(args).anyMatch(a ->check2(a, propertyName))){
                        map.put(propertyName, result != null ? bean2Map(result, toArray(args)) : null);
                    }else{
                        map.put(propertyName, result);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    private static boolean check(String var, String propertyName) {
        return var.split("\\.", 2)[0].equals(propertyName) || var.equalsIgnoreCase("all");
    }

    private static boolean check2(String var, String propertyName) {
        return var.split("\\.", 2)[0].equals(propertyName) && var.split("\\.",2).length > 1;
    }

    private static String[] toArray(String... args) {
        return Arrays.stream(args).filter(a -> a.split("\\.", 2).length > 1).map(a -> a.split("\\.", 2)[1]).toArray(String[]::new);
    }
}
