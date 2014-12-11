package com.example.openfirechat.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


/**
 * 
 * @Description: 时间工具包
 * @author yehj
 * @date 2014-8-26    下午3:32:07 
 * @version 1.0.0
 */
public class TimeUtils {
     
	
	private final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
	};
	
	private final static ThreadLocal<SimpleDateFormat> dateFormater1 = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm");
		}
	};	

	private final static ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd");
		}
	};
	
	private final static ThreadLocal<SimpleDateFormat> dateFormater3 = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("HH:mm");
		}
	};
	
	private final static ThreadLocal<SimpleDateFormat> dateFormater4 = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("HH:mm");
		}
	};	
	
	/**
	 * 将字符串转为日期类型
	 * 
	 * @param sdate
	 * @return
	 */
	public static Date toDate(String sdate) {
		try {
			return dateFormater.get().parse(sdate);
		} catch (ParseException e) {
			return null;
		}
	}
	
	public static String friendly_time(long ldate,boolean hasSecond) {
//		new Data(ldate);
		Date date = new Date(ldate);
		return friendly_time(date,hasSecond);
	}
	
	/**
	 * 以友好的方式显示时间
	 * 
	 * @param sdate
	 * @param hasSecond:是不是带秒
	 * @return
	 */
	public static String friendly_time(String sdate,boolean hasSecond) {
		Date time = null;
		if (TimeZoneUtil.isInEasternEightZones()) {
			time = toDate(sdate);
		} else {
			time = TimeZoneUtil.transformTime(toDate(sdate),
					TimeZone.getTimeZone("GMT+08"), TimeZone.getDefault());
		}
		return friendly_time(time,hasSecond);
	}
	
	public static String friendly_time(Date time,boolean hasSecond){


		if (time == null) {
			return "Unknown";
		}
		String ftime = "";
		Calendar cal = Calendar.getInstance();

		// 判断是否是同一天
		String curDate = dateFormater2.get().format(cal.getTime());
		String paramDate = dateFormater2.get().format(time);
		
		ThreadLocal<SimpleDateFormat> df = null;
		ThreadLocal<SimpleDateFormat> df2 = null;
		if(hasSecond){
			df = dateFormater3;
			df2 = dateFormater;
		}else{
			df = dateFormater4;
			df2 = dateFormater1;
		}
		
		if (curDate.equals(paramDate)) {
			ftime="今天"+df.get().format(time);
			return ftime;
		}

		long lt = time.getTime() / 86400000;
		long ct = cal.getTimeInMillis() / 86400000;
		int days = (int) (ct - lt);
		try{
			if (days == 0) {
				ftime="今天"+df.get().format(time);
			} else if (days == 1) {
				ftime = "昨天"+df.get().format(time);
			} else if (days == 2) {
				ftime = "前天"+df.get().format(time);
			} else if (days >=3) {
				ftime = df2.get().format(time);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return ftime;
	
	}
	
	/**
	 * 获得当前字符串的时间
	 * @author luopeng
	 * @return
	 */
	public static String getNow(){
		Calendar cal = Calendar.getInstance();
		return dateFormater.get().format(cal.getTime());
	}
	
	public static long getLongNow(){
		Calendar cal = Calendar.getInstance();
		return cal.getTimeInMillis();
	}	
	
	
}
