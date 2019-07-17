package com.meitianhui.test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;

import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.common.util.HttpClientUtil;
import com.meitianhui.common.util.IDUtil;

public class InterfaceTest {

	@Test
		public void salesmanLogin() {
			try {
				String url = "http://127.0.0.1:8080/ops-infrastructure/user";
				Map<String, String> requestData = new HashMap<String, String>();
				requestData.put("service", "infrastructure.salesmanLogin");
				Map<String, Object> params = new LinkedHashMap<String, Object>();
				params.put("mobile", "14795781694");
				params.put("request_info", "127.0.0.1");
				params.put("data_source", "SJLY_01");
				requestData.put("params", FastJsonUtil.toJson(params));
				String result = HttpClientUtil.post(url, requestData);
				System.out.println(result);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
	@Test
	public void appValidate() {
		try {
			String url = "http://127.0.0.1:8080/ops-infrastructure/infrastructure";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "infrastructure.appValidate");
			Map<String, Object> params = new LinkedHashMap<String, Object>();
			params.put("app_token", "42b0a3cc6e6ddd2146a105dc6c3e60911fed6a0b53745436aa1cc0d503e0a540a8c05dc4e9b7d66ba8be8ddc9539b31c");
			params.put("request_info", "127.0.0.1");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void appTokenAuth() {
		try {
			String url = "http://127.0.0.1:8080/ops-infrastructure/infrastructure";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "infrastructure.appTokenAuth");
			Map<String, Object> params = new LinkedHashMap<String, Object>();
			params.put("app_id", "bcc1cf2e-b376-11e5-921c-fcaa1490ccaf");
			params.put("private_key", "690b98fcb37711e5921cfcaa1490ccaf717130d1b37711e5921cfcaa1490ccaf");
			params.put("request_info", "127.0.0.1");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// @Test
	public void payPasswordSetting() {
		try {
			String url = "http://127.0.0.1:8080/ops-infrastructure/user";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "infrastructure.payPasswordSetting");
			Map<String, Object> params = new LinkedHashMap<String, Object>();
			params.put("user_id", "10531921");
			params.put("payment_password", "test");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// @Test
	public void paySecurityOptions() {
		try {
			String url = "http://127.0.0.1:8080/ops-infrastructure/user";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "infrastructure.paySecurityOptions");
			Map<String, Object> params = new LinkedHashMap<String, Object>();
			params.put("user_id", "10531921");
			params.put("small_direct", "Y");
			params.put("sms_notify", "Y");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// @Test
	public void validateCheckCode() {
		try {
			String url = "http://127.0.0.1:8080/ops-infrastructure/infrastructure";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "infrastructure.validateCheckCode");
			Map<String, Object> params = new LinkedHashMap<String, Object>();
			params.put("mobile", "18676794743");
			params.put("check_code", "4379");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// @Test
	public void appInfo() {
		try {
			String url = "http://127.0.0.1:8080/ops-infrastructure/infrastructure";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "infrastructure.appInfo");
			Map<String, Object> params = new LinkedHashMap<String, Object>();
			params.put("app_id", "04fefd7e-ad6b-11e5-9ba7-fcaa1490ccaf");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void consumerUserRegister() {
		try {
			String url = "http://127.0.0.1:8080/ops-infrastructure/user";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "infrastructure.consumerUserRegister");
			Map<String, Object> params = new LinkedHashMap<String, Object>();
			params.put("mobile", "13059111117");
			params.put("password", "123456");
			params.put("member_id", "ecb2da70058e11e999236fe6e615345c");
			params.put("check_code", "48112");
			params.put("data_source", "H5");
			params.put("type", "h5");
			params.put("user_id",IDUtil.getUUID());
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// @Test
	public void shumeLoginRegister() {
		try {
			String url = "http://127.0.0.1:8080/ops-infrastructure/infrastructure";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "infrastructure.shumeLoginRegister");
			Map<String, Object> params = new LinkedHashMap<String, Object>();
			params.put("mobile", "13018089936");
			params.put("device_id", "e10adc3949ba59abbe56e057f20f883e");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// @Test
	public void shumeLoginVerify() {
		try {
			String url = "http://127.0.0.1:8080/ops-infrastructure/infrastructure";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "infrastructure.shumeLoginVerify");
			Map<String, Object> params = new LinkedHashMap<String, Object>();
			params.put("mobile", "13018089936");
			params.put("device_id", "e10adc3949ba59abbe56e057f20f883e");
			params.put("code", "e10adc3949ba59abbe56e057f20f883e");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void consumerLogin() {
		try {
			String url = "http://127.0.0.1:8080/ops-infrastructure/user";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "infrastructure.consumerLogin");
			Map<String, Object> params = new LinkedHashMap<String, Object>();
			params.put("mobile", "13800000115");
			params.put("password", "9aa94c2d30e67db45c01c0a7f0b8f4f2");
			params.put("member_type_key", "consumer");
			params.put("request_info", "1ce8a08b-63c1-3e6a-a84d-7fb820cdb1be");
			params.put("data_source", "local_life");
			params.put("invite_code", "27590");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	

	// @Test
	public void userLoginForOAuth() {
		try {
			String url = "http://127.0.0.1:8080/ops-infrastructure/user";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "infrastructure.consumerLoginForOAuth");
			Map<String, Object> params = new LinkedHashMap<String, Object>();
			params.put("user_account", "18818706542");
			params.put("password", "96e79218965eb72c92a549dd5a330112");
			params.put("request_info", "127.0.0.1");
			params.put("member_type_key", "consumer");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// @Test
	public void consumerInfoFindForOAuth() {
		try {
			String url = "http://127.0.0.1:8080/ops-infrastructure/user";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "infrastructure.consumerInfoFindForOAuth");
			Map<String, Object> params = new LinkedHashMap<String, Object>();
			params.put("user_id", "10192099");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// @Test
	public void storeLogin() {
		try {
			String url = "http://127.0.0.1:8080/ops-infrastructure/user";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "infrastructure.storeLogin");
			Map<String, Object> params = new LinkedHashMap<String, Object>();
			params.put("user_account", "13018089936");
			params.put("request_info", "127.0.0.1");
			params.put("data_source", "SJLY_02");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void userFindByMobile() {
		try {
			String url = "http://127.0.0.1:8080/ops-infrastructure/user";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "infrastructure.userFindByMobile");
			Map<String, Object> params = new LinkedHashMap<String, Object>();
			params.put("mobile", "13018089900");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// @Test
	public void memberTypeValidateByMobile() {
		try {
			String url = "http://127.0.0.1:8080/ops-infrastructure/user";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "infrastructure.memberTypeValidateByMobile");
			Map<String, Object> params = new LinkedHashMap<String, Object>();
			params.put("mobile", "13018089936");
			params.put("member_type_key", "stores");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// @Test
	public void memberTypeValidate() {
		try {
			String url = "http://127.0.0.1:8080/ops-infrastructure/user";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "infrastructure.memberTypeValidate");
			Map<String, Object> params = new LinkedHashMap<String, Object>();
			params.put("user_account", "13018089936");
			params.put("member_type_key", "stores");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// @Test
	public void memberTypeFindByMobile() {
		try {
			String url = "http://127.0.0.1:8080/ops-infrastructure/user";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "infrastructure.memberTypeFindByMobile");
			Map<String, Object> params = new LinkedHashMap<String, Object>();
			params.put("mobile", "13018089936");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// @Test
	public void userEdit() {
		try {
			String url = "http://127.0.0.1:8080/ops-infrastructure/user";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "infrastructure.userEdit");
			Map<String, Object> params = new LinkedHashMap<String, Object>();
			// params.put("user_id", "12f27687-a7c3-11e5-bcf8-fcaa1490ccaf");
			params.put("user_token", "eff20f2c6b605fe2df0fb31a3bc21ba5");
			params.put("mobile", "1256454");
			params.put("im1", "44444444444444");
			params.put("im2", "1111111111111");
			params.put("im3", "333333333333333");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// @Test
	public void userFind() {
		try {
			String url = "http://127.0.0.1:8080/ops-infrastructure/user";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "infrastructure.userFind");
			Map<String, Object> params = new LinkedHashMap<String, Object>();
			params.put("token", "eff20f2c6b605fe2df0fb31a3bc21ba5");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	 @Test
	public void userPasswordChange() {
		try {
			String url = "http://127.0.0.1:8080/ops-infrastructure/user";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "infrastructure.userPasswordChange");
			Map<String, Object> params = new LinkedHashMap<String, Object>();
			params.put("user_id", "e95521aa69484f3b9e35b23b66138e40");
			params.put("old_password", "e10adc3949ba59abbe56e057f20f883e");
			params.put("new_password", "123456");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	 @Test
	public void userPasswordReset() {
		try {
			String url = "http://127.0.0.1:8080/ops-infrastructure/user";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "infrastructure.userPasswordReset");
			Map<String, Object> params = new LinkedHashMap<String, Object>();
			params.put("mobile", "15012650695");
			// 123456
			params.put("password", "e10adc3949ba59abbe56e057f20f883e");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// @Test
	public void transactionRegister() {
		try {
			String url = "http://127.0.0.1:8080/ops-infrastructure/infrastructure";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "infrastructure.transactionRegister");
			Map<String, String> params = new HashMap<String, String>();
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// @Test
	public void userValidate() {
		try {
			String url = "http://127.0.0.1:8080/ops-infrastructure/user";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "infrastructure.userValidateNoRequestInfo");
			Map<String, String> params = new HashMap<String, String>();
			params.put("user_token",
					"12153d5e2c901e64ed717531c92f591f7dd98482aa2eccaaddb4172d47d655b6e335796b5ecd2413a47cad87dcb233a6");
			params.put("request_info", "FHSnnm1j");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// @Test
	public void transactionVerify() {
		try {
			String url = "http://127.0.0.1:8080/ops-infrastructure/infrastructure";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "infrastructure.transactionVerify");
			Map<String, String> params = new HashMap<String, String>();
			params.put("flow_no", "741124020195");
			params.put("security_code", "FHSnnm1j");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// @Test
	public void feedback() {
		try {
			String url = "http://127.0.0.1:8080/ops-infrastructure/infrastructure";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "infrastructure.feedback");
			Map<String, String> params = new HashMap<String, String>();
			params.put("category", "意见");
			params.put("desc1", "youbug");
			params.put("data_source", "SJLY_01");
			params.put("contact", "张三");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// @Test
	public void feedbackPageFind() {
		try {
			String url = "http://127.0.0.1:8080/ops-infrastructure/infrastructure";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "infrastructure.feedbackPageFind");
			Map<String, String> params = new HashMap<String, String>();
			params.put("category", "意见");
			requestData.put("params", FastJsonUtil.toJson(params));
			Map<String, String> page = new HashMap<String, String>();
			page.put("page_no", "1");
			page.put("page_size", "5");
			requestData.put("page", FastJsonUtil.toJson(page));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void  testWeChat() {
		try {
			String url = "http://127.0.0.1:8080/ops-infrastructure/user";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "infrastructure.consumerWeChatLogin");
			Map<String, String> params = new HashMap<String, String>();
			params.put("openid", "123456789");
			params.put("data_source", "wechat");
			params.put("device_model", "iphone 6");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void  testMobileLogin() {
		try {
			String url = "http://127.0.0.1:8080/ops-infrastructure/user";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "infrastructure.mobileLogin");
			Map<String, String> params = new HashMap<String, String>();
			params.put("mobile", "18612825566");
			params.put("data_source", "wechat");
			params.put("request_info", "iphone 6");
			params.put("member_type_key", "consumer");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	@Test
	public void  testRegister() {
		try {
			String url = "http://127.0.0.1:8080/ops-infrastructure/user";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "infrastructure.consumerUserRegister");
			Map<String, String> params = new HashMap<String, String>();
			params.put("user_id", "123456789");
			params.put("mobile", "13059115329");
			params.put("password", "123456");
			params.put("type", "wechat");
			params.put("openid", "147258");
			params.put("nickname", "yixiu");
			params.put("sex", "1");
			params.put("city", "sz");
			params.put("province", "gd");
			params.put("country", "CN");
			params.put("headimgurl", "120");
			params.put("unionid", "10");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void  mobileLogin() {
		try {
			String url = "http://127.0.0.1:8080/ops-infrastructure/user";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "infrastructure.mobileLogin");
			Map<String, String> params = new HashMap<String, String>();
			params.put("member_type_key", "consumer");
			params.put("mobile", "13059115329");
			params.put("request_info", "123456");
			params.put("data_source", "SJLY_01");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void  testAPI() {
		try {
			String url = "http://weiqing.meitianhui.com/addons/ewei_shopv2/payment/huipay/userDataLog.php";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("api_type", "1");
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	
	//infrastructure.userValidateNoRequestInfo
	//42b0a3cc6e6ddd2146a105dc6c3e60911fed6a0b53745436aa1cc0d503e0a540a8c05dc4e9b7d66ba8be8ddc9539b31c
	@Test
	public void  testJedis() {
		try {
			String url = "http://127.0.0.1:8080/ops-infrastructure/user";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "infrastructure.userValidateNoRequestInfo");
			Map<String, String> params = new HashMap<String, String>();
			params.put("user_token", "c9a4463f825145b112a4ec7a61b5e7b4477ff07bfaa2a8500528dc17fcf488a373d6809efa157aa9058c055b9624a08d");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void userMemberRegister() {
		try {
			String url = "http://127.0.0.1:8080/ops-infrastructure/user";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "infrastructure.userMemberRegister");
			Map<String, Object> params = new LinkedHashMap<String, Object>();
			params.put("mobile", "15323748897");
			params.put("password", "123456");
			params.put("user_id",IDUtil.getUUID());
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void loginMobileChange() {
		try {
			String url = "http://127.0.0.1:8080/ops-infrastructure/user";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "infrastructure.loginMobileChange");
			Map<String, Object> params = new LinkedHashMap<String, Object>();
			params.put("mobile", "13059115329");
			params.put("user_id","b1324c51c12e4fd1aaf22a9a4022cf68");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
