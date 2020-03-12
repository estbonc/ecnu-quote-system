package com.juran.quote.service.support;

import com.juran.quote.bean.enums.MainMaterialEnum;
import com.juran.quote.bean.po.QuoteRoomMapPo;
import com.juran.quote.bean.quote.BomItemDto;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 涂饰19	西班牙伊斯威尔M-18奥林匹亚内墙漆（白色/浅色、进口）	平米		刷墙漆时的自动带出项，数量等于刷墙漆的面积
 */
public class CiTushi19QuantityCalc extends ACiQuantityCalc {

    @Override
    public BigDecimal getQuantity() {
        BigDecimal qty = BigDecimal.ZERO;

        // 计算当前施工项所属空间对应的3d设计中墙漆使用量
        if (!CollectionUtils.isEmpty(getContext().getQrList())) {
            List<String> d3RoomIds = getContext().getQrList().stream().map(QuoteRoomMapPo::getDesignRoomId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(d3RoomIds) && !CollectionUtils.isEmpty(getContext().getBom().getBomList())) {
                // 过滤指定空间及主材
                List<BomItemDto> biList = getContext().getBom().getBomList().stream().filter(bi -> !StringUtils.isEmpty(bi.getRoomID()) && !StringUtils.isEmpty(bi.getType()) && d3RoomIds.contains(bi.getRoomID()) && bi.getType().equals(MainMaterialEnum.D3_QIANG_QI.getCode())).collect(Collectors.toList());
                // 计算墙漆使用量总数
                for (BomItemDto bi : biList) {
                    if (bi.getAddtional() != null && bi.getAddtional().getArea() != null) {
                        qty = qty.add(bi.getAddtional().getArea());
                    }
                }
            }
        } else {
            log.info("施工项={}，空间={} 未与3d-bom做空间匹配，无法计算墙漆面积！", getContext().getCiId(), getContext().getRoom().getType());
        }
        return qty;
    }
}
