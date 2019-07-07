package com.ande.buyb2c.web.contorller.order;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ande.buyb2c.common.generateOrderNo.ISerialNumberService;
import com.ande.buyb2c.common.util.AbstractController;
import com.ande.buyb2c.common.util.JsonResponse;
import com.ande.buyb2c.common.util.PageResult;
import com.ande.buyb2c.common.util.SystemCode;
import com.ande.buyb2c.order.RefundOrderCode;
import com.ande.buyb2c.order.dao.OrderDetailMapper;
import com.ande.buyb2c.order.entity.Order;
import com.ande.buyb2c.order.entity.OrderDetail;
import com.ande.buyb2c.order.entity.RefundOrder;
import com.ande.buyb2c.order.service.IOrderDetailService;
import com.ande.buyb2c.order.service.IOrderService;
import com.ande.buyb2c.order.service.IRefundOrderService;
import com.ande.buyb2c.web.UserJsonConvertUtil;

/**
 * @author chengzb
 * @date 2018年2月6日上午10:07:12
 * 退款单
 */
@RestController
@RequestMapping("/front/refundOrder")
public class RefundOrderController extends AbstractController{
	@Resource
	private IOrderService orderService;
	@Resource
	private IOrderDetailService orderDetailService;
	@Resource
	private OrderDetailMapper orderDetailMapper;
	@Resource
	private UserJsonConvertUtil userJsonConvertUtil;
	@Resource
	private IRefundOrderService refundOrderService;
	@Resource
	private ISerialNumberService serialNumberService;
	/**
	 * 
	 * @param page
	 * @param detail
	 * @return  查询可退款的商品列表
	 */
	@RequestMapping("/getCanRefundOrderPage")
	public JsonResponse<PageResult<Map<String,Object>>> getCanRefundOrderPage(PageResult<Map<String,Object>> page,HttpServletRequest request){
		JsonResponse<PageResult<Map<String,Object>>> json=new JsonResponse<PageResult<Map<String,Object>>>();
		orderService.getCanRefundOrderPage(page,userJsonConvertUtil.getUser(request).getUserId());
		if(page.getTotal()!=0){
			json.set(SystemCode.SUCCESS.getCode(), 
					SystemCode.SUCCESS.getMsg());
			json.setObj(page);
		}
		return json;
	}
	/**
	 * 
	 * @param page
	 * @param detail
	 * @return  查询是否应该退运费
	 */
	@RequestMapping("/getCanRefundFreight")
	public JsonResponse<String> getCanRefundFreight(Integer orderId){
		JsonResponse<String> json=new JsonResponse<String>();
		if(orderService.getCanRefundFreight(orderId)==1){
			//可退运费
			json.setRes(SystemCode.SUCCESS.getCode());
		}
		return json;
	}
	/**
	 * 
	 * @param request
	 * @return
	 * 申请退款
	 */
	@RequestMapping("/refundOrder")
	public JsonResponse<String> refundOrder(HttpServletRequest request,@RequestBody RefundOrder order){
		JsonResponse<String> json=new JsonResponse<String>();
		try {
			OrderDetail detail=orderDetailService.selectByPrimaryKey(order.getOrderDetailId());
			Order o=orderService.selectByPrimaryKey(order.getOrderId());
			order.setOrderNo(o.getOrderNo());
			order.setRefundApplyTime(new Date());
			order.setCustomerId(userJsonConvertUtil.getUser(request).getUserId());
			order.setRefundOrderNo("RO"+serialNumberService.getOrderNO("buyb2c:order")+""+userJsonConvertUtil.getUser(request).getUserId());
			order.setGoodsId(detail.getGoodsId());
			order.setGoodsNo(detail.getGoodsNo());
			order.setGoodNum(detail.getGoodsNum());
			order.setGoodsAttribute(detail.getGoodsAttribute());
			order.setGoodsImage(detail.getGoodsImage());
			order.setGoodsName(detail.getGoodsName());
			order.setGoodsPrice(detail.getGoodsPrice());
			order.setReundGoodsPrice(detail.getGoodsTotalPrice());
			order.setCustomerName(o.getCustomerName());
			order.setCustomerAddress(o.getCustomerAddress());
			order.setCustomerPhone(o.getCustomerPhone());
			refundOrderService.insertSelective(order);
				//退款申请成功 更改订单中商品状态
				//OrderDetail detail=new OrderDetail();
				detail.setOrderDetailId(order.getOrderDetailId());
				detail.setGoodsState(RefundOrderCode.WAIT_REFUND.getCode());//设置为退款中
			orderDetailMapper.updateByPrimaryKeySelective(detail);
			json.set(SystemCode.SUCCESS.getCode(), 
					SystemCode.SUCCESS.getMsg());
		} catch (Exception e) {
			logError("申请退款失败", e);
		}
		return json;
	}
	/**
	 * 获取退款列表
	 * @return
	 */
	@RequestMapping("/getRefundOrderPage")
	public JsonResponse<PageResult<RefundOrder>> getRefundOrderPage(PageResult<RefundOrder> page,RefundOrder refundOrder,HttpServletRequest request){
		JsonResponse<PageResult<RefundOrder>> json=new JsonResponse<PageResult<RefundOrder>>();
		refundOrder.setCustomerId(userJsonConvertUtil.getUser(request).getUserId());
		refundOrderService.queryByPageFront(page, refundOrder);
		if(page.getTotal()!=0){
			json.set(SystemCode.SUCCESS.getCode(), 
					SystemCode.SUCCESS.getMsg());
			json.setObj(page);
		}
		return json;
	}
	/**
	 * 获取退款详情
	 * @return
	 */
	@RequestMapping("/getRefundOrder")
	public JsonResponse<RefundOrder> getRefundOrder(Integer refundOrderId){
		JsonResponse<RefundOrder> json=new JsonResponse<RefundOrder>();
		json.set(SystemCode.SUCCESS.getCode(), 
				SystemCode.SUCCESS.getMsg());
		json.setObj(refundOrderService.selectByPrimaryKey(refundOrderId));
		return json;
	}
	/**
	 * 撤销退款申请
	 * @return
	 */
	@RequestMapping("/updateRefundOrderState")
	public JsonResponse<RefundOrder> updateRefundOrderState(Integer refundOrderId,Integer orderDetailId){
		JsonResponse<RefundOrder> json=new JsonResponse<RefundOrder>();
		RefundOrder order=new RefundOrder();
		order.setRefundOrderId(refundOrderId);
		order.setRefundState(RefundOrderCode.CANCEL_REFUND_APPLICATION.getCode());
		
		OrderDetail detail=new OrderDetail();
		detail.setOrderDetailId(orderDetailId);
		detail.setGoodsState(RefundOrderCode.CANCEL_REFUND_APPLICATION.getCode());//设置为取消退款
		try {
			refundOrderService.updateByPrimaryKeySelective(order);
			orderDetailMapper.updateByPrimaryKeySelective(detail);
		
			json.set(SystemCode.SUCCESS.getCode(), 
					SystemCode.SUCCESS.getMsg());
		} catch (Exception e) {
			logError("撤销退款申请异常", e);
		}
		return json;
	}
}
