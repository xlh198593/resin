package com.meitianhui.goods.street.handler.impl;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;
import com.meitianhui.common.util.DocUtil;
import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.goods.constant.RspCode;
import com.meitianhui.goods.street.consts.ServiceName;
import com.meitianhui.goods.street.dao.BkcqProductsDAO;
import com.meitianhui.goods.street.entity.BkcqProducts;
import com.meitianhui.goods.street.handler.ServiceHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 查询街市商品业务请求接口
 *
 * @author tortoise
 * @since 2019/3/27 20:40
 */
@Component
public class FindProductByKeyServiceHandler implements ServiceHandler {

    @Autowired
    private BkcqProductsDAO bkcqProductsDAO;

    @Autowired
    private DocUtil docUtil;

    @Override
    public ServiceName getServiceName() {
        return ServiceName.FIND_PRODUCT_BY_KEY;
    }

    @SuppressWarnings({"Duplicates"})
    @Override
    public void handle(Map<String, Object> paramsMap, ResultData result) throws BusinessException {
        Object goodsIdObj = paramsMap.get("goodsId");
        Object goodsCodeObj = paramsMap.get("goodsCode");
        if (null == goodsIdObj && null == goodsCodeObj) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "商品编号或者商品编码不能同时为空");
        }

        //获取商品编号
        BkcqProducts bkcqProducts;
        if (null != goodsIdObj) {
            Long goodsId = Long.valueOf(goodsIdObj.toString());

            bkcqProducts = bkcqProductsDAO.selectByPrimaryKey(goodsId);
            if (null == bkcqProducts) {
                throw new BusinessException(RspCode.RESPONSE_FAIL, "商品编号不存在");
            }
        } else {
            String goodsCode = goodsCodeObj.toString();

            bkcqProducts = bkcqProductsDAO.selectByGoodsCode(goodsCode);
            if (null == bkcqProducts) {
                throw new BusinessException(RspCode.RESPONSE_FAIL, "商品编码不存在");
            }
        }

        try {
            assemblePicUrl(bkcqProducts);
        } catch (SystemException e) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "商品图片格式错误");
        }

        result.setResultData(bkcqProducts);
    }

    /**
     * 转换商品图片
     *
     * @param bkcqProducts BkcqProducts 商品实体
     * @throws SystemException 系统异常
     */
    @SuppressWarnings({"unchecked", "Duplicates"})
    private void assemblePicUrl(BkcqProducts bkcqProducts) throws SystemException {
        //转换商品图片地址
        List<String> pathIds = Lists.newArrayList();
        String picInfo = bkcqProducts.getPicInfo();
        List<Map<String, Object>> picList = Lists.newArrayList();
        if (StrUtil.isNotBlank(picInfo)) {
            picList = FastJsonUtil.jsonToList(picInfo);
            for (Map<String, Object> stringObjectMap : picList) {
                if (null != stringObjectMap.get("path_id")) {
                    pathIds.add(stringObjectMap.get("path_id").toString());
                }
            }
        }

        //转换商品详情图片地址
        String picDetailInfo = bkcqProducts.getPicDetailInfo();
        List<Map<String, Object>> detailPicList = Lists.newArrayList();
        if (StrUtil.isNotBlank(picDetailInfo)) {
            detailPicList = FastJsonUtil.jsonToList(picDetailInfo);
            for (Map<String, Object> stringObjectMap : detailPicList) {
                if (null != stringObjectMap.get("path_id")) {
                    pathIds.add(stringObjectMap.get("path_id").toString());
                }
            }
        }

        //开始转换
        Map<String, Object> pathMap = docUtil.imageUrlFind(pathIds);

        //重写商品图片地址
        if (!picList.isEmpty()) {
            for (Map<String, Object> stringObjectMap : picList) {
                if (null != stringObjectMap.get("path_id")) {
                    stringObjectMap.put("path_id", pathMap.get(stringObjectMap.get("path_id").toString()));
                }
            }
            bkcqProducts.setPicInfo(FastJsonUtil.toJson(picList));
        }

        //重写商品详情图片地址
        if (!detailPicList.isEmpty()) {
            for (Map<String, Object> stringObjectMap : detailPicList) {
                if (null != stringObjectMap.get("path_id")) {
                    stringObjectMap.put("path_id", pathMap.get(stringObjectMap.get("path_id").toString()));
                }
            }
            bkcqProducts.setPicDetailInfo(FastJsonUtil.toJson(detailPicList));
        }
    }

}
