package com.meitianhui.order.street.entity;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

/**
 * <pre> 街市订单核销码实体 </pre>
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
public class BkcqLoadCode implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 提货码/核销码
     */
    private String checkCode;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 核销码状态,可选值:CODE_WAIT_USE(待使用);CODE_IS_USED(已使用);CODE_EXPIRED(过期);CODE_NOT_EXIST(码不存在)
     */
    private String codeStatus;

    /**
     * 创建日志
     */
    private Date createdDate;

    /**
     * 修改日期
     */
    private Date modifiedDate;

    /**
     * 过期时间
     */
    private Date expiredDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 订单
     */
    private BkcqLocalOrder bkcqLocalOrder;

    /**
     * 原状态
     */
    private String oldCodeStatus;

    private static final long serialVersionUID = 1L;

}