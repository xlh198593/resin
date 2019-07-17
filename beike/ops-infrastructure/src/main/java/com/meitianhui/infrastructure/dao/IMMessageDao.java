package com.meitianhui.infrastructure.dao;

import java.util.Map;

/**
 * 即时通讯消息记录
 */
public interface IMMessageDao {

    /**
     *插入通讯消息记录
     */
    void insertMsgInfo(Map<String,Object> map)throws Exception;

    /**
     * 获取最近联系人列表
     */
    void getRecentContacts(Map<String,Object> map)throws Exception;

}
