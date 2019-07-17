package com.meitianhui.order.street.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Map;

/**
 * <pre> 街市订单下单传输对象 </pre>
 *
 * @author tortoise
 * @since 2019/3/27 15:05
 */
@Data
public class BkcqLocalOrderCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消费者标识
     */
    @NotBlank
    @Pattern(regexp = "[a-zA-Z0-9]{1,50}")
    private String consumerId;

    /**
     * 商品编号
     */
    @NotBlank
    @Pattern(regexp = "[a-zA-Z0-9]{1,50}")
    private String goodsId;

    /**
     * 商品sku编号
     */
    @NotBlank
    @Pattern(regexp = "[a-zA-Z0-9]{1,50}")
    private String skuId;

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
     * 订单编号，请求不需要传递
     */
    private String orderNo;

    /**
     * 商品信息，请求不需要传递
     */
    private Map<String, Object> product;

    /**
     * 消费者信息，请求不需要传递
     */
    private Map<String, Object> consumer;

}
