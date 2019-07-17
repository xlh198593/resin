package com.meitianhui.order.street.entity;

import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * <pre> 街市订单实体 </pre>
 *
 * @author tortoise
 * @since 2019/3/27 15:05
 */
@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BkcqLocalOrderItem implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 订单商品标识
     */
    private Long orderItemId;
    /**
     * 订单标识
     */
    private Long orderId;
    /**
     * 商品sku
     */
    private Long skuId;
    /**
     * 商品主键
     */
    private Long goodsId;
    /**
     * 商品标识
     */
    private String itemStoreId;
    /**
     * 商品标题
     */
    private String itemName;
    /**
     * 货品图片信息，JSON格式，{[title=原始图,path_id:xxxxx1],[title=缩小图,path_id:xxxxx2]}
     */
    private String imageInfo;
    /**
     * 数量
     */
    private Integer qty;
    /**
     * 规格
     */
    private String specification;
    /**
     * 重量
     */
    private Integer weight;
    /**
     * 'bkcq_products 中的service_level 字段， ''服务级别，可选类型 1(随时退）
     */
    private String serviceLevel;
    /**
     * 'bkcq_products 中的对应字段'
     */
    private String perchaseNotice;
    /**
     * 创建时间
     */
    private Date createdDate;
    /**
     * 最后修改时间
     */
    private Date modifiedDate;
    /**
     * 备注
     */
    private String remark;

    /**
     * 商品信息
     */
    private Map<String,Object> product;

}