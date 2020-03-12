package com.juran.quote.bean.enums;

public enum QuoteErrorEnum {

    INSERT_QUOTE_ERROR(110600001, "新建报价失败"),
    QUOTE_NOT_FOUND(110600002, "未找到相应报价"),
    PACKAGE_VERSION_NOT_FOUND(110600003,"未找到相应套餐版本"),
    QUOTE_HOUSE_TYPE_EMPTY(110600004, "报价-房型未设置"),
    QUOTE_HOUSE_ROOM_MAP_ERROR(110600005, "房型空间映射失败"),
    QUOTE_KEY_REQUIRED(110600006, "报价ID为空"),
    FIND_PACKAGE_PRICE_ERROR(110600007, "套餐价格信息不存在"),
    SEND_SMS_CODE_MOBILE_ERROR(110600008, "手机号格式不对"),
    EXECUTE_REQUIRED(110600009, "execute参数错误"),
    BUDGET_SMS_TOOFAST(110600010, "60s内只能获取一次短信验证码，请稍后再试"),
    BUDGET_AREA_ERROR(110600011, "建筑面积有误"),
    BUDGET_MOBILE_REQUIRED(110600012, "接收验证码短信的手机号为空"),
    BUDGET_SMS_CODE_REQUIRED(110600013, "验证码为空"),
    BUDGET_SMS_EXPIRED(110600014, "短信验证码已失效，请重新获取"),
    BUDGET_SMS_CODE_NOT_MATCH(110600015, "验证码不匹配"),
    BUDGET_PACKAGE_VERSION_NOT_FOUND(110600016, "套餐版本未找到"),
    BUDGET_PACKAGE_PRICE_NOT_FOUND(110600016, "套餐报价未找到"),
    PACKAGE_NOT_FOUND(1106000017,"未找到套餐"),
    PARAM_HOUSE_TYPE_ERROR(1106000017,"房型参数错误"),
    CUSTOMER_INFORMATION_NOT_FOUND(1106000018,"客户信息未找到"),
    QUOTE_ZERO(110600019, "材料数量不能为空"),
    DESIGN_NOT_FOUNT(110600020,"design is not exist")

    ;

    private final int errorCode;
    private final String errorMsg;

    QuoteErrorEnum(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public static QuoteErrorEnum valueOf(int errorCode) {
        for (QuoteErrorEnum eSpotsError : QuoteErrorEnum.values()) {
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
