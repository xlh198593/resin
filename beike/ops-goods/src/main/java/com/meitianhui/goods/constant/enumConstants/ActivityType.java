package com.meitianhui.goods.constant.enumConstants;

//Author:chefu
public enum  ActivityType {
    BEIKE_MALL("贝壳商城","HDMS_01"),
    BEIKE_STREET("贝壳街市","HDMS_02"),
    HONG_BAO_GOODS("红包兑","HDMS_03"),
    BEIKE_MALL_COMMOND_GOODS("推荐商品-贝壳商城","HDMS_04"),//  -。-
    BEIKE_STREET_COMMOND_GOODS("推荐商品-贝壳街市","HDMS_05"),
    HONG_BAO_VIP_GOODS("vip-商品","HDMS_07"),
    HONG_BAO_COMMOND_GOODS("推荐商品-红包兑","HDMS_06");

    private String name;
    private String type;

    private ActivityType(String name,String type){
        this.name = name;
        this.type = type;
    }

    public static String getTypeByName(String name){
        for (ActivityType t : ActivityType.values()) {
            if (t.getName() == name) {
                return t.type;
            }
        }
        return null;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
