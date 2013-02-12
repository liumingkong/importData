package com.weibo.data;

public class CacheInfo {

	private String cachename;
	private String cacheHits;
	private String onDiskHits;
	private String inMemoryHits;
	private String misses;
	private String size;
	private String averageGetTime;
	private String evictionCount;
	/**
	 * @param cachename the cachename to set
	 */
	public void setCachename(String cachename) {
		this.cachename = cachename;
	}
	/**
	 * @return the cachename
	 */
	public String getCachename() {
		return cachename;
	}
	/**
	 * @param cacheHits the cacheHits to set
	 */
	public void setCacheHits(String cacheHits) {
		this.cacheHits = cacheHits;
	}
	/**
	 * @return the cacheHits
	 */
	public String getCacheHits() {
		return cacheHits;
	}
	/**
	 * @param onDiskHits the onDiskHits to set
	 */
	public void setOnDiskHits(String onDiskHits) {
		this.onDiskHits = onDiskHits;
	}
	/**
	 * @return the onDiskHits
	 */
	public String getOnDiskHits() {
		return onDiskHits;
	}
	/**
	 * @param inMemoryHits the inMemoryHits to set
	 */
	public void setInMemoryHits(String inMemoryHits) {
		this.inMemoryHits = inMemoryHits;
	}
	/**
	 * @return the inMemoryHits
	 */
	public String getInMemoryHits() {
		return inMemoryHits;
	}
	/**
	 * @param misses the misses to set
	 */
	public void setMisses(String misses) {
		this.misses = misses;
	}
	/**
	 * @return the misses
	 */
	public String getMisses() {
		return misses;
	}
	/**
	 * @param size the size to set
	 */
	public void setSize(String size) {
		this.size = size;
	}
	/**
	 * @return the size
	 */
	public String getSize() {
		return size;
	}
	/**
	 * @param averageGetTime the averageGetTime to set
	 */
	public void setAverageGetTime(String averageGetTime) {
		this.averageGetTime = averageGetTime;
	}
	/**
	 * @return the averageGetTime
	 */
	public String getAverageGetTime() {
		return averageGetTime;
	}
	/**
	 * @param evictionCount the evictionCount to set
	 */
	public void setEvictionCount(String evictionCount) {
		this.evictionCount = evictionCount;
	}
	/**
	 * @return the evictionCount
	 */
	public String getEvictionCount() {
		return evictionCount;
	}
}
