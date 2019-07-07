package com.ande.buyb2c.admin.controller;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ande.buyb2c.admin.vo.goods.CountVo;
import com.ande.buyb2c.common.util.AbstractController;
import com.ande.buyb2c.common.util.JsonResponse;
import com.ande.buyb2c.common.util.SystemCode;
import com.ande.buyb2c.order.service.IOrderService;
import com.ande.buyb2c.user.service.frontUser.IUserService;

/**
 * @author chengzb
 * @date 2018年3月13日下午5:25:33
 * 首页统计功能
 */
@RestController
@RequestMapping("/admin/count")
public class CountController extends AbstractController{
	@Resource
	private IOrderService orderService;
	@Resource
	private IUserService userService;
	@RequestMapping("/getCount")
	public JsonResponse<CountVo> getCount(){
		JsonResponse<CountVo> json=new JsonResponse<CountVo>();
		CountVo vo=new CountVo();
		vo.setUserTotalCount(userService.getUserCount());
		vo.setToDayUserTotalCount(userService.getUserCountToday());
		BigDecimal big =orderService.getOrderTotalAmount();
		vo.setOrderTotalAmount(big==null?new BigDecimal(0):big);
		 big=orderService.getTodayOrderTotalAmount();
		vo.setToDayOrderTotalAmount(big==null?new BigDecimal(0):big);
		vo.setOrderStateList(orderService.getAllOrderCount());
		vo.setUserCountByYear(userService.getUserCountByYear());
		json.setObj(vo);
		json.set(SystemCode.SUCCESS.getCode(), 
				SystemCode.SUCCESS.getMsg());
		return json;
	}

}
