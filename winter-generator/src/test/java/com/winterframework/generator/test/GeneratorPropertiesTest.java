package com.winterframework.generator.test;

import com.winterframework.generator.GeneratorProperties;

import junit.framework.TestCase;

public class GeneratorPropertiesTest extends TestCase {
	@Override
	public void setUp() {
		GeneratorProperties.getProperties().setProperty("blank", "  ");
		GeneratorProperties.getProperties().setProperty("empty", "");
	}

	public void testgetNullIfBlankProperty() {
		assertEquals(null, GeneratorProperties.getNullIfBlank("blank"));
		assertEquals(null, GeneratorProperties.getNullIfBlank("empty"));
		assertEquals(null, GeneratorProperties.getNullIfBlank("null"));
	}

	public void testgetRequiredProperty() {
		try {
			assertEquals(null, GeneratorProperties.getRequiredProperty("blank"));
			fail();
		} catch (Exception e) {
		}
		try {
			assertEquals(null, GeneratorProperties.getRequiredProperty("empty"));
			fail();
		} catch (Exception e) {
		}
		try {
			assertEquals(null, GeneratorProperties.getRequiredProperty("null"));
			fail();
		} catch (Exception e) {
		}
	}
}
