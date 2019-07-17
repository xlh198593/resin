package com.meitianhui.goods.street.handler.impl;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.goods.constant.RspCode;
import com.meitianhui.goods.street.consts.ServiceName;
import com.meitianhui.goods.street.dao.BkcqProductsStockDAO;
import com.meitianhui.goods.street.entity.BkcqProductsStock;
import com.meitianhui.goods.street.handler.ServiceHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 查询街市商品库存业务请求接口
 *
 * @author tortoise
 * @since 2019/3/27 20:40
 */
@Component
public class FindProductStockByKeyServiceHandler implements ServiceHandler {

    @Autowired
    private BkcqProductsStockDAO bkcqProductsStockDAO;

    @Override
    public ServiceName getServiceName() {
        return ServiceName.FIND_PRODUCT_STOCK_BY_KEY;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void handle(Map<String, Object> paramsMap, ResultData result) throws BusinessException {
        Object goodsIdObj = paramsMap.get("goodsId");
        if (null == goodsIdObj) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "商品编号不能为空");
        }

        //获取商品编号
        Long goodsId = Long.valueOf(goodsIdObj.toString());

        BkcqProductsStock bkcqProductsStock = bkcqProductsStockDAO.selectByPrimaryKey(goodsId);
        if (null == bkcqProductsStock) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "商品编号不存在");
        }

        result.setResultData(bkcqProductsStock);
    }

}
