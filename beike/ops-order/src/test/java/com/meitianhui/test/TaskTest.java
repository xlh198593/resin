package com.meitianhui.test;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.common.util.HttpClientUtil;

public class TaskTest {

	
//	@Test
	public void tsActivityCheck() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/orderTask";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "order.tsActivityCheck");
			Map<String, String> params = new HashMap<String, String>();
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
//	@Test
	public void tsOrderAutoReceived() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/orderTask";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "order.tsOrderAutoReceived");
			Map<String, String> params = new HashMap<String, String>();
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
//	@Test
	public void tsActivityAutoSettle() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/orderTask";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "order.tsActivityAutoSettle");
			Map<String, String> params = new HashMap<String, String>();
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	

//	@Test
	public void odTaskTimeout() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/orderTask";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "order.odTaskTimeout");
			Map<String, String> params = new HashMap<String, String>();
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void transmatic() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/orderTask";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "order.transmaticByFgOrderCommission");
			Map<String, String> params = new HashMap<String, String>();
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void fgOrderAutoCancel() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/orderTask";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "order.fgOrderAutoCancel");
			Map<String, String> params = new HashMap<>();
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void beikeMallOrderForOwnCancel() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/orderTask";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "order.beikeMallOrderForOwnCancel");
			Map<String, String> params = new HashMap<>();
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void hongBaoOrderForOwnCancel() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/orderTask";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "order.hongBaoOrderForOwnCancel");
			Map<String, String> params = new HashMap<>();
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
