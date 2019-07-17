package com.meitianhui.goods.street.entity;

import lombok.*;

import java.io.Serializable;

/**
 * <pre> 街市商品sku库存实体 </pre>
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
public class BkcqSkuStock implements Serializable {

    /**
     * sku ID
     */
    private Long skuId;

    /**
     * 商品ID
     */
    private Long goodsId;

    /**
     * 商品可用数量
     */
    private Integer store;

    /**
     * 商品冻结数量
     */
    private Integer freez;

    private static final long serialVersionUID = 1L;

}