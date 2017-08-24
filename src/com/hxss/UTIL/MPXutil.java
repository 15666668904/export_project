package com.hxss.UTIL;

import java.io.File;
import java.io.IOException;

/**

工具类　把ｍｐｘ转换成ｍｐｐ　，这个依赖与系统已安装了ｐｒｏｊｅｃｔ　

 **/



public class MPXutil {
	public static boolean convertMpxToMpp(String file_path) {
		String mpx_file=file_path.substring(0, 28);
		String mpx_name=file_path.substring(28);
		//如果文件存在，先删除  如果进程存在  先杀进程  避免因为文件占用删除失败
		Process process;
		try {
			process = Runtime.getRuntime().exec("taskkill /f /t /im WINPROJ.EXE");
			process.waitFor();
			Thread.sleep(100);
			File file=new File(mpx_file+mpx_name.substring(0,mpx_name.indexOf("."))+".mpp");
			if(file.exists()){
				file.delete();
			}
		} catch (IOException | InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			process=Runtime.getRuntime().exec("powershell G://export_project.ps1 "+mpx_file+","+
					mpx_name.substring(0, mpx_name.indexOf(".")));
			//一定要waitfor  不然不会等待cmd命令执行完毕!!!!!
			process.waitFor();
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
}