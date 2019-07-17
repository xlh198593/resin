package com.meitianhui.order.street.utils;

import java.util.Calendar;
import java.util.Map;
import java.util.Objects;

/**
 * <pre> 调用其它系统Http请求类 </pre>
 *
 * @author tortoise
 * @since 2019/3/29 17:05
 */
public class StreetUtils {

    /**
     * 根据键获取Map字符串值
     *
     * @param key 键
     * @param map map
     * @return 值
     */
    public static String getValue(String key, Map<String, Object> map) {
        if (null == map || null == key) {
            return "";
        }

        Object value = map.get(key);
        if (null == value) {
            return "";
        }

        String nullStr = "null";
        if (Objects.equals(nullStr, value.toString())) {
            return "";
        }

        return value.toString();
    }

    /**
     * 获取当天开始时间
     *
     * @return Calendar 日历对象
     */
    public static Calendar getNowDayStart() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

}
