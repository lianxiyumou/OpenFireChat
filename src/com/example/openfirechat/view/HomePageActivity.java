package com.example.openfirechat.view;



import com.example.openfirechat.R;
import com.example.openfirechat.R.id;
import com.example.openfirechat.R.layout;
import com.example.openfirechat.comm.Logger;
import com.example.openfirechat.constants.ConstantsAction;
import com.example.openfirechat.model.ChatMsg;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.Toast;

public class HomePageActivity extends Activity {

	private static final String TAG = "luopeng";
	
	private TabHost tabHost;
	public static final int TAB_NUM = 2;
	public static final int TAB_FRIEND = 0;
	public static final String TAB_FRIEND_TAG = "tab_friend";
	public static final int TAB_IM = 1;
	public static final String TAB_IM_TAG = "tab_im";
	private RadioButton mTabBtn[] = new RadioButton[TAB_NUM];
	private LocalActivityManager mLAM;
	
	private BroadcastReceiver receiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action  = intent.getAction();
			Logger.d(TAG, "HomePageActivity onReceive");
			if(action == null ){
				return;
			}
			Bundle bundle = intent.getExtras();
			if(bundle == null){
				return;
			}
			if(ConstantsAction.ACTION_ADD_FRIEND_AGREE.equals(action)){
				Logger.d(TAG, "HomePageActivity onReceive ACTION_ADD_FRIEND_AGREE");
				String tip = bundle.get("friendName")+"同意加您为好友";
				Toast.makeText(getApplicationContext(), tip, Toast.LENGTH_LONG).show();
			}
			if(ConstantsAction.ACTION_ADD_FRIEND.equals(action) ||
				ConstantsAction.ACTION_CHAT_MSG.equals(action) ){
				Logger.d(TAG, "HomePageActivity onReceive ACTION_ADD_FRIEND ACTION_CHAT_MSG");
				Object content = bundle.get("chat") ;
				if(content != null && content instanceof ChatMsg){
					ChatMsg chatMsg = (ChatMsg)content;
					Toast.makeText(getApplicationContext(), chatMsg.getContent(), Toast.LENGTH_LONG).show();
				}				
			}
		}
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main_page_layout);
		registerReceiver();
		initTab(savedInstanceState);
		
	}
	
	private void registerReceiver(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConstantsAction.ACTION_ADD_FRIEND);
		filter.addAction(ConstantsAction.ACTION_CHAT_MSG);
		filter.addAction(ConstantsAction.ACTION_ADD_FRIEND_AGREE);
		registerReceiver(receiver, filter);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		onRecheck(-1);
		Logger.d(TAG, "HomePageActivity onResume");
		mLAM.dispatchResume();		
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mLAM.dispatchPause(this.isFinishing());
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mLAM.dispatchDestroy(this.isFinishing());
		unregisterReceiver(receiver);
		super.onDestroy();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
//		Key tab expected Bundle but value was a java.lang.Integer.  The default value <null> was returned.
//		Attempt to cast generated internal exception:
//		java.lang.ClassCastException: java.lang.Integer cannot be cast to android.os.Bundle
//			at android.os.Bundle.getBundle(Bundle.java:1142)
//			at android.app.LocalActivityManager.dispatchCreate(LocalActivityManager.java:455)
//			at com.aini25.netchat.LauncherUI.onCreate(LauncherUI.java:55)
//		outState.putInt("tab", mTabHost.getCurrentTab());		
		// See dispatchCreate()
		Bundle bundle = new Bundle();
		bundle.putInt("tab", tabHost.getCurrentTab());
		outState.putBundle("current", bundle);
		
		// Create sub-activity state.
		outState.putBundle("subbundle", mLAM.saveInstanceState());
		
		super.onSaveInstanceState(outState);
	}	
	
	private void initTab(Bundle savedInstanceState){
		tabHost = (TabHost) findViewById(R.id.tabhost);
		// Seem weird, Why? FIXME
		// Second param MUST be TRUE, see getCurrentActivity() for more.
        LocalActivityManager lam = new LocalActivityManager(this, true);  
        if (savedInstanceState != null)
        	lam.dispatchCreate(savedInstanceState.getBundle("subbundle"));
        else
        	lam.dispatchCreate(null);
        
        tabHost.setup(lam);  
		
        mLAM = lam;
        TabHost.TabSpec spec;
        
		spec = tabHost.newTabSpec(TAB_FRIEND_TAG)
				.setIndicator(TAB_FRIEND_TAG)
				.setContent(new Intent().setClass(this, TabFriendActivity.class));		
		tabHost.addTab(spec);
				
		spec = tabHost.newTabSpec(TAB_IM_TAG)
				.setIndicator(TAB_IM_TAG)
				.setContent(new Intent().setClass(this, TabIM.class));		
		tabHost.addTab(spec);
		
		
		mTabBtn[TAB_FRIEND] = (RadioButton) findViewById(R.id.tab_friend);
		mTabBtn[TAB_IM] = (RadioButton) findViewById(R.id.tab_im);
		
		mTabBtn[TAB_FRIEND].setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				switchToWall();
			}
        });
		
		onRecheck(0);
        
		mTabBtn[TAB_IM].setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				switchToIM();
			}
        });
		
	}
	
	private void onRecheck(int oldTab) {
		int checked = tabHost.getCurrentTab();		
		for (int i = 0; i < TAB_NUM; i++) {
			if (checked == i)
				mTabBtn[i].setChecked(true);
			else
				mTabBtn[i].setChecked(false);
		}		
	}	
	
	private void switchToWall(){
		if(tabHost.getCurrentTab() == TAB_FRIEND){
			return;
		}
		int curTab = tabHost.getCurrentTab();
		tabHost.setCurrentTabByTag(TAB_FRIEND_TAG);
		onRecheck(curTab);			
	}	
	
	private void switchToIM(){
		if(tabHost.getCurrentTab() == TAB_IM){
			return;
		}
		int curTab = tabHost.getCurrentTab();
		tabHost.setCurrentTabByTag(TAB_IM_TAG);
		onRecheck(curTab);			
	}	
	
	
	
}
