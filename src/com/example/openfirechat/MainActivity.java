package com.example.openfirechat;

import io.rong.imkit.RongIM;
import io.rong.imkit.model.RichContentMessage;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.SendMessageCallback.ErrorCode;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SmackAndroid;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.ConnectionException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.util.dns.HostAddress;

import com.example.openfirechat.FriendsAdapter.ViewHolder;
import com.example.openfirechat.comm.ChatService;
import com.example.openfirechat.comm.ConnectionManager;
import com.example.openfirechat.model.User;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MainActivity extends Activity implements OnClickListener,OnItemClickListener {

	private String account;
	private ListView list;
	private FriendsAdapter friendsAdapter;
//	private Collection<RosterEntry> friends = new HashSet<RosterEntry>();
	private List<User> friends = new ArrayList<User>();
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			Log.i("luopeng", "update listview");
			friendsAdapter.notifyDataSetChanged();
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		account =  getIntent().getStringExtra("account");
		list = (ListView) findViewById(R.id.list);
		setListener();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initFriendList();
	}
	
	private void initFriendList(){
		friendsAdapter = new FriendsAdapter(this);
		friendsAdapter.setFriendlist(friends);
		list.setAdapter(friendsAdapter);
		new Thread(){
			public void run() {
				if(friends ==  null){
					return; 
				}
				friends.clear();
//				friends.addAll(ConnectionManager.getInstance(getApplicationContext()).getAllFriend());
//				for(RosterEntry friend : friends){
//					Log.i("luopeng", "friend use:"+friend.getUser()+" name:"+friend.getName()+" string:"+friend.getUser());
//				}		
				List<User> list = ConnectionManager.getInstance(getApplicationContext()).getFriends();
				if(list != null && !list.isEmpty()){
					friends.addAll(list);
				}
				handler.sendEmptyMessage(0);
				
			};
		}.start();
	}
	
	
	
	public void setListener(){
		findViewById(R.id.add).setOnClickListener(this);
		list.setOnItemClickListener(this);
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
	                connection.connect();
//	            	MainActivity.this.socket = new Socket("192.168.80.109", 5222);
//	            	connectUsingConfiguration(connectionConfiguration);
	            } catch (ConnectionException e) {
	                e.printStackTrace();
	            } catch (SmackException e) {
	                e.printStackTrace();
	            } catch (IOException e) {
	                e.printStackTrace();
	            } catch (XMPPException e) {
					// TODO Auto-generated catch block
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
		Intent intent;
		switch (v.getId()) {
		case R.id.add:
			intent = new Intent(this,AddFriend.class);
			intent.putExtra("from", account);
			startActivity(intent);	
		default:
			break;
		}
	}
	

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		if(view.getTag() == null || !(view.getTag() instanceof ViewHolder)){
			return;
		}
		ViewHolder vh = (ViewHolder) view.getTag();
		Intent intent = new Intent(this,ChatRoom.class);
		intent.putExtra("from", account);
		intent.putExtra("to",vh.to);
		Log.i("luopeng", "list to chat to:"+vh.to);
		startActivity(intent);			
	}
}
