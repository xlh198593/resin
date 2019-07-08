package com.winterframework.generator.test;

import java.io.File;

import com.winterframework.generator.provider.db.DbTableFactory;
import com.winterframework.generator.provider.db.model.Table;

public class SpringMVCGeneratorTest extends GeneratorTestCase {

	public void testGenerate() throws Exception {

		System.out.println(DbTableFactory.getInstance().getAllTables());

		Table table = DbTableFactory.getInstance().getTable("USER_INFO");

		g.addTemplateRootDir(new File("template").getAbsoluteFile());
		g.addTemplateRootDir(new File("plugins/springmvc_rest/template"));

		generateByTable(table);

		//		Runtime.getRuntime().exec("cmd.exe /c start "+new File(g.outRootDir).getAbsolutePath());
	}

}
