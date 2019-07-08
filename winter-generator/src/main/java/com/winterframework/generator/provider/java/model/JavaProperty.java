package com.winterframework.generator.provider.java.model;

import java.beans.PropertyDescriptor;

import com.winterframework.generator.util.ActionScriptDataTypesUtils;

public class JavaProperty {
	PropertyDescriptor propertyDescriptor;
	JavaClass clazz;

	public JavaProperty(PropertyDescriptor pd, JavaClass javaClass) {
		this.propertyDescriptor = pd;
		this.clazz = javaClass;
	}

	public String getName() {
		return propertyDescriptor.getName();
	}

	public String getJavaType() {
		return propertyDescriptor.getPropertyType().getName();
	}

	public String getAsType() {
		return ActionScriptDataTypesUtils.getPreferredAsType(propertyDescriptor.getPropertyType().getName());
	}

	@Override
	public String toString() {
		return "JavaClass:" + clazz + " JavaProperty:" + getName();
	}
}
