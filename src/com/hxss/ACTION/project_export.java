package com.hxss.ACTION;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import com.hxss.UTIL.MPXutil;
import com.hxss.service.hxss_service;
import com.hxss.service.hxss_serviceimpl;
import com.opensymphony.xwork2.ActionSupport;

public class project_export extends ActionSupport{
	private String file_name;
	private String plan_version_sid;
	private String xpmobs_sid;
	public String getPlan_version_sid() {
		return plan_version_sid;
	}
	public void setPlan_version_sid(String plan_version_sid) {
		this.plan_version_sid = plan_version_sid;
	}
	public String getXpmobs_sid() {
		return xpmobs_sid;
	}
	public void setXpmobs_sid(String xpmobs_sid) {
		this.xpmobs_sid = xpmobs_sid;
	}
	public String getFile_name() {
		return file_name;
	}
	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}
	public InputStream getInputStream(){
		hxss_service hxss_service=new hxss_serviceimpl();
		String file_path=hxss_service.getprojectfile(plan_version_sid, xpmobs_sid);
		MPXutil.convertMpxToMpp(file_path);
		File file=new File(file_path.substring(0,28)+file_path.substring(28).substring(0,file_path.substring(28).indexOf("."))+".mpp");
		try {
			InputStream	inputStream = new FileInputStream(file);
			//解决中文乱码
			file_name=new String(file.getName().getBytes("gbk"),"8859_1");
			return inputStream;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	@Override
	public String execute() throws Exception {
		// TODO Auto-generated method stub
		hxss_service hxss_service=new hxss_serviceimpl();
		String file_path=hxss_service.getprojectfile(plan_version_sid, xpmobs_sid);
		if ("error".equals(file_path)) {
			return ERROR;
		}
		return SUCCESS;
	}
}

