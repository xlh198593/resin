package com.ande.buyb2c.web.contorller.pay;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ande.base.pay.wxpay.AbstractWXPay;
import com.ande.base.pay.wxpay.WxPlaceOrder;
import com.ande.buyb2c.common.util.JsonResponse;
import com.ande.buyb2c.common.util.RedisUtil;
import com.ande.buyb2c.common.util.SystemCode;
import com.ande.buyb2c.order.CalculateUtils;
import com.ande.buyb2c.order.OrderCode;
import com.ande.buyb2c.order.PayCode;
import com.ande.buyb2c.order.PayType;
import com.ande.buyb2c.order.entity.Order;
import com.ande.buyb2c.order.service.IOrderService;
import com.ande.buyb2c.web.UserJsonConvertUtil;

/**
 * @author chengzb
 * @date 2018年2月5日下午4:35:22
 */
@RestController
@RequestMapping("/wxPay")
public class WxPayController extends AbstractWXPay{
	public  final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Resource
	private IOrderService orderService;
	@Resource
	private UserJsonConvertUtil userJsonConvertUtil;
	@Resource
	private RedisUtil redisUtil;
	@Value("${app.wxpay.appId}")
	private String appId;
	@Value("${app.wxpay.mchId}")
	private String mchId;
	@Value("${app.wxpay.key}")
	private String key;
	@Value("${app.wxpay.notifyUrl}")
	private String notifyUrl;
	@Override
	public String getAppID() {
		return appId;
	}
	@Override
	public String getMchID() {
		return mchId;
	}
	@Override
	public String getKey() {
		return key;
	}
	@Override
	public Integer updateOrderStatus(String orderInfo, String transactionId)
			throws Exception {
		Order order=new Order();
		order.setOrderNo(orderInfo);
		order.setPayTime(new Date());
		order.setTransactionId(transactionId);
		order.setPayType(PayType.WXPAY.getCode());
		order.setPayState(PayCode.ALREADY_PAY.getCode());
		order.setOrderState(OrderCode.WAIT_SEND.getCode());
		return orderService.updateOrderByOrderNo(order);
	}
	@RequestMapping("/pay")
	public JsonResponse<Map<String,String>> pay(String orderNo,HttpServletRequest request){
		JsonResponse<Map<String,String>> json=new JsonResponse<Map<String,String>>();
		String token=request.getParameter("token");
		if(StringUtils.isEmpty(token)||"null".equals(redisUtil.get(token))){
			json.setRes(301);
			json.setResult("token is null");
			return json;
		}
		if(userJsonConvertUtil.getUser(request)==null){
			json.setRes(301);
			json.setResult("token is null");
			return json;
		}
		if(userJsonConvertUtil.getUser(request).getOpenId() == null||
				"".equals(userJsonConvertUtil.getUser(request).getOpenId())){
			json.setRes(302);
			json.setResult("openid为空，请授权");
			return json;
		}
		if(StringUtils.isEmpty(orderNo)){
			System.out.println("ordreNo----"+orderNo);
			json.setResult("orderNo不能为空");
			return json;
		}
		Order order=orderService.getOrderByNo(orderNo);
		if(order==null){
			System.out.println("ordre不存在----"+orderNo);
			json.setResult("order不存在");
			return json;
		}
		WxPlaceOrder wxOrder=new WxPlaceOrder();
		wxOrder.setAttach(orderNo);
		wxOrder.setBody("商品");
		wxOrder.setNotifyUrl(notifyUrl);
		wxOrder.setOutTradeNo(orderNo);
		wxOrder.setOpenId(userJsonConvertUtil.getUser(request).getOpenId());
		wxOrder.setTotalFee(CalculateUtils.mul(order.getOrderTotalAmount(),new BigDecimal(100)).intValue()+"");
		wxOrder.setTradeType("JSAPI");
		try {
			Map<String,String> map=unifiedOrder(wxOrder);
			json.set(SystemCode.SUCCESS.getCode(), 
					SystemCode.SUCCESS.getMsg());
			json.setObj(map);
		} catch (Exception e) {
			logger.error("微信支付下单失败",e);
		}
		return json;
	}
	@RequestMapping("/webPay")
	public JsonResponse<Map<String,String>> webPay(String orderNo,HttpServletRequest request){
		JsonResponse<Map<String,String>> json=new JsonResponse<Map<String,String>>();
		String token=request.getParameter("token");
		if(StringUtils.isEmpty(token)||"null".equals(redisUtil.get(token))){
			json.setRes(301);
			json.setResult("token is null");
			return json;
		}
		if(userJsonConvertUtil.getUser(request)==null){
			json.setRes(301);
			json.setResult("token is null");
			return json;
		}
		if(StringUtils.isEmpty(orderNo)){
			System.out.println("ordreNo----"+orderNo);
			json.setResult("orderNo不能为空");
			return json;
		}
		Order order=orderService.getOrderByNo(orderNo);
		if(order==null){
			System.out.println("ordre不存在----"+orderNo);
			json.setResult("order不存在");
			return json;
		}
		WxPlaceOrder wxOrder=new WxPlaceOrder();
		wxOrder.setAttach(orderNo);
		wxOrder.setBody("商品");
		wxOrder.setNotifyUrl(notifyUrl);
		wxOrder.setOutTradeNo(orderNo);
		wxOrder.setOpenId(userJsonConvertUtil.getUser(request).getOpenId());
		wxOrder.setTotalFee(CalculateUtils.mul(order.getOrderTotalAmount(),new BigDecimal(100)).intValue()+"");
		wxOrder.setTradeType("MWEB");
		String ip=getIpAddr(request);
		System.out.println(ip+"------------");
		wxOrder.setSpbillCreateIp(ip);
		try {
			Map<String,String> map=unifiedOrder(wxOrder);
			json.set(SystemCode.SUCCESS.getCode(), 
					SystemCode.SUCCESS.getMsg());
			json.setObj(map);
		} catch (Exception e) {
			logger.error("微信web支付下单失败",e);
		}
		return json;
	}
	@RequestMapping("/notify")
	public void notifyPay(HttpServletRequest request,HttpServletResponse response){
		notify(request, response);
	}
	@Override
	public String getCertPath() {
		// TODO Auto-generated method stub
		return null;
	}
	public  String getIpAddr(HttpServletRequest request){
    	String ip = request.getHeader("X-Real-IP");
    	if (!StringUtils.isEmpty(ip)&& !"unknown".equalsIgnoreCase(ip)) {
			return ip;
		}
    	ip = request.getHeader("X-Forwarded-For");
    	if (!StringUtils.isEmpty(ip)&& !"unknown".equalsIgnoreCase(ip)) {
    		// 多次反向代理后会有多个IP值，第一个为真实IP。
    		int index = ip.indexOf(',');
    		if (index != -1) {
    			return ip.substring(0, index);
    		} else {
    			return ip;
    		}
		} else {
			return request.getRemoteAddr();
		}
    }
}
