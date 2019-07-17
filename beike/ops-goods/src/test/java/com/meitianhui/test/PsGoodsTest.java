package com.meitianhui.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.common.util.HttpClientUtil;

public class PsGoodsTest {

	@Test
	public void freeGetGoodsByLabelListPageFind() {
		try {
			String url = "http://127.0.0.1:8080/ops-goods/goods";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "psGoods.consumer.freeGetGoodsByLabelListPageFind");
			Map<String, String> params = new HashMap<String, String>();
			params.put("label_promotion", "flashSale");
			params.put("version", "zeroPurchase");
			params.put("time", "15");
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
	public void gdFreeGetGoodsByLabelListPageFind() {
		try {
			String url = "http://127.0.0.1:8080/ops-goods/goods";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "psGoods.consumer.gdFreeGetGoodsByLabelListPageFind");
			Map<String, String> params = new HashMap<String, String>();
			params.put("label_promotion", "flashSale");
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

	// @Test
	public void freeGetGoodsForOperateListFind() {
		try {
			String url = "http://127.0.0.1:8080/ops-goods/goods";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "psGoods.operate.freeGetGoodsForOperateListFind");
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

	// @Test
	public void wpyGoodsSkuSync() {
		try {
			String url = "http://127.0.0.1:8080/ops-goods/goods";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "goods.wpyGoodsSkuSync");
			Map<String, String> params = new HashMap<String, String>();
			List<Map<String, Object>> goods_sku_list = new ArrayList<Map<String, Object>>();
			Map<String, Object> sku1 = new HashMap<String, Object>();
			sku1.put("goods_id", "416466f0b47b48c8b510d283dab64eb2");
			sku1.put("goods_stock_id", "fad4022a-bc66-11e6-a5d0-00163e000086");
			sku1.put("sku", "S");
			sku1.put("desc1", "小码");
			sku1.put("cost_price", "5.00");
			sku1.put("sales_price", "5.00");
			sku1.put("sale_qty", "10");
			goods_sku_list.add(sku1);
			Map<String, Object> sku2 = new HashMap<String, Object>();
			sku2.put("goods_id", "416466f0b47b48c8b510d283dab64eb2");
			sku2.put("goods_stock_id", "");
			sku2.put("sku", "M");
			sku2.put("desc1", "中码");
			sku2.put("cost_price", "5.00");
			sku2.put("sales_price", "5.00");
			sku2.put("sale_qty", "10");
			goods_sku_list.add(sku2);
			params.put("goods_sku_list", FastJsonUtil.toJson(goods_sku_list));
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// @Test
	public void psGoodsStockFind() {
		try {
			String url = "http://127.0.0.1:8080/ops-goods/goods";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "goods.psGoodsStockFind");
			Map<String, String> params = new HashMap<String, String>();
			params.put("goods_id", "1ac5e0d8fe9c4dbe8b1a361ce841e60f");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// @Test
	public void wypGoodsFindForH5() {
		try {
			String url = "http://127.0.0.1:8080/ops-goods/goods";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "goods.wypGoodsFindForH5");
			Map<String, String> params = new HashMap<String, String>();
			params.put("area_id", "440304");
			requestData.put("params", FastJsonUtil.toJson(params));
			Map<String, String> page = new HashMap<String, String>();
			page.put("page_no", "1");
			page.put("page_size", "10");
			requestData.put("page", FastJsonUtil.toJson(page));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// @Test
	public void freeGetGoodsListPageFind() {
		try {
			String url = "http://127.0.0.1:8080/ops-goods/goods";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "psGoods.consumer.freeGetGoodsListPageFind");
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

	@Test
	public void freeGetGoodsPreSaleListPageFind() {
		try {
			String url = "http://127.0.0.1:8080/ops-goods/goods";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "goods.freeGetGoodsPreSaleListPageFind");
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

	// @Test
	public void freeGetGoodsNewestPageFind() {
		try {
			String url = "http://127.0.0.1:8080/ops-goods/goods";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "goods.freeGetGoodsNewestPageFind");
			Map<String, String> params = new HashMap<String, String>();
			params.put("area_id", "440304");
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

	// @Test
	public void goldExchangeGoodsDetailFind() {
		try {
			String url = "http://127.0.0.1:8080/ops-goods/goods";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "goods.goldExchangeGoodsDetailFind");
			Map<String, String> params = new HashMap<String, String>();
			params.put("goods_id", "2957d632f1a449a4acae31bb9df65ce1");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// @Test
	public void recommendStoresGoodsListFind() {
		try {
			String url = "http://127.0.0.1:8080/ops-goods/goods";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "goods.recommendStoresGoodsListFind");
			Map<String, String> params = new HashMap<String, String>();
			params.put("goods_id", "2531c0f36d6e466390b5380f18cc3b5f");
			params.put("area_id", "2531c0f36d6e466390b5380f18cc3b5f");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void psGoodsSync() {
		String url = "http://127.0.0.1:8080/ops-goods/goods";
		Map<String, String> requestData = new HashMap<String, String>();
		requestData.put("service", "goods.psGoodsSync");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("goods_id", "481f5db06af24bec9b9caf5rta45d8gf");
		params.put("goods_code", "16061309");
		params.put("title", "【家居必备】晾衣架");
		params.put("desc1", "工厂直批，一 卡 10 个，20卡起批包邮");
		params.put("area_id", "440000");
		params.put("display_area", "440000");
		params.put("label", "文艺");
		params.put("category", "新品");
		params.put("contact_person", "邢先生");
		params.put("contact_tel", "15014314649");
		params.put("pic_info", "[{'path_id':'4fb812d4696846189955d7914a5984a5','title':''}]");
		params.put("specification", "5*10");
		params.put("pack", "包");
		params.put("cost_price", "0.50");
		params.put("market_price", "1.00");
		params.put("discount_price", "0.00");
		params.put("shipping_fee", "0.00");
		params.put("min_buy_qty", "2");
		params.put("max_buy_qty", "20");
		params.put("sale_qty", "1000");
		params.put("stock_qty", "1000");
		params.put("goods_unit", "个");
		params.put("valid_thru", "2016-08-11");
		params.put("delivery_area", "440300");
		params.put("payment_way", "online");
		params.put("status", "normal");
		params.put("created_date", "2016-06-11 18:30:30");
		params.put("ladder_price", "{}");
		try {
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// @Test
	public void psGoodsEdit() {
		try {
			String url = "http://127.0.0.1:8080/ops-goods/goods";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "goods.psGoodsEdit");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("goods_id", "3deab1ba676148b9b3cb776d5ddd0696");
			params.put("status", "off_shelf");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// @Test
	public void psGoodsNewEdit() {
		try {
			String url = "http://127.0.0.1:8080/ops-goods/goods";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "goods.psGoodsNewEdit");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("goods_id", "3deab1ba676148b9b3cb776d5ddd0696");
			// params : { "goods_id", "title", "market_price",
			// "discount_price","desc1","category","data_source","goods_code","pic_info"
			// })
			params.put("title", "11");
			params.put("market_price", "12");
			params.put("discount_price", "13");
			params.put("desc1", "14");
			params.put("category", "15");
			params.put("data_source", "16");
			params.put("goods_code", "17");
			params.put("pic_info", "18");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// @Test
	public void psGoodsDelete() {
		try {
			String url = "http://127.0.0.1:8080/ops-goods/goods";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "goods.psGoodsDelete");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("goods_id", "0e4c8e8024ce4beca515bf57913038ef");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// @Test
	public void psGoodsActivitySync() {
		try {
			String url = "http://127.0.0.1:8080/ops-goods/goods";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "goods.psGoodsActivitySync");
			Map<String, Object> params = new HashMap<String, Object>();
			List<Map<String, Object>> paramsList = new ArrayList<Map<String, Object>>();
			Map<String, Object> temp1 = new HashMap<String, Object>();
			temp1.put("goods_id", "1ac5e0d8fe9c4dbe8b1a361ce841e60f");
			temp1.put("pic_info", "[{\"path_id\":\"df6bf7da62ae4c34b9319b380000dc3e\",\"title\":\"\"}]");
			temp1.put("order_no", "1");
			paramsList.add(temp1);
			Map<String, Object> temp2 = new HashMap<String, Object>();
			temp2.put("goods_id", "1ac5e0d8fe9c4dbe8b1a361ce841e60f");
			temp2.put("pic_info", "[{\"path_id\":\"df6bf7da62ae4c34b9319b380000dc3e\",\"title\":\"\"}]");
			temp2.put("order_no", "2");
			paramsList.add(temp2);
			params.put("activity_params", FastJsonUtil.toJson(paramsList));
			params.put("activity_type", "HDMD_04");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// @Test
	public void psGoodsSupplyCount() {
		try {
			String url = "http://127.0.0.1:8080/ops-goods/goods";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "psGoods.supply.psGoodsSupplyCount");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("supplier_id", "86387627d39345a79dd5338681c27cbe");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// @Test
	public void storesActivityCount() {
		try {
			String url = "http://127.0.0.1:8080/ops-goods/goods";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "goods.storesActivityCount");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("stores_id", "3295a61d-dece-11e5-8f52-00163e0009c6");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// @Test
	public void psGoodsActivityFind() {
		try {
			String url = "http://127.0.0.1:8080/ops-goods/goods";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "goods.psGoodsActivityFind");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("activity_type", "HDMS_06");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void psGoodsSync2() {
		String url = "http://127.0.0.1:8080/ops-goods/goods";
		Map<String, String> requestData = new HashMap<String, String>();
		requestData.put("service", "goods.psGoodsSync");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("area_id", "440304");
		params.put("contact_tel", "17097217172");
		params.put("delivery_area", "100000");
		params.put("discount_end", "2017-11-09 23:59:59");
		params.put("pic_info", "[{'title':'','path_id':'0bdb5df01b4647db8bf892aabfb33f28'}]");
		params.put("data_source", "hsrj");
		params.put("min_buy_qty", "0");
		params.put("desc1", "经常穿高跟鞋的姑娘，脚上都有老茧，基本上用了一天脚垫就范白了，不刺激皮肤~");
		params.put("title", "【云南本草】祛脚底鸡眼手脚老茧膏");
		params.put("max_buy_qty", "0");
		params.put("token", "9f7b23aa7f164ce68fa58697f30c9804");
		params.put("discount_price", "0.00");
		params.put("goods_code", "550928554796");
		params.put("payment_way", "online");
		params.put("taobao_price", "35.90");
		params.put("ladder_price", "0");
		params.put("cost_price", "0.00");
		params.put("status", "normal");
		params.put("contact_person", "厉先生");
		params.put("commission_rate", "90.50");
		params.put("voucher_link",
				"http://shop.m.taobao.com/shop/coupon.htm?seller_id=2336283349&activity_id=0d39b3f435fc49bfa3477eef073cc707");
		params.put("shipping_fee", "0.00");
		params.put("category", "试样");
		params.put("data_source_type", "0");
		params.put("market_price", "5.90");
		params.put(" manufacturer", "硕果旗舰店");
		params.put("goods_id", "c429ffc32d8b42b6aa2d86f770ff4f54");
		params.put("display_area", "家饰家纺,百货,餐厨,家庭保健");
		params.put("quan_id", "0d39b3f435fc49bfa3477eef073cc707");
		params.put("pic_detail_info", "481f5db06af24bec9b9caf5rta45d8gf");
		params.put("goods_id",
				"[{'title':'','path_id':'5acce689064a42bca65bd7ba42378323'},{'title':'','path_id':'f9201ae644d34cf58592381f54d170ce'},{'title':'','path_id':'dbb9264e744340e1b424cd33da706a37'},{'title':'','path_id':'a4e5c37725904717a97cfbb98ac20e13'},{'title':'','path_id':'c33c9d33156046ee928abeb4c9961905'}]");
		try {
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void psGoodsDetailFind() {
		try {
			String url = "http://127.0.0.1:8080/ops-goods/goods";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "goods.psGoodsDetailFind");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("goods_id", "15bce8873a80453397cbfa89ea69cee7");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void gdFreeGetGoodsDetailFind() {
		try {
			String url = "http://127.0.0.1:8080/ops-goods/goods";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "goods.gdFreeGetGoodsDetailFind");
			Map<String, Object> params = new HashMap<String, Object>();
			// params.put("goods_id", "0071238e6fa5890da049f1e6e7fcdf3a");
			params.put("goods_id", "3bbca0e426a847738a074799fedf6337");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void freeGetGoodsListFindForOwn() {
		try {
			String url = "http://127.0.0.1:8080/ops-goods/goods";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "psGoods.consumer.freeGetGoodsForNewOwnListPageFind");
			Map<String, Object> params = new HashMap<String, Object>();
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
	public void newPsGoodsSaleQtyDeduction() {
		try {
			String url = "http://127.0.0.1:8080/ops-goods/goods";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "goods.newPsGoodsSaleQtyDeduction");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("goods_id", "9bdbb71d4cfe4dd3b5a61c0dd474ffcd");
			params.put("sell_qty", "10");
			params.put("sku_id", "022ff1b6420442808bcefb849515eabd");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void newPsGoodsSaleQtyRestore() {
		try {
			String url = "http://127.0.0.1:8080/ops-goods/goods";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "goods.newPsGoodsSaleQtyRestore");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("goods_id", "9bdbb71d4cfe4dd3b5a61c0dd474ffcd");
			params.put("restore_qty", "10");
			// params.put("sku_id", "022ff1b6420442808bcefb849515eabd");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void selectPsGoodsSkuid() {
		try {
			String url = "http://127.0.0.1:8080/ops-goods/goods";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "goods.selectPsGoodsSkuid");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("goods_code", "FS154089726185");
			// params.put("attr_zvalue", "33");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void selectPsGoodsSkuidBySkuId() {
		try {
			String url = "http://127.0.0.1:8080/ops-goods/goods";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "goods.selectPsGoodsSkuidBySkuId");
			Map<String, Object> params = new HashMap<String, Object>();
			List<String> list = new ArrayList<>();
			list.add("30d0286c7f9b486493a09983bd1cce5c");
			params.put("sku_ids", list);
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void less9PsGoodsListPageFind() {
		try {
			String url = "http://127.0.0.1:8080/ops-goods/goods";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "psGoods.consumer.less9PsGoodsListPageFindNewOwn");
			Map<String, Object> params = new HashMap<String, Object>();
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
	public void freeGetGoodsByLabelListPageFindForOwn() {
		try {
			String url = "http://127.0.0.1:8080/ops-goods/goods";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "psGoods.consumer.freeGetGoodsForOwnByLabelListPageFind");
			Map<String, String> params = new HashMap<String, String>();
			params.put("label_promotion", "日用家居");
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
	public void gdFreegetGoodsListPageFind() {
		try {
			String url = "http://127.0.0.1:8080/ops-goods/goods";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "psGoods.consumer.gdFreegetGoodsListPageFind");
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

	@Test
	public void gdFreeGetGoodsFindForOrder() {
		try {
			String url = "http://127.0.0.1:8080/ops-goods/goods";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "goods.gdFreeGetGoodsFindForOrder");
			Map<String, String> params = new HashMap<String, String>();
			params.put("goods_code", "FS152661617033");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void selectPsGoodsforGoodsCodeSet() {
		try {
			String url = "http://127.0.0.1:8080/ops-goods/goods";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "psGoods.consumer.selectPsGoodsforGoodsCodeSet");
			Map<String, Object> params = new HashMap<>();
			Set<String> set = new HashSet<>();
			set.add("FS152661617033");
			set.add("FS152663294591");
			set.add("hg3499");
			set.add("BS148116971836");
			set.add("TS149035253693");
			params.put("goodsCode_set", set);
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void psUnionAllGoodsListFind() {
		try {
			long startTime = System.currentTimeMillis();
			String url = "http://127.0.0.1:8080/ops-goods/goods";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "goods.psUnionAllGoodsListFind");
			Map<String, String> page = new HashMap<String, String>();
			page.put("page_no", "1");
			page.put("page_size", "100");
			Map<String, String> params = new HashMap<String, String>();
			params.put("goods_like", "hg6551");
			requestData.put("params", FastJsonUtil.toJson(params));
			requestData.put("page", FastJsonUtil.toJson(page));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
			long endTime = System.currentTimeMillis();
			System.err.println("该程序执行了"+(endTime-startTime)+"秒");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void beiKeGoodsForListPageFind() {
		try {
			String url = "http://127.0.0.1:8080/ops-goods/goods";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "psGoods.consumer.beiKeGoodsForListPageFind");
			Map<String, Object> params = new HashMap<String, Object>();
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
	public void hongbaoGoodsForListPageFind() {
		try {
			String url = "http://127.0.0.1:8080/ops-goods/goods";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "psGoods.consumer.hongbaoGoodsForListPageFind");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("label_promotion", "7");
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
	public void beiKeGoodsForListByLabelListPageFind() {
		try {
			String url = "http://127.0.0.1:8080/ops-goods/goods";
			Map<String, String> requestData = new HashMap<String, String>(); 
			requestData.put("service", "psGoods.consumer.beiKeGoodsForListByLabelListPageFind");
			Map<String, Object> params = new HashMap<>();
			params.put("label_promotion", "母婴用品");
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
	
	
	@Test
	public void beikeMallGoodsFindForOrder() {
		try {
			String url = "http://127.0.0.1:8080/ops-goods/goods";
			Map<String, String> requestData = new HashMap<String, String>(); 
			requestData.put("service", "goods.beikeMallGoodsFindForOrder");
			Map<String, Object> params = new HashMap<>();
			params.put("goods_code", "FS152429673442");
			params.put("sku_id", "022ff1b6420442808bcefb849515eabd");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void beikeMallGoodsDetailFind() {
		try {
			String url = "http://127.0.0.1:8080/ops-goods/goods";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "goods.beikeMallGoodsDetailFind");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("goods_id", "33");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void homeGoodsFind() {
		try {
			String url = "http://127.0.0.1:8080/ops-goods/goods";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "goods.homeGoodsFind");
			Map<String, Object> params = new HashMap<String, Object>();
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void vipGoodsForListPageFind() {
		try {
			String url = "http://127.0.0.1:8080/ops-goods/goods";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "psGoods.consumer.vipGoodsForListPageFind");
			Map<String, Object> params = new HashMap<String, Object>();
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void itemStorePageFindForConsumer() {
		try {
			String url = "http://127.0.0.1:8080/ops-goods/goods";
			Map<String, String> requestData = new HashMap<String, String>(); 
			requestData.put("service", "goods.itemStorePageFindForConsumer");
			Map<String, Object> params = new HashMap<>();
			params.put("store_id", "ee98dfef8c604de88361553b7f5f3ad6");
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

	@Test
	public void beikeMallGoodsDetailFindNew() {
		try {
			String url = "http://127.0.0.1:8080/ops-goods/goods";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "goods.beikeMallGoodsDetailFindNew");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("goods_id", "1");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void hongbaoGoodsDetailFind() {
		try {
			String url = "http://127.0.0.1:8080/ops-goods/goods";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "goods.hongbaoGoodsDetailFind");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("goods_id", "99");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void beikeMallGoodsSalesVolumeUpdate() {
		try {
			String url = "http://127.0.0.1:8080/ops-goods/goods";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "goods.beikeMallGoodsSalesVolumeUpdate");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("goods_id", "40");
			params.put("item_num", 11);
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void hongBaoGoodsSalesVolumeUpdate() {
		try {
			String url = "http://127.0.0.1:8080/ops-goods/goods";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "goods.hongBaoGoodsSalesVolumeUpdate");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("goods_id", "64");
			params.put("sales_volume", 11);
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void homeGoodsFind_V1() {
		try {
			String url = "http://127.0.0.1:8080/ops-goods/goods";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "psGoods.homeGoodsFind_V1");
			Map<String, Object> params = new HashMap<String, Object>();
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void findCommendGoods() {
		try {
			String url = "http://127.0.0.1:8080/ops-goods/goods";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "psGoods.findCommendGoods");
			Map<String, Object> params = new HashMap<String, Object>();
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
	
	@Test
	public void vipGoodsFind() {
		try {
			String url = "http://127.0.0.1:8080/ops-goods/goods";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "goods.vipGoodsFind");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("activity_type", "HDMS_08");
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void newYearGoodsFind() {
		try {
			String url = "http://127.0.0.1:8080/ops-goods/goods";
			Map<String, String> requestData = new HashMap<String, String>();
			requestData.put("service", "psGoods.consumer.newYearGoodsFind");
			Map<String, Object> params = new HashMap<String, Object>();
			requestData.put("params", FastJsonUtil.toJson(params));
			String result = HttpClientUtil.post(url, requestData);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
