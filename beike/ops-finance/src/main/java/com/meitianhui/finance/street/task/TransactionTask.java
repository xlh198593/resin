package com.meitianhui.finance.street.task;

import cn.hutool.core.date.DateUtil;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.google.common.collect.Maps;
import com.meitianhui.finance.street.consts.PayWay;
import com.meitianhui.finance.street.dao.FdTransactionsResultDAO;
import com.meitianhui.finance.street.entity.FdTransactionsResult;
import com.meitianhui.finance.street.handler.impl.AliPayHandler;
import com.meitianhui.finance.street.handler.impl.WxPayHandler;
import com.meitianhui.finance.street.service.StreetPayService;
import com.meitianhui.finance.street.utils.HttpRequestUtils;
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
 * @since 2019/4/4 10:05
 */
@Component
public class TransactionTask {

    private static final Logger logger = Logger.getLogger(TransactionTask.class);

    @Autowired
    private FdTransactionsResultDAO fdTransactionsResultDAO;

    @Autowired
    private WxPayHandler wxPayHandler;

    @Autowired
    private AliPayHandler aliPayHandler;

    @Autowired
    private StreetPayService streetPayService;

    /**
     * 每隔5分钟定时同步交易支付结果
     */
    //@Scheduled(cron = "0 */5 * * * ?")
    public void syncTransactionPayResult() {
        logger.info("定时执行交易同步开始...............................");

        Date now = new Date();

        //查询5分钟以前的订单
        Map<String, Object> params = Maps.newHashMap();
        params.put("orderTypeKey", "DDLX_13");
        params.put("transactionStatus", "underway");
        params.put("createdDateBegin", DateUtil.offsetMinute(now, -6));
        params.put("createdDateEnd", DateUtil.offsetMinute(now, -5));
        List<FdTransactionsResult> results = fdTransactionsResultDAO.selectByParams(params);
        for (FdTransactionsResult result : results) {
            try {
                //支付宝支付
                if (PayWay.ZFFS_01.getValue().equals(result.getPaymentWayKey())) {
                    AlipayTradeQueryResponse payResult = aliPayHandler.getPayResult(result.getTransactionNo());
                    //支付成功
                    if ("TRADE_SUCCESS".equals(payResult.getTradeStatus())) {
                        FdTransactionsResult fdTransactionsResult = streetPayService.aliPaySyncSuccess(payResult);
                        if ("completed".equals(fdTransactionsResult.getTransactionStatus())) {
                            // 调用订单接口回写订单完成并生成核销码
                            HttpRequestUtils.orderPaySuccess(fdTransactionsResult.getOutTradeNo(),result.getPaymentWayKey());
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(String.format("自动同步交易结果失败，失败参数：[%s]", result), e);
            }
        }

        logger.info("定时执行交易同步结束...............................");
    }

}
