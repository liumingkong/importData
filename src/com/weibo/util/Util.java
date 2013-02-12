package com.weibo.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.weibo.data.PerfTimeInfo;
import com.weibo.util.C3P0Pool;

public class Util {
	static String pattern = "\\s+";

	// 用来去掉字符串的头和尾的空格
	public static String stringTest(String s){
		int j=0,k=0,i=0;
		//改行代码用于修复k没有初始值的bug
		k = s.length()-1;
		char[] stra=new char[s.length()];
		s.getChars(0,s.length(),stra,0);
		for(i=0;i<s.length();i++){
			if(stra[i]==' '||stra[i]==' '){
				j=i+1;
			}else{
				break;
			}
		 }
		 for(i=s.length()-1;i+1>0;i--){
			 if(stra[i]==' '||stra[i]==' '){
				 k=i;
			 }else{
				 break;
			 }
		 }
		 String strb=new String(stra,j,k-j+1);
		 return strb;
	}
	
	public static boolean checkDuplicatedData(String tagserver,String tableName){	
		
		System.out.println("判断"+tableName+"表是否重复");
		boolean flag = false;
		Connection conn = C3P0Pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs=null;

		
		String testsql = "select * from "+tableName+" where tagserver = ?";
		try {
			ps = conn.prepareStatement(testsql);
			ps.setObject(1,tagserver);
			rs = ps.executeQuery();			
			while (rs.next()){
				flag = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		C3P0Pool.free(ps, conn);
		if(flag==true){
			System.out.println(tableName+"表：插入的数据出现重复,或者数据已经插入完成");
		}
		return flag;		
	}
	
	public static String getFileInfo(File file){		
        String sp[];
        String patternSeparator = File.separator;
        if(System.getProperties().getProperty("os.name").toUpperCase().indexOf("WINDOWS")!=-1){
        	patternSeparator = "\\\\";
        }        
        sp = file.getAbsolutePath().split(patternSeparator);          
        return sp[sp.length-2];
	}
	
	public static String getFileName(File file){
		
        String sp[];
        String patternSeparator = File.separator;
        if(System.getProperties().getProperty("os.name").toUpperCase().indexOf("WINDOWS")!=-1){
        	patternSeparator = "\\\\";
        }        
        sp = file.getAbsolutePath().split(patternSeparator);        
        return sp[sp.length-1];
	}
	
	public static void setFlag(File file,String flagName){
		
		String tagserver = Util.getFileInfo(file);
		
		Connection conn = C3P0Pool.getConnection();
		PreparedStatement ps = null;
		
		String sql = "UPDATE stressinfo SET "+flagName+"=1 where tagserver=?";
		try {
			ps = conn.prepareStatement(sql);
			ps.setObject(1,tagserver);
			//ps.setObject(1,tagserver);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("在stressinfo的"+flagName+"标志已经更新");
		C3P0Pool.free(ps, conn);
	}	
	
	public static String switchGM(String str){
		double value=0;
		DecimalFormat df= new DecimalFormat("0.0");
		
		if("g".equals(str.substring(str.length()-1))){
			str = str.replace("g","");
			value = Double.valueOf(str);
			value = value * 1024 * 1024;
			str = df.format(value);
			return str;
		}
		if("m".equals(str.substring(str.length()-1))){
			str = str.replace("m","");
			value = Double.valueOf(str);
			value = value * 1024;
			str = df.format(value);
			return str;
		}
		return str;
	}
	
	public static boolean checkDirectory(String str){
		String regexStr = "([0-1][0-9][0-3][0-9][0-2][0-9][0-5][0-9])" +
			"-"+"((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)";
		Pattern reg = Pattern.compile(regexStr);
		Matcher matcher = reg.matcher(str);
		if(matcher.matches()){
			System.out.println("match true");
			return true;
		}
		return false;
	}
	
	public static void insertStressInfo(String tagserver){
		
		Connection conn = C3P0Pool.getConnection();
		PreparedStatement ps = null;
		
		String sql = "insert into stressinfo(tagserver) value(?)";
		try {
			ps = conn.prepareStatement(sql);
			ps.setObject(1,tagserver);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		C3P0Pool.free(ps, conn);
	}
	
	public static boolean checkFileExist(File file,String filename){
		File fileInDir = new File(file.getAbsolutePath()+File.separator+filename);
		if(fileInDir.exists()){
			return true;
		}
		return false;
	}
	
	
	public static String readUserInput(String prompt) throws IOException{
		//先定义接受用户输入的变量
		String result;
		do{
			//输出提示文字
			System.out.print(prompt);
			InputStreamReader is_reader = new InputStreamReader(System.in);
			result = new BufferedReader(is_reader).readLine();
		}while(isInvalid(result));
		//当用户输入无效的时候，反复提示要求用户输入
		return result;
	}
	
	private static boolean isInvalid(String str){
		return str.equals("");
	}
	
	public static long getTimeSlice(PerfTimeInfo perfTimeInfo) throws Exception{
		Long startTime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(perfTimeInfo.getStartDay()+" "+perfTimeInfo.getStartTime()).getTime()/1000;
		Long endTime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(perfTimeInfo.getEndDay()+" "+perfTimeInfo.getEndTime()).getTime()/1000;
		Long timeslice = endTime-startTime;
		return timeslice;		
	}
	
	
	
	public static void main(String args[]){
		
		checkDirectory("12230512-123.43.43.43");
		
		
		
		/*
		String str ="Time: 02:23:02 PM";
		
		String[] sp=Util.stringTest(str).split(pattern);

		for(int i =0 ;i<sp.length;i++){
			System.out.println(i+":"+sp[i]);
		}
		String time = "1215782027390";
		Long timestamp = Long.valueOf(time);
		System.out.println(""+new java.text.SimpleDateFormat("HH:mm:ss").
				format(new java.util.Date (timestamp)));
				
		*/
	}
}
