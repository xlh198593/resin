package com.meitianhui.finance.street.handler.impl;

import com.google.common.collect.Lists;
import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.finance.constant.RspCode;
import com.meitianhui.finance.street.consts.ServiceName;
import com.meitianhui.finance.street.dao.FdTransactionsResultDAO;
import com.meitianhui.finance.street.entity.FdTransactionsResult;
import com.meitianhui.finance.street.handler.ServiceHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 查询街市订单交易业务请求接口
 *
 * @author tortoise
 * @since 2019/4/2 14:40
 */
@Component
public class FindTransactionByOrderNoServiceHandler implements ServiceHandler {

    @Autowired
    private FdTransactionsResultDAO fdTransactionsResultDAO;

    @Override
    public ServiceName getServiceName() {
        return ServiceName.FIND_TRANSACTION_BY_ORDER_NO;
    }

    @SuppressWarnings({"Duplicates"})
    @Override
    public void handle(Map<String, Object> paramsMap, ResultData result) throws BusinessException {
        Object orderNoObj = paramsMap.get("orderNo");
        if (null == orderNoObj) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "订单编号不能为空");
        }

        List<FdTransactionsResult> results = fdTransactionsResultDAO
                .selectByOutTradeNoAndPayWay(orderNoObj.toString(), null, null);

        if (null == results) {
            results = Lists.newArrayList();
        }

        result.setResultData(results);
    }

}
