package com.example.openfirechat;


import org.apache.commons.lang.StringUtils;

import com.example.openfirechat.comm.ConnectionManager;
import com.example.openfirechat.comm.Logger;
import com.example.openfirechat.http.HttpParamUtil;
import com.example.openfirechat.http.RequestParams;
import com.example.openfirechat.http.my.MyHttpClient;
import com.example.openfirechat.http.my.MyHttpResponseHandler;
import com.example.openfirechat.model.User;
import com.example.openfirechat.parse.UserParse;
import com.example.openfirechat.util.MMap;
import com.google.gson.Gson;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends Activity implements OnClickListener {
	
	private static final String TAG = "luopeng";
	
	private EditText accountEdit;
	private EditText pwdEdit;
	
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what){
			case 0:
				Toast.makeText(getApplicationContext(), "register success", Toast.LENGTH_LONG).show();
				finish();
				break;
			case -100:
				Toast.makeText(getApplicationContext(), "already register", Toast.LENGTH_LONG).show();
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		initView();
		setListener();
	}
	
	private void initView(){
		accountEdit = (EditText) findViewById(R.id.account);
		pwdEdit = (EditText)findViewById(R.id.pwd);
	}
	
	private void setListener(){
		findViewById(R.id.register).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.register:
			register();
			break;
		}
	}
	
	private void register(){
		String account = accountEdit.getEditableText().toString().trim();
		String pwd = pwdEdit.getEditableText().toString().trim();
		if(StringUtils.isEmpty(account) || StringUtils.isEmpty(pwd)){
			Toast.makeText(this, "account or pwd is not null", Toast.LENGTH_LONG).show();
			return;
		}
    	MyHttpClient httpClient = new MyHttpClient();
    	RequestParams params = HttpParamUtil.getInstance();
    	params.put("account", account);
    	params.put("pwd", pwd);
    	httpClient.post("user/register.do",params, new MyHttpResponseHandler(this,true){
    		
    		@Override
    		public void onSuccessGson(MMap content, Gson gson) {
    			int error = content.getInt("error");
    			Logger.d(TAG, "onSuccessGson error:"+error);
    			if( error == 0 || error == -100){
    				Logger.d(TAG, content.get("user").toString());
    				User user = UserParse.parseJson(content.get("user").toString());
    				registerOpenfire(user.getUserName(),user.getPassword(),user.getName());
    			}
    		}
    		
    		@Override
    		public void onFailure(Throwable error, String content) {
    			
    		}
    		
    	});    	
	}
	
	private void registerOpenfire(final String userName,final String pwd, final String name){
		new Thread(){
			public void run() {
				ConnectionManager cm = ConnectionManager.getInstance(getApplicationContext());
				cm.connect();
				Log.i("luopeng", "thread register account:"+userName+" pwd:"+pwd+" name:"+name);
				String code = cm.regist(userName, pwd, name);
				Logger.d(TAG, "registerOpenfire code:"+code);
				if("1".equals(code) ){	
					User user = new User();
//					if(!StringUtils.isEmpty(name)){
//						user.setName(name);
//						cm.updateVCard(user);
//					}					
					handler.sendEmptyMessage(0);
				}else if("2".equals(code)){
					handler.sendEmptyMessage(-100);
				}
			};
		}.start();
	}
	
	
	
}
