package com.ande.buyb2c.content.dao;

import com.ande.buyb2c.common.util.IBaseDao;
import com.ande.buyb2c.content.entity.Content;

public interface ContentMapper extends IBaseDao<Content>{
	Content getConentByType(String type);
}