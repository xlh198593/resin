package com.ande.buyb2c.content.service;


import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ande.buyb2c.common.util.BaseServiceImpl;
import com.ande.buyb2c.common.util.IBaseDao;
import com.ande.buyb2c.content.dao.ContentMapper;
import com.ande.buyb2c.content.entity.Content;

/**
 * @author chengzb
 * @date 2018年1月27日下午2:12:33
 */
@Service
public class ContentServiceImpl extends BaseServiceImpl<Content> implements
		IContentService {
@Resource
private ContentMapper contentMapper;
	@Override
	protected IBaseDao<Content> getMapper() {
		return contentMapper;
	}
	@Override
	public Content getConentByType(String type) {
		return contentMapper.getConentByType(type);
	}



}
