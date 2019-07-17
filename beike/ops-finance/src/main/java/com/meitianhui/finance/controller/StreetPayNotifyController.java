package com.meitianhui.finance.controller;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.google.common.collect.Maps;
import com.jpay.alipay.AliPayApi;
import com.jpay.alipay.AliPayApiConfig;
import com.jpay.alipay.AliPayApiConfigKit;
import com.jpay.ext.kit.PaymentKit;
import com.jpay.weixin.api.WxPayApiConfig;
import com.jpay.weixin.api.WxPayApiConfigKit;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.common.util.PropertiesConfigUtil;
import com.meitianhui.finance.street.entity.FdTransactionsResult;
import com.meitianhui.finance.street.service.StreetPayService;
import com.meitianhui.finance.street.utils.HttpRequestUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <pre> 街市订单支付回调控制器 </pre>
 *
 * @author tortoise
 * @since 2019/3/27 12:40
 */
@SuppressWarnings("unchecked")
@Controller
@RequestMapping("/streetPayNotify")
public class StreetPayNotifyController {

    private static final Logger logger = Logger.getLogger(StreetPayNotifyController.class);

    @Autowired
    private StreetPayService streetPayService;

    /**
     * 阿里支付回调控制器
     *
     * @param request HttpServletRequest对象
     * @return 字符串
     */
    @ResponseBody
    @RequestMapping(value = "/aliPayNotify")
    public String aliPayNotify(HttpServletRequest request) {
        // 获取支付宝POST过来反馈信息
        Map<String, String> params = AliPayApi.toMap(request);

        logger.info(String.format("支付宝回调参数%s", FastJsonUtil.toJson(params)));

        try {
            //设置工具支付参数
            AliPayApiConfigKit.setThreadLocalAliPayApiConfig(getAliPayApiConfig());

            boolean verify_result = AlipaySignature.rsaCheckV1(params, AliPayApiConfigKit.getAliPayApiConfig()
                    .getAlipayPublicKey(), "UTF-8", "RSA2");

            // 验证成功
            if (verify_result) {
                if (!"TRADE_SUCCESS".equals(params.get("trade_status"))) {
                    return "failure";
                }

                FdTransactionsResult fdTransactionsResult = streetPayService.aliPayCallbackSuccess(params);
                if ("completed".equals(fdTransactionsResult.getTransactionStatus())) {
                    // 调用订单接口回写订单完成并生成核销码
                    HttpRequestUtils.orderPaySuccess(fdTransactionsResult.getOutTradeNo(), fdTransactionsResult.getPaymentWayKey());
                }
                return "success";
            } else {
                logger.error(String.format("支付宝回调失败，验证失败，失败参数：%s", FastJsonUtil.toJson(params)));
                return "failure";
            }
        } catch (AlipayApiException e) {
            logger.error(String.format("支付宝回调失败，失败参数：%s", FastJsonUtil.toJson(params)), e);
            return "failure";
        } catch (BusinessException e) {
            logger.error(String.format("支付宝回调失败，后台处理失败，失败参数：%s", FastJsonUtil.toJson(params)), e);
            return "failure";
        } catch (Exception e) {
            logger.error(String.format("支付宝回调失败，处理失败，失败参数：%s", FastJsonUtil.toJson(params)), e);
            return "failure";
        }
    }

    /**
     * 微信支付回调控制器
     *
     * @param request HttpServletRequest对象
     * @return 字符串
     */
    @ResponseBody
    @RequestMapping(value = "/wxPayNotify")
    public String wxPayNotify(HttpServletRequest request) {
        WxPayApiConfigKit.setThreadLocalWxPayApiConfig(getWxPayApiConfig());

        String data = request.getParameter("data");
        try {
            logger.info(String.format("微信回调参数%s", data));
            Map<String, String> params = JSONObject.parseObject(data, Map.class);
            String result_code = params.get("result_code");

            if (PaymentKit.verifyNotify(params, WxPayApiConfigKit.getWxPayApiConfig().getPaternerKey())) {
                if (("SUCCESS").equals(result_code)) {
                    FdTransactionsResult fdTransactionsResult = streetPayService.wxPayCallbackSuccess(params);
                    if ("completed".equals(fdTransactionsResult.getTransactionStatus())) {
                        // 调用订单接口回写订单完成并生成核销码
                        HttpRequestUtils.orderPaySuccess(fdTransactionsResult.getOutTradeNo(), fdTransactionsResult.getPaymentWayKey());
                    }

                    Map<String, String> xml = Maps.newHashMap();
                    xml.put("return_code", "SUCCESS");
                    xml.put("return_msg", "OK");
                    return PaymentKit.toXml(xml);
                }
            }
            return null;
        } catch (BusinessException e) {
            logger.error(String.format("微信回调失败，后台处理失败，失败参数：%s", data), e);
            return null;
        } catch (Exception e) {
            logger.error(String.format("微信回调失败，处理失败，失败参数：%s", data), e);
            return null;
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
