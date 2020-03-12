package com.juran.quote.exception;

import com.juran.core.exception.ParentException;
import com.juran.quote.bean.enums.PackageError;

import java.util.UUID;

public class PackageException extends ParentException {

    private static final long serialVersionUID = -4260173643819764490L;

    public PackageException(PackageError eSpotsError) {
        setErrorCode(eSpotsError.getErrorCode());
        setErrorMsg(eSpotsError.getErrorMsg());
        setErrorId(UUID.randomUUID().toString());
    }
    public PackageException(String errorMsg) {
        setErrorMsg(errorMsg);
        setErrorId(UUID.randomUUID().toString());
    }
}
