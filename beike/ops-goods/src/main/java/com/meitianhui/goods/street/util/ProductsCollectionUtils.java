package com.meitianhui.goods.street.util;

import com.meitianhui.common.exception.SystemException;
import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.common.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@Slf4j
public class ProductsCollectionUtils {

    /**
     * 从商品列表中获取门店的Id stores_id
     * @param productList
     * @return
     */
    public static List<String> getStoreIdList(List<Map<String, Object>> productList) {
        //获取所有门店的id
        List<String> storeIdList = new ArrayList<>();
        for (Map<String, Object> e : productList) {
            storeIdList.add(e.get("supplier_id")==null?null:e.get("supplier_id").toString());
        }
        //去重
        Set<String> set  = new HashSet<>();
        set.addAll(storeIdList);
        storeIdList = new ArrayList<>(set);
        return storeIdList;
    }

    /**
     * 将门店信息图片信息整合进商品列表
     */
    public static List<Map<String,Object>> formProductionList(List<Map<String,Object>> productList,List<Map<String,Object>> storeList,Map<String,Object> picMap) throws SystemException{
        String store_head_pic_path = null;
        String pic_info = null;

        Map<String,Object> store = null;
        for (Map<String, Object> product : productList) {

            if (product.get("supplier_id") != null) {//匹配商品所在的门店
                for (Map<String, Object> map : storeList) {
                    if(map.get("stores_id").toString().equals(product.get("supplier_id").toString())){
                        store = map;
                        break;
                    }
                }
            }

            if ( store != null) {//将门店信息加入商品信息中
                product.put("distance", store.get("distance"));
                store_head_pic_path = StringUtil.formatStr(store.get("store_head_pic_path"));
                log.info("store_head_pic_path:{}",PicUrlUtil.getRealPicUrl(picMap,store_head_pic_path));
                product.put("store_head_pic_path",PicUrlUtil.getRealPicUrl(picMap,store_head_pic_path));
                product.put("stores_name", store.get("stores_name") == null ? "" : store.get("stores_name"));
            }

            pic_info = StringUtil.formatStr(product.get("pic_info"));//推荐商品图片
            log.info("--------------pic_info:{}", PicUrlUtil.getRealPicUrl(picMap,pic_info));
            List<Map<String,Object>> picInfoList = new ArrayList<>();
            if(StringUtils.isNotEmpty(StringUtil.formatStr(product.get("pic_info")))){
                picInfoList =  FastJsonUtil.jsonToList(product.get("pic_info")+"");
            }
            product.put("pic_info",picInfoList);//图片解析重新封装

            product.put("picList", PicUrlUtil.getRealPicUrlList( pic_info,picMap));//图片解析重新封装

        }

        return productList;
    }

    /**
     * 商品列表按距离排序
     */
    public static List<Map<String,Object>> getProductsOrderByDistance(List<Map<String,Object>> productList){
        //讲商品根据商家距离进行重排序
        Collections.sort(productList, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                return Integer.parseInt(o1.get("distance").toString()) - Integer.parseInt(o2.get("distance").toString());
            }
        });

        return productList;
    }
}
