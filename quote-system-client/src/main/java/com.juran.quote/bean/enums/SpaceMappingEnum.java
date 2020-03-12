package com.juran.quote.bean.enums;

/**
 * 3D空间映射关系
 */
public enum SpaceMappingEnum {

	BATHROOM("卫生间", QuoteRoomEnum.BATH_ROOM_1),
	BEDROOM("卧室", QuoteRoomEnum.BED_ROOM_1),
	DININGROOM("餐厅", QuoteRoomEnum.LIVING_ROOM),
	KIDSROOM("儿童房", QuoteRoomEnum.BED_ROOM_2),
	KITCHEN("厨房", QuoteRoomEnum.KITCHEN),
	LIVINGROOM("客厅", QuoteRoomEnum.LIVING_ROOM),
	OFFICE("办公室", QuoteRoomEnum.OTHERS),

	OTHERROOM("其他房间", QuoteRoomEnum.OTHERS),
	OUTDOORS("户外", QuoteRoomEnum.OTHERS),
	PUBLICEXTERIOR("商用/公用室外区域", QuoteRoomEnum.OTHERS),
	PUBLICINTERIOR("商用/公用室内区域", QuoteRoomEnum.OTHERS),
	RESIDENTIALEXTERIOR("住宅室外区域", QuoteRoomEnum.OTHERS),
	ENTRANCEHALLWAY("入口和过厅", QuoteRoomEnum.OTHERS),
	PRODUCTSHOWCASE("产品展示柜", QuoteRoomEnum.OTHERS),
	FLOORPLAN("平面图", QuoteRoomEnum.OTHERS),
	STUDIO("工作室", QuoteRoomEnum.OTHERS),
	BASEMENT("地下室", QuoteRoomEnum.OTHERS),

	HOMECINEMA("家庭影院", QuoteRoomEnum.OTHERS),
	LIBRARY("书房", QuoteRoomEnum.OTHERS),
	DEN("小房间", QuoteRoomEnum.OTHERS),
	SKETCH("草图", QuoteRoomEnum.OTHERS),
	LIVINGDININGROOM("客厅及餐厅", QuoteRoomEnum.LIVING_ROOM),
	HALLWAY("门厅", QuoteRoomEnum.OTHERS),
	BALCONY("阳台", QuoteRoomEnum.BALCONY),
	
	MASTERBEDROOM("主卧", QuoteRoomEnum.BED_ROOM_1),
	SECONDBEDROOM("次卧", QuoteRoomEnum.BED_ROOM_2),
	ELDERLYROOM("老人房", QuoteRoomEnum.BED_ROOM_2),
	LOUNGE("休闲厅", QuoteRoomEnum.OTHERS),
	AUDITORIUM("影视厅", QuoteRoomEnum.OTHERS),
	NANNYROOM("保姆间", QuoteRoomEnum.OTHERS),

	LAUNDRYROOM("洗衣间", QuoteRoomEnum.OTHERS),
	STORAGEROOM("储藏间", QuoteRoomEnum.OTHERS),
	CLOAKROOM("衣帽间", QuoteRoomEnum.OTHERS),
	MASTERBATHROOM("主卫", QuoteRoomEnum.BATH_ROOM_1),
	SECONDBATHROOM("次卫", QuoteRoomEnum.BATH_ROOM_1),
	STAIRWELL("楼梯间", QuoteRoomEnum.OTHERS),

	AISLE("过道", QuoteRoomEnum.OTHERS),
	CORRIDOR("走廊", QuoteRoomEnum.OTHERS),
	PORCHBALCONY("玄关和阳台", QuoteRoomEnum.BALCONY),
	
	UNKNOW("无房间类型",QuoteRoomEnum.OTHERS);

	// 3d空间名称
	private String designName;
	// 对应主数据-空间
	private QuoteRoomEnum quoteSpace;

	SpaceMappingEnum(String designName, QuoteRoomEnum quoteSpace) {
		this.designName = designName;
		this.quoteSpace = quoteSpace;
	}

	public String getDesignName() {
		return designName;
	}

	public void setDesignName(String designName) {
		this.designName = designName;
	}

	public QuoteRoomEnum getQuoteSpace() {
		return quoteSpace;
	}

	public void setQuoteSpace(QuoteRoomEnum quoteSpace) {
		this.quoteSpace = quoteSpace;
	}

}
