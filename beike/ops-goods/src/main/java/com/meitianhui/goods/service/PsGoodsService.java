package com.meitianhui.goods.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;
import com.meitianhui.goods.entity.GdGoodsSkuid;
import com.meitianhui.goods.entity.PsGoods;

/**
 * 我要批，领了么，名品汇商品
 * 
 * @author Tiny
 *
 */
public interface PsGoodsService {

	
	/**
	 * 我要批商品同步
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void gdGoodsItemSync(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	
	/**
	 * 我要批商品同步
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void psGoodsSync(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 我要批商品同步
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void handlePsOwnGoodsSync(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	
	/**
	 * 我要批商品库存同步
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void wpyGoodsSkuSync(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 自营商品可销售数量恢复
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void psGoodsSaleQtyRestore(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 自营商品可销售数量扣减
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void psGoodsSaleQtyDeduction(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 我要批商品可销售数扣减
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void wypGoodsSaleQtyDeduction(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 我要批商品可销售库存恢复
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void handleWypGoodsSaleQtyRestore(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 我要批商品可销售库存验证
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void wypGoodsSaleQtyValidate(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 我要批商品商品详情查询
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void psGoodsDetailFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 我要批商品SKU信息查询(运营系统)
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void psGoodsSkuForOpFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 我要批商品SKU信息查询(店东助手)
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void psGoodsSkuForStoresFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 最新的商品信息查询(运营)
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */

	void newestPsGoodsListForOpFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 查询供应商“已上架的“(领了么)商品总数
	 *    
	 * @param map
	 * @return
	 * @throws Exception
	 */
	void psGoodsSupplyCount(Map<String, Object> map,ResultData result) throws Exception;
	
	/**
	 * 商品列表查询
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void psGoodsListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 商品列表查询
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void psUnionAllGoodsListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	
	void selectGdGoodsItemAndFiterOffDay(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	
	void selectPsGoodsAndFiterOffDay(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**  
	 * 商品等级和积分查询  
	 *     
	 * @param map    
	 * @throws BusinessException 
	 * @throws SystemException  
	 */   
	void psGoodsLevelAndPointFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 订单中goods_id对应的我要批商品的基本信息查询
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void psGoodsFindForOrder(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 伙拼团查询商品信息
	 * 
	 * @Title: selectPsGoodsForTcActivity
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author tiny
	 */
	void psGoodsFindForTsActivity(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 编辑我要批商品
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void psGoodsEdit(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	
	/**
	 * 新商品库编辑我要批商品
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void psGoodsNewEdit(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 名品汇商品查询
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void goldExchangeGoodsDetailFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 名品汇商品查询
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void goldExchangeGoodsFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 我要批商品查询
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void psGoodsFindForWeb(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 商品置底
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void psGoodsOrderBottom(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 我要批商品状态更新
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void psGoodsStatusEdit(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 逻辑删除商品，更新status属性
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void psGoodsDelete(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	
	/**
	 * 伙拼团商品查询(H5)
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void tsGoodsListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 我要批商品查询(H5)
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void psGoodsFindForH5(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 我要批商品商品详情查询(H5)
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void wypGoodsDetailFindForH5(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 领了么商品信息更新
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void freeGetGoodsStatusEdit(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	
	/** 接口改造 **/

	
	/**
	 * 查询预售商品列表 店东助手端
	 * 
	 * @Title: freeGetGoodsListFind
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author tiny
	 */
	void freeGetGoodsPreSaleListFindForStores(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	
	/**
	 * 查询预售商品列表 领有惠端
	 * 
	 * @Title: freeGetGoodsListFind
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author tiny
	 */
	void freeGetGoodsPreSaleListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	
	/**
	 * 预售免费领商品列表查询 小程序端
	 * 
	 * @Title: freeGetGoodsListFind
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author tiny
	 */
	void freeGetGoodsPreSaleListFindForSmallProgram(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	
	/**
	 * 免费领商品列表查询（来自花生日记并且上架）
	 * 
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 */
	void freeGetGoodsForOperateListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	
	
	/**
	 * 免费领商品列表查询（模糊条件查询）小程序端
	 * 
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 */
	void freeGetGoodsListBySearchFindForSmallProgram(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	
	/**
	 * 免费领商品列表查询（模糊条件查询）APP端
	 * 
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 */
	void freeGetGoodsListBySearchFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	
	/**
	 * 查询会过商品列表  小程序端
	 * 
	 * @Title: freeGetGoodsListFind
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author tiny
	 */
	void freeGetGoodsListFindByHuiguoForSmallProgram(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	
	/**
	 * 查询自营商品列表  小程序端
	 * 
	 * @Title: freeGetGoodsListFind
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author tiny
	 */
	void freeGetGoodsListFindByOwnForSmallProgram(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	
	/**
	 * 查询商品列表  店东助手端
	 * 
	 * @Title: freeGetGoodsListFind
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author tiny
	 */
	void freeGetGoodsListPageFindForStores(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	
	/**
	 * 查询商品列表  小程序端
	 * 
	 * @Title: freeGetGoodsListFind
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author tiny
	 */
	void freeGetGoodsListFindForSmallProgram(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	/**
	 * 查询商品列表  领有惠端
	 * 
	 * @Title: freeGetGoodsListFind
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author tiny
	 */
	void freeGetGoodsListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 免费领商品列表查询  APP端 会过
	 * 
	 * @Title: freeGetGoodsListFind
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author tiny
	 */
	void freeGetGoodsListFindForHuiguo(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	
	/**
	 * 免费领商品列表查询  APP端 自营
	 * 
	 * @Title: freeGetGoodsListFind
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author tiny
	 */
	void freeGetGoodsListFindForOwn(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	
	/**
	 * 免费领商品列表查询  APP端  新自营
	 */
	void freeGetGoodsListFindForNewOwn(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	
	/**
	 * 按标签查询商品列表 小程序端
	 * 
	 * @Title: freeGetGoodsListFind
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author tiny
	 */
	void freeGetGoodsByLabelListFindForSmallProgram(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	
	
	/**
	 * 按标签查询商品列表 店东助手端 
	 * 
	 * @Title: freeGetGoodsListFind
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author tiny
	 */
	void freeGetGoodsByLabelListFindForStores(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	
	/**
	 * 按标签查询商品列表 APP端 淘淘领
	 * 
	 * @Title: freeGetGoodsListFind
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author tiny
	 */
	void freeGetGoodsByLabelListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	/**
	 * 按标签查询商品列表 APP端 0元购
	 */
	void gdFreeGetGoodsByLabelListPageFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 按标签查询商品列表 APP端 会过
	 * 
	 * @Title: freeGetGoodsListFind
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author tiny
	 */
	void freeGetGoodsByLabelListFindForHuiguo(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	
	
	/**
	 * 按标签查询商品列表 APP端 自营
	 * 
	 * @Title: freeGetGoodsListFind
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author tiny
	 */
	void freeGetGoodsByLabelListFindForOwn(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	
	
	
	/**
	 * 查询上新商品列表  店东助手端
	 * 
	 * @Title: freeGetGoodsListFind
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author tiny
	 */
	void freeGetGoodsNewestListFindForStores(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	
	/**
	 * 查询上新商品列表  领有惠端
	 * 
	 * @Title: freeGetGoodsListFind
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 * @author tiny
	 */
	void freeGetGoodsNewestListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 推荐商品创建
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void handlePsGoodsActivitySync(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 推荐商品列表（运营）
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void psGoodsActivityForOpFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 推荐商品列表(APP)
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void psGoodsActivityListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 推荐商品列表(APP)
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void psGoodsActivityFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	
	
	/**
	 * 推荐商品列表 小程序端
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void freeGetGoodsActivityHomeListFindForSmallProgram(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	/**
	 * 推荐商品列表 APP端
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void psGoodsActivityHomeForConsumerFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 商品查看已售记录统计查询
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void gdViewSellDetailFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 商品查看次数增加
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void gdViewAdd(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 商品已售数量增加
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void gdSellAdd(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 添加商品操作日志
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	void goodsLogAdd(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 查询商品操作日志
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	void goodsLogListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 销售数量增加
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	void freeGetGoodsTaobaoSalesEdit(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;



	/**
	 * 会过商品列表查询
	 * 逍遥子 2018/02/01
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void psHuiGuoGoodsListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 会过商品上/下架
	 * 20180210 逍遥子
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 * @throws Exception
	 */
	void updateGoodsSaleOrShelves(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	
	/**
	 * 查询价格小于等于9.9已上架的商品
	 * @return
	 * @throws Exception
	 */
	void less9PsGoodsListFind(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception ;
	
    /**
	 * 会过商品规格列表信息
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	void selectGdGoodsSkuidList(Map<String, Object> map, ResultData result) throws Exception;

	/**
	 * 新自营商品的修改库存
	 */
	void newPsGoodsSaleQtyDeduction(Map<String, Object> paramsMap, ResultData result)throws BusinessException, SystemException, Exception;
	
	/**
	 * 新自营商品可销售数量恢复
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void newPsGoodsSaleQtyRestore(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	
	/**
	 * 新的自营商品可销售数量恢复
	 * 
	 * @param map
	 * @throws BusinessException
	 * @throws SystemException
	 */
	void handlePsGoodsSaleQtyForOwnRestore(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	void queryGdGoodsSkuidBySkuId(Map<String, Object> map,ResultData result)
			throws BusinessException, SystemException, Exception;
	/**
	 * 查询价格小于等于9.9已上架的商品(新自营)
	 */
	void less9PsGoodsListFindNewOwn(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	/**
	 * 新自营通过标签筛选商品
	 */
	void freeGetGoodsByLabelListFindForNewOwn(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	/**
	 * 查询所有的0元购商品
	 */
	void gdFreegetGoodsListPageFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 查询0元购商品信息用于订单创建
	 */
	void gdFreeGetGoodsFindForOrder(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 0元购商品库存扣减
	 */
	void gdFreeGetGoodsSaleQtyDeduction(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;
	
	/**
	 * 0元购商品销售数量增加
	 */
	void gdFreeGetGoodsTaobaoSalesEdit(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 0元购商品库存数量恢复
	 */
	void gdFreeGetGoodsSaleQtyRestore(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 0元购商品详情页面
	 */
	void gdFreeGetGoodsDetailFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 分享模块-根据商品code查询商品信息
	 */
	void selectPsGoodsforGoodsCodeSet(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 商品列表查询 APP端   贝壳商城
	 */
	void beiKeGoodsForListPageFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 商品列表查询 APP端   贝壳商城按label查询商品
	 */
	void beiKeGoodsForListByLabelListPageFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 查询贝壳商品
	 */
	void beikeMallGoodsFindForOrder(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 查询贝壳商品详细信息
	 */
	void beikeMallGoodsDetailFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception;

	/**
	 * 贝壳商城商品可销售数量恢复
	 */
	void handleBeikeMallGoodsSaleQtyForOwnRestore(Map<String, Object> paramsMap, ResultData result) throws Exception;

	/**
	 * 查询红包兑商城列表
	 */
	void hongbaoGoodsForListPageFind(Map<String, Object> paramsMap, ResultData result) throws Exception;

	/**
	 * 查询红包兑商品详细信息
	 */
	void hongbaoGoodsDetailFind(Map<String, Object> paramsMap, ResultData result) throws Exception;

	/**
	 * 修改红包兑商品的库存
	 */
	void hongbaoGoodsSaleQtyUpdate(Map<String, Object> paramsMap, ResultData result) throws Exception;

	/**
	 * 贝壳商城修改库存
	 */
	void beikeMallGoodsSaleQtyDeduction(Map<String, Object> paramsMap, ResultData result) throws Exception;

	/**
	 * 查询贝壳商超商品详情(新接口)
	 */
	void beikeMallGoodsDetailFindNew(Map<String, Object> paramsMap, ResultData result) throws Exception;

	/**
	 * 增加贝壳商超销量
	 */
	void beikeMallGoodsSalesVolumeUpdate(Map<String, Object> paramsMap, ResultData result) throws Exception;
	
	/**
	 * 增加红包商品销量
	 */
	void hongBaoGoodsSalesVolumeUpdate(Map<String, Object> paramsMap, ResultData result) throws Exception;
}
