package com.meitianhui.goods.street.handler.impl;

import com.meitianhui.common.constant.PageParam;
import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;
import com.meitianhui.common.util.*;
import com.meitianhui.goods.constant.RspCode;
import com.meitianhui.goods.service.GdAppAdvertService;
import com.meitianhui.goods.service.GoodsService;
import com.meitianhui.goods.street.consts.ServiceName;
import com.meitianhui.goods.street.dao.BkcqProductsDAO;
import com.meitianhui.goods.street.handler.ServiceHandler;
import com.meitianhui.goods.street.service.RecommendedProductService;
import com.meitianhui.goods.street.util.ProductsCollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *根据城市查询首页推荐商品列表
 *
 * @author tortoise
 * @since 2019/3/27 20:40
 */
@Component
@Slf4j
public class StreetHomeServiceHandler implements ServiceHandler {


    @Autowired
    private GdAppAdvertService gdAppAdvertService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public ServiceName getServiceName() {
        return ServiceName.STREET_HOME_INIT;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void handle(Map<String, Object> paramsMap, ResultData result) throws BusinessException,SystemException,Exception {

        ValidateUtil.validateParams(paramsMap, new String[]{"area_name"});

//        String key = "StreetHomeInit"+paramsMap.get("area_name").toString();
//        Object obj = redisUtil.getObj(key);
//        if (null != obj) {
//            result.setResultData(obj);
//            return;
//        }

        ValidateUtil.validateParams(paramsMap,new String[]{"area_name"});

        Map<String,Object> resultMap = new HashMap<>();

        Map<String,Object> findMap = new HashMap<>();

        //设置轮播图位置
        findMap.put("category","bkjsHome");

        try {
            //获取轮播图
            gdAppAdvertService.gdAppAdvertListForAppFind_V1(findMap, result);
            resultMap.put("gdAppAdvertList",result.getResultData());
        }catch (Exception e){
            resultMap.put("gdAppAdvertList",new HashMap<>());
            log.error("轮播图数据错误:{}",e);
        }

        findMap.clear();
        findMap.put("label","11");
        findMap.put("area_name",paramsMap.get("area_name"));
        findMap.put("page_no", 1);
        findMap.put("page_size", 6);

//        11贝壳专区 12立享五折
        try {
            goodsService.findGoodsByLabelAndAreaName(findMap, result);
            resultMap.put("bkZone",result.getResultData());
        }catch (Exception e){
            resultMap.put("bkZone",new HashMap<>());
            log.error("贝壳专区数据错误:{}",e);
        }

        findMap.put("label","12");
        try {
            goodsService.findGoodsByLabelAndAreaName(findMap, result);
            resultMap.put("halfOf",result.getResultData());
        }catch (Exception e){
            resultMap.put("halfOf",new HashMap<>());
            log.error("立享五折数据错误:{}",e);
        }

        result.setResultData(resultMap);

        // 设置缓存 120秒缓存
//        redisUtil.setObj("ljh", result.getResultData(), 101);//上线前换回来
//        redisUtil.setObj(key, "ljh", 101);
    }


}
