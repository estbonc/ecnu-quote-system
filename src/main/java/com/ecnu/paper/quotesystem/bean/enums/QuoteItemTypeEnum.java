package com.ecnu.paper.quotesystem.bean.enums;

/**
 * 报价-清单类型枚举，表：quote_item，字段：ITEM_TYPE
 */
public enum QuoteItemTypeEnum {

	INNER_MM("MM-I", "套餐内主材"),
	OUTER_MM("MM-O", "套餐外主材"),
	INNER_CI("CI-I", "空间主材带出施工项"),
	GIFT_CI("CI-G", "可选装饰包施工项"),
	MANUAL_CI("CI-M", "套餐外自定义施工项");

	private String type;
	private String info;

	QuoteItemTypeEnum(String type, String info) {
		this.type = type;
		this.info = info;
	}

	public String getType() {
		return type;
	}

	public String getInfo() {
		return info;
	}

}
