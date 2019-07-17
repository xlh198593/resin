package com.meitianhui.order.street.handler.impl;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.order.constant.OrderIDUtil;
import com.meitianhui.order.constant.RspCode;
import com.meitianhui.order.street.consts.ServiceName;
import com.meitianhui.order.street.dto.BkcqLocalOrderCreateDTO;
import com.meitianhui.order.street.entity.BkcqLocalOrder;
import com.meitianhui.order.street.handler.BaseServiceHandler;
import com.meitianhui.order.street.service.BkcqLocalOrderService;
import com.meitianhui.order.street.utils.HttpRequestUtils;
import com.meitianhui.order.street.utils.StreetUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Map;

/**
 * 创建街市订单业务请求接口
 *
 * @author tortoise
 * @since 2019/3/27 16:40
 */
@Component
public class CreateStreetOrderServiceHandler extends BaseServiceHandler {

    private static final Logger logger = Logger.getLogger(CreateStreetOrderServiceHandler.class);

    @Autowired
    private BkcqLocalOrderService bkcqLocalOrderService;

    @Override
    public ServiceName getServiceName() {
        return ServiceName.CREATE_STREET_ORDER;
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
        BkcqLocalOrderCreateDTO bkcqLocalOrderCreateDTO = validate(paramsMap, BkcqLocalOrderCreateDTO.class);

        //获取消费者信息
        Map<String, Object> consumer = HttpRequestUtils.findConsumerById(bkcqLocalOrderCreateDTO.getConsumerId());
        bkcqLocalOrderCreateDTO.setConsumer(consumer);

        Long vipEndTime = (Long) consumer.get("vip_end_time");

        //获取当前年月日的时间
        Calendar calendar = StreetUtils.getNowDayStart();
//        if (null == vipEndTime || vipEndTime - calendar.getTimeInMillis() < 0) {
//            throw new BusinessException(RspCode.RESPONSE_FAIL, "创建订单失败, 会员时间已到期");
//        }

        String orderNo = OrderIDUtil.getOrderNo();
        bkcqLocalOrderCreateDTO.setOrderNo(orderNo);

        //冻结库存
        Map<String, Object> product = HttpRequestUtils.freezeSkuStock(bkcqLocalOrderCreateDTO.getGoodsId()
                , bkcqLocalOrderCreateDTO.getSkuId(), orderNo, orderNo, bkcqLocalOrderCreateDTO.getOrderType()
                , bkcqLocalOrderCreateDTO.getQuantity(), null);

        //确定库存是否回滚
        boolean flag = false;
        try {
            bkcqLocalOrderCreateDTO.setProduct(product);
            BkcqLocalOrder order = bkcqLocalOrderService.createOrder(bkcqLocalOrderCreateDTO);
            result.setResultData(order);
        } catch (BusinessException e) {
            flag = true;
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            flag = true;
            throw new BusinessException(RspCode.RESPONSE_FAIL, "创建订单失败");
        } finally {
            if (flag) {
                try {
                    HttpRequestUtils.unfreezeSkuStock(bkcqLocalOrderCreateDTO.getGoodsId(), bkcqLocalOrderCreateDTO.getSkuId()
                            , orderNo, orderNo, bkcqLocalOrderCreateDTO.getOrderType()
                            , bkcqLocalOrderCreateDTO.getQuantity(), null);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

}
