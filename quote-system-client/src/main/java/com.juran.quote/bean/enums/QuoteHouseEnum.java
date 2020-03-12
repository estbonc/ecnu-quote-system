package com.juran.quote.bean.enums;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 主数据-房型空间映射
 */
public enum QuoteHouseEnum {

	ALL("所有房型"){
		@Override
		public List<QuoteRoomEnum> getQuoteRooms() {
			return Collections.EMPTY_LIST;
		}
	},
	L0B0("零居") {
		@Override
		public List<QuoteRoomEnum> getQuoteRooms() {
			List<QuoteRoomEnum> rooms = new ArrayList<>();
			rooms.add(QuoteRoomEnum.LIVING_ROOM);
			rooms.add(QuoteRoomEnum.KITCHEN);
			rooms.add(QuoteRoomEnum.BATH_ROOM_1);
			rooms.add(QuoteRoomEnum.BALCONY);
			rooms.add(QuoteRoomEnum.OTHERS); 
			return rooms;
		}
	},

	L1B1("一居一卫") {
		@Override
		public List<QuoteRoomEnum> getQuoteRooms() {
			List<QuoteRoomEnum> rooms = new ArrayList<>();
			rooms.add(QuoteRoomEnum.LIVING_ROOM);
			rooms.add(QuoteRoomEnum.BED_ROOM_1);
			rooms.add(QuoteRoomEnum.KITCHEN);
			rooms.add(QuoteRoomEnum.BATH_ROOM_1);
			rooms.add(QuoteRoomEnum.BALCONY);
			rooms.add(QuoteRoomEnum.OTHERS); 
			return rooms;
		}
	},

	L2B1("二居一卫") {
		@Override
		public List<QuoteRoomEnum> getQuoteRooms() {
			List<QuoteRoomEnum> rooms = new ArrayList<>();
			rooms.add(QuoteRoomEnum.LIVING_ROOM);
			rooms.add(QuoteRoomEnum.BED_ROOM_1);
			rooms.add(QuoteRoomEnum.BED_ROOM_2);
			rooms.add(QuoteRoomEnum.KITCHEN);
			rooms.add(QuoteRoomEnum.BATH_ROOM_1);
			rooms.add(QuoteRoomEnum.BALCONY);
			rooms.add(QuoteRoomEnum.OTHERS); 
			return rooms;
		}
	},

	L2B2("二居二卫") {
		@Override
		public List<QuoteRoomEnum> getQuoteRooms() {
			List<QuoteRoomEnum> rooms = new ArrayList<>();
			rooms.add(QuoteRoomEnum.LIVING_ROOM);
			rooms.add(QuoteRoomEnum.BED_ROOM_1);
			rooms.add(QuoteRoomEnum.BED_ROOM_2);
			rooms.add(QuoteRoomEnum.KITCHEN);
			rooms.add(QuoteRoomEnum.BATH_ROOM_1);
			rooms.add(QuoteRoomEnum.BATH_ROOM_2);
			rooms.add(QuoteRoomEnum.BALCONY);
			rooms.add(QuoteRoomEnum.OTHERS); 
			return rooms;
		}
	},

	L3B1("三居一卫") {
		@Override
		public List<QuoteRoomEnum> getQuoteRooms() {
			List<QuoteRoomEnum> rooms = new ArrayList<>();
			rooms.add(QuoteRoomEnum.LIVING_ROOM);
			rooms.add(QuoteRoomEnum.BED_ROOM_1);
			rooms.add(QuoteRoomEnum.BED_ROOM_2);
			rooms.add(QuoteRoomEnum.BED_ROOM_3);
			rooms.add(QuoteRoomEnum.KITCHEN);
			rooms.add(QuoteRoomEnum.BALCONY);
			rooms.add(QuoteRoomEnum.BATH_ROOM_1);
			rooms.add(QuoteRoomEnum.OTHERS); 
			return rooms;
		}
	},

	L3B2("三居二卫") {
		@Override
		public List<QuoteRoomEnum> getQuoteRooms() {
			List<QuoteRoomEnum> rooms = new ArrayList<>();
			rooms.add(QuoteRoomEnum.LIVING_ROOM);
			rooms.add(QuoteRoomEnum.BED_ROOM_1);
			rooms.add(QuoteRoomEnum.BED_ROOM_2);
			rooms.add(QuoteRoomEnum.BED_ROOM_3);
			rooms.add(QuoteRoomEnum.KITCHEN);
			rooms.add(QuoteRoomEnum.BALCONY);
			rooms.add(QuoteRoomEnum.BATH_ROOM_1);
			rooms.add(QuoteRoomEnum.BATH_ROOM_2);
			rooms.add(QuoteRoomEnum.OTHERS); 
			return rooms;
		}
	},

	L4B2("四居二卫") {
		@Override
		public List<QuoteRoomEnum> getQuoteRooms() {
			List<QuoteRoomEnum> rooms = new ArrayList<>();
			rooms.add(QuoteRoomEnum.LIVING_ROOM);
			rooms.add(QuoteRoomEnum.BED_ROOM_1);
			rooms.add(QuoteRoomEnum.BED_ROOM_2);
			rooms.add(QuoteRoomEnum.BED_ROOM_3);
			rooms.add(QuoteRoomEnum.BED_ROOM_4);
			rooms.add(QuoteRoomEnum.KITCHEN);
			rooms.add(QuoteRoomEnum.BATH_ROOM_1);
			rooms.add(QuoteRoomEnum.BATH_ROOM_2);
			rooms.add(QuoteRoomEnum.BALCONY);
			rooms.add(QuoteRoomEnum.OTHERS); 
			return rooms;
		}
	},
	
	;

	private String name;

	/**
	 * 房型#空间
	 */
	public abstract List<QuoteRoomEnum> getQuoteRooms();

	QuoteHouseEnum(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
