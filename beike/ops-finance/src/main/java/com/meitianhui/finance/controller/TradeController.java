package com.meitianhui.finance.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.meitianhui.base.controller.BaseController;
import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;
import com.meitianhui.finance.constant.RspCode;
import com.meitianhui.finance.service.OrderPayService;
import com.meitianhui.finance.service.OrderPaymentService;
import com.meitianhui.finance.service.TradeService;

@Controller
@RequestMapping("/finance/pay")
public class TradeController extends BaseController{
	
	@Autowired
	private TradeService tradeService;
	
	@Autowired
	private OrderPayService orderPayService;
	
	@Autowired
	private OrderPaymentService orderPaymentService;

	@Override
	public void operate(HttpServletRequest request, HttpServletResponse response, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		try {
			String operateName = request.getParameter("service");
			String type = operateName.split("\\.")[0];
			if ("orderPay".equals(operateName)) {
				paramsMap.put("out_member_id", paramsMap.get("buyer_id"));
				paramsMap.put("in_member_id", paramsMap.get("seller_id"));
				paramsMap.remove("buyer_id");
				paramsMap.remove("seller_id");
				orderServer(request, response, paramsMap, result);
			} else if (operateName.equals("voucher")) {
				//voucherServer(request, response, paramsMap, result);
			} else if (operateName.equals("mobileRecharge")) {
				//mobileRechargeServer(request, response, paramsMap, result);
			} else {
				//financeServer(request, response, paramsMap, result);
			}
		} /*catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} */catch (Exception e) {
			throw e;
		}
		
	}

	private void orderServer(HttpServletRequest request, HttpServletResponse response, Map<String, Object> paramsMap,
			ResultData result) {

		try {
			String operateName = request.getParameter("service");
			if ("orderPay.recharge".equals(operateName)) {
				orderPaymentService.balanceRecharge(paramsMap, result);
			} else if ("trade.tradeCodeVerify".equals(operateName)) {
				//tradeService.tradeCodeVerify(paramsMap, result);
			} else if ("trade.barCodeCreate".equals(operateName)) {
				//tradeService.barCodeCreate(paramsMap, result);
			} else if ("trade.barCodePay".equals(operateName)) {
				//tradeService.barCodePay(paramsMap, result);
			} else if ("trade.transactionStatusFind".equals(operateName)) {
				//tradeService.transactionStatusFind(paramsMap, result);
			} else if ("trade.transactionStatusConfirmed".equals(operateName)) {
				//tradeService.transactionStatusConfirmed(paramsMap, result);
			} else if ("trade.salesassistant.barCodePay".equals(operateName)) {
				//tradeService.barCodePayForSalesassistant(paramsMap, result);
			} else if ("trade.salesassistant.posPay".equals(operateName)) {
				//pos机支付入口
				//tradeService.posPayForSalesassistant(paramsMap, result);
			} else {
				throw new BusinessException(RspCode.SYSTEM_SERVICE_ERROR,
						RspCode.MSG.get(RspCode.SYSTEM_SERVICE_ERROR));
			}
		} catch (BusinessException e) {
			//throw e;
		} catch (SystemException e) {
			//throw e;
		} catch (Exception e) {
			//throw e;
		}
	
	}

}
