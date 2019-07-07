package com.ande.buyb2c.common.generateOrderNo;


import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class SerialNumberServiceImpl implements ISerialNumberService{

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Override
	public String getOrderNO(String key) {
    	return getOrderNO(key,4);
	}
	@Override
	public String getOrderNO(String key,int length) {
		 Date date=new Date();
	        SimpleDateFormat format=new SimpleDateFormat("MMdd");
	        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
	        String dateStr=format.format(date);
	        long value=operations.increment(key+":"+dateStr,1);
	        if(value==1){
	            redisTemplate.expire(key+":"+format.format(date), 24, TimeUnit.HOURS);
	        }
	        StringBuilder sb=new StringBuilder(dateStr);
	        for (int i = 0; i < length-String.valueOf(value).length(); i++) {
	            sb.append(0);
	        }
	        sb.append(value);
	        return sb.toString();
	}

}
