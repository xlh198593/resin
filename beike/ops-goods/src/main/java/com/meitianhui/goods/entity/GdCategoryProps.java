package com.meitianhui.goods.entity;

import java.io.Serializable;

public class GdCategoryProps implements Serializable{

	private static final long serialVersionUID = 4669936133539377796L;
	private Integer prop_id;
	private Integer cat_id;
	private String prop_name;
	private String type;
	private String search;
	private String show_type;
	private String prop_type;
	private String prop_memo;
	private Integer create_time;
	private Integer modified_time;
	private Integer disabled;
	public Integer getProp_id() {
		return prop_id;
	}
	public void setProp_id(Integer prop_id) {
		this.prop_id = prop_id;
	}
	public Integer getCat_id() {
		return cat_id;
	}
	public void setCat_id(Integer cat_id) {
		this.cat_id = cat_id;
	}
	public String getProp_name() {
		return prop_name;
	}
	public void setProp_name(String prop_name) {
		this.prop_name = prop_name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSearch() {
		return search;
	}
	public void setSearch(String search) {
		this.search = search;
	}
	public String getShow_type() {
		return show_type;
	}
	public void setShow_type(String show_type) {
		this.show_type = show_type;
	}
	public String getProp_type() {
		return prop_type;
	}
	public void setProp_type(String prop_type) {
		this.prop_type = prop_type;
	}
	public String getProp_memo() {
		return prop_memo;
	}
	public void setProp_memo(String prop_memo) {
		this.prop_memo = prop_memo;
	}
	public Integer getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Integer create_time) {
		this.create_time = create_time;
	}
	public Integer getModified_time() {
		return modified_time;
	}
	public void setModified_time(Integer modified_time) {
		this.modified_time = modified_time;
	}
	public Integer getDisabled() {
		return disabled;
	}
	public void setDisabled(Integer disabled) {
		this.disabled = disabled;
	}
	
}
