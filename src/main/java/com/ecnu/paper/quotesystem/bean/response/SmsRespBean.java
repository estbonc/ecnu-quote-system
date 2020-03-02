package com.ecnu.paper.quotesystem.bean.response;

import lombok.Data;

import java.io.Serializable;

/**
 * Redis-短信发送记录
 */
@Data
public class SmsRespBean implements Serializable {

	private static final long serialVersionUID = -6462414439374078985L;
	/**
	 * 已发送次数
	 */
	private int sendCount;
	/**
	 * 验证码
	 */
	private String smsCode;
	/**
	 * 最后一次发送时间
	 */
	private long lastSend;
	/**
	 * 该条短信的验证码是否校验通过
	 */
	private boolean valid;
}
