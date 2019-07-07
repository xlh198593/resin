package com.ande.buyb2c.column.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.ande.buyb2c.column.entity.ColumnGoods;
import com.ande.buyb2c.common.util.IBaseService;

/**
 * @author chengzb
 * @date 2018年1月27日下午5:24:56
 */
public interface IColumnGoodsService extends IBaseService<ColumnGoods> {
	public Integer addGoodsToColumn(HttpServletRequest request,List<ColumnGoods> list)throws Exception;
	public Integer  delColumnGoods(String columnGoodsIds)throws Exception;
	public Integer deleteByColumnId(Integer columnId)throws Exception;
}
