package com.juran.quote.bean.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 发短信验证码请求
 */
@Data
public class SmsCodeReqBean implements Serializable {

	private static final long serialVersionUID = -728980629779258600L;
	private String execute;
	private String mobile;

}
