package com.ande.buyb2c.web.contorller.comment;


import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ande.buyb2c.comment.entity.Comment;
import com.ande.buyb2c.comment.service.ICommentService;
import com.ande.buyb2c.common.util.AbstractController;
import com.ande.buyb2c.common.util.JsonResponse;
import com.ande.buyb2c.common.util.PageResult;
import com.ande.buyb2c.common.util.SystemCode;

/**
 * @author chengzb
 * @date 2018年2月2日上午11:03:41
 * 评价
 */
@RestController
@RequestMapping("/comment")
public class CommentController extends AbstractController{
	@Resource
	private ICommentService commentService;
	/**
	 * 
	 * @param page  
	 * @return  评价列表
	 */
	@RequestMapping("/getCommentPageByGoodsId")
	public JsonResponse<PageResult<Comment>> getCommentPageByGoodsId(PageResult<Comment> page,Comment comment){
		JsonResponse<PageResult<Comment>> json=new JsonResponse<PageResult<Comment>>();
		if(comment.getGoodsId()==null){
			json.setResult("goodsId不能为空");
			return json;
		}
		commentService.queryByPageFront(page, comment);
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
	 * @return  评价统计
	 */
	@RequestMapping("/getCommentCount")
	public JsonResponse<Integer> getCommentCount(Integer goodsId){
		JsonResponse<Integer> json=new JsonResponse<Integer>();
		List<Integer> list = commentService.getCommentCount(goodsId);
		if(list!=null&&list.size()!=0){
			json.set(SystemCode.SUCCESS.getCode(), 
					SystemCode.SUCCESS.getMsg());
			json.setList(list);
		}
		return json;
	}
}
