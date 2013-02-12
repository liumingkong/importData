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
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import com.weibo.data.PidInfo;
import com.weibo.data.TopInfo;
import com.weibo.data.TopPidBlock;
import com.weibo.util.C3P0Pool;
import com.weibo.util.Util;

public class TopProc {
	
	// 基本信息
	private static String pidstr="PID";
	private static String topstr="top -";
	private static String taskstr="Tasks:";
	private static String cpustr="Cpu";
	private static String memstr="Mem:";
	private static String swapstr="Swap:";
	private static String pattern = "\\s+";
	
	// topInfo的列数，一共七列
	private static int topInfoColumn=10;
	
	// top5指的是在整个压测过程中，CPU%的数据曾经排进过前5的所有线程，	
	private static int topAll = 5;

	// 缓存TOP块的数目，是cpuPid，memPid，topInfo的行数，这个数值在建立索引之后获得topHash
	private int topBlockRow;	
	// 缓存top5的数目，是cpuPid，memPid，maxCpu,topPid的列数，可以通过pidHash.size()来获取这个数值
	private int pidCountColumn;
	// 缓存top5的pid的信息的数组
	private String topPid[];
	// 用来缓存top的信息块，通过自动增长的PID块来进行索引，保证top信息块是有序存储
	private HashMap<Integer,TopPidBlock> topHashmap = new HashMap<Integer,TopPidBlock>();
	
	// 缓存筛选出来的每个pid的cpu%的最大值：列信息：pid号，与pid的列表对应，一位数组
	// maxCpu[pidCountColumn]
	private String maxCpu[];
	
	// 缓存筛选出来的pid的cpu%的存储矩阵，行信息:PID块数，列信息：pid号，与pid的列表对应的信息
	// cpu[topBlockRow][pidCountColumn]
	private String cpuPid[][];
	private String memPid[][];
	private String virtPid[][];
	private String resPid[][];
	
	// 缓存top的所有有效信息，行信息:TOP/PID块数，列信息：timeline,load1,load5,load15,usedmem,freemem,usedswap,ofvirt,ofres
	// topInfo[topBlockRow]][topInfoColumn]
	private String topInfo[][];
	
	// 处理文件，将每个PID作为一块，块内用列表存储，块用hash索引，建立检索hash表
	private boolean cacheFile(File file) throws Exception{
		
		// read from file
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		Integer id=0;
		String sp[];
		Boolean errorFlag = false;
		// read a line 
		while (null != (line = br.readLine())) {
			
			// 遇到top这一行的信息
			if(line.indexOf(topstr)>=0){
				
				// 开始生成索引的内容
				TopPidBlock tpb = new TopPidBlock();
				// 存储top的第一块信息的对象
				TopInfo tf = new TopInfo();
				// 取出行首行尾的空格，并去除逗号
				sp = Util.stringTest(line).replace(","," ").split(pattern);
				// 判断该行是否出错,top行拆分后共14个单位
				// top - 09:58:33 up 10 days, 21:44,  2 users,  load average: 0.00, 0.00, 0.00
				if(sp.length<14){
					errorFlag = true;
					break;
				}
				// 开始存储信息 
				tf.setTimeline(sp[2]);
				tf.setLoadAverage1(sp[sp.length-3]);
				tf.setLoadAverage5(sp[sp.length-2]);
				tf.setLoadAverage15(sp[sp.length-1]);
				
				// 开始读取tasks这行的信息
				if((line = br.readLine())==null||line.indexOf(taskstr)<0){
					errorFlag = true;
					break;
				}
				// 说明已经成功读取到该行信息
				sp = Util.stringTest(line).replace(","," ").split(pattern);
				// tasks行拆分后，共11个单元
				// Tasks: 116 total,   1 running, 115 sleeping,   0 stopped,   0 zombie
				if(sp.length<11){
					errorFlag = true;
					break;
				}
				
				// 开始读取Cpus这行的信息
				if((line = br.readLine())==null||line.indexOf(cpustr)<0){
					errorFlag = true;
					break;
				}
				// 说明已经成功读取到该行信息
				sp = Util.stringTest(line).replace(","," ").split(pattern);
				// 共9个单元 
				// Cpu(s):  0.0%us   0.7%sy   0.0%ni  99.3%id   0.0%wa   0.0%hi   0.0%si   0.0%st
				if(sp.length<9){
					errorFlag = true;
					break;
				}
				
				// 开始读取Mem这行的信息
				if((line = br.readLine())==null||line.indexOf(memstr)<0){
					errorFlag = true;
					break;
				}
				// 说明已经成功读取到该行信息
				sp = Util.stringTest(line).replace(","," ").replace("k","").split(pattern);
				// 共9个单元 
				// Mem:   1100516k total,   397304k used,   703212k free,   179708k buffers
				if(sp.length<9){
					errorFlag = true;
					break;
				}
				// 开始存储mem信息
				tf.setMemTotal(sp[1]);
				tf.setMemUsed(sp[3]);
				tf.setMemFree(sp[5]);
				
				// 开始读取Swap这行的信息
				if((line = br.readLine())==null||line.indexOf(swapstr)<0){
					errorFlag = true;
					break;
				}
				// 说明已经成功读取到该行信息
				sp = Util.stringTest(line).replace(","," ").replace("k","").split(pattern);
				// 共9个单元 
				// Swap:  1802232k total,        0k used,  1802232k free,   124708k cached
				if(sp.length<9){
					errorFlag = true;
					break;
				}
				tf.setSwapUsed(sp[3]);
				
				// top块的基本信息读取完成
				tpb.setTopInfo(tf);
				
				
				// 连续向下读取两行数据
				line = br.readLine();//这是一行空行
				if((line = br.readLine())==null||line.indexOf(pidstr)<0){
					errorFlag = true;
					break;
				}
				LinkedList<PidInfo> pidInfoList = new LinkedList<PidInfo>();
				// 说明到达PID信息块，即可向下读取一行数据
				while(null != (line = br.readLine())){
					// 如果这一行信息为空，说明这个块的读取结束
					if(line.trim().length() == 0){
						break;
					}
					// 对这一行的数据进行拆分
					sp = Util.stringTest(line).split(pattern);
					// 3808 root      20   0  2664 1096  864 R  0.3  0.1   0:02.84 top
					if(sp.length<12){
						errorFlag = true;
						break;
					}
					PidInfo pidInfo = new PidInfo();
					pidInfo.setPid(sp[0]);
					pidInfo.setVirt(sp[4]);
					pidInfo.setRes(sp[5]);
					pidInfo.setCpu(sp[8]);
					pidInfo.setMem(sp[9]);
					// 将该信息块加入链表中去
					pidInfoList.add(pidInfo);
				}
				if(errorFlag == true){
					break;
				}
				tpb.setPidInfoList(pidInfoList);
				// 此时，一个TopPidBlock已经组装完成
				// 存入hash表中
				topHashmap.put(id,tpb);
				id++ ;
			}
			
			// 如果写入中途出错，导致top信息写入是中断的，将直接跳过失败的这个块，读取有效的块
		}
		// 发现出错码，表示执行失败
		if(errorFlag == true){
			br.close();
			return false ; 
		}
		
		// 行数：TOP块的数目
		topBlockRow	= id;
		return true;
}
	
	
	// 获取TOP5的pid列表
	private void getPidList(File file) throws IOException{
		// 缓存top5的pid的信息hash表
		LinkedHashSet<String> pidHash = new LinkedHashSet<String>();
		BufferedReader br = new BufferedReader(new FileReader(file));
		String temp;
		while (null != (temp = br.readLine())) {
			if(temp.indexOf(pidstr)>=0){
				for(int i=1;i<=topAll;i++){
					temp=br.readLine();
					if(temp.indexOf("java")>=0){
						temp=Util.stringTest(temp);
						String[] apid=temp.split(pattern);
						// 加上线程标志p，用来区分数字
						pidHash.add(apid[0]);
						// 确保回收
						apid = null;
					}
				}
			 }
		}
		// 列数：top5的pid线程的总数
		pidCountColumn = pidHash.size();
		// 为了确保每次取PID列表的一致性，改为用数组的方式
		topPid = new String[pidCountColumn];
		Iterator<String> itpidHash = pidHash.iterator();
		for(int i=0;i<pidCountColumn;i++){
			topPid[i] = itpidHash.next();
		}
		pidHash = null;
	}
	
	// 将pid的cpu和mem的信息缓存到两个二维数组中
	private void dealCache() throws Exception{
		// 用两个二维数组来存储计算结果
		cpuPid = new String[topBlockRow][pidCountColumn];
		memPid = new String[topBlockRow][pidCountColumn];
		virtPid = new String[topBlockRow][pidCountColumn];
		resPid = new String[topBlockRow][pidCountColumn];

		String valueCpu = "";
		String valueMem = "";
		String valueVirt = "";
		String valueRes = "";
		String pid;
		int j=0;
		// get a PID
		for(int pidcount=0;pidcount<pidCountColumn;pidcount++){
			pid=topPid[pidcount];
			// System.out.println("pid:"+pid);
			// search the blocks by every PID
			for(int i=0;i<topBlockRow;i++){
				boolean flag=false;
				LinkedList<PidInfo> pidInfoList = topHashmap.get(i).getPidInfoList();
				Iterator<PidInfo> pidInfoListIt = pidInfoList.iterator();
				// search a block 
				while(pidInfoListIt.hasNext()){
					PidInfo pidinfo = pidInfoListIt.next();					
					if(pid.equals(pidinfo.getPid())){
						valueCpu = pidinfo.getCpu();
						valueMem = pidinfo.getMem();
						valueVirt = Util.switchGM(pidinfo.getVirt());
						valueRes = Util.switchGM(pidinfo.getRes());
						flag = true;
						break;
					}
				}
				// if we couldn't find the PID
				if(flag==false){
					 valueCpu = "-1.0";
					 valueMem = "-1.0";
					 valueVirt = "-1.0";
					 valueRes = "-1.0";
				}
				// the result line
				//System.out.println("valueCpu"+valueCpu+":"+"valueMem"+valueMem+":"+i);
				cpuPid[i][j] = valueCpu;
				memPid[i][j] = valueMem;
				virtPid[i][j] = valueVirt;
				resPid[i][j] = valueRes;
			}			
			j++ ;
		}
	}

	
	// 基于处理获得的两个二维数组，以及top5的pid列表
	// cpu的pid信息已经可以直接存入DB中去
	// mem的pid信息还需要继续进行处理
	
	// top的所有信息都会存储到topInfo数组中去
	// 首先要获得包含最完整的mem%信息的pid号
	// 通过累计总和的方式来获得
	// 返回的是该pid在topInfo数组中的索引号
	public int getPidMem(){
		
		// 在memPid数组中每一列的数据的总和,因此totalMem数组的列数与top5pid的数目相同.
		Double totalMem[] = new Double[pidCountColumn];
		for(int i=0;i<pidCountColumn;i++){
			totalMem[i]=0.0;
		}		
		for(int j=0;j<pidCountColumn;j++){
			for(int i=0;i<topBlockRow;i++){
				totalMem[j] = totalMem[j]+ Double.valueOf(memPid[i][j]);
			}
		}
		int max = 0 ;
		int maxPidMem = 0;
		for(int i=0;i<pidCountColumn;i++){
			if(totalMem[i].intValue()>max){
				max = totalMem[i].intValue();
				maxPidMem = i;
			}		
		}
		return maxPidMem;
	}
	
	// 根据这个pid的值
	// 开始填写topInfo的内容
	public void topInfoGet(){
		int num = getPidMem();
		topInfo = new String[topBlockRow][topInfoColumn];
		for(int i=0;i<topBlockRow;i++){
			topInfo[i][7] = virtPid[i][num];
			topInfo[i][8] = resPid[i][num];
			topInfo[i][9] = memPid[i][num];
		}
		
		TopInfo tf;
		for(int i =0;i<topBlockRow;i++){
			tf = topHashmap.get(i).getTopInfo();
			topInfo[i][0] = tf.getTimeline();
			topInfo[i][1] = tf.getLoadAverage1();
			topInfo[i][2] = tf.getLoadAverage5();
			topInfo[i][3] = tf.getLoadAverage15();
			topInfo[i][4] = tf.getMemUsed();
			topInfo[i][5] = tf.getMemFree();
			topInfo[i][6] = tf.getSwapUsed();
		}	
	}
	
	private void culCpuMax(){
		int maxCpuValue;
		int temp =0 ;
		maxCpu = new String[pidCountColumn];
		for(int j=0;j<pidCountColumn;j++){
			maxCpuValue = 0;
			for(int i=0;i<topBlockRow;i++){
				temp = Double.valueOf(cpuPid[i][j]).intValue(); 
				if(temp >= maxCpuValue){
					maxCpuValue = temp;
				}
			}
			maxCpu[j] = String.valueOf(maxCpuValue);
		}
	}
	
	private void insertPidInfo(String tagserver){
		Connection conn = C3P0Pool.getConnection();
		PreparedStatement ps = null;
		String sql = "update stressinfo set toppidlist = ? where tagserver = ?";	
		String toppidlist = "p"+topPid[0];
		for(int i =1;i<pidCountColumn;i++){
			toppidlist = toppidlist+":p"+topPid[i];			
		}
		String cpuMaxData = maxCpu[0];
		for(int i=1;i<pidCountColumn;i++){
			cpuMaxData = cpuMaxData + ":" + maxCpu[i];
		}
		toppidlist = toppidlist + ";"+cpuMaxData;
		//System.out.println(toppidlist);		
		try {
				ps = conn.prepareStatement(sql);
				ps.setObject(1,toppidlist);
				ps.setObject(2,tagserver);
				ps.executeUpdate();			
			} catch (SQLException e) {
				e.printStackTrace();
			}		
		C3P0Pool.free(ps, conn);
	}
	
	private boolean saveResultCpu(String tagserver){
				
		Connection conn = C3P0Pool.getConnection();
		PreparedStatement ps = null;
		String sql = "insert into cpupid(tagserver,createtime,data) values(?,?,?) ";
		Date date = new Date();
		
		String pid;
		String cpuData ;
		String timestamp;
		int k=0;
		for(int i = 0;i<topBlockRow;i++){
			timestamp = topInfo[k++][0];
			cpuData = "{timestamp:"+"\""+timestamp+"\"";
			for(int j = 0;j<pidCountColumn;j++){
				pid = "p"+topPid[j];
				cpuData = cpuData + "," + pid + ":" +cpuPid[i][j];
			}
			cpuData = cpuData +"}";
			
			try {
				ps = conn.prepareStatement(sql);
				ps.setObject(1,tagserver);
				ps.setObject(2,date);
				ps.setObject(3,cpuData);
				ps.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}	
			//System.out.println(cpuData);
		}		
		C3P0Pool.free(ps, conn);
		return true;
}
	public void programCpu(File file){

		// 存储top5线程的信息
		saveResultCpu(Util.getFileInfo(file));
		Util.setFlag(file,"toppidcpu");
	}
	
	
	
	private boolean saveTopInfo(String tagserver){

		Connection conn = C3P0Pool.getConnection();
		PreparedStatement ps = null;
		String sql = "insert into mempid(tagserver,createtime,data) values(?,?,?) ";
		
		Date date = new Date();	
		String memData ;
		String timestamp;
		String loadAverage1;
		String loadAverage5;
		String loadAverage15;
		String memUsed;
		String memFree;
		String swapUsed;
		String memVirt;
		String memRes;
		String memPer;
		for(int i = 0;i<topBlockRow;i++){
			timestamp = topInfo[i][0];
			loadAverage1 = topInfo[i][1];
			loadAverage5 = topInfo[i][2];
			loadAverage15 = topInfo[i][3];
			memUsed = topInfo[i][4];
			memFree = topInfo[i][5];
			swapUsed = topInfo[i][6];
			memVirt = topInfo[i][7];
			memRes = topInfo[i][8];
			memPer = topInfo[i][9];
			memData = "{timestamp:"+"\""+timestamp+"\"";
			memData = memData+",loadAverage1:"+loadAverage1+",loadAverage5:"+loadAverage5+",loadAverage15:"+loadAverage15;
			memData = memData+",memUsed:"+memUsed+",memFree:"+memFree+",swapUsed:"+swapUsed;
			memData = memData+",memVirt:"+memVirt+",memRes:"+memRes+",memPer:"+memPer+"}";
			
			try {
				ps = conn.prepareStatement(sql);
				ps.setObject(1,tagserver);
				ps.setObject(2,date);
				ps.setObject(3,memData);
				ps.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		C3P0Pool.free(ps, conn);
		return true;
	}
	
	public void programTopInfo(File file){		
		saveTopInfo(Util.getFileInfo(file));
		Util.setFlag(file,"toppidmem");
		Util.setFlag(file, "topload");
	}
	
	public void program(File file) throws Exception{
		
		//处理标志，默认都是要处理的
		boolean cpuFlag = true;
		boolean memFlag = true;
		String tagserver = Util.getFileInfo(file);
		
		if(Util.checkDuplicatedData(tagserver,"cpupid")){
			cpuFlag = false;
		}
		
		if(Util.checkDuplicatedData(tagserver,"mempid")){
			memFlag = false;
		}
		// 如果cpu和mem的Flag标识存在有效的，就进行基本的数据处理
		if(cpuFlag|| memFlag){
			this.getPidList(file);
			this.cacheFile(file);
			this.dealCache();
			this.topInfoGet();
			topHashmap = null;
		}
		if(cpuFlag){
			this.culCpuMax();
			this.insertPidInfo(Util.getFileInfo(file));
			programCpu(file);
		}
		if(memFlag){
			programTopInfo(file);
		}
		// 最后要进行垃圾回收
		collectionResource();
	}
	
	private void collectionResource(){
		cpuPid = null;
		memPid = null;
		maxCpu = null;
	}
	
}



