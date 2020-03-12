package com.juran.application.quote.bean.enums;

/**
 * 房屋状态枚举:N-新房，O-老房, A-所有房型
 */
public enum HouseStateEnum {

	OLD("O"), NEW("N"), ALL("A");

	private String state;

	HouseStateEnum(String state) {
		this.state = state;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

}
