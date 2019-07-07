package com.ande.buyb2c.advert.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.ande.buyb2c.advert.dao.AdvertPositionDetailMapper;
import com.ande.buyb2c.advert.dao.AdvertPositionMapper;
import com.ande.buyb2c.advert.entity.AdvertPosition;
import com.ande.buyb2c.advert.entity.AdvertPositionDetail;
import com.ande.buyb2c.common.util.BaseServiceImpl;
import com.ande.buyb2c.common.util.IBaseDao;
import com.ande.buyb2c.common.util.SessionUtil;
import com.ande.buyb2c.user.entity.AdminUser;

/**
 * @author chengzb
 * @date 2018年2月2日下午6:24:36
 */
@Service
public class AdvertPositionServiceImpl extends BaseServiceImpl<AdvertPosition>
		implements IAdvertPositionService {
@Resource
private AdvertPositionMapper advertPositionMapper;
@Resource
private AdvertPositionDetailMapper advertPositionDetailMapper;
@Resource
private SessionUtil<AdminUser> sessionUtil;
	@Override
	protected IBaseDao<AdvertPosition> getMapper() {
		return advertPositionMapper;
	}
	@Override
	@Transactional
	public int add(AdvertPosition entity,HttpServletRequest request) throws Exception{
		entity.setCreateTime(new Date());
		entity.setAdminId(sessionUtil.getAdminUser(request).getAdminId());
		advertPositionMapper.insertSelective(entity);
		List<AdvertPositionDetail> list = entity.getList();
		if(list!=null&&list.size()!=0){
			for(AdvertPositionDetail ad:list){
				ad.setAdvertPositionId(entity.getAdvertPositionId());
			}
			advertPositionDetailMapper.addBatch(list);
		}
		return 1;
	}
	 @Override
	 @Transactional
	public int updateByPrimaryKeySelective(AdvertPosition entity) throws Exception {
		 advertPositionMapper.updateByPrimaryKeySelective(entity);
		 List<AdvertPositionDetail> list = entity.getList();
		 StringBuffer buffer=new StringBuffer();
		 List<AdvertPositionDetail> newList=new ArrayList<AdvertPositionDetail>();
		 List<AdvertPositionDetail> updateList=new ArrayList<AdvertPositionDetail>();
		 if(list!=null&&list.size()!=0){
				for(AdvertPositionDetail ad:list){
					if(ad.getAdvertPositionDetailId()!=null){
						if(StringUtils.isEmpty(ad.getImage())){
							//删除
							buffer.append(ad.getAdvertPositionDetailId()+",");
						}else{
							updateList.add(ad);
						}
					}else{
						//新增
						ad.setAdvertPositionId(entity.getAdvertPositionId());
						newList.add(ad);
					}
				}
			}
		 	if(newList.size()!=0){
			 advertPositionDetailMapper.addBatch(newList);
			}
		 	if(updateList.size()!=0){
				 advertPositionDetailMapper.updateBatch(updateList);
			}
			if(buffer.length()!=0){
				String str=buffer.toString();
				advertPositionDetailMapper.del(str.substring(0,str.length()-1));
			}
			return 1;
	 }
}
