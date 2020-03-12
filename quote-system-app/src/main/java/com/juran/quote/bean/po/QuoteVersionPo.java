package com.juran.quote.bean.po;

import lombok.Data;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * @Author: xiongtao
 * @Date: 09/10/2018 1:29 PM
 * @Description: 报价版本po
 * @Email: xiongtao@juran.com.cn
 */
@Data
@Document(collection = "quoteVersion")
@CompoundIndexes({
        @CompoundIndex(name = "version_company_status",def = "{'quoteVersionCode':1,'decorationCompany':1,'status':1}",unique = true)
})
public class QuoteVersionPo extends BasePo {

    /**
     * 报价版本id
     */
    private Long quoteVersionId;

    /**
     * 装饰公司
     */
    private String decorationCompany;

    /**
     * 装修类型id
     */
    private Long decorationTypeId;

    /**
     * 报价类型id
     */
    private Long quoteTypeId;

    /**
     * 报价版本编码
     */
    private String quoteVersionCode;

    /**
     * 生效日期
     */
    private Date startTime;

    /**
     * 失效日期
     */
    private Date endTime;

    /**
     * 区域代码
     */
    private List<String> region;

    /**
     * 门店列表
     */
    private List<String> stores;

    /**
     * 报价版本状态
     */
    private Integer status;

}
