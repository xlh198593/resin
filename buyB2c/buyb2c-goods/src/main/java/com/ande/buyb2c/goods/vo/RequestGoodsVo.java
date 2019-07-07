package com.ande.buyb2c.goods.vo;

import com.ande.buyb2c.goods.entity.Goods;

/**
 * @author chengzb
 * @date 2018年1月30日下午5:37:55
 * 更新商品时传
 */
public class RequestGoodsVo extends Goods {
private String goodsAttributeIds;
private String isChang;  //属性是否改变 1是 2不是
public String getIsChang() {
	return isChang;
}
public void setIsChang(String isChang) {
	this.isChang = isChang;
}
public String getGoodsAttributeIds() {
	return goodsAttributeIds;
}
public void setGoodsAttributeIds(String goodsAttributeIds) {
	this.goodsAttributeIds = goodsAttributeIds;
}
}
