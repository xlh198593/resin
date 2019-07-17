package com.meitianhui.infrastructure.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.meitianhui.common.util.DateUtil;
import com.meitianhui.infrastructure.service.IMMessageService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

@RestController
public class IMcallbackController {

    private static final Logger logger = Logger.getLogger(IMcallbackController.class);

    @Autowired
    private IMMessageService imMessageService;


    @SuppressWarnings("finally")
    @RequestMapping(value = "/imcallback")
    public Map<String,Object> consumerAlipayNotity(HttpServletRequest request, HttpServletResponse response) {

//        {
//            "CallbackCommand": "C2C.CallbackAfterSendMsg", // 回调命令
//                "From_Account": "jared", // 发送者
//                "To_Account": "Jonh", // 接收者
//                "MsgBody": [ // 消息体
//            {
//                "MsgType": "TIMTextElem", // 文本
//                    "MsgContent": {
//                "Text": "red packet"
//            }
//            }
//    ]
//        }

        try {
            String param = null;
            JSONObject jsonObject = null;
            try {
                BufferedReader streamReader = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
                StringBuilder responseStrBuilder = new StringBuilder();
                String inputStr;
                while ((inputStr = streamReader.readLine()) != null)
                    responseStrBuilder.append(inputStr);

                jsonObject = JSONObject.parseObject(responseStrBuilder.toString());

                param = jsonObject.toJSONString();
                System.out.println(param);
            } catch (Exception e) {
                e.printStackTrace();
            }
            logger.info("消息回调参数:" + param);

            String CallbackCommand = jsonObject.get("CallbackCommand").toString();
            logger.info("消息回调类型:" + CallbackCommand);
            if ("C2C.CallbackAfterSendMsg".equals(CallbackCommand)) {//单聊发送后回调
                String fromAccount = jsonObject.get("From_Account") + "";
                String toAccount = jsonObject.get("To_Account") + "";

                JSONArray msg = jsonObject.getJSONArray("MsgBody");

                Map<String, String> jobj = (Map) msg.getObject(0, Map.class).get("MsgContent");
                Map<String, Object> paramsMap = new HashMap();
                paramsMap.put("message_sender", fromAccount);
                paramsMap.put("message_receiver", toAccount);
                paramsMap.put("message_content", jobj.get("Text"));
                paramsMap.put("created_time", new Date());
                imMessageService.callbackAfterSendMsg(paramsMap);
                logger.info("消息发送后回调:fromAccount:" + fromAccount);
            } else {
                logger.info("其它消息回调");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("ActionStatus", "OK");
        responseMap.put("ErrorInfo", "");
        responseMap.put("ErrorCode", 0);
        return responseMap;
    }
}
