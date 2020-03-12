package com.juran.quote.bean.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 3d-主数据-单位映射
 */
public enum QuoteUnitEnum {

	METER("m", "00", "米"),

	METER_SQUARE("m²", "01", "平米"),

	ONE("个", "03", "个"),

	METER_WIDTH("延米", "26", "延米"),

	TANG("樘", "18", "樘"),

	FAN("扇", "11", "扇"),

	ITEM("项", "40", "项"),

	STEP("踏步", "25", "踏步"),

	ROOT("根", "04", "根"),

	SET("套", "13", "套"),

	CI("次", "34", "次"),

	BLOCK("块", "07", "块"),

	GROUP("组", "17", "组"),

	VOLUME("卷", "21", "卷"),

	PIECE("片", "09", "片"),

    UNKNOWN("-", "-", "-");

	// 3d-bom中的unit
	private String design;
	// 主数据-QTYUNIT_ID
	private String mdm;
	// 单位描述
	private String name;

	QuoteUnitEnum(String design, String mdm, String name) {
		this.design = design;
		this.mdm = mdm;
		this.name = name;
	}

	/**
	 * 通过主数据单位编号，换取单位
	 * 
	 * @param unitId
	 *            单位编号
	 * @return
	 */
	public static QuoteUnitEnum getByUnitId(String unitId) {
		if (StringUtils.isEmpty(unitId)) {
            return UNKNOWN;
		}
		for (QuoteUnitEnum un : QuoteUnitEnum.values()) {
			if (un.getMdm().equals(unitId)) {
				return un;
			}
		}
        return UNKNOWN;
	}

	/**
	 * 通过主数据单位编号，换取单位中文描述
	 * 
	 * @param unitId
	 *            单位编号
	 * @return
	 */
	public static String getNameByUnitId(String unitId) {
		String name = "-";
		if (StringUtils.isEmpty(unitId)) {
			return name;
		}
		for (QuoteUnitEnum un : QuoteUnitEnum.values()) {
			if (un.getMdm().equals(unitId)) {
				name = un.getName();
				break;
			}
		}
		return name;
	}

	/**
	 * 通过主数据单位编号，换取单位中文描述
	 * 
	 * @param design
	 *            单位编号
	 * @return
	 */
	public static String getNameByDesign(String design) {
		String name = "-";
		if (StringUtils.isEmpty(design)) {
			return design;
		}
		for (QuoteUnitEnum un : QuoteUnitEnum.values()) {
			if (un.getDesign().equals(design)) {
				name = un.getName();
				break;
			}
		}
		return name;
	}

	/**
	 * 通过名称获取mdm
	 * @param name
	 * @return
	 */
	public static String getMdmByName(String name) {
		if (StringUtils.isEmpty(name)) {
			return UNKNOWN.getMdm();
		}
		for (QuoteUnitEnum un : QuoteUnitEnum.values()) {
			if (un.getName().equals(name)) {
				return un.getMdm();
			}
		}
		return UNKNOWN.getMdm();
	}
	public String getDesign() {
		return design;
	}

	public String getMdm() {
		return mdm;
	}

	public String getName() {
		return name;
	}

}
