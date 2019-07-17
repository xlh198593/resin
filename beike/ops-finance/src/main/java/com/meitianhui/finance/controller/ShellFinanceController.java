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
import com.meitianhui.finance.service.ShellOrderPayService;
/**
 * 贝壳传奇交易控制层 
 * @author 丁龙
 *
 */
@Controller
@RequestMapping("/ShellFinance")
public class ShellFinanceController extends BaseController {
	
	@Autowired
	private ShellOrderPayService shellOrderPayService;

	@Override
	public void operate(HttpServletRequest request, HttpServletResponse response, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
			String operateName = request.getParameter("service");
			String type = operateName.split("\\.")[0];
			if (type.equals("orderPay")) {
				orderPayServer(request, response, paramsMap, result);
			} else if (type.equals("voucher")) {
				//voucherServer(request, response, paramsMap, result);
			} else if (type.equals("mobileRecharge")) {
				//mobileRechargeServer(request, response, paramsMap, result);
			} else {
				//financeServer(request, response, paramsMap, result);
			}
	}

	private void orderPayServer(HttpServletRequest request, HttpServletResponse response, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception{
			String operateName = request.getParameter("service");

			if ("orderPay.shellRecharge".equals(operateName)) {
				shellOrderPayService.shellRecharge(paramsMap, result);
			} else if ("orderPay.orderRefund".equals(operateName)) {
				// 买方双方互换位置
				String buyer_id = paramsMap.get("buyer_id") + "";
				String seller_id = paramsMap.get("seller_id") + "";
				paramsMap.put("out_member_id", seller_id);
				paramsMap.put("in_member_id", buyer_id);
				paramsMap.remove("buyer_id");
				paramsMap.remove("seller_id"); 
				shellOrderPayService.shellOrderRefund(paramsMap, result);
			} else if ("orderPay.beikeMallOrderPay".equals(operateName)) {//贝壳商城  订单支付
				shellOrderPayService.beikeMallOrderPay(paramsMap, result);
			} else if ("orderPay.hongBaoOrderPay".equals(operateName)) {
				shellOrderPayService.hongBaoOrderPay(paramsMap, result);//红包兑  订单支付
			} else if ("orderPay.beikeStreetOrderPay".equals(operateName)) {	
				shellOrderPayService.beikeStreetOrderPay(paramsMap, result);//贝壳街市  订单支付
			} else if ("orderPay.handleBeikeMallOrderPay".equals(operateName)) {	
				shellOrderPayService.handleBeikeMallOrderPay(paramsMap, result);//新的支付接口
			} else if ("orderPay.telephoneRecharge".equals(operateName)) {	
				//话费充值接口
				shellOrderPayService.telephoneRecharge(paramsMap, result);
			} else {
				throw new BusinessException(RspCode.SYSTEM_SERVICE_ERROR,
						RspCode.MSG.get(RspCode.SYSTEM_SERVICE_ERROR));
			}
	}

}
