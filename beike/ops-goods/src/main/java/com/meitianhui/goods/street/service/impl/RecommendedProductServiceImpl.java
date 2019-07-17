package com.meitianhui.goods.street.service.impl;

import com.meitianhui.common.constant.PageParam;
import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;
import com.meitianhui.common.util.*;
import com.meitianhui.goods.constant.RspCode;
import com.meitianhui.goods.service.GdAppAdvertService;
import com.meitianhui.goods.service.GoodsService;
import com.meitianhui.goods.street.dao.BkcqProductsDAO;
import com.meitianhui.goods.street.service.RecommendedProductService;
import com.meitianhui.goods.street.util.PicUrlUtil;
import com.meitianhui.goods.street.util.ProductsCollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
//import java.util.stream.Collectors;

@Slf4j
@Service
public class RecommendedProductServiceImpl implements RecommendedProductService{

    @Autowired
    private BkcqProductsDAO bkcqProductsDAO;

    @Autowired
    private DocUtil docUtil;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private GdAppAdvertService gdAppAdvertService;

    @Autowired
    private GoodsService goodsService;

    /**
     * 根据areaCode查询areaId
     * @param areaCode 城市的对应的areaCode
     * @return
     */
    @Override
    public String findAreaIdByAreaCode(String areaCode) throws BusinessException,Exception{
        //1.根据经纬度查询附近商家列表
        String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
        Map<String, String> reqParams = new HashMap<>();
        Map<String, Object> findMap = new HashMap<>();
        String resultStr = null;

        reqParams.put("service", "stores.street.findAreaIdByAreaCode");

        findMap.put("area_code",areaCode);

        reqParams.put("params", FastJsonUtil.toJson(findMap));

        Map<String, Object> resultMap = null;

        resultStr = HttpClientUtil.postShort(member_service_url, reqParams);

        if (StringUtils.isEmpty(resultStr)) {
            throw new BusinessException(RspCode.RESPONSE_DATA_ERROR, RspCode.MSG.get(RspCode.RESPONSE_DATA_ERROR));
        }

        resultMap = FastJsonUtil.jsonToMap(resultStr);

        if (!(resultMap.get("rsp_code")+"").equals(RspCode.RESPONSE_SUCC)) {
            throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
        }

        if (resultMap.get("data") == null) {
            throw new BusinessException(RspCode.RESPONSE_DATA_ERROR, RspCode.MSG.get(RspCode.RESPONSE_DATA_ERROR));
        }

        return resultMap.get("data").toString();
    }

    /**
     * 根据areaId查询该区域的所有商品 按销量排序
     * @param areaIdList 所查询的区域area_id集合
     */
    @Override
    public List<Map<String, Object>> findProducsByCityOrderBySales(List<String> areaIdList,Integer pageNo,Integer pageSize) throws BusinessException, Exception {
        List<Map<String, Object>>  productList = bkcqProductsDAO.findProducsByCityOrderBySales(areaIdList,pageNo,pageSize,new Date());
        return productList;
    }

    /**
     * 根据门店idList查询门店信息包括距离信息
     * @param storeIdList 查询的门店stores_id的集合
     * @param longitude 用户当前的经度
     * @param latitude 用户当前的纬度
     */
    @Override
    public List<Map<String, Object>> findStroesDistanceInIdList(List<String> storeIdList, Double longitude, Double latitude) throws BusinessException, Exception {
        //1.根据经纬度查询附近商家列表
        String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
        Map<String, String> reqParams = new HashMap<String, String>();
        Map<String, Object> findMap = new HashMap<String, Object>();
        String resultStr = null;

        reqParams.put("service", "stores.street.findStroesDistanceInIdList");

        findMap.put("storeIdList",storeIdList);
        findMap.put("longitude",longitude);
        findMap.put("latitude",latitude);

        reqParams.put("params", FastJsonUtil.toJson(findMap));

        Map<String, Object> resultMap = null;

        List<Map<String, Object>> storeList = null;
        log.info("根据门店idList查询门店信息包括距离信息,请求参数:{}",FastJsonUtil.toJson(findMap).toString());
        resultStr = HttpClientUtil.postShort(member_service_url, reqParams);

        if (StringUtils.isEmpty(resultStr)) {
            throw new BusinessException(RspCode.RESPONSE_DATA_ERROR, RspCode.MSG.get(RspCode.RESPONSE_DATA_ERROR));
        }

        resultMap = FastJsonUtil.jsonToMap(resultStr);

        if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
            throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
        }

        if (resultMap.get("data") == null) {
            throw new BusinessException(RspCode.RESPONSE_DATA_ERROR, RspCode.MSG.get(RspCode.RESPONSE_DATA_ERROR));
        }

        storeList = ( List<Map<String, Object>>)resultMap.get("data");

        return storeList;
    }

    /***
     *调用common服务获取图片真实路径
     * @param picUrlList 数据库存储的图片信息的集合
     * @return path_id 图片MD5加密码合集
     */
    public Map<String,Object> getPicUrlList(List<String>  picUrlList) throws SystemException {

        List<String> docIdList = new ArrayList<>();

        if (CollectionUtils.isEmpty(picUrlList)) {
            return null;
        }

        for (String picUrl : picUrlList) {
            if(CollectionUtils.isNotEmpty(PicUrlUtil.getPicUrlId(picUrl))){
                docIdList.addAll(PicUrlUtil.getPicUrlId(picUrl));
            }
        }

        log.info("docIdList:{}",docIdList);
        return docUtil.imageUrlFind(docIdList);
    }

    /***
     * 获取离我最近商品列表
     *
     */
    @Override
    public void findNearbyStoresProducts(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception {

        Map<String, Object> productsListMap = new HashMap<>();//返回结果封装

        PageParam pageParam = (PageParam)paramsMap.get("pageParam");

        Integer limit = (pageParam.getPage_no()-1)*pageParam.getPage_size();
        Integer offset = pageParam.getPage_size();

        //1.根据经纬度查询附近商家列表
        String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
        Map<String, String> reqParams = new HashMap<String, String>();
        Map<String, Object> findMap = new HashMap<String, Object>();
        String resultStr = null;
        reqParams.put("service", "stores.consumer.getNearbyStores");
        findMap.put("longitude", paramsMap.get("longitude"));
        findMap.put("latitude", paramsMap.get("latitude"));
        findMap.put("limit", limit);
        findMap.put("offset", offset);
        findMap.put("distance",paramsMap.get("distance"));
        reqParams.put("params", FastJsonUtil.toJson(findMap));

        Map<String, Object> resultMap = null;
        List<Map<String, Object>> productList = new ArrayList<>();

        log.info("根据经纬度查询附近商家列表,请求参数:{}",FastJsonUtil.toJson(findMap));

        resultStr = HttpClientUtil.postShort(member_service_url, reqParams);

        if (StringUtils.isEmpty(resultStr)) {
            log.error("根据经纬度查询附近商家列表返回null");
            throw new BusinessException(RspCode.RESPONSE_DATA_ERROR, RspCode.MSG.get(RspCode.RESPONSE_DATA_ERROR));
        }

        resultMap = FastJsonUtil.jsonToMap(resultStr);

        if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
            throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
        }

        if (resultMap.get("data") == null) {//没有查询到门店信息
            productsListMap.put("productList", productList);
            result.setResultData(productsListMap);
            return;
        }

        List<Map<String, Object>> storesMapList = (List<Map<String, Object>>) resultMap.get("data");

        //获取门店id列表
        List<String> storeIdList = new ArrayList<>();
        for (Map<String, Object> e : storesMapList) {
            storeIdList.add( e.get("stores_id")==null?null: e.get("stores_id").toString());
        }
        log.info("获取附近门店信息:{}", storesMapList.toString());
        log.info("获取附近门店的id:{}", storeIdList.toString());

        productList = bkcqProductsDAO.findNearStoresProducts(storeIdList,new Date());
        if (CollectionUtils.isEmpty(productList)) {
            productsListMap.put("productList", productList);
            result.setResultData(productsListMap);
            return;
        }

        log.info("获取附近门店的商品:{}",productList.toString());




        List<Map<String, Object>> storeList = null;
        Map<String, Object> store = null;
        String store_head_pic_path = null;//门店客服头像
        String pic_info = null;//商品图片

        List<String> docIds = new ArrayList<>();
        for (Map<String, Object> e : storesMapList) {
            docIds.add(e.get("store_head_pic_path")==null?null:e.get("store_head_pic_path").toString());
        }

        for (Map<String, Object> e : productList) {
            docIds.add(e.get("pic_info")==null?null:e.get("pic_info").toString());
        }

        Map<String,Object> map = getPicUrlList(docIds);

        //6.将店铺信息封装进商品列表
        productList = ProductsCollectionUtils.formProductionList(productList,storesMapList,map);

//        for (Map<String, Object> product : productList) {
//
//            if (product.get("supplier_id") != null) {//匹配商品所在的门店
//                for (Map<String, Object> e : storesMapList) {
//                    if(e.get("stores_id").toString().equals(product.get("supplier_id").toString())){
//                        store = e;
//                    }
//                }
//            }
//
//            if (store!= null) {//将门店信息加入商品信息中 storeList.get(0) != null
//                product.put("distance", store.get("distance"));
//                store_head_pic_path = StringUtil.formatStr(store.get("store_head_pic_path"));
//                product.put("store_head_pic_path",PicUrlUtil.getRealPicUrl(map,store_head_pic_path));
//                product.put("stores_name", store.get("stores_name") == null ? "" : store.get("stores_name"));
//            }
//
//            pic_info = StringUtil.formatStr(product.get("pic_info"));//推荐商品图片
//            product.put("pic_info", PicUrlUtil.getRealPicUrl(map,pic_info));//图片解析重新封装
//        }

        //讲商品根据商家距离进行重排序
        Collections.sort(productList, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                return Integer.parseInt(o1.get("distance").toString()) - Integer.parseInt(o2.get("distance").toString());
            }
        });

        productsListMap.put("productList", productList);
        result.setResultData(productsListMap);
    }

    /**
     *获取首页商品推荐按销量查询
     * @param paramsMap area_id 查询城市的区域id
     */
    @Override
    public void findProductsBySalesVolume(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception {

        //1.根据area_code查询area_id  按区查询
//        String areaId = findAreaIdByAreaCode(paramsMap.get("area_code").toString());
//
//        if(StringUtil.isEmpty(areaId)){
//            throw new BusinessException(RspCode.AREA_NO_EXIT_ERROR,RspCode.MSG.get(RspCode.AREA_NO_EXIT_ERROR));
//        }

        PageParam pageParam = (PageParam)paramsMap.get("pageParam");

        Integer limit = (pageParam.getPage_no()-1)*pageParam.getPage_size();
        Integer offset = pageParam.getPage_size();

        String area_id = paramsMap.get("area_id").toString();

        //先判断缓存中是否存在
        String key = "findProductsBySalesVolume_"+area_id+"_"+limit+"_"+""+offset+"";
        Object obj = redisUtil.getObj(key);
        if (null != obj) {
            result.setResultData(obj);
            return;
        }

        Map<String,Object> productsListMap = new HashMap<>();
        List<String> areaIdList =  findAreaIdListByParentId(area_id);

        if(CollectionUtils.isEmpty(areaIdList)){
            throw new BusinessException(RspCode.AREA_NO_EXIT_ERROR,RspCode.MSG.get(RspCode.AREA_NO_EXIT_ERROR));
        }
        log.info("查询该市下所有区idList:{}",areaIdList.toString());
        //2.查询该区中按销量排序的商品列表
        List<Map<String,Object>> productList = findProducsByCityOrderBySales(areaIdList,limit,offset);
        if(CollectionUtils.isEmpty(productList)){
            getNullProducts(pageParam,result);
            return;
        }

        log.info("查询该区中按销量排序的商品列表:{}",productList.toString());
        //3.从商品列表中获取商品的店铺storeIdList
        List<String> storeIdList = ProductsCollectionUtils.getStoreIdList(productList);
        if(CollectionUtils.isEmpty(storeIdList)){
            throw new BusinessException(RspCode.STORE_NO_EXIT_ERROR,RspCode.MSG.get(RspCode.STORE_NO_EXIT_ERROR));
        }

        log.info("从商品列表中获取商品的店铺storeIdList:{}",storeIdList.toString());
        //4.根据店铺id获取店铺信息(包括距离信息)
        List<Map<String,Object>> storeList = findStroesDistanceInIdList(storeIdList,Double.parseDouble(paramsMap.get("longitude").toString()),
                Double.parseDouble(paramsMap.get("latitude").toString()));

        if(CollectionUtils.isEmpty(storeList)){
            throw new BusinessException(RspCode.STORE_NO_EXIT_ERROR,RspCode.MSG.get(RspCode.STORE_NO_EXIT_ERROR));
        }
        log.info("根据店铺id获取店铺信息:{}",storeList.toString());
        //5.获取整个列表的图片信息
        List<String> docIds = new ArrayList<>();

        for (Map<String, Object> e : storeList) {
            docIds.add(e.get("store_head_pic_path")==null?null:e.get("store_head_pic_path").toString());
        }
        for (Map<String, Object> e : productList) {
            docIds.add(e.get("pic_info")==null?null:e.get("pic_info").toString());
        }
        Map<String,Object> picMap = getPicUrlList(docIds);
        //6.将店铺信息封装进商品列表
        productList = ProductsCollectionUtils.formProductionList(productList,storeList,picMap);
        log.info("商品列表,排序前:{}",productList.toString());
        //7.按距离排序
        ProductsCollectionUtils.getProductsOrderByDistance(productList);

        Long count = bkcqProductsDAO.getProducsByCityOrderBySalesCount(areaIdList,new Date());
        Integer totalCount = Integer.parseInt(count==null?"0":count.toString());
        pageParam.setTotal_count(totalCount);

        if(totalCount%pageParam.getPage_size()==0){
            pageParam.setTotal_page(totalCount/pageParam.getPage_size());
        }else {
            pageParam.setTotal_page(totalCount/pageParam.getPage_size()+1);
        }

        productsListMap.put("page",pageParam);
        productsListMap.put("productList",productList);
        result.setResultData(productsListMap);

        // 设置缓存 60秒缓存
        redisUtil.setObj(key, result.getResultData(), 30);

    }

    /**
     * 根据父级id查询下级区域的areaIdList
     * @param areaId 父级区域id
     * @return
     */
    @Override
    public List<String> findAreaIdListByParentId(String areaId) throws BusinessException, SystemException, Exception {
        String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
        Map<String, String> reqParams = new HashMap<String, String>();
        Map<String, Object> findMap = new HashMap<String, Object>();
        String resultStr = null;

        reqParams.put("service", "stores.street.getAreaByParentId");

        findMap.put("parent_area_id",areaId);

        reqParams.put("params", FastJsonUtil.toJson(findMap));

        Map<String, Object> resultMap = null;

        List<Map<String, Object>> productList = new ArrayList<>();

        log.info("请求市地下所有区的area_id,请求参数:{}",FastJsonUtil.toJson(findMap));

        resultStr = HttpClientUtil.postShort(member_service_url, reqParams);

        if (StringUtils.isEmpty(resultStr)) {
            log.error("stores.street.getAreaByParentId返回null");
            throw new BusinessException(RspCode.RESPONSE_DATA_ERROR, RspCode.MSG.get(RspCode.RESPONSE_DATA_ERROR));
        }

        resultMap = FastJsonUtil.jsonToMap(resultStr);

        if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
            throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
        }

        if (resultMap.get("data") == null) {
            throw new BusinessException(RspCode.RESPONSE_DATA_ERROR, RspCode.MSG.get(RspCode.RESPONSE_DATA_ERROR));
        }

        List<String> areaIdList = (List<String>) resultMap.get("data");

        return areaIdList;
    }


    /**
     * 获取门店信息不带距离信息
     * @param storeIdList 门店stores_id 集合
     */
    @Override
    public List<Map<String, Object>> findStroesInIdList(List<String> storeIdList) throws BusinessException, Exception {
        String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
        Map<String, String> reqParams = new HashMap<String, String>();
        Map<String, Object> findMap = new HashMap<String, Object>();
        String resultStr = null;
        reqParams.put("service","stores.street.findStroesInIdList");
        findMap.put("storeIdList",storeIdList);
        reqParams.put("params", FastJsonUtil.toJson(findMap));

        resultStr = HttpClientUtil.postShort(member_service_url, reqParams);
        log.info("获取门店的信息:{}",resultStr);
        if (StringUtils.isEmpty(resultStr)) {
            log.error("FindNearStoresProductsServiceHandler请求stores.consumer.getNearbyStores返回null");
            throw new BusinessException(RspCode.RESPONSE_DATA_ERROR, RspCode.MSG.get(RspCode.RESPONSE_DATA_ERROR));
        }

        Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);

        if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
            throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
        }

        if (resultMap.get("data") == null) {
            throw new BusinessException(RspCode.RESPONSE_DATA_ERROR, RspCode.MSG.get(RspCode.RESPONSE_DATA_ERROR));
        }

        List<Map<String, Object>> storeList = (List<Map<String,Object>>) resultMap.get("data");
        return storeList;
    }

    /**
     * 返回空的产品列表
     * @param pageParam 分页对象
     * @param result 返回结果
     */
    public void getNullProducts(PageParam pageParam,ResultData result){
        Map<String,Object> productsListMap = new HashMap();

        productsListMap.put("productList", new ArrayList<>());
        pageParam.setTotal_count(0);
        pageParam.setTotal_page(0);
        productsListMap.put("page",pageParam);
        result.setResultData(productsListMap);
    }


}
