package com.ande.buyb2c.common.city.service;


import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ande.buyb2c.common.city.dao.ProvinceMapper;
import com.ande.buyb2c.common.city.entity.Province;
import com.ande.buyb2c.common.util.BaseServiceImpl;
import com.ande.buyb2c.common.util.IBaseDao;

/**
 * @author chengzb
 * @date 2018年1月26日下午2:51:21
 */
@Service
public class ProvinceServiceImpl extends BaseServiceImpl<Province> implements
		IProvinceService {
	@Resource
	private ProvinceMapper provinceMapper;
	@Override
	protected IBaseDao<Province> getMapper() {
		return provinceMapper;
	}
}
