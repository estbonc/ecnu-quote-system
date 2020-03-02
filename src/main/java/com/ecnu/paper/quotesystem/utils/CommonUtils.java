package com.ecnu.paper.quotesystem.utils;

import com.juran.core.utils.dateTime.DateTimeUtil;

import java.util.Date;
import java.util.Random;

/**
 * 字符串工具类
 */
public class CommonUtils {

    private CommonUtils() {
    }

    /**
     * 获取名称后缀
     *
     * @param name
     * @return
     */
    public static String getExt(String name) {
        if (name == null || "".equals(name) || !name.contains(".")) {
            return "";
        }
        return name.substring(name.lastIndexOf('.') + 1);
    }

    /**
     * 获取指定位数的随机数
     *
     * @param num
     * @return
     */
    public static String getRandom(int num) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < num; i++) {
            sb.append(String.valueOf(random.nextInt(10)));
        }
        return sb.toString();
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getCurrentDate() {
        Date cDate = DateTimeUtil.getNowDateTime4BeiJing();
        return DateTimeUtil.getFormatDateTime(cDate, DateTimeUtil.DateFormat.FORMAT_DATE_SMALL);
    }

}
