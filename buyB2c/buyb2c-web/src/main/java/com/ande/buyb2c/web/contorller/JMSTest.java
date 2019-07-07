package com.ande.buyb2c.web.contorller;

import java.util.Random;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ande.buyb2c.web.jms.JMSProducerUtil;

/**
 * @author chengzb
 * @date 2018年2月5日上午11:25:31
 */
@RestController
@RequestMapping("/test")
public class JMSTest {
	@Resource
	private JMSProducerUtil orderProducer;
	@RequestMapping("/tt")
	public void test(){
		/*  Destination destination = new ActiveMQQueue("mytest.queue");  
		orderProducer.sendMessage(destination,"gggg");*/
		orderProducer.sendDelayMessage("dfgdfg", "chengzb",Long.valueOf(1000*60));
	}
	public static void main(String[] args) {
		System.out.println(System.currentTimeMillis());
		System.out.println("G"+System.currentTimeMillis()+""+(new Random().nextInt(10000)+10000));
	}
}
