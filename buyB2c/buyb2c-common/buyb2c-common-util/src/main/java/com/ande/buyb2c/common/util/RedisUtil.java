package com.ande.buyb2c.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

/**
 * @author chengzb
 * @version 2017年8月14日上午9:36:16
 */
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

/**
 * redis cache 工具类
 * 
 */
@Service
public final class RedisUtil {
	@Resource
	private RedisTemplate<String, Object> redisTemplate;

	/**
	 * 批量删除对应的value
	 * 
	 * @param keys
	 */
	public void remove(final String... keys) {
		for (String key : keys) {
			remove(key);
		}
	}

	/**
	 * 批量删除key
	 * 
	 * @param pattern
	 */
	public void removePattern(final String pattern) {
		Set<String> keys = redisTemplate.keys(pattern);
		if (keys.size() > 0)
			redisTemplate.delete(keys);
	}

	/**
	 * 批量更新key
	 * 
	 * @param pattern
	 */
	public void updatePattern(final String pattern) {
		Set<String> keys = redisTemplate.keys(pattern);
		if (keys.size() > 0) {
			for (String key : keys) {
				ValueOperations<String, Object> operations = redisTemplate.opsForValue();
				operations.set(key, "null");
				redisTemplate.expire(key, 60 * 60, TimeUnit.SECONDS);
			}
		}
	}

	/**
	 * 删除对应的value
	 * 
	 * @param key
	 */
	public void remove(final String key) {
		if (exists(key)) {
			redisTemplate.delete(key);
		}
	}

	/**
	 * 判断缓存中是否有对应的value
	 * 
	 * @param key
	 * @return
	 */
	public boolean exists(final String key) {
		return redisTemplate.hasKey(key);
	}

	/**
	 * 读取缓存
	 * 
	 * @param key
	 * @return
	 */
	public Object get(final String key) {
		Object result = null;
		ValueOperations<String, Object> operations = redisTemplate.opsForValue();
		result = operations.get(key);
		return result;
	}

	/**
	 * 读取缓存
	 * 
	 * @param key
	 * @return
	 */
	public Object getHash(final String key, String hkey) {
		HashOperations<String, Object, Object> opsForHash = redisTemplate.opsForHash();
		return opsForHash.get(key, hkey);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(final String key, Class<T> t) {
		Object result = null;
		ValueOperations<String, Object> operations = redisTemplate.opsForValue();
		result = operations.get(key);
		return (T) result;
	}

	/**
	 * 写入缓存
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean set(final String key, Object value) {
		boolean result = false;
		try {
			ValueOperations<String, Object> operations = redisTemplate.opsForValue();
			operations.set(key, value);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public long getOrderNO(final String key) {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		ValueOperations<String, Object> operations = redisTemplate.opsForValue();
		long value = operations.increment(key + ":" + format.format(date), 1);
		if (value == 1) {
			redisTemplate.expire(key + ":" + format.format(date), 24, TimeUnit.HOURS);
		}
		return value;
	}

	public String getOrderNo(String key) {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		ValueOperations<String, Object> operations = redisTemplate.opsForValue();
		String dateStr = format.format(date);
		long value = operations.increment(key + ":" + dateStr, 1);
		if (value == 1) {
			redisTemplate.expire(key + ":" + format.format(date), 24, TimeUnit.HOURS);
		}
		StringBuilder sb = new StringBuilder(dateStr);
		for (int i = 0; i < 4 - String.valueOf(value).length(); i++) {
			sb.append(0);
		}
		sb.append(value);
		return sb.toString();
	}

	/**
	 * 写入缓存
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean set(final String key, Object value, Long expireTime) {
		boolean result = false;
		try {
			ValueOperations<String, Object> operations = redisTemplate.opsForValue();
			operations.set(key, value);
			redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public boolean setHash(final String key, String hkey, String hvalue, Long expireTime) {
		boolean result = false;
		try {
			HashOperations<String, Object, Object> opsForHash = redisTemplate.opsForHash();
			opsForHash.put(key, hkey, hvalue);
			redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}
}
