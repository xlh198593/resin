package com.meitianhui.finance.street.dao;

import com.meitianhui.finance.street.entity.FdTransactionsResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <pre> 交易结果记录数据库操作 </pre>
 *
 * @author tortoise
 * @since 2019/3/31 10:13
 */
public interface FdTransactionsResultDAO {

    /**
     * 新增，只插入不为空的字段
     *
     * @param record 交易结果记录
     * @return 影响的行数
     */
    int insertSelective(FdTransactionsResult record);

    /**
     * 根据交易编号查询
     *
     * @param transactionNo 交易编号
     * @return 资产记录
     */
    FdTransactionsResult selectByTransactionNo(String transactionNo);

    /**
     * 根据参数查询交易结果
     *
     * @param params 参数
     * @return 资产记录
     */
    List<FdTransactionsResult> selectByParams(Map<String, Object> params);

    /**
     * 根据订单编号、支付方式、交易状态查询
     *
     * @param outTradeNo        订单编号
     * @param payWay            支付方式
     * @param transactionStatus 交易状态
     * @return 资产记录
     */
    List<FdTransactionsResult> selectByOutTradeNoAndPayWay(@Param("outTradeNo") String outTradeNo,
                                                           @Param("payWay") String payWay, @Param("transactionStatus")
                                                                   String transactionStatus);

    /**
     * 根据主键修改
     *
     * @param record 交易结果记录
     * @return 影响的行数
     */
    int updateByPrimaryKeySelective(FdTransactionsResult record);

}