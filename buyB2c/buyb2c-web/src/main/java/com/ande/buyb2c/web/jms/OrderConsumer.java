package com.ande.buyb2c.web.jms;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.ande.buyb2c.order.dao.OrderAttributeMapper;
import com.ande.buyb2c.order.entity.OrderAttribute;

/**
 * @author chengzb
 * @date 2018年2月5日上午11:23:39
 */
@Service
public class OrderConsumer {
	@Resource
	private OrderAttributeMapper orderAttributeMapper;
    // 使用JmsListener配置消费者监听的队列，其中text是接收到的消息  
	//添加订单属性
  @JmsListener(destination = "order.attribute")  
  public void receiveQueue(String text) {  
      List<OrderAttribute> list = JSON.parseArray(text,OrderAttribute.class);
      orderAttributeMapper.addBatch(list);
  }  
}
