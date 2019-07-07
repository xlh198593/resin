package com.ande.buyb2c.web.jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ScheduledMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * @author chengzb
 * @date 2018年2月5日上午9:22:31 创建订单 向 mq发送消息
 */
@Service
public class JMSProducerUtil {
	@Autowired // 也可以注入JmsTemplate，JmsMessagingTemplate对JmsTemplate进行了封装
	private JmsMessagingTemplate jmsTemplate;

	// 发送消息，destination是发送到的队列，message是待发送的消息
	public void sendMessage(Destination destination, final String message) {
		jmsTemplate.convertAndSend(destination, message);
	}

	/**
	 * @desc 延时发送
	 */
	public void sendDelayMessage(String text, String queueName, Long time) {
		// 获取连接工厂
		ConnectionFactory connectionFactory = this.jmsTemplate.getConnectionFactory();
		try {
			// 获取连接
			Connection connection = connectionFactory.createConnection();
			connection.start();
			// 获取session
			Session session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
			// 创建一个消息队列
			Destination destination = session.createQueue(queueName);
			MessageProducer producer = session.createProducer(destination);
			producer.setDeliveryMode(DeliveryMode.PERSISTENT);
			TextMessage message = session.createTextMessage(text);
			// TextMessage message =new ActiveMQTextMessage();
			// message.setText(text);
			// //设置延迟时间
			// message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, time);
			// jmsTemplate.convertAndSend(queueName, message);
			// 发送
			message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, time);
			producer.send(message);
			session.commit();
			producer.close();
			session.close();
			connection.close();
		} catch (Exception e) {
			e.getMessage();
		}
	}
}
