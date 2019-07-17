package com.meitianhui.finance.street.handler.impl;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.finance.constant.RspCode;
import com.meitianhui.finance.street.consts.PayWay;
import com.meitianhui.finance.street.consts.ServiceName;
import com.meitianhui.finance.street.dto.StreetOrderPayDTO;
import com.meitianhui.finance.street.entity.FdTransactionsResult;
import com.meitianhui.finance.street.handler.BaseServiceHandler;
import com.meitianhui.finance.street.handler.PayHandler;
import com.meitianhui.finance.street.service.StreetPayService;
import com.meitianhui.finance.street.utils.HttpRequestUtils;
import com.meitianhui.finance.street.utils.StreetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 订单支付业务请求接口
 *
 * @author tortoise
 * @since 2019/3/27 20:40
 */
@Component
public class OrderPayServiceHandler extends BaseServiceHandler {

    @Autowired
    private StreetPayService streetPayService;

    /**
     * 业务处理类
     */
    private final Map<PayWay, PayHandler> payHandlers = new ConcurrentHashMap<>();

    @Autowired
    public OrderPayServiceHandler(List<PayHandler> payHandlers) {
        if (null != payHandlers && !payHandlers.isEmpty()) {
            for (PayHandler payHandler : payHandlers) {
                this.payHandlers.put(payHandler.getPayWay(), payHandler);
            }
        }
    }

    @Override
    public ServiceName getServiceName() {
        return ServiceName.ORDER_PAY;
    }

    @Override
    public void handle(Map<String, Object> paramsMap, ResultData result) throws BusinessException {
        StreetOrderPayDTO streetOrderPayDTO = validate(paramsMap, StreetOrderPayDTO.class);

        Map<String, Object> order = HttpRequestUtils.findOrderByNo(streetOrderPayDTO.getOrderNo());
        String status = StreetUtils.getValue("status", order);
        if (!"WAIT_BUYER_PAY".equals(status)) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "支付失败，订单状态异常");
        }

        streetOrderPayDTO.setOrder(order);

        Map<String, FdTransactionsResult> stringFdTransactionsResultMap = streetPayService.orderPay(streetOrderPayDTO);

        PayWay payWay = PayWay.parse(streetOrderPayDTO.getPayWay());

        // 微信支付宝支付需要先下单
        if (payWay.equals(PayWay.ZFFS_01) || payWay.equals(PayWay.ZFFS_02)) {
            FdTransactionsResult fdTransactionsResult = stringFdTransactionsResultMap.get(streetOrderPayDTO.getPayWay());
            if ("completed".equals(fdTransactionsResult.getTransactionStatus())) {
                // 调用订单接口回写订单完成并生成核销码
                Map<String, Object> map = HttpRequestUtils.orderPaySuccess(fdTransactionsResult.getOutTradeNo(),
                        streetOrderPayDTO.getPayWay());
                result.setResultData(map);
            } else {
                Map<String, String> handle = payHandlers.get(payWay)
                        .handle(streetOrderPayDTO, stringFdTransactionsResultMap.get(streetOrderPayDTO.getPayWay()));
                result.setResultData(handle);
            }
        } else {
            FdTransactionsResult fdTransactionsResult = stringFdTransactionsResultMap.get(streetOrderPayDTO.getPayWay());
            if ("completed".equals(fdTransactionsResult.getTransactionStatus())) {
                // 调用订单接口回写订单完成并生成核销码
                Map<String, Object> map = HttpRequestUtils.orderPaySuccess(fdTransactionsResult.getOutTradeNo(),
                        streetOrderPayDTO.getPayWay());
                result.setResultData(map);
            }
        }
    }

}
