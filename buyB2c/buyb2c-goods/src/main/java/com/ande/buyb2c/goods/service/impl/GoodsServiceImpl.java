package com.ande.buyb2c.goods.service.impl;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ande.buyb2c.common.util.BaseServiceImpl;
import com.ande.buyb2c.common.util.IBaseDao;
import com.ande.buyb2c.common.util.PageResult;
import com.ande.buyb2c.goods.dao.GoodsAttributeMapper;
import com.ande.buyb2c.goods.dao.GoodsAttributeValMapper;
import com.ande.buyb2c.goods.dao.GoodsMapper;
import com.ande.buyb2c.goods.entity.Goods;
import com.ande.buyb2c.goods.entity.GoodsAttribute;
import com.ande.buyb2c.goods.entity.GoodsAttributeVal;
import com.ande.buyb2c.goods.service.IGoodsService;
import com.ande.buyb2c.goods.vo.RequestGoodsVo;
import com.github.pagehelper.PageHelper;

/**
 * @author chengzb
 * @date 2018年1月30日上午9:50:48
 */
@Service
public class GoodsServiceImpl extends BaseServiceImpl<Goods> implements
		IGoodsService {
@Resource
private GoodsMapper goodsMapper;
@Resource
private GoodsAttributeMapper goodsAttributeMapper;
@Resource
private GoodsAttributeValMapper goodsAttributeValMapper;
	@Override
	protected IBaseDao<Goods> getMapper() {
		return goodsMapper;
	}
	@Override
	public List<Goods> ifCanDel(String level, Integer goodTypeId) {
		return goodsMapper.ifCanDel(level, goodTypeId);
	}
	@Override
	public PageResult<Goods> getGoodsPageByColumn(PageResult<Goods> page,
			Goods goods) {
		int pageNo=page.getPageNo();
    	int pageSize=page.getPageSize();
		pageNo = pageNo == 0?1:pageNo;
		pageSize = pageSize == 0?10:pageSize;
		PageHelper.startPage(pageNo,pageSize); 
		return PageResult.toPageResult(goodsMapper.getGoodsPageByColumn(goods),page);
	}
	@Transactional
	@Override
	 public int insertSelective(Goods goods) throws Exception{
		goodsMapper.insertSelective(goods);
		Integer goodsId=goods.getGoodsId();
		addGoodsAttributeList(goodsId, goods.getGoodsAttributeList());
		return 1;
	 }
	public void addGoodsAttributeList(Integer goodsId,List<GoodsAttribute> goodsAttributeList) throws Exception{
		if(goodsAttributeList!=null&&goodsAttributeList.size()!=0){
			for(GoodsAttribute goodsAttribute:goodsAttributeList){
				goodsAttribute.setGoodsId(goodsId);
				goodsAttributeMapper.insertSelective(goodsAttribute);
				Integer goodsAttributeId=goodsAttribute.getGoodsAttributeId();
				List<GoodsAttributeVal> valList=goodsAttribute.getGoodsAttributeValList();
				if(valList!=null&&valList.size()!=0){
					for(GoodsAttributeVal val:valList){
						val.setGoodsAttributeId(goodsAttributeId);
					}
					goodsAttributeValMapper.addBatch(valList);
				}
			}
		}
	}
	@Override
	public Goods getGoodsById(Integer goodsId) {
		return goodsMapper.getGoodsById(goodsId);
	}
	@Transactional
	@Override
	public int updateByPrimaryKeySelective(Goods goods) throws Exception{
		RequestGoodsVo vo=(RequestGoodsVo)goods;
		//String goodsAttributeIds=vo.getGoodsAttributeIds();
		goodsMapper.updateByPrimaryKeySelective(goods);
		/*if("1".equals(vo.getIsChang())){
			goodsAttributeMapper.delGoodsAttribute(goodsAttributeIds);
			goodsAttributeValMapper.delGoodsAttributeVal(goodsAttributeIds);
			addGoodsAttributeList(goods.getGoodsId(),goods.getGoodsAttributeList());
		}*/
		/*if(!"".equals(goodsAttributeIds)){
			//有需要删除的
			goodsAttributeMapper.delGoodsAttribute(goodsAttributeIds);
			goodsAttributeValMapper.delGoodsAttributeVal(goodsAttributeIds);
		}*/
		if("1".equals(vo.getIsChang())){
			//属性有改动
			List<GoodsAttribute> goodsAttributeList = goods.getGoodsAttributeList();
			List<GoodsAttribute> newAttrList = new ArrayList<GoodsAttribute>();
			List<GoodsAttributeVal> newAttrValList = new ArrayList<GoodsAttributeVal>();
			StringBuffer attrBuffer=new StringBuffer();
			StringBuffer attrValbuffer=new StringBuffer();
			for(GoodsAttribute goodsAttribute:goodsAttributeList){
				if(goodsAttribute.getGoodsAttributeId()!=null){
					if("".equals(goodsAttribute.getGoodsAttributeName())){
						//一整行删除
						attrBuffer.append(goodsAttribute.getGoodsAttributeId()+",");
					}else{
						//更新
						//1 新增val 2 删除val
						for(GoodsAttributeVal val:goodsAttribute.getGoodsAttributeValList()){
							if(val.getGoodsAttributeValId()!=null&&"".equals(val.getGoodsAttributeVal())){
								//删除
								attrValbuffer.append(val.getGoodsAttributeValId()+",");
							}else if(val.getGoodsAttributeValId()==null){
								//新增
								val.setGoodsAttributeId(goodsAttribute.getGoodsAttributeId());
								newAttrValList.add(val);
							}
						}
					}
				}else{
					//新增 一整行属性
					goodsAttribute.setGoodsId(goods.getGoodsId());
					newAttrList.add(goodsAttribute);
				}
			}
			//新增的属性对
			if(newAttrList.size()!=0){
				addGoodsAttributeList(goods.getGoodsId(),newAttrList);
			}
			//新增的属性值
			if(newAttrValList.size()!=0){
				goodsAttributeValMapper.addBatch(newAttrValList);
			}
			//删除的属性对
			if(attrBuffer.length()!=0){
				String str=attrBuffer.toString();
				goodsAttributeMapper.delGoodsAttribute(str.substring(0,str.length()-1));
				goodsAttributeValMapper.delGoodsAttributeVal(str.substring(0,str.length()-1));
			}
			//删除的属性值
			if(attrValbuffer.length()!=0){
				String str=attrValbuffer.toString();
				System.out.println(str.substring(0,str.length()-1));
				goodsAttributeValMapper.delGoodsAttributeValById(str.substring(0,str.length()-1));
			}
			
		}
		return 1;
	}
	 public Integer updateState(String ids,String saleState,String delState,Date date)throws Exception{
		 return goodsMapper.updateState(ids,saleState,delState,date);
	 }
	@Override
	public PageResult<Goods> getGoodsPageByColumnId(PageResult<Goods> page,
			String goodsName, Integer columnId) {
		int pageNo=page.getPageNo();
    	int pageSize=page.getPageSize();
		pageNo = pageNo == 0?1:pageNo;
		pageSize = pageSize == 0?10:pageSize;
		PageHelper.startPage(pageNo,pageSize); 
		return PageResult.toPageResult(goodsMapper.getGoodsPageByColumnId(goodsName, columnId),page);
	}
	@Override
	public Integer getGoodsByTypeId(Integer typeId) {
		return goodsMapper.getGoodsByTypeId(typeId);
	}
	@Override
	public Integer updateByTypeId(Goods goods) throws Exception{
		return goodsMapper.updateByTypeId(goods);
	}
	@Override
	public Goods getGoodsByCopy(Integer goodsId) {
		return goodsMapper.getGoodsByCopy(goodsId);
	}
}
