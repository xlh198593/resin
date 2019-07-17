package com.meitianhui.goods.dao;

import java.util.List;
import java.util.Map;

import com.meitianhui.goods.entity.BeikeMallGoods;
import com.meitianhui.goods.entity.GdGoodsItem;
import com.meitianhui.goods.entity.GdViewSell;
import com.meitianhui.goods.entity.HongBaoGoods;
import com.meitianhui.goods.entity.PsGoods;
import com.meitianhui.goods.entity.PsGoodsActivity;
import com.meitianhui.goods.entity.PsGoodsLog;
import com.meitianhui.goods.entity.PsGoodsSku;

public interface HongbaoGoodsDao {
	
	List<HongBaoGoods>selectHongbaoGoodsListBycode(Map<String,Object> paramsMap);

	int updateByPrimaryKeySelective (HongBaoGoods hongBaoGoods);

	/**
	 * 自营商品新增
	 * 
	 */
	int insert (HongBaoGoods hongBaoGoods);
	/**
	 * 查询红包兑列表
	 */
	List<HongBaoGoods> selectHongbaoGoodsList(Map<String, Object> paramsMap);

	/**
	 * 红包兑商品修改库存
	 */
	Integer updateHongBaoGoodsSaleQty(Map<String, Object> paramsMap);

	/**
	 * 上架更新红包兑商品信息
	 */
    int updateBySelective(Map<String, Object> paramsMap);

	/**
	 * 查询该商品图片信息
	 */
	String selectPicByGoodsId(String goods_id);
	/**
	 * 查询该商品描述信息
	 */
	String selectDescByGoodsId(String goods_id);

	/**
	 * 下架
	 */
	int updateOffLine(String goods_id);

	int updateHongBaoGoods(Map<String, Object> paramsMap);

	/**
	 * 修改礼券专区
	 */
	int hongbaoGoodsUpdate(Map<String, Object> paramsMap) throws Exception;
	
	/**
	 * 增加商品销量
	 */
	int  updateSalesVolume(Map<String, Object> paramsMap) throws Exception;
}
