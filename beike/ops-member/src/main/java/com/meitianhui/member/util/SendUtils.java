package com.meitianhui.member.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;


public class SendUtils {
	 public static void main(String[] args) {
	     //使用分配的秘钥和开发者 ID 替代 getMblNo 方法中的第 3 和第 5 个参数即可
		 String mblNo =getMblNo("1587797,1466651047,1466651647,1466651047,127.0.0.1,client.cmpay.com",
		                        "4e925199db805bfdfc89f9b4cbeb322b", 
		                        "el1YCx8se4m2dT5HP4TApjxOD4C32LuQ",
		                        "http://222.245.77.101:28700/ccaweb/CCLIMCA4/2208000.dor", 
		                        "MTH123456");
		 System.out.println("单点登录成功获取的手机号码：" + mblNo);
	 }
	 
	 public static String getMblNo(String credtential, String signData, String secret, String url,
			String devId) {
			 //传入的参数为链接中的 CREDTENTIAL 参数，SIGN_DATA 参数，分配的秘钥，接口
			//地址，开发者 ID
			 String xml = buildConfirmXmlMessage(credtential, signData, secret, devId);
			 String respXml = sendRequest(xml, url);
			 String mblNo = "";
			 System.out.println(respXml);
			 try {
			 Document document = DocumentHelper.parseText(respXml);
			 Element root = document.getRootElement();
			 mblNo = root.element("BODY").element("MBL_NO").getText();
			 } catch (Exception e) {
			 e.printStackTrace();
			 return null;
			 }
			 return mblNo;
	 }
	 /**
	 * 发送请求
	 */
	 private static String sendRequest(String reqData, String url) {
		 StringWriter writer = new StringWriter();
		 OutputStreamWriter osw = null;
		 try {
		 try {
		 URL reqURL = new URL(url);
		 HttpURLConnection conn = (HttpURLConnection) reqURL.openConnection();
		 conn.setRequestMethod("POST");
		 conn.setDoInput(true);
		 conn.setDoOutput(true);
		 conn.setRequestProperty("Accept", "*/*");
		 conn.setRequestProperty("User-Agent", "stargate");
		 conn.setRequestProperty("Content-Type", "application/json");
		 osw = new OutputStreamWriter(conn.getOutputStream(), "utf-8");
		 osw.write(reqData);
		 osw.flush();
		 BufferedReader br = new BufferedReader(new
		InputStreamReader(conn.getInputStream(), "utf-8"));
		 char[] chars = new char[256];
		 int count = 0;
		 while ((count = br.read(chars)) > 0) {
		 writer.write(chars, 0, count);
		 }
		 } catch (Exception e) {
		 e.printStackTrace();
		 } finally {
		 osw.close();
		 }
		 } catch (Exception e) {
		 e.printStackTrace();
		 }
		 return writer.toString();
	 }
	 
	 /**
	 * 组装 XML 报文 ,credtential 待验签数据 signData 验签密文 secret 接入者秘钥
	 *
	 * @param credtential
	 * @param signData
	 * @param secret
	 * @param devId
	 * @return
	 */
	 private static String buildConfirmXmlMessage(String credtential, String signData, String
			secret, String devId) {
			 // 使用 dom4j 组装 xml
			 Document document = DocumentHelper.createDocument();
			 Element root = document.addElement("ROOT");
			 Element head = root.addElement("HEAD");
			 Element body = root.addElement("BODY");
			 // HEAD 部分
			 head.addElement("TXNCD").setText("2208000");
			 head.addElement("MBLNO").setText("");
			 head.addElement("SESSIONID").setText("");
			 head.addElement("PLAT").setText("99");
			 head.addElement("UA").setText("default");
			 head.addElement("VERSION").setText("default");
			 head.addElement("PLUGINVER").setText("");
			 head.addElement("NETTYPE").setText("");
			 head.addElement("MCID").setText("default");
			 head.addElement("MCA").setText("default");
			 head.addElement("IMEI").setText("default");
			 head.addElement("IMSI").setText("default");
			 head.addElement("SOURCE").setText("default");
			 head.addElement("DEVID").setText(devId);
			 Date currentTime = new Date();
			 SimpleDateFormat df = new SimpleDateFormat("HHmmss");
			 String serlno = df.format(currentTime);
			 head.addElement("SERLNO").setText(serlno);
			 // BODY 部分
			 body.addElement("CREDTENTIAL").setText(credtential);
			 body.addElement("SIGN_DATA").setText(signData);
			 body.addElement("SIGN_TYPE").setText("MD5");
			 // 获取没有头声明的 xml
			 String reqDate = root.asXML();
			 // 去掉 reqDate 中的 root 标签来进行加密签名
			 reqDate = reqDate.substring(0, reqDate.length() - 7);
			 reqDate = reqDate.substring(6, reqDate.length());
			 String signature = signature(reqDate, secret);
			 // 把签名出来的结果组装到 xml 中
			 root.addElement("SIGNATURE").setText(signature);
			 String xml = null;
			 XMLWriter writer = null;
			 ByteArrayOutputStream baos = null;
			 try {
			 try {
			 OutputFormat format = OutputFormat.createCompactFormat();
			 format.setIndent(false);
			 format.setNewlines(false);
			 format.setLineSeparator("");
			 baos = new ByteArrayOutputStream();
			 writer = new XMLWriter(baos, format);
			 writer.write(document);
			 xml = baos.toString("utf-8");
			 return xml;
			 } catch (Exception e) {
			 e.printStackTrace();
			 } finally {
			 writer.close();
			 baos.close();
			 }
			 } catch (Exception e) {
			 e.printStackTrace();
			 }
			 return xml;
			 }
		 /**
		 * signature 加密 reqDate 加密数据 secret 加密 key
		 *
		 * @param reqDate
		 * @param secret
		 * @return
		 */
		 private static String signature(String reqDate, String secret) {
			 String hashAlgorithmName = "HmacSHA1";
			 String appSecret = secret;
			 String requestData = reqDate;
			 SecretKeySpec spec = new SecretKeySpec(appSecret.getBytes(), hashAlgorithmName);
			 Mac mac;
			 String Signature = "";
			 try {
			 mac = Mac.getInstance(hashAlgorithmName);
			 mac.init(spec);
			 byte[] bytes = mac.doFinal(requestData.getBytes());
//			 Signature = Base64Utils.encodeToString(bytes);
//			 Signature = new String((new BASE64Encoder()).encodeBuffer(bytes));
			 } catch (NoSuchAlgorithmException | InvalidKeyException e) {
			 e.printStackTrace();
			 }
			 return Signature;
		 }

}
