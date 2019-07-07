package com.ande.buyb2c.admin.controller.comment;

import javax.annotation.Resource;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ande.buyb2c.comment.entity.Comment;
import com.ande.buyb2c.comment.service.ICommentService;
import com.ande.buyb2c.comment.vo.RequestCommentVo;
import com.ande.buyb2c.common.util.AbstractController;
import com.ande.buyb2c.common.util.JsonResponse;
import com.ande.buyb2c.common.util.PageResult;
import com.ande.buyb2c.common.util.SystemCode;
/**
 * @author chengzb
 * @date 2018年2月1日上午9:08:08
 */
@RestController
@RequestMapping("/admin/comment")
public class CommentController extends AbstractController{
	@Resource
	private ICommentService commentService;
	/**
	 * @return  查询详情
	 */
	@RequestMapping("/getCommentById")
	public JsonResponse<Comment> getCommentById(Integer commentId){
		JsonResponse<Comment> json=new JsonResponse<Comment>();
		if(commentId==null){
			json.setResult("commentId不能为空");
			return json;
		}
		json.set(SystemCode.SUCCESS.getCode(), 
				SystemCode.SUCCESS.getMsg());
		json.setObj(commentService.selectByPrimaryKey(commentId));
		return json;
	}
	/**
	 * 开启/关闭评论  commentId  isShow是否显示 1是 2否
	 */
	@RequestMapping("/openComment")
	public JsonResponse<String> openComment(Comment comment){
		JsonResponse<String> json=new JsonResponse<String>();
		if(comment.getCommentId()==null||StringUtils.isEmpty(comment.getIsShow())){
			json.setResult("commentId,isShow不能为空");
			return json;
		}
		try {
			commentService.updateByPrimaryKeySelective(comment);
			json.set(SystemCode.SUCCESS.getCode(), 
					SystemCode.SUCCESS.getMsg());
		} catch (Exception e) {
			logError("开启评论异常", e);
		}
		return json;
	}
	/**
	 * 
	 * @param page
	 * @param logistics
	 * @return  查询列表
	 */
	@RequestMapping("/getCommentPage")
	public JsonResponse<PageResult<Comment>> getCommentPage(PageResult<Comment> page,RequestCommentVo comment){
		JsonResponse<PageResult<Comment>> json=new JsonResponse<PageResult<Comment>>();
		commentService.queryByPage(page, comment);
		if(page.getTotal()!=0){
			json.set(SystemCode.SUCCESS.getCode(), 
					SystemCode.SUCCESS.getMsg());
			json.setObj(page);
		}
		return json;
	}
}
