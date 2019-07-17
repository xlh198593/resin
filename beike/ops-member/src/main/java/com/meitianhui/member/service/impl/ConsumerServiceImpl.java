package com.meitianhui.member.service.impl;

import com.meitianhui.common.constant.CommonConstant;
import com.meitianhui.common.constant.CommonRspCode;
import com.meitianhui.common.constant.ResultData;
import com.meitianhui.common.exception.BusinessException;
import com.meitianhui.common.exception.SystemException;
import com.meitianhui.common.util.*;
import com.meitianhui.member.constant.Constant;
import com.meitianhui.member.constant.RspCode;
import com.meitianhui.member.dao.*;
import com.meitianhui.member.entity.*;
import com.meitianhui.member.service.ConsumerService;
import com.meitianhui.member.service.MemberService;
import com.meitianhui.member.util.HiCryptUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 消费者管理
 *
 * @author tiny
 * @ClassName: ConsumerServiceImpl
 * @date 2017年2月23日 下午3:17:55
 */
@SuppressWarnings("unchecked")
@Service
public class ConsumerServiceImpl implements ConsumerService {

    private static final Logger logger = Logger.getLogger(ConsumerServiceImpl.class);
    @Autowired
    public RedisUtil redisUtil;
    @Autowired
    private DocUtil docUtil;
    @Autowired
    public MemberDao memberDao;
    @Autowired
    public ConsumerDao consumerDao;
    @Autowired
    public ConsumerSignDao consumerSignDao;
    @Autowired
    public FavoriteStoreDao favoriteStoreDao;
    @Autowired
    public MemberService memberService;
    @Autowired
    public StoresDao storesDao;
    @Autowired
    public MdAppVersionDao mdAppVersionDao;
    @Autowired
    public MemberDistributionDao memberDistributionDao;
    @Autowired
    public MemberDistrbutionInfoDao memberDistrbutionInfoDao;
    @Autowired
    public MemberInvitationCodeDao memberInvitationCodeDao;
    @Autowired
    public HongbaoActivityDao hongbaoActivityDao;

    /**
     * 查询消费者信息
     *
     * @param paramsMap
     * @param result
     * @throws Exception
     */
    @Override
    public void findConsumerById(Map<String, Object> paramsMap, ResultData result) throws Exception {
        ValidateUtil.validateParams(paramsMap, new String[]{"consumer_id"});
        MDConsumer mDConsumer = consumerDao.selectMDConsumerBaseInfo(paramsMap);
        if (null == mDConsumer) {
            throw new BusinessException(RspCode.MEMBER_NOT_EXIST, "消费者信息不存在");
        }

        result.setResultData(mDConsumer);
    }

    @Override
    public void handleConsumerSync(Map<String, Object> paramsMap, ResultData result)
            throws BusinessException, SystemException, Exception {
        try {
            ValidateUtil.validateParams(paramsMap,
                    new String[]{"user_id", "member_id", "mobile", "registered_date", "status"});
            String member_id = paramsMap.get("member_id") + "";
            String user_id = paramsMap.get("user_id") + "";
            String type = StringUtil.formatStr(paramsMap.get("type"));
            String mobile = StringUtil.formatStr(paramsMap.get("mobile"));
            Map<String, Object> tempMap = new HashMap<String, Object>();
            boolean flag = false;
            if (StringUtil.equals(type, "app") || StringUtil.equals(type, "account")) {
                String invite_code = StringUtil.formatStr(paramsMap.get("invite_code"));
                //从表里面 查询出member_id  然后进行关联
                if (StringUtil.isNotBlank(invite_code)) {
                    tempMap.clear();
                    tempMap.put("invite_code", invite_code);
                    MDMemberInvitationCode invitationCode = memberInvitationCodeDao.findMemberInviteCode(tempMap);
                    if (null == invitationCode) {
                        throw new BusinessException(RspCode.INVITE_CODE_ERROR, RspCode.MSG.get(RspCode.INVITE_CODE_ERROR));
                    }
                    tempMap.clear();
                    tempMap.put("member_id", invitationCode.getMemberId());
                    List<MDConsumer> consumerList = consumerDao.selectMDConsumer(tempMap);
                    if (consumerList.size() == 0) {
                        throw new BusinessException(RspCode.RECOMMEND_NOT_EXIST, RspCode.MSG.get(RspCode.RECOMMEND_NOT_EXIST));
                    }
                    MDConsumer consumerEntity = consumerList.get(0);
                    //判断时间是否过期
                    if (consumerEntity.getType() == 4) {
                        throw new BusinessException(RspCode.RECOMMEND_INVITE_CODE_NOT_EXIST, RspCode.MSG.get(RspCode.RECOMMEND_INVITE_CODE_NOT_EXIST));
                    }
                    Integer memberType = consumerEntity.getType();
                    if (null != memberType && memberType == 1) {
                        //通过查询 info 表里面 的数据量 来进行
                        tempMap.clear();
                        tempMap.put("parent_id", invitationCode.getMemberId());
                        Integer count = memberDistributionDao.findMemberDistrCount(tempMap);
                        if (count < 100) {
                            flag = true;
                        }
                    }
                    //写入分销
                    this.memberDistrbutionEdit(member_id, invitationCode.getMemberId(), flag);
                }
            }
            //分销逻辑,绑定推荐人的关系
            if (StringUtil.equals(type, "h5")) {
                String distribution = StringUtil.formatStr(paramsMap.get("distributionId"));
                Map<String, Object> paramMap = new HashMap<String, Object>();
                paramMap.put("member_id", distribution);
                List<MDConsumer> distrConsumerList = consumerDao.selectMDConsumer(paramMap);
                MDConsumer distrConsumer = distrConsumerList.get(0);
                if (null == distrConsumer) {
                    throw new BusinessException(RspCode.RECOMMEND_NOT_EXIST, RspCode.MSG.get(RspCode.RECOMMEND_NOT_EXIST));
                }
                //判断时间是否过期
                if (distrConsumer.getType() == 4) {
                    throw new BusinessException(RspCode.RECOMMEND_INVITE_CODE_NOT_EXIST, RspCode.MSG.get(RspCode.RECOMMEND_INVITE_CODE_NOT_EXIST));
                }
                Integer memberType = distrConsumer.getType();
                if (null != memberType && memberType == 1) {
                    //通过查询 info 表里面 的数据量 来进行
                    tempMap.clear();
                    tempMap.put("parent_id", distribution);
                    Integer count = memberDistributionDao.findMemberDistrCount(tempMap);
                    if (count < 100) {
                        flag = true;
                    }
                }
                this.memberDistrbutionEdit(member_id, distribution);
            }
            tempMap.clear();
            tempMap.put("mobile", mobile);
            MDConsumer mDConsumer = consumerDao.selectMDConsumerById(tempMap);
            if (null == mDConsumer) {
                // 生成消费者会员id
                mDConsumer = new MDConsumer();
                Date date = new Date();
                BeanConvertUtil.mapToBean(mDConsumer, paramsMap);
                mDConsumer.setConsumer_id(member_id);
                mDConsumer.setSex_key(Constant.SEX_03);
                mDConsumer.setCreated_date(date);
                mDConsumer.setModified_date(date);
                mDConsumer.setIs_vip_expired(0);
                mDConsumer.setType(3);
                if (flag == true) {
                    String date2Str = DateUtil.date2Str(new Date(), DateUtil.fmt_yyyyMMdd);
                    date2Str = DateUtil.addDate(date2Str, DateUtil.fmt_yyyyMMdd, 1, 1);
                    Date vipEnddate = DateUtil.str2Date(date2Str, DateUtil.fmt_yyyyMMdd);
                    mDConsumer.setGrowth_value(68);
                    mDConsumer.setVip_start_time(new Date());
                    mDConsumer.setVip_end_time(vipEnddate);
                    mDConsumer.setType(2);
                }
                consumerDao.insertMDConsumer(mDConsumer);
                Map<String, Object> logMap = new HashMap<String, Object>();
                logMap.put("log_id", IDUtil.getUUID());
                logMap.put("consumer_id", member_id);
                logMap.put("category", "数据同步");
                logMap.put("tracked_date", new Date());
                logMap.put("event", "消费者同步注册");
                consumerDao.insertMDConsumerLog(logMap);
                // 删除原有的关系
                tempMap.clear();
                tempMap.put("member_id", member_id);
                tempMap.put("member_type_key", CommonConstant.MEMBER_TYPE_CONSUMER);
                memberService.userMemberRelRemove(tempMap);
                // 重新设置关系
                MDUserMember mDUserMember = new MDUserMember();
                mDUserMember.setMember_id(member_id);
                mDUserMember.setUser_id(user_id);
                mDUserMember.setMember_type_key(CommonConstant.MEMBER_TYPE_CONSUMER);
                mDUserMember.setIs_admin("Y");
                memberDao.insertMDUserMember(mDUserMember);

            } else {
                throw new BusinessException(RspCode.MOBILE_EXIST, RspCode.MSG.get(RspCode.MOBILE_EXIST));
            }
            if (flag == true) {
                //赠送一张礼券
                this.handleFreeMemberGift(member_id);
                //插入会员贝壳号表
                this.memberInviteCodeSave(member_id);
                Map<String, Object> paramMap = new HashMap<String, Object>();
                paramMap.put("type", 1);
                result.setResultData(paramMap);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (SystemException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    //免费会员送一张礼券
    public void handleFreeMemberGift(String memberId) {
        try {
            String service_url = PropertiesConfigUtil.getProperty("finance_service_url");
            Map<String, String> requestData = new HashMap<>();
            requestData.put("service", "finance.consumer.editMemberCoupon");
            Map<String, Object> params = new LinkedHashMap<>();
            params.put("member_id", memberId);
            params.put("is_New", "N");
            requestData.put("params", FastJsonUtil.toJson(params));
            String result = HttpClientUtil.post(service_url, requestData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //外部用户送 12张礼券 赠送贝壳
    public void handleMemberGift(String memberId) {
        try {
            String service_url = PropertiesConfigUtil.getProperty("finance_service_url");
            Map<String, String> requestData = new HashMap<>();
            requestData.put("service", "finance.consumer.editMemberCoupon");
            Map<String, Object> params = new LinkedHashMap<>();
            params.put("member_id", memberId);
            params.put("is_New", "Y");
            requestData.put("params", FastJsonUtil.toJson(params));
            String result = HttpClientUtil.post(service_url, requestData);
            Map<String, Object> resultMapData = FastJsonUtil.jsonToMap(result);
            String rsp_code = (String) resultMapData.get("rsp_code");
            if (!rsp_code.equals(RspCode.RESPONSE_SUCC)) {
                throw new BusinessException((String) resultMapData.get("error_code"), (String) resultMapData.get("error_msg"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * (来赏会员)绑定分销关系
     *
     * @param memberId 会员id
     * @param mobile   手机号
     */
    public void memberDistrbutionAdd(String memberId, String mobile) throws Exception {
        // 直邀会员id
        String parent_id = PropertiesConfigUtil.getProperty("laishang_direct_member");
        // 间邀会员id
        String grand_id = PropertiesConfigUtil.getProperty("laishang_indirect_member");
        Map<String, Object> distrMap = new HashMap<>();
        distrMap.put("member_id", memberId);
        MDMemberDistribution memberDistribution = memberDistributionDao.getMemberDistributionInfo(distrMap);
        MDMemberDistrbutionInfo entity = memberDistrbutionInfoDao.findMemberDistrInfo(distrMap);
        if (null == entity) {
            entity = new MDMemberDistrbutionInfo();
            entity.setParentId(parent_id);
            entity.setMemberId(memberId);
            entity.setCreateTime(new Date());
            entity.setStatus(1);
            memberDistrbutionInfoDao.insert(entity);
        } else {
            entity.setParentId(parent_id);
            entity.setCreateTime(new Date());
            entity.setStatus(1);
            memberDistrbutionInfoDao.update(entity);
        }
        // 推荐人之前没有绑定过分销会员
        if (null == memberDistribution) {
            // 写入分销逻辑
            memberDistribution = new MDMemberDistribution();
            memberDistribution.setMemberId(memberId);
            memberDistribution.setParentId(parent_id);
            memberDistribution.setGrandId(grand_id);
            memberDistribution.setCreateTime(new Date());
            memberDistribution.setDistrLevel(2);
            memberDistribution.setRegistLevel(2);
            memberDistribution.setStatus(1);
            memberDistributionDao.insert(memberDistribution);

            BigDecimal receiveMoney = new BigDecimal("120");
            BigDecimal grandMoney = new BigDecimal("60");
            String type = "recharge";
            String direct = "直邀奖励";
            String indirect = "间邀奖励";
            if (StringUtil.isNotBlank(parent_id)) {
                Map<String, Object> tempMdMap = new HashMap<>();
                tempMdMap.put("consumer_id", parent_id);
                MDConsumer parentInfo = consumerDao.selectMDConsumerBaseInfo(tempMdMap);
                this.handlerConsumerReceiveMoney(parent_id, receiveMoney, mobile, parentInfo.getMobile(), type, direct);
            }
            if (StringUtil.isNotBlank(grand_id)) {
                Map<String, Object> tempMdMap = new HashMap<>();
                tempMdMap.put("consumer_id", grand_id);
                MDConsumer grandInfo = consumerDao.selectMDConsumerBaseInfo(tempMdMap);
                this.handlerConsumerReceiveMoney(grand_id, grandMoney, mobile, grandInfo.getMobile(), type, indirect);
            }
        }
    }

    /**
     * 绑定临时分销关系
     *
     * @param memberId
     * @param parentId
     * @throws Exception
     */
    public void memberDistrbutionEdit(String memberId, String parentId) throws Exception {
        Map<String, Object> tempMap = new HashMap<String, Object>();
        tempMap.put("member_id", memberId);
        MDMemberDistrbutionInfo entity = memberDistrbutionInfoDao.findMemberDistrInfo(tempMap);
        if (null == entity) {
            entity = new MDMemberDistrbutionInfo();
            entity.setParentId(parentId);
            entity.setMemberId(memberId);
            entity.setCreateTime(new Date());
            entity.setStatus(0);
            memberDistrbutionInfoDao.insert(entity);
        } else {
            entity.setParentId(parentId);
            entity.setCreateTime(new Date());
            memberDistrbutionInfoDao.update(entity);
        }
    }


    /**
     * 免费用户，绑定分销关系，需要同时写入memberDistrbution表
     *
     * @param memberId
     * @param parentId
     * @throws Exception
     */
    public void memberDistrbutionEdit(String memberId, String parentId, boolean flag) throws Exception {
        Map<String, Object> tempMap = new HashMap<String, Object>();
        tempMap.put("member_id", memberId);
        MDMemberDistrbutionInfo entity = memberDistrbutionInfoDao.findMemberDistrInfo(tempMap);
        if (null == entity) {
            entity = new MDMemberDistrbutionInfo();
            entity.setParentId(parentId);
            entity.setMemberId(memberId);
            entity.setCreateTime(new Date());
            entity.setStatus(0);
            memberDistrbutionInfoDao.insert(entity);
        } else {
            entity.setParentId(parentId);
            entity.setCreateTime(new Date());
            memberDistrbutionInfoDao.update(entity);
        }
        if (flag == true) {
            //手动添加的，是普通会员  二级
            this.buildingMemberDistrbution(memberId, parentId);
        }
    }

    /**
     * 免费邀请的会员 生成贝壳号
     *
     * @param member_id
     * @throws Exception
     */
    public void memberInviteCodeSave(String member_id) throws Exception {
        Map<String, Object> tempMap = new HashMap<String, Object>();
        tempMap.put("member_id", member_id);
        MDMemberInvitationCode entity = memberInvitationCodeDao.findMemberInviteCode(tempMap);
        if (null == entity) {
            entity = new MDMemberInvitationCode();
            String inviteCode = IDUtil.generateCode(5);
            tempMap.clear();
            tempMap.put("invite_code", inviteCode);
            MDMemberInvitationCode inviteCodeEntity = memberInvitationCodeDao.findMemberInviteCode(tempMap);
            if (null != inviteCodeEntity) {
                inviteCode = IDUtil.generateCode(5);
            }
            entity.setInviteCode(inviteCode);
            entity.setMemberId(member_id);
            entity.setStatus("1");
            memberInvitationCodeDao.addMemberInvitationCode(entity);
        }
    }


    public void memberDistrEdit(String member_id, String invite_code) throws BusinessException, SystemException, Exception {
        Map<String, Object> distrMap = new HashMap<String, Object>();
        distrMap.clear();
        distrMap.put("member_id", member_id);
        List<MDConsumer> distrConsumerList = consumerDao.selectMDConsumer(distrMap);
        MDConsumer distrConsumer = distrConsumerList.get(0);
        if (null == distrConsumer.getVip_start_time() && null == distrConsumer.getVip_end_time()) {
            distrMap.clear();
            distrMap.put("invite_code", invite_code);
            MDMemberInvitationCode memberCode = memberInvitationCodeDao.findMemberInviteCode(distrMap);
            if (null != memberCode) {
                distrMap.clear();
                distrMap.put("member_id", member_id);
                MDConsumer consumer = consumerDao.selectMDConsumerById(distrMap);
                if (null != consumer) {
                    if (consumer.getType() == 4) {
                        throw new BusinessException(RspCode.RECOMMEND_INVITE_CODE_NOT_EXIST, RspCode.MSG.get(RspCode.RECOMMEND_INVITE_CODE_NOT_EXIST));
                    } else {
                        MDMemberDistrbutionInfo userDistribution = memberDistrbutionInfoDao.findMemberDistrInfo(distrMap);
                        if (null != userDistribution) {
                            userDistribution.setParentId(memberCode.getMemberId());
                            userDistribution.setCreateTime(new Date());
                            memberDistrbutionInfoDao.update(userDistribution);
                        } else {
                            userDistribution = new MDMemberDistrbutionInfo();
                            userDistribution.setMemberId(member_id);
                            userDistribution.setParentId(memberCode.getMemberId());
                            userDistribution.setCreateTime(new Date());
                            memberDistrbutionInfoDao.insert(userDistribution);
                        }
                    }
                } else {
                    throw new BusinessException(RspCode.INVITE_CODE_ERROR, "该贝壳号已过期，请重新输入");
                }
            } else {
                throw new BusinessException(RspCode.INVITE_CODE_ERROR, RspCode.MSG.get(RspCode.INVITE_CODE_ERROR));
            }
        } else {
            throw new BusinessException(RspCode.CONSUMER_MEMBER_ERROR, "已经是会员，不能通过贝壳号登录更改绑定关系");
        }
    }


    /**
     * 只有第一次才能进入这个分销逻辑，如果已经和别人绑定了关系，就不会进来这个方法
     *
     * @param member_id
     * @param distribution
     */
    public int buildingMemberDistrbution(String member_id, String distribution)
            throws BusinessException, SystemException, Exception {
        Map<String, Object> distrMap = new HashMap<String, Object>();
        distrMap.clear();
        distrMap.put("member_id", member_id);
        MDMemberDistribution userDistribution = memberDistributionDao.getMemberDistributionInfo(distrMap);
        if (null == userDistribution) {
            userDistribution = new MDMemberDistribution();
            // 默认所在的分销是二级分销
            userDistribution.setDistrLevel(2);
            if (StringUtils.isNotEmpty(distribution) == true) {
                distrMap.clear();
                distrMap.put("member_id", distribution);
                MDMemberDistribution memberDistribution = memberDistributionDao.getMemberDistributionInfo(distrMap);
                // 推荐人之前没有绑定过分销会员
                if (null == memberDistribution) {
                    // 写入分销逻辑
                    memberDistribution = new MDMemberDistribution();
                    memberDistribution.setMemberId(distribution);
                    memberDistribution.setCreateTime(new Date());
                    memberDistribution.setDistrLevel(2);
                    memberDistribution.setRegistLevel(2);
                    memberDistribution.setStatus(1);
                    memberDistributionDao.insert(memberDistribution);
                }
                // 设置父级ID
                userDistribution.setParentId(distribution);

                // 设置爷爷级ID
                if (StringUtils.isNotEmpty(memberDistribution.getParentId()) == true) {
                    userDistribution.setGrandId(memberDistribution.getParentId());
                }

                if (memberDistribution.getDistrLevel() == 3) {
                    userDistribution.setDistrLevel(3);
                    // 设置顶层ID,三级分销链路才有顶层ID,顶层也是掌柜
                    if (StringUtils.isNotEmpty(memberDistribution.getTopId()) == true) {
                        distrMap.clear();
                        distrMap.put("member_id", memberDistribution.getGrandId());
                        MDMemberDistribution grandDistribution = memberDistributionDao.getMemberDistributionInfo(distrMap);
                        if (grandDistribution.getRegistLevel() == 3) {
                            userDistribution.setTopId(memberDistribution.getGrandId());
                            userDistribution.setManagerId(memberDistribution.getTopId());
                            // 设置最终掌柜id
                            String managerId = StringUtil.formatStr(memberDistribution.getManagerId());
                            if (StringUtils.isNotEmpty(managerId)) {
                                userDistribution.setGeneralId(managerId);
                            }
                        } else {
                            userDistribution.setTopId(memberDistribution.getTopId());
                            // 设置掌柜id
                            String managerId = StringUtil.formatStr(memberDistribution.getManagerId());
                            if (StringUtils.isNotEmpty(managerId)) {
                                userDistribution.setManagerId(managerId);
                            }
                            // 设置最后关联的掌柜id
                            String generalId = StringUtil.formatStr(memberDistribution.getGeneralId());
                            if (StringUtils.isNotEmpty(generalId)) {
                                userDistribution.setGeneralId(generalId);
                            }
                        }
                    } else {
                        String grandId = StringUtil.formatStr(memberDistribution.getGrandId());
                        if (StringUtils.isNotEmpty(grandId)) {
                            distrMap.clear();
                            distrMap.put("member_id", grandId);
                            MDMemberDistribution grandDistribution = memberDistributionDao.getMemberDistributionInfo(distrMap);
                            if (null != grandDistribution && null != grandDistribution.getRegistLevel()
                                    && grandDistribution.getRegistLevel() == 3) {
                                userDistribution.setTopId(grandId);
                            }
                        }
                    }
                }
            }
            userDistribution.setRegistLevel(2);
            userDistribution.setMemberId(member_id);
            userDistribution.setCreateTime(new Date());
            userDistribution.setStatus(1);
            return memberDistributionDao.insert(userDistribution);
        } else {
            throw new BusinessException(RspCode.USER_MEMBER_ERROR, "已经和其他人绑定分销关系");
        }

    }

    @Override
    public void buildingMemberDistrbution(Map<String, Object> paramsMap, ResultData result)
            throws BusinessException, SystemException, Exception {
        ValidateUtil.validateParams(paramsMap, new String[]{"member_id"});
        //  写入会员绑定关系
        this.memberDistrInfoEdit(paramsMap);
    }

    public void insertMemberDisrtInfo(MDMemberDistrbutionInfo entity) throws Exception {
        memberDistrbutionInfoDao.insert(entity);
    }

    @Override
    public void consumerLoginValidate(Map<String, Object> paramsMap, ResultData result)
            throws BusinessException, SystemException, Exception {
        try {
            ValidateUtil.validateParams(paramsMap, new String[]{"user_id"});
            paramsMap.put("member_type_key", Constant.MEMBER_TYPE_CONSUMER);
            List<MDUserMember> mDUserMemberList = memberDao.selectMDUserMember(paramsMap);
            if (mDUserMemberList.size() == 0) {
                throw new BusinessException(RspCode.MEMBER_NOT_EXIST, RspCode.MSG.get(RspCode.MEMBER_NOT_EXIST));
            }

            String invite_code = StringUtil.formatStr(paramsMap.get("invite_code"));
            MDUserMember mDUserMember = mDUserMemberList.get(0);
            // 验证会员状态
            Map<String, Object> tempMap = new HashMap<String, Object>();
            tempMap.put("consumer_id", mDUserMember.getMember_id());
            List<MDConsumer> consumerList = consumerDao.selectMDConsumer(tempMap);
            if (consumerList.size() == 0) {
                throw new BusinessException(RspCode.MEMBER_NOT_EXIST, RspCode.MSG.get(RspCode.MEMBER_NOT_EXIST));
            }
            MDConsumer consumer = consumerList.get(0);
            //判断是否是分销会员
            if (StringUtils.isNotEmpty(invite_code)) {
                this.memberDistrEdit(mDUserMember.getMember_id(), invite_code);
            }
            String status = consumer.getStatus();
            if (!status.equals(Constant.STATUS_NORMAL)) {
                throw new BusinessException(RspCode.MEMBER_STATUS_ERROR, "会员被禁用");
            }
            //查询是否是掌柜。
            tempMap.clear();
            tempMap.put("member_id", mDUserMember.getMember_id());
            MDMemberDistribution entity = memberDistributionDao.getMemberDistributionInfo(tempMap);
            Map<String, Object> resultMap = new HashMap<String, Object>();
            if ((null != entity && null != entity.getRegistLevel() && entity.getRegistLevel() == 3) &&
                    (null != entity && null != entity.getDistrLevel() && entity.getDistrLevel() == 3)) {
                resultMap.put("is_manager", "Y");
            } else {
                resultMap.put("is_manager", "N");
            }
            resultMap.put("member_id", mDUserMember.getMember_id());
            result.setResultData(resultMap);
        } catch (BusinessException e) {
            throw e;
        } catch (SystemException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void consumerEdit(Map<String, Object> paramsMap, ResultData result)
            throws BusinessException, SystemException, Exception {
        try {
            ValidateUtil.validateParams(paramsMap, new String[]{"member_id"});
            if (paramsMap.size() == 1) {
                throw new BusinessException(RspCode.SYSTEM_NO_PARAMS_UPDATE,
                        RspCode.MSG.get(RspCode.SYSTEM_NO_PARAMS_UPDATE));
            }
            List<MDConsumer> mDConsumerList = consumerDao.selectMDConsumer(paramsMap);
            if (mDConsumerList.size() == 0) {
                throw new BusinessException(RspCode.MEMBER_NOT_EXIST, RspCode.MSG.get(RspCode.MEMBER_NOT_EXIST));
            }
            MDConsumer mdConsumer = mDConsumerList.get(0);
            Date vip_end_time = DateUtil.parseToDate(DateUtil.date2Str(mdConsumer.getVip_end_time(), DateUtil.fmt_yyyyMMdd));
            Date nowDate = DateUtil.parseToDate(DateUtil.date2Str(new Date(), DateUtil.fmt_yyyyMMdd));
            if (vip_end_time != null && nowDate.getTime() < vip_end_time.getTime()) {
                int growth_value = 0;
                String head_pic_path = StringUtil.formatStr(paramsMap.get("head_pic_path"));
                if (StringUtil.isNotBlank(head_pic_path) && mdConsumer.getHead_pic_path() == null) {
                    growth_value = growth_value + 10;
                }
                String nick_name = StringUtil.formatStr(paramsMap.get("nick_name"));
                if (StringUtil.isNotBlank(nick_name) && mdConsumer.getNick_name() == null) {
                    growth_value = growth_value + 10;
                }
                String sex_key = StringUtil.formatStr(paramsMap.get("sex_key"));
                if (StringUtil.isNotBlank(sex_key) && mdConsumer.getSex_key() == null) {
                    growth_value = growth_value + 10;
                }
                if (paramsMap.get("birthday") != null && mdConsumer.getBirthday() == null) {
                    growth_value = growth_value + 10;
                }
                if (growth_value > 0) {
                    paramsMap.put("growth_value", growth_value);
                }
            }
            consumerDao.updateMDConsumer(paramsMap);
        } catch (BusinessException e) {
            throw e;
        } catch (SystemException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void consumerFind(Map<String, Object> paramsMap, ResultData result)
            throws BusinessException, SystemException, Exception {
        try {
            if (StringUtil.isBlank(paramsMap.get("member_id") + "")) {
                result.setResultData(null);
                return;
            }
            List<MDConsumer> mDConsumerList = consumerDao.selectMDConsumer(paramsMap);
            if (mDConsumerList.size() == 0) {
                throw new BusinessException(RspCode.MEMBER_NOT_EXIST, RspCode.MSG.get(RspCode.MEMBER_NOT_EXIST));
            }
            MDConsumer mDConsumer = mDConsumerList.get(0);
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("type", mDConsumer.getType());
            resultMap.put("nick_name", StringUtil.formatStr(mDConsumer.getNick_name()));
            resultMap.put("sex_key", StringUtil.formatStr(mDConsumer.getSex_key()));
            resultMap.put("birthday", mDConsumer.getBirthday() == null ? ""
                    : DateUtil.date2Str(mDConsumer.getBirthday(), DateUtil.fmt_yyyyMMdd));
            resultMap.put("full_name", StringUtil.formatStr(mDConsumer.getFull_name()));
            resultMap.put("mobile", StringUtil.formatStr(mDConsumer.getMobile()));
            String head_pic_path = StringUtil.formatStr(mDConsumer.getHead_pic_path());
            resultMap.put("head_pic_path", head_pic_path);
            List<String> url_list = new ArrayList<String>();
            url_list.add(head_pic_path);
            resultMap.put("doc_url", docUtil.imageUrlFind(url_list));
            resultMap.put("address", StringUtil.formatStr(mDConsumer.getAddress()));
            resultMap.put("marital_status_key", StringUtil.formatStr(mDConsumer.getMarital_status_key()));
            resultMap.put("montly_income", StringUtil.formatStr(mDConsumer.getMontly_income()));
            resultMap.put("id_card", StringUtil.formatStr(mDConsumer.getId_card()));
            resultMap.put("education", StringUtil.formatStr(mDConsumer.getEducation()));
            resultMap.put("industry", StringUtil.formatStr(mDConsumer.getIndustry()));
            resultMap.put("home_circle", StringUtil.formatStr(mDConsumer.getHome_circle()));
            resultMap.put("home_circle_address", StringUtil.formatStr(mDConsumer.getHome_circle_address()));
            resultMap.put("work_circle", StringUtil.formatStr(mDConsumer.getWork_circle()));
            resultMap.put("work_circle_address", StringUtil.formatStr(mDConsumer.getWork_circle_address()));
            resultMap.put("life_circle", StringUtil.formatStr(mDConsumer.getLife_circle()));
            resultMap.put("life_circle_address", StringUtil.formatStr(mDConsumer.getLife_circle_address()));
            resultMap.put("hobby_circle", StringUtil.formatStr(mDConsumer.getHobby_circle()));
            resultMap.put("grade", mDConsumer.getGrade().intValue() + "");
            resultMap.put("registered_date",
                    DateUtil.date2Str(mDConsumer.getRegistered_date(), DateUtil.fmt_yyyyMMddHHmmss));
            resultMap.put("area_id", StringUtil.formatStr(mDConsumer.getArea_id()));
            resultMap.put("area_desc", StringUtil.formatStr(mDConsumer.getArea_desc()));
            if (mDConsumer.getVip_end_time() != null) {
                long vip_end_time = DateUtil
                        .parseToDate(DateUtil.date2Str(mDConsumer.getVip_end_time(), DateUtil.fmt_yyyyMMdd)).getTime();
                long nowDate = DateUtil.parseToDate(DateUtil.date2Str(new Date(), DateUtil.fmt_yyyyMMdd)).getTime();
                if (vip_end_time >= nowDate) {
                    resultMap.put("vip_end_time",
                            DateUtil.date2Str(mDConsumer.getVip_end_time(), DateUtil.fmt_yyyyMMdd));
                    resultMap.put("is_vip", "Y");
                    Integer type = mDConsumer.getType();
                    if (type == 1 || type == 2) {
                        resultMap.put("is_closedBeta", "Y");// 内测会员
                    }
                } else {
                    resultMap.put("vip_end_time", "");
                    resultMap.put("is_vip", "N");
                }
            } else {
                resultMap.put("vip_end_time", "");
                resultMap.put("is_vip", "New");
            }

            resultMap.put("level", mDConsumer.getLevel());
            resultMap.put("growth_value", mDConsumer.getGrowth_value());
            //查询微信openid
            String user_service_url = PropertiesConfigUtil.getProperty("user_service_url");
            Map<String, String> requestData = new HashMap<String, String>();
            requestData.put("service", "infrastructure.userWechatFindByMobile");
            Map<String, String> params = new HashMap<String, String>();
            params.put("mobile", StringUtil.formatStr(mDConsumer.getMobile()));
            requestData.put("params", FastJsonUtil.toJson(params));
            String resultStr = HttpClientUtil.post(user_service_url, requestData);
            Map<String, Object> resultMapData = FastJsonUtil.jsonToMap(resultStr);

            String rsp_code = (String) resultMapData.get("rsp_code");
            if (!rsp_code.equals(RspCode.RESPONSE_SUCC)) {
                throw new BusinessException((String) resultMapData.get("error_code"), (String) resultMapData.get("error_msg"));
            }
            Map<String, Object> dataMap = (Map<String, Object>) resultMapData.get("data");
            if (null != dataMap && !dataMap.isEmpty()) {
                resultMap.put("openid", dataMap.get("openid"));
            }

            //**********************************
            // 作者：丁忍  注释：查询推荐人信息
            //**********************************
			/*Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("member_id", paramsMap.get("member_id"));
			Map<String, Object> tempBindingRecommendMap = new HashMap<String, Object>();
			tempBindingRecommendMap = consumerDao.selectUserRecommend(tempMap);
			if(tempBindingRecommendMap != null){
				resultMap.put("reference_mobile", tempBindingRecommendMap.get("reference_mobile")+"");
			}else{
				resultMap.put("reference_mobile","");
			}*/

            result.setResultData(resultMap);
        } catch (BusinessException e) {
            throw e;
        } catch (SystemException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void consumerListFind(Map<String, Object> paramsMap, ResultData result)
            throws BusinessException, SystemException, Exception {
        try {
            List<MDConsumer> mDConsumerList = consumerDao.selectMDConsumerList(paramsMap);
            List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
            for (MDConsumer mDConsumer : mDConsumerList) {
                Map<String, Object> resultMap = new HashMap<String, Object>();
                resultMap.put("consumer_id", StringUtil.formatStr(mDConsumer.getConsumer_id()));
                resultMap.put("nick_name", StringUtil.formatStr(mDConsumer.getNick_name()));
                resultMap.put("full_name", StringUtil.formatStr(mDConsumer.getFull_name()));
                resultMap.put("mobile", StringUtil.formatStr(mDConsumer.getMobile()));
                resultMap.put("grade", StringUtil.formatStr(mDConsumer.getGrade()));
                resultMap.put("area_id", StringUtil.formatStr(mDConsumer.getArea_id()));
                resultMap.put("area_desc", StringUtil.formatStr(mDConsumer.getArea_desc()));
                resultMap.put("address", StringUtil.formatStr(mDConsumer.getAddress()));
                resultMap.put("registered_date",
                        DateUtil.date2Str(mDConsumer.getRegistered_date(), DateUtil.fmt_yyyyMMddHHmmss));
                resultList.add(resultMap);
            }
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("list", resultList);
            result.setResultData(map);
        } catch (BusinessException e) {
            throw e;
        } catch (SystemException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void consumerLevelFind(Map<String, Object> paramsMap, ResultData result)
            throws BusinessException, SystemException, Exception {
        ValidateUtil.validateParams(paramsMap, new String[]{"member_id"});
        List<MDConsumer> mDConsumerList = consumerDao.selectMDConsumer(paramsMap);
        if (mDConsumerList.size() == 0) {
            throw new BusinessException(RspCode.MEMBER_NOT_EXIST, RspCode.MSG.get(RspCode.MEMBER_NOT_EXIST));
        }
        MDConsumer mDConsumer = mDConsumerList.get(0);
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("grade", mDConsumer.getGrade());
        result.setResultData(resultMap);
    }

    @Override
    public void consumerAddressFind(Map<String, Object> paramsMap, ResultData result)
            throws BusinessException, SystemException, Exception {
        try {
            ValidateUtil.validateParams(paramsMap, new String[]{"consumer_id"});
            List<Map<String, Object>> mDConsumerAddressList = consumerDao.selectMDConsumerAddress(paramsMap);
            for (Map<String, Object> addressMap : mDConsumerAddressList) {
                addressMap.put("created_date",
                        DateUtil.date2Str((Date) addressMap.get("created_date"), DateUtil.fmt_yyyyMMddHHmmss));
            }
            result.setResultData(mDConsumerAddressList);
        } catch (BusinessException e) {
            throw e;
        } catch (SystemException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void consumerAddressCreate(Map<String, Object> paramsMap, ResultData result)
            throws BusinessException, SystemException, Exception {
        try {
            ValidateUtil.validateParams(paramsMap,
                    new String[]{"consumer_id", "area_id", "address", "consignee", "mobile", "is_major_addr"});
            MDConsumerAddress mDConsumerAddress = new MDConsumerAddress();
            BeanConvertUtil.mapToBean(mDConsumerAddress, paramsMap);
            mDConsumerAddress.setAddress_id(IDUtil.getUUID());
            mDConsumerAddress.setCreated_date(new Date());
            consumerDao.insertMDConsumerAddress(mDConsumerAddress);
            Map<String, Object> resultMap = new HashMap<String, Object>();
            resultMap.put("address_id", mDConsumerAddress.getAddress_id());
            result.setResultData(resultMap);
        } catch (BusinessException e) {
            throw e;
        } catch (SystemException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void consumerAddressEdit(Map<String, Object> paramsMap, ResultData result)
            throws BusinessException, SystemException, Exception {
        try {
            ValidateUtil.validateParams(paramsMap, new String[]{"address_id", "consumer_id"});
            String is_major_addr = StringUtil.formatStr(paramsMap.get("is_major_addr"));
            Map<String, Object> tempMap = new HashMap<String, Object>();
            // 如果是修改默认地址,先将所有的地址设置为非默认
            if (is_major_addr.equals("Y")) {
                tempMap.clear();
                tempMap.put("consumer_id", paramsMap.get("consumer_id"));
                tempMap.put("is_major_addr", "N");
                consumerDao.updateMDConsumerAddress(tempMap);
            }
            consumerDao.updateMDConsumerAddress(paramsMap);
            // 更新消费者表中地址字段
            tempMap.clear();
            tempMap.put("is_major_addr", is_major_addr);
            tempMap.put("address_id", paramsMap.get("address_id"));
            tempMap.put("consumer_id", paramsMap.get("consumer_id"));
            List<Map<String, Object>> mDConsumerAddressList = consumerDao.selectMDConsumerAddress(tempMap);
            if (mDConsumerAddressList.size() > 0) {
                Map<String, Object> addressMap = mDConsumerAddressList.get(0);
                String area_id = addressMap.get("area_id") + "";
                String address = addressMap.get("address") + "";
                tempMap.clear();
                tempMap.put("address", address);
                tempMap.put("area_id", area_id);
                tempMap.put("consumer_id", paramsMap.get("consumer_id"));
                consumerDao.updateMDConsumer(tempMap);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (SystemException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void handleConsumerAddressRemove(Map<String, Object> paramsMap, ResultData result)
            throws BusinessException, SystemException, Exception {
        try {
            ValidateUtil.validateParams(paramsMap, new String[]{"address_id"});
            consumerDao.deleteMDConsumerAddress(paramsMap);
        } catch (BusinessException e) {
            throw e;
        } catch (SystemException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void consumerSign(Map<String, Object> paramsMap, ResultData result)
            throws BusinessException, SystemException, Exception {
        RedisLock redisLock = null;
        try {
            String[] gole_poll = new String[]{"10", "20", "50"};
            ValidateUtil.validateParams(paramsMap, new String[]{"consumer_id"});
            String consumer_id = paramsMap.get("consumer_id") + "";
            String sign_in_date = DateUtil.getFormatDate(DateUtil.fmt_yyyyMMdd);
            String lockKey = "[consumerSign]_" + consumer_id + sign_in_date;
            // redis 锁30秒超时时间
            redisLock = new RedisLock(redisUtil, lockKey, 30 * 1000);
            redisLock.lock();
            Map<String, Object> tempMap = new HashMap<String, Object>();
            tempMap.put("consumer_id", consumer_id);
            tempMap.put("sign_in_date", sign_in_date);
            MDConsumerSign mdConsumerSign = consumerSignDao.selectMDConsumerSign(tempMap);
            String title = "一天只能签到一次哦";
            String url = "";
            String path_url = "";
            // 未签到
            if (null == mdConsumerSign) {
                int index = RandomUtils.nextInt(gole_poll.length);
                String gold = gole_poll[index];
                title = "签到成功,+" + gold + "金币";
                String sign_in_id = IDUtil.getUUID();
                mdConsumerSign = new MDConsumerSign();
                mdConsumerSign.setSign_in_id(sign_in_id);
                mdConsumerSign.setConsumer_id(consumer_id);
                mdConsumerSign.setSign_in_date(sign_in_date);
                mdConsumerSign.setCategory("gold");
                mdConsumerSign.setRemark("签到系统赠送" + gold + "金币");
                consumerSignDao.insertMDConsumerSign(mdConsumerSign);
                // 签到送5金币
                Map<String, Object> out_trade_body = new HashMap<String, Object>();
                out_trade_body.put("sign_in_id", sign_in_id);
                out_trade_body.put("sign_date", sign_in_date);
                memberService.balancePay(Constant.MEMBER_ID_MTH, consumer_id, "ZFFS_08", new BigDecimal(gold),
                        sign_in_id, "签到系统赠送", out_trade_body);
            }
            // 获取图片地址
            String goods_service_url = PropertiesConfigUtil.getProperty("goods_service_url");
            Map<String, String> reqParams = new HashMap<String, String>();
            Map<String, Object> bizParams = new HashMap<String, Object>();
            bizParams.put("category", "c_app_sign");
            reqParams.put("service", "gdAppAdvert.app.gdAppAdvertFind");
            reqParams.put("params", FastJsonUtil.toJson(bizParams));
            String resultStr = HttpClientUtil.postShort(goods_service_url, reqParams);
            Map<String, Object> resultMap = FastJsonUtil.jsonToMap(resultStr);
            if (!((String) resultMap.get("rsp_code")).equals(RspCode.RESPONSE_SUCC)) {
                throw new BusinessException((String) resultMap.get("error_code"), (String) resultMap.get("error_msg"));
            }
            Map<String, Object> data = (Map<String, Object>) resultMap.get("data");
            Map<String, Object> docUrl = (Map<String, Object>) data.get("doc_url");
            List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("list");
            if (list.size() > 0) {
                Map<String, Object> gdAppAdvertMap = list.get(0);
                url = StringUtil.formatStr(gdAppAdvertMap.get("url"));
                String path_id = StringUtil.formatStr(gdAppAdvertMap.get("path_id"));
                path_url = docUrl.get(path_id) + "";
            }
            Map<String, Object> resultDataMap = new HashMap<String, Object>();
            resultDataMap.put("title", title);
            resultDataMap.put("path_url", path_url);
            resultDataMap.put("url", url);
            result.setResultData(resultDataMap);
        } catch (BusinessException e) {
            throw e;
        } catch (SystemException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        } finally {
            if (redisLock != null) {
                redisLock.unlock();
            }
        }
    }

    @Override
    public void consumerStatusEdit(Map<String, Object> paramsMap, ResultData result)
            throws BusinessException, SystemException, Exception {
        try {
            ValidateUtil.validateParams(paramsMap, new String[]{"consumer_id", "status"});
            String status = paramsMap.get("status") + "";
            Map<String, String> reqParams = new HashMap<String, String>();
            Map<String, Object> bizParams = new HashMap<String, Object>();
            bizParams.put("consumer_id", paramsMap.get("consumer_id"));
            bizParams.put("status", status);
            consumerDao.updateMDConsumer(bizParams);
            // 状态如果是禁用的话，则删除用户登陆的授权信息
            if (status.equals(Constant.STATUS_DISABLED)) {
                bizParams.clear();
                bizParams.put("member_id", paramsMap.get("consumer_id"));
                bizParams.put("member_type_key", Constant.MEMBER_TYPE_CONSUMER);
                List<MDUserMember> userMemberList = memberDao.selectMDUserMember(bizParams);
                String user_service_url = PropertiesConfigUtil.getProperty("user_service_url");
                for (MDUserMember um : userMemberList) {
                    // 删除授权token信息
                    reqParams.clear();
                    bizParams.clear();
                    reqParams.put("service", "infrastructure.userTokenClear");
                    bizParams.put("member_id", um.getMember_id());
                    bizParams.put("user_id", um.getUser_id());
                    reqParams.put("params", FastJsonUtil.toJson(bizParams));
                    HttpClientUtil.postShort(user_service_url, reqParams);
                }
            }
        } catch (BusinessException e) {
            throw e;
        } catch (SystemException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void consumerBaseInfoFind(Map<String, Object> paramsMap, ResultData result)
            throws BusinessException, SystemException, Exception {
        try {
            ValidateUtil.validateParams(paramsMap, new String[]{"consumer_id"});
            MDConsumer mDConsumer = consumerDao.selectMDConsumerBaseInfo(paramsMap);
            if (null == mDConsumer) {
                throw new BusinessException(RspCode.MEMBER_NOT_EXIST, "消费者信息不存在");
            }
            Map<String, Object> resultMap = new HashMap<String, Object>();
            resultMap.put("consumer_id", StringUtil.formatStr(mDConsumer.getConsumer_id()));
            resultMap.put("nick_name", StringUtil.formatStr(mDConsumer.getNick_name()));
            resultMap.put("mobile", StringUtil.formatStr(mDConsumer.getMobile()));
            resultMap.put("head_pic_path", StringUtil.formatStr(mDConsumer.getHead_pic_path()));
            resultMap.put("grade", StringUtil.formatStr(mDConsumer.getGrade()));
            result.setResultData(resultMap);
        } catch (BusinessException e) {
            throw e;
        } catch (SystemException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void favoriteStore(Map<String, Object> paramsMap, ResultData result)
            throws BusinessException, SystemException, Exception {
        try {
            ValidateUtil.validateParams(paramsMap, new String[]{"consumer_id", "stores_id"});
            String is_llm_stores = StringUtil.formatStr(paramsMap.get("is_llm_stores"));
            Map<String, Object> tempMap = new HashMap<String, Object>();
            tempMap.put("consumer_id", paramsMap.get("consumer_id"));
            tempMap.put("stores_id", paramsMap.get("stores_id"));
            MDFavoriteStore favoriteStore = favoriteStoreDao.selectMDFavoriteStore(tempMap);
            if (favoriteStore == null) {
                favoriteStore = new MDFavoriteStore();
                BeanConvertUtil.mapToBean(favoriteStore, paramsMap);
                favoriteStore.setCreated_date(new Date());
                if (StringUtils.isEmpty(is_llm_stores)) {
                    favoriteStore.setIs_llm_stores("N");
                } else {
                    if (is_llm_stores.equals("Y")) {
                        // 设置其他门店为非默认
                        tempMap.clear();
                        tempMap.put("consumer_id", paramsMap.get("consumer_id"));
                        tempMap.put("is_llm_stores", "N");
                        favoriteStoreDao.updateMDFavoriteStore(tempMap);
                    }
                }
                favoriteStoreDao.insertMDFavoriteStore(favoriteStore);
            } else {
                if (is_llm_stores.equals("Y")) {
                    favoriteStoreEdit(paramsMap, result);
                }
            }
        } catch (BusinessException e) {
            throw e;
        } catch (SystemException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void favoriteStoreEdit(Map<String, Object> paramsMap, ResultData result)
            throws BusinessException, SystemException, Exception {
        try {
            ValidateUtil.validateParams(paramsMap, new String[]{"consumer_id", "stores_id", "is_llm_stores"});
            String is_llm_stores = paramsMap.get("is_llm_stores") + "";
            if (is_llm_stores.equals("Y")) {
                //设置如果消费者第一次设置默认门店,将不需要判断绑定30天
                List<Map<String, Object>> list = favoriteStoreDao.selectMDFavoriteStoreByIsLlmStores(paramsMap);
                if (list != null && list.size() != 0) {
                    paramsMap.put("time", "falg");
                }
                // 设置其他门店为非默认
                Map<String, Object> tempMap = new HashMap<String, Object>();
                tempMap.put("consumer_id", paramsMap.get("consumer_id"));
                tempMap.put("is_llm_stores", "N");
                favoriteStoreDao.updateMDFavoriteStore(tempMap);
            }
            paramsMap.put("created_date", new Date());
            int falg = favoriteStoreDao.updateMDFavoriteStore(paramsMap);
            if (falg == 0) {
                throw new BusinessException("取消默认门店失败", "取消默认门店失败,必须与该门店绑定30工作日,才可以进行解绑");
            }
        } catch (BusinessException e) {
            throw e;
        } catch (SystemException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void favoriteStoreCancel(Map<String, Object> paramsMap, ResultData result)
            throws BusinessException, SystemException, Exception {
        try {
            ValidateUtil.validateParams(paramsMap, new String[]{"consumer_id", "stores_id"});
            String stores_id = StringUtil.formatStr(paramsMap.get("stores_id"));
            if (!StringUtils.isEmpty(stores_id)) {
                List<String> list = StringUtil.str2List(stores_id, ",");
                if (list.size() > 1) {
                    paramsMap.remove("stores_id");
                    paramsMap.put("stores_id_in", list);
                }
            }
            int flag = favoriteStoreDao.deleteMDFavoriteStore(paramsMap);
            if (flag == 0) {
                throw new BusinessException("取消收藏门店失败", "取消门店收藏失败,必须与该门店绑定30工作日,才可以进行解绑");
            }
        } catch (BusinessException e) {
            throw e;
        } catch (SystemException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void favoriteStoreList(Map<String, Object> paramsMap, ResultData result)
            throws BusinessException, SystemException, Exception {
        try {
            ValidateUtil.validateParams(paramsMap, new String[]{"consumer_id"});
            List<Map<String, Object>> resultlist = new ArrayList<Map<String, Object>>();
            List<Map<String, Object>> list = favoriteStoreDao.selectMDFavoriteStoreList(paramsMap);
            List<String> doc_ids = new ArrayList<String>();
            for (Map<String, Object> map : list) {
                Map<String, Object> tempMap = new HashMap<String, Object>();
                tempMap.put("stores_id", map.get("stores_id"));
                tempMap.put("stores_name", map.get("stores_name"));
                tempMap.put("address", map.get("address") + "");
                tempMap.put("is_llm_stores", map.get("is_llm_stores") + "");
                // 如果街景为空,则设置街景为门头图
                String neighbor_pic_path = StringUtil.formatStr(map.get("neighbor_pic_path"));
                if (neighbor_pic_path.equals("")) {
                    List<String> tempList = new ArrayList<String>();
                    String new_facade_pic_path = StringUtil.formatStr(map.get("new_facade_pic_path"));
                    if (!new_facade_pic_path.equals("")) {
                        tempList.addAll(StringUtil.str2List(new_facade_pic_path, "\\|"));
                    }
                    String new_stores_pic_path = StringUtil.formatStr(map.get("new_stores_pic_path"));
                    if (!new_stores_pic_path.equals("")) {
                        tempList.addAll(StringUtil.str2List(new_stores_pic_path, "\\|"));
                    }
                    if (tempList.size() > 0) {
                        neighbor_pic_path = tempList.get(0);
                    }
                }
                tempMap.put("longitude", StringUtil.formatStr(map.get("longitude")));
                tempMap.put("latitude", StringUtil.formatStr(map.get("latitude")));
                tempMap.put("neighbor_pic_path", neighbor_pic_path);
                doc_ids.addAll(StringUtil.str2List(neighbor_pic_path, ","));
                resultlist.add(tempMap);
            }
            Map<String, Object> resultDate = new HashMap<String, Object>();
            resultDate.put("list", resultlist);
            resultDate.put("doc_url", docUtil.imageUrlFind(doc_ids));
            result.setResultData(resultDate);
        } catch (BusinessException e) {
            throw e;
        } catch (SystemException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void favoriteStoreListPage(Map<String, Object> paramsMap, ResultData result)
            throws BusinessException, SystemException, Exception {
        try {
            String consumer_mobile = (String) paramsMap.get("consumer_mobile");
            String store_mobile = (String) paramsMap.get("store_mobile");

            if (!StringUtil.isEmpty(consumer_mobile) && !RegexpValidateUtil.isPhone(consumer_mobile)) {
                throw new BusinessException(CommonRspCode.SYSTEM_BEAN_TO_MAP_ERROR, "会员手机号码格式错误");
            }


            if (!StringUtil.isEmpty(store_mobile) && !RegexpValidateUtil.isPhone(store_mobile)) {
                throw new BusinessException(CommonRspCode.SYSTEM_BEAN_TO_MAP_ERROR, "店东手机号码格式错误");
            }


            List<Map<String, Object>> resultlist = new ArrayList<Map<String, Object>>();
            List<Map<String, Object>> list = favoriteStoreDao.selectMDFavoriteStoreListPage(paramsMap);
            if (list != null && list.size() > 0) {
                for (Map<String, Object> map : list) {
                    Map<String, Object> tempMap = new HashMap<String, Object>();
                    tempMap.put("consumer_id", map.get("consumer_id"));
                    tempMap.put("stores_id", map.get("stores_id"));

                    tempMap.put("stores_name", map.get("stores_name"));
                    tempMap.put("store_person", map.get("store_person"));
                    tempMap.put("store_mobile", map.get("store_mobile"));
                    tempMap.put("consumer_mobile", map.get("consumer_mobile"));
                    tempMap.put("store_address", map.get("store_address"));
                    tempMap.put("favorite_status", map.get("favorite_status"));
                    tempMap.put("created_date", map.get("created_date"));
                    resultlist.add(tempMap);
                }
            }
            Map<String, Object> resultDate = new HashMap<String, Object>();
            resultDate.put("list", resultlist);
            result.setResultData(resultDate);
        } catch (BusinessException e) {
            throw e;
        } catch (SystemException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void bingStoreLogListPage(Map<String, Object> paramsMap, ResultData result)
            throws BusinessException, SystemException, Exception {
        try {
            String consumer_mobile = (String) paramsMap.get("consumer_mobile");
            String store_mobile = (String) paramsMap.get("store_mobile");

            if (!StringUtil.isEmpty(consumer_mobile) && !RegexpValidateUtil.isPhone(consumer_mobile)) {
                throw new BusinessException(CommonRspCode.SYSTEM_BEAN_TO_MAP_ERROR, "会员手机号码格式错误");
            }


            if (!StringUtil.isEmpty(store_mobile) && !RegexpValidateUtil.isPhone(store_mobile)) {
                throw new BusinessException(CommonRspCode.SYSTEM_BEAN_TO_MAP_ERROR, "店东手机号码格式错误");
            }


            List<Map<String, Object>> resultlist = new ArrayList<Map<String, Object>>();
            List<Map<String, Object>> list = favoriteStoreDao.selectBingStoreLogListPage(paramsMap);
            if (list != null && list.size() > 0) {
                for (Map<String, Object> map : list) {
                    Map<String, Object> tempMap = new HashMap<String, Object>();
                    tempMap.put("consumer_id", map.get("consumer_id"));
                    tempMap.put("stores_id", map.get("stores_id"));
                    tempMap.put("stores_mobile", map.get("stores_mobile"));
                    tempMap.put("consumer_mobile", map.get("consumer_mobile"));

                    tempMap.put("operator", map.get("operator"));
                    tempMap.put("remark", map.get("remark"));
                    tempMap.put("create_date", map.get("create_date"));
                    resultlist.add(tempMap);
                }
            }
            Map<String, Object> resultDate = new HashMap<String, Object>();
            resultDate.put("list", resultlist);
            result.setResultData(resultDate);
        } catch (BusinessException e) {
            throw e;
        } catch (SystemException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    @Transactional
    public void handleBingStore(Map<String, Object> paramsMap, ResultData result)
            throws BusinessException, SystemException, Exception {

        try {

            ValidateUtil.validateParams(paramsMap, new String[]{"consumer_mobile", "store_mobile", "bing_type"});
            String consumer_mobile = (String) paramsMap.get("consumer_mobile");
            String store_mobile = (String) paramsMap.get("store_mobile");
            String bing_type = (String) paramsMap.get("bing_type");
            int bing_type_int = Integer.parseInt(bing_type);

            if (!RegexpValidateUtil.isPhone(consumer_mobile)) {
                throw new BusinessException(CommonRspCode.SYSTEM_BEAN_TO_MAP_ERROR, "会员手机号码格式错误");
            }

            if (!RegexpValidateUtil.isPhone(store_mobile)) {
                throw new BusinessException(CommonRspCode.SYSTEM_BEAN_TO_MAP_ERROR, "店东手机号码格式错误");
            }

            if (!RegexpValidateUtil.isNumber(bing_type)) {
                throw new BusinessException(CommonRspCode.SYSTEM_BEAN_TO_MAP_ERROR, "更新类型错误");
            }

            if (bing_type_int != 1 && bing_type_int != 2) {
                throw new BusinessException(CommonRspCode.SYSTEM_BEAN_TO_MAP_ERROR, "更新类型参数非法");
            }

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("mobile", consumer_mobile);
            List<MDConsumer> consumerList = consumerDao.selectMDConsumerList(map);
            map = new HashMap<String, Object>();
            map.put("contact_tel", store_mobile);

            if (paramsMap.get("store_id_select") != null && !StringUtil.isEmpty(paramsMap.get("store_id_select").toString())) {
                map.put("store_id", paramsMap.get("store_id_select"));
            }
            MDStores mdStores = storesDao.selectMDStoresForDefault(map);


            if (consumerList == null || consumerList.size() == 0) {
                throw new BusinessException(CommonRspCode.SYSTEM_BEAN_TO_MAP_ERROR, "该会员不存在！");
            }

            if (mdStores == null) {
                throw new BusinessException(CommonRspCode.SYSTEM_BEAN_TO_MAP_ERROR, "该门店不存在！");
            }
            List<Map<String, Object>> list = favoriteStoreDao.selectMDFavoriteStoreListPage(paramsMap);

            if (bing_type_int == 1) {  //新增
                if (list != null && list.size() > 0) {
                    throw new BusinessException(CommonRspCode.SYSTEM_BEAN_TO_MAP_ERROR, "该会员已经收藏了该门店，不能新增绑定！你可以根据会员手机号码查询出店东，进行绑定！");
                }

                //将该会员所有的门店设为非默认
                Map<String, Object> map2 = new HashMap<String, Object>();
                map2.put("consumer_id", consumerList.get(0).getConsumer_id());
                map2.put("is_llm_stores", "N");
                favoriteStoreDao.updateMDFavoriteStore(map2);


                //将该会员绑定的门店新增为默认门店
                MDFavoriteStore favoriteStore = new MDFavoriteStore();
                favoriteStore.setConsumer_id(consumerList.get(0).getConsumer_id());
                favoriteStore.setStores_id(mdStores.getStores_id());
                favoriteStore.setIs_llm_stores("Y");
                favoriteStore.setCreated_date(new Date());
                favoriteStore.setRemark(paramsMap.get("operator").toString());
                favoriteStoreDao.insertMDFavoriteStore(favoriteStore);


                MDFavoriteStoreLog favoriteStoreLog = new MDFavoriteStoreLog();
                favoriteStoreLog.setConsumer_id(consumerList.get(0).getConsumer_id());
                favoriteStoreLog.setConsumer_mobile(consumer_mobile);
                favoriteStoreLog.setStores_id(mdStores.getStores_id());
                favoriteStoreLog.setStores_mobile(store_mobile);
                favoriteStoreLog.setOperator(paramsMap.get("operator").toString());
                favoriteStoreLog.setRemark(paramsMap.get("operator").toString() + "将会员（" + consumer_mobile + "）新增店东（" + store_mobile + "）为默认门店");
                favoriteStoreDao.insertBingStoreLog(favoriteStoreLog);

            } else if (bing_type_int == 2) {  //修改
                if (list == null || list.size() == 0) {
                    throw new BusinessException(CommonRspCode.SYSTEM_BEAN_TO_MAP_ERROR, "该会员还没有收藏该门店，不能绑定店东！你可以新增绑定！");
                }

                //将该会员所有的门店设为非默认
                Map<String, Object> map2 = new HashMap<String, Object>();
                map2.put("consumer_id", consumerList.get(0).getConsumer_id());
                map2.put("is_llm_stores", "N");
                favoriteStoreDao.updateMDFavoriteStore(map2);

                //将该会员对应绑定的门店更新为默认门店
                map2.put("is_llm_stores", "Y");
                map2.put("stores_id", mdStores.getStores_id());
                map2.put("created_date", new Date());
                map2.put("remark", paramsMap.get("operator").toString());
                favoriteStoreDao.updateMDFavoriteStore(map2);


                MDFavoriteStoreLog favoriteStoreLog = new MDFavoriteStoreLog();
                favoriteStoreLog.setConsumer_id(consumerList.get(0).getConsumer_id());
                favoriteStoreLog.setConsumer_mobile(consumer_mobile);
                favoriteStoreLog.setStores_id(mdStores.getStores_id());
                favoriteStoreLog.setStores_mobile(store_mobile);
                favoriteStoreLog.setOperator(paramsMap.get("operator").toString());
                favoriteStoreLog.setRemark(paramsMap.get("operator").toString() + "将会员（" + consumer_mobile + "）修改店东（" + store_mobile + "）为默认门店");
                favoriteStoreDao.insertBingStoreLog(favoriteStoreLog);
            }

        } catch (BusinessException e) {
            throw e;
        } catch (SystemException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }

    }

    @Override
    public void defaultStore(Map<String, Object> paramsMap, ResultData result)
            throws BusinessException, SystemException, Exception {
        try {
            Map<String, Object> tempMap = new HashMap<String, Object>();
            tempMap.put("contact_tel", "13713720725");
            MDStores s = storesDao.selectMDStoresForDefault(tempMap);
            tempMap.clear();
            tempMap.put("stores_name", s.getStores_name());
            tempMap.put("stores_id", s.getStores_id());
            result.setResultData(tempMap);
        } catch (BusinessException e) {
            throw e;
        } catch (SystemException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void userRecommendCreate(Map<String, Object> paramsMap, ResultData result)
            throws BusinessException, SystemException, Exception {
        try {
            ValidateUtil.validateParams(paramsMap, new String[]{"recommend_mobile", "member_id", "member_mobile"});
            if (paramsMap.get("recommend_mobile").equals(paramsMap.get("member_mobile"))) {
                throw new BusinessException(RspCode.RECOMMEND_NOT_OWN, "不能设定自己为推荐人");
            }
            Map<String, Object> tempMap = new HashMap<String, Object>();
            tempMap.put("mobile", paramsMap.get("recommend_mobile"));
            List<MDConsumer> resultlist = new ArrayList<MDConsumer>();
            resultlist = consumerDao.selectMDConsumer(tempMap);
            if (resultlist.size() < 1) {
                throw new BusinessException(RspCode.RECOMMEND_NOT_EXIST, "推荐人信息不存在");
            }
            tempMap.clear();
            tempMap.put("member_id", paramsMap.get("member_id"));
            Map<String, Object> resoultMap = consumerDao.selectUserRecommend(tempMap);
            if (resoultMap != null) {
                throw new BusinessException(RspCode.RECOMMEND_EXIST, "您已经存在推荐人");
            }


            MDConsumer mdConsumer = resultlist.get(0);
            tempMap.clear();
            tempMap.put("recommend_id", IDUtil.getUUID());
            tempMap.put("reference_type_key", "consumer");
            tempMap.put("reference_mobile", paramsMap.get("recommend_mobile"));
            tempMap.put("reference_id", mdConsumer.getConsumer_id());
            tempMap.put("member_id", paramsMap.get("member_id"));
            tempMap.put("member_mobile", paramsMap.get("member_mobile"));
            tempMap.put("member_type_key", "consumer");
            tempMap.put("data_source", "SJLY_01");
            tempMap.put("reference_type", "领有惠APP");
            tempMap.put("created_date", new Date());
            tempMap.put("remark", "");
            consumerDao.insertUserRecommend(tempMap);
        } catch (BusinessException e) {
            throw e;
        } catch (SystemException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void userRecommendFind(Map<String, Object> paramsMap, ResultData result)
            throws BusinessException, SystemException, Exception {
        try {
            Map<String, Object> tempMap = new HashMap<String, Object>();
            tempMap.put("member_id", paramsMap.get("member_id"));
            Map<String, Object> tempBindingRecommendMap = new HashMap<String, Object>();
            tempBindingRecommendMap = consumerDao.selectUserRecommend(tempMap);
            result.setResultData(tempBindingRecommendMap);
        } catch (BusinessException e) {
            throw e;
        } catch (SystemException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 修改用户临时绑定的邀请码
     */
    @Override
    public void modifyInvitationCode(Map<String, Object> paramsMap, ResultData result) throws Exception {
        ValidateUtil.validateParams(paramsMap, new String[]{"member_id", "invite_code"});
        String memberId = paramsMap.get("member_id") + "";
        Map<String, Object> tempMap = new HashMap<>();
        String invite_code = StringUtil.formatStr(paramsMap.get("invite_code"));

        //判断该用户是否已存在推荐人
        tempMap.clear();
        tempMap.put("member_id", memberId);
        Map<String, Object> resoultMap = consumerDao.selectUserRecommend(tempMap);
        if (resoultMap != null) {
            throw new BusinessException(RspCode.RECOMMEND_EXIST, RspCode.MSG.get(RspCode.RECOMMEND_EXIST));
        }

        //通过查找用户邀请码记录判断推荐人是否存在
        tempMap.clear();
        tempMap.put("invite_code", invite_code);
        MDMemberInvitationCode invitationCode = memberInvitationCodeDao.findMemberInviteCode(tempMap);
        if (null == invitationCode) {
            throw new BusinessException(RspCode.INVITE_CODE_ERROR, RspCode.MSG.get(RspCode.INVITE_CODE_ERROR));
        }

        //获取推荐人会员信息
        tempMap.clear();
        tempMap.put("member_id", invitationCode.getMemberId());
        List<MDConsumer> consumerList = consumerDao.selectMDConsumer(tempMap);
        if (consumerList.size() == 0) {
            throw new BusinessException(RspCode.RECOMMEND_NOT_EXIST, RspCode.MSG.get(RspCode.RECOMMEND_NOT_EXIST));
        }

        //查询用户临时绑定关系记录
        tempMap.clear();
        tempMap.put("member_id", memberId);
        MDMemberDistrbutionInfo entity = memberDistrbutionInfoDao.findMemberDistrInfo(tempMap);
        if (entity == null) {
            throw new BusinessException(RspCode.SYSTEM_SERVICE_ERROR, "绑定记录不见了");
        }

        //推荐人不能是自己
        if (invitationCode.getMemberId().equals(memberId)) {
            throw new BusinessException(RspCode.RECOMMEND_NOT_OWN, RspCode.MSG.get(RspCode.RECOMMEND_NOT_OWN));
        }

        //修改推荐人和之前的推荐人一样
        if (entity.getParentId() != null && entity.getParentId().equals(invitationCode.getMemberId())) {
            throw new BusinessException(RspCode.RECOMMEND_NOT_OLD, RspCode.MSG.get(RspCode.RECOMMEND_NOT_OLD));
        }

        //修改临时推荐人
        entity.setParentId(invitationCode.getMemberId());
        memberDistrbutionInfoDao.update(entity);

    }


    /**
     * 查询领有惠app版本
     *
     * @param paramsMap
     * @param result
     * @throws BusinessException
     * @throws SystemException
     * @throws Exception
     */
    @Override
    public void appVersionFind(Map<String, Object> paramsMap, ResultData result)
            throws BusinessException, SystemException, Exception {
        try {
            ValidateUtil.validateParams(paramsMap, new String[]{"app_type", "version_no"});

            String appTypeStr = paramsMap.get("app_type").toString();
            if (!RegexpValidateUtil.isNumber(appTypeStr)) {
                throw new BusinessException(CommonRspCode.SYSTEM_BEAN_TO_MAP_ERROR, "参数app_type格式非法");
            }
            Integer app_type = Integer.parseInt(appTypeStr);
            if (app_type != 1 && app_type != 2) {
                throw new BusinessException(CommonRspCode.SYSTEM_BEAN_TO_MAP_ERROR, "参数app_type非法参数");
            }

            MdAppVersion mdAppVersion = mdAppVersionDao.selectMdAppVersionBy(app_type, paramsMap.get("version_no").toString());
            MdAppVersion lasterMdAppVersion = mdAppVersionDao.selectLasterMdAppVersion(paramsMap);
            Map<String, Object> map = new HashMap<String, Object>();

            if (mdAppVersion == null) {  //查无记录，则返回最新的一条记录给前端
                if (lasterMdAppVersion == null) {
                    map.put("is_update", 0);
                    return;
                }
                map.put("is_update", 1);  //是否需要更新 1需要更新，0不需要更新
                map.put("version_name", lasterMdAppVersion.getVersion_name());
                map.put("app_type", lasterMdAppVersion.getApp_type());
                map.put("version_no", lasterMdAppVersion.getVersion_no());
                //更新类型，1普通更新，2强制更新
                map.put("update_type", lasterMdAppVersion.getUpdate_type());
                //更新状态，1开启，2关闭
                map.put("update_status", lasterMdAppVersion.getUpdate_status());
                map.put("update_content", lasterMdAppVersion.getUpdate_content());
                map.put("download_path", lasterMdAppVersion.getDownload_path());
                result.setResultData(map);
                return;
            }

            //最新版本的版本值大于app当前的版本值，则需要进行更新
            if (lasterMdAppVersion.getVersion_value() > mdAppVersion.getVersion_value()) {
                map.put("is_update", 1);  //是否需要更新 1需要更新，0不需要更新
                map.put("version_name", lasterMdAppVersion.getVersion_name());
                map.put("app_type", lasterMdAppVersion.getApp_type());
                map.put("version_no", lasterMdAppVersion.getVersion_no());
                //更新类型，1普通更新，2强制更新
                map.put("update_type", lasterMdAppVersion.getUpdate_type());
                //更新状态，1开启，2关闭
                map.put("update_status", lasterMdAppVersion.getUpdate_status());
                map.put("update_content", lasterMdAppVersion.getUpdate_content());
                map.put("download_path", lasterMdAppVersion.getDownload_path());
                result.setResultData(map);
            } else {
                map.put("is_update", 0);  //是否需要更新 1需要更新，0不需要更新
                map.put("version_name", mdAppVersion.getVersion_name());
                map.put("app_type", mdAppVersion.getApp_type());
                map.put("version_no", mdAppVersion.getVersion_no());
                //更新类型，1普通更新，2强制更新
                map.put("update_type", mdAppVersion.getUpdate_type());
                //更新状态，1开启，2关闭
                map.put("update_status", mdAppVersion.getUpdate_status());
                map.put("update_content", lasterMdAppVersion.getUpdate_content());
                map.put("download_path", lasterMdAppVersion.getDownload_path());
                result.setResultData(map);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (SystemException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }

    }


    /**
     * 分页查询app版本列表
     *
     * @param paramsMap
     * @param result
     * @throws BusinessException
     * @throws SystemException
     * @throws Exception
     */
    @Override
    public void appversionPageListFind(Map<String, Object> paramsMap, ResultData result)
            throws BusinessException, SystemException, Exception {

        List<Map<String, Object>> list = mdAppVersionDao.selectMdAppVersionPageList(paramsMap);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("list", list);
        result.setResultData(map);

    }


    /**
     * 新增app版本
     *
     * @param paramsMap
     * @param result
     * @throws BusinessException
     * @throws SystemException
     * @throws Exception
     */
    @Transactional
    public void saveAppversion(Map<String, Object> paramsMap, ResultData result)
            throws BusinessException, SystemException, Exception {
        try {

            validateAppVersionParams(paramsMap, new String[]{"version_name", "app_type", "version_no", "update_status", "update_type"});

            Integer app_type = Integer.parseInt(paramsMap.get("app_type").toString());
            Integer update_status = Integer.parseInt(paramsMap.get("update_status").toString());
            MdAppVersion mdAppVersion = mdAppVersionDao.selectMdAppVersionBy(app_type, paramsMap.get("version_no").toString());
            if (mdAppVersion != null) {
                throw new BusinessException(CommonRspCode.USER_EXIST, "该版本号已存在");
            }

            MdAppVersion lasterMdAppVersion = mdAppVersionDao.selectLasterMdAppVersion(paramsMap);

            MdAppVersion appVersion = new MdAppVersion();
            appVersion.setApp_type(app_type);
            appVersion.setVersion_no(paramsMap.get("version_no").toString());
            appVersion.setVersion_name(paramsMap.get("version_name").toString());
            appVersion.setCreate_time(new Date());
            appVersion.setUpdate_status(update_status);
            appVersion.setUpdate_type(paramsMap.get("update_type").toString());
            appVersion.setVersion_value(lasterMdAppVersion != null ? generateValue(lasterMdAppVersion.getVersion_value()) : 1);
            if (app_type == 2) {
                appVersion.setUpdate_content(paramsMap.get("update_content").toString());
                appVersion.setDownload_path(paramsMap.get("download_path").toString());
            }
            mdAppVersionDao.insertMdAppVersion(appVersion);
        } catch (BusinessException e) {
            throw e;
        } catch (SystemException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    public void updateApversionStatus(Map<String, Object> paramsMap, ResultData result)
            throws BusinessException, SystemException, Exception {
        try {

            ValidateUtil.validateParams(paramsMap, new String[]{"id", "update_status"});
            String updateStatusStr = paramsMap.get("update_status").toString();

            if (!RegexpValidateUtil.isNumber(updateStatusStr)) {
                throw new BusinessException(CommonRspCode.SYSTEM_BEAN_TO_MAP_ERROR, "参数update_status格式非法");
            }

            Integer id = Integer.parseInt(paramsMap.get("id").toString());
            Integer update_status = Integer.parseInt(paramsMap.get("update_status").toString());
            mdAppVersionDao.updateUpdateStatusById(update_status, id);
        } catch (BusinessException e) {
            throw e;
        } catch (SystemException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    private static void validateAppVersionParams(Map<String, Object> paramsMap, String[] validateParam)
            throws BusinessException, SystemException {
        ValidateUtil.validateParams(paramsMap, validateParam);

        String appTypeStr = paramsMap.get("app_type").toString();
        String updateStatusStr = paramsMap.get("update_status").toString();
        String update_type = paramsMap.get("update_type").toString();

        if (!RegexpValidateUtil.isNumber(appTypeStr)) {
            throw new BusinessException(CommonRspCode.SYSTEM_BEAN_TO_MAP_ERROR, "参数app_type格式非法");
        }
        Integer app_type = Integer.parseInt(appTypeStr);
        if (app_type != 1 && app_type != 2) {
            throw new BusinessException(CommonRspCode.SYSTEM_BEAN_TO_MAP_ERROR, "参数app_type非法参数");
        }

        if (!RegexpValidateUtil.isNumber(updateStatusStr)) {
            throw new BusinessException(CommonRspCode.SYSTEM_BEAN_TO_MAP_ERROR, "参数update_status格式非法");
        }

        if (!RegexpValidateUtil.isNumber(update_type)) {
            throw new BusinessException(CommonRspCode.SYSTEM_BEAN_TO_MAP_ERROR, "参数update_type格式非法");
        }
    }

    private int generateValue(Integer value) {
        if (value == null) {
            return 1;
        }
        return value + 1;
    }


    @Override
    public void selectMDStoresListByContactTel(Map<String, Object> paramsMap, ResultData result)
            throws BusinessException, SystemException, Exception {
        try {
            String store_mobile = (String) paramsMap.get("contact_tel");

            if (!StringUtil.isEmpty(store_mobile) && !RegexpValidateUtil.isPhone(store_mobile)) {
                throw new BusinessException(CommonRspCode.SYSTEM_BEAN_TO_MAP_ERROR, "店东手机号码格式错误");
            }

            List<MDStores> list = storesDao.selectMDStoresListByContactTel(paramsMap);
            if (list != null && list.size() > 0) {
				/*for (Map<String, Object> map : list) {
					Map<String, Object> tempMap = new HashMap<String, Object>();
					tempMap.put("consumer_id", map.get("consumer_id"));
					tempMap.put("stores_id", map.get("stores_id"));
					tempMap.put("stores_mobile", map.get("stores_mobile"));
					tempMap.put("consumer_mobile", map.get("consumer_mobile"));

					tempMap.put("operator", map.get("operator"));
					tempMap.put("remark", map.get("remark"));
					tempMap.put("create_date", map.get("create_date"));
					resultlist.add(tempMap);
				}*/
            }
            Map<String, Object> resultDate = new HashMap<String, Object>();
            resultDate.put("list", list);
            result.setResultData(resultDate);
        } catch (BusinessException e) {
            throw e;
        } catch (SystemException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void hebaoUserInfoFind(Map<String, Object> paramsMap, ResultData result)
            throws BusinessException, SystemException, Exception {
        try {

            ValidateUtil.validateParams(paramsMap, new String[]{"mobile"});
            logger.info("获取的和包登陆手机号码,mobile:" + paramsMap.get("mobile"));

            Object mobile = paramsMap.get("mobile");
            Map<String, Object> tempMap = new HashMap<String, Object>();
            tempMap.put("mobile", paramsMap.get("mobile"));
            List<MDConsumer> list = consumerDao.selectMDConsumer(tempMap);

            if (list == null || list.size() == 0) {

                Map<String, Object> paramsMap2 = new HashMap<String, Object>();
                paramsMap2.put("contact_tel", mobile);
                List<MDStores> storeList = storesDao.selectMDStoresListByContactTel(paramsMap2);
                if (storeList == null || storeList.size() == 0) {
                    //用户没有注册，则进行注册
                    String user_service_url = PropertiesConfigUtil.getProperty("user_service_url");
                    Map<String, String> reqParams = new HashMap<String, String>();
                    Map<String, Object> bizParams = new HashMap<String, Object>();
                    reqParams.put("service", "infrastructure.consumerInfoSync");

                    bizParams.put("mobile", mobile);
                    bizParams.put("password", "123456");
                    bizParams.put("slat", "hbdl12");
                    bizParams.put("registered_date", DateUtil.date2Str(new Date(), DateUtil.fmt_yyyyMMdd));
                    reqParams.put("params", FastJsonUtil.toJson(bizParams));
                    HttpClientUtil.postShort(user_service_url, reqParams);
                } else {
                    //该用户只是店东，需要注册会员
                    MDConsumer mDConsumer = new MDConsumer();
                    Date registered_date = new Date();
                    mDConsumer.setConsumer_id(storeList.get(0).getStores_id());
                    mDConsumer.setMobile(mobile.toString());
                    mDConsumer.setStatus("normal");
                    mDConsumer.setRegistered_date(registered_date);
                    mDConsumer.setModified_date(registered_date);
                    mDConsumer.setCreated_date(registered_date);
                    consumerDao.insertMDConsumer(mDConsumer);
                }


            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * 获取和包用户已领现金券列表
     */
    @Override
    public void hebaoCashCouponListFind(Map<String, Object> paramsMap, ResultData result)
            throws BusinessException, SystemException, Exception {
        try {
            ValidateUtil.validateParams(paramsMap, new String[]{"mobile"});


            String mobile = paramsMap.get("mobile").toString();
            logger.info("和包登陆信息,mobile:" + mobile);
            //用户手机号码不存在我们redis中，说明没有从和包入口进入系统或登陆信息失效，需要重新登陆
            if (mobile == null || "null".equals(mobile) || StringUtil.isEmpty(mobile)) {
                throw new BusinessException(CommonRspCode.SYSTEM_PARAM_MISS, "和包登陆查询用户信息为空");
            }

            Map<String, Object> tempMap = new HashMap<String, Object>();
            tempMap.put("mobile", mobile);
            List<MDConsumer> list = consumerDao.selectMDConsumer(tempMap);
            if (list == null || list.size() == 0 || list.get(0) == null) {
                throw new BusinessException(CommonRspCode.USER_NOT_EXIST, "用户不存在，请先登陆和包账号");
            }

            String user_service_url = PropertiesConfigUtil.getProperty("finance_service_url");
            Map<String, String> reqParams = new HashMap<String, String>();
            Map<String, Object> bizParams = new HashMap<String, Object>();
            reqParams.put("service", "finance.consumer.hebaoCashCouponListFind");
            bizParams.put("member_id", list.get(0).getConsumer_id());
            reqParams.put("params", FastJsonUtil.toJson(bizParams));
            String resultJsonstr = HttpClientUtil.postShort(user_service_url, reqParams);
            JSONObject resultMap = JSONObject.fromObject(resultJsonstr);
            if (resultMap.containsKey("rsp_code") && CommonRspCode.RESPONSE_FAIL.equals(resultMap.get("rsp_code"))) {
                throw new BusinessException(resultMap.getString("error_code"), resultMap.getString("error_msg"));
            }
            net.sf.json.JSONArray array = new net.sf.json.JSONArray();
            if (resultMap.getJSONObject("data") != null && resultMap.getJSONObject("data").containsKey("list") && resultMap.getJSONObject("data").getJSONArray("list") != null) {
                array = resultMap.getJSONObject("data").getJSONArray("list");
            }

            result.setResultData(array);
        } catch (BusinessException e) {
            throw e;
        } catch (SystemException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 和包用户领取现金券
     */
    @Override
    public void hebaoGetCashCoupon(Map<String, Object> paramsMap, ResultData result)
            throws BusinessException, SystemException, Exception {
        try {
            ValidateUtil.validateParams(paramsMap, new String[]{"mobile", "cashCouponCode", "cashcouponValue"});
            String mobile = paramsMap.get("mobile").toString();
            logger.info("和包登陆信息,mobile:" + mobile);
            //用户手机号码不存在我们redis中，说明没有从和包入口进入系统或登陆信息失效，需要重新登陆
            if (mobile == null || "null".equals(mobile) || StringUtil.isEmpty(mobile)) {
                throw new BusinessException(CommonRspCode.SYSTEM_PARAM_MISS, "和包登陆查询用户信息为空");
            }


            Map<String, Object> tempMap = new HashMap<String, Object>();
            tempMap.put("mobile", mobile);
            List<MDConsumer> list = consumerDao.selectMDConsumer(tempMap);
            if (list == null || list.size() == 0 || list.get(0) == null) {
                throw new BusinessException(CommonRspCode.USER_NOT_EXIST, "用户不存在，请先登陆和包账号");
            }

            String user_service_url = PropertiesConfigUtil.getProperty("finance_service_url");
            Map<String, String> reqParams = new HashMap<String, String>();
            Map<String, Object> bizParams = new HashMap<String, Object>();
            reqParams.put("service", "finance.consumer.hebaoGetCashCoupon");
            bizParams.put("member_id", list.get(0).getConsumer_id());
            bizParams.put("cashCouponCode", paramsMap.get("cashCouponCode"));
            bizParams.put("cashcouponValue", paramsMap.get("cashcouponValue"));
            reqParams.put("params", FastJsonUtil.toJson(bizParams));
            String resultJson = HttpClientUtil.postShort(user_service_url, reqParams);
            JSONObject jsonResult = JSONObject.fromObject(resultJson);
            if (jsonResult.containsKey("rsp_code") && CommonRspCode.RESPONSE_FAIL.equals(jsonResult.get("rsp_code"))) {
                throw new BusinessException(jsonResult.getString("error_code"), jsonResult.getString("error_msg"));
            }

            result.setResultData("领取优惠券成功");
        } catch (BusinessException e) {
            throw e;
        } catch (SystemException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * 和包用户核销现金券
     */
    @Override
    public void hebaoUseCashCoupon(Map<String, Object> paramsMap, ResultData result)
            throws BusinessException, SystemException, Exception {
        try {
            ValidateUtil.validateParams(paramsMap, new String[]{"credtential", "signData", "cashCouponCode"});
            String credtential = paramsMap.get("credtential").toString();
            String signData = paramsMap.get("signData").toString();
            logger.info("和包登陆信息,credtential:" + credtential + ",signData:" + signData);
            String mobile = redisUtil.getStr(credtential + signData);

            //用户手机号码不存在我们redis中，说明没有从和包入口进入系统或登陆信息失效，需要重新登陆
            if (mobile == null || "null".equals(mobile) || StringUtil.isEmpty(mobile)) {
                throw new BusinessException(CommonRspCode.SYSTEM_PARAM_MISS, "和包登陆查询用户信息为空");
            }


            Map<String, Object> tempMap = new HashMap<String, Object>();
            tempMap.put("mobile", mobile);
            List<MDConsumer> list = consumerDao.selectMDConsumer(tempMap);
            if (list == null || list.size() == 0 || list.get(0) == null) {
                throw new BusinessException(CommonRspCode.USER_NOT_EXIST, "用户不存在，请先登陆和包账号");
            }

            String user_service_url = PropertiesConfigUtil.getProperty("finance_service_url");
            Map<String, String> reqParams = new HashMap<String, String>();
            Map<String, Object> bizParams = new HashMap<String, Object>();

            reqParams.put("service", "finance.consumer.hebaoUseCashCoupon");
            bizParams.put("member_id", list.get(0).getConsumer_id());
            bizParams.put("cashCouponCode", paramsMap.get("cashCouponCode"));
            reqParams.put("params", FastJsonUtil.toJson(bizParams));
            HttpClientUtil.postShort(user_service_url, reqParams);


            result.setResultData("核销优惠券成功");
        } catch (BusinessException e) {
            throw e;
        } catch (SystemException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void hebaoMd5Sign(Map<String, Object> paramsMap, ResultData result)
            throws BusinessException, SystemException, Exception {
        try {
            ValidateUtil.validateParams(paramsMap, new String[]{"accesstoken", "appid", "format", "operationtime", "signaturemethod", "appsecret"});

            Map<String, String> params = new HashMap<String, String>();
            params.put("accesstoken", paramsMap.get("accesstoken").toString());
            params.put("appid", paramsMap.get("appid").toString());
            params.put("format", paramsMap.get("format").toString());
            params.put("operationtime", paramsMap.get("operationtime").toString());
            params.put("signaturemethod", paramsMap.get("signaturemethod").toString());

            //params.put("appsecret", paramsMap.get("appsecret").toString());
            String resultSign = HiCryptUtils.cryptMd5(params, paramsMap.get("appsecret").toString());
            result.setResultData(resultSign);
        } catch (BusinessException e) {
            throw e;
        } catch (SystemException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void mdConsumerVipTimeUpdate(Map<String, Object> paramsMap, ResultData result)
            throws BusinessException, SystemException, Exception {
        logger.info("开始====>延续消费者VIP时长  consumer_id = " + paramsMap.get("consumer_id") + " 充值类型   recharge_type = "
                + paramsMap.get("recharge_type"));
        ValidateUtil.validateParams(paramsMap, new String[]{"consumer_id"});
        // 17:06 2019/5/7 欧少辉
        String memberId = (String) paramsMap.get("consumer_id");
        Map<String, Object> tempMap = new HashMap<String, Object>();
        tempMap.put("member_id", memberId);
        // 首先查找是否是会员
        MDConsumer mdConsumer = consumerDao.selectMDConsumerById(tempMap);
        if (mdConsumer == null) {
            throw new BusinessException(RspCode.MEMBER_NOT_EXIST,RspCode.MSG.get(RspCode.MEMBER_NOT_EXIST));
        }
        Map<String, Object> resultMap = new HashMap<>();
        String type = (String)paramsMap.get("type");
        // 体验会员不走分销逻辑 只修改会员有效期以及会员类型
        if("5".equals(type)){
            Date now = new Date();
            // 设置体验会员时间（有效期为10天）
            String startDate = DateUtil.date2Str(now, DateUtil.fmt_yyyyMMddHHmmss);
            // 起始时间
            paramsMap.put("vip_start_time", startDate);
            // 结束时间
            paramsMap.put("vip_end_time", DateUtil.addDate(startDate,DateUtil.fmt_yyyyMMdd,3,9)+" 23:59:59");
            paramsMap.put("type", type);
            paramsMap.put("is_vip_expired", 1);
            // 购买次数+1
            if(mdConsumer.getMember_recharge_cnt()==null || mdConsumer.getMember_recharge_cnt()==0){
                paramsMap.put("member_recharge_cnt", 2);
            }else {
                paramsMap.put("member_recharge_cnt", mdConsumer.getMember_recharge_cnt() - 1);
            }
            // 用户类型
            resultMap.put("type", type);
            //是否是新会员
            resultMap.put("is_New", "Y");
        }else {
            //分销佣金逻辑，根据时间来判断是否是第一次充值，所以必须在修改会员时间之前
            this.editMemberDistributionByRecharge(paramsMap);
            MDConsumer consumer = consumerDao.selectMDConsumerBaseInfo(paramsMap);
            paramsMap.put("vip_start_time", new Date());
            String dateStr = DateUtil.date2Str(new Date(), DateUtil.fmt_yyyyMMdd);
            Date date2 = DateUtil.str2Date(dateStr, DateUtil.fmt_yyyyMMdd);
            resultMap.put("type", consumer.getType());
            if (consumer.getVip_end_time() == null || consumer.getVip_end_time().getTime() - date2.getTime() < 0 || consumer.getType() == 2) {
                String date2Str = DateUtil.date2Str(new Date(), DateUtil.fmt_yyyyMMdd);
                date2Str = DateUtil.addDate(date2Str, DateUtil.fmt_yyyyMMdd, 1, 1);
                Date date = DateUtil.str2Date(date2Str, DateUtil.fmt_yyyyMMdd);
                paramsMap.put("vip_end_time", date);
                if (consumer.getVip_end_time() == null || consumer.getType() == 2) {
                    paramsMap.put("growth_value", 68);
                    //是否是新会员
                    resultMap.put("is_New", "Y");
                }
            } else {
                String date2Str = DateUtil.date2Str(consumer.getVip_end_time(), DateUtil.fmt_yyyyMMdd);
                date2Str = DateUtil.addDate(date2Str, DateUtil.fmt_yyyyMMdd, 1, 1);
                Date date = DateUtil.str2Date(date2Str, DateUtil.fmt_yyyyMMdd);
                paramsMap.put("vip_end_time", date);
            }
            //内测会员 续费还是为 1   type =1  的情况为1   isvip 为1 的情况下 type 为1 其他情况下为0  之前的数据 需要改
            if (null != consumer.getIs_vip_expired() && consumer.getIs_vip_expired() == 1) {
                paramsMap.put("type", 1);
            } else {
                paramsMap.put("type", 0);
            }
            if(consumer.getVip_start_time()!=null && consumer.getVip_end_time()!=null) {
                // 如果是体验会员会员过期后购买399会员，则认为是新会员，要送12张礼券
                String vip_start_time = DateUtil.date2Str(consumer.getVip_start_time(), DateUtil.fmt_yyyyMMdd);
                String vip_end_time = DateUtil.date2Str(consumer.getVip_end_time(), DateUtil.fmt_yyyyMMdd);
                if (Integer.valueOf(DateUtil.getDistanceDays(vip_start_time, vip_end_time)) <= 10) {
                    //是否是新会员
                    resultMap.put("is_New", "Y");
                    resultMap.put("type", 0);
                    // 只要购买399会员要把这个字段改为0
                    paramsMap.put("member_recharge_cnt", 0);
                }
            }
        }
        Integer res = consumerDao.updateMDConsumer(paramsMap);
        if (res == 0) {
            throw new BusinessException("延续会员时长失败", "延续会员时长失败");
        }
        logger.info("结束====>延续消费者VIP时长  consumer_id = " + paramsMap.get("consumer_id") + " 充值类型   recharge_type = "
                + paramsMap.get("recharge_type"));
        String consumer_id = StringUtil.formatStr(paramsMap.get("consumer_id"));
        //查询用户是否已经有贝壳号
        Map<String, Object> temp = new HashMap<>();
        temp.put("member_id", consumer_id);
        MDMemberInvitationCode inviteCode = memberInvitationCodeDao.findMemberInviteCode(temp);
        if (inviteCode == null) {
            String code = "";
            //判断生成的贝壳号是否唯一.
            for (int i = 0; i < 500; i++) {
                code = IDUtil.generateCode(5);
                temp.clear();
                temp.put("invite_code", code);
                MDMemberInvitationCode memberCode = memberInvitationCodeDao.findMemberInviteCode(temp);
                if (memberCode == null) {
                    break;
                } else if (memberCode != null && i == 499) {
                    code = IDUtil.generateCode(6);
                }
            }
            // 自动帮充值成功的用户生成贝壳号
            MDMemberInvitationCode invitationCode = new MDMemberInvitationCode();
            invitationCode.setInviteCode(code);
            invitationCode.setMemberId(consumer_id);
            invitationCode.setStatus("0");// 默认为0
            memberInvitationCodeDao.addMemberInvitationCode(invitationCode);
        }
        result.setResultData(resultMap);
    }


    @Override
    public void memberDistributionByRecharge(Map<String, Object> paramsMap, ResultData result)
            throws BusinessException, SystemException, Exception {
        // TODO Auto-generated method stub
        this.editMemberDistributionByRecharge(paramsMap);
    }


    /**
     * 充值分销逻辑
     *
     * @param paramsMap
     * @throws Exception
     */
    public void editMemberDistributionByRecharge(Map<String, Object> paramsMap) throws Exception {
        String memberId = (String) paramsMap.get("consumer_id");
        logger.info("开始====充值分销逻辑  consumer_id = " + paramsMap.get("consumer_id") + " 充值类型   recharge_type = "
                + paramsMap.get("recharge_type"));
        BigDecimal rechargeMoney = new BigDecimal("399");
        BigDecimal receiveMoney = new BigDecimal("120");
        BigDecimal grandMoney = new BigDecimal("60");
        BigDecimal TopMoney = new BigDecimal("20");
        String type = "recharge";
        String direct = "直邀奖励";
        String indirect = "间邀奖励";
        String nextdirect = "次邀奖励";
        String parentMobile = null;
        //判断是否是第一次充值
        Map<String, Object> tempMap = new HashMap<String, Object>();
        tempMap.put("member_id", memberId);
        MDMemberDistribution memberDistribution = memberDistributionDao.getMemberDistributionInfo(tempMap);
        //先判断是否已经有会员了
        Map<String, Object> tempMdMap = new HashMap<String, Object>();
        tempMdMap.put("consumer_id", memberId);
        MDConsumer mDConsumer = consumerDao.selectMDConsumerBaseInfo(tempMdMap);
        if (null == mDConsumer) {
            throw new BusinessException(CommonRspCode.USER_NOT_EXIST, "用户不存在，请先登陆");
        }
        logger.info("充值分销逻辑 ，是否是首次充值 开始时间 Vip_start_time() = " + mDConsumer.getVip_start_time() + " 结束时间Vip_end_time() = "
                + mDConsumer.getVip_end_time());
        //判断开始时间为空  和 结束时间为空,首次充值为会员才有返佣。
        if (null != memberDistribution) {
            if ((mDConsumer.getType() == 2) || (mDConsumer.getVip_start_time() == null && mDConsumer.getVip_end_time() == null)) {
                //取得父级的id
                String parentId = memberDistribution.getParentId();
                //计算父级金额
                if (StringUtils.isNotEmpty(parentId) == true) {
                    //邀请好友首次购买会员，按会员购买金额1:1发放
                    Map<String, Object> tempParentMap = new HashMap<String, Object>();
                    tempParentMap.put("member_id", parentId);
                    List<MDConsumer> mDConsumerList = consumerDao.selectMDConsumer(tempParentMap);
                    if (null != mDConsumerList && mDConsumerList.size() > 0) {
                        MDConsumer parentConsumer = mDConsumerList.get(0);
                        tempMap.clear();
                        tempMap.put("consumer_id", parentConsumer.getConsumer_id());
                        tempMap.put("growth_value", rechargeMoney.intValue());
                        consumerDao.updateMDConsumer(tempMap);
                    }
                    tempMdMap.put("consumer_id", parentId);
                    MDConsumer parConsumer = consumerDao.selectMDConsumerBaseInfo(tempMdMap);
                    parentMobile = null != parConsumer ? parConsumer.getMobile() : "";
                    this.handlerConsumerReceiveMoney(parentId, receiveMoney, mDConsumer.getMobile(), parentMobile, type, direct);
                    //取得爷爷级Id
                    String grandId = memberDistribution.getGrandId();
                    if (StringUtils.isNotEmpty(grandId) == true) {
                        this.handlerConsumerReceiveMoney(grandId, grandMoney, mDConsumer.getMobile(), parentMobile, type, indirect);
                    }
                }
                //计算顶层金额
                String topId = memberDistribution.getTopId();
                if (StringUtils.isNotEmpty(topId)) {
                    // 顶层id  一定是一个掌柜，
                    String managerId = StringUtil.formatStr(memberDistribution.getManagerId());
                    //最后有关联的掌柜id
                    String generalId = StringUtil.formatStr(memberDistribution.getGeneralId());
                    //0.07*0.07
                    BigDecimal rate = MoneyUtil.moneyMul(new BigDecimal("0.07"), new BigDecimal("0.07")).setScale(3, BigDecimal.ROUND_HALF_UP);
                    //最后关联的的掌柜的分佣
                    BigDecimal generalRate = MoneyUtil.moneyMul(TopMoney, rate).setScale(3, BigDecimal.ROUND_HALF_UP);
                    //第二个掌柜的分佣
                    BigDecimal managerRate = MoneyUtil.moneyMul(TopMoney, new BigDecimal("0.07")).setScale(3, BigDecimal.ROUND_HALF_UP);
                    //top差
                    BigDecimal subRate = generalRate.add(managerRate);
                    //第一个（直属）掌柜的分佣(有最后关联掌柜的)
                    BigDecimal topRate = MoneyUtil.moneySub(TopMoney, subRate).setScale(3, BigDecimal.ROUND_HALF_UP);
                    //第一个（直属）掌柜的分佣(没有有最后关联掌柜的)
                    BigDecimal topRateN = MoneyUtil.moneyMul(TopMoney, new BigDecimal("0.93")).setScale(3, BigDecimal.ROUND_HALF_UP);
                    if (StringUtil.isNotEmpty(generalId)) {
                        //插入最后关联掌柜的分佣
                        this.handlerConsumerReceiveMoney(generalId, generalRate, mDConsumer.getMobile(), parentMobile, type, nextdirect);
                        //插入第二个掌柜的分佣
                        this.handlerConsumerReceiveMoney(managerId, managerRate, mDConsumer.getMobile(), parentMobile, type, nextdirect);
                        //插入直属掌柜的Id
                        this.handlerConsumerReceiveMoney(topId, topRate, mDConsumer.getMobile(), parentMobile, type, nextdirect);
                    } else {
                        if (StringUtils.isNotEmpty(managerId)) {
                            this.handlerConsumerReceiveMoney(managerId, managerRate, mDConsumer.getMobile(), parentMobile, type, nextdirect);
                            this.handlerConsumerReceiveMoney(topId, topRateN, mDConsumer.getMobile(), parentMobile, type, nextdirect);
                        } else {
                            this.handlerConsumerReceiveMoney(topId, TopMoney, mDConsumer.getMobile(), parentMobile, type, nextdirect);
                        }
                    }
                }
            }
        }
    }


    /**
     * 会员充值，分佣插入会员资产表
     *
     * @param memberId
     * @param receiveMoney
     * @param mobile
     * @param type
     * @param remark
     * @throws Exception
     */
    public void handlerConsumerReceiveMoney(String memberId, BigDecimal receiveMoney, String mobile, String inviteMobile, String type, String remark) throws Exception {
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("member_id", memberId);
        paramsMap.put("receiveMoney", receiveMoney.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        paramsMap.put("mobile", mobile);
        paramsMap.put("inviteMobile", inviteMobile);
        paramsMap.put("type", type);
        paramsMap.put("remark", remark);
        String finance_service_url = PropertiesConfigUtil.getProperty("finance_service_url");
        Map<String, String> reqParams = new HashMap<String, String>();
        reqParams.put("service", "finance.memberReceiveMoney");
        reqParams.put("params", FastJsonUtil.toJson(paramsMap));
        HttpClientUtil.postShort(finance_service_url, reqParams);
    }


    @Override
    public void consumerVipTime(Map<String, Object> paramsMap, ResultData result)
            throws BusinessException, SystemException, Exception {
        try {
            ValidateUtil.validateParams(paramsMap, new String[]{"consumer_id"});
            MDConsumer mDConsumer = consumerDao.selectMDConsumerBaseInfo(paramsMap);
            if (null == mDConsumer) {
                throw new BusinessException(RspCode.MEMBER_NOT_EXIST, "消费者信息不存在");
            }

            //商城订单分销逻辑
			/*String  orderCount  = (String) paramsMap.get("orderCount");
			String  orderMoney  = (String) paramsMap.get("orderMoney");
			if(StringUtils.equals(orderCount, "1")) {
				this.editMemberDistributionByOrder(mDConsumer.getConsumer_id(),orderMoney,mDConsumer.getMobile());
			}*/

            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("consumer_id", StringUtil.formatStr(mDConsumer.getConsumer_id()));
            resultMap.put("vip_end_time", mDConsumer.getVip_end_time());
            resultMap.put("vip_start_time", mDConsumer.getVip_start_time());
            result.setResultData(resultMap);
        } catch (BusinessException e) {
            throw e;
        } catch (SystemException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * 被邀请会员首次下单 增加成长值
     *
     * @param memberId
     * @throws Exception
     */
    public void editMemberDistributionByOrder(String memberId, String orderMoney, String mobile) throws Exception {
        String type = "order";
        //通过会员id,进行查询分销表是否有上级，如果有查询上级会员分销，然后做update
        Map<String, Object> tempMap = new HashMap<String, Object>();
        tempMap.put("member_id", memberId);
        //先判断是否已经有会员了
        MDMemberDistribution memberDistribution = memberDistributionDao.getMemberDistributionInfo(tempMap);
        //取得父级的id
        if (null != memberDistribution) {
            String parentId = memberDistribution.getParentId();
            if (StringUtils.isNotEmpty(parentId)) {
                //给上级增加成长指数
                Map<String, Object> tempParentMap = new HashMap<String, Object>();
                tempParentMap.put("member_id", parentId);
                List<MDConsumer> mDConsumerList = consumerDao.selectMDConsumer(tempParentMap);
                Integer growthValue = null;
                if (null != mDConsumerList && mDConsumerList.size() > 0) {
                    MDConsumer parentConsumer = mDConsumerList.get(0);
                    growthValue = parentConsumer.getGrowth_value();
//						tempMap.clear();
//						tempMap.put("consumer_id", parentConsumer.getConsumer_id());
//						tempMap.put("growth_value",20);
//						consumerDao.updateMDConsumer(tempMap);
                }
                tempMap.clear();
                tempMap.put("member_id", parentId);
                MDMemberDistribution parentDistribution = memberDistributionDao.getMemberDistributionInfo(tempMap);
                BigDecimal money = new BigDecimal(orderMoney);
                String rabate = this.computerMemberGrade(growthValue);
                BigDecimal receiveMoney = money.multiply(new BigDecimal(rabate)).setScale(2, BigDecimal.ROUND_HALF_UP);
                // 把这个钱给到财务那个表
//				this.handlerConsumerReceiveMoney(parentId, receiveMoney, mobile,type);
                BigDecimal reMoney = null == parentDistribution.getReceiveMoney() ? new BigDecimal("0") : parentDistribution.getReceiveMoney();
                parentDistribution.setReceiveMoney(reMoney.add(receiveMoney));
                memberDistributionDao.update(parentDistribution);
            }
        }

    }


    public String computerMemberGrade(Integer growthValue) {
        String rebate = null;
        if (growthValue <= 399) {
            rebate = "0.03";
        } else if (growthValue <= 999 && growthValue >= 400) {
            rebate = "0.04";
        } else if (growthValue <= 1999 && growthValue >= 1000) {
            rebate = "0.05";
        } else if (growthValue <= 4999 && growthValue >= 2000) {
            rebate = "0.06";
        } else if (growthValue >= 5000) {
            rebate = "0.07";
        }
        return rebate;
    }


    /**
     * 设置会员成长值，通过别人分享的
     */
    public void handleMemberGrowthValue(Map<String, Object> paramsMap, ResultData result)
            throws BusinessException, SystemException, Exception {
        ValidateUtil.validateParams(paramsMap, new String[]{"member_id"});
        String growthValue = (String) paramsMap.get("growth_value");
        List<MDConsumer> mDConsumerList = consumerDao.selectMDConsumer(paramsMap);
        Map<String, Object> tempMap = new HashMap<String, Object>();
        if (null != mDConsumerList && mDConsumerList.size() > 0) {
            MDConsumer parentConsumer = mDConsumerList.get(0);
            tempMap.clear();
            tempMap.put("consumer_id", parentConsumer.getConsumer_id());
            tempMap.put("growth_value", Integer.valueOf(growthValue));
            consumerDao.updateMDConsumer(tempMap);
        }
    }

    @Override
    public void updateConsumerGrowthValue(Map<String, Object> paramsMap, ResultData result) throws Exception {
        ValidateUtil.validateParams(paramsMap, new String[]{"member_id", "growth_value"});
        consumerDao.updateMDConsumer(paramsMap);
    }

    @Override
    public void consumerVipLevel(Map<String, Object> paramsMap, ResultData result) throws Exception {
        paramsMap.put("vip_end_time", new Date());
        List<Map<String, Object>> consumerList = consumerDao.selectMDConsumerLevel(paramsMap);
        result.setResultData(consumerList);
    }


    @SuppressWarnings("null")
    public void handleConsumerGrowthValue(Map<String, Object> paramsMap, ResultData result)
            throws BusinessException, SystemException, Exception {
        ValidateUtil.validateParams(paramsMap, new String[]{"user_id"});
        String growthValue = (String) paramsMap.get("growth_value");
        String user_id = (String) paramsMap.get("user_id");
        Map<String, Object> tempMap = new HashMap<String, Object>();
        tempMap.put("user_id", user_id);
        List<MDUserMember> mdUserList = memberDao.selectMDUserMember(tempMap);
        if (null == mdUserList && mdUserList.size() == 0) {
            throw new BusinessException(RspCode.USER_MEMBER_ERROR, RspCode.MSG.get(RspCode.USER_MEMBER_ERROR));
        }
        MDUserMember userMember = mdUserList.get(0);
        tempMap.clear();
        tempMap.put("member_id", userMember.getMember_id());
        tempMap.put("growth_value", Integer.valueOf(growthValue));
        consumerDao.updateMDConsumer(tempMap);
    }


    /**
     * 判断是否会员
     */
    public void isMemberInfo(Map<String, Object> paramsMap, ResultData result)
            throws BusinessException, SystemException, Exception {
        ValidateUtil.validateParamsNum(paramsMap, new String[]{"member_id", "mobile"}, 1);
        String consumer_id = StringUtil.formatStr(paramsMap.get("member_id"));
        String mobile = StringUtil.formatStr(paramsMap.get("mobile"));
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (StringUtil.isNotBlank(consumer_id)) {
            paramMap.put("consumer_id", consumer_id);
        }
        if (StringUtil.isNotBlank(mobile)) {
            paramMap.put("mobile", mobile);
        }
        MDConsumer mDConsumer = consumerDao.selectMDConsumerById(paramMap);
        Map<String, Object> tempMap = new HashMap<String, Object>();
        if (null == mDConsumer) {
            tempMap.put("isMember", "N");
        } else {
            if (mDConsumer.getVip_end_time() == null) {
                tempMap.put("isMember", "N");
            } else {
                Date vipEndTime = mDConsumer.getVip_end_time();
                Date vip_end_time = DateUtil.str2Date(DateUtil.date2Str(vipEndTime, "yyyy-MM-dd"), "yyyy-MM-dd");
                Date newDate = DateUtil.parseToDate(DateUtil.getFormatDate("yyyy-MM-dd"));
                if (vip_end_time.before(newDate)) {
                    tempMap.put("isMember", "N");
                } else {
                    tempMap.put("isMember", "Y");
                }
            }
        }
        result.setResultData(tempMap);
    }


    /**
     * 充值的时候，写入分销关系到分销表里
     *
     * @param paramsMap
     */
    public void memberDistrInfoEdit(Map<String, Object> paramsMap)
            throws BusinessException, SystemException, Exception {
        String memberId = (String) paramsMap.get("member_id");
        Map<String, Object> tempMap = new HashMap<String, Object>();
        tempMap.put("member_id", memberId);
        // 首先查找是否是会员
        MDConsumer mDConsumerList = consumerDao.selectMDConsumerById(tempMap);
        if (mDConsumerList.getType() != 2 && null != mDConsumerList.getVip_start_time()
                && null != mDConsumerList.getVip_end_time()) {
            return;
        } else {
            MDMemberDistrbutionInfo memberDistrInfo = memberDistrbutionInfoDao.findMemberDistrInfo(tempMap);
            if (null != memberDistrInfo) {
                String parentId = StringUtil.formatStr(memberDistrInfo.getParentId());
                Integer flag = this.buildingMemberDistrbution(memberId, parentId);
                if (flag == 1) {
                    memberDistrInfo.setStatus(1);
                    memberDistrbutionInfoDao.update(memberDistrInfo);
                    //添加一条红包活动记录(用于红包抽奖)
//                    addHongbaoActivity(memberId, parentId);
                } else {
                    throw new BusinessException("绑定会员分销关系", "绑定会员分销关系失败");
                }
            } else {
                // 如果在MDMemberDistrbutionInfo表里没有数据，就是通过APP购买的。
                MDMemberDistribution memberDistribution = memberDistributionDao.getMemberDistributionInfo(tempMap);
                if (null == memberDistribution) {
                    memberDistribution = new MDMemberDistribution();
                    memberDistribution.setMemberId(memberId);
                    memberDistribution.setCreateTime(new Date());
                    memberDistribution.setDistrLevel(2);
                    memberDistribution.setRegistLevel(2);
                    memberDistribution.setStatus(1);
                    memberDistributionDao.insert(memberDistribution);
                }
            }
        }
    }

//    private void addHongbaoActivity(String memberId, String parentId) throws Exception {
//        MdHongbaoActivity hongbaoActivity = new MdHongbaoActivity();
//        Date date = new Date();
//        hongbaoActivity.setMember_id(memberId);
//        hongbaoActivity.setParent_id(parentId);
//        hongbaoActivity.setCreated_date(date);
//        hongbaoActivity.setModified_date(date);
//        hongbaoActivity.setIs_activation("N");
//        hongbaoActivity.setIs_use("N");
//        hongbaoActivityDao.insertHongbaoActivity(hongbaoActivity);
//        //如果邀请满3个人,就帮邀请人激活一张精品礼券.
//        Map<String, Object> tempMap = new HashMap<>();
//        tempMap.put("parent_id", parentId);
//        tempMap.put("is_activation", "N");
//        List<MdHongbaoActivity> activityList = hongbaoActivityDao.findHongbaoActivity(tempMap);
//        //修改 未激活状态为已激活状态
//        if (activityList.size() >= 3) {
//            HashSet<Long> activity_id_in = new HashSet<>();
//            for (int i = 0; i < 3; i++) {
//                activity_id_in.add(activityList.get(i).getActivity_id());
//            }
//            tempMap.clear();
//            tempMap.put("is_activation", "Y");
//            tempMap.put("activity_id_in", activity_id_in);
//            hongbaoActivityDao.hongbaoActivityEdit(tempMap);
//
//            //激活时间最前的一张未激活状态的精品礼券
//            String finance_service_url = PropertiesConfigUtil.getProperty("finance_service_url");
//            Map<String, String> reqParams = new HashMap<>();
//            Map<String, Object> bizParams = new HashMap<>();
//            reqParams.put("service", "finance.consumer.activationFormerOneGiftCoupon");
//            bizParams.put("member_id", parentId);
//            reqParams.put("params", FastJsonUtil.toJson(bizParams));
//            HttpClientUtil.postShort(finance_service_url, reqParams);
//        }
//
//    }


    @Override
    public void distrbutionFansManage(Map<String, Object> paramsMap, ResultData result)
            throws BusinessException, SystemException, Exception {
        ValidateUtil.validateParams(paramsMap, new String[]{"member_id"});
        String memberId = StringUtil.formatStr(paramsMap.get("member_id"));
        String type = StringUtil.formatStr(paramsMap.get("type"));
        int tempFans = 0;
        int directFans = 0;
        int indirectFans = 0;
        int count = 0;
        Map<String, Object> tempMap = new HashMap<String, Object>();
        Map<String, Object> reqMap = new HashMap<String, Object>();
        List<Map<String, Object>> distrList = new ArrayList<Map<String, Object>>();
        reqMap.clear();
        reqMap.put("member_id", memberId);
        MDConsumer consumer = consumerDao.selectMDConsumerById(reqMap);
        String mobile = null;
        if (null != consumer) {
            mobile = consumer.getMobile();
        }
        //list遍历map 取得全部值
        tempMap.clear();
        tempMap.put("parent_id", memberId);
        //查询直接粉丝
        List<Map<String, Object>> parentIdList = memberDistributionDao.selectMemberDirectFans(tempMap);
        directFans = parentIdList.size();
        if (null != parentIdList && parentIdList.size() > 0) {
            for (Map<String, Object> map : parentIdList) {
                map.put("inviterMobile", mobile);
                distrList.add(map);
            }
        }

        //查询间接粉丝
        tempMap.clear();
        tempMap.put("grand_id", memberId);
        List<Map<String, Object>> grandIdList = memberDistributionDao.selectMemberIndirectFans(tempMap);
        indirectFans = grandIdList.size();
        if (null != grandIdList && grandIdList.size() > 0) {
            for (Map<String, Object> map : grandIdList) {
                reqMap.clear();
                reqMap.put("member_id", map.get("consumer_id"));
                String consumerMobile = memberDistributionDao.selectConsumerMobile(reqMap);
                map.put("inviterMobile", consumerMobile);
                distrList.add(map);
            }
        }

        //查询临时粉丝
        tempMap.clear();
        tempMap.put("member_id", memberId);
        List<Map<String, Object>> tempFansList = memberDistributionDao.selectTempFans(tempMap);
        tempFans = tempFansList.size();
        if (null != tempFansList && tempFansList.size() > 0) {
            for (Map<String, Object> map : tempFansList) {
                map.put("inviterMobile", mobile);
                distrList.add(map);
            }
        }

        //查询全部粉丝
//			tempMap.clear();
//			tempMap.put("member_id", memberId);
//			tempMap.put("parent_id", memberId);
//			tempMap.put("grand_id", memberId);
//			List<Map<String,Object>>  distrList =  memberDistributionDao.selectMemberFans(tempMap);

        count = distrList.size();
        System.out.println("全部粉丝数量:" + count + ";临时粉丝数量:" + tempFans + "直邀粉丝数量:" + directFans + ";间邀粉丝数量：" + indirectFans);
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("count", count);
        resultMap.put("tempFansCount", tempFans);
        resultMap.put("directFansCount", directFans);
        resultMap.put("indirectFansCount", indirectFans);

        if (StringUtil.equals(type, "direct")) {
            resultMap.put("directMemberList", parentIdList);
            grandIdList.clear();
            resultMap.put("indirectMemberList", grandIdList);
            tempFansList.clear();
            resultMap.put("tempFansList", tempFansList);
            distrList.clear();
            resultMap.put("distrMemberList", distrList);
        } else if (StringUtil.equals(type, "indirect")) {
            resultMap.put("indirectMemberList", grandIdList);
            parentIdList.clear();
            resultMap.put("directMemberList", parentIdList);
            tempFansList.clear();
            resultMap.put("tempFansList", tempFansList);
            distrList.clear();
            resultMap.put("distrMemberList", distrList);
        } else if (StringUtil.equals(type, "temp")) {
            resultMap.put("tempFansList", tempFansList);
            parentIdList.clear();
            resultMap.put("directMemberList", parentIdList);
            grandIdList.clear();
            resultMap.put("indirectMemberList", grandIdList);
            distrList.clear();
            resultMap.put("distrMemberList", distrList);
        } else {
            resultMap.put("distrMemberList", distrList);
            parentIdList.clear();
            resultMap.put("directMemberList", parentIdList);
            grandIdList.clear();
            resultMap.put("indirectMemberList", grandIdList);
            tempFansList.clear();
            resultMap.put("tempFansList", tempFansList);
        }
        result.setResultData(resultMap);
    }


    //递归查询所有的会员
    public Map<String, Object> getSimpleMemberdistrByParentId(List<MDMemberDistribution> parentList, Set<String> memberIdSet) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        List<List<MDMemberDistribution>> listMemberPar = new ArrayList<List<MDMemberDistribution>>();
        int count = 0;
        Map<String, Object> tempMap = new HashMap<String, Object>();
        if (null != parentList & parentList.size() > 0) {
            for (MDMemberDistribution memDistr : parentList) {
                memberIdSet.add(memDistr.getMemberId());
                tempMap.clear();
                tempMap.put("parent_id", memDistr.getMemberId());
                List<MDMemberDistribution> parentIdList = memberDistributionDao.getSimpleMemberdistrByParentId(tempMap);
                if (null != parentIdList && parentIdList.size() > 0) {
                    for (MDMemberDistribution memberDistrution : parentIdList) {
                        memberIdSet.add(memberDistrution.getMemberId());
                    }
                    listMemberPar.add(parentIdList);
                    this.getSimpleMemberdistrByParentId(parentIdList, memberIdSet);

                }
            }
        }
        count = memberIdSet.size();
        paramMap.put("count", memberIdSet.size());
        paramMap.put("memberIdSet", memberIdSet);
        System.out.println("测试分销递归 count：" + count);
        return paramMap;
    }

    //通过memberid，得到手机号，昵称，关联时间，粉丝类型，跟 parentIdList
    @SuppressWarnings("unlikely-arg-type")
    public List<Map<String, Object>> findDistrMemberData(Set<String> memberIdSet, List<MDMemberDistribution> disrtList) throws Exception {
        List<String> memberIdList = new ArrayList<String>();
        for (MDMemberDistribution entity : disrtList) {
            memberIdList.add(entity.getMemberId());
        }
        List<Map<String, Object>> distrMemberList = new ArrayList<Map<String, Object>>();
        Iterator<String> memberIdStr = memberIdSet.iterator();
        while (memberIdStr.hasNext()) {
            String memberId = memberIdStr.next();
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("member_id", memberId);
            MDConsumer consumerList = consumerDao.selectMDConsumerById(paramMap);
            Map<String, Object> distrMemberMap = new HashMap<String, Object>();
            distrMemberMap.put("member_id", memberId);
            if (null != consumerList) {
                String nickName = StringUtil.formatStr(consumerList.getNick_name());
                if (StringUtil.isEmpty(nickName)) {
                    distrMemberMap.put("nick_name", "昵称");
                } else {
                    distrMemberMap.put("nick_name", nickName);
                }
                distrMemberMap.put("mobile", consumerList.getMobile());
                MDMemberDistribution memberDistrEntity = memberDistributionDao.getMemberDistributionInfo(paramMap);
                distrMemberMap.put("create_time", memberDistrEntity.getCreateTime());
                if (memberIdList.contains(memberId) == true) {
                    distrMemberMap.put("fans_type", "直邀");
                } else {
                    distrMemberMap.put("fans_type", "间邀");
                }
                distrMemberList.add(distrMemberMap);
            }
        }
        for (Map<String, Object> map1 : distrMemberList) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            if (StringUtil.isNotEmpty(StringUtil.formatStr(map1.get("create_time")))) {
                Date date = (Date) map1.get("create_time");
                String res = simpleDateFormat.format(date);
                System.out.println(res);
            }
        }
        return distrMemberList;
    }


    @Override
    public void consumerFind_v1(Map<String, Object> paramsMap, ResultData result)
            throws BusinessException, SystemException, Exception {
        try {
            if (StringUtil.isBlank(paramsMap.get("member_id") + "")) {
                result.setResultData(null);
                return;
            }
            List<MDConsumer> mDConsumerList = consumerDao.selectMDConsumer(paramsMap);
            if (mDConsumerList.size() == 0) {
                throw new BusinessException(RspCode.MEMBER_NOT_EXIST, RspCode.MSG.get(RspCode.MEMBER_NOT_EXIST));
            }
            MDConsumer mDConsumer = mDConsumerList.get(0);
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("nick_name", StringUtil.formatStr(mDConsumer.getNick_name()));
            resultMap.put("sex_key", StringUtil.formatStr(mDConsumer.getSex_key()));
            resultMap.put("birthday", mDConsumer.getBirthday() == null ? ""
                    : DateUtil.date2Str(mDConsumer.getBirthday(), DateUtil.fmt_yyyyMMdd));
            String head_pic_path = StringUtil.formatStr(mDConsumer.getHead_pic_path());
            resultMap.put("head_pic_path", head_pic_path);
            List<String> url_list = new ArrayList<String>();
            url_list.add(head_pic_path);
            resultMap.put("doc_url", docUtil.imageUrlFind(url_list));
            resultMap.put("type", mDConsumer.getType());
            resultMap.put("level", mDConsumer.getLevel());
            resultMap.put("growth_value", mDConsumer.getGrowth_value());
            resultMap.put("mobile", StringUtil.formatStr(mDConsumer.getMobile()));
            // 10:52 2019/5/14 欧少辉 体验会员购买次数
            resultMap.put("member_recharge_cnt", mDConsumer.getMember_recharge_cnt());
            //查询用户临时绑定关系记录
            Map<String, Object> findMap = new HashMap<>();
            findMap.clear();
            findMap.put("member_id", paramsMap.get("member_id"));
            MDMemberDistrbutionInfo memberDistrbutionInfo = memberDistrbutionInfoDao.findMemberDistrInfo(findMap);
            if (memberDistrbutionInfo == null) {
                resultMap.put("invitation_mobile", "");
            } else {
                //查询推荐人信息
                findMap.clear();
                findMap.put("member_id", memberDistrbutionInfo.getParentId());
                List<MDConsumer> consumerList = consumerDao.selectMDConsumer(findMap);
                if (consumerList.size() == 0) {
                    resultMap.put("invitation_mobile", "");
                } else {
                    resultMap.put("invitation_mobile", consumerList.get(0).getMobile());
                }

            }

            if (mDConsumer.getVip_end_time() != null) {
                long vip_end_time = DateUtil
                        .parseToDate(DateUtil.date2Str(mDConsumer.getVip_end_time(), DateUtil.fmt_yyyyMMdd)).getTime();
                long nowDate = DateUtil.parseToDate(DateUtil.date2Str(new Date(), DateUtil.fmt_yyyyMMdd)).getTime();
                if (vip_end_time >= nowDate) {
                    resultMap.put("vip_end_time",
                            DateUtil.date2Str(mDConsumer.getVip_end_time(), DateUtil.fmt_yyyyMMdd));
                } else {
                    resultMap.put("vip_end_time", "");
                }
            } else {
                resultMap.put("vip_end_time", "");
            }
            //查询微信openid
            String user_service_url = PropertiesConfigUtil.getProperty("user_service_url");
            Map<String, String> requestData = new HashMap<String, String>();
            requestData.put("service", "infrastructure.userWechatFindByMobile");
            Map<String, String> params = new HashMap<String, String>();
            params.put("mobile", StringUtil.formatStr(mDConsumer.getMobile()));
            requestData.put("params", FastJsonUtil.toJson(params));
            String resultStr = HttpClientUtil.post(user_service_url, requestData);
            Map<String, Object> resultMapData = FastJsonUtil.jsonToMap(resultStr);

            String rsp_code = (String) resultMapData.get("rsp_code");
            if (!rsp_code.equals(RspCode.RESPONSE_SUCC)) {
                throw new BusinessException((String) resultMapData.get("error_code"), (String) resultMapData.get("error_msg"));
            }
            Map<String, Object> dataMap = (Map<String, Object>) resultMapData.get("data");
            if (null != dataMap && !dataMap.isEmpty()) {
                resultMap.put("openid", dataMap.get("openid"));
            }

            //查询是否是掌柜。
            Map<String, Object> tempMap = new HashMap<String, Object>();
            tempMap.clear();
            tempMap.put("member_id", mDConsumer.getConsumer_id());
            MDMemberDistribution entity = memberDistributionDao.getMemberDistributionInfo(tempMap);
            if ((null != entity && null != entity.getRegistLevel() && entity.getRegistLevel() == 3) &&
                    (null != entity && null != entity.getDistrLevel() && entity.getDistrLevel() == 3)) {
                resultMap.put("is_manager", "Y");
            } else {
                resultMap.put("is_manager", "N");
            }

            result.setResultData(resultMap);
        } catch (BusinessException e) {
            throw e;
        } catch (SystemException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void consumerPhoneUpdate(Map<String, Object> paramsMap, ResultData result)
            throws BusinessException, SystemException, Exception {
        ValidateUtil.validateParams(paramsMap, new String[]{"user_id", "mobile"});
        String mobile = StringUtil.formatStr(paramsMap.get("mobile"));
        List<MDUserMember> mDUserMemberList = memberDao.selectMDUserMember(paramsMap);
        if (mDUserMemberList.size() == 0) {
            throw new BusinessException(RspCode.MEMBER_NOT_EXIST, RspCode.MSG.get(RspCode.MEMBER_NOT_EXIST));
        }
        MDUserMember mDUserMember = mDUserMemberList.get(0);
        // 验证会员状态
        Map<String, Object> tempMap = new HashMap<String, Object>();
        tempMap.put("consumer_id", mDUserMember.getMember_id());
        List<MDConsumer> consumerList = consumerDao.selectMDConsumer(tempMap);
        if (consumerList.size() == 0) {
            throw new BusinessException(RspCode.MEMBER_NOT_EXIST, RspCode.MSG.get(RspCode.MEMBER_NOT_EXIST));
        }
        tempMap.put("mobile", mobile);
        consumerDao.updateMDConsumer(tempMap);
    }

    @Override
    public void memberDistributionManagerId(Map<String, Object> paramsMap, ResultData result)
            throws BusinessException, SystemException, Exception {
        List<String> managerList = memberDistributionDao.getMemberManagerId();
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("managerList", managerList);
        result.setResultData(resultMap);
    }

    @Override
    public void managerMemberFans(Map<String, Object> paramsMap, ResultData result)
            throws BusinessException, SystemException, Exception {
        ValidateUtil.validateParams(paramsMap, new String[]{"member_id"});
        //查询等于 topid  managerid， generalId
        List<Map<String, Object>> mapList = memberDistributionDao.getdistrMemberInfo(paramsMap);
        if (null != mapList && mapList.size() > 0) {
            for (Map<String, Object> map : mapList) {
                String memberId = StringUtil.formatStr(map.get("member_id"));
                String parent_id = StringUtil.formatStr(map.get("parent_id"));
                Date create_time = (Date) map.get("create_time");
                map.put("create_time", DateUtil.date2Str(create_time, DateUtil.fmt_yyyyMMddHHmmss));
                Map<String, Object> tempMap = new HashMap<String, Object>();
                tempMap.put("member_id", memberId);
                MDConsumer consumer = consumerDao.selectMDConsumerById(tempMap);
                map.put("mobile", consumer.getMobile());
                tempMap.clear();
                tempMap.put("member_id", parent_id);
                MDConsumer entity = consumerDao.selectMDConsumerById(tempMap);
                map.put("inviterMobile", entity.getMobile());
                map.put("fans_type", "次邀");
            }
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("list", mapList);
        result.setResultData(resultMap);
    }

    @Override
    public void consumerFindByPhone(Map<String, Object> paramsMap, ResultData result)
            throws BusinessException, SystemException, Exception {
        ValidateUtil.validateParams(paramsMap, new String[]{"mobile"});
        MDConsumer consumer = consumerDao.selectMDConsumerById(paramsMap);
        Map<String, Object> resultMap = new HashMap<>();
        if (null != consumer) {
            resultMap.put("type", consumer.getType());
            resultMap.put("consumer_id", consumer.getConsumer_id());
        }
        result.setResultData(resultMap);
    }

    @Override
    public void consumerRegistSimple(Map<String, Object> paramsMap, ResultData result)
            throws BusinessException, SystemException, Exception {
        try {
            ValidateUtil.validateParams(paramsMap,
                    new String[]{"user_id", "member_id", "mobile", "registered_date", "status"});
            String member_id = paramsMap.get("member_id") + "";
            String user_id = paramsMap.get("user_id") + "";
            String mobile = StringUtil.formatStr(paramsMap.get("mobile"));
            Map<String, Object> tempMap = new HashMap<String, Object>();
            boolean flag = false;
            tempMap.clear();
            tempMap.put("mobile", mobile);
            MDConsumer mDConsumer = consumerDao.selectMDConsumerById(tempMap);
            if (null == mDConsumer) {
                // 生成消费者会员id
                mDConsumer = new MDConsumer();
                Date date = new Date();
                BeanConvertUtil.mapToBean(mDConsumer, paramsMap);
                mDConsumer.setConsumer_id(member_id);
                mDConsumer.setSex_key(Constant.SEX_03);
                mDConsumer.setCreated_date(date);
                mDConsumer.setModified_date(date);
                mDConsumer.setIs_vip_expired(0);
                String date2Str = DateUtil.date2Str(new Date(), DateUtil.fmt_yyyyMMdd);
                date2Str = DateUtil.addDate(date2Str, DateUtil.fmt_yyyyMMdd, 1, 1);
                Date vipEnddate = DateUtil.str2Date(date2Str, DateUtil.fmt_yyyyMMdd);
                mDConsumer.setGrowth_value(68);
                mDConsumer.setVip_start_time(new Date());
                mDConsumer.setVip_end_time(vipEnddate);
                mDConsumer.setType(0);
                consumerDao.insertMDConsumer(mDConsumer);
                Map<String, Object> logMap = new HashMap<String, Object>();
                logMap.put("log_id", IDUtil.getUUID());
                logMap.put("consumer_id", member_id);
                logMap.put("category", "数据同步");
                logMap.put("tracked_date", new Date());
                logMap.put("event", "消费者同步注册");
                consumerDao.insertMDConsumerLog(logMap);
                // 删除原有的关系
                tempMap.clear();
                tempMap.put("member_id", member_id);
                tempMap.put("member_type_key", CommonConstant.MEMBER_TYPE_CONSUMER);
                memberService.userMemberRelRemove(tempMap);
                // 重新设置关系
                MDUserMember mDUserMember = new MDUserMember();
                mDUserMember.setMember_id(member_id);
                mDUserMember.setUser_id(user_id);
                mDUserMember.setMember_type_key(CommonConstant.MEMBER_TYPE_CONSUMER);
                mDUserMember.setIs_admin("Y");
                memberDao.insertMDUserMember(mDUserMember);
            } else {
                Map<String, Object> temp = new HashMap<>();
                Date date = new Date();
                temp.put("modified_date", date);
                temp.put("is_vip_expired", 0);
                String date2Str = DateUtil.date2Str(new Date(), DateUtil.fmt_yyyyMMdd);
                date2Str = DateUtil.addDate(date2Str, DateUtil.fmt_yyyyMMdd, 1, 1);
                Date vipEnddate = DateUtil.str2Date(date2Str, DateUtil.fmt_yyyyMMdd);
                temp.put("vip_start_time", date);
                temp.put("vip_end_time", vipEnddate);
                temp.put("type", 0);
                temp.put("growth_value", 68);
                temp.put("consumer_id", mDConsumer.getConsumer_id());
                consumerDao.updateMDConsumer(temp);// 删除原有的关系
                temp.clear();
                temp.put("member_id", mDConsumer.getConsumer_id());
                temp.put("member_type_key", CommonConstant.MEMBER_TYPE_CONSUMER);
                memberService.userMemberRelRemove(temp);
                // 重新设置关系
                MDUserMember mDUserMember = new MDUserMember();
                mDUserMember.setMember_id(mDConsumer.getConsumer_id());
                mDUserMember.setUser_id(user_id);
                mDUserMember.setMember_type_key(CommonConstant.MEMBER_TYPE_CONSUMER);
                mDUserMember.setIs_admin("Y");
                memberDao.insertMDUserMember(mDUserMember);
                member_id = mDConsumer.getConsumer_id();
            }
            //直接写入分销
            this.memberDistrbutionAdd(member_id, mobile);
            //赠送12张礼券
            this.handleMemberGift(member_id);
            //插入会员贝壳号表
            this.memberInviteCodeSave(member_id);
        } catch (BusinessException e) {
            throw e;
        } catch (SystemException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void consumerIsVip(Map<String, Object> paramsMap, ResultData result)
            throws BusinessException, SystemException, Exception {
        ValidateUtil.validateParams(paramsMap, new String[]{"mobile"});
        Map<String, Object> resultMap = new HashMap<>();
        MDConsumer baseInfo = consumerDao.selectMDConsumerBaseInfo(paramsMap);
        if (null == baseInfo || null == baseInfo.getVip_end_time()) {
            resultMap.put("is_vip", "N");
        } else {
            resultMap.put("is_vip", "Y");
        }
        result.setResultData(resultMap);
    }

    @Override
    public void memberDistributionInfoById(Map<String, Object> paramsMap, ResultData result) throws Exception {
        ValidateUtil.validateParams(paramsMap, new String[]{"member_id"});
        Map<String, Object> resultMap = new HashMap<>();
        MDMemberDistrbutionInfo memberDistrInfo = memberDistrbutionInfoDao.findMemberDistrInfoById(paramsMap);
		if(memberDistrInfo!=null) {
			String pId = memberDistrInfo.getParentId();
			if(StringUtils.isBlank(pId)){
				logger.info("editMemberCoupon--member_id没有父ID, member_id是: " + paramsMap.get("member_id"));
				throw new BusinessException("没有父ID", "没有父ID");
			}
			resultMap.put("parent_id", memberDistrInfo.getParentId());
			result.setResultData(resultMap);
		}else{
			logger.info("editMemberCoupon--member_id没有父ID, member_id是: " + paramsMap.get("member_id"));
			throw new BusinessException("没有父ID", "没有父ID");
		}
	}


}
