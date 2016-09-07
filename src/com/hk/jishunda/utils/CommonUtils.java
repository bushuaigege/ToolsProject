package com.hk.jishunda.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonUtils {
	
	/**
	 * 当前时间格式化
	 * @param parseFormat
	 * @return
	 */
	public static String DateFormat(String parseFormat,Date date){
		SimpleDateFormat simpleFormat = new SimpleDateFormat(parseFormat);
		return simpleFormat.format(date);
	}
}
