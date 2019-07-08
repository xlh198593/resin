package com.winterframework.generator.provider.java.model;

import junit.framework.TestCase;

import com.winterframework.generator.provider.java.model.JavaClass;

public class JavaClassTest extends TestCase {

	public void test() {
		JavaClass c = new JavaClass(JavaClass.class);
		assertEquals(c.getClassName(), "JavaClass");
	}

}
