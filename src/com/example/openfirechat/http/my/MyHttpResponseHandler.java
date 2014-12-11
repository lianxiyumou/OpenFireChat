package com.example.openfirechat.http.my;

import java.util.Map;

import org.json.JSONObject;

import com.example.openfirechat.comm.Logger;
import com.example.openfirechat.http.AsyncHttpResponseHandler;
import com.example.openfirechat.util.MMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.content.Context;


public class MyHttpResponseHandler extends AsyncHttpResponseHandler {

	private static final String TAG = "luopeng";
	
    public MyHttpResponseHandler(Context context, boolean json) {
    	this();
    	super.mContext = context;
    	super.mJsonResponse = json;
    }
	
    public MyHttpResponseHandler() {
		// TODO Auto-generated constructor stub
	}

	protected void handleSuccessMessage(String responseBody) {
    	if (responseBody == null || responseBody.length() < 2)
    		onFailureJson();
    	if (!mJsonResponse)
    		onSuccess(responseBody);
    	else {
    		try {
    			Logger.d(TAG, "responseBody:"+responseBody);
    			Gson gson  = new Gson();
    			MMap rsp = gson.fromJson(responseBody, new TypeToken<MMap>(){}.getType());
    			onSuccessGson(rsp,gson);
    		} catch (Exception e) {
				Logger.e(TAG, "Return invalid json message: " + responseBody);
				e.printStackTrace();
				onFailureJson();
			}
    	}
    }
    
	protected void onSuccessGson(MMap content,Gson gson) {}
	
}
