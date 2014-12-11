package com.example.openfirechat.comm;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ChatService extends Service {

	private String username;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.i("luopeng", "ChatService onCreate");
		new OpenFireThread().start();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		if(intent == null)
			return super.onStartCommand(intent, flags, startId);
		return super.onStartCommand(intent, flags, startId);
	}
	
	class OpenFireThread extends Thread{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.i("luopeng", "Connect OpenFire start");
			ConnectionManager cm = ConnectionManager.getInstance(getApplicationContext());
			cm.connect();
			Log.i("luopeng", "Connect OpenFire end");
//			Log.i("luopeng", "thread register:"+username);
//			String code = cm.regist(username, "123456", username);
//			if("1".equals(code) || "2".equals(code)){
//				Log.i("luopeng", "login");
//				boolean result = cm.login(username, "123456");
////				cm.setPresence(5);
//				if(result)
//					cm.getGroup();
//			}
		}
	}

}
