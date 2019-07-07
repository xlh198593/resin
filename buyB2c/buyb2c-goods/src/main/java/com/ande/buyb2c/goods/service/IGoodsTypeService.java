package com.ande.buyb2c.goods.service;

import java.util.List;

import com.ande.buyb2c.common.util.IBaseService;
import com.ande.buyb2c.goods.entity.GoodsType;

/**
 * @author chengzb
 * @date 2018年1月30日上午9:48:17
 */
public interface IGoodsTypeService extends IBaseService<GoodsType> {
	public List<GoodsType> getGoodsTypeList();
	public Integer delGoodsType(String level,Integer goodsTypeId)throws Exception ;
	/**
	 * 级联查询
	 */
	public List<GoodsType> getGoodsTypeByParentId(Integer parentId);
}
