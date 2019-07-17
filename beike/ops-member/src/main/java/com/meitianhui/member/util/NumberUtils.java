package com.meitianhui.member.util;

import java.util.Random;

public class NumberUtils {

    /**
     * 获取32位无符号的随机数
     * @return
     */
    public static String getRandom32(){
        Random rand = new Random();
        StringBuffer sb=new StringBuffer();
        for (int i=1;i<=32;i++){
            int randNum = rand.nextInt(9)+1;
            String num=randNum+"";
            sb=sb.append(num);
        }
        return String.valueOf(sb);
    }
}
