package com.ande.buyb2c.column.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ande.buyb2c.column.entity.Column;
import com.ande.buyb2c.column.vo.FrontColumnGoodsVo;
import com.ande.buyb2c.column.vo.FrontColumnVo;
import com.ande.buyb2c.common.util.IBaseDao;

public interface ColumnMapper extends IBaseDao<Column>{
	List<FrontColumnVo> getColumnByPage();
	
	public List<FrontColumnGoodsVo> getGolumnGoodsList(@Param("sort")String sort,
			@Param("num") Integer num,
			@Param("columnId")Integer columnId,@Param("desc") String desc);
}