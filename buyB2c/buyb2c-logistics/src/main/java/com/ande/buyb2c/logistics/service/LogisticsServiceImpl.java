package com.ande.buyb2c.logistics.service;


import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ande.buyb2c.common.util.BaseServiceImpl;
import com.ande.buyb2c.common.util.IBaseDao;
import com.ande.buyb2c.logistics.dao.LogisticsMapper;
import com.ande.buyb2c.logistics.entity.Logistics;

/**
 * @author chengzb
 * @date 2018年1月27日上午9:16:11
 */
@Service
public class LogisticsServiceImpl extends BaseServiceImpl<Logistics> implements
		ILogisticsService {
@Resource
private LogisticsMapper logisticsMapper;
	@Override
	protected IBaseDao<Logistics> getMapper() {
		return logisticsMapper;
	}
}
