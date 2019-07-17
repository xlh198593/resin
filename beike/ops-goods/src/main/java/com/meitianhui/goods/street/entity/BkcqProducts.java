package com.meitianhui.goods.street.entity;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <pre> 街市商品实体 </pre>
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
public class BkcqProducts implements Serializable {
    /**
     * 商品标识
     */
    private Long goodsId;

    /**
     * 商品编码
     */
    private String goodsCode;

    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    private String desc1;

    /**
     * 一级分类
     */
    private Integer topCatid;

    /**
     * 二级分类
     */
    private Integer twoCatid;

    /**
     * 三级分类
     */
    private Integer threeCatid;

    /**
     * 品牌id
     */
    private Integer brandId;

    /**
     * 标签
     */
    private String label;

    /**
     * 促销标签(客厅用品,卫浴用品,厨房用品,卧室用品,书房玩具)
     */
    private String labelPromotion;

    /**
     * 商品来源以字符标识，可选值：self（贝壳商超）,merchants（精品商城）,coupons(礼包区)，street(街市)
     */
    private String goodsSource;

    /**
     * 推荐展馆，可选值：工艺、美食、母婴、日杂、家具、兑换
     */
    private String displayArea;

    /**
     * 联系人
     */
    private String contactPerson;

    /**
     * 联系人电话
     */
    private String contactTel;

    /**
     * 货品图片信息，JSON格式，{[title=原始图,path_id:xxxxx1],[title=缩小图,path_id:xxxxx2]}
     */
    private String picInfo;

    /**
     * 货品详细，JSON格式，{[title=原始图,path_id:xxxxx1],[title=缩小图,path_id:xxxxx2]}
     */
    private String picDetailInfo;

    /**
     * 规格
     */
    private String specification;

    /**
     * 包装
     */
    private String pack;

    /**
     * 市场价
     */
    private BigDecimal marketPrice;

    /**
     * 成本价/进货价
     */
    private BigDecimal costPrice;

    /**
     * 普通会员价
     */
    private BigDecimal salePrice;

    /**
     * 抵扣贝壳
     */
    private Long beikeCredit;

    /**
     * vip会员价
     */
    private BigDecimal vipPrice;

    /**
     * 邮费
     */
    private BigDecimal shippingFee;

    /**
     * 结算价
     */
    private BigDecimal settledPrice;

    /**
     * 产地
     */
    private String producer;

    /**
     * 供应商标识
     */
    private String supplierId;

    /**
     * 供应商
     */
    private String supplier;

    /**
     * 店铺自定义分类
     */
    private Integer supplierCatId;

    /**
     * 供应商类型(store 门店   ;supplier 入驻商 ;agent 区域代理)
     */
    private String supplierType;

    /**
     * 促销类型
     */
    private String promotionType;

    /**
     * 促销ID
     */
    private Integer promotionId;

    /**
     * 生产商
     */
    private String manufacturer;

    /**
     * 起订量
     */
    private Integer minBuyQty;

    /**
     * 限购量
     */
    private Integer maxBuyQty;

    /**
     * 虚拟删除
     */
    private Byte isDeleted;

    /**
     * 是否未虚拟产品
     */
    private Byte isVirtual;

    /**
     * 可销售库存量
     */
    private Integer saleQty;

    /**
     * 总库存量
     */
    private Integer stockQty;

    /**
     * 单位
     */
    private String goodsUnit;

    /**
     * 仓库名称
     */
    private String warehouse;

    /**
     * 仓库标识
     */
    private String warehouseId;

    /**
     * 配送地区
     */
    private String deliveryArea;

    /**
     * 支付方式，可选值：online（在线支付） offline（货到付款）
     */
    private String paymentWay;

    /**
     * 状态，可选值：normal（正常）delete（删除）on_shelf(上架) off_shelf（下架）violation(违规) suspend(待审核) checked_online(审核后直接上架)
     */
    private String status;

    /**
     * 创建时间
     */
    private Date createdDate;

    /**
     * 最后修改时间
     */
    private Date modifiedDate;

    /**
     * 排序号
     */
    private Integer sorted;

    /**
     * 实际销量
     */
    private Integer salesVolume;

    /**
     * 伪销量
     */
    private Integer fakeSalesVolume;

    /**
     * 开卖日期
     */
    private Date validThru;

    /**
     * 上架时间
     */
    private Date onlineDate;

    /**
     * 下架时间
     */
    private Date offlineDate;

    /**
     * 礼券价
     */
    private Integer couponPrice;

    /**
     * 服务级别，可选类型 1(随时退）2（过期退),3（免预约）
     */
    private String serviceLevel;

    /**
     * 导自其他商家商品编码
     */
    private String bn;

    /**
     * 发票ID，关联发票表
     */
    private String invoiceId;

    /**
     * 用户购买须知,json串 举例{"shell_validay":30，"use_time":"Mon Sat 14:00 15:00","max_count":2,"is_offer_invoice":1,"rest_remark":"不开发票"}
     */
    private String perchaseNotice;

    /**
     * 贝壳街市商品售卖开始时间
     */
    private Date activityStartTime;

    /**
     * 贝壳街市商品售卖结束时间
     */
    private Date ativityEndTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 商品sku
     */
    private BkcqSku bkcqSku;

    private static final long serialVersionUID = 1L;

}