package com.meitianhui.order.street.handler.impl;

import com.google.common.collect.Maps;
import com.meitianhui.common.constant.CommonRspCode;
import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.order.constant.RspCode;
import com.meitianhui.order.street.consts.ServiceName;
import com.meitianhui.order.street.dao.BkcqLoadCodeDAO;
import com.meitianhui.order.street.dao.BkcqLocalOrderDAO;
import com.meitianhui.order.street.encryption.Aes;
import com.meitianhui.order.street.encryption.Encryption;
import com.meitianhui.order.street.entity.BkcqLoadCode;
import com.meitianhui.order.street.entity.BkcqLocalOrder;
import com.meitianhui.order.street.handler.BaseServiceHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 根据订单编号查询街市订单核销码业务请求接口
 *
 * @author tortoise
 * @since 2019/3/27 16:40
 */
@SuppressWarnings("Duplicates")
@Component
public class FindStreetOrderCodeServiceHandler extends BaseServiceHandler {

    @Autowired
    private BkcqLocalOrderDAO bkcqLocalOrderDAO;

    @Autowired
    private BkcqLoadCodeDAO bkcqLoadCodeDAO;

    @Override
    public ServiceName getServiceName() {
        return ServiceName.FIND_STREET_ORDER_CODE;
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

        BkcqLocalOrder bkcqLocalOrder = bkcqLocalOrderDAO.selectByOrderNo(orderNoObj.toString());
        if (null == bkcqLocalOrder) {
            throw new BusinessException(CommonRspCode.RESPONSE_FAIL, "订单记录不存在");
        }

        List<BkcqLoadCode> codeList = bkcqLoadCodeDAO.selectByOrderNo(orderNoObj.toString());
        if (null == codeList || codeList.isEmpty()) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "该订单不存在核销码");
        }

        StringBuilder codeStr = new StringBuilder();
        for (BkcqLoadCode bkcqLoadCode : codeList) {
            codeStr.append(bkcqLoadCode.getCheckCode()).append(",");
        }

        Map<String, Object> data = Maps.newHashMap();
        if (codeStr.length() > 1) {
            data.put("codeStr", Aes.getInstance().encrypt(Encryption.EncryptionKey.builder()
                    .key(bkcqLocalOrder.getStoresId()).build(), codeStr.substring(0, codeStr.length() - 1)));
        }

        data.put("codes", codeList);

        result.setResultData(data);
    }

}
