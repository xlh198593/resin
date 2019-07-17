package com.meitianhui.finance.street.handler.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.jpay.ext.kit.PaymentKit;
import com.jpay.weixin.api.WxPayApi;
import com.jpay.weixin.api.WxPayApiConfig;
import com.jpay.weixin.api.WxPayApiConfigKit;
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

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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
public class WxPayHandler implements PayHandler {

    private static final Logger logger = Logger.getLogger(WxPayHandler.class);

    @Override
    public PayWay getPayWay() {
        return PayWay.ZFFS_02;
    }

    /**
     * 处理支付
     *
     * @param streetOrderPayDTO    支付请求传输对象
     * @param fdTransactionsResult 交易结果
     * @return String 下单结果
     * @throws BusinessException 业务异常
     */

    @Override
    public Map<String, String> handle(StreetOrderPayDTO streetOrderPayDTO,
                                      FdTransactionsResult fdTransactionsResult) throws BusinessException {
        //设置工具支付参数
        WxPayApiConfigKit.setThreadLocalWxPayApiConfig(getWxPayApiConfig());
        try {
            Map<String, Object> order = streetOrderPayDTO.getOrder();
            String desc1 = order.get("desc1").toString();
            List<Map<String, Object>> list = FastJsonUtil.jsonToList(desc1);
            StringBuilder body = new StringBuilder();
            for (Map<String, Object> map : list) {
                body.append(map.get("goodsCode"));
            }

            long expiredDate = Long.parseLong(order.get("expiredDate").toString());
            if (System.currentTimeMillis() > expiredDate) {
                throw new BusinessException(RspCode.RESPONSE_FAIL, "已过期订单，不能支付");
            }

            Map<String, String> params = WxPayApiConfigKit.getWxPayApiConfig().setAttach("贝壳街市订单")
                    .setBody(body.toString()).setSpbillCreateIp("127.0.0.1")
                    .setTimeExpire(DateUtil.format(new Date(expiredDate), new SimpleDateFormat("yyyyMMddHHmmss")))
                    .setTotalFee((fdTransactionsResult.getAmount().multiply(new BigDecimal("100")))
                            .setScale(0, BigDecimal.ROUND_HALF_UP).toString()).setTradeType(WxPayApi.TradeType.APP)
                    .setNotifyUrl(PropertiesConfigUtil.getProperty("wxpay.domain"))
                    .setOutTradeNo(fdTransactionsResult.getTransactionNo()).build();
            logger.info(String.format("WxPay push order param [%s]", FastJsonUtil.toJson(params)));

            String xmlResult = WxPayApi.pushOrder(false, params);
            logger.info(String.format("WxPay push order result [%s]", xmlResult));

            System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
            Map<String, String> resultMap = PaymentKit.xmlToMap(xmlResult);
            String returnCode = resultMap.get("return_code");
            String returnMsg = resultMap.get("return_msg");
            if (!PaymentKit.codeIsOK(returnCode)) {
                throw new BusinessException(RspCode.RESPONSE_FAIL, returnMsg);
            }

            String result_code = resultMap.get("result_code");
            if (!PaymentKit.codeIsOK(result_code)) {
                throw new BusinessException(RspCode.RESPONSE_FAIL, returnMsg);
            }

            String prepayId = resultMap.get("prepay_id");
            Map<String, String> packageParams = Maps.newHashMap();
            packageParams.put("appid", WxPayApiConfigKit.getWxPayApiConfig().getAppId());
            packageParams.put("partnerid", WxPayApiConfigKit.getWxPayApiConfig().getMchId());
            packageParams.put("prepayid", prepayId);
            packageParams.put("package", "Sign=WXPay");
            packageParams.put("noncestr", RandomUtil.randomString(32));
            packageParams.put("timestamp", System.currentTimeMillis() / 1000 + "");
            String packageSign = PaymentKit.createSign(packageParams,
                    WxPayApiConfigKit.getWxPayApiConfig().getPaternerKey());
            packageParams.put("sign", packageSign);
            logger.info(String.format("WxPay push order app data [%s]", JSON.toJSONString(packageParams)));
            return packageParams;
        } catch (Exception e) {
            logger.error(String.format("微信下单失败，订单数据[%s]", streetOrderPayDTO), e);
            throw new BusinessException(RspCode.RESPONSE_FAIL, "微信下单失败");
        }
    }

    /**
     * 获取支付结果
     *
     * @param transactionNo 交易编号
     * @return Map<String, String> 支付结果
     * @throws BusinessException 业务异常
     */
    public Map<String, String> getPayResult(String transactionNo) throws BusinessException {
        //设置工具支付参数
        WxPayApiConfigKit.setThreadLocalWxPayApiConfig(getWxPayApiConfig());
        try {
            Map<String, String> params = Maps.newHashMap();
            params.put("appid", WxPayApiConfigKit.getWxPayApiConfig().getAppId());
            params.put("mch_id", WxPayApiConfigKit.getWxPayApiConfig().getMchId());
            params.put("out_trade_no", transactionNo);
            params.put("nonce_str", RandomUtil.randomString(32));

            String sign = PaymentKit.createSign(params,
                    WxPayApiConfigKit.getWxPayApiConfig().getPaternerKey());
            params.put("sign", sign);
            String xmlResult = WxPayApi.orderQuery(false, params);

            System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
            Map<String, String> orderQuery = PaymentKit.xmlToMap(xmlResult);
            logger.info(String.format("WxPay order query response result[%s]", FastJsonUtil.toJson(orderQuery)));
            return orderQuery;
        } catch (Exception e) {
            logger.error(String.format("获取微信支付结果失败，交易编号[%s]", transactionNo), e);
            throw new BusinessException(RspCode.RESPONSE_FAIL, "获取微信支付结果失败");
        }
    }

    /**
     * 申请退款
     *
     * @param fdRefundAftersales 退款记录
     * @return Map<String, String> 退款申请结果
     * @throws BusinessException 业务异常
     */
    public Map<String, String> applyRefund(FdRefundAftersales fdRefundAftersales) throws BusinessException {
        //设置工具支付参数
        WxPayApiConfigKit.setThreadLocalWxPayApiConfig(getWxPayApiConfig());
        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("appid", WxPayApiConfigKit.getWxPayApiConfig().getAppId());
            params.put("mch_id", WxPayApiConfigKit.getWxPayApiConfig().getMchId());
            params.put("nonce_str", RandomUtil.randomString(32));
            params.put("out_trade_no", fdRefundAftersales.getTransactionNo());
            params.put("out_refund_no", fdRefundAftersales.getRefundsNo());
            params.put("total_fee", "1");
            params.put("refund_fee", fdRefundAftersales.getTotalPrice().multiply(new BigDecimal("100"))
                    .setScale(BigDecimal.ROUND_HALF_UP, 0).toString());
            params.put("sign", PaymentKit.createSign(params, WxPayApiConfigKit.getWxPayApiConfig().getPaternerKey()));
            String refund = WxPayApi.orderRefund(false, params, PropertiesConfigUtil
                    .getProperty("wxpay.certPath"), WxPayApiConfigKit.getWxPayApiConfig().getMchId());
            System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
            return PaymentKit.xmlToMap(refund);
        } catch (Exception e) {
            logger.error(String.format("微信支付申请退款失败，退款信息[%s]", fdRefundAftersales), e);
            throw new BusinessException(RspCode.RESPONSE_FAIL, "微信支付申请退款失败");
        }
    }

    /**
     * 获取退款结果
     *
     * @param fdRefundAftersales 退款记录
     * @return Map<String, String> 退款申请结果
     * @throws BusinessException 业务异常
     */
    public Map<String, String> getRefundResult(FdRefundAftersales fdRefundAftersales) throws BusinessException {
        //设置工具支付参数
        WxPayApiConfigKit.setThreadLocalWxPayApiConfig(getWxPayApiConfig());
        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("appid", WxPayApiConfigKit.getWxPayApiConfig().getAppId());
            params.put("mch_id", WxPayApiConfigKit.getWxPayApiConfig().getMchId());
            params.put("nonce_str", RandomUtil.randomString(32));
            params.put("out_refund_no", fdRefundAftersales.getRefundsNo());
            params.put("sign", PaymentKit.createSign(params, WxPayApiConfigKit.getWxPayApiConfig().getPaternerKey()));
            String refund = WxPayApi.orderRefundQuery(false, params);
            System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
            return PaymentKit.xmlToMap(refund);
        } catch (Exception e) {
            logger.error(String.format("微信支付获取退款结果失败失败，退款信息[%s]", fdRefundAftersales), e);
            throw new BusinessException(RspCode.RESPONSE_FAIL, "微信支付获取退款结果失败失败");
        }
    }

    /**
     * 获取微信支付配置
     *
     * @return WxPayApiConfig
     */
    private WxPayApiConfig getWxPayApiConfig() {
        return WxPayApiConfig.New().setAppId(PropertiesConfigUtil.getProperty("wxpay.appId"))
                .setMchId(PropertiesConfigUtil.getProperty("wxpay.mchId"))
                .setPaternerKey(PropertiesConfigUtil.getProperty("wxpay.partnerKey"))
                .setPayModel(WxPayApiConfig.PayModel.BUSINESSMODEL);
    }

}
