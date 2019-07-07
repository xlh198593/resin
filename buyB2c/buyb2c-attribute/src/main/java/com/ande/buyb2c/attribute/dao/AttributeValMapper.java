package com.ande.buyb2c.attribute.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ande.buyb2c.attribute.entity.AttributeVal;
import com.ande.buyb2c.common.util.IBaseDao;

public interface AttributeValMapper extends IBaseDao<AttributeVal>{
	public int addBatch(List<AttributeVal> list);
	public int updateBatch(List<AttributeVal> list);
	public void delAttribute(@Param("attributeValIds")String attributeValIds);
}
