package com.meitianhui.goods.service.impl;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.meitianhui.goods.constant.*;
import com.meitianhui.goods.constant.enumConstants.ActivityType;
import com.meitianhui.goods.entity.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meitianhui.common.constant.CommonRspCode;
import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;
import com.meitianhui.common.util.BeanConvertUtil;
import com.meitianhui.common.util.DateUtil;
import com.meitianhui.common.util.DocUtil;
import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.common.util.HttpClientUtil;
import com.meitianhui.common.util.IDUtil;
import com.meitianhui.common.util.PropertiesConfigUtil;
import com.meitianhui.common.util.RedisLock;
import com.meitianhui.common.util.RedisUtil;
import com.meitianhui.common.util.StringUtil;
import com.meitianhui.common.util.ValidateUtil;
import com.meitianhui.goods.dao.BeiKeMallGoodsDao;
import com.meitianhui.goods.dao.GdGoodsLabelDao;
import com.meitianhui.goods.dao.GdGoodsSkuidDao;
import com.meitianhui.goods.dao.GoodsDao;
import com.meitianhui.goods.dao.HongbaoGoodsDao;
import com.meitianhui.goods.dao.PsGoodsDao;
import com.meitianhui.goods.service.GoodsService;
import com.meitianhui.goods.service.PsGoodsService;
/*import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.TbkCouponConvertRequest;
import com.taobao.api.request.TbkTpwdCreateRequest;
import com.taobao.api.response.TbkCouponConvertResponse;
import com.taobao.api.response.TbkTpwdCreateResponse;*/


/**
 * 
 * @author Tiny
 *
 */
@SuppressWarnings("unchecked")
@Service
public class PsGoodsServiceImpl implements PsGoodsService {

	private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(PsGoodsServiceImpl.class);

	@Autowired
	public RedisUtil redisUtil;
	@Autowired
	private DocUtil docUtil;
	@Autowired
	public GoodsDao goodsDao;
	@Autowired
	public PsGoodsDao psGoodsDao;
	@Autowired
	public HongbaoGoodsDao hongbaoGoodsDao;
	@Autowired
	public BeiKeMallGoodsDao beiKeMallGoodsDao;
	@Autowired
	public GdGoodsSkuidDao gdGoodsSkuidDao;
	@Autowired
	public GoodsService goodsService;
	@Autowired
	public GdGoodsLabelDao gdGoodsLabelDao;

	/** 免费领商品列表缓存key **/
	public static Set<String> freeGetGoodsListCacheKeySet = new HashSet<String>();


	@Override
	public void gdGoodsItemSync(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap,
				new String[] { "goods_code", "title", "goods_attr",
						"cat_topid" ,"cat_twoid","cat_threeid","cat_topname","cat_twoname","cat_threename",
						"brand_id","brand_name","category","display_area",  
						"contact_person","contact_tel", "pic_info", 
						"cost_price","market_price", "discount_price", "ladder_price", "shipping_fee", 
						"sale_qty","stock_qty","payment_way","status" });
		RedisLock lock = null;
		String lockKey = null;
		try {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			String goods_code = StringUtil.formatStr(paramsMap.get("goods_code"));

			lockKey = "[gdGoodsItemSync]_" + goods_code;
			lock = new RedisLock(redisUtil, lockKey, 10 * 1000);
			lock.lock();

			String lockValue = redisUtil.getStr(lockKey);
			if (StringUtil.isNotEmpty(lockValue)) {
				throw new BusinessException(RspCode.GOODS_CODE_ERROR, "您的操作过于频繁,请稍后重试");
			}
			redisUtil.setStr(lockKey, goods_code, 180);

			// 根据商品id查询商品，存在则更新，不存在新增
			tempMap.clear();
			tempMap.put("goods_id", paramsMap.get("goods_id"));
			List<GdGoodsItem> gdHuiguoGoodsList = psGoodsDao.selectGdGoodsItem(tempMap);
           
			if (gdHuiguoGoodsList.size() > 0) {
				GdGoodsItem gdHuiguoGoods = gdHuiguoGoodsList.get(0);
				String sale_qty = StringUtil.formatStr(paramsMap.get("sale_qty"));
				if (!StringUtils.isEmpty(sale_qty)) {
					// 计算差值
					Integer num = Integer.parseInt(sale_qty) - gdHuiguoGoods.getSale_qty();
					// 计算总库存量
					Integer new_stock_qty = gdHuiguoGoods.getStock_qty() + num;
					paramsMap.put("stock_qty", new_stock_qty);
				}
				
				psGoodsDao.updateGdGoodsItem(paramsMap);
				tempMap.clear();
				tempMap.put("goods_id", paramsMap.get("goods_id"));
				tempMap.put("category", paramsMap.get("category"));
				Map<String, Object> eventTempMap = new HashMap<String, Object>();
				eventTempMap.put("remark","修改商品:" + paramsMap.get("title") + ",商品ID：" + paramsMap.get("goods_id") + ",商品码：" + goods_code);
				tempMap.put("event", FastJsonUtil.toJson(eventTempMap));
				goodsLogAdd(tempMap, result);
			} else {
				String cost_allocation = StringUtil.formatStr(paramsMap.get("cost_allocation"));
				if (StringUtils.isEmpty(cost_allocation)) {
					paramsMap.put("cost_allocation", "0.00");
				}
				String sale_qty = StringUtil.formatStr(paramsMap.get("sale_qty"));
				if (StringUtils.isBlank(sale_qty)) {
					paramsMap.put("sale_qty", "0");
				}
				if (!StringUtils.isEmpty(goods_code)) {
					// 验证商品码是否已存在
					tempMap.clear();
					tempMap.put("goods_code", paramsMap.get("goods_code"));
					List<GdGoodsItem> tempGdHuiguoGoodsList = psGoodsDao.selectGdGoodsItem(tempMap);
					if (tempGdHuiguoGoodsList.size() > 0) {
						throw new BusinessException(RspCode.GOODS_CODE_EXIST,
								RspCode.MSG.get(RspCode.GOODS_CODE_EXIST));
					}
				}
				Date date = new Date();
				GdGoodsItem gdHuiguoGoods = new GdGoodsItem();
				BeanConvertUtil.mapToBean(gdHuiguoGoods, paramsMap);
				gdHuiguoGoods.setStock_qty(gdHuiguoGoods.getSale_qty());
				gdHuiguoGoods.setModified_date(date);
				gdHuiguoGoods.setCreated_date(date);
				if (gdHuiguoGoods.getSettled_price() == null) {
					gdHuiguoGoods.setSettled_price(new BigDecimal("0.00"));
				}
				if (gdHuiguoGoods.getService_fee() == null) {
					gdHuiguoGoods.setService_fee(new BigDecimal("0.00"));
				}

				if(gdHuiguoGoods.getTotal_sales() == null || gdHuiguoGoods.getTotal_sales().equals("")){
					gdHuiguoGoods.setTotal_sales("0");
				}
				psGoodsDao.insertGdGoodsItem(gdHuiguoGoods);

				tempMap.clear();
				tempMap.put("goods_id", paramsMap.get("goods_id"));
				tempMap.put("category", paramsMap.get("category"));
				Map<String, Object> eventTempMap = new HashMap<String, Object>();
				eventTempMap.put("remark","新增商品:" + paramsMap.get("title") + ",商品ID：" + paramsMap.get("goods_id") + ",商品码：" + goods_code);
				tempMap.put("event", FastJsonUtil.toJson(eventTempMap));
				goodsLogAdd(tempMap, result);
			}

		} catch (Exception e) {
			throw e;
		} finally {
			if (lock != null) {
				lock.unlock();
			}
			if (StringUtil.isNotEmpty(lockKey)) {
				redisUtil.del(lockKey);
			}
		}
	}
	
	@Override
	public void psGoodsSync(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		Object object = paramsMap.get("title");
		logger.info(object);
		ValidateUtil.validateParams(paramsMap,
				new String[] {"title", "area_id", "display_area", "desc1", "contact_person",
						"contact_tel", "pic_info", "cost_price", "market_price", "discount_price", "ladder_price",
						"min_buy_qty", "max_buy_qty", "payment_way", "status", "shipping_fee" });
		RedisLock lock = null;
		String lockKey = null;



		try {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			String goods_code = StringUtil.formatStr(paramsMap.get("goods_code"));
			String operator_info = StringUtil.formatStr(paramsMap.get("operator_info"));
			lockKey = "[psGoodsSync]_" + goods_code;
			lock = new RedisLock(redisUtil, lockKey, 10 * 1000);
			lock.lock();

			String lockValue = redisUtil.getStr(lockKey);
			if (StringUtil.isNotEmpty(lockValue)) {
				throw new BusinessException(RspCode.GOODS_CODE_ERROR, "您的操作过于频繁,请稍后重试");
			}
			redisUtil.setStr(lockKey, goods_code, 180);

			// 根据商品id查询商品，存在则更新，不存在新增
			tempMap.clear();
			tempMap.put("goods_code", paramsMap.get("goods_code"));
			
			//List<PsGoods> psGoodsList =null;
			List<HongBaoGoods> hongBaoGoodsList = hongbaoGoodsDao.selectHongbaoGoodsListBycode(paramsMap);
//			List<HongBaoGoods> hongBaoGoodsList = hongbaoGoodsDao.selectHongbaoGoodsListBycode(paramsMap);

			/*//商品类型 1=特卖商品 2=0元购商品
			if(paramsMap.get("goods_type")!=null && Integer.parseInt(paramsMap.get("goods_type").toString())==2){
				//psGoodsList = psGoodsDao.selectFreePsGoods(tempMap);
				hongBaoGoodsList = hongbaoGoodsDao.selectHongbaoGoodsList(paramsMap);
			}else{
				psGoodsList = psGoodsDao.selectPsGoods(tempMap);
			}*/
			
           
			if (hongBaoGoodsList.size() > 0) {
				HongBaoGoods hongBaoGoods = hongBaoGoodsList.get(0);
				String sale_qty = StringUtil.formatStr(paramsMap.get("sale_qty"));
				if (!StringUtils.isEmpty(sale_qty)) {
					// 计算差值
					Integer num = Integer.parseInt(sale_qty) - hongBaoGoods.getSale_qty();
					// 计算总库存量
					Integer new_stock_qty = hongBaoGoods.getStock_qty() + num;
					paramsMap.put("stock_qty", new_stock_qty);
				}
		/*		if ("hsrj".equals(paramsMap.get("data_source"))) {
					if (!StringUtil.isEmpty(StringUtil.formatStr(paramsMap.get("quan_id")))) {
						Map<String, Object> tempTaobaoMap = createCommandAndUrlByTaobaoAPI(
								paramsMap.get("goods_code").toString(), paramsMap.get("quan_id").toString());
						if (tempTaobaoMap != null) {
							String url_short = tempTaobaoMap.get("url_short").toString();
							if (url_short == null) {
								String url_long = tempTaobaoMap.get("url_long").toString();
								paramsMap.put("taobao_link", url_long);
							} else {
								paramsMap.put("taobao_link", url_short);
							}
							String command = tempTaobaoMap.get("command").toString();
							if (command != null) {
								paramsMap.put("product_source", command);
							}
						}
					}
				}*/
				
			/*	if(paramsMap.get("goods_type")!=null && Integer.parseInt(paramsMap.get("goods_type").toString())==2){
					psGoodsDao.updateFreePsGoods(paramsMap);
				}else{
					psGoodsDao.updatePsGoods(paramsMap);
				}*/
			     String code = hongBaoGoods.getGoods_id();
				paramsMap.put("goods_id",code);
				HongBaoGoods hongBaoGood = new HongBaoGoods();

				BeanConvertUtil.mapToBean(hongBaoGood,paramsMap);
			   int a = hongbaoGoodsDao.updateByPrimaryKeySelective(hongBaoGood);

				
				tempMap.clear();
				//商品日志
				tempMap.put("goods_id", paramsMap.get("goods_id"));
				tempMap.put("goods_code", goods_code);
				//tempMap.put("category", paramsMap.get("category"));
				Map<String, Object>  oldMap = BeanConvertUtil.beanToMap(hongBaoGoods);
				paramsMap.remove("operator_info");
				if(paramsMap.containsKey("created_date")){
					paramsMap.remove("created_date");
				}
			    Set<Map.Entry<String, Object>> sets = paramsMap.entrySet();
			    Map<String, Object> updateDetailInfo = new HashMap<String, Object>();
			         for (Map.Entry<String, Object> set : sets) {
			             String key = set.getKey();
			             String oldValue =  StringUtil.formatStr(oldMap.get(key));
			             String newValue =  StringUtil.formatStr(paramsMap.get(key));
			             if(!StringUtils.isEmpty(newValue)){
			                 if (!newValue.equals(oldValue)) {
			                	 updateDetailInfo.put(key, newValue);
			                 }
			             }
			         }
				Map<String, Object> eventTempMap = new HashMap<String, Object>();
				eventTempMap.put("desc","修改商品");
				eventTempMap.put("edit_info", FastJsonUtil.toJson(updateDetailInfo));
				tempMap.put("event", operator_info +FastJsonUtil.toJson(eventTempMap).replace("\\", ""));
				tempMap.put("category",paramsMap.get("category"));
				goodsLogAdd(tempMap, result);
			} else {
				String cost_allocation = StringUtil.formatStr(paramsMap.get("cost_allocation"));
				if (StringUtils.isEmpty(cost_allocation)) {
					paramsMap.put("cost_allocation", "0.00");
				}
				String sale_qty = StringUtil.formatStr(paramsMap.get("sale_qty"));
				if (StringUtils.isBlank(sale_qty)) {
					paramsMap.put("sale_qty", "0");
				}
				if (!StringUtils.isEmpty(goods_code)) {
					// 验证商品码是否已存在
					tempMap.clear();
					tempMap.put("goods_code", paramsMap.get("goods_code"));
					List<HongBaoGoods> hongBaoGoodsLists = hongbaoGoodsDao.selectHongbaoGoodsList(paramsMap);
					if (hongBaoGoodsLists.size() > 0) {
						throw new BusinessException(RspCode.GOODS_CODE_EXIST,
								RspCode.MSG.get(RspCode.GOODS_CODE_EXIST));
					}
				}
				Date date = new Date();
				PsGoods psGoods = new PsGoods();
				BeanConvertUtil.mapToBean(psGoods, paramsMap);
				psGoods.setStock_qty(psGoods.getSale_qty());
				psGoods.setModified_date(date);
				psGoods.setCreated_date(date);
				if (psGoods.getSettled_price() == null) {
					psGoods.setSettled_price(new BigDecimal("0.00"));
				}
				if (psGoods.getService_fee() == null) {
					psGoods.setService_fee(new BigDecimal("0.00"));
				}

			/*	if ("hsrj".equals(paramsMap.get("data_source"))) {
					if (!StringUtil.isEmpty(StringUtil.formatStr(paramsMap.get("quan_id")))) {

						Map<String, Object> tempTaobaoMap = createCommandAndUrlByTaobaoAPI(
								paramsMap.get("goods_code").toString(), paramsMap.get("quan_id").toString());
						if (tempTaobaoMap != null) {
							String url_short = tempTaobaoMap.get("url_short").toString();
							if (url_short == null) {
								String url_long = tempTaobaoMap.get("url_long").toString();
								psGoods.setTaobao_link(url_long);
							} else {
								psGoods.setTaobao_link(url_short);
							}
							String command = tempTaobaoMap.get("command").toString();
							if (command != null) {
								psGoods.setProduct_source(command);
							}
						}
					}
				}*/
				/*if(psGoods.getTaobao_sales() == null || psGoods.getTaobao_sales().equals("")){
					psGoods.setTaobao_sales("0");
				}*/
				HongBaoGoods hbg = new HongBaoGoods();
				BeanConvertUtil.mapToBean(hbg,paramsMap);

				int id = hongbaoGoodsDao.insert(hbg);


				/*tempMap.clear();
				tempMap.put("goods_id",id);
				tempMap.put("goods_code", goods_code);
				tempMap.put("category", paramsMap.get("category"));
				Map<String, Object> eventTempMap = new HashMap<String, Object>();
				eventTempMap.put("remark","新增商品:" + paramsMap.get("title") + ",商品ID：" + paramsMap.get("goods_id") + ",商品码：" + goods_code);
				tempMap.put("event", operator_info+FastJsonUtil.toJson(eventTempMap));
				goodsLogAdd(tempMap, result);*/
			}

		} catch (Exception e) {
			throw e;
		} finally {
			if (lock != null) {
				lock.unlock();
			}
			if (StringUtil.isNotEmpty(lockKey)) {
				redisUtil.del(lockKey);
			}
		}
	}

	 
	public Map<String, Object> createCommandAndUrlByTaobaoAPI(String itemId, String quanId)
			throws BusinessException, SystemException, Exception {/*
		Map<String, Object> tempParams = new HashMap<String, Object>();
		try {
			TaobaoClient client = new DefaultTaobaoClient("http://gw.api.taobao.com/router/rest", "24635795",
					"b8709ffa33efaff9a7d4813ba77de28d");
			TbkCouponConvertRequest tbkCouponConvertRequest = new TbkCouponConvertRequest();
			tbkCouponConvertRequest.setItemId(Long.parseLong(itemId));
			tbkCouponConvertRequest.setAdzoneId(Long.parseLong("146130492"));
			TbkCouponConvertResponse tbkCouponConvertResponse = client.execute(tbkCouponConvertRequest);
			if (tbkCouponConvertResponse != null) {
				if (StringUtils.isNotBlank(tbkCouponConvertResponse.getErrorCode())) {
					throw new BusinessException(RspCode.PS_GOODS_NOT_EXIST,
							RspCode.MSG.get(RspCode.SYSTEM_NO_PARAMS_UPDATE));
				}
				String originalURL = (((Map<String, Object>) ((Map<String, Object>) ((Map<String, Object>) FastJsonUtil
						.jsonToMap(tbkCouponConvertResponse.getBody()).get("tbk_coupon_convert_response"))
								.get("result")).get("results")).get("coupon_click_url")).toString();
				Map<String, String> reqParams = new HashMap<String, String>();
				reqParams.put("source", "896267270");
				String longUrl = URLEncoder.encode(originalURL + "&activityId=" + quanId ,"UTF-8");
				reqParams.put("url_long", longUrl);
				String weibo_service_url = "https://api.weibo.com/2/short_url/shorten.json";
				String resultStr = HttpClientUtil.getShort(weibo_service_url, reqParams);
				String getSignInfo = resultStr.substring(resultStr.indexOf("[") + 1, resultStr.indexOf("]"));
				Map<String, Object> bizParams = new HashMap<String, Object>();
				bizParams = FastJsonUtil.jsonToMap(getSignInfo);
				tempParams.put("url_long", bizParams.get("url_long").toString());
				tempParams.put("url_short", bizParams.get("url_short").toString());

				TbkTpwdCreateRequest tbkTpwdCreateRequest = new TbkTpwdCreateRequest();
				tbkTpwdCreateRequest.setUserId("");
				tbkTpwdCreateRequest.setText("收到一条优惠信息");
				tbkTpwdCreateRequest.setUrl(originalURL);
				tbkTpwdCreateRequest.setLogo("");
				tbkTpwdCreateRequest.setExt("");
				TbkTpwdCreateResponse tbkTpwdCreateResponse = client.execute(tbkTpwdCreateRequest);
				String command = (((Map<String, Object>) ((Map<String, Object>) FastJsonUtil
						.jsonToMap(tbkTpwdCreateResponse.getBody()).get("tbk_tpwd_create_response")).get("data"))
								.get("model")).toString();
				tempParams.put("command", command);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw e;
		}
		return tempParams;
	*/  return null;
		}

	@Override
	public void wpyGoodsSkuSync(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "goods_sku_list" });
		Map<String, Object> tempMap = new HashMap<String, Object>();
		List<Map<String, Object>> goods_sku_list = FastJsonUtil.jsonToList(paramsMap.get("goods_sku_list") + "");
		String goods_id = null;
		// 总库存量的变更
		Integer total_sale_qty = 0;
		// 总可销售库存
		Integer total_stock_qty = 0;
		for (Map<String, Object> skuMap : goods_sku_list) {
			ValidateUtil.validateParams(skuMap,
					new String[] { "goods_id", "sku", "desc1", "cost_price", "sales_price", "sale_qty" });
			String goods_stock_id = StringUtil.formatStr(skuMap.get("goods_stock_id"));
			goods_id = StringUtil.formatStr(skuMap.get("goods_id"));
			if (StringUtils.isNotBlank(goods_stock_id)) {
				// 根据商品库存id查询商品，存在则更新，不存在新增
				tempMap.clear();
				tempMap.put("goods_stock_id", goods_stock_id);
				List<PsGoodsSku> psGoodsSkuList = psGoodsDao.selectPsGoodsSku(tempMap);
				PsGoodsSku psGoodsSku = psGoodsSkuList.get(0);
				// 如果可销售库存数量和数据的数量不相等,则更新库存,否则不更新库存
				Integer sale_qty = Integer.parseInt(skuMap.get("sale_qty") + "");
				// 计算差值
				Integer modify_num = sale_qty - psGoodsSku.getSale_qty();
				// 计算总库存
				Integer new_stock_qty = psGoodsSku.getStock_qty() + modify_num;
				// 累加可销售库存量
				total_sale_qty += sale_qty;
				// 累加总销售库存量
				total_stock_qty += new_stock_qty;
				skuMap.put("stock_qty_modify", modify_num);
				skuMap.put("sale_qty_modify", modify_num);
				psGoodsDao.updatePsGoodsSku(skuMap);
			} else {
				Date date = new Date();
				PsGoodsSku psGoodsSku = new PsGoodsSku();
				BeanConvertUtil.mapToBean(psGoodsSku, skuMap);
				psGoodsSku.setGoods_stock_id(IDUtil.getUUID());
				psGoodsSku.setStock_qty(psGoodsSku.getSale_qty());
				psGoodsSku.setModified_date(date);
				psGoodsSku.setCreated_date(date);
				psGoodsSku.setStatus(Constant.STATUS_NORMAL);
				psGoodsDao.insertPsGoodsSku(psGoodsSku);
				// 累加可销售库存量
				total_sale_qty += psGoodsSku.getSale_qty();
				// 累加总销售库存量
				total_stock_qty += psGoodsSku.getSale_qty();
			}
		}
		if (StringUtils.isNotBlank(goods_id)) {
			tempMap.clear();
			tempMap.put("goods_id", goods_id);
			// 修改主商品的商品库存
			tempMap.put("stock_qty", total_stock_qty);
			tempMap.put("sale_qty", total_sale_qty);
			psGoodsDao.updatePsGoods(tempMap);
		}
	}

	@Override
	public void psGoodsSaleQtyDeduction(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "goods_id", "sell_qty" });
		int updateFlag = psGoodsDao.updatePsGoodsSaleQtyDeduction(paramsMap);
		if (updateFlag == 0) {
			throw new BusinessException(RspCode.PROCESSING, "可售库存不足");
		}
		gdSellAdd(paramsMap, new ResultData());
	}
	
	@Override
	public void gdFreeGetGoodsSaleQtyDeduction(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "goods_id", "sell_qty" });
		int updateFlag = psGoodsDao.updateGdFreeGetGoodsSaleQtyDeduction(paramsMap);
		if (updateFlag == 0) {
			throw new BusinessException(RspCode.PROCESSING, "可售库存不足");
		}
		gdSellAdd(paramsMap, new ResultData());
	}

	@Override
	public void psGoodsSaleQtyRestore(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "goods_id", "restore_qty" });
		int updateFlag = psGoodsDao.updatePsGoodsSaleQtyRestore(paramsMap);
		if (updateFlag == 0) {
			throw new BusinessException(RspCode.PS_GOODS_NOT_EXIST, "系统繁忙,请重新操作");
		}
		paramsMap.put("sell_qty", "-" + paramsMap.get("restore_qty"));
		gdSellAdd(paramsMap, new ResultData());
	}
	
	@Override
	public void gdFreeGetGoodsSaleQtyRestore(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "goods_id", "restore_qty" });
		int updateFlag = psGoodsDao.updateGdFreeGetGoodsSaleQtyRestore(paramsMap);
		if (updateFlag == 0) {
			throw new BusinessException(RspCode.PS_GOODS_NOT_EXIST, "系统繁忙,请重新操作");
		}
		paramsMap.put("sell_qty", "-" + paramsMap.get("restore_qty"));
		gdSellAdd(paramsMap, new ResultData());
	}

	@Override
	public void handleWypGoodsSaleQtyRestore(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "goods_list" });
		List<Map<String, Object>> goods_list = FastJsonUtil.jsonToList(paramsMap.get("goods_list") + "");
		for (Map<String, Object> goodsMap : goods_list) {
			ValidateUtil.validateParams(goodsMap, new String[] { "goods_id", "goods_stock_id", "restore_qty" });
			// 修改sku库存表
			psGoodsDao.updateWypGoodsSkuSaleQtyRestore(goodsMap);

			// 修改主表库存
			psGoodsDao.updateWypGoodsSaleQtyRestore(goodsMap);
			goodsMap.put("sell_qty", "-" + goodsMap.get("restore_qty"));
			gdSellAdd(goodsMap, new ResultData());
		}
	}

	@Override
	public void wypGoodsSaleQtyDeduction(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "goods_list" });
		List<Map<String, Object>> goods_list = FastJsonUtil.jsonToList(paramsMap.get("goods_list") + "");
		for (Map<String, Object> goodsMap : goods_list) {
			ValidateUtil.validateParams(goodsMap,
					new String[] { "goods_id", "goods_title", "goods_stock_id", "sell_qty" });
			// 修改sku库存表
			psGoodsDao.updateWypGoodsSkuSaleQtyDeduction(goodsMap);
			// 修改主表库存
			psGoodsDao.updateWypGoodsSaleQtyDeduction(goodsMap);
			gdSellAdd(goodsMap, new ResultData());
		}
	}

	@Override
	public void wypGoodsSaleQtyValidate(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "goods_list" });
		List<Map<String, Object>> goods_list = FastJsonUtil.jsonToList(paramsMap.get("goods_list") + "");
		for (Map<String, Object> goodsMap : goods_list) {
			ValidateUtil.validateParams(goodsMap, new String[] { "goods_id", "goods_title", "sell_qty" });
			List<PsGoodsSku> psGoodsSkuList = psGoodsDao.selectPsGoodsSku(goodsMap);
			if (psGoodsSkuList.size() == 0) {
				throw new BusinessException(RspCode.PS_GOODS_NOT_EXIST, "商品的规格信息不存在");
			}
			PsGoodsSku psGoodsSku = psGoodsSkuList.get(0);
			String sell_qty = StringUtil.formatStr(goodsMap.get("sell_qty"));
			// 计算差值
			if (psGoodsSku.getSale_qty() - Integer.parseInt(sell_qty) < 0) {
				throw new BusinessException(RspCode.GOODS_SALE_ERROR, goodsMap.get("goods_title") + "库存不足");
			}
		}
	}

	@Override
	public void psGoodsOrderBottom(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "goods_id" });
		if(paramsMap.get("goods_type")!=null && Integer.parseInt(paramsMap.get("goods_type").toString())==2){
			psGoodsDao.updateFreePsGoodsBottom(paramsMap);
		}else{
			psGoodsDao.updatePsGoodsBottom(paramsMap);
		}
		
	}

	@Override
	public void psGoodsEdit(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "goods_id" });
		if (paramsMap.size() == 1) {
			throw new BusinessException(RspCode.SYSTEM_NO_PARAMS_UPDATE,
					RspCode.MSG.get(RspCode.SYSTEM_NO_PARAMS_UPDATE));
		}
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("goods_id", paramsMap.get("goods_id"));
		/*List<PsGoods> psGoodsList =null;
		if(paramsMap.get("goods_type")!=null && Integer.parseInt(paramsMap.get("goods_type").toString())==2){
			psGoodsList = psGoodsDao.selectFreePsGoods(tempMap);
		}else{
			psGoodsList = psGoodsDao.selectPsGoods(tempMap);
		}*/
		//
		List<HongBaoGoods> hongBaoGoodsList = hongbaoGoodsDao.selectHongbaoGoodsList(tempMap);

		if (hongBaoGoodsList.size() == 0) {
			throw new BusinessException(RspCode.PS_GOODS_NOT_EXIST, RspCode.MSG.get(RspCode.PS_GOODS_NOT_EXIST));
		}
		HongBaoGoods hongBaoGoods = hongBaoGoodsList.get(0);
		String sale_qty = StringUtil.formatStr(paramsMap.get("sale_qty"));
		if (!StringUtils.isEmpty(sale_qty)) {
			// 计算差值
			Integer num = Integer.parseInt(sale_qty) - hongBaoGoods.getSale_qty();
			// 计算总库存量
			Integer new_stock_qty = hongBaoGoods.getStock_qty() + num;
			paramsMap.put("stock_qty", new_stock_qty);
		}
		// 更新红包兑商品
		int resulta = 0;
		// 上架的时候，判断是否是首页展示
		if (BizConstants.Operator.ON_LINE.equals(paramsMap.get("status"))) {// 上架
			// paramsMap.put("online_date",paramsMap.get("valid_thru"));//上架时间
			paramsMap.put("online_date", new Date());// 上架时间
			paramsMap.put("valid_thru", new Date());
			resulta = hongbaoGoodsDao.updateHongBaoGoods(paramsMap);
			PsGoodsActivity psGoodsActivity = activityGood(paramsMap);
			insertActivityGoods(paramsMap, psGoodsActivity);

		} else if (BizConstants.Operator.OFF_LINE.equals(paramsMap.get("status"))) {// 下架操作，清除活动
			paramsMap.put("offline_date", new Date());// 上架时间
			resulta = hongbaoGoodsDao.updateOffLine(paramsMap.get("goods_id").toString());
			psGoodsDao.updateActivityGoodsStatus(BizConstants.ActivityStatus.YES, paramsMap.get("goods_id"));
		}
		if (resulta <= 0) {
			throw new BusinessException(BizConstants.ErrorCode.OPERATOR_FAIL_CODE,
					BizConstants.ErrorConstants.OPERATOR_FAIL);
		}}

	@Override
	public void psGoodsNewEdit(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "goods_id", "title", "market_price", "discount_price",
				"desc1", "category", "data_source", "goods_code", "pic_info" });
		if (paramsMap.size() == 1) {
			throw new BusinessException(RspCode.SYSTEM_NO_PARAMS_UPDATE,
					RspCode.MSG.get(RspCode.SYSTEM_NO_PARAMS_UPDATE));
		}
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("goods_id", paramsMap.get("goods_id"));
		List<PsGoods> psGoodsList = psGoodsDao.selectPsGoods(tempMap);
		if (psGoodsList.size() == 0) {
			throw new BusinessException(RspCode.PS_GOODS_NOT_EXIST, RspCode.MSG.get(RspCode.PS_GOODS_NOT_EXIST));
		}
		tempMap.put("title", paramsMap.get("title"));
		tempMap.put("market_price", paramsMap.get("market_price"));
		tempMap.put("discount_price", paramsMap.get("discount_price"));
		tempMap.put("desc1", paramsMap.get("desc1"));
		tempMap.put("category", paramsMap.get("category"));
		tempMap.put("data_source", paramsMap.get("data_source"));
		tempMap.put("goods_code", paramsMap.get("goods_code"));
		tempMap.put("pic_info", paramsMap.get("pic_info"));
		Integer i = psGoodsDao.updatePsGoods(tempMap);
		if (i > 0) {
			tempMap.clear();
			tempMap.put("msg", "修改成功");
			result.setResultData(tempMap);
		}
	}

	@Override
	public void psGoodsStatusEdit(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "goods_id", "status" });
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("goods_id", paramsMap.get("goods_id"));
		List<PsGoods> psGoodsList = null;
		if(paramsMap.get("goods_type")!=null && Integer.parseInt(paramsMap.get("goods_type").toString())==2){
			psGoodsList = psGoodsDao.selectFreePsGoods(tempMap);
		}else{
			psGoodsList = psGoodsDao.selectPsGoods(tempMap);
		}
		
		if (psGoodsList==null || psGoodsList.size() == 0) {
			throw new BusinessException(RspCode.PS_GOODS_NOT_EXIST, RspCode.MSG.get(RspCode.PS_GOODS_NOT_EXIST));
		}
		tempMap.put("status", paramsMap.get("status"));
		if(paramsMap.get("goods_type")!=null && Integer.parseInt(paramsMap.get("goods_type").toString())==2){
			psGoodsDao.updateFreePsGoods(tempMap);
		}else{
			psGoodsDao.updatePsGoods(tempMap);
		}
		
	}

	@Override
	public void psGoodsDelete(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "goods_id" });
		Integer i = psGoodsDao.updatePsGoodsStatus(paramsMap);
		Map<String, Object> tempMap = new HashMap<String, Object>();
		if (i > 0) {
			tempMap.put("msg", "删除成功");
			result.setResultData(tempMap);
		}
	}

	//车夫修改
	@Override
	public void freeGetGoodsStatusEdit(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "status" });
		// 商品id和商品编码必须有一个
		ValidateUtil.validateParamsNum(paramsMap, new String[] { "goods_id", "goods_code" }, 1);
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("goods_id", StringUtil.formatStr(paramsMap.get("goods_id")));
		tempMap.put("goods_code", StringUtil.formatStr(paramsMap.get("goods_code")));
		tempMap.put("category", "试样");
		List<BeikeMallGoods> beiKeGoodsList = null;
		if (paramsMap.get("goods_type") != null && Integer.parseInt(paramsMap.get("goods_type").toString()) == 2) {
			// psGoodsList = psGoodsDao.selectFreePsGoods(tempMap);
		} else {
			beiKeGoodsList = psGoodsDao.selectBeiKeGoods(tempMap);
		}
		if (beiKeGoodsList.size() == 0) {
			throw new BusinessException(RspCode.PS_GOODS_NOT_EXIST, RspCode.MSG.get(RspCode.PS_GOODS_NOT_EXIST));
		}
		BeikeMallGoods psGoods = beiKeGoodsList.get(0);
		// 将更新前商品信息返回
		result.setResultData(psGoods);

		// paramsMap.put("goods_id", psGoods.getGoods_id());
		String sale_qty = StringUtil.formatStr(paramsMap.get("sale_qty"));
		if (StringUtils.isNotEmpty(sale_qty)) {
			// 计算差值
			Integer num = Integer.parseInt(sale_qty) - psGoods.getSale_qty();
			// 计算总库存量
			Integer new_stock_qty = psGoods.getStock_qty() + num;
			paramsMap.put("stock_qty", new_stock_qty);
		}
		paramsMap.put("market_price", paramsMap.get("market_price"));
		paramsMap.put("sale_price", paramsMap.get("sale_price22"));
		paramsMap.put("vip_price", paramsMap.get("total_sales22"));
		paramsMap.put("beike_credit", paramsMap.get("beike_credit22"));
		paramsMap.put("online_date", paramsMap.get(DateUtil.date2Str(new Date(), DateUtil.fmt_yyyyMMddHHmmss)));
		// 同步sku数据
		if ("on_shelf".equals(paramsMap.get("status"))) {
			handleGoodsSkuid(paramsMap, result);
		}

		Integer i = 0;

		if (paramsMap.get("goods_type") != null && Integer.parseInt(paramsMap.get("goods_type").toString()) == 2) {
			i = psGoodsDao.updateFreePsGoods(paramsMap);
		} else {
			i = psGoodsDao.updateBeiKeGoods(paramsMap);
		}
		if (i <= 0) {
			throw new BusinessException("上架失败", "上架失败,请刷新后重试");
		}
		if (BizConstants.Operator.ON_LINE.equals(paramsMap.get("status"))) {// 上架
			// 限制活动商品20 个之内，超过20 个 需提示用户下架商品 -- 只是做一下数量控制，如需数据可修改方法
			// 是否首页展示 -活动商品 chefu modify -- 因 活动排序问题 协议插入2个同样的数据，一样浪费资源，后期优化
			PsGoodsActivity psGoodsActivity = activityGood(paramsMap);// 封装
																		// activity。如果不是展示或者推荐浪费资源，可优化
			// insertActivityGoods(paramsMap, psGoodsActivity);//插入商品
			// 皇城修改，情急之下代码只能先这样。后期再优化
			if (BizConstants.AppIndexShow.YES.equals(paramsMap.get(BizConstants.AppIndexShow.MAP_FIELD))
					&& BizConstants.AppCommondGoods.YES.equals(paramsMap.get(BizConstants.AppCommondGoods.MAP_FIELD))) {
				if (BizConstants.AppIndexShow.YES.equals(paramsMap.get(BizConstants.AppIndexShow.MAP_FIELD))) {// 6个活动商品
					logger.info("当前首页展示activity Type :" + psGoodsActivity.getActivity_type());
					int counts = psGoodsDao.selectPsGoodsActivityListByStatushb(BizConstants.ActivityStatus.NO);
					if (counts > 20 || counts == 20) {
						throw new BusinessException(BizConstants.ErrorCode.ACTIVITY_COMMOND_GOODS_FULL_CODE,
								BizConstants.ErrorConstants.ACTIVITY_COMMOND_GOODS_FULL);
					} else if (counts < 20) {
						Integer activityResult = psGoodsDao.insertPsGoodsActivity(psGoodsActivity);
						if (activityResult < 0) {
							throw new BusinessException(BizConstants.ErrorCode.DISPLAY_INDEX_FAIL_CODE,
									BizConstants.ErrorConstants.DISPLAY_INDEX_FAIL);
						}
					}
				}
				if (BizConstants.AppCommondGoods.YES.equals(paramsMap.get(BizConstants.AppCommondGoods.MAP_FIELD))) {// 推荐商品
					// ID不同 - 活动类型不同
					psGoodsActivity.setGoods_activity_id(IDUtil.getUUID());
					psGoodsActivity.setActivity_type(
							ActivityType.getTypeByName(BizConstants.ActivityTypeName.BEIKE_MALL_COMMOND_GOODS));
					logger.info("当前推荐activity Type :" + psGoodsActivity.getActivity_type());
					Integer activityResult = psGoodsDao.insertPsGoodsActivity(psGoodsActivity);
					if (activityResult < 0) {
						throw new BusinessException(BizConstants.ErrorCode.COMMOND_GOODS_FAIL_CODE,
								BizConstants.ErrorConstants.COMMOND_GOODS_FAIL);
					}
				}
			} else if (!(BizConstants.AppIndexShow.YES.equals(paramsMap.get(BizConstants.AppIndexShow.MAP_FIELD))
					&& BizConstants.AppCommondGoods.YES
							.equals(paramsMap.get(BizConstants.AppCommondGoods.MAP_FIELD)))) {// 6个活动商品
				if (BizConstants.AppIndexShow.YES.equals(paramsMap.get(BizConstants.AppIndexShow.MAP_FIELD))) {
					logger.info("当前首页展示activity Type :" + psGoodsActivity.getActivity_type());
					Integer activityResult = psGoodsDao.insertPsGoodsActivity(psGoodsActivity);
					if (activityResult < 0) {
						throw new BusinessException(BizConstants.ErrorCode.DISPLAY_INDEX_FAIL_CODE,
								BizConstants.ErrorConstants.DISPLAY_INDEX_FAIL);
					}
				}
				if (BizConstants.AppCommondGoods.YES.equals(paramsMap.get(BizConstants.AppCommondGoods.MAP_FIELD))) {// 推荐商品
					// ID不同 - 活动类型不同
					psGoodsActivity.setGoods_activity_id(IDUtil.getUUID());
					psGoodsActivity.setActivity_type(
							ActivityType.getTypeByName(BizConstants.ActivityTypeName.BEIKE_MALL_COMMOND_GOODS));
					logger.info("当前推荐activity Type :" + psGoodsActivity.getActivity_type());
					int counts = psGoodsDao.selectPsGoodsActivityListByStatushb(BizConstants.ActivityStatus.NO);
					if (counts > 20 || counts == 20) {
						throw new BusinessException(BizConstants.ErrorCode.COMMOND_GOODS_FAIL_CODE,
								BizConstants.ErrorConstants.COMMOND_GOODS_FAIL);
					} else {
						Integer activityResultone = psGoodsDao.insertPsGoodsActivity(psGoodsActivity);
						if (activityResultone < 0) {
							throw new BusinessException(BizConstants.ErrorCode.COMMOND_GOODS_FAIL_CODE,
									BizConstants.ErrorConstants.COMMOND_GOODS_FAIL);
						}
					}
				}
			}
		} else if (BizConstants.Operator.OFF_LINE.equals(paramsMap.get("status"))) {// 下架操作，清除活动
			psGoodsDao.updateActivityGoodsStatus(BizConstants.ActivityStatus.YES, paramsMap.get("goods_id"));
		}
		// 清除缓存
		boolean flag = false;
		for (String key : freeGetGoodsListCacheKeySet) {
			redisUtil.delObj(key);
			flag = true;
		}
		if (flag) {
			freeGetGoodsListCacheKeySet.clear();
		}
	}


	//插入活动商品
	private void insertActivityGoods(Map<String, Object> paramsMap, PsGoodsActivity psGoodsActivity) throws Exception {
		if(BizConstants.AppIndexShow.YES.equals(paramsMap.get(BizConstants.AppIndexShow.MAP_FIELD))){//6个活动商品
			logger.info("当前首页展示activity Type :" + psGoodsActivity.getActivity_type());
			Integer activityResult = psGoodsDao.insertPsGoodsActivity(psGoodsActivity);
			if(activityResult < 0){
				throw new BusinessException(BizConstants.ErrorCode.DISPLAY_INDEX_FAIL_CODE,BizConstants.ErrorConstants.DISPLAY_INDEX_FAIL);
			}
		}else if(BizConstants.vipgoods.YES.equals(paramsMap.get(BizConstants.vipgoods.MAP_FIELD))) { //vip商品
			logger.info("activity Type ：" + psGoodsActivity.getActivity_type());
			psGoodsActivity.setActivity_type(ActivityType.getTypeByName(BizConstants.ActivityTypeName.HONG_BAO_VIP_GOODS));
			Integer activityResult = psGoodsDao.insertPsGoodsActivity(psGoodsActivity);
			if (activityResult < 0) {
				throw new BusinessException(BizConstants.ErrorCode.DISPLAY_INDEX_FAIL_CODE, BizConstants.ErrorConstants.DISPLAY_INDEX_FAIL);
			}
		}
		if(BizConstants.AppCommondGoods.YES.equals(paramsMap.get(BizConstants.AppCommondGoods.MAP_FIELD))) {//推荐商品
			//ID不同   -  活动类型不同
			psGoodsActivity.setGoods_activity_id(IDUtil.getUUID());
			int count = psGoodsDao.selectPsGoodsActivityListByStatushb(BizConstants.ActivityStatus.NO);
			if (count > 20 || count == 20) {
				throw new BusinessException(BizConstants.ErrorCode.ACTIVITY_COMMOND_GOODS_FULL_CODE, BizConstants.ErrorConstants.ACTIVITY_COMMOND_GOODS_FULL);
			} else if (count < 20) {
				if (paramsMap.get("goods_type") != null && Integer.parseInt(paramsMap.get("goods_type").toString()) == 2) {//红包兑推荐商品
					psGoodsActivity.setActivity_type(ActivityType.getTypeByName(BizConstants.ActivityTypeName.HONG_BAO_COMMOND_GOODS));
				} else {//贝壳商城推荐商品
					psGoodsActivity.setActivity_type(ActivityType.getTypeByName(BizConstants.ActivityTypeName.BEIKE_MALL_COMMOND_GOODS));
				}
				logger.info("当前推荐activity Type :" + psGoodsActivity.getActivity_type());
				Integer activityResult = psGoodsDao.insertPsGoodsActivity(psGoodsActivity);
				if (activityResult < 0) {
					throw new BusinessException(BizConstants.ErrorCode.COMMOND_GOODS_FAIL_CODE, BizConstants.ErrorConstants.COMMOND_GOODS_FAIL);
				}
			}
		}
	}

	//封装 activityGood  chefu modify
	private PsGoodsActivity activityGood(Map<String, Object> paramsMap) {
		PsGoodsActivity psGoodsActivity = (PsGoodsActivity) BeanUtil.mapToBean(paramsMap, PsGoodsActivity.class);
		psGoodsActivity.setGoods_activity_id(IDUtil.getUUID());
		//字段完全对应不上 - 草
		//psGoodsActivity.setGood
		psGoodsActivity.setStart_date(DateUtil.str2Date(paramsMap.get("valid_thru").toString(),DateUtil.fmt_yyyyMMddHHmmss));
		if(paramsMap.containsKey("offline_date")){
			psGoodsActivity.setEnd_date(DateUtil.str2Date(paramsMap.get("offline_date").toString(),DateUtil.fmt_yyyyMMddHHmmss));
		}
		psGoodsActivity.setIs_finished(BizConstants.ActivityStatus.NO);
		psGoodsActivity.setGoods_title(paramsMap.get("title").toString());
		if(paramsMap.containsKey("vip_price")) {
			psGoodsActivity.setGoods_price(BigDecimalUtil.stringToBigDecimal(paramsMap.get("vip_price").toString()));
		}else{
			psGoodsActivity.setGoods_price(BigDecimalUtil.ZERO);
		}
		if(paramsMap.get("goods_type")!=null && Integer.parseInt(paramsMap.get("goods_type").toString())==2){//红包对
			String pic_info = hongbaoGoodsDao.selectPicByGoodsId(paramsMap.get("goods_id").toString());
			String goods_desc = hongbaoGoodsDao.selectDescByGoodsId(paramsMap.get("goods_id").toString());
			psGoodsActivity.setPic_info(pic_info);
			psGoodsActivity.setGoods_desc(goods_desc);
			psGoodsActivity.setActivity_type(ActivityType.getTypeByName(BizConstants.ActivityTypeName.HONG_BAO_GOODS));
			psGoodsActivity.setGoods_hongbao(BigDecimalUtil.stringToBigDecimal(paramsMap.get("hongbao_price").toString()));
			psGoodsActivity.setGoods_beike(BigDecimalUtil.ZERO);
		}else if(paramsMap.get("goods_type")!=null && Integer.parseInt(paramsMap.get("goods_type").toString())==3){//贝壳Street
			psGoodsActivity.setActivity_type(ActivityType.getTypeByName(BizConstants.ActivityTypeName.BEIKE_STREET));
		}else{//贝壳Mall
			String pic_info = psGoodsDao.selectBeikePicByGoodsId(paramsMap.get("goods_id").toString());
			String goods_desc = psGoodsDao.selectDescByGoodsId(paramsMap.get("goods_id").toString());
			psGoodsActivity.setPic_info(pic_info);
			psGoodsActivity.setGoods_desc(goods_desc);
			psGoodsActivity.setActivity_type(ActivityType.getTypeByName(BizConstants.ActivityTypeName.BEIKE_MALL));
			psGoodsActivity.setGoods_beike(BigDecimalUtil.stringToBigDecimal(paramsMap.get("beike_credit").toString()));
			psGoodsActivity.setGoods_hongbao(BigDecimalUtil.ZERO);
		}
		if(psGoodsActivity.getStart_date() == null){
			psGoodsActivity.setStart_date(new Date());
		}
		if(psGoodsActivity.getMarket_price() == null){
			psGoodsActivity.setMarket_price(BigDecimalUtil.ZERO);
		}
		return psGoodsActivity;
	}

	@Override
	public void psGoodsFindForWeb(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "area_id" });
		List<String> areaIdList = new ArrayList<String>();
		areaIdList.add((String) paramsMap.get("area_id"));
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("area_code", paramsMap.get("area_id"));
		Map<String, Object> areaMap = goodsDao.selectAreaCodeTree(tempMap);
		if (areaMap != null) {
			if (null != areaMap.get("s_code") && !"".equals(areaMap.get("s_code"))) {
				areaIdList.add((String) areaMap.get("s_code"));
			}
			if (null != areaMap.get("t_code") && !"".equals(areaMap.get("t_code"))) {
				areaIdList.add((String) areaMap.get("t_code"));
			}
		}
		areaIdList.add("100000");
		paramsMap.put("areaList", areaIdList);
		List<PsGoods> psGoodsList = psGoodsDao.selectPsGoodsForWeb(paramsMap);
		List<String> doc_ids = new ArrayList<String>();
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (PsGoods psGoods : psGoodsList) {
			Map<String, Object> psGoodsMap = new HashMap<String, Object>();
			psGoodsMap.put("goods_id", psGoods.getGoods_id());
			psGoodsMap.put("goods_code", StringUtil.formatStr(psGoods.getGoods_code()));
			psGoodsMap.put("title", psGoods.getTitle());
			psGoodsMap.put("label", StringUtil.formatStr(psGoods.getLabel()));
			psGoodsMap.put("label_promotion", StringUtil.formatStr(psGoods.getLabel_promotion()));
			psGoodsMap.put("category", psGoods.getCategory());
			psGoodsMap.put("display_area", psGoods.getDisplay_area());
			psGoodsMap.put("specification", StringUtil.formatStr(psGoods.getSpecification()));
			psGoodsMap.put("pack", StringUtil.formatStr(psGoods.getPack()));
			psGoodsMap.put("market_price", psGoods.getMarket_price() + "");
			psGoodsMap.put("discount_price", psGoods.getDiscount_price() + "");
			psGoodsMap.put("min_buy_qty", psGoods.getMin_buy_qty() + "");
			psGoodsMap.put("max_buy_qty", psGoods.getMax_buy_qty() + "");
			psGoodsMap.put("valid_thru", StringUtil.formatStr(psGoods.getValid_thru()));
			psGoodsMap.put("payment_way", StringUtil.formatStr(psGoods.getPayment_way()));
			psGoodsMap.put("status", psGoods.getStatus());
			psGoodsMap.put("sale_qty", psGoods.getSale_qty() + "");
			psGoodsMap.put("stock_qty", psGoods.getStock_qty() + "");
			psGoodsMap.put("pic_info", StringUtil.formatStr(psGoods.getPic_info()));
			// 解析区域
			String[] areaStr = psGoods.getDelivery_area().split(",");
			tempMap.clear();
			tempMap.put("areaStr", areaStr);
			List<Map<String, Object>> areaList = goodsDao.selectMDArea(tempMap);
			String delivery_desc = "";
			for (Map<String, Object> area : areaList) {
				delivery_desc = delivery_desc + "|" + area.get("path");
			}
			if (delivery_desc.trim().length() > 0) {
				delivery_desc = delivery_desc.substring(1);
			}
			psGoodsMap.put("delivery_area", StringUtil.formatStr(psGoods.getDelivery_area()));
			psGoodsMap.put("delivery_desc", delivery_desc);
			resultList.add(psGoodsMap);
			// 解析图片
			String pic_info = StringUtil.formatStr(psGoods.getPic_info());
			if (!pic_info.equals("")) {
				List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_info);
				for (Map<String, Object> m : tempList) {
					if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
						doc_ids.add(m.get("path_id") + "");
					}
				}
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("doc_url", docUtil.imageUrlFind(doc_ids));
		map.put("list", resultList);
		result.setResultData(map);
	}

	@Override
	public void tsGoodsListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "area_id" });
		List<String> areaIdList = new ArrayList<String>();
		String area_id = paramsMap.get("area_id") + "";
		areaIdList.add(area_id);
		areaIdList.add(area_id.substring(0, area_id.length() - 2) + "00");
		areaIdList.add(area_id.substring(0, area_id.length() - 3) + "000");
		areaIdList.add(area_id.substring(0, area_id.length() - 4) + "0000");
		areaIdList.add("100000");
		paramsMap.put("areaList", areaIdList);
		List<PsGoods> psGoodsList = psGoodsDao.selectPsGoodsForH5(paramsMap);
		List<String> doc_ids = new ArrayList<String>();
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (PsGoods psGoods : psGoodsList) {
			Map<String, Object> psGoodsMap = new HashMap<String, Object>();
			psGoodsMap.put("goods_id", psGoods.getGoods_id());
			psGoodsMap.put("label", psGoods.getLabel());
			psGoodsMap.put("title", psGoods.getTitle());
			psGoodsMap.put("market_price", psGoods.getMarket_price() + "");
			// 修复ios版本 621后的版本可以废除
			psGoodsMap.put("discount_price", psGoods.getTs_price() + "");
			psGoodsMap.put("ts_min_num", psGoods.getTs_min_num());
			psGoodsMap.put("ts_price", psGoods.getTs_price() + "");
			psGoodsMap.put("sale_qty", psGoods.getSale_qty() + "");
			psGoodsMap.put("stock_qty", psGoods.getStock_qty() + "");
			psGoodsMap.put("pic_info", StringUtil.formatStr(psGoods.getPic_info()));
			psGoodsMap.put("taobao_link", StringUtil.formatStr(psGoods.getTaobao_link()));
			psGoodsMap.put("taobao_sales", StringUtil.formatStr(psGoods.getTaobao_sales()));
			resultList.add(psGoodsMap);
			// 解析图片
			String pic_info = StringUtil.formatStr(psGoods.getPic_info());
			if (!pic_info.equals("")) {
				List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_info);
				for (Map<String, Object> m : tempList) {
					if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
						doc_ids.add(m.get("path_id") + "");
					}
				}
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("doc_url", docUtil.imageUrlFind(doc_ids));
		map.put("list", resultList);
		result.setResultData(map);
	}

	@Override
	public void psGoodsFindForH5(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "area_id" });
		List<String> areaIdList = new ArrayList<String>();
		String area_id = paramsMap.get("area_id") + "";
		areaIdList.add(area_id);
		areaIdList.add(area_id.substring(0, area_id.length() - 2) + "00");
		areaIdList.add(area_id.substring(0, area_id.length() - 3) + "000");
		areaIdList.add(area_id.substring(0, area_id.length() - 4) + "0000");
		areaIdList.add("100000");
		paramsMap.put("areaList", areaIdList);
		List<PsGoods> psGoodsList = psGoodsDao.selectPsGoodsForH5(paramsMap);
		Map<String, Object> tempMap = new HashMap<String, Object>();
		List<String> doc_ids = new ArrayList<String>();
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (PsGoods psGoods : psGoodsList) {
			Map<String, Object> psGoodsMap = new HashMap<String, Object>();
			psGoodsMap.put("goods_id", psGoods.getGoods_id());
			psGoodsMap.put("title", psGoods.getTitle());
			psGoodsMap.put("desc1", StringUtil.formatStr(psGoods.getDesc1()));
			psGoodsMap.put("label", StringUtil.formatStr(psGoods.getLabel()));
			psGoodsMap.put("label_promotion", StringUtil.formatStr(psGoods.getLabel_promotion()));
			psGoodsMap.put("category", psGoods.getCategory());
			psGoodsMap.put("display_area", psGoods.getDisplay_area());
			psGoodsMap.put("specification", StringUtil.formatStr(psGoods.getSpecification()));
			psGoodsMap.put("supplier", StringUtil.formatStr(psGoods.getSupplier()));
			psGoodsMap.put("pack", StringUtil.formatStr(psGoods.getPack()));
			psGoodsMap.put("market_price", psGoods.getMarket_price() + "");
			psGoodsMap.put("discount_price", psGoods.getDiscount_price() + "");
			psGoodsMap.put("min_buy_qty", psGoods.getMin_buy_qty() + "");
			psGoodsMap.put("max_buy_qty", psGoods.getMax_buy_qty() + "");
			psGoodsMap.put("valid_thru", StringUtil.formatStr(psGoods.getValid_thru()));
			psGoodsMap.put("status", psGoods.getStatus());
			psGoodsMap.put("sale_qty", psGoods.getSale_qty() + "");
			psGoodsMap.put("stock_qty", psGoods.getStock_qty() + "");
			psGoodsMap.put("pic_info", StringUtil.formatStr(psGoods.getPic_info()));
			// 解析区域
			String[] areaStr = psGoods.getDelivery_area().split(",");
			tempMap.clear();
			tempMap.put("areaStr", areaStr);
			List<Map<String, Object>> areaList = goodsDao.selectMDArea(tempMap);
			String delivery_desc = "";
			for (Map<String, Object> area : areaList) {
				delivery_desc = delivery_desc + "|" + area.get("path");
			}
			if (delivery_desc.trim().length() > 0) {
				delivery_desc = delivery_desc.substring(1);
			}
			psGoodsMap.put("delivery_area", StringUtil.formatStr(psGoods.getDelivery_area()));
			psGoodsMap.put("delivery_desc", delivery_desc);
			resultList.add(psGoodsMap);
			// 解析图片
			String pic_info = StringUtil.formatStr(psGoods.getPic_info());
			if (!pic_info.equals("")) {
				List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_info);
				for (Map<String, Object> m : tempList) {
					if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
						doc_ids.add(m.get("path_id") + "");
					}
				}
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("doc_url", docUtil.imageUrlFind(doc_ids));
		map.put("list", resultList);
		result.setResultData(map);
	}

	@Override
	public void wypGoodsDetailFindForH5(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "goods_id" });
		List<PsGoods> psGoodsList = psGoodsDao.selectPsGoods(paramsMap);
		if (psGoodsList.size() == 0) {
			throw new BusinessException(RspCode.PS_GOODS_EXIST, "我要批商品不存在");
		}
		List<String> doc_ids = new ArrayList<String>();
		Map<String, Object> goodsMap = new HashMap<String, Object>();
		PsGoods psGoods = psGoodsList.get(0);
		goodsMap.put("goods_id", psGoods.getGoods_id());
		goodsMap.put("goods_code", StringUtil.formatStr(psGoods.getGoods_code()));
		goodsMap.put("title", psGoods.getTitle());
		goodsMap.put("desc1", StringUtil.formatStr(psGoods.getDesc1()));
		goodsMap.put("area_id", psGoods.getArea_id());
		goodsMap.put("label", StringUtil.formatStr(psGoods.getLabel()));
		goodsMap.put("label_promotion", StringUtil.formatStr(psGoods.getLabel_promotion()));
		goodsMap.put("category", psGoods.getCategory());
		goodsMap.put("data_source", StringUtil.formatStr(psGoods.getData_source()));
		goodsMap.put("pack", StringUtil.formatStr(psGoods.getPack()));
		goodsMap.put("supplier", StringUtil.formatStr(psGoods.getSupplier()));
		goodsMap.put("supplier_id", StringUtil.formatStr(psGoods.getSupplier_id()));
		goodsMap.put("warehouse", StringUtil.formatStr(psGoods.getWarehouse()));
		goodsMap.put("warehouse_id", StringUtil.formatStr(psGoods.getWarehouse_id()));
		goodsMap.put("min_buy_qty", psGoods.getMin_buy_qty() + "");
		goodsMap.put("max_buy_qty", psGoods.getMax_buy_qty() + "");
		goodsMap.put("goods_unit", StringUtil.formatStr(psGoods.getGoods_unit()));
		goodsMap.put("delivery", StringUtil.formatStr(psGoods.getDelivery()));
		goodsMap.put("valid_thru", StringUtil.formatStr(psGoods.getValid_thru()));
		goodsMap.put("payment_way", StringUtil.formatStr(psGoods.getPayment_way()));
		goodsMap.put("pic_info", StringUtil.formatStr(psGoods.getPic_info()));
		goodsMap.put("pic_detail_info", StringUtil.formatStr(psGoods.getPic_detail_info()));
		goodsMap.put("online_date", DateUtil.date2Str(psGoods.getOnline_date(), DateUtil.fmt_yyyyMMddHHmmss));
		goodsMap.put("sale_qty", psGoods.getSale_qty() + "");
		goodsMap.put("stock_qty", psGoods.getStock_qty() + "");
		goodsMap.put("discount_price", psGoods.getDiscount_price() + "");
		goodsMap.put("market_price", psGoods.getMarket_price() + "");
		goodsMap.put("specification", StringUtil.formatStr(psGoods.getSpecification()));
		goodsMap.put("remark", StringUtil.formatStr(psGoods.getRemark()));
		// 解析区域
		String[] areaStr = psGoods.getDelivery_area().split(",");
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("areaStr", areaStr);
		List<Map<String, Object>> areaList = goodsDao.selectMDArea(tempMap);
		String delivery_desc = "";
		for (Map<String, Object> area : areaList) {
			delivery_desc = delivery_desc + "|" + area.get("path");
		}
		if (delivery_desc.trim().length() > 0) {
			delivery_desc = delivery_desc.substring(1);
		}
		goodsMap.put("delivery_area", StringUtil.formatStr(psGoods.getDelivery_area()));
		goodsMap.put("delivery_desc", delivery_desc);
		// 解析图片
		String pic_info = StringUtil.formatStr(psGoods.getPic_info());
		if (!pic_info.equals("")) {
			List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_info);
			for (Map<String, Object> m : tempList) {
				if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
					doc_ids.add(m.get("path_id") + "");
				}
			}
		}
		// 解析详情图片
		String pic_detail_info = StringUtil.formatStr(psGoods.getPic_detail_info());
		if (!pic_detail_info.equals("")) {
			List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_detail_info);
			for (Map<String, Object> m : tempList) {
				if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
					doc_ids.add(m.get("path_id") + "");
				}
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("doc_url", docUtil.imageUrlFind(doc_ids));
		map.put("detail", goodsMap);
		result.setResultData(map);
	}

	@Override
	public void psGoodsLevelAndPointFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "goods_id" });
		List<PsGoods> list = psGoodsDao.selectPsGoods(paramsMap);
		if (CollectionUtils.isNotEmpty(list)) {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			// tempMap.put("good_level", list.get(0).getGood_level());
			// tempMap.put("good_point", list.get(0).getGood_point());
			result.setResultData(tempMap);
		}
	}

	
	@Override
	public void freeGetGoodsListFindByOwnForSmallProgram(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		String key = "[freeGetGoodsListFindByOwnForSmallProgram]";
		Object obj = redisUtil.getObj(key);
		if (null != obj) {
			result.setResultData(obj);
			return;
		}
		// 查询可以购买的商品
		paramsMap.put("valid_thru_end", DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss));
		List<PsGoods> psGoodsList = psGoodsDao.selectFgGoodsListByOwnForSmallProgram(paramsMap);
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (PsGoods psGoods : psGoodsList) {
			Map<String, Object> psGoodsMap = new HashMap<String, Object>();
			psGoodsMap.put("goods_id", psGoods.getGoods_id());
			psGoodsMap.put("title", psGoods.getTitle());
			psGoodsMap.put("market_price", psGoods.getMarket_price() + "");
			psGoodsMap.put("discount_price", psGoods.getDiscount_price() + "");
			psGoodsMap.put("sale_qty", psGoods.getSale_qty() + "");
			psGoodsMap.put("stock_qty", psGoods.getStock_qty() + "");
			psGoodsMap.put("taobao_price", StringUtil.formatStr(psGoods.getTaobao_price()));
			psGoodsMap.put("taobao_link", StringUtil.formatStr(psGoods.getTaobao_link()));
			psGoodsMap.put("taobao_sales", StringUtil.formatStr(psGoods.getTaobao_sales()));
			psGoodsMap.put("data_source", StringUtil.formatStr(psGoods.getData_source()));
			// 解析图片
			String pic_info = StringUtil.formatStr(psGoods.getPic_info());
			if (StringUtil.isNotEmpty(pic_info)) {
				List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_info);
				for (Map<String, Object> m : tempList) {
					if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
						String path_id = m.get("path_id") + "";
						String pic_info_url = docUtil.imageUrlFind(path_id);
						psGoodsMap.put("pic_info_url", pic_info_url);
						break;
					}
				}
			}
			resultList.add(psGoodsMap);
		}
		Map<String, Object> resultDataMap = new HashMap<String, Object>();
		resultDataMap.put("list", resultList);
		result.setResultData(resultDataMap);
		// 设置缓存
		redisUtil.setObj(key, resultDataMap, 300);
		freeGetGoodsListCacheKeySet.add(key);
		
	}
	
	@Override
	public void freeGetGoodsListFindByHuiguoForSmallProgram(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		String key = "[freeGetGoodsListFindByHuiguoForSmallProgram]";
		Object obj = redisUtil.getObj(key);
		if (null != obj) {
			result.setResultData(obj);
			return;
		}
		// 查询可以购买的商品
		paramsMap.put("valid_thru_end", DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss));
		List<PsGoods> psGoodsList = psGoodsDao.selectFgGoodsListByHuiguoForSmallProgram(paramsMap);
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (PsGoods psGoods : psGoodsList) {
			Map<String, Object> psGoodsMap = new HashMap<String, Object>();
			psGoodsMap.put("goods_id", psGoods.getGoods_id());
			psGoodsMap.put("title", psGoods.getTitle());
			psGoodsMap.put("market_price", psGoods.getMarket_price() + "");
			psGoodsMap.put("discount_price", psGoods.getDiscount_price() + "");
			psGoodsMap.put("sale_qty", psGoods.getSale_qty() + "");
			psGoodsMap.put("stock_qty", psGoods.getStock_qty() + "");
			psGoodsMap.put("taobao_price", StringUtil.formatStr(psGoods.getTaobao_price()));
			psGoodsMap.put("taobao_link", StringUtil.formatStr(psGoods.getTaobao_link()));
			psGoodsMap.put("taobao_sales", StringUtil.formatStr(psGoods.getTaobao_sales()));
			psGoodsMap.put("data_source", StringUtil.formatStr(psGoods.getData_source()));
			// 解析图片
			String pic_info = StringUtil.formatStr(psGoods.getPic_info());
			if (StringUtil.isNotEmpty(pic_info)) {
				List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_info);
				for (Map<String, Object> m : tempList) {
					if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
						String path_id = m.get("path_id") + "";
						String pic_info_url = docUtil.imageUrlFind(path_id);
						psGoodsMap.put("pic_info_url", pic_info_url);
						break;
					}
				}
			}
			resultList.add(psGoodsMap);
		}
		Map<String, Object> resultDataMap = new HashMap<String, Object>();
		resultDataMap.put("list", resultList);
		result.setResultData(resultDataMap);
		// 设置缓存
		redisUtil.setObj(key, resultDataMap, 300);
		freeGetGoodsListCacheKeySet.add(key);
		
	}
	
	@Override
	public void freeGetGoodsListPageFindForStores(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		String key = "[freeGetGoodsListPageFindForStores]";
		Object obj = redisUtil.getObj(key);
		if (null != obj) {
			result.setResultData(obj);
			return;
		}
		// 查询可以购买的商品
		paramsMap.put("valid_thru_end", DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss));
		List<PsGoods> psGoodsList = psGoodsDao.selectFgGoodsListForStores(paramsMap);
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (PsGoods psGoods : psGoodsList) {
			Map<String, Object> psGoodsMap = new HashMap<String, Object>();
			psGoodsMap.put("goods_id", psGoods.getGoods_id());
			psGoodsMap.put("title", psGoods.getTitle());
			psGoodsMap.put("market_price", psGoods.getMarket_price() + "");
			psGoodsMap.put("discount_price", psGoods.getDiscount_price() + "");
			psGoodsMap.put("sale_qty", psGoods.getSale_qty() + "");
			psGoodsMap.put("stock_qty", psGoods.getStock_qty() + "");
			psGoodsMap.put("taobao_price", StringUtil.formatStr(psGoods.getTaobao_price()));
			psGoodsMap.put("taobao_link", StringUtil.formatStr(psGoods.getTaobao_link()));
			psGoodsMap.put("taobao_sales", StringUtil.formatStr(psGoods.getTaobao_sales()));
			psGoodsMap.put("data_source", StringUtil.formatStr(psGoods.getData_source()));
			// 解析图片
			String pic_info = StringUtil.formatStr(psGoods.getPic_info());
			if (StringUtil.isNotEmpty(pic_info)) {
				List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_info);
				for (Map<String, Object> m : tempList) {
					if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
						String path_id = m.get("path_id") + "";
						String pic_info_url = docUtil.imageUrlFind(path_id);
						psGoodsMap.put("pic_info_url", pic_info_url);
						break;
					}
				}
			}
			resultList.add(psGoodsMap);
		}
		Map<String, Object> resultDataMap = new HashMap<String, Object>();
		resultDataMap.put("list", resultList);
		result.setResultData(resultDataMap);
		// 设置缓存
		redisUtil.setObj(key, resultDataMap, 300);
		freeGetGoodsListCacheKeySet.add(key);
	}
	
	@Override
	public void freeGetGoodsListFindForSmallProgram(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		String key = "[freeGetGoodsListFindForSmallProgram]";
		Object obj = redisUtil.getObj(key);
		if (null != obj) {
			result.setResultData(obj);
			return;
		}
		// 查询可以购买的商品
		paramsMap.put("valid_thru_end", DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss));
		List<PsGoods> psGoodsList = psGoodsDao.selectFgGoodsListForSmallProgram(paramsMap);
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (PsGoods psGoods : psGoodsList) {
			Map<String, Object> psGoodsMap = new HashMap<String, Object>();
			psGoodsMap.put("goods_id", psGoods.getGoods_id());
			psGoodsMap.put("title", psGoods.getTitle());
			psGoodsMap.put("market_price", psGoods.getMarket_price() + "");
			psGoodsMap.put("discount_price", psGoods.getDiscount_price() + "");
			psGoodsMap.put("sale_qty", psGoods.getSale_qty() + "");
			psGoodsMap.put("stock_qty", psGoods.getStock_qty() + "");
			psGoodsMap.put("taobao_price", StringUtil.formatStr(psGoods.getTaobao_price()));
			psGoodsMap.put("taobao_link", StringUtil.formatStr(psGoods.getTaobao_link()));
			psGoodsMap.put("taobao_sales", StringUtil.formatStr(psGoods.getTaobao_sales()));
			psGoodsMap.put("data_source", StringUtil.formatStr(psGoods.getData_source()));
			// 解析图片
			String pic_info = StringUtil.formatStr(psGoods.getPic_info());
			if (StringUtil.isNotEmpty(pic_info)) {
				List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_info);
				for (Map<String, Object> m : tempList) {
					if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
						String path_id = m.get("path_id") + "";
						String pic_info_url = docUtil.imageUrlFind(path_id);
						psGoodsMap.put("pic_info_url", pic_info_url);
						break;
					}
				}
			}
			resultList.add(psGoodsMap);
		}
		Map<String, Object> resultDataMap = new HashMap<String, Object>();
		resultDataMap.put("list", resultList);
		result.setResultData(resultDataMap);
		// 设置缓存
		redisUtil.setObj(key, resultDataMap, 300);
		freeGetGoodsListCacheKeySet.add(key);
	}
	
	
	@Override
	public void freeGetGoodsListFindForOwn(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		String key = "[freeGetGoodsListFindForOwn]";
		Object obj = redisUtil.getObj(key);
		if (null != obj) {
			result.setResultData(obj);
			return;
		}
		// 查询可以购买的商品
		paramsMap.put("valid_thru_end", DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss));
		List<PsGoods> psGoodsList = psGoodsDao.selectFgGoodsListForOwn(paramsMap);
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (PsGoods psGoods : psGoodsList) {
			Map<String, Object> psGoodsMap = new HashMap<String, Object>();
			psGoodsMap.put("goods_id", psGoods.getGoods_id());
			psGoodsMap.put("title", psGoods.getTitle());
			psGoodsMap.put("market_price", psGoods.getMarket_price() + "");
			psGoodsMap.put("discount_price", psGoods.getDiscount_price() + "");
			psGoodsMap.put("sale_qty", psGoods.getSale_qty() + "");
			psGoodsMap.put("stock_qty", psGoods.getStock_qty() + "");
			psGoodsMap.put("goods_code", psGoods.getGoods_code());
			psGoodsMap.put("taobao_price", StringUtil.formatStr(psGoods.getTaobao_price()));
			psGoodsMap.put("taobao_link", StringUtil.formatStr(psGoods.getTaobao_link()));
			psGoodsMap.put("taobao_sales", StringUtil.formatStr(psGoods.getTaobao_sales()));
			psGoodsMap.put("data_source", StringUtil.formatStr(psGoods.getData_source()));
			// 解析图片
			String pic_info = StringUtil.formatStr(psGoods.getPic_info());
			if (StringUtil.isNotEmpty(pic_info)) {
				List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_info);
				for (Map<String, Object> m : tempList) {
					if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
						String path_id = m.get("path_id") + "";
						String pic_info_url = docUtil.imageUrlFind(path_id);
						psGoodsMap.put("pic_info_url", pic_info_url);
						break;
					}
				}
			}
			resultList.add(psGoodsMap);
		}
		Map<String, Object> resultDataMap = new HashMap<String, Object>();
		resultDataMap.put("list", resultList);
		result.setResultData(resultDataMap);
		// 设置缓存
		redisUtil.setObj(key, resultDataMap, 300);
		freeGetGoodsListCacheKeySet.add(key);
		
	}
	
	
	@Override
	public void freeGetGoodsListFindForHuiguo(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		String key = "[freeGetGoodsListFindForHuiguo]";
		Object obj = redisUtil.getObj(key);
		if (null != obj) {
			result.setResultData(obj);
			return;
		}
		// 查询可以购买的商品
		paramsMap.put("valid_thru_end", DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss));
		List<PsGoods> psGoodsList = psGoodsDao.selectFgGoodsListForHuiguo(paramsMap);
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (PsGoods psGoods : psGoodsList) {
			Map<String, Object> psGoodsMap = new HashMap<String, Object>();
			psGoodsMap.put("goods_id", psGoods.getGoods_id());
			psGoodsMap.put("title", psGoods.getTitle());
			psGoodsMap.put("market_price", psGoods.getMarket_price() + "");
			psGoodsMap.put("discount_price", psGoods.getDiscount_price() + "");
			psGoodsMap.put("sale_qty", psGoods.getSale_qty() + "");
			psGoodsMap.put("stock_qty", psGoods.getStock_qty() + "");
			psGoodsMap.put("taobao_price", StringUtil.formatStr(psGoods.getTaobao_price()));
			psGoodsMap.put("taobao_link", StringUtil.formatStr(psGoods.getTaobao_link()));
			psGoodsMap.put("taobao_sales", StringUtil.formatStr(psGoods.getTaobao_sales()));
			psGoodsMap.put("data_source", StringUtil.formatStr(psGoods.getData_source()));
			// 解析图片
			String pic_info = StringUtil.formatStr(psGoods.getPic_info());
			if (StringUtil.isNotEmpty(pic_info)) {
				List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_info);
				for (Map<String, Object> m : tempList) {
					if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
						String path_id = m.get("path_id") + "";
						String pic_info_url = docUtil.imageUrlFind(path_id);
						psGoodsMap.put("pic_info_url", pic_info_url);
						break;
					}
				}
			}
			resultList.add(psGoodsMap);
		}
		Map<String, Object> resultDataMap = new HashMap<String, Object>();
		resultDataMap.put("list", resultList);
		result.setResultData(resultDataMap);
		// 设置缓存
		redisUtil.setObj(key, resultDataMap, 300);
		freeGetGoodsListCacheKeySet.add(key);
		
	}
	
	@Override
	public void freeGetGoodsListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		String key = "[freeGetGoodsListFind]";
		Object obj = redisUtil.getObj(key);
		if (null != obj) {
			result.setResultData(obj);
			return;
		}
		// 查询可以购买的商品
		paramsMap.put("valid_thru_end", DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss));
		List<PsGoods> psGoodsList = psGoodsDao.selectFgGoodsListForH5(paramsMap);
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (PsGoods psGoods : psGoodsList) {
			Map<String, Object> psGoodsMap = new HashMap<String, Object>();
			psGoodsMap.put("goods_id", psGoods.getGoods_id());
			psGoodsMap.put("title", psGoods.getTitle());
			psGoodsMap.put("market_price", psGoods.getMarket_price() + "");
			psGoodsMap.put("discount_price", psGoods.getDiscount_price() + "");
			psGoodsMap.put("sale_qty", psGoods.getSale_qty() + "");
			psGoodsMap.put("stock_qty", psGoods.getStock_qty() + "");
			psGoodsMap.put("taobao_price", StringUtil.formatStr(psGoods.getTaobao_price()));
			psGoodsMap.put("taobao_link", StringUtil.formatStr(psGoods.getTaobao_link()));
			psGoodsMap.put("taobao_sales", StringUtil.formatStr(psGoods.getTaobao_sales()));
			psGoodsMap.put("data_source", StringUtil.formatStr(psGoods.getData_source()));
			// 解析图片
			String pic_info = StringUtil.formatStr(psGoods.getPic_info());
			if (StringUtil.isNotEmpty(pic_info)) {
				List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_info);
				for (Map<String, Object> m : tempList) {
					if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
						String path_id = m.get("path_id") + "";
						String pic_info_url = docUtil.imageUrlFind(path_id);
						psGoodsMap.put("pic_info_url", pic_info_url);
						break;
					}
				}
			}
			resultList.add(psGoodsMap);
		}
		Map<String, Object> resultDataMap = new HashMap<String, Object>();
		resultDataMap.put("list", resultList);
		result.setResultData(resultDataMap);
		// 设置缓存
		redisUtil.setObj(key, resultDataMap, 300);
		freeGetGoodsListCacheKeySet.add(key);
	}
	
	
	@Override
	public void freeGetGoodsPreSaleListFindForSmallProgram(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		String key = "[freeGetGoodsPreSaleListFindForSmallProgram]";
		Object obj = redisUtil.getObj(key);
		if (null != obj) {
			result.setResultData(obj);
			return;
		}
		// 有效期大于当前时间的都是预售
		String valid_thru = DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss);
		paramsMap.put("valid_thru_start", valid_thru);
		// 有效期大于当前时间的都是预售
		// paramsMap.put("valid_thru_end", DateUtil.addDate(valid_thru,
		// DateUtil.fmt_yyyyMMddHHmmss, 3, 1));
		List<PsGoods> psGoodsList = psGoodsDao.selectfreeGetGoodsPreSaleForH5(paramsMap);
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (PsGoods psGoods : psGoodsList) {
			Map<String, Object> psGoodsMap = new HashMap<String, Object>();
			psGoodsMap.put("goods_id", psGoods.getGoods_id());
			psGoodsMap.put("title", psGoods.getTitle());
			psGoodsMap.put("market_price", psGoods.getMarket_price() + "");
			psGoodsMap.put("discount_price", psGoods.getDiscount_price() + "");
			psGoodsMap.put("sale_qty", psGoods.getSale_qty() + "");
			psGoodsMap.put("valid_thru", StringUtil.formatStr(psGoods.getValid_thru()));
			psGoodsMap.put("taobao_price", StringUtil.formatStr(psGoods.getTaobao_price()));
			psGoodsMap.put("taobao_link", StringUtil.formatStr(psGoods.getTaobao_link()));
			psGoodsMap.put("taobao_sales", StringUtil.formatStr(psGoods.getTaobao_sales()));
			psGoodsMap.put("data_source", StringUtil.formatStr(psGoods.getData_source()));
			psGoodsMap.put("desc1", StringUtil.formatStr(psGoods.getDesc1()));
			
			// 解析图片
			String pic_info = StringUtil.formatStr(psGoods.getPic_info());
			if (StringUtil.isNotEmpty(pic_info)) {
				List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_info);
				for (Map<String, Object> m : tempList) {
					if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
						String path_id = m.get("path_id") + "";
						String pic_info_url = docUtil.imageUrlFind(path_id);
						psGoodsMap.put("pic_info_url", pic_info_url);
						break;
					}
				}
			}
			resultList.add(psGoodsMap);
		}
		Map<String, Object> resultDataMap = new HashMap<String, Object>();
		resultDataMap.put("list", resultList);
		result.setResultData(resultDataMap);
		// 设置缓存
		redisUtil.setObj(key, resultDataMap, 300);
		freeGetGoodsListCacheKeySet.add(key);
		
	}
	
	
	@Override
	public void freeGetGoodsPreSaleListFindForStores(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		String key = "[freeGetGoodsPreSaleListFindForStores]";
		Object obj = redisUtil.getObj(key);
		if (null != obj) {
			result.setResultData(obj);
			return;
		}
		// 有效期大于当前时间的都是预售
		String valid_thru = DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss);
		paramsMap.put("valid_thru_start", valid_thru);
		// 有效期大于当前时间的都是预售
		// paramsMap.put("valid_thru_end", DateUtil.addDate(valid_thru,
		// DateUtil.fmt_yyyyMMddHHmmss, 3, 1));
		List<PsGoods> psGoodsList = psGoodsDao.selectfreeGetGoodsPreSaleForStores(paramsMap);
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (PsGoods psGoods : psGoodsList) {
			Map<String, Object> psGoodsMap = new HashMap<String, Object>();
			psGoodsMap.put("goods_id", psGoods.getGoods_id());
			psGoodsMap.put("title", psGoods.getTitle());
			psGoodsMap.put("market_price", psGoods.getMarket_price() + "");
			psGoodsMap.put("discount_price", psGoods.getDiscount_price() + "");
			psGoodsMap.put("sale_qty", psGoods.getSale_qty() + "");
			psGoodsMap.put("valid_thru", StringUtil.formatStr(psGoods.getValid_thru()));
			psGoodsMap.put("taobao_price", StringUtil.formatStr(psGoods.getTaobao_price()));
			psGoodsMap.put("taobao_link", StringUtil.formatStr(psGoods.getTaobao_link()));
			psGoodsMap.put("taobao_sales", StringUtil.formatStr(psGoods.getTaobao_sales()));
			psGoodsMap.put("data_source", StringUtil.formatStr(psGoods.getData_source()));
			// 解析图片
			String pic_info = StringUtil.formatStr(psGoods.getPic_info());
			if (StringUtil.isNotEmpty(pic_info)) {
				List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_info);
				for (Map<String, Object> m : tempList) {
					if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
						String path_id = m.get("path_id") + "";
						String pic_info_url = docUtil.imageUrlFind(path_id);
						psGoodsMap.put("pic_info_url", pic_info_url);
						break;
					}
				}
			}
			resultList.add(psGoodsMap);
		}
		Map<String, Object> resultDataMap = new HashMap<String, Object>();
		resultDataMap.put("list", resultList);
		result.setResultData(resultDataMap);
		// 设置缓存
		redisUtil.setObj(key, resultDataMap, 300);
		freeGetGoodsListCacheKeySet.add(key);
	}
	
	@Override
	public void freeGetGoodsPreSaleListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		String flags = (String)paramsMap.get("flags");
		String key = null;
		if(StringUtil.isNotBlank(flags)){
			//明日推荐(只包含明天开卖的商品)
			key = "[freeGetGoodsPreSaleListFind_tomorrow]";
		}else {
			//商品推荐(包含今天的没到开卖时间的商品)
			key = "[freeGetGoodsPreSaleListFind]";
		}
		Object obj = redisUtil.getObj(key);
		if (null != obj) {
			result.setResultData(obj);
			return;
		}
		String valid_thru = null;
		if(StringUtil.isNotBlank(flags)){
			//明日推荐(只包含明天开卖的商品)
			valid_thru = DateUtil.addDate(DateUtil.getFormatDate(DateUtil.fmt_yyyyMMdd), DateUtil.fmt_yyyyMMdd, 3, 1);
		}else {
			// 有效期大于当前时间的都是预售
			valid_thru = DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss);
		}
		paramsMap.put("valid_thru_start", valid_thru);
		// 有效期大于当前时间的都是预售
		// paramsMap.put("valid_thru_end", DateUtil.addDate(valid_thru,
		// DateUtil.fmt_yyyyMMddHHmmss, 3, 1));
		List<PsGoods> psGoodsList = psGoodsDao.selectfreeGetGoodsPreSaleForH5(paramsMap);
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (PsGoods psGoods : psGoodsList) {
			Map<String, Object> psGoodsMap = new HashMap<String, Object>();
			psGoodsMap.put("goods_id", psGoods.getGoods_id());
			psGoodsMap.put("title", psGoods.getTitle());
			psGoodsMap.put("market_price", psGoods.getMarket_price() + "");
			psGoodsMap.put("discount_price", psGoods.getDiscount_price() + "");
			psGoodsMap.put("sale_qty", psGoods.getSale_qty() + "");
			psGoodsMap.put("valid_thru", StringUtil.formatStr(psGoods.getValid_thru()));
			psGoodsMap.put("taobao_price", StringUtil.formatStr(psGoods.getTaobao_price()));
			psGoodsMap.put("taobao_link", StringUtil.formatStr(psGoods.getTaobao_link()));
			psGoodsMap.put("taobao_sales", StringUtil.formatStr(psGoods.getTaobao_sales()));
			psGoodsMap.put("data_source", StringUtil.formatStr(psGoods.getData_source()));
			// 解析图片
			String pic_info = StringUtil.formatStr(psGoods.getPic_info());
			if (StringUtil.isNotEmpty(pic_info)) {
				List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_info);
				for (Map<String, Object> m : tempList) {
					if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
						String path_id = m.get("path_id") + "";
						String pic_info_url = docUtil.imageUrlFind(path_id);
						psGoodsMap.put("pic_info_url", pic_info_url);
						break;
					}
				}
			}
			resultList.add(psGoodsMap);
		}
		Map<String, Object> resultDataMap = new HashMap<String, Object>();
		resultDataMap.put("list", resultList);
		result.setResultData(resultDataMap);
		// 设置缓存
		redisUtil.setObj(key, resultDataMap, 300);
		freeGetGoodsListCacheKeySet.add(key);
	}
	
	
	
	@Override
	public void freeGetGoodsNewestListFindForStores(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		String label_promotion = StringUtil.formatStr(paramsMap.get("label_promotion"));
		// 如果有标签的话,缓存的key就要增加标签
		String key = "[freeGetGoodsNewestListFindForStores]" + label_promotion;

		Object obj = redisUtil.getObj(key);
		if (null != obj) {
			result.setResultData(obj);
			return;
		}

		// 上架时间在1天以内的
		String online_date_end = DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss);
		String online_date_start = DateUtil.addDate(online_date_end, DateUtil.fmt_yyyyMMddHHmmss, 3, -1);
		paramsMap.put("online_date_start", online_date_start);
		String valid_thru = DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss);
		paramsMap.put("valid_thru_end", valid_thru);

		List<PsGoods> psGoodsList = psGoodsDao.selectFgGoodsListByNewestForStores(paramsMap);
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (PsGoods psGoods : psGoodsList) {
			Map<String, Object> psGoodsMap = new HashMap<String, Object>();
			psGoodsMap.put("goods_id", psGoods.getGoods_id());
			psGoodsMap.put("title", psGoods.getTitle());
			psGoodsMap.put("market_price", psGoods.getMarket_price() + "");
			psGoodsMap.put("discount_price", psGoods.getDiscount_price() + "");
			psGoodsMap.put("sale_qty", psGoods.getSale_qty() + "");
			psGoodsMap.put("stock_qty", psGoods.getStock_qty() + "");
			psGoodsMap.put("taobao_price", StringUtil.formatStr(psGoods.getTaobao_price()));
			psGoodsMap.put("taobao_link", StringUtil.formatStr(psGoods.getTaobao_link()));
			psGoodsMap.put("taobao_sales", StringUtil.formatStr(psGoods.getTaobao_sales()));
			psGoodsMap.put("data_source", StringUtil.formatStr(psGoods.getData_source()));
			// 解析图片
			String pic_info = StringUtil.formatStr(psGoods.getPic_info());
			if (StringUtil.isNotEmpty(pic_info)) {
				List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_info);
				for (Map<String, Object> m : tempList) {
					if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
						String path_id = m.get("path_id") + "";
						String pic_info_url = docUtil.imageUrlFind(path_id);
						psGoodsMap.put("pic_info_url", pic_info_url);
						break;
					}
				}
			}
			resultList.add(psGoodsMap);
		}
		Map<String, Object> resultDataMap = new HashMap<String, Object>();
		resultDataMap.put("list", resultList);
		result.setResultData(resultDataMap);
		// 设置缓存
		redisUtil.setObj(key, resultDataMap, 300);
		freeGetGoodsListCacheKeySet.add(key);
	}

	@Override
	public void freeGetGoodsNewestListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		String label_promotion = StringUtil.formatStr(paramsMap.get("label_promotion"));
		// 如果有标签的话,缓存的key就要增加标签
		String key = "[freeGetGoodsNewestListFind]" + label_promotion;

		Object obj = redisUtil.getObj(key);
		if (null != obj) {
			result.setResultData(obj);
			return;
		}

		// 上架时间在1天以内的
		String online_date_end = DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss);
		String online_date_start = DateUtil.addDate(online_date_end, DateUtil.fmt_yyyyMMddHHmmss, 3, -1);
		paramsMap.put("online_date_start", online_date_start);
		String valid_thru = DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss);
		paramsMap.put("valid_thru_end", valid_thru);

		List<PsGoods> psGoodsList = psGoodsDao.selectFgGoodsListByNewest(paramsMap);
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (PsGoods psGoods : psGoodsList) {
			Map<String, Object> psGoodsMap = new HashMap<String, Object>();
			psGoodsMap.put("goods_id", psGoods.getGoods_id());
			psGoodsMap.put("title", psGoods.getTitle());
			psGoodsMap.put("market_price", psGoods.getMarket_price() + "");
			psGoodsMap.put("discount_price", psGoods.getDiscount_price() + "");
			psGoodsMap.put("sale_qty", psGoods.getSale_qty() + "");
			psGoodsMap.put("stock_qty", psGoods.getStock_qty() + "");
			psGoodsMap.put("taobao_price", StringUtil.formatStr(psGoods.getTaobao_price()));
			psGoodsMap.put("taobao_link", StringUtil.formatStr(psGoods.getTaobao_link()));
			psGoodsMap.put("taobao_sales", StringUtil.formatStr(psGoods.getTaobao_sales()));
			psGoodsMap.put("data_source", StringUtil.formatStr(psGoods.getData_source()));
			// 解析图片
			String pic_info = StringUtil.formatStr(psGoods.getPic_info());
			if (StringUtil.isNotEmpty(pic_info)) {
				List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_info);
				for (Map<String, Object> m : tempList) {
					if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
						String path_id = m.get("path_id") + "";
						String pic_info_url = docUtil.imageUrlFind(path_id);
						psGoodsMap.put("pic_info_url", pic_info_url);
						break;
					}
				}
			}
			resultList.add(psGoodsMap);
		}
		Map<String, Object> resultDataMap = new HashMap<String, Object>();
		resultDataMap.put("list", resultList);
		result.setResultData(resultDataMap);
		// 设置缓存
		redisUtil.setObj(key, resultDataMap, 300);
		freeGetGoodsListCacheKeySet.add(key);
	}
	
	
	@Override
	public void freeGetGoodsByLabelListFindForSmallProgram(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "label_promotion" });
		String label_promotion = paramsMap.get("label_promotion") + "";
		String key = "[freeGetGoodsByLabelListFindForSmallProgram]" + label_promotion;
		Object obj = redisUtil.getObj(key);
		if (null != obj) {
			result.setResultData(obj);
			return;
		}
		// 查询可以购买的商品
		paramsMap.put("valid_thru_end", DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss));
		List<PsGoods> psGoodsList = psGoodsDao.selectfreeGetGoodsListByLabelForSmallProgram(paramsMap);
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (PsGoods psGoods : psGoodsList) {
			Map<String, Object> psGoodsMap = new HashMap<String, Object>();
			psGoodsMap.put("goods_id", psGoods.getGoods_id());
			psGoodsMap.put("title", psGoods.getTitle());
			psGoodsMap.put("market_price", psGoods.getMarket_price() + "");
			psGoodsMap.put("discount_price", psGoods.getDiscount_price() + "");
			psGoodsMap.put("sale_qty", psGoods.getSale_qty() + "");
			psGoodsMap.put("stock_qty", psGoods.getStock_qty() + "");
			psGoodsMap.put("taobao_price", StringUtil.formatStr(psGoods.getTaobao_price()));
			psGoodsMap.put("online_date", DateUtil.date2Str(psGoods.getOnline_date(), DateUtil.fmt_yyyyMMddHHmmss));
			psGoodsMap.put("valid_thru", StringUtil.formatStr(psGoods.getValid_thru()));
			psGoodsMap.put("taobao_link", StringUtil.formatStr(psGoods.getTaobao_link()));
			psGoodsMap.put("taobao_sales", StringUtil.formatStr(psGoods.getTaobao_sales()));
			psGoodsMap.put("data_source", StringUtil.formatStr(psGoods.getData_source()));
		
			// 解析图片
			String pic_info = StringUtil.formatStr(psGoods.getPic_info());
			if (StringUtil.isNotEmpty(pic_info)) {
				List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_info);
				for (Map<String, Object> m : tempList) {
					if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
						String path_id = m.get("path_id") + "";
						String pic_info_url = docUtil.imageUrlFind(path_id);
						psGoodsMap.put("pic_info_url", pic_info_url);
						break;
					}
				}
			}
			resultList.add(psGoodsMap);
		}
		Map<String, Object> resultDataMap = new HashMap<String, Object>();
		resultDataMap.put("list", resultList);
		result.setResultData(resultDataMap);
		// 设置缓存
		redisUtil.setObj(key, resultDataMap, 10);
		freeGetGoodsListCacheKeySet.add(key);
	}
	
	
	
	@Override
	public void freeGetGoodsByLabelListFindForOwn(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "label_promotion" });
		String label_promotion = paramsMap.get("label_promotion") + "";
		String key = "[freeGetGoodsByLabelListFindForOwn]" + label_promotion;
		Object obj = redisUtil.getObj(key);
		if (null != obj) {
			result.setResultData(obj);
			return;
		}
		// 查询可以购买的商品
		paramsMap.put("valid_thru_end", DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss));
		List<PsGoods> psGoodsList = psGoodsDao.selectfreeGetGoodsListByLabelForOwn(paramsMap);
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (PsGoods psGoods : psGoodsList) {
			Map<String, Object> psGoodsMap = new HashMap<String, Object>();
			psGoodsMap.put("goods_id", psGoods.getGoods_id());
			psGoodsMap.put("title", psGoods.getTitle());
			psGoodsMap.put("market_price", psGoods.getMarket_price() + "");
			psGoodsMap.put("discount_price", psGoods.getDiscount_price() + "");
			psGoodsMap.put("sale_qty", psGoods.getSale_qty() + "");
			psGoodsMap.put("stock_qty", psGoods.getStock_qty() + "");
			psGoodsMap.put("taobao_price", StringUtil.formatStr(psGoods.getTaobao_price()));
			psGoodsMap.put("online_date", DateUtil.date2Str(psGoods.getOnline_date(), DateUtil.fmt_yyyyMMddHHmmss));
			psGoodsMap.put("valid_thru", StringUtil.formatStr(psGoods.getValid_thru()));
			psGoodsMap.put("taobao_link", StringUtil.formatStr(psGoods.getTaobao_link()));
			psGoodsMap.put("taobao_sales", StringUtil.formatStr(psGoods.getTaobao_sales()));
			psGoodsMap.put("data_source", StringUtil.formatStr(psGoods.getData_source()));
			
			// 解析图片
			String pic_info = StringUtil.formatStr(psGoods.getPic_info());
			if (StringUtil.isNotEmpty(pic_info)) {
				List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_info);
				for (Map<String, Object> m : tempList) {
					if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
						String path_id = m.get("path_id") + "";
						String pic_info_url = docUtil.imageUrlFind(path_id);
						psGoodsMap.put("pic_info_url", pic_info_url);
						break;
					}
				}
			}
			resultList.add(psGoodsMap);
		}
		Map<String, Object> resultDataMap = new HashMap<String, Object>();
		resultDataMap.put("list", resultList);
		result.setResultData(resultDataMap);
		// 设置缓存
		redisUtil.setObj(key, resultDataMap, 10);
		freeGetGoodsListCacheKeySet.add(key);
		
	}
	
	@Override
	public void freeGetGoodsByLabelListFindForHuiguo(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "label_promotion" });
		String label_promotion = paramsMap.get("label_promotion") + "";
		String key = "[freeGetGoodsByLabelListFindForHuiguo]" + label_promotion;
		Object obj = redisUtil.getObj(key);
		if (null != obj) {
			result.setResultData(obj);
			return;
		}
		// 查询可以购买的商品
		paramsMap.put("valid_thru_end", DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss));
		List<PsGoods> psGoodsList = psGoodsDao.selectfreeGetGoodsListByLabelForHuiguo(paramsMap);
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (PsGoods psGoods : psGoodsList) {
			Map<String, Object> psGoodsMap = new HashMap<String, Object>();
			psGoodsMap.put("goods_id", psGoods.getGoods_id());
			psGoodsMap.put("title", psGoods.getTitle());
			psGoodsMap.put("market_price", psGoods.getMarket_price() + "");
			psGoodsMap.put("discount_price", psGoods.getDiscount_price() + "");
			psGoodsMap.put("sale_qty", psGoods.getSale_qty() + "");
			psGoodsMap.put("stock_qty", psGoods.getStock_qty() + "");
			psGoodsMap.put("taobao_price", StringUtil.formatStr(psGoods.getTaobao_price()));
			psGoodsMap.put("online_date", DateUtil.date2Str(psGoods.getOnline_date(), DateUtil.fmt_yyyyMMddHHmmss));
			psGoodsMap.put("valid_thru", StringUtil.formatStr(psGoods.getValid_thru()));
			psGoodsMap.put("taobao_link", StringUtil.formatStr(psGoods.getTaobao_link()));
			psGoodsMap.put("taobao_sales", StringUtil.formatStr(psGoods.getTaobao_sales()));
			psGoodsMap.put("data_source", StringUtil.formatStr(psGoods.getData_source()));
			
			// 解析图片
			String pic_info = StringUtil.formatStr(psGoods.getPic_info());
			if (StringUtil.isNotEmpty(pic_info)) {
				List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_info);
				for (Map<String, Object> m : tempList) {
					if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
						String path_id = m.get("path_id") + "";
						String pic_info_url = docUtil.imageUrlFind(path_id);
						psGoodsMap.put("pic_info_url", pic_info_url);
						break;
					}
				}
			}
			resultList.add(psGoodsMap);
		}
		Map<String, Object> resultDataMap = new HashMap<String, Object>();
		resultDataMap.put("list", resultList);
		result.setResultData(resultDataMap);
		// 设置缓存
		redisUtil.setObj(key, resultDataMap, 10);
		freeGetGoodsListCacheKeySet.add(key);
		
	}
	
	
	
	@Override
	public void freeGetGoodsByLabelListFindForStores(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "label_promotion" });
		String label_promotion = paramsMap.get("label_promotion") + "";
		String key = "[freeGetGoodsByLabelListFindForStores]" + label_promotion;
		Object obj = redisUtil.getObj(key);
		if (null != obj) {
			result.setResultData(obj);
			return;
		}
		// 查询可以购买的商品
		paramsMap.put("valid_thru_end", DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss));
		List<PsGoods> psGoodsList = psGoodsDao.selectfreeGetGoodsListByLabelForStores(paramsMap);
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (PsGoods psGoods : psGoodsList) {
			Map<String, Object> psGoodsMap = new HashMap<String, Object>();
			psGoodsMap.put("goods_id", psGoods.getGoods_id());
			psGoodsMap.put("title", psGoods.getTitle());
			psGoodsMap.put("market_price", psGoods.getMarket_price() + "");
			psGoodsMap.put("discount_price", psGoods.getDiscount_price() + "");
			psGoodsMap.put("sale_qty", psGoods.getSale_qty() + "");
			psGoodsMap.put("stock_qty", psGoods.getStock_qty() + "");
			psGoodsMap.put("taobao_price", StringUtil.formatStr(psGoods.getTaobao_price()));
			psGoodsMap.put("online_date", DateUtil.date2Str(psGoods.getOnline_date(), DateUtil.fmt_yyyyMMddHHmmss));
			psGoodsMap.put("valid_thru", StringUtil.formatStr(psGoods.getValid_thru()));
			psGoodsMap.put("taobao_link", StringUtil.formatStr(psGoods.getTaobao_link()));
			psGoodsMap.put("taobao_sales", StringUtil.formatStr(psGoods.getTaobao_sales()));
			psGoodsMap.put("data_source", StringUtil.formatStr(psGoods.getData_source()));
			
			// 解析图片
			String pic_info = StringUtil.formatStr(psGoods.getPic_info());
			if (StringUtil.isNotEmpty(pic_info)) {
				List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_info);
				for (Map<String, Object> m : tempList) {
					if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
						String path_id = m.get("path_id") + "";
						String pic_info_url = docUtil.imageUrlFind(path_id);
						psGoodsMap.put("pic_info_url", pic_info_url);
						break;
					}
				}
			}
			resultList.add(psGoodsMap);
		}
		Map<String, Object> resultDataMap = new HashMap<String, Object>();
		resultDataMap.put("list", resultList);
		result.setResultData(resultDataMap);
		// 设置缓存
		redisUtil.setObj(key, resultDataMap, 10);
		freeGetGoodsListCacheKeySet.add(key);
	}
	
	@Override
	public void freeGetGoodsByLabelListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "label_promotion" });
		String label_promotion = paramsMap.get("label_promotion") + "";
		String key = null;
		if("new".equals(paramsMap.get("version"))){
			key = "[freeGetGoodsByLabelListFind_new]" + label_promotion;
		}else if ("zeroPurchase".equals(paramsMap.get("version"))) {
			//限时抢购不添加缓存 注释掉
			//key = "[freeGetGoodsByLabelListFind_zeroPurchase]" + label_promotion;
		}else{
			key = "[freeGetGoodsByLabelListFind]" + label_promotion;
		}
		if(StringUtil.isNotBlank(key)){
			Object obj = redisUtil.getObj(key);
			if (null != obj) {
				result.setResultData(obj);
				return;
			}
		}
		// 查询可以购买的商品
		paramsMap.put("valid_thru_end", DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss));
		List<PsGoods> psGoodsList =null;
		if("new".equals(paramsMap.get("version"))){
			psGoodsList= psGoodsDao.selectfreeGetGoodsListByLabelTwo(paramsMap);
		}else if ("zeroPurchase".equals(paramsMap.get("version"))) {
			//此段代码是为了查找出超出限时抢购时间,但是还没有卖完的商品,再进行销售.
			List<Integer> timeList = null;
			if("10".equals(paramsMap.get("time"))){//10点开卖
				timeList = new ArrayList<>();
				timeList.add(0);
				timeList.add(1);
				timeList.add(2);
				timeList.add(3);
				timeList.add(4);
				timeList.add(5);
				timeList.add(6);
				timeList.add(7);
				timeList.add(8);
				timeList.add(9);
				timeList.add(10);
			}else if ("12".equals(paramsMap.get("time"))) {//12点开卖
				timeList = new ArrayList<>();
				timeList.add(11);
				timeList.add(12);
			}else if("16".equals(paramsMap.get("time"))){//16点开卖
				timeList = new ArrayList<>();
				timeList.add(13);
				timeList.add(14);
				timeList.add(15);
				timeList.add(16);
			}else if("20".equals(paramsMap.get("time"))){//20点开卖
				timeList = new ArrayList<>();
				timeList.add(17);
				timeList.add(18);
				timeList.add(19);
				timeList.add(20);
			}
			if(null != timeList){
				paramsMap.put("timeList", timeList);
				paramsMap.put("valid_thru_end", DateUtil.addDate(DateUtil.getFormatDate(DateUtil.fmt_yyyyMMdd), DateUtil.fmt_yyyyMMdd, 3, 1));
			}
			psGoodsList= psGoodsDao.selectfreeGetGoodsListByLabelTwo(paramsMap);
		}else{
			psGoodsList = psGoodsDao.selectfreeGetGoodsListByLabel(paramsMap);
		}
		
		
		
		
		
		if(psGoodsList==null||psGoodsList.size()==0){
			throw new BusinessException(CommonRspCode.SYSTEM_PARAM_MISS, "查无结果");
		}
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (PsGoods psGoods : psGoodsList) {
			Map<String, Object> psGoodsMap = new HashMap<String, Object>();
			psGoodsMap.put("goods_id", psGoods.getGoods_id());
			psGoodsMap.put("title", psGoods.getTitle());
			psGoodsMap.put("market_price", psGoods.getMarket_price() + "");
			psGoodsMap.put("discount_price", psGoods.getDiscount_price() + "");
			psGoodsMap.put("sale_qty", psGoods.getSale_qty() + "");
			psGoodsMap.put("stock_qty", psGoods.getStock_qty() + "");
			psGoodsMap.put("taobao_price", StringUtil.formatStr(psGoods.getTaobao_price()));
			psGoodsMap.put("online_date", DateUtil.date2Str(psGoods.getOnline_date(), DateUtil.fmt_yyyyMMddHHmmss));
			psGoodsMap.put("valid_thru", StringUtil.formatStr(psGoods.getValid_thru()));
			psGoodsMap.put("taobao_link", StringUtil.formatStr(psGoods.getTaobao_link()));
			psGoodsMap.put("taobao_sales", StringUtil.formatStr(psGoods.getTaobao_sales()));
			psGoodsMap.put("data_source", StringUtil.formatStr(psGoods.getData_source()));
			psGoodsMap.put("system_date", DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss));
			// 解析图片
			String pic_info = StringUtil.formatStr(psGoods.getPic_info());
			if (StringUtil.isNotEmpty(pic_info)) {
				List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_info);
				for (Map<String, Object> m : tempList) {
					if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
						String path_id = m.get("path_id") + "";
						String pic_info_url = docUtil.imageUrlFind(path_id);
						psGoodsMap.put("pic_info_url", pic_info_url);
						break;
					}
				}
			}
			resultList.add(psGoodsMap);
		}
		Map<String, Object> resultDataMap = new HashMap<String, Object>();
		resultDataMap.put("list", resultList);
		result.setResultData(resultDataMap);
		// 设置缓存
		if(StringUtil.isNotBlank(key)){
			redisUtil.setObj(key, resultDataMap, 10);
			freeGetGoodsListCacheKeySet.add(key);
		}
	}
	
	@Override
	public void gdFreeGetGoodsByLabelListPageFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "label_promotion" });
		String label_promotion = paramsMap.get("label_promotion") + "";
		String key = null;
		if("new".equals(paramsMap.get("version"))){
			key = "[gdFreeGetGoodsByLabelListPageFind_new]" + label_promotion;
		}else{
			key = "[gdFreeGetGoodsByLabelListPageFind]" + label_promotion;
		}
		
		Object obj = redisUtil.getObj(key);
		if (null != obj) {
			result.setResultData(obj);
			return;
		}
		// 查询可以购买的商品
		paramsMap.put("valid_thru_end", DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss));
		List<PsGoods> psGoodsList =null;
		if("new".equals(paramsMap.get("version"))){
			psGoodsList= psGoodsDao.selectGdFreeGetGoodsListByLabelTwo(paramsMap);
		}else{
			psGoodsList = psGoodsDao.selectGdFreeGetGoodsListByLabel(paramsMap);
		}
		if(psGoodsList==null||psGoodsList.size()==0){
			throw new BusinessException(CommonRspCode.SYSTEM_PARAM_MISS, "查无结果");
		}
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (PsGoods psGoods : psGoodsList) {
			Map<String, Object> psGoodsMap = new HashMap<String, Object>();
			psGoodsMap.put("goods_id", psGoods.getGoods_id());
			psGoodsMap.put("title", psGoods.getTitle());
			psGoodsMap.put("market_price", psGoods.getMarket_price() + "");
			psGoodsMap.put("discount_price", psGoods.getDiscount_price() + "");
			psGoodsMap.put("sale_qty", psGoods.getSale_qty() + "");
			psGoodsMap.put("stock_qty", psGoods.getStock_qty() + "");
			psGoodsMap.put("taobao_price", StringUtil.formatStr(psGoods.getTaobao_price()));
			psGoodsMap.put("online_date", DateUtil.date2Str(psGoods.getOnline_date(), DateUtil.fmt_yyyyMMddHHmmss));
			psGoodsMap.put("valid_thru", StringUtil.formatStr(psGoods.getValid_thru()));
			psGoodsMap.put("taobao_link", StringUtil.formatStr(psGoods.getTaobao_link()));
			psGoodsMap.put("taobao_sales", StringUtil.formatStr(psGoods.getTaobao_sales()));
			psGoodsMap.put("data_source", StringUtil.formatStr(psGoods.getData_source()));
			
			// 解析图片
			String pic_info = StringUtil.formatStr(psGoods.getPic_info());
			if (StringUtil.isNotEmpty(pic_info)) {
				List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_info);
				for (Map<String, Object> m : tempList) {
					if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
						String path_id = m.get("path_id") + "";
						String pic_info_url = docUtil.imageUrlFind(path_id);
						psGoodsMap.put("pic_info_url", pic_info_url);
						break;
					}
				}
			}
			resultList.add(psGoodsMap);
		}
		Map<String, Object> resultDataMap = new HashMap<String, Object>();
		resultDataMap.put("list", resultList);
		result.setResultData(resultDataMap);
		// 设置缓存
		redisUtil.setObj(key, resultDataMap, 10);
		freeGetGoodsListCacheKeySet.add(key);
	}

	@Override
	public void goldExchangeGoodsDetailFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "goods_id" });
		List<PsGoods> psGoodsList = psGoodsDao.selectPsGoods(paramsMap);
		List<String> doc_ids = new ArrayList<String>();
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		Map<String, Object> queryMap = new HashMap<String, Object>();
		for (PsGoods psGoods : psGoodsList) {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("goods_id", psGoods.getGoods_id());
			tempMap.put("goods_code", StringUtil.formatStr(psGoods.getGoods_code()));
			tempMap.put("title", psGoods.getTitle());
			tempMap.put("desc1", StringUtil.formatStr(psGoods.getDesc1()));
			tempMap.put("area_id", psGoods.getArea_id());
			tempMap.put("label", StringUtil.formatStr(psGoods.getLabel()));
			tempMap.put("category", psGoods.getCategory());
			tempMap.put("display_area", psGoods.getDisplay_area());
			tempMap.put("contact_person", psGoods.getContact_person());
			tempMap.put("contact_tel", psGoods.getContact_tel());
			tempMap.put("specification", StringUtil.formatStr(psGoods.getSpecification()));
			tempMap.put("pack", StringUtil.formatStr(psGoods.getPack()));
			tempMap.put("market_price", psGoods.getMarket_price() + "");
			tempMap.put("discount_price", psGoods.getDiscount_price() + "");
			tempMap.put("product_source", StringUtil.formatStr(psGoods.getProduct_source()));
			tempMap.put("shipping_fee", psGoods.getShipping_fee() + "");
			tempMap.put("producer", StringUtil.formatStr(psGoods.getProducer()));
			tempMap.put("supplier", StringUtil.formatStr(psGoods.getSupplier()));
			tempMap.put("manufacturer", StringUtil.formatStr(psGoods.getManufacturer()));
			tempMap.put("min_buy_qty", psGoods.getMin_buy_qty() + "");
			tempMap.put("max_buy_qty", psGoods.getMax_buy_qty() + "");
			tempMap.put("goods_unit", StringUtil.formatStr(psGoods.getGoods_unit()));
			tempMap.put("payment_way", StringUtil.formatStr(psGoods.getPayment_way()));
			tempMap.put("pic_info", StringUtil.formatStr(psGoods.getPic_info()));
			tempMap.put("pic_detail_info", StringUtil.formatStr(psGoods.getPic_detail_info()));
			tempMap.put("sale_qty", psGoods.getSale_qty() + "");
			tempMap.put("stock_qty", psGoods.getStock_qty() + "");
			String deliver_date = DateUtil.getDistanceDays(psGoods.getValid_thru(),
					DateUtil.getFormatDate(DateUtil.fmt_yyyyMMdd));
			tempMap.put("deliver_date", deliver_date);
			// 解析区域
			String[] deliveryAreaStr = psGoods.getDelivery_area().split(",");
			queryMap.clear();
			queryMap.put("areaStr", deliveryAreaStr);
			List<Map<String, Object>> deliveryAreaList = goodsDao.selectMDArea(queryMap);
			String delivery_desc = "";
			for (Map<String, Object> area : deliveryAreaList) {
				delivery_desc = delivery_desc + "|" + area.get("path");
			}
			if (delivery_desc.trim().length() > 0) {
				delivery_desc = delivery_desc.substring(1);
			}
			tempMap.put("delivery_area", StringUtil.formatStr(psGoods.getDelivery_area()));
			tempMap.put("delivery_desc", delivery_desc);
			resultList.add(tempMap);
			// 解析图片
			String pic_info = StringUtil.formatStr(psGoods.getPic_info());
			if (!StringUtils.isEmpty(pic_info)) {
				List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_info);
				for (Map<String, Object> m : tempList) {
					if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
						doc_ids.add(m.get("path_id") + "");
					}
				}
			}
			// 解析详情图片
			String pic_detail_info = StringUtil.formatStr(psGoods.getPic_detail_info());
			if (!StringUtils.isEmpty(pic_detail_info)) {
				List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_detail_info);
				for (Map<String, Object> m : tempList) {
					if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
						doc_ids.add(m.get("path_id") + "");
					}
				}
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("doc_url", docUtil.imageUrlFind(doc_ids));
		map.put("list", resultList);
		result.setResultData(map);
	}

	@Override
	public void goldExchangeGoodsFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "area_id" });
		List<String> areaList = new ArrayList<String>();
		areaList.add((String) paramsMap.get("area_id"));
		areaList.add("100000");
		Map<String, Object> queryMap = new HashMap<String, Object>();
		queryMap.put("area_code", paramsMap.get("area_id"));
		Map<String, Object> areaMap = goodsDao.selectAreaCodeTree(queryMap);
		if (areaMap != null) {
			if (null != areaMap.get("s_code") && !"".equals(areaMap.get("s_code"))) {
				areaList.add((String) areaMap.get("s_code"));
			}
			if (null != areaMap.get("t_code") && !"".equals(areaMap.get("t_code"))) {
				areaList.add((String) areaMap.get("t_code"));
			}
		}
		paramsMap.put("areaList", areaList);
		paramsMap.put("status", Constant.STATUS_ON_SHELF);
		paramsMap.put("category", "兑换");
		List<PsGoods> psGoodsList = psGoodsDao.selectPsGoods(paramsMap);
		List<String> doc_ids = new ArrayList<String>();
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (PsGoods psGoods : psGoodsList) {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("goods_id", psGoods.getGoods_id());
			tempMap.put("goods_code", StringUtil.formatStr(psGoods.getGoods_code()));
			tempMap.put("title", psGoods.getTitle());
			tempMap.put("desc1", StringUtil.formatStr(psGoods.getDesc1()));
			tempMap.put("area_id", psGoods.getArea_id());
			tempMap.put("label", StringUtil.formatStr(psGoods.getLabel()));
			tempMap.put("category", psGoods.getCategory());
			tempMap.put("display_area", psGoods.getDisplay_area());
			tempMap.put("contact_person", psGoods.getContact_person());
			tempMap.put("contact_tel", psGoods.getContact_tel());
			tempMap.put("specification", StringUtil.formatStr(psGoods.getSpecification()));
			tempMap.put("pack", StringUtil.formatStr(psGoods.getPack()));
			tempMap.put("market_price", psGoods.getMarket_price() + "");
			tempMap.put("discount_price", psGoods.getDiscount_price() + "");
			tempMap.put("product_source", StringUtil.formatStr(psGoods.getProduct_source()));
			tempMap.put("shipping_fee", psGoods.getShipping_fee() + "");
			tempMap.put("producer", StringUtil.formatStr(psGoods.getProducer()));
			tempMap.put("supplier", StringUtil.formatStr(psGoods.getSupplier()));
			tempMap.put("manufacturer", StringUtil.formatStr(psGoods.getManufacturer()));
			tempMap.put("min_buy_qty", psGoods.getMin_buy_qty() + "");
			tempMap.put("max_buy_qty", psGoods.getMax_buy_qty() + "");
			tempMap.put("goods_unit", StringUtil.formatStr(psGoods.getGoods_unit()));
			tempMap.put("payment_way", StringUtil.formatStr(psGoods.getPayment_way()));
			tempMap.put("pic_info", StringUtil.formatStr(psGoods.getPic_info()));
			tempMap.put("pic_detail_info", StringUtil.formatStr(psGoods.getPic_detail_info()));
			tempMap.put("sale_qty", psGoods.getSale_qty() + "");
			tempMap.put("stock_qty", psGoods.getStock_qty() + "");
			String deliver_date = DateUtil.getDistanceDays(psGoods.getValid_thru(),
					DateUtil.getFormatDate(DateUtil.fmt_yyyyMMdd));
			tempMap.put("deliver_date", deliver_date);
			// 解析区域
			String[] deliveryAreaStr = psGoods.getDelivery_area().split(",");
			queryMap.clear();
			queryMap.put("areaStr", deliveryAreaStr);
			List<Map<String, Object>> deliveryAreaList = goodsDao.selectMDArea(queryMap);
			String delivery_desc = "";
			for (Map<String, Object> area : deliveryAreaList) {
				delivery_desc = delivery_desc + "|" + area.get("path");
			}
			if (delivery_desc.trim().length() > 0) {
				delivery_desc = delivery_desc.substring(1);
			}
			tempMap.put("delivery_area", StringUtil.formatStr(psGoods.getDelivery_area()));
			tempMap.put("delivery_desc", delivery_desc);
			resultList.add(tempMap);
			// 解析图片
			String pic_info = StringUtil.formatStr(psGoods.getPic_info());
			if (!pic_info.equals("")) {
				List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_info);
				for (Map<String, Object> m : tempList) {
					if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
						doc_ids.add(m.get("path_id") + "");
					}
				}
			}
			// 解析详情图片
			String pic_detail_info = StringUtil.formatStr(psGoods.getPic_detail_info());
			if (!pic_detail_info.equals("")) {
				List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_detail_info);
				for (Map<String, Object> m : tempList) {
					if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
						doc_ids.add(m.get("path_id") + "");
					}
				}
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("doc_url", docUtil.imageUrlFind(doc_ids));
		map.put("list", resultList);
		result.setResultData(map);
	}

	@Override
	public void psGoodsSkuForOpFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "goods_id" });
		List<PsGoodsSku> psGoodsSkuList = psGoodsDao.selectPsGoodsSku(paramsMap);
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (PsGoodsSku psGoodsSku : psGoodsSkuList) {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("goods_stock_id", psGoodsSku.getGoods_stock_id());
			tempMap.put("sku", StringUtil.formatStr(psGoodsSku.getSku()));
			tempMap.put("desc1", StringUtil.formatStr(psGoodsSku.getDesc1()));
			tempMap.put("sales_price", psGoodsSku.getSales_price() + "");
			tempMap.put("sale_qty", psGoodsSku.getSale_qty() + "");
			tempMap.put("stock_qty", psGoodsSku.getStock_qty() + "");
			resultList.add(tempMap);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", resultList);
		result.setResultData(map);
	}

	@Override
	public void psGoodsSkuForStoresFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "goods_id" });
		List<PsGoodsSku> psGoodsSkuList = psGoodsDao.selectPsGoodsSku(paramsMap);
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (PsGoodsSku psGoodsSku : psGoodsSkuList) {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("goods_stock_id", psGoodsSku.getGoods_stock_id());
			tempMap.put("sku", StringUtil.formatStr(psGoodsSku.getSku()));
			tempMap.put("desc1", StringUtil.formatStr(psGoodsSku.getDesc1()));
			tempMap.put("sales_price", psGoodsSku.getSales_price() + "");
			tempMap.put("sale_qty", psGoodsSku.getSale_qty() + "");
			resultList.add(tempMap);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", resultList);
		result.setResultData(map);
	}

	@Override
	public void psGoodsDetailFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		if (paramsMap.size() == 0) {
			throw new BusinessException(RspCode.SYSTEM_PARAM_MISS, "参数缺失");
		}
		List<String> doc_ids = new ArrayList<String>();
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> map = new HashMap<String, Object>();
		// Map<String, Object> imgMap = new HashMap<String, Object>();
		// String data_source = "";
		String goods_code="";
		int count=0;
		//红包商品
		if(paramsMap.get("goods_type") !=null && 2 == Integer.parseInt(paramsMap.get("goods_type").toString())){
			//psGoodsList = psGoodsDao.selectFreePsGoods(paramsMap);  -- 查询免费商品 - 原逻辑
			//目前逻辑 - 获取红包商品
			List<HongBaoGoods> hongBaoGoodsList = hongbaoGoodsDao.selectHongbaoGoodsList(paramsMap); //由于原逻辑完全变了，就这样吧
			for (HongBaoGoods hongBaoGoods : hongBaoGoodsList) {
				Map<String, Object> tempMap = BeanUtil.goodsToMap(hongBaoGoods);

				if(count==0){
					goods_code=StringUtil.formatStr(hongBaoGoods.getGoods_code());
				}
				count++;
				// data_source = beikeGoods.getData_source();
				// tempMap.put("good_level", beikeGoods.getGood_level() + "");
				// tempMap.put("good_points", beikeGoods.getGood_point() + "");
				// 解析区域
				if(StringUtils.isNotEmpty(hongBaoGoods.getDelivery_area())){
					String[] areaStr = hongBaoGoods.getDelivery_area().split(",");
					params.clear();
					params.put("areaStr", areaStr);
					List<Map<String, Object>> areaList = goodsDao.selectMDArea(params);
					String delivery_desc = "";
					for (Map<String, Object> area : areaList) {
						delivery_desc = delivery_desc + "|" + area.get("path");
					}
					if (delivery_desc.trim().length() > 0) {
						delivery_desc = delivery_desc.substring(1);
					}
					tempMap.put("delivery_area", StringUtil.formatStr(hongBaoGoods.getDelivery_area()));
					tempMap.put("delivery_desc", delivery_desc);
				}
				resultList.add(tempMap);
				// 解析图片
				// List<String> strList = new ArrayList<String>();
				String pic_info = StringUtil.formatStr(hongBaoGoods.getPic_info());
				try {
					if (!pic_info.equals("")) {
						List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_info);
						for (Map<String, Object> m : tempList) {
							// if(!StringUtil.isEmpty(beikeGoods.getData_source()) &&
							// beikeGoods.getData_source().equals("hsrj")){
							// strList.add(
							// StringUtil.formatStr(m.get("path_id"))+"");
							// }else{
							if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
								doc_ids.add(m.get("path_id") + "");
							}
							// }
						}
					}
					// tempMap.put("pic_info_url",strList);
					// 解析详情图片
					String pic_detail_info = StringUtil.formatStr(hongBaoGoods.getPic_detail_info());
					if (!pic_detail_info.equals("")) {
						List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_detail_info);
						for (Map<String, Object> m : tempList) {
							// if(!StringUtil.isEmpty(beikeGoods.getData_source()) &&
							// beikeGoods.getData_source().equals("hsrj")){
							// strList.add(
							// StringUtil.formatStr(m.get("path_id"))+"");
							// }else{
							if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
								doc_ids.add(m.get("path_id") + "");
							}
							// }
						}
					}
				} catch (Exception e) {
					map.put("doc_url", "");
					e.printStackTrace();
				}
				// imgMap.put("img_url",strList);
			}
		}else{//贝克商品
			List<BeikeMallGoods> beikeGoodsList = psGoodsDao.selectBeiKeGoods(paramsMap);
			for (BeikeMallGoods beikeGoods : beikeGoodsList) {
				Map<String, Object> tempMap = BeanUtil.goodsToMap(beikeGoods);

				if(count==0){
					goods_code=StringUtil.formatStr(beikeGoods.getGoods_code());
				}
				count++;
				// data_source = beikeGoods.getData_source();
				// tempMap.put("good_level", beikeGoods.getGood_level() + "");
				// tempMap.put("good_points", beikeGoods.getGood_point() + "");
				// 解析区域
				if(StringUtils.isNotEmpty(beikeGoods.getDelivery_area())){
					String[] areaStr = beikeGoods.getDelivery_area().split(",");
					params.clear();
					params.put("areaStr", areaStr);
					List<Map<String, Object>> areaList = goodsDao.selectMDArea(params);
					String delivery_desc = "";
					for (Map<String, Object> area : areaList) {
						delivery_desc = delivery_desc + "|" + area.get("path");
					}
					if (delivery_desc.trim().length() > 0) {
						delivery_desc = delivery_desc.substring(1);
					}
					tempMap.put("delivery_area", StringUtil.formatStr(beikeGoods.getDelivery_area()));
					tempMap.put("delivery_desc", delivery_desc);
				}
				resultList.add(tempMap);
				// 解析图片
				// List<String> strList = new ArrayList<String>();
				String pic_info = StringUtil.formatStr(beikeGoods.getPic_info());
				try {
					if (!pic_info.equals("")) {
						List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_info);
						for (Map<String, Object> m : tempList) {
							// if(!StringUtil.isEmpty(beikeGoods.getData_source()) &&
							// beikeGoods.getData_source().equals("hsrj")){
							// strList.add(
							// StringUtil.formatStr(m.get("path_id"))+"");
							// }else{
							if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
								doc_ids.add(m.get("path_id") + "");
							}
							// }
						}
					}
					// tempMap.put("pic_info_url",strList);
					// 解析详情图片
					String pic_detail_info = StringUtil.formatStr(beikeGoods.getPic_detail_info());
					if (!pic_detail_info.equals("")) {
						List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_detail_info);
						for (Map<String, Object> m : tempList) {
							// if(!StringUtil.isEmpty(beikeGoods.getData_source()) &&
							// beikeGoods.getData_source().equals("hsrj")){
							// strList.add(
							// StringUtil.formatStr(m.get("path_id"))+"");
							// }else{
							if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
								doc_ids.add(m.get("path_id") + "");
							}
							// }
						}
					}
				} catch (Exception e) {
					map.put("doc_url", "");
					e.printStackTrace();
				}
				// imgMap.put("img_url",strList);
			}
		}

		// if(!StringUtil.isEmpty(data_source) && data_source.equals("hsrj")){
		// map.put("doc_url",imgMap);
		// }else{
		map.put("doc_url", docUtil.imageUrlFind(doc_ids));
		// }
		map.put("list", resultList);



		if(StringUtil.isNotEmpty(goods_code)){
			Map<String, Object> skumap=new HashMap<String, Object>();
			skumap.put("goods_code", goods_code);
			List<GdGoodsSkuid>  list=gdGoodsSkuidDao.selectGdGoodsSkuidList(skumap);
			if(list==null){
				list=new ArrayList<GdGoodsSkuid>();
			}
			map.put("goodsSkuList", list);
		}

		result.setResultData(map);
	}



	@Override
	public void psGoodsSupplyCount(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "supplier_id" });
		Map<String, Object> map = psGoodsDao.selectPsGoodsSupplyCount(paramsMap);
		Map<String, Object> countMap = new HashMap<String, Object>();
		if (null != map) {
			countMap.put("lingleme", map.get("count_num") + "");
		}
		result.setResultData(countMap);
	}

	@Override
	public void psGoodsListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		List<BeikeMallGoods> list = psGoodsDao.selectBeiKeGoodsList(paramsMap);
		List<HongBaoGoods> hongBaoGoodsList = hongbaoGoodsDao.selectHongbaoGoodsList(paramsMap);
		//psGoodlistFindResultSetup(result, list);
		if("2".equals(paramsMap.get("goods_type"))){//品牌零商品
			packageGoods(result,hongBaoGoodsList);
		}else{//自营商品
			packageGoods(result,list);
		}
	}
	
	@Override
	public void psUnionAllGoodsListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		List<PsGoods> list = psGoodsDao.selectUnionAllPsGoods(paramsMap);
		psGoodlistFindResultSetup2(result, list);
	}

	@Override
	public void selectPsGoodsAndFiterOffDay(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		List<PsGoods> list = psGoodsDao.selectPsGoodsAndFiterOffDay(paramsMap);
		psGoodlistFindResultSetup2(result, list);
	}

	//原逻辑
	private void psGoodlistFindResultSetup2(ResultData result, List<PsGoods> list) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (PsGoods psGoods : list) {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("goods_id", psGoods.getGoods_id());
			tempMap.put("goods_code", StringUtil.formatStr(psGoods.getGoods_code()));
			tempMap.put("title", psGoods.getTitle());
			tempMap.put("desc1", StringUtil.formatStr(psGoods.getDesc1()));
			tempMap.put("label", StringUtil.formatStr(psGoods.getLabel()));
			tempMap.put("label_promotion", StringUtil.formatStr(psGoods.getLabel_promotion()));
			tempMap.put("category", psGoods.getCategory());
			tempMap.put("data_source", StringUtil.formatStr(psGoods.getData_source()));
			tempMap.put("display_area", psGoods.getDisplay_area());
			tempMap.put("contact_person", psGoods.getContact_person());
			tempMap.put("contact_tel", psGoods.getContact_tel());
			tempMap.put("specification", StringUtil.formatStr(psGoods.getSpecification()));
			tempMap.put("cost_price", psGoods.getCost_price() + "");
			tempMap.put("market_price", psGoods.getMarket_price() + "");
			tempMap.put("discount_price", psGoods.getDiscount_price() + "");
			tempMap.put("settled_price", psGoods.getSettled_price() + "");
			/*tempMap.put("ts_min_num", psGoods.getTs_min_num() + "");
			tempMap.put("ts_price", psGoods.getTs_price() + "");
			tempMap.put("ts_date", psGoods.getTs_date() + "");*/
			tempMap.put("service_fee", psGoods.getService_fee() + "");
			tempMap.put("product_source", StringUtil.formatStr(psGoods.getProduct_source()));
			tempMap.put("shipping_fee", psGoods.getShipping_fee() + "");
			//tempMap.put("cost_allocation", psGoods.getCost_allocation() + "");
			tempMap.put("producer", StringUtil.formatStr(psGoods.getProducer()));
			tempMap.put("supplier", StringUtil.formatStr(psGoods.getSupplier()));
			tempMap.put("supplier_id", StringUtil.formatStr(psGoods.getSupplier_id()));
			tempMap.put("manufacturer", StringUtil.formatStr(psGoods.getManufacturer()));
			tempMap.put("min_buy_qty", psGoods.getMin_buy_qty() + "");
			tempMap.put("max_buy_qty", psGoods.getMax_buy_qty() + "");
			tempMap.put("goods_unit", StringUtil.formatStr(psGoods.getGoods_unit()));
			tempMap.put("warehouse", StringUtil.formatStr(psGoods.getWarehouse()));
			tempMap.put("delivery", StringUtil.formatStr(psGoods.getDelivery()));
			tempMap.put("valid_thru", StringUtil.formatStr(psGoods.getValid_thru()));
			tempMap.put("offline_date", DateUtil.date2Str(psGoods.getOffline_date(), DateUtil.fmt_yyyyMMddHHmmss));
			tempMap.put("payment_way", StringUtil.formatStr(psGoods.getPayment_way()));
			tempMap.put("status", psGoods.getStatus());
			tempMap.put("remark", StringUtil.formatStr(psGoods.getRemark()));
			tempMap.put("pic_info", StringUtil.formatStr(psGoods.getPic_info()));
			tempMap.put("sale_qty", psGoods.getSale_qty() + "");
			tempMap.put("stock_qty", psGoods.getStock_qty() + "");
			/*tempMap.put("data_source_type", psGoods.getData_source_type() + "");
			tempMap.put("taobao_price", StringUtil.formatStr(psGoods.getTaobao_price()));*/
			tempMap.put("created_date", DateUtil.date2Str(psGoods.getCreated_date(), DateUtil.fmt_yyyyMMddHHmmss));
			/*tempMap.put("voucher_link", StringUtil.formatStr(psGoods.getVoucher_link()));
			tempMap.put("taobao_link", StringUtil.formatStr(psGoods.getTaobao_link()));
			tempMap.put("store_name", StringUtil.formatStr(psGoods.getTaobao_link()));*/
			/*tempMap.put("nowdiffOffline_date", StringUtil.formatStr(psGoods.getNowdiffOffline_date()));*/
			/*tempMap.put("goods_type", psGoods.getGoods_type());*/
			// 解析区域
			String[] areaStr = psGoods.getDelivery_area().split(",");
			params.clear();
			params.put("areaStr", areaStr);
			List<Map<String, Object>> areaList = goodsDao.selectMDArea(params);
			String delivery_desc = "";
			for (Map<String, Object> area : areaList) {
				delivery_desc = delivery_desc + "|" + area.get("path");
			}
			if (delivery_desc.trim().length() > 0) {
				delivery_desc = delivery_desc.substring(1);
			}
			tempMap.put("delivery_area", StringUtil.formatStr(psGoods.getDelivery_area()));
			tempMap.put("delivery_desc", delivery_desc);
			resultList.add(tempMap);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", resultList);
		result.setResultData(map);
	}
	//车夫修改方法 - 转换bean
	private ResultData packageGoods(ResultData result, List listT){
		Map<String, Object> params = new HashMap<String, Object>();
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (Object o : listT){
			Map<String, Object> tempMap = BeanUtil.goodsToMap(o);
			try {
				o.getClass().getDeclaredField("delivery_area").setAccessible(true);
				Field delivery_area = o.getClass().getDeclaredField("delivery_area");
				String delivery_area_value = (String)o.getClass().getMethod("getDelivery_area", new Class[]{}).invoke(o);
				if("delivery_area".equals(delivery_area.getName())){
					String[] areaStr = delivery_area_value.split(",");
					params.clear();
					params.put("areaStr", areaStr);
					List<Map<String, Object>> areaList = goodsDao.selectMDArea(params);
					String delivery_desc = "";
					for (Map<String, Object> area : areaList) {
						delivery_desc = delivery_desc + "|" + area.get("path");
					}
					if (delivery_desc.trim().length() > 0) {
						delivery_desc = delivery_desc.substring(1);
					}
					tempMap.put("delivery_area", StringUtil.formatStr(delivery_area_value));
					tempMap.put("delivery_desc", delivery_desc);
				}
				resultList.add(tempMap);
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", resultList);
		result.setResultData(map);
		return result;
	}

	@Override
	public void selectGdGoodsItemAndFiterOffDay(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		List<GdGoodsItem> list = psGoodsDao.selectGdGoodsItemAndFiterOffDay(paramsMap);
		GdGoodItemlistFindResultSetup(result, list);
	}
	
	private void GdGoodItemlistFindResultSetup(ResultData result, List<GdGoodsItem> list) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (GdGoodsItem psGoods : list) {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("goods_id", psGoods.getGoods_id());
			tempMap.put("goods_code", StringUtil.formatStr(psGoods.getGoods_code()));
			tempMap.put("title", psGoods.getTitle());
			tempMap.put("desc1", StringUtil.formatStr(psGoods.getDesc1()));
			tempMap.put("goods_attr", StringUtil.formatStr(psGoods.getGoods_attr()));
			tempMap.put("cat_topid", StringUtil.formatStr(psGoods.getCat_topid()));
			tempMap.put("cat_twoid", StringUtil.formatStr(psGoods.getCat_twoid()));
			tempMap.put("cat_threeid", StringUtil.formatStr(psGoods.getCat_threeid()));
			tempMap.put("cat_topname", StringUtil.formatStr(psGoods.getCat_topname()));
			tempMap.put("cat_twoname", StringUtil.formatStr(psGoods.getCat_twoname()));
			tempMap.put("cat_threename", StringUtil.formatStr(psGoods.getCat_threename()));
			tempMap.put("brand_id", StringUtil.formatStr(psGoods.getBrand_id()));
			tempMap.put("brand_name", StringUtil.formatStr(psGoods.getBrand_name()));
			tempMap.put("online_date", StringUtil.formatStr(psGoods.getOnline_date()));
			tempMap.put("offline_date", StringUtil.formatStr(psGoods.getOffline_date()));
			tempMap.put("label", StringUtil.formatStr(psGoods.getLabel()));
			tempMap.put("label_promotion", StringUtil.formatStr(psGoods.getLabel_promotion()));
			tempMap.put("category", psGoods.getCategory());
			tempMap.put("display_area", psGoods.getDisplay_area());
			tempMap.put("contact_person", psGoods.getContact_person());
			tempMap.put("contact_tel", psGoods.getContact_tel());
			tempMap.put("pic_info", StringUtil.formatStr(psGoods.getPic_info()));
			tempMap.put("pic_detail_info", StringUtil.formatStr(psGoods.getPic_detail_info()));
			tempMap.put("specification", StringUtil.formatStr(psGoods.getSpecification()));
			tempMap.put("pack", StringUtil.formatStr(psGoods.getPack()));
			tempMap.put("cost_price", psGoods.getCost_price() + "");
			tempMap.put("market_price", psGoods.getMarket_price() + "");
			tempMap.put("discount_price", psGoods.getDiscount_price() + "");
			tempMap.put("ladder_price", psGoods.getLadder_price() + "");			
			tempMap.put("ts_min_num", psGoods.getTs_min_num() + "");
			tempMap.put("ts_price", psGoods.getTs_price() + "");
			tempMap.put("ts_date", psGoods.getTs_date() + "");
			tempMap.put("cost_allocation", psGoods.getCost_allocation() + "");
			tempMap.put("product_source", StringUtil.formatStr(psGoods.getProduct_source()));
			tempMap.put("shipping_fee", psGoods.getShipping_fee() + "");
			tempMap.put("settled_price", psGoods.getSettled_price() + "");
			tempMap.put("service_fee", psGoods.getService_fee() + "");
			tempMap.put("producer", StringUtil.formatStr(psGoods.getProducer()));
			tempMap.put("manufacturer", StringUtil.formatStr(psGoods.getManufacturer()));
			tempMap.put("sale_qty", psGoods.getSale_qty() + "");
			tempMap.put("stock_qty", psGoods.getStock_qty() + "");
			tempMap.put("goods_unit", StringUtil.formatStr(psGoods.getGoods_unit()));
			tempMap.put("valid_thru", StringUtil.formatStr(psGoods.getValid_thru()));
			tempMap.put("payment_way", StringUtil.formatStr(psGoods.getPayment_way()));
			tempMap.put("status", psGoods.getStatus());
			tempMap.put("created_date", DateUtil.date2Str(psGoods.getCreated_date(), DateUtil.fmt_yyyyMMddHHmmss));
			tempMap.put("offline_date", DateUtil.date2Str(psGoods.getOffline_date(), DateUtil.fmt_yyyyMMddHHmmss));
			tempMap.put("remark", StringUtil.formatStr(psGoods.getRemark()));
			tempMap.put("parity_price", StringUtil.formatStr(psGoods.getParity_price()));
			tempMap.put("parity_platform", StringUtil.formatStr(psGoods.getParity_platform()));
			tempMap.put("parity_url", StringUtil.formatStr(psGoods.getParity_url()));
			tempMap.put("commission_fee", StringUtil.formatStr(psGoods.getCommission_fee()));
			tempMap.put("nowdiffOffline_date", StringUtil.formatStr(psGoods.getNowdiffOffline_date()));
			List<Map<String, Object>> tempList = FastJsonUtil.jsonToList((String) tempMap.get("pic_info"));
			for (Map<String, Object> m : tempList) {
				if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
					String path_id = m.get("path_id") + "";
					String pic_info_url = docUtil.imageUrlFind(path_id);
					tempMap.put("pic_info_url", pic_info_url);
					break;
				}
			}
			resultList.add(tempMap);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", resultList);
		result.setResultData(map);
	}

	@Override
	public void newestPsGoodsListForOpFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		List<PsGoods> list = psGoodsDao.selectNewestPsGoodsForOp(paramsMap);
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (PsGoods psGoods : list) {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("goods_id", psGoods.getGoods_id());
			tempMap.put("goods_code", StringUtil.formatStr(psGoods.getGoods_code()));
			tempMap.put("title", psGoods.getTitle());
			tempMap.put("desc1", StringUtil.formatStr(psGoods.getDesc1()));
			tempMap.put("label", StringUtil.formatStr(psGoods.getLabel()));
			tempMap.put("label_promotion", StringUtil.formatStr(psGoods.getLabel_promotion()));
			tempMap.put("category", psGoods.getCategory());
			tempMap.put("data_source", StringUtil.formatStr(psGoods.getData_source()));
			tempMap.put("display_area", psGoods.getDisplay_area());
			tempMap.put("contact_person", psGoods.getContact_person());
			tempMap.put("contact_tel", psGoods.getContact_tel());
			tempMap.put("specification", StringUtil.formatStr(psGoods.getSpecification()));
			tempMap.put("cost_price", psGoods.getCost_price() + "");
			tempMap.put("market_price", psGoods.getMarket_price() + "");
			tempMap.put("discount_price", psGoods.getDiscount_price() + "");
			tempMap.put("settled_price", psGoods.getSettled_price() + "");
			tempMap.put("service_fee", psGoods.getService_fee() + "");
			tempMap.put("product_source", StringUtil.formatStr(psGoods.getProduct_source()));
			tempMap.put("shipping_fee", psGoods.getShipping_fee() + "");
			tempMap.put("producer", StringUtil.formatStr(psGoods.getProducer()));
			tempMap.put("supplier", StringUtil.formatStr(psGoods.getSupplier()));
			tempMap.put("supplier_id", StringUtil.formatStr(psGoods.getSupplier_id()));
			tempMap.put("manufacturer", StringUtil.formatStr(psGoods.getManufacturer()));
			tempMap.put("min_buy_qty", psGoods.getMin_buy_qty() + "");
			tempMap.put("max_buy_qty", psGoods.getMax_buy_qty() + "");
			tempMap.put("goods_unit", StringUtil.formatStr(psGoods.getGoods_unit()));
			tempMap.put("warehouse", StringUtil.formatStr(psGoods.getWarehouse()));
			tempMap.put("delivery", StringUtil.formatStr(psGoods.getDelivery()));
			tempMap.put("valid_thru", StringUtil.formatStr(psGoods.getValid_thru()));
			tempMap.put("status", psGoods.getStatus());
			tempMap.put("remark", StringUtil.formatStr(psGoods.getRemark()));
			tempMap.put("pic_info", StringUtil.formatStr(psGoods.getPic_info()));
			tempMap.put("sale_qty", psGoods.getSale_qty() + "");
			tempMap.put("stock_qty", psGoods.getStock_qty() + "");
			resultList.add(tempMap);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", resultList);
		result.setResultData(map);
	}

	@Override
	public void psGoodsFindForOrder(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		// goods_id 和 goods_code 必须有一个
		ValidateUtil.validateParamsNum(paramsMap, new String[] { "goods_id", "goods_code" }, 1);
		Map<String, Object> psGoodsMap = psGoodsDao.selectPsGoodsForOrder(paramsMap);
		if (null == psGoodsMap) {
			throw new BusinessException(RspCode.PS_GOODS_NOT_EXIST, RspCode.MSG.get(RspCode.PS_GOODS_NOT_EXIST));
		}
		result.setResultData(psGoodsMap);
	}
	
	@Override
	public void gdFreeGetGoodsFindForOrder(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		// goods_id 和 goods_code 必须有一个
		ValidateUtil.validateParamsNum(paramsMap, new String[] { "goods_id", "goods_code" }, 1);
		Map<String, Object> psGoodsMap = psGoodsDao.selectGdFreeGetGoodsForOrder(paramsMap);
		if (null == psGoodsMap) {
			throw new BusinessException(RspCode.PS_GOODS_NOT_EXIST, RspCode.MSG.get(RspCode.PS_GOODS_NOT_EXIST));
		}
		result.setResultData(psGoodsMap);
	}

	@Override
	public void psGoodsFindForTsActivity(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "goods_id" });
		Map<String, Object> psGoodsMap = psGoodsDao.selectPsGoodsForTsActivity(paramsMap);
		if (null == psGoodsMap) {
			throw new BusinessException(RspCode.PS_GOODS_NOT_EXIST, RspCode.MSG.get(RspCode.PS_GOODS_NOT_EXIST));
		}
		result.setResultData(psGoodsMap);
	}

	@Override
	public void psGoodsActivityForOpFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "activity_type" });
		List<Map<String, Object>> goodsActivityList = psGoodsDao.selectPsGoodsActivityForOpList(paramsMap);
		List<Map<String, Object>> activityList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> activityMap : goodsActivityList) {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("goods_id", activityMap.get("goods_id"));
			tempMap.put("goods_code", activityMap.get("goods_code"));
			tempMap.put("title", activityMap.get("title"));
			tempMap.put("pic_info", activityMap.get("pic_info"));
			activityList.add(tempMap);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", activityList);
		result.setResultData(map);
	}

	/**
	 * 推荐商品创建
	 * 
	 * @param consumer_id
	 * @param seller_id
	 * @param voucher_amount
	 * @throws BusinessException
	 * @throws SystemException
	 */
	public void handlePsGoodsActivitySync(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "activity_params", "activity_type" });
		String activity_type = StringUtil.formatStr(paramsMap.get("activity_type"));

		// 清除推荐商品对应的缓存数据
		String key = "[psGoodsActivityListFind]" + activity_type;
		redisUtil.del(key);
		key = "[psGoodsActivityHomeForConsumerFind]" + activity_type;
		redisUtil.del(key);

		List<Map<String, Object>> activityParamsList = FastJsonUtil
				.jsonToList((String) paramsMap.get("activity_params"));
		if(activityParamsList.size() > 0){
			// 删除推荐商品类型
			psGoodsDao.deletePsGoodsActivity(activity_type);
			for (Map<String, Object> activity : activityParamsList) {
				ValidateUtil.validateParams(activity, new String[] { "order_no", "goods_id", "pic_info" });
				PsGoodsActivity psGoodsActivity = new PsGoodsActivity();
				BeanConvertUtil.mapToBean(psGoodsActivity, activity);
				psGoodsActivity.setActivity_type(activity_type);
				psGoodsActivity.setGoods_activity_id(IDUtil.getUUID());
				psGoodsActivity.setStart_date(new Date());
				psGoodsActivity.setCreated_date(new Date());
				psGoodsActivity.setIs_finished("N");
				psGoodsDao.insertPsGoodsActivity(psGoodsActivity);
			}
		}else {
			psGoodsDao.deletePsGoodsActivity(activity_type);
		}
	}

	@Override
	public void psGoodsActivityListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "activity_type" });
		String activity_type = StringUtil.formatStr(paramsMap.get("activity_type"));
		String key = "[psGoodsActivityListFind]" + activity_type;
		Object obj = redisUtil.getObj(key);
		if (null != obj) {
			result.setResultData(obj);
			return;
		}

		List<Map<String, Object>> goodsActivityList = psGoodsDao.selectPsGoodsActivityList(paramsMap);
		List<Map<String, Object>> activityList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> activityMap : goodsActivityList) {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("goods_id", activityMap.get("goods_id"));
			tempMap.put("goods_code", activityMap.get("goods_code"));
			tempMap.put("label_promotion", StringUtil.formatStr(activityMap.get("label_promotion")));
			tempMap.put("data_source", StringUtil.formatStr(activityMap.get("data_source")));
			tempMap.put("title", activityMap.get("title"));
			tempMap.put("market_price", activityMap.get("market_price") + "");
			// 如果是伙拼团推荐,优惠价取成团价格
			if (activity_type.equals("HDMS_06")) {
				tempMap.put("discount_price", activityMap.get("ts_price") + "");
			} else {
				tempMap.put("discount_price", activityMap.get("discount_price") + "");
			}
			tempMap.put("specification", StringUtil.formatStr(activityMap.get("specification")));
			tempMap.put("supplier", StringUtil.formatStr(activityMap.get("supplier")));
			tempMap.put("min_buy_qty", StringUtil.formatStr(activityMap.get("min_buy_qty")));
			tempMap.put("valid_thru", StringUtil.formatStr(activityMap.get("valid_thru")));
			tempMap.put("sale_qty", activityMap.get("sale_qty") + "");
			tempMap.put("stock_qty", activityMap.get("stock_qty") + "");

			// 解析图片
			List<Map<String, Object>> tempList = FastJsonUtil.jsonToList((String) activityMap.get("pic_info"));
			for (Map<String, Object> m : tempList) {
				if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
					String path_id = m.get("path_id") + "";
					String pic_info_url = docUtil.imageUrlFind(path_id);
					tempMap.put("pic_info_url", pic_info_url);
					break;
				}
			}
			activityList.add(tempMap);
		}
		Map<String, Object> resultDataMap = new HashMap<String, Object>();
		resultDataMap.put("list", activityList);
		result.setResultData(resultDataMap);

		redisUtil.setObj(key, resultDataMap, 3600);
	}

	@Deprecated
	@Override
	public void psGoodsActivityFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "activity_type" });
		String activity_type = StringUtil.formatStr(paramsMap.get("activity_type"));
		String key = "[psGoodsActivityFind]" + activity_type;
		Object obj = redisUtil.getObj(key);
		if (null != obj) {
			result.setResultData(obj);
			return;
		}

		List<Map<String, Object>> goodsActivityList = psGoodsDao.selectPsGoodsActivityList(paramsMap);
		List<Map<String, Object>> activityList = new ArrayList<Map<String, Object>>();
		List<String> doc_ids = new ArrayList<String>();
		for (Map<String, Object> activityMap : goodsActivityList) {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("goods_id", activityMap.get("goods_id"));
			tempMap.put("goods_code", activityMap.get("goods_code"));
			tempMap.put("label_promotion", StringUtil.formatStr(activityMap.get("label_promotion")));
			tempMap.put("data_source", StringUtil.formatStr(activityMap.get("data_source")));
			tempMap.put("title", activityMap.get("title"));
			tempMap.put("pic_info", activityMap.get("pic_info"));
			tempMap.put("market_price", activityMap.get("market_price") + "");
			// 如果是伙拼团推荐,优惠价取成团价格
			if (activity_type.equals("HDMS_06")) {
				tempMap.put("discount_price", activityMap.get("ts_price") + "");
			} else {
				tempMap.put("discount_price", activityMap.get("discount_price") + "");
			}
			tempMap.put("specification", StringUtil.formatStr(activityMap.get("specification")));
			tempMap.put("supplier", StringUtil.formatStr(activityMap.get("supplier")));
			tempMap.put("min_buy_qty", StringUtil.formatStr(activityMap.get("min_buy_qty")));
			tempMap.put("valid_thru", StringUtil.formatStr(activityMap.get("valid_thru")));
			tempMap.put("sale_qty", activityMap.get("sale_qty") + "");
			tempMap.put("stock_qty", activityMap.get("stock_qty") + "");

			// 解析图片
			List<Map<String, Object>> tempList = FastJsonUtil.jsonToList((String) tempMap.get("pic_info"));
			for (Map<String, Object> m : tempList) {
				if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
					doc_ids.add(m.get("path_id") + "");
					break;
				}
			}
			activityList.add(tempMap);
		}
		Map<String, Object> resultDataMap = new HashMap<String, Object>();
		Map<String, Object> doc_ids_url = docUtil.imageUrlFind(doc_ids);
		resultDataMap.put("doc_url", doc_ids_url);
		resultDataMap.put("list", activityList);
		result.setResultData(resultDataMap);

		// 设置缓存
		if (doc_ids_url.size() > 0) {
			redisUtil.setObj(key, resultDataMap, 3600);
		}
	}

	
	@Override
	public void freeGetGoodsActivityHomeListFindForSmallProgram(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "activity_type" });
		String activity_type = StringUtil.formatStr(paramsMap.get("activity_type"));
		String key = "[freeGetGoodsActivityHomeListFindForSmallProgram]" + activity_type;
		Object obj = redisUtil.getObj(key);
		if (null != obj) {
			result.setResultData(obj);
			return;
		}

		List<Map<String, Object>> goodsActivityList = psGoodsDao.selectPsGoodsActivityHomeListForSmallProgram(paramsMap);
		List<Map<String, Object>> activityList = new ArrayList<Map<String, Object>>();
		List<String> doc_ids = new ArrayList<String>();
		for (Map<String, Object> activityMap : goodsActivityList) {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("goods_id", activityMap.get("goods_id"));
			tempMap.put("title", activityMap.get("title"));
			tempMap.put("desc1", StringUtil.formatStr(activityMap.get("desc1")));
			tempMap.put("pic_info", activityMap.get("pic_info"));
			tempMap.put("market_price", activityMap.get("market_price") + "");
			tempMap.put("data_source", activityMap.get("data_source") + "");
			tempMap.put("taobao_sales", activityMap.get("taobao_sales") + "");
			tempMap.put("taobao_price", activityMap.get("taobao_price") + "");
			// 如果是伙拼团推荐,优惠价取成团价格
			if (activity_type.equals("HDMS_06")) {
				tempMap.put("ts_min_num", activityMap.get("ts_min_num"));
				tempMap.put("discount_price", activityMap.get("ts_price") + "");
			} else {
				tempMap.put("discount_price", activityMap.get("discount_price") + "");
			}
			tempMap.put("valid_thru", StringUtil.formatStr(activityMap.get("valid_thru")));
			// 解析图片
			List<Map<String, Object>> tempList = FastJsonUtil.jsonToList((String) tempMap.get("pic_info"));
			for (Map<String, Object> m : tempList) {
				if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
					doc_ids.add(m.get("path_id") + "");
				}
			}
			activityList.add(tempMap);
		}

		Map<String, Object> resultDataMap = new HashMap<String, Object>();
		Map<String, Object> doc_ids_url = docUtil.imageUrlFind(doc_ids);
		resultDataMap.put("doc_url", doc_ids_url);
		resultDataMap.put("list", activityList);
		result.setResultData(resultDataMap);

		// 设置缓存
		if (doc_ids_url.size() > 0) {
			redisUtil.setObj(key, resultDataMap, 3600);
		}
	}
	
	
	@Override
	public void psGoodsActivityHomeForConsumerFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "activity_type" });
		String activity_type = StringUtil.formatStr(paramsMap.get("activity_type"));
		String key = "[psGoodsActivityHomeForConsumerFind]" + activity_type;
		Object obj = redisUtil.getObj(key);
		if (null != obj) {
			result.setResultData(obj);
			return;
		}

		List<Map<String, Object>> goodsActivityList = psGoodsDao.selectPsGoodsActivityHomeListForConsumer(paramsMap);
		List<Map<String, Object>> activityList = new ArrayList<Map<String, Object>>();
		List<String> doc_ids = new ArrayList<String>();
		for (Map<String, Object> activityMap : goodsActivityList) {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("goods_id", activityMap.get("goods_id"));
			tempMap.put("title", activityMap.get("title"));
			tempMap.put("desc1", StringUtil.formatStr(activityMap.get("desc1")));
			tempMap.put("pic_info", activityMap.get("pic_info"));
			tempMap.put("market_price", activityMap.get("market_price") + "");
			tempMap.put("data_source", activityMap.get("data_source") + "");
			// 如果是伙拼团推荐,优惠价取成团价格
			if (activity_type.equals("HDMS_06")) {
				tempMap.put("ts_min_num", activityMap.get("ts_min_num"));
				tempMap.put("discount_price", activityMap.get("ts_price") + "");
			} else {
				tempMap.put("discount_price", activityMap.get("discount_price") + "");
			}
			tempMap.put("valid_thru", StringUtil.formatStr(activityMap.get("valid_thru")));
			// 解析图片
			List<Map<String, Object>> tempList = FastJsonUtil.jsonToList((String) tempMap.get("pic_info"));
			for (Map<String, Object> m : tempList) {
				if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
					doc_ids.add(m.get("path_id") + "");
				}
			}
			activityList.add(tempMap);
		}

		Map<String, Object> resultDataMap = new HashMap<String, Object>();
		Map<String, Object> doc_ids_url = docUtil.imageUrlFind(doc_ids);
		resultDataMap.put("doc_url", doc_ids_url);
		resultDataMap.put("list", activityList);
		result.setResultData(resultDataMap);

		// 设置缓存
		if (doc_ids_url.size() > 0) {
			redisUtil.setObj(key, resultDataMap, 3600);
		}
	}

	@Override
	public void goodsLogListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			List<Map<String, Object>> goodsLogList = psGoodsDao.selectGoodsLog(paramsMap);
			List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
			for (Map<String, Object> goodsLogMap : goodsLogList) {
				Map<String, Object> tempMap = new HashMap<String, Object>();
				tempMap.put("log_id", StringUtil.formatStr(goodsLogMap.get("log_id")));
				tempMap.put("goods_id", StringUtil.formatStr(goodsLogMap.get("goods_id")));
				tempMap.put("goods_code", StringUtil.formatStr(goodsLogMap.get("goods_code")));
				tempMap.put("category", StringUtil.formatStr(goodsLogMap.get("category")));
				tempMap.put("tracked_date",
						DateUtil.date2Str((Date) goodsLogMap.get("tracked_date"), DateUtil.fmt_yyyyMMddHHmmss));
				tempMap.put("event", StringUtil.formatStr(goodsLogMap.get("event")));
				resultList.add(tempMap);
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("list", resultList);
			result.setResultData(map);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void goodsLogAdd(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "goods_id", "category", "event" });
			PsGoodsLog psGoodsLog = new PsGoodsLog();
			Date date = new Date();
			BeanConvertUtil.mapToBean(psGoodsLog, paramsMap);
			psGoodsLog.setLog_id(IDUtil.getUUID());
			psGoodsLog.setTracked_date(date);
			psGoodsDao.insertGoodsLog(psGoodsLog);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void gdViewSellDetailFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			List<Map<String, Object>> viewSellDetailList = psGoodsDao.selectGdViewSellDetail(paramsMap);
			List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
			for (Map<String, Object> viewSellDetailMap : viewSellDetailList) {
				Map<String, Object> tempMap = new HashMap<String, Object>();
				tempMap.put("goods_id", StringUtil.formatStr(viewSellDetailMap.get("goods_id")));
				tempMap.put("goods_code", StringUtil.formatStr(viewSellDetailMap.get("goods_code")));
				tempMap.put("title", StringUtil.formatStr(viewSellDetailMap.get("title")));
				tempMap.put("desc1", StringUtil.formatStr(viewSellDetailMap.get("desc1")));
				tempMap.put("label", StringUtil.formatStr(viewSellDetailMap.get("label")));
				tempMap.put("specification", StringUtil.formatStr(viewSellDetailMap.get("specification")));
				tempMap.put("pack", StringUtil.formatStr(viewSellDetailMap.get("pack")));
				tempMap.put("market_price", viewSellDetailMap.get("market_price") + "");
				tempMap.put("discount_price", viewSellDetailMap.get("discount_price") + "");
				tempMap.put("product_source", StringUtil.formatStr(viewSellDetailMap.get("product_source") + ""));
				tempMap.put("shipping_fee", viewSellDetailMap.get("shipping_fee") + "");
				tempMap.put("producer", StringUtil.formatStr(viewSellDetailMap.get("producer")));
				tempMap.put("supplier", StringUtil.formatStr(viewSellDetailMap.get("supplier")));
				tempMap.put("manufacturer", StringUtil.formatStr(viewSellDetailMap.get("manufacturer")));
				tempMap.put("min_buy_qty", viewSellDetailMap.get("min_buy_qty") + "");
				tempMap.put("max_buy_qty", viewSellDetailMap.get("max_buy_qty") + "");
				tempMap.put("goods_unit", StringUtil.formatStr(viewSellDetailMap.get("goods_unit")));
				tempMap.put("payment_way", StringUtil.formatStr(viewSellDetailMap.get("payment_way")));
				tempMap.put("pic_info", StringUtil.formatStr(viewSellDetailMap.get("pic_info")));
				tempMap.put("sale_qty", viewSellDetailMap.get("sale_qty") + "");
				tempMap.put("stock_qty", viewSellDetailMap.get("stock_qty") + "");
				tempMap.put("view", viewSellDetailMap.get("view") == null ? '0' : viewSellDetailMap.get("view") + "");
				tempMap.put("sell", viewSellDetailMap.get("sell") == null ? '0' : viewSellDetailMap.get("sell") + "");
				resultList.add(tempMap);
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("list", resultList);
			result.setResultData(map);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void gdViewAdd(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "goods_id" });
			String goods_id = paramsMap.get("goods_id") + "";
			Map<String, Integer> viewMap = new HashMap<String, Integer>();
			Object obj = redisUtil.getObj("ps_goods_view");
			if (obj != null) {
				viewMap = (Map<String, Integer>) redisUtil.getObj("ps_goods_view");
			}
			Integer view_count = viewMap.get(goods_id) == null ? 0 : viewMap.get(goods_id);
			viewMap.put(goods_id, view_count + 1);
			redisUtil.setObj("ps_goods_view", viewMap);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void gdSellAdd(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "goods_id", "sell_qty" });
			int updateFlag = psGoodsDao.updateGdViewSell(paramsMap);
			if (updateFlag == 0) { // 更新失败
				// 插入商品记录
				GdViewSell gdViewSell = new GdViewSell();
				gdViewSell.setGoods_id((String) paramsMap.get("goods_id"));
				gdViewSell.setView(1);
				gdViewSell.setSell(1);
				psGoodsDao.insertGdViewSell(gdViewSell);
			}
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}
	
	
	@Override
	public void freeGetGoodsListBySearchFindForSmallProgram(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			// ValidateUtil.validateParams(paramsMap, new String[] {"search"});

			// 后端暂时不返回字段给前段 当search字段为空时 等下次前端发版之后改回来 2017-09-18 丁忍
			Object tempObj = paramsMap.get("search");
			if (tempObj == null) {
				return;
			}

			String key = "[freeGetGoodsListBySearchFindForSmallProgram]" + paramsMap.get("search");
			Object obj = redisUtil.getObj(key);
			if (null != obj) {
				result.setResultData(obj);
				return;
			}
			// 查询可以购买的商品
			paramsMap.put("valid_thru_end", DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss));
			List<PsGoods> psGoodsList = psGoodsDao.selectfreeGetGoodsListBySearchForSmallProgram(paramsMap);
			List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
			for (PsGoods psGoods : psGoodsList) {
				Map<String, Object> psGoodsMap = new HashMap<String, Object>();
				psGoodsMap.put("goods_id", psGoods.getGoods_id());
				psGoodsMap.put("title", psGoods.getTitle());
				psGoodsMap.put("market_price", psGoods.getMarket_price() + "");
				psGoodsMap.put("discount_price", psGoods.getDiscount_price() + "");
				psGoodsMap.put("sale_qty", psGoods.getSale_qty() + "");
				psGoodsMap.put("stock_qty", psGoods.getStock_qty() + "");
				psGoodsMap.put("taobao_price", StringUtil.formatStr(psGoods.getTaobao_price()));
				psGoodsMap.put("taobao_link", StringUtil.formatStr(psGoods.getTaobao_link()));
				psGoodsMap.put("taobao_sales", StringUtil.formatStr(psGoods.getTaobao_sales()));
				psGoodsMap.put("data_source", StringUtil.formatStr(psGoods.getData_source()));
				// 解析图片
				String pic_info = StringUtil.formatStr(psGoods.getPic_info());
				if (StringUtil.isNotEmpty(pic_info)) {
					List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_info);
					for (Map<String, Object> m : tempList) {
						if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
							String path_id = m.get("path_id") + "";
							String pic_info_url = docUtil.imageUrlFind(path_id);
							psGoodsMap.put("pic_info_url", pic_info_url);
							break;
						}
					}
				}
				resultList.add(psGoodsMap);
			}
			Map<String, Object> resultDataMap = new HashMap<String, Object>();
			resultDataMap.put("list", resultList);
			result.setResultData(resultDataMap);
			// 设置缓存
			redisUtil.setObj(key, resultDataMap, 300);
			freeGetGoodsListCacheKeySet.add(key);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}
	

	@Override
	public void freeGetGoodsListBySearchFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			// ValidateUtil.validateParams(paramsMap, new String[] {"search"});

			// 后端暂时不返回字段给前段 当search字段为空时 等下次前端发版之后改回来 2017-09-18 丁忍
			Object tempObj = paramsMap.get("search");
			if (tempObj == null) {
				return;
			}

			String key = "[freeGetGoodsListBySearchFind]" + paramsMap.get("search");
			Object obj = redisUtil.getObj(key);
			if (null != obj) {
				result.setResultData(obj);
				return;
			}
			// 查询可以购买的商品
			paramsMap.put("valid_thru_end", DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss));
			List<PsGoods> psGoodsList = psGoodsDao.selectfreeGetGoodsListBySearch(paramsMap);
			List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
			for (PsGoods psGoods : psGoodsList) {
				Map<String, Object> psGoodsMap = new HashMap<String, Object>();
				psGoodsMap.put("goods_id", psGoods.getGoods_id());
				psGoodsMap.put("title", psGoods.getTitle());
				psGoodsMap.put("market_price", psGoods.getMarket_price() + "");
				psGoodsMap.put("discount_price", psGoods.getDiscount_price() + "");
				psGoodsMap.put("sale_qty", psGoods.getSale_qty() + "");
				psGoodsMap.put("stock_qty", psGoods.getStock_qty() + "");
				psGoodsMap.put("taobao_price", StringUtil.formatStr(psGoods.getTaobao_price()));
				psGoodsMap.put("taobao_link", StringUtil.formatStr(psGoods.getTaobao_link()));
				psGoodsMap.put("taobao_sales", StringUtil.formatStr(psGoods.getTaobao_sales()));
				psGoodsMap.put("data_source", StringUtil.formatStr(psGoods.getData_source()));
				// 解析图片
				String pic_info = StringUtil.formatStr(psGoods.getPic_info());
				if (StringUtil.isNotEmpty(pic_info)) {
					List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_info);
					for (Map<String, Object> m : tempList) {
						if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
							String path_id = m.get("path_id") + "";
							String pic_info_url = docUtil.imageUrlFind(path_id);
							psGoodsMap.put("pic_info_url", pic_info_url);
							break;
						}
					}
				}
				resultList.add(psGoodsMap);
			}
			Map<String, Object> resultDataMap = new HashMap<String, Object>();
			resultDataMap.put("list", resultList);
			result.setResultData(resultDataMap);
			// 设置缓存
			redisUtil.setObj(key, resultDataMap, 300);
			freeGetGoodsListCacheKeySet.add(key);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void freeGetGoodsForOperateListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		String key = "[freeGetGoodsForOperateListFind]";
		Object obj = redisUtil.getObj(key);
		if (null != obj) {
			result.setResultData(obj);
			return;
		}
		// 查询来源是花生日记
		paramsMap.put("data_source", "hsrj");
		// 商品状态 上架的
		paramsMap.put("status", "on_shelf");

		List<PsGoods> psGoodsList = psGoodsDao.selectFgGoodsListForOperate(paramsMap);
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (PsGoods psGoods : psGoodsList) {
			Map<String, Object> psGoodsMap = new HashMap<String, Object>();
			psGoodsMap.put("goods_id", psGoods.getGoods_id());
			psGoodsMap.put("title", psGoods.getTitle());
			psGoodsMap.put("market_price", psGoods.getMarket_price() + "");
			psGoodsMap.put("discount_price", psGoods.getDiscount_price() + "");
			psGoodsMap.put("sale_qty", psGoods.getSale_qty() + "");
			psGoodsMap.put("stock_qty", psGoods.getStock_qty() + "");
			psGoodsMap.put("taobao_price", StringUtil.formatStr(psGoods.getTaobao_price()));
			psGoodsMap.put("data_source", StringUtil.formatStr(psGoods.getData_source()));
			psGoodsMap.put("status", StringUtil.formatStr(psGoods.getStatus()));
			psGoodsMap.put("goods_code", StringUtil.formatStr(psGoods.getGoods_code()));
			psGoodsMap.put("commission_rate", StringUtil.formatStr(psGoods.getCommission_rate()));
			psGoodsMap.put("taobao_link", StringUtil.formatStr(psGoods.getTaobao_link()));
			psGoodsMap.put("taobao_sales", StringUtil.formatStr(psGoods.getTaobao_sales()));
			psGoodsMap.put("discount_end",
					StringUtil.formatStr(DateUtil.date2Str(psGoods.getDiscount_end(), DateUtil.fmt_yyyyMMddHHmmss)));
			// 解析图片
			String pic_info = StringUtil.formatStr(psGoods.getPic_info());
			if (StringUtil.isNotEmpty(pic_info)) {
				List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_info);
				for (Map<String, Object> m : tempList) {
					if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
						String path_id = m.get("path_id") + "";
						String pic_info_url = docUtil.imageUrlFind(path_id);
						psGoodsMap.put("pic_info_url", pic_info_url);
						break;
					}
				}
			}
			resultList.add(psGoodsMap);
		}
		Map<String, Object> resultDataMap = new HashMap<String, Object>();
		resultDataMap.put("list", resultList);
		result.setResultData(resultDataMap);
		// 设置缓存
		redisUtil.setObj(key, resultDataMap, 300);
	}



	@Override
	public void freeGetGoodsTaobaoSalesEdit(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "goods_id"});
			psGoodsDao.updateFreeGetGoodsTaobaoSales(paramsMap);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}
	
	@Override
	public void gdFreeGetGoodsTaobaoSalesEdit(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "goods_id"});
			psGoodsDao.updateGdFreeGetGoodsTaobaoSales(paramsMap);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void psHuiGuoGoodsListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		List<PsGoods> list = psGoodsDao.selectPsHuiGuoGoods(paramsMap);
		psHuiGuoGoodlistFindResultSetup(result, list);
	}

	private void psHuiGuoGoodlistFindResultSetup(ResultData result, List<PsGoods> list) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (PsGoods psGoods : list) {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("goods_id", psGoods.getGoods_id());
			tempMap.put("goods_code", StringUtil.formatStr(psGoods.getGoods_code()));
			tempMap.put("title", psGoods.getTitle());
			tempMap.put("desc1", StringUtil.formatStr(psGoods.getDesc1()));
			tempMap.put("label", StringUtil.formatStr(psGoods.getLabel()));
			tempMap.put("label_promotion", StringUtil.formatStr(psGoods.getLabel_promotion()));
			tempMap.put("category", psGoods.getCategory());
//			tempMap.put("data_source", StringUtil.formatStr(psGoods.getData_source()));
			tempMap.put("display_area", psGoods.getDisplay_area());
			tempMap.put("contact_person", psGoods.getContact_person());
			tempMap.put("contact_tel", psGoods.getContact_tel());
			tempMap.put("specification", StringUtil.formatStr(psGoods.getSpecification()));
			tempMap.put("cost_price", psGoods.getCost_price() + "");
			tempMap.put("market_price", psGoods.getMarket_price() + "");
			tempMap.put("discount_price", psGoods.getDiscount_price() + "");
			tempMap.put("settled_price", psGoods.getSettled_price() + "");
			tempMap.put("ts_min_num", psGoods.getTs_min_num() + "");
			tempMap.put("ts_price", psGoods.getTs_price() + "");
			tempMap.put("ts_date", psGoods.getTs_date() + "");
			tempMap.put("service_fee", psGoods.getService_fee() + "");
			tempMap.put("product_source", StringUtil.formatStr(psGoods.getProduct_source()));
			tempMap.put("shipping_fee", psGoods.getShipping_fee() + "");
			tempMap.put("cost_allocation", psGoods.getCost_allocation() + "");
			tempMap.put("producer", StringUtil.formatStr(psGoods.getProducer()));
			tempMap.put("supplier", StringUtil.formatStr(psGoods.getSupplier()));
			tempMap.put("supplier_id", StringUtil.formatStr(psGoods.getSupplier_id()));
			tempMap.put("manufacturer", StringUtil.formatStr(psGoods.getManufacturer()));
			tempMap.put("min_buy_qty", psGoods.getMin_buy_qty() + "");
			tempMap.put("max_buy_qty", psGoods.getMax_buy_qty() + "");
			tempMap.put("goods_unit", StringUtil.formatStr(psGoods.getGoods_unit()));
			tempMap.put("warehouse", StringUtil.formatStr(psGoods.getWarehouse()));
			tempMap.put("delivery", StringUtil.formatStr(psGoods.getDelivery()));
			tempMap.put("valid_thru", StringUtil.formatStr(psGoods.getValid_thru()));
			tempMap.put("offline_date", DateUtil.date2Str(psGoods.getOffline_date(), DateUtil.fmt_yyyyMMddHHmmss));
			tempMap.put("payment_way", StringUtil.formatStr(psGoods.getPayment_way()));
			tempMap.put("status", psGoods.getStatus());
			tempMap.put("remark", StringUtil.formatStr(psGoods.getRemark()));
			tempMap.put("pic_info", StringUtil.formatStr(psGoods.getPic_info()));
			tempMap.put("sale_qty", psGoods.getSale_qty() + "");
			tempMap.put("stock_qty", psGoods.getStock_qty() + "");
			tempMap.put("data_source_type", psGoods.getData_source_type() + "");
			tempMap.put("taobao_price", StringUtil.formatStr(psGoods.getTaobao_price()));
			tempMap.put("created_date", DateUtil.date2Str(psGoods.getCreated_date(), DateUtil.fmt_yyyyMMddHHmmss));
			tempMap.put("voucher_link", StringUtil.formatStr(psGoods.getVoucher_link()));
			tempMap.put("taobao_link", StringUtil.formatStr(psGoods.getTaobao_link()));
			tempMap.put("store_name", StringUtil.formatStr(psGoods.getTaobao_link()));
			tempMap.put("nowdiffOffline_date", StringUtil.formatStr(psGoods.getNowdiffOffline_date()));

			// 解析区域
			String[] areaStr = psGoods.getDelivery_area().split(",");
			params.clear();
			params.put("areaStr", areaStr);
			List<Map<String, Object>> areaList = goodsDao.selectMDArea(params);
			String delivery_desc = "";
			for (Map<String, Object> area : areaList) {
				delivery_desc = delivery_desc + "|" + area.get("path");
			}
			if (delivery_desc.trim().length() > 0) {
				delivery_desc = delivery_desc.substring(1);
			}
			tempMap.put("delivery_area", StringUtil.formatStr(psGoods.getDelivery_area()));
			tempMap.put("delivery_desc", delivery_desc);
			resultList.add(tempMap);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", resultList);
		result.setResultData(map);
	}

	@Override
	public void updateGoodsSaleOrShelves(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "goods_id" });
		Map<String, Object> tempMap = new HashMap<String, Object>();
		RedisLock lock = null;
		String lockKey = null;
		try{
			// 修改sku库存表
			psGoodsDao.updateGdGoodsItem(paramsMap);
			tempMap.clear();
			tempMap.put("goods_id", paramsMap.get("goods_id"));
			Map<String, Object> eventTempMap = new HashMap<String, Object>();
			eventTempMap.put("remark","修改商品:" + paramsMap.get("title") + ",商品ID：" + paramsMap.get("goods_id") );
			tempMap.put("event", FastJsonUtil.toJson(eventTempMap));
			goodsLogAdd(tempMap, result);
		}catch (Exception e) {
			throw e;
		} finally {
			if (lock != null) {
				lock.unlock();
			}
			if (StringUtil.isNotEmpty(lockKey)) {
				redisUtil.del(lockKey);
			}
		}

	}

	/**
	 * 查询价格小于等于9.9已上架的商品
	 */
	@Override
	public void less9PsGoodsListFind(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception {
		try {
			List<PsGoods> psGoodsList=psGoodsDao.selectLess9PsGoodsList(paramsMap);
			
			List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
			for (PsGoods psGoods :psGoodsList) {
				Map<String, Object> psGoodsMap = new HashMap<String, Object>();
				psGoodsMap.put("goods_id", psGoods.getGoods_id());
				psGoodsMap.put("title", psGoods.getTitle());
				psGoodsMap.put("market_price", psGoods.getMarket_price() + "");
				psGoodsMap.put("discount_price", psGoods.getDiscount_price() + "");
				psGoodsMap.put("sale_qty", psGoods.getSale_qty() + "");
				psGoodsMap.put("stock_qty", psGoods.getStock_qty() + "");
				psGoodsMap.put("taobao_price", StringUtil.formatStr(psGoods.getTaobao_price()));
				psGoodsMap.put("taobao_link", StringUtil.formatStr(psGoods.getTaobao_link()));
				psGoodsMap.put("taobao_sales", StringUtil.formatStr(psGoods.getTaobao_sales()));
				psGoodsMap.put("data_source", StringUtil.formatStr(psGoods.getData_source()));
				psGoodsMap.put("label_promotion", StringUtil.formatStr(psGoods.getLabel_promotion()));
				// 解析图片
				String pic_info = StringUtil.formatStr(psGoods.getPic_info());
				if (StringUtil.isNotEmpty(pic_info)) {
					List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_info);
					for (Map<String, Object> m : tempList) {
						if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
							String path_id = m.get("path_id") + "";
							String pic_info_url = docUtil.imageUrlFind(path_id);
							psGoodsMap.put("pic_info_url", pic_info_url);
							break;
						}
					}
				}
				resultList.add(psGoodsMap);
			}
			
			Map<String, Object> resultDataMap = new HashMap<String, Object>();
			resultDataMap.put("list", resultList);
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
	public void handlePsGoodsSaleQtyForOwnRestore(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "goods_id","goods_code","restore_qty" ,"sku_id" });
		//第一步先恢复商品主表可销售库存
		int updateFlag = psGoodsDao.updatePsGoodsSaleQtyForOwnRestore(paramsMap);
	    //第二步恢复对应商品规格表中的库存
		int updateSkuFlag = gdGoodsSkuidDao.updatePsGoodsSkuidStockForOwnRestore(paramsMap);
		
		if (updateFlag == 0 || updateSkuFlag == 0) {
			throw new BusinessException(RspCode.PS_GOODS_NOT_EXIST, "系统繁忙,请重新操作");
		}
		paramsMap.put("sell_qty", "-" + paramsMap.get("restore_qty"));
		gdSellAdd(paramsMap, new ResultData());
	}

	@Override
	public void newPsGoodsSaleQtyDeduction(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "goods_id", "sell_qty" });
		String sku_id = (String) paramsMap.get("sku_id");
		int updateFlag = psGoodsDao.updatePsGoodsSaleQtyDeduction(paramsMap);
		if (updateFlag == 0) {
			throw new BusinessException(RspCode.PROCESSING, "可售库存不足");
		}
		if (sku_id != null && StringUtil.isNotBlank(sku_id)) {
			Integer flag = psGoodsDao.updatePsGoodsSkuStockDeduction(paramsMap);
			if (flag == 0) {
				throw new BusinessException(RspCode.PROCESSING, "可售库存不足");
			}
		}
		gdSellAdd(paramsMap, new ResultData());
	}

	@Override
	public void handlePsOwnGoodsSync(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		/*ValidateUtil.validateParams(paramsMap,
				new String[] { "goods_id", "title", "area_id",  "desc1", "contact_person",
						"contact_tel", "pic_info", "cost_price", "market_price", "total_sales", "ladder_price",
						"min_buy_qty", "max_buy_qty", "payment_way", "status", "shipping_fee" });*/
		ValidateUtil.validateParams(paramsMap,
				new String[] { "goods_id", "title", "area_id",  "desc1", "contact_person",
						"contact_tel", "cost_price", "total_sales", "ladder_price",
						"min_buy_qty", "max_buy_qty", "payment_way", "status", "shipping_fee" });
		
		RedisLock lock = null;
		String lockKey = null;
		try {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			String goods_code = StringUtil.formatStr(paramsMap.get("goods_code"));
			lockKey = "[psGoodsSync]_" + goods_code;
			lock = new RedisLock(redisUtil, lockKey, 10 * 1000);
			lock.lock();

			String lockValue = redisUtil.getStr(lockKey);
			if (StringUtil.isNotEmpty(lockValue)) {
				throw new BusinessException(RspCode.GOODS_CODE_ERROR, "您的操作过于频繁,请稍后重试");
			}
			redisUtil.setStr(lockKey, goods_code, 180);

			// 根据商品id查询商品，存在则更新，不存在新增
			tempMap.clear();
			tempMap.put("goods_id", paramsMap.get("goods_id"));
			//List<PsGoods> psGoodsList = psGoodsDao.selectPsGoods(tempMap); -原代码
			List<BeikeMallGoods> beikeGoodList = psGoodsDao.selectBeiKeGoodscode(paramsMap);
			if (beikeGoodList.size() > 0) {
				BeikeMallGoods beikeMallGoods = beikeGoodList.get(0);
				String sale_qty = StringUtil.formatStr(paramsMap.get("sale_qty"));
				if (!StringUtils.isEmpty(sale_qty)) {
					// 计算差值
					Integer num = Integer.parseInt(sale_qty) - beikeMallGoods.getSale_qty();
					// 计算总库存量
					Integer new_stock_qty = beikeMallGoods.getStock_qty() + num;
					paramsMap.put("stock_qty", new_stock_qty);
				}
				/*if ("hsrj".equals(paramsMap.get("data_source"))) {
					if (!StringUtil.isEmpty(StringUtil.formatStr(paramsMap.get("quan_id")))) {
						Map<String, Object> tempTaobaoMap = createCommandAndUrlByTaobaoAPI(
								paramsMap.get("goods_code").toString(), paramsMap.get("quan_id").toString());
						if (tempTaobaoMap != null) {
							String url_short = tempTaobaoMap.get("url_short").toString();
							if (url_short == null) {
								String url_long = tempTaobaoMap.get("url_long").toString();
								paramsMap.put("taobao_link", url_long);
							} else {
								paramsMap.put("taobao_link", url_short);
							}
							String command = tempTaobaoMap.get("command").toString();
							if (command != null) {
								paramsMap.put("product_source", command);
							}
						}
					}
				}*/




				/*Map<String,Object> map = new HashMap<String, Object>();
				map.put("goods_id",paramsMap.get("goods_id"));
				map.put("goods_code",paramsMap.get("goods_code"));
				map.put("title",paramsMap.get("title"));
				map.put("desc1",paramsMap.get("desc1"));
				map.put("top_catid",paramsMap.get("top_catid"));
				map.put("two_catid",paramsMap.get("two_catid"));
				map.put("three_catid",paramsMap.get("three_catid"));
				map.put("label",paramsMap.get("label"));
				map.put("label_promotion",paramsMap.get("label_promotion"));
				map.put("goods_source",paramsMap.get("goods_source"));
				map.put("display_area",paramsMap.get("display_area"));
				map.put("contact_person",paramsMap.get("contact_person"));
				map.put("contact_tel",paramsMap.get("contact_tel"));
				map.put("pic_info",paramsMap.get("pic_info"));
				map.put("pic_detail_info",paramsMap.get("pic_detail_info"));
				map.put("specification",paramsMap.get("specification"));
				map.put("market_price",paramsMap.get("market_price"));
				map.put("sale_price",paramsMap.get("sale_price"));
				map.put("beike_credit",paramsMap.get("beike_credit"));
				map.put("vip_price",paramsMap.get("vip_price"));
				map.put("shipping_fee",paramsMap.get("shipping_fee"));
				map.put("settled_price",paramsMap.get("settled_price"));
				map.put("producer",paramsMap.get("producer"));
				map.put("supplier",paramsMap.get("supplier"));
				map.put("supplier_id",paramsMap.get("supplier_id"));
				map.put("manufacturer",paramsMap.get("manufacturer"));
				map.put("min_buy_qty",paramsMap.get("min_buy_qty"));
				map.put("max_buy_qty",paramsMap.get("max_buy_qty"));
				map.put("sale_qty",paramsMap.get("sale_qty"));
				map.put("stock_qty",paramsMap.get("stock_qty"));
				map.put("goods_unit",paramsMap.get("goods_unit"));
				map.put("valid_thru",paramsMap.get("valid_thru"));
				map.put("warehouse",paramsMap.get("warehouse"));
				map.put("warehouse_id",paramsMap.get("warehouse_id"));
				map.put("delivery",paramsMap.get("delivery"));
				map.put("delivery_id",paramsMap.get("delivery_id"));
				map.put("delivery_area",paramsMap.get("delivery_area"));
				map.put("payment_way",paramsMap.get("payment_way"));
				map.put("status",paramsMap.get("status"));
				map.put("created_date",paramsMap.get("created_date"));
				map.put("modified_date",paramsMap.get("modified_date"));
				map.put("remark",paramsMap.get("remark"));
				map.put("online_date",paramsMap.get("online_date"));
				map.put("offline_date",paramsMap.get("offline_date"));*/
				paramsMap.put("goods_id",beikeMallGoods.getGoods_id());
				BeikeMallGoods beikeMallGoodsone = new BeikeMallGoods();
				BeanConvertUtil.mapToBean(beikeMallGoodsone,paramsMap);
				int a  = psGoodsDao.updateBeiKeGoodsedit(beikeMallGoodsone);
				tempMap.clear();
				tempMap.put("goods_id", paramsMap.get("goods_id"));
				tempMap.put("category", paramsMap.get("category"));
				Map<String, Object> eventTempMap = new HashMap<String, Object>();
				eventTempMap.put("remark","修改商品:" + paramsMap.get("title") + ",商品ID：" + paramsMap.get("goods_id") + ",商品码：" + goods_code);
				tempMap.put("event", FastJsonUtil.toJson(eventTempMap));
				goodsLogAdd(tempMap, result);
			} else {
				String cost_allocation = StringUtil.formatStr(paramsMap.get("cost_allocation"));
				if (StringUtils.isEmpty(cost_allocation)) {
					paramsMap.put("cost_allocation", "0.00");
				}
				String sale_qty = StringUtil.formatStr(paramsMap.get("sale_qty"));
				if (StringUtils.isBlank(sale_qty)) {
					paramsMap.put("sale_qty", "0");
				}
				if (!StringUtils.isEmpty(goods_code)) {
					// 验证商品码是否已存在
					tempMap.clear();
					tempMap.put("goods_code", paramsMap.get("goods_code"));
					List<BeikeMallGoods> beikeGoodTempList = psGoodsDao.selectBeiKeGoods(tempMap);
					if (beikeGoodTempList.size() > 0) {
						throw new BusinessException(RspCode.GOODS_CODE_EXIST,
								RspCode.MSG.get(RspCode.GOODS_CODE_EXIST));
					}
				}
				BeikeMallGoods beikeMallGoods = new BeikeMallGoods();
				BeanConvertUtil.mapToBean(beikeMallGoods, paramsMap);
				setValueToBean(paramsMap, beikeMallGoods);
				psGoodsDao.insertBeikeGoods(beikeMallGoods);

				tempMap.clear();
				tempMap.put("goods_id", paramsMap.get("goods_id"));
				tempMap.put("category", paramsMap.get("category"));
				Map<String, Object> eventTempMap = new HashMap<String, Object>();
				eventTempMap.put("remark","新增商品:" + paramsMap.get("title") + ",商品ID：" + paramsMap.get("goods_id") + ",商品码：" + goods_code);
				tempMap.put("event", FastJsonUtil.toJson(eventTempMap));
				goodsLogAdd(tempMap, result);
			}
			
			//同步sku数据
			handleGoodsSkuid(paramsMap,result);
			
			
			

		} catch (Exception e) {
			throw e;
		} finally {
			if (lock != null) {
				lock.unlock();
			}
			if (StringUtil.isNotEmpty(lockKey)) {
				redisUtil.del(lockKey);
			}
		}
	}

	//很多字段对应不上-设value  车夫修改代码
	private void setValueToBean(Map<String, Object> paramsMap, BeikeMallGoods beikeMallGoods) {
		Date date = new Date();
		beikeMallGoods.setStock_qty(beikeMallGoods.getSale_qty());
		beikeMallGoods.setModified_date(date);
		beikeMallGoods.setCreated_date(date);
		//String market_price = paramsMap.get("market_price").toString();
		//String total_sales =  paramsMap.get("total_sales").toString();
		//market_price = market_price.substring(2, market_price.length() - 2);
		//total_sales = total_sales.substring(2, total_sales.length() - 2);
		beikeMallGoods.setSale_price(new BigDecimal(paramsMap.get("market_price_first").toString()));
		beikeMallGoods.setVip_price(new BigDecimal(paramsMap.get("total_sales_first").toString()));
		if (beikeMallGoods.getSettled_price() == null) {
			beikeMallGoods.setSettled_price(new BigDecimal("0.00"));
		}
		if (beikeMallGoods.getShipping_fee() == null) {//邮费
			beikeMallGoods.setShipping_fee(new BigDecimal("0.00"));
		}
		beikeMallGoods.setTop_catid(Integer.parseInt(paramsMap.get("top_catid").toString()));
		beikeMallGoods.setTwo_catid(Integer.parseInt(paramsMap.get("two_catid").toString()));
		beikeMallGoods.setThree_catid(Integer.parseInt(paramsMap.get("three_catid").toString()));
	}


	private void handleGoodsSkuid(Map<String, Object> paramsMap, ResultData result) throws BusinessException{


		if(paramsMap.get("attr_zvalue_str")!=null &&StringUtil.isNotEmpty(paramsMap.get("attr_zvalue_str").toString())){
			String[] attr_zvalue=paramsMap.get("attr_zvalue_str").toString().split("\\,");
			String[] attr_fvalue= paramsMap.get("attr_fvalue_str").toString().split("\\,");
			String[] market_price = new String[0];
			if(paramsMap.containsKey("market_price_str")){
				market_price= paramsMap.get("market_price_str").toString().split("\\,");//市场价- 暂时sku表没字段
			}
			String[] sale_price = new String[0];
			if(paramsMap.containsKey("sale_price_str")) {
				sale_price = paramsMap.get("sale_price_str").toString().split("\\,");
			}
			if(sale_price.length == 0){
				sale_price = market_price;
			}
			String[] total_sales=paramsMap.get("total_sales_str").toString().split("\\,");
			String[] beike_credit=paramsMap.get("beike_credit_str").toString().split("\\,");
			String[] stock=paramsMap.get("stock_str").toString().split("\\,");
			Object sku_id_str=paramsMap.get("sku_id_str");
			String[] sku_id=null;
			if(sku_id_str!=null && StringUtil.isNotEmpty(sku_id_str.toString())){
				sku_id=sku_id_str.toString().split("\\,");
			}

			Date currentTime=new Date();
			List<String> tmpmarket_price = new ArrayList<String>();
			List<String> tmptotal_sales = new ArrayList<String>();
			if(attr_zvalue!=null&&attr_zvalue.length>0){




				for(int i=0;i<attr_zvalue.length;i++){
					try{
						GdGoodsSkuid record=new GdGoodsSkuid();
						record.setSku_id(UUID.randomUUID().toString().replaceAll("\\-", ""));
						record.setGoods_code(paramsMap.get("goods_code").toString());
						record.setAttr_zid(paramsMap.get("attr_zid").toString());
						record.setAttr_zvalue(attr_zvalue[i]);
						if(paramsMap.get("attr_fid") != null && !StringUtil.isEmpty(paramsMap.get("attr_fid").toString())){
							record.setAttr_fid(paramsMap.get("attr_fid").toString());
							record.setAttr_fvalue(attr_fvalue[i]);
						}
						record.setSale_price(new BigDecimal(sale_price[i]));
						if(StringUtil.isEmpty(total_sales[i])){
							total_sales[i]="0";
						}
						record.setBeike_credit(Integer.parseInt(beike_credit[i]));

						//record.setTotal_sales(calDiscountPrice(market_price[i],total_sales[i]));
						record.setVip_price(new BigDecimal(total_sales[i]));
						record.setStock(stock[i]);
						//贝壳字段来源？  -- 页面录入
						//record.setRemark("");


						record.setModified_time(currentTime);
						//只有值不为空才能保存
						if(StringUtil.isNotEmpty(attr_zvalue[i]) && StringUtil.isNotEmpty(sale_price[i]) && StringUtil.isNotEmpty(stock[i])){
							tmpmarket_price.add(sale_price[i]);
							tmptotal_sales.add(total_sales[i]);
							if(sku_id!=null && i<sku_id.length){
								record.setSku_id(sku_id[i]);
								gdGoodsSkuidDao.updateGdGoodsSkuid(record);
							}else{
								record.setCreated_time(currentTime);
								gdGoodsSkuidDao.insert(record);
							}
						}
					}catch(ArrayIndexOutOfBoundsException e){
						continue;
					}
				}

				paramsMap.put("market_price", getMinValue(tmpmarket_price));
				paramsMap.put("discount_price", getMinValue(tmptotal_sales));
			}

		}

	}
	
    public static String getMinValue(List<String> arr) {  
    	String minValue = "";
        int min = 0;  
        for(int i=0;i<arr.size();i++){  
        	BigDecimal arrbig = new BigDecimal(arr.get(i));
        	BigDecimal arrmin= new BigDecimal(arr.get(min));
            if(arrbig.compareTo(arrmin)<0){  
                min = i;  
            }
       }  
        minValue = arr.get(min);
        return minValue;
    }  

	private String calDiscountPrice(String market_price,String total_sales) throws BusinessException{
		if(StringUtil.isEmpty(market_price) || StringUtil.isEmpty(total_sales) ){
			return "0.00";
		}
		
		BigDecimal result=new BigDecimal(market_price).subtract(new BigDecimal(total_sales));
		if(result.doubleValue()<0){
			throw new BusinessException(RspCode.GOODS_CODE_ERROR, "市场价不能小于优惠价");
		}
		DecimalFormat df=new DecimalFormat("0.00");
		return  df.format(result);
	}

	@Override
	public void selectGdGoodsSkuidList(Map<String, Object> map, ResultData result) throws Exception {
		List<GdGoodsSkuid>  list=gdGoodsSkuidDao.selectGdGoodsSkuidList(map);
		Map<String, Object> resultDataMap = new HashMap<String, Object>();
		resultDataMap.put("list", list);
		result.setResultData(resultDataMap);
	}



	@Override
	public void newPsGoodsSaleQtyRestore(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "goods_id", "restore_qty" });
		String sku_id = (String) paramsMap.get("sku_id");
		int updateFlag = psGoodsDao.updatePsGoodsSaleQtyRestore(paramsMap);
		if (updateFlag == 0) {
			throw new BusinessException(RspCode.PS_GOODS_NOT_EXIST, "系统繁忙,请重新操作");
		}
		if (sku_id != null && StringUtil.isNotBlank(sku_id)) {
			Integer flag = psGoodsDao.updatePsGoodsSkuStockRestore(paramsMap);
			if (flag == 0) {
				throw new BusinessException(RspCode.PS_GOODS_NOT_EXIST, "系统繁忙,请重新操作");
			}
		}
		paramsMap.put("sell_qty", "-" + paramsMap.get("restore_qty"));
		gdSellAdd(paramsMap, new ResultData());
	}

	@Override
	public void queryGdGoodsSkuidBySkuId(Map<String, Object> map ,ResultData result)
			throws BusinessException, SystemException, Exception {
		  ValidateUtil.validateParams(map, new String[] {"sku_id"});
		  List<Map<String, Object>> list=gdGoodsSkuidDao.queryGdGoodsSkuidBySkuId(map);
		  if(list!=null && list.size()>0){
			  result.setResultData(list.get(0));
		  }
	}


	@Override
	public void freeGetGoodsListFindForNewOwn(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		String key = "[freeGetGoodsListFindForNewOwn]";
		Object obj = redisUtil.getObj(key);
		if (null != obj) {
			result.setResultData(obj);
			return;
		}
		// 查询可以购买的商品
		paramsMap.put("valid_thru_end", DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss));
		List<PsGoods> psGoodsList = psGoodsDao.selectFgGoodsListForNewOwn(paramsMap);
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (PsGoods psGoods : psGoodsList) {
			Map<String, Object> psGoodsMap = new HashMap<String, Object>();
			psGoodsMap.put("goods_id", psGoods.getGoods_id());
			psGoodsMap.put("title", psGoods.getTitle());
			psGoodsMap.put("market_price", psGoods.getMarket_price() + "");
			psGoodsMap.put("discount_price", psGoods.getDiscount_price() + "");
			psGoodsMap.put("sale_qty", psGoods.getSale_qty() + "");
			psGoodsMap.put("stock_qty", psGoods.getStock_qty() + "");
			psGoodsMap.put("goods_code", psGoods.getGoods_code());
			psGoodsMap.put("taobao_price", StringUtil.formatStr(psGoods.getTaobao_price()));
			psGoodsMap.put("taobao_link", StringUtil.formatStr(psGoods.getTaobao_link()));
			psGoodsMap.put("taobao_sales", StringUtil.formatStr(psGoods.getTaobao_sales()));
			psGoodsMap.put("data_source", StringUtil.formatStr(psGoods.getData_source()));
			// 解析图片
			String pic_info = StringUtil.formatStr(psGoods.getPic_info());
			if (StringUtil.isNotEmpty(pic_info)) {
				List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_info);
				for (Map<String, Object> m : tempList) {
					if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
						String path_id = m.get("path_id") + "";
						String pic_info_url = docUtil.imageUrlFind(path_id);
						psGoodsMap.put("pic_info_url", pic_info_url);
						break;
					}
				}
			}
			resultList.add(psGoodsMap);
		}
		Map<String, Object> resultDataMap = new HashMap<String, Object>();
		resultDataMap.put("list", resultList);
		result.setResultData(resultDataMap);
		// 设置缓存
		redisUtil.setObj(key, resultDataMap, 300);
		freeGetGoodsListCacheKeySet.add(key);
		
	}
	
	/**
	 * 查询价格小于等于9.9已上架的商品(新自营)
	 */
	@Override
	public void less9PsGoodsListFindNewOwn(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception {
		try {
			List<PsGoods> psGoodsList=psGoodsDao.selectLess9PsGoodsListNewOwn(paramsMap);
			
			List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
			for (PsGoods psGoods :psGoodsList) {
				Map<String, Object> psGoodsMap = new HashMap<String, Object>();
				psGoodsMap.put("goods_id", psGoods.getGoods_id());
				psGoodsMap.put("title", psGoods.getTitle());
				psGoodsMap.put("market_price", psGoods.getMarket_price() + "");
				psGoodsMap.put("discount_price", psGoods.getDiscount_price() + "");
				psGoodsMap.put("sale_qty", psGoods.getSale_qty() + "");
				psGoodsMap.put("stock_qty", psGoods.getStock_qty() + "");
				psGoodsMap.put("taobao_price", StringUtil.formatStr(psGoods.getTaobao_price()));
				psGoodsMap.put("taobao_link", StringUtil.formatStr(psGoods.getTaobao_link()));
				psGoodsMap.put("taobao_sales", StringUtil.formatStr(psGoods.getTaobao_sales()));
				psGoodsMap.put("data_source", StringUtil.formatStr(psGoods.getData_source()));
				psGoodsMap.put("label_promotion", StringUtil.formatStr(psGoods.getLabel_promotion()));
				// 解析图片
				String pic_info = StringUtil.formatStr(psGoods.getPic_info());
				if (StringUtil.isNotEmpty(pic_info)) {
					List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_info);
					for (Map<String, Object> m : tempList) {
						if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
							String path_id = m.get("path_id") + "";
							String pic_info_url = docUtil.imageUrlFind(path_id);
							psGoodsMap.put("pic_info_url", pic_info_url);
							break;
						}
					}
				}
				resultList.add(psGoodsMap);
			}
			
			Map<String, Object> resultDataMap = new HashMap<String, Object>();
			resultDataMap.put("list", resultList);
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
	public void freeGetGoodsByLabelListFindForNewOwn(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "label_promotion" });
		String label_promotion = paramsMap.get("label_promotion") + "";
		String key = "[freeGetGoodsByLabelListFindForOwn]" + label_promotion;
		Object obj = redisUtil.getObj(key);
		if (null != obj) {
			result.setResultData(obj);
			return;
		}
		// 查询可以购买的商品
		paramsMap.put("valid_thru_end", DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss));
		List<PsGoods> psGoodsList = psGoodsDao.selectfreeGetGoodsListByLabelForNewOwn(paramsMap);
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (PsGoods psGoods : psGoodsList) {
			Map<String, Object> psGoodsMap = new HashMap<String, Object>();
			psGoodsMap.put("goods_id", psGoods.getGoods_id());
			psGoodsMap.put("title", psGoods.getTitle());
			psGoodsMap.put("market_price", psGoods.getMarket_price() + "");
			psGoodsMap.put("discount_price", psGoods.getDiscount_price() + "");
			psGoodsMap.put("sale_qty", psGoods.getSale_qty() + "");
			psGoodsMap.put("stock_qty", psGoods.getStock_qty() + "");
			psGoodsMap.put("taobao_price", StringUtil.formatStr(psGoods.getTaobao_price()));
			psGoodsMap.put("online_date", DateUtil.date2Str(psGoods.getOnline_date(), DateUtil.fmt_yyyyMMddHHmmss));
			psGoodsMap.put("valid_thru", StringUtil.formatStr(psGoods.getValid_thru()));
			psGoodsMap.put("taobao_link", StringUtil.formatStr(psGoods.getTaobao_link()));
			psGoodsMap.put("taobao_sales", StringUtil.formatStr(psGoods.getTaobao_sales()));
			psGoodsMap.put("data_source", StringUtil.formatStr(psGoods.getData_source()));
			
			// 解析图片
			String pic_info = StringUtil.formatStr(psGoods.getPic_info());
			if (StringUtil.isNotEmpty(pic_info)) {
				List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_info);
				for (Map<String, Object> m : tempList) {
					if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
						String path_id = m.get("path_id") + "";
						String pic_info_url = docUtil.imageUrlFind(path_id);
						psGoodsMap.put("pic_info_url", pic_info_url);
						break;
					}
				}
			}
			resultList.add(psGoodsMap);
		}
		Map<String, Object> resultDataMap = new HashMap<String, Object>();
		resultDataMap.put("list", resultList);
		result.setResultData(resultDataMap);
		// 设置缓存
		redisUtil.setObj(key, resultDataMap, 10);
		freeGetGoodsListCacheKeySet.add(key);
		
	}

	@Override
	public void gdFreegetGoodsListPageFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		String key = "[gdFreegetGoodsListFind]";
		Object obj = redisUtil.getObj(key);
		if (null != obj) {
			result.setResultData(obj);
			return;
		}
		// 查询可以购买的商品
		paramsMap.put("valid_thru_end", DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss));
		List<PsGoods> psGoodsList = psGoodsDao.selectGdFreeGetGoodsList(paramsMap);
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (PsGoods psGoods : psGoodsList) {
			Map<String, Object> psGoodsMap = new HashMap<String, Object>();
			psGoodsMap.put("goods_id", psGoods.getGoods_id());
			psGoodsMap.put("title", psGoods.getTitle());
			psGoodsMap.put("market_price", psGoods.getMarket_price() + "");
			psGoodsMap.put("discount_price", psGoods.getDiscount_price() + "");
			psGoodsMap.put("sale_qty", psGoods.getSale_qty() + "");
			psGoodsMap.put("stock_qty", psGoods.getStock_qty() + "");
			psGoodsMap.put("taobao_price", StringUtil.formatStr(psGoods.getTaobao_price()));
			psGoodsMap.put("taobao_link", StringUtil.formatStr(psGoods.getTaobao_link()));
			psGoodsMap.put("taobao_sales", StringUtil.formatStr(psGoods.getTaobao_sales()));
			psGoodsMap.put("data_source", StringUtil.formatStr(psGoods.getData_source()));
			// 解析图片
			String pic_info = StringUtil.formatStr(psGoods.getPic_info());
			if (StringUtil.isNotEmpty(pic_info)) {
				List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_info);
				for (Map<String, Object> m : tempList) {
					if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
						String path_id = m.get("path_id") + "";
						String pic_info_url = docUtil.imageUrlFind(path_id);
						psGoodsMap.put("pic_info_url", pic_info_url);
						break;
					}
				}
			}
			resultList.add(psGoodsMap);
		}
		Map<String, Object> resultDataMap = new HashMap<String, Object>();
		resultDataMap.put("list", resultList);
		result.setResultData(resultDataMap);
		// 设置缓存
		redisUtil.setObj(key, resultDataMap, 300);
		freeGetGoodsListCacheKeySet.add(key);
	}
	
	@Override
	public void gdFreeGetGoodsDetailFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		if (paramsMap.size() == 0) {
			throw new BusinessException(RspCode.SYSTEM_PARAM_MISS, "参数缺失");
		}
		List<PsGoods> psGoodsList = psGoodsDao.selectGdFreeGetGoods(paramsMap);
		List<String> doc_ids = new ArrayList<String>();
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> map = new HashMap<String, Object>();
		String goods_code="";
		int count=0;
		for (PsGoods psGoods : psGoodsList) {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("goods_id", psGoods.getGoods_id());
			tempMap.put("goods_code", StringUtil.formatStr(psGoods.getGoods_code()));
			tempMap.put("title", psGoods.getTitle());
			tempMap.put("desc1", StringUtil.formatStr(psGoods.getDesc1()));
			tempMap.put("area_id", psGoods.getArea_id());
			tempMap.put("label", StringUtil.formatStr(psGoods.getLabel()));
			tempMap.put("label_promotion", StringUtil.formatStr(psGoods.getLabel_promotion()));
			tempMap.put("category", psGoods.getCategory());
			tempMap.put("data_source", StringUtil.formatStr(psGoods.getData_source()));
			tempMap.put("display_area", psGoods.getDisplay_area());
			tempMap.put("contact_person", psGoods.getContact_person());
			tempMap.put("contact_tel", psGoods.getContact_tel());
			tempMap.put("specification", StringUtil.formatStr(psGoods.getSpecification()));
			tempMap.put("agent", StringUtil.formatStr(psGoods.getAgent()));
			tempMap.put("pack", StringUtil.formatStr(psGoods.getPack()));
			tempMap.put("cost_price", psGoods.getCost_price() + "");
			tempMap.put("market_price", psGoods.getMarket_price() + "");
			tempMap.put("discount_price", psGoods.getDiscount_price() + "");
			tempMap.put("ladder_price", StringUtil.formatStr(psGoods.getLadder_price()));
			tempMap.put("product_source", StringUtil.formatStr(psGoods.getProduct_source()));
			tempMap.put("shipping_fee", psGoods.getShipping_fee() + "");
			tempMap.put("cost_allocation", psGoods.getCost_allocation() + "");
			tempMap.put("settled_price", psGoods.getSettled_price() + "");
			tempMap.put("ts_min_num", psGoods.getTs_min_num() + "");
			tempMap.put("ts_price", psGoods.getTs_price() + "");
			tempMap.put("ts_date", psGoods.getTs_date() + "");
			tempMap.put("service_fee", psGoods.getService_fee() + "");
			tempMap.put("producer", StringUtil.formatStr(psGoods.getProducer()));
			tempMap.put("supplier", StringUtil.formatStr(psGoods.getSupplier()));
			tempMap.put("supplier_id", StringUtil.formatStr(psGoods.getSupplier_id()));
			tempMap.put("warehouse", StringUtil.formatStr(psGoods.getWarehouse()));
			tempMap.put("warehouse_id", StringUtil.formatStr(psGoods.getWarehouse_id()));
			tempMap.put("manufacturer", StringUtil.formatStr(psGoods.getManufacturer()));
			tempMap.put("min_buy_qty", psGoods.getMin_buy_qty() + "");
			tempMap.put("max_buy_qty", psGoods.getMax_buy_qty() + "");
			tempMap.put("goods_unit", StringUtil.formatStr(psGoods.getGoods_unit()));
			tempMap.put("delivery", StringUtil.formatStr(psGoods.getDelivery()));
			tempMap.put("delivery_id", StringUtil.formatStr(psGoods.getDelivery_id()));
			tempMap.put("valid_thru", StringUtil.formatStr(psGoods.getValid_thru()));
			tempMap.put("payment_way", StringUtil.formatStr(psGoods.getPayment_way()));
			tempMap.put("status", psGoods.getStatus());
			tempMap.put("remark", psGoods.getRemark());
			tempMap.put("pic_info", StringUtil.formatStr(psGoods.getPic_info()));
			tempMap.put("pic_detail_info", StringUtil.formatStr(psGoods.getPic_detail_info()));
			tempMap.put("online_date", DateUtil.date2Str(psGoods.getOnline_date(), DateUtil.fmt_yyyyMMddHHmmss));
			tempMap.put("sale_qty", psGoods.getSale_qty() + "");
			tempMap.put("stock_qty", psGoods.getStock_qty() + "");
			tempMap.put("data_source_type", psGoods.getData_source_type());
			tempMap.put("taobao_price", StringUtil.formatStr(psGoods.getTaobao_price()));
			tempMap.put("voucher_link", StringUtil.formatStr(psGoods.getVoucher_link()));
			tempMap.put("discount_end", DateUtil.date2Str(psGoods.getDiscount_end(), DateUtil.fmt_yyyyMMddHHmmss));
			tempMap.put("taobao_link", StringUtil.formatStr(psGoods.getTaobao_link()));
			tempMap.put("taobao_sales", StringUtil.formatStr(psGoods.getTaobao_sales()));
			tempMap.put("huiguo_commission", StringUtil.formatStr(psGoods.getHuiguo_commission()));
			tempMap.put("huiguo_vip", StringUtil.formatStr(psGoods.getHuiguo_vip()));
			tempMap.put("self_label",  StringUtil.formatStr(psGoods.getSelf_label()));	
			tempMap.put("commission", StringUtil.formatStr(psGoods.getCommission()));
			tempMap.put("flags", psGoods.getFlags());
			if(count==0){
				goods_code=StringUtil.formatStr(psGoods.getGoods_code());
			}
			count++;
			// 解析区域
			String[] areaStr = psGoods.getDelivery_area().split(",");
			params.clear();
			params.put("areaStr", areaStr);
			List<Map<String, Object>> areaList = goodsDao.selectMDArea(params);
			String delivery_desc = "";
			for (Map<String, Object> area : areaList) {
				delivery_desc = delivery_desc + "|" + area.get("path");
			}
			if (delivery_desc.trim().length() > 0) {
				delivery_desc = delivery_desc.substring(1);
			}
			tempMap.put("delivery_area", StringUtil.formatStr(psGoods.getDelivery_area()));
			tempMap.put("delivery_desc", delivery_desc);
			resultList.add(tempMap);
			// 解析图片
			String pic_info = StringUtil.formatStr(psGoods.getPic_info());
			try {
				if (!pic_info.equals("")) {
					List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_info);
					for (Map<String, Object> m : tempList) {
						if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
							doc_ids.add(m.get("path_id") + "");
						}
					}
				}
				// 解析详情图片
				String pic_detail_info = StringUtil.formatStr(psGoods.getPic_detail_info());
				if (!pic_detail_info.equals("")) {
					List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_detail_info);
					for (Map<String, Object> m : tempList) {
						if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
							doc_ids.add(m.get("path_id") + "");
						}
					}
				}
			} catch (Exception e) {
				map.put("doc_url", "");
				e.printStackTrace();
			}
		}
		map.put("doc_url", docUtil.imageUrlFind(doc_ids));
		map.put("list", resultList);
		result.setResultData(map);
	}

	@Override
	public void selectPsGoodsforGoodsCodeSet(Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "goodsCode_set" });
		List<String> goods_code_in = (List<String>)paramsMap.get("goodsCode_set");
		Map<String, Object> map = new HashMap<>();
		map.put("goods_code_in", goods_code_in);
		List<PsGoods> list = psGoodsDao.selectPsGoods(map);
		List<Map<String,Object>> resList = new  ArrayList<>();
		for (PsGoods psGoods : list) {
			Map<String,Object> resMap = new HashMap<>();
			resMap.put("goods_id", psGoods.getGoods_id());
			resMap.put("goods_code", psGoods.getGoods_code());
			resMap.put("data_source", psGoods.getData_source());
			resMap.put("discount_price", psGoods.getDiscount_price());
			String pic_info = StringUtil.formatStr(psGoods.getPic_info());
			if (StringUtil.isNotBlank(pic_info)) {
				List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_info);
				String path_id = tempList.get(0).get("path_id") + "";
				String pic_info_url = docUtil.imageUrlFind(path_id);
				resMap.put("image_url", pic_info_url);
			}
			resList.add(resMap);
		}
		result.setResultData(resList);
	}
	
	@Override
	public void beiKeGoodsForListPageFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		String label_promotion = StringUtil.formatStr(paramsMap.get("label_promotion"));
		Map<String, Object> resultDataMap = new HashMap<>();
		if(StringUtil.isBlank(label_promotion)){
			Map<String, Object> temp = new HashMap<>();
			temp.put("label_type", "beikeMall");
			temp.put("status", "Y");
			List<GdGoodsLabel> label = gdGoodsLabelDao.findGoodsLabel(temp);
			resultDataMap.put("labelList", label);
			temp.clear();
		}
		// 查询可以购买的商品
		paramsMap.put("valid_thru_end", DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss));
		paramsMap.put("status", "on_shelf");
		List<BeikeMallGoods> beikeMallGoodsList = psGoodsDao.selectBeiKeGoodsList(paramsMap);
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (BeikeMallGoods psGoods : beikeMallGoodsList) {
			Map<String, Object> psGoodsMap = new HashMap<>();
			psGoodsMap.put("goods_id", psGoods.getGoods_id());
			psGoodsMap.put("title", psGoods.getTitle());
			psGoodsMap.put("sale_price", psGoods.getSale_price()); 
			psGoodsMap.put("beike_credit", psGoods.getBeike_credit());
			psGoodsMap.put("vip_price", psGoods.getVip_price());
			psGoodsMap.put("sale_qty", psGoods.getSale_qty() + "");
			psGoodsMap.put("stock_qty", psGoods.getStock_qty() + "");
			psGoodsMap.put("goods_code", psGoods.getGoods_code());
			psGoodsMap.put("desc1", psGoods.getDesc1());
			// 解析图片
			String pic_info = StringUtil.formatStr(psGoods.getPic_info());
			if (StringUtil.isNotEmpty(pic_info)) {
				List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_info);
				for (Map<String, Object> m : tempList) {
					if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
						String path_id = m.get("path_id") + "";
						String pic_info_url = docUtil.imageUrlFind(path_id);
						psGoodsMap.put("pic_info_url", pic_info_url);
						break;
					}
				}
			}
			resultList.add(psGoodsMap);
		}
		
		resultDataMap.put("list", resultList);
		result.setResultData(resultDataMap);
	}
	
	@Override
	public void beiKeGoodsForListByLabelListPageFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "label_promotion" });
		String label_promotion = paramsMap.get("label_promotion") + "";
		String key = "[beiKeGoodsForListByLabelListPageFind]" + label_promotion;
		Object obj = redisUtil.getObj(key);
		if (null != obj) {
			result.setResultData(obj);
			return;
		}
		// 查询可以购买的商品
		paramsMap.put("valid_thru_end", DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss));
		List<BeikeMallGoods> psGoodsList = psGoodsDao.selectBeiKeGoodsList(paramsMap);
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (BeikeMallGoods psGoods : psGoodsList) {
			Map<String, Object> psGoodsMap = new HashMap<String, Object>();
			psGoodsMap.put("goods_id", psGoods.getGoods_id());
			psGoodsMap.put("title", psGoods.getTitle());
			psGoodsMap.put("sale_price", psGoods.getSale_price());
			psGoodsMap.put("beike_credit", psGoods.getBeike_credit());
			psGoodsMap.put("vip_price", psGoods.getVip_price());
			psGoodsMap.put("sale_qty", psGoods.getSale_qty() + "");
			psGoodsMap.put("stock_qty", psGoods.getStock_qty() + "");
			psGoodsMap.put("goods_code", psGoods.getGoods_code());
			
			// 解析图片
			String pic_info = StringUtil.formatStr(psGoods.getPic_info());
			if (StringUtil.isNotEmpty(pic_info)) {
				List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_info);
				for (Map<String, Object> m : tempList) {
					if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
						String path_id = m.get("path_id") + "";
						String pic_info_url = docUtil.imageUrlFind(path_id);
						psGoodsMap.put("pic_info_url", pic_info_url);
						break;
					}
				}
			}
			resultList.add(psGoodsMap);
		}
		Map<String, Object> resultDataMap = new HashMap<String, Object>();
		resultDataMap.put("list", resultList);
		result.setResultData(resultDataMap);
		// 设置缓存
		redisUtil.setObj(key, resultDataMap, 10);
		freeGetGoodsListCacheKeySet.add(key);
		
	}
	
	@Override
	public void beikeMallGoodsFindForOrder(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		// goods_id 和 goods_code 必须有一个
		ValidateUtil.validateParamsNum(paramsMap, new String[] { "goods_id", "goods_code" }, 1);
		List<BeikeMallGoods> beiKeGoodsList = psGoodsDao.selectBeiKeGoodsList(paramsMap);
		if (null == beiKeGoodsList || beiKeGoodsList.isEmpty()) {
			throw new BusinessException(RspCode.PS_GOODS_NOT_EXIST, RspCode.MSG.get(RspCode.PS_GOODS_NOT_EXIST));
		}
		BeikeMallGoods beikeMallGoods = beiKeGoodsList.get(0);
		Map<String,Object> temp = new HashMap<>();
		temp.put("sku_id", paramsMap.get("sku_id"));
		GdGoodsSkuid goodsSkuid = gdGoodsSkuidDao.selectOneGdGoodsSkuid(temp);
		if (null == goodsSkuid) {
			throw new BusinessException(RspCode.PS_GOODS_NOT_EXIST, "未查询到商品对应的SKU");
		}
		temp.clear();
		temp.put("beikeMallGoods", beikeMallGoods);
		temp.put("goodsSkuidList", goodsSkuid);
		result.setResultData(temp);
	}
	
	@Override
	public void beikeMallGoodsDetailFindNew(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParamsNum(paramsMap, new String[] { "goods_id", "goods_code" }, 1);
		
		List<BeikeMallGoods> psGoodsList = psGoodsDao.selectBeiKeGoodsList(paramsMap);
		if(null ==psGoodsList  &&  psGoodsList.size() ==0 ) {
			throw new BusinessException(RspCode.PS_GOODS_NOT_EXIST, RspCode.MSG.get(RspCode.PS_GOODS_NOT_EXIST));
		}
		//加个member_id  如果memberid 不为空的话，那就是分销的，首先  会员id +goodsId 放入到redis 里面，如果已经有了，就不发送给会员接口，如果没有就发送给会员接口，然后设置有效时间
		//规定 取goods_id 
		  String member_id =  (String) paramsMap.get("member_id");
		if(StringUtils.isNotEmpty(member_id)) {
			String goods_id =  (String) paramsMap.get("goods_id");
			String key = member_id+"_"+goods_id;
			boolean   flag =redisUtil.exists(key);
			if(flag == false) {
				//设置过期时间
				redisUtil.setStr(key, key);
				String  diffSecound =  DateUtil.getTimeDiffSecound();
				redisUtil.expire(key, diffSecound);
				
				String memberIdShare =  member_id+"_"+"share";
				if(redisUtil.exists(memberIdShare) ==false) {
					redisUtil.setStr(memberIdShare, "1");
					redisUtil.expire(memberIdShare, diffSecound);
					this.handleMemberGrowhValue(member_id);
				}else {
					String incrValue =  redisUtil.getStr(memberIdShare);
					if(Integer.valueOf(incrValue) <=3) {
						redisUtil.incr(memberIdShare);
						this.handleMemberGrowhValue(member_id);
					}
				}
			}
		}
		
		List<String> doc_ids = new ArrayList<>();
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		String goods_code="";
		int count=0;
		for (BeikeMallGoods psGoods : psGoodsList) {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("goods_id", psGoods.getGoods_id());
			tempMap.put("goods_code", StringUtil.formatStr(psGoods.getGoods_code()));
			tempMap.put("title", psGoods.getTitle());
			tempMap.put("desc1", StringUtil.formatStr(psGoods.getDesc1()));
			tempMap.put("sale_price", psGoods.getSale_price());
			tempMap.put("beike_credit", psGoods.getBeike_credit());
			tempMap.put("vip_price", psGoods.getVip_price());
			tempMap.put("shipping_fee", psGoods.getShipping_fee());
			tempMap.put("pic_info", StringUtil.formatStr(psGoods.getPic_info()));
			tempMap.put("sale_qty", StringUtil.formatStr(psGoods.getSale_qty()));
			tempMap.put("pic_detail_info", StringUtil.formatStr(psGoods.getPic_detail_info()));
			tempMap.put("fake_sales_volume", StringUtil.formatStr(psGoods.getFake_sales_volume()));
			//运费界限(满79包邮8元) 后台写死,后期要改礼券专区的也要改
			tempMap.put("shipping_fee_line", 79);
			if(count==0){
				goods_code=StringUtil.formatStr(psGoods.getGoods_code());
			}
			count++;
			// 解析区域
			resultList.add(tempMap);
			// 解析图片
			String pic_info = StringUtil.formatStr(psGoods.getPic_info());
			try {
				if (!pic_info.equals("")) {
					List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_info);
					for (Map<String, Object> m : tempList) {
						if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
							doc_ids.add(m.get("path_id") + "");
						}
					}
				}
				// 解析详情图片
				String pic_detail_info = StringUtil.formatStr(psGoods.getPic_detail_info());
				if (!pic_detail_info.equals("")) {
					List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_detail_info);
					for (Map<String, Object> m : tempList) {
						if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
							doc_ids.add(m.get("path_id") + "");
						}
					}
				}
			} catch (Exception e) {
				map.put("doc_url", "");
				e.printStackTrace();
			}
		}
		map.put("doc_url", docUtil.imageUrlFind(doc_ids));
		map.put("list", resultList);
		List<Object>  list3 = null;
		if(StringUtil.isNotEmpty(goods_code)){
			Map<String, Object> skumap=new HashMap<>();
			skumap.put("goods_code", goods_code);
			List<GdGoodsSkuid>  list =gdGoodsSkuidDao.selectGdGoodsSkuidListNew(skumap);
			list3 = new ArrayList<>();
			for (GdGoodsSkuid gdGoodsSkuid : list) {
				Map<String, Object> sku = new HashMap<>();
				List<GdGoodsSkuid> list2 = null;
				if (StringUtil.isNotBlank(gdGoodsSkuid.getAttr_fid()) && !"0".equals(gdGoodsSkuid.getAttr_fid())) {
					skumap.clear();
					skumap.put("attr_zvalue", gdGoodsSkuid.getAttr_zvalue());
					skumap.put("goods_code", gdGoodsSkuid.getGoods_code());
					list2 = gdGoodsSkuidDao.selectGdGoodsSkuidListForFvaule(skumap);
				} else { 
					sku.put("stock", gdGoodsSkuid.getStock());
					sku.put("sale_price", gdGoodsSkuid.getSale_price());
					sku.put("vip_price", gdGoodsSkuid.getVip_price());
					sku.put("beike_credit", gdGoodsSkuid.getBeike_credit());
					sku.put("sku_id", gdGoodsSkuid.getSku_id());
				}
				sku.put("goods_code", gdGoodsSkuid.getGoods_code());
				sku.put("attr_zid", gdGoodsSkuid.getAttr_zid());
				sku.put("prop_zname", gdGoodsSkuid.getProp_zname());
				sku.put("attr_zvalue", gdGoodsSkuid.getAttr_zvalue());
				sku.put("attr_zpic", gdGoodsSkuid.getAttr_zpic());
				if (list2 != null && list2.size() > 0) {
					sku.put("fValue", list2);
				}
				list3.add(sku);
			}
		}
		map.put("skuList", list3);
		result.setResultData(map);
	}
	
	
	public  void  handleMemberGrowhValue(String member_id) {
		try {
			String url =PropertiesConfigUtil.getProperty("member_service_url");
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "member.memberGrowthValue");
			Map<String, String> params = new HashMap<String, String>();
			params.put("member_id", member_id);
			params.put("growth_value", "5");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			Map<String, Object> resultMap = FastJsonUtil.jsonToMap(result);
			if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
				throw new BusinessException((String) resultMap.get("error_code"),
						(String) resultMap.get("error_msg"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	@Override
	public void handleBeikeMallGoodsSaleQtyForOwnRestore(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "goods_id","goods_code","restore_qty" ,"sku_id" });
		//第一步先恢复商品主表可销售库存
		int updateFlag = psGoodsDao.updateBeikeMallGoodsSaleQtyForOwnRestore(paramsMap);
	    //第二步恢复对应商品规格表中的库存
		int updateSkuFlag = gdGoodsSkuidDao.updatePsGoodsSkuidStockForOwnRestore(paramsMap);
		
		if (updateFlag == 0 || updateSkuFlag == 0) {
			throw new BusinessException(RspCode.PS_GOODS_NOT_EXIST, "系统繁忙,请重新操作");
		}
		paramsMap.put("sell_qty", "-" + paramsMap.get("restore_qty"));
		gdSellAdd(paramsMap, new ResultData());
	}

	@Override
	public void hongbaoGoodsForListPageFind(Map<String, Object> paramsMap, ResultData result) throws Exception {
		Map<String, Object> resultDataMap = new HashMap<>();
		String label_promotion = StringUtil.formatStr(paramsMap.get("label_promotion"));
		if(StringUtil.isBlank(label_promotion)){
			Map<String, Object> temp = new HashMap<>();
			temp.put("label_type", "liBao");
			temp.put("status", "Y");
			List<GdGoodsLabel> label = gdGoodsLabelDao.findGoodsLabel(temp);
			resultDataMap.put("labelList", label);
			temp.clear();
			paramsMap.put("label_promotion", label.get(0).getLabel_id());
		}
		// 查询可以购买的商品
		paramsMap.put("valid_thru_end", DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss));//开卖时间
		paramsMap.put("status", "on_shelf");//上架商品
		List<HongBaoGoods> hongbaoGoodsList = hongbaoGoodsDao.selectHongbaoGoodsList(paramsMap);
		if (hongbaoGoodsList.isEmpty() || hongbaoGoodsList.size() == 0) {
			resultDataMap.put("list", hongbaoGoodsList);
			result.setResultData(resultDataMap);
			return;
		}
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		for (HongBaoGoods psGoods : hongbaoGoodsList) {
			Map<String, Object> psGoodsMap = new HashMap<>();
			psGoodsMap.put("goods_id", psGoods.getGoods_id());
			psGoodsMap.put("title", psGoods.getTitle());
			psGoodsMap.put("hongbao_price", psGoods.getHongbao_price());
			psGoodsMap.put("market_price", psGoods.getMarket_price());
			psGoodsMap.put("sale_qty", psGoods.getSale_qty() + "");
			psGoodsMap.put("stock_qty", psGoods.getStock_qty() + "");
			psGoodsMap.put("goods_code", psGoods.getGoods_code());
			psGoodsMap.put("desc1", psGoods.getDesc1());
			psGoodsMap.put("coupon_price", psGoods.getCoupon_price());
			psGoodsMap.put("sale_price", psGoods.getSale_price());
			// 解析图片
			String pic_info = StringUtil.formatStr(psGoods.getPic_info());
			if (StringUtil.isNotEmpty(pic_info)) {
				List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_info);
				for (Map<String, Object> m : tempList) {
					if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
						String path_id = m.get("path_id") + "";
						String pic_info_url = docUtil.imageUrlFind(path_id);
						psGoodsMap.put("pic_info_url", pic_info_url);
						break;
					}
				}
			}
			resultList.add(psGoodsMap);
		}
		
		resultDataMap.put("list", resultList);
		result.setResultData(resultDataMap);
	}
	
	@Override
	public void hongbaoGoodsDetailFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParamsNum(paramsMap, new String[] { "goods_id", "goods_code" }, 1);
		List<HongBaoGoods> hongbaoGoodsList = hongbaoGoodsDao.selectHongbaoGoodsList(paramsMap);
		if (null == hongbaoGoodsList || hongbaoGoodsList.isEmpty()) {
			throw new BusinessException(RspCode.ITEM_STORE_NOT_EXIST, RspCode.MSG.get(RspCode.ITEM_STORE_NOT_EXIST));
		}
		List<String> doc_ids = new ArrayList<>();
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		for (HongBaoGoods psGoods : hongbaoGoodsList) {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("goods_id", psGoods.getGoods_id());
			tempMap.put("goods_code", StringUtil.formatStr(psGoods.getGoods_code()));
			tempMap.put("title", psGoods.getTitle());
			tempMap.put("desc1", StringUtil.formatStr(psGoods.getDesc1()));
			tempMap.put("hongbao_price", psGoods.getHongbao_price());
			tempMap.put("market_price", psGoods.getMarket_price());
			tempMap.put("sale_qty", psGoods.getSale_qty());
			tempMap.put("shipping_fee", psGoods.getShipping_fee());
			tempMap.put("pic_info", StringUtil.formatStr(psGoods.getPic_info()));
			tempMap.put("pic_detail_info", StringUtil.formatStr(psGoods.getPic_detail_info()));
			tempMap.put("shipping_fee_line","79");
			tempMap.put("fake_sales_volume",StringUtil.formatStr(psGoods.getFake_sales_volume()));
			tempMap.put("coupon_price",psGoods.getCoupon_price());
			// 解析区域
			resultList.add(tempMap);
			// 解析图片
			String pic_info = StringUtil.formatStr(psGoods.getPic_info());
			try {
				if (!pic_info.equals("")) {
					List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_info);
					for (Map<String, Object> m : tempList) {
						if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
							doc_ids.add(m.get("path_id") + "");
						}
					}
				}
				// 解析详情图片
				String pic_detail_info = StringUtil.formatStr(psGoods.getPic_detail_info());
				if (!pic_detail_info.equals("")) {
					List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_detail_info);
					for (Map<String, Object> m : tempList) {
						if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
							doc_ids.add(m.get("path_id") + "");
						}
					}
				}
			} catch (Exception e) {
				map.put("doc_url", "");
				e.printStackTrace();
			}
		}
		map.put("doc_url", docUtil.imageUrlFind(doc_ids));
		map.put("list", resultList);
		result.setResultData(map);
	}

	@Override
	public void hongbaoGoodsSaleQtyUpdate(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "goods_code","sell_qty"});
		Integer i = hongbaoGoodsDao.updateHongBaoGoodsSaleQty(paramsMap);
		if(i==0){
			throw new BusinessException(RspCode.PS_GOODS_NOT_EXIST, RspCode.PS_GOODS_NOT_EXIST);
		}
	}

	@Override
	public void beikeMallGoodsSaleQtyDeduction(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "goods_id", "sell_qty" });
		String sku_id = (String) paramsMap.get("sku_id");
		String sell_qty = (String) paramsMap.get("sell_qty");
		int updateFlag = psGoodsDao.updateBeikeMallGoodsSaleQtyDeduction(paramsMap);
		if (updateFlag == 0) {
			throw new BusinessException(RspCode.PROCESSING, "可售库存不足");
		}
		if (sku_id != null && StringUtil.isNotBlank(sku_id)) {
			Integer flag = psGoodsDao.updatePsGoodsSkuStockDeduction(paramsMap);
			if (flag == 0) {
				throw new BusinessException(RspCode.PROCESSING, "可售库存不足");
			}
		}
		if(!sell_qty.contains("-")){
			gdSellAdd(paramsMap, new ResultData());
		}
	}
	
	@Override
	public void beikeMallGoodsDetailFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParamsNum(paramsMap, new String[] { "goods_id", "goods_code" }, 1);
		
		List<BeikeMallGoods> psGoodsList = psGoodsDao.selectBeiKeGoodsList(paramsMap);
		if(null ==psGoodsList  &&  psGoodsList.size() ==0 ) {
			throw new BusinessException(RspCode.PS_GOODS_NOT_EXIST, RspCode.MSG.get(RspCode.PS_GOODS_NOT_EXIST));
		}
		//加个member_id  如果memberid 不为空的话，那就是分销的，首先  会员id +goodsId 放入到redis 里面，如果已经有了，就不发送给会员接口，如果没有就发送给会员接口，然后设置有效时间
		//规定 取goods_id 
		  String member_id =  (String) paramsMap.get("member_id");
		if(StringUtils.isNotEmpty(member_id)) {
			String goods_id =  (String) paramsMap.get("goods_id");
			String key = member_id+"_"+goods_id;
			boolean   flag =redisUtil.exists(key);
			if(flag == false) {
				//设置过期时间
				redisUtil.setStr(key, key);
				String  diffSecound =  DateUtil.getTimeDiffSecound();
				redisUtil.expire(key, diffSecound);
				
				String memberIdShare =  member_id+"_"+"share";
				if(redisUtil.exists(memberIdShare) ==false) {
					redisUtil.setStr(memberIdShare, "1");
					redisUtil.expire(memberIdShare, diffSecound);
					this.handleMemberGrowhValue(member_id);
				}else {
					String incrValue =  redisUtil.getStr(memberIdShare);
					if(Integer.valueOf(incrValue) <=3) {
						redisUtil.incr(memberIdShare);
						this.handleMemberGrowhValue(member_id);
					}
				}
			}
		}
		
		List<String> doc_ids = new ArrayList<>();
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		String goods_code="";
		int count=0;
		for (BeikeMallGoods psGoods : psGoodsList) {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("goods_id", psGoods.getGoods_id());
			tempMap.put("goods_code", StringUtil.formatStr(psGoods.getGoods_code()));
			tempMap.put("title", psGoods.getTitle());
			tempMap.put("desc1", StringUtil.formatStr(psGoods.getDesc1()));
			tempMap.put("sale_price", psGoods.getSale_price());
			tempMap.put("beike_credit", psGoods.getBeike_credit());
			tempMap.put("vip_price", psGoods.getVip_price());
			tempMap.put("shipping_fee", psGoods.getShipping_fee());
			tempMap.put("pic_info", StringUtil.formatStr(psGoods.getPic_info()));
			tempMap.put("pic_detail_info", StringUtil.formatStr(psGoods.getPic_detail_info()));
			if(count==0){
				goods_code=StringUtil.formatStr(psGoods.getGoods_code());
			}
			count++;
			// 解析区域
			resultList.add(tempMap);
			// 解析图片
			String pic_info = StringUtil.formatStr(psGoods.getPic_info());
			try {
				if (!pic_info.equals("")) {
					List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_info);
					for (Map<String, Object> m : tempList) {
						if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
							doc_ids.add(m.get("path_id") + "");
						}
					}
				}
				// 解析详情图片
				String pic_detail_info = StringUtil.formatStr(psGoods.getPic_detail_info());
				if (!pic_detail_info.equals("")) {
					List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_detail_info);
					for (Map<String, Object> m : tempList) {
						if (!StringUtil.formatStr(m.get("path_id")).equals("")) {
							doc_ids.add(m.get("path_id") + "");
						}
					}
				}
			} catch (Exception e) {
				map.put("doc_url", "");
				e.printStackTrace();
			}
		}
		map.put("doc_url", docUtil.imageUrlFind(doc_ids));
		map.put("list", resultList);
		
		if(StringUtil.isNotEmpty(goods_code)){
			Map<String, Object> skumap=new HashMap<String, Object>();
			skumap.put("goods_code", goods_code);
			List<GdGoodsSkuid>  list=gdGoodsSkuidDao.selectGdGoodsSkuidList(skumap);
			if(list==null){
				list=new ArrayList<GdGoodsSkuid>();
			}
			map.put("goodsSkuList", list);
		}
		
		result.setResultData(map);
	}

	@Override
	public void beikeMallGoodsSalesVolumeUpdate(Map<String, Object> paramsMap, ResultData result)throws Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "goods_id", "item_num" });
		int i = beiKeMallGoodsDao.beikeMallGoodsSalesVolumeUpdate(paramsMap);
	}

	@Override
	public void hongBaoGoodsSalesVolumeUpdate(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "goods_id", "sales_volume" });
		int i = hongbaoGoodsDao.updateSalesVolume(paramsMap);
	}
}
