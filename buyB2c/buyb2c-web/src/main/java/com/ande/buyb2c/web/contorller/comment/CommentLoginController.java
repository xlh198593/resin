package com.ande.buyb2c.web.contorller.comment;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ande.buyb2c.comment.entity.Comment;
import com.ande.buyb2c.comment.service.ICommentService;
import com.ande.buyb2c.common.util.AbstractController;
import com.ande.buyb2c.common.util.JsonResponse;
import com.ande.buyb2c.common.util.SystemCode;
import com.ande.buyb2c.order.OrderCode;
import com.ande.buyb2c.order.entity.Order;
import com.ande.buyb2c.order.entity.OrderDetail;
import com.ande.buyb2c.order.service.IOrderDetailService;
import com.ande.buyb2c.order.service.IOrderService;
import com.ande.buyb2c.user.entity.User;
import com.ande.buyb2c.web.UserJsonConvertUtil;

/**
 * @author chengzb
 * @date 2018年2月23日下午6:11:29
 */
@RestController
@RequestMapping("/front/comment")
public class CommentLoginController extends AbstractController{
	@Resource
	private ICommentService commentService;
	@Resource
	private UserJsonConvertUtil userJsonConvertUtil;
	@Resource
	private IOrderService orderService;
	@Resource
	private IOrderDetailService orderDetailService;
	@RequestMapping("/addComment")
	public JsonResponse<String> addComment(HttpServletRequest request,@RequestBody List<Comment> commentList){
		JsonResponse<String> json=new JsonResponse<String>();
		try {
			Integer orderId=null;
			User user =	userJsonConvertUtil.getUser(request);
			for(Comment c:commentList){
				if(orderId==null){
					orderId=c.getOrderId();
				}
				if(orderId==null||c.getOrderDetailId()==null){
					json.setResult("orderDetailId或者orderId为空");
					return json;
				}
				c.setCustomerId(user.getUserId());
				c.setCustomerName(user.getName());
				c.setCustomerPhone(user.getPhone());
				c.setCreateTime(new Date());
				
				OrderDetail orderDetail = orderDetailService.selectByPrimaryKey(c.getOrderDetailId());
				c.setGoodsAttribute(orderDetail.getGoodsAttribute());
				c.setGoodsId(orderDetail.getGoodsId());
				c.setGoodsImage(orderDetail.getGoodsImage());
				c.setGoodsName(orderDetail.getGoodsName());
				c.setGoodsNo(orderDetail.getGoodsNo());
				c.setGoodsNum(Integer.valueOf(orderDetail.getGoodsNum()));
				c.setGoodsPrice(orderDetail.getGoodsPrice());
				c.setGoodsTotalAmount(orderDetail.getGoodsTotalPrice());
				
				Order order = orderService.selectByPrimaryKey(c.getOrderId());
				c.setOrderNo(order.getOrderNo());
				c.setOrderCreateTime(order.getCreateTime());
			}
			commentService.addBatch(commentList);
			Order order=new Order();
			order.setOrderId(orderId);
			order.setOrderState(OrderCode.ALREADY_COMPLETE.getCode());//已完成订单
			orderService.updateByPrimaryKeySelective(order);
			json.set(SystemCode.SUCCESS.getCode(), 
					SystemCode.SUCCESS.getMsg());
		} catch (Exception e) {
			logError("添加评论失败", e);
		}
		return json;
	}
}
