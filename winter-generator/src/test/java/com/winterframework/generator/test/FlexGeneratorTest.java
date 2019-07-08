package com.winterframework.generator.test;

import java.io.File;

import com.winterframework.generator.provider.db.DbTableFactory;
import com.winterframework.generator.provider.db.model.Table;

public class FlexGeneratorTest extends GeneratorTestCase {

	public void testGenerate() throws Exception {

		Table table = DbTableFactory.getInstance().getTable("USER_INFO");

		g.addTemplateRootDir(new File("template").getAbsoluteFile());
		g.addTemplateRootDir(new File("plugins/flex/template"));
		g.addTemplateRootDir(new File("plugins/flex_cairngorm/template"));

		generateByTable(table);

		//Runtime.getRuntime().exec("cmd.exe /c start "+new File(g.outRootDir).getAbsolutePath());
	}

}
