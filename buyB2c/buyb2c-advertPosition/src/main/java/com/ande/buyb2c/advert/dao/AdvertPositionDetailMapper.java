package com.ande.buyb2c.advert.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ande.buyb2c.advert.entity.AdvertPositionDetail;
import com.ande.buyb2c.common.util.IBaseDao;

public interface AdvertPositionDetailMapper extends IBaseDao<AdvertPositionDetail>{
    public Integer addBatch(List<AdvertPositionDetail> list) throws Exception;
    public Integer updateBatch(List<AdvertPositionDetail> list) throws Exception;
    public Integer del(@Param("ids") String ids) throws Exception;
}