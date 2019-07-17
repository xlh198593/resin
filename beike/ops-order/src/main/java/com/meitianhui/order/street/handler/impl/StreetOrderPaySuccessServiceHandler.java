package com.meitianhui.order.street.handler.impl;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.order.constant.RspCode;
import com.meitianhui.order.street.consts.ServiceName;
import com.meitianhui.order.street.dto.OrderPayResuktDTO;
import com.meitianhui.order.street.entity.BkcqLocalOrderItem;
import com.meitianhui.order.street.handler.BaseServiceHandler;
import com.meitianhui.order.street.service.BkcqLocalOrderService;
import com.meitianhui.order.street.utils.HttpRequestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 创建街市订单业务请求接口
 *
 * @author tortoise
 * @since 2019/3/27 16:40
 */
@SuppressWarnings("Duplicates")
@Component
public class StreetOrderPaySuccessServiceHandler extends BaseServiceHandler {

    @Autowired
    private BkcqLocalOrderService bkcqLocalOrderService;

    @Override
    public ServiceName getServiceName() {
        return ServiceName.STREET_ORDER_PAY_SUCCESS;
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

        Object payWayObj = paramsMap.get("payWay");
        if (null == payWayObj) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "支付方式不能为空");
        }

        //支付成功处理订单
        OrderPayResuktDTO orderPayResuktDTO = bkcqLocalOrderService.orderPaySuccess(orderNoObj.toString(),
                payWayObj.toString());

        //订单处理成功扣减库存
        List<BkcqLocalOrderItem> items = orderPayResuktDTO.getBkcqLocalOrder().getItems();
        if (null != items && !items.isEmpty()) {
            for (BkcqLocalOrderItem item : items) {
                HttpRequestUtils.deductionSkuStock(item.getGoodsId().toString(), item.getSkuId().toString(),
                        orderPayResuktDTO.getBkcqLocalOrder().getOrderNo(),
                        orderPayResuktDTO.getBkcqLocalOrder().getOrderNo(), "BKJS",
                        orderPayResuktDTO.getBkcqLocalOrder().getItemNum(), null);
            }
        }

        result.setResultData(orderPayResuktDTO);
    }

}
