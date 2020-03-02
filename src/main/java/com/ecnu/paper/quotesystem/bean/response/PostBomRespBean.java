package com.ecnu.paper.quotesystem.bean.response;


import lombok.Data;

import java.io.Serializable;

/**
 * 创建新报价响应
 */
@Data
public class PostBomRespBean implements Serializable {

	private static final long serialVersionUID = 7841864588948019286L;

	/**
	 * 报价唯一标识
	 */
	private String id;

	/**
	 * 报价页面URL
	 */
	private String quoteUrl;

}
