package com.ande.buyb2c.admin.vo.goods;

import java.util.List;

import com.ande.buyb2c.attribute.vo.AttributeVo;
import com.ande.buyb2c.goods.entity.Goods;

/**
 * @author chengzb
 * @date 2018年1月30日下午3:49:05
 * 后台编辑商品时 查询数据给前端用
 */
public class GoodsVo {
private List<AttributeVo> attributeVo;
private Goods goods;

public List<AttributeVo> getAttributeVo() {
	return attributeVo;
}
public void setAttributeVo(List<AttributeVo> attributeVo) {
	this.attributeVo = attributeVo;
}
public Goods getGoods() {
	return goods;
}
public void setGoods(Goods goods) {
	this.goods = goods;
}
}
