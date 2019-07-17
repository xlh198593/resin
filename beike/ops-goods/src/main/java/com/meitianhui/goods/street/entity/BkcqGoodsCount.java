package com.meitianhui.goods.street.entity;

import lombok.*;

import java.io.Serializable;

/**
 * <pre> 街市商品统计实体 </pre>
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
public class BkcqGoodsCount implements Serializable {
    /**
     * 商品id
     */
    private Integer itemId;

    /**
     * 商品销量
     */
    private Integer soldQuantity;

    /**
     * 店铺地址
     */
    private String shopId;

    /**
     * 店铺类型,可选值 stores(联盟店),supplier（供应商）
     */
    private String shopType;

    /**
     * 店家区域id（字典在md_area里面）
     */
    private String areaId;

    /**
     * 评论次数
     */
    private Integer rateCount;

    /**
     * 好评次数
     */
    private Integer rateGoodCount;

    /**
     * 中评次数
     */
    private Integer rateNeutralCount;

    /**
     * 差评次数
     */
    private Integer rateBadCount;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 购买次数（包括）
     */
    private Integer buyCount;

    private static final long serialVersionUID = 1L;

}