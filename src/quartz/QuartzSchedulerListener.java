package quartz;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class QuartzSchedulerListener implements ServletContextListener{

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		ResourceBundle bundle=ResourceBundle.getBundle("quartz", Locale.getDefault());
		JobDetail jobDetail=JobBuilder.newJob(SchedulerJob.class).
				withIdentity("anyjobname","group1").build();
		Trigger trigger=TriggerBuilder.newTrigger()
				.withIdentity("anyjobname", "group1")
				.withSchedule(CronScheduleBuilder
						.cronSchedule(bundle.getString("quartz.task.time"))).build();
		try {
			Scheduler scheduler = new StdSchedulerFactory().getScheduler();
			scheduler.start();
			scheduler.scheduleJob(jobDetail,trigger);
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
