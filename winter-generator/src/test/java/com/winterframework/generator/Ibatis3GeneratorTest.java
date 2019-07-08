package com.winterframework.generator;

import java.io.File;

import org.junit.Test;

import com.winterframework.generator.provider.db.DbTableFactory;
import com.winterframework.generator.provider.db.model.Table;
import com.winterframework.generator.test.GeneratorTestCase;

public class Ibatis3GeneratorTest extends GeneratorTestCase {

	//private static String[] keys = {"ba_account_goods","ba_customer","ba_customer_dot","ba_customer_level","ba_customer_quote_price","ba_customer_quote_price_detail","ba_customer_type","ba_dictionary","ba_goods","ba_goods_convert","ba_goods_description","ba_goods_type","ba_pogroup","ba_supplier","ba_supplier_credentials","ba_supplier_type","ba_sys_file","ba_warehouse","ba_warehousetype","po_pay","po_podetail","po_pomain","po_settle_and_pay","po_settle_detail","po_settle_main","po_soandporelation","sales_account_collection_check","sales_account_detail","sales_account_main","sales_collection","sales_packing_detail","sales_packing_main","set_hotcolumn","so_settle_detail","so_settle_main","so_sodetail","so_somain","user_and_pogroup","wh_iotype","wh_rdrecord","wh_rdrecord_detail","wh_safe_stock","wh_stock","wh_stock_lock"};
	private static String[] keys = {"app_access_log"};
	
	
	@Test
	public void testAll() throws Exception {
		for (String str : keys) {
			testGenerate(str);
		}
	}

	public void testGenerate(String tableName) throws Exception {
		Table table = DbTableFactory.getInstance().getTable(tableName);
		g.addTemplateRootDir(new File("template").getAbsoluteFile());
		g.addTemplateRootDir(new File("plugins/ibatis3/template"));
		generateByTable(table);
		// Runtime.getRuntime().exec("cmd.exe /c start "+new
		// File(g.outRootDir).getAbsolutePath());
	}

}
