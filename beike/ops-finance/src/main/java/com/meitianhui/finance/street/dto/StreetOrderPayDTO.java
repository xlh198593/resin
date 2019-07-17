package com.meitianhui.finance.street.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Map;

/**
 * <pre> 街市订单支付传输对象 </pre>
 *
 * @author tortoise
 * @since 2019/3/31 11:05
 */
@Data
public class StreetOrderPayDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消费者标识
     */
    @NotBlank
    @Pattern(regexp = "[a-zA-Z0-9]{1,50}")
    private String consumerId;

    /**
     * 订单编号
     */
    @NotBlank
    @Pattern(regexp = "[a-zA-Z0-9]{1,50}")
    private String orderNo;

    /**
     * 支付来源 SJLY_01 贝壳传奇App
     */
    @NotBlank
    @Pattern(regexp = "[a-zA-Z0-9_-]{1,50}")
    private String dataSource;

    /**
     * 支付方式 ZFFS_01 支付宝 ZFFS_02 微信 ZFFS_05 零钱  ZFFS_07 贝壳 ZFFS_08 红包 ZFFS_09 金币
     */
    @NotBlank
    @Pattern(regexp = "[a-zA-Z0-9-_]{1,50}")
    private String payWay;

    /**
     * 订单信息，请求不需要传递
     */
    private Map<String, Object> order;

}
