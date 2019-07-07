package com.ande.buyb2c.web.jms;


import javax.annotation.Resource;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import com.ande.buyb2c.order.service.IOrderService;


/**
 * @author chengzb
 * @date 2018年2月6日上午9:45:35
 * 超过一天没有支付的订单  平台自动取消
 */
@Service
public class OrderCancelConsumer {
	@Resource
	private IOrderService orderService;
	  @JmsListener(destination = "order.delay.cancelOrder")  
	  public void receiveQueue(String text) {  
		  System.out.println("取消订单-----------"+text);
		  orderService.cancelOrder(Integer.valueOf(text));
	  }  
}
