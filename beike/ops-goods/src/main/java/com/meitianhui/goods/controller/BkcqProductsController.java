package com.meitianhui.goods.controller;

import com.meitianhui.base.controller.BaseController;
import com.meitianhui.common.constant.CommonRspCode;
import com.meitianhui.common.constant.PageParam;
import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.common.util.StringUtil;
import com.meitianhui.goods.street.consts.ServiceName;
import com.meitianhui.goods.street.handler.ServiceHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <pre> 街市商品控制器 </pre>
 *
 * @author tortoise
 * @since 2019/3/27 20:40
 */
@Controller
@RequestMapping("/bkcqProducts")
public class
BkcqProductsController extends BaseController {

    /**
     * 业务处理类
     */
    private final Map<ServiceName, ServiceHandler> serviceHandlers = new ConcurrentHashMap<>();

    @Autowired
    public BkcqProductsController(List<ServiceHandler> serviceHandlers) {
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
            throw new BusinessException(CommonRspCode.RESPONSE_FAIL, "非法操作");
        }

        PageParam pageParam = new PageParam();
        String page = request.getParameter("page");
        if (page == null) {
            paramsMap.put("pageParam",null);
        } else {
            Map<String, Object> pageMap = FastJsonUtil.jsonToMap(page);
            String page_no = StringUtil.formatStr(pageMap.get("page_no"));
            if (!"".equals(page_no)) {
                pageParam.setPage_no(Integer.parseInt(page_no));
            } else {
                pageParam.setPage_no(1);
            }

            String page_size = StringUtil.formatStr(pageMap.get("page_size"));
            if (!"".equals(page_size)) {
                pageParam.setPage_size(Integer.parseInt(page_size));
            }else {
                pageParam.setPage_size(10);
            }

            paramsMap.put("pageParam",pageParam);
        }

        //处理业务
        serviceHandler.handle(paramsMap, result);
    }

}
