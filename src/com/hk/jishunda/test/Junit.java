package com.hk.jishunda.test;

import org.apache.commons.httpclient.NameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

/** 
* @ClassName: Junit 
* @author bushuai
* @Description: 抢号测试用例
* @date 2016年8月22日 下午2:20:57 
*/
public class Junit {
	//号源段落:
	//8:00-09:00  09:00-10:00 11:00-12:00 
	//14:00-15:00  15:00-16:00 16:00-17:00
	//17:00-18:00
	
	
	@Test
	public void captureHtml(){
		String htmlContext = "<div class='divInvalid' style='font-size:14px;line-height:30px;' onclick=\"SelectTime('1；2','2780537；2780538')\" id='divTime1；2'>"
				+"<span id='span1；2'>09:00-10:00</span><input type='checkbox' "
				+ "id='chk1；2' value='2780537；2780538' name='chkTime' style='display:none' /></div>";
		
		Document doc = Jsoup.parse(htmlContext);
		
		Element schedule09 = doc.select("span:contains(09:00)").first();
		Element schedule16 = doc.select("span:contains(16:00)").first();
		Element schedule11 = doc.select("span:contains(11:00)").first();
		
		//优先选择
		String hidScheduleId = schedule09 != null ? schedule09.nextElementSibling().val() : 
			(schedule16 != null ? schedule16.nextElementSibling().val() : 
				(schedule11 != null? schedule11.nextElementSibling().val() : ""));
		
		System.out.println(hidScheduleId);
		
		
		htmlContext = "<span id='lblEduSiteName'>西乡</span>";
		Document docm = Jsoup.parse(htmlContext);
		System.out.println(docm.getElementById("lblEduSiteName").html());
		
	}
	
	
	
	@Test
	public void datapair(){
		NameValuePair[] data = { 
				/*new NameValuePair("__VIEWSTATE", submit_VIEWSTATE),
				new NameValuePair("__EVENTVALIDATION", submit_EVENTVALIDATION),*/
				new NameValuePair("hidEduSiteId", "2F7FF2A1-C46D-446D-9793-140BBA157DFB"),
				new NameValuePair("hidTrainId", "F66AB04A-A986-4D60-9016-D0D0C855322E"),
				new NameValuePair("hidCoachId", "432F26C5-57E0-4A93-8F17-DAADC0654638"),
				new NameValuePair("hidScheduleId", "2514331；2514332"),
				new NameValuePair("hidFeesType", "2"),
				new NameValuePair("hidPrice", "0.00"),
				new NameValuePair("btnSave", "提交"),
				//hidden字段
				new NameValuePair("hidEduSiteName","西乡"),
				new NameValuePair("hidTrainName", "科目二训练"),
				new NameValuePair("hidCoachName", "杨毅"),
				new NameValuePair("hidTrainTime","2016-09-01 11:00-12:00"),
				new NameValuePair("hidOrderMinMinute","60")};
				System.out.println(data.toString());
	}
}
