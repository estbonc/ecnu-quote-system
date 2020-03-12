package com.juran.quote.utils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import java.util.Map;

/**
 * Created by admin on 2017/4/24.
 */
public class FreemarkerUtil {

    /**
     * 获取configuration
     *
     * @return
     */
    public static Configuration getConfiguration(String filePath) {
        Configuration configuration = new Configuration();
        configuration.setDefaultEncoding("utf-8");
        configuration.setLocale(Locale.CHINA);
        configuration.setClassicCompatible(true);
        try {
            configuration.setClassLoaderForTemplateLoading(Thread.currentThread().getContextClassLoader(), filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return configuration;
    }


    /**
     * 将模版文件装换成excel文件
     * @param root
     * @return
     */
    public static void exportToExcel(String templatePath,Map<String , Object> root,String mFileName, Writer wout){
        Configuration configuration = FreemarkerUtil.getConfiguration(templatePath);
        try {
            Template template = configuration.getTemplate(mFileName);

            template.process(root , wout);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (wout != null) {
                try {
                    wout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
