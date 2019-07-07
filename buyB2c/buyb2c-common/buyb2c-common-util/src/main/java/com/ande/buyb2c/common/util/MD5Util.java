package com.ande.buyb2c.common.util;

import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import com.sun.swing.internal.plaf.metal.resources.metal;

public class MD5Util {

    private final static String[] hexDigits = {"0", "1", "2", "3", "4", "5", "6", "7",
        "8", "9", "a", "b", "c", "d", "e", "f"};
	public static String MD5EncodeBySalt(String psd, String salt) {
	    try {
	        StringBuffer stingBuffer = new StringBuffer();
	        // 1.指定加密算法
	        MessageDigest digest = MessageDigest.getInstance("MD5");
	        // 2.将需要加密的字符串转化成byte类型的数据，然后进行哈希过程
	        byte[] bs = digest.digest((psd + salt).getBytes());
	        // 3.遍历bs,让其生成32位字符串，固定写法

	        // 4.拼接字符串
	        for (byte b : bs) {
	            int i = b & 0xff;
	            String hexString = Integer.toHexString(i);
	            if (hexString.length() < 2) {
	                hexString = "0" + hexString;
	            }
	            stingBuffer.append(hexString);
	        }
	        return stingBuffer.toString();
	    } catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	/**
     * MD5编码
     * @param origin 原始字符串
     * @return 经过MD5加密之后的结果
     */
    public static String MD5Encode(String origin) {
        String resultString = null;
        try {
            resultString = origin;
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(resultString.getBytes("UTF-8"));
            resultString = byteArrayToHexString(md.digest());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultString;
    }
	public static String checkSign(Object o,String key){
        ArrayList<String> list = new ArrayList<String>();
        Class<? extends Object> cls = o.getClass();
        Field[] fields = cls.getDeclaredFields();
        String result ="";
            try {
            	for (Field f : fields) {
            		 f.setAccessible(true);
					if (f.get(o) != null && f.get(o) != "") {
					    list.add(f.getName() + "=" + f.get(o) + "&");
					}
            	}
				int size = list.size();
		        String [] arrayToSort = list.toArray(new String[size]);
		        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
		        StringBuilder sb = new StringBuilder();
		        for(int i = 0; i < size; i ++) {
		        	if(!arrayToSort[i].equals("sign"))
		            sb.append(arrayToSort[i]);
		        }
		        result = sb.toString();
		        result += "key=" + key;
		        result = MD5Encode(result).toUpperCase();
			} catch (IllegalArgumentException  | IllegalAccessException e) {
			} 
        return result;
}
	public static String getRand(){
		return new Random().nextInt(888888)+111111+"";
	}


    /**
     * 转换字节数组为16进制字串
     * @param b 字节数组
     * @return 16进制字串
     */
    public static String byteArrayToHexString(byte[] b) {
        StringBuilder resultSb = new StringBuilder();
        for (byte aB : b) {
            resultSb.append(byteToHexString(aB));
        }
        return resultSb.toString();
    }

    /**
     * 转换byte到16进制
     * @param b 要转换的byte
     * @return 16进制格式
     */
    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0) {
            n = 256 + n;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }
}
