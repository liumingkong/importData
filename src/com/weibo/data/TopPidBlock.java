package com.weibo.data;

import java.util.LinkedList;


// 用来标识一组TOP信息
// 一组top信息包括两个部分：top信息块+pid信息块
public class TopPidBlock {
	
	// top信息块，用TopInfo对象来描述
	private TopInfo topInfo;
	// pid信息块，用链表来存储PID信息块
	// 每个链表单元采用一个PidInfo对象来记录每行的PID信息
	private LinkedList<PidInfo> pidInfoList = new LinkedList<PidInfo>();
	
	public TopPidBlock(){
		
	}
	
	/**
	 * @param topInfo the topInfo to set
	 */
	public void setTopInfo(TopInfo topInfo) {
		this.topInfo = topInfo;
	}
	/**
	 * @return the topInfo
	 */
	public TopInfo getTopInfo() {
		return topInfo;
	}
	/**
	 * @param pid the pid to set
	 */
	public void setPidInfoList(LinkedList<PidInfo> pidInfoList) {
		this.pidInfoList = pidInfoList;
	}
	/**
	 * @return the pid
	 */
	public LinkedList<PidInfo> getPidInfoList() {
		return pidInfoList;
	}
}
