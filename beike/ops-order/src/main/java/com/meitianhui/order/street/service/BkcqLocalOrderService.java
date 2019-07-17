package com.meitianhui.order.street.service;

import com.github.pagehelper.PageInfo;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.order.street.dto.BkcqLocalOrderCreateDTO;
import com.meitianhui.order.street.dto.BkcqLocalOrderQueryDTO;
import com.meitianhui.order.street.dto.OrderPayResuktDTO;
import com.meitianhui.order.street.entity.BkcqLoadCode;
import com.meitianhui.order.street.entity.BkcqLocalOrder;

/**
 * <pre> 街市订单业务操作接口 </pre>
 *
 * @author tortoise
 * @since 2019/3/27 17:05
 */
public interface BkcqLocalOrderService {

    /**
     * 创建街市订单
     *
     * @param bkcqLocalOrderCreateDTO 订单传入参数
     * @return BkcqLocalOrder 街市订单
     * @throws BusinessException 业务异常
     */
    BkcqLocalOrder createOrder(BkcqLocalOrderCreateDTO bkcqLocalOrderCreateDTO) throws BusinessException;

    /**
     * 根据订单编号查询
     *
     * @param orderNo 订单编号
     * @return BkcqLocalOrder 街市订单实体
     * @throws BusinessException 业务异常
     */
    BkcqLocalOrder findOrderByNo(String orderNo) throws BusinessException;

    /**
     * 根据参数分页查询
     *
     * @param bkcqLocalOrderQueryDTO 参数
     * @return PageInfo<BkcqLocalOrder> 街市订单分页实体
     * @throws BusinessException 业务异常
     */
    PageInfo<BkcqLocalOrder> pageByParams(BkcqLocalOrderQueryDTO bkcqLocalOrderQueryDTO) throws BusinessException;

    /**
     * 订单支付成功
     *
     * @param orderNo 订单编号
     * @param payWay  支付方式
     * @return OrderPayResuktDTO 支付结果
     * @throws BusinessException 业务异常
     */
    OrderPayResuktDTO orderPaySuccess(String orderNo, String payWay) throws BusinessException;

    /**
     * 取消订单
     *
     * @param orderNo    订单编号
     * @param consumerId 消费者编号
     * @throws BusinessException 业务异常
     */
    BkcqLocalOrder cancelOrder(String orderNo, String consumerId) throws BusinessException;

    /**
     * 自动过期订单核销码
     *
     * @param id 核销码编号
     * @throws BusinessException 业务异常
     */
    BkcqLoadCode autoExpiredOrderCode(Long id) throws BusinessException;

    /**
     * 自动取消订单
     *
     * @param orderNo 订单编号
     * @throws BusinessException 业务异常
     */
    BkcqLocalOrder autoCancelOrder(String orderNo) throws BusinessException;

    /**
     * 申请退款订单
     *
     * @param orderNo      订单编号
     * @param consumerId   消费者编号
     * @param refundReason 退款说明
     * @throws BusinessException 业务异常
     */
    BkcqLocalOrder applyRefundOrder(String orderNo, String consumerId, String refundReason) throws BusinessException;

}
