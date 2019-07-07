package com.ande.buyb2c.column.vo;

import java.util.List;

/**
 * @author chengzb
 * @date 2018年2月1日下午4:15:07
 */
public class FrontColumnVo {
private String columnName;
private Integer columnId;
private String goodsSort;  //'1 上架时间降序 2 商品价格降序 3 商品价格升序',
private Integer showGoodsNum;
private List<FrontColumnGoodsVo> goodsList;
public String getColumnName() {
	return columnName;
}
public void setColumnName(String columnName) {
	this.columnName = columnName;
}
public Integer getColumnId() {
	return columnId;
}
public void setColumnId(Integer columnId) {
	this.columnId = columnId;
}
public String getGoodsSort() {
	return goodsSort;
}
public void setGoodsSort(String goodsSort) {
	this.goodsSort = goodsSort;
}
public Integer getShowGoodsNum() {
	return showGoodsNum;
}
public void setShowGoodsNum(Integer showGoodsNum) {
	this.showGoodsNum = showGoodsNum;
}
public List<FrontColumnGoodsVo> getGoodsList() {
	return goodsList;
}
public void setGoodsList(List<FrontColumnGoodsVo> goodsList) {
	this.goodsList = goodsList;
}
}
