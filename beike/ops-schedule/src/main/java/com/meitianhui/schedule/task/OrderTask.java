package com.meitianhui.schedule.task;

public interface OrderTask {

	
	/**
	 * 我要批超时订单自动取消
	 */
	public void fgOrderAutoCancel();
	
	/**
	 * 新自营商品订单取消
	 */
	public void fgOrderForOwnAutoCancel();
	
	
	/**
	 * 我要批超时订单自动取消
	 */
	public void proPsGroupOrder();
	

	/**
	 * 我要批超时订单自动确认收货
	 */
	public void psOrderAutoReceived();

	/**
	 * 精选特卖超时订单自动确认收货
	 */
	public void pcOrderAutoReceived();
	
	/**
	 * 伙拼团验证是否成团
	 */
	public void tsActivityCheck();

	/**
	 * 伙拼团订单超时自动收货
	 */
	public void tsOrderAutoReceived();
	
	/**
	 * 处理过期任务
	 */
	public void timeOutOdTaskAutoClosed();
	
	/**
	 * 每个星期天的晚上23-24点,每20分钟执行一次
	 */
	void transmaticByFgOrderCommission();

	/**
	 * 贝壳商城商品订单定时任务 每1分账执行一次
	 */
	void beikeMallGoodsForOwnAutoCancel();
	
	/**
	 * 贝壳商城商品订单定时任务 每1分账执行一次
	 */
	void hongBaoGoodsForOwnAutoCancel();
	
}
