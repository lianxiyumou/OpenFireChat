package com.example.openfirechat.comm;

import android.util.Log;

/**
 * 
 * @Description: 日志输出规范
 * @author yehj
 * @date 2014-8-7    下午3:15:19 
 * @version 1.01
 */
public class Logger {
	
	public final static int LOG_LEVEL = 6;//�?��模式�?，上线模式为0
	public final static int ERROR = 1;
	public final static int WARN = 2;
	public final static int INFO = 3;
	public final static int DEBUG = 4;
	public final static int VERBOS = 5;

	public static void e(String tag, String msg) {
//		if (LOG_LEVEL > ERROR)
			Log.e(tag, msg);
	}

	public static void w(String tag, String msg) {
		if (LOG_LEVEL > WARN)
			Log.w(tag, msg);
	}

	public static void i(String tag, String msg) {
		if (LOG_LEVEL > INFO)
			Log.i(tag, msg);
	}

	public static void d(String tag, String msg) {
//		if (LOG_LEVEL > DEBUG)
			Log.d(tag, msg);
	}

	public static void v(String tag, String msg) {
		if (LOG_LEVEL > VERBOS)
			Log.v(tag, msg);
	}


}



