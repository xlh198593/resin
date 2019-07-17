package com.meitianhui.test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.meitianhui.common.util.DateUtil;
import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.common.util.HttpClientUtil;

public class PcOrderTest {

//	@Test
	public void pcOrderDetailPageFind() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/order";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "order.pcOrderDetailPageFind");
			Map<String, String> params = new HashMap<String, String>();
			params.put("order_id", "f93ef3c7e6e04d48b40bec1406fd55bb");
			requestData.put("params", FastJsonUtil.toJson(params));
			Map<String, String> page = new HashMap<String, String>();
			page.put("page_no", "1");
			page.put("page_size", "20");
			requestData.put("page", FastJsonUtil.toJson(page));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void pcOrderListFind() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/order";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "pcOrder.operation.pcOrderListFind");
			Map<String, String> params = new HashMap<String, String>();
			params.put("order_id", "f93ef3c7e6e04d48b40bec1406fd55bb");
			requestData.put("params", FastJsonUtil.toJson(params));
			Map<String, String> page = new HashMap<String, String>();
			page.put("page_no", "1");
			page.put("page_size", "20");
			requestData.put("page", FastJsonUtil.toJson(page));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	@Test
	public void pcOrderSellCount() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/order";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "pcOrder.stores.pcOrderSellCount");
			Map<String, String> params = new HashMap<String, String>();
			params.put("stores_id", "88e7aff8bfc548eaa4a3872931ff9fe3");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void pcOrderPayCreateNotify() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/order";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "order.pcOrderPayCreateNotify");
			Map<String, String> params = new HashMap<String, String>();
			params.put("contact_tel", "15803817013");
			params.put("remark", "111111111");
			params.put("consumer_id", "6051b83028da11e8b2e3436403f30aef");
			params.put("item_num", "1");
			params.put("stores_id", "5fa0b7ba-f252-11e5-8f52-00163e0009c6");
			params.put("stores_name", "丁戈测试加盟店");
			params.put("payment_way_key", "ZFFS_09");
			params.put("delivery_address", "车公庙泰然科技园");
			params.put("goods_code", "XS153605099450");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void beikeStreetOrderCreateNotify() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/order";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "order.beikeStreetOrderCreateNotify");
			Map<String, String> params = new HashMap<String, String>();
			params.put("contact_tel", "15803817013");
			params.put("remark", "111111111");
			params.put("consumer_id", "6051b83028da11e8b2e3436403f30aef");
			params.put("item_num", "1");
			params.put("stores_id", "a1c1d38f7df743e2bcf878a4fbb6c6c9");
			params.put("stores_name", "丁戈测试加盟店");
			params.put("payment_way_key", "ZFFS_09");
			params.put("delivery_address", "车公庙泰然科技园");
			params.put("item_store_id", "0142614b3a1c4877b330b8a5c00a6ac2");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
	
	@Test
	public  void  test() {
		Date vipEndDate = DateUtil.parseToDate(DateUtil.TimeStamp2Date("1621958400000".substring(0, "1621958400000".length()-3), "yyyy-MM-dd HH:mm:ss"));
		System.out.println("newDate:"+vipEndDate);
	}
	
}
