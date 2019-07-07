package com.ande.buyb2c.admin.controller.content;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ande.buyb2c.common.util.AbstractController;
import com.ande.buyb2c.common.util.JsonResponse;
import com.ande.buyb2c.common.util.SessionUtil;
import com.ande.buyb2c.common.util.SystemCode;
import com.ande.buyb2c.content.entity.Content;
import com.ande.buyb2c.content.service.IContentService;
import com.ande.buyb2c.user.entity.AdminUser;

/**
 * @author chengzb
 * @date 2018年1月27日下午2:14:38
 * 内容模块 (注册协议  售后服务)已测
 */
@RestController
@RequestMapping("/admin/content")
public class ContentController extends AbstractController{
	@Resource
	private IContentService contentService;
	@Resource
	private SessionUtil<AdminUser> sessionUtil;
	/**
	 * 
	 * @param page
	 * @param logistics
	 * @return  查询内容详情
	 */
	@RequestMapping("/getContentById")
	public JsonResponse<Content> getContentById(Integer contentId){
		JsonResponse<Content> json=new JsonResponse<Content>();
		Content content = contentService.selectByPrimaryKey(contentId);
		if(content!=null){
			json.set(SystemCode.SUCCESS.getCode(), 
					SystemCode.SUCCESS.getMsg());
			json.setObj(content);
		}
		return json;
	}
	/**
	 * 
	 * @param page
	 * @param logistics
	 * @return  新增内容
	 */
	@RequestMapping("/addContent")
	public JsonResponse<Content> addContent(HttpServletRequest request,Content content){
		JsonResponse<Content> json=new JsonResponse<Content>();
	 try {
		 content.setCreateTime(new Date());
		 content.setUpdateTime(new Date());
		 content.setAdminId(sessionUtil.getAdminUser(request).getAdminId());
		contentService.insertSelective(content);
		json.set(SystemCode.SUCCESS.getCode(), 
				SystemCode.SUCCESS.getMsg());
	} catch (Exception e) {
		logError("新增内容模板异常", e);
	}
		return json;
	}
	/**
	 * 
	 * @param page
	 * @param logistics
	 * @return  编辑内容
	 */
	@RequestMapping("/updateContent")
	public JsonResponse<Content> updateContent(Content content){
		JsonResponse<Content> json=new JsonResponse<Content>();
	 try {
		 content.setUpdateTime(new Date());
		contentService.updateByPrimaryKeySelective(content);
		json.set(SystemCode.SUCCESS.getCode(), 
				SystemCode.SUCCESS.getMsg());
	} catch (Exception e) {
		logError("更新内容模板异常", e);
	}
		return json;
	}
}
