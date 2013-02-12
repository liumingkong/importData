package com.weibo.temp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;

import com.weibo.data.CacheSize;
import com.weibo.util.C3P0Pool;
import com.weibo.util.Util;

public class CacheSizeProc {

	private static String jidstr = "Checking cache sizes";
	private static String pattern = "\\s+";	
	private Integer cachesizeCount;
	private String tagserver;
	private File file;
	
	private HashMap<Integer,CacheSize> cacheSizeHash = new HashMap<Integer,CacheSize>();
	
	CacheSizeProc(File file){
		this.file = file;
		tagserver = Util.getFileInfo(file);
	}
	
	
	private boolean cacheSize()throws IOException{
		
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		// 运行标志位
		boolean runFlag = true;
		int id=0;
		
		// 2012-06-06 19:54:37 [WARN] JID statistics: JID count: 6 nonumCount: 6 [  name = JIDCache cacheHits = 5 onDiskHits = 0 inMemoryHits = 5 misses = 6 size = 7 averageGetTime = 0.0 evictionCount = 0 ][  name = stringprepCache cacheHits = 0 onDiskHits = 0 inMemoryHits = 0 misses = 0 size = 0 averageGetTime = 0.0 evictionCount = 0 ]
		while (null != (line = br.readLine())) {
			
			if(line.indexOf(jidstr)>=0){
				CacheSize cacheSize = new CacheSize();				
				int indexInfo = line.indexOf("Favicon");
				if(indexInfo <= 0){
					runFlag = false;
					break;
				}			
				
				//
				String baseStr = line.substring(0,indexInfo);
				String[] spBaseStr = baseStr.split(pattern);				
				if(spBaseStr.length<3){
					runFlag = false;
					break;
				}
				cacheSize.setTimeline(spBaseStr[1]); 			
								
				// store cache information
				String infoStr = line.substring(indexInfo);	
				String spInfoStr[] = infoStr.split(",");
				if(spInfoStr.length<28){
					runFlag = false;
					break;
				}
				cacheSize.setFaviconHits(getValue(spInfoStr[0]));
				cacheSize.setRoutingUserSessions(getValue(spInfoStr[1]));
				cacheSize.setValidatedDomains(getValue(spInfoStr[2]));
				cacheSize.setRoutingUsersCache(getValue(spInfoStr[3]));
				cacheSize.setMulticastService(getValue(spInfoStr[4]));
				cacheSize.setRoutingAnonymousUsersCache(getValue(spInfoStr[5]));
				cacheSize.setComponentsSessions(getValue(spInfoStr[6]));
				cacheSize.setEntityCapabilitiesPendingHashes(getValue(spInfoStr[7]));
				cacheSize.setEntityCapabilitiesUsers(getValue(spInfoStr[8]));
				cacheSize.setLoginFailIp(getValue(spInfoStr[9]));
				cacheSize.setRoster(getValue(spInfoStr[10]));
				cacheSize.setSessionsbyHostname(getValue(spInfoStr[11]));
				cacheSize.setRemoteUsersExistence(getValue(spInfoStr[12]));
				cacheSize.setVCard(getValue(spInfoStr[13]));
				cacheSize.setOfflineMessageCount(getValue(spInfoStr[14]));
				cacheSize.setDiscoServerItems(getValue(spInfoStr[15]));
				cacheSize.setDirectedPresences(getValue(spInfoStr[16]));
				cacheSize.setClientSessionInfoCache(getValue(spInfoStr[17]));
				cacheSize.setFaviconMisses(getValue(spInfoStr[18]));
				cacheSize.setUser(getValue(spInfoStr[19]));
				cacheSize.setConnectionManagersSessions(getValue(spInfoStr[20]));
				cacheSize.setIncomingServerSessions(getValue(spInfoStr[21]));
				cacheSize.setPrivacyLists(getValue(spInfoStr[22]));
				cacheSize.setRoutingServersCache(getValue(spInfoStr[23]));
				cacheSize.setPubsubnodes(getValue(spInfoStr[24]));
				cacheSize.setDiscoServerFeatures(getValue(spInfoStr[25]));
				cacheSize.setEntityCapabilities(getValue(spInfoStr[26]));
				cacheSize.setRoutingComponentsCache(getValue(spInfoStr[27]));
				cacheSizeHash.put(id,cacheSize);
				id ++;
			}		
		}		
		cachesizeCount = id;
		br.close();
		return runFlag;
	}
	
	private String getValue(String str){
		str = str.substring(0,str.indexOf("(")).replace(" ","");
		String spInfo[] = str.split(":");
		return spInfo[1];
	}
	
	private void  cacheSizeStore(){
		
		Connection conn = C3P0Pool.getConnection();
		PreparedStatement ps = null;
		String sql = "insert into cachesize(tagserver,date,data) values(?,?,?) ";
		Date date = new Date();
		String data;
		
		System.out.println("向cachesize表插入数据");
		for(int i=0;i<cachesizeCount;i++){
			data = getData(i);
			try {
				ps = conn.prepareStatement(sql);
				ps.setObject(1,tagserver);
				ps.setObject(2,date);
				ps.setObject(3,data);
				ps.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		C3P0Pool.free(ps, conn);		
	}
	
	private String getData(int i){
		String data = null;
		CacheSize cacheSize = cacheSizeHash.get(i);	
		data = "{timestamp:"+"\""+cacheSize.getTimeline()+"\""+","+
				"\""+"FaviconHits"+"\""+":"+cacheSize.getFaviconHits()+","+
				"\""+"RoutingUserSessions"+"\""+":"+cacheSize.getRoutingUserSessions()+","+
				"\""+"ValidatedDomains"+"\""+":"+cacheSize.getValidatedDomains()+","+
				"\""+"RoutingUsersCache"+"\""+":"+cacheSize.getRoutingUsersCache()+","+
				"\""+"MulticastService"+"\""+":"+cacheSize.getMulticastService()+","+
				"\""+"RoutingAnonymousUsersCache"+"\""+":"+cacheSize.getRoutingAnonymousUsersCache()+","+
				"\""+"ComponentsSessions"+"\""+":"+cacheSize.getComponentsSessions()+","+
				"\""+"EntityCapabilitiesPendingHashes"+"\""+":"+cacheSize.getEntityCapabilitiesPendingHashes()+","+
				"\""+"EntityCapabilitiesUsers"+"\""+":"+cacheSize.getEntityCapabilitiesUsers()+","+
				"\""+"LoginFailIp"+"\""+":"+cacheSize.getLoginFailIp()+","+
				"\""+"Roster"+"\""+":"+cacheSize.getRoster()+","+
				"\""+"SessionsbyHostname"+"\""+":"+cacheSize.getSessionsbyHostname()+","+
				"\""+"RemoteUsersExistence"+"\""+":"+cacheSize.getRemoteUsersExistence()+","+
				"\""+"VCard"+"\""+":"+cacheSize.getVCard()+","+
				"\""+"OfflineMessageCount"+"\""+":"+cacheSize.getOfflineMessageCount()+","+
				"\""+"DiscoServerItems"+"\""+":"+cacheSize.getDiscoServerItems()+","+
				"\""+"DirectedPresences"+"\""+":"+cacheSize.getDirectedPresences()+","+
				"\""+"ClientSessionInfoCache"+"\""+":"+cacheSize.getClientSessionInfoCache()+","+
				"\""+"FaviconMissess"+"\""+":"+cacheSize.getFaviconMisses()+","+
				"\""+"User"+"\""+":"+cacheSize.getUser()+","+
				"\""+"ConnectionManagersSessions"+"\""+":"+cacheSize.getConnectionManagersSessions()+","+
				"\""+"IncomingServerSessions"+"\""+":"+cacheSize.getIncomingServerSessions()+","+
				"\""+"PrivacyLists"+"\""+":"+cacheSize.getPrivacyLists()+","+
				"\""+"RoutingServersCache"+"\""+":"+cacheSize.getRoutingServersCache()+","+
				"\""+"pubsubnodes"+"\""+":"+cacheSize.getPubsubnodes()+","+
				"\""+"DiscoServerFeatures"+"\""+":"+cacheSize.getDiscoServerFeatures()+","+
				"\""+"EntityCapabilities"+"\""+":"+cacheSize.getEntityCapabilities()+","+
				"\""+"RoutingComponentsCache"+"\""+":"+cacheSize.getRoutingComponentsCache()+
				"}";			
		return data;		
	}
	public static void main(String args[]) throws Exception{
		File file = new File("C:\\Users\\liumingkong\\Desktop\\stress\\06071831-123.126.54.33\\cachesize.log");		
		CacheSizeProc cs = new CacheSizeProc(file);
		cs.cacheSize();
		cs.cacheSizeStore();
	}
}
