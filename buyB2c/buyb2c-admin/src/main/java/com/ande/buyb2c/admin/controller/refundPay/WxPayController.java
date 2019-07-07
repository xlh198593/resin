package com.ande.buyb2c.admin.controller.refundPay;


import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ande.base.pay.wxpay.AbstractWXPay;
import com.ande.base.pay.wxpay.WxPlaceOrder;
import com.ande.buyb2c.common.util.JsonResponse;
import com.ande.buyb2c.common.util.SystemCode;
import com.ande.buyb2c.order.CalculateUtils;
import com.ande.buyb2c.order.OrderCode;
import com.ande.buyb2c.order.PayCode;
import com.ande.buyb2c.order.RefundOrderCode;
import com.ande.buyb2c.order.dao.OrderDetailMapper;
import com.ande.buyb2c.order.entity.Order;
import com.ande.buyb2c.order.entity.OrderDetail;
import com.ande.buyb2c.order.entity.RefundOrder;
import com.ande.buyb2c.order.service.IOrderService;
import com.ande.buyb2c.order.service.IRefundOrderService;

/**
 * @author chengzb
 * @date 2018年2月5日下午4:35:22
 */
@RestController
@RequestMapping("/admin/wxPay")
public class WxPayController extends AbstractWXPay{
	public  final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Resource
	private IOrderService orderService;
	@Resource
	private IRefundOrderService refundOrderService;
	@Resource
	private OrderDetailMapper orderDetailMapper;
	@Value("${app.wxpay.appId}")
	private String appId;
	@Value("${app.wxpay.mchId}")
	private String mchId;
	@Value("${app.wxpay.key}")
	private String key;
	@Value("${app.wxpay.certPath}")
	private String certPath;
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
		return null;
	}
	@RequestMapping("/refundPay")
	public JsonResponse<String> refundPay(Integer refundOrderId,Double refundPrice,Double totalPrice){
		JsonResponse<String> json=new  JsonResponse<String> ();
		if(refundOrderId==null){
			json.setResult("refundOrderId不能为空");
			return json;
		}
		RefundOrder refundOrder = refundOrderService.selectByPrimaryKey(refundOrderId);
		if(refundOrder==null){
			json.setResult("退款单不存在");
			return json;
		}
		if(refundPrice>refundOrder.getRefundApplyPrice().doubleValue()){
			//退款金额数目不对
			json.setResult("退款金额大于可退金额");
			return json;
		}
		WxPlaceOrder order=new WxPlaceOrder();
		order.setOutTradeNo(refundOrder.getOrderNo());
		Double d=new BigDecimal(totalPrice).multiply(new BigDecimal(100)).doubleValue();
		order.setTotalFee(d.intValue()+"");
		try {
			 d=new BigDecimal(refundPrice).multiply(new BigDecimal(100)).doubleValue();
			 Map<String, String> map =refundOrder(order,refundOrder.getRefundOrderNo(),d.intValue()+"");
			  if("SUCCESS".equals(map.get("return_code"))&&"SUCCESS".equals(map.get("result_code"))){
				//退款成功
				  RefundOrder r=new RefundOrder();
					 r.setRefundOrderId(refundOrderId);
					 r.setRefundState(RefundOrderCode.ALREADY_REFUND.getCode());
					 r.setRefundTime(new Date());
					 r.setRefundPrice(new BigDecimal(refundPrice));
					
					OrderDetail detail=new OrderDetail();
					detail.setOrderDetailId(refundOrder.getOrderDetailId());
					detail.setGoodsState(RefundOrderCode.ALREADY_REFUND.getCode());//设置为退款成功
						refundOrderService.updateByPrimaryKeySelective(r);
						orderDetailMapper.updateByPrimaryKeySelective(detail);
						
						//判断订单中的商品是否全部退款成功  如果是则将订单状态置为待评价
						if(orderDetailMapper.getRefundCount(refundOrder.getOrderId())==0){
							Order o=new Order();
							o.setOrderId(refundOrder.getOrderId());
							o.setOrderState(OrderCode.WAIT_EVALUATE.getCode());
							o.setPayState(PayCode.ALREADY_REFUND.getCode());
							orderService.updateByPrimaryKeySelective(o);
						}
				  json.setRes(SystemCode.SUCCESS.getCode());
				  json.setResult(SystemCode.SUCCESS.getMsg());
	            }else{
	            	logger.error("退款失败，微信返回:--"+map);
	            }
		} catch (Exception e) {
			logger.error("退款异常", e);
		}
		return json;
	}
	@Override
	public String getCertPath() {
		return certPath;
	}
public static void main(String[] args) {
	Double d=new BigDecimal(0.03).multiply(new BigDecimal(100)).doubleValue();
	System.out.println(d.intValue());
}
}
