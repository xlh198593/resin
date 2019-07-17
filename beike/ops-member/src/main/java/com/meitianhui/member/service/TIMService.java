package com.meitianhui.member.service;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;

import java.util.Map;

/**
 * 腾讯云通信
 */
public interface TIMService {

    /**批次导入云通讯账号请求地址*/
    String MULTIACCOUNT_IMPORT_URL = "https://console.tim.qq.com/v4/im_open_login_svc/multiaccount_import?usersig=USERSIG&identifier=IDENTIFIER&sdkappid=SDKAPPID&random=RANDOM&contenttype=json";

    /**导入单个云通讯账号请求地址*/
    String ACCOUNT_IMPORT_URL = "https://console.tim.qq.com/v4/im_open_login_svc/account_import?usersig=USERSIG&identifier=IDENTIFIER&sdkAppid=SDKAPPID&random=RANDOM&contenttype=json";

    /**登陆账号失效接口请求地址*/
    String KICK_URL ="https://console.tim.qq.com/v4/im_open_login_svc/kick?usersig=USERSIG&identifier=IDENTIFIER&sdkappid=SDKAPPID&random=RANDOM&contenttype=json";

    /**单发单聊消息*/
    String SENDMSG_URL = "https://console.tim.qq.com/v4/openim/sendmsg?usersig=USERSIG&identifier=IDENTIFIER&sdkappid=SDKAPPID&random=RANDOM&contenttype=json";

    /**获取用户登录状态*/
    String QUERYSTATE_URL = "https://console.tim.qq.com/v4/openim/querystate?usersig=USERSIG&identifier=IDENTIFIER&sdkappid=SDKAPPID&random=RANDOM&contenttype=json";

    /**批量单发消息*/
    String BATCHSENDMSG_URL = "https://console.tim.qq.com/v4/openim/batchsendmsg?usersig=USERSIG&identifier=IDENTIFIER&sdkappid=SDKAPPID&random=RANDOM&contenttype=json";

    /**获取聊天记录*/
    String GET_HISTORY_URL = "https://console.tim.qq.com/v4/open_msg_svc/get_history?usersig=USERSIG&identifier=IDENTIFIER&sdkappid=SDKAPPID&random=RANDOM&contenttype=json";

    /**
     * 获取userSig
     */
    void genSig(Map<String, Object> paramsMap, ResultData result)throws BusinessException,Exception;

    /**
     * 批量导入账号
     */
    void multiaccountImport(Map<String, Object> paramsMap, ResultData result)throws BusinessException,Exception;

    /**
     * 导入单个账号
     */
    void accountImport(Map<String, Object> paramsMap, ResultData result)throws BusinessException,Exception;

    /**
     *登陆账号失效接口
     */
    void kick(Map<String, Object> paramsMap, ResultData result)throws BusinessException,Exception;

    /**
     * 单发单聊消息
     * */
    void sendMsg(Map<String, Object> paramsMap, ResultData result)throws BusinessException,Exception;

    /**
     * 批量发单聊消息
     * */
    void batchSendMsg(Map<String, Object> paramsMap, ResultData result)throws BusinessException,Exception;

    /**
     * 查询用户云通讯登录状态
     */
    void queryState(Map<String, Object> paramsMap, ResultData result)throws BusinessException,Exception;

    /**
     * 获取聊天历史记录
     */
    void getHistory(Map<String, Object> paramsMap, ResultData result)throws BusinessException,Exception;

}
