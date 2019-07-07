package com.ande.buyb2c.web.contorller.pay;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ande.base.pay.alipay.AbstractAliPay;
import com.ande.base.pay.alipay.AliConfig;
import com.ande.base.pay.alipay.ParamEntity;
import com.ande.buyb2c.order.OrderCode;
import com.ande.buyb2c.order.PayCode;
import com.ande.buyb2c.order.PayType;
import com.ande.buyb2c.order.entity.Order;
import com.ande.buyb2c.order.service.IOrderService;
import com.ande.buyb2c.web.UserJsonConvertUtil;

/**
 * @author chengzb
 * @date 2018年2月5日下午6:19:01
 */
@RestController
@RequestMapping("/aliPay")
public class AliPayController extends AbstractAliPay{
	@Value("${app.alipay.aliPayPublicKey}")
	private   String aliPayPublicKey;
	@Value("${app.alipay.appId}")
	private   String appId;
	@Value("${app.alipay.appPrivatKey}")
	private   String appPrivatKey;
	@Value("${app.alipay.notifyUrl}")
	private  String notifyUrl;
	@Value("${app.alipay.notifyFrontUrl}")
	private  String notifyFrontUrl;
	@Resource
	private IOrderService orderService;
	@Resource
	private UserJsonConvertUtil userJsonConvertUtil;
	@Override
	public Integer updateOrderStatus(String attach, String transactionId)
			throws Exception {
		
		Order order=new Order();
		order.setOrderNo(attach);
		order.setTransactionId(transactionId);
		order.setPayTime(new Date());
		order.setPayType(PayType.ALIPAY.getCode());
		order.setPayState(PayCode.ALREADY_PAY.getCode());
		order.setOrderState(OrderCode.WAIT_SEND.getCode());
		return orderService.updateOrderByOrderNo(order);
	}
	@RequestMapping("/payByPhone")
	public String pay(String orderNo,HttpServletRequest request){
		if(userJsonConvertUtil.getUser(request)==null){
			return "{\"res\":301}";
		}
		if(StringUtils.isEmpty(orderNo)){
			return "orderNo不能为空";
		}
		Order order=orderService.getOrderByNo(orderNo);
		if(order==null){
			return "order不存在";
		}
		ParamEntity entity=new ParamEntity();
		entity.setAttach(order.getOrderNo());
		entity.setBody("商品");
		entity.setNotifyUrl(notifyUrl);
		entity.setReturnUrl(notifyFrontUrl);
		entity.setOutTradeNo(order.getOrderNo());
		entity.setSubject("主题");
		entity.setTotalAmount(order.getOrderTotalAmount().doubleValue());
		return payByPhone(entity);
	}
	@Override
	public String getAppPrivatKey() {
		return appPrivatKey;
	}
	@Override
	public String getAliPayPublicKey() {
		return aliPayPublicKey;
	}
	@Override
	public String getAppId() {
		return appId;
	}
	@RequestMapping("/notify")
	public void notifyPay(HttpServletRequest request,HttpServletResponse response){
		notify(request, response);
	}

}
