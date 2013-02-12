package com.weibo.product;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import com.weibo.util.C3P0Pool;
import com.weibo.util.Util;

public class JstatProc {

	public boolean jstatInput(File file) throws IOException{
		
		String tagserver = Util.getFileInfo(file);
		Connection conn = C3P0Pool.getConnection();
		PreparedStatement ps = null;
		String sql = "insert into jstat(tagserver,createtime,data) values(?,?,?) ";
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = br.readLine();;
		Date date = new Date();

		System.out.println("向jstat表插入数据");
		while (null != (line = br.readLine())) {
			String data = jstatToParse(line);
			if("false".equalsIgnoreCase(data)){
				System.out.println("jstat数据转换出错");
				break;
			}
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
		br.close();
		C3P0Pool.free(ps, conn);
		return true;
	}
	
	private String jstatToParse(String line) {
		line = Util.stringTest(line);
		String[] splitT=line.split("\\s+");
		String data ; 
		if(splitT.length<14){
			data = "false";
		}
		String ts = splitT[0];
		String eu = splitT[6];
		String ou = splitT[8];
		String pu = splitT[10];
		Double eou = Double.valueOf(eu)+Double.valueOf(ou);
		String fgc = splitT[13];
		data = "{timestamp:"+ts+",eu:"+eu+",ou:"+ou+",pu:"+pu
					+",eou:"+eou+",fgc:"+fgc+"}";
		return data;
	}
}
