package com.juran.quote.bean.exception;

import com.juran.quote.bean.enums.QuoteErrorEnum;
import com.juran.core.exception.ParentException;

import java.util.UUID;

public class QuoteException extends ParentException {

    private static final long serialVersionUID = -4260173643819764490L;

    public QuoteException(QuoteErrorEnum eSpotsError) {
        setErrorCode(eSpotsError.getErrorCode());
        setErrorMsg(eSpotsError.getErrorMsg());
        setErrorId(UUID.randomUUID().toString());
    }

    public QuoteException(String errorMsg) {
        setErrorMsg(errorMsg);
        setErrorId(UUID.randomUUID().toString());
    }
}
