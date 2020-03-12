package com.juran.quote.service.support;

import com.juran.quote.bean.enums.QuoteHouseEnum;
import com.juran.quote.bean.enums.QuoteRoomEnum;
import com.juran.quote.bean.po.QuoteRoomMapPo;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author liushuaishuai
 * @version 1.0
 * @date 2018/8/13 13:58
 * @description
 */
public class CiInstall09QuantityCalc extends ACiQuantityCalc {

    @Override
    public BigDecimal getQuantity() {
        BigDecimal quantity = BigDecimal.ZERO;

        //识别方案中厨房、卫生间数量   quantity = 厨房数量C1 + 卫生间数量C2
        //通过房型识别
        String houseType = getContext().getHouse().getHouseType();
        if (!StringUtils.isEmpty(houseType)) {
            QuoteHouseEnum quoteHouseEnum = QuoteHouseEnum.valueOf(houseType);
            List<QuoteRoomEnum> quoteRooms = quoteHouseEnum.getQuoteRooms();
            for (QuoteRoomEnum room : quoteRooms) {
                String type = room.getType();
                if (type.equals(QuoteRoomEnum.BATH_ROOM_1.getType()) || type.equals(QuoteRoomEnum.KITCHEN.getType())) {
                    quantity = quantity.add(BigDecimal.ONE);
                }
            }
        } else {
            log.info("空间匹配信息为null,施工项数量计算失败。施工项id：{}", getContext().getCiId());
        }
        return quantity;
    }
}
