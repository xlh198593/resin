package com.meitianhui.goods.street.handler.impl;

import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.goods.street.consts.ServiceName;
import com.meitianhui.goods.street.dto.FreezeSkuStockDTO;
import com.meitianhui.goods.street.entity.BkcqProducts;
import com.meitianhui.goods.street.handler.BaseServiceHandler;
import com.meitianhui.goods.street.service.BkcqProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 冻结库存业务请求接口
 *
 * @author tortoise
 * @since 2019/3/27 20:40
 */
@Component
public class UnfreezeSkuServiceHandler extends BaseServiceHandler {

    @Autowired
    private BkcqProductsService bkcqProductsService;

    @Override
    public ServiceName getServiceName() {
        return ServiceName.UNFREEZE_SKU_STOCK;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void handle(Map<String, Object> paramsMap, ResultData result) throws BusinessException {
        FreezeSkuStockDTO freezeSkuStockDTO = validate(paramsMap, FreezeSkuStockDTO.class);
        BkcqProducts bkcqProducts = bkcqProductsService.unfreezeSkuStock(freezeSkuStockDTO);
        result.setResultData(bkcqProducts);
    }

}
