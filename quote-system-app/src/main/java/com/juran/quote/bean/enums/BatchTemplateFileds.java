package com.juran.quote.bean.enums;

public enum BatchTemplateFileds {
    CONSTRUCTCATEGORY(0,"施工项分类"),
    CONSTRUCTCODE(1,"施工项编码"),
    CONSTRUCTNAME(2,"施工项名称"),
    UNIT(3,"计量单位"),
    DESCRIPTION(4,"工艺材料简介"),
    ASSITSPEC(5,"辅料名称规格"),
    STANDARD(6,"验收标准"),
    SOURCEOFSTANDARD(7,"标准出处"),
    REMARK(8,"备注"),
    ;

    private Integer index;
    private String filed;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getFiled() {
        return filed;
    }

    public void setFiled(String filed) {
        this.filed = filed;
    }

    BatchTemplateFileds(Integer index, String filed) {
        this.index = index;
        this.filed = filed;
    }
}
