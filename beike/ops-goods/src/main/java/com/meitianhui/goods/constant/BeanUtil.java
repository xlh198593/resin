package com.meitianhui.goods.constant;

import com.alibaba.fastjson.JSON;
import com.meitianhui.common.util.DateUtil;
import com.meitianhui.common.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
//author： chefu
public class BeanUtil {
    private static Log logger = LogFactory.getLog(BeanUtil.class);

    //goodstoMap
    public static Map<String,Object> goodsToMap(Object o){
        Map<String, Object> tempMap = new HashMap<String, Object>();
        Class<?> oClase = o.getClass();
        Field[] fields = oClase.getDeclaredFields();
        for (Field field:fields){
            try {
                field.setAccessible(true);
                if(field.getType() == String.class ){
                    if(StringUtils.isEmpty((String)field.get(o))){
                        continue;
                    }else{
                        tempMap.put(field.getName(), StringUtil.formatStr(field.get(o)));
                    }
                }else if(field.getType() == Date.class){
                    tempMap.put(field.getName(), DateUtil.date2Str((Date)field.get(o), DateUtil.fmt_yyyyMMddHHmmss));
                }else{
                    tempMap.put(field.getName(),field.get(o));
                }
            }catch (Exception e){
                logger.error("goods to Map Wrong. errorMethod:goodsToMap  parameter" + JSON.toJSONString(o));
            }

        }
        return tempMap;
    }

    //mapToBean
    public static Object mapToBean(Map map , Class oclass){
        Object o = null;
        try {
            o = oclass.newInstance();
            Field[] fields = oclass.getDeclaredFields();
            for (Field field:fields){
                field.setAccessible(true);
                if("serialVersionUID".equals(field.getName())){
                    continue;
                }
                if(field.getType() == Integer.class){
                    field.set(o,map.get(field.getName()) == null ? null:Integer.parseInt(map.get(field.getName()).toString()));
                }else if(field.getType() == String.class){
                    field.set(o,map.get(field.getName()) == null ? null:map.get(field.getName()).toString());
                }else if(field.getType() == Date.class){
                    field.set(o,map.get(field.getName()) == null ? null:DateUtil.str2Date(map.get(field.getName()).toString(), DateUtil.fmt_yyyyMMddHHmmss));
                }else if(field.getType() == BigDecimal.class){
                    field.set(o,map.get(field.getName()) == null ? BigDecimalUtil.ZERO:BigDecimalUtil.stringToBigDecimal(map.get(field.getName()).toString()));
                }else {
                    field.set(o,map.get(field.getName()) == null ? null:map.get(field.getName()));
                }

            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("Map to Bean Error , errorMethod: mapToBean parameter:Map:");
        }

        return o;
    }
}
