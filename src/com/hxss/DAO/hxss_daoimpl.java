package com.hxss.DAO;
import java.util.List;

import org.hibernate.Session;

import com.hxss.UTIL.HibernateUtil;
import com.hxss.VO.EN_PLAN_CALENDAR;
import com.hxss.VO.EN_RESOURCES;
import com.hxss.VO.HXSS_FK;
import com.hxss.VO.HXSS_task_resources;
import com.hxss.VO.hxss_task_ready;
import com.hxss.VO.noworking_day;
import com.hxss.VO.pro_obj;

import net.sf.mpxj.mspdi.schema.Project.Resources;

public class hxss_daoimpl implements hxss_dao{

	@Override
	public List<EN_PLAN_CALENDAR> getEN_PLAN_CALENDAR(String xpmobs_sid, String calendar_deafult_version) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.getSession();
		try {
			if (calendar_deafult_version.equals("1")){
				@SuppressWarnings("unchecked")
				List<EN_PLAN_CALENDAR>calendars=session.createQuery(
						"from EN_PLAN_CALENDAR where xpmobs_sid='"+xpmobs_sid+"' "
								+ "and calendar_deafult_version='1'").list();
				return calendars;
			}else {
				@SuppressWarnings("unchecked")
				List<EN_PLAN_CALENDAR>calendars=session.createQuery(
						"from EN_PLAN_CALENDAR where xpmobs_sid='"+xpmobs_sid+"' "
								+ "and calendar_deafult_version<>'1'").list();
				return calendars;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally {
			session.close();
		}
	}

	@Override
	public List<noworking_day> getnoworking_day(String calendar_sid) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.getSession();
		try {
			return session.createQuery("from noworking_day where calendar_sid='"+calendar_sid+"'").list();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}finally {
			session.close();
		}
	}

	@Override
	public String getplan_version_title(String plan_version_sid) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.getSession();
		try {
			return (String) session.createSQLQuery("select cast(plan_version_title as varchar(255)) from en_plan_version_cj where "
					+ "plan_version_sid='"+plan_version_sid+"'").uniqueResult();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}finally {
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<pro_obj> getpro_obj(String plan_version_sid) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.getSession();
		try {
			return session.createQuery("from pro_obj where plan_version_sid='"+plan_version_sid+"' AND ISNULL(OBJ_TYPE,'')='TASK'").list();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}finally {
			session.close();
		}
	}

	@Override
	public List<String> getPred_task_sid(String obj_sid,String plan_version_sid) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.getSession();
		try {
			//带缓冲的版本
			//return	session.createQuery("select pred_task_sid from HXSS_taskpred_CCPM where task_sid='"+obj_sid+"' and plan_version_sid='"+plan_version_sid+"'").list();
			return	session.createQuery("select pred_task_sid from HXSS_taskpred_CCPM where task_sid='"+obj_sid+"'").list();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}finally {
			session.close();
		}
	}

	@Override
	public List<EN_RESOURCES> getresources(String xpmobs_sid) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.getSession();
		try{
			return session.createQuery("from EN_RESOURCES where xpmobs_sid='"+xpmobs_sid+"'").list();
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}finally {
			session.close();
		}
	}

	@Override
	public List<HXSS_task_resources> gettaskresources(String obj_sid) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.getSession();
		try{
			return session.createQuery("from HXSS_task_resources where obj_sid='"+obj_sid+"'").list();
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}finally{
			session.close();
		}
	}

	@Override
	public EN_RESOURCES getresource(String resource_sid) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.getSession();
		try{
			return (EN_RESOURCES) session.createQuery("from EN_RESOURCES where resources_sid='"+resource_sid+"'").uniqueResult();
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}finally {
			session.close();
		}
	}

	@Override
	public EN_PLAN_CALENDAR getresource_calendar(String calendar_sid) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.getSession();
		try{
			return (EN_PLAN_CALENDAR) session.createQuery("from EN_PLAN_CALENDAR where calendar_sid='"+calendar_sid+"'").uniqueResult();
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}finally {
			session.close();
		}
	}

	@Override
	public List<hxss_task_ready> getHxss_task_readies(String obj_sid) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.getSession();
		try{
			return  session.createQuery("from hxss_task_ready where obj_sid='"+obj_sid+"'").list();
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}finally {
			session.close();
		}
	}

	@Override
	public List<HXSS_FK> getfk_list(String obj_sid) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.getSession();
		try{
			return  session.createQuery("from HXSS_FK where obj_sid='"+obj_sid+"'").list();
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}finally {
			session.close();
		}
	}

	@Override
	public int getTime_difference(String plan_version_sid) {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.getSession();
		try{
			int resule=(int) session.
					createSQLQuery("select datediff(day,min(ccpm_m_ls_date),"
							+ "getdate()) from pro_obj where "
							+ "plan_version_sid='"+plan_version_sid+"'").uniqueResult();
			if(resule<0) {
				resule=0;
			}
			return resule;
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return 0;
		}finally {
			session.close();
		}
	}
}
