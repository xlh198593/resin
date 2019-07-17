package com.meitianhui.finance.street.utils;

import com.google.common.collect.Maps;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.common.util.HttpClientUtil;
import com.meitianhui.common.util.PropertiesConfigUtil;
import com.meitianhui.finance.constant.RspCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * <pre> 调用其它系统Http请求类 </pre>
 *
 * @author tortoise
 * @since 2019/3/29 17:05
 */
@SuppressWarnings({"Duplicates", "unchecked"})
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HttpRequestUtils {

    /**
     * 订单服务地址
     */
    public static final String ORDER_SERVICE_URL = PropertiesConfigUtil.getProperty("order_service_url");

    /**
     * 街市订单服务地址
     */
    public static final String LOCAL_ORDER_SERVICE_URL = ORDER_SERVICE_URL.substring(0, ORDER_SERVICE_URL.lastIndexOf("/")) + "/localOrder";

    /**
     * 订单支付成功
     *
     * @param orderNo 订单编号
     * @return Map<String, Object> 核销码信息
     * @throws BusinessException 业务异常
     */
    public static Map<String, Object> orderPaySuccess(String orderNo,String payWay) throws BusinessException {
        Map<String, String> reqParams = Maps.newHashMap();
        Map<String, Object> bizParams = Maps.newHashMap();
        reqParams.put("service", "streetOrderPaySuccess");
        bizParams.put("orderNo", orderNo);
        bizParams.put("payWay", payWay);
        reqParams.put("params", FastJsonUtil.toJson(bizParams));
        try {
            String resultStr = HttpClientUtil.postShort(LOCAL_ORDER_SERVICE_URL, reqParams);
            Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
            if (!RspCode.RESPONSE_SUCC.equals(resultMap.get("rsp_code").toString())) {
                throw new BusinessException(resultMap.get("error_code").toString()
                        , resultMap.get("error_msg").toString());
            }
            return (Map<String, Object>) resultMap.get("data");
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "街市订单支付失败，系统通讯异常");
        }
    }

    /**
     * 根据订单编号查询订单
     *
     * @param orderNo 订单编号
     * @return Map<String, Object> 订单信息
     * @throws BusinessException 业务异常
     */
    public static Map<String, Object> findOrderByNo(String orderNo) throws BusinessException {
        Map<String, String> reqParams = Maps.newHashMap();
        Map<String, Object> bizParams = Maps.newHashMap();
        reqParams.put("service", "findStreetOrderByOrderNo");
        bizParams.put("orderNo", orderNo);
        bizParams.put("onlyOrder", "true");
        reqParams.put("params", FastJsonUtil.toJson(bizParams));
        try {
            String resultStr = HttpClientUtil.postShort(LOCAL_ORDER_SERVICE_URL, reqParams);
            Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
            if (!RspCode.RESPONSE_SUCC.equals(resultMap.get("rsp_code").toString())) {
                throw new BusinessException(resultMap.get("error_code").toString()
                        , resultMap.get("error_msg").toString());
            }
            return (Map<String, Object>) resultMap.get("data");
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "订单查询失败，系统通讯异常");
        }
    }

}
