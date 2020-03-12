package com.juran.quote.bean.enums;

/**
 * @Author: xiongtao
 * @Date: 03/12/2018 10:10 AM
 * @Description: 报价导出excel的枚举类
 * @Email: xiongtao@juran.com.cn
 */
public enum ExportExcelEnums {


    WHOLEHOME(1,"全屋定制报价","全屋报价.doc","全屋报价.ftl"),
    CONSTRUCT(2,"施工报价","施工报价.doc","施工报价.ftl"),
    MATERIAL(3,"材料报价","材料报价.doc","材料报价.ftl"),
    HARD_DECORATION(4,"硬装报价","硬装报价.doc","硬装报价.ftl"),
    SOFT_DECORATION(5,"软装报价","软装报价.doc","软装报价.ftl");


    private int code;

    private String name;

    private String fileName;

    private String template;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    ExportExcelEnums(int code, String name, String fileName, String template) {
        this.code = code;
        this.name = name;
        this.fileName = fileName;
        this.template = template;
    }

     ExportExcelEnums() {
    }

    public  static ExportExcelEnums getEnumByCode(int code){
        for (ExportExcelEnums e:ExportExcelEnums.values() ) {
            if(e.getCode()==code){
                return e;
            }
        }
        return null;
    }

}
