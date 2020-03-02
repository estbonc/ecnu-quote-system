package com.ecnu.paper.quotesystem.utils;

import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Map;

/**
 * 断言工具类
 */
public class AssertUtils {

    public static void isTrue(boolean expression, QuoteErrorEnum errorCode) throws QuoteException {
        if (!expression) {
            error(errorCode);
        }
    }

    public static void isFalse(boolean expression, QuoteErrorEnum errorCode) throws QuoteException {
        if (expression) {
            error(errorCode);
        }
    }

    public static void isNull(Object object, QuoteErrorEnum errorCode) throws QuoteException {
        if (object != null) {
            error(errorCode);
        }
    }

    public static void notNull(Object obj, QuoteErrorEnum errorCode) throws QuoteException {
        if (obj == null) {
            error(errorCode);
        }
    }

    public static void notEmpty(Collection<?> collection, QuoteErrorEnum errorCode) throws QuoteException {
        if (CollectionUtils.isEmpty(collection)) {
            error(errorCode);
        }
    }

    public static void notEmpty(Map<?, ?> map, QuoteErrorEnum errorCode) throws QuoteException {
        if (CollectionUtils.isEmpty(map)) {
            error(errorCode);
        }
    }

    public static void error(QuoteErrorEnum errorCode) throws QuoteException {
        throw new QuoteException(errorCode);
    }

}
