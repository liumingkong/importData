package com.weibo.product;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import com.weibo.util.C3P0Pool;
import com.weibo.util.Util;

public class TcpStatProc {

	private static String pattern = "\\s+";
	
	public boolean tcpInput(File file) throws Exception{
		
		String tagserver = Util.getFileInfo(file);		
		Connection conn = C3P0Pool.getConnection();
		PreparedStatement ps = null;
		String sql = "insert into tcpstat(tagserver,createtime,data) values(?,?,?) ";
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = br.readLine();
		Date date = new Date();
		// 运行标志位，true表示成功运行，false表示失败运行
		boolean runFlag = true;
		System.out.println("向tcpstat表插入数据");
		while (null != (line = br.readLine())) {
			String data = tcpstatToParse(line);
			if("false".equalsIgnoreCase(data)){
				System.out.println("tcpstat数据转换出错");
				runFlag = false;
				break;				
			}
			try {
				ps = conn.prepareStatement(sql);
				ps.setObject(1,tagserver);
				ps.setObject(2,date);
				ps.setObject(3,data);
				ps.executeUpdate();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
		br.close();
		C3P0Pool.free(ps, conn);
		return runFlag;
	}
	
	private static String tcpstatToParse(String line) {

		String[] sp=Util.stringTest(line).replace(":"," ").replace("="," ").split(pattern);
		String data ; 
		if(sp.length<10){
			data = "false";
		}		
		String timestamp = new java.text.SimpleDateFormat("HH:mm:ss").
				format(new java.util.Date (Long.valueOf(sp[1])*1000));
		String num = sp[3];
		String avg_size = sp[5];
		String sd_size = sp[7];
		String Bps = sp[9];
		data = "{timestamp:"+"\""+timestamp+"\""+",num:"+num+",avg_size:"+avg_size
				+",sd_size:"+sd_size+",Bps:"+Bps+"}";
		return data;
	}
}
