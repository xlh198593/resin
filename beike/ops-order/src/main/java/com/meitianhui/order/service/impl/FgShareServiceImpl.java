package com.meitianhui.order.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;
import com.meitianhui.common.util.DocUtil;
import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.common.util.HttpClientUtil;
import com.meitianhui.common.util.PropertiesConfigUtil;
import com.meitianhui.common.util.StringUtil;
import com.meitianhui.order.constant.RspCode;
import com.meitianhui.order.dao.FgShareDao;
import com.meitianhui.order.entity.FgShare;
import com.meitianhui.order.service.FgShareService;

@Service("fgShareService")
public class FgShareServiceImpl implements FgShareService {

	@Autowired
	private FgShareDao fgShareDao;
	@Autowired
	private DocUtil docUtil;
	
	@Override
	public void handleSaveShare(Map<String, Object> map,ResultData result) throws Exception {
		FgShare fgShare=new FgShare();
		fgShare.setCreate_time(new Date());
		fgShare.setPicture_link(map.get("pic_detail_info").toString());
		fgShare.setShare_content(map.get("share_content").toString());
		fgShare.setShare_name(map.get("title").toString());
		fgShare.setShare_status(0);
		fgShareDao.insertFgShare(fgShare);
	}

	@Override
	public void selectFgShareByPage(Map<String, Object> map, ResultData result) throws Exception {
		//map.put("keyword", 1);
		List<Map<String, Object>>  list=fgShareDao.queryShareList(map);
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (Map sharemap : list) {
			Map<String, Object> psGoodsMap = new HashMap<String, Object>();
			psGoodsMap.put("share_id", sharemap.get("share_id"));
			psGoodsMap.put("share_name", sharemap.get("share_name"));
			psGoodsMap.put("share_content", sharemap.get("share_content"));
			psGoodsMap.put("share_status", sharemap.get("share_status"));
			psGoodsMap.put("is_open", sharemap.get("is_open"));
			psGoodsMap.put("create_time",sharemap.get("create_time2"));
			// 解析图片
			List<Map<String, Object>> pictureList = new ArrayList<Map<String, Object>>();
			String pic_info = StringUtil.formatStr(sharemap.get("picture_link"));
			if (StringUtil.isNotEmpty(pic_info)) {
				List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_info);
				for (Map<String, Object> m : tempList) {
					Map<String, Object> pic_info_urlMap = new HashMap<String, Object>();
					if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
						String path_id = m.get("path_id") + "";
						String pic_info_url = docUtil.imageUrlFind(path_id);
						pic_info_urlMap.put("image_url", pic_info_url);
						pic_info_urlMap.put("goods_code", m.get("goods_code") + "");
						pictureList.add(pic_info_urlMap);
					}
				}
			}
			psGoodsMap.put("pic_info", pictureList);
			resultList.add(psGoodsMap);
		}
		Map<String, Object> resultDataMap = new HashMap<String, Object>();
		resultDataMap.put("list", resultList);
		result.setResultData(resultDataMap);
	}

	@Override
	public void updateFgShare(Map<String, Object> map,ResultData result) throws Exception {
		fgShareDao.updateFgShare(map);
		result.setResultData("保存成功");
	}

	@Override
	public void queryAllFgShareList(Map<String, Object> map,ResultData result) throws Exception {
		
		map.put("share_status", 1);
		List<Map<String, Object>>  list=fgShareDao.selectFgShare(map);
		if(list == null || list.size()==0){
			throw new BusinessException("未查询到分享商品","未查询到分享商品");
		}
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		Set<String> set = new HashSet<>();
		for (Map sharemap : list) {
			Map<String, Object> psGoodsMap = new HashMap<String, Object>();
			psGoodsMap.put("share_id", sharemap.get("share_id"));
			psGoodsMap.put("share_name", sharemap.get("share_name"));
			psGoodsMap.put("share_content", sharemap.get("share_content"));
			psGoodsMap.put("share_status", sharemap.get("share_status"));
			psGoodsMap.put("create_time",sharemap.get("create_time"));
			// 解析图片
			List<Map<String, Object>> pictureList = new ArrayList<Map<String, Object>>();
			String pic_info = StringUtil.formatStr(sharemap.get("picture_link"));
			if (StringUtil.isNotEmpty(pic_info)) {
				List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_info);
				for (Map<String, Object> m : tempList) {
					Map<String, Object> pic_info_urlMap = new HashMap<String, Object>();
					if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
						String path_id = m.get("path_id") + "";
						String pic_info_url = docUtil.imageUrlFind(path_id);
						pic_info_urlMap.put("share_image_url", pic_info_url);
						pic_info_urlMap.put("goods_code", m.get("goods_code") + "");
						pictureList.add(pic_info_urlMap);
						set.add(m.get("goods_code") + "");
					}
				}
			}
			psGoodsMap.put("pic_info", pictureList);
			resultList.add(psGoodsMap);
		}
		Map<String, Object> resultMap = selectPsGoodsforGoodsCodeSet(set);
		List<Map<String,Object>> data_list = (List<Map<String,Object>>)resultMap.get("data");
		for (Map<String, Object> res : resultList) {
			List<Map<String,Object>> pic_info_list = (List<Map<String,Object>>)res.get("pic_info");
			for (Map<String, Object> pic_info_res : pic_info_list) {
				String goods_code = (String)pic_info_res.get("goods_code");
				for (Map<String, Object> data_list_res : data_list) {
					String goods_code_data = (String)data_list_res.get("goods_code");
					if(goods_code.equals(goods_code_data)){
						pic_info_res.put("goods_id", data_list_res.get("goods_id"));
						pic_info_res.put("data_source", data_list_res.get("data_source"));
						pic_info_res.put("discount_price", data_list_res.get("discount_price"));
						pic_info_res.put("image_url", data_list_res.get("image_url"));
					}
				}
			}
		}
		Map<String, Object> resultDataMap = new HashMap<String, Object>();
		resultDataMap.put("list", resultList);
		result.setResultData(resultDataMap);
	}

	/**
	 * 通过goodsCode查找相对应的商品
	 */
	private Map<String, Object> selectPsGoodsforGoodsCodeSet(Set<String> set) throws Exception, SystemException, BusinessException {
		String goods_service_url = PropertiesConfigUtil.getProperty("goods_service_url");
		Map<String, String> reqParams = new HashMap<>();
		Map<String, Object> bizParams = new HashMap<>();
		reqParams.put("service", "psGoods.consumer.selectPsGoodsforGoodsCodeSet");
		bizParams.put("goodsCode_set", set);
		reqParams.put("params", FastJsonUtil.toJson(bizParams));
		String resultStr = HttpClientUtil.postShort(goods_service_url, reqParams);
		Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
		if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
			throw new BusinessException((String) resultMap.get("error_code"),
					(String) resultMap.get("error_msg"));
		}
		return resultMap;
	}
	
	
	
	
	

}
