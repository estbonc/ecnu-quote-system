package com.juran.quote.service.support;

import com.juran.quote.bean.enums.QuoteRoomEnum;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;

/**
 * @author liushuaishuai
 * @version 1.0
 * @date 2018/8/14 10:54
 * @description
 */
public class CiWaterproofAndOther01QuantityCalc extends ACiQuantityCalc {

    @Override
    public BigDecimal getQuantity() {
        String roomType = getContext().getRoom().getType();
        // 卫生间
        if (roomType.equals(QuoteRoomEnum.BATH_ROOM_1.getType())) {
            if (!CollectionUtils.isEmpty(getContext().getQrList()) && !CollectionUtils.isEmpty(getContext().getBom().getRoomList())) {
                return getRoomPerimeter().multiply(new BigDecimal(1.8)).add(getRoomArea());
            } else {
                log.info("施工项={}，空间={} 未与3d-bom做空间匹配，无法计算面积周长！", getContext().getCiId(), roomType);
                return BigDecimal.ZERO;
            }
        }
        // 厨房
        else if (roomType.equals(QuoteRoomEnum.KITCHEN.getType())) {
            if (!CollectionUtils.isEmpty(getContext().getQrList()) && !CollectionUtils.isEmpty(getContext().getBom().getRoomList())) {
                return getRoomPerimeter().multiply(new BigDecimal(0.4)).add(getRoomArea());
            } else {
                log.info("施工项={}，空间={} 未与3d-bom做空间匹配，无法计算面积周长！", getContext().getCiId(), roomType);
                return BigDecimal.ZERO;
            }
        } else {
            log.info("当前施工项={}和空间={}匹配规则不对！", getContext().getCiId(), roomType);
            return BigDecimal.ZERO;
        }
    }

}

