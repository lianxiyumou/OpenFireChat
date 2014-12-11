package com.example.openfirechat.http.my;

import com.example.openfirechat.comm.Logger;
import com.example.openfirechat.constants.ConstantsApp;
import com.example.openfirechat.http.AsyncHttpClient;
import com.example.openfirechat.http.AsyncHttpResponseHandler;
import com.example.openfirechat.http.RequestParams;


public class MyHttpClient extends AsyncHttpClient {

	private static String TAG = "luopeng";
	
	@Override
	public void post(String action,RequestParams params, AsyncHttpResponseHandler responseHandler) {
		// TODO Auto-generated method stub
		Logger.d(TAG, "MyHttpClient post action:"+action);
		super.post(ConstantsApp.URL+action,params, responseHandler);
	}
	
	
}
