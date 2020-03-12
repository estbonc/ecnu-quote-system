package com.juran.quote.bean.enums;

/**
 * Created by dell on 2017/6/9.
 */
public enum PackageError {

    INSERT_PACKAGE_VERSION_ERROR(110600001, "套餐版本不存在"),
    INSERT_PACKAGE_ID_ERROR(110600002, "套餐ID错误"),
    INSERT_PACKAGE_ERROR(110600003, "套餐不存在"),
    INSERT_PROMOTION_JID_ERROR(110600004, "促销优惠券JID错误"),
    INSERT_PACKAGE_PARAMS_ERROR(110600005, "参数错误"),
    INSERT_PACKAGE_KEY_MANAGER_ERROR(110600006, "key-manager生成ID错误"),
    UPDATE_PACKAGE_PACKAGE_BEAN_ERROR(110600007, "套餐不存在"),
    INSERT_PACKAGE_IN_SYSTEM_ERROR(110600008, "套餐已经存在"),
    INSERT_PACKAGE_MEMBER_JID_ERROR(110600009, "获取JID错误"),
    FIND_PACKAGE_PRICE_ERROR(1106000010, "套餐价格信息不存在"),
    INSERT_PACKAGE_VERSION_IN_SYSTEM_ERROR(110600011, "套餐版本已经存在"),
    ;

    private final int errorCode;
    private final String errorMsg;

    PackageError(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public static PackageError valueOf(int errorCode) {
        for (PackageError eSpotsError : PackageError.values()) {
            if (eSpotsError.errorCode == errorCode) {
                return eSpotsError;
            }
        }
        throw new IllegalArgumentException("No matching constant for [" + errorCode + "]");
    }

    @Override
    public String toString() {
        return Integer.toString(this.errorCode);
    }

}
