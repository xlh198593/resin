package com.meitianhui.test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.common.util.HttpClientUtil;
import com.meitianhui.common.util.IDUtil;
import com.meitianhui.common.util.StringUtil;

public class TestConsumer {

	// @Test
	public void consumerSign() {
		try {
			String url = "http://127.0.0.1:8080/ops-member/member";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "consumer.consumer.consumerSign");
			Map<String, String> params = new HashMap<String, String>();
			params.put("consumer_id", "11613920");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void consumerFind_v1() {
		try {
			String url = "http://127.0.0.1:8080/ops-member/member";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "member.consumerFind_v1");
			Map<String, String> params = new HashMap<String, String>();
			params.put("member_id", "000cb940e7b911e89a9629040c0a10a8");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void favoriteStoreCancel() {
		try {
			String url = "http://127.0.0.1:8080/ops-member/member";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "member.favoriteStoreCancel");
			Map<String, String> params = new HashMap<String, String>();
			params.put("consumer_id", "012342e0cc4411e781641d14d21b0cce");
			params.put("stores_id", "ee98dfef8c604de88361553b7f5f3ad6,222,333");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void numberOfMembers() {
		try {
			String url = "http://127.0.0.1:8080/ops-member/member";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "member.numberOfMembers");
			Map<String, String> params = new HashMap<String, String>();  
			params.put("stores_id", "a4abb8ca63b941138f691bcf1cbc872f");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void memberInformationPage() {
		try {
			String url = "http://127.0.0.1:8080/ops-member/member";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "member.memberInformationPage");
			Map<String, String> params = new HashMap<String, String>();
			params.put("stores_id", "a4abb8ca63b941138f691bcf1cbc872f");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void mdConsumerVipTimeUpdate() {
		try {
			String url = "http://127.0.0.1:8080/ops-member/member";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "consumer.mdConsumerVipTimeUpdate");
			Map<String, String> params = new HashMap<String, String>();
			params.put("consumer_id", "843080200f4211e98ca3a185f61fd0f0");
			params.put("recharge_type", "year");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	@Test
	public void mdConsumerVipTest() {
		try {
			String url = "http://127.0.0.1:8080/ops-member/member";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "consumer.consumer.consumerBaseInfoFind");
			Map<String, String> params = new HashMap<String, String>();
			params.put("consumer_id", "0d917c20d0f611e794ff1b7699d69fc2");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void consumerVipLevel() {
		try {
			String url = "http://127.0.0.1:8080/ops-member/member";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "consumer.consumerVipLevel");
			Map<String, String> params = new HashMap<String, String>();
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void consumerVipTime() {
		try {
			String url = "http://127.0.0.1:8080/ops-member/member";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "consumer.consumer.consumerVipTime");
			Map<String, String> params = new HashMap<String, String>();
			params.put("consumer_id", "43698020dce111e88da45b14aad7edd4");
			params.put("orderCount", "1");
			params.put("orderMoney", "1.00");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			Map<String, Object> resultMap = FastJsonUtil.jsonToMap(result);
			Map<String, Object> consumerMap = (Map<String, Object>) resultMap.get("data");
			Long vipEndTime = (Long) consumerMap.get("vip_end_time");
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	@Test
	public void isMember() {
		try {
			String url = "http://127.0.0.1:8080/ops-member/member";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "consumer.consumerIsMember");
			Map<String, String> params = new HashMap<String, String>();
			params.put("member_id", "43698020dce111e88da45b14aad7edd4");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			Map<String, Object> resultMap = FastJsonUtil.jsonToMap(result);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void isMemberDistr() {
		try {
			String url = "http://127.0.0.1:8080/ops-member/member";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "consumer.consumerBuildingMemberDistrbution");
			Map<String, String> params = new HashMap<String, String>();
			params.put("member_id", "9ed4de00160c11e9ba6f654ec74cd035");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			Map<String, Object> resultMap = FastJsonUtil.jsonToMap(result);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void isMemberDistrRecharge() {
		try {
			String url = "http://127.0.0.1:8080/ops-member/member";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "consumer.updateMemberDistributionByRecharge");
			Map<String, String> params = new HashMap<String, String>();
			params.put("consumer_id", "08839b30e0e711e8b28993f8f4333e30");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			Map<String, Object> resultMap = FastJsonUtil.jsonToMap(result);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	@Test
	public void getMemberParentMobile() {
		try {
			String url = "http://127.0.0.1:8080/ops-member/member";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "consumer.consumerParentMobile");
			Map<String, String> params = new HashMap<String, String>();
			params.put("member_id", "c3a1cc20eb2811e8ba75a10e9c71637a");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			Map<String, Object> resultMap = FastJsonUtil.jsonToMap(result);
			Map<String, Object>   map = (Map<String, Object>) resultMap.get("data");
			System.out.println(result+"   "+map.get("mobile"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void getMemberIdFind() {
		try {
			String url = "http://127.0.0.1:8080/ops-member/member";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "member.memberIdFind");
			Map<String, String> params = new HashMap<String, String>();
			params.put("user_id", "b4d972b1f6894a158ecde019534f0715");
			params.put("member_type_key", "consumer");
			params.put("parentId", "e10616b0ecf711e8a262c3b8f69c93ff");
			params.put("type", "h5");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			Map<String, Object> resultMap = FastJsonUtil.jsonToMap(result);
			Map<String, Object>   map = (Map<String, Object>) resultMap.get("data");
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	@Test
	public void distrbutionFansManage() {
		try {
			String url = "http://127.0.0.1:8080/ops-member/member";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "consumer.consumerDistrbutionFansManage");
			Map<String, String> params = new HashMap<String, String>();
			Map<String, String> maps = new HashMap<String, String>();
			maps.put("page_size", "20");
			maps.put("page_no", "1");
			params.put("member_id", "ba0df070db5c11e8aa0813aa2729de6a");
			requestData.put("params", FastJsonUtil.toJson(params));
			requestData.put("page", FastJsonUtil.toJson(maps));
			String result = HttpClientUtil.post(url, requestData);
			Map<String, Object> resultMap = FastJsonUtil.jsonToMap(result);
			Map<String, Object>   map = (Map<String, Object>) resultMap.get("data");
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void memberManagerFans() {
		try {
			String url = "http://127.0.0.1:8080/ops-member/member";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "consumer.consumerManagerFansManage");
			Map<String, String> params = new HashMap<String, String>();
			Map<String, String> maps = new HashMap<String, String>();
			maps.put("page_size", "20");
			maps.put("page_no", "1");
			params.put("member_id", "72de63c00fc211e98ca3a185f61fd0f0");
			requestData.put("params", FastJsonUtil.toJson(params));
			requestData.put("page", FastJsonUtil.toJson(maps));
			String result = HttpClientUtil.post(url, requestData);
			Map<String, Object> resultMap = FastJsonUtil.jsonToMap(result);
			Map<String, Object>   map = (Map<String, Object>) resultMap.get("data");
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void findMemberInfoByInvitationCode() {
		try {
			String url = "http://127.0.0.1:8080/ops-member/member";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "member.findMemberInfoByInvitationCode");
			Map<String, String> params = new HashMap<String, String>();
			params.put("invite_code", "95055");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			Map<String, Object> resultMap = FastJsonUtil.jsonToMap(result);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void findInvitationCode() {
		try {
			String url = "http://127.0.0.1:8080/ops-member/member";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "member.findInvitationCode");
			Map<String, String> params = new HashMap<String, String>();
			params.put("member_id", "a8faf4178b1a4b1e9e86231e1b61bd51");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			Map<String, Object> resultMap = FastJsonUtil.jsonToMap(result);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public  void  createInviteCode() {
		Set<String>  strList =  new HashSet<String>();
		for(int i=0;i<700;i++) {
			strList.add(IDUtil.random(5));
		}
		Iterator<String> memberIdStr = strList.iterator();
		while (memberIdStr.hasNext()) {
			String memberId = memberIdStr.next();
			System.out.println(memberId);
		}
	}
	
	@Test
	public void memberDistrRecharge() {
		try {
			String url = "http://127.0.0.1:8080/ops-member/member";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "consumer.memberDistributionByRecharge");
			Map<String, String> params = new HashMap<String, String>();
			params.put("consumer_id", "184712b0f2ba11e88df6056b1fa82701");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			Map<String, Object> resultMap = FastJsonUtil.jsonToMap(result);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void consumerIsVip() {
		try {
			String url = "http://127.0.0.1:8080/ops-member/member";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "member.consumerIsVip");
			Map<String, String> params = new HashMap<String, String>();
			//params.put("mobile", "13874335689");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//
	@Test
	public void consumerPhoneUpdate() {
		try {
			String url = "http://127.0.0.1:8080/ops-member/member";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "member.consumerPhoneUpdate");
			Map<String, String> params = new HashMap<String, String>();
			params.put("user_id", "184712b0f2ba11e88df6056b1fa82701");
			params.put("mobile", "184712b0f2ba11e88df6056b1fa82701");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			Map<String, Object> resultMap = FastJsonUtil.jsonToMap(result);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//
	@Test
	public void hongbaoActivityInfo() {
		try {
			String url = "http://127.0.0.1:8080/ops-member/member";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "member.hongbaoActivityInfo");
			Map<String, String> params = new HashMap<String, String>();
			params.put("parent_id", "c6786e70c07811e8b39013707cf542bd");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			Map<String, Object> resultMap = FastJsonUtil.jsonToMap(result);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void drawHongbao() {
		try {
			String url = "http://127.0.0.1:8080/ops-member/member";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "member.drawHongbao");
			Map<String, String> params = new HashMap<String, String>();
			params.put("parent_id", "c6786e70c07811e8b39013707cf542bd");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			Map<String, Object> resultMap = FastJsonUtil.jsonToMap(result);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	@Test
	public void findMessage() {
		try {
			String url = "http://127.0.0.1:8080/ops-member/member";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "member.findMessage");
			Map<String, String> params = new HashMap<String, String>();
			Map<String, String> maps = new HashMap<String, String>();
			maps.put("page_size", "20");
			maps.put("page_no", "1");
			params.put("member_id", "eec49e3008d511e98fb8510f49e5b2d4");
			params.put("message_type", "order");
			requestData.put("params", FastJsonUtil.toJson(params));
			requestData.put("page", FastJsonUtil.toJson(maps));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void readMessage() {
		try {
			String url = "http://127.0.0.1:8080/ops-member/member";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "member.readMessage");
			Map<String, String> params = new HashMap<String, String>();
			params.put("message_id", "0d2ada5832a646f985e26ecc79dc5c5f");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
