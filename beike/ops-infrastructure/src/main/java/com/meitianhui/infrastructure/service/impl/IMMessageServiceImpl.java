package com.meitianhui.infrastructure.service.impl;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.util.IDUtil;
import com.meitianhui.infrastructure.constant.RspCode;
import com.meitianhui.infrastructure.dao.IMMessageDao;
import com.meitianhui.infrastructure.service.IMMessageService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class IMMessageServiceImpl implements IMMessageService{

    Logger logger = Logger.getLogger(IMMessageServiceImpl.class);

    @Autowired
    IMMessageDao imMessageDao;

    /**
     *单聊消息回调处理
     */
    @Override
    public void callbackAfterSendMsg(Map<String, Object> paramsMap){
        paramsMap.put("message_id", IDUtil.getUUID());
        try {
            imMessageDao.insertMsgInfo(paramsMap);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("单聊消息回调处理,本地保存消息记录失败,paramsMap:"+paramsMap.toString());
        }
    }

}
