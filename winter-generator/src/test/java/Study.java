import java.io.IOException;
import java.util.Properties;

import com.winterframework.generator.util.PropertiesHelper;

/**
* Copyright (C) abba, 2010
*/
/**
* 
* @author abba(xuhengbiao@gmail.com) in 2010-4-30
* @since 1.0
*/
public class Study {
	private String aaa;

	public static void main(String[] args) throws IOException {
		Properties p = PropertiesHelper.loadAllPropertiesFromClassLoader("a.properties");
		System.out.println(p.get("user.dir"));
	}
}
