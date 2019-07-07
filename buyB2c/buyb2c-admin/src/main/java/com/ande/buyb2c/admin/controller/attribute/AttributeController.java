package com.ande.buyb2c.admin.controller.attribute;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ande.buyb2c.attribute.entity.Attribute;
import com.ande.buyb2c.attribute.service.IAttributeService;
import com.ande.buyb2c.common.util.AbstractController;
import com.ande.buyb2c.common.util.JsonResponse;
import com.ande.buyb2c.common.util.PageResult;
import com.ande.buyb2c.common.util.SessionUtil;
import com.ande.buyb2c.common.util.SystemCode;
import com.ande.buyb2c.user.entity.AdminUser;

/**
 * @author chengzb
 * @date 2018年1月30日上午9:51:18
 * 属性
 */
@RestController
@RequestMapping("/admin/attribute")
public class AttributeController extends AbstractController{
	@Resource
	private IAttributeService attributeService;
	@Resource
	private SessionUtil<AdminUser> sessionUtil;
	/**
	 * 
	 * @param page
	 * @param 
	 * @return  查询详情
	 */
	@RequestMapping("/getAttributeById")
	public JsonResponse<Attribute> getLogisticsById(Integer attributeId){
		JsonResponse<Attribute> json=new JsonResponse<Attribute>();
		Attribute attribute = attributeService.selectByPrimaryKey(attributeId);
		if(attribute!=null){
			json.set(SystemCode.SUCCESS.getCode(), 
					SystemCode.SUCCESS.getMsg());
			json.setObj(attribute);
		}
		return json;
	}
	/**
	 * 
	 * @param page
	 * @param logistics
	 * @return  查询列表
	 */
	@RequestMapping("/getAttributePage")
	public JsonResponse<PageResult<Attribute>> getAttributePage(PageResult<Attribute> page,Attribute attribute){
		JsonResponse<PageResult<Attribute>> json=new JsonResponse<PageResult<Attribute>>();
		attributeService.queryByPage(page, attribute);
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
	 * @param logistics
	 * @return  新增
	 */
	@RequestMapping("/addAttribute")
	public JsonResponse<String> addAttribute(HttpServletRequest request,@RequestBody Attribute attribute){
		JsonResponse<String> json=new JsonResponse<String>();
		try {
			attribute.setCreateTime(new Date());
			attribute.setUpdateTime(new Date());
			attribute.setAdminId(sessionUtil.getAdminUser(request).getAdminId());
			if("1".equals(attribute.getAttributeType())){
				//1 复选框 2文本框
				attributeService.addAttribute(request, attribute);
			}else{
				attributeService.insertSelective(attribute);
			}
			json.set(SystemCode.SUCCESS.getCode(), 
					SystemCode.SUCCESS.getMsg());
		} catch (Exception e) {
			logError("新增属性异常", e);
		}
		return json;
	}
	/**
	 * 
	 * @param page
	 * @param logistics
	 * @return  修改属性
	 */
	@RequestMapping("/updateAttribute")
	public JsonResponse<String> updateAttribute(HttpServletRequest request,@RequestBody Attribute attribute){
		JsonResponse<String> json=new JsonResponse<String>();
		try {
			attribute.setUpdateTime(new Date());
			if("1".equals(attribute.getAttributeType())){
				//1 复选框 2文本框
				attributeService.updateAttribute(request, attribute);
			}else if("2".equals(attribute.getAttributeType())){
				attributeService.updateByPrimaryKeySelective(attribute);
			}
			json.set(SystemCode.SUCCESS.getCode(), 
					SystemCode.SUCCESS.getMsg());
		} catch (Exception e) {
			logError("编辑属性异常", e);
		}
		return json;
	}
	/**
	 * 
	 * @param page
	 * @param logistics
	 * @return  删除属性
	 */
	@RequestMapping("/delAttribute")
	public JsonResponse<String> delAttribute(Integer attributeId,String attributeType){
		JsonResponse<String> json=new JsonResponse<String>();
		try {
			if("1".equals(attributeType)){
				//1 复选框
				attributeService.delAttribute(attributeId);
			}else if("2".equals(attributeType)){
				// 2文本框
				attributeService.deleteByPrimaryKey(attributeId);
			}
			json.set(SystemCode.SUCCESS.getCode(), 
					SystemCode.SUCCESS.getMsg());
		} catch (Exception e) {
			logError("编辑属性异常", e);
		}
		return json;
	}
}
