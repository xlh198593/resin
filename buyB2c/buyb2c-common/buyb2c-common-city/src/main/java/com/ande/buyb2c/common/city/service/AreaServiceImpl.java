package com.ande.buyb2c.common.city.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ande.buyb2c.common.city.dao.AreaMapper;
import com.ande.buyb2c.common.city.entity.Area;
import com.ande.buyb2c.common.util.BaseServiceImpl;
import com.ande.buyb2c.common.util.IBaseDao;

/**
 * @author chengzb
 * @date 2018年1月26日下午2:54:40
 */
@Service
public class AreaServiceImpl extends BaseServiceImpl<Area> implements 
    IAreaService{
@Resource
private AreaMapper areaMapper;
	@Override
	protected IBaseDao<Area> getMapper() {
		return areaMapper;
	}

}
