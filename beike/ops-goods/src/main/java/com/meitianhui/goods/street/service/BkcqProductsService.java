package com.meitianhui.goods.street.service;


import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.goods.street.dto.FreezeSkuStockDTO;
import com.meitianhui.goods.street.entity.BkcqProducts;

/**
 * <pre> 街市订单业务操作接口 </pre>
 *
 * @author tortoise
 * @since 2019/3/27 17:05
 */
public interface BkcqProductsService {

    /**
     * 冻结库存
     *
     * @param freezeSkuStockDTO 请求参数
     * @return BkcqProducts 街市商品，会把街市商品sku设置进去
     * @throws BusinessException 业务异常
     */
    BkcqProducts freezeSkuStock(FreezeSkuStockDTO freezeSkuStockDTO) throws BusinessException;

    /**
     * 解冻库存
     *
     * @param freezeSkuStockDTO 请求参数
     * @return BkcqProducts 街市商品，会把街市商品sku设置进去
     * @throws BusinessException 业务异常
     */
    BkcqProducts unfreezeSkuStock(FreezeSkuStockDTO freezeSkuStockDTO) throws BusinessException;

    /**
     * 扣减库存
     *
     * @param freezeSkuStockDTO 请求参数
     * @return BkcqProducts 街市商品，会把街市商品sku设置进去
     * @throws BusinessException 业务异常
     */
    BkcqProducts deductionSkuStock(FreezeSkuStockDTO freezeSkuStockDTO) throws BusinessException;

}
