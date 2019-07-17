package com.meitianhui.goods.constant;

import java.math.BigDecimal;

//author: chefu
public class BigDecimalUtil {
    public static BigDecimal ZERO = BigDecimal.ZERO;
    public static BigDecimal stringToBigDecimal(String value){
        return new BigDecimal(value);
    }
}
