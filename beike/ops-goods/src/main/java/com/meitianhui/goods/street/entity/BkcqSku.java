package com.meitianhui.goods.street.entity;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <pre> 街市商品sku实体 </pre>
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
public class BkcqSku implements Serializable {
    /**
     * sku_id
     */
    private Long skuId;

    /**
     * 商品ID
     */
    private Long goodsId;

    /**
     * 普通会员价
     */
    private BigDecimal salePrice;

    /**
     * vip会员价
     */
    private BigDecimal vipPrice;

    /**
     * 结算价
     */
    private BigDecimal settledPrice;

    /**
     * 抵扣贝壳
     */
    private Integer beikeCredit;

    /**
     * 商品码
     */
    private String goodsCode;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 属性组合值,json 字符串
     */
    private String propSpec;

    /**
     * 商品规格信息
     */
    private String specInfo;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 修改时间
     */
    private Date modifiedTime;

    /**
     * 备注
     */
    private String remark;

    private static final long serialVersionUID = 1L;

}