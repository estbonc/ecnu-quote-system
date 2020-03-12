package com.juran.quote.service.support;

import com.juran.quote.bean.enums.ConstructionItemEnum;
import com.juran.quote.bean.enums.QuoteRoomEnum;
import com.juran.quote.bean.po.QuoteDocumentPo;
import com.juran.quote.bean.po.QuoteRoomMapPo;
import com.juran.quote.bean.quote.BomDto;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * 施工项数量自动带出计算规则
 */
@Data
public class AutoCiQuantityContext {

    // 施工项编号
    private String ciId;

    // 房屋信息
    private QuoteDocumentPo house;

    // 空间信息
    private QuoteRoomEnum room;

    // 3d-bom清单详情
    private BomDto bom;
    // 空间匹配信息，空间带出施工项，需要据此反查3d中空间及bom信息
    private List<QuoteRoomMapPo> qrList;

    /**
     * 自动计算施工项数量
     *
     * @return 数量
     */
    public BigDecimal getCiQuantity() {
        ICiQuantityCalc ciCalc = new CiDefaultQuantityCalc();
        if (StringUtils.isEmpty(getCiId())) {
            ciCalc = new CiDefaultQuantityCalc();
        } else if (getCiId().equals(ConstructionItemEnum.TUSHI_19.getName())) {
            ciCalc = new CiTushi19QuantityCalc();
        } else if (getCiId().equals(ConstructionItemEnum.DINGMIAN_36.getName())) {
            ciCalc = new CiDingmian36QuantityCalc();
        } else if (getCiId().equals(ConstructionItemEnum.WATERPROOF_AND_OTHER_01.getName())) {
            ciCalc = new CiWaterproofAndOther01QuantityCalc();
        } else if (getCiId().equals(ConstructionItemEnum.QITA_02.getName())) {
            ciCalc = new CiQita02QuantityCalc();
        } else if (getCiId().equals(ConstructionItemEnum.QITA_03.getName())) {
            ciCalc = new CiQita03QuantityCalc();
        } else if (getCiId().equals(ConstructionItemEnum.QITA_05.getName())) {
            ciCalc = new CiQita05QuantityCalc();
        }else if(getCiId().equals(ConstructionItemEnum.INSTALL_09.getName())){
            ciCalc = new CiInstall09QuantityCalc();
        }

        // 绑定参数
        ciCalc.setContext(this);

        // 计算数量
        return ciCalc.getQuantity();
    }
}
