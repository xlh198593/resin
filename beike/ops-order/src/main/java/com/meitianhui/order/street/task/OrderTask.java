package com.meitianhui.order.street.task;

import com.google.common.collect.Lists;
import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.order.street.dao.BkcqLoadCodeDAO;
import com.meitianhui.order.street.dao.BkcqLocalOrderDAO;
import com.meitianhui.order.street.entity.BkcqLoadCode;
import com.meitianhui.order.street.entity.BkcqLocalOrder;
import com.meitianhui.order.street.service.BkcqLocalOrderService;
import com.meitianhui.order.street.utils.HttpRequestUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <pre> 订单扫描任务 </pre>
 *
 * @author tortoise
 * @since 2019/3/28 17:05
 */
@SuppressWarnings("unchecked")
@Component
public class OrderTask {

    private static final Logger logger = Logger.getLogger(OrderTask.class);

    @Autowired
    private BkcqLocalOrderDAO bkcqLocalOrderDAO;

    @Autowired
    private BkcqLoadCodeDAO bkcqLoadCodeDAO;

    @Autowired
    private BkcqLocalOrderService bkcqLocalOrderService;

    /**
     * 每隔一分钟定时处理过期订单
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void handleExpireOrder() {
        logger.info("定时执行订单过期开始...............................");
        List<BkcqLocalOrder> orders = bkcqLocalOrderDAO.selectExpireOrder(new Date(),
                Lists.newArrayList("WAIT_BUYER_PAY"));

        for (BkcqLocalOrder order : orders) {
            try {
                BkcqLocalOrder bkcqLocalOrder = bkcqLocalOrderService.autoCancelOrder(order.getOrderNo());
                String desc = bkcqLocalOrder.getDesc1();
                List<Map<String, Object>> list = FastJsonUtil.jsonToList(desc);
                for (Map<String, Object> map : list) {
                    try {
                        HttpRequestUtils.unfreezeSkuStock(map.get("goodsId").toString(), map.get("skuId").toString()
                                , bkcqLocalOrder.getOrderNo(), bkcqLocalOrder.getOrderNo(), "BKJS"
                                , Integer.parseInt(map.get("quantity").toString()), "自动取消订单解冻库存");
                    } catch (Exception e) {
                        logger.error(String.format("自动取消订单失败，解冻库存失败，失败参数：[%s]", order), e);
                    }

                    try {
                        if (order.getBeikeCredit() > 0) {
                            HttpRequestUtils.beikeRefund(bkcqLocalOrder.getOrderNo());
                        }
                    } catch (Exception e) {
                        logger.error(String.format("自动取消订单失败，退款贝壳失败，失败参数：[%s]", order), e);
                    }
                }
            } catch (Exception e) {
                logger.error(String.format("自动取消订单失败，失败参数：[%s]", order), e);
            }
        }
        logger.info("定时执行订单过期结束...............................");
    }

    /**
     * 每隔一分钟定时处理过期订单核销码
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void handleExpireOrderCode() {
        logger.info("定时执行过期核销码开始...............................");
        List<BkcqLoadCode> codes = bkcqLoadCodeDAO.selectExpireCode(new Date(),
                Lists.newArrayList("CODE_WAIT_USE"));

        for (BkcqLoadCode code : codes) {
            try {
                bkcqLocalOrderService.autoExpiredOrderCode(code.getId());
            } catch (Exception e) {
                logger.error(String.format("自动行过期核销码失败，失败参数：[%s]", code), e);
            }
        }
        logger.info("定时执行过期核销码结束...............................");
    }
}
