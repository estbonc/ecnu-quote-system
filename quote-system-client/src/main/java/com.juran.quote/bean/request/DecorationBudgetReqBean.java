package com.juran.quote.bean.request;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 装修预算
 */
@Data
public class DecorationBudgetReqBean implements Serializable {


	private static final long serialVersionUID = 4341301005604215495L;
	/**
	 * 接受的短信验证码
	 */
	private String smsCode;

	/**
	 * 接受短信的手机号
	 */
	private String mobile;

	/**
	 * 建筑面积
	 */
	private BigDecimal area;

	/**
	 * 房型
	 */
	private String houseType;

	/**
	 * 房屋状态 true 新房 false 老房
	 */
	private Boolean houseState;


}
