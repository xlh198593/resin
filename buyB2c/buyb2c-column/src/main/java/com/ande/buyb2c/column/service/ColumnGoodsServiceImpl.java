package com.ande.buyb2c.column.service;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ande.buyb2c.column.dao.ColumnGoodsMapper;
import com.ande.buyb2c.column.entity.ColumnGoods;
import com.ande.buyb2c.common.util.BaseServiceImpl;
import com.ande.buyb2c.common.util.IBaseDao;
import com.ande.buyb2c.common.util.SessionUtil;
import com.ande.buyb2c.user.entity.AdminUser;

/**
 * @author chengzb
 * @date 2018年1月27日下午5:25:46
 */
@Service
public class ColumnGoodsServiceImpl extends BaseServiceImpl<ColumnGoods>
		implements IColumnGoodsService {
@Resource
private ColumnGoodsMapper columnGoodsMapper;
@Resource
private SessionUtil<AdminUser> sessionUtil;
	@Override
	protected IBaseDao<ColumnGoods> getMapper() {
		return columnGoodsMapper;
	}
	@Override
	public Integer addGoodsToColumn(HttpServletRequest request,List<ColumnGoods> list) throws Exception{
		Integer adminId=sessionUtil.getAdminUser(request).getAdminId();
	/*	for(ColumnGoods cg:list){
			cg.setAdminId(adminId);
			cg.setCreateTime(new Date());
		}
		return columnGoodsMapper.addGoodsToColumn(list);*/
		StringBuffer buffer=new StringBuffer();
		List<ColumnGoods> newList=new ArrayList<ColumnGoods>();
		for(ColumnGoods ata:list){
			if(ata.getColumnGoodsId()!=null){
				if(StringUtils.isEmpty(ata.getGoodsName())){
					//删除
					buffer.append(ata.getColumnGoodsId()+",");
				}
			}else{
				ata.setCreateTime(new Date());
				ata.setAdminId(adminId);
				newList.add(ata);
			}
		}
		if(newList.size()!=0){
			columnGoodsMapper.addGoodsToColumn(newList);
		}
		if(buffer.length()!=0){
			String str=buffer.toString();
			columnGoodsMapper.delColumnGoods(str.substring(0,str.length()-1));
		}
		return 1;
	}
	@Override
	public Integer delColumnGoods(String columnGoodsIds) throws Exception {
		return columnGoodsMapper.delColumnGoods(columnGoodsIds);
	}
	@Override
	public Integer deleteByColumnId(Integer columnId) throws Exception {
		return columnGoodsMapper.deleteByColumnId(columnId);
	}
}
