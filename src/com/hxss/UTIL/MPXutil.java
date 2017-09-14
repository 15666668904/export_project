package com.hxss.UTIL;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**

工具类　把ｍｐｘ转换成ｍｐｐ　，这个依赖与系统已安装了ｐｒｏｊｅｃｔ　

 **/



public class MPXutil {
	public static boolean convertMpxToMpp(String file_path,String real_path){
		String mpx_file=file_path.substring(0, 28);
		String mpx_name=file_path.substring(28);
		//如果文件存在，先删除  如果进程存在  先杀进程  避免因为文件占用删除失败
		try {	
			real_path=real_path+"powershell\\export_project.ps1";
			Process process = Runtime.getRuntime().exec("taskkill /f /t /im WINPROJ.EXE");
			process.waitFor();
			Thread.sleep(100);
			File file=new File(mpx_file+mpx_name.substring(0,mpx_name.indexOf("."))+".mpp");
			if(file.exists()){
				file.delete();
			}
			Process powershell=Runtime.getRuntime().exec("powershell  "+real_path+" "+mpx_file+","+
					mpx_name.substring(0, mpx_name.indexOf(".")));
			String line=null;
			BufferedReader  bufferedReader = new BufferedReader  
					(new InputStreamReader(powershell.getInputStream()));  
			while ((line = bufferedReader.readLine()) != null) { 
				if(line.equals("export_project_over")) {
					return true;
				}
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return false;
	}
}