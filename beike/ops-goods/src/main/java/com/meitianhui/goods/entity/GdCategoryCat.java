package com.meitianhui.goods.entity;

import java.io.Serializable;
/**
 * 商品分类
 * @author Administrator
 *
 */
public class GdCategoryCat implements Serializable{

	private static final long serialVersionUID = 9128573857539435171L;
	
	    private Integer cat_id;

	    private Integer parent_id;

	    private String cat_name;

	    private String cat_path;

	    private String level;

	    private String is_leaf;

	    private String disabled;

	    private Integer child_count;

	    private Integer order_sort;
	    
	    private Integer create_time;

	    private Integer modified_time;

	    private String cat_template;

		public Integer getCat_id() {
			return cat_id;
		}

		public void setCat_id(Integer cat_id) {
			this.cat_id = cat_id;
		}

		public Integer getParent_id() {
			return parent_id;
		}

		public void setParent_id(Integer parent_id) {
			this.parent_id = parent_id;
		}

		public String getCat_name() {
			return cat_name;
		}

		public void setCat_name(String cat_name) {
			this.cat_name = cat_name;
		}

		public String getCat_path() {
			return cat_path;
		}

		public void setCat_path(String cat_path) {
			this.cat_path = cat_path;
		}

		public String getLevel() {
			return level;
		}

		public void setLevel(String level) {
			this.level = level;
		}

		public String getIs_leaf() {
			return is_leaf;
		}

		public void setIs_leaf(String is_leaf) {
			this.is_leaf = is_leaf;
		}

		public String getDisabled() {
			return disabled;
		}

		public void setDisabled(String disabled) {
			this.disabled = disabled;
		}

		public Integer getChild_count() {
			return child_count;
		}

		public void setChild_count(Integer child_count) {
			this.child_count = child_count;
		}

		public Integer getOrder_sort() {
			return order_sort;
		}

		public void setOrder_sort(Integer order_sort) {
			this.order_sort = order_sort;
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

		public String getCat_template() {
			return cat_template;
		}

		public void setCat_template(String cat_template) {
			this.cat_template = cat_template;
		}

	    
}
