package com.ande.buyb2c.web.contorller.receiptAddress;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ande.buyb2c.common.util.AbstractController;
import com.ande.buyb2c.common.util.JsonResponse;
import com.ande.buyb2c.common.util.SystemCode;
import com.ande.buyb2c.order.service.IOrderService;
import com.ande.buyb2c.receiptAddress.entity.ReceiptAddress;
import com.ande.buyb2c.receiptAddress.service.IReceiptAddressService;
import com.ande.buyb2c.user.entity.User;
import com.ande.buyb2c.web.UserJsonConvertUtil;

/**
 * @author chengzb
 * @date 2018年2月2日下午4:59:29
 */
@RestController
@RequestMapping("/front/receiptAddress")
public class ReceiptAddressController extends AbstractController{
	@Resource
	private UserJsonConvertUtil userJsonConvertUtil;
	@Resource
	private IReceiptAddressService receiptAddressService;
	@Resource
	private IOrderService orderService;
	/**
	 * 添加
	 */
	@RequestMapping("/addOrUpdateReceiptAddress")
	public JsonResponse<String> addReceiptAddress(HttpServletRequest request,@RequestBody ReceiptAddress address){
		 User user = userJsonConvertUtil.getUser(request);
		JsonResponse<String> json=new JsonResponse<String>();
		try{
		//	ReceiptAddress receiptAddress=receiptAddressService.getDefaultAddress(user.getUserId());
			if(address.getReceiptAddressId()==null){
				/*if("1".equals(address.getDefaultAddress())){
					//默认地址
					if(receiptAddress!=null){
						//存在默认地址
						json.setRes(SystemCode.ADDRESS_EXIT.getCode());
						json.setResult(SystemCode.ADDRESS_EXIT.getMsg());
						return json;
					}
				}*/
				address.setUpdateTime(new Date());
				//新增
				address.setCreateTime(new Date());
				address.setCustomerId(user.getUserId());
				receiptAddressService.insertSelective(address);
			}else{
				//更新
			/*	if("1".equals(address.getDefaultAddress())){
					//默认地址
					if(receiptAddress!=null&&!receiptAddress.getReceiptAddressId().equals(address.getReceiptAddressId())){
						//存在默认地址
						json.setRes(SystemCode.ADDRESS_EXIT.getCode());
						json.setResult(SystemCode.ADDRESS_EXIT.getMsg());
						return json;
					}
				}*/
				address.setUpdateTime(new Date());
				receiptAddressService.updateByPrimaryKeySelective(address);
			}
			json.set(SystemCode.SUCCESS.getCode(), 
					SystemCode.SUCCESS.getMsg());
		} catch (Exception e) {
			logError("添加收货地址异常", e);
		}
		return json;
	}
	/**
	 * 收货地址列表
	 */
	@RequestMapping("/getReceiptAddressList")
	public JsonResponse<ReceiptAddress> getReceiptAddressList(HttpServletRequest request){
		 User user = userJsonConvertUtil.getUser(request);
		JsonResponse<ReceiptAddress> json=new JsonResponse<ReceiptAddress>();
			ReceiptAddress address=new ReceiptAddress();
			address.setCustomerId(user.getUserId());
			List<ReceiptAddress> list=receiptAddressService.getAllBySelect(address);
			if(list!=null&&list.size()!=0){
				json.set(SystemCode.SUCCESS.getCode(), 
						SystemCode.SUCCESS.getMsg());
				json.setList(list);
			}
		return json;
	}
	/**
	 * 收货地址列表
	 */
	@RequestMapping("/getReceiptAddressById")
	public JsonResponse<ReceiptAddress> getReceiptAddressById(Integer receiptAddressId){
		JsonResponse<ReceiptAddress> json=new JsonResponse<ReceiptAddress>();
		json.set(SystemCode.SUCCESS.getCode(), 
				SystemCode.SUCCESS.getMsg());
		json.setObj(receiptAddressService.selectByPrimaryKey(receiptAddressId));
		return json;
	}
	/**
	 * 删除
	 */
	@RequestMapping("/delReceiptAddress")
	public JsonResponse<ReceiptAddress> delReceiptAddress(Integer receiptAddressId){
		JsonResponse<ReceiptAddress> json=new JsonResponse<ReceiptAddress>();
		try {
			receiptAddressService.deleteByPrimaryKey(receiptAddressId);
			json.set(SystemCode.SUCCESS.getCode(), 
					SystemCode.SUCCESS.getMsg());
		} catch (Exception e) {
			logError("删除收货地址异常", e);
		}
		return json;
	}
	//下单时拉取用户收货地址
	@RequestMapping("/getReceiptAddress")
	public JsonResponse<ReceiptAddress> getReceiptAddress(HttpServletRequest request){
		JsonResponse<ReceiptAddress> json=new JsonResponse<ReceiptAddress>();
		Integer userId=userJsonConvertUtil.getUser(request).getUserId();
		Integer id=orderService.getOrderLast(userId);
		ReceiptAddress address=null;
		if(id==null){
			//没有下过订单
			 address=receiptAddressService.getAddress(null,userId);
		}else{
			address=receiptAddressService.getAddress(id,null);
		}
		if(address!=null){
			json.set(SystemCode.SUCCESS.getCode(), 
					SystemCode.SUCCESS.getMsg());
			json.setObj(address);
		}
		return json;
	}
}
