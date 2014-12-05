package com.example.openfirechat;

import java.io.IOException;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.SmackAndroid;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.ConnectionException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.util.dns.HostAddress;

import com.example.openfirechat.comm.ChatService;
import com.example.openfirechat.comm.ConnectionManager;
import com.example.openfirechat.comm.Logger;
import com.example.openfirechat.constants.ConstantsApp;
import com.example.openfirechat.db.ChatMsgDao;
import com.example.openfirechat.http.HttpParamUtil;
import com.example.openfirechat.http.RequestParams;
import com.example.openfirechat.http.my.MyHttpClient;
import com.example.openfirechat.http.my.MyHttpResponseHandler;
import com.example.openfirechat.model.User;
import com.example.openfirechat.parse.UserParse;
import com.example.openfirechat.util.MMap;
import com.google.gson.Gson;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity  implements OnClickListener {

	private static final String TAG = "luopeng";
	private EditText edit_name;
	private EditText edit_pwd;
	private ProgressDialog pd;
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case 0:
				if(msg.obj instanceof String){
					String username = msg.obj.toString();
					Log.i("luopeng", "MainActivity username:"+username);
					Intent intent = new Intent(getApplicationContext(),MainActivity.class);
					Log.i("luopeng", "login username:"+edit_name.getEditableText().toString());
					intent.putExtra("account", username);
					startActivity(intent);			
				}
				break;
			case -1:
				Toast.makeText(getApplicationContext(), "didn't regist", Toast.LENGTH_LONG).show();
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		edit_name = (EditText)findViewById(R.id.account);
		edit_pwd = (EditText)findViewById(R.id.pwd);
		setListener();
	}
	
	public void setListener(){
		findViewById(R.id.login).setOnClickListener(this);
		findViewById(R.id.regist).setOnClickListener(this);
	}
	
	public void connect() {
	    Thread t = new Thread(new Runnable() {

	        @Override
	        public void run() {
	            Context context = getApplicationContext();
	            SmackAndroid.init(context);
	            ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration("192.168.80.109", 5222);
	            connectionConfiguration.setDebuggerEnabled(true);
	            connectionConfiguration.setSecurityMode(SecurityMode.disabled);
	            XMPPConnection connection = new XMPPTCPConnection(connectionConfiguration);     
	            try {
//	                connection.connect();
	            	LoginActivity.this.socket = new Socket("192.168.80.109", 5222);
	            	connectUsingConfiguration(connectionConfiguration);
	            } catch (ConnectionException e) {
	                e.printStackTrace();
	            } catch (SmackException e) {
	                e.printStackTrace();
	            } catch (IOException e) {
	                e.printStackTrace();
	            } 
	        }
	    });
	    t.start();
	}	
	Socket socket;
    public void connectUsingConfiguration(ConnectionConfiguration config) throws SmackException, IOException {
        Exception exception = null;
        Iterator<HostAddress> it = config.getHostAddresses().iterator();
        List<HostAddress> failedAddresses = new LinkedList<HostAddress>();
        while (it.hasNext()) {
            exception = null;
            HostAddress hostAddress = it.next();
            String host = hostAddress.getFQDN();
            int port = hostAddress.getPort();
            try {
                if (config.getSocketFactory() == null) {
                    this.socket = new Socket(host, port);
                }
                else {
                    this.socket = config.getSocketFactory().createSocket(host, port);
                }
            } catch (Exception e) {
                exception = e;
            }
            if (exception == null) {
                // We found a host to connect to, break here
                host = hostAddress.getFQDN();
                port = hostAddress.getPort();
                break;
            }
            hostAddress.setException(exception);
            failedAddresses.add(hostAddress);
            if (!it.hasNext()) {
                // There are no more host addresses to try
                // throw an exception and report all tried
                // HostAddresses in the exception
                throw new ConnectionException(failedAddresses);
            }
        }
    }	
	
	


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.login:
			final String name = edit_name.getEditableText().toString();
			final String pwd = edit_pwd.getEditableText().toString();
			pd = new ProgressDialog(this);
			pd.setMessage("正在登陆....");
			pd.show();
			login(name,pwd);
			break;
		case R.id.regist:
			Intent intent = new Intent(this,Register.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}
	
	private void login(String account, String pwd){
	   	MyHttpClient httpClient = new MyHttpClient();
    	RequestParams params = HttpParamUtil.getInstance();
    	params.put("account", account);
    	params.put("pwd", pwd);
    	httpClient.post("user/login.do",params, new MyHttpResponseHandler(this,true){
    		
    		@Override
    		public void onSuccessGson(MMap content, Gson gson) {
    			int error = content.getInt("error");
    			if( error == 0){
    				Logger.d(TAG, content.get("user").toString());
    				User user = UserParse.parseJson(content.get("user").toString());
    				loginOpenfire(user.getUserName(),user.getPassword());
    			}else if(error == -1){
    				Logger.d(TAG, "didn't registe");
					Message msg = new Message();
					msg.what = -1;
					handler.sendMessage(msg);    	
					pd.dismiss();
    			}
    		}
    		
    		@Override
    		public void onFailure(Throwable error, String content) {
    			
    		}
    		
    	});
	}
	
	private void loginOpenfire(final String username,final String pwd){
		new Thread(){
			public void run() {
				// TODO Auto-generated method stub
				Log.i("luopeng", "OpenFire thread run");
				ConnectionManager cm = ConnectionManager.getInstance(getApplicationContext());
				cm.connect();
				Log.i("luopeng", "login account:"+username+" pwd:"+pwd);
				boolean result = cm.login(username, pwd);
				Logger.d(TAG, "login result:"+result);
				if(result){
					try{
						ConstantsApp.USERNAME = username;
					}catch(Exception e){
						e.printStackTrace();
					}
					Message msg = new Message();
					msg.obj = username;
					msg.what = 0;
					handler.sendMessage(msg);
					pd.dismiss();
				}
			};
		}.start();		
	}
	
	
	
}
