package com.meitianhui.schedule.task;

/**
 * 会员任务
 * @author Tiny
 *
 */
public interface MemberTask {

	/**
	 * 冻结服务费
	 */
	public void freezeServiceFree();
	
	
	/**
	 * 处理服务费
	 */
	public void proServiceFree();
	
	/**
	 * 锁定门店对应的助教申请单超过三天不处理系统自动改为驳回状态
	 */
	public void assistantServiceFree();

	/**
	 * 会员每日自动获取成长值
	 */
	void memberServiceFree();
	
	/**
	 * 分销会员达标升为掌柜
	 */
	public  void memberDistribtionEdit();

}
