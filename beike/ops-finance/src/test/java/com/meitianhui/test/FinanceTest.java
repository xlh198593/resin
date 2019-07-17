package com.meitianhui.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.junit.Test;

import com.meitianhui.common.exception.SystemException;
import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.common.util.HttpClientUtil;
import com.meitianhui.common.util.StringUtil;

public class FinanceTest {

	
	
	@Test
	public void memberPointEdit() {
		try {
			String url = "http://127.0.0.1:8080/ops-finance/finance";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "finance.consumer.memberPointEdit");
			Map<String, Object> params = new LinkedHashMap<String, Object>();
			params.put("member_id", "12105561");
			params.put("point_values", "5");
			params.put("booking_mark", "income");
			params.put("member_type_key", "consumer");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void memberCashLogListFind() {
		try {
			String url = "http://127.0.0.1:8080/ops-finance/finance";
			Map<String, String> requestData = new HashMap<>();
			requestData.put("service", "finance.memberCashLogPageFindNew");
			Map<String, Object> params = new LinkedHashMap<>();
			params.put("member_id", "10494374");
			params.put("member_type_key", "consumer");
			requestData.put("params", FastJsonUtil.toJson(params));
			Map<String, String> page = new HashMap<>();
			page.put("page_no", "1");
			page.put("page_size", "10");
			requestData.put("page", FastJsonUtil.toJson(page));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void memberAssetFind() {
		try {
			String url = "http://127.0.0.1:8080/ops-finance/finance";
			Map<String, String> requestData = new HashMap<>();
			requestData.put("service", "finance.memberAssetFind");
			Map<String, Object> params = new LinkedHashMap<>();
			params.put("member_id", "12105625");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void consumerWechatNotify() {
		try {
			String url = "http://127.0.0.1:8080/ops-finance/consumerWechatNotify";
			Map<String, String> requestData = new HashMap<>();
			requestData.put("result_code", "SUCCESS");
			requestData.put("return_code", "SUCCESS");
			requestData.put("out_trade_no", "102239015023011");
			requestData.put("wechat_notify_data", FastJsonUtil.toJson(requestData));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void findGiftCouponList() {
		try {
			String url = "http://127.0.0.1:8080/ops-finance/finance";
			Map<String, String> requestData = new HashMap<>();
			requestData.put("service", "finance.consumer.findGiftCouponType");
			Map<String, Object> params = new LinkedHashMap<>();
			params.put("member_id", "ece1d360ddb211e88fad2bd132815f0f");
			requestData.put("params", FastJsonUtil.toJson(params));
		/*	Map<String, String> page = new HashMap<>();
			page.put("page_no", "1");
			page.put("page_size", "10");
			requestData.put("page", FastJsonUtil.toJson(page));*/
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void updateGiftCoupon() {
		try {
			String url = "http://127.0.0.1:8080/ops-finance/finance";
			Map<String, String> requestData = new HashMap<>();
			requestData.put("service", "finance.consumer.updateGiftCoupon");
			Map<String, Object> params = new LinkedHashMap<>();
			params.put("member_id", "1cf625b0dddb11e8bf7287cc92a53858");
			params.put("coupons_id", "265,266");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void editMemberCoupon() {
		try {
			String url = "http://127.0.0.1:8080/ops-finance/finance";
			Map<String, String> requestData = new HashMap<>();
			requestData.put("service", "finance.consumer.editMemberCoupon");
			Map<String, Object> params = new LinkedHashMap<>();
			params.put("member_id", "c6786e70c07811e8b39013707cf542bd");
			params.put("is_New", "Y");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public  void testMap() {
		Map<String,Object>  map =  new HashMap<>();
		map.put("Status", 1);
		String amount = StringUtil.formatStr(map.get("amount"));
		if(StringUtils.isNotBlank(amount)) {
			System.out.println(111111);
		}
	}
	
	
	@Test
	public void findMemberBankBinding() {
		try {
			String url = "http://127.0.0.1:8080/ops-finance/finance";
			Map<String, String> requestData = new HashMap<>();
			requestData.put("service", "finance.consumer.findMemberBankBinding");
			Map<String, Object> params = new LinkedHashMap<>();
			params.put("member_id", "12746259");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void addFdMemberBankInfo() {
		try {
			String url = "http://127.0.0.1:8080/ops-finance/finance";
			Map<String, String> requestData = new HashMap<>();
			requestData.put("service", "finance.consumer.addFdMemberBankInfo");
			Map<String, Object> params = new LinkedHashMap<>();
			params.put("member_id", "12746259");
			params.put("member_type_key", "12746259");
			params.put("mobile", "12746259");
			params.put("proposer", "12746259");
			params.put("bank_code", "12746259");
			params.put("bank_account", "12746259");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void insertWithdrawal() {
		try {
			String url = "http://127.0.0.1:8080/ops-finance/finance";
			Map<String, String> requestData = new HashMap<>();
			requestData.put("service", "finance.consumer.insertWithdrawal");
			Map<String, Object> params = new LinkedHashMap<>();
			params.put("bank_id", "28506032f3c04b679f807d8ed29be906");
			params.put("amount", "100");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	@Test
	public void findGiftCouponList_v1() {
		try {
			String url = "http://127.0.0.1:8080/ops-finance/finance";
			Map<String, String> requestData = new HashMap<>();
			requestData.put("service", "finance.consumer.findGiftCouponList_v1");
			Map<String, Object> params = new LinkedHashMap<>();
			params.put("member_id", "e34f5680f6d311e8bff03bdb884a7794");
			requestData.put("params", FastJsonUtil.toJson(params)); 
			Map<String, String> page = new HashMap<>();
			page.put("page_no", "1");
			page.put("page_size", "10");
			requestData.put("page", FastJsonUtil.toJson(page));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void findGiftCouponSignInfo() {
		try {
			String url = "http://127.0.0.1:8080/ops-finance/finance";
			Map<String, String> requestData = new HashMap<>();
			requestData.put("service", "finance.consumer.findGiftCouponSignInfo");
			Map<String, Object> params = new LinkedHashMap<>();
			params.put("member_id", "4d501c60e89f11e8b25381e409051fda");
			requestData.put("params", FastJsonUtil.toJson(params)); 
			String result = HttpClientUtil.post(url, requestData); 
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void activationGiftCoupon() {
		try {
			String url = "http://127.0.0.1:8080/ops-finance/finance";
			Map<String, String> requestData = new HashMap<>();
			requestData.put("service", "finance.consumer.activationGiftCoupon");
			Map<String, Object> params = new LinkedHashMap<>();
			params.put("member_coupons_id", "862");
			requestData.put("params", FastJsonUtil.toJson(params)); 
			String result = HttpClientUtil.post(url, requestData); 
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void findGiftCouponTransform() {
		try {
			String url = "http://127.0.0.1:8080/ops-finance/finance";
			Map<String, String> requestData = new HashMap<>();
			requestData.put("service", "finance.consumer.findGiftCouponTransform");
			Map<String, Object> params = new LinkedHashMap<>();
			params.put("member_id", "4d501c60e89f11e8b25381e409051fda");
			params.put("coupons_key", "lj_399");
			requestData.put("params", FastJsonUtil.toJson(params)); 
			String result = HttpClientUtil.post(url, requestData); 
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void updateGiftCouponMun() {
		try {
			String url = "http://127.0.0.1:8080/ops-finance/finance";
			Map<String, String> requestData = new HashMap<>();
			requestData.put("service", "finance.consumer.updateGiftCouponMun");
			Map<String, Object> params = new LinkedHashMap<>();
			params.put("coupons_ids", "1117,1118,1116");
			params.put("coupons_key", "lj_399");
			requestData.put("params", FastJsonUtil.toJson(params)); 
			String result = HttpClientUtil.post(url, requestData); 
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public static void main(String[] args) throws SystemException {
		List<String>  strList =  new  ArrayList<String>();
		strList.add("10773750630978723840311781001");
		strList.add("10773752192727818241956041001");
		strList.add("10773774250950041608785661001");
		strList.add("10773779052715089925198321001");
		JSONArray jsonArray=new JSONArray(strList);
		String str  =  jsonArray.toString();
		List<String> noList =  FastJsonUtil.jsonToList(str);
		System.out.println(noList.size());
		
	}
	
	
	@Test
	public void giftCouponType() {
		try {
			String url = "http://127.0.0.1:8080/ops-finance/finance";
			Map<String, String> requestData = new HashMap<>();
			requestData.put("service", "finance.consumer.findGiftCouponType");
			Map<String, Object> params = new LinkedHashMap<>();
			requestData.put("params", FastJsonUtil.toJson(params)); 
			String result = HttpClientUtil.post(url, requestData); 
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void manageRateToBalance() {
		try {
			String url = "http://127.0.0.1:8080/ops-finance/finance";
			Map<String, String> requestData = new HashMap<>();
			requestData.put("service", "finance.consumer.manageRateToBalance");
			Map<String, Object> params = new LinkedHashMap<>();
			requestData.put("params", FastJsonUtil.toJson(params)); 
			String result = HttpClientUtil.post(url, requestData); 
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void findGiftCouponSignInfoForLastMonth() {
		try {
			String url = "http://127.0.0.1:8080/ops-finance/finance";
			Map<String, String> requestData = new HashMap<>();
			requestData.put("service", "finance.consumer.findGiftCouponSignInfoForLastMonth");
			Map<String, Object> params = new LinkedHashMap<>();
			params.put("member_id", "54a7b980ec7f11e8b67e7da70197f9fe");
			requestData.put("params", FastJsonUtil.toJson(params)); 
			String result = HttpClientUtil.post(url, requestData); 
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
