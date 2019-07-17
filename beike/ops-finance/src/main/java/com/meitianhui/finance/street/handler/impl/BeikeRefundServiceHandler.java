package com.meitianhui.finance.street.handler.impl;

import com.google.common.collect.Lists;
import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.finance.constant.RspCode;
import com.meitianhui.finance.street.consts.PayWay;
import com.meitianhui.finance.street.consts.ServiceName;
import com.meitianhui.finance.street.dao.FdTransactionsResultDAO;
import com.meitianhui.finance.street.entity.FdTransactionsResult;
import com.meitianhui.finance.street.handler.ServiceHandler;
import com.meitianhui.finance.street.service.StreetPayService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 贝壳支付退款请求接口
 *
 * @author tortoise
 * @since 2019/3/27 20:40
 */
@Component
public class BeikeRefundServiceHandler implements ServiceHandler {

    private static final Logger logger = Logger.getLogger(BeikeRefundServiceHandler.class);

    @Autowired
    private FdTransactionsResultDAO fdTransactionsResultDAO;

    @Autowired
    private StreetPayService streetPayService;

    @Override
    public ServiceName getServiceName() {
        return ServiceName.BEIKE_REFUND;
    }

    @Override
    public void handle(Map<String, Object> paramsMap, ResultData result) throws BusinessException {
        Object orderNoObj = paramsMap.get("orderNo");
        if (null == orderNoObj) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "订单编号不能为空");
        }

        List<FdTransactionsResult> results = fdTransactionsResultDAO
                .selectByOutTradeNoAndPayWay(orderNoObj.toString(), PayWay.ZFFS_07.getValue(), "completed");
        List<FdTransactionsResult> refundResults = Lists.newArrayList();
        if (null != results && !results.isEmpty()) {
            for (FdTransactionsResult fdTransactionsResult : results) {
                try {
                    FdTransactionsResult fdTransactionsResult1 =
                            streetPayService.beikeRefund(fdTransactionsResult.getTransactionNo());
                    refundResults.add(fdTransactionsResult1);
                } catch (Exception e) {
                    logger.warn(String.format("订单自动关闭退款贝壳失败，订单编号：%s，交易编号：%s", orderNoObj,
                            fdTransactionsResult.getTransactionNo()), e);
                }
            }
        }

        result.setResultData(refundResults);
    }

}
