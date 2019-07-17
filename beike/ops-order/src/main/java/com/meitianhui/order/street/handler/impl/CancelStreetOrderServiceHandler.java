package com.meitianhui.order.street.handler.impl;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;
import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.order.constant.RspCode;
import com.meitianhui.order.street.consts.ServiceName;
import com.meitianhui.order.street.entity.BkcqLocalOrder;
import com.meitianhui.order.street.handler.BaseServiceHandler;
import com.meitianhui.order.street.service.BkcqLocalOrderService;
import com.meitianhui.order.street.utils.HttpRequestUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 取消街市订单业务请求接口
 *
 * @author tortoise
 * @since 2019/3/27 16:40
 */
@SuppressWarnings({"Duplicates", "unchecked"})
@Component
public class CancelStreetOrderServiceHandler extends BaseServiceHandler {

    private static final Logger logger = Logger.getLogger(CancelStreetOrderServiceHandler.class);

    @Autowired
    private BkcqLocalOrderService bkcqLocalOrderService;

    @Override
    public ServiceName getServiceName() {
        return ServiceName.CANCEL_STREET_ORDER;
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

        BkcqLocalOrder bkcqLocalOrder = bkcqLocalOrderService.cancelOrder(orderNoObj.toString(), consumerIdObj.toString());
        String desc = bkcqLocalOrder.getDesc1();
        try {
            List<Map<String, Object>> list = FastJsonUtil.jsonToList(desc);
            for (Map<String, Object> map : list) {
                HttpRequestUtils.unfreezeSkuStock(map.get("goodsId").toString(), map.get("skuId").toString()
                        , bkcqLocalOrder.getOrderNo(), bkcqLocalOrder.getOrderNo(), "BKJS"
                        , Integer.parseInt(map.get("quantity").toString()), "手动取消订单解冻库存");
            }
        } catch (SystemException e) {
            logger.error(String.format("手动取消解冻库存失败，订单信息[%s]", bkcqLocalOrder), e);
        }

        result.setResultData(bkcqLocalOrder);
    }

}
