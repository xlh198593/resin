package com.meitianhui.finance.util;

import com.google.common.collect.Maps;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.common.util.HttpClientUtil;
import com.meitianhui.common.util.PropertiesConfigUtil;
import com.meitianhui.finance.constant.RspCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * <pre> 调用其它系统Http请求类 </pre>
 *
 * @author tortoise
 * @since 2019/3/29 17:05
 */
@SuppressWarnings({"Duplicates", "unchecked"})
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HttpRequestUtils {

    /**
     * 会员服务地址
     */
    public static final String MEMBER_SERVICE_URL = PropertiesConfigUtil.getProperty("member_service_url");

    /**
     * 根据消费者查询消费者
     *
     * @param consumerId 消费者编号
     * @return Map<String, Object>消费者Map
     * @throws BusinessException 业务异常
     */
    public static Map<String, Object> findConsumerById(String consumerId) throws BusinessException {
        Map<String, String> reqParams = Maps.newHashMap();
        Map<String, Object> bizParams = Maps.newHashMap();
        reqParams.put("service", "consumer.findConsumerById");
        bizParams.put("consumer_id", consumerId);
        reqParams.put("params", FastJsonUtil.toJson(bizParams));
        try {
            String resultStr = HttpClientUtil.postShort(MEMBER_SERVICE_URL, reqParams);
            Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
            if (!RspCode.RESPONSE_SUCC.equals(resultMap.get("rsp_code").toString())) {
                throw new BusinessException(resultMap.get("error_code").toString()
                        , resultMap.get("error_msg").toString());
            }
            return (Map<String, Object>) resultMap.get("data");
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "查询消费者信息失败，系统通讯异常");
        }
    }

}

