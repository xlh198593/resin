package com.meitianhui.goods.dao;

import java.util.List;
import java.util.Map;

import com.meitianhui.goods.entity.GdGoodsLabel;

/**
 * 标签管理数据层
 */
public interface GdGoodsLabelDao {

	List<GdGoodsLabel> findGoodsLabel(Map<String, Object> paramsMap) throws Exception;

}
