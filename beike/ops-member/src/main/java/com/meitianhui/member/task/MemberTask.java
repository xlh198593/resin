package com.meitianhui.member.task;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.meitianhui.member.service.MemberService;
import com.meitianhui.member.service.MemberTaskService;

@Component
public class MemberTask {
	
	private static final Logger logger = Logger.getLogger(MemberTask.class);
	
	@Autowired
	public   MemberService   memberService;
	
	@Autowired
	public  MemberTaskService   memberTaskService;
	
	/**
	 * 定时任务 ，每天比较会员结束时间，每天执行一次
	 * @throws Exception
	 */
//	@Scheduled(cron = "0 0 10 * * ?")
	 public void memberGrowthValue() throws Exception{
		memberService.handleMemberGrowth();
    }
	
	
	/**
	 * 定时任务 ，判断成为掌柜
	 * @throws Exception
	 */
	//@Scheduled(cron = "0 */1 * * * ?")
	 @Scheduled(cron = "0 0 0 * * ?")
	 public void memberDistribtionEdit() throws Exception{
		memberTaskService.handledMemberDistribtion();
    }
	/**
	 * 定时任务，把到期的体验会员改为普通会员
	 * @throws Exception
	 */
	// 每天凌晨执行
	//@Scheduled(cron = "0 0 0 * * *")
	@Scheduled(cron = "0 0/2 * * * *")
	public void consumerMemberEdit() throws Exception{
		memberTaskService.consumerMemberEdit();
	}
}
