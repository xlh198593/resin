package com.meitianhui.goods.street.entity;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

/**
 * <pre> 街市商品库存变动日志实体 </pre>
 *
 * @author tortoise
 * @since 2019/3/28 15:05
 */
@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BkcqStockChangeLog implements Serializable {
    /**
     * id
     */
    private Long logId;

    /**
     * 商品sku
     */
    private Long skuId;

    /**
     * 商品ID
     */
    private Long goodsId;

    /**
     * 子订单号
     */
    private String oid;

    /**
     * 主订单号
     */
    private String tid;

    /**
     * 变动数量
     */
    private Integer amount;

    /**
     * 订单类别
     */
    private String orderType;

    /**
     * 生成时间
     */
    private Date createdTime;

    /**
     * 库存操作状态,可选值:UNKNOW(未知),FAILED(失败）SUCCESS（成功)
     */
    private String status;

    /**
     * 最后修改时间
     */
    private Date updateTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 旧状态
     */
    private String oldStatus;

    private static final long serialVersionUID = 1L;

}