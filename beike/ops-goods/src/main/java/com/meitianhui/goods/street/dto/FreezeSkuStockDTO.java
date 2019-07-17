package com.meitianhui.goods.street.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * <pre> 冻结街市商品库存实体 </pre>
 *
 * @author tortoise
 * @since 2019/3/28 15:05
 */
@Data
public class FreezeSkuStockDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品编号
     */
    @NotBlank
    @Pattern(regexp = "[a-zA-Z0-9]{1,50}")
    private String goodsId;

    /**
     * 商品编号
     */
    @NotBlank
    @Pattern(regexp = "[a-zA-Z0-9]{1,50}")
    private String skuId;

    /**
     * 子订单编号
     */
    @NotBlank
    @Pattern(regexp = "[a-zA-Z0-9]{1,50}")
    private String oid;

    /**
     * 主订单编号
     */
    @NotBlank
    @Pattern(regexp = "[a-zA-Z0-9]{1,50}")
    private String tid;

    /**
     * 订单类型
     */
    @NotBlank
    @Pattern(regexp = "[a-zA-Z0-9]{1,255}")
    private String orderType;

    /**
     * 商品数量
     */
    @Min(value = 1)
    private int quantity;

    /**
     * 备注
     */
    @Length(max = 255)
    private String remark;

}
