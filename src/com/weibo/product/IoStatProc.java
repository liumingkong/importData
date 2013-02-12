package com.weibo.product;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;

import com.weibo.data.IoStatInfo;
import com.weibo.util.C3P0Pool;
import com.weibo.util.Util;

public class IoStatProc {

	private static String timestr = "Time:";
	//private static String avgstr = "avg-cpu:";
	//private static String devicestr = "Device:";
	private static String pattern = "\\s+";
	
	private int timeBlockCount;
	private HashMap<Integer,IoStatInfo> iostatHashmap = new HashMap<Integer,IoStatInfo>();
	
	
	private boolean cacheIoStat(File file) throws IOException{
		
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		// 运行标志位
		boolean runFlag = true;
		String sp[];
		int id=0;
		while (null != (line = br.readLine())) {
			
			//  如果遇到Time:串，表明遇到指定的Time块
			if(line.indexOf(timestr)>=0){
				IoStatInfo isi = new IoStatInfo();
				// Time: 02:23:02 PM
				sp=Util.stringTest(line).split(pattern);
				if(sp.length<3){
					runFlag = false;
					break;
				}	
				isi.setTimeline(sp[1]);
				// 向下读取两行
				if(null == (line = br.readLine())){
					runFlag = false;
					break;
				}
				if(null == (line = br.readLine())){
					runFlag = false;
					break;
				}
				// 说明具体数据的这一行存在
				// 0.42    0.00    0.10    0.04    0.00   99.44
				sp=Util.stringTest(line).split(pattern);
				if(sp.length<6){
					runFlag = false;
					break;
				}
				// 开始存入概述信息
				isi.setUserPer(sp[0]);
				isi.setNicePer(sp[1]);
				isi.setSysPer(sp[2]);
				isi.setIowaitPer(sp[3]);
				isi.setStealPer(sp[4]);
				isi.setIdlePer(sp[5]);
				
				// 向下读取两行
				// 一行空行
				// Device: rrqm/s wrqm/s r/s w/s rMB/s wMB/s avgrq-sz avgqu-sz awaitsvctm %util
				if(null == (line = br.readLine())){
					runFlag = false;
					break;
				}
				if(null == (line = br.readLine())){
					runFlag = false;
					break;
				}
				while (null != (line = br.readLine())){
					
					// 如果这一行信息为空，说明这个块的读取结束
					if(line.trim().length() == 0){
						break;
					}
					
					//if(line.indexOf("sdb1")>=0){
						sp=Util.stringTest(line).split(pattern);
						if(sp.length<12){
							runFlag = false;
							break;
						}	
						IoStatInfo.setDevice(sp[0]);
						isi.setRrqms(sp[1]);
						isi.setWrqms(sp[2]);
						isi.setRs(sp[3]);
						isi.setWs(sp[4]);
						isi.setrMBs(sp[5]);
						isi.setwMBs(sp[6]);
						isi.setAvgrqsz(sp[7]);
						isi.setAvgqusz(sp[8]);
						isi.setAwait(sp[9]);
						isi.setSvctm(sp[10]);
						isi.setUtil(sp[11]);
					//}
				}
				if(runFlag == false){
					break;
				}
				iostatHashmap.put(id,isi);
				id ++;
			}	
		}
		timeBlockCount = id;
		br.close();
		return runFlag;
	}
	
	private void IoStatSave(File file) throws IOException{
		
		String tagserver = Util.getFileInfo(file);
		Connection conn = C3P0Pool.getConnection();
		PreparedStatement ps = null;
		String sql = "insert into iostat(tagserver,createtime,data) values(?,?,?) ";
		Date date = new Date();
		String data;
		
		System.out.println("向iostat表插入数据");
		for(int i=0;i<timeBlockCount;i++){
			data = IostatToJson(i);
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
	
	private String IostatToJson(int index){
		String data = null;
		IoStatInfo isi = iostatHashmap.get(index);
		data = "{timestamp:"+"\""+isi.getTimeline()+"\""+","+
				"\""+"%user"+"\""+":"+isi.getUserPer()+","+
				"\""+"%nice"+"\""+":"+isi.getNicePer()+","+
				"\""+"%sys"+"\""+":"+isi.getSysPer()+","+
				"\""+"%iowait"+"\""+":"+isi.getIowaitPer()+","+
				"\""+"%steal"+"\""+":"+isi.getStealPer()+","+
				"\""+"%idle"+"\""+":"+isi.getIdlePer()+","+
				"\""+"rrqms"+"\""+":"+isi.getRrqms()+","+
				"\""+"wrqms"+"\""+":"+isi.getWrqms()+","+
				"\""+"rs"+"\""+":"+isi.getRs()+","+
				"\""+"ws"+"\""+":"+isi.getWs()+","+
				"\""+"rMBs"+"\""+":"+isi.getrMBs()+","+
				"\""+"wMBs"+"\""+":"+isi.getwMBs()+","+
				"\""+"avgrq-sz"+"\""+":"+isi.getAvgrqsz()+","+
				"\""+"avgqu-sz"+"\""+":"+isi.getAvgqusz()+","+
				"\""+"await"+"\""+":"+isi.getAwait()+","+
				"\""+"svctm"+"\""+":"+isi.getSvctm()+","+
				"\""+"util"+"\""+":"+isi.getUtil()+
				"}";
		return data;
	}
	
	public boolean IoStatInput(File file) throws Exception{
		if(cacheIoStat(file)){
			IoStatSave(file);
			return true;
		}
		return false;
	}
	
}
