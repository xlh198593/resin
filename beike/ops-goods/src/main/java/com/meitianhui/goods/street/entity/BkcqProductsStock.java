package com.meitianhui.goods.street.entity;

import lombok.*;

import java.io.Serializable;

/**
 * <pre> 街市商品库存实体 </pre>
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
public class BkcqProductsStock implements Serializable {

    /**
     * 商品ID
     */
    private Long goodsId;

    /**
     * 商品可用数量
     */
    private Integer store;

    /**
     * 商品冻结
     */
    private Integer freez;

    private static final long serialVersionUID = 1L;

}