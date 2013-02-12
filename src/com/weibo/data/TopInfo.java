package com.weibo.data;

public class TopInfo {

	// 当前时间
	private String timeline;
	// 系统运行时间，格式为时分
	private String systemRuntime;
	// 当前登录用户数
	private int loginUser;
	// 系统负载，即任务队列的平均长度
	// 三个数值分别为 1分钟，5分钟，15分钟前到现在的平均值
	private String loadAverage1;
	private String loadAverage5;
	private String loadAverage15;
	
	// 进场总数,正运行进程，睡眠的，停止的，僵尸的进程数
	private int taskTotal;
	private int taskRunning;
	private int taskSleeping;	
	private int taskStopped;	
	private int taskZombie;
	
	// 用户空间占用CPU百分比
	private String us;
	// 内核空间占用CPU百分比
	private String sy;
	// 用户进程空间内改变过优先级的进程占用CPU百分比
	private String ni;
	// id空闲百分比
	private String id;
	// 等待输入输出的CPU时间百分比
	private String wa;
	private String hi;
	private String si;
	
	// 物理内存总量
	private String memTotal;
	// 使用的物理内存总量
	private String memUsed;
	// 空闲内存总量
	private String memFree;
	// 用作内核缓存的内存量
	private String buffers;
	// 交换区总量
	private String swapTotal;
	// 使用的交换区总量
	private String swapUsed;
	// 空闲交换区总量
	private String swapFree;
	// 缓存的交换区总量
	private String cached;

	/**
	 * @param timeline the timeline to set
	 */
	public void setTimeline(String timeline) {
		this.timeline = timeline;
	}
	/**
	 * @return the timeline
	 */
	public String getTimeline() {
		return timeline;
	}
	/**
	 * @param systemRuntime the systemRuntime to set
	 */
	public void setSystemRuntime(String systemRuntime) {
		this.systemRuntime = systemRuntime;
	}
	/**
	 * @return the systemRuntime
	 */
	public String getSystemRuntime() {
		return systemRuntime;
	}
	/**
	 * @param loginUser the loginUser to set
	 */
	public void setLoginUser(int loginUser) {
		this.loginUser = loginUser;
	}
	/**
	 * @return the loginUser
	 */
	public int getLoginUser() {
		return loginUser;
	}
	/**
	 * @param loadAverage1 the loadAverage1 to set
	 */
	public void setLoadAverage1(String loadAverage1) {
		this.loadAverage1 = loadAverage1;
	}
	/**
	 * @return the loadAverage1
	 */
	public String getLoadAverage1() {
		return loadAverage1;
	}
	/**
	 * @param loadAverage5 the loadAverage5 to set
	 */
	public void setLoadAverage5(String loadAverage5) {
		this.loadAverage5 = loadAverage5;
	}
	/**
	 * @return the loadAverage5
	 */
	public String getLoadAverage5() {
		return loadAverage5;
	}
	/**
	 * @param loadAverage15 the loadAverage15 to set
	 */
	public void setLoadAverage15(String loadAverage15) {
		this.loadAverage15 = loadAverage15;
	}
	/**
	 * @return the loadAverage15
	 */
	public String getLoadAverage15() {
		return loadAverage15;
	}
	/**
	 * @param taskTotal the taskTotal to set
	 */
	public void setTaskTotal(int taskTotal) {
		this.taskTotal = taskTotal;
	}
	/**
	 * @return the taskTotal
	 */
	public int getTaskTotal() {
		return taskTotal;
	}
	/**
	 * @param taskRunning the taskRunning to set
	 */
	public void setTaskRunning(int taskRunning) {
		this.taskRunning = taskRunning;
	}
	/**
	 * @return the taskRunning
	 */
	public int getTaskRunning() {
		return taskRunning;
	}
	/**
	 * @param taskSleeping the taskSleeping to set
	 */
	public void setTaskSleeping(int taskSleeping) {
		this.taskSleeping = taskSleeping;
	}
	/**
	 * @return the taskSleeping
	 */
	public int getTaskSleeping() {
		return taskSleeping;
	}
	/**
	 * @param taskStopped the taskStopped to set
	 */
	public void setTaskStopped(int taskStopped) {
		this.taskStopped = taskStopped;
	}
	/**
	 * @return the taskStopped
	 */
	public int getTaskStopped() {
		return taskStopped;
	}
	/**
	 * @param taskZombie the taskZombie to set
	 */
	public void setTaskZombie(int taskZombie) {
		this.taskZombie = taskZombie;
	}
	/**
	 * @return the taskZombie
	 */
	public int getTaskZombie() {
		return taskZombie;
	}
	/**
	 * @param us the us to set
	 */
	public void setUs(String us) {
		this.us = us;
	}
	/**
	 * @return the us
	 */
	public String getUs() {
		return us;
	}
	/**
	 * @param sy the sy to set
	 */
	public void setSy(String sy) {
		this.sy = sy;
	}
	/**
	 * @return the sy
	 */
	public String getSy() {
		return sy;
	}
	/**
	 * @param ni the ni to set
	 */
	public void setNi(String ni) {
		this.ni = ni;
	}
	/**
	 * @return the ni
	 */
	public String getNi() {
		return ni;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param hi the hi to set
	 */
	public void setHi(String hi) {
		this.hi = hi;
	}
	/**
	 * @return the hi
	 */
	public String getHi() {
		return hi;
	}
	/**
	 * @param wa the wa to set
	 */
	public void setWa(String wa) {
		this.wa = wa;
	}
	/**
	 * @return the wa
	 */
	public String getWa() {
		return wa;
	}
	/**
	 * @param memTotal the memTotal to set
	 */
	public void setMemTotal(String memTotal) {
		this.memTotal = memTotal;
	}
	/**
	 * @return the memTotal
	 */
	public String getMemTotal() {
		return memTotal;
	}
	/**
	 * @param memFree the memFree to set
	 */
	public void setMemFree(String memFree) {
		this.memFree = memFree;
	}
	/**
	 * @return the memFree
	 */
	public String getMemFree() {
		return memFree;
	}
	/**
	 * @param si the si to set
	 */
	public void setSi(String si) {
		this.si = si;
	}
	/**
	 * @return the si
	 */
	public String getSi() {
		return si;
	}
	/**
	 * @param memUsed the memUsed to set
	 */
	public void setMemUsed(String memUsed) {
		this.memUsed = memUsed;
	}
	/**
	 * @return the memUsed
	 */
	public String getMemUsed() {
		return memUsed;
	}
	/**
	 * @param buffers the buffers to set
	 */
	public void setBuffers(String buffers) {
		this.buffers = buffers;
	}
	/**
	 * @return the buffers
	 */
	public String getBuffers() {
		return buffers;
	}
	/**
	 * @param swapTotal the swapTotal to set
	 */
	public void setSwapTotal(String swapTotal) {
		this.swapTotal = swapTotal;
	}
	/**
	 * @return the swapTotal
	 */
	public String getSwapTotal() {
		return swapTotal;
	}
	/**
	 * @param swapUsed the swapUsed to set
	 */
	public void setSwapUsed(String swapUsed) {
		this.swapUsed = swapUsed;
	}
	/**
	 * @return the swapUsed
	 */
	public String getSwapUsed() {
		return swapUsed;
	}
	/**
	 * @param swapFree the swapFree to set
	 */
	public void setSwapFree(String swapFree) {
		this.swapFree = swapFree;
	}
	/**
	 * @return the swapFree
	 */
	public String getSwapFree() {
		return swapFree;
	}
	/**
	 * @param cached the cached to set
	 */
	public void setCached(String cached) {
		this.cached = cached;
	}
	/**
	 * @return the cached
	 */
	public String getCached() {
		return cached;
	}
	
}
