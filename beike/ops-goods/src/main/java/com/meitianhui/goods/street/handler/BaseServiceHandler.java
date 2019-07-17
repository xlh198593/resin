package com.meitianhui.goods.street.handler;

import cn.hutool.core.bean.BeanUtil;
import com.google.common.collect.Maps;
import com.meitianhui.common.constant.CommonRspCode;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.util.FastJsonUtil;
import org.hibernate.validator.HibernateValidator;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 处理不同的业务请求接抽象类
 *
 * @author tortoise
 * @since 2019/3/27 16:40
 */
@SuppressWarnings("Duplicates")
public abstract class BaseServiceHandler implements ServiceHandler {

    /**
     * 初始化验证器
     */
    protected static Validator validator = Validation.byProvider(HibernateValidator.class).configure().failFast(true)
            .buildValidatorFactory().getValidator();

    /**
     * 验证传输对象，成功返回传输对象，失败抛出业务异常
     *
     * @param paramsMap 请求参数
     * @param cls       传输对象的Class
     * @param <T>       泛型，请求传输对象
     * @return T 请求传输对象
     * @throws BusinessException 业务异常，参数验证失败
     */
    protected <T> T validate(Map<String, Object> paramsMap, Class<T> cls) throws BusinessException {
        T t = getRequestDto(paramsMap, cls);

        Set<ConstraintViolation<T>> constraintViolations = validator.validate(t);
        Map<String, String> fieldErrors = Maps.newHashMap();

        for (ConstraintViolation<?> constraintViolation : constraintViolations) {
            Path propertyPath = constraintViolation.getPropertyPath();
            Iterator<Path.Node> iterator = propertyPath.iterator();
            Path.Node node = null;
            while (iterator.hasNext()) {
                node = iterator.next();
            }

            //有错误添加进Map
            if (node != null) {
                fieldErrors.put(node.getName(), constraintViolation.getMessage());
            }
        }

        if (!fieldErrors.isEmpty()) {
            throw new BusinessException(CommonRspCode.SYSTEM_BEAN_TO_MAP_ERROR, FastJsonUtil.toJson(fieldErrors));
        }

        return t;
    }

    /**
     * 获取请求传输对象
     *
     * @param paramsMap 请求参数
     * @param cls       请求传输对象的Class
     * @param <T>       泛型，请求传输对象
     * @return T 请求传输对象
     */
    protected <T> T getRequestDto(Map<String, Object> paramsMap, Class<T> cls) {
        return BeanUtil.mapToBeanIgnoreCase(paramsMap, cls, true);
    }

}
