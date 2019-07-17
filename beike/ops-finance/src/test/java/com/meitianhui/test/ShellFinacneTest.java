package com.meitianhui.test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;
import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.common.util.HttpClientUtil;
import com.meitianhui.common.util.StringUtil;

public class ShellFinacneTest {
	
	@Test
	public void  testAlipay() {
		try {
			String url ="http://127.0.0.1:8080/ops-finance/ShellFinance";
			Map<String, String> requestData = new HashMap<>();
			requestData.put("service", "orderPay.shellRecharge");
			
			Map<String, Object> params = new LinkedHashMap<>();
			params.put("data_source", "SJLY_01");
			params.put("payment_way_key", "ZFFS_01");
			params.put("amount", "8");
			params.put("member_id", "6051b83028da11e8b2e3436403f30aef");
			params.put("trade_type_key", "JYLX_01");
			params.put("detail", "个人充值");
			params.put("trade_type_key", "JYLX_01");
			params.put("currency_code", "人民币");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (SystemException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	@Test
	public void  testTelephoneCharge() {
		try {
			String url ="http://127.0.0.1:8080/ops-finance/ShellFinance";
			Map<String, String> requestData = new HashMap<>();
			requestData.put("service", "orderPay.telephoneRecharge");
			
			Map<String, Object> params = new LinkedHashMap<>();
			params.put("data_source", "SJLY_01");
			params.put("out_trade_no", "201812252332156183679563048566964088626163");
			params.put("payment_way_key", "ZFFS_02");
			params.put("amount", "48.00");
			params.put("member_id", "e34f5680f6d311e8bff03bdb884a7794");
			params.put("trade_type_key", "JYLX_01");
			params.put("shell", 2);
			params.put("mobile", "19926420263");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (SystemException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	public static void main(String[] args) {
		BigDecimal tradeAmount =  new BigDecimal("48.00");
		if(tradeAmount.compareTo(new BigDecimal("48")) !=0) {
			System.out.println("++++++++++++");
		}else {
			System.out.println("+++22222222222");
		}
	}
	

}
