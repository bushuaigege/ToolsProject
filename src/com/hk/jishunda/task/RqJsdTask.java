package com.hk.jishunda.task;


import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.hk.jishunda.utils.CommonUtils;

/** 
* @ClassName: RqJsdTask 
* @author bushuai
* @Description: 吉顺达约车抢号工具
* @date 2016年8月13日 下午5:43:39 
*/
public class RqJsdTask implements Job{
	private Logger log = Logger.getLogger(RqJsdTask.class);
	
	/** 
	* @Fields loginUrl : 吉顺达微信平台登录URL
	*/
	private final String loginUrl = "http://dsmis.jishunda.cn/WeiXin/Student/StuLogin.aspx?"
			+ "wx=gh_5151a34f78a2&openId=ojiehs1W_XBX2TndTMlx9JgccymQ"
			+ "&back=%2fWeiXin%2fStudent%2fHome.aspx"
			+ "%3fcode%3d021zIBZV1GDnP81HFJYV15wwZV1zIBZT%26state%3dgh_5151a34f78a2";
	
	/** 
	* @Fields wantGetCar : 我要约车入口
	*/
	private final String wantGetCar = "http://dsmis.jishunda.cn/WeiXin/Student/AddOrder.aspx";
	
	
	private final String brower = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 "
			+ "(KHTML, like Gecko) Chrome/39.0.2171.65 Safari/537.36";
	
	/**
	 * 查询约车时间
	 */
	private String queryScheduleList = "http://dsmis.jishunda.cn/WeiXin/Student/ScheduleList.aspx?&LoadAjaxData=LoadTime"
			+ "&coachId=432F26C5-57E0-4A93-8F17-DAADC0654638&date=%1$s&id=0.7073767797095698";
	
	//表达所需参数
	private final String viewstate = "/wEPDwUKLTc1MTg1MzgwNA9kFgICAw9kFgQCAQ8PFgIeCEltYWdlVXJsBTFod"
			+ "HRwOi8vZHNtaXMuamlzaHVuZGEuY24vU2Nob29sSW1hZ2UvMDEwc21hbGwucG5nZGQCEQ8PFgIeBFRleHQF"
			+ "Nua3seWcs+W4guWQiemhuui+vuacuuWKqOi9pumpvumptuWRmOWfueiureaciemZkOWFrOWPuGRkGAEFHl9"
			+ "fQ29udHJvbHNSZXF1aXJlUG9zdEJhY2tLZXlfXxYBBQ5jaGtSZW1lbWJlclB3ZGUDGEFnRCYisNU4mDVoVsY"
			+ "BSyqdQSUPgUR6HHeUHAAH";
	private final String eventvalidation = "/wEWBwLzq/flCAKl1bKzCQK1qbSRCwKw8v7wBgKO3rnoDQLl1Z2cDwK128WQAcOJ/"
			+ "4vyiURz65bQ9AOZMyfZLfxMUlToDzbEoKG/x59t";
	
	/** 
	* @Fields submit_VIEWSTATE : 提交约车的参数
	*/
	private final String submit_VIEWSTATE = "/wEPDwULLTE1MTc1Njc5MzJkZP+QaPM9triDAIE"
			+ "KYI6VoVALi9nopJJE1j5vbaEpv5sG";
	private final String submit_EVENTVALIDATION = "/wEWDQK1zv26DgKP0OeuBgK9mbPjAwKA+7SWBAKi2b28AgLlic"
			+ "SCDgL5h+3dBgKct7iSDAL4p/7IAgKJ3pHHDQLypMGrDQKP3rHHD"
			+ "QKv3bT4B5mdIet/5ACKPZpHkluXYXRCgydVKoN/IFH0BZfBU3Z8";
	
	
	/** 
	* @Fields httpClient : client请求对象
	*/
	private HttpClient httpClient = new HttpClient();
	/** 
	* @Fields tmpcookies : cookies信息
	*/
	private StringBuffer tmpcookies = new StringBuffer();
	
	/** 
	* @Title: signIn 
	* @Description:   用户登录
	*/
	public void signIn() {
		//httpClient.getHostConfiguration().setProxy("127.0.0.1", 8888);
		// 模拟登陆，按实际服务器端要求选用 Post 或 Get 请求方式
		PostMethod postMethod = new PostMethod(loginUrl);
		// 设置登陆时要求的信息，用户名和密码
		NameValuePair[] data = { new NameValuePair("txtUserName", "430921199512035453"),
				new NameValuePair("txtPassword", "035453"),
				new NameValuePair("hdnWxUserId", "ojiehs1W_XBX2TndTMlx9JgccymQ"),
				new NameValuePair("hdnWeiXinNo", "gh_5151a34f78a2"),
				new NameValuePair("btnBinding", "登录系统"),
				new NameValuePair("__VIEWSTATE", viewstate),
				new NameValuePair("__EVENTVALIDATION",eventvalidation )};
					
		// 你还可以通过 PostMethod/GetMethod 设置更多的请求后数据
		// 例如，referer 从哪里来的，UA 像搜索引擎都会表名自己是谁，无良搜索引擎除外
		postMethod.setRequestHeader("Referer", loginUrl);
		postMethod.setRequestHeader("User-Agent",brower);
		postMethod.setRequestBody(data);
		try {
			postMethod.releaseConnection();//这里最好把之前的资源放掉
			// 设置 HttpClient 接收 Cookie,用与浏览器一样的策略
			httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
			httpClient.getParams().setParameter(HttpMethodParams.SINGLE_COOKIE_HEADER, true);  
			int isSuccess = httpClient.executeMethod(postMethod);
			System.out.println("状态:" + isSuccess);
			
			// 获得登陆后的 Cookie
			Cookie[] cookies = httpClient.getState().getCookies();
			for (Cookie c : cookies)
				tmpcookies.append(c.toString() + ";");
		} catch (Exception e) {
			log.error(e.getMessage().concat("-----signIn method throw exception"), e);
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 请求工具类
	 * @param url
	 * @param isget
	 * @return
	 */
	private String menthod_request(String url,boolean isget){
		String msg = null;
		try{
			log.info("cookies is..." + tmpcookies.toString());
			
			if(isget){
				//请求我要约车页面
				GetMethod getMethod = new GetMethod(url);
				getMethod.setRequestHeader("cookie",tmpcookies.toString());
				httpClient.executeMethod(getMethod);
				msg = getMethod.getResponseBodyAsString();
				
			}else{
				//请求我要约车页面
				PostMethod postMethod = new PostMethod(url);
				postMethod.setRequestHeader("cookie",tmpcookies.toString());
				httpClient.executeMethod(postMethod);
				msg = postMethod.getResponseBodyAsString();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return msg;
	}
	
	/** 
	* @Title: requestWantCar 
	* @Description: 请求约车页面逻辑操作
	* @return  
	*/
	private synchronized boolean requestWantCar(){
		int isSuccess = 0;
		try {
			//查询约车时间号源段
			Calendar calen = Calendar.getInstance();
			calen.set(Calendar.DAY_OF_MONTH, calen.get(Calendar.DAY_OF_MONTH) + 1);
			String hidTrainTime = "",hidScheduleId = "",lblTrainDate = CommonUtils.DateFormat("yyyy-MM-dd", calen.getTime());
			String timePeriodHTML = menthod_request(String.format(queryScheduleList,lblTrainDate),true);
			log.info("约车时间段~~" + timePeriodHTML);
			
			if(timePeriodHTML == null || timePeriodHTML.trim().isEmpty())return false;
			
			//转换HTML拾取器
			Document doc = Jsoup.parse(timePeriodHTML);
			
			//获取时间ID
			Element schedule09 = doc.select("span:contains(09:00)").first();
			Element schedule10 = doc.select("span:contains(10:00)").first();
			Element schedule17 = doc.select("span:contains(17:00)").first();
			Element schedule11 = doc.select("span:contains(11:00)").first();
			
			//页面表单逻辑可以连续多选时间结果后台服务不允许60min
			//优先选择(hidScheduleId不需要进行逗号替换,页面代码只是作为判断而非表单提交的参数)
			if(schedule09 != null){
				hidTrainTime = lblTrainDate.concat(" ").concat("09:00-10:00");
				hidScheduleId += schedule09.nextElementSibling().val();
			}else if(schedule10 != null){
				hidTrainTime = lblTrainDate.concat(" ").concat("10:00-11:00");
				hidScheduleId += schedule10.nextElementSibling().val();
			}else if(schedule17 != null){
				hidTrainTime = lblTrainDate.concat(" ").concat("17:00-18:00");
				hidScheduleId = schedule17.nextElementSibling().val();
			}else if(schedule11 != null){
				hidTrainTime = lblTrainDate.concat(" ").concat("11:00-12:00");
				hidScheduleId = schedule11.nextElementSibling().val();
			}
			
			//没有号源就中断请求
			/*if(hidScheduleId.isEmpty()){
				log.info("获取号源ID为空~~~~");
				return false;
			}*/
			
			PostMethod postMethod = new PostMethod(wantGetCar);
			postMethod.setRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=gbk");
			NameValuePair[] data = { 
					new NameValuePair("__VIEWSTATE", submit_VIEWSTATE),
					new NameValuePair("__EVENTVALIDATION", submit_EVENTVALIDATION),
					new NameValuePair("hidEduSiteId", "2F7FF2A1-C46D-446D-9793-140BBA157DFB"),
					new NameValuePair("hidTrainId", "F66AB04A-A986-4D60-9016-D0D0C855322E"),
					new NameValuePair("hidCoachId", "432F26C5-57E0-4A93-8F17-DAADC0654638"),
					new NameValuePair("hidScheduleId", hidScheduleId),
					new NameValuePair("hidFeesType", "2"),
					new NameValuePair("hidPrice", "0.00"),
					new NameValuePair("btnSave", "提交"),
					//hidden字段
					new NameValuePair("hidEduSiteName","西乡"),
					new NameValuePair("hidTrainName", "科目二训练"),
					new NameValuePair("hidCoachName", "杨毅"),
					new NameValuePair("hidTrainTime",hidTrainTime),
					new NameValuePair("hidOrderMinMinute","60")};
			postMethod.setRequestBody(data);
			isSuccess = httpClient.executeMethod(postMethod);
			
			System.out.println("response status code:" + isSuccess);
			System.out.println(postMethod.getResponseBodyAsString());
		} catch (Exception e) {
			log.error("requestWantCar error....", e);
			e.printStackTrace();
		}
		//除非没有号源,否则一直请求
		return true;
	}
	
	
	
	/** 
	* @ClassName: Reminder 
	* @author bushuai
	* @Description: 子调度任务
	* @date 2016年8月25日 上午9:59:36 
	*/
	public class Reminder {
		Timer timer;
		public Reminder(int sec) {
			timer = new Timer(true);
			timer.schedule(new TimerTask(){
				  @Override
				  public void run() {
					  try {
						    log.info("timeDispatch4Jxd jxd 3S function in....");
						    //没有号源结束子调度
						    requestWantCar();
						  	/*if(!requestWantCar()){
						  		timer.cancel();
								return;
						  	}*/
					  	} catch (Exception e) {
					  		log.debug("10S Task Scheduler(10S scheduler dont get dt)exception", e);
						}
				  }
			 },0,sec * 1000);
		}
	}
	   
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		log.info("start jxdTask Capture。。。");
		signIn();
		new Reminder(3);
	}
	
	@Test
	public void test(){
		signIn();
		/*signIn();
		new Reminder(3);*/
	}
	
	public static void main(String[] args) {
		/*System.setProperty("http.proxyHost", "localhost"); 
		System.setProperty("http.proxyPort", "8888"); */
		final RqJsdTask jsd = new RqJsdTask();
		jsd.signIn();
		
		final Timer timer = new Timer();
			timer.schedule(new TimerTask(){
				  @Override
				  public void run() {
						    //没有号源结束子调度
						  	if(!jsd.requestWantCar()){
						  		timer.cancel();
								return;
						  	}
				  }
			 },0,3 * 1000);
	}
	
	
	@Test
	public void initCache(){
		menthod_request("http://192.168.0.21:1115/api/PreTreatment/PullDoctorSchedulingDataToCache",false);
		
	}
}
