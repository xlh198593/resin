package com.meitianhui.goods.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.util.DateUtil;
import com.meitianhui.common.util.DocUtil;
import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.common.util.StringUtil;
import com.meitianhui.common.util.ValidateUtil;
import com.meitianhui.goods.constant.Constant;
import com.meitianhui.goods.dao.BeiKeMallGoodsDao;
import com.meitianhui.goods.dao.HongbaoGoodsDao;
import com.meitianhui.goods.dao.PsGoodsActivityDao;
import com.meitianhui.goods.dao.PsGoodsDao;
import com.meitianhui.goods.entity.BeikeMallGoods;
import com.meitianhui.goods.entity.GdGoodsLabel;
import com.meitianhui.goods.entity.PsGoodsActivity;
import com.meitianhui.goods.service.BeikeMallGoodsService;

@Service
public class BeikeMallGoodsServiceImpl implements BeikeMallGoodsService {

	@Autowired
	private BeiKeMallGoodsDao beiKeMallGoodsDao;
	@Autowired
	private PsGoodsActivityDao psGoodsActivityDao;
	@Autowired
	private DocUtil docUtil;
	@Autowired
	public HongbaoGoodsDao hongbaoGoodsDao;
	@Autowired
	public PsGoodsDao psGoodsDao;

	@Override
	public void homeGoodsFind(Map<String, Object> paramsMap, ResultData result) throws Exception {
		paramsMap.put("is_finished", "N");
		List<PsGoodsActivity> list = psGoodsActivityDao.selectPsGoodsActivity(paramsMap);
		List<String> doc_ids = new ArrayList<>();
		List<Map<String, Object>> beikeMallList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> hongBaoList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> beikeStreetList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> recommendList = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		if (list == null || list.isEmpty()) {
			map.put("beikeMallList", beikeMallList);
			map.put("hongBaoList", hongBaoList);
			map.put("beikeStreetList", beikeStreetList);
			map.put("recommendList", recommendList);
			result.setResultData(map);
			return;
		}
		for (PsGoodsActivity psGoods : list) {
			Map<String, Object> tempMap = new HashMap<>();
			tempMap.put("goods_id", psGoods.getGoods_id());
			tempMap.put("goods_title", psGoods.getGoods_title());
			tempMap.put("goods_desc", psGoods.getGoods_desc());
			tempMap.put("goods_price", psGoods.getGoods_price());
			tempMap.put("goods_hongbao", psGoods.getGoods_hongbao());
			tempMap.put("goods_beike", psGoods.getGoods_beike());
			tempMap.put("activity_type", psGoods.getActivity_type());
			tempMap.put("market_price", psGoods.getMarket_price());
			tempMap.put("pic_info", StringUtil.formatStr(psGoods.getPic_info()));
			// 解析区域
			if (Constant.ACTIVITY_01.equals(psGoods.getActivity_type())) {
				beikeMallList.add(tempMap);
			} else if (Constant.ACTIVITY_02.equals(psGoods.getActivity_type())) {
				beikeStreetList.add(tempMap);
			} else if (Constant.ACTIVITY_03.equals(psGoods.getActivity_type())) {
				hongBaoList.add(tempMap);
			} else if (Constant.ACTIVITY_04.equals(psGoods.getActivity_type())
					|| Constant.ACTIVITY_05.equals(psGoods.getActivity_type())
					|| Constant.ACTIVITY_06.equals(psGoods.getActivity_type())) {
				recommendList.add(tempMap);
			}
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
			} catch (Exception e) {
				map.put("doc_url", "");
				e.printStackTrace();
			}
		}
		list.clear();
		map.put("doc_url", docUtil.imageUrlFind(doc_ids));
		map.put("beikeMallList", beikeMallList);
		map.put("hongBaoList", hongBaoList);
		map.put("beikeStreetList", beikeStreetList);
		map.put("recommendList", recommendList);
		result.setResultData(map);
	}

	@Override
	public void vipGoodsFind(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "activity_type" });
		paramsMap.put("is_finished", "N");
		List<Map<String, Object>> list = psGoodsActivityDao.findPsGoodsActivity(paramsMap);
		List<String> doc_ids = new ArrayList<>();
		List<Map<String, Object>> goodsList = new ArrayList<>();
		Map<String, Object> map = new HashMap<>();
		if (list == null || list.isEmpty()) {
			map.put("goodsList", goodsList);
			result.setResultData(map);
			return;
		}
		
		for (Map<String, Object> psGoods : list) {
			Map<String, Object> tempMap = new HashMap<>();
			tempMap.put("goods_id", psGoods.get("goods_id"));
			tempMap.put("activity_type", psGoods.get("activity_type"));
			tempMap.put("pic_info", psGoods.get("pic_info"));
			// 解析图片
			String pic_info = StringUtil.formatStr(psGoods.get("pic_info"));
			try {
				if (!pic_info.equals("")) {
					List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(pic_info);
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
			goodsList.add(tempMap);
		}
		list.clear();
		map.put("doc_url", docUtil.imageUrlFind(doc_ids));
		map.put("goodsList", goodsList);
		result.setResultData(map);
	}

	@Override
	public void homeGoodsFind_V1(Map<String, Object> paramsMap, ResultData result) throws Exception {
		paramsMap.put("is_finished", "N");
		List<String> activity_type_in = new ArrayList<>();
		activity_type_in.add(Constant.ACTIVITY_01);
		activity_type_in.add(Constant.ACTIVITY_02);
		activity_type_in.add(Constant.ACTIVITY_03);
		paramsMap.put("activity_type_in", activity_type_in);
		List<PsGoodsActivity> list = psGoodsActivityDao.selectPsGoodsActivity(paramsMap);
		List<String> doc_ids = new ArrayList<>();
		List<Map<String, Object>> beikeMallList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> hongBaoList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> beikeStreetList = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		if (list == null || list.isEmpty()) {
			map.put("beikeMallList", beikeMallList);
			map.put("hongBaoList", hongBaoList);
			map.put("beikeStreetList", beikeStreetList);
			result.setResultData(map);
			return;
		}
		for (PsGoodsActivity psGoods : list) {
			Map<String, Object> tempMap = new HashMap<>();
			tempMap.put("goods_id", psGoods.getGoods_id());
			tempMap.put("goods_title", psGoods.getGoods_title());
			tempMap.put("goods_desc", psGoods.getGoods_desc());
			tempMap.put("goods_price", psGoods.getGoods_price());
			tempMap.put("goods_hongbao", psGoods.getGoods_hongbao());
			tempMap.put("goods_beike", psGoods.getGoods_beike());
			tempMap.put("activity_type", psGoods.getActivity_type());
			tempMap.put("market_price", psGoods.getMarket_price());
			tempMap.put("pic_info", StringUtil.formatStr(psGoods.getPic_info()));
			// 解析区域
			if (Constant.ACTIVITY_01.equals(psGoods.getActivity_type())) {
				beikeMallList.add(tempMap);
			} else if (Constant.ACTIVITY_02.equals(psGoods.getActivity_type())) {
				beikeStreetList.add(tempMap);
			} else if (Constant.ACTIVITY_03.equals(psGoods.getActivity_type())) {
				hongBaoList.add(tempMap);
			}
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
			} catch (Exception e) {
				map.put("doc_url", "");
				e.printStackTrace();
			}
		}
		list.clear();
		map.put("doc_url", docUtil.imageUrlFind(doc_ids));
		map.put("beikeMallList", beikeMallList);
		map.put("hongBaoList", hongBaoList);
		map.put("beikeStreetList", beikeStreetList);
		result.setResultData(map);
	}

	@Override
	public void findCommendGoods(Map<String, Object> paramsMap, ResultData result) throws Exception {
		paramsMap.put("is_finished", "N");
		List<String> activity_type_in = new ArrayList<>();
		activity_type_in.add(Constant.ACTIVITY_04);
		activity_type_in.add(Constant.ACTIVITY_05);
		activity_type_in.add(Constant.ACTIVITY_06);
		paramsMap.put("activity_type_in", activity_type_in);
		List<PsGoodsActivity> list = psGoodsActivityDao.selectPsGoodsActivity(paramsMap);
		List<String> doc_ids = new ArrayList<>();
		List<Map<String, Object>> recommendList = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		if (list == null || list.isEmpty()) {
			map.put("recommendList", recommendList);
			result.setResultData(map);
			return;
		}
		for (PsGoodsActivity psGoods : list) {
			Map<String, Object> tempMap = new HashMap<>();
			tempMap.put("goods_id", psGoods.getGoods_id());
			tempMap.put("goods_title", psGoods.getGoods_title());
			tempMap.put("goods_desc", psGoods.getGoods_desc());
			tempMap.put("goods_price", psGoods.getGoods_price());
			tempMap.put("goods_hongbao", psGoods.getGoods_hongbao());
			tempMap.put("goods_beike", psGoods.getGoods_beike());
			tempMap.put("activity_type", psGoods.getActivity_type());
			tempMap.put("market_price", psGoods.getMarket_price());
			tempMap.put("pic_info", StringUtil.formatStr(psGoods.getPic_info()));
			recommendList.add(tempMap);
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
			} catch (Exception e) {
				map.put("doc_url", "");
				e.printStackTrace();
			}
		}
		list.clear();
		map.put("doc_url", docUtil.imageUrlFind(doc_ids));
		map.put("recommendList", recommendList);
		result.setResultData(map);
	}

	@Override
	public void newYearGoodsFind(Map<String, Object> paramsMap, ResultData result) throws Exception {
		Map<String, Object> resultDataMap = new HashMap<>();
		// 查询可以购买的商品
		paramsMap.put("valid_thru_end", DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss));
		paramsMap.put("status", "on_shelf");
		paramsMap.put("label_promotion", "9");
		List<BeikeMallGoods> beikeMallGoodsList = psGoodsDao.selectBeiKeGoodsList(paramsMap);
		List<Map<String, Object>> yingCunList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> jianGuoList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> ziBuList = new ArrayList<Map<String, Object>>();
		for (BeikeMallGoods psGoods : beikeMallGoodsList) {
			Map<String, Object> psGoodsMap = new HashMap<>();
			psGoodsMap.put("goods_id", psGoods.getGoods_id());
			psGoodsMap.put("label", psGoods.getLabel());
			psGoodsMap.put("title", psGoods.getTitle());
			psGoodsMap.put("sale_price", psGoods.getSale_price());
			psGoodsMap.put("beike_credit", psGoods.getBeike_credit());
			psGoodsMap.put("vip_price", psGoods.getVip_price());
			psGoodsMap.put("sale_qty", psGoods.getSale_qty() + "");
			psGoodsMap.put("stock_qty", psGoods.getStock_qty() + "");
			psGoodsMap.put("goods_code", psGoods.getGoods_code());
			psGoodsMap.put("desc1", psGoods.getDesc1());
			psGoodsMap.put("sorted", psGoods.getSorted());
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
			if ("迎春礼包".equals(psGoods.getLabel())) {
				yingCunList.add(psGoodsMap);
			} else if ("坚果福袋".equals(psGoods.getLabel())) {
				jianGuoList.add(psGoodsMap);
			} else if ("滋补尚品".equals(psGoods.getLabel())) {
				ziBuList.add(psGoodsMap);
			}
		}
		resultDataMap.put("yingCunList", yingCunList);
		resultDataMap.put("jianGuoList", jianGuoList);
		resultDataMap.put("ziBuList", ziBuList);
		result.setResultData(resultDataMap);
	}
}
