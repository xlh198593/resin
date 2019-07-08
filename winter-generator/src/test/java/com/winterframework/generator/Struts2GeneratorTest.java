package com.winterframework.generator;

import java.io.File;

import org.junit.Test;

import com.winterframework.generator.test.GeneratorTestCase;
import com.winterframework.generator.provider.db.DbTableFactory;
import com.winterframework.generator.provider.db.model.Table;

public class Struts2GeneratorTest extends GeneratorTestCase {
	private static String[] keys = { "USER_APPEAL","USER_CUSTOMER","USER_FREEZE_LOG","USER_LOGIN_LOG","user_message_reply","user_message"};

	@Test
	public void testAll() throws Exception {
		//keys = new String[] { "USERINFO" };
		for (String str : keys) {
			testGenerate(str);
		}
	}

	public void testGenerate(String tableName) throws Exception {

		Table table = DbTableFactory.getInstance().getTable(tableName);

		g.addTemplateRootDir(new File("template").getAbsoluteFile());
		g.addTemplateRootDir(new File("plugins/struts2/template"));

		generateByTable(table);

		//		Runtime.getRuntime().exec("cmd.exe /c start "+new File(g.outRootDir).getAbsolutePath());
	}

}
