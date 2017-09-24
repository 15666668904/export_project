package com.hxss.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import com.hxss.DAO.hxss_dao;
import com.hxss.DAO.hxss_daoimpl;
import com.hxss.VO.EN_PLAN_CALENDAR;
import com.hxss.VO.EN_RESOURCES;
import com.hxss.VO.HXSS_FK;
import com.hxss.VO.HXSS_task_resources;
import com.hxss.VO.hxss_task_ready;
import com.hxss.VO.noworking_day;
import com.hxss.VO.pro_obj;

import net.sf.mpxj.ConstraintType;
import net.sf.mpxj.DateRange;
import net.sf.mpxj.Day;
import net.sf.mpxj.Duration;
import net.sf.mpxj.ProjectCalendar;
import net.sf.mpxj.ProjectCalendarException;
import net.sf.mpxj.ProjectCalendarHours;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.Rate;
import net.sf.mpxj.Relation;
import net.sf.mpxj.RelationType;
import net.sf.mpxj.Resource;
import net.sf.mpxj.ResourceAssignment;
import net.sf.mpxj.ResourceContainer;
import net.sf.mpxj.Task;
import net.sf.mpxj.TaskType;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.WorkContour;
import net.sf.mpxj.common.SplitTaskFactory;
import net.sf.mpxj.mpx.MPXWriter;
import net.sf.mpxj.mspdi.MSPDIWriter;
import net.sf.mpxj.mspdi.schema.Project.Calendars.Calendar;
import net.sf.mpxj.planner.PlannerWriter;
import net.sf.mpxj.planner.schema.Constraint;
import net.sf.mpxj.writer.ProjectWriter;

public class hxss_serviceimpl implements hxss_service{
	private static hxss_dao hxss_dao=new hxss_daoimpl();
	@Override
	public String getprojectfile(String plan_version_sid,String xpmobs_sid,String xmlflag) {
		// TODO Auto-generated method stub
		ProjectFile projectFile=new ProjectFile();
		ProjectProperties projectProperties= projectFile.getProjectProperties();
		projectProperties.setProjectTitle(hxss_dao.getplan_version_title(plan_version_sid));
		projectProperties.setAuthor("zhenlong.shan's Project_export");
		projectProperties.setCompany("杭萧钢构股份有限公司");
		try {
			savecalendar(projectFile, xpmobs_sid);
			save_resource(plan_version_sid, projectFile, xpmobs_sid);
			savepro_obj(plan_version_sid, projectFile);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String filepath=getconfig("export_project_path")+"/"+new SimpleDateFormat("yyyyMMdd").format(new Date());
		createfilepath(filepath);
		if(xmlflag.equals("mpx")) {
			MPXWriter writer=new MPXWriter();
			writer.setLocale(Locale.CHINESE);
			try {
				writer.write(projectFile, filepath+"/"+hxss_dao.getplan_version_title(plan_version_sid)+".mpx");
				return filepath+"/"+hxss_dao.getplan_version_title(plan_version_sid)+".mpx";	
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(xmlflag.equals("xml")) {
			try {
				MSPDIWriter mspdiWriter=new MSPDIWriter();
				mspdiWriter.write(projectFile, filepath+"/"+hxss_dao.getplan_version_title(plan_version_sid)+".xml");
				return filepath+"/"+hxss_dao.getplan_version_title(plan_version_sid)+".xml";
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	private static String getconfig(String key){
		String prop_path = Thread.currentThread().getContextClassLoader().
				getResource("export_project.properties").getPath();
		Properties properties=new Properties();
		try {
			properties.load(new FileInputStream(prop_path));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return properties.getProperty(key);
	}

	//保存日历
	private static void savecalendar(ProjectFile projectFile,String xpmobs_sid) throws ParseException{
		//项目日历录入
		{
			List<EN_PLAN_CALENDAR>calendars= hxss_dao.getEN_PLAN_CALENDAR(xpmobs_sid, "1");
			ProjectCalendar projectCalendar=projectFile.addCalendar();
			if (calendars.size()==1){
				EN_PLAN_CALENDAR en_PLAN_CALENDAR=calendars.get(0);
				projectCalendar.setWorkingDay(Day.FRIDAY,Boolean.parseBoolean(en_PLAN_CALENDAR.getDay_5()));
				projectCalendar.setWorkingDay(Day.MONDAY,Boolean.parseBoolean(en_PLAN_CALENDAR.getDay_1()));
				projectCalendar.setWorkingDay(Day.SATURDAY,Boolean.parseBoolean(en_PLAN_CALENDAR.getDay_6()));
				projectCalendar.setWorkingDay(Day.SUNDAY,Boolean.parseBoolean(en_PLAN_CALENDAR.getDay_7()));
				projectCalendar.setWorkingDay(Day.THURSDAY,Boolean.parseBoolean(en_PLAN_CALENDAR.getDay_4()));
				projectCalendar.setWorkingDay(Day.TUESDAY,Boolean.parseBoolean(en_PLAN_CALENDAR.getDay_2()));
				projectCalendar.setWorkingDay(Day.WEDNESDAY,Boolean.parseBoolean(en_PLAN_CALENDAR.getDay_3()));
				projectCalendar.setName(en_PLAN_CALENDAR.getCalendar_name());
				//例外日期录入
				List<noworking_day>noworking_days=hxss_dao.getnoworking_day(en_PLAN_CALENDAR.getCalendar_sid());
				for(int i=0;i<noworking_days.size();i++){
					noworking_day noworking_day=noworking_days.get(i);
					if (null!=noworking_day.getFlag()&&noworking_day.getFlag().equals("1")) {
						ProjectCalendarException projectCalendarException=
								projectCalendar.addCalendarException(
										new SimpleDateFormat("yyyy-MM-dd").parse(noworking_day.getNonworking_day().substring(0, 10)),
										new SimpleDateFormat("yyyy-MM-dd").parse(noworking_day.getNonworking_day().substring(0, 10)));
						projectCalendarException.addRange(new DateRange(
								new SimpleDateFormat("yyyy-MM-dd").parse(noworking_day.getNonworking_day().substring(0, 10)), 
								new SimpleDateFormat("yyyy-MM-dd").parse(noworking_day.getNonworking_day().substring(0, 10))));
					}
					if(null!=noworking_day.getFlag()&&noworking_day.getFlag().equals("0")){
						ProjectCalendarException projectCalendarException=
								projectCalendar.addCalendarException(
										new SimpleDateFormat("yyyy-MM-dd").parse(noworking_day.getNonworking_day().substring(0, 10)),
										new SimpleDateFormat("yyyy-MM-dd").parse(noworking_day.getNonworking_day().substring(0, 10)));
					}
				}
			}else {
				//如果没有默认日历  默认七天工作制
				projectCalendar.setWorkingDay(Day.FRIDAY,true);
				projectCalendar.setWorkingDay(Day.MONDAY,true);
				projectCalendar.setWorkingDay(Day.SATURDAY,true);
				projectCalendar.setWorkingDay(Day.SUNDAY,true);
				projectCalendar.setWorkingDay(Day.THURSDAY,true);
				projectCalendar.setWorkingDay(Day.TUESDAY,true);
				projectCalendar.setWorkingDay(Day.WEDNESDAY,true);
				projectCalendar.setName("项目标准日历(7天工作制)");
			}
			ProjectCalendarHours h1=projectCalendar.addCalendarHours(Day.FRIDAY);
			h1.addRange(projectCalendar.DEFAULT_WORKING_MORNING);
			h1.addRange(projectCalendar.DEFAULT_WORKING_AFTERNOON);
			ProjectCalendarHours h2=projectCalendar.addCalendarHours(Day.MONDAY);
			h2.addRange(projectCalendar.DEFAULT_WORKING_MORNING);
			h2.addRange(projectCalendar.DEFAULT_WORKING_AFTERNOON);
			ProjectCalendarHours h3=projectCalendar.addCalendarHours(Day.SATURDAY);
			h3.addRange(projectCalendar.DEFAULT_WORKING_MORNING);
			h3.addRange(projectCalendar.DEFAULT_WORKING_AFTERNOON);
			ProjectCalendarHours h4=projectCalendar.addCalendarHours(Day.SUNDAY);
			h4.addRange(projectCalendar.DEFAULT_WORKING_MORNING);
			h4.addRange(projectCalendar.DEFAULT_WORKING_AFTERNOON);
			ProjectCalendarHours h5=projectCalendar.addCalendarHours(Day.THURSDAY);
			h5.addRange(projectCalendar.DEFAULT_WORKING_MORNING);
			h5.addRange(projectCalendar.DEFAULT_WORKING_AFTERNOON);
			ProjectCalendarHours h6=projectCalendar.addCalendarHours(Day.TUESDAY);
			h6.addRange(projectCalendar.DEFAULT_WORKING_MORNING);
			h6.addRange(projectCalendar.DEFAULT_WORKING_AFTERNOON);
			ProjectCalendarHours h7=projectCalendar.addCalendarHours(Day.WEDNESDAY);
			h7.addRange(projectCalendar.DEFAULT_WORKING_MORNING);
			h7.addRange(projectCalendar.DEFAULT_WORKING_AFTERNOON);
			projectFile.setDefaultCalendar(projectCalendar);
		}
		//非项目日历录入
		{
			List<EN_PLAN_CALENDAR>calendars= hxss_dao.getEN_PLAN_CALENDAR(xpmobs_sid, "0");
			out: for(int i=0;i<calendars.size();i++){
				EN_PLAN_CALENDAR en_PLAN_CALENDAR=calendars.get(i);
				//避免日历名称重复导致出错  如果存在同名日历  则直接跳过不录入
				List<ProjectCalendar>projectCalendars=projectFile.getCalendars();
				for(int j=0;j<projectCalendars.size();j++) {
					ProjectCalendar projectCalendar=projectCalendars.get(j);
					if(en_PLAN_CALENDAR.getCalendar_name().equals(projectCalendar.getName())) {
						projectCalendar.remove();
						break out;
					}
				}
				ProjectCalendar projectCalendar=projectFile.addDefaultBaseCalendar();
				projectCalendar.setName(en_PLAN_CALENDAR.getCalendar_name());
				projectCalendar.setWorkingDay(Day.FRIDAY,Boolean.parseBoolean(en_PLAN_CALENDAR.getDay_5()));
				projectCalendar.setWorkingDay(Day.MONDAY,Boolean.parseBoolean(en_PLAN_CALENDAR.getDay_1()));
				projectCalendar.setWorkingDay(Day.SATURDAY,Boolean.parseBoolean(en_PLAN_CALENDAR.getDay_6()));
				projectCalendar.setWorkingDay(Day.SUNDAY,Boolean.parseBoolean(en_PLAN_CALENDAR.getDay_7()));
				projectCalendar.setWorkingDay(Day.THURSDAY,Boolean.parseBoolean(en_PLAN_CALENDAR.getDay_4()));
				projectCalendar.setWorkingDay(Day.TUESDAY,Boolean.parseBoolean(en_PLAN_CALENDAR.getDay_2()));
				projectCalendar.setWorkingDay(Day.WEDNESDAY,Boolean.parseBoolean(en_PLAN_CALENDAR.getDay_3()));
				projectCalendar.setName(en_PLAN_CALENDAR.getCalendar_name());
				ProjectCalendarHours h1=projectCalendar.addCalendarHours(Day.FRIDAY);
				h1.addRange(projectCalendar.DEFAULT_WORKING_MORNING);
				h1.addRange(projectCalendar.DEFAULT_WORKING_AFTERNOON);
				ProjectCalendarHours h2=projectCalendar.addCalendarHours(Day.MONDAY);
				h2.addRange(projectCalendar.DEFAULT_WORKING_MORNING);
				h2.addRange(projectCalendar.DEFAULT_WORKING_AFTERNOON);
				ProjectCalendarHours h3=projectCalendar.addCalendarHours(Day.SATURDAY);
				h3.addRange(projectCalendar.DEFAULT_WORKING_MORNING);
				h3.addRange(projectCalendar.DEFAULT_WORKING_AFTERNOON);
				ProjectCalendarHours h4=projectCalendar.addCalendarHours(Day.SUNDAY);
				h4.addRange(projectCalendar.DEFAULT_WORKING_MORNING);
				h4.addRange(projectCalendar.DEFAULT_WORKING_AFTERNOON);
				ProjectCalendarHours h5=projectCalendar.addCalendarHours(Day.THURSDAY);
				h5.addRange(projectCalendar.DEFAULT_WORKING_MORNING);
				h5.addRange(projectCalendar.DEFAULT_WORKING_AFTERNOON);
				ProjectCalendarHours h6=projectCalendar.addCalendarHours(Day.TUESDAY);
				h6.addRange(projectCalendar.DEFAULT_WORKING_MORNING);
				h6.addRange(projectCalendar.DEFAULT_WORKING_AFTERNOON);
				ProjectCalendarHours h7=projectCalendar.addCalendarHours(Day.WEDNESDAY);
				h7.addRange(projectCalendar.DEFAULT_WORKING_MORNING);
				h7.addRange(projectCalendar.DEFAULT_WORKING_AFTERNOON);
				//例外日期录入
				List<noworking_day>noworking_days=hxss_dao.getnoworking_day(en_PLAN_CALENDAR.getCalendar_sid());
				for(int j=0;j<noworking_days.size();j++){
					noworking_day noworking_day=noworking_days.get(j);
					if (null!=noworking_day.getFlag()&&noworking_day.getFlag().equals("1")) {
						ProjectCalendarException projectCalendarException=
								projectCalendar.addCalendarException(
										new SimpleDateFormat("yyyy-MM-dd").parse(noworking_day.getNonworking_day().substring(0, 10)),
										new SimpleDateFormat("yyyy-MM-dd").parse(noworking_day.getNonworking_day().substring(0, 10)));
						projectCalendarException.addRange(new DateRange(
								new SimpleDateFormat("yyyy-MM-dd").parse(noworking_day.getNonworking_day().substring(0, 10)), 
								new SimpleDateFormat("yyyy-MM-dd").parse(noworking_day.getNonworking_day().substring(0, 10))));
					}
					if(null!=noworking_day.getFlag()&&noworking_day.getFlag().equals("0")){
						ProjectCalendarException projectCalendarException=
								projectCalendar.addCalendarException(
										new SimpleDateFormat("yyyy-MM-dd").parse(noworking_day.getNonworking_day().substring(0, 10)),
										new SimpleDateFormat("yyyy-MM-dd").parse(noworking_day.getNonworking_day().substring(0, 10)));
					}
				}
			}
		}
	}

	//创建目录
	private static void createfilepath(String filepath){
		File file=new File(filepath);
		file.mkdirs();
	}

	//录入任务
	private static String savepro_obj(String plan_version_sid,ProjectFile projectFile) throws ParseException{
		List<pro_obj>pro_objs=hxss_dao.getpro_obj(plan_version_sid);
		int Time_difference=hxss_dao.getTime_difference(plan_version_sid);
		Task task0=projectFile.addTask();
		task0.setUniqueID(0);
		task0.setID(0);
		task0.setName(hxss_dao.getplan_version_title(plan_version_sid));
		for(int i=0;i<pro_objs.size();i++){
			pro_obj pro_obj=pro_objs.get(i);
			Task task=task0.addTask();
			java.util.Calendar start_date = java.util.Calendar.getInstance();
			java.util.Calendar finish_date = java.util.Calendar.getInstance();
			start_date.setTime(new SimpleDateFormat("yyyy-MM-dd").
					parse(pro_obj.getCcpm_m_ls_date()));
			finish_date.setTime(new SimpleDateFormat("yyyy-MM-dd").
					parse(pro_obj.getCcpm_m_lf_date()));
			if(null!=pro_obj.getIs_fk()
					&&null!=pro_obj.getTask_type()
					&&pro_obj.getTask_type().equals("任务作业")
					&&pro_obj.getIs_fk().equals("一般")
					&&null!=pro_obj.getTask_manager()) {
				task.setContact(pro_obj.getTask_manager());
				settask_ready(task, pro_obj);
			}
			if(null!=pro_obj.getIs_fk()
					&&pro_obj.getIs_fk().equals("齐套")
					&&null!=pro_obj.getFk_manager()) {
				task.setContact(pro_obj.getFk_manager());
				task.setMilestone(true);
				task.setConstraintType(ConstraintType.START_NO_EARLIER_THAN);
				task.setConstraintDate(start_date.getTime());
				setfk_list(task, pro_obj);
			}
			task.setID(i+1);
			task.setName(pro_obj.getObj_name());
			if(null==pro_obj.getBuffer_period()){
				pro_obj.setBuffer_period("0");
			}

			if(null!=pro_obj.getTask_type()&&pro_obj.getTask_type().equals("完成里程碑")){
				task.setMilestone(true);
			} 
			task.setStart(start_date.getTime());
			task.setFinish(finish_date.getTime());
			task.setDuration(Duration.getInstance(Double.parseDouble(pro_obj.getBuffer_period()), 
					TimeUnit.DAYS));
			task.setType(TaskType.FIXED_DURATION);
		}
		savetasklogic(pro_objs, projectFile,plan_version_sid);
		savehxss_task_resources(pro_objs, projectFile);
		return "success";
	}

	private static void setfk_list(Task task,pro_obj pro_obj) {
		List<HXSS_FK>fk_list=hxss_dao.getfk_list(pro_obj.getObj_sid());
		if(fk_list.size()!=0) {
			String notes="";
			for(int i=0;i<fk_list.size();i++) {
				HXSS_FK hxss_FK=fk_list.get(i);
				if(i!=fk_list.size()-1) {
					notes=notes+hxss_FK.getFk_name()+"\n";
				}else {
					notes=notes+hxss_FK.getFk_name();
				}
			}
			task.setNotes(notes);
		}
	}

	private static void settask_ready(Task task,pro_obj pro_obj) {
		List<hxss_task_ready>hxss_task_readies=hxss_dao.getHxss_task_readies(pro_obj.getObj_sid());
		if(hxss_task_readies.size()!=0) {
			String notes="";
			for(int i=0;i<hxss_task_readies.size();i++) {
				hxss_task_ready hxss_task_ready=hxss_task_readies.get(i);
				if(i!=hxss_task_readies.size()-1) {
					notes=notes+hxss_task_ready.getTask_ready_item()+"\n";
				}else {
					notes=notes+hxss_task_ready.getTask_ready_item();
				}
			}
			task.setNotes(notes);
		}
	}

	private static void savehxss_task_resources(List<pro_obj>pro_objs,ProjectFile projectFile) {
		for(int i=0;i<pro_objs.size();i++){
			pro_obj pro_obj=pro_objs.get(i);
			List<HXSS_task_resources>hxss_task_resources=hxss_dao.gettaskresources(pro_obj.getObj_sid());
			if(null!=hxss_task_resources&&hxss_task_resources.size()!=0){
				for(int j=0;j<hxss_task_resources.size();j++){
					HXSS_task_resources task_resources=hxss_task_resources.get(j);
					Task task=projectFile.getTaskByID(i+1);
					ResourceAssignment resourceAssignment= task.addResourceAssignment(getresource(
							task_resources.getResources_sid(), projectFile));
					try {
						resourceAssignment.setWorkContour(WorkContour.FLAT);
						resourceAssignment.setWork(Duration.getInstance(task.getDuration().getDuration()*8, TimeUnit.HOURS));
						resourceAssignment.setRemainingWork(Duration.getInstance(task.getDuration().getDuration()*8, TimeUnit.HOURS));
						resourceAssignment.setUnits(NumberFormat.getInstance().parse(
								String.valueOf(Integer.parseInt(task_resources.getReq_quantity())*100)));
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		}
	}

	private static Resource getresource(String resource_sid,ProjectFile projectFile){
		String resources_name= hxss_dao.getresource(resource_sid).getResources_name();
		List allresource=projectFile.getAllResources();
		for(int i=0;i<allresource.size();i++){
			Resource resource=(Resource) allresource.get(i);
			if(resources_name.equals(resource.getName())){
				return resource;
			}
		}
		return null;
	}

	private static void savetasklogic(List<pro_obj>pro_objs,ProjectFile projectFile,String plan_version_sid){
		for(int i=0;i<pro_objs.size();i++){
			pro_obj pro_obj=pro_objs.get(i);
			List<String>pred_task_sids=hxss_dao.getPred_task_sid(pro_obj.getObj_sid(),plan_version_sid);
			if(pred_task_sids.size()>0){
				for(int j=0;j<pred_task_sids.size();j++){
					String pred_task_sid=pred_task_sids.get(j);
					projectFile.getTaskByID(i+1).addPredecessor(
							projectFile.getTaskByID(gettssk_id(pro_objs, pred_task_sid)),
							RelationType.FINISH_START, null);
				}
			}
		}
	}

	//取得任务id
	private static int gettssk_id(List<pro_obj>pro_objs,String obj_sid){
		for(int i=0;i<pro_objs.size();i++){
			pro_obj pro_obj=pro_objs.get(i);
			if(pro_obj.getObj_sid().equals(obj_sid)){
				return i+1;
			}
		}
		return -1;
	}

	//资源录入
	private static void save_resource(String plan_version_sid,ProjectFile projectFile,String xpmobs_sid) throws ParseException{
		try {

		} catch (Exception e) {
			// TODO: handle exception
		}
		List<EN_RESOURCES>resourcelist=hxss_dao.getresources(xpmobs_sid);
		ResourceContainer resources=projectFile.getAllResources();
		for(int i=0;i<resourcelist.size();i++){
			EN_RESOURCES en_RESOURCES=resourcelist.get(i);
			Resource resource=resources.add();
			resource.setName(en_RESOURCES.getResources_name());
			resource.setMaxUnits(NumberFormat.getInstance().parse(String.valueOf
					(Integer.parseInt(en_RESOURCES.getMax_supply())*100)));
			resource.setStandardRate(new Rate(0,TimeUnit.DAYS));
			resource.setOvertimeRate(new Rate(0, TimeUnit.DAYS));
			if(null!=en_RESOURCES.getResource_calendar_sid()&&
					!en_RESOURCES.getResource_calendar_sid().equals("")
					&&null!=hxss_dao.getresource_calendar(en_RESOURCES.getResource_calendar_sid())) {
				ProjectCalendar projectCalendar= projectFile.addCalendar();
				projectCalendar.setParent(getresources_calendar(projectFile, en_RESOURCES.getResource_calendar_sid()));
				resource.setResourceCalendar(projectCalendar);
			}
		}
	}

	//取得资源日历
	private static ProjectCalendar getresources_calendar(ProjectFile projectFile,String calendar_sid) {
		String calendar_name= hxss_dao.getresource_calendar(calendar_sid).getCalendar_name();
		return projectFile.getCalendarByName(calendar_name);
	}

	@Override
	public String Data_validation(String plan_version_sid) {
		// TODO Auto-generated method stub
		String result="success";
		List<pro_obj>pro_objs=hxss_dao.getpro_obj(plan_version_sid);
		for(int i=0;i<pro_objs.size();i++) {
			pro_obj pro_obj=pro_objs.get(i);
			if(pro_obj.getBuffer_period()==null||pro_obj.getBuffer_period().equals("")
					||pro_obj.getCcpm_m_ls_date()==null||pro_obj.getCcpm_m_ls_date().equals("")
					||pro_obj.getCcpm_m_lf_date()==null||pro_obj.getCcpm_m_lf_date().equals("")) 
			{
				if(result.equals("success")) {
					result="请先进行缓冲处理";
				}else {
					result=result+"<br/>清先进行缓冲处理";
				}
				return result;
			}
		}
		return result;
	}
}
