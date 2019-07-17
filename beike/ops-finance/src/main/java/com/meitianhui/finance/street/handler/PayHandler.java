package com.meitianhui.finance.street.handler;

import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.finance.street.consts.PayWay;
import com.meitianhui.finance.street.dto.StreetOrderPayDTO;
import com.meitianhui.finance.street.entity.FdTransactionsResult;

import java.util.Map;

/**
 * 处理不同支付的请求接口
 *
 * @author tortoise
 * @since 2019/3/27 16:40
 */
public interface PayHandler {

    /**
     * 公司红包账号
     */
    String RED_MEMBER_ASSET_ID = "10000001";

    /**
     * 公司微信账号
     */
    String WX_MEMBER_ASSET_ID = "10000002";

    /**
     * 公司支付宝账号
     */
    String ALI_MEMBER_ASSET_ID = "10000003";

    /**
     * 公司贝壳账号
     */
    String SHELL_ALI_ASSET_ID = "10000004";

    /**
     * 获取支付方式
     *
     * @return 支付方式枚举类
     */
    PayWay getPayWay();

    /**
     * 处理支付
     *
     * @param streetOrderPayDTO    支付请求传输对象
     * @param fdTransactionsResult 交易结果
     * @return Map<String, String> 下单结果
     * @throws BusinessException 业务异常
     */
    Map<String, String> handle(StreetOrderPayDTO streetOrderPayDTO,
                               FdTransactionsResult fdTransactionsResult) throws BusinessException;

}
