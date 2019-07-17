package com.meitianhui.goods.street.dao;

import com.meitianhui.goods.street.entity.BkcqProducts;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <pre> 街市商品数据库操作 </pre>
 *
 * @author tortoise
 * @since 2019/3/28 15:13
 */
public interface BkcqProductsDAO {

    /**
     * 根据主键查询
     *
     * @param goodsId 商品ID
     * @return 街市商品记录
     */
    BkcqProducts selectByPrimaryKey(Long goodsId);

    /**
     * 根据商品编码查询
     *
     * @param goodsCode 商品编码
     * @return 街市商品记录
     */
    BkcqProducts selectByGoodsCode(String goodsCode);

    /**
     * 获取附近商家的热销商品
     * @param storesList
     * @return
     */
    List<Map<String,Object>> findNearStoresProducts(@Param("list") List<String> storesList, @Param("currentDate")Date currentDate);

    /**
     *查询某个城市商品按销量排序
     * @param areaIdList
     * @return
     */
    List<Map<String,Object>> findProducsByCityOrderBySales(@Param("list") List<String> areaIdList, @Param("limit") Integer limit, @Param("offset") Integer offset, @Param("currentDate")Date currentDate);

    /**
     * 查询某个城市商品按销量排序 总数
     * @param areaIdList
     * @return
     */
    Long getProducsByCityOrderBySalesCount(@Param("list") List<String> areaIdList, @Param("currentDate")Date currentDate);


}