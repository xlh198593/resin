package com.meitianhui.order.street.handler.impl;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.order.constant.RspCode;
import com.meitianhui.order.street.consts.ServiceName;
import com.meitianhui.order.street.entity.BkcqLocalOrder;
import com.meitianhui.order.street.handler.BaseServiceHandler;
import com.meitianhui.order.street.service.BkcqLocalOrderService;
import com.meitianhui.order.street.utils.StreetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 申请退款街市订单业务请求接口
 *
 * @author tortoise
 * @since 2019/3/27 16:40
 */
@SuppressWarnings({"Duplicates"})
@Component
public class ApplyRefundStreetOrderServiceHandler extends BaseServiceHandler {

    @Autowired
    private BkcqLocalOrderService bkcqLocalOrderService;

    @Override
    public ServiceName getServiceName() {
        return ServiceName.APPLY_REFUND_STREET_ORDER;
    }

    /**
     * 处理业务
     *
     * @param paramsMap 请求参数
     * @param result    返回结果回写
     * @throws BusinessException 业务异常
     */
    @Override
    public void handle(Map<String, Object> paramsMap, ResultData result) throws BusinessException {
        Object orderNoObj = paramsMap.get("orderNo");
        if (null == orderNoObj) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "订单编号不能为空");
        }

        Object consumerIdObj = paramsMap.get("consumerId");
        if (null == consumerIdObj) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "消费者编号不能为空");
        }

        BkcqLocalOrder bkcqLocalOrder = bkcqLocalOrderService.applyRefundOrder(orderNoObj.toString(),
                consumerIdObj.toString(), StreetUtils.getValue("refundReason", paramsMap));
        result.setResultData(bkcqLocalOrder);
    }

}
