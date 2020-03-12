package com.juran.quote.bean.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 主数据系统-空间类型定义:空间，名称，空间类型
 */
public enum QuoteRoomEnum {

	BED_ROOM_1("BED_ROOM_1", "主卧", "BED_ROOM", "01"),
	BED_ROOM_2("BED_ROOM_2", "次卧一", "BED_ROOM", "02"),
	BED_ROOM_3("BED_ROOM_3", "次卧二", "BED_ROOM", "03"),
	BED_ROOM_4("BED_ROOM_4", "次卧三", "BED_ROOM", "04"),
	LIVING_ROOM("LIVING_ROOM", "客厅及餐厅", "LIVING_ROOM", "11"),
	BATH_ROOM_1("BATH_ROOM_1", "主卫", "BATH_ROOM", "21"),
	BATH_ROOM_2("BATH_ROOM_2", "次卫", "BATH_ROOM", "22"),
	BALCONY("BALCONY", "阳台", "BALCONY", "51"),
	KITCHEN("KITCHEN", "厨房", "KITCHEN", "31"),
	OTHERS("OTHERS", "其他", "OTHERS", "99");

	private String id;
	private String name;
	private String type;
	private String erp;

	QuoteRoomEnum(String id, String name, String type, String erp) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.erp = erp;
	}

	public static boolean checkRoomType(String roomType) {
		for (QuoteRoomEnum en : QuoteRoomEnum.values()) {
			if (en.getType().equals(roomType)) {
				return true;
			}
		}
		return false;
	}

	public static QuoteRoomEnum getById(String id) {
		if (StringUtils.isEmpty(id)) {
			return null;
		}
		for (QuoteRoomEnum en : QuoteRoomEnum.values()) {
			if (en.getId().equals(id)) {
				return en;
			}
		}
		return null;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getErp() {
		return erp;
	}

}
