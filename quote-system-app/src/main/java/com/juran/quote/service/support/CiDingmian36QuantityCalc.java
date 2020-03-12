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
 * 顶面36	顶面石膏线条（含普通石膏线）	米		有顶角线的时候自动带出，数量等于顶角线长度之和
 */
public class CiDingmian36QuantityCalc extends ACiQuantityCalc {

    @Override
    public BigDecimal getQuantity() {
        BigDecimal qty = BigDecimal.ZERO;

        // 计算当前施工项所属空间对应的3d设计中石膏线使用量
        if (!CollectionUtils.isEmpty(getContext().getQrList())) {
            List<String> d3RoomIds = getContext().getQrList().stream().map(QuoteRoomMapPo::getDesignRoomId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(d3RoomIds) && !CollectionUtils.isEmpty(getContext().getBom().getBomList())) {
                // 过滤指定空间及主材
                List<BomItemDto> biList = getContext().getBom().getBomList().stream().filter(bi -> !StringUtils.isEmpty(bi.getRoomID()) && !StringUtils.isEmpty(bi.getType()) && d3RoomIds.contains(bi.getRoomID()) && bi.getType().equals(MainMaterialEnum.D3_SHI_GAO_XIAN.getCode())).collect(Collectors.toList());
                // 计算石膏线使用量总数
                for (BomItemDto bi : biList) {
                    if (bi.getQuantity() != null) {
                        qty = qty.add(bi.getQuantity());
                    }
                }
            }
        } else {
            log.info("施工项={}，空间={} 未与3d-bom做空间匹配，无法计算石膏线长度！", getContext().getCiId(), getContext().getRoom().getType());
        }
        return qty;
    }

}
