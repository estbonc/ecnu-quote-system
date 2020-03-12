package com.juran.quote.utils;

import com.juran.core.log.contants.LoggerName;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LogUtil {
    private static Logger log = LoggerFactory.getLogger(LoggerName.INFO);
    private LogUtil(){
        // never use outside
    }

    @Pointcut("@annotation(com.juran.quote.utils.LogAnnotation)")
    private void cut() { }

    @Around("cut()")
    public Object advice(ProceedingJoinPoint joinPoint) throws Throwable {
        StringBuilder sb = new StringBuilder();
        Object[] paramValues = joinPoint.getArgs();//获取参数值
        Class<? extends Object> invokeClass = joinPoint.getTarget().getClass();
        String signatureName = joinPoint.getSignature().getName();

        for (int i = 0; i < paramValues.length; i++) {
            if( paramValues[i] == null){
                sb.append( "null,");
            } else{
                sb.append(paramValues[i].toString() + ",");
            }
        }
        //入参日志
        log.info(invokeClass  + ":" + signatureName + "开始执行,输入参数:" + sb.toString());
        try {
            Object result = joinPoint.proceed();
            //出参日志
            String resultMessage = String.format("%s:%s 执行结束，输出内容:%s",
                    invokeClass, signatureName, result==null? "null":result.toString());
            log.info(resultMessage);
            return result;
        } catch (Exception e) {
            log.error(invokeClass  + ":" + signatureName
                    + "执行出错,异常内容：" + e);
            throw e;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw throwable;
        }
    }

}
