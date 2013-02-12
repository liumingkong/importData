package com.weibo.product;

import java.io.File;
import com.weibo.util.Util;

public class ProgramInput {
	
	private static String JSTAT = "jstat.log";
	private static String TOP = "top.log";
	private static String IOSTAT = "iostat.log";
	private static String TCPSTAT = "tcpstat.log";	
	private static String PERFSTAT = "perfStats.log";	
	private static String CACHESIZE = "cachesize.log";
	
	
	public static void program(File file) throws Exception{
		
		String tagserver;
		if(file.isDirectory() == true){
			// 如果是目录
			tagserver = Util.getFileName(file);
			// 检测目录名称是否符合标准
			if(Util.checkDirectory(tagserver)){
				// 检测StressInfo是否存在于数据库中
				if(Util.checkDuplicatedData(tagserver, "stressinfo")==false){
					// 如果不存在这条数据，就需要新建这条数据
					Util.insertStressInfo(tagserver);					
				}
				
				// 开始执行数据处理的操作，检测文件是否存在
				if(Util.checkFileExist(file,JSTAT)){
					// 如果发现数据没有重复才会执行后续处理
					System.out.println(JSTAT+" is existed");
					File fileJStat = new File(file.getAbsolutePath()+File.separator+JSTAT);
					if(Util.checkDuplicatedData(tagserver,"jstat")==false&&(new JstatProc().jstatInput(fileJStat))){
						Util.setFlag(fileJStat,"jstat");
					}
				}
				
				if(Util.checkFileExist(file,IOSTAT)){
					// 如果发现数据没有重复才会执行后续处理
					System.out.println(IOSTAT+" is existed");
					File fileIoStat = new File(file.getAbsolutePath()+File.separator+IOSTAT);
					if(Util.checkDuplicatedData(tagserver,"iostat")==false&&(new IoStatProc().IoStatInput(fileIoStat))){
						Util.setFlag(fileIoStat,"iostat");
					}
				}
				
				if(Util.checkFileExist(file,TOP)){
					System.out.println("insert:"+TOP);
					new TopProc().program(new File(file.getAbsolutePath()+File.separator+TOP));
				}
				
				if(Util.checkFileExist(file,TCPSTAT)){
					System.out.println(TCPSTAT+" is existed");
					File fileTcpStat = new File(file.getAbsolutePath()+File.separator+TCPSTAT);
					if(Util.checkDuplicatedData(tagserver,"tcpstat")==false&&(new TcpStatProc().tcpInput(fileTcpStat))){
						Util.setFlag(fileTcpStat,"tcpstat");
					}
				}
				
				if(Util.checkFileExist(file, PERFSTAT)){
					System.out.println(PERFSTAT+" is existed");
					File filePerfStat = new File(file.getAbsolutePath()+File.separator+PERFSTAT);
					if(Util.checkDuplicatedData(tagserver,"perftimelist")==false&&(new PerfStatProc().perfInput(filePerfStat))){
						Util.setFlag(filePerfStat,"perfstat");
					}					
				}
				
				if(Util.checkFileExist(file, CACHESIZE)){
					System.out.println(CACHESIZE + " is existed");
					File fileCacheSize = new File(file.getAbsolutePath()+File.separator+CACHESIZE);
					if(Util.checkDuplicatedData(tagserver,"cachesize")==false&&(new CacheSizeProc(fileCacheSize).cacheProgram())){
						Util.setFlag(fileCacheSize,"cachesize");
					}					
				}
				
			}else{
				System.out.println("Directory is not standard！");
				System.out.println("fileDirectory format must be MMDDHHmm-IP");
				System.out.println("for example:05091423-180.149.138.89");
			}			
		}else{
			// 如果是文件
			tagserver = Util.getFileInfo(file);
			
			if(Util.checkDirectory(tagserver)){
				// 检测StressInfo是否存在于数据库中
				if(Util.checkDuplicatedData(tagserver, "stressinfo")==false){
					// 如果不存在这条数据，就需要新建这条数据
					Util.insertStressInfo(tagserver);					
				}				
				
				if(JSTAT.equalsIgnoreCase(Util.getFileName(file))){
					System.out.println(JSTAT+" is existed");
					if(Util.checkDuplicatedData(tagserver,"jstat")==false&&(new JstatProc().jstatInput(file))){
						Util.setFlag(file,"jstat");
					}
				}
				
				if(TOP.equalsIgnoreCase(Util.getFileName(file))){
					System.out.println("insert"+TOP);
					new TopProc().program(file);
				}
				
				if(IOSTAT.equalsIgnoreCase(Util.getFileName(file))){
					System.out.println("insert"+IOSTAT );
					if(Util.checkDuplicatedData(tagserver,"iostat")==false&&(new IoStatProc().IoStatInput(file))){
						Util.setFlag(file,"iostat");
					}
				}
				
				if(TCPSTAT.equalsIgnoreCase(Util.getFileName(file))){
					System.out.println("insert"+TCPSTAT);
					if(Util.checkDuplicatedData(tagserver,"tcpstat")==false&&(new TcpStatProc().tcpInput(file))){
						Util.setFlag(file,"tcpstat");
					}
				}
				

				if(PERFSTAT.equalsIgnoreCase(Util.getFileName(file))){
					System.out.println("insert"+PERFSTAT);
					if(Util.checkDuplicatedData(tagserver,"perftimelist")==false&&(new PerfStatProc().perfInput(file))){
						Util.setFlag(file,"perfstat");
					}
				}
 

				if(CACHESIZE.equalsIgnoreCase(Util.getFileName(file))){
					System.out.println("insert"+CACHESIZE);
					if(Util.checkDuplicatedData(tagserver,"cachesize")==false&&(new CacheSizeProc(file).cacheProgram())){
						Util.setFlag(file,"cachesize");
					}
				}
				
			}else{
				System.out.println("Directory is not standard！");
				System.out.println("fileDirectory format must be MMDDHHmm-IP");
				System.out.println("for example:05091423-180.149.138.89");
			}	
		}
	}
}
