package com.meitianhui.order.street.handler.impl;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;
import com.meitianhui.common.util.DocUtil;
import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.order.constant.RspCode;
import com.meitianhui.order.street.consts.ServiceName;
import com.meitianhui.order.street.entity.BkcqLocalOrder;
import com.meitianhui.order.street.entity.BkcqLocalOrderItem;
import com.meitianhui.order.street.handler.BaseServiceHandler;
import com.meitianhui.order.street.service.BkcqLocalOrderService;
import com.meitianhui.order.street.utils.HttpRequestUtils;
import com.meitianhui.order.street.utils.StreetUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 根据订单编号查询街市订单业务请求接口
 *
 * @author tortoise
 * @since 2019/3/27 16:40
 */
@SuppressWarnings({"Duplicates", "unchecked"})
@Component
public class FindStreetOrderByOrderNoServiceHandler extends BaseServiceHandler {

    private static final Logger logger = Logger.getLogger(FindStreetOrderByOrderNoServiceHandler.class);

    @Autowired
    private BkcqLocalOrderService bkcqLocalOrderService;

    @Autowired
    private DocUtil docUtil;

    @Override
    public ServiceName getServiceName() {
        return ServiceName.FIND_STREET_ORDER_BY_ORDER_NO;
    }

    /**
     * 处理业务
     *
     * @param paramsMap 请求参数
     * @param result    返回结果回写
     * @throws BusinessException 业务异常
     */
    @Override
    public void handle(Map<String, Object> paramsMap, ResultData result) throws BusinessException {
        Object orderNoObj = paramsMap.get("orderNo");
        if (null == orderNoObj) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "订单编号不能为空");
        }

        BkcqLocalOrder bkcqLocalOrder = bkcqLocalOrderService.findOrderByNo(orderNoObj.toString());
        // resetOrderDesc(bkcqLocalOrder);

        Object isOnlyOrderObj = paramsMap.get("onlyOrder");
        boolean isOnlyOrder = false;
        if (null != isOnlyOrderObj) {
            isOnlyOrder = Boolean.valueOf(isOnlyOrderObj.toString());
        }

        if (isOnlyOrder) {
            result.setResultData(bkcqLocalOrder);
            return;
        }

        for (BkcqLocalOrderItem bkcqLocalOrderItem : bkcqLocalOrder.getItems()) {
            try {
                assembleItemPicUrl(bkcqLocalOrderItem);
            } catch (SystemException e) {
                logger.warn(String.format("订单明细图片异常，异常信息[%S]", FastJsonUtil.toJson(bkcqLocalOrderItem)));
            }

            Map<String, Object> product = HttpRequestUtils.findProductById(bkcqLocalOrderItem.getGoodsId().toString());
            bkcqLocalOrderItem.setProduct(getProduct(product));
        }

        //查询店铺信息
        Map<String, Object> stores = HttpRequestUtils.findStoresById(bkcqLocalOrder.getStoresId());
        String headPicPath = StreetUtils.getValue("head_pic_path", stores);
        if (StrUtil.isNotBlank(headPicPath)) {
            try {
                List<Map<String, Object>> mapList = FastJsonUtil.jsonToList(headPicPath);
                if(CollectionUtils.isEmpty(mapList)){
                    stores.put("head_pic_path","");
                }else {
                    Map<String, Object> map = mapList.get(0);
                    String path = docUtil.imageUrlFind(StreetUtils.getValue("path_id", map));
                    map.put("path_id", path);
                    stores.put("head_pic_path", FastJsonUtil.toJson(map));
                }

            } catch (SystemException e) {
                throw new BusinessException(RspCode.RESPONSE_FAIL, "店铺头像数据异常");
            }
        }

        //查询交易记录
        List<Map<String, Object>> transactions = HttpRequestUtils.findTransactionById(bkcqLocalOrder.getOrderNo());

        Map<String, Object> data = Maps.newHashMap();
        data.put("consumerId", bkcqLocalOrder.getConsumerId());
        data.put("beikeCredit", bkcqLocalOrder.getBeikeCredit());
        data.put("codeStr", bkcqLocalOrder.getCodeStr());
        data.put("codes", bkcqLocalOrder.getCodes());
        data.put("contactTel", bkcqLocalOrder.getContactTel());
        data.put("createdDate", bkcqLocalOrder.getCreatedDate());
        data.put("orderDate", bkcqLocalOrder.getOrderDate());
        data.put("expiredDate", bkcqLocalOrder.getExpiredDate());
        data.put("orderId", bkcqLocalOrder.getOrderId());
        data.put("orderNo", bkcqLocalOrder.getOrderNo());
        data.put("remark", bkcqLocalOrder.getRemark());
        data.put("saleFee", bkcqLocalOrder.getSaleFee());
        data.put("status", bkcqLocalOrder.getStatus());
        data.put("vipFee", bkcqLocalOrder.getVipFee());
        data.put("refundProcessRecord", bkcqLocalOrder.getRefundProcessRecord());
        data.put("itemNum", bkcqLocalOrder.getItemNum());
        data.put("storesId", bkcqLocalOrder.getStoresId());
        data.put("settledPrice", bkcqLocalOrder.getSettledPrice());
        data.put("stores", getStores(stores));
        data.put("transactions", getTransactions(transactions));
        data.put("items", getItems(bkcqLocalOrder.getItems()));

        result.setResultData(data);
    }

    /**
     * 获取商品信息
     *
     * @param product 原始商品
     * @return 组装后的交易
     */
    private Map<String, Object> getProduct(Map<String, Object> product) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("beikeCredit", product.get("beikeCredit"));
        map.put("desc1", product.get("desc1"));
        map.put("goodsId", product.get("goodsId"));
        map.put("marketPrice", product.get("marketPrice"));
        map.put("paymentWay", product.get("paymentWay"));
        map.put("picInfo", product.get("picInfo"));
        map.put("salePrice", product.get("salePrice"));
        map.put("saleQty", product.get("saleQty"));
        map.put("shippingFee", product.get("shippingFee"));
        map.put("specification", product.get("specification"));
        map.put("status", product.get("status"));
        map.put("title", product.get("title"));
        map.put("vipPrice", product.get("vipPrice"));
        map.put("perchaseNotice", product.get("perchaseNotice"));
        map.put("activityStartTime", product.get("activityStartTime"));
        map.put("ativityEndTime", product.get("ativityEndTime"));
        return map;
    }

    /**
     * 获取订单明细集合
     *
     * @param items 交易明细
     * @return 组装后的交易
     */
    private List<Map<String, Object>> getItems(List<BkcqLocalOrderItem> items) {
        List<Map<String, Object>> itemList = Lists.newArrayList();
        for (BkcqLocalOrderItem item : items) {
            Map<String, Object> map = Maps.newHashMap();
            map.put("itemStoreId", item.getItemStoreId());
            map.put("qty", item.getQty());
            map.put("product", item.getProduct());
            itemList.add(map);
        }
        return itemList;
    }

    /**
     * 获取交易集合
     *
     * @param transactions 原始交易
     * @return 组装后的交易
     */
    private List<Map<String, Object>> getTransactions(List<Map<String, Object>> transactions) {
        List<Map<String, Object>> transactionList = Lists.newArrayList();
        for (Map<String, Object> map : transactions) {
            if (StreetUtils.getValue("transactionStatus", map).equals("completed")) {
                Map<String, Object> transaction = Maps.newHashMap();
                transaction.put("amount", map.get("amount"));
                transaction.put("paymentWayKey", map.get("paymentWayKey"));
                transaction.put("transactionNo", map.get("transactionNo"));
                transaction.put("transactionStatus", map.get("transactionStatus"));
                transactionList.add(transaction);
            }
        }
        return transactionList;
    }

    /**
     * 获取店铺信息
     *
     * @param stores 店铺
     */
    private Map<String, Object> getStores(Map<String, Object> stores) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("address", stores.get("address"));
        map.put("contact_tel", stores.get("contact_tel"));
        map.put("latitude", stores.get("latitude"));
        map.put("longitude", stores.get("longitude"));
        map.put("stores_id", stores.get("stores_id"));
        map.put("stores_name", stores.get("stores_name"));
        map.put("head_pic_path", stores.get("head_pic_path"));
        return map;
    }

    /**
     * 重置订单描述
     *
     * @param bkcqLocalOrder BkcqLocalOrder 订单实体
     */
    private void resetOrderDesc(BkcqLocalOrder bkcqLocalOrder) {
        String desc1 = bkcqLocalOrder.getDesc1();
        if (StrUtil.isNotBlank(desc1)) {
            try {
                List<Map<String, Object>> list = FastJsonUtil.jsonToList(desc1);
                if (null != list && !list.isEmpty()) {
                    for (Map<String, Object> product : list) {
                        assembleDescPicUrl(product);
                    }
                }
                bkcqLocalOrder.setDesc1(FastJsonUtil.toJson(list));
            } catch (SystemException e) {
                logger.warn(String.format("订单desc1异常，异常信息[%S]", desc1));
            }
        }
    }

    /**
     * 转化订单明细图片
     *
     * @param item 订单明细
     * @throws SystemException 系统异常
     */
    @SuppressWarnings({"unchecked"})
    private void assembleItemPicUrl(BkcqLocalOrderItem item) throws SystemException {
        //转换商品图片地址
        List<String> pathIds = Lists.newArrayList();
        String picInfo = item.getImageInfo();
        List<Map<String, Object>> picList = Lists.newArrayList();
        if (StrUtil.isNotBlank(picInfo)) {
            picList = FastJsonUtil.jsonToList(picInfo);
            for (Map<String, Object> stringObjectMap : picList) {
                if (null != stringObjectMap.get("path_id")) {
                    pathIds.add(stringObjectMap.get("path_id").toString());
                }
            }
        }

        //开始转换
        Map<String, Object> pathMap = docUtil.imageUrlFind(pathIds);

        //重写商品图片地址
        if (!picList.isEmpty()) {
            for (Map<String, Object> stringObjectMap : picList) {
                if (null != stringObjectMap.get("path_id")) {
                    stringObjectMap.put("path_id", pathMap.get(stringObjectMap.get("path_id").toString()));
                }
            }
            item.setImageInfo(FastJsonUtil.toJson(picList));
        }
    }

    /**
     * 转换商品图片
     *
     * @param product 商品简化实体
     * @throws SystemException 系统异常
     */
    @SuppressWarnings({"unchecked", "Duplicates"})
    private void assembleDescPicUrl(Map<String, Object> product) throws SystemException {
        //转换商品图片地址
        List<String> pathIds = Lists.newArrayList();
        String picInfo = product.get("picInfo").toString();
        List<Map<String, Object>> picList = Lists.newArrayList();
        if (StrUtil.isNotBlank(picInfo)) {
            picList = FastJsonUtil.jsonToList(picInfo);
            for (Map<String, Object> stringObjectMap : picList) {
                if (null != stringObjectMap.get("path_id")) {
                    pathIds.add(stringObjectMap.get("path_id").toString());
                }
            }
        }

        //开始转换
        Map<String, Object> pathMap = docUtil.imageUrlFind(pathIds);

        //重写商品图片地址
        if (!picList.isEmpty()) {
            for (Map<String, Object> stringObjectMap : picList) {
                if (null != stringObjectMap.get("path_id")) {
                    stringObjectMap.put("path_id", pathMap.get(stringObjectMap.get("path_id").toString()));
                }
            }
            product.put("picInfo", FastJsonUtil.toJson(picList));
        }
    }

}
