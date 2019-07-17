package com.meitianhui.member.service.impl;

import com.meitianhui.common.constant.CommonRspCode;
import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;
import com.meitianhui.common.util.*;
import com.meitianhui.member.constant.Constant;
import com.meitianhui.member.constant.RspCode;
import com.meitianhui.member.dao.*;
import com.meitianhui.member.entity.*;
import com.meitianhui.member.service.CompanyAccountService;
import com.meitianhui.member.service.MemberService;
import com.meitianhui.member.service.StoresService;
import com.meitianhui.member.util.AESTool;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Executors;

@SuppressWarnings("unchecked")
@Service
public class CompanyAccountServiceImpl implements CompanyAccountService {

	private static final Logger logger = Logger.getLogger(CompanyAccountServiceImpl.class);

	@Autowired
	private DocUtil docUtil;

	@Autowired
	public MemberDao memberDao;

	@Autowired
	public CompanyAccountDao companyAccountDao;

	@Autowired
	public MemberService memberService;

	@Autowired
	public RedisUtil redisUtil;

	@Autowired
	public StoresDao storesDao;


	@Override
	public void invitation(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "login_name", "account_type", "check_code", "invite_code" });
		logger.info("invitation--paramsMap：" + paramsMap);
		HashMap<String, Object> webJsonMap = new LinkedHashMap<String, Object>();

		String key = "invitation_"+paramsMap.get("login_name") + "_" + paramsMap.get("check_code");
		String check_code = redisUtil.getStr(key);
		if (!StringUtils.equalsIgnoreCase(check_code, paramsMap.get("check_code")+"")) {
			logger.info("invitation--短信验证码不正确：" + paramsMap+"_"+check_code);
			throw new BusinessException("3000", "短信验证码不正确");
		}
		redisUtil.del(key);


		String memberId = UUID.randomUUID().toString();
		Map<String, Object> tempSqlAccountMap = new LinkedHashMap<String, Object>();
		tempSqlAccountMap.put("account_id", memberId);
		tempSqlAccountMap.put("account_type", paramsMap.get("account_type")); //subcompany、co-partner(社区合伙人,co-partner)
		tempSqlAccountMap.put("login_name", paramsMap.get("login_name"));
		int retV = companyAccountDao.createCompanyAccount(tempSqlAccountMap);
		if (retV<=0) {
			logger.info("invitation--创建合伙人失败：" + paramsMap);
			throw new BusinessException("4000", "创建合伙人失败");
		}

		Map<String, Object> tempSqlMap = new LinkedHashMap<String, Object>();
		tempSqlMap.put("account_id", memberId);
		tempSqlMap.put("account_type", paramsMap.get("account_type")+"");
		tempSqlMap.put("login_name", paramsMap.get("login_name")+"");
		int retVDistribution = companyAccountDao.createMemberDistributionInfo(tempSqlMap);
		if (retVDistribution<=0) {
			logger.info("invitation--创建合伙人临时关系表失败：" + paramsMap);
			throw new BusinessException("5000", "创建合伙人临时关系表失败");
		}

		webJsonMap.put("msg", "ok");
		result.setResultData(webJsonMap);
	}


	@Override
	public void findStoresInfo(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParams(paramsMap, new String[]{"code"});
		String stores_id = AESTool.decrypt(paramsMap.get("code") + "");
		logger.info("解密的店铺stores_id是："+stores_id);
		paramsMap.clear();
		paramsMap.put("stores_id", stores_id);


		Map<String, Object> storesMap = storesDao.selectMDStoresBaseInfo(paramsMap);
		if (storesMap==null) {
			logger.info("findStoresInfo--没有查到商户：" + paramsMap);
			throw new BusinessException("4000", "商户不存在");
		}

		HashMap<String, Object> webJsonMap = new HashMap<String, Object>();

		findPic(storesMap, "head_pic_path");

		webJsonMap.put("stores_id", storesMap.get("stores_id"));
		webJsonMap.put("head_pic_path", storesMap.get("head_pic_path"));
		webJsonMap.put("stores_type_key", storesMap.get("stores_type_key"));
		webJsonMap.put("stores_name", storesMap.get("stores_name"));

		result.setResultData(webJsonMap);
	}


	@Override
	public void findMemberInfo(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParams(paramsMap, new String[]{"login_name", "account_type"});
		HashMap<String, Object> webJsonMap = new HashMap<String, Object>();

		Map<String, Object> tempSqlMap = new HashMap<String, Object>();
		tempSqlMap.put("login_name", paramsMap.get("login_name"));
		tempSqlMap.put("account_type", paramsMap.get("account_type"));
		Map<String, Object> companyAccountMap = companyAccountDao.findByPhone(tempSqlMap);
		if (companyAccountMap==null) {
			logger.info("login--没有查到代理或合伙人：" + paramsMap);
			throw new BusinessException("4000", "账号不存在");
		}
		String profile_pic = companyAccountMap.get("profile_pic")+"";
		logger.info("login-profile_pic-->"+profile_pic);
		findPic(companyAccountMap, "profile_pic");


		companyAccountMap.put("money", "0");
		companyAccountMap.put("in_money", "0");
		companyAccountMap.put("bank_account", "");
		String finance_service_url = PropertiesConfigUtil.getProperty("finance_service_url");
		Map<String, String> requestData = new HashMap<>();
		requestData.put("service", "finance.consumer.findMemberMoneyAndBank");
		Map<String, Object> params = new LinkedHashMap<>();
		params.put("member_id", companyAccountMap.get("account_id"));
		params.put("account_type", paramsMap.get("account_type"));
		requestData.put("params", FastJsonUtil.toJson(params));
		String resultStr = HttpClientUtil.post(finance_service_url, requestData);
		logger.info("login-resultStr-->"+resultStr);
		Map<String, Object> resultM = FastJsonUtil.jsonToMap(resultStr);
		logger.info("login-resultM-->"+resultM);
		if ((resultM.get("rsp_code")+"").equals(RspCode.RESPONSE_SUCC)) {
			Map<String, Object> supplierMap = FastJsonUtil.jsonToMap(resultM.get("memberInfo")+"");
			logger.info("login-resultM-->"+supplierMap);
			companyAccountMap.put("bank_account", supplierMap.get("bank_account"));
			companyAccountMap.put("money", supplierMap.get("money"));
			companyAccountMap.put("in_money", supplierMap.get("in_money"));
		}

		Map<String, Object> memberSqlMap = new HashMap<String, Object>();
		String account_id = companyAccountMap.get("account_id")+"";

		tempSqlMap.put("member_id", account_id);
		String invite_code= companyAccountDao.findMemberInvitationCode(memberSqlMap);
		companyAccountMap.put("invite_code", invite_code);


		tempSqlMap.put("member_id", account_id);
		Map<String, Object> memberDistribution = companyAccountDao.findMemberDistribution(memberSqlMap);
		if(memberDistribution==null){
			companyAccountMap.put("is_vip","no");
		}else {
			companyAccountMap.put("is_vip","yes");
		}
		webJsonMap.put("CompanyAccount", companyAccountMap);

		result.setResultData(webJsonMap);
	}



	@Override
	public void login(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "login_name", "account_type", "check_code" });
		logger.info("login--paramsMap：" + paramsMap);
		HashMap<String, Object> webJsonMap = new HashMap<String, Object>();

		String key = "login"+"_"+paramsMap.get("login_name");
		String check_code = redisUtil.getStr(key);
		if (!StringUtils.equalsIgnoreCase(check_code, paramsMap.get("check_code")+"")) {
			logger.info("login--验证码错误：" + paramsMap+"_"+check_code);
			throw new BusinessException("3000", "验证码错误");
		}
		redisUtil.del(key);

		findMemberInfo(paramsMap, result);
	}


	@Override
	public void sendSMS(Map<String, Object> paramsMap, ResultData result) throws Exception {
		ValidateUtil.validateParams(paramsMap, new String[] { "login_name", "type_name" });
		logger.info("login--paramsMap：" + paramsMap);
		final String mobile = paramsMap.get("login_name")+"";
		final String type_name = paramsMap.get("type_name")+"";

		Executors.newCachedThreadPool().execute(new Runnable() {
			@Override
			public void run() {
				try {
					if (StringUtil.isNotEmpty(mobile)) {
						String check_code = IDUtil.random(4);
						redisUtil.setStr(type_name +"_"+ mobile, check_code, 120);
						String notification_service_url = PropertiesConfigUtil.getProperty("notification_service_url");
						Map<String, String> reqParams = new HashMap<String, String>();
						Map<String, String> bizParams = new HashMap<String, String>();
						reqParams.put("service", "notification.Type.SendSMS");
						bizParams.put("sms_source", Constant.DATA_SOURCE_SJLY_01);
						bizParams.put("mobiles", mobile);
						bizParams.put("code", check_code);
						reqParams.put("params", FastJsonUtil.toJson(bizParams));
						HttpClientUtil.postShort(notification_service_url, reqParams);
					}
				} catch (Exception e) {
					logger.error(e);
				}
			}
		});

		HashMap<String, Object> webJsonMap = new HashMap<String, Object>();
		webJsonMap.put("state", "2000");
		webJsonMap.put("msg", "ok");
		result.setResultData(webJsonMap);
	}


	private void findPicList(List<Map<String, Object>> list, String[] arrStr) throws SystemException {
		if(list!=null && list.size()>0){
            for (Map<String, Object> stringObjectMap : list) {
				for (String pic_info : arrStr) {
					findPic(stringObjectMap, pic_info);
				}
            }
        }else{
          logger.info("");
		}
	}

	private void findPic(Map<String, Object> mapinfo, String pic_path) {
		ArrayList<String> doc_ids = new ArrayList<>();
		Map<String, Object> idsMap = new LinkedHashMap<>();
		try{
			String neighbor_pic_path = StringUtil.formatStr(mapinfo.get(pic_path));
			if (StringUtils.isNotBlank(neighbor_pic_path)) {
				List<Map<String, Object>> listTemp = FastJsonUtil.jsonToList(neighbor_pic_path);
				for (Map<String, Object> map : listTemp) {
					doc_ids.add(map.get("path_id") + "");
				}
			}else{
				mapinfo.put(pic_path, "null");
				return;
			}
		}catch (Exception e){
			logger.error("findPic--"+pic_path+":"+mapinfo.get(pic_path));
			logger.error(mapinfo.get(pic_path)+" :findPic--{}",e);
			mapinfo.put(pic_path, "");
			return;
		}

		if(doc_ids!=null && !doc_ids.isEmpty()){
//			String path = "http://beike-app-pic.oss-cn-hangzhou.aliyuncs.com/";
//			Map<String, Object> stringObjectMap = goodsDao.imageUrlFind(doc_ids.get(0));
			Map<String, Object> stringObjectMap = docUtil.imageUrlFind(doc_ids);
//			String stringObject = docUtil.imageUrlFind(doc_ids.get(0));
			if(stringObjectMap!=null){
				ArrayList<String> objects = new ArrayList<>();
				for (Map.Entry<String, Object> entry: stringObjectMap.entrySet()) {
					objects.add(entry.getValue()+"");
				}
				mapinfo.put(pic_path, objects);
			}else{
				mapinfo.put(pic_path, "null");
			}
		}
	}


}





