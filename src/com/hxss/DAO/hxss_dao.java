package com.hxss.DAO;

import java.util.List;

import com.hxss.VO.EN_PLAN_CALENDAR;
import com.hxss.VO.EN_RESOURCES;
import com.hxss.VO.HXSS_FK;
import com.hxss.VO.HXSS_task_resources;
import com.hxss.VO.hxss_task_ready;
import com.hxss.VO.noworking_day;
import com.hxss.VO.pro_obj;

import net.sf.mpxj.mspdi.schema.Project.Resources;

public interface hxss_dao {
	public List<EN_PLAN_CALENDAR> getEN_PLAN_CALENDAR(String xpmobs_sid,String calendar_deafult_version);
	public List<noworking_day>getnoworking_day(String calendar_sid);
	public String getplan_version_title(String plan_version_sid);
	public List<pro_obj>getpro_obj(String plan_version_sid);
	public List<String> getPred_task_sid(String obj_sid,String plan_version_sid);
	public List<EN_RESOURCES>getresources(String xpmobs_sid);
	public List<HXSS_task_resources>gettaskresources(String obj_sid);
	public EN_RESOURCES getresource(String resource_sid);
	public EN_PLAN_CALENDAR getresource_calendar(String calendar_sid);
	public List<hxss_task_ready>getHxss_task_readies(String obj_sid);
	public List<HXSS_FK>getfk_list(String obj_sid);
	public int getTime_difference(String plan_version_sid);
}
