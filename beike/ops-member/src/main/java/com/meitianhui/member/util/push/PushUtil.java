package com.meitianhui.member.util.push;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.meitianhui.common.util.PropertiesConfigUtil;
import com.meitianhui.common.util.StringUtil;
import com.meitianhui.common.util.ValidateUtil;
import com.meitianhui.member.util.push.android.AndroidBroadcast;
import com.meitianhui.member.util.push.android.AndroidCustomizedcast;
import com.meitianhui.member.util.push.android.AndroidFilecast;
import com.meitianhui.member.util.push.android.AndroidGroupcast;
import com.meitianhui.member.util.push.android.AndroidNotification;
import com.meitianhui.member.util.push.android.AndroidUnicast;
import com.meitianhui.member.util.push.ios.IOSBroadcast;
import com.meitianhui.member.util.push.ios.IOSCustomizedcast;
import com.meitianhui.member.util.push.ios.IOSFilecast;
import com.meitianhui.member.util.push.ios.IOSGroupcast;
import com.meitianhui.member.util.push.ios.IOSUnicast;

public class PushUtil {
	private static String androidAppkey = PropertiesConfigUtil.getProperty("umeng_android_appkey");
	private static String androidAppMasterSecret = PropertiesConfigUtil.getProperty("umeng_android_appMasterSecret");
	private static String iosAppkey = PropertiesConfigUtil.getProperty("umeng_ios_appkey");
	private static String iosAppMasterSecret = PropertiesConfigUtil.getProperty("umeng_ios_appMasterSecret");
	private static PushClient client = new PushClient();

	public void sendAndroidBroadcast() throws Exception {
		AndroidBroadcast broadcast = new AndroidBroadcast(androidAppkey, androidAppMasterSecret);
		broadcast.setTicker("Android broadcast ticker");
		broadcast.setTitle("中文的title");
		broadcast.setText("Android broadcast text");
		broadcast.goAppAfterOpen();
		broadcast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
		// TODO Set 'production_mode' to 'false' if it's a test device.
		// For how to register a test device, please see the developer doc.
		broadcast.setProductionMode();
		// Set customized fields
		broadcast.setExtraField("test", "helloworld");
		client.send(broadcast);
	}

	public void sendAndroidUnicast() throws Exception {
		AndroidUnicast unicast = new AndroidUnicast(androidAppkey, androidAppMasterSecret);
		// TODO Set your device token
		unicast.setDeviceToken("your device token");
		unicast.setTicker("Android unicast ticker");
		unicast.setTitle("中文的title");
		unicast.setText("Android unicast text");
		unicast.goAppAfterOpen();
		unicast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
		// TODO Set 'production_mode' to 'false' if it's a test device.
		// For how to register a test device, please see the developer doc.
		unicast.setProductionMode();
		// Set customized fields
		unicast.setExtraField("test", "helloworld");
		client.send(unicast);
	}

	public void sendAndroidGroupcast() throws Exception {
		AndroidGroupcast groupcast = new AndroidGroupcast(androidAppkey, androidAppMasterSecret);
		/*
		 * TODO Construct the filter condition: "where": { "and": [ {"tag":"test"},
		 * {"tag":"Test"} ] }
		 */
		JSONObject filterJson = new JSONObject();
		JSONObject whereJson = new JSONObject();
		JSONArray tagArray = new JSONArray();
		JSONObject testTag = new JSONObject();
		JSONObject TestTag = new JSONObject();
		testTag.put("tag", "test");
		TestTag.put("tag", "Test");
		tagArray.put(testTag);
		tagArray.put(TestTag);
		whereJson.put("and", tagArray);
		filterJson.put("where", whereJson);
		System.out.println(filterJson.toString());

		groupcast.setFilter(filterJson);
		groupcast.setTicker("Android groupcast ticker");
		groupcast.setTitle("苗人凤");
		groupcast.setText("Android groupcast text");
		// groupcast.setExtraField(key, value);
		// groupcast.goAppAfterOpen();
		// groupcast.goUrlAfterOpen("https://www.baidu.com/");
		groupcast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
		// TODO Set 'production_mode' to 'false' if it's a test device.
		// For how to register a test device, please see the developer doc.
		groupcast.setProductionMode(false);
		client.send(groupcast);
	}

	public void sendAndroidCustomizedcastFile() throws Exception {
		AndroidCustomizedcast customizedcast = new AndroidCustomizedcast(androidAppkey, androidAppMasterSecret);
		// TODO Set your alias here, and use comma to split them if there are multiple
		// alias.
		// And if you have many alias, you can also upload a file containing these
		// alias, then
		// use file_id to send customized notification.
		String fileId = client.uploadContents(androidAppkey, androidAppMasterSecret,
				"aa" + "\n" + "bb" + "\n" + "alias");
		customizedcast.setFileId("android", "10086");
		customizedcast.setTicker("Android customizedcast ticker");
		customizedcast.setTitle("中文的title");
		customizedcast.setText("Android customizedcast text");
		customizedcast.goAppAfterOpen();
		customizedcast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
		// TODO Set 'production_mode' to 'false' if it's a test device.
		// For how to register a test device, please see the developer doc.
		customizedcast.setProductionMode();
		client.send(customizedcast);
	}

	public void sendAndroidFilecast() throws Exception {
		AndroidFilecast filecast = new AndroidFilecast(androidAppkey, androidAppMasterSecret);
		// TODO upload your device tokens, and use '\n' to split them if there are
		// multiple tokens
		String fileId = client.uploadContents(androidAppkey, androidAppMasterSecret, "aa" + "\n" + "bb");
		filecast.setFileId(fileId);
		filecast.setTicker("Android filecast ticker");
		filecast.setTitle("中文的title");
		filecast.setText("Android filecast text");
		filecast.goAppAfterOpen();
		filecast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
		client.send(filecast);
	}

	public void sendIOSBroadcast() throws Exception {
		IOSBroadcast broadcast = new IOSBroadcast(iosAppkey, iosAppMasterSecret);

		broadcast.setAlert("IOS 广播测试");
		broadcast.setBadge(0);
		broadcast.setSound("default");
		// TODO set 'production_mode' to 'true' if your app is under production mode
		broadcast.setTestMode();
		// Set customized fields
		broadcast.setCustomizedField("test", "helloworld");
		client.send(broadcast);
	}

	public void sendIOSUnicast() throws Exception {
		IOSUnicast unicast = new IOSUnicast(iosAppkey, iosAppMasterSecret);
		// TODO Set your device token
		unicast.setDeviceToken("xx");
		unicast.setAlert("IOS 单播测试");
		unicast.setBadge(0);
		unicast.setSound("default");
		// TODO set 'production_mode' to 'true' if your app is under production mode
		unicast.setTestMode();
		// Set customized fields
		unicast.setCustomizedField("test", "helloworld");
		client.send(unicast);
	}

	public void sendIOSGroupcast() throws Exception {
		IOSGroupcast groupcast = new IOSGroupcast(iosAppkey, iosAppMasterSecret);
		/*
		 * TODO Construct the filter condition: "where": { "and": [ {"tag":"iostest"} ]
		 * }
		 */
		JSONObject filterJson = new JSONObject();
		JSONObject whereJson = new JSONObject();
		JSONArray tagArray = new JSONArray();
		JSONObject testTag = new JSONObject();
		testTag.put("tag", "iostest");
		tagArray.put(testTag);
		whereJson.put("and", tagArray);
		filterJson.put("where", whereJson);
		System.out.println(filterJson.toString());

		// Set filter condition into rootJson
		groupcast.setFilter(filterJson);
		groupcast.setAlert("IOS 组播测试");
		groupcast.setBadge(0);
		groupcast.setSound("default");
		// TODO set 'production_mode' to 'true' if your app is under production mode
		groupcast.setTestMode();
		client.send(groupcast);
	}

	public static Boolean sendIOSCustomizedcast(Map<String, Object> paramsMap) throws Exception {
		ValidateUtil.validateParams(paramsMap,
				new String[] { "message_title", "member_id", "message_subtitle", "message_text" });
		IOSCustomizedcast customizedcast = new IOSCustomizedcast(iosAppkey, iosAppMasterSecret);
		// TODO Set your alias and alias_type here, and use comma to split them if there
		// are multiple alias.
		// And if you have many alias, you can also upload a file containing these
		// alias, then
		// use file_id to send customized notification.
		String message_title = StringUtil.formatStr(paramsMap.get("message_title"));
		String member_id = StringUtil.formatStr(paramsMap.get("member_id"));
		String message_subtitle = StringUtil.formatStr(paramsMap.get("message_subtitle"));
		String message_text = StringUtil.formatStr(paramsMap.get("message_text"));
		customizedcast.setAlias(member_id, "UMENGTEST");
		JSONObject alertJson = new JSONObject();
		alertJson.put("body", message_text);
		alertJson.put("subtitle", message_subtitle);
		alertJson.put("title", message_title);
		customizedcast.setAlert(alertJson);
		customizedcast.setBadge(0);
		customizedcast.setSound("default");
		Object tempMap = paramsMap.get("tempMap");
		if (tempMap != null) {
			Map<String, Object> map = (Map<String, Object>) tempMap;
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				customizedcast.setCustomizedField(entry.getKey(), entry.getValue().toString());
			}
		}
		// TODO set 'production_mode' to 'true' if your app is under production mode
		// customizedcast.setTestMode();
		customizedcast.setProductionMode(false);
		return client.send(customizedcast);
	}

	public static Boolean sendAndroidCustomizedcast(Map<String, Object> paramsMap) throws Exception {
		ValidateUtil.validateParams(paramsMap,
				new String[] { "message_title", "member_id", "message_subtitle", "message_text" });
		AndroidCustomizedcast customizedcast = new AndroidCustomizedcast(androidAppkey, androidAppMasterSecret);
		// TODO Set your alias here, and use comma to split them if there are multiple alias.
		// And if you have many alias, you can also upload a file containing these alias, then
		// use file_id to send customized notification.
		String message_title = StringUtil.formatStr(paramsMap.get("message_title"));
		String member_id = StringUtil.formatStr(paramsMap.get("member_id"));
		String message_subtitle = StringUtil.formatStr(paramsMap.get("message_subtitle"));
		String message_text = StringUtil.formatStr(paramsMap.get("message_text"));
		customizedcast.setAlias(member_id, "10086");
		customizedcast.setTicker(message_subtitle);
		customizedcast.setTitle(message_title);
		customizedcast.setText(message_text);
		Object tempMap = paramsMap.get("tempMap");
		if (tempMap != null) {
			Map<String, Object> map = (Map<String, Object>) tempMap;
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				customizedcast.setExtraField(entry.getKey(), entry.getValue().toString());
			}
		}
		customizedcast.goCustomAfterOpen("11111111");
		customizedcast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
		// TODO Set 'production_mode' to 'false' if it's a test device.
		// For how to register a test device, please see the developer doc.
		customizedcast.setProductionMode(false);
		return client.send(customizedcast);
	}

	public void sendIOSFilecast() throws Exception {
		IOSFilecast filecast = new IOSFilecast(iosAppkey, iosAppMasterSecret);
		// TODO upload your device tokens, and use '\n' to split them if there are
		// multiple tokens
		String fileId = client.uploadContents(iosAppkey, iosAppMasterSecret, "aa" + "\n" + "bb");
		filecast.setFileId(fileId);
		filecast.setAlert("IOS 文件播测试");
		filecast.setBadge(0);
		filecast.setSound("default");
		// TODO set 'production_mode' to 'true' if your app is under production mode
		filecast.setTestMode();
		client.send(filecast);
	}

	public static void main(String[] args) {
		// TODO set your appkey and master secret here 安卓
		// Demo demo = new Demo("5c0a35cef1f5563efc000143",
		// "nawrcy1yqf6vuh2g7n4inb1wmrfdhjw6");
		// ios
		// PushUtil demo = new PushUtil("5c26cc28f1f5566d6500071b",
		// "sqr3vdyakzlxojxwxixo7aliilhgnbfd");
		try {
			// demo.sendAndroidUnicast();
			/*
			 * TODO these methods are all available, just fill in some fields and do the
			 * test demo.sendAndroidCustomizedcastFile(); demo.sendAndroidBroadcast();
			 * demo.sendAndroidGroupcast(); demo.sendAndroidFilecast();
			 * 
			 * demo.sendIOSBroadcast(); demo.sendIOSUnicast(); demo.sendIOSGroupcast();
			 * demo.sendIOSCustomizedcast(); demo.sendIOSFilecast();
			 */
			// demo.sendAndroidGroupcast();
			// demo.sendIOSCustomizedcast();
			// demo.sendAndroidCustomizedcast();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
