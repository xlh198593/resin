package com.ande.buyb2c.advert.service;

import javax.servlet.http.HttpServletRequest;

import com.ande.buyb2c.advert.entity.AdvertPosition;
import com.ande.buyb2c.common.util.IBaseService;

/**
 * @author chengzb
 * @date 2018年2月2日下午6:23:52
 */
public interface IAdvertPositionService extends IBaseService<AdvertPosition> {
	public int add(AdvertPosition entity,HttpServletRequest request) throws Exception;
}
