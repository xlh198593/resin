package com.ande.buyb2c.column.service;



import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ande.buyb2c.column.dao.ColumnMapper;
import com.ande.buyb2c.column.entity.Column;
import com.ande.buyb2c.column.vo.FrontColumnGoodsVo;
import com.ande.buyb2c.column.vo.FrontColumnVo;
import com.ande.buyb2c.common.util.BaseServiceImpl;
import com.ande.buyb2c.common.util.IBaseDao;
import com.ande.buyb2c.common.util.PageResult;
import com.github.pagehelper.PageHelper;

/**
 * @author chengzb
 * @date 2018年1月27日下午4:01:31
 */
@Service
public class ColumnServiceImpl extends BaseServiceImpl<Column> implements
		IColumnService {
	@Resource
	private ColumnMapper columnMapper;
	@Override
	protected IBaseDao<Column> getMapper() {
		return columnMapper;
	}
	@Override
	public void getColumnByPage(PageResult<FrontColumnVo> page) {
		int pageNo=page.getPageNo();
    	int pageSize=page.getPageSize();
		pageNo = pageNo == 0?1:pageNo;
		pageSize = pageSize == 0?10:pageSize;
		PageHelper.startPage(pageNo,pageSize); 
		PageResult.toPageResult(columnMapper.getColumnByPage(),page);
	}
	@Override
	public List<FrontColumnGoodsVo> getGolumnGoodsList(String sort,
			Integer num, Integer columnId, String desc) {
		return columnMapper.getGolumnGoodsList(sort, num, columnId, desc);
	}

}
