package com.meitianhui.goods.service.impl;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.util.StringUtil;
import com.meitianhui.common.util.ValidateUtil;
import com.meitianhui.goods.constant.RspCode;
import com.meitianhui.goods.dao.HongbaoGoodsDao;
import com.meitianhui.goods.entity.HongBaoGoods;
import com.meitianhui.goods.service.HongbaoGoodsService;

@Service
public class HongbaoGoodsServiceImpl implements HongbaoGoodsService {
	
	@Autowired
	private HongbaoGoodsDao hongbaoGoodsDao;

	@Override
	public void hongbaoGoodsSaleQtyUpdate(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "goods_id", "qty" });
		Map<String, Object> tempMap = new HashMap<>();
		List<String> goods_ids = StringUtil.str2List(StringUtil.formatStr(paramsMap.get("goods_id")), ",");
		tempMap.put("goods_ids", goods_ids);
		int updateFlag = hongbaoGoodsDao.hongbaoGoodsUpdate(paramsMap);
		if (updateFlag == 0) {
			throw new BusinessException(RspCode.PROCESSING, RspCode.PROCESSING);
		}
	}
	
	@Override
	public void hongbaoGoodsFindForOrder(Map<String, Object> paramsMap, ResultData result)
			throws Exception {
		// goods_id 和 goods_code 必须有一个
		ValidateUtil.validateParamsNum(paramsMap, new String[] { "goods_id", "goods_code" }, 1);
		List<HongBaoGoods> hongBaoGoods = hongbaoGoodsDao.selectHongbaoGoodsList(paramsMap);
		if (null == hongBaoGoods || hongBaoGoods.isEmpty()) {
			throw new BusinessException(RspCode.PS_GOODS_NOT_EXIST, RspCode.MSG.get(RspCode.PS_GOODS_NOT_EXIST));
		}
		HongBaoGoods goods = hongBaoGoods.get(0);
		Map<String,Object> temp = new HashMap<>();
		temp.put("hongBaoGoods", goods);
		result.setResultData(temp);
	}
}
