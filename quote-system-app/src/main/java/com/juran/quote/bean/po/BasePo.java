package com.juran.quote.bean.po;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * @Author: xiongtao
 * @Date: 29/09/2018 2:38 PM
 * @Description: 公用的信息Po
 * @Email: xiongtao@juran.com.cn
 */
@Data
public class BasePo {

    @Id
    private String id;
    /**
     * 创建日期
     */
    private Date createTime;
    /**
     * 更新日期
     */
    private Date updateTime;
    /**
     * 创建人/更新人
     */
    private String updateBy;



}
