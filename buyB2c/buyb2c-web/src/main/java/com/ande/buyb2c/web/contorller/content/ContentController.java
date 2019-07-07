package com.ande.buyb2c.web.contorller.content;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ande.buyb2c.common.util.AbstractController;
import com.ande.buyb2c.common.util.JsonResponse;
import com.ande.buyb2c.common.util.SystemCode;
import com.ande.buyb2c.content.entity.Content;
import com.ande.buyb2c.content.service.IContentService;

/**
 * @author chengzb
 * @date 2018年2月2日上午11:17:52
 * 静态内容
 */
@RestController
@RequestMapping("/content")
public class ContentController extends AbstractController{
	@Resource
	private IContentService contentService;
	/**
	 *  类型:1注册协议 2 售后服务
	 */
	@RequestMapping("/getContentService")
	public JsonResponse<Content> getCustomerService(String type){
		JsonResponse<Content> json=new JsonResponse<Content>();
		if(type==null){
			json.setResult("type不能为空");
			return json;
		}
		json.set(SystemCode.SUCCESS.getCode(), 
				SystemCode.SUCCESS.getMsg());
		json.setObj(contentService.getConentByType(type));
		return json;
	}
	

}
