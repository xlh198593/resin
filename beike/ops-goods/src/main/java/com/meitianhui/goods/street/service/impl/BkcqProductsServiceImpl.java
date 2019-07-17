package com.meitianhui.goods.street.service.impl;

import cn.hutool.core.util.StrUtil;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.goods.constant.RspCode;
import com.meitianhui.goods.street.consts.OrderType;
import com.meitianhui.goods.street.consts.ProductStatus;
import com.meitianhui.goods.street.consts.StockChangeStatus;
import com.meitianhui.goods.street.dao.*;
import com.meitianhui.goods.street.dto.FreezeSkuStockDTO;
import com.meitianhui.goods.street.entity.*;
import com.meitianhui.goods.street.service.BkcqProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 冻结街市订单业务请求接口
 *
 * @author tortoise
 * @since 2019/3/27 16:40
 */
@SuppressWarnings("Duplicates")
@Service
public class BkcqProductsServiceImpl implements BkcqProductsService {

    @Autowired
    private BkcqProductsDAO bkcqProductsDAO;

    @Autowired
    private BkcqProductsStockDAO bkcqProductsStockDAO;

    @Autowired
    private BkcqSkuDAO bkcqSkuDAO;

    @Autowired
    private BkcqSkuStockDAO bkcqSkuStockDAO;

    @Autowired
    private BkcqStockChangeLogDAO bkcqStockChangeLogDAO;

    @Autowired
    private BkcqGoodsCountDAO bkcqGoodsCountDAO;

    /**
     * 冻结库存
     *
     * @param freezeSkuStockDTO 请求参数
     * @return BkcqProducts 街市商品，会把街市商品sku设置进去
     * @throws BusinessException 业务异常
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BkcqProducts freezeSkuStock(FreezeSkuStockDTO freezeSkuStockDTO) throws BusinessException {
        Date now = new Date();

        BkcqProducts bkcqProducts = bkcqProductsDAO.selectByPrimaryKey(Long.valueOf(freezeSkuStockDTO.getGoodsId()));
        if (null == bkcqProducts) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "商品编号不存在");
        }

        if (!ProductStatus.ON_SHELF.getValue().equals(bkcqProducts.getStatus())) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "商品未上架");
        }

        if (now.getTime() < bkcqProducts.getActivityStartTime().getTime()) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "商品尚未开始销售");
        }

        BkcqSku bkcqSku = bkcqSkuDAO.selectByPrimaryKey(Long.valueOf(freezeSkuStockDTO.getSkuId()));
        if (null == bkcqSku) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "商品sku编号不存在");
        }

        OrderType orderType = OrderType.parse(freezeSkuStockDTO.getOrderType());
        String remark = StrUtil.isBlank(freezeSkuStockDTO.getRemark()) ? "下单冻结库存" : freezeSkuStockDTO.getRemark();
        BkcqStockChangeLog bkcqStockChangeLog = BkcqStockChangeLog.builder().goodsId(bkcqSku.getGoodsId())
                .skuId(bkcqSku.getSkuId()).tid(freezeSkuStockDTO.getTid()).oid(freezeSkuStockDTO.getOid())
                .amount(-freezeSkuStockDTO.getQuantity()).orderType(orderType.getValue())
                .status(StockChangeStatus.UNKNOW.getValue()).createdTime(now).updateTime(now).remark(remark).build();
        int row = bkcqStockChangeLogDAO.insertSelective(bkcqStockChangeLog);
        if (row <= 0) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "商品库存不足");
        }

        //修改商品sku库存
        BkcqSkuStock bkcqSkuStock = BkcqSkuStock.builder().skuId(bkcqSku.getSkuId())
                .store(-freezeSkuStockDTO.getQuantity()).freez(freezeSkuStockDTO.getQuantity()).build();
        try {
            row = bkcqSkuStockDAO.updateByPrimaryKeySelective(bkcqSkuStock);
        } catch (Exception e) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "商品库存不足");
        }

        if (row <= 0) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "商品库存不足");
        }

        //修改商品库存
        BkcqProductsStock bkcqProductsStock = BkcqProductsStock.builder().goodsId(bkcqProducts.getGoodsId())
                .store(-freezeSkuStockDTO.getQuantity()).freez(freezeSkuStockDTO.getQuantity()).build();
        try {
            row = bkcqProductsStockDAO.updateByPrimaryKeySelective(bkcqProductsStock);
        } catch (Exception e) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "商品库存不足");
        }

        if (row <= 0) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "商品库存不足");
        }

        bkcqProducts.setBkcqSku(bkcqSku);
        return bkcqProducts;
    }

    /**
     * 解冻库存
     *
     * @param freezeSkuStockDTO 请求参数
     * @return BkcqProducts 街市商品，会把街市商品sku设置进去
     * @throws BusinessException 业务异常
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BkcqProducts unfreezeSkuStock(FreezeSkuStockDTO freezeSkuStockDTO) throws BusinessException {
        BkcqProducts bkcqProducts = bkcqProductsDAO.selectByPrimaryKey(Long.valueOf(freezeSkuStockDTO.getGoodsId()));
        if (null == bkcqProducts) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "商品编号不存在");
        }

        BkcqSku bkcqSku = bkcqSkuDAO.selectByPrimaryKey(Long.valueOf(freezeSkuStockDTO.getSkuId()));
        if (null == bkcqSku) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "商品sku编号不存在");
        }

        bkcqProducts.setBkcqSku(bkcqSku);

        Date now = new Date();
        String remark = StrUtil.isBlank(freezeSkuStockDTO.getRemark()) ? "下单取消解冻库存" : freezeSkuStockDTO.getRemark();
        BkcqStockChangeLog bkcqStockChangeLog = BkcqStockChangeLog.builder().skuId(bkcqSku.getSkuId())
                .oid(freezeSkuStockDTO.getOid()).build();

        BkcqStockChangeLog findBkcqStockChangeLog = bkcqStockChangeLogDAO.selectBySkuIdAndOrderNo(bkcqStockChangeLog);
        if (!StockChangeStatus.UNKNOW.getValue().equals(findBkcqStockChangeLog.getStatus())) {
            return bkcqProducts;
        }

        bkcqStockChangeLog.setLogId(findBkcqStockChangeLog.getLogId());
        bkcqStockChangeLog.setStatus(StockChangeStatus.FAILED.getValue());
        bkcqStockChangeLog.setUpdateTime(now);
        bkcqStockChangeLog.setRemark(remark);
        bkcqStockChangeLog.setOldStatus(StockChangeStatus.UNKNOW.getValue());
        int row = bkcqStockChangeLogDAO.updateByPrimaryKeySelective(bkcqStockChangeLog);
        if (row <= 0) {
            return bkcqProducts;
        }

        //修改商品sku库存
        BkcqSkuStock bkcqSkuStock = BkcqSkuStock.builder().skuId(bkcqSku.getSkuId())
                .store(freezeSkuStockDTO.getQuantity()).freez(-freezeSkuStockDTO.getQuantity()).build();
        row = bkcqSkuStockDAO.updateByPrimaryKeySelective(bkcqSkuStock);
        if (row <= 0) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "库存解冻失败");
        }

        //修改商品库存
        BkcqProductsStock bkcqProductsStock = BkcqProductsStock.builder().goodsId(bkcqProducts.getGoodsId())
                .store(freezeSkuStockDTO.getQuantity()).freez(-freezeSkuStockDTO.getQuantity()).build();
        row = bkcqProductsStockDAO.updateByPrimaryKeySelective(bkcqProductsStock);
        if (row <= 0) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "库存解冻失败");
        }

        return bkcqProducts;
    }

    /**
     * 扣减库存
     *
     * @param freezeSkuStockDTO 请求参数
     * @return BkcqProducts 街市商品，会把街市商品sku设置进去
     * @throws BusinessException 业务异常
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BkcqProducts deductionSkuStock(FreezeSkuStockDTO freezeSkuStockDTO) throws BusinessException {
        BkcqProducts bkcqProducts = bkcqProductsDAO.selectByPrimaryKey(Long.valueOf(freezeSkuStockDTO.getGoodsId()));
        if (null == bkcqProducts) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "商品编号不存在");
        }

        BkcqSku bkcqSku = bkcqSkuDAO.selectByPrimaryKey(Long.valueOf(freezeSkuStockDTO.getSkuId()));
        if (null == bkcqSku) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "商品sku编号不存在");
        }

        bkcqProducts.setBkcqSku(bkcqSku);

        Date now = new Date();
        String remark = StrUtil.isBlank(freezeSkuStockDTO.getRemark()) ? "支付成功扣减库存" : freezeSkuStockDTO.getRemark();
        BkcqStockChangeLog bkcqStockChangeLog = BkcqStockChangeLog.builder().skuId(bkcqSku.getSkuId())
                .oid(freezeSkuStockDTO.getOid()).build();

        BkcqStockChangeLog findBkcqStockChangeLog = bkcqStockChangeLogDAO.selectBySkuIdAndOrderNo(bkcqStockChangeLog);
        if (StockChangeStatus.SUCCESS.getValue().equals(findBkcqStockChangeLog.getStatus())) {
            return bkcqProducts;
        }

        bkcqStockChangeLog.setLogId(findBkcqStockChangeLog.getLogId());
        bkcqStockChangeLog.setStatus(StockChangeStatus.SUCCESS.getValue());
        bkcqStockChangeLog.setUpdateTime(now);
        bkcqStockChangeLog.setRemark(remark);
        bkcqStockChangeLog.setOldStatus(StockChangeStatus.UNKNOW.getValue());
        int row = bkcqStockChangeLogDAO.updateByPrimaryKeySelective(bkcqStockChangeLog);
        if (row <= 0) {
            return bkcqProducts;
        }

        //修改商品sku库存
        BkcqSkuStock bkcqSkuStock = BkcqSkuStock.builder().skuId(bkcqSku.getSkuId())
                .freez(-freezeSkuStockDTO.getQuantity()).build();
        row = bkcqSkuStockDAO.updateByPrimaryKeySelective(bkcqSkuStock);
        if (row <= 0) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "库存扣减失败");
        }

        //修改商品库存
        BkcqProductsStock bkcqProductsStock = BkcqProductsStock.builder().goodsId(bkcqProducts.getGoodsId())
                .freez(-freezeSkuStockDTO.getQuantity()).build();
        row = bkcqProductsStockDAO.updateByPrimaryKeySelective(bkcqProductsStock);
        if (row <= 0) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "库存扣减失败");
        }

        //增加销量
        BkcqGoodsCount bkcqGoodsCount = BkcqGoodsCount.builder().itemId(bkcqProducts.getGoodsId().intValue())
                .buyCount(-findBkcqStockChangeLog.getAmount()).soldQuantity(-findBkcqStockChangeLog.getAmount()).build();
        bkcqGoodsCountDAO.updateByPrimaryKeySelective(bkcqGoodsCount);

        return bkcqProducts;
    }
}
