package com.weibo.test;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.weibo.product.ProgramInput;
import com.weibo.util.Util;


public class TestData {

	public static File chooseFile(File dir){
		JFileChooser choser=new JFileChooser();
	    FileNameExtensionFilter filter = new FileNameExtensionFilter(null,"log");
	    choser.addChoosableFileFilter(filter);
	    choser.setDialogTitle("选择文件或者目录");
	    choser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	    choser.setCurrentDirectory(dir);
	    int flag = choser.showDialog(null,"选择文件");
	    if(flag==JFileChooser.CANCEL_OPTION||flag==JFileChooser.ERROR_OPTION){
	        return null;
	    }else{
	    	File file=choser.getSelectedFile();            
	    	return file;
	    }
	}
	
	public static void timeProgram(File file) throws Exception{		
		long startMili=System.currentTimeMillis();
		System.out.println("开始 "+startMili);		
		ProgramInput.program(file);		
		long endMili=System.currentTimeMillis();		
		System.out.println("结束 s"+endMili);
		System.out.println("总耗时为："+(endMili-startMili)+"毫秒");		
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		File file ;
		// windows系统将激活图像界面版本
		if(System.getProperties().getProperty("os.name").toUpperCase().indexOf("WINDOWS")!=-1){
			System.out.println("This is a windows system");
			System.out.println("We will use Gui");
			File dir = new File("C:"+File.pathSeparator);
			file = chooseFile(dir);
		}else{
			// 如果不是windows系统
			String str = Util.readUserInput("Input the directory or file:");
			System.out.println("your input is："+str);
			file = new File(str);
		}		

		if(file != null && file.exists()){
			System.out.println("File path is " + file.getAbsolutePath());
			timeProgram(file);
		}else{
			System.out.println(file.getAbsolutePath()+"is not existed!");
		}
		System.in.read();
		System.exit(0);
	}
}
