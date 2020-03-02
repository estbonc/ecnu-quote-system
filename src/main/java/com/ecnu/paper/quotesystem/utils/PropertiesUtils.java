package com.ecnu.paper.quotesystem.utils;

import com.juran.core.log.contants.LoggerName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Component
public class PropertiesUtils {
    private final Logger logger = LoggerFactory.getLogger(LoggerName.INFO);
    private Properties properties;

    @PostConstruct
    private void loadProperties(){
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream ins = classloader.getResourceAsStream("config/AnyVariable.properties");
        properties = new Properties();
        try {
            properties.load(ins);
        } catch (IOException e) {
            logger.error("加载AnyVariable.properties文件异常{}",e.getMessage());
        }
    }

    public String getProperty(String key){
        return properties.getProperty(key);
    }

    public Properties buildProperties(String[] keys, Object[] values){
        Properties properties = new Properties();
        for (int i=0,j= 0 ; i < keys.length&&j<values.length; i++,j++) {
            if(keys[i]!=null&&values[j]!=null){
                properties.put(keys[i], values[j]);
            }
        }
        return properties;
    }
}
