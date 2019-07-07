package com.ande.buyb2c.admin.controller.order;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ande.buyb2c.common.util.AbstractController;
import com.ande.buyb2c.common.util.JsonResponse;
import com.ande.buyb2c.common.util.PageResult;
import com.ande.buyb2c.common.util.SystemCode;
import com.ande.buyb2c.order.entity.RefundOrder;
import com.ande.buyb2c.order.service.IRefundOrderService;
import com.ande.buyb2c.order.vo.RequestRefundVo;

/**
 * @author chengzb
 * @date 2018年2月6日下午5:51:10
 */
@RestController
@RequestMapping("/admin/refundOrder")
public class RefundOrderController extends AbstractController{
	@Resource
	private IRefundOrderService refundOrderService;
	/**
	 * 退款单列表
	 * @param page
	 * @param comment
	 * @return
	 */
	@RequestMapping("/getRefundOrderPage")
	public JsonResponse<PageResult<RefundOrder>> getRefundOrderPage(PageResult<RefundOrder> page,RequestRefundVo comment){
		JsonResponse<PageResult<RefundOrder>> json=new JsonResponse<PageResult<RefundOrder>>();
		refundOrderService.queryByPage(page, comment);
		if(page.getTotal()!=0){
			json.set(SystemCode.SUCCESS.getCode(), 
					SystemCode.SUCCESS.getMsg());
			json.setObj(page);
		}
		return json;
	}
	/**
	 * 退款单详情
	 * @param page
	 * @param comment
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
	

}
