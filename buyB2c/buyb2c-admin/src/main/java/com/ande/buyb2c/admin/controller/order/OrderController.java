package com.ande.buyb2c.admin.controller.order;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ande.buyb2c.common.util.AbstractController;
import com.ande.buyb2c.common.util.JsonResponse;
import com.ande.buyb2c.common.util.PageResult;
import com.ande.buyb2c.common.util.SessionUtil;
import com.ande.buyb2c.common.util.SystemCode;
import com.ande.buyb2c.order.OrderCode;
import com.ande.buyb2c.order.entity.Order;
import com.ande.buyb2c.order.service.IOrderService;
import com.ande.buyb2c.order.vo.RequestOrderVo;
import com.ande.buyb2c.user.entity.AdminUser;

/**
 * @author chengzb
 * @date 2018年1月31日下午1:51:34
 */
@RestController
@RequestMapping("/admin/order")
public class OrderController extends AbstractController {
	@Resource
	private SessionUtil<AdminUser> sessionUtil;
	@Resource
	private IOrderService orderService;
	/**
	 * 
	 * @param page
	 * @param logistics
	 * @return  查询详情
	 */
	@RequestMapping("/getOrderById")
	public JsonResponse<Order> getOrderById(Integer orderId){
		JsonResponse<Order> json=new JsonResponse<Order>();
		if(orderId==null){
			json.setResult("orderId不能为空");
			return json;
		}
		json.set(SystemCode.SUCCESS.getCode(), 
				SystemCode.SUCCESS.getMsg());
		json.setObj(orderService.getOrderById(orderId));
		return json;
	}
	/**
	 * 
	 * @param page
	 * @param logistics
	 * @return  查询列表
	 */
	@RequestMapping("/getOrderPage")
	public JsonResponse<PageResult<Order>> getOrderPage(PageResult<Order> page,RequestOrderVo order){
		JsonResponse<PageResult<Order>> json=new JsonResponse<PageResult<Order>>();
		orderService.queryByPage(page, order);
		if(page.getTotal()!=0){
			json.set(SystemCode.SUCCESS.getCode(), 
					SystemCode.SUCCESS.getMsg());
			json.setObj(page);
		}
		return json;
	}
	/**
	 * 发货
	 * @param orderId 订单号
	 * @param logisticsId 物流单id
	 * @param logisticsName 物流名称
	 * @param logisticsNo 物流单号
	 */
	@RequestMapping("/sendGoods")
	public JsonResponse<String> sendGoods(Integer orderId,Integer sendLogisticsId,String sendLogisticsName,String logisticsNo){
		JsonResponse<String> json=new JsonResponse<String>();
		if(StringUtils.isEmpty(sendLogisticsName)||StringUtils.isEmpty(logisticsNo)||sendLogisticsId==null||orderId==null){
			json.setResult("orderId,sendLogisticsId,logisticsNo,sendLogisticsName不能为空");
			return json;
		}
		Order order=new Order();
		order.setOrderId(orderId);
		order.setLogisticsNo(logisticsNo);
		order.setSendLogisticsId(sendLogisticsId);
		order.setSendLogisticsName(sendLogisticsName);
		order.setSendGoodsTime(new Date());
		order.setOrderState(OrderCode.WAIT_RECIVE.getCode());
		try {
			orderService.updateByPrimaryKeySelective(order);
			json.set(SystemCode.SUCCESS.getCode(), 
					SystemCode.SUCCESS.getMsg());
		} catch (Exception e) {
			logError("发货失败", e);
		}
		return json;
	}

}
