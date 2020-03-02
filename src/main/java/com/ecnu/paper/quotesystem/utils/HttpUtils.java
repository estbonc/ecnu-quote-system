package com.ecnu.paper.quotesystem.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.juran.quote.bean.exception.QuoteException;
import com.juran.quote.exception.PackageException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Cookie;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @Author: xiongtao
 * @Date: 14/09/2018 2:30 PM
 * @Description: cookie操作工具类
 * @Email: xiongtao@juran.com.cn
 */
public class HttpUtils {

    @Autowired
    PropertiesUtils propertiesUtils;

    /**
     * 从cookie中获取制定的key的内容
     * @param user
     * @return
     * @throws PackageException
     */
    public static String decodeUser(String user) throws QuoteException {
        String userName = null;
        try {
            userName = java.net.URLDecoder.decode(user, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new QuoteException("解析用户名错误");
        }
        return userName;
    }

    /**
     * 从cookie中获取制定的key中的指定的的内容
     * @param context
     * @param cookieKey cookie中的key
     * @param valueKey 具体对应cookies内容中字段的key
     * @return
     * @throws PackageException
     */
    public  static String getValueFromCookies(ContainerRequestContext context,String cookieKey,String valueKey) throws PackageException {
        String value = "";
        try {
            if (context.getCookies() != null){
                Cookie cookie = context.getCookies().get(cookieKey);
                if(cookie != null){
                    String cookieStr = cookie.getValue();
                    String cookieJsonString = java.net.URLDecoder.decode(cookieStr, "utf-8");
                    JSONObject cookieJsonObj = JSON.parseObject(cookieJsonString);
                    value = cookieJsonObj.getString(valueKey);
                }
            }
        } catch (UnsupportedEncodingException e) {
            throw new PackageException("解析cookie异常");
        }
        //todo 这个需要从cookie中拿，后面需要cms支持 暂时先写死
        //return value;
        return "设计创客";
    }


    /**
     * 从header中拿出浏览器信息
     * @param context
     * @return
     */
    public  static String getUserAgent(ContainerRequestContext context){
        String userAgent = context.getHeaderString("user-Agent").toLowerCase();
        return userAgent;
    }


    /**
     * 从header中拿出浏览器信息
     * @param context
     * @return
     */
    public  static String encodeFileNameByAgent(ContainerRequestContext context,String originFileName) throws  PackageException{
        String userAgent =getUserAgent(context);
        String newFileName;
        try {
            if (userAgent.contains("msie") || userAgent.contains("trident")||userAgent.contains("gecko")) {
                // IE浏览器
                newFileName = URLEncoder.encode(originFileName, "UTF-8");
            } else {
                // (非ie)firefox浏览器或者谷歌,safari等等
                newFileName = new String(originFileName.getBytes("UTF-8"), "ISO8859-1");
            }
        } catch (UnsupportedEncodingException e) {
            throw  new PackageException("文件名中文转码失败");
        }
        return newFileName;
    }
}
