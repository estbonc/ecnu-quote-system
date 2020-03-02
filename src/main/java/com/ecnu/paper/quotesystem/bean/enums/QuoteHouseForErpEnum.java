package com.ecnu.paper.quotesystem.bean.enums;

/**
 * 主数据-房型空间映射
 */
public enum QuoteHouseForErpEnum {
	
	L0B0("L0B0","零居", "01","0+1"),

	L1B1("L1B1", "一居一卫", "11","1+1"),

	L2B1("L2B1", "二居一卫", "21","2+1"),

	L2B2("L2B2", "二居二卫", "22","2+2"),

	L3B1("L3B1", "三居一卫", "31","3+1"),

	L3B2("L3B2", "三居二卫", "32","3+2"),

	L4B2("L4B2", "四居二卫", "42","4+2");

	private String id;
	private String name;
	private String value;
	private String contactValue;
	public static String getIDByValue(String value){
		for (QuoteHouseForErpEnum e : QuoteHouseForErpEnum.values()) {
			if (e.getValue().equals(value)) {
				return e.getId();
			}
		}
		return "DEFAULT";
	}

	public static String getValueById(String id) {
		for (QuoteHouseForErpEnum e:QuoteHouseForErpEnum.values()) {
			if (e.getId().equals(id)) {
				return e.getValue();
			}
		}
		return "DEFAULT";
	}
	public static String getContactValueById(String id) {
		for (QuoteHouseForErpEnum e:QuoteHouseForErpEnum.values()) {
			if (e.getId().equals(id)) {
				return e.getContactValue();
			}
		}
		return "";
	}
	QuoteHouseForErpEnum(String id, String name, String value,String contactValue) {
		this.id = id;
		this.name = name;
		this.value = value;
		this.contactValue=contactValue;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getContactValue() {
		return contactValue;
	}

	public void setContactValue(String contactValue) {
		this.contactValue = contactValue;
	}
}
