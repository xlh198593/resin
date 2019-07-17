package com.meitianhui.goods.street.handler.impl;

import com.meitianhui.common.constant.PageParam;
import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;
import com.meitianhui.common.util.*;
import com.meitianhui.goods.constant.RspCode;
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
import java.util.*;

/**
 *根据城市查询首页推荐商品列表
 *
 * @author tortoise
 * @since 2019/3/27 20:40
 */
@Component
@Slf4j
public class FindProductsByCityServiceHandler implements ServiceHandler {

    @Autowired
    private BkcqProductsDAO bkcqProductsDAO;

    @Autowired
    private RecommendedProductService recommendedProductService;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public ServiceName getServiceName() {
        return ServiceName.FIND_PRODUCTS_BY_CITY;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void handle(Map<String, Object> paramsMap, ResultData result) throws BusinessException,SystemException,Exception {

        ValidateUtil.validateParams(paramsMap, new String[]{"area_id"});

        PageParam pageParam = (PageParam)paramsMap.get("pageParam");

        Integer page_no = (pageParam.getPage_no()-1)*pageParam.getPage_size();
        Integer page_size = pageParam.getPage_size();
        String key = "FindProductsByCity_"+paramsMap.get("area_id").toString()+"_"+page_no+"_"+"_"+page_size+"_";
        Object obj = redisUtil.getObj(key);
        if (null != obj) {
            result.setResultData(obj);
            return;
        }

        String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
        Map<String, Object> productsListMap = new HashMap<>();//返回结果封装

        //1.根据城市area_id获取区域的areaIdList
        List<String> areaIdList =  recommendedProductService.findAreaIdListByParentId(paramsMap.get("area_id").toString());
        if(CollectionUtils.isEmpty(areaIdList)){
            throw new BusinessException(RspCode.AREA_NO_EXIT_ERROR,RspCode.MSG.get(RspCode.AREA_NO_EXIT_ERROR));
        }

        log.info("该市所有区域id:{}", areaIdList.toString());

        //2.获取该市内所有商品,按销量排序,分页读取
        List<Map<String, Object>> productList = bkcqProductsDAO.findProducsByCityOrderBySales(areaIdList,page_no, page_size,new Date());
        if (CollectionUtils.isEmpty(productList)) {
            productsListMap.put("productList", productList);
            pageParam.setTotal_count(0);
            pageParam.setTotal_page(0);
            productsListMap.put("page",pageParam);
            result.setResultData(productsListMap);
            return;
        }
            log.info("获取附近门店的商品:{}",productList.toString());

        //3.从商品列表中获取商品的门店storeIdList
        List<String> storeIdList = ProductsCollectionUtils.getStoreIdList(productList);
       
        if(CollectionUtils.isEmpty(storeIdList)){
            throw new BusinessException(RspCode.STORE_NO_EXIT_ERROR,RspCode.MSG.get(RspCode.STORE_NO_EXIT_ERROR));
        }

        //4.获取门店信息
        List<Map<String, Object>> storeList = recommendedProductService.findStroesInIdList(storeIdList);
        if (CollectionUtils.isEmpty(storeList)) {//没有查询到附近商家时直接返回
            productsListMap.put("productList", productList);
            pageParam.setTotal_count(0);
            pageParam.setTotal_page(0);
            productsListMap.put("page",pageParam);
            result.setResultData(productsListMap);
            return;
        }

        //5.获取整个列表的图片信息
        List<String> docIds = new ArrayList<>();
        for (Map<String, Object> e : storeList) {
            docIds.add(e.get("store_head_pic_path")==null?null:e.get("store_head_pic_path").toString());
        }

        for (Map<String, Object> e : productList) {
            docIds.add( e.get("pic_info")==null?null:e.get("pic_info").toString());
        }
        log.info("docIds:{}",docIds.toString());
        Map<String,Object> picMap = recommendedProductService.getPicUrlList(docIds);
        log.info("picMap:{}",picMap.toString());

        //6.将店铺信息封装进商品列表
        productList = ProductsCollectionUtils.formProductionList(productList,storeList,picMap);

        //7.查询总条数
        Long count = bkcqProductsDAO.getProducsByCityOrderBySalesCount(areaIdList,new Date());
        Integer totalCount = Integer.parseInt(count==null?"0":count.toString());
        pageParam.setTotal_count(totalCount);
        if(totalCount%pageParam.getPage_size()==0){
            pageParam.setTotal_page(totalCount/pageParam.getPage_size());
        }else {
            pageParam.setTotal_page(totalCount/pageParam.getPage_size()+1);
        }

        productsListMap.put("productList", productList);
        productsListMap.put("page",pageParam);
        result.setResultData(productsListMap);
        // 设置缓存 120秒缓存
        redisUtil.setObj(key, result.getResultData(), 10);//上线前换回来
    }

    public PageParam getPageParam(HttpServletRequest request) throws BusinessException, SystemException, Exception {
        PageParam pageParam = new PageParam();
        String page = request.getParameter("page");
        if (page == null) {
            return null;
        } else {
            Map<String, Object> pageMap = FastJsonUtil.jsonToMap(page);
            String page_no = StringUtil.formatStr(pageMap.get("page_no"));
            if (!"".equals(page_no)) {
                pageParam.setPage_no(Integer.parseInt(page_no));
            } else {
                pageParam.setPage_no(1);
            }

            String page_size = StringUtil.formatStr(pageMap.get("page_size"));
            if (!"".equals(page_size)) {
                pageParam.setPage_size(Integer.parseInt(page_size));
            }

            return pageParam;
        }
    }
}
