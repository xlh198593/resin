package com.meitianhui.goods.street.service;


import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;

import java.util.List;
import java.util.Map;

/**
 * 贝壳街推荐商品服务接口
 */
public interface RecommendedProductService {

    /**
     * 根据areaCode查询areaId
     * @param areaCode
     * @return
     */
    public String findAreaIdByAreaCode(String areaCode) throws BusinessException,Exception;

    /**
     * 根据areaId查询该区域的所有商品 按销量排序
     */
    public List<Map<String,Object>> findProducsByCityOrderBySales(List<String> areaList, Integer pageNo, Integer pageSize) throws  BusinessException,Exception;


    /**
     *根据门店idList查询门店信息,经纬度计算距离
     */
    public List<Map<String,Object>> findStroesDistanceInIdList(List<String> storeIdList, Double longitude, Double latitude) throws  BusinessException,Exception;

    /***
     *调用common服务获取图片真实路径
     */
    public Map<String,Object> getPicUrlList(List<String> picUrlList) throws SystemException;

    /***
     * 获取离我最近商品列表
     */
    public void findNearbyStoresProducts(Map<String, Object> paramsMap, ResultData result) throws BusinessException,SystemException,Exception;


    /**
     *获取首页商品推荐按销量查询
     */
    public void findProductsBySalesVolume(Map<String, Object> paramsMap, ResultData result) throws BusinessException,SystemException,Exception;


    /**
     * 根据父级id查询下级区域的arraIdList
     * @param areaId
     * @return
     */
    public List<String> findAreaIdListByParentId(String areaId)throws BusinessException,SystemException,Exception;


    /**
     * 获取门店信息不带距离信息
     */
    public List<Map<String,Object>> findStroesInIdList(List<String> storeIdList) throws  BusinessException,Exception;

}
