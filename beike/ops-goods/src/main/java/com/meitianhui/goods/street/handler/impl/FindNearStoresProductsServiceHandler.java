package com.meitianhui.goods.street.handler.impl;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;
import com.meitianhui.common.util.*;
import com.meitianhui.goods.constant.RspCode;
import com.meitianhui.goods.street.consts.ServiceName;
import com.meitianhui.goods.street.dao.BkcqProductsDAO;
import com.meitianhui.goods.street.handler.ServiceHandler;
import com.meitianhui.goods.street.service.RecommendedProductService;
import com.meitianhui.goods.street.util.PicUrlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 查询首页附近商家商品列表
 *
 * @author tortoise
 * @since 2019/3/27 20:40
 */
@Component
@Slf4j
public class FindNearStoresProductsServiceHandler implements ServiceHandler {


    @Autowired
    private RecommendedProductService recommendedProductService;

    @Override
    public ServiceName getServiceName() {
        return ServiceName.FIND_NEARBY_STORES_PRODUCTS;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void handle(Map<String, Object> paramsMap, ResultData result) throws BusinessException,SystemException,Exception {

        ValidateUtil.validateParams(paramsMap, new String[]{"longitude", "latitude"});

        if(paramsMap.get("area_id")==null){
            recommendedProductService.findNearbyStoresProducts(paramsMap,result);
        }else {
            recommendedProductService.findProductsBySalesVolume(paramsMap,result);
        }

    }


}
