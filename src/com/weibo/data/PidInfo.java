package com.weibo.data;

public class PidInfo {
	
	// 进程ID
	private String pid;
	// 进程的所有者的用户名
	private String user;
	// 优先级
	private String pr;
	// nice值
	private String ni;
	// 进程使用的虚拟内存总量，单位kb
	// virt = swap + res
	private String virt;
	// 进程使用的虚拟内存中，未被换出的大小，单位kb
	// res = code + data
	private String res;
	// 共享内存大小，单位kb
	private String shr;
	// 进程的状态，即S
	private String state;
	// 上次更新到现在的CPU时间占用的百分比
	private String cpu ;
	// 进程使用的物理内存百分比
	private String mem ;
	// 进程使用的物理内存百分比
	private String time;
	// 命令名/命令行
	private String command;
	
	/**
	 * @param id the id to set
	 */
	public void setPid(String pid) {
		this.pid = pid;
	}
	/**
	 * @return the id
	 */
	public String getPid() {
		return pid;
	}
	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}
	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}
	/**
	 * @param pr the pr to set
	 */
	public void setPr(String pr) {
		this.pr = pr;
	}
	/**
	 * @return the pr
	 */
	public String getPr() {
		return pr;
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
	 * @param virt the virt to set
	 */
	public void setVirt(String virt) {
		this.virt = virt;
	}
	/**
	 * @return the virt
	 */
	public String getVirt() {
		return virt;
	}
	/**
	 * @param res the res to set
	 */
	public void setRes(String res) {
		this.res = res;
	}
	/**
	 * @return the res
	 */
	public String getRes() {
		return res;
	}
	/**
	 * @param shr the shr to set
	 */
	public void setShr(String shr) {
		this.shr = shr;
	}
	/**
	 * @return the shr
	 */
	public String getShr() {
		return shr;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}
	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}
	/**
	 * @param cpu the cpu to set
	 */
	public void setCpu(String cpu) {
		this.cpu = cpu;
	}
	/**
	 * @return the cpu
	 */
	public String getCpu() {
		return cpu;
	}
	/**
	 * @param mem the mem to set
	 */
	public void setMem(String mem) {
		this.mem = mem;
	}
	/**
	 * @return the mem
	 */
	public String getMem() {
		return mem;
	}
	/**
	 * @param time the time to set
	 */
	public void setTime(String time) {
		this.time = time;
	}
	/**
	 * @return the time
	 */
	public String getTime() {
		return time;
	}
	/**
	 * @param command the command to set
	 */
	public void setCommand(String command) {
		this.command = command;
	}
	/**
	 * @return the command
	 */
	public String getCommand() {
		return command;
	}	
}
