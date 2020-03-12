package com.juran.quote.utils;

import java.text.MessageFormat;
import java.util.Locale;

/**
 * 报价服务-涉及工具类
 */
public class QuoteUtils {

    /**
     * 模板内容格式化
     *
     * @param template 模板
     * @param params   参数
     * @return
     */
    public static String stringFormat(String template, Object[] params) {
        return new MessageFormat(template, Locale.getDefault()).format(params);
    }
}
