package com.juran.quote.bean.po;
import com.juran.quote.bean.quote.BomDto;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
@Document(collection = "quote")
@Data
public class QuotePo {
    /**
     * 报价ID
     */
    private Long quoteId;
    /**
     * 3D-设计方案ID
     *
     * @see BomDto.acsAssetID
     */
    private String designId;
    /**
     * 项目ID
     *
     * @see BomDto.acsProjectID
     */
    private Long projectId;
    /**
     * 套内面积
     */
    private BigDecimal innerArea;
    /**
     * 房型
     */
    private String houseType;
    /**
     * 房屋状态：N-新房，O-老房
     */
    private String houseStatus;
    /**
     * 门店ID
     */
    private String storeId;
    /**
     * 关联套餐ID
     *
     * @see
     */
    private Long packageId;
    /**
     * 套餐编码
     */
    private String packageCode;
    /**
     * 套餐版本ID
     *
     * @see
     */
    private Long packageVersionId;
    /**
     * 版本信息
     */
    private String packageVersionCode;
    /**
     * 创建时间
     */
    private Date createDate;
    /**
     * 修改时间
     */
    private Date updateDate;
    /**
     * 3D-设计内容
     */
    private BomDto bomList;
    /**
     * 套餐房间匹配信息
     */
    private List<QuoteRoomMapPo> quoteRoomMapList;
    /**
     * 报价所属装饰公司
     */
    private String decorationCompany;
    /**
     * 报价级别
     */
    private String quoteLevel;
}