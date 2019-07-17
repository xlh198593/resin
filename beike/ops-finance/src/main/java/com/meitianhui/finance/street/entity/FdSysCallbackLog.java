package com.meitianhui.finance.street.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <pre> 支付通知日志对象 </pre>
 *
 * @author tortoise
 * @since 2019/4/1 11:05
 */
@Data
public class FdSysCallbackLog implements Serializable {
    private Long callbackId;

    /**
     * 支付回调类型，可选值 wechat(微信),alipay（支付宝）
     */
    private String callType;

    /**
     * 回调url
     */
    private String callUrl;

    /**
     * 回调参数
     */
    private Date callParams;

    /**
     * 回调状态,可选值failed (回调失败), succ(回调成功)
     */
    private String callStatus;

    /**
     * 处理状态,可选值 wait(待处理) handling(处理中) failed (处理失败), succ(处理成功)
     */
    private String handleStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;

}