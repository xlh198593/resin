package com.ande.buyb2c.admin.vo.goods;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author chengzb
 * @date 2018年3月13日下午5:27:15
 */
public class CountVo {
private BigDecimal orderTotalAmount;//订单总金额
private BigDecimal toDayOrderTotalAmount;//今日新增订单总金额
private int userTotalCount;//会员总数
private int toDayUserTotalCount;//今日新增会员总数

private List<Integer> orderStateList;
private List<Map<String,Object>> userCountByYear;//近半年会员数量

public List<Map<String, Object>> getUserCountByYear() {
	return userCountByYear;
}

public void setUserCountByYear(List<Map<String, Object>> userCountByYear) {
	this.userCountByYear = userCountByYear;
}

public BigDecimal getOrderTotalAmount() {
	return orderTotalAmount;
}

public void setOrderTotalAmount(BigDecimal orderTotalAmount) {
	this.orderTotalAmount = orderTotalAmount;
}

public BigDecimal getToDayOrderTotalAmount() {
	return toDayOrderTotalAmount;
}

public void setToDayOrderTotalAmount(BigDecimal toDayOrderTotalAmount) {
	this.toDayOrderTotalAmount = toDayOrderTotalAmount;
}

public int getUserTotalCount() {
	return userTotalCount;
}

public void setUserTotalCount(int userTotalCount) {
	this.userTotalCount = userTotalCount;
}

public int getToDayUserTotalCount() {
	return toDayUserTotalCount;
}

public void setToDayUserTotalCount(int toDayUserTotalCount) {
	this.toDayUserTotalCount = toDayUserTotalCount;
}

public List<Integer> getOrderStateList() {
	return orderStateList;
}

public void setOrderStateList(List<Integer> orderStateList) {
	this.orderStateList = orderStateList;
}



}
