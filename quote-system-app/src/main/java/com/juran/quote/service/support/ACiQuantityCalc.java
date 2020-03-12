package com.juran.quote.service.support;

import com.juran.core.log.contants.LoggerName;
import com.juran.quote.bean.po.QuoteRoomMapPo;
import com.juran.quote.bean.quote.BomRoomDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 施工项数量自动计算：绑定计算参数，提供默认计算规则
 */
public abstract class ACiQuantityCalc implements ICiQuantityCalc {

    protected final transient Logger log = LoggerFactory.getLogger(LoggerName.INFO);

    // 计算参数
    private AutoCiQuantityContext context;

    /**
     * 计算当前施工项所属空间对应3d设计空间的面积和
     */
    protected BigDecimal getRoomArea() {
        BigDecimal total = BigDecimal.ZERO;
        List<String> d3RoomIds = getContext().getQrList().stream().map(QuoteRoomMapPo::getDesignRoomId).collect(Collectors.toList());
        List<BomRoomDto> d3RoomList = getContext().getBom().getRoomList().stream().filter(room -> !StringUtils.isEmpty(room.getRoomID()) && d3RoomIds.contains(room.getRoomID().toString())).collect(Collectors.toList());
        for (BomRoomDto room : d3RoomList) {
            if (!StringUtils.isEmpty(room.getArea())) {
                total = total.add(new BigDecimal(room.getArea()));
            }
        }
        return total;
    }

    /**
     * 计算当前施工项所属空间对应3d设计空间的周长和
     */
    protected BigDecimal getRoomPerimeter() {
        BigDecimal total = BigDecimal.ZERO;
        List<String> d3RoomIds = getContext().getQrList().stream().map(QuoteRoomMapPo::getDesignRoomId).collect(Collectors.toList());
        List<BomRoomDto> d3RoomList = getContext().getBom().getRoomList().stream().filter(room -> !StringUtils.isEmpty(room.getRoomID()) && d3RoomIds.contains(room.getRoomID().toString())).collect(Collectors.toList());
        for (BomRoomDto room : d3RoomList) {
            if (!StringUtils.isEmpty(room.getPerimeter())) {
                total = total.add(new BigDecimal(room.getPerimeter()));
            }
        }
        return total;
    }

    /**
     * 施工项数量-等同于套内建筑面积
     */
    protected BigDecimal getQuantityAsInnerArea() {
        return getContext().getHouse().getInnerArea();
    }

    public void setContext(AutoCiQuantityContext context) {
        this.context = context;
    }

    public AutoCiQuantityContext getContext() {
        return this.context;
    }
}
