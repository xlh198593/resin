package com.ande.buyb2c.common.generateOrderNo;
/**
 * 依赖redis环境  流水号生成
 */
public interface ISerialNumberService {
	 /**
     * 
     * @param key 缓存key,一般用项目名，以做区分   length默认为4
     * @return
     */
    public String getOrderNO(String key);
    /**
     * 
     * @param key 缓存key,一般用项目名，以做区分
     * @param length 生成的流水号规则     日期+序号  2018012500101 length代表日期后面的序列号位数 例如此处length=5
     * @return
     */
    public String getOrderNO(String key,int length) ;
}
