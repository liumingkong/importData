package com.weibo.product;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import com.weibo.data.PerfInfo;
import com.weibo.data.PerfTimeInfo;
import com.weibo.data.TagInfo;
import com.weibo.util.C3P0Pool;
import com.weibo.util.Util;

public class PerfStatProc {
	
	private static String pattern = "\\s+";
	
	// 本次的tagserver信息
	private String tagserver;
	// tagname的数目就是perfNamehs.size()
	private int perfTagCount;
	// 数据块的数目，即时间线的数目
	private int perfBlockCount;
	// 缓存整个文件的信息
	private HashMap<Integer, PerfInfo> perfMap = new HashMap<Integer,PerfInfo>();
	// 在perfstat出现过的名字的列表
	private HashSet<String> perfNamehs = new HashSet<String>();	
	// 存储时间线 perfBlockCount
	private String timelist[];
	// 缓存结果，列为tagname,mean,min,max,stddev,tps
	// 行数为tagname的数目，就是perfNamehs.size()
	private String cacheResult[][];
	
	// 将文件缓存为hashmap的结构
	private boolean cacheFile(File file) throws Exception{
		
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line ;
		String[] sp;
		boolean runFlag = true;
		perfBlockCount = 0;
		while((line = br.readLine())!=null){
			if(line.indexOf("Performance Statistics")>-1){				
				PerfInfo perfInfo = new PerfInfo();
				// 处理Performance Statistics这一行
				sp=Util.stringTest(line).split(pattern);
				if(sp.length!=7){
					runFlag = false;
					break;
				}
				PerfTimeInfo perfTimeInfo = new PerfTimeInfo();
				perfTimeInfo.setStartDay(sp[2]);
				perfTimeInfo.setStartTime(sp[3]);
				perfTimeInfo.setEndDay(sp[5]);
				perfTimeInfo.setEndTime(sp[6]);
				// 存到perfInfo中
				perfInfo.setPerfTimeInfo(perfTimeInfo);
				// 处理Tag行
				line = br.readLine();
				line = Util.stringTest(line);
				if(line.indexOf("Tag")==-1){
					runFlag = false;
					break;
				}
				// 处理Tag信息
				//如果没有遇到空行，就说明这块信息没有结束
				// 初始化Tag信息存储
				LinkedList<TagInfo> tagList = new LinkedList<TagInfo>();
				while(null != (line = br.readLine())){
					// 如果这一行信息为空，说明这个块的读取结束
					if(line.trim().length() == 0){
						break;
					}
					sp=Util.stringTest(line).split(pattern);
					if(sp.length!=6){
						runFlag = false;
						break;
					}
					TagInfo tagInfo = new TagInfo();
					tagInfo.setTagName(sp[0]);
					tagInfo.setTagAvg(sp[1]);
					tagInfo.setTagMin(sp[2]);
					tagInfo.setTagMax(sp[3]);
					tagInfo.setTagStdDev(sp[4]);
					tagInfo.setTagCount(sp[5]);
					// 将其加入到Tag列表
					tagList.add(tagInfo);
				}
				if(runFlag == false){
					break;
				}
				// 存到perfInfo中
				perfInfo.setTagList(tagList);	
				perfMap.put(perfBlockCount,perfInfo);
				perfBlockCount ++;
			}			
		}		
		br.close();
		if(runFlag == false){
			return false;
		}
		return true;
	}
	
	// 将tagname和时间线数据缓存到数组cacheResult中
	private void cacheTagName(){		
		timelist = new String[perfBlockCount];
		// 获取tagname的hashset，去掉重复的tag
		for(int i=0;i<perfBlockCount;i++){
			PerfInfo perfInfo = perfMap.get(i);
			timelist[i] = perfInfo.getPerfTimeInfo().getStartTime();
			Iterator<TagInfo> itPerfInfo = perfInfo.getTagList().iterator();
			while(itPerfInfo.hasNext()){
				String tagname = itPerfInfo.next().getTagName();
				//System.out.println(tagname);
				perfNamehs.add(tagname);
			}			
		}	
		// 读取hashset存入到cacheResult数组中
		perfTagCount = perfNamehs.size();
		cacheResult = new String[perfTagCount][6];
		Iterator<String> itPerfNamehs = perfNamehs.iterator();
		for(int i=0;i<perfTagCount;i++){
			cacheResult[i][0] = itPerfNamehs.next();
		}	
		itPerfNamehs = null;
	}
	
	// 从 hashmap中获取其他的信息，存入数组，包括mean,min,max,stddev,tps,内部调用dealInfo
	private void cacheInfo() throws Exception{
		String tagname;
		// 根据每个tag名称
		for(int i=0;i<perfTagCount;i++){
			tagname = cacheResult[i][0];			
			this.dealInfo(tagname,i);			
		}		
	}
	
	// 对于每个tagName检索hashmap块来寻找信息，并存入cache数组中
	private void dealInfo(String tagname,int index) throws Exception{		
		boolean match = false;
		TagInfo tagInfo;
		String tagAvg = "0.0";
		String tagMin = "0.0";
		String tagMax = "0.0";
		String tagStdDev = "0.0";
		String tagTPS = "0.0";

		// 获取第一个块的数据
		PerfInfo perfInfo = perfMap.get(0);
		Iterator<TagInfo> itPerfTagList = perfInfo.getTagList().iterator();
		double timeslice = (Util.getTimeSlice(perfInfo.getPerfTimeInfo()));
		while(itPerfTagList.hasNext()){
			// 如果标志匹配
			tagInfo = itPerfTagList.next();
			String tagInfoName = tagInfo.getTagName();
			if(tagname.equalsIgnoreCase(tagInfoName)){
				// 表示匹配成功
				match = true;
				tagAvg = tagInfo.getTagAvg();
				tagMin = tagInfo.getTagMin();
				tagMax = tagInfo.getTagMax();
				tagStdDev = tagInfo.getTagStdDev();
				tagTPS = String.valueOf(Double.valueOf(tagInfo.getTagCount())/timeslice);
			}		
		}
		if(match == false){
			tagAvg = "0.0";
			tagMin = "0.0";
			tagMax = "0.0";
			tagStdDev = "0.0";
			tagTPS = "0.0";
		}
		match = false;
		String strMean = tagAvg;
		String strMin = tagMin;
		String strMax = tagMax;
		String strStddev = tagStdDev;
		String strTps = tagTPS;		
		
		// 开始读取其他的块的信息
		for(int i=1;i<perfBlockCount;i++){
			// 获取一个数据块
			perfInfo = perfMap.get(i);
			// 根据数据块获取数据列表和时间间隔
			itPerfTagList = perfInfo.getTagList().iterator();
			while(itPerfTagList.hasNext()){
				// 如果标志匹配
				tagInfo = itPerfTagList.next();
				String tagInfoName = tagInfo.getTagName();
				if(tagname.equalsIgnoreCase(tagInfoName)){
					// 表示匹配成功
					match = true;
					tagAvg = tagInfo.getTagAvg();
					tagMin = tagInfo.getTagMin();
					tagMax = tagInfo.getTagMax();
					tagStdDev = tagInfo.getTagStdDev();
					tagTPS = String.valueOf(Double.valueOf(tagInfo.getTagCount())/timeslice);
				}		
			}
			if(match == false){
				tagAvg = "0.0";
				tagMin = "0.0";
				tagMax = "0.0";
				tagStdDev = "0.0";
				tagTPS = "0.0";
			}
			match = false;
			strMean = strMean + "," +tagAvg;
			strMin = strMin +","+tagMin;
			strMax = strMax +","+ tagMax;
			strStddev = strStddev +","+tagStdDev;
			strTps = strTps +","+tagTPS;			
		}
		// 缓存结果到数组中
		cacheResult[index][1] = strMean;
		cacheResult[index][2] = strMin;
		cacheResult[index][3] = strMax;
		cacheResult[index][4] = strStddev;
		cacheResult[index][5] = strTps;
	}
	
	// 开始将处理完成的信息存入数据库去
	private void storePerfStat(){
		// 保存时间线到perftimelist
		this.storeTimeList();
		// 保存mean到perfmean
		storeInfo("perfmean",1);
		// 保存min到perfmin
		storeInfo("perfmin",2);
		// 保存max到perfmax
		storeInfo("perfmax",3);
		// 保存stddev到perfstddev
		storeInfo("perfstddev",4);
		// 保存tps到perftps
		storeInfo("perftps",5);
	}
	
	private void storeTimeList(){
		Connection conn = C3P0Pool.getConnection();
		PreparedStatement ps = null;
		String sql = "insert into perftimelist(tagserver,data) " +"values(?,?) ";
		for(int i=0;i<timelist.length;i++){			
			try {
				ps = conn.prepareStatement(sql);
				ps.setObject(1,tagserver);
				ps.setObject(2,timelist[i]);				
				ps.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}		 
		C3P0Pool.free(ps, conn);
	}
	
	private void storeInfo(String tableName,int index){
		Connection conn = C3P0Pool.getConnection();
		PreparedStatement ps = null;
		String sql = "insert into "+tableName+"(tagserver,tagname,data) " +"values(?,?,?) ";
		for(int i=0;i<perfTagCount;i++){
			try {
				ps = conn.prepareStatement(sql);
				ps.setObject(1,tagserver);
				ps.setObject(2,cacheResult[i][0].replace(".","_"));
				ps.setObject(3,cacheResult[i][index]);
				ps.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}		 
		C3P0Pool.free(ps, conn);
	}
	
	public boolean perfInput(File file) throws Exception{
		this.tagserver = Util.getFileInfo(file); 		
		if(cacheFile(file)==false){
			return false;
		}
		cacheTagName();	
		cacheInfo();
		storePerfStat();
		return true;
	}

}
