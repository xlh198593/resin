package com.meitianhui.finance.street.handler.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.domain.AlipayTradeFastpayRefundQueryModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.google.common.collect.Maps;
import com.jpay.alipay.AliPayApi;
import com.jpay.alipay.AliPayApiConfig;
import com.jpay.alipay.AliPayApiConfigKit;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.common.util.PropertiesConfigUtil;
import com.meitianhui.finance.constant.RspCode;
import com.meitianhui.finance.street.consts.PayWay;
import com.meitianhui.finance.street.dto.StreetOrderPayDTO;
import com.meitianhui.finance.street.entity.FdRefundAftersales;
import com.meitianhui.finance.street.entity.FdTransactionsResult;
import com.meitianhui.finance.street.handler.PayHandler;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 阿里支付请求接口
 *
 * @author tortoise
 * @since 2019/3/27 20:40
 */
@SuppressWarnings({"unchecked", "Duplicates"})
@Component
public class AliPayHandler implements PayHandler {

    private static final Logger logger = Logger.getLogger(AliPayHandler.class);

    @Override
    public PayWay getPayWay() {
        return PayWay.ZFFS_01;
    }

    /**
     * 处理支付
     *
     * @param streetOrderPayDTO    支付请求传输对象
     * @param fdTransactionsResult 交易结果
     * @return Map<String, String> 下单结果
     * @throws BusinessException 业务异常
     */
    @Override
    public Map<String, String> handle(StreetOrderPayDTO streetOrderPayDTO, FdTransactionsResult fdTransactionsResult) throws BusinessException {
        //设置工具支付参数
        AliPayApiConfigKit.setThreadLocalAliPayApiConfig(getAliPayApiConfig());
        try {
            Map<String, Object> order = streetOrderPayDTO.getOrder();
            String desc1 = order.get("desc1").toString();
            List<Map<String, Object>> list = FastJsonUtil.jsonToList(desc1);
            StringBuilder body = new StringBuilder();
            for (Map<String, Object> map : list) {
                body.append(map.get("goodsCode"));
            }

            long expiredDate = Long.parseLong(order.get("expiredDate").toString());
            if (System.currentTimeMillis() > expiredDate + 60000) {
                throw new BusinessException(RspCode.RESPONSE_FAIL, "已过期订单，不能支付");
            }

            AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
            model.setBody(body.toString());
            model.setSubject("贝壳街市订单");
            model.setOutTradeNo(fdTransactionsResult.getTransactionNo());
            model.setTimeoutExpress((System.currentTimeMillis() - expiredDate) / 1000 / 60 + "m");
            model.setTotalAmount(fdTransactionsResult.getAmount().toString());
            model.setPassbackParams("STREET_ORDER");
            model.setProductCode("QUICK_MSECURITY_PAY");
            logger.info(String.format("AliPay request params[%s]", FastJsonUtil.toJson(model)));

            AlipayTradeAppPayResponse alipayTradeAppPayResponse = AliPayApi.appPayToResponse(model
                    , PropertiesConfigUtil.getProperty("alipay.domain"));
            logger.info(String.format("AliPay response result[%s]", FastJsonUtil.toJson(alipayTradeAppPayResponse)));
            Map<String, String> data = Maps.newHashMap();
            data.put("alipay_params", alipayTradeAppPayResponse.getBody());
            return data;
        } catch (Exception e) {
            logger.error(String.format("支付宝下单失败，订单数据[%s]", streetOrderPayDTO), e);
            throw new BusinessException(RspCode.RESPONSE_FAIL, "支付宝下单失败");
        }
    }

    /**
     * 获取支付结果
     *
     * @param transactionNo 交易编号
     * @return Map<String, String> 支付结果
     * @throws BusinessException 业务异常
     */
    public AlipayTradeQueryResponse getPayResult(String transactionNo) throws BusinessException {
        //设置工具支付参数
        AliPayApiConfigKit.setThreadLocalAliPayApiConfig(getAliPayApiConfig());
        try {
            AlipayTradeQueryModel model = new AlipayTradeQueryModel();
            model.setOutTradeNo(transactionNo);
            logger.info(String.format("AliPay order query request params[%s]", FastJsonUtil.toJson(model)));

            AlipayTradeQueryResponse alipayTradeQueryResponse = AliPayApi.tradeQueryToResponse(model);
            logger.info(String.format("AliPay order query response result[%s]", FastJsonUtil.toJson(alipayTradeQueryResponse)));

            return alipayTradeQueryResponse;
        } catch (Exception e) {
            logger.error(String.format("获取支付宝支付结果失败，交易编号[%s]", transactionNo), e);
            throw new BusinessException(RspCode.RESPONSE_FAIL, "下单失败");
        }
    }

    /**
     * 申请退款
     *
     * @param fdRefundAftersales 退款记录
     * @return AlipayTradeRefundResponse 退款申请结果
     * @throws BusinessException 业务异常
     */
    public AlipayTradeRefundResponse applyRefund(FdRefundAftersales fdRefundAftersales) throws BusinessException {
        //设置工具支付参数
        AliPayApiConfigKit.setThreadLocalAliPayApiConfig(getAliPayApiConfig());
        try {
            AlipayTradeRefundModel model = new AlipayTradeRefundModel();
            model.setOutRequestNo(fdRefundAftersales.getRefundsNo());
            model.setOutTradeNo(fdRefundAftersales.getTransactionNo());
            model.setRefundAmount(fdRefundAftersales.getTotalPrice().toString());
            model.setRefundReason(fdRefundAftersales.getRefundsReason());
            return AliPayApi.tradeRefundToResponse(model);
        } catch (AlipayApiException e) {
            logger.error(String.format("支付宝支付申请退款失败，退款信息[%s]", fdRefundAftersales), e);
            throw new BusinessException(RspCode.RESPONSE_FAIL, "申请退款失败");
        }
    }

    /**
     * 获取退款结果
     *
     * @param fdRefundAftersales 退款记录
     * @return AlipayTradeFastpayRefundQueryResponse 支付宝退款结果
     * @throws BusinessException 业务异常
     */
    public AlipayTradeFastpayRefundQueryResponse getRefundResult(FdRefundAftersales fdRefundAftersales) throws BusinessException {
        //设置工具支付参数
        AliPayApiConfigKit.setThreadLocalAliPayApiConfig(getAliPayApiConfig());
        try {
            AlipayTradeFastpayRefundQueryModel model = new AlipayTradeFastpayRefundQueryModel();
            model.setOutRequestNo(fdRefundAftersales.getRefundsNo());
            model.setOutTradeNo(fdRefundAftersales.getTransactionNo());
            return AliPayApi.tradeRefundQueryToResponse(model);
        } catch (AlipayApiException e) {
            logger.error(String.format("支付宝支付获取退款结果失败，退款信息[%s]", fdRefundAftersales), e);
            throw new BusinessException(RspCode.RESPONSE_FAIL, "获取退款结果失败");
        }
    }

    /**
     * 获取阿里支付配置
     *
     * @return AliPayApiConfig
     */
    private AliPayApiConfig getAliPayApiConfig() {
        return AliPayApiConfig.New().setAppId(PropertiesConfigUtil.getProperty("alipay.appId"))
                .setAlipayPublicKey(PropertiesConfigUtil.getProperty("alipay.publicKey"))
                .setCharset("UTF-8").setPrivateKey(PropertiesConfigUtil.getProperty("alipay.privateKey"))
                .setServiceUrl(PropertiesConfigUtil.getProperty("alipay.serverUrl"))
                .setSignType("RSA2").build();
    }

}
