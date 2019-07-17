package com.meitianhui.goods.constant;

//Author: chefu
public class BizConstants {
    //上架下架操作
    public class Operator{
        public static final String OFF_LINE = "off_shelf";//下架
        public static final String ON_LINE = "on_shelf";//上架
    }

    //活动商品activityType
    public class ActivityTypeName{
        public static final String BEIKE_MALL = "贝壳商城";
        public static final String BEIKE_STREET = "贝壳街市";
        public static final String HONG_BAO_GOODS = "红包兑";
        public static final String BEIKE_MALL_COMMOND_GOODS = "推荐商品-贝壳商城";
        public static final String HONG_BAO_COMMOND_GOODS = "推荐商品-红包兑";
        public static final String BEIKE_STREET_COMMOND_GOODS = "推荐商品-贝壳街市";
        public static final String HONG_BAO_VIP_GOODS = "vip-商品";
    }
    
    public class vipgoods{
        public static final String YES = "1";//show
        public static final String NO = "0";//display
        public static final String  MAP_FIELD = "is_vip_goods";//display
    }
    
    //是否首页展示
    public class AppIndexShow{
        public static final String YES = "1";//show
        public static final String NO = "0";//display
        public static final String MAP_FIELD = "is_app_index_show";//display
    }
    //是否是APP推荐商品
    public class AppCommondGoods{
        public static final String YES = "1";//show
        public static final String NO = "0";//display
        public static final String MAP_FIELD = "is_commend_goods";//display
    }
    //activity 是否进行中
    public class ActivityStatus{
        public static final String YES = "Y";//商品活动结束
        public static final String NO = "N";//商品活动进行中
    }
    public class ErrorConstants{
        public static final String OPERATOR_FAIL = "操作失败";//首页展示该商品失败
        public static final String DISPLAY_INDEX_FAIL = "首页展示该商品失败";//首页展示该商品失败
        public static final String COMMOND_GOODS_FAIL = "推荐该商品失败";//首页展示该商品失败
        public static final String ACTIVITY_COMMOND_GOODS_FULL = "推荐商品超过20个";//首页展示该商品失败
    }
    public class ErrorCode{
        public static final String OPERATOR_FAIL_CODE = "02000";//首页展示该商品失败
        public static final String DISPLAY_INDEX_FAIL_CODE = "02001";//首页展示该商品失败
        public static final String COMMOND_GOODS_FAIL_CODE = "02002";//首页展示该商品失败
        public static final String ACTIVITY_COMMOND_GOODS_FULL_CODE = "02003";//首页展示该商品失败
    }
}
