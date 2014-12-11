package com.example.openfirechat.http;

import com.example.openfirechat.http.my.MRequestParams;



public class HttpParamUtil {
	public static RequestParams getInstance() {
		RequestParams params = new RequestParams();	
        // Post need NameValuePair[] to pass key-value.	
//		params.put("appname", MyContext.APP_NAME);
//		params.put("version", String.valueOf(MyContext.mVersionCode));
//		params.put("enablemipush", String.valueOf(1));
//		params.put("token", MyContext.mToken);
//		params.put("loginfrom", MyContext.mLoginFrom);
//		params.put("username", MyContext.mUserName);
////		params.put("nickname", MyContext.mNickName);		
////		params.put("sex", String.valueOf(MyContext.mSex));
//		params.put("uid", String.valueOf(MyContext.mUID));
//		params.put("timestamp", String.valueOf(System.currentTimeMillis()));
		return params;		
	}
	
	public static RequestParams getMInstance() {
		RequestParams params = new MRequestParams();	
        // Post need NameValuePair[] to pass key-value.	
//		params.put("appname", MyContext.APP_NAME);
//		params.put("version", String.valueOf(MyContext.mVersionCode));
//		params.put("enablemipush", String.valueOf(1));
//		params.put("token", MyContext.mToken);
//		params.put("loginfrom", MyContext.mLoginFrom);
//		params.put("username", MyContext.mUserName);
////		params.put("nickname", MyContext.mNickName);		
////		params.put("sex", String.valueOf(MyContext.mSex));
//		params.put("uid", String.valueOf(MyContext.mUID));
//		params.put("timestamp", String.valueOf(System.currentTimeMillis()));
		return params;		
	}	

}
