package com.weibo.temp;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

import com.weibo.data.CacheInfo;
import com.weibo.data.JidCache;
import com.weibo.util.C3P0Pool;
import com.weibo.util.Util;


public class JidStatisticsProc {

	private static String jidstr = "statistics";
	private static String pattern = "\\s+";	
	private Integer jidBlockCount;
	private String tagserver;
	private File file;
		
	private HashMap<Integer,JidCache> jidHash = new HashMap<Integer,JidCache>();
	
	JidStatisticsProc(File file){
		this.file = file;
		tagserver = Util.getFileInfo(file);
	}
	
	
	private boolean cacheJidStatistics()throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		// 运行标志位
		boolean runFlag = true;
		int id=0;
		
		// 2012-06-06 19:54:37 [WARN] JID statistics: JID count: 6 nonumCount: 6 [  name = JIDCache cacheHits = 5 onDiskHits = 0 inMemoryHits = 5 misses = 6 size = 7 averageGetTime = 0.0 evictionCount = 0 ][  name = stringprepCache cacheHits = 0 onDiskHits = 0 inMemoryHits = 0 misses = 0 size = 0 averageGetTime = 0.0 evictionCount = 0 ]
		while (null != (line = br.readLine())) {
			
			if(line.indexOf(jidstr)>=0){
				JidCache jidCache = new JidCache();
				line = line.replace("[WARN]", "");
				int indexbc = line.indexOf("[");
				if(indexbc <= 0){
					runFlag = false;
					break;
				}				
				
				// 2012-06-06 19:54:37 JID statistics: JID count: 6 nonumCount: 6 
				String basicStr = line.substring(0,indexbc);
				String[] spBasicStr = basicStr.split(pattern);
				if(spBasicStr.length<9){
					runFlag = false;
					break;
				}
				jidCache.setTimestamp(spBasicStr[1]);
				jidCache.setJidCount(spBasicStr[6]);
				jidCache.setNonumCount(spBasicStr[8]);				
								
				// store cache information
				String cacheStr = line.substring(indexbc);
				int indexCache = cacheStr.indexOf("]")+1;
				// [  name = JIDCache cacheHits = 5 onDiskHits = 0 inMemoryHits = 5 misses = 6 size = 7 averageGetTime = 0.0 evictionCount = 0 ]
				String cacheStr1 = cacheStr.substring(0,indexCache);
				// [  name = stringprepCache cacheHits = 0 onDiskHits = 0 inMemoryHits = 0 misses = 0 size = 0 averageGetTime = 0.0 evictionCount = 0 ]
				String cacheStr2 = cacheStr.substring(indexCache);
				String spCacheStr1[] = cacheStr1.split(pattern);
				if(spCacheStr1.length < 19){
					runFlag = false;
					break;
				}		
				CacheInfo JIDCache = jidCache.getJIDCache();				
				JIDCache.setCachename(spCacheStr1[3]);
				JIDCache.setCacheHits(spCacheStr1[6]);
				JIDCache.setOnDiskHits(spCacheStr1[9]);
				JIDCache.setInMemoryHits(spCacheStr1[12]);
				JIDCache.setMisses(spCacheStr1[15]);
				JIDCache.setSize(spCacheStr1[18]);
				JIDCache.setAverageGetTime(spCacheStr1[21]);
				JIDCache.setEvictionCount(spCacheStr1[24]);
				
				String spCacheStr2[] = cacheStr2.split(pattern);
				if(spCacheStr2.length < 19){
					runFlag = false;
					break;
				}	
				CacheInfo StringprepCache = jidCache.getStringprepCache();
				StringprepCache.setCachename(spCacheStr2[3]);
				StringprepCache.setCacheHits(spCacheStr2[6]);
				StringprepCache.setOnDiskHits(spCacheStr2[9]);
				StringprepCache.setInMemoryHits(spCacheStr2[12]);
				StringprepCache.setMisses(spCacheStr2[15]);
				StringprepCache.setSize(spCacheStr2[18]);
				StringprepCache.setAverageGetTime(spCacheStr2[21]);
				StringprepCache.setEvictionCount(spCacheStr2[24]);
				
				if(runFlag == false){
					break;
				}
				jidHash.put(id,jidCache);
				id ++;
			}		
		}		
		jidBlockCount = id;
		br.close();
		return runFlag;
	}
	
	private void jidCacheSave(String cachename) throws IOException{
		
		Connection conn = C3P0Pool.getConnection();
		PreparedStatement ps = null;
		String sql = "insert into jidcache(tagserver,cachename,data) values(?,?,?) ";
		String data;
		
		System.out.println("向jidcache表插入数据");
		for(int i=0;i<jidBlockCount;i++){
			data = getData(cachename,i);
			try {
				ps = conn.prepareStatement(sql);
				ps.setObject(1,tagserver);
				ps.setObject(2,cachename);
				ps.setObject(3,data);
				ps.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		C3P0Pool.free(ps, conn);
	}
	
	private String getData(String cachename,int i){
		String data = null;
		JidCache jidCache = jidHash.get(i);	
		CacheInfo cache;
		if("total".equalsIgnoreCase(cachename)){
			data = "{timestamp:"+"\""+jidCache.getTimestamp()+"\""+","+
					"\""+"JIDcount"+"\""+":"+jidCache.getJidCount()+","+
					"\""+"nonumCount"+"\""+":"+jidCache.getJidCount()+
					"}";		
		}else if("stringprepcache".equalsIgnoreCase(cachename)){
			cache = jidCache.getStringprepCache();
			data = "{timestamp:"+"\""+jidCache.getTimestamp()+"\""+","+
					"\""+"cacheHits"+"\""+":"+cache.getCacheHits()+","+
					"\""+"onDiskHits"+"\""+":"+cache.getOnDiskHits()+","+
					"\""+"inMemoryHits"+"\""+":"+cache.getInMemoryHits()+","+
					"\""+"misses"+"\""+":"+cache.getMisses()+","+
					"\""+"size"+"\""+":"+cache.getSize()+","+
					"\""+"averageGetTime"+"\""+":"+cache.getAverageGetTime()+","+
					"\""+"evictionCount"+"\""+":"+cache.getEvictionCount()+
					"}";			
		}else if("jidcache".equalsIgnoreCase(cachename)){
			cache = jidCache.getJIDCache();
			data = "{timestamp:"+"\""+jidCache.getTimestamp()+"\""+","+
					"\""+"cacheHits"+"\""+":"+cache.getCacheHits()+","+
					"\""+"onDiskHits"+"\""+":"+cache.getOnDiskHits()+","+
					"\""+"inMemoryHits"+"\""+":"+cache.getInMemoryHits()+","+
					"\""+"misses"+"\""+":"+cache.getMisses()+","+
					"\""+"size"+"\""+":"+cache.getSize()+","+
					"\""+"averageGetTime"+"\""+":"+cache.getAverageGetTime()+","+
					"\""+"evictionCount"+"\""+":"+cache.getEvictionCount()+
					"}";
		}		
		return data;		
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		File file = new File("C:\\Users\\liumingkong\\Desktop\\stress\\06071831-123.126.54.33\\jidcache.log");		
		JidStatisticsProc jsp = new JidStatisticsProc(file);		
		jsp.cacheJidStatistics();
		jsp.jidCacheSave("total");
		jsp.jidCacheSave("stringprepcache");
		jsp.jidCacheSave("jidcache");
		
	}

}
