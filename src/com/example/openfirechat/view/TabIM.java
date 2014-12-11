package com.example.openfirechat.view;

import java.util.ArrayList;
import java.util.List;

import com.example.openfirechat.R;
import com.example.openfirechat.view.adapter.SessionAdapter.ViewHolder;
import com.example.openfirechat.comm.Logger;
import com.example.openfirechat.constants.ConstantsAction;
import com.example.openfirechat.constants.ConstantsApp;
import com.example.openfirechat.constants.ConstantsMsgType;
import com.example.openfirechat.db.ChatMsgDao;
import com.example.openfirechat.model.ChatMsg;
import com.example.openfirechat.view.RefreshListView.RefreshListener;
import com.example.openfirechat.view.adapter.SessionAdapter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class TabIM extends Activity implements RefreshListener,OnItemClickListener {

	private static final String TAG = "luopeng";
	
	private RefreshListView listView ;
	private SessionAdapter sessionAdapter;
	private List<ChatMsg> msglist = new ArrayList<ChatMsg>();
	
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Logger.d(TAG, "TabIM handleMessage");
			switch(msg.what){
			case 1:
				if(msg.obj != null && msg.obj instanceof List){
					updateListView((List<ChatMsg>)msg.obj);
				}
				break;
			}
		}
	};
	
	
	private BroadcastReceiver receiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			Logger.d(TAG, "onReceive action:"+action);
			if(ConstantsAction.ACTION_ADD_FRIEND.equals(action) || 
					ConstantsAction.ACTION_CHAT_MSG.equals(action)||
					ConstantsAction.ACTION_ADD_FRIEND_AGREE.equals(action)){
				Bundle bundle = intent.getExtras();
				Object content = bundle.get("chat") ;
				Logger.d(TAG, "TabIM receiver");
				if(content != null && content instanceof ChatMsg){
					ChatMsg chatMsg = (ChatMsg)content;
					Toast.makeText(getApplicationContext(), chatMsg.getContent(), Toast.LENGTH_LONG).show();
				}
				getLastChatMsg();
			}
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Logger.d(TAG, "onCreate");
		setContentView(R.layout.tab_im);
		initView();
		setListeners();
		registerReceiver();
	}
	
	private void registerReceiver(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConstantsAction.ACTION_ADD_FRIEND);
		filter.addAction(ConstantsAction.ACTION_CHAT_MSG);
		filter.addAction(ConstantsAction.ACTION_ADD_FRIEND_AGREE);
		registerReceiver(receiver, filter);		
	}
	
	private void initView(){
		listView = (RefreshListView) findViewById(R.id.list);
		sessionAdapter = new SessionAdapter(this,msglist);
		listView.setAdapter(sessionAdapter);
		listView.removeFootView();
	}
	
	private void setListeners(){
		listView.setOnRefreshListener(this);
		listView.setOnItemClickListener(this);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Logger.d(TAG, "onResume");
		getLastChatMsg();
	}
	
	private void getLastChatMsg(){
		new Thread(){
			public void run() {
				Logger.d(TAG, "getLastChatMsg");
				List<ChatMsg> list = ChatMsgDao.getInstance().getLastChatMsg();
				Message msg = handler.obtainMessage();
				msg.obj = list;
				msg.what = 1;
				handler.sendMessage(msg);
			};
		}.start();
	}
	
	private void updateListView(List<ChatMsg> templist){
		Logger.d(TAG, "TabIM updateListView");
		msglist.clear();
		msglist.addAll(templist);
		sessionAdapter.notifyDataSetChanged();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	@Override
	public Object refreshing() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void refreshed(Object obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void more() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void autoMore() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		if(view.getTag() == null || !(view.getTag() instanceof ViewHolder)){
			return;
		}
		ViewHolder vh = (ViewHolder) view.getTag();
		Intent intent = null;
		if(vh.type == ConstantsMsgType.MSG_CHAT_REQUEST){
			Toast.makeText(getApplicationContext(), "friend request", Toast.LENGTH_LONG).show();
			intent = new Intent(this,FriendRequestActivity.class);
			startActivity(intent);
		}else{
			intent = new Intent(this,ChatRoom.class);
			intent.putExtra("from", ConstantsApp.USERNAME);
			intent.putExtra("to",vh.to);
			Log.i("luopeng", "list to chat to:"+vh.to);
			startActivity(intent);			
		}
	}	
	
}
