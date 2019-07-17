package com.meitianhui.member.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;
import com.meitianhui.common.util.*;
import com.meitianhui.member.constant.Constant;
import com.meitianhui.member.constant.RspCode;
import com.meitianhui.member.dao.ConsumerDao;
import com.meitianhui.member.service.ConsumerService;
import com.meitianhui.member.service.MemberService;
import com.meitianhui.member.service.TIMService;
import com.meitianhui.member.util.NumberUtils;
import com.tls.tls_sigature.tls_sigature;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

@Service
public class TIMServiceImpl implements TIMService {


    private static final Logger logger = Logger.getLogger(TIMServiceImpl.class);

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ConsumerService consumerService;

    @Autowired
    private ConsumerDao consumerDao;

    @Autowired
    private MemberService memberService;

    @Autowired
    private DocUtil docUtil;

    /**
     *获取userSig
     */
    @Override
    public void genSig(Map<String, Object> paramsMap, ResultData result) throws BusinessException,Exception {
        ValidateUtil.validateParams(paramsMap,new String[]{"identifier"});

        Map<String, Object> tempMap = new HashMap<>();
        //先判断用户是否已经创建了账号,如果没有就创建
        if(paramsMap.get("identifier")!=null&&(!paramsMap.get("identifier").equals(""))){
            tempMap.put("to_Account",paramsMap.get("identifier"));
            this.queryState(tempMap,result);
        }

        if(result.getResultData()!=null){
            tempMap.clear();
           List<Map<String,String>> stateList = (List<Map<String,String>>)result.getResultData();
           //判断是否有账号
           if(CollectionUtils.isNotEmpty(stateList)&&"@TLS#NOT_FOUND".equals(stateList.get(0).get("To_Account"))){
               logger.info("创建一个新的通讯云账号，手机号为"+paramsMap.get("identifier"));
               //注册一个新账号，根据手机号用户的基本信息
               tempMap.put("mobile",paramsMap.get("identifier").toString());
               tempMap.put("member_type_key", Constant.MEMBER_TYPE_CONSUMER);
               memberService.memberInfoFindByMobile(tempMap,result);
               Map<String,Object> consumerMap =  (Map<String,Object> )result.getResultData();
               String nick_name = consumerMap==null?null:consumerMap.get("nick_name").toString();
               String head_pic_path = consumerMap==null?null:consumerMap.get("head_pic_path").toString();
               tempMap.put("identifier",paramsMap.get("identifier"));
               tempMap.put("nick",nick_name);
               tempMap.put("faceUrl",head_pic_path);
               this.accountImport(tempMap,result);
           }else{
               logger.info("手机号为"+paramsMap.get("identifier")+"已有通讯账号");
           }
        }

        String usersig = this.getUsig(paramsMap.get("identifier")+"");
        logger.error("获取usersig:"+usersig);
        Map<String,Object> resultMap = new HashMap<String,Object>();
        resultMap.put("usersig",usersig);
        result.setResultData(resultMap);
    }

    /**
     * 批量导入账号
     * @param paramsMap
     * @param result
     * @throws BusinessException
     * @throws Exception
     */
    @Override
    public void multiaccountImport(Map<String, Object> paramsMap, ResultData result) throws BusinessException, Exception {

        //1.先给所有用户都创建一个账号
        String multiaccount_import_url =  this.getTimUrl(MULTIACCOUNT_IMPORT_URL,"admin");

        logger.info("请求云通讯批量导入账号,请求地址:"+multiaccount_import_url);
        //查询用户账号
        Long userCount = consumerDao.getAllConsumerCount();
        List<String> mobileList = consumerDao.getAllConsumerMobile();
        List<String> importAccount = new ArrayList<>();
        if(CollectionUtils.isEmpty(mobileList)){
            new BusinessException(RspCode.MEMBER_NOT_EXIST,"未查询到导入账号");
        }

        Map<String, Object> tempMap = new HashMap<>();
        //先判断用户是否已经创建了账号,防止重复导入
        tempMap.put("to_Account", mobileList.get(0));
        this.queryState(tempMap, result);
        if(result.getResultData()!=null) {
            tempMap.clear();
            List<Map<String, String>> stateList = (List<Map<String, String>>) result.getResultData();
            //判断是否有账号,已有账号就不导入了
            if (CollectionUtils.isNotEmpty(stateList) && !"@TLS#NOT_FOUND".equals(stateList.get(0).get("To_Account"))) {
                new BusinessException(RspCode.MEMBER_EXIST,"云通讯已存在账号,请勿重复导入");
            }
        }

        logger.info("userCount:{}"+userCount.toString());
        for(int i=0;i<=(userCount/100);i++){
            logger.info("userCount_i:{}"+i);
            if((i+1)*100<userCount){
                logger.info("subList1:{}"+ i*100+","+(i+1)*100);
                importAccount = mobileList.subList(i*100,(i+1)*100);
            }else {
                logger.info("subList2:{}"+ (i)*100+","+Integer.parseInt(userCount.toString()));
                importAccount = mobileList.subList((i-1)*100,Integer.parseInt(userCount.toString()));
            }

              logger.info("importAccount:{}"+ JSONObject.toJSONString(importAccount));
            Map<String, Object> reqParams = new HashMap<String, Object>();
            String resultStr = null;
            reqParams.put("Accounts",importAccount);
            logger.info("请求云通讯批量导入账号,请求参数:"+FastJsonUtil.toJson(reqParams));
            resultStr = HttpClientUtil.postWithJSON(multiaccount_import_url, FastJsonUtil.toJson(reqParams));
            logger.info("请求云通讯批量导入账号,返回值:"+resultStr);

            if(StringUtils.isEmpty(resultStr)){
                logger.info("请求云通讯批量导入账号,返回为空");
                throw new BusinessException(RspCode.REQUEST_TIM_ERROR,RspCode.MSG.get(RspCode.REQUEST_TIM_ERROR) );
            }

            Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);

            handlerResponseStr(resultStr,"批量导入账号:"+i);

            Thread.sleep(10);

        }
        result.setResultData("批量导入云通讯账号成功");

    }

    /**
     * 导入单个账号
     */
    @Override
    public void accountImport(Map<String, Object> paramsMap, ResultData result) throws BusinessException, Exception {
        ValidateUtil.validateParams(paramsMap,new String[]{"identifier"});
        String account_import_url =  this.getTimUrl(ACCOUNT_IMPORT_URL,"admin");
        logger.info("请求云通讯批量导入账号,请求地址:"+account_import_url);
        String identifier = paramsMap.get("identifier")+"";

        Map<String, Object> reqParams = new HashMap<String, Object>();
        String resultStr = null;

        reqParams.put("Identifier",identifier);
        if(paramsMap.get("nick")!=null){
            reqParams.put("Nick",paramsMap.get("nick"));
        }
        if(paramsMap.get("faceUrl")!=null){
            //图片解析一下
            reqParams.put("FaceUrl",getPicUrlId(StringUtil.formatStr(paramsMap.get("faceUrl"))));
        }

        logger.info("请求云通讯导入单个账号,请求参数:"+FastJsonUtil.toJson(reqParams));
        resultStr = HttpClientUtil.postWithJSON(account_import_url, FastJsonUtil.toJson(reqParams));
        logger.info("请求云通讯导入单个账号,返回值:"+resultStr);

        if(StringUtils.isEmpty(resultStr)){
            logger.info("请求云通讯批量导入账号,返回为空");
            throw new BusinessException(RspCode.REQUEST_TIM_ERROR,RspCode.MSG.get(RspCode.REQUEST_TIM_ERROR) );
        }

        handlerResponseStr(resultStr,"导入单个账号");

    }

    /**
     *登陆状态失效接口
     */
    @Override
    public void kick(Map<String, Object> paramsMap, ResultData result) throws BusinessException, Exception {
        String optName = "登陆状态失效接口";
        ValidateUtil.validateParams(paramsMap,new String[]{"identifier"});
        String kick_url =  this.getTimUrl(KICK_URL,"admin");
        logger.info(optName+",请求地址:"+kick_url);
        String identifier = paramsMap.get("identifier")+"";

        Map<String, Object> reqParams = new HashMap<String, Object>();
        String resultStr = null;

        reqParams.put("Identifier",identifier);

        logger.info(optName+",请求参数:"+FastJsonUtil.toJson(reqParams));
        resultStr = HttpClientUtil.postWithJSON(kick_url, FastJsonUtil.toJson(reqParams));
        logger.info(optName+",返回值:"+resultStr);

        if(StringUtils.isEmpty(resultStr)){
            logger.info("请求"+optName+"返回为空");
            throw new BusinessException(RspCode.REQUEST_TIM_ERROR,RspCode.MSG.get(RspCode.REQUEST_TIM_ERROR) );
        }

        handlerResponseStr(resultStr,optName);
    }

    /**
     *单发消息
     */
    @Override
    public void sendMsg(Map<String, Object> paramsMap, ResultData result) throws BusinessException, Exception {
        String optName = "单发消息接口";
        ValidateUtil.validateParams(paramsMap,new String[]{"fromAccount","toAccount","msgContent"});
        String sendmsg_url =  this.getTimUrl(SENDMSG_URL,"admin");
        logger.info(optName+",请求地址:"+sendmsg_url);
        String identifier = paramsMap.get("identifier")+"";

        Map<String, Object> reqParams = new HashMap<String, Object>();
        reqParams.put("SyncOtherMachine",2);//1：把消息同步到 From_Account 在线终端和漫游上；2：消息不同步至 From_Account；
        reqParams.put("From_Account",paramsMap.get("fromAccount"));
        reqParams.put("To_Account",paramsMap.get("toAccount"));
        reqParams.put("MsgLifeTime",3600);//604800  上线前改回离线缓存七天  测试只缓存一小时
        reqParams.put("MsgRandom",Integer.parseInt(IDUtil.random(7)));
        reqParams.put("MsgTimeStamp",System.currentTimeMillis()/1000);

        //MsgBody
        Map<String, Object> msgBodyMap = new HashMap<String, Object>();
        msgBodyMap.put("MsgType","TIMTextElem");

        //MsgContent
        Map<String, Object> msgContentMap = new HashMap<String, Object>();
        msgContentMap.put("Text",paramsMap.get("msgContent"));
        msgBodyMap.put("MsgContent",msgContentMap);

        List MsgBody = new ArrayList<>();
        MsgBody.add(msgBodyMap);

        reqParams.put("MsgBody",MsgBody);

        String resultStr = null;

        logger.info(optName+",请求参数:"+FastJsonUtil.toJson(reqParams));
        resultStr = HttpClientUtil.postWithJSON(sendmsg_url, FastJsonUtil.toJson(reqParams));
        logger.info(optName+",返回值:"+resultStr);

        if(StringUtils.isEmpty(resultStr)){
            logger.info("请求"+optName+"返回为空");
            throw new BusinessException(RspCode.REQUEST_TIM_ERROR,RspCode.MSG.get(RspCode.REQUEST_TIM_ERROR) );
        }

        handlerResponseStr(resultStr,optName);
    }

    /**
     * 批量发单聊消息
     * */
    @Override
    public void batchSendMsg(Map<String, Object> paramsMap, ResultData result) throws BusinessException, Exception {
        String optName = "批量发单聊消息";
        ValidateUtil.validateParams(paramsMap,new String[]{"fromAccount","toAccount","msgContent"});
        String sendmsg_url =  this.getTimUrl(BATCHSENDMSG_URL,"admin");
        logger.info(optName+",请求地址:"+sendmsg_url);
        String identifier = paramsMap.get("identifier")+"";
        String[] toAccounts = paramsMap.get("toAccount").toString().split(",");
        List<String> toAccountList = Arrays.asList(toAccounts);

        Map<String, Object> reqParams = new HashMap<String, Object>();
        reqParams.put("SyncOtherMachine",2);//1：把消息同步到 From_Account 在线终端和漫游上；2：消息不同步至 From_Account；
        reqParams.put("From_Account",paramsMap.get("fromAccount"));
        reqParams.put("To_Account",toAccountList);
        reqParams.put("MsgRandom",Integer.parseInt(IDUtil.random(7)));

        //MsgBody
        Map<String, Object> msgBodyMap = new HashMap<String, Object>();
        msgBodyMap.put("MsgType","TIMTextElem");

        //MsgContent
        Map<String, Object> msgContentMap = new HashMap<String, Object>();
        msgContentMap.put("Text",paramsMap.get("msgContent"));
        msgBodyMap.put("MsgContent",msgContentMap);

        List MsgBody = new ArrayList<>();
        MsgBody.add(msgBodyMap);

        reqParams.put("MsgBody",MsgBody);

        String resultStr = null;

        logger.info(optName+",请求参数:"+FastJsonUtil.toJson(reqParams));
        resultStr = HttpClientUtil.postWithJSON(sendmsg_url, FastJsonUtil.toJson(reqParams));
        logger.info(optName+",返回值:"+resultStr);

        if(StringUtils.isEmpty(resultStr)){
            logger.info("请求"+optName+"返回为空");
            throw new BusinessException(RspCode.REQUEST_TIM_ERROR,RspCode.MSG.get(RspCode.REQUEST_TIM_ERROR) );
        }

        handlerResponseStr(resultStr,optName);

    }

    /**
     * 查询用户云通讯登录状态
     */
    @Override
    public void queryState(Map<String, Object> paramsMap, ResultData result) throws BusinessException, Exception {
        String optName = "查询用户云通讯登录状态接口";
        ValidateUtil.validateParams(paramsMap,new String[]{"to_Account"});
        String querystate_url =  this.getTimUrl(QUERYSTATE_URL,"admin");
        logger.info(optName+",请求地址:"+querystate_url);

        String to_Account = paramsMap.get("to_Account")+"";
        String[] accounts = to_Account.split(",");

        Map<String, Object> reqParams = new HashMap<String, Object>();
        String resultStr = null;

        reqParams.put("To_Account",Arrays.asList(accounts));

        logger.info(optName+",请求参数:"+FastJsonUtil.toJson(reqParams));
        resultStr = HttpClientUtil.postWithJSON(querystate_url, FastJsonUtil.toJson(reqParams));
        logger.info(optName+",返回值:"+resultStr);

        if(StringUtils.isEmpty(resultStr)){
            logger.info("请求"+optName+"返回为空");
            throw new BusinessException(RspCode.REQUEST_TIM_ERROR,RspCode.MSG.get(RspCode.REQUEST_TIM_ERROR) );
        }

        Map<String, Object> resultMap = handlerResponseStr(resultStr,optName);
        if(resultMap!=null){
            List<Map<String,String>> list = (List<Map<String,String>>) resultMap.get("QueryResult");
            result.setResultData(list);
        }
    }

    /**
     * 获取聊天历史记录
     */
    @Override
    public void getHistory(Map<String, Object> paramsMap, ResultData result) throws BusinessException, Exception {
        String optName = "获取聊天历史记录";
        ValidateUtil.validateParams(paramsMap,new String[]{"chatType","msgTime"});
        String get_history_url =  this.getTimUrl(GET_HISTORY_URL,"admin");
        logger.info(optName+",请求地址:"+get_history_url);

        Map<String, Object> reqParams = new HashMap<String, Object>();
        String resultStr = null;

        reqParams.put("ChatType",paramsMap.get("chatType"));
        reqParams.put("MsgTime",paramsMap.get("msgTime"));


        logger.info(optName+",请求参数:"+FastJsonUtil.toJson(reqParams));
        resultStr = HttpClientUtil.postWithJSON(get_history_url, FastJsonUtil.toJson(reqParams));
        logger.info(optName+",返回值:"+resultStr);

        if(StringUtils.isEmpty(resultStr)){
            logger.info("请求"+optName+"返回为空");
            throw new BusinessException(RspCode.REQUEST_TIM_ERROR,RspCode.MSG.get(RspCode.REQUEST_TIM_ERROR) );
        }

        Map<String, Object> resultMap = handlerResponseStr(resultStr,optName);
        if(resultMap!=null){
            List<Map<String,String>> list = (List<Map<String,String>>) resultMap.get("File");
            result.setResultData(list);
        }
    }

    /**
     *生成userSig
     */
    public String getUsig(String identifier) throws BusinessException {

        String usersig = null;
        String redisKey = null;
        boolean isAdmin = false;

        //先判断缓存中的值是否已经失效StringUtil.isBlank(identifier)
        if ("admin".equals(identifier)) {//如果没有传用户标志 获取的就是管理员的usersig
            if (redisUtil.getStr("usersig") != null) {
                usersig = redisUtil.getStr("usersig");
                return usersig;
            }
            identifier = PropertiesConfigUtil.getProperty("tencentyun.admin");
            redisKey = "usersig";
        } else {
            if (redisUtil.getStr("usersig_" + identifier) != null) {
                usersig = redisUtil.getStr("usersig_" + identifier);
                return usersig;
            }
            redisKey = "usersig_" + identifier;
        }

        //获取私钥
        String private_key_local_path = System.getProperty("mth.ops.member.root") + PropertiesConfigUtil.getProperty("tencentyun.private_key_local_path");
        FileInputStream inputStream = null;
        String private_key = null;
        try {
            inputStream = new FileInputStream(
                    new File(private_key_local_path));

            int len = 0;
            byte[] buff = new byte[1024];
            while ((len = inputStream.read(buff)) != -1) {
                private_key = new String(buff, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(RspCode.PRIVATE_KEY_ERROR, RspCode.MSG.get(RspCode.PRIVATE_KEY_ERROR));
        }
        logger.error("获取" + identifier + "的usersig");
        //生成usersig
        tls_sigature.GenTLSSignatureResult result1 = tls_sigature.GenTLSSignatureEx(Long.parseLong(PropertiesConfigUtil.getProperty("tencentyun.SdkAppId")), identifier, private_key);
        if (result1 != null) {
            usersig = result1.urlSig;
            redisUtil.setStr(redisKey, usersig, 15541);
        } else {
            logger.error("获取" + identifier + "的usersig失败");
            throw new BusinessException(RspCode.USERSIG_ERROR, RspCode.MSG.get(RspCode.USERSIG_ERROR));
        }

        if(StringUtil.isEmpty(usersig)){
            throw new BusinessException(RspCode.USERSIG_ERROR, RspCode.MSG.get(RspCode.USERSIG_ERROR));
        }

        return usersig;
    }

    /**
     * 通讯云请求路径,替换一些账号值
     * @return
     */
    public String getTimUrl(String url,String identifier) throws BusinessException{
        return url.replace("USERSIG",this.getUsig(identifier))
                .replace("IDENTIFIER",PropertiesConfigUtil.getProperty("tencentyun.admin"))
                .replace("SDKAPPID",PropertiesConfigUtil.getProperty("tencentyun.SdkAppId"))
                .replace("RANDOM", NumberUtils.getRandom32());
    }

    /**
     *云通讯常用返回值处理
     */
    public Map<String, Object> handlerResponseStr(String resultStr,String optName) throws BusinessException,Exception{
        Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
        if (((String) resultMap.get("ActionStatus")).equals(RspCode.TIM_RESPONSE_SUCC)) {
            Integer errCode = Integer.parseInt(resultMap.get("ErrorCode")+"");
            if(errCode!=0){
                List<String> FailAccounts = (List<String>) resultMap.get("FailAccounts");
                if(CollectionUtils.isNotEmpty(FailAccounts)){
                    logger.info(optName+"，部分失败用户:"+FailAccounts.toString());
                }
                throw new BusinessException(RspCode.REQUEST_TIM_ERROR, errCode+","+resultMap.get("ErrorInfo"));
            }
            logger.info(optName+"成功");
        }else if(((String) resultMap.get("ActionStatus")).equals(RspCode.TIM_RESPONSE_FAIL)){
            Integer errCode = Integer.parseInt(resultMap.get("ErrorCode")+"");
            if(errCode!=0){
                List<String> FailAccounts = (List<String>) resultMap.get("FailAccounts");
                if(CollectionUtils.isNotEmpty(FailAccounts)){
                    logger.info(optName+"，操作失败用户:"+FailAccounts.toString());
                }
                throw new BusinessException(RspCode.REQUEST_TIM_ERROR, errCode+","+resultMap.get("ErrorInfo"));
            }
        }else{
            throw new BusinessException(RspCode.REQUEST_TIM_ERROR, optName+"返回失败");
        }

        return resultMap;
    }


    /**
     *解析图片字段获取图片path_id
     */
    public String getPicUrlId(String picUrl) throws SystemException {

        if(StringUtils.isEmpty(picUrl)){
            return "";
        }


        String url = docUtil.imageUrlFind(picUrl);
        if(StringUtils.isEmpty(url)){
            return "";
        }
        logger.info("图片信息:"+url);
        return url;
    }

}
