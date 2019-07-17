package com.meitianhui.finance.constant;

import com.meitianhui.common.util.PropertiesConfigUtil;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;

/**
 * @author Created by oushaohui on 2019/5/7 14:12
 * @description
 **/
public class ConsumerConstant {
    /**
     * @param tradeTypeKey 交易类型
     * @param orderTypeKey 订单类型
     * @param amount       金额
     * @return
     */
    public static boolean isConsumerType(String tradeTypeKey, String orderTypeKey, BigDecimal amount) {
        if (StringUtils.isBlank(tradeTypeKey) && StringUtils.isBlank(orderTypeKey)) {
            return false;
        } else {
            if (ShellConstant.TRADE_TYPE_01.equals(tradeTypeKey) && ShellConstant.ORDER_TYPE_06.equals(orderTypeKey)) {
                String experience_member_recharge_amout = PropertiesConfigUtil.getProperty("experience_member_recharge_amout");
                int val = amount.setScale(2, BigDecimal.ROUND_HALF_UP).compareTo(new BigDecimal(experience_member_recharge_amout));
                if (val == 0) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }
}
