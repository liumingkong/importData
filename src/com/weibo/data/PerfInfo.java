package com.weibo.data;

import java.util.LinkedList;

public class PerfInfo {

	private PerfTimeInfo perfTimeInfo;
	private LinkedList<TagInfo> tagList = new LinkedList<TagInfo>();
	/**
	 * @param taglist the taglist to set
	 */
	public void setTagList(LinkedList<TagInfo> taglist) {
		this.tagList = taglist;
	}
	/**
	 * @return the taglist
	 */
	public LinkedList<TagInfo> getTagList() {
		return tagList;
	}

	/**
	 * @param perfTimeInfo the perfTimeInfo to set
	 */
	public void setPerfTimeInfo(PerfTimeInfo perfTimeInfo) {
		this.perfTimeInfo = perfTimeInfo;
	}
	/**
	 * @return the perfTimeInfo
	 */
	public PerfTimeInfo getPerfTimeInfo() {
		return perfTimeInfo;
	}
}
