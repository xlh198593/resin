package com.meitianhui.infrastructure.service;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;

import java.util.Map;

public interface IMMessageService {

    /**
     *单聊消息回调处理
     */
    void callbackAfterSendMsg(Map<String, Object> paramsMap);

}
