package com.example.openfirechat.view;

import java.util.ArrayList;
import java.util.List;

import com.example.openfirechat.R;
import com.example.openfirechat.util.UtilDialog;
import com.example.openfirechat.view.adapter.FriendRequestAdapter.ViewHolder;
import com.example.openfirechat.comm.ConnectionManager;
import com.example.openfirechat.comm.Logger;
import com.example.openfirechat.constants.ConstantsAction;
import com.example.openfirechat.constants.ConstantsApp;
import com.example.openfirechat.db.ChatMsgDao;
import com.example.openfirechat.model.ChatMsg;
import com.example.openfirechat.view.RefreshListView.RefreshListener;
import com.example.openfirechat.view.adapter.FriendRequestAdapter;
import com.example.openfirechat.view.dialog.DialogItem;

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
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Toast;

public class FriendRequestActivity extends Activity implements RefreshListener,OnItemClickListener,OnItemLongClickListener {

	private static final String TAG = "luopeng";
	
	private RefreshListView listView ;
	private FriendRequestAdapter friendRequestAdapter;
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
			if(ConstantsAction.ACTION_ADD_FRIEND.equals(action) ||
					ConstantsAction.ACTION_ADD_FRIEND_AGREE.equals(action)){
				Bundle bundle = intent.getExtras();
				Object content = bundle.get("chat") ;
				Logger.d(TAG, "TabIM receiver");
				if(content != null && content instanceof ChatMsg){
					ChatMsg chatMsg = (ChatMsg)content;
					Toast.makeText(getApplicationContext(), chatMsg.getContent(), Toast.LENGTH_LONG).show();
				}
				getFriendRequests();
			}
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Logger.d(TAG, "onCreate");
		setContentView(R.layout.friend_request);
		initView();
		setListeners();
		registerReceiver();
	}
	
	private void registerReceiver(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConstantsAction.ACTION_ADD_FRIEND);
		filter.addAction(ConstantsAction.ACTION_ADD_FRIEND_AGREE);
		registerReceiver(receiver, filter);		
	}
	
	private void initView(){
		listView = (RefreshListView) findViewById(R.id.list);
		friendRequestAdapter = new FriendRequestAdapter(this,msglist);
		listView.setAdapter(friendRequestAdapter);
		listView.removeFootView();
	}
	
	private void setListeners(){
		listView.setOnRefreshListener(this);
		listView.setOnItemClickListener(this);
		listView.setOnItemLongClickListener(this);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Logger.d(TAG, "onResume");
		getFriendRequests();
	}
	
	private void getFriendRequests(){
		new Thread(){
			public void run() {
				Logger.d(TAG, "getFriendRequests");
				queryFriendRequests();
			};
		}.start();
	}
	
	private void queryFriendRequests(){
		List<ChatMsg> list = ChatMsgDao.getInstance().getAddFriendRequest(10);
		Message msg = handler.obtainMessage();
		msg.obj = list;
		msg.what = 1;
		handler.sendMessage(msg);		
	}
	
	private void updateListView(List<ChatMsg> templist){
		Logger.d(TAG, "TabIM updateListView");
		msglist.clear();
		msglist.addAll(templist);
		friendRequestAdapter.notifyDataSetChanged();
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
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		// TODO Auto-generated method stub
		Logger.d(TAG, "request onItemLongClick");
		if(view.getTag() == null || !(view.getTag() instanceof ViewHolder)){
			return false;
		}
		ViewHolder vh = (ViewHolder) view.getTag();
		popuWindow(vh.state,vh.to,vh.from);
		
		return false;
	}	
	
	private void popuWindow(int state, String tname, final String uname){
		ArrayList<DialogItem> mItems = new ArrayList<DialogItem>();
		Logger.d(TAG, "tname:"+tname+" loginname:"+ConstantsApp.USERNAME);
		if(state == 0 && ConstantsApp.USERNAME.equals(tname)){//自己收到的且未处理的请求
			mItems.add(new DialogItem(R.string.agree, R.layout.custom_bottom_dialog_normal) {
				@Override
				public void onClick() {
					new Thread(){
						@Override
						public void run() {
							// TODO Auto-generated method stub
							ConnectionManager.getInstance(getApplicationContext()).agree(uname);
							ChatMsgDao.getInstance().updateMyRequestState(1, uname);
							queryFriendRequests();
						}
					}.start();
				}
			});
			mItems.add(new DialogItem(R.string.refuse, R.layout.custom_bottom_dialog_normal) {
				@Override
				public void onClick() {
					new Thread(){
						@Override
						public void run() {
							// TODO Auto-generated method stub
							ConnectionManager.getInstance(getApplicationContext()).unsubscribe(uname);
							ChatMsgDao.getInstance().updateMyRequestState(2, uname);
							queryFriendRequests();
						}
					}.start();
				}
			});			
		}
		mItems.add(new DialogItem(R.string.delete, R.layout.custom_bottom_dialog_special) {
			
			@Override
			public void onClick() {
				
			}
		});

		// 取消
		mItems.add(new DialogItem(R.string.cancel, R.layout.custom_bottom_dialog_cancel));
		UtilDialog.createBottomDialog(this, mItems, R.style.CustomDialog);
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		
	}
	
	
}
