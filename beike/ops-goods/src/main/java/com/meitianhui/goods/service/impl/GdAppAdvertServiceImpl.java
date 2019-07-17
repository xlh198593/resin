package com.meitianhui.goods.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;
import com.meitianhui.common.util.BeanConvertUtil;
import com.meitianhui.common.util.DocUtil;
import com.meitianhui.common.util.EHCacheUtil;
import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.common.util.IDUtil;
import com.meitianhui.common.util.StringUtil;
import com.meitianhui.common.util.ValidateUtil;
import com.meitianhui.goods.dao.GdAppAdvertDao;
import com.meitianhui.goods.entity.GdAppAdvert;
import com.meitianhui.goods.service.GdAppAdvertService;

/**
 * 广告
 * 
 * @ClassName: GdAdvertServiceImpl
 * @author tiny
 * @date 2017年4月5日 下午5:01:57
 *
 */
@SuppressWarnings("unchecked")
@Service
public class GdAppAdvertServiceImpl implements GdAppAdvertService {
	public static String CACHENAME = "ops-goods-cache";

	public static String CACHE_KEY = "[gdAppAdvertListForAppFind]_";

	@Autowired
	private DocUtil docUtil;

	@Autowired
	private GdAppAdvertDao gdAppAdvertDao;

	@Override
	public void gdAppAdvertCreate(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "category", "json_data" });
			GdAppAdvert gdAdvert = new GdAppAdvert();
			BeanConvertUtil.mapToBean(gdAdvert, paramsMap);
			gdAdvert.setAdvert_id(IDUtil.getUUID());
			Date date = new Date();
			gdAdvert.setCreated_date(date);
			gdAdvert.setModified_date(date);
			gdAppAdvertDao.insertGdAppAdvert(gdAdvert);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void gdAppAdvertEdit(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "advert_id", "category", "json_data" });
			gdAppAdvertDao.updateGdAppAdvert(paramsMap);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}
	
	
	@Override
	public void gdAppAdvertDelete(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "advert_id" });
			gdAppAdvertDao.deleteGdAppAdvert(paramsMap);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void gdAppAdvertListForOpFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			List<Map<String, Object>> gdAppAdvertList = gdAppAdvertDao.selectGdAppAdvert(paramsMap);
			Map<String, Object> resultDataMap = new HashMap<String, Object>();
			resultDataMap.put("list", gdAppAdvertList);
			result.setResultData(resultDataMap);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void gdAppAdvertListForAppFind_V1(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			//ValidateUtil.validateParams(paramsMap, new String[] { "category" });
			//String category = StringUtil.formatStr(paramsMap.get("category"));
			//String key = CACHE_KEY + category;
			// EHCache缓存
			/*Object obj = EHCacheUtil.get(CACHENAME, key);
			if (null != obj) {
				result.setResultData(obj);
				return;
			}*/
			//查处所有的符合条件的轮播图
			List<Map<String, Object>> gdAppAdvertPicList = new ArrayList<Map<String, Object>>();
			List<String> doc_ids = new ArrayList<String>();
			//paramsMap.put("end_time", new Date());
			List<Map<String, Object>> qryList = gdAppAdvertDao.selectGdAppAdvert(paramsMap);
			if (qryList.size() > 0) {
				//Map<String, Object> gdAppAdvert = qryList.get(0);
				for (Map<String, Object> map : qryList) {
					String json_data = StringUtil.formatStr(map.get("json_data"));
					if (StringUtils.isNotEmpty(json_data)) {
						List<Map<String, Object>> jsonList = FastJsonUtil.jsonToList(json_data);
						map.put("json_data", jsonList);
						for (Map<String, Object> json : jsonList) {
							gdAppAdvertPicList.add(json);
							if (!StringUtil.formatStr(json.get("path_id")).equals("")) {
								doc_ids.add(json.get("path_id") + "");
							}
						}
					}
				}
			}
			Map<String, Object> resultDataMap = new HashMap<String, Object>();
			Map<String, Object> doc_ids_url = docUtil.imageUrlFind(doc_ids);
			resultDataMap.put("doc_url", doc_ids_url);
			resultDataMap.put("list", qryList);
			result.setResultData(resultDataMap);
			/*if (doc_ids_url.size() > 0) {
				// EHCache缓存
				EHCacheUtil.put(CACHENAME, key, resultDataMap);
			}*/
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void gdAppAdSplash(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception {
		try {
			//ToDo (先写死,后期从数据库读取)
			Map<String, Object> mapDocUrl = new HashMap<String, Object>();
			mapDocUrl.put("f828075fa4b04c3c93e2587d05a999eb", "https://app-static.huigujia.cn/communityDocs/bkcqapp/ad_qidong11.jpg");

			Map<String, Object> mapJsonData = new HashMap<String, Object>();
			mapJsonData.put("category", "libao"); // 跳转分类：bksc(精品商城)；libao(礼包专区)
			mapJsonData.put("jump_type", "goods"); // 跳转类型（goods；h5；vip ）
			mapJsonData.put("content", "439"); // 跳转内容：goods(商品ID)；h5(URL地址)；vip("")
			mapJsonData.put("path_id", "f828075fa4b04c3c93e2587d05a999eb"); // 图片ID
			///
			Map<String, Object> resultDataMap = new HashMap<String, Object>();
			resultDataMap.put("doc_url", mapDocUrl);
			resultDataMap.put("json_data", mapJsonData);
			result.setResultData(resultDataMap);

		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void gdAppVipPageAd(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception {
		try {
			//ToDo (先写死,后期从数据库读取)
			Map<String, Object> mapDocUrl = new HashMap<String, Object>();
			mapDocUrl.put("f828075fa4b04c3c93e2587d05a999eb", "https://app-static.huigujia.cn/communityDocs/bkcqapp/qiandao_ads1.jpg");
			mapDocUrl.put("f828075fa4b04c3c93e2587d05a234wd", "https://app-static.huigujia.cn/communityDocs/bkcqapp/qiandao_ads2.jpg");

			List<Map<String, Object>> goodsList = new ArrayList<Map<String,Object>>();

			Map<String, Object> goods1 = new HashMap<String, Object>();
			goods1.put("goods_type", "bksc");// 跳转分类：bksc(精品商城)；libao(礼包专区)
			goods1.put("goods_id", "2314");
			goods1.put("url", "f828075fa4b04c3c93e2587d05a999eb");

			Map<String, Object> goods2 = new HashMap<String, Object>();
			goods2.put("goods_type", "bksc");// 跳转分类：bksc(精品商城)；libao(礼包专区)
			goods2.put("goods_id", "2125");
			goods2.put("url", "f828075fa4b04c3c93e2587d05a234wd");
            ///
			goodsList.add(goods1);
			goodsList.add(goods2);

			Map<String, Object> resultDataMap = new HashMap<String, Object>();
			resultDataMap.put("doc_url", mapDocUrl);
			resultDataMap.put("goods_list", goodsList);
			result.setResultData(resultDataMap);

		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void gdAppAdvertListForAppFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "category" });
			String category = StringUtil.formatStr(paramsMap.get("category"));
			String key = CACHE_KEY + category;
			// EHCache缓存
			Object obj = EHCacheUtil.get(CACHENAME, key);
			if (null != obj) {
				result.setResultData(obj);
				return;
			}

			List<Map<String, Object>> gdAppAdvertPicList = new ArrayList<Map<String, Object>>();
			List<String> doc_ids = new ArrayList<String>();
			List<Map<String, Object>> qryList = gdAppAdvertDao.selectGdAppAdvert(paramsMap);
			if (qryList.size() > 0) {
				Map<String, Object> gdAppAdvert = qryList.get(0);
				String json_data = StringUtil.formatStr(gdAppAdvert.get("json_data"));
				if (StringUtils.isNotEmpty(json_data)) {
					List<Map<String, Object>> jsonList = FastJsonUtil.jsonToList(json_data);
					for (Map<String, Object> json : jsonList) {
						gdAppAdvertPicList.add(json);
						if (!StringUtil.formatStr(json.get("path_id")).equals("")) {
							doc_ids.add(json.get("path_id") + "");
						}
					}
				}
			}
			Map<String, Object> resultDataMap = new HashMap<String, Object>();
			Map<String, Object> doc_ids_url = docUtil.imageUrlFind(doc_ids);
			resultDataMap.put("doc_url", doc_ids_url);
			resultDataMap.put("list", gdAppAdvertPicList);
			result.setResultData(resultDataMap);
			if (doc_ids_url.size() > 0) {
				// EHCache缓存
				EHCacheUtil.put(CACHENAME, key, resultDataMap);
			}
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}
}
