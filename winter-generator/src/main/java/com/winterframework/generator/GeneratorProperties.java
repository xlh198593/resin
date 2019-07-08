package com.winterframework.generator;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import com.winterframework.generator.util.PropertiesHelper;

/**
 * 用于装载generator.properties文件
 * @author abba
 * @email xuhengbiao@gmail.com
 */
public class GeneratorProperties {

	static final String PROPERTIES_FILE_NAME = "generator.properties";

	static PropertiesHelper props;

	private GeneratorProperties() {
	}

	private static void loadProperties() {
		try {
			System.out.println("Load [generator.properties] from classpath");
			props = new PropertiesHelper(PropertiesHelper.loadAllPropertiesFromClassLoader(PROPERTIES_FILE_NAME));

			String basepackage = getRequiredProperty("basepackage");
			String basepackage_dir = basepackage.replace('.', '/');
			props.setProperty("basepackage_dir", basepackage_dir);

			for (Map.Entry entity : props.entrySet()) {
				System.out.println("[Property] " + entity.getKey() + "=" + entity.getValue());
			}

			System.out.println();

		} catch (IOException e) {
			throw new RuntimeException("Load Properties error", e);
		}
	}

	public static Properties getProperties() {
		return getHelper().getProperties();
	}

	private static PropertiesHelper getHelper() {
		if (props == null)
			loadProperties();
		return props;
	}

	public static String getProperty(String key, String defaultValue) {
		return getHelper().getProperty(key, defaultValue);
	}

	public static String getProperty(String key) {
		return getHelper().getProperty(key);
	}

	public static String getRequiredProperty(String key) {
		return getHelper().getRequiredProperty(key);
	}

	public static int getRequiredInt(String key) {
		return getHelper().getRequiredInt(key);
	}

	public static boolean getRequiredBoolean(String key) {
		return getHelper().getRequiredBoolean(key);
	}

	public static String getNullIfBlank(String key) {
		return getHelper().getNullIfBlank(key);
	}

	public static void setProperty(String key, String value) {
		getHelper().setProperty(key, value);
	}

	public static void setProperties(Properties v) {
		props = new PropertiesHelper(v);
	}

}
