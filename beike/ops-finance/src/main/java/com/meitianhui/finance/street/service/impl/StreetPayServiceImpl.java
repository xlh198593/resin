package com.meitianhui.finance.street.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;
import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.finance.constant.RspCode;
import com.meitianhui.finance.constant.TradeIDUtil;
import com.meitianhui.finance.street.consts.PayWay;
import com.meitianhui.finance.street.dao.*;
import com.meitianhui.finance.street.dto.StreetOrderPayDTO;
import com.meitianhui.finance.street.entity.*;
import com.meitianhui.finance.street.handler.PayHandler;
import com.meitianhui.finance.street.service.StreetPayService;
import com.meitianhui.finance.street.utils.StreetUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 街市订单支付业务请求接口
 *
 * @author tortoise
 * @since 2019/3/27 16:40
 */
@SuppressWarnings({"Duplicates", "unchecked"})
@Service
public class StreetPayServiceImpl implements StreetPayService {

    private static final Logger logger = Logger.getLogger(StreetPayServiceImpl.class);

    @Autowired
    private FdTransactionsResultDAO fdTransactionsResultDAO;

    @Autowired
    private FdMemberAssetDAO fdMemberAssetDAO;

    @Autowired
    private FdMemberShellLogDAO fdMemberShellLogDAO;

    @Autowired
    private FdMemberRebateLogDAO fdMemberRebateLogDAO;

    @Autowired
    private FdMemberCashLogDAO fdMemberCashLogDAO;

    /**
     * 订单支付
     *
     * @param streetOrderPayDTO 支付传输对象
     * @return Map<PayWay, FdTransactionsResult> 交易结果集合
     * @throws BusinessException 业务异常
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, FdTransactionsResult> orderPay(StreetOrderPayDTO streetOrderPayDTO) throws BusinessException {
        Map<String, FdTransactionsResult> results = Maps.newHashMap();
        String consumerId = StreetUtils.getValue("consumerId", streetOrderPayDTO.getOrder());
        if (!streetOrderPayDTO.getConsumerId().equals(consumerId)) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "非法操作，请慎重！");
        }

        FdMemberAsset consumer = fdMemberAssetDAO.selectByMemberInfo("consumer", consumerId);

        //获取交易体
        String tradeBody = getTradeBody(streetOrderPayDTO.getOrder());

        // 贝壳支付
        FdTransactionsResult fdTransactionsResult = bikePay(streetOrderPayDTO, consumer, tradeBody);
        if (null != fdTransactionsResult) {
            results.put(PayWay.ZFFS_07.getValue(), fdTransactionsResult);
        }

        //其他支付方式
        PayWay payWay = PayWay.parse(streetOrderPayDTO.getPayWay());
        switch (payWay) {
            case ZFFS_01:
                FdTransactionsResult aliFdTransactionsResult = aliPay(streetOrderPayDTO, consumer, tradeBody);
                results.put(payWay.getValue(), aliFdTransactionsResult);
                break;
            case ZFFS_02:
                FdTransactionsResult wxFdTransactionsResult = wxPay(streetOrderPayDTO, consumer, tradeBody);
                results.put(payWay.getValue(), wxFdTransactionsResult);
                break;
            case ZFFS_07:
                break;
            case ZFFS_08:
                FdTransactionsResult redFdTransactionsResult = redPay(streetOrderPayDTO, consumer, tradeBody);
                results.put(payWay.getValue(), redFdTransactionsResult);
                break;
            default:
                throw new BusinessException(RspCode.RESPONSE_FAIL, "支付方式不存在");
        }

        return results;
    }

    /**
     * 贝壳支付退款
     *
     * @param transactionNo 退款交易编号
     * @return FdTransactionsResult 交易结果
     * @throws BusinessException 业务异常
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public FdTransactionsResult beikeRefund(String transactionNo) throws BusinessException {
        FdTransactionsResult fdTransactionsResult = fdTransactionsResultDAO.selectByTransactionNo(transactionNo);
        if (!"completed".equals(fdTransactionsResult.getTransactionStatus())) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "交易结果状态异常！");
        }

        //如果存在退款
        List<FdTransactionsResult> fdTransactionsResults = fdTransactionsResultDAO.selectByOutTradeNoAndPayWay(
                transactionNo, PayWay.ZFFS_07.getValue(), "completed");
        if (null != fdTransactionsResults && !fdTransactionsResults.isEmpty()) {
            return fdTransactionsResults.get(0);
        }

        Date now = new Date();

        //复制Bean
        FdTransactionsResult refundFdTransactionsResult = new FdTransactionsResult();
        BeanUtil.copyProperties(fdTransactionsResult, refundFdTransactionsResult);

        refundFdTransactionsResult.setTransactionId(null);
        refundFdTransactionsResult.setTransactionNo(TradeIDUtil.getTradeNo());
        refundFdTransactionsResult.setOutTradeNo(fdTransactionsResult.getTransactionNo());
        refundFdTransactionsResult.setOutTradeBody(null);
        refundFdTransactionsResult.setTransactionBody(null);
        refundFdTransactionsResult.setTradeTypeKey("JYLX_02");
        refundFdTransactionsResult.setDetail("贝壳街市订单退款");
        refundFdTransactionsResult.setTransactionDate(now);
        refundFdTransactionsResult.setCreatedDate(now);
        refundFdTransactionsResult.setModifiedDate(now);
        refundFdTransactionsResult.setRemark("消费者端App|街市订单退款|贝壳退款|" + fdTransactionsResult.getAmount().toString());
        fdTransactionsResultDAO.insertSelective(refundFdTransactionsResult);

        //消费者增加贝壳
        FdMemberAsset consumer = fdMemberAssetDAO.selectByMemberInfo("consumer", fdTransactionsResult.getBuyerId());
        FdMemberShellLog fdMemberShellLog = new FdMemberShellLog();
        fdMemberShellLog.setMemberTypeKey("consumer");
        fdMemberShellLog.setMemberId(consumer.getMemberId());
        fdMemberShellLog.setCategory("income");
        fdMemberShellLog.setPreBalance(consumer.getShell().intValue());
        fdMemberShellLog.setAmount(refundFdTransactionsResult.getAmount().intValue());
        fdMemberShellLog.setTransactionNo(refundFdTransactionsResult.getTransactionNo());
        fdMemberShellLog.setBalance(consumer.getShell().intValue() + refundFdTransactionsResult.getAmount().intValue());
        fdMemberShellLog.setTrackedDate(now);
        fdMemberShellLog.setRemark(String.format("{\"detail\":\"%s\",\"orderNo\":\"%s\"}"
                , "贝壳街市订单自动关闭退款贝壳", fdTransactionsResult.getOutTradeNo()));
        fdMemberShellLogDAO.insertSelective(fdMemberShellLog);

        consumer.setShell(consumer.getShell() + refundFdTransactionsResult.getAmount().longValue());
        FdMemberAsset updateConsumer = new FdMemberAsset();
        updateConsumer.setAssetId(consumer.getAssetId());
        updateConsumer.setShell(refundFdTransactionsResult.getAmount().longValue());
        updateConsumer.setModifiedDate(now);
        fdMemberAssetDAO.updateByPrimaryKeySelective(updateConsumer);

        // 公司减少贝壳
        FdMemberAsset company = fdMemberAssetDAO.selectByMemberInfo("company", PayHandler.SHELL_ALI_ASSET_ID);
        FdMemberShellLog companyFdMemberShellLog = new FdMemberShellLog();
        companyFdMemberShellLog.setMemberTypeKey("company");
        companyFdMemberShellLog.setMemberId(PayHandler.SHELL_ALI_ASSET_ID);
        companyFdMemberShellLog.setCategory("expenditure");
        companyFdMemberShellLog.setPreBalance(company.getShell().intValue());
        companyFdMemberShellLog.setAmount(-refundFdTransactionsResult.getAmount().intValue());
        companyFdMemberShellLog.setTransactionNo(refundFdTransactionsResult.getTransactionNo());
        companyFdMemberShellLog.setBalance(company.getShell().intValue() - refundFdTransactionsResult.getAmount().intValue());
        companyFdMemberShellLog.setTrackedDate(now);
        companyFdMemberShellLog.setRemark(String.format("{\"detail\":\"%s\",\"orderNo\":\"%s\"}"
                , "贝壳街市订单自动关闭退款贝壳", fdTransactionsResult.getOutTradeNo()));
        fdMemberShellLogDAO.insertSelective(companyFdMemberShellLog);

        company.setShell(company.getShell() - refundFdTransactionsResult.getAmount().longValue());
        FdMemberAsset updateCompany = new FdMemberAsset();
        updateCompany.setAssetId(company.getAssetId());
        updateCompany.setShell(-refundFdTransactionsResult.getAmount().longValue());
        updateCompany.setModifiedDate(now);
        fdMemberAssetDAO.updateByPrimaryKeySelective(updateCompany);

        return refundFdTransactionsResult;
    }

    /**
     * 微信支付回调成功
     *
     * @param params 回调参数
     * @return FdTransactionsResult 交易结果集合
     * @throws BusinessException 业务异常
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public FdTransactionsResult wxPayCallbackSuccess(Map<String, String> params) throws BusinessException {
        // 流水号
        String outTradeNo = params.get("out_trade_no");

        return updateWxFdTransactionsResult(outTradeNo, params);
    }

    /**
     * 支付宝支付回调成功
     *
     * @param params 回调参数
     * @return FdTransactionsResult 交易结果集合
     * @throws BusinessException 业务异常
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public FdTransactionsResult aliPayCallbackSuccess(Map<String, String> params) throws BusinessException {
        // 流水号
        String outTradeNo = params.get("out_trade_no");

        return updateAliFdTransactionsResult(outTradeNo, params);
    }

    @Override
    public FdTransactionsResult aliPaySyncSuccess(AlipayTradeQueryResponse params) throws BusinessException {
        return null;
    }

    /**
     * 贝壳支付
     *
     * @param streetOrderPayDTO 街市订单支付传输对象
     * @param consumer          消费者
     * @param tradeBody         交易体
     * @return FdTransactionsResult
     * @throws BusinessException 业务异常
     */
    private FdTransactionsResult bikePay(StreetOrderPayDTO streetOrderPayDTO, FdMemberAsset consumer,
                                         String tradeBody) throws BusinessException {
        FdTransactionsResult fdTransactionsResult = null;
        Calendar calendar = Calendar.getInstance();
        //贝壳交易订单
        BigDecimal beikeCredit = new BigDecimal(StreetUtils.getValue("beikeCredit", streetOrderPayDTO.getOrder()));
        if (beikeCredit.compareTo(new BigDecimal("0")) > 0) {
            if (consumer.getShell() < beikeCredit.longValue()) {
                throw new BusinessException(RspCode.RESPONSE_FAIL, "账户贝壳余额不足");
            }

            fdTransactionsResult = insertBeikeFdTransactionsResult(streetOrderPayDTO, calendar, tradeBody, beikeCredit, consumer);
        }

        return fdTransactionsResult;
    }

    /**
     * 红包支付
     *
     * @param streetOrderPayDTO 街市订单支付传输对象
     * @param consumer          消费者
     * @param tradeBody         交易体
     * @return FdTransactionsResult
     * @throws BusinessException 业务异常
     */
    private FdTransactionsResult redPay(StreetOrderPayDTO streetOrderPayDTO, FdMemberAsset consumer,
                                        String tradeBody) throws BusinessException {
        FdTransactionsResult fdTransactionsResult = null;
        Calendar calendar = Calendar.getInstance();
        //红包交易订单
        BigDecimal vipFee = new BigDecimal(StreetUtils.getValue("vipFee", streetOrderPayDTO.getOrder()));
        if (vipFee.compareTo(new BigDecimal("0")) > 0) {
            if (consumer.getCashBalance().compareTo(vipFee) < 0) {
                throw new BusinessException(RspCode.RESPONSE_FAIL, "账户红包余额不足");
            }

            fdTransactionsResult = insertRedFdTransactionsResult(streetOrderPayDTO, calendar, tradeBody, vipFee, consumer);
        }

        return fdTransactionsResult;
    }

    /**
     * 微信支付
     *
     * @param streetOrderPayDTO 街市订单支付传输对象
     * @param consumer          消费者
     * @param tradeBody         交易体
     * @return FdTransactionsResult
     * @throws BusinessException 业务异常
     */
    private FdTransactionsResult wxPay(StreetOrderPayDTO streetOrderPayDTO, FdMemberAsset consumer,
                                       String tradeBody) throws BusinessException {
        Calendar calendar = Calendar.getInstance();
        BigDecimal vipFee = new BigDecimal(StreetUtils.getValue("vipFee", streetOrderPayDTO.getOrder()));
        return insertWxFdTransactionsResult(streetOrderPayDTO, calendar, tradeBody, vipFee, consumer);
    }

    /**
     * 支付宝支付
     *
     * @param streetOrderPayDTO 街市订单支付传输对象
     * @param consumer          消费者
     * @param tradeBody         交易体
     * @return FdTransactionsResult
     * @throws BusinessException 业务异常
     */
    private FdTransactionsResult aliPay(StreetOrderPayDTO streetOrderPayDTO, FdMemberAsset consumer,
                                        String tradeBody) throws BusinessException {
        Calendar calendar = Calendar.getInstance();
        BigDecimal vipFee = new BigDecimal(StreetUtils.getValue("vipFee", streetOrderPayDTO.getOrder()));
        return insertAliFdTransactionsResult(streetOrderPayDTO, calendar, tradeBody, vipFee, consumer);
    }

    /**
     * 新增贝壳交易结果实体记录
     *
     * @param streetOrderPayDTO 街市订单支付传输对象
     * @param calendar          日历
     * @param tradeBody         交易体
     * @param beikeCredit       贝壳
     * @param consumer          消费者
     * @return FdTransactionsResult 交易结果实体
     */
    private FdTransactionsResult insertBeikeFdTransactionsResult(StreetOrderPayDTO streetOrderPayDTO, Calendar calendar,
                                                                 String tradeBody, BigDecimal beikeCredit, FdMemberAsset consumer) {
        String orderNo = StreetUtils.getValue("orderNo", streetOrderPayDTO.getOrder());
        String consumerId = StreetUtils.getValue("consumerId", streetOrderPayDTO.getOrder());
        String contactPerson = StreetUtils.getValue("contactPerson", streetOrderPayDTO.getOrder());
        String contactTel = StreetUtils.getValue("contactTel", streetOrderPayDTO.getOrder());
        String storesId = StreetUtils.getValue("storesId", streetOrderPayDTO.getOrder());
        String storesName = StreetUtils.getValue("storesName", streetOrderPayDTO.getOrder());

        List<FdTransactionsResult> fdTransactionsResults = fdTransactionsResultDAO
                .selectByOutTradeNoAndPayWay(streetOrderPayDTO.getOrderNo(), PayWay.ZFFS_07.getValue(), "completed");
        if (null != fdTransactionsResults && !fdTransactionsResults.isEmpty()) {
            return fdTransactionsResults.get(0);
        }

        //新增贝壳交易结果
        FdTransactionsResult fdTransactionsResult = FdTransactionsResult.builder().transactionNo(TradeIDUtil.getTradeNo())
                .dataSource(streetOrderPayDTO.getDataSource()).tradeTypeKey("JYLX_01").orderTypeKey("DDLX_13")
                .paymentWayKey(PayWay.ZFFS_07.getValue()).transactionDate(calendar.getTime())
                .detail("贝壳街市订单交易").amount(beikeCredit).outTradeNo(orderNo).outTradeBody(tradeBody)
                .buyerMemberType("consumer").buyerId(consumerId).buyerName(contactPerson).buyerContact(contactTel)
                .sellerMemberType("stores").sellerId(storesId).sellerName(storesName)
                .transactionStatus("completed").createdDate(calendar.getTime()).modifiedDate(calendar.getTime())
                .remark("消费者端App|街市订单支付|贝壳支付|" + beikeCredit.toString()).build();
        fdTransactionsResultDAO.insertSelective(fdTransactionsResult);

        //消费者减去贝壳
        FdMemberShellLog fdMemberShellLog = new FdMemberShellLog();
        fdMemberShellLog.setMemberTypeKey("consumer");
        fdMemberShellLog.setMemberId(consumerId);
        fdMemberShellLog.setCategory("expenditure");
        fdMemberShellLog.setPreBalance(consumer.getShell().intValue());
        fdMemberShellLog.setAmount(-beikeCredit.intValue());
        fdMemberShellLog.setTransactionNo(fdTransactionsResult.getTransactionNo());
        fdMemberShellLog.setBalance(consumer.getShell().intValue() - beikeCredit.intValue());
        fdMemberShellLog.setTrackedDate(calendar.getTime());
        fdMemberShellLog.setRemark(String.format("{\"detail\":\"%s\",\"orderNo\":\"%s\",\"orderType\":\"DDLX_13\",\"itemNum\":\"%s\"}"
                , "贝壳街市订单", orderNo, StreetUtils.getValue("itemNum", streetOrderPayDTO.getOrder())));
        fdMemberShellLogDAO.insertSelective(fdMemberShellLog);

        consumer.setShell(consumer.getShell() - beikeCredit.longValue());
        FdMemberAsset updateConsumer = new FdMemberAsset();
        updateConsumer.setAssetId(consumer.getAssetId());
        updateConsumer.setShell(-beikeCredit.longValue());
        updateConsumer.setModifiedDate(calendar.getTime());
        fdMemberAssetDAO.updateByPrimaryKeySelective(updateConsumer);

        // 公司增加贝壳
        FdMemberAsset company = fdMemberAssetDAO.selectByMemberInfo("company", PayHandler.SHELL_ALI_ASSET_ID);
        FdMemberShellLog companyFdMemberShellLog = new FdMemberShellLog();
        companyFdMemberShellLog.setMemberTypeKey("company");
        companyFdMemberShellLog.setMemberId(company.getMemberId());
        companyFdMemberShellLog.setCategory("income");
        companyFdMemberShellLog.setPreBalance(company.getShell().intValue());
        companyFdMemberShellLog.setAmount(beikeCredit.intValue());
        companyFdMemberShellLog.setTransactionNo(fdTransactionsResult.getTransactionNo());
        companyFdMemberShellLog.setBalance(company.getShell().intValue() + beikeCredit.intValue());
        companyFdMemberShellLog.setTrackedDate(calendar.getTime());
        companyFdMemberShellLog.setRemark(String.format("{\"detail\":\"%s\",\"orderNo\":\"%s\",\"orderType\":\"DDLX_13\",\"itemNum\":\"%s\"}"
                , "贝壳街市订单", orderNo, StreetUtils.getValue("itemNum", streetOrderPayDTO.getOrder())));
        fdMemberShellLogDAO.insertSelective(companyFdMemberShellLog);

        company.setShell(company.getShell() + beikeCredit.longValue());
        FdMemberAsset updateCompany = new FdMemberAsset();
        updateCompany.setAssetId(company.getAssetId());
        updateCompany.setShell(beikeCredit.longValue());
        updateCompany.setModifiedDate(calendar.getTime());
        fdMemberAssetDAO.updateByPrimaryKeySelective(updateCompany);

        //门店增加待结算金额
        if (PayWay.ZFFS_07.getValue().equals(streetOrderPayDTO.getPayWay())) {
            String settledPrice = StreetUtils.getValue("settledPrice", streetOrderPayDTO.getOrder());
            FdMemberAsset stores = fdMemberAssetDAO.selectByMemberInfo("stores", storesId);
            FdMemberAsset updateStores = new FdMemberAsset();
            updateStores.setAssetId(stores.getAssetId());
            updateStores.setSettledBalance(new BigDecimal(settledPrice));
            updateStores.setModifiedDate(calendar.getTime());
            fdMemberAssetDAO.updateByPrimaryKeySelective(updateStores);
        }

        return fdTransactionsResult;
    }

    /**
     * 新增红包交易结果实体记录
     *
     * @param streetOrderPayDTO 街市订单支付传输对象
     * @param calendar          日历
     * @param tradeBody         交易体
     * @param vipFee            红包金额
     * @param consumer          消费者
     * @return FdTransactionsResult 交易结果实体
     */
    private FdTransactionsResult insertRedFdTransactionsResult(StreetOrderPayDTO streetOrderPayDTO, Calendar calendar,
                                                               String tradeBody, BigDecimal vipFee, FdMemberAsset consumer) {
        String orderNo = StreetUtils.getValue("orderNo", streetOrderPayDTO.getOrder());
        String consumerId = StreetUtils.getValue("consumerId", streetOrderPayDTO.getOrder());
        String contactPerson = StreetUtils.getValue("contactPerson", streetOrderPayDTO.getOrder());
        String contactTel = StreetUtils.getValue("contactTel", streetOrderPayDTO.getOrder());
        String storesId = StreetUtils.getValue("storesId", streetOrderPayDTO.getOrder());
        String storesName = StreetUtils.getValue("storesName", streetOrderPayDTO.getOrder());
        String settledPrice = StreetUtils.getValue("settledPrice", streetOrderPayDTO.getOrder());

        List<FdTransactionsResult> fdTransactionsResults = fdTransactionsResultDAO
                .selectByOutTradeNoAndPayWay(streetOrderPayDTO.getOrderNo(), streetOrderPayDTO.getPayWay(), "completed");
        if (null != fdTransactionsResults && !fdTransactionsResults.isEmpty()) {
            return fdTransactionsResults.get(0);
        }

        //新增红包交易结果
        FdTransactionsResult fdTransactionsResult = FdTransactionsResult.builder().transactionNo(TradeIDUtil.getTradeNo())
                .dataSource(streetOrderPayDTO.getDataSource()).tradeTypeKey("JYLX_01").orderTypeKey("DDLX_13")
                .paymentWayKey(streetOrderPayDTO.getPayWay()).transactionDate(calendar.getTime())
                .detail("贝壳街市订单交易").amount(vipFee).outTradeNo(orderNo).outTradeBody(tradeBody)
                .buyerMemberType("consumer").buyerId(consumerId).buyerName(contactPerson).buyerContact(contactTel)
                .sellerMemberType("stores").sellerId(storesId).sellerName(storesName)
                .transactionStatus("completed").createdDate(calendar.getTime()).modifiedDate(calendar.getTime())
                .remark("消费者端App|街市订单支付|红包支付|" + vipFee.toString()).build();
        fdTransactionsResultDAO.insertSelective(fdTransactionsResult);

        //消费者减去红包
        FdMemberRebateLog fdMemberRebateLog = new FdMemberRebateLog();
        fdMemberRebateLog.setMemberId(consumerId);
        fdMemberRebateLog.setMobile(contactTel);
        fdMemberRebateLog.setCategory("expenditure");
        fdMemberRebateLog.setPreBalance(consumer.getCashBalance());
        fdMemberRebateLog.setCashMoney(new BigDecimal("0").subtract(vipFee));
        fdMemberRebateLog.setBalance(consumer.getCashBalance().subtract(vipFee));
        fdMemberRebateLog.setCreateTime(calendar.getTime());
        fdMemberRebateLog.setRemark(String.format("{\"transactionNo\":\"%s\",\"detail\":\"贝壳街市订单\",\"orderNo\":\"%s\",\"orderType\":\"DDLX_13\",\"itemNum\":\"%s\"}"
                , fdTransactionsResult.getTransactionNo(), orderNo, StreetUtils.getValue("itemNum", streetOrderPayDTO.getOrder())));
        fdMemberRebateLogDAO.insertSelective(fdMemberRebateLog);

        consumer.setCashBalance(consumer.getCashBalance().subtract(vipFee));
        FdMemberAsset updateConsumer = new FdMemberAsset();
        updateConsumer.setAssetId(consumer.getAssetId());
        updateConsumer.setCashBalance(new BigDecimal("0").subtract(vipFee));
        updateConsumer.setModifiedDate(calendar.getTime());
        fdMemberAssetDAO.updateByPrimaryKeySelective(updateConsumer);

        //公司新增红包
        FdMemberAsset company = fdMemberAssetDAO.selectByMemberInfo("company", PayHandler.RED_MEMBER_ASSET_ID);
        FdMemberRebateLog companyFdMemberRebateLog = new FdMemberRebateLog();
        companyFdMemberRebateLog.setMemberId(company.getMemberId());
        companyFdMemberRebateLog.setMobile("company");
        companyFdMemberRebateLog.setCategory("income");
        companyFdMemberRebateLog.setPreBalance(company.getCashBalance());
        companyFdMemberRebateLog.setCashMoney(vipFee);
        companyFdMemberRebateLog.setBalance(company.getCashBalance().add(vipFee));
        companyFdMemberRebateLog.setCreateTime(calendar.getTime());
        companyFdMemberRebateLog.setRemark(String.format("{\"transactionNo\":\"%s\",\"detail\":\"贝壳街市订单\",\"orderNo\":\"%s\",\"orderType\":\"DDLX_13\",\"itemNum\":\"%s\"}"
                , fdTransactionsResult.getTransactionNo(), orderNo, StreetUtils.getValue("itemNum", streetOrderPayDTO.getOrder())));
        fdMemberRebateLogDAO.insertSelective(companyFdMemberRebateLog);

        company.setCashBalance(company.getCashBalance().add(vipFee));
        FdMemberAsset updateCompany = new FdMemberAsset();
        updateCompany.setAssetId(company.getAssetId());
        updateCompany.setCashBalance(vipFee);
        updateCompany.setModifiedDate(calendar.getTime());
        fdMemberAssetDAO.updateByPrimaryKeySelective(updateCompany);

        //门店增加待结算红包
        FdMemberAsset stores = fdMemberAssetDAO.selectByMemberInfo("stores", storesId);
        FdMemberAsset updateStores = new FdMemberAsset();
        updateStores.setAssetId(stores.getAssetId());
        updateStores.setSettledBalance(new BigDecimal(settledPrice));
        updateStores.setModifiedDate(calendar.getTime());
        fdMemberAssetDAO.updateByPrimaryKeySelective(updateStores);

        return fdTransactionsResult;
    }

    /**
     * 新增微信交易结果实体记录
     *
     * @param streetOrderPayDTO 街市订单支付传输对象
     * @param calendar          日历
     * @param tradeBody         交易体
     * @param vipFee            支付金额
     * @param consumer          消费者
     * @return FdTransactionsResult 交易结果实体x
     */
    private FdTransactionsResult insertWxFdTransactionsResult(StreetOrderPayDTO streetOrderPayDTO, Calendar calendar,
                                                              String tradeBody, BigDecimal vipFee, FdMemberAsset consumer) {
        String orderNo = StreetUtils.getValue("orderNo", streetOrderPayDTO.getOrder());
        String consumerId = StreetUtils.getValue("consumerId", streetOrderPayDTO.getOrder());
        String contactPerson = StreetUtils.getValue("contactPerson", streetOrderPayDTO.getOrder());
        String contactTel = StreetUtils.getValue("contactTel", streetOrderPayDTO.getOrder());
        String storesId = StreetUtils.getValue("storesId", streetOrderPayDTO.getOrder());
        String storesName = StreetUtils.getValue("storesName", streetOrderPayDTO.getOrder());

        List<FdTransactionsResult> fdTransactionsResults = fdTransactionsResultDAO
                .selectByOutTradeNoAndPayWay(streetOrderPayDTO.getOrderNo(), streetOrderPayDTO.getPayWay(), "completed");
        if (null != fdTransactionsResults && !fdTransactionsResults.isEmpty()) {
            return fdTransactionsResults.get(0);
        }

        //新增微信支付交易结果
        FdTransactionsResult fdTransactionsResult = FdTransactionsResult.builder().transactionNo(TradeIDUtil.getTradeNo())
                .dataSource(streetOrderPayDTO.getDataSource()).tradeTypeKey("JYLX_01").orderTypeKey("DDLX_13")
                .paymentWayKey(streetOrderPayDTO.getPayWay()).transactionDate(calendar.getTime())
                .detail("贝壳街市订单交易").amount(vipFee).outTradeNo(orderNo).outTradeBody(tradeBody)
                .buyerMemberType("consumer").buyerId(consumerId).buyerName(contactPerson).buyerContact(contactTel)
                .sellerMemberType("stores").sellerId(storesId).sellerName(storesName)
                .transactionStatus("underway").createdDate(calendar.getTime()).modifiedDate(calendar.getTime())
                .remark("消费者端App|街市订单支付|微信支付|" + vipFee.toString()).build();
        fdTransactionsResultDAO.insertSelective(fdTransactionsResult);
        return fdTransactionsResult;
    }

    /**
     * 新增支付宝交易结果实体记录
     *
     * @param streetOrderPayDTO 街市订单支付传输对象
     * @param calendar          日历
     * @param tradeBody         交易体
     * @param vipFee            交易金额
     * @param consumer          消费者
     * @return FdTransactionsResult 交易结果实体x
     */
    private FdTransactionsResult insertAliFdTransactionsResult(StreetOrderPayDTO streetOrderPayDTO, Calendar calendar,
                                                               String tradeBody, BigDecimal vipFee, FdMemberAsset consumer) {
        String orderNo = StreetUtils.getValue("orderNo", streetOrderPayDTO.getOrder());
        String consumerId = StreetUtils.getValue("consumerId", streetOrderPayDTO.getOrder());
        String contactPerson = StreetUtils.getValue("contactPerson", streetOrderPayDTO.getOrder());
        String contactTel = StreetUtils.getValue("contactTel", streetOrderPayDTO.getOrder());
        String storesId = StreetUtils.getValue("storesId", streetOrderPayDTO.getOrder());
        String storesName = StreetUtils.getValue("storesName", streetOrderPayDTO.getOrder());

        List<FdTransactionsResult> fdTransactionsResults = fdTransactionsResultDAO
                .selectByOutTradeNoAndPayWay(streetOrderPayDTO.getOrderNo(), streetOrderPayDTO.getPayWay(), "completed");
        if (null != fdTransactionsResults && !fdTransactionsResults.isEmpty()) {
            return fdTransactionsResults.get(0);
        }

        //新增贝壳交易结果
        FdTransactionsResult fdTransactionsResult = FdTransactionsResult.builder().transactionNo(TradeIDUtil.getTradeNo())
                .dataSource(streetOrderPayDTO.getDataSource()).tradeTypeKey("JYLX_01").orderTypeKey("DDLX_13")
                .paymentWayKey(streetOrderPayDTO.getPayWay()).transactionDate(calendar.getTime())
                .detail("贝壳街市订单交易").amount(vipFee).outTradeNo(orderNo).outTradeBody(tradeBody)
                .buyerMemberType("consumer").buyerId(consumerId).buyerName(contactPerson).buyerContact(contactTel)
                .sellerMemberType("stores").sellerId(storesId).sellerName(storesName)
                .transactionStatus("underway").createdDate(calendar.getTime()).modifiedDate(calendar.getTime())
                .remark("消费者端App|街市订单支付|支付宝支付|" + vipFee.toString()).build();
        fdTransactionsResultDAO.insertSelective(fdTransactionsResult);
        return fdTransactionsResult;
    }

    /**
     * 修改微信交易结果实体记录
     *
     * @param transactionNo 交易流水号
     * @return FdTransactionsResult 交易结果实体
     */
    private FdTransactionsResult updateWxFdTransactionsResult(String transactionNo, Map<String, String> params) throws BusinessException {
        FdTransactionsResult fdTransactionsResult = fdTransactionsResultDAO.selectByTransactionNo(transactionNo);
        if (null == fdTransactionsResult) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "交易记录不存在");
        }

        if (!PayWay.ZFFS_02.getValue().equals(fdTransactionsResult.getPaymentWayKey())) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "支付方式异常");
        }

        String totalFee = params.get("total_fee");
        if (new BigDecimal(totalFee).compareTo(fdTransactionsResult.getAmount().multiply(new BigDecimal("100"))) != 0) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "支付金额异常");
        }

        Calendar calendar = Calendar.getInstance();

        FdTransactionsResult updateTransactionResult = new FdTransactionsResult();
        updateTransactionResult.setTransactionId(fdTransactionsResult.getTransactionId());
        updateTransactionResult.setTransactionBody(FastJsonUtil.toJson(params));
        updateTransactionResult.setModifiedDate(calendar.getTime());
        updateTransactionResult.setTransactionStatus("completed");
        updateTransactionResult.setExternalNumber(params.get("transaction_id"));
        updateTransactionResult.setOldStatus("underway");
        int row = fdTransactionsResultDAO.updateByPrimaryKeySelective(updateTransactionResult);
        if (row <= 0) {
            return fdTransactionsResult;
        }

        fdTransactionsResult.setTransactionStatus("completed");
        fdTransactionsResult.setTransactionBody(updateTransactionResult.getTransactionBody());
        fdTransactionsResult.setModifiedDate(updateTransactionResult.getModifiedDate());

        Map<String, Object> order = Maps.newHashMap();

        try {
            order = FastJsonUtil.jsonToMap(fdTransactionsResult.getOutTradeBody());
        } catch (SystemException e) {
            logger.error("交易记录中订单体信息失败", e);
        }

        //消费者减去现金
        FdMemberCashLog fdMemberCashLog = new FdMemberCashLog();
        fdMemberCashLog.setMemberTypeKey("consumer");
        fdMemberCashLog.setMemberId(fdTransactionsResult.getBuyerId());
        fdMemberCashLog.setCategory("expenditure");
        fdMemberCashLog.setPreBalance(new BigDecimal("0"));
        fdMemberCashLog.setAmount(new BigDecimal("0").subtract(fdTransactionsResult.getAmount()));
        fdMemberCashLog.setTransactionNo(fdTransactionsResult.getTransactionNo());
        fdMemberCashLog.setBalance(new BigDecimal("0"));
        fdMemberCashLog.setTrackedDate(calendar.getTime());
        fdMemberCashLog.setRemark(String.format("{\"orderNo\":\"%s\",\"detail\":\"贝壳街市订单\",\"orderType\":\"DDLX_13\",\"itemNum\":\"%s\",\"settledPrice\":\"%s\"}"
                , fdTransactionsResult.getOutTradeNo(), StreetUtils.getValue("itemNum", order), StreetUtils.getValue("settledPrice", order)));

        fdMemberCashLogDAO.insertSelective(fdMemberCashLog);

        // 公司增加现金
        FdMemberCashLog companyFdMemberCashLog = new FdMemberCashLog();
        companyFdMemberCashLog.setMemberTypeKey("company");
        companyFdMemberCashLog.setMemberId(PayHandler.WX_MEMBER_ASSET_ID);
        companyFdMemberCashLog.setCategory("income");
        companyFdMemberCashLog.setPreBalance(new BigDecimal("0"));
        companyFdMemberCashLog.setAmount(fdTransactionsResult.getAmount());
        companyFdMemberCashLog.setTransactionNo(fdTransactionsResult.getTransactionNo());
        companyFdMemberCashLog.setBalance(new BigDecimal("0"));
        companyFdMemberCashLog.setTrackedDate(calendar.getTime());
        companyFdMemberCashLog.setRemark(String.format("{\"orderNo\":\"%s\",\"detail\":\"贝壳街市订单\",\"orderType\":\"DDLX_13\",\"itemNum\":\"%s\",\"settledPrice\":\"%s\"}"
                , fdTransactionsResult.getOutTradeNo(), StreetUtils.getValue("itemNum", order), StreetUtils.getValue("settledPrice", order)));
        fdMemberCashLogDAO.insertSelective(companyFdMemberCashLog);

        //门店增加待结算金额
        String settledPrice = StreetUtils.getValue("settledPrice", order);
        String storesId = StreetUtils.getValue("storesId", order);
        FdMemberAsset stores = fdMemberAssetDAO.selectByMemberInfo("stores", storesId);
        FdMemberAsset updateStores = new FdMemberAsset();
        updateStores.setAssetId(stores.getAssetId());
        updateStores.setSettledBalance(new BigDecimal(settledPrice));
        updateStores.setModifiedDate(calendar.getTime());
        fdMemberAssetDAO.updateByPrimaryKeySelective(updateStores);

        return fdTransactionsResult;
    }

    /**
     * 修改支付宝交易结果实体记录
     *
     * @param transactionNo 交易流水号
     * @return FdTransactionsResult 交易结果实体
     */
    private FdTransactionsResult updateAliFdTransactionsResult(String transactionNo, Map<String, String> params) throws BusinessException {
        FdTransactionsResult fdTransactionsResult = fdTransactionsResultDAO.selectByTransactionNo(transactionNo);
        if (null == fdTransactionsResult) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "交易记录不存在");
        }

        if (!PayWay.ZFFS_01.getValue().equals(fdTransactionsResult.getPaymentWayKey())) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "支付方式异常");
        }

        String totalAmount = params.get("total_amount");
        if (new BigDecimal(totalAmount).compareTo(fdTransactionsResult.getAmount()) != 0) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "支付金额异常");
        }

        Calendar calendar = Calendar.getInstance();

        FdTransactionsResult updateTransactionResult = new FdTransactionsResult();
        updateTransactionResult.setTransactionId(fdTransactionsResult.getTransactionId());
        updateTransactionResult.setTransactionBody(FastJsonUtil.toJson(params));
        updateTransactionResult.setModifiedDate(calendar.getTime());
        updateTransactionResult.setTransactionStatus("completed");
        updateTransactionResult.setExternalNumber(params.get("trade_no"));
        updateTransactionResult.setOldStatus("underway");
        int row = fdTransactionsResultDAO.updateByPrimaryKeySelective(updateTransactionResult);
        if (row <= 0) {
            return fdTransactionsResult;
        }


        fdTransactionsResult.setTransactionStatus(updateTransactionResult.getTransactionStatus());
        fdTransactionsResult.setTransactionBody(updateTransactionResult.getTransactionBody());
        fdTransactionsResult.setModifiedDate(updateTransactionResult.getModifiedDate());

        Map<String, Object> order = Maps.newHashMap();

        try {
            order = FastJsonUtil.jsonToMap(fdTransactionsResult.getOutTradeBody());
        } catch (SystemException e) {
            logger.error("交易记录中订单体信息失败", e);
        }

        //消费者减去现金
        FdMemberCashLog fdMemberCashLog = new FdMemberCashLog();
        fdMemberCashLog.setMemberTypeKey("consumer");
        fdMemberCashLog.setMemberId(fdTransactionsResult.getBuyerId());
        fdMemberCashLog.setCategory("expenditure");
        fdMemberCashLog.setPreBalance(new BigDecimal("0"));
        fdMemberCashLog.setAmount(new BigDecimal("0").subtract(fdTransactionsResult.getAmount()));
        fdMemberCashLog.setTransactionNo(fdTransactionsResult.getTransactionNo());
        fdMemberCashLog.setBalance(new BigDecimal("0"));
        fdMemberCashLog.setTrackedDate(calendar.getTime());
        fdMemberCashLog.setRemark(String.format("{\"orderNo\":\"%s\",\"detail\":\"贝壳街市订单\",\"orderType\":\"DDLX_13\",\"itemNum\":\"%s\",\"settledPrice\":\"%s\"}"
                , fdTransactionsResult.getOutTradeNo(), StreetUtils.getValue("itemNum", order), StreetUtils.getValue("settledPrice", order)));

        fdMemberCashLogDAO.insertSelective(fdMemberCashLog);

        // 公司增加现金
        FdMemberCashLog companyFdMemberCashLog = new FdMemberCashLog();
        companyFdMemberCashLog.setMemberTypeKey("company");
        companyFdMemberCashLog.setMemberId(PayHandler.ALI_MEMBER_ASSET_ID);
        companyFdMemberCashLog.setCategory("income");
        companyFdMemberCashLog.setPreBalance(new BigDecimal("0"));
        companyFdMemberCashLog.setAmount(fdTransactionsResult.getAmount());
        companyFdMemberCashLog.setTransactionNo(fdTransactionsResult.getTransactionNo());
        companyFdMemberCashLog.setBalance(new BigDecimal("0"));
        companyFdMemberCashLog.setTrackedDate(calendar.getTime());
        companyFdMemberCashLog.setRemark(String.format("{\"orderNo\":\"%s\",\"detail\":\"贝壳街市订单\",\"orderType\":\"DDLX_13\",\"itemNum\":\"%s\",\"settledPrice\":\"%s\"}"
                , fdTransactionsResult.getOutTradeNo(), StreetUtils.getValue("itemNum", order), StreetUtils.getValue("settledPrice", order)));
        fdMemberCashLogDAO.insertSelective(companyFdMemberCashLog);

        //门店增加待结算金额
        String settledPrice = StreetUtils.getValue("settledPrice", order);
        String storesId = StreetUtils.getValue("storesId", order);
        FdMemberAsset stores = fdMemberAssetDAO.selectByMemberInfo("stores", storesId);
        FdMemberAsset updateStores = new FdMemberAsset();
        updateStores.setAssetId(stores.getAssetId());
        updateStores.setSettledBalance(new BigDecimal(settledPrice));
        updateStores.setModifiedDate(calendar.getTime());
        fdMemberAssetDAO.updateByPrimaryKeySelective(updateStores);

        return fdTransactionsResult;
    }

    /**
     * 获取交易body
     *
     * @param order 订单
     * @return String
     */
    private String getTradeBody(Map<String, Object> order) throws BusinessException {
        Object items = order.get("items");
        if (null == items) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "订单明细异常，缺少数据，不能发起支付请求，请联系管理员");
        }

        //组装body
        Map<String, Object> body = Maps.newHashMap();
        body.put("orderId", order.get("orderId"));
        body.put("memberId", order.get("consumerId"));
        body.put("memberTypeKey", "consumer");
        body.put("itemNum", order.get("itemNum"));
        body.put("storesId", order.get("storesId"));
        body.put("settledPrice", order.get("settledPrice"));

        List<Map<String, Object>> bodyItems = Lists.newLinkedList();
        List<Map<String, Object>> orderItems = (List<Map<String, Object>>) items;
        if (!orderItems.isEmpty()) {
            for (Map<String, Object> orderItem : orderItems) {
                Map<String, Object> map = Maps.newHashMap();
                map.put("orderItemId", orderItem.get("orderItemId"));
                map.put("goodsId", orderItem.get("itemStoreId"));
                map.put("goodsName", orderItem.get("itemName"));
                map.put("qty", orderItem.get("qty"));
                bodyItems.add(map);
            }
            body.put("items", bodyItems);
        }

        return FastJsonUtil.toJson(body);
    }

}
