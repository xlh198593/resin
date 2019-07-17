 package com.meitianhui.member.controller;

 import com.meitianhui.base.controller.BaseController;
 import com.meitianhui.common.constant.PageParam;
 import com.meitianhui.common.constant.ResultData;
 import com.meitianhui.common.exception.BusinessException;
 import com.meitianhui.common.exception.SystemException;
 import com.meitianhui.common.util.ImageUtil;
 import com.meitianhui.common.util.StringUtil;
 import com.meitianhui.common.util.ValidateUtil;
 import com.meitianhui.member.constant.Constant;
 import com.meitianhui.member.constant.RspCode;
 import com.meitianhui.member.service.*;
 import org.apache.log4j.Logger;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Controller;
 import org.springframework.web.bind.annotation.RequestMapping;

 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.concurrent.ExecutorService;
 import java.util.concurrent.Executors;

 @Controller
 @RequestMapping("/member/stores")
 public class StoresController extends BaseController {

     private static final Logger logger = Logger.getLogger(StoresController.class);

     @Autowired
     private StoresService storesService;

     @Override
     public void operate(HttpServletRequest request, HttpServletResponse response, Map<String, Object> paramsMap, ResultData result) throws BusinessException, SystemException, Exception {
         try {
             String operateName = request.getParameter("service");
             if ("member.stores.home.info".equals(operateName)) {
                 //查询商家店铺首页
                 storesService.getStroesHomeInfo(paramsMap, result);
             } else if ("member.stores.supplier.info".equals(operateName)) {
                 //商家信息
                 storesService.getStroesInfo(paramsMap, result);
             } else if ("member.stores.by.areaName".equals(operateName)) {
                 storesService.getStroesByAreaName(paramsMap, result);
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
