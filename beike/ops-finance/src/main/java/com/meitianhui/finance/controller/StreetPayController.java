package com.meitianhui.finance.controller;

import com.meitianhui.base.controller.BaseController;
import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.finance.constant.RspCode;
import com.meitianhui.finance.street.consts.ServiceName;
import com.meitianhui.finance.street.handler.ServiceHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <pre> 街市订单支付控制器 </pre>
 *
 * @author tortoise
 * @since 2019/3/27 12:40
 */
@Controller
@RequestMapping("/streetPay")
public class StreetPayController extends BaseController {

    /**
     * 业务处理类
     */
    private final Map<ServiceName, ServiceHandler> serviceHandlers = new ConcurrentHashMap<>();

    @Autowired
    public StreetPayController(List<ServiceHandler> serviceHandlers) {
        if (null != serviceHandlers && !serviceHandlers.isEmpty()) {
            for (ServiceHandler serviceHandler : serviceHandlers) {
                this.serviceHandlers.put(serviceHandler.getServiceName(), serviceHandler);
            }
        }
    }

    /**
     * 请求处理
     *
     * @param request   HttpServletRequest对象
     * @param response  HttpServletResponse对象
     * @param paramsMap 请求参数
     * @param result    返回结果
     * @throws Exception 异常
     */
    @Override
    public void operate(HttpServletRequest request, HttpServletResponse response, Map<String, Object> paramsMap,
                        ResultData result)
            throws Exception {
        String serviceName = request.getParameter("service");

        //获取对应业务的处理器
        ServiceHandler serviceHandler = serviceHandlers.get(ServiceName.parse(serviceName));
        if (null == serviceHandler) {
            throw new BusinessException(RspCode.SYSTEM_SERVICE_ERROR, "非法操作");
        }

        //处理业务
        serviceHandler.handle(paramsMap, result);
    }

}
