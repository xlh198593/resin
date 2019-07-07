package com.ande.buyb2c.common.city.service;


import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ande.buyb2c.common.city.dao.CityMapper;
import com.ande.buyb2c.common.city.entity.City;
import com.ande.buyb2c.common.util.BaseServiceImpl;
import com.ande.buyb2c.common.util.IBaseDao;

/**
 * @author chengzb
 * @date 2018年1月26日下午2:53:16
 */
@Service
public class CityServiceImpl extends BaseServiceImpl<City> implements
		ICityService {
	@Resource
	private CityMapper cityMapper;
	@Override
	protected IBaseDao<City> getMapper() {
		return cityMapper;
	}

	

}
