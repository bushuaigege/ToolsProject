package com.hk.jishunda.control;

import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.hk.jishunda.task.RqJsdTask;


/** 
* @ClassName: JxdScheduler 
* @author bushuai
* @Description: 吉顺达约车抢号调度器
* @date 2016年8月22日 下午6:16:30 
*/
public class JxdScheduler {
	
	private final static SchedulerFactory sf = new StdSchedulerFactory();
	private final Logger logger = Logger.getLogger(JxdScheduler.class);
	
	
	public void scheduleJxdCron(){
		try {
			/**************************数据请求调度器**************************************/
			Scheduler capture = sf.getScheduler();
			
			JobDetail job = JobBuilder.newJob().ofType(RqJsdTask.class)
					.withIdentity("JxdDayJob", "jxdGroup").build();
	
			Trigger trigger = TriggerBuilder.newTrigger().withIdentity("jxdDayTrigger", "jxdGroup")
					.startNow().withSchedule(CronScheduleBuilder
							.cronSchedule("0 0 9 * * ?")).build();
			
			capture.scheduleJob(job, trigger);
			
			if (!capture.isShutdown()) {
				capture.start();     
			}
		} catch (SchedulerException e) {
			logger.error("运行scheduleJxdCron方法===吉顺达与车定时调度错误", e);
			e.printStackTrace();
		}	
	}
	
	
	public static void main(String[] args) {
		new JxdScheduler().scheduleJxdCron();
	}
	
}
