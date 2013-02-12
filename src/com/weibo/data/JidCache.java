package com.weibo.data;


public class JidCache {

	private String timestamp;
	private String JidCount;
	private String nonumCount;
	private CacheInfo JIDCache = new CacheInfo();
	private CacheInfo stringprepCache = new CacheInfo();

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	/**
	 * @return the timestamp
	 */
	public String getTimestamp() {
		return timestamp;
	}
	/**
	 * @param jidCount the jidCount to set
	 */
	public void setJidCount(String jidCount) {
		JidCount = jidCount;
	}
	/**
	 * @return the jidCount
	 */
	public String getJidCount() {
		return JidCount;
	}
	/**
	 * @param nonumCount the nonumCount to set
	 */
	public void setNonumCount(String nonumCount) {
		this.nonumCount = nonumCount;
	}
	/**
	 * @return the nonumCount
	 */
	public String getNonumCount() {
		return nonumCount;
	}
	/**
	 * @param jIDCache the jIDCache to set
	 */
	public void setJIDCache(CacheInfo jIDCache) {
		JIDCache = jIDCache;
	}
	
	/**
	 * @return the jIDCache
	 */
	public CacheInfo getJIDCache() {
		return JIDCache;
	}
	/**
	 * @param stringprepCache the stringprepCache to set
	 */
	public void setStringprepCache(CacheInfo stringprepCache) {
		this.stringprepCache = stringprepCache;
	}
	/**
	 * @return the stringprepCache
	 */
	public CacheInfo getStringprepCache() {
		return stringprepCache;
	}

}
