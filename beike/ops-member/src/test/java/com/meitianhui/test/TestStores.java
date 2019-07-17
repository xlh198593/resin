package com.meitianhui.test;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.common.util.HttpClientUtil;

public class TestStores {

	// @Test
	public void storeListPageFind() {
		try {
			String url = "http://127.0.0.1:8080/ops-member/member";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "stores.consumer.storesTypeGroupFind");
			Map<String, String> params = new HashMap<String, String>();
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	 @Test
	public void nearbyHSPostListPageFind() {
		try {
			String url = "http://127.0.0.1:8080/ops-member/member";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "stores.consumer.nearbyHSPostListPageFind");
			Map<String, String> params = new HashMap<String, String>();
			params.put("longitude", "122.035096");
			params.put("latitude", "37.399296");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	@Test
	public void stageStoresListPageFind() {
		try {
			String url = "http://127.0.0.1:8080/ops-member/member";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "stores.operate.stageStoresListPageFind");
			Map<String, String> params = new HashMap<String, String>();
			Map<String, String> page = new HashMap<String, String>();
			page.put("page_no", "1");
			page.put("page_size", "20");
			requestData.put("page", FastJsonUtil.toJson(page));
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	@Test
	public void stageStoresEdit() {
		try {
			String url = "http://127.0.0.1:8080/ops-member/member";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "stores.operate.stageStoresEdit");
			Map<String, String> params = new HashMap<String, String>();
			Map<String, String> page = new HashMap<String, String>();
			params.put("stores_id", "68aada35208c414097465471f501c888");
			params.put("is_stage_hpt", "Y");
			requestData.put("page", FastJsonUtil.toJson(page));
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void favoriteStoreEdit() {
		try {
			String url = "http://127.0.0.1:8080/ops-member/member";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "stores.consumer.favoriteStoreEdit");
			Map<String, String> params = new HashMap<String, String>();
			Map<String, String> page = new HashMap<String, String>();
			params.put("stores_id", "111111111");
			params.put("consumer_id", "0047eaa4e22d46288a93c132749f14ef");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void storesBaseInfoFind() {
		try {
			String url = "http://127.0.0.1:8080/ops-member/member";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "stores.stores.storesBaseInfoFind");
			Map<String, String> params = new HashMap<String, String>();
			Map<String, String> page = new HashMap<String, String>();
			params.put("stores_id", "45fe2f6e6ae141afbfbd67a62c843e01");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void favoriteStore() {
		try {
			String url = "http://127.0.0.1:8080/ops-member/member";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "stores.consumer.favoriteStore");
			Map<String, String> params = new HashMap<String, String>();
			Map<String, String> page = new HashMap<String, String>();
			params.put("stores_id", "111111111");
			params.put("consumer_id", "0047eaa4e22d46288a93c132749f14ef");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
