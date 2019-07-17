package com.meitianhui.goods.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;
import com.meitianhui.common.util.BeanConvertUtil;
import com.meitianhui.common.util.StringUtil;
import com.meitianhui.common.util.ValidateUtil;
import com.meitianhui.goods.constant.RspCode;
import com.meitianhui.goods.dao.GdCategoryCatDao;
import com.meitianhui.goods.dao.GdCategoryPropsDao;
import com.meitianhui.goods.entity.GdCategoryCat;
import com.meitianhui.goods.entity.GdCategoryProps;
import com.meitianhui.goods.service.GdCategoryCatService;
@Service
public class GdCategoryCatServiceImpl implements GdCategoryCatService{
	
	@Autowired
	public GdCategoryCatDao gdCategoryCatDao;
	@Autowired
	public GdCategoryPropsDao gdCategoryPropsDao;

	@Override
	public void gdCategoryCatListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		// TODO Auto-generated method stub
		String parentIdValue =StringUtil.formatStr(paramsMap.get("parent_id"));
		if(StringUtil.isEmpty(parentIdValue) && (paramsMap.get("queryAllStatus")==null || StringUtil.isEmpty(paramsMap.get("queryAllStatus").toString()))){
			
			paramsMap.put("parent_id", "0");
		}
		List<Map<String, Object>> gdCategoryCatList = gdCategoryCatDao.selectGdCategoryCatList(paramsMap);
		List<Map<String, Object>> categoryCatList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> categoryCatMap : gdCategoryCatList) {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("cat_id", categoryCatMap.get("cat_id"));
			tempMap.put("parent_id", categoryCatMap.get("parent_id"));
			tempMap.put("cat_name", categoryCatMap.get("cat_name"));
			tempMap.put("level", categoryCatMap.get("level"));
			tempMap.put("child_count", categoryCatMap.get("child_count"));
			tempMap.put("disabled", categoryCatMap.get("disabled"));
			tempMap.put("modified_time", categoryCatMap.get("modified_time"));
			tempMap.put("create_time", parseTime(categoryCatMap.get("create_time")));
			
			categoryCatList.add(tempMap);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", categoryCatList);
		result.setResultData(map);
	}
	
	
	@Override
	public void getCategoryCatDetail(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap,new String[] { "cat_id"});
		List<Map<String, Object>> gdCategoryCatList = gdCategoryCatDao.selectGdCategoryCatList(paramsMap);
		List<Map<String, Object>> categoryCatList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> categoryCatMap : gdCategoryCatList) {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("cat_id", categoryCatMap.get("cat_id"));
			tempMap.put("parent_id", categoryCatMap.get("parent_id"));
			tempMap.put("cat_name", categoryCatMap.get("cat_name"));
			tempMap.put("level", categoryCatMap.get("level"));
			tempMap.put("child_count", categoryCatMap.get("child_count"));
			tempMap.put("disabled", categoryCatMap.get("disabled"));
			tempMap.put("modified_time", categoryCatMap.get("modified_time"));
			tempMap.put("create_time", parseTime(categoryCatMap.get("create_time")));
			Object cat_path=categoryCatMap.get("cat_path");
			tempMap.put("cat_path",cat_path);
			
			if(cat_path!=null && StringUtil.isNotEmpty(cat_path.toString())){
				String[] catids=cat_path.toString().split("\\,");
				if(catids!=null && catids.length>0){
					if(StringUtil.isNotEmpty(catids[0])){
						paramsMap.put("cat_id", catids[0]);
						List<Map<String, Object>> firstParentList = gdCategoryCatDao.selectGdCategoryCatList(paramsMap);
						if(firstParentList!=null && firstParentList.size()>0){
							Map<String, Object> map=firstParentList.get(0);
							if(map!=null){
								tempMap.put("firstParentCatName", map.get("cat_name"));
								tempMap.put("firstParentCatId", catids[0]);
							}
						}
						
					}
					
					tempMap.put("secondParentCatName", categoryCatMap.get("cat_name"));
					tempMap.put("secondParentCatId", categoryCatMap.get("cat_id"));
				}else{
					tempMap.put("firstParentCatName", categoryCatMap.get("cat_name"));
					tempMap.put("firstParentCatId", categoryCatMap.get("cat_id"));
				}
			}else{
				tempMap.put("firstParentCatName", categoryCatMap.get("cat_name"));
				tempMap.put("firstParentCatId", categoryCatMap.get("cat_id"));
			}
			
			categoryCatList.add(tempMap);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", categoryCatList);
		result.setResultData(map);
	}
	
	private String parseTime(Object timeValue){
		try{
			if(timeValue==null || StringUtil.isEmpty(timeValue.toString())){
				return null;
			}
			return DateFormatUtils.format(new Long(timeValue.toString()).longValue()*1000, "yyyy-MM-dd HH:mm:ss");
		}catch(Exception e){
			return null;
		}
	}

	@Override
	public void gdCategoryPropsFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		// TODO Auto-generated method stub
		List<Map<String, Object>> gdCategoryPropsList = gdCategoryPropsDao.selectGdCategoryPropsList(paramsMap);
		List<Map<String, Object>> propsList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> propsMap : gdCategoryPropsList) {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("prop_id", propsMap.get("prop_id"));
			tempMap.put("cat_id", propsMap.get("cat_id"));
			tempMap.put("prop_name", propsMap.get("prop_name"));
			tempMap.put("order_sort", propsMap.get("order_sort"));
			int cuTime  = 0;
			if(null != propsMap.get("create_time")){
				cuTime = (int) propsMap.get("create_time");
			}
			tempMap.put("cat_name", propsMap.get("cat_name"));
			tempMap.put("one_cat_name", propsMap.get("one_cat_name"));
			tempMap.put("two_cat_name", propsMap.get("two_cat_name"));
			tempMap.put("two_cat_id", propsMap.get("parent_id"));
			tempMap.put("create_time",DateFormatUtils.format(new Long(cuTime).longValue()*1000, "yyyy-MM-dd HH:mm:ss"));
			tempMap.put("disabled", propsMap.get("disabled"));
			propsList.add(tempMap);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", propsList);
		result.setResultData(map);
	}
	
	@Override
	public void gdCategoryPropsDetailFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		// TODO Auto-generated method stub
		List<Map<String, Object>> gdCategoryPropsList = gdCategoryPropsDao.selectGdCategoryProps(paramsMap);
		List<Map<String, Object>> propsList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> propsMap : gdCategoryPropsList) {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("prop_id", propsMap.get("prop_id"));
			tempMap.put("cat_id", propsMap.get("cat_id"));
			tempMap.put("prop_name", propsMap.get("prop_name"));
			tempMap.put("order_sort", propsMap.get("order_sort"));
			int cuTime  = 0;
			if(null != propsMap.get("create_time")){
				cuTime = (int) propsMap.get("create_time");
			}
			tempMap.put("three_cat_name", propsMap.get("three_cat_name"));
			tempMap.put("two_cat_name", propsMap.get("two_cat_name"));
			tempMap.put("one_cat_name", propsMap.get("one_cat_name"));
			tempMap.put("parent_id", propsMap.get("parent_id"));
			tempMap.put("create_time",DateFormatUtils.format(new Long(cuTime).longValue()*1000, "yyyy-MM-dd HH:mm:ss"));
			tempMap.put("disabled", propsMap.get("disabled"));
			propsList.add(tempMap);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", propsList);
		result.setResultData(map);
	}
	

	@Override
	public void gdCategoryPropsCreate(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap,new String[] { "cat_id", "prop_name"});
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("cat_id", paramsMap.get("cat_id"));
		List<Map<String, Object>> gdCategoryPropsList = gdCategoryPropsDao.selectGdCategoryPropsList(tempMap);
		if (null != gdCategoryPropsList && gdCategoryPropsList.size() > 1) {
			throw new BusinessException(RspCode.GOODS_PROPS_EXIST, "商品规格属性不能超过两类");
		}
		tempMap.put("prop_name", paramsMap.get("prop_name"));
		List<Map<String, Object>> existProps = gdCategoryPropsDao.selectGdCategoryPropsList(tempMap);
	    if(null != existProps && existProps.size()>0){
	    	throw new BusinessException(RspCode.GOODS_PROPS_EXIST, "该商品规格属性已设置");
	    }
		//paramsMap.put("sale_qty", paramsMap.get("stock_qty"));
		GdCategoryProps gdCategoryProps = new GdCategoryProps();
		BeanConvertUtil.mapToBean(gdCategoryProps, paramsMap);
		gdCategoryProps.setDisabled(0);
		String date=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());  
		long time=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date).getTime(); 
		int  changeCreateTime=(int) (time/1000); 
		gdCategoryProps.setCreate_time(changeCreateTime);
		gdCategoryPropsDao.insertGdCategoryProps(gdCategoryProps);
	}
	
	

	@Override
	public void gdCategoryPropsEdit(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		// TODO Auto-generated method stub
		ValidateUtil.validateParams(paramsMap, new String[] { "prop_id" });
		if (paramsMap.size() == 1) {
			throw new BusinessException(RspCode.SYSTEM_NO_PARAMS_UPDATE,
					RspCode.MSG.get(RspCode.SYSTEM_NO_PARAMS_UPDATE));
		}
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("prop_id", paramsMap.get("prop_id"));
		List<Map<String, Object>> gdCategoryPropsList = gdCategoryPropsDao.selectGdCategoryPropsList(tempMap);
		if (null == gdCategoryPropsList || gdCategoryPropsList.size() == 0) {
			throw new BusinessException(RspCode.GOODS_PROPS_NOT_EXIST,  "商品规格属性不存在");
		}
		String disabled = StringUtil.formatStr(paramsMap.get("disabled"));
		if(StringUtil.isBlank(disabled)){
			 paramsMap.put("disabled", 0);
		}
		gdCategoryPropsDao.updateGdCategoryProps(paramsMap);
	}

	@Override
	public void gdCategoryCatCreate(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		// TODO Auto-generated method stub
		ValidateUtil.validateParams(paramsMap,new String[] {"owngood_category_name"});
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("cat_name", paramsMap.get("owngood_category_name"));
		/*if(StringUtil.isNotEmpty(StringUtil.formatStr(paramsMap.get("parent_id")))){
			tempMap.put("parent_id", paramsMap.get("parent_id"));
		}else{
			tempMap.put("parent_id", 0);
		}*/
		handleParams(paramsMap,tempMap);
		List<Map<String, Object>> gdCategoryCatList = gdCategoryCatDao.selectGdCategoryCatList(tempMap);
		if (null != gdCategoryCatList && gdCategoryCatList.size() > 0) {
			throw new BusinessException(RspCode.GOODS_PROPS_EXIST, "商品分类添加重复");
		}
		
		//计算父分类有多少子分类数量
		if(tempMap.get("parent_id") !=null && Integer.parseInt(tempMap.get("parent_id").toString())>0){
			Map<String, Object> tempMap2 = new HashMap<String, Object>();
			tempMap2.put("cat_id", tempMap.get("parent_id"));
			List<Map<String, Object>> gdCategoryCatList2 =gdCategoryCatDao.selectGdCategoryCatList(tempMap2);
			if(gdCategoryCatList2!=null && gdCategoryCatList2.size() > 0){
				Map<String, Object> map=gdCategoryCatList2.get(0);
				if(map.get("child_count")!=null && StringUtil.isNotEmpty(map.get("child_count").toString())){
					int child_count=Integer.parseInt(map.get("child_count").toString());
					tempMap2.put("child_count", child_count+1);
				}else{
					tempMap2.put("child_count",1);
				}
			}
			gdCategoryCatDao.updateCountByCatId(tempMap2);
		}
		tempMap.put("disabled", 0);
		tempMap.put("child_count",0);
		GdCategoryCat gdCategoryCat = new GdCategoryCat();
		BeanConvertUtil.mapToBean(gdCategoryCat, tempMap);
		gdCategoryCat.setDisabled("0");
		String date=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());  
		long time=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date).getTime(); 
		int  changeCreateTime=(int) (time/1000); 
		gdCategoryCat.setCreate_time(changeCreateTime);
		gdCategoryCatDao.insertGdCategoryCat(gdCategoryCat);
	}
	
	
	private void handleParams(Map<String, Object> paramsMap, Map<String, Object> tempMap){
		
		if(paramsMap==null){
			return ;
		}
		//owngood_cat_two不为空，则添加的是三级分类
		if(paramsMap.get("owngood_cat_two")!=null && StringUtils.isNotEmpty(paramsMap.get("owngood_cat_two").toString())){
			tempMap.put("level",3);
			tempMap.put("cat_path", paramsMap.get("owngood_cat_one")+","+paramsMap.get("owngood_cat_two"));
			tempMap.put("parent_id", paramsMap.get("owngood_cat_two"));
			tempMap.put("is_leaf", 1);
			return ;
		}
		if(paramsMap.get("owngood_cat_one")!=null && StringUtils.isNotEmpty(paramsMap.get("owngood_cat_one").toString())){
			tempMap.put("level",2);
			tempMap.put("cat_path", paramsMap.get("owngood_cat_one"));
			tempMap.put("parent_id", paramsMap.get("owngood_cat_one"));
			tempMap.put("is_leaf", 0);
			return ;
		}
		tempMap.put("level",1);
		tempMap.put("is_leaf", 0);
		tempMap.put("parent_id",0);
	}

	@Override
	public void gdCategoryCatEdit(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "cat_id" });
		if (paramsMap.size() == 1) {
			throw new BusinessException(RspCode.SYSTEM_NO_PARAMS_UPDATE,
					RspCode.MSG.get(RspCode.SYSTEM_NO_PARAMS_UPDATE));
		}
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("cat_id", paramsMap.get("cat_id"));
		List<Map<String, Object>> gdCategoryCatList = gdCategoryCatDao.selectGdCategoryCatList(tempMap);
		if (null == gdCategoryCatList || gdCategoryCatList.size() == 0) {
			throw new BusinessException(RspCode.GOODS_CAT_NOT_EXIST,  "商品类别不存在");
		}
		gdCategoryCatDao.updateGdCategoryCat(paramsMap);
		
	}

}
