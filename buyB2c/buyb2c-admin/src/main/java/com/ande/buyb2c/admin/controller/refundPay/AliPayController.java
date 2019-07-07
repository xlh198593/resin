package com.ande.buyb2c.admin.controller.refundPay;


import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ande.base.pay.alipay.AbstractAliPay;
import com.ande.base.pay.alipay.AliConfig;
import com.ande.buyb2c.common.util.JsonResponse;
import com.ande.buyb2c.common.util.SystemCode;
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
 * @date 2018年2月5日下午6:19:01
 */
@RestController
@RequestMapping("/admin/aliPay")
public class AliPayController extends AbstractAliPay{
	@Resource
	private OrderDetailMapper orderDetailMapper;
	protected   final Logger logger = LoggerFactory.getLogger(this.getClass());  
	@Value("${app.alipay.aliPayPublicKey}")
	private   String aliPayPublicKey;
	@Value("${app.alipay.appId}")
	private   String appId;
	@Value("${app.alipay.appPrivatKey}")
	private   String appPrivatKey;
	@Resource
	private IRefundOrderService refundOrderService;
	@Resource
	private IOrderService orderService;
	@RequestMapping("/refundPay")
	public JsonResponse<String> refundPay(Integer refundOrderId,Double refundPrice){
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
		 try {
			 
			boolean su=refund(refundOrder.getOrderNo(), "", refundPrice,refundOrder.getRefundOrderNo());
			if(su){
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
					json.set(SystemCode.SUCCESS.getCode(), 
							SystemCode.SUCCESS.getMsg());
			}
		} catch (Exception e) {
			logger.error("支付宝退款异常",e);
		}
		return json;
		
	}
	@Override
	public Integer updateOrderStatus(String attach, String trade_no)
			throws Exception {
	return null;
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
}
