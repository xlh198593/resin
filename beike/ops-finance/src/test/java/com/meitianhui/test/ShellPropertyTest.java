package com.meitianhui.test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.common.util.HttpClientUtil;

public class ShellPropertyTest {
	@Test
	public void consumerFdMemberAssetShellLogFind() {
		try {
			String url = "http://127.0.0.1:8080/ops-finance/ShellProperty";
			Map<String, String> requestData = new HashMap<>();
			requestData.put("service", "consumer.consumerFdMemberAssetShellLogFind");
			Map<String, Object> params = new LinkedHashMap<>();
			params.put("member_id", "10810950324e11e8bc1fa9e3c784f465");
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
	public void consumerFdMemberAssetPointLogFind() {
		try {
			String url = "http://127.0.0.1:8080/ops-finance/ShellProperty";
			Map<String, String> requestData = new HashMap<>();
			requestData.put("service", "consumer.consumerFdMemberAssetPointLogFind");
			Map<String, Object> params = new LinkedHashMap<>();
			params.put("member_id", "10810950324e11e8bc1fa9e3c784f465");
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
	public void consumerFdMemberAssetCashLogFind() {
		try {
			String url = "http://127.0.0.1:8080/ops-finance/ShellProperty";
			Map<String, String> requestData = new HashMap<>();
			requestData.put("service", "consumer.consumerFdMemberAssetCashLogFind");
			Map<String, Object> params = new LinkedHashMap<>();
			params.put("member_id", "111111111");
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
	public void consumerMemberRabatePageFind() {
		try {
			String url = "http://127.0.0.1:8080/ops-finance/ShellProperty";
			Map<String, String> requestData = new HashMap<>();
			requestData.put("service", "consumer.memberRabatePageFind");
			Map<String, Object> params = new LinkedHashMap<>();
			params.put("member_id", "ba0df070db5c11e8aa0813aa2729de6a");
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
}
