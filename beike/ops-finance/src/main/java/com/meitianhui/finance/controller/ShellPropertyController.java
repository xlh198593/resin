package com.meitianhui.finance.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.meitianhui.base.controller.BaseController;
import com.meitianhui.common.constant.PageParam;
import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;
import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.finance.constant.RspCode;
import com.meitianhui.finance.service.ShellConsumerService;
@Controller
@RequestMapping("/ShellProperty")
public class ShellPropertyController extends BaseController {
	private static final Logger logger = Logger.getLogger(ShellPropertyController.class);

	@Autowired
	private ShellConsumerService shellConsumerService;
	
	@Override
	public void operate(HttpServletRequest request, HttpServletResponse response, Map<String, Object> paramsMap,
			ResultData result) throws BusinessException, SystemException, Exception {
		String operateName = request.getParameter("service");
		String type = operateName.split("\\.")[0];
		if (type.equals("consumer")) {
			consumerServer(request, response, paramsMap, result);
			logger.info("返回参数===============>>>>>"+FastJsonUtil.toJson(result));
		} else if (type.equals("voucher")) {
			//voucherServer(request, response, paramsMap, result);
		} else if (type.equals("mobileRecharge")) {
			//mobileRechargeServer(request, response, paramsMap, result);
		} else {
			//financeServer(request, response, paramsMap, result);
		}

	}

	private void consumerServer(HttpServletRequest request, HttpServletResponse response, Map<String, Object> paramsMap,
			ResultData result) throws Exception {
		String operateName = request.getParameter("service");

		if ("consumer.consumerFdMemberAsset".equals(operateName)) {
			shellConsumerService.consumerFdMemberAssetFind(paramsMap, result);
		} else if ("consumer.consumerFdMemberAssetShellLogFind".equals(operateName)) {
			consumerFdMemberAssetShellLogListFind(request,paramsMap, result);
		} else if ("consumer.consumerFdMemberAssetPointLogFind".equals(operateName)) {
			consumerFdMemberAssetPointLogFind(request,paramsMap, result);
		} else if ("consumer.consumerFdMemberAssetCashLogFind".equals(operateName)) {
			consumerFdMemberAssetCashLogFind(request,paramsMap, result);
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
		} else if ("consumer.memberRabatePageFind".equals(operateName)) {
			//app红包明细
			memberRabatePageFind(request, paramsMap, result);
		} else if ("consumer.memberManagerRabatePageFind".equals(operateName)) {
			//掌柜红包明细
			memberManagerRabatePageFind(request, paramsMap, result);
//			Map<String, Object> newMap = new HashMap<>();
//			newMap.put("info", "该功能已经下线");
//			result.setResultData(newMap);

		} else if ("consumer.memberManagerRabateCount".equals(operateName)) {
			//掌柜次级返佣总数
			shellConsumerService.memberManagerRabateCount(paramsMap, result);
//			Map<String, Object> newMap = new HashMap<>();
//			newMap.put("info", "该功能已经下线");
//			result.setResultData(newMap);

		}else if ("consumer.registerUserReceiveShell".equals(operateName)) {
			//新用户领取68贝壳
			shellConsumerService.registerUserReceiveShell(paramsMap, result);
		} else if ("consumer.getBulletBoxStatus".equals(operateName)) {
			//获取用户弹框状态
			shellConsumerService.getBulletBoxStatus(paramsMap, result);
		}  else {
			throw new BusinessException(RspCode.SYSTEM_SERVICE_ERROR,
					RspCode.MSG.get(RspCode.SYSTEM_SERVICE_ERROR));
		}
		
	}

	private void consumerFdMemberAssetPointLogFind(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result) 
			throws BusinessException, SystemException, Exception {
		try {
			String page = request.getParameter("page");
			// 引入分页查询
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
			}
			shellConsumerService.consumerFdMemberAssetPointLogListFind(paramsMap, result);
			Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
			resultData.put("page", pageParam);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	
	
		
	}

	private void consumerFdMemberAssetShellLogListFind(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			// 引入分页查询
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
				
			}
			shellConsumerService.consumerFdMemberAssetShellLogListFind(paramsMap, result);
			Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
			resultData.put("page", pageParam);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	
	}
	
	private void consumerFdMemberAssetCashLogFind(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			// 引入分页查询
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
				
			}
			shellConsumerService.consumerFdMemberAssetCashLogFind(paramsMap, result);
			Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
			resultData.put("page", pageParam);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		
	}

	
	private void memberRabatePageFind(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			// 引入分页查询
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
				
			}
			shellConsumerService.memberRabateListFind(paramsMap, result);
			Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
			resultData.put("page", pageParam);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}
	
	private void memberManagerRabatePageFind(HttpServletRequest request, Map<String, Object> paramsMap, ResultData result)
			throws BusinessException, SystemException, Exception {
		try {
			// 引入分页查询
			PageParam pageParam = getPageParam(request);
			if (null != pageParam) {
				paramsMap.put("pageParam", pageParam);
				
			}
			shellConsumerService.memberManagerRabateListFind(paramsMap, result);
			Map<String, Object> resultData = (Map<String, Object>) result.getResultData();
			resultData.put("page", pageParam);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}
	
}
