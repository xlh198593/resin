package com.meitianhui.member.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HiCryptUtils {

	/**
	 * MD5签名，对传入参数进行排序，并md5签名
	 * 
	 * @param source
	 *            参数
	 * @param key
	 *            签名密钥
	 * @return
	 */
	public static String cryptMd5(Map source, String key) {
		Object[] paras = source.keySet().toArray();
		Arrays.sort(paras);
		StringBuffer sb = new StringBuffer();
		String tmp = null;
		for (int i = 0; i < paras.length; i++) {
			tmp = (String) source.get(paras[i]);
			if (null != tmp && tmp.length() > 0) {
				sb.append(tmp);
			}
		}

		return cryptMd5(sb.toString(), key);
	}

	/**
	 * MD5签名
	 * 
	 * @param source
	 *            签名参数
	 * @param key
	 *            签名密钥
	 * @return
	 */
	public static String cryptMd5(String source, String key) {
		byte k_ipad[] = new byte[64];
		byte k_opad[] = new byte[64];
		byte keyb[];
		byte value[];
		try {
			keyb = key.getBytes("UTF-8");
			value = source.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			keyb = key.getBytes();
			value = source.getBytes();
		}
		Arrays.fill(k_ipad, keyb.length, 64, (byte) 54);
		Arrays.fill(k_opad, keyb.length, 64, (byte) 92);
		for (int i = 0; i < keyb.length; i++) {
			k_ipad[i] = (byte) (keyb[i] ^ 0x36);
			k_opad[i] = (byte) (keyb[i] ^ 0x5c);
		}
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		md.update(k_ipad);
		md.update(value);
		byte dg[] = md.digest();
		md.reset();
		md.update(k_opad);
		md.update(dg, 0, 16);
		dg = md.digest();
		return toHex(dg);
	}

	private static String toHex(byte input[]) {
		if (input == null)
			return null;

		StringBuffer output = new StringBuffer(input.length * 2);
		for (int i = 0; i < input.length; i++) {
			int current = input[i] & 0xff;
			if (current < 16)
				output.append("0");
			output.append(Integer.toString(current, 16));
		}

		return output.toString();
	}
	
	public static void main(String[] args) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("accesstoken", "972f73f063d0eb76ac4d3f741ef0441a");
		params.put("appid", "10020112");
		params.put("format", "json");
		params.put("operationtime", "20180512034027");
		params.put("signaturemethod", "MD5");
		System.out.println(cryptMd5(params,"sandbox0a9a42dd593be3b9e226b557ab0cae65"));
	}

}
