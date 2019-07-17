package com.meitianhui.goods.dao;

import java.util.List;
import java.util.Map;

import com.meitianhui.goods.entity.BeikeMallGoods;
import com.meitianhui.goods.entity.GdGoodsItem;
import com.meitianhui.goods.entity.GdViewSell;
import com.meitianhui.goods.entity.PsGoods;
import com.meitianhui.goods.entity.PsGoodsActivity;
import com.meitianhui.goods.entity.PsGoodsLog;
import com.meitianhui.goods.entity.PsGoodsSku;
import org.apache.ibatis.annotations.Param;

public interface PsGoodsDao {
	
	List<BeikeMallGoods>selectBeiKeGoodscode(Map<String, Object> map) throws Exception;
	
	int updateBeiKeGoodsedit(BeikeMallGoods beikeMallGoods);

    /**
     * 自营商品新增
     *
     * @param map
     * @throws Exception
     */
    void insertGdGoodsItem(GdGoodsItem gdHuiguoGoods) throws Exception;

    /**
     * 自营商品新增
     *
     * @param map
     * @throws Exception
     */
    void insertPsGoods(PsGoods psGoods) throws Exception;

    /**
     * 0元购商品新增
     *
     * @param map
     * @throws Exception
     */
    void insertFreePsGoods(PsGoods psGoods) throws Exception;


    /**
     * 自营商品SKU信息新增
     *
     * @param map
     * @throws Exception
     */
    void insertPsGoodsSku(PsGoodsSku psGoodsSku) throws Exception;

    /**
     * 每日抢活动
     *
     * @param map
     * @throws Exception
     */
    int insertPsGoodsActivity(PsGoodsActivity psGoodsActivity) throws Exception;

    /**
     * 新增商品查看销售统计记录
     *
     * @param map
     * @return
     * @throws Exception
     */
    void insertGdViewSell(GdViewSell gdViewSell) throws Exception;

    /**
     * 添加商品操作日志
     *
     * @param psGoodsLog
     * @throws Exception
     */
    void insertGoodsLog(PsGoodsLog psGoodsLog) throws Exception;


    /**
     * 会过商品信息
     *
     * @param map
     * @return
     * @throws Exception
     */
    List<GdGoodsItem> selectGdGoodsItem(Map<String, Object> map) throws Exception;

    /**
     * 查询预售商品信息
     *
     * @param map
     * @return
     * @throws Exception
     */
    List<PsGoods> selectPsGoods(Map<String, Object> map) throws Exception;

    /**
     * 查询预售商品信息
     *
     * @param map
     * @return
     * @throws Exception
     */
    List<PsGoods> selectFreePsGoods(Map<String, Object> map) throws Exception;

    /**
     * 查询预售商品信息
     *
     * @param map
     * @return
     * @throws Exception
     */
    List<PsGoods> selectUnionAllPsGoods(Map<String, Object> map) throws Exception;

    /**
     * 查询价格小于等于9.9已上架的商品
     * @return
     * @throws Exception
     */
    List<PsGoods> selectLess9PsGoodsList(Map<String, Object> map) throws Exception;

    /**
     * 查询价格小于等于9.9已上架的商品(新自营)
     */
    List<PsGoods> selectLess9PsGoodsListNewOwn(Map<String, Object> map) throws Exception;

    /**
     * @param map
     * @return
     * @throws Exception
     */
    List<PsGoods> selectPsGoodsAndFiterOffDay(Map<String, Object> map) throws Exception;

    /**
     * @param map
     * @return
     * @throws Exception
     */
    List<GdGoodsItem> selectGdGoodsItemAndFiterOffDay(Map<String, Object> map) throws Exception;

    /**
     * 查询供应商“已上架的“(领了么)商品总数
     *
     * @param map
     * @return
     * @throws Exception
     */
    Map<String, Object> selectPsGoodsSupplyCount(Map<String, Object> map) throws Exception;

    /**
     * 查询预售商品SKU信息
     *
     * @param map
     * @return
     * @throws Exception
     */
    List<PsGoodsSku> selectPsGoodsSku(Map<String, Object> map) throws Exception;

    /**
     * 运营系统查询最新的商品信息，按创建时间倒序(去除状态为删除的)
     *
     * @param map
     * @return
     * @throws Exception
     */
    List<PsGoods> selectNewestPsGoodsForOp(Map<String, Object> map) throws Exception;


    /**
     * 查售商品信息(app)
     *
     * @param map
     * @return
     * @throws Exception
     */
    List<PsGoods> selectPsGoodsForWeb(Map<String, Object> map) throws Exception;



    /**
     * 查询预售商品列表 店东助手端
     *
     * @param map
     * @return
     * @throws Exception
     */
    List<PsGoods> selectfreeGetGoodsPreSaleForStores(Map<String, Object> map) throws Exception;

    /**
     * 查询预售商品列表 领有惠端
     *
     * @param map
     * @return
     * @throws Exception
     */
    List<PsGoods> selectfreeGetGoodsPreSaleForH5(Map<String, Object> map) throws Exception;

    /**
     * 查询领了么预售商品信息(小程序端)
     *
     * @param map
     * @return
     * @throws Exception
     */
    List<PsGoods> selectfreeGetGoodsPreSaleForSmallProgram(Map<String, Object> map) throws Exception;

    /**
     * 根据标签查询淘淘领商品 小程序端
     *
     * @param map
     * @return
     * @throws Exception
     */
    List<PsGoods> selectfreeGetGoodsListByLabelForSmallProgram(Map<String, Object> map) throws Exception;

    /**
     * 根据标签查询淘淘领商品 APP端 会过
     *
     * @param map
     * @return
     * @throws Exception
     */
    List<PsGoods> selectfreeGetGoodsListByLabelForHuiguo(Map<String, Object> map) throws Exception;

    /**
     * 根据标签查询淘淘领商品 APP端 自营
     *
     * @param map
     * @return
     * @throws Exception
     */
    List<PsGoods> selectfreeGetGoodsListByLabelForOwn(Map<String, Object> map) throws Exception;



    /**
     * 根据标签查询商品列表 店东助手端
     *
     * @param map
     * @return
     * @throws Exception
     */
    List<PsGoods> selectfreeGetGoodsListByLabelForStores(Map<String, Object> map) throws Exception;

    /**
     * 根据标签查询商品列表 领有惠端
     *
     * @param map
     * @return
     * @throws Exception
     */
    List<PsGoods> selectfreeGetGoodsListByLabel(Map<String, Object> map) throws Exception;
    /**
     * 根据标签查询商品列表 领有惠端(0元购)
     */
    List<PsGoods> selectGdFreeGetGoodsListByLabel(Map<String, Object> map) throws Exception;

    /**
     * 根据标签查询商品列表 领有惠端 新接口
     *
     * @param map
     * @return
     * @throws Exception
     */
    List<PsGoods> selectfreeGetGoodsListByLabelTwo(Map<String, Object> map) throws Exception;

    /**
     * 根据标签查询商品列表 领有惠端 新接口(0元购)
     */
    List<PsGoods> selectGdFreeGetGoodsListByLabelTwo(Map<String, Object> map) throws Exception;

    /**
     * 查询淘淘领商品（根据搜索条件查询） 小程序端
     *
     * @param map
     * @return
     * @throws Exception
     */
    List<PsGoods> selectfreeGetGoodsListBySearchForSmallProgram(Map<String, Object> map) throws Exception;

    /**
     * 查询淘淘领商品（根据搜索条件查询）APP端
     *
     * @param map
     * @return
     * @throws Exception
     */
    List<PsGoods> selectfreeGetGoodsListBySearch(Map<String, Object> map) throws Exception;

    /**
     * 搜索查询淘淘领商品列表对应运营平台
     *
     * @param map
     * @return
     * @throws Exception
     */
    List<PsGoods> selectFgGoodsListForOperate(Map<String, Object> map) throws Exception;

    /**
     * 查询上新商品列表  店东助手端
     *
     * @param map
     * @return
     * @throws Exception
     */
    List<PsGoods> selectFgGoodsListByNewestForStores(Map<String, Object> map) throws Exception;

    /**
     * 查询上新商品列表  领有惠端
     *
     * @param map
     * @return
     * @throws Exception
     */
    List<PsGoods> selectFgGoodsListByNewest(Map<String, Object> map) throws Exception;

    /**
     * 会过商品查询 小程序端
     *
     * @param map
     * @return
     * @throws Exception
     */
    List<PsGoods> selectFgGoodsListByHuiguoForSmallProgram(Map<String, Object> map) throws Exception;

    /**
     * 自营商品查询 小程序端
     *
     * @param map
     * @return
     * @throws Exception
     */
    List<PsGoods> selectFgGoodsListByOwnForSmallProgram(Map<String, Object> map) throws Exception;

    /**
     * 查询商品列表 领有惠端
     *
     * @param map
     * @return
     * @throws Exception
     */
    List<PsGoods> selectFgGoodsListForH5(Map<String, Object> map) throws Exception;

    /**
     * 查询商品列表 小程序端
     *
     * @param map
     * @return
     * @throws Exception
     */
    List<PsGoods> selectFgGoodsListForSmallProgram(Map<String, Object> map) throws Exception;

    /**
     * 查询商品列表 店东助手端
     *
     * @param map
     * @return
     * @throws Exception
     */
    List<PsGoods> selectFgGoodsListForStores(Map<String, Object> map) throws Exception;

    /**
     * 淘淘领商品查询 APP端 会过
     *
     * @param map
     * @return
     * @throws Exception
     */
    List<PsGoods> selectFgGoodsListForHuiguo(Map<String, Object> map) throws Exception;

    /**
     * 淘淘领商品查询 APP端 自营
     *
     * @param map
     * @return
     * @throws Exception
     */
    List<PsGoods> selectFgGoodsListForOwn(Map<String, Object> map) throws Exception;

    /**
     * 淘淘领商品查询 APP端 自营
     */
    List<PsGoods> selectFgGoodsListForNewOwn(Map<String, Object> map) throws Exception;

    /**
     * 查询商品信息(H5)
     *
     * @param map
     * @return
     * @throws Exception
     */
    List<PsGoods> selectPsGoodsForH5(Map<String, Object> map) throws Exception;

    /**
     * 订单中goods_id对应的预售商品的基本信息查询
     *
     * @param map
     * @return
     * @throws Exception
     */
    Map<String, Object> selectPsGoodsForOrder(Map<String, Object> map) throws Exception;

    /**
     * 订单中goods_id对应的预售商品的基本信息查询(0元购)
     */
    Map<String, Object> selectGdFreeGetGoodsForOrder(Map<String, Object> map) throws Exception;

    /**
     * 伙拼团查询商品信息
     *
     * @param map
     * @return
     * @throws Exception
     */
    Map<String, Object> selectPsGoodsForTsActivity(Map<String, Object> map) throws Exception;

    /**
     * 查询推荐商品
     *
     * @param map
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> selectPsGoodsActivityList(Map<String, Object> map) throws Exception;

    /**
     * 查询首页推荐商品 小程序端
     *
     * @param map
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> selectPsGoodsActivityHomeListForSmallProgram(Map<String, Object> map) throws Exception;

    /**
     * 查询首页推荐商品 APP端
     *
     * @param map
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> selectPsGoodsActivityHomeListForConsumer(Map<String, Object> map) throws Exception;

    /**
     * 查询推荐商品列表(运营)
     *
     * @param map
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> selectPsGoodsActivityForOpList(Map<String, Object> map) throws Exception;

    /**
     * 查询商品查看销售统计记录
     *
     * @param map
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> selectGdViewSellDetail(Map<String, Object> map) throws Exception;

    /**
     * 查询商品操作日志
     *
     * @param map
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> selectGoodsLog(Map<String, Object> map) throws Exception;

    /**
     * 商品置底
     *
     * @param map
     * @return
     * @throws Exception
     */
    Integer updatePsGoodsBottom(Map<String, Object> map) throws Exception;

    /**
     * 商品置底
     *
     * @param map
     * @return
     * @throws Exception
     */
    Integer updateFreePsGoodsBottom(Map<String, Object> map) throws Exception;

    /**
     * 更新会过商品信息
     *
     * @param map
     * @return
     * @throws Exception
     */
    Integer updateGdGoodsItem(Map<String, Object> map) throws Exception;

    /**
     * 更新会过商品上下架信息
     *
     * @param map
     * @return
     * @throws Exception
     */
    Integer updateGoodsSaleOrShelves(Map<String, Object> map) throws Exception;

    /**
     * 更新预售商品信息
     *
     * @param map
     * @return
     * @throws Exception
     */
    Integer updatePsGoods(Map<String, Object> map) throws Exception;

    /**
     * 更新预售商品信息
     *
     * @param map
     * @return
     * @throws Exception
     */
    Integer updateFreePsGoods(Map<String, Object> map) throws Exception;

    /**
     * 更新预售商品信息
     *
     * @param map
     * @return
     * @throws Exception
     */
    Integer updatePsOwnGoods(Map<String, Object> map) throws Exception;


    /**
     * 更新预售商品SKU信息
     *
     * @param map
     * @return
     * @throws Exception
     */
    Integer updatePsGoodsSku(Map<String, Object> map) throws Exception;

    /**
     * 逻辑删除商品，更新商品status属性
     *
     * @param map
     * @return
     * @throws Exception
     */
    Integer updatePsGoodsStatus(Map<String, Object> map) throws Exception;

    /**
     * 更新商品查看销售统计记录
     *
     * @param map
     * @return
     * @throws Exception
     */
    int updateGdViewSell(Map<String, Object> map) throws Exception;

    /**
     * 自营商品库存恢复
     *
     * @param map
     * @return
     * @throws Exception
     */
    Integer updatePsGoodsSaleQtyRestore(Map<String, Object> map) throws Exception;

    /**
     * 0元购商品库存恢复
     */
    Integer updateGdFreeGetGoodsSaleQtyRestore(Map<String, Object> map) throws Exception;

    /**
     * 自营商品库存扣减
     *
     * @param map
     * @return
     * @throws Exception
     */
    Integer updatePsGoodsSaleQtyDeduction(Map<String, Object> map) throws Exception;

    /**
     * 0元购商品库存扣减
     */
    Integer updateGdFreeGetGoodsSaleQtyDeduction(Map<String, Object> map) throws Exception;

    /**
     * 我要批商品库存扣减
     *
     * @param map
     * @return
     * @throws Exception
     */
    Integer updateWypGoodsSaleQtyDeduction(Map<String, Object> map) throws Exception;

    /**
     * 我要批商品库存扣减
     *
     * @param map
     * @return
     * @throws Exception
     */
    Integer updateWypGoodsSkuSaleQtyDeduction(Map<String, Object> map) throws Exception;

    /**
     * 我要批商品库存恢复
     *
     * @param map
     * @return
     * @throws Exception
     */
    Integer updateWypGoodsSaleQtyRestore(Map<String, Object> map) throws Exception;

    /**
     * 我要批商品库存恢复
     *
     * @param map
     * @return
     * @throws Exception
     */
    Integer updateWypGoodsSkuSaleQtyRestore(Map<String, Object> map) throws Exception;

    /**
     * 推荐商品删除
     *
     * @param activity_type
     * @return
     * @throws Exception
     */
    Integer deletePsGoodsActivity(String activity_type) throws Exception;

    /**
     * 销售数量增加
     *
     * @param map
     * @return
     * @throws Exception
     */
    Integer updateFreeGetGoodsTaobaoSales(Map<String, Object> map) throws Exception;

    /**
     * 0元购销售数量增加
     */
    Integer updateGdFreeGetGoodsTaobaoSales(Map<String, Object> map) throws Exception;


    /**
     * 查询会过预售商品信息
     * 逍遥子 2018/02/01
     * @param map
     * @return
     * @throws Exception
     */
    List<PsGoods> selectPsHuiGuoGoods(Map<String, Object> map) throws Exception;

    /**
     * 新自营商品修改SKU表的库存
     */
    Integer updatePsGoodsSkuStockDeduction(Map<java.lang.String, Object> paramsMap) throws Exception;

    /**
     * 新自营商品可销售数量恢复
     */
    Integer updatePsGoodsSkuStockRestore(Map<String, Object> paramsMap);

    /**
     * 新的自营商品库存恢复
     *
     * @param map
     * @return
     * @throws Exception
     */
    Integer updatePsGoodsSaleQtyForOwnRestore(Map<String, Object> map) throws Exception;

    /**
     * 新自营通过标签筛选商品
     */
    List<PsGoods> selectfreeGetGoodsListByLabelForNewOwn(Map<String, Object> paramsMap) throws Exception;

    /**
     * 查询所有0元购商品列表
     */
    List<PsGoods> selectGdFreeGetGoodsList(Map<String, Object> paramsMap) throws Exception;

    /**
     * 查询0元购商品信息
     */
    List<PsGoods> selectGdFreeGetGoods(Map<String, Object> map) throws Exception;

    /**
     * 查询贝壳商品信息
     */
    List<BeikeMallGoods> selectBeiKeGoodsList(Map<String, Object> paramsMap) throws Exception;


    /**
     * 贝壳商城商品可销售数量恢复
     */
    int updateBeikeMallGoodsSaleQtyForOwnRestore(Map<String, Object> paramsMap) throws Exception;

    /**
     * 查询贝壳商品信息
     *
     * @param map
     * @return beikeGoods
     */
    List<BeikeMallGoods> selectBeiKeGoods(Map<String, Object> map) throws Exception;

    /**
     * insert BeikeGoods
     * @param record
     * @return
     */
    int insertBeikeGoods(BeikeMallGoods record);


    /**
     * 上架
     * @param map
     * @return
     */
    int updateBeiKeGoods(Map<String, Object> map);

    /**
     * get PicInfo by goods_id
     * @param goods_id
     * @return
     */
    String selectBeikePicByGoodsId(String goods_id);

    /**
     * select activityGoods count by status (activiting)
     * @param status
     * @return
     */
    int selectPsGoodsActivityListByStatus(@Param("status") String status);


    int selectPsGoodsActivityListByStatushb(@Param("status") String Status);
    /**
     * offline activity
     * @param status
     * @param goodsId
     * @return
     */
    int updateActivityGoodsStatus(@Param("status") String status,@Param("goodsId") Object goodsId);

    /**
     * select desc
     * @param goods_id
     * @return
     */
    String selectDescByGoodsId(String goods_id);

    /**
     * 扣减贝壳商城商品库存
     */
    Integer updateBeikeMallGoodsSaleQtyDeduction(Map<String, Object> map) throws Exception;
}
