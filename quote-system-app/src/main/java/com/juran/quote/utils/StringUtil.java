package com.juran.quote.utils;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.juran.core.log.contants.LoggerName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class StringUtil {

    private static final Logger logger = LoggerFactory.getLogger(LoggerName.INFO);

    private StringUtil() {
    }

    /**
     * 获取参数名称
     *
     * @param dbSource
     * @return
     */
    public static String getMethodParams(String dbSource) {
        if (dbSource.contains("(")) {
            String[] s1 = dbSource.split("\\(");
            if (s1.length == 2 && s1[1].contains(")")) {
                String[] s2 = s1[1].split("\\)");
                if (s2.length > 0) {
                    return s2[0];
                }
            }
        }
        logger.info("数据源无参方法,数据源:{}", dbSource);
        return null;
    }

    /**
     * 获取方法
     *
     * @param dbSource
     * @return
     */
    public static String getMethod(String dbSource) {
        if (dbSource.contains("(")) {
            String[] s1 = dbSource.split("\\(");
            return s1[0];
        }
        logger.info("三方方法解析错误:{}", dbSource);
        return null;
    }

    /**
     * 返回参数
     *
     * @param dbSource
     * @return
     */
    public static String[] getParamsName(String dbSource) {
        if (StringUtils.isEmpty(dbSource)) {
            return null;
        }
        String paramsString = getMethodParams(dbSource);
        if (!StringUtils.isEmpty(paramsString)) {
            return paramsString.split(",");
        }
        return null;
    }

    /**
     * 参数字符串转换为map
     *
     * @param targetString
     * @return
     */
    public static Map<String, Object> analyseString2Map(String targetString) {
        Map<String, Object> resultMap = Maps.newHashMap();
        String[] s1 = targetString.split("&");
        for (String s : s1) {
            String[] result = s.split("=");
            if (result.length == 2) {
                resultMap.put(result[0], result[1]);
            }
        }
        return resultMap;
    }

    /**
     * 替换参数，获取最终请求的url
     *
     * @param sourceUrl
     * @param params
     * @return
     */
    public static String getFinalUrl(String sourceUrl, Map<String, Object> params) {
        for (Map.Entry<String, Object> objectEntry : params.entrySet()) {
            Object value = params.get(objectEntry.getKey());
            sourceUrl = sourceUrl.replace("{" + objectEntry.getKey() + "}", value.toString());
        }
        return sourceUrl;
    }

    /**
     * 字符串转换为List
     *
     * @param ids
     * @return
     */
    public static List<String> transformString2StringList(String ids) {
        if (StringUtils.isEmpty(ids) || "null".equals(ids)) {
            return Collections.emptyList();
        }
        return Lists.newArrayList(Splitter.on(",").omitEmptyStrings().trimResults().split(ids));
    }

    public static String parseHouseTypeCode(String code) {
        StringBuffer sb = new StringBuffer();
        try {
            if(StringUtils.isEmpty(code)){
                return "空户型";
            }
            for (char c:code.toCharArray()){
                if(Character.isDigit(c)){
                    sb.append(c);
                }
                if (c == 'B'){
                    sb.append("室");
                }
            }
            sb.append("卫");
            return sb.toString();
        } catch (Exception e) {
            logger.error("解析户型出错:", e.getMessage());
        }
        return code;
    }
}
