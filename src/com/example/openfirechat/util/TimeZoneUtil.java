package com.example.openfirechat.util;

import java.util.Date;
import java.util.TimeZone;

/**
 * 
 * @Description: 不同时区对应的时间处理工具类
 * @author yehj
 * @date 2014-8-26    下午4:39:28 
 * @version 1.0.0
 */
public class TimeZoneUtil {

	
	public static boolean isInEasternEightZones() {
		boolean defaultVaule = true;
		if (TimeZone.getDefault() == TimeZone.getTimeZone("GMT+08"))
			defaultVaule = true;
		else
			defaultVaule = false;
		return defaultVaule;
	}


	public static Date transformTime(Date date, TimeZone oldZone,
			TimeZone newZone) {
		Date finalDate = null;
		if (date != null) {
			int timeOffset = oldZone.getOffset(date.getTime())
					- newZone.getOffset(date.getTime());
			finalDate = new Date(date.getTime() - timeOffset);
		}
		return finalDate;

	}
}
