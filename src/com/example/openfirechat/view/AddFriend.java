package com.example.openfirechat.view;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException.NotConnectedException;

import com.example.openfirechat.R;
import com.example.openfirechat.R.id;
import com.example.openfirechat.R.layout;
import com.example.openfirechat.comm.ConnectionManager;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;


public class AddFriend extends Activity implements OnClickListener {

	private EditText edit;
	private String from;
	
	private Handler hander = new Handler(){
		@Override
		public void handleMessage(android.os.Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(msg.obj instanceof Boolean){
				if(Boolean.valueOf(msg.obj.toString())){
					Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_LONG).show();
					finish();
				}else{
					Toast.makeText(getApplicationContext(), "添加失败", Toast.LENGTH_LONG).show();
				}
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addfriend);
		from = getIntent().getStringExtra("from");
		edit = (EditText) findViewById(R.id.input);
		findViewById(R.id.submit).setOnClickListener(this);
	}
	
	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.submit:
			final String friendName = edit.getEditableText().toString();
			if(from != null && !"".equals(from)){
				new Thread(){
					public void run() {
						Log.i("luopeng", "add friend:" + friendName);
						boolean result = ConnectionManager.getInstance(getApplicationContext()).addFriend(from, friendName);
						Message msg = new Message();
						msg.obj = result;
						hander.sendMessage(msg);
					};
				}.start();
			}
			break;

		default:
			break;
		}
	}
	
}
