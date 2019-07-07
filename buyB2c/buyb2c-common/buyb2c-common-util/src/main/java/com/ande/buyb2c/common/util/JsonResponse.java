package com.ande.buyb2c.common.util;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 处理结果Vo
 * 
 * @author: chengzb
 *
 *
 * @param <T>
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class JsonResponse<T> implements Serializable {

	private static final long serialVersionUID = 2273610255200563857L;

	/**
	 * 结果
	 */
	private int res;

	/**
	 * 结果
	 */
	private String result;
	/**
	 * 对象
	 */
	private T obj;

	/**
	 * 结果集
	 */
	private List<T> list;

	public JsonResponse() {
		this.res = SystemCode.FAILURE.getCode();
		this.result = SystemCode.FAILURE.getMsg();
	}

	public void set(int res, String result) {
		this.res = res;
		this.result = result;
	}

	public JsonResponse(int res) {
		this.res = res;
	}

	public JsonResponse(int res, String result) {
		this.res = res;
		this.result = result;
	}

	public int getRes() {
		return res;
	}

	public void setRes(int res) {
		this.res = res;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public T getObj() {
		return obj;
	}

	public void setObj(T obj) {
		this.obj = obj;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

}
