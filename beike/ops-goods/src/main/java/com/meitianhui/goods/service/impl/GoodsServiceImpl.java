package com.meitianhui.goods.service.impl;

import java.math.BigDecimal;
import java.util.*;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.meitianhui.goods.dao.*;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;
import com.meitianhui.common.util.BeanConvertUtil;
import com.meitianhui.common.util.DateUtil;
import com.meitianhui.common.util.DocUtil;
import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.common.util.HttpClientUtil;
import com.meitianhui.common.util.IDUtil;
import com.meitianhui.common.util.MoneyUtil;
import com.meitianhui.common.util.PropertiesConfigUtil;
import com.meitianhui.common.util.StringUtil;
import com.meitianhui.common.util.ValidateUtil;
import com.meitianhui.goods.constant.Constant;
import com.meitianhui.goods.constant.RspCode;
import com.meitianhui.goods.entity.GdSysitemItem;
import com.meitianhui.goods.entity.GdSysitemItemCouponProp;
import com.meitianhui.goods.entity.GdSysitemItemSku;
import com.meitianhui.goods.service.GoodsService;

/**
 * 商品信息
 * 
 * @ClassName: GoodsServiceImpl
 * @author tiny
 * @date 2017年2月21日 上午11:37:35
 *
 */
@SuppressWarnings("unchecked")
@Service
public class GoodsServiceImpl implements GoodsService {

	private static final Logger logger = Logger.getLogger(GoodsServiceImpl.class);

	@Autowired
	private DocUtil docUtil;

	@Autowired
	public GoodsDao goodsDao;

	@Autowired
	public PsGoodsDao psGoodsDao;
	@Autowired
	public PsGoodsActivityDao psGoodsActivityDao;

	@Autowired
	public LdActivityDao ldActivityDao;
	@Autowired
	public GdAppAdvertDao gdAppAdvertDao;

	@Override
	public void sysitemItemStatusEdit(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		goodsDao.updateGdSysitemItem(paramsMap);
	}

	@Override
	public void handleCouponIssueApply(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap,
				new String[] { "category_id", "title", "member_type_key", "member_id", "cost_price", "market_price",
						"vip_price", "market_price_voucher", "market_price_gold", "market_price_bonus", "coupon_prop",
						"voucher_amount", "settle_price", "limit_amount", "issued_num", "pic_path", "expired_date",
						"is_refund_anytime", "is_refund_expired" });
		String member_type_key = StringUtil.formatStr(paramsMap.get("member_type_key"));
		if (member_type_key.equals(Constant.MEMBER_TYPE_STORES)) {
			// 手动补全门店信息
			String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
			Map<String, String> reqParams = new HashMap<String, String>();
			Map<String, Object> bizParams = new HashMap<String, Object>();
			reqParams.put("service", "member.storeFind");
			bizParams.put("stores_id", paramsMap.get("member_id"));
			reqParams.put("params", FastJsonUtil.toJson(bizParams));
			String resultStr = HttpClientUtil.post(member_service_url, reqParams);
			Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
			if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
				throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
			}
			Map<String, Object> storeInfo = (Map<String, Object>) resultMap.get("data");
			paramsMap.put("member_info", StringUtil.formatStr(storeInfo.get("stores_name")));
		}
		Date created_date = new Date();
		GdSysitemItem gdSysitemItem = new GdSysitemItem();
		BeanConvertUtil.mapToBean(gdSysitemItem, paramsMap);
		gdSysitemItem.setStatus(Constant.STATUS_ON_SHELF);
		gdSysitemItem.setCreated_date(created_date);
		String item_id = IDUtil.getUUID();
		gdSysitemItem.setItem_id(item_id);
		gdSysitemItem.setItem_code(item_id);
		gdSysitemItem.setBarcode(IDUtil.getShortUUID());
		gdSysitemItem.setCreated_date(created_date);
		gdSysitemItem.setStock_qty(Integer.parseInt((String) paramsMap.get("issued_num")));
		goodsDao.insertGdSysitemItem(gdSysitemItem);
		GdSysitemItemCouponProp gdSysitemItemCouponProp = new GdSysitemItemCouponProp();
		BeanConvertUtil.mapToBean(gdSysitemItemCouponProp, paramsMap);
		if (gdSysitemItemCouponProp.getPer_limit() == null) {
			gdSysitemItemCouponProp.setPer_limit(0);
		}
		// 礼券值为0
		gdSysitemItemCouponProp.setVoucher_amount(0);
		gdSysitemItemCouponProp.setOnsell_num(gdSysitemItemCouponProp.getIssued_num());
		gdSysitemItemCouponProp.setUsed_num(0);
		gdSysitemItemCouponProp.setVerified_num(0);
		gdSysitemItemCouponProp.setOffsell_num(0);
		gdSysitemItemCouponProp.setRevoked_num(0);
		gdSysitemItemCouponProp.setUsable_num(gdSysitemItemCouponProp.getIssued_num());
		gdSysitemItemCouponProp.setItem_id(item_id);
		gdSysitemItemCouponProp.setOnsell_date(created_date);
		gdSysitemItemCouponProp.setIssued_date(created_date);
		gdSysitemItemCouponProp.setCreated_date(created_date);
		gdSysitemItemCouponProp.setModified_date(created_date);
		goodsDao.insertGdSysitemItemCouponProp(gdSysitemItemCouponProp);
	}

	@Override
	public void couponAndStoreFindForShare(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "member_id", "item_id" });
		paramsMap.put("status", Constant.STATUS_ON_SHELF);
		List<Map<String, Object>> couponList = goodsDao.selectCouponItem(paramsMap);
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		List<String> pic_path_list = new ArrayList<String>();
		for (Map<String, Object> map : couponList) {
			if (StringUtil.formatStr(map.get("category_id")).equals(Constant.COUPON_CATEGORY_VOUCHER)) {
				continue;
			}
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("category_id", map.get("category_id"));
			tempMap.put("item_id", map.get("item_id"));
			tempMap.put("title", map.get("title"));
			tempMap.put("member_type_key", map.get("member_type_key"));
			tempMap.put("member_id", map.get("member_id"));
			tempMap.put("desc1", StringUtil.formatStr(map.get("desc1")));
			tempMap.put("pic_path", map.get("pic_path"));
			tempMap.put("coupon_prop", map.get("coupon_prop") + "");
			tempMap.put("market_price", map.get("market_price") + "");
			tempMap.put("market_price_voucher", map.get("market_price_voucher") + "");
			tempMap.put("voucher_amount", map.get("voucher_amount") + "");
			tempMap.put("limit_amount", map.get("limit_amount") + "");
			tempMap.put("per_limit", map.get("per_limit") + "");
			tempMap.put("voucher_amount", map.get("voucher_amount") + "");
			tempMap.put("expired_date", DateUtil.date2Str((Date) map.get("expired_date"), DateUtil.fmt_yyyyMMddHHmmss));
			tempMap.put("issued_date", DateUtil.date2Str((Date) map.get("issued_date"), DateUtil.fmt_yyyyMMddHHmmss));
			tempMap.put("onsell_num", map.get("onsell_num") + "");
			tempMap.put("used_num", map.get("used_num") + "");
			tempMap.put("usable_num", map.get("usable_num") + "");
			tempMap.put("is_refund_anytime", map.get("is_refund_anytime") + "");
			tempMap.put("is_refund_expired", map.get("is_refund_expired") + "");
			pic_path_list.addAll(StringUtil.str2List((String) map.get("pic_path"), ","));
			StringBuffer buf = new StringBuffer();
			if (map.get("category_id").equals(Constant.COUPON_CATEGORY_CASH)) {
				buf.append("进店消费后可凭此券抵扣现金" + map.get("coupon_prop") + "元\n");
			} else if (map.get("category_id").equals(Constant.COUPON_CATEGORY_VOUCHER)) {
				buf.append("进店使用此券可以兑换" + map.get("coupon_prop") + "\n");
			}
			Integer voucher_amount = (Integer) map.get("voucher_amount");
			if (voucher_amount > 0) {
				buf.append("消费后,可获得商家赠送的" + map.get("voucher_amount") + "礼券\n");
			}
			buf.append(map.get("desc1"));
			tempMap.put("coupon_desc", buf.toString());
			String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
			Map<String, String> reqParams = new HashMap<String, String>();
			Map<String, Object> paramMap = new HashMap<String, Object>();
			reqParams.put("service", "member.storeFind");
			paramMap.put("stores_id", map.get("member_id"));
			reqParams.put("params", FastJsonUtil.toJson(paramMap));
			String resultStr = HttpClientUtil.post(member_service_url, reqParams);
			Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
			if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
				throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
			}
			Map<String, Object> storeInfo = (Map<String, Object>) resultMap.get("data");
			tempMap.put("stores_name", StringUtil.formatStr(storeInfo.get("stores_name")));
			tempMap.put("stores_type_key", StringUtil.formatStr(storeInfo.get("stores_type_key")));
			tempMap.put("business_type_key", StringUtil.formatStr(storeInfo.get("business_type_key")));
			tempMap.put("contact_person", StringUtil.formatStr(storeInfo.get("contact_person")));
			tempMap.put("contact_tel", StringUtil.formatStr(storeInfo.get("contact_tel")));
			tempMap.put("desc1", StringUtil.formatStr(storeInfo.get("desc1")));
			tempMap.put("label", StringUtil.formatStr(storeInfo.get("label")));
			tempMap.put("address", StringUtil.formatStr(storeInfo.get("address")));
			tempMap.put("area_id", StringUtil.formatStr(storeInfo.get("area_id")));
			tempMap.put("logo_pic_path", StringUtil.formatStr(storeInfo.get("logo_pic_path")));
			tempMap.put("neighbor_pic_path", StringUtil.formatStr(storeInfo.get("neighbor_pic_path")));
			pic_path_list.addAll(StringUtil.str2List(StringUtil.formatStr(storeInfo.get("logo_pic_path")), ","));
			pic_path_list.addAll(StringUtil.str2List(StringUtil.formatStr(storeInfo.get("neighbor_pic_path")), ","));
			resultList.add(tempMap);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("doc_url", docUtil.imageUrlFind(pic_path_list));
		map.put("list", resultList);
		result.setResultData(map);
	}

	/**
	 * 消费者查询店东的优惠券列表
	 * 
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	@Override
	public void couponListFindForConsumer(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		/*ValidateUtil.validateParams(paramsMap, new String[] { "stores_id" });
		// 转换查询条件
		paramsMap.put("member_id", paramsMap.get("stores_id"));
		paramsMap.put("member_type_key", "stores");
		paramsMap.put("status", Constant.STATUS_ON_SHELF);
		paramsMap.remove("stores_id");
		List<Map<String, Object>> couponList = goodsDao.selectCouponItem(paramsMap);
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		List<String> pic_path_list = new ArrayList<String>();
		for (Map<String, Object> map : couponList) {
			if (StringUtil.formatStr(map.get("category_id")).equals(Constant.COUPON_CATEGORY_VOUCHER)) {
				continue;
			}
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("category_id", map.get("category_id"));
			tempMap.put("item_id", map.get("item_id"));
			tempMap.put("title", map.get("title"));
			tempMap.put("member_type_key", map.get("member_type_key"));
			tempMap.put("member_id", map.get("member_id"));
			tempMap.put("desc1", StringUtil.formatStr(map.get("desc1")));
			tempMap.put("coupon_prop", map.get("coupon_prop") + "");
			tempMap.put("expired_date", DateUtil.date2Str((Date) map.get("expired_date"), DateUtil.fmt_yyyyMMddHHmmss));
			tempMap.put("onsell_num", map.get("onsell_num") + "");
			tempMap.put("used_num", map.get("used_num") + "");
			tempMap.put("usable_num", map.get("usable_num") + "");
			tempMap.put("is_refund_anytime", map.get("is_refund_anytime") + "");
			tempMap.put("is_refund_expired", map.get("is_refund_expired") + "");
			pic_path_list.addAll(StringUtil.str2List((String) map.get("pic_path"), ","));
			resultList.add(tempMap);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", resultList);*/
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", new ArrayList<>());
		result.setResultData(map);
	}

	/**
	 * 消费者查询店东的优惠券列表
	 * 
	 * @param paramsMap
	 * @param result
	 * @throws BusinessException
	 * @throws SystemException
	 */
	@Override
	public void couponListFindForStores(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "stores_id" });
		// 转换查询条件
		paramsMap.put("member_id", paramsMap.get("stores_id"));
		paramsMap.put("member_type_key", "stores");
		paramsMap.remove("stores_id");
		List<Map<String, Object>> couponList = goodsDao.selectCouponItem(paramsMap);
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		List<String> pic_path_list = new ArrayList<String>();
		for (Map<String, Object> map : couponList) {
			if (StringUtil.formatStr(map.get("category_id")).equals(Constant.COUPON_CATEGORY_VOUCHER)) {
				continue;
			}
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("category_id", map.get("category_id"));
			tempMap.put("item_id", map.get("item_id"));
			tempMap.put("title", map.get("title"));
			tempMap.put("coupon_prop", map.get("coupon_prop") + "");
			tempMap.put("expired_date", DateUtil.date2Str((Date) map.get("expired_date"), DateUtil.fmt_yyyyMMddHHmmss));
			tempMap.put("onsell_num", map.get("onsell_num") + "");
			tempMap.put("used_num", map.get("used_num") + "");
			tempMap.put("usable_num", map.get("usable_num") + "");
			tempMap.put("issued_num", map.get("issued_num") + "");
			tempMap.put("verified_num", map.get("verified_num") + "");
			tempMap.put("is_refund_anytime", map.get("is_refund_anytime") + "");
			tempMap.put("is_refund_expired", map.get("is_refund_expired") + "");
			tempMap.put("status", map.get("status") + "");
			pic_path_list.addAll(StringUtil.str2List((String) map.get("pic_path"), ","));
			resultList.add(tempMap);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", resultList);
		result.setResultData(map);
	}

	@Override
	public void couponDetailForConsumerFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "item_id" });
		paramsMap.put("status", Constant.STATUS_ON_SHELF);
		List<Map<String, Object>> couponList = goodsDao.selectCouponItem(paramsMap);
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		List<String> pic_path_list = new ArrayList<String>();
		Map<String, Object> tempMap = new HashMap<String, Object>();
		if (couponList.size() > 0) {
			Map<String, Object> map = couponList.get(0);
			if (!StringUtil.formatStr(map.get("category_id")).equals(Constant.COUPON_CATEGORY_VOUCHER)) {
				tempMap.put("category_id", map.get("category_id"));
				tempMap.put("item_id", map.get("item_id"));
				tempMap.put("title", map.get("title"));
				tempMap.put("member_type_key", map.get("member_type_key"));
				tempMap.put("member_id", map.get("member_id"));
				tempMap.put("desc1", StringUtil.formatStr(map.get("desc1")));
				tempMap.put("pic_path", map.get("pic_path"));
				tempMap.put("coupon_prop", map.get("coupon_prop") + "");
				tempMap.put("market_price", map.get("market_price") + "");
				tempMap.put("limit_amount", map.get("limit_amount") + "");
				tempMap.put("per_limit", map.get("per_limit") + "");
				tempMap.put("expired_date",
						DateUtil.date2Str((Date) map.get("expired_date"), DateUtil.fmt_yyyyMMddHHmmss));
				tempMap.put("onsell_num", map.get("onsell_num") + "");
				tempMap.put("usable_num", map.get("usable_num") + "");
				tempMap.put("is_refund_anytime", map.get("is_refund_anytime") + "");
				tempMap.put("is_refund_expired", map.get("is_refund_expired") + "");
				StringBuffer buf = new StringBuffer();
				if (map.get("category_id").equals(Constant.COUPON_CATEGORY_CASH)) {
					buf.append("进店消费后可凭此券抵扣现金" + map.get("coupon_prop") + "元\n");
				}
				buf.append(map.get("desc1"));
				tempMap.put("coupon_desc", buf.toString());
				pic_path_list.addAll(StringUtil.str2List((String) map.get("pic_path"), ","));
				resultList.add(tempMap);
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("doc_url", docUtil.imageUrlFind(pic_path_list));
		map.put("detail", resultList);
		result.setResultData(map);
	}

	@Override
	public void couponDetailForStoresFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "item_id" });
		List<Map<String, Object>> couponList = goodsDao.selectCouponItem(paramsMap);
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		List<String> pic_path_list = new ArrayList<String>();
		Map<String, Object> tempMap = new HashMap<String, Object>();
		if (couponList.size() > 0) {
			Map<String, Object> map = couponList.get(0);
			tempMap.put("item_id", map.get("item_id"));
			tempMap.put("title", map.get("title"));
			tempMap.put("category_id", map.get("category_id"));
			tempMap.put("desc1", StringUtil.formatStr(map.get("desc1")));
			tempMap.put("pic_path", map.get("pic_path"));
			tempMap.put("market_price", map.get("market_price") + "");
			tempMap.put("coupon_prop", map.get("coupon_prop") + "");
			tempMap.put("market_price", map.get("market_price") + "");
			tempMap.put("market_price_gold", map.get("market_price_gold") + "");
			tempMap.put("market_price_bonus", map.get("market_price_bonus") + "");
			tempMap.put("expired_date", DateUtil.date2Str((Date) map.get("expired_date"), DateUtil.fmt_yyyyMMddHHmmss));
			tempMap.put("issued_date", DateUtil.date2Str((Date) map.get("issued_date"), DateUtil.fmt_yyyyMMddHHmmss));
			tempMap.put("status", map.get("status") + "");
			tempMap.put("remark", StringUtil.formatStr(map.get("remark")));
			tempMap.put("coupon_prop", map.get("coupon_prop") + "");
			tempMap.put("voucher_amount", map.get("voucher_amount") + "");
			tempMap.put("settle_price", map.get("settle_price") + "");
			tempMap.put("limit_amount", map.get("limit_amount") + "");
			tempMap.put("per_limit", map.get("per_limit") + "");
			tempMap.put("issued_num", map.get("issued_num") + "");
			tempMap.put("onsell_num", map.get("onsell_num") + "");
			tempMap.put("used_num", map.get("used_num") + "");
			tempMap.put("verified_num", map.get("verified_num") + "");
			tempMap.put("usable_num", map.get("usable_num") + "");
			tempMap.put("is_refund_anytime", map.get("is_refund_anytime") + "");
			tempMap.put("is_refund_expired", map.get("is_refund_expired") + "");
			pic_path_list.addAll(StringUtil.str2List((String) map.get("pic_path"), ","));
			resultList.add(tempMap);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("doc_url", docUtil.imageUrlFind(pic_path_list));
		map.put("detail", resultList);
		result.setResultData(map);
	}

	@Override
	public void couponDetailFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		Map<String, Object> couponDetailMap = new HashMap<String, Object>();
		ValidateUtil.validateParams(paramsMap, new String[] { "sku_id" });
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("sku_id", paramsMap.get("sku_id"));
		Map<String, Object> map = goodsDao.selectCouponSkuCode(tempMap);
		if (map == null) {
			throw new BusinessException(RspCode.COUPON_NOT_EXIST, RspCode.MSG.get(RspCode.COUPON_NOT_EXIST));
		}
		couponDetailMap.put("member_type_key", map.get("member_type_key"));
		couponDetailMap.put("member_id", map.get("member_id"));
		couponDetailMap.put("category_id", map.get("category_id"));
		couponDetailMap.put("title", map.get("title"));
		couponDetailMap.put("desc1", StringUtil.formatStr(map.get("desc1")));
		couponDetailMap.put("pic_path", StringUtil.formatStr(map.get("pic_path")));
		couponDetailMap.put("market_price", map.get("market_price") + "");
		couponDetailMap.put("market_price_voucher", map.get("market_price_voucher") + "");
		couponDetailMap.put("market_price_gold", map.get("market_price_gold") + "");
		couponDetailMap.put("market_price_bonus", map.get("market_price_bonus") + "");
		couponDetailMap.put("coupon_prop", map.get("coupon_prop") + "");
		couponDetailMap.put("item_id", map.get("item_id"));
		couponDetailMap.put("sku_code", map.get("sku_code"));
		couponDetailMap.put("coupon_prop", map.get("coupon_prop") + "");
		couponDetailMap.put("voucher_amount", map.get("voucher_amount") + "");
		couponDetailMap.put("limit_amount", map.get("limit_amount") + "");
		couponDetailMap.put("voucher_amount", map.get("voucher_amount") + "");
		couponDetailMap.put("is_refund_anytime", map.get("is_refund_anytime") + "");
		couponDetailMap.put("is_refund_expired", map.get("is_refund_expired") + "");
		couponDetailMap.put("expired_date",
				DateUtil.date2Str((Date) map.get("expired_date"), DateUtil.fmt_yyyyMMddHHmmss));

		StringBuffer buf = new StringBuffer();
		if (map.get("category_id").equals(Constant.COUPON_CATEGORY_CASH)) {
			buf.append("进店消费后可凭此券抵扣现金" + map.get("coupon_prop") + "元\n");
		} else if (map.get("category_id").equals(Constant.COUPON_CATEGORY_VOUCHER)) {
			buf.append("进店使用此券可以兑换" + map.get("coupon_prop") + "\n");
		}
		Integer voucher_amount = (Integer) map.get("voucher_amount");
		if (voucher_amount > 0) {
			buf.append("消费后,可获得商家赠送的" + map.get("voucher_amount") + "礼券\n");
		}
		buf.append(map.get("desc1"));
		couponDetailMap.put("coupon_desc", buf.toString());

		String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
		Map<String, String> reqParams = new HashMap<String, String>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		reqParams.put("service", "member.storeForOrderFind");
		paramMap.put("stores_id", map.get("member_id"));
		reqParams.put("params", FastJsonUtil.toJson(paramMap));
		String resultStr = HttpClientUtil.post(member_service_url, reqParams);
		Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
		if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
			throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
		}
		Map<String, Object> storeInfo = (Map<String, Object>) resultMap.get("data");
		couponDetailMap.put("stores_name", StringUtil.formatStr(storeInfo.get("stores_name")));
		couponDetailMap.put("stores_type_key", StringUtil.formatStr(storeInfo.get("stores_type_key")));
		couponDetailMap.put("business_type_key", StringUtil.formatStr(storeInfo.get("business_type_key")));
		couponDetailMap.put("contact_person", StringUtil.formatStr(storeInfo.get("contact_person")));
		couponDetailMap.put("contact_tel", StringUtil.formatStr(storeInfo.get("contact_tel")));
		couponDetailMap.put("address", StringUtil.formatStr(storeInfo.get("address")));
		couponDetailMap.put("area_id", StringUtil.formatStr(storeInfo.get("area_id")));
		couponDetailMap.put("longitude", StringUtil.formatStr(storeInfo.get("longitude")));
		couponDetailMap.put("latitude", StringUtil.formatStr(storeInfo.get("latitude")));
		couponDetailMap.put("logo_pic_path", StringUtil.formatStr(storeInfo.get("logo_pic_path")));
		couponDetailMap.put("label", StringUtil.formatStr(storeInfo.get("label")));
		result.setResultData(couponDetailMap);
	}

	@Override
	public void handleCouponPresented(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "member_id", "member_type_key", "item_id", "num" });
		String item_id = paramsMap.get("item_id") + "";
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("item_id", item_id);
		List<Map<String, Object>> couponList = goodsDao.selectCouponItem(tempMap);
		if (couponList.size() == 0) {
			throw new BusinessException(RspCode.COUPON_NOT_EXIST, RspCode.MSG.get(RspCode.COUPON_NOT_EXIST));
		}
		Map<String, Object> couponMap = couponList.get(0);
		String category_id = couponMap.get("category_id") + "";
		if (category_id.equals(Constant.COUPON_CATEGORY_CASH)) {
			throw new BusinessException(RspCode.COUPON_CATEGORY_ERROR, "现金购买型不容许派发");
		}
		Integer num = Integer.parseInt(paramsMap.get("num") + "");
		// 修改优惠券库存
		tempMap.clear();
		tempMap.put("item_id", item_id);
		tempMap.put("used_num", num + "");
		int updateFlag = goodsDao.updateGdSysitemItemCouponProp(tempMap);
		if (updateFlag == 0) {
			throw new BusinessException(RspCode.PP_ACTIVITY_ERROR, "操作失败,请刷新后重试");
		}
		GdSysitemItemSku gdSysitemItemSku = new GdSysitemItemSku();
		gdSysitemItemSku.setStatus(Constant.STATUS_ACTIVATED);
		gdSysitemItemSku.setCreated_date(new Date());
		gdSysitemItemSku.setSku_id(IDUtil.getUUID());
		gdSysitemItemSku.setSku_code(IDUtil.generateCode(16));
		gdSysitemItemSku.setItem_id(item_id);
		gdSysitemItemSku.setCost_price((BigDecimal) couponMap.get("cost_price"));
		gdSysitemItemSku.setMarket_price((BigDecimal) couponMap.get("market_price"));
		gdSysitemItemSku.setVip_price((BigDecimal) couponMap.get("vip_price"));
		gdSysitemItemSku.setMarket_price_bonus((Integer) couponMap.get("market_price_bonus"));
		gdSysitemItemSku.setMarket_price_gold((Integer) couponMap.get("market_price_gold"));
		gdSysitemItemSku.setMarket_price_bonus((Integer) couponMap.get("market_price_bonus"));
		gdSysitemItemSku.setTitle((String) couponMap.get("title"));
		gdSysitemItemSku.setExpired_date((Date) couponMap.get("expired_date"));
		gdSysitemItemSku.setRemark("店东赠送");
		// 消费者增加优惠券信息
		String finance_service_url = PropertiesConfigUtil.getProperty("finance_service_url");
		Map<String, String> reqParams = new HashMap<String, String>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		reqParams.put("service", "finance.memberCouponCreate");
		paramMap.put("member_id", paramsMap.get("member_id"));
		paramMap.put("member_type_key", paramsMap.get("member_type_key"));
		paramMap.put("expired_date",
				DateUtil.date2Str(gdSysitemItemSku.getExpired_date(), DateUtil.fmt_yyyyMMddHHmmss));
		paramMap.put("status", gdSysitemItemSku.getStatus());
		paramMap.put("item_id", gdSysitemItemSku.getItem_id());
		paramMap.put("title", gdSysitemItemSku.getTitle());
		paramMap.put("sku_id", gdSysitemItemSku.getSku_id());
		paramMap.put("sku_code", gdSysitemItemSku.getSku_code());
		reqParams.put("params", FastJsonUtil.toJson(paramMap));
		String resultStr = HttpClientUtil.post(finance_service_url, reqParams);
		Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
		if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
			throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
		}
		// 新增优惠券商品信息
		goodsDao.insertGdSysitemItemSku(gdSysitemItemSku);
	}

	@Override
	public void handleCouponCreateNotify(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "member_id", "member_type_key", "item_id", "num" });
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("item_id", paramsMap.get("item_id"));
		List<Map<String, Object>> couponList = goodsDao.selectCouponItem(tempMap);
		if (couponList.size() == 0) {
			throw new BusinessException(RspCode.COUPON_NOT_EXIST, RspCode.MSG.get(RspCode.COUPON_NOT_EXIST));
		}
		Integer num = Integer.parseInt(paramsMap.get("num") + "");
		Map<String, Object> couponMap = couponList.get(0);
		// 修改优惠券库存
		tempMap.clear();
		tempMap.put("item_id", paramsMap.get("item_id"));
		tempMap.put("used_num", num + "");
		int updateFlag = goodsDao.updateGdSysitemItemCouponProp(tempMap);
		if (updateFlag == 0) {
			throw new BusinessException(RspCode.PP_ACTIVITY_ERROR, "操作失败,请刷新后重试");
		}
		for (int i = 0; i < num; i++) {
			GdSysitemItemSku gdSysitemItemSku = new GdSysitemItemSku();
			gdSysitemItemSku.setStatus(Constant.STATUS_ACTIVATED);
			gdSysitemItemSku.setCreated_date(new Date());
			gdSysitemItemSku.setSku_id(IDUtil.getUUID());
			gdSysitemItemSku.setSku_code(IDUtil.generateCode(16));
			gdSysitemItemSku.setItem_id((String) couponMap.get("item_id"));
			gdSysitemItemSku.setCost_price((BigDecimal) couponMap.get("cost_price"));
			gdSysitemItemSku.setMarket_price((BigDecimal) couponMap.get("market_price"));
			gdSysitemItemSku.setVip_price((BigDecimal) couponMap.get("vip_price"));
			gdSysitemItemSku.setMarket_price_bonus((Integer) couponMap.get("market_price_bonus"));
			gdSysitemItemSku.setMarket_price_gold((Integer) couponMap.get("market_price_gold"));
			gdSysitemItemSku.setMarket_price_bonus((Integer) couponMap.get("market_price_bonus"));
			gdSysitemItemSku.setTitle((String) couponMap.get("title"));
			gdSysitemItemSku.setExpired_date((Date) couponMap.get("expired_date"));

			// 消费者增加优惠券信息
			String finance_service_url = PropertiesConfigUtil.getProperty("finance_service_url");
			Map<String, String> reqParams = new HashMap<String, String>();
			Map<String, Object> paramMap = new HashMap<String, Object>();
			reqParams.put("service", "finance.memberCouponCreate");
			paramMap.put("member_id", paramsMap.get("member_id"));
			paramMap.put("member_type_key", paramsMap.get("member_type_key"));
			paramMap.put("expired_date",
					DateUtil.date2Str(gdSysitemItemSku.getExpired_date(), DateUtil.fmt_yyyyMMddHHmmss));
			paramMap.put("status", gdSysitemItemSku.getStatus());
			paramMap.put("item_id", gdSysitemItemSku.getItem_id());
			paramMap.put("title", gdSysitemItemSku.getTitle());
			paramMap.put("sku_id", gdSysitemItemSku.getSku_id());
			paramMap.put("sku_code", gdSysitemItemSku.getSku_code());
			reqParams.put("params", FastJsonUtil.toJson(paramMap));
			String resultStr = HttpClientUtil.post(finance_service_url, reqParams);
			Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
			if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
				throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
			}

			// 新增优惠券商品信息
			goodsDao.insertGdSysitemItemSku(gdSysitemItemSku);
		}
	}

	@Override
	public void couponFree(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "member_id", "member_type_key", "item_id" });

		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("item_id", paramsMap.get("item_id"));
		List<Map<String, Object>> couponList = goodsDao.selectCouponItem(tempMap);
		if (couponList.size() == 0) {
			throw new BusinessException(RspCode.COUPON_NOT_EXIST, RspCode.MSG.get(RspCode.COUPON_NOT_EXIST));
		}

		Map<String, Object> couponMap = couponList.get(0);
		if (!couponMap.get("category_id").equals("ac3446f0-0474-11e6-b922-fcaa1490ccaf")) {
			throw new BusinessException(RspCode.COUPON_CATEGORY_ERROR, RspCode.MSG.get(RspCode.COUPON_CATEGORY_ERROR));
		}

		// 修改优惠券商品已领取数量
		tempMap.clear();
		tempMap.put("item_id", paramsMap.get("item_id"));
		tempMap.put("used_num", "1");
		int updateFlag = goodsDao.updateGdSysitemItemCouponProp(tempMap);
		if (updateFlag == 0) {
			throw new BusinessException(RspCode.PP_ACTIVITY_ERROR, "操作失败,请刷新后重试");
		}

		GdSysitemItemSku gdSysitemItemSku = new GdSysitemItemSku();
		gdSysitemItemSku.setStatus(Constant.STATUS_ACTIVATED);
		gdSysitemItemSku.setCreated_date(new Date());
		gdSysitemItemSku.setSku_id(IDUtil.getUUID());
		gdSysitemItemSku.setSku_code(IDUtil.generateCode(16));
		gdSysitemItemSku.setItem_id((String) couponMap.get("item_id"));
		gdSysitemItemSku.setCost_price((BigDecimal) couponMap.get("cost_price"));
		gdSysitemItemSku.setMarket_price((BigDecimal) couponMap.get("market_price"));
		gdSysitemItemSku.setVip_price((BigDecimal) couponMap.get("vip_price"));
		gdSysitemItemSku.setMarket_price_bonus((Integer) couponMap.get("market_price_bonus"));
		gdSysitemItemSku.setMarket_price_gold((Integer) couponMap.get("market_price_gold"));
		gdSysitemItemSku.setMarket_price_bonus((Integer) couponMap.get("market_price_bonus"));
		gdSysitemItemSku.setTitle((String) couponMap.get("title"));
		gdSysitemItemSku.setExpired_date((Date) couponMap.get("expired_date"));

		// 消费者增加优惠券信息
		String finance_service_url = PropertiesConfigUtil.getProperty("finance_service_url");
		Map<String, String> reqParams = new HashMap<String, String>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		reqParams.put("service", "finance.memberCouponCreate");
		paramMap.put("member_id", paramsMap.get("member_id"));
		paramMap.put("member_type_key", paramsMap.get("member_type_key"));
		paramMap.put("expired_date",
				DateUtil.date2Str(gdSysitemItemSku.getExpired_date(), DateUtil.fmt_yyyyMMddHHmmss));
		paramMap.put("status", gdSysitemItemSku.getStatus());
		paramMap.put("item_id", gdSysitemItemSku.getItem_id());
		paramMap.put("title", gdSysitemItemSku.getTitle());
		paramMap.put("sku_id", gdSysitemItemSku.getSku_id());
		paramMap.put("sku_code", gdSysitemItemSku.getSku_code());
		reqParams.put("params", FastJsonUtil.toJson(paramMap));
		String resultStr = HttpClientUtil.post(finance_service_url, reqParams);
		Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
		if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
			throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
		}
		// 新增优惠券商品信息
		goodsDao.insertGdSysitemItemSku(gdSysitemItemSku);
	}

	@Override
	public void handleCouponValidate(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "member_id", "sku_code" });
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("sku_code", paramsMap.get("sku_code"));
		Map<String, Object> skuCodeMap = goodsDao.selectCouponSkuCode(tempMap);
		if (null == skuCodeMap) {
			throw new BusinessException(RspCode.SKU_CODE_ERROR, "无效的二维码");
		}
		if (new Date().getTime() > ((Date) skuCodeMap.get("expired_date")).getTime()) {
			throw new BusinessException(RspCode.SKU_CODE_EXPIRED, "优惠券已过期");
		}
		if (!((String) skuCodeMap.get("status")).equals(Constant.STATUS_ACTIVATED)) {
			throw new BusinessException(RspCode.SKU_CODE_EXPIRED, RspCode.MSG.get(RspCode.SKU_CODE_EXPIRED));
		}
		if (!((String) skuCodeMap.get("member_id")).equals((String) paramsMap.get("member_id"))) {
			throw new BusinessException(RspCode.COUPON_OF_STORES_ERROR, "非本店优惠券");
		}
		/**
		 * 核销优惠券 1:先去核销消费者的优惠券 2:修改sku商品的状态为验证 3:优惠券商品已验证数量更新 4:赠送礼券
		 */
		// 修改消费者的优惠券状态为验证
		String finance_service_url = PropertiesConfigUtil.getProperty("finance_service_url");
		Map<String, String> reqParams = new HashMap<String, String>();
		Map<String, Object> bizParams = new HashMap<String, Object>();
		reqParams.put("service", "finance.memberIdFindBySkuCode");
		bizParams.put("sku_code", paramsMap.get("sku_code"));
		reqParams.put("params", FastJsonUtil.toJson(bizParams));
		String resultStr = HttpClientUtil.post(finance_service_url, reqParams);
		Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
		if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
			throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
		}
		reqParams.clear();
		bizParams.clear();
		resultMap.clear();
		reqParams.put("service", "finance.memberCouponStatusEdit");
		bizParams.put("sku_code", paramsMap.get("sku_code"));
		bizParams.put("status", Constant.STATUS_VERIFIED);
		reqParams.put("params", FastJsonUtil.toJson(bizParams));
		resultStr = HttpClientUtil.post(finance_service_url, reqParams);
		resultMap = FastJsonUtil.jsonToMap(resultStr);
		if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
			throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
		}
		// 修改sku商品的状态为验证
		tempMap.clear();
		tempMap.put("sku_id", skuCodeMap.get("sku_id"));
		tempMap.put("status", Constant.STATUS_VERIFIED);
		tempMap.put("remark",
				paramsMap.get("sku_code") + "验证完成,时间:" + DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss));
		goodsDao.updateGdSysitemItemSku(tempMap);
		tempMap.clear();
		tempMap.put("item_id", skuCodeMap.get("item_id"));
		tempMap.put("verified_num", "1");
		// 优惠券商品已验证数量更新
		goodsDao.updateGdSysitemItemCouponProp(tempMap);
		// 将优惠券的结算金额转给门店
		String settle_price = skuCodeMap.get("settle_price") + "";
		if (!MoneyUtil.moneyComp("0.00", settle_price)) {
			reqParams.put("service", "finance.orderPay");
			bizParams.put("data_source", "SJLY_02");
			bizParams.put("payment_way_key", "ZFFS_05");
			bizParams.put("detail", "优惠券");
			bizParams.put("amount", settle_price);
			bizParams.put("out_trade_no", IDUtil.getTimestamp(4));
			bizParams.put("buyer_id", Constant.MEMBER_ID_MTH);
			bizParams.put("seller_id", paramsMap.get("member_id"));

			Map<String, Object> out_trade_body = new HashMap<String, Object>();
			out_trade_body.put("member_id", paramsMap.get("member_id"));
			out_trade_body.put("sku_id", skuCodeMap.get("sku_id") + "");
			out_trade_body.put("title", skuCodeMap.get("title") + "");
			out_trade_body.put("market_price", skuCodeMap.get("market_price") + "");
			out_trade_body.put("settle_price", settle_price);
			bizParams.put("out_trade_body", FastJsonUtil.toJson(out_trade_body));
			reqParams.put("params", FastJsonUtil.toJson(bizParams));
			resultStr = HttpClientUtil.post(finance_service_url, reqParams);
			resultMap = FastJsonUtil.jsonToMap(resultStr);
			if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
				logger.error("给" + paramsMap.get("member_id") + "进行优惠券结算异常;" + (String) resultMap.get("error_msg"));
			}
		}
		resultMap.clear();
		resultMap.put("title", skuCodeMap.get("title"));
		result.setResultData(resultMap);
	}

	@Override
	public void couponValidateFindForStores(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "member_id", "sku_code" });
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("sku_code", paramsMap.get("sku_code"));
		Map<String, Object> skuCodeMap = goodsDao.selectCouponSkuCode(tempMap);
		if (null == skuCodeMap) {
			throw new BusinessException(RspCode.SKU_CODE_ERROR, "无效的二维码");
		}
		if (new Date().getTime() > ((Date) skuCodeMap.get("expired_date")).getTime()) {
			throw new BusinessException(RspCode.SKU_CODE_EXPIRED, "优惠券已过期");
		}
		if (!((String) skuCodeMap.get("status")).equals(Constant.STATUS_ACTIVATED)) {
			throw new BusinessException(RspCode.SKU_CODE_EXPIRED, RspCode.MSG.get(RspCode.SKU_CODE_EXPIRED));
		}
		if (!((String) skuCodeMap.get("member_id")).equals((String) paramsMap.get("member_id"))) {
			throw new BusinessException(RspCode.COUPON_OF_STORES_ERROR, "非本店优惠券");
		}
		Map<String, Object> couponDetailMap = new HashMap<String, Object>();
		couponDetailMap.put("title", skuCodeMap.get("title"));
		couponDetailMap.put("market_price", skuCodeMap.get("market_price") + "");
		couponDetailMap.put("limit_amount", skuCodeMap.get("limit_amount") + "");
		couponDetailMap.put("coupon_prop", skuCodeMap.get("coupon_prop") + "");
		couponDetailMap.put("expired_date",
				DateUtil.date2Str((Date) skuCodeMap.get("expired_date"), DateUtil.fmt_yyyyMMddHHmmss));
		result.setResultData(couponDetailMap);
	}

	@Override
	public void storesCouponIsExist(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "member_id" });
		String member_id = (String) paramsMap.get("member_id");
		List<String> list = StringUtil.str2List(member_id, ",");
		if (list.size() > 1) {
			paramsMap.remove("member_id");
			paramsMap.put("member_id_in", list);
		}
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> couponList = goodsDao.selectCouponItem(paramsMap);
		for (Map<String, Object> tempMap : couponList) {
			resultMap.put(tempMap.get("member_id") + "", tempMap.get("item_id"));
		}
		result.setResultData(resultMap);
	}

	@Override
	public void recommendStoresGoodsListFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "area_id" });
		Map<String, String> reqParams = new HashMap<String, String>();
		Map<String, Object> bizParams = new HashMap<String, Object>();
		String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
		reqParams.put("service", "member.recommendStoresListForGoodsFind");
		bizParams.put("area_id", paramsMap.get("area_id"));
		reqParams.put("params", FastJsonUtil.toJson(bizParams));
		String resultStr = HttpClientUtil.postShort(member_service_url, reqParams);
		Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
		if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
			throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
		}
		Map<String, Object> dataMap = (Map<String, Object>) resultMap.get("data");

		List<Map<String, Object>> list = (List<Map<String, Object>>) dataMap.get("list");
		List<String> storesList = new ArrayList<String>();
		for (Map<String, Object> storesMap : list) {
			storesList.add(storesMap.get("stores_id") + "");
		}
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		List<String> pic_path_list = new ArrayList<String>();
		// 如果没有推荐会员，则不执行查询数据
		if (storesList.size() > 0) {
			bizParams.clear();
			bizParams.put("member_id_in", storesList);
			bizParams.put("status", Constant.STATUS_ON_SHELF);
			List<Map<String, Object>> couponList = goodsDao.selectCouponItem(bizParams);
			for (Map<String, Object> map : couponList) {
				if (!StringUtil.formatStr(map.get("category_id")).equals(Constant.COUPON_CATEGORY_VOUCHER)) {
					Map<String, Object> tempMap = new HashMap<String, Object>();
					tempMap.put("member_id", map.get("member_id"));
					tempMap.put("member_type_key", map.get("member_type_key"));
					tempMap.put("item_id", map.get("item_id"));
					tempMap.put("title", map.get("title"));
					tempMap.put("pic_path", map.get("pic_path"));
					tempMap.put("usable_num", map.get("usable_num") + "");
					pic_path_list.addAll(StringUtil.str2List((String) map.get("pic_path"), ","));
					resultList.add(tempMap);
				}
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("doc_url", docUtil.imageUrlFind(pic_path_list));
		map.put("list", resultList);
		result.setResultData(map);
	}

	@Override
	public void storesActivityCountFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "stores_id" });
		String member_id = paramsMap.get("stores_id") + "";
		Map<String, Object> resultDataMap = new HashMap<String, Object>();
		// 优惠活动统计
		resultDataMap.put("coupon_count", "0");
		// 一元抽奖统计
		resultDataMap.put("ld_activity_count", "0");
		// 团购预售
		resultDataMap.put("group_booking_count", "0");
		// 查询优惠券的统计
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("member_id", member_id);
		tempMap.put("status", Constant.STATUS_ON_SHELF);
		Map<String, Object> countsMap = goodsDao.selectStoresCouponCount(tempMap);
		if (null != countsMap) {
			resultDataMap.put("coupon_count", countsMap.get("count_num") + "");
		}
		// 一元抽奖统计
		tempMap.clear();
		countsMap.clear();
		tempMap.put("stores_id", member_id);
		tempMap.put("status", Constant.STATUS_PROCESSING);
		countsMap = ldActivityDao.selectLdActivityCount(tempMap);
		if (null != countsMap) {
			resultDataMap.put("ld_activity_count", countsMap.get("count_num") + "");
		}
		// 团购预售统计
		countsMap.clear();
		String order_service_url = PropertiesConfigUtil.getProperty("order_service_url");
		Map<String, String> reqParams = new HashMap<String, String>();
		Map<String, Object> bizParams = new HashMap<String, Object>();
		reqParams.put("service", "order.psOrderCount");
		bizParams.put("member_id", member_id);
		bizParams.put("member_type_key", Constant.MEMBER_TYPE_STORES);
		bizParams.put("order_type", "activity");
		reqParams.put("params", FastJsonUtil.toJson(bizParams));
		String resultStr = HttpClientUtil.post(order_service_url, reqParams);
		Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
		if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
			throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
		}
		Map<String, Object> countMap = (Map<String, Object>) resultMap.get("data");
		resultDataMap.put("group_booking_count", countMap.get("count_num") + "");
		result.setResultData(resultDataMap);
	}

	@Override
	public void storesCouponTotalFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "member_id" });
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("total_issued_num", "0");
		resultMap.put("total_onsell_num", "0");
		resultMap.put("total_used_num", "0");
		resultMap.put("total_usable_num", "0");
		resultMap.put("total_verified_num", "0");
		Map<String, Object> tempMap = new HashMap<String, Object>();
		tempMap.put("member_id", paramsMap.get("member_id"));
		Map<String, Object> countsMap = goodsDao.selectStoresCouponTotal(tempMap);
		if (null != countsMap) {
			resultMap.put("total_issued_num", countsMap.get("total_issued_num") + "");
			resultMap.put("total_onsell_num", countsMap.get("total_onsell_num") + "");
			resultMap.put("total_used_num", countsMap.get("total_used_num") + "");
			resultMap.put("total_usable_num", countsMap.get("total_usable_num") + "");
			resultMap.put("total_verified_num", countsMap.get("total_verified_num") + "");
		}
		result.setResultData(resultMap);
	}

	@Override
	public void storesCouponCountFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		String member_id = StringUtil.formatStr(paramsMap.get("member_id"));
		if (!StringUtils.isEmpty(member_id)) {
			List<String> list = StringUtil.str2List(member_id, ",");
			if (list.size() > 1) {
				paramsMap.remove("member_id");
				paramsMap.put("member_id_in", list);
			}
		}
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("count_num", "0");
		Map<String, Object> countsMap = goodsDao.selectStoresCouponCount(paramsMap);
		if (null != countsMap) {
			resultMap.put("count_num", countsMap.get("count_num") + "");
		}
		result.setResultData(resultMap);
	}

	@Override
	public void gdAdvertEdit(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "advert_id" });
			if (paramsMap.size() == 1) {
				throw new BusinessException(RspCode.SYSTEM_NO_PARAMS_UPDATE,
						RspCode.MSG.get(RspCode.SYSTEM_NO_PARAMS_UPDATE));
			}
			List<Map<String, Object>> advertList = goodsDao.selectGdAdvert(paramsMap);
			if (advertList.size() == 0) {
				throw new BusinessException(RspCode.ADVERT_NOT_EXIST, RspCode.MSG.get(RspCode.ADVERT_NOT_EXIST));
			}
			Map<String, Object> advert = advertList.get(0);
			paramsMap.put("modified_date",
					DateUtil.date2Str((Date) advert.get("modified_date"), DateUtil.fmt_yyyyMMddHHmmss));
			int updateFlag = goodsDao.updateGdAdvert(paramsMap);
			if (updateFlag == 0) {
				throw new BusinessException(RspCode.PROCESSING, "正在处理中,请重试");
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
	public void gdAdvertFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			List<Map<String, Object>> advertList = goodsDao.selectGdAdvert(paramsMap);
			List<String> doc_ids = new ArrayList<String>();
			for (Map<String, Object> map : advertList) {
				map.put("label", StringUtil.formatStr(map.get("label")));
				map.put("modified_date",
						DateUtil.date2Str((Date) map.get("modified_date"), DateUtil.fmt_yyyyMMddHHmmss));
				String pic_info = StringUtil.formatStr(map.get("pic_info"));
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
			map.put("list", advertList);
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
	public void freeGoodsBannerImageFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			
			List<Map<String, Object>> advertList = gdAppAdvertDao.selectGdAppAdvert(paramsMap);
			List<String> doc_ids = new ArrayList<String>();
			if(advertList!=null && advertList.size()>0){
				Map<String, Object> map=advertList.get(0);
				map.put("category", StringUtil.formatStr(map.get("category")));
				
				String pic_info = StringUtil.formatStr(map.get("json_data"));
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
			//map.put("list", advertList);
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
	public void storesCouponProp(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "member_id" });
			String member_id = (String) paramsMap.get("member_id");
			List<String> list = StringUtil.str2List(member_id, ",");
			if (list.size() > 1) {
				paramsMap.remove("member_id");
				paramsMap.put("member_id_in", list);
			}
			List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> couponList = goodsDao.selectCouponItem(paramsMap);
			for (Map<String, Object> m : couponList) {
				Map<String, Object> tempMap = new HashMap<String, Object>();
				tempMap.put("stores_id", m.get("member_id"));
				tempMap.put("item_id", m.get("item_id"));
				tempMap.put("title", m.get("title"));
				tempMap.put("expired_date", DateUtil.date2Str((Date) m.get("expired_date"), DateUtil.fmt_yyyyMMdd));
				resultList.add(tempMap);
			}
			result.setResultData(resultList);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void memberGiftCardSend(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap,
					new String[] { "member_id", "member_type_key", "gift_type", "gift_value", "operator", "remark" });
			goodsDao.callMemberGiftCard(paramsMap);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void memberGiftCardBatchSend(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "member_type_key", "criteria_type", "criteria_value",
					"gift_type", "gift_value", "operator", "remark" });
			goodsDao.callMemberGiftCardBatch(paramsMap);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void handleMemberLotteryNumFind(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			ValidateUtil.validateParams(paramsMap, new String[] { "member_id", "member_type_key" });
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("member_id", paramsMap.get("member_id"));
			resultMap.put("member_type_key", paramsMap.get("member_type_key"));
			resultMap.put("lottery_num", "0");
			Map<String, Object> memberLotteryNum = goodsDao.callMemberLotteryNum(paramsMap);
			if (null != memberLotteryNum) {
				resultMap.put("lottery_num", memberLotteryNum.get("lottery_num"));
			}
			result.setResultData(resultMap);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void selectPsGoodsSkuid(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "goods_code" });
		if (paramsMap.get("attr_zvalue") != null) {
			List<Map<String, Object>> listFuValue = goodsDao.selectPsGoodsSkuidToFuValue(paramsMap);
			result.setResultData(listFuValue);
			return;
		}
		List<Map<String, Object>> list = goodsDao.selectPsGoodsSkuid(paramsMap);
		Map<String, Object> firstMap = list.get(0);
		if (firstMap.get("attr_fid") != null) {
			Object goods_code = firstMap.get("goods_code");
			Object attr_zvalue = firstMap.get("attr_zvalue");
			Map<String, Object> tempMap = new HashMap<>();
			tempMap.put("goods_code", goods_code);
			tempMap.put("attr_zvalue", attr_zvalue);
			List<Map<String, Object>> listFuValue = goodsDao.selectPsGoodsSkuidToFuValue(tempMap);
			firstMap.put("fuValue", listFuValue);
		}

		result.setResultData(list);
	}

	@Override
	public void selectPsGoodsSkuidBySkuId(Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "sku_ids" });
		List<Map<String, Object>> skuIdList = goodsDao.selectPsGoodsSkuidBySkuId(paramsMap);
		if (skuIdList == null || skuIdList.size() == 0) {
			throw new BusinessException("订单异常", "订单异常,请稍后查看");
		}
		result.setResultData(skuIdList);
	}

	@Override
	public void findAllGoodsAndActivity(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "supplier_id"});
		List<Map<String, Object>> bkcqProductsAllList = psGoodsActivityDao.findAllGoodsBySupplierId(paramsMap);
		List<Map<String, Object>> goodsActivityOfBkcqList = psGoodsActivityDao.findActivityGoodsBySupplierId(paramsMap);
		Map<String, Object> map =new HashMap<>();
		map.put("bkcqProductsAllList", bkcqProductsAllList);
		map.put("goodsActivityOfBkcqList", goodsActivityOfBkcqList);
		result.setResultData(map);
	}

	@Override
	public void findGoodsOfActivity(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[]{"supplier_id", "page_no", "page_size"});
		Map<String, Object> webJsonMap = new HashMap<>();
		int page_no = 0;
		int page_size = 0;
		try{
			page_no = Integer.valueOf(paramsMap.get("page_no")+"");
			page_size = Integer.valueOf(paramsMap.get("page_size")+"");
		} catch (Exception e){
			page_no = 1;
			logger.error("页码错误：{}", e);
		}
		if(page_no<=0){
			page_no = 1;
		}
		PageHelper.startPage(page_no, page_size);
		List<Map<String, Object>> goodsActivityOfBkcqList = psGoodsActivityDao.findActivityGoodsBySupplierId02(paramsMap);

		findPicList(goodsActivityOfBkcqList, new String[]{"pic_info","pic_detail_info"});

		Map<String, Object> map =new HashMap<>();
		PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(goodsActivityOfBkcqList);
		Map<String, Object> pageMap = new HashMap<>();
		pageMap.put("page_no", pageInfo.getPageNum());
		pageMap.put("page_size", pageInfo.getPageSize());
		pageMap.put("total_count", pageInfo.getTotal());
		pageMap.put("total_page", pageInfo.getPages());
		map.put("goodsActivityOfBkcqList", goodsActivityOfBkcqList);
		map.put("page", pageMap);

		result.setResultData(map);
	}


	@Override
	public void findAppMemberAd(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception {
//		ValidateUtil.validateParams(paramsMap, new String[] { "supplier_id"});
//		List<Map<String, Object>> gdAppAdvertPicList = new ArrayList<Map<String, Object>>();
//		Map<String, Object> mapObj = new HashMap<String, Object>();

		List<Map<String, Object>> qryList = gdAppAdvertDao.findAppMemberAd(paramsMap);
		if (qryList!=null && qryList.size() > 0) {
			for (Map<String, Object> map : qryList) {
				String json_data = StringUtil.formatStr(map.get("json_data"));
				if (StringUtils.isNotEmpty(json_data)) {
					List<Map<String, Object>> jsonList = FastJsonUtil.jsonToList(json_data);
					if(jsonList!=null&&jsonList.size()>0){
						Map<String, Object> objectMap = jsonList.get(0);
						String imgPath = docUtil.imageUrlFind(objectMap.get("path_id")+"");
						map.put("imgPath", imgPath);
						map.put("url", objectMap.get("url")+"");
						map.put("json_data", json_data);
					}
				}
			}
		}else{
			qryList = new ArrayList<Map<String, Object>>();
		}
		result.setResultData(qryList);
	}

	private Map<String, Object> switchMap(Map<String, Object> dataMap, String[] nameArr) {
//		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<String> asList = Arrays.asList(nameArr);

		Iterator<Map.Entry<String, Object>> it = dataMap.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String, Object> entry=it.next();
			String key=entry.getKey();
			if(!asList.contains(key)){
				it.remove();
			}
		}

//		for (String name : nameArr) {
//			if(!dataMap.containsKey(name)){
//				dataMap.remove(name);
//			}else{
//				Object obj = dataMap.get(name);
//				dataMap.put(name, obj==null ? "null" : obj);
//			}
//		}
		return dataMap;
	}


	/**
	 * 某个商品详情 TODO
	 */
	@Override
	public void findDetailGoods(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception {
		Map<String, Object> webJsonMap =new HashMap<>();
		ValidateUtil.validateParams(paramsMap, new String[] { "goods_id" }); //, "supplier_id"

		//查询商品详情
		Map<String, Object> oneSqlMap = new HashMap<>();
		oneSqlMap.put("goods_id", paramsMap.get("goods_id"));
//		oneMap.put("supplier_id", paramsMap.get("supplier_id"));
//		oneSqlMap.put("supplier_type", "store");
		Map<String, Object> bkcqProductsOne = psGoodsActivityDao.selectBkcqProductsByGoodsId(oneSqlMap);
		if(bkcqProductsOne==null){
			logger.info("没有查询到商品详情goods_id："+paramsMap.get("goods_id"));
			throw new BusinessException("查询商品详情", "没有查询到商品详情goods_id："+paramsMap.get("goods_id"));
		}

		//查询商品详情图片
		findPic(bkcqProductsOne, "pic_info");
		findPic(bkcqProductsOne, "pic_detail_info");

		webJsonMap.put("goods_detail", bkcqProductsOne);


		//查询商家信息
		String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
		member_service_url = member_service_url+"/stores";
		Map<String, String> reqParams = new HashMap<>();
		Map<String, Object> bizParams = new HashMap<>();
//		bizParams.put("stores_id", paramsMap.get("supplier_id"));
		bizParams.put("stores_id", bkcqProductsOne.get("supplier_id"));
		bizParams.put("supplier_type", "store");
		reqParams.put("service", "member.stores.supplier.info");
		reqParams.put("params", FastJsonUtil.toJson(bizParams));
		String resultStr = HttpClientUtil.post(member_service_url, reqParams);
		if(resultStr==null||"".equals(resultStr)){
			resultStr = "{}";
		}
		Map<String, Object> storeMap = FastJsonUtil.jsonToMap(resultStr);
		if (((String) storeMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
			Map<String, Object> supplierMap = FastJsonUtil.jsonToMap(storeMap.get("data").toString());

//			findPic(supplierMap, "head_pic_path");
//			findPic(supplierMap, "neighbor_pic_path");
//			findPic(supplierMap, "activity_pic_info");
//			findPic(supplierMap, "stores_thumbnail_path");

			String[] nameArr02=new String[]{"stores_id", "stores_no", "stores_name", "desc1"
					, "address", "contact_tel", "longitude", "latitude"
					, "contact_person", "contact_tel", "service_tel"};
			webJsonMap.put("stores_info", switchMap(supplierMap, nameArr02));

		} else {
			logger.info("没有查询商家信息goods_id："+paramsMap.get("goods_id"));
			webJsonMap.put("stores_info", new HashMap<String, Object>());
		}



		//某个商家的精品推荐
		webJsonMap.put("activity_products_list", "[]");
		Map<String, Object> activityMap = new HashMap<>();
		activityMap.put("supplier_id", bkcqProductsOne.get("supplier_id"));
		List<Map<String, Object>> activityProductsList = psGoodsActivityDao.findHDMS05GoodsBySupplierId(activityMap);
		if(activityProductsList==null || activityProductsList.size()<=0){
			activityProductsList = new ArrayList<>();
			logger.info("没有查询到商品详情中的精品推荐supplier_id："+paramsMap.get("supplier_id"));
		}else{
			for (Map<String, Object> stringObjectMap : activityProductsList) {
				findPic(stringObjectMap, "pic_info");
			}
			webJsonMap.put("activity_products_list", activityProductsList);
		}

		result.setResultData(webJsonMap);
	}



	private void findPic(Map<String, Object> mapinfo, String pic_path) {
		ArrayList<String> doc_ids = new ArrayList<>();
		Map<String, Object> idsMap = new LinkedHashMap<>();
		try{
			String neighbor_pic_path = StringUtil.formatStr(mapinfo.get(pic_path));
			if (StringUtils.isNotBlank(neighbor_pic_path)) {
				List<Map<String, Object>> listTemp = FastJsonUtil.jsonToList(neighbor_pic_path);
				for (Map<String, Object> map : listTemp) {
					doc_ids.add(map.get("path_id") + "");
					idsMap.put(map.get("path_id") + "", map.get("type") + "");
				}
			}else{
				mapinfo.put(pic_path, "null");
				idsMap.clear();
				return;
			}
		}catch (Exception e){
			logger.error("findPic--"+pic_path+":"+mapinfo.get(pic_path));
			logger.error(mapinfo.get(pic_path)+" :findPic--{}",e);
			mapinfo.put(pic_path, "");
			idsMap.clear();
			return;
		}

		if(doc_ids!=null && !doc_ids.isEmpty()){
//			String path = "http://beike-app-pic.oss-cn-hangzhou.aliyuncs.com/";
//			Map<String, Object> stringObjectMap = goodsDao.imageUrlFind(doc_ids.get(0));

			Map<String, Object> stringObjectMap = docUtil.imageUrlFind(doc_ids);
//			String stringObject = docUtil.imageUrlFind(doc_ids.get(0));
			if(stringObjectMap!=null){
				ArrayList<Map<String, Object>> objects = new ArrayList<>();
				for (Map.Entry<String, Object> entryObject: idsMap.entrySet()) {
					for (Map.Entry<String, Object> entry: stringObjectMap.entrySet()) {
						if (entryObject.getKey().equals(entry.getKey())) {
							Map<String, Object> rMap = new LinkedHashMap<>();
							rMap.put("pic", entry.getValue()+"");
							rMap.put("type", idsMap.get(entry.getKey()));
							objects.add(rMap);
							break;
						}
					}
				}
				mapinfo.put(pic_path, objects);
			}else{
				mapinfo.put(pic_path, "[]");
			}
		}
	}


	/**
	 * 查询贝壳专区和立享五折
	 */
	@Override
	public void findGoodsByLabelAndAreaName(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[]{"label", "area_name", "page_no", "page_size"});
		Map<String, Object> webJsonMap = new HashMap<>();
		int page_no = 0;
		int page_size = 0;
		try{
			page_no = Integer.valueOf(paramsMap.get("page_no")+"");
			page_size = Integer.valueOf(paramsMap.get("page_size")+"");
		} catch (Exception e){
			page_no = 1;
			logger.error("页码错误：{}", e);
		}
		if(page_no<=0){
			page_no = 1;
		}

		String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
		member_service_url = member_service_url+"/stores";
		Map<String, String> reqParams = new HashMap<>();
		Map<String, Object> bizParams = new HashMap<>();
		bizParams.put("area_name", paramsMap.get("area_name"));
		reqParams.put("service", "member.stores.by.areaName");
		reqParams.put("params", FastJsonUtil.toJson(bizParams));
		String resultStr = HttpClientUtil.post(member_service_url, reqParams);
		Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);

		List<Map<String, Object>> data = new ArrayList<>();
		ArrayList<String> list = new ArrayList<String>();
		if (((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
			data = FastJsonUtil.jsonToList(resultMap.get("data").toString());
			if(data!=null&&data.size()>0){
				for (Map<String, Object> stringObjectMap : data) {
					String stores_id = (String) stringObjectMap.get("stores_id");
					if(StringUtils.isNotBlank(stores_id)){
						list.add(stores_id);
					}
				}
			}else{
				data = new ArrayList<>();
			}

		}

		if(list==null || list.size()<=0) {
			webJsonMap.put("goods_by_label_list", new ArrayList<>());
			result.setResultData(webJsonMap);
			logger.info("没有查询到商家area_name：" + paramsMap.get("area_name"));
//			throw new BusinessException("查询商家", "没有查询到商家area_name：" + paramsMap.get("area_name"));
			return;
		}


		//查询商品列表
		Map<String, Object> oneMap = new HashMap<>();
		PageHelper.startPage(page_no, page_size);
		oneMap.put("label", paramsMap.get("label"));
		oneMap.put("page_num", page_no);
		oneMap.put("page_size", page_size);
		oneMap.put("supplier_id_list", list);
		List<Map<String, Object>> goodsByLabelList = goodsDao.findGoodsByLabel(oneMap);
		if(goodsByLabelList==null || goodsByLabelList.size()<=0){
			webJsonMap.put("goods_by_label_list", new ArrayList<>());
			result.setResultData(webJsonMap);
			logger.info("没有查询贝壳专区和立享五折：" + paramsMap.get("label"));
//			throw new BusinessException("查询商品", "没有查询贝壳专区和立享五折：" + paramsMap.get("label"));
			return;
		}

		for (Map<String, Object> stringObjectMap : goodsByLabelList) {
			//查询商品详情图片
			findPic(stringObjectMap, "pic_info");
//			findPic(stringObjectMap, "pic_detail_info");

			String supplier_id = stringObjectMap.get("supplier_id")+"";


			for (Map<String, Object> bjectMap : data) {
				String stores_id = bjectMap.get("stores_id")+"";
				if(StringUtils.equals(supplier_id, stores_id)){
					stringObjectMap.put("stores_id", bjectMap.get("stores_id"));
					stringObjectMap.put("stores_name", bjectMap.get("stores_name"));
					stringObjectMap.put("longitude", bjectMap.get("longitude"));
					stringObjectMap.put("latitude", bjectMap.get("latitude"));
					stringObjectMap.put("head_pic_path", bjectMap.get("head_pic_path"));
				}
			}
		}
 		webJsonMap.put("goods_by_label_list", goodsByLabelList);

		PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(goodsByLabelList);
		Map<String, Object> pageMap = new HashMap<>();
		pageMap.put("page_no", pageInfo.getPageNum());
		pageMap.put("page_size", pageInfo.getPageSize());
		pageMap.put("total_count", pageInfo.getTotal());
		pageMap.put("total_page", pageInfo.getPages());
		webJsonMap.put("page", pageMap);

		result.setResultData(webJsonMap);
	}


	/**
	 * 搜索
	 */
	@Override
	public void findGoodsByAreaNameAndNickname(Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception {
		ValidateUtil.validateParams(paramsMap, new String[]{"nick_name", "area_name", "page_no", "page_size"});
		Map<String, Object> webJsonMap = new HashMap<>();

		int page_no = 0;
		int page_size = 0;
		try{
			page_no = Integer.valueOf(paramsMap.get("page_no")+"");
			page_size = Integer.valueOf(paramsMap.get("page_size")+"");
		} catch (Exception e){
			page_no = 1;
			logger.error("页码错误：{}", e);
		}
		if(page_no<=0){
			page_no = 1;
		}

		String member_service_url = PropertiesConfigUtil.getProperty("member_service_url");
		member_service_url = member_service_url+"/stores";
		Map<String, String> reqParams = new HashMap<>();
		Map<String, Object> bizParams = new HashMap<>();
		bizParams.put("area_name", paramsMap.get("area_name"));
		reqParams.put("service", "member.stores.by.areaName");
		reqParams.put("params", FastJsonUtil.toJson(bizParams));
		String resultStr = HttpClientUtil.post(member_service_url, reqParams);
		Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);

		ArrayList<String> list = new ArrayList<String>();
		if (((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC))  {
			List<Map<String, Object>> data = FastJsonUtil.jsonToList(resultMap.get("data").toString());
			if(data!=null&&data.size()>0){
				for (Map<String, Object> stringObjectMap : data) {
					String stores_id = (String) stringObjectMap.get("stores_id");
					String stores_name = (String) stringObjectMap.get("stores_name");
					if(StringUtils.isNotBlank(stores_id)){
						list.add(stores_id);
					}
				}
			}
		}

		if(list==null || list.size()<=0) {
			webJsonMap.put("goods_by_label_list", new ArrayList<>());
			result.setResultData(webJsonMap);
			logger.info("没有查询到商家area_name：" + paramsMap.get("area_name"));
//			throw new BusinessException("查询商家", "没有查询到商家area_name：" + paramsMap.get("area_name"));
			return;
		}


		//查询商品详情
		Map<String, Object> oneMap = new HashMap<>();
		PageHelper.startPage(page_no, page_size);
		oneMap.put("title", paramsMap.get("nick_name"));
		oneMap.put("page_num", page_no);
		oneMap.put("page_size", page_size);
		oneMap.put("supplier_id_list", list);
		List<Map<String, Object>> goodsByLabelList = goodsDao.findGoodsByAreaNameAndNickname(oneMap);
		if(goodsByLabelList==null || goodsByLabelList.size()<=0){
			webJsonMap.put("goods_by_label_list", new ArrayList<>());
			result.setResultData(webJsonMap);
			logger.info("没有查询贝壳专区和立享五折：" + paramsMap.get("label"));
//			throw new BusinessException("查询商品", "没有查询贝壳专区和立享五折：" + paramsMap.get("label"));
			return;
		}

		for (Map<String, Object> stringObjectMap : goodsByLabelList) {
			//查询商品详情图片
			findPic(stringObjectMap, "pic_info");
//			findPic(stringObjectMap, "pic_detail_info");
		}
		webJsonMap.put("goods_by_label_list", goodsByLabelList);

		PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(goodsByLabelList);
		Map<String, Object> pageMap = new HashMap<>();
		pageMap.put("page_no", pageInfo.getPageNum());
		pageMap.put("page_size", pageInfo.getPageSize());
		pageMap.put("total_count", pageInfo.getTotal());
		pageMap.put("total_page", pageInfo.getPages());
		webJsonMap.put("page", pageMap);

		result.setResultData(webJsonMap);
	}




	private void findPicList(List<Map<String, Object>> list, String[] arrStr) throws SystemException {
		if(list!=null && list.size()>0){
			for (Map<String, Object> stringObjectMap : list) {
				for (String pic_info : arrStr) {
					findPic(stringObjectMap, pic_info);
				}
			}
		}else{
//          logger.info("没有查询到商品详情中的精品推荐supplier_id："+paramsMap.get("supplier_id"));
			//throw new BusinessException("查询商品详情", "没有查询到商品详情中的精品推荐supplier_id："+paramsMap.get("supplier_id"));
		}
	}






}







