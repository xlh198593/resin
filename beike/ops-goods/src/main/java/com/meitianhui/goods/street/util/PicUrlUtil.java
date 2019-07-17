package com.meitianhui.goods.street.util;

import com.meitianhui.common.exception.SystemException;
import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.common.util.StringUtil;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PicUrlUtil {

    /**
     * 解析图片字段获取图片path_id
     * @param picUrl [{"path_id":"bab92555d94948e2985bcd84e2d4a18e","title":""}]
     */
    public static List<String> getPicUrlId(String picUrl) throws SystemException {

        List<String> pathIdList = new ArrayList<>();

        if (StringUtil.isEmpty(picUrl)) {
            return null;
        }

        List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(picUrl);
        if (CollectionUtils.isEmpty(tempList)) {//商品列表只显示一张
            return null;
        }

        Map<String,Object> picMap = null;
        for (Map<String, Object> map : tempList) {
            if(map.get("path_id")!=null){
                picMap = map;
                pathIdList.add(picMap.get("path_id").toString());
            }
        }

//        if (picMap==null) {//商品列表只显示一张
//            return path_id;
//        }
//
//        if (picMap.get("path_id")==null) {//商品列表只显示一张
//            return path_id;
//        }

        return pathIdList;
    }

    /**
     * 从请求得到的图片集中返回真实图片地址
     * @return
     */
    public static String getRealPicUrl(Map<String,Object> map,String key)throws SystemException{

        if(map==null){
            return "";
        }

        if(key==null){
            return "";
        }

        if(CollectionUtils.isEmpty(getPicUrlId(key))){
            return "";
        }

        if(map.get(getPicUrlId(key).get(0))==null){
            return "";
        }

        return map.get(getPicUrlId(key).get(0)).toString();
    }


    /**
     * 获取真实图片路径
     * @param picUrl
     * @param realPicMap
     * @return
     * @throws SystemException
     */
    public static Map<String,Object> getRealPicUrlList(String picUrl,Map<String,Object> realPicMap) throws SystemException{
        List<String> pathIdList = new ArrayList<>();
        Map<String,Object> picList = new HashMap<>();

        if (StringUtil.isEmpty(picUrl)) {
            return null;
        }

        List<Map<String, Object>> tempList = FastJsonUtil.jsonToList(picUrl);
        if (CollectionUtils.isEmpty(tempList)) {
            return null;
        }

        Map<String,Object> picMap = null;
        for (Map<String, Object> map : tempList) {
            if(map.get("path_id")!=null){
                picMap = map;
                pathIdList.add(picMap.get("path_id").toString());
                picList.put(picMap.get("path_id").toString(),realPicMap.get(picMap.get("path_id").toString()));
            }
        }
        return picList;
    }
}
