package com.ecnu.paper.quotesystem.utils;

import com.google.common.collect.Maps;
import com.juran.core.log.contants.LoggerName;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;

@Component
public class SpringUtil implements ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(LoggerName.INFO);

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (SpringUtil.applicationContext == null) {
            SpringUtil.applicationContext = applicationContext;
        }
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }


    /**
     * 通过反射执行目标方法
     *
     * @param className    全路径：package + className
     * @param invokeMethod 反射执行的方法
     * @param args         执行方法的参数
     * @return
     */
    public static Object beanInvoke(String className, String invokeMethod, Object... args) {
        Class<?>[] classes = null;
        try {
            Class c = Class.forName(className);
            Object objectBean = SpringUtil.getBean(c);
            Class beanClass = objectBean.getClass();
            Method[] methods = beanClass.getMethods();
            for (Method m : methods) {
                String methodString = m.getName();
                if (!methodString.equals(invokeMethod)) {
                    continue;
                }
                classes = m.getParameterTypes();
                break;
            }
            //类型转换
            int i = 0;
            for (Class cl : classes) {
                Object object = args[i];
                if (object != null) {
                    if (cl.equals(String.class)) {
                        args[i] = new String(object.toString());
                    } else if (cl.equals(Integer.class)) {
                        args[i] = Integer.valueOf(object.toString());
                    }
                }
                i++;
            }
            Method method = beanClass.getDeclaredMethod(invokeMethod, classes);
            return method.invoke(objectBean, args);
        } catch (Exception e) {
            logger.error("{}中的方法:{}执行错误,参数长度:{},实参长度:{},错误信息:{}", className, invokeMethod, classes == null ? -1 : classes.length, args.length, e);
            return null;
        }
    }

    /**
     * 获取方法的参数名称
     *
     * @param className
     * @param invokeMethod
     * @return
     */
    public static Map<String, Object> getParamsName(String className, String invokeMethod) {
        Map<String, Object> methodParamsName = Maps.newHashMap();
        try {
            Class clazz = Class.forName(className);
            //获取bean
            Object objectBean = SpringUtil.getBean(clazz);
            Class beanClass = objectBean.getClass();
            ClassPool pool = ClassPool.getDefault();
            CtClass cc = pool.get(beanClass.getName());
            CtMethod cm = cc.getDeclaredMethod(invokeMethod);

            MethodInfo methodInfo = cm.getMethodInfo();
            CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
            LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
            if (attr == null) {
                return methodParamsName;
            }
            String[] paramNames = new String[cm.getParameterTypes().length];
            int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
            for (int i = 0; i < paramNames.length; i++) {
                paramNames[i] = attr.variableName(i + pos);
            }
            // paramNames即参数名
            for (int i = 0; i < paramNames.length; i++) {
                methodParamsName.put(paramNames[i], null);
            }
        } catch (Exception e) {
            logger.error("{}中的方法:{}获取参数名称错误,错误信息:{}", className, invokeMethod, e);
        }
        return methodParamsName;
    }

    /**
     * 整合数据
     *
     * @param nameArray
     * @param sourceMap
     * @return
     */
    public static Object[] getParamsValuesByMap(String[] nameArray, Map<String, Object> sourceMap) {
        if (nameArray == null) {
            return null;
        }
        Object[] paramsValues = new Object[nameArray.length];
        int i = 0;
        for (String name : nameArray) {
            paramsValues[i] = sourceMap.get(name);
            i++;
        }
        return paramsValues;
    }

}
