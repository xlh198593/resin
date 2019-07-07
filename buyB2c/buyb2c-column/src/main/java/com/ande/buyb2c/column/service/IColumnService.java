package com.ande.buyb2c.column.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ande.buyb2c.column.entity.Column;
import com.ande.buyb2c.column.vo.FrontColumnGoodsVo;
import com.ande.buyb2c.column.vo.FrontColumnVo;
import com.ande.buyb2c.common.util.IBaseService;
import com.ande.buyb2c.common.util.PageResult;
/**
 * @author chengzb
 * @date 2018年1月27日下午4:00:53
 */
public interface IColumnService extends IBaseService<Column>{
	void getColumnByPage(PageResult<FrontColumnVo> page);
	public List<FrontColumnGoodsVo> getGolumnGoodsList(String sort,
			Integer num,
			Integer columnId,String desc);
}
