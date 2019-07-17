package com.meitianhui.finance.street.handler;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.finance.street.consts.ServiceName;

import java.util.Map;

/**
 * 处理不同的业务请求接口
 *
 * @author tortoise
 * @since 2019/3/27 16:40
 */
public interface ServiceHandler {

    /**
     * 获取业务名称
     *
     * @return 业务名称枚举类
     */
    ServiceName getServiceName();

    /**
     * 处理业务
     *
     * @param paramsMap 请求参数
     * @param result    返回结果回写
     * @throws BusinessException 业务异常
     */
    void handle(Map<String, Object> paramsMap, ResultData result) throws BusinessException;

}
