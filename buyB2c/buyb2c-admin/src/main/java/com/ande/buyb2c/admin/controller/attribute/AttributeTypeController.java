package com.ande.buyb2c.admin.controller.attribute;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ande.buyb2c.attribute.dao.AttributeTypeAttributeMapper;
import com.ande.buyb2c.attribute.entity.Attribute;
import com.ande.buyb2c.attribute.entity.AttributeType;
import com.ande.buyb2c.attribute.entity.AttributeTypeAttribute;
import com.ande.buyb2c.attribute.service.IAttributeService;
import com.ande.buyb2c.attribute.service.IAttributeTypeAttributeService;
import com.ande.buyb2c.attribute.service.IAttributeTypeService;
import com.ande.buyb2c.attribute.vo.AttributeVo;
import com.ande.buyb2c.common.util.AbstractController;
import com.ande.buyb2c.common.util.JsonResponse;
import com.ande.buyb2c.common.util.SessionUtil;
import com.ande.buyb2c.common.util.SystemCode;
import com.ande.buyb2c.user.entity.AdminUser;

/**
 * @author chengzb
 * @date 2018年1月30日上午11:51:01
 * 
 */
@RestController
@RequestMapping("/admin/attributeType")
public class AttributeTypeController extends AbstractController{
@Resource
private IAttributeTypeService attributeTypeService;
@Resource
private SessionUtil<AdminUser> sessionUtil;
@Resource
private IAttributeTypeAttributeService attributeTypeAttributeService;
@Resource
private IAttributeService attributeService;
@Resource
private AttributeTypeAttributeMapper attributeTypeAttributeMapper;
@RequestMapping("/getAttributeType")
public JsonResponse<AttributeType> getAttributeType(Integer attributeTypeId){
	JsonResponse<AttributeType> json=new JsonResponse<AttributeType>();
	if(attributeTypeId==null){
		json.setResult("attributeTypeId不能为空");
		return json;
	}
	json.set(SystemCode.SUCCESS.getCode(), 
			SystemCode.SUCCESS.getMsg());
	json.setObj(attributeTypeService.selectByPrimaryKey(attributeTypeId));
	return json;
}
@RequestMapping("/addOrUpdateAttributeType")
public JsonResponse<String> addOrUpdateAttributeType(HttpServletRequest request,@RequestBody AttributeType attributeType){
	JsonResponse<String> json=new JsonResponse<String>();
	try {
		attributeType.setUpdateTime(new Date());
		if(attributeType.getAttributeTypeId()==null){
			//新增
			attributeType.setCreateTime(new Date());
			attributeType.setAdminId(sessionUtil.getAdminUser(request).getAdminId());
			attributeTypeService.insertSelective(attributeType);
			List<AttributeTypeAttribute> attributeTypeAttributeList = attributeType.getAttributeTypeAttributeList();
			if(attributeTypeAttributeList!=null&&attributeTypeAttributeList.size()!=0){
				for(AttributeTypeAttribute ata:attributeTypeAttributeList){
					ata.setCreateTime(new Date());
					ata.setAttributeTypeId(attributeType.getAttributeTypeId());
					ata.setAdminId(sessionUtil.getAdminUser(request).getAdminId());
				}
				attributeTypeAttributeMapper.addBatch(attributeTypeAttributeList);
			}
		}else{
			//编辑
			attributeTypeService.updateByPrimaryKeySelective(attributeType);
			if(attributeType.getAttributeTypeAttributeList()!=null&&attributeType.getAttributeTypeAttributeList().size()!=0){
				attributeTypeAttributeService.addAttributeTypeAttribute(request, attributeType.getAttributeTypeAttributeList());
			}
		}
		json.set(SystemCode.SUCCESS.getCode(), 
				SystemCode.SUCCESS.getMsg());
	} catch (Exception e) {
		if(e.getMessage().indexOf("for key 'sort'")!=-1){
			json.setRes(SystemCode.FIELD_REPETITION.getCode());
			json.setResult("排序字段重复");
		}
		logError("新增或编辑属性分类异常", e);
	}
	return json;
}
@RequestMapping("/delAttributeType")
public JsonResponse<String> delAttributeType(Integer attributeTypeId,String attributeTypeLevel){
	JsonResponse<String> json=new JsonResponse<String>();
	if(StringUtils.isEmpty(attributeTypeLevel)||attributeTypeId==null){
		json.setResult("attributeTypeId且attributeTypeLevel不能为空");
		return json;
	}
	List<AttributeTypeAttribute> list=attributeTypeAttributeService.ifCanDel(attributeTypeLevel, attributeTypeId);
	if(null!=list&&list.size()!=0){
		//当前分类 有属性关联不允许删除
		json.setRes(SystemCode.NOT_ALLOW_OPERATION.getCode());
		json.setResult("当前分类有属性关联不允许删除");
		return json;
	}
	//删除 当前分类以及子分类
	try {
		attributeTypeService.delAttributeType(attributeTypeId, attributeTypeLevel);
		json.set(SystemCode.SUCCESS.getCode(), 
				SystemCode.SUCCESS.getMsg());
	} catch (Exception e) {
		logError("删除属性分类异常", e);
	}
	return json;
}
/**
 * 
 * @param page
 * @param 
 * @return  属性分类 树形结构(包括列表页数据)
 */
@RequestMapping("/getAttributeTypeList")
public JsonResponse<AttributeType> getAttributeTypeList(){
	JsonResponse<AttributeType> json=new JsonResponse<AttributeType>();
	json.set(SystemCode.SUCCESS.getCode(), 
			SystemCode.SUCCESS.getMsg());
	json.setList(attributeTypeService.getAttributeTypeList());
	return json;
	
}

/**
 * 
 * @param page
 * @param 
 * @return  属性分类 级联查询
 */
@RequestMapping("/getAttributeTypeListByParentId")
public JsonResponse<AttributeType> getAttributeTypeListByParentId(Integer parentId){
	JsonResponse<AttributeType> json=new JsonResponse<AttributeType>();
	List<AttributeType> list = attributeTypeService.getAttributeTypeListByParentId(parentId);
	if(list!=null&&list.size()!=0){
		json.set(SystemCode.SUCCESS.getCode(), 
				SystemCode.SUCCESS.getMsg());
		json.setList(list);
	}
	return json;
}
/**
 * 
 * @param page
 * @param 
 * @return  根据属性分类  查其关联的属性及属性值(添加商品时查询)
 */
@RequestMapping("/getAttributeListByType")
public JsonResponse<AttributeVo> getAttributeListByType(Integer attributeTypeId){
	JsonResponse<AttributeVo> json=new JsonResponse<AttributeVo>();
	List<AttributeVo> list = attributeTypeAttributeService.getAttributeTypeAttributeList(attributeTypeId);
	if(list!=null&&list.size()!=0){
		json.set(SystemCode.SUCCESS.getCode(), 
				SystemCode.SUCCESS.getMsg());
		json.setList(list);
	}
	return json;
}
/**
 * 
 * @param page
 * @param 
 * @return  关联属性 查询左边的商品属性
 */
@RequestMapping("/getAttributeListLeft")
public JsonResponse<Attribute> getAttributeListLeft(Integer attributeTypeId){
	JsonResponse<Attribute> json=new JsonResponse<Attribute>();
	if(StringUtils.isEmpty(attributeTypeId)){
		json.setResult("attributeTypeId不能为空");
		return json;
	}
	List<Attribute> list = attributeService.getAttributeList(attributeTypeId);
	if(list!=null&&list.size()!=0){
		json.set(SystemCode.SUCCESS.getCode(), 
				SystemCode.SUCCESS.getMsg());
		json.setList(list);
	}
	return json;
}
/**
 * 
 * @param page
 * @param 
 * @return  关联属性 查询右边的商品属性(已关联的)
 */
@RequestMapping("/getAttributeListRight")
public JsonResponse<AttributeTypeAttribute> getAttributeListRight(Integer attributeTypeId){
	JsonResponse<AttributeTypeAttribute> json=new JsonResponse<AttributeTypeAttribute>();
	if(StringUtils.isEmpty(attributeTypeId)){
		json.setResult("attributeTypeId不能为空");
		return json;
	}
	List<AttributeTypeAttribute> attributeListRight = attributeTypeAttributeService.getAttributeListRight(attributeTypeId);
	if(attributeListRight!=null&&attributeListRight.size()!=0){
		json.set(SystemCode.SUCCESS.getCode(), 
				SystemCode.SUCCESS.getMsg());
		json.setList(attributeListRight);
	}
	
	return json;
}
/**
 * 
 * @param page
 * @param 
 * @return  关联属性 到属性分类
 */
@RequestMapping("/addAttributeToType")
public JsonResponse<String> addAttributeToType(@RequestBody List<AttributeTypeAttribute> list,HttpServletRequest request){
	JsonResponse<String> json=new JsonResponse<String>();
	try {
		attributeTypeAttributeService.addAttributeTypeAttribute(request, list);
		json.set(SystemCode.SUCCESS.getCode(), 
				SystemCode.SUCCESS.getMsg());
	} catch (Exception e) {
		logError("关联属性到分类异常", e);
	}
	return json;
}

}
