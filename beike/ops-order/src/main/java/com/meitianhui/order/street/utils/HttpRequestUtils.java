package com.meitianhui.order.street.utils;

import com.google.common.collect.Maps;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.common.util.HttpClientUtil;
import com.meitianhui.common.util.PropertiesConfigUtil;
import com.meitianhui.order.constant.RspCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
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
     * 会员服务地址
     */
    public static final String MEMBER_SERVICE_URL = PropertiesConfigUtil.getProperty("member_service_url");

    /**
     * 商品服务地址
     */
    public static final String GOODS_SERVICE_URL = PropertiesConfigUtil.getProperty("goods_service_url");

    /**
     * 资产服务地址
     */
    public static final String FINANCE_SERVICE_URL = PropertiesConfigUtil.getProperty("finance_service_url");

    /**
     * 资产服务地址
     */
    public static final String STREET_FINANCE_SERVICE_URL = FINANCE_SERVICE_URL.substring(0, FINANCE_SERVICE_URL.lastIndexOf("/")) + "/streetPay";

    /**
     * 街市商品服务地址
     */
    public static final String PRODUCTS_SERVICE_URL = GOODS_SERVICE_URL.substring(0, GOODS_SERVICE_URL.lastIndexOf("/")) + "/bkcqProducts";

    /**
     * 根据消费者查询消费者
     *
     * @param consumerId 消费者编号
     * @return Map<String, Object>消费者Map
     * @throws BusinessException 业务异常
     */
    public static Map<String, Object> findConsumerById(String consumerId) throws BusinessException {
        Map<String, String> reqParams = Maps.newHashMap();
        Map<String, Object> bizParams = Maps.newHashMap();
        reqParams.put("service", "consumer.findConsumerById");
        bizParams.put("consumer_id", consumerId);
        reqParams.put("params", FastJsonUtil.toJson(bizParams));
        try {
            String resultStr = HttpClientUtil.postShort(MEMBER_SERVICE_URL, reqParams);
            Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
            if (!RspCode.RESPONSE_SUCC.equals(resultMap.get("rsp_code").toString())) {
                throw new BusinessException(resultMap.get("error_code").toString()
                        , resultMap.get("error_msg").toString());
            }
            return (Map<String, Object>) resultMap.get("data");
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "查询消费者信息失败，系统通讯异常");
        }
    }

    /**
     * 根据店铺编号查询消费者
     *
     * @param storeId 消费者编号
     * @return Map<String, Object>消费者Map
     * @throws BusinessException 业务异常
     */
    public static Map<String, Object> findStoresById(String storeId) throws BusinessException {
        Map<String, String> reqParams = Maps.newHashMap();
        Map<String, Object> bizParams = Maps.newHashMap();
        reqParams.put("service", "stores.stores.storesBaseInfoFind");
        bizParams.put("stores_id", storeId);
        reqParams.put("params", FastJsonUtil.toJson(bizParams));
        try {
            String resultStr = HttpClientUtil.postShort(MEMBER_SERVICE_URL, reqParams);
            Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
            if (!RspCode.RESPONSE_SUCC.equals(resultMap.get("rsp_code").toString())) {
                throw new BusinessException(resultMap.get("error_code").toString()
                        , resultMap.get("error_msg").toString());
            }
            return (Map<String, Object>) resultMap.get("data");
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "查询门店信息失败，系统通讯异常");
        }
    }

    /**
     * 根据订单编号查询交易
     *
     * @param orderNo 订单编号
     * @return Map<String, Object>消费者Map
     * @throws BusinessException 业务异常
     */
    public static List<Map<String, Object>> findTransactionById(String orderNo) throws BusinessException {
        Map<String, String> reqParams = Maps.newHashMap();
        Map<String, Object> bizParams = Maps.newHashMap();
        reqParams.put("service", "findTransactionByOrderNo");
        bizParams.put("orderNo", orderNo);
        reqParams.put("params", FastJsonUtil.toJson(bizParams));
        try {
            String resultStr = HttpClientUtil.postShort(STREET_FINANCE_SERVICE_URL, reqParams);
            Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
            if (!RspCode.RESPONSE_SUCC.equals(resultMap.get("rsp_code").toString())) {
                throw new BusinessException(resultMap.get("error_code").toString()
                        , resultMap.get("error_msg").toString());
            }
            return (List<Map<String, Object>>) resultMap.get("data");
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "查询订单交易信息失败，系统通讯异常");
        }
    }

    /**
     * 根据商品编码查询商品详情
     *
     * @param goodsId 商品编号
     * @return Map<String, Object> 商品信息
     * @throws BusinessException 业务异常
     */
    public static Map<String, Object> findProductById(String goodsId) throws BusinessException {
        Map<String, String> reqParams = Maps.newHashMap();
        Map<String, Object> bizParams = Maps.newHashMap();
        reqParams.put("service", "findProductByKey");
        bizParams.put("goodsId", goodsId);
        reqParams.put("params", FastJsonUtil.toJson(bizParams));
        try {
            String resultStr = HttpClientUtil.postShort(PRODUCTS_SERVICE_URL, reqParams);
            Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
            if (!RspCode.RESPONSE_SUCC.equals(resultMap.get("rsp_code").toString())) {
                throw new BusinessException(resultMap.get("error_code").toString()
                        , resultMap.get("error_msg").toString());
            }
            return (Map<String, Object>) resultMap.get("data");
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "查询商品信息失败，系统通讯异常");
        }
    }

    /**
     * 冻结sku库存
     *
     * @param goodsId   商品编号
     * @param skuId     商品sku编号
     * @param oid       子订单号
     * @param tid       主订单号
     * @param orderType 订单类型
     * @param quantity  数量
     * @param remark    备注
     * @return Map<String, Object> 商品信息
     * @throws BusinessException 业务异常
     */
    public static Map<String, Object> freezeSkuStock(String goodsId, String skuId, String oid, String tid
            , String orderType, int quantity, String remark) throws BusinessException {
        Map<String, String> reqParams = new HashMap<String, String>();
        Map<String, Object> bizParams = new HashMap<String, Object>();
        reqParams.put("service", "freezeSkuStock");
        bizParams.put("goodsId", goodsId);
        bizParams.put("skuId", skuId);
        bizParams.put("oid", oid);
        bizParams.put("tid", tid);
        bizParams.put("orderType", orderType);
        bizParams.put("quantity", quantity);
        bizParams.put("remark", remark);
        reqParams.put("params", FastJsonUtil.toJson(bizParams));
        try {
            String resultStr = HttpClientUtil.postShort(PRODUCTS_SERVICE_URL, reqParams);
            Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
            if (!RspCode.RESPONSE_SUCC.equals(resultMap.get("rsp_code").toString())) {
                throw new BusinessException(resultMap.get("error_code").toString()
                        , resultMap.get("error_msg").toString());
            }
            return (Map<String, Object>) resultMap.get("data");
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "库存冻结失败，系统通讯异常");
        }
    }

    /**
     * 解冻sku库存
     *
     * @param goodsId   商品编号
     * @param skuId     商品sku编号
     * @param oid       子订单号
     * @param tid       主订单号
     * @param orderType 订单类型
     * @param quantity  数量
     * @param remark    备注
     * @return Map<String, Object> 商品信息
     * @throws BusinessException 业务异常
     */
    public static Map<String, Object> unfreezeSkuStock(String goodsId, String skuId, String oid, String tid
            , String orderType, int quantity, String remark) throws BusinessException {
        Map<String, String> reqParams = new HashMap<String, String>();
        Map<String, Object> bizParams = new HashMap<String, Object>();
        reqParams.put("service", "unfreezeSkuStock");
        bizParams.put("goodsId", goodsId);
        bizParams.put("skuId", skuId);
        bizParams.put("oid", oid);
        bizParams.put("tid", tid);
        bizParams.put("orderType", orderType);
        bizParams.put("quantity", quantity);
        bizParams.put("remark", remark);
        reqParams.put("params", FastJsonUtil.toJson(bizParams));
        try {
            String resultStr = HttpClientUtil.postShort(PRODUCTS_SERVICE_URL, reqParams);
            Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
            if (!RspCode.RESPONSE_SUCC.equals(resultMap.get("rsp_code").toString())) {
                throw new BusinessException(resultMap.get("error_code").toString()
                        , resultMap.get("error_msg").toString());
            }
            return (Map<String, Object>) resultMap.get("data");
        } catch (Exception e) {
            throw new BusinessException(RspCode.ORDER_ERROE, "库存冻结失败，系统通讯异常");
        }
    }

    /**
     * 扣减sku库存
     *
     * @param goodsId   商品编号
     * @param skuId     商品sku编号
     * @param oid       子订单号
     * @param tid       主订单号
     * @param orderType 订单类型
     * @param quantity  数量
     * @param remark    备注
     * @return Map<String, Object> 商品信息
     * @throws BusinessException 业务异常
     */
    public static Map<String, Object> deductionSkuStock(String goodsId, String skuId, String oid, String tid
            , String orderType, int quantity, String remark) throws BusinessException {
        Map<String, String> reqParams = new HashMap<String, String>();
        Map<String, Object> bizParams = new HashMap<String, Object>();
        reqParams.put("service", "deductionSkuStock");
        bizParams.put("goodsId", goodsId);
        bizParams.put("skuId", skuId);
        bizParams.put("oid", oid);
        bizParams.put("tid", tid);
        bizParams.put("orderType", orderType);
        bizParams.put("quantity", quantity);
        bizParams.put("remark", remark);
        reqParams.put("params", FastJsonUtil.toJson(bizParams));
        try {
            String resultStr = HttpClientUtil.postShort(PRODUCTS_SERVICE_URL, reqParams);
            Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
            if (!RspCode.RESPONSE_SUCC.equals(resultMap.get("rsp_code").toString())) {
                throw new BusinessException(resultMap.get("error_code").toString()
                        , resultMap.get("error_msg").toString());
            }
            return (Map<String, Object>) resultMap.get("data");
        } catch (Exception e) {
            throw new BusinessException(RspCode.ORDER_ERROE, "库存扣减失败，系统通讯异常");
        }
    }

    /**
     * 贝壳支付退款
     *
     * @param orderNo 订单编号
     * @return Map<String, Object>消费者Map
     * @throws BusinessException 业务异常
     */
    public static List<Map<String, Object>> beikeRefund(String orderNo) throws BusinessException {
        Map<String, String> reqParams = Maps.newHashMap();
        Map<String, Object> bizParams = Maps.newHashMap();
        reqParams.put("service", "beikeRefund");
        bizParams.put("orderNo", orderNo);
        reqParams.put("params", FastJsonUtil.toJson(bizParams));
        try {
            String resultStr = HttpClientUtil.postShort(STREET_FINANCE_SERVICE_URL, reqParams);
            Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
            if (!RspCode.RESPONSE_SUCC.equals(resultMap.get("rsp_code").toString())) {
                throw new BusinessException(resultMap.get("error_code").toString()
                        , resultMap.get("error_msg").toString());
            }
            return (List<Map<String, Object>>) resultMap.get("data");
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "贝壳支付退款失败，系统通讯异常");
        }
    }

}
