package com.meitianhui.finance.street.service;

import com.alipay.api.response.AlipayTradeQueryResponse;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.finance.street.dto.StreetOrderPayDTO;
import com.meitianhui.finance.street.entity.FdTransactionsResult;

import java.util.Map;

/**
 * 街市支付请求接口
 *
 * @author tortoise
 * @since 2019/3/27 16:40
 */
public interface StreetPayService {

    /**
     * 订单支付
     *
     * @param streetOrderPayDTO 支付传输对象
     * @return Map<PayWay, FdTransactionsResult> 交易结果集合
     * @throws BusinessException 业务异常
     */
    Map<String, FdTransactionsResult> orderPay(StreetOrderPayDTO streetOrderPayDTO) throws BusinessException;

    /**
     * 贝壳支付退款
     *
     * @param transactionNo 退款交易编号
     * @return FdTransactionsResult 交易结果
     * @throws BusinessException 业务异常
     */
    FdTransactionsResult beikeRefund(String transactionNo) throws BusinessException;

    /**
     * 微信支付回调成功
     *
     * @param params 回调参数
     * @return FdTransactionsResult 交易结果集合
     * @throws BusinessException 业务异常
     */
    FdTransactionsResult wxPayCallbackSuccess(Map<String, String> params) throws BusinessException;

    /**
     * 支付宝支付回调成功
     *
     * @param params 回调参数
     * @return FdTransactionsResult 交易结果集合
     * @throws BusinessException 业务异常
     */
    FdTransactionsResult aliPayCallbackSuccess(Map<String, String> params) throws BusinessException;

    /**
     * 支付宝支付同步成功
     *
     * @param params 交易查询结果
     * @return FdTransactionsResult 交易结果集合
     * @throws BusinessException 业务异常
     */
    FdTransactionsResult aliPaySyncSuccess(AlipayTradeQueryResponse params) throws BusinessException;

}
