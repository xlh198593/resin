package com.meitianhui.order.street.handler.impl;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;
import com.meitianhui.common.util.DocUtil;
import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.order.street.consts.ServiceName;
import com.meitianhui.order.street.dto.BkcqLocalOrderQueryDTO;
import com.meitianhui.order.street.entity.BkcqLocalOrder;
import com.meitianhui.order.street.handler.BaseServiceHandler;
import com.meitianhui.order.street.service.BkcqLocalOrderService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 根据订单编号查询街市订单业务请求接口
 *
 * @author tortoise
 * @since 2019/3/27 16:40
 */
@SuppressWarnings({"Duplicates", "unchecked"})
@Component
public class QueryStreetOrderServiceHandler extends BaseServiceHandler {

    private static final Logger logger = Logger.getLogger(FindStreetOrderByOrderNoServiceHandler.class);

    @Autowired
    private BkcqLocalOrderService bkcqLocalOrderService;

    @Autowired
    private DocUtil docUtil;

    @Override
    public ServiceName getServiceName() {
        return ServiceName.QUERY_STREET_ORDER;
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
        BkcqLocalOrderQueryDTO bkcqLocalOrderCreateDTO = validate(paramsMap, BkcqLocalOrderQueryDTO.class);

        PageInfo<BkcqLocalOrder> bkcqLocalOrders = bkcqLocalOrderService.pageByParams(bkcqLocalOrderCreateDTO);
        resetOrderDesc(bkcqLocalOrders.getList());
        Map<String, Object> page = Maps.newHashMap();
        page.put("page_no", bkcqLocalOrders.getPageNum());
        page.put("page_size", bkcqLocalOrders.getPageSize());
        page.put("total_count", bkcqLocalOrders.getTotal());
        page.put("total_page", bkcqLocalOrders.getPages());
        Map<String, Object> data = Maps.newHashMap();
        data.put("page", page);
        data.put("orders", bkcqLocalOrders.getList());
        result.setResultData(data);
    }

    /**
     * 重置订单描述
     *
     * @param bkcqLocalOrders BkcqLocalOrder 订单实体集合
     */
    private void resetOrderDesc(List<BkcqLocalOrder> bkcqLocalOrders) {
        //获取商品图片地址
        Set<String> pathIds = Sets.newHashSet();
        if (null != bkcqLocalOrders && !bkcqLocalOrders.isEmpty()) {
            String desc1 = "";
            try {
                for (BkcqLocalOrder bkcqLocalOrder : bkcqLocalOrders) {
                    desc1 = bkcqLocalOrder.getDesc1();
                    if (StrUtil.isNotBlank(desc1)) {
                        List<Map<String, Object>> list = FastJsonUtil.jsonToList(desc1);
                        if (null != list && !list.isEmpty()) {
                            for (Map<String, Object> product : list) {
                                pathIds.addAll(getPathIds(product));
                            }
                        }
                    }
                }
            } catch (SystemException e) {
                logger.warn(String.format("订单desc1异常，异常信息[%S]", desc1));
            }

            //开始转换
            Map<String, Object> pathMap = docUtil.imageUrlFind(new ArrayList<>(pathIds));
            try {
                for (BkcqLocalOrder bkcqLocalOrder : bkcqLocalOrders) {
                    desc1 = bkcqLocalOrder.getDesc1();
                    if (StrUtil.isNotBlank(desc1)) {

                        List<Map<String, Object>> list = FastJsonUtil.jsonToList(desc1);
                        if (null != list && !list.isEmpty()) {
                            for (Map<String, Object> product : list) {
                                assembleDescPicUrl(product, pathMap);
                            }

                        }
                        bkcqLocalOrder.setDesc1(FastJsonUtil.toJson(list));
                    }
                }
            } catch (SystemException e) {
                logger.warn(String.format("订单desc1异常，异常信息[%S]", desc1));
            }
        }
    }

    /**
     * 获取PathId
     *
     * @param product 产品简化实体
     * @return List<String> 获取PathId集合
     * @throws SystemException 系统异常
     */
    private List<String> getPathIds(Map<String, Object> product) throws SystemException {
        List<String> pathIds = Lists.newArrayList();
        String picInfo = product.get("picInfo").toString();
        if (StrUtil.isNotBlank(picInfo)) {
            List<Map<String, Object>> picList = FastJsonUtil.jsonToList(picInfo);
            for (Map<String, Object> stringObjectMap : picList) {
                if (null != stringObjectMap.get("path_id")) {
                    pathIds.add(stringObjectMap.get("path_id").toString());
                }
            }
        }
        return pathIds;
    }

    /**
     * 转换商品图片
     *
     * @param product 商品简化实体
     * @param pathMap 转化后的URLMap
     * @throws SystemException 系统异常
     */
    @SuppressWarnings({"unchecked"})
    private void assembleDescPicUrl(Map<String, Object> product, Map<String, Object> pathMap) throws SystemException {
        String picInfo = product.get("picInfo").toString();
        if (StrUtil.isNotBlank(picInfo)) {
            List<Map<String, Object>> picList = FastJsonUtil.jsonToList(picInfo);
            for (Map<String, Object> stringObjectMap : picList) {
                if (null != stringObjectMap.get("path_id")) {
                    stringObjectMap.put("path_id", pathMap.get(stringObjectMap.get("path_id").toString()));
                }
            }
            product.put("picInfo", FastJsonUtil.toJson(picList));
        }
    }

}
