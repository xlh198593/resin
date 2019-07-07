package com.ande.buyb2c.web.contorller.order;


import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ande.buyb2c.common.generateOrderNo.ISerialNumberService;
import com.ande.buyb2c.common.util.AbstractController;
import com.ande.buyb2c.common.util.JsonResponse;
import com.ande.buyb2c.common.util.PageResult;
import com.ande.buyb2c.common.util.SystemCode;
import com.ande.buyb2c.order.OrderCode;
import com.ande.buyb2c.order.dao.OrderDetailMapper;
import com.ande.buyb2c.order.entity.Order;
import com.ande.buyb2c.order.entity.OrderDetail;
import com.ande.buyb2c.order.service.IOrderService;
import com.ande.buyb2c.shopcart.service.IShopCartService;
import com.ande.buyb2c.user.entity.User;
import com.ande.buyb2c.web.UserJsonConvertUtil;
import com.ande.buyb2c.web.jms.JMSProducerUtil;

/**
 * @author chengzb
 * @date 2018年2月5日下午1:42:15
 */
@RestController
@RequestMapping("/front/order")
public class OrderController extends AbstractController{
	@Resource
	private IOrderService orderService;
	@Resource
	private OrderDetailMapper orderDetailMapper;
	@Resource
	private UserJsonConvertUtil userJsonConvertUtil;
	@Resource
	private ISerialNumberService serialNumberService;
	@Resource
	private JMSProducerUtil producerUtil;
	@Resource
	private IShopCartService shopCartService;
	/**
	 * 下单
	 */
@RequestMapping("/addOrder")
public JsonResponse<String> addOrder(@RequestBody Order order,HttpServletRequest request){
	JsonResponse<String> json=new JsonResponse<String>();
	User user=userJsonConvertUtil.getUser(request);
	order.setOrderNo("O"+serialNumberService.getOrderNO("buyb2c:order")+""+userJsonConvertUtil.getUser(request).getUserId());
	order.setCustomerId(user.getUserId());
	try {
		orderService.insertSelective(order);
		if(!StringUtils.isEmpty(order.getShopCartIds())){
			shopCartService.delShopCart(order.getShopCartIds());
		}
		/*List<OrderAttribute> list=new ArrayList<OrderAttribute>();
		for(OrderDetail detail:order.getOrderDetailList()){
			for(OrderAttribute attribute:detail.getOrderAttributeList()){
				attribute.setOrderId(order.getOrderId());
				attribute.setCustomerId(user.getUserId());
				attribute.setCreateTime(new Date());
			}
			list.addAll(detail.getOrderAttributeList());
		}
		 Destination destination = new ActiveMQQueue("order.attribute");
		 //添加订单属性
		 producerUtil.sendMessage(destination,JSON.toJSONString(list));*/
	 //
		 producerUtil.sendDelayMessage(order.getOrderId()+"","order.delay.cancelOrder",Long.valueOf(1000*60*60*24));
			json.set(SystemCode.SUCCESS.getCode(), 
					SystemCode.SUCCESS.getMsg()); 
			json.setObj(order.getOrderNo());
	} catch (Exception e) {
		logError("下单异常", e);
	}
	return json;
}
/**
 * 
 * @return  查询订单状态数目
 */
@RequestMapping("/getOrderCount")
public JsonResponse<Integer> getOrderCount(HttpServletRequest request){
	JsonResponse<Integer> json=new JsonResponse<Integer>();
	json.set(SystemCode.SUCCESS.getCode(), 
			SystemCode.SUCCESS.getMsg());
	json.setList(orderService.getOrderCount(userJsonConvertUtil.getUser(request).getUserId()));
	return json;
}
/**
 * @return  查询订单列表
 */
@RequestMapping("/getOrderPage")
public JsonResponse<PageResult<Order>> getOrderPage(PageResult<Order> page,Order order,HttpServletRequest request){
	JsonResponse<PageResult<Order>> json=new JsonResponse<PageResult<Order>>();
	order.setCustomerId(userJsonConvertUtil.getUser(request).getUserId());
	orderService.queryByPageFront(page, order);
	if(page.getTotal()!=0){
		json.set(SystemCode.SUCCESS.getCode(), 
				SystemCode.SUCCESS.getMsg());
		json.setObj(page);
		for(Order o:page.getDataList()){
			OrderDetail detail=new OrderDetail();
			detail.setOrderId(o.getOrderId());
			o.setOrderDetailList(orderDetailMapper.getAllBySelect(detail));
		}
	}
	return json;
}
/**
 * @return  查询订单详情
 */
@RequestMapping("/getOrderById")
public JsonResponse<Order> getOrderById(Integer orderId){
	JsonResponse<Order> json=new JsonResponse<Order>();
	json.setObj(orderService.getOrderById(orderId));
	json.set(SystemCode.SUCCESS.getCode(), 
			SystemCode.SUCCESS.getMsg());
	return json;
}
/**
 * 
 * @param orderId
 * @return  取消订单
 */
@RequestMapping("/cancelOrder")
public JsonResponse<Order> cancelOrder(Integer orderId){
	JsonResponse<Order> json=new JsonResponse<Order>();
	Order order=new Order();
	order.setOrderId(orderId);
	order.setOrderState(OrderCode.ALREADY_CANCEL.getCode());//用户取消订单
	try {
		orderService.updateByPrimaryKeySelective(order);
		json.set(SystemCode.SUCCESS.getCode(), 
				SystemCode.SUCCESS.getMsg());
	} catch (Exception e) {
		logError("用户取消订单异常", e);
	}
	return json;
}
/**
 * 
 * @param orderId
 * @return  确认收货
 */
@RequestMapping("/confirmReceipt")
public JsonResponse<Order> confirmReceipt(Integer orderId){
	JsonResponse<Order> json=new JsonResponse<Order>();
	Order order=new Order();
	order.setOrderId(orderId);
	order.setConfirmTime(new Date());
	order.setOrderState(OrderCode.WAIT_EVALUATE.getCode());//置为待评价
	try {
		orderService.updateByPrimaryKeySelective(order);
		json.set(SystemCode.SUCCESS.getCode(), 
				SystemCode.SUCCESS.getMsg());
	} catch (Exception e) {
		logError("用户确认收货异常", e);
	}
	return json;
}
}
