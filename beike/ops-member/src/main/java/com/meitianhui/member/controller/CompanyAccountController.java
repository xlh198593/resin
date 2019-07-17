 package com.meitianhui.member.controller;

 import com.meitianhui.base.controller.BaseController;
 import com.meitianhui.common.constant.ResultData;
 import com.meitianhui.common.exception.BusinessException;
 import com.meitianhui.common.exception.SystemException;
 import com.meitianhui.member.constant.RspCode;
 import com.meitianhui.member.service.CompanyAccountService;
 import com.meitianhui.member.service.StoresService;
 import org.apache.log4j.Logger;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Controller;
 import org.springframework.web.bind.annotation.RequestMapping;

 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;
 import java.util.Map;

 @Controller
 @RequestMapping("/CompanyAccount")
 public class CompanyAccountController extends BaseController {

     private static final Logger logger = Logger.getLogger(CompanyAccountController.class);

     @Autowired
     private CompanyAccountService companyAccountService;

     @Override
     public void operate(HttpServletRequest request, HttpServletResponse response, Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception {
         try {
             String operateName = request.getParameter("service");
             if ("Company.Account.Login".equals(operateName)) {
                 companyAccountService.login(paramsMap, result);
             } else if ("Company.Account.SMS".equals(operateName)) {
                 companyAccountService.sendSMS(paramsMap, result);
             } else if ("Company.Account.Find.MemberInfo".equals(operateName)) {
                 companyAccountService.findMemberInfo(paramsMap, result);
             } else if ("Stores.Find.StoresInfo".equals(operateName)) {
                 companyAccountService.findStoresInfo(paramsMap, result);
             } else if ("Company.Account.Invitation".equals(operateName)) {
                 companyAccountService.invitation(paramsMap, result);
             } else {
                 throw new BusinessException(RspCode.SYSTEM_SERVICE_ERROR, RspCode.MSG.get(RspCode.SYSTEM_SERVICE_ERROR));
             }
         } catch (BusinessException e) {
             throw e;
         } catch (Exception e) {
             throw e;
         }
     }



 }
