package com.meitianhui.goods.street.handler.impl;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.goods.constant.RspCode;
import com.meitianhui.goods.street.consts.ServiceName;
import com.meitianhui.goods.street.dao.BkcqSkuStockDAO;
import com.meitianhui.goods.street.entity.BkcqSkuStock;
import com.meitianhui.goods.street.handler.ServiceHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 查询街市商品sku库存业务请求接口
 *
 * @author tortoise
 * @since 2019/3/27 20:40
 */
@Component
public class FindSkuStockByKeyServiceHandler implements ServiceHandler {

    @Autowired
    private BkcqSkuStockDAO bkcqSkuStockDAO;

    @Override
    public ServiceName getServiceName() {
        return ServiceName.FIND_SKU_STOCK_BY_KEY;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void handle(Map<String, Object> paramsMap, ResultData result) throws BusinessException {
        Object skuIdObj = paramsMap.get("skuId");
        if (null == skuIdObj) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "商品sku编号不能为空");
        }

        //获取商品sku编号
        Long skuId = Long.valueOf(skuIdObj.toString());

        BkcqSkuStock bkcqSkuStock = bkcqSkuStockDAO.selectByPrimaryKey(skuId);
        if (null == bkcqSkuStock) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "商品sku编号不存在");
        }

        result.setResultData(bkcqSkuStock);
    }

}
