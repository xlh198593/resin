package com.meitianhui.test;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.meitianhui.common.util.DateUtil;
import com.meitianhui.common.util.HttpClientUtil;
import com.meitianhui.finance.util.DESUtils;

import net.sf.json.JSONObject;

public class TestTradeChange {

	//测试环境
//	private static final String base_url = "http://test.17bst.cn/charge-api/trade/";
//	private static final String AppId="378cb034df";  //384b4dfb55
//	private static final String AppKey="NPu4q6nBVE5R2VGo"; //  62NLEhuzrPCOlL0i
//	private static final String tradePwd= "e3ceb5881a0a1fdaad01296d7554868d";
	
	//正式环境
	private static final String base_url = "http://platform.17bst.com/charge-api/trade/";
	private static final String AppId="c40ff84c89";  //384b4dfb55
	private static final String AppKey="OMHqrShs"; //  62NLEhuzrPCOlL0i
	private static final String tradePwd= "48764404bcee081ba9377e41f5f4ece3";//48764404bcee081ba9377e41f5f4ece3
	
	
	//{"data":{"reqStreamId":"2018121811350005698405","orderNo":"A154546673816355851","balance":0,"applyTime":"2018-12-22 16:18:58",
	//"chargeNumBalance":0,"checkTime":null},"status":1011,"order":null,"msg":"充值提交成功"}
	public static void main(String[] args) {
		try {
//			String encryptStr=DESUtils.encrypt("http://test.17bst.cn/charge-api/trade/trade/", AppKey);
//			System.out.println(encryptStr);			
//			String decryptStr=DESUtils.decrypt(encryptStr, AppKey);
//			System.out.println(decryptStr);
			tradeRecharge();
		} catch (Exception e) {			
			e.printStackTrace();
		}
	}
	
	
	public  static String tradeRecharge() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("reqStreamId", DateUtil.date2Str(new Date(), "yyMMddHHmmssSSS"));//流水号
		params.put("agtPhone","13590120770");//代理号码  测试：18664940068
		params.put("chargeAddr","17512029274");//充值号码		
		params.put("chargeType","1");//充值类型
		params.put("chargeMoney","10");//充值金额（元
		params.put("tradePwd",tradePwd);//交易密码 --32位md5小写
		params.put("notifyUrl","http://bek12345.com/trade/notify");//回调地址
		
		String result1 = requestUrl("charge", "chat.getIMUserAccount", params);
		JSONObject result = JSONObject.fromObject(result1);
		String status  = result.get("status")+"";
		Map<String,Object> maps = JSON.parseObject(result.getString("data")); 
		//String reqStreamId=maps.get("reqStreamId")+"";//流水号
		String orderNo=maps.get("orderNo")+"";//平台订单号
		Double balance=Double.parseDouble(maps.get("balance")+"");	//充值后代理商保证金余额
		String applyTime=maps.get("applyTime")+"";	//交易时间
		Double chargeNumBalance=Double.parseDouble(maps.get("chargeNumBalance")+"");
		
		System.out.println("status:"+status+";orderNo:"+orderNo);
		
		return result.toString();
	}		
	
	//6、订单查询
	public static String getOrderInfo() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("reqStreamId", "201812181135000569845");//流水号
		params.put("agtPhone","18664940068");//代理号码
		String result = requestUrl("getOrderInfo", "chat.getIMUserAccount", params);
		JSONObject json = JSONObject.fromObject(result);
		return json.toString();
	}

	private static String requestUrl(String url, String service, Map<String, Object> params){
		try{
			Map<String, String> reqParams = new HashMap<String, String>();
			String paramStr=JSONObject.fromObject(params).toString();
			reqParams.put("appId", AppId);
			reqParams.put("param", DESUtils.encrypt(paramStr, AppKey));
			System.out.println(JSONObject.fromObject(reqParams));
			String result = HttpClientUtil.postCharge(base_url + url, JSONObject.fromObject(reqParams));
			System.out.println(result);
			return result;
		} catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
}
