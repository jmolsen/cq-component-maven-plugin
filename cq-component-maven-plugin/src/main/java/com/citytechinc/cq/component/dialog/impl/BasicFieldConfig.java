package com.citytechinc.cq.component.dialog.impl;

import java.util.Map;

import com.citytechinc.cq.component.dialog.Widget;

public class BasicFieldConfig implements Widget {

	private final String name;
	private final String primaryType;
	private final String xtype;

	public BasicFieldConfig(String xtype) {
		this.name = "fieldConfig";
		this.primaryType = "nt:unstructured";
		this.xtype = xtype;
	}

	public String getPrimaryType() {
		return primaryType;
	}

	public String getXType() {
		return xtype;
	}

	public String getName() {
		return name;
	}

	public String getLabel() {
		return null;
	}

	public String getFieldName() {
		return null;
	}

	public String getFieldDescription() {
		return null;
	}

	public Boolean hasFieldDescription() {
		return null;
	}

	public Boolean isRequired() {
		return null;
	}

	public Map<String, String> getAdditionalProperties() {
		return null;
	}

}