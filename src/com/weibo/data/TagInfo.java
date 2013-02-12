package com.weibo.data;

// 代表每行的信息
public class TagInfo {

	private String tagName;
	private String tagAvg;
	private String tagMax;
	private String tagMin;
	private String tagStdDev;
	private String tagCount;
	/**
	 * @param tagName the tagName to set
	 */
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	/**
	 * @return the tagName
	 */
	public String getTagName() {
		return tagName;
	}
	/**
	 * @param tagAvg the tagAvg to set
	 */
	public void setTagAvg(String tagAvg) {
		this.tagAvg = tagAvg;
	}
	/**
	 * @return the tagAvg
	 */
	public String getTagAvg() {
		return tagAvg;
	}
	/**
	 * @param tagMax the tagMax to set
	 */
	public void setTagMax(String tagMax) {
		this.tagMax = tagMax;
	}
	/**
	 * @return the tagMax
	 */
	public String getTagMax() {
		return tagMax;
	}
	/**
	 * @param tagMin the tagMin to set
	 */
	public void setTagMin(String tagMin) {
		this.tagMin = tagMin;
	}
	/**
	 * @return the tagMin
	 */
	public String getTagMin() {
		return tagMin;
	}
	/**
	 * @param tagStdDev the tagStdDev to set
	 */
	public void setTagStdDev(String tagStdDev) {
		this.tagStdDev = tagStdDev;
	}
	/**
	 * @return the tagStdDev
	 */
	public String getTagStdDev() {
		return tagStdDev;
	}
	/**
	 * @param tagCount the tagCount to set
	 */
	public void setTagCount(String tagCount) {
		this.tagCount = tagCount;
	}
	/**
	 * @return the tagCount
	 */
	public String getTagCount() {
		return tagCount;
	}
}
