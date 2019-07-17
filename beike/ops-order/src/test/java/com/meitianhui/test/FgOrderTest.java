package com.meitianhui.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

import com.alibaba.fastjson.JSONArray;
import com.meitianhui.common.util.DateUtil;
import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.common.util.HttpClientUtil;

public class FgOrderTest {
	

	@Test
	public void fgOrderListPageFind() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/order";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "order.fgOrderListPageFind");
			Map<String, String> params = new HashMap<String, String>();
			params.put("member_id", "e1bd20c02c0f11e8b2e3436403f30aef");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void rcOrderPageListFind() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/order";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "order.consumer.rcOrderPageListFind");
			Map<String, String> params = new HashMap<String, String>();
			params.put("member_id", "12105511");
			requestData.put("params", FastJsonUtil.toJson(params));
			Map<String, String> page = new HashMap<>();
			page.put("page_no", "1");
			page.put("page_size", "20");
			requestData.put("page", FastJsonUtil.toJson(page));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	@Test
	public void fgOrderViolationCount() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/order";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "order.fgOrderViolationCount");
			Map<String, String> params = new HashMap<String, String>();
			params.put("member_id", "20000001");
			params.put("member_type_key", "stores");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
//	@Test
	public void qualificationValidate() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/order";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "tsActivity.consumer.qualificationValidate");
			Map<String, String> params = new HashMap<String, String>();
			params.put("consumer_id", "20000001");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//@Test
	public void fgOrderSupplyCount() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/order";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "fgOrder.supply.fgOrderSupplyCount");
			Map<String, String> params = new HashMap<String, String>();
			params.put("supplier_id", "17ca2a7a83094fe69da31df5047914ce");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//@Test
	public void fgOrderDetailFind() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/order";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "order.operation.fgOrderDetailFind");
			Map<String, String> params = new HashMap<String, String>();
			params.put("external_order_no", "86483486555555555555");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	//@Test
	public void fgOrderListForOpPageFind() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/order";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "order.fgOrderListForOpPageFind");
			Map<String, String> params = new HashMap<String, String>();
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
	
	//@Test
	public void fgOrderListForMoneyFind() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/order";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "order.fgOrderListForMoneyFind");
			Map<String, String> params = new HashMap<String, String>();
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		
		} catch (Exception e) {
			e.printStackTrace();
			
		}
	}
	
	//@Test
	public void fgOrderFormMoneyTabulationFind() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/order";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "order.fgOrderFormMoneyTabulationFind");
			Map<String, String> params = new HashMap<String, String>();
			params.put("goods_code","FS151177321578");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		
		} catch (Exception e) {
			e.printStackTrace();
			
		}
	}

	 //@Test
	public void freeGetOrderImport() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/order";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "order.freeGetOrderImport");
			Map<String, String> params = new HashMap<String, String>();
			params.put("data_source", "SJLY_03");
			params.put("goods_code", "FS149225005844");
			params.put("external_order_no", "55681236845668441269");
			params.put("contact_person", "张三");
			params.put("external_buyer_name", "18718688437");
			params.put("contact_tel", "13881237612");
			params.put("delivery_address", "深圳市福田区车公庙泰然科技园");
			Map<String, String> params233232 = new HashMap<String, String>();
			params233232.put("company", "百世汇通");
			params233232.put("number", "1354546546汇通");
			params.put("logistics",params233232.toString());
			params.put("amount", "100");
			params.put("order_type", "自营");
			params.put("qty", "5");
			params.put("transaction_no", "9585816593508638725634391001");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	@Test
	public void fgOrderSettlement() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/order";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "order.fgOrderSettlement");
			Map<String, String> params = new HashMap<String, String>();
			params.put("order_date", "2016-12-01");
			params.put("operator", "广东省公司|刘涛");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void fgOrderRevoke() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/order";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "order.fgOrderRevoke");
			Map<String, String> params = new HashMap<String, String>();
			params.put("order_id", "ba7d8a131fea474fb0408aed1d0e126b");
			params.put("biz_remark", "店东返佣测试撤销反馈的情况");
			params.put("operator", "2121212");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	@Test
	public void fgOrderCancelled() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/order";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "order.fgOrderCancelled");
			Map<String, String> params = new HashMap<String, String>();
			params.put("data_source", "SJLY_03");
			params.put("order_id", "fe44bbcd1c154dda983ede8bacf20854");
			params.put("remark", "广东省公司|刘涛");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	@Test
	public void fgOrderCreateNotity() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/order";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "order.fgOrderCreateNotity");
			Map<String, String> params = new HashMap<String, String>();
			params.put("data_source", "SJLY_01");
			params.put("order_id", "d4f94cf3621a43c18b87962a45d8e040");
			params.put("payment_way_key", "ZFFS_01");
			params.put("delivery_address", "四川省成都市锦江区水碾河南街u37创意仓库7栋");
			params.put("contact_person", "丁戈");
			params.put("contact_tel", "13018089936");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	

//	@Test
	public void consumerFreeGetOrderCancelled() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/order";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "order.consumerFreeGetOrderCancelled");
			Map<String, String> params = new HashMap<String, String>();
			params.put("order_id", "10e7acea136f4d5aa4657b648e38cb91");
			params.put("remark", "不想领取了");
			params.put("data_source", "SJLY_02");
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
	public void handleFgOrderCreateNotity() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/order";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "order.fgOrderCreateNotity");
			Map<String, String> params = new HashMap<String, String>();
			params.put("order_id", "002b697ba3ff45a080730316293a3509");
			params.put("data_source", "taobao");
			params.put("payment_way_key", "ZFFS_02");
			params.put("delivery_address", "中国,北京,北京市,朝阳区嗯嗯嗯嗯嗯嗯嗯呢嫩嗯嗯嗯恩恩额嗯呢讷讷呢");
			params.put("contact_person", "小六");
			params.put("contact_tel", "18219203688");
			params.put("transaction_no", "222222222");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void selectFgOrderCommissionList() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/order";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "order.fgOrderCommissionListPageFind");
			Map<String, String> params = new HashMap<String, String>();
			params.put("stores_id", "96c23f04239443018d7bde9b004075c3");
			requestData.put("params", FastJsonUtil.toJson(params));
			Map<String, String> page = new HashMap<String, String>();
			page.put("page_no", "1");
			page.put("page_size", "50");
			requestData.put("page", FastJsonUtil.toJson(page));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void fgOrderCommissionToAmountMemberTotal() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/order";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "order.fgOrderCommissionToAmountMemberTotal");
			Map<String, String> params = new HashMap<String, String>();
			params.put("stores_id", "860d8371-e433-11e5-a165-00163e010763");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void fgOrderrCommissionWeekAmountPageFind() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/order";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "order.fgOrderrCommissionWeekAmountPageFind");
			Map<String, String> params = new HashMap<String, String>();
			params.put("stores_id", "860d8371-e433-11e5-a165-00163e010763");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void fgOrderListByNewOwnPageFind() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/order";
			Map<String, String> requestData = new HashMap<String, String>();//order.consumer.fgOrderListByOwnPageFind
			requestData.put("service", "order.consumer.fgOrderListByOwnPageFind");
			Map<String, String> params = new HashMap<String, String>();
			params.put("consumer_id", "db8020b0290611e8b2e3436403f30aef");
			params.put("member_id", "db8020b0290611e8b2e3436403f30aef");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void fgShareListFind() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/order";
			Map<String, String> requestData = new HashMap<>();
			requestData.put("service", "order.consumer.fgShareListFind");
			Map<String, String> params = new HashMap<String, String>();
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void handleFreeGetOrderImport() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/order";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "order.freeGetOrderImport");
			Map<String, String> params = new HashMap<String, String>();
			params.put("goods_code", "538461844251");
			params.put("qty", "1");
			params.put("contact_person", "桑华");
			params.put("contact_tel", "18718688437");
			params.put("delivery_address", "山东省聊城市茌平县茌平公路局家属院");
			params.put("logistics", "1111111");
			params.put("transaction_no", "9762733704200888321008151001");
			params.put("data_source", "SJLY_08");
			params.put("external_order_no", "985255558886694665");
			params.put("external_buyer_name", "13076946007");
			params.put("amount", "0.04");
			params.put("order_type", "自营");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void OwnOrderCreate() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/order";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "order.consumer.newOwnOrderCreate");
			Map<String, String> params = new HashMap<String, String>();
			params.put("data_source", "SJLY_01");
			params.put("order_type", "meitianhui");
			params.put("contact_person", "健康");
			params.put("mobile", "15626584674");
			params.put("recommend_stores_id", "3295a61d-dece-11e5-8f52-00163e0009c6");
			params.put("contact_tel", "15626584674");
			params.put("goods_code", "FS151807033979");
			params.put("nick_name", "");
			params.put("delivery_address", "北京北京市东城区开学就参谋参谋");
			params.put("consumer_id", "db8020b0290611e8b2e3436403f30aef");
			params.put("qty", "1");
			params.put("remark", "");
			params.put("sku_id", "");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void rcOrderCreate() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/order";
			Map<String, String> requestData = new HashMap<>();
			requestData.put("service", "order.rcOrderCreate");
			Map<String, Object> params = new HashMap<>();
			params.put("transaction_no", "112233445566");
			params.put("amount", "1.00");
			params.put("payment_way_key", "ZFFS_01");
			params.put("member_type_key", "consumer");
			params.put("member_id", "12566814");
			params.put("remark", "充值订单");
			params.put("data_source", "SJLY_01");
			params.put("trade_type_key", "JYLX_01");
			params.put("out_trade_no", "111122223333444");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void consumerFreeGetRecordCreate() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/order";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "order.consumerFreeGetRecordCreate");
			Map<String, String> params = new HashMap<String, String>();
			params.put("goods_code", "FS152661617033");
			params.put("consumer_id", "6051b83028da11e8b2e3436403f30aef");
			params.put("recommend_stores_id", "5fa0b7ba-f252-11e5-8f52-00163e0009c6");
			params.put("mobile", "15803817013");
			params.put("channel_id", "1");
			params.put("goods_url", "111");
			params.put("mobile", "15803817013");
			params.put("stores_name", "丁戈测试加盟店");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void beikeMallOrderCreate() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/order";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "order.consumer.beikeMallOrderCreate");
			Map<String, String> params = new HashMap<String, String>();
			params.put("data_source", "SJLY_01");
			params.put("coupons_id_lq", "268");
			params.put("contact_person", "哈哈");
			params.put("mobile", "15809369746");
			params.put("delivery_address", "北京北京市东城区啦啦啦");
			params.put("goods_code", "FS154201557164");
			params.put("nick_name", "哈哈");
			params.put("sku_id", "86e33c422237434ebc152f17dee6b75f");
			params.put("consumer_id", "56903250d1d511e8b97f512a26f6fd83");
			params.put("qty", "1");
			params.put("remark", "ddsss");
			params.put("coupons_id_my", "");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void hongbaoOrderCreate() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/order";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "order.consumer.hongbaoOrderCreate");
			Map<String, String> params = new HashMap<String, String>();
			params.put("data_source", "SJLY_01");
			params.put("contact_person", "睡醒没");
			params.put("payment_way_key", "ZFFS_09");
			params.put("mobile", "17771413163");
			params.put("delivery_address", "北京北京市东城区您可以");
			params.put("contact_tel", "17771413163");
			params.put("goods_code", "FS154572879840");
			params.put("nick_name", "睡醒没");
			params.put("consumer_id", "ba0df070db5c11e8aa0813aa2729de6a");
			params.put("qty", "1");
			params.put("remark", "");
			params.put("coupons_id", "8452");
			params.put("goods_id", "");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void beikeMallOrderListByNewOwnPageFind() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/order";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "order.consumer.beikeMallOrderListByNewOwnPageFind");
			Map<String, String> params = new HashMap<String, String>();
			params.put("member_id", "12024518");
			params.put("status", "wait_seller_send_goods");
			requestData.put("params", FastJsonUtil.toJson(params));
			Map<String, String> page = new HashMap<String, String>();
			page.put("page_no", "1");
			page.put("page_size", "50");
			requestData.put("page", FastJsonUtil.toJson(page));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void hongBaoOrderListByNewOwnPageFind() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/order";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "order.consumer.hongBaoOrderListByNewOwnPageFind");
			Map<String, String> params = new HashMap<String, String>();
			params.put("member_id", "87c8bda0125911e987ef11dc09f08b06");
			params.put("status", "wait_buyer_pay");
			requestData.put("params", FastJsonUtil.toJson(params));
			Map<String, String> page = new HashMap<String, String>();
			page.put("page_no", "1");
			page.put("page_size", "50");
			requestData.put("page", FastJsonUtil.toJson(page));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void beikeMallOrderCancelled() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/order";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "order.beikeMallOrderCancelled");
			Map<String, String> params = new HashMap<String, String>();
			params.put("data_source", "SJLY_01");
			params.put("order_id", "871");
			params.put("remark", "刚认识的人");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void hongBaoOrderCancelled() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/order";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "order.hongBaoOrderCancelled");
			Map<String, String> params = new HashMap<String, String>();
			params.put("data_source", "SJLY_01");
			params.put("order_id", "08fc48579bd64f509abfe09d8ff65ec7");
			params.put("remark", "广东省公司|XINGTL");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	@Test
	public void beikeStreetOrdeListByPageFind() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/order";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "order.consumer.beikeStreetOrdeListByPageFind");
			Map<String, String> params = new HashMap<String, String>();
			params.put("consumer_id", "6051b83028da11e8b2e3436403f30aef");
			requestData.put("params", FastJsonUtil.toJson(params));
			Map<String, String> page = new HashMap<String, String>();
			
			page.put("page_no", "1");
			page.put("page_size", "50");
			requestData.put("page", FastJsonUtil.toJson(page));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void beikeStreetOrderCancelled() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/order";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "order.beikeStreetOrderCancelled");
			Map<String, String> params = new HashMap<String, String>();
			params.put("order_id", "1c0b11ffeac344479b5ff844d0cb70f7");
			params.put("remark", "广东省公司|XINGTL");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void beikeMallOrderUpdate() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/order";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "order.consumer.beikeMallOrderUpdate");
			Map<String, String> params = new HashMap<String, String>();
			params.put("order_no", "105975652240012492811");
			params.put("payment_way_key", "ZFFS_08");
			params.put("redpacket_fee", "36.2");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
@Test
	public  void getBeiKeMallOrder() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/order";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "order.consumer.beikeMallOrderDetailFind");
			Map<String, String> params = new HashMap<String, String>();
			params.put("order_no", "10491181479179100161001");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}@Test
	public void beikeMallOrderForAutoInterface() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/order";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "order.consumer.beikeMallOrderForAutoInterface");
			Map<String, String> params = new HashMap<String, String>();
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void beikeMallOrderForConfirmReceipt() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/order";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "order.beikeMallOrderForConfirmReceipt");
			Map<String, String> params = new HashMap<String, String>();
			params.put("order_id", "510");
			params.put("member_id", "fc8b7500bbb611e89fe8df57a53019a1");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void beikeMallOrderShell() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/order";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "order.consumer.beikeMallOrderShell");
			Map<String, String> params = new HashMap<>();
			params.put("member_id", "ba0df070db5c11e8aa0813aa2729de6a");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Test
	public  void  testDate() {
		String  str =DateUtil.addDate(DateUtil.getFormatDate(DateUtil.fmt_yyyyMMddHHmmss),
				DateUtil.fmt_yyyyMMddHHmmss, 1, 1);
		Date   date  = DateUtil.str2Date(str, "yyyy-MM-dd HH:mm:ss");
		
		System.out.println("测试时间:"+str+"++;:"+date);
	}


	@Test
	public void beikeMallOrderDetail() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/order";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "order.consumer.beikeMallOrderDetail");
			Map<String, String> params = new HashMap<>();
			params.put("order_no", "10662629759946629121001");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	@Test
	public void doGet() {
		try {
			String url = "https://wuliu.market.alicloudapi.com/kdi";
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("Authorization", "APPCODE 4494ac1d36904c98a1c1a766d03fc1fc");
			Map<String, String> params = new HashMap<>();
			params.put("no", "462587770684");
			params.put("type", "zto");
			String result = HttpClientUtil.doGet(url, params, headers);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	@Test
	public void hongbaoOrderDetail() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/order";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "order.consumer.hongbaoOrderDetail");
			Map<String, String> params = new HashMap<>();
			params.put("order_no", "107750294645794816011");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	@Test
	public void findhongbaoOrder() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/order";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "order.consumer.findHongBaoOrderInfoByNo");
			Map<String, String> params = new HashMap<>();
			params.put("order_no", "10629660272838615041001");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void hongbaoOrderAnnul() {
		try {
			String url = "http://127.0.0.1:8080/ops-order/order";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "order.hongbaoOrderAnnul");
			Map<String, String> params = new HashMap<>();
			params.put("order_ids", "405,404,403");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	 public  void testPayOrder() {
		 try {
				String url = "http://127.0.0.1:8080/ops-order/order";
				Map<String, String> requestData = new HashMap<String, String>();
				requestData.put("service", "order.consumer.rcOrderPayNotify");
				Map<String, String> params = new HashMap<>();
				params.put("member_id", "27276390fea211e8841c7f8ce07da938");
				params.put("out_trade_no", "20181213153732209143339499878752842146954");
				params.put("payment_way_key", "ZFFS_02");
				params.put("transaction_no", "10731197144718417920753771001");
				requestData.put("params", FastJsonUtil.toJson(params));
				String result = HttpClientUtil.post(url, requestData);
				System.out.println(result);
			} catch (Exception e) {
				e.printStackTrace();
			}
	 }
	
	
	@Test
	 public  void testTelePhoneOrder() {
		 try {
			 	List<Object>  strList =  new  ArrayList<Object>();
				strList.add("10773750630978723840311781001");
				strList.add("10773752192727818241956041001");
				strList.add("10773774250950041608785661001");
				strList.add("10773779052715089925198321001");
				JSONArray jsonArray=new JSONArray(strList);
				String str  =  jsonArray.toString();
				String url = "http://127.0.0.1:8080/ops-order/order";
				Map<String, String> requestData = new HashMap<String, String>();
				requestData.put("service", "order.consumer.telephoneOrderStatus");
				Map<String, String> params = new HashMap<>();
				params.put("transactionNoList", str);
				requestData.put("params", FastJsonUtil.toJson(params));
				String result = HttpClientUtil.post(url, requestData);
				System.out.println(result);
			} catch (Exception e) {
				e.printStackTrace();
			}
	 }
}

