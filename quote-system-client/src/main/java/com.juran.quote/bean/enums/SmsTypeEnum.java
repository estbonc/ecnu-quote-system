package com.juran.quote.bean.enums;

/**
 * 发短信场景定义
 */
public enum SmsTypeEnum {

	// 装修预算
	SMS_DECORATION_BUDGET("sms_decoration_budget");

	private String code;

	SmsTypeEnum(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
