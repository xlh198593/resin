package com.meitianhui.order.street.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.meitianhui.common.constant.CommonRspCode;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;
import com.meitianhui.common.util.FastJsonUtil;
import com.meitianhui.order.constant.RspCode;
import com.meitianhui.order.street.consts.OrderStatus;
import com.meitianhui.order.street.dao.BkcqLoadCodeDAO;
import com.meitianhui.order.street.dao.BkcqLocalOrderDAO;
import com.meitianhui.order.street.dao.BkcqLocalOrderItemDAO;
import com.meitianhui.order.street.dto.BkcqLocalOrderCreateDTO;
import com.meitianhui.order.street.dto.BkcqLocalOrderQueryDTO;
import com.meitianhui.order.street.dto.OrderPayResuktDTO;
import com.meitianhui.order.street.encryption.Aes;
import com.meitianhui.order.street.encryption.Encryption;
import com.meitianhui.order.street.entity.BkcqLoadCode;
import com.meitianhui.order.street.entity.BkcqLocalOrder;
import com.meitianhui.order.street.entity.BkcqLocalOrderItem;
import com.meitianhui.order.street.service.BkcqLocalOrderService;
import com.meitianhui.order.street.utils.StreetUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * <pre> 街市订单业务操作 </pre>
 *
 * @author tortoise
 * @since 2019/3/27 17:05
 */
@SuppressWarnings({"unchecked", "Duplicates"})
@Service
public class BkcqLocalOrderServiceImpl implements BkcqLocalOrderService {

    private static final Logger logger = Logger.getLogger(BkcqLocalOrderServiceImpl.class);

    @Autowired
    private BkcqLocalOrderDAO bkcqLocalOrderDAO;

    @Autowired
    private BkcqLocalOrderItemDAO bkcqLocalOrderItemDAO;

    @Autowired
    private BkcqLoadCodeDAO bkcqLoadCodeDAO;

    /**
     * 创建街市订单
     *
     * @param bkcqLocalOrderCreateDTO 订单传入参数
     * @return BkcqLocalOrder 街市订单
     * @throws BusinessException 业务异常
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BkcqLocalOrder createOrder(BkcqLocalOrderCreateDTO bkcqLocalOrderCreateDTO) throws BusinessException {
        Calendar calendar = Calendar.getInstance();
        Map<String, Object> product = bkcqLocalOrderCreateDTO.getProduct();

        String supplierId = StreetUtils.getValue("supplierId", product);
        String supplierName = StreetUtils.getValue("supplier", product);
        String contactPerson = StreetUtils.getValue("nick_name", bkcqLocalOrderCreateDTO.getConsumer());
        String contactTel = StreetUtils.getValue("mobile", bkcqLocalOrderCreateDTO.getConsumer());

        //获取sku
        Map<String, Object> skuObj = (Map<String, Object>) product.get("bkcqSku");
        String salePrice = StreetUtils.getValue("salePrice", skuObj);
        String beikeCredit = StreetUtils.getValue("beikeCredit", skuObj);
        String vipPrice = StreetUtils.getValue("vipPrice", skuObj);
        String settledPrice = StreetUtils.getValue("settledPrice", skuObj);

        //创建街市订单
        BkcqLocalOrder bkcqLocalOrder = BkcqLocalOrder.builder().orderNo(bkcqLocalOrderCreateDTO.getOrderNo())
                .orderDate(calendar.getTime()).storesId(supplierId).storesName(supplierName)
                .consumerId(bkcqLocalOrderCreateDTO.getConsumerId()).desc1(getOrderDesc(product,
                        bkcqLocalOrderCreateDTO.getQuantity(), bkcqLocalOrderCreateDTO.getSkuId()))
                .itemNum(bkcqLocalOrderCreateDTO.getQuantity()).saleFee(new BigDecimal(salePrice)
                        .multiply(new BigDecimal(bkcqLocalOrderCreateDTO.getQuantity())))
                .vipFee(new BigDecimal(vipPrice).multiply(new BigDecimal(bkcqLocalOrderCreateDTO.getQuantity())))
                .settledPrice(new BigDecimal(settledPrice).multiply(new BigDecimal(bkcqLocalOrderCreateDTO.getQuantity())))
                .beikeCredit(Long.valueOf(beikeCredit) * bkcqLocalOrderCreateDTO.getQuantity())
                .contactPerson(contactPerson).contactTel(contactTel).status(OrderStatus.WAIT_BUYER_PAY.getValue())
                .refundProcessRecord("[]").createdDate(calendar.getTime()).remark("订单已提交，等待用户付款")
                .modifiedDate(calendar.getTime()).memberMobile(bkcqLocalOrderCreateDTO.getConsumer().get("mobile")+"").build();

        //设置超时时间30分钟
        calendar.add(Calendar.MINUTE, 30);
        bkcqLocalOrder.setExpiredDate(calendar.getTime());

        //插入街市订单
        int row = bkcqLocalOrderDAO.insertSelective(bkcqLocalOrder);
        if (row <= 0) {
            throw new BusinessException(CommonRspCode.RESPONSE_FAIL, "订单录入失败");
        }

        //创建街市订单明细
        BkcqLocalOrderItem bkcqLocalOrderItem = BkcqLocalOrderItem.builder().orderId(bkcqLocalOrder.getOrderId())
                .itemStoreId(StreetUtils.getValue("goodsCode", product))
                .skuId(Long.valueOf(StreetUtils.getValue("skuId", skuObj)))
                .goodsId(Long.valueOf(StreetUtils.getValue("goodsId", product)))
                .itemName(StreetUtils.getValue("title", product))
                .imageInfo(StreetUtils.getValue("picInfo", product)).qty(bkcqLocalOrderCreateDTO.getQuantity())
                .specification(StreetUtils.getValue("specInfo", skuObj))
                .serviceLevel(StreetUtils.getValue("serviceLevel", product))
                .perchaseNotice(StreetUtils.getValue("perchaseNotice", product))
                .createdDate(bkcqLocalOrder.getCreatedDate()).modifiedDate(bkcqLocalOrder.getCreatedDate())
                .remark(String.format("{\"startTime\":\"%s\",\"endTime\":\"%s\"}",
                        StreetUtils.getValue("activityStartTime", product),
                        StreetUtils.getValue("ativityEndTime", product))).build();

        //插入街市订单明细
        row = bkcqLocalOrderItemDAO.insertSelective(bkcqLocalOrderItem);
        if (row <= 0) {
            throw new BusinessException(CommonRspCode.RESPONSE_FAIL, "订单明细失败");
        }

        List<BkcqLocalOrderItem> bkcqLocalOrderItems = new LinkedList<>();
        bkcqLocalOrderItems.add(bkcqLocalOrderItem);
        bkcqLocalOrder.setItems(bkcqLocalOrderItems);
        return bkcqLocalOrder;
    }


    /**
     * 根据订单编号查询
     *
     * @param orderNo 订单编号
     * @return BkcqLocalOrder订单实体
     * @throws BusinessException 业务异常
     */
    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public BkcqLocalOrder findOrderByNo(String orderNo) throws BusinessException {
        BkcqLocalOrder bkcqLocalOrder = bkcqLocalOrderDAO.selectByOrderNo(orderNo);
        if (null == bkcqLocalOrder) {
            throw new BusinessException(CommonRspCode.RESPONSE_FAIL, "订单记录不存在");
        }

        List<BkcqLocalOrderItem> bkcqLocalOrderItems =
                bkcqLocalOrderItemDAO.selectByOrderId(bkcqLocalOrder.getOrderId());
        if (null == bkcqLocalOrderItems || bkcqLocalOrderItems.isEmpty()) {
            throw new BusinessException(CommonRspCode.RESPONSE_FAIL, "订单明细记录不存在");
        }

        bkcqLocalOrder.setItems(bkcqLocalOrderItems);

        List<BkcqLoadCode> codeList = bkcqLoadCodeDAO.selectByOrderNo(bkcqLocalOrder.getOrderNo());
        bkcqLocalOrder.setCodes(codeList);

        StringBuilder codeStr = new StringBuilder();
        if (null != codeList && !codeList.isEmpty()) {
            for (BkcqLoadCode bkcqLoadCode : codeList) {
                codeStr.append(bkcqLoadCode.getCheckCode()).append(",");
            }
        }

        if (codeStr.length() > 1) {
            bkcqLocalOrder.setCodeStr(Aes.getInstance().encrypt(Encryption.EncryptionKey.builder()
                    .key(bkcqLocalOrder.getStoresId()).build(), codeStr.substring(0, codeStr.length() - 1)));
        }
        return bkcqLocalOrder;
    }

    /**
     * 根据参数分页查询
     *
     * @param bkcqLocalOrderQueryDTO 参数
     * @return PageInfo<BkcqLocalOrder> 街市订单分页实体
     * @throws BusinessException 业务异常
     */
    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public PageInfo<BkcqLocalOrder> pageByParams(BkcqLocalOrderQueryDTO bkcqLocalOrderQueryDTO) throws BusinessException {
        PageHelper.startPage(bkcqLocalOrderQueryDTO.getPage_no(), bkcqLocalOrderQueryDTO.getPage_size());

        List<String> statuses = null;
        if (StrUtil.isNotBlank(bkcqLocalOrderQueryDTO.getStatus())) {
            statuses = Lists.newArrayList(bkcqLocalOrderQueryDTO.getStatus().split(","));
        }
        List<BkcqLocalOrder> bkcqLocalOrders =
                bkcqLocalOrderDAO.selectByParams(bkcqLocalOrderQueryDTO.getCustomerId(), statuses);

        return new PageInfo<>(bkcqLocalOrders);
    }

    /**
     * 订单支付成功
     *
     * @param orderNo 订单编号
     * @param payWay  支付方式
     * @return List<BkcqLoadCode> 核销码
     * @throws BusinessException 业务异常
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderPayResuktDTO orderPaySuccess(String orderNo, String payWay) throws BusinessException {
        BkcqLocalOrder bkcqLocalOrder = bkcqLocalOrderDAO.selectByOrderNo(orderNo);
        if (null == bkcqLocalOrder) {
            throw new BusinessException(CommonRspCode.RESPONSE_FAIL, "订单记录不存在");
        }

        List<BkcqLocalOrderItem> bkcqLocalOrderItems =
                bkcqLocalOrderItemDAO.selectByOrderId(bkcqLocalOrder.getOrderId());
        if (null == bkcqLocalOrderItems || bkcqLocalOrderItems.isEmpty()) {
            throw new BusinessException(CommonRspCode.RESPONSE_FAIL, "订单明细记录不存在");
        }

        bkcqLocalOrder.setItems(bkcqLocalOrderItems);

        if (!"WAIT_BUYER_PAY".equals(bkcqLocalOrder.getStatus())) {
            return OrderPayResuktDTO.builder().bkcqLocalOrder(bkcqLocalOrder).build();
        }

        Date now = new Date();

        BkcqLocalOrder updateBkcqLocalOrder = new BkcqLocalOrder();
        updateBkcqLocalOrder.setOrderId(bkcqLocalOrder.getOrderId());
        updateBkcqLocalOrder.setPaymentWayKey(payWay);
        bkcqLocalOrder.setPaymentWayKey(updateBkcqLocalOrder.getPaymentWayKey());
        updateBkcqLocalOrder.setRemark("订单支付成功");
        bkcqLocalOrder.setRemark(updateBkcqLocalOrder.getRemark());
        updateBkcqLocalOrder.setOrderDate(now);
        bkcqLocalOrder.setOrderDate(updateBkcqLocalOrder.getOrderDate());
        updateBkcqLocalOrder.setStatus("WAIT_BUYER_USE");
        bkcqLocalOrder.setStatus(updateBkcqLocalOrder.getStatus());
        updateBkcqLocalOrder.setModifiedDate(now);
        bkcqLocalOrder.setModifiedDate(updateBkcqLocalOrder.getModifiedDate());
        updateBkcqLocalOrder.setOldStatus("WAIT_BUYER_PAY");
        int row = bkcqLocalOrderDAO.updateByPrimaryKeySelective(updateBkcqLocalOrder);
        if (row <= 0) {
            logger.error(String.format("支付成功订单修改失败，订单编号[%s]", orderNo));
            throw new BusinessException(CommonRspCode.RESPONSE_FAIL, "订单修改失败");
        }

        List<BkcqLoadCode> codeList = Lists.newArrayList();
        StringBuilder codeStr = new StringBuilder();
        for (BkcqLocalOrderItem bkcqLocalOrderItem : bkcqLocalOrderItems) {
            String remark = bkcqLocalOrderItem.getRemark();
            long time;
            try {
                Map<String, Object> product = FastJsonUtil.jsonToMap(remark);
                time = Long.parseLong(product.get("endTime").toString());
            } catch (SystemException e) {
                throw new BusinessException(CommonRspCode.RESPONSE_FAIL, "订单明细记录数据错误");
            }

            for (int i = 0; i < bkcqLocalOrder.getItemNum(); i++) {
                BkcqLoadCode bkcqLoadCode = BkcqLoadCode.builder().checkCode(RandomUtil.randomString(12).toUpperCase())
                        .orderNo(bkcqLocalOrder.getOrderNo()).codeStatus("CODE_WAIT_USE").createdDate(now)
                        .modifiedDate(now).expiredDate(new Date(time)).remark("待使用").build();
                //保证核销码唯一
                boolean flag = true;
                int times = 0;
                while (flag && times < 30) {
                    try {
                        row = bkcqLoadCodeDAO.insertSelective(bkcqLoadCode);
                        if (row > 0) {
                            codeStr.append(bkcqLoadCode.getCheckCode()).append(",");
                            codeList.add(bkcqLoadCode);
                            flag = false;
                        }
                    } catch (Exception e) {
                        times++;
                    }
                }

                if (flag) {
                    logger.error(String.format("支付成功订单生成核销码失败，订单编号[%s]", orderNo));
                    throw new BusinessException(CommonRspCode.RESPONSE_FAIL, "生成核销码失败");
                }
            }
        }

        return OrderPayResuktDTO.builder().bkcqLocalOrder(bkcqLocalOrder).codes(codeList)
                .codeStr(Aes.getInstance().encrypt(Encryption.EncryptionKey.builder()
                        .key(bkcqLocalOrder.getStoresId()).build(), codeStr.substring(0, codeStr.length() - 1))).build();
    }

    /**
     * 取消订单
     *
     * @param orderNo    订单编号
     * @param consumerId 消费者编号
     * @throws BusinessException 业务异常
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BkcqLocalOrder cancelOrder(String orderNo, String consumerId) throws BusinessException {
        BkcqLocalOrder bkcqLocalOrder = bkcqLocalOrderDAO.selectByOrderNo(orderNo);
        if (null == bkcqLocalOrder) {
            throw new BusinessException(CommonRspCode.RESPONSE_FAIL, "取消订单失败，订单记录不存在");
        }

        if (!"WAIT_BUYER_PAY".equals(bkcqLocalOrder.getStatus())) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "取消订单失败，订单状态异常");
        }

        if (!consumerId.equals(bkcqLocalOrder.getConsumerId())) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "取消订单失败，非法操作");
        }

        if ("TRADE_CLOSED_BY_USER".equals(bkcqLocalOrder.getStatus())) {
            return bkcqLocalOrder;
        }

        BkcqLocalOrder updateOrder = new BkcqLocalOrder();
        updateOrder.setOrderId(bkcqLocalOrder.getOrderId());
        updateOrder.setStatus("TRADE_CLOSED_BY_USER");
        updateOrder.setOldStatus("WAIT_BUYER_PAY");
        updateOrder.setRemark("订单取消成功");
        updateOrder.setModifiedDate(new Date());
        int row = bkcqLocalOrderDAO.updateByPrimaryKeySelective(updateOrder);
        if (row <= 0) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "取消订单失败，订单状态异常");
        }

        bkcqLocalOrder.setStatus(updateOrder.getStatus());
        bkcqLocalOrder.setRemark(updateOrder.getRemark());
        bkcqLocalOrder.setModifiedDate(updateOrder.getModifiedDate());
        return bkcqLocalOrder;
    }

    /**
     * 自动过期订单核销码
     *
     * @param id 核销码编号
     * @throws BusinessException 业务异常
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BkcqLoadCode autoExpiredOrderCode(Long id) throws BusinessException {
        BkcqLoadCode bkcqLoadCode = bkcqLoadCodeDAO.selectByPk(id);
        if (null == bkcqLoadCode) {
            throw new BusinessException(CommonRspCode.RESPONSE_FAIL, "自动过期订单核销码失败，核销码不存在");
        }

        if (!"CODE_WAIT_USE".equals(bkcqLoadCode.getCodeStatus())) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "自动过期订单核销码失败，核销码状态异常");
        }

        BkcqLoadCode updateCode = new BkcqLoadCode();
        updateCode.setId(bkcqLoadCode.getId());
        updateCode.setCodeStatus("CODE_EXPIRED");
        updateCode.setOldCodeStatus("CODE_WAIT_USE");
        updateCode.setRemark("已过期");
        updateCode.setModifiedDate(new Date());
        int row = bkcqLoadCodeDAO.updateByPrimaryKeySelective(updateCode);
        if (row <= 0) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "自动过期订单核销码失败，核销码状态异常");
        }

        bkcqLoadCode.setCodeStatus(updateCode.getCodeStatus());
        bkcqLoadCode.setRemark(updateCode.getRemark());
        bkcqLoadCode.setModifiedDate(updateCode.getModifiedDate());
        return bkcqLoadCode;
    }


    /**
     * 自动取消订单
     *
     * @param orderNo 订单编号
     * @throws BusinessException 业务异常
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BkcqLocalOrder autoCancelOrder(String orderNo) throws BusinessException {
        BkcqLocalOrder bkcqLocalOrder = bkcqLocalOrderDAO.selectByOrderNo(orderNo);
        if (null == bkcqLocalOrder) {
            throw new BusinessException(CommonRspCode.RESPONSE_FAIL, "取消订单失败，订单记录不存在");
        }

        if (!"WAIT_BUYER_PAY".equals(bkcqLocalOrder.getStatus())) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "取消订单失败，订单状态异常");
        }

        BkcqLocalOrder updateOrder = new BkcqLocalOrder();
        updateOrder.setOrderId(bkcqLocalOrder.getOrderId());
        updateOrder.setStatus("TRADE_CLOSED_BY_SYSTEM");
        updateOrder.setOldStatus("WAIT_BUYER_PAY");
        updateOrder.setRemark("过期订单自动取消成功");
        updateOrder.setModifiedDate(new Date());
        int row = bkcqLocalOrderDAO.updateByPrimaryKeySelective(updateOrder);
        if (row <= 0) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "取消订单失败，订单状态异常");
        }

        bkcqLocalOrder.setStatus(updateOrder.getStatus());
        bkcqLocalOrder.setRemark(updateOrder.getRemark());
        bkcqLocalOrder.setModifiedDate(updateOrder.getModifiedDate());
        return bkcqLocalOrder;
    }

    /**
     * 申请退款订单
     *
     * @param orderNo    订单编号
     * @param consumerId 消费者编号
     * @throws BusinessException 业务异常
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BkcqLocalOrder applyRefundOrder(String orderNo, String consumerId, String refundReason) throws BusinessException {
        BkcqLocalOrder bkcqLocalOrder = bkcqLocalOrderDAO.selectByOrderNo(orderNo);
        if (null == bkcqLocalOrder) {
            throw new BusinessException(CommonRspCode.RESPONSE_FAIL, "申请退款订单失败，订单记录不存在");
        }

        if (!"WAIT_BUYER_USE".equals(bkcqLocalOrder.getStatus())) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "申请退款订单失败，订单状态异常");
        }

        if (!consumerId.equals(bkcqLocalOrder.getConsumerId())) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "申请退款订单失败，非法操作");
        }

        Date date = new Date();

        List<Map<String, Object>> refundProcessRecord =
                JSONObject.parseObject(bkcqLocalOrder.getRefundProcessRecord(), List.class);
        HashMap<String, Object> map = Maps.newHashMap();
        map.put("status", "REFUND_WAIT_SELLER_CONFIRM");
        map.put("record_time", date.getTime());
        refundProcessRecord.add(map);
        BkcqLocalOrder updateOrder = new BkcqLocalOrder();
        updateOrder.setRefundProcessRecord(FastJsonUtil.toJson(refundProcessRecord));
        updateOrder.setOrderId(bkcqLocalOrder.getOrderId());
        updateOrder.setStatus("REFUND_WAIT_SELLER_CONFIRM");
        updateOrder.setOldStatus("WAIT_BUYER_USE");
        updateOrder.setRemark("订单申请退款成功，等待商家确认");
        updateOrder.setRefundReason(refundReason);
        updateOrder.setModifiedDate(date);
        int row = bkcqLocalOrderDAO.updateByPrimaryKeySelective(updateOrder);
        if (row <= 0) {
            throw new BusinessException(RspCode.RESPONSE_FAIL, "申请退款订单失败，订单状态异常");
        }

        bkcqLocalOrder.setStatus(updateOrder.getStatus());
        bkcqLocalOrder.setRemark(updateOrder.getRemark());
        bkcqLocalOrder.setRefundReason(updateOrder.getRefundReason());
        bkcqLocalOrder.setModifiedDate(updateOrder.getModifiedDate());
        return bkcqLocalOrder;
    }

    /**
     * 获取订单描述
     *
     * @param product  商品信息
     * @param quantity 数量
     * @param skuId    SKU编码
     * @return String字符串
     */
    private String getOrderDesc(Map<String, Object> product, int quantity, String skuId) {
        //组装订单描述字段，获取商品的title、desc1、pic_info
        List<Map<String, Object>> descList = Lists.newLinkedList();
        Map<String, Object> descMap = Maps.newHashMap();
        descMap.put("goodsId", product.get("goodsId"));
        descMap.put("goodsCode", product.get("goodsCode"));
        descMap.put("skuId", skuId);
        descMap.put("title", product.get("title"));
        descMap.put("desc1", product.get("desc1"));
        descMap.put("picInfo", product.get("picInfo"));
        descMap.put("quantity", quantity);
        descList.add(descMap);
        return FastJsonUtil.toJson(descList);
    }

}
