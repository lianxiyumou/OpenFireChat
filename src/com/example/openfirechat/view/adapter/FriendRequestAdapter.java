package com.example.openfirechat.view.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jivesoftware.smack.RosterEntry;

import com.example.openfirechat.R;
import com.example.openfirechat.constants.ConstantsApp;
import com.example.openfirechat.constants.ConstantsMsgType;
import com.example.openfirechat.model.ChatMsg;
import com.example.openfirechat.model.ChatMsg;
import com.example.openfirechat.util.TimeUtils;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendRequestAdapter extends BaseAdapter {
	private static final String TAG = "luopeng";
	private Context context;
	private List<ChatMsg> chatlist;
	
	public FriendRequestAdapter(Context context,List<ChatMsg> chatlist){
		this.context = context;
		this.chatlist = chatlist;
	}
	
	
	
	public List<ChatMsg> getFriendlist() {
		return chatlist;
	}

	public void setFriendlist(List<ChatMsg> chatlist) {
		this.chatlist = chatlist;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if( chatlist == null)
			return 0;
		return chatlist.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if(chatlist == null)
			return null;
		Object[] objs = chatlist.toArray();
		if(objs.length <= position)
			return null;
		return objs[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder vh;
		if(convertView==null){
			vh = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.friend_request_item, null);
			vh.friend  = (TextView) convertView.findViewById(R.id.friend);
			vh.content = (TextView) convertView.findViewById(R.id.content);
			vh.time =  (TextView) convertView.findViewById(R.id.time);
			vh.stateview =  (TextView) convertView.findViewById(R.id.state);
			convertView.setTag(vh);
		}else{
			vh = (ViewHolder) convertView.getTag();
		}
		if(getItem(position) == null){
			Log.i(TAG, "getview is empty");
			return convertView;
		}
		ChatMsg chatMsg = (ChatMsg) getItem(position);
		if(chatMsg != null){
			boolean isSelf = ConstantsApp.USERNAME.equals(chatMsg.getUname());
			if(isSelf){//is yourself request
				vh.content.setText("向 "+chatMsg.getTname()+" 发送好友请求");
				vh.friend.setText(chatMsg.getTname());
			}else{//your friend request
				vh.content.setText("来自 "+chatMsg.getUname()+" 好友请求");
				vh.friend.setText(chatMsg.getUname());
			}
			setState(vh.stateview,chatMsg.getState(),isSelf);
			vh.time.setText(TimeUtils.friendly_time(chatMsg.getCreateTS(), false));
			vh.state = chatMsg.getState();
			vh.to = chatMsg.getTname();
			vh.from = chatMsg.getUname();
		}
		
		return convertView;
	}
	
	private void setState(TextView state,int type,boolean isSelf){
		StringBuffer stateStr = new StringBuffer();
		if(isSelf){
			stateStr.append("对方");
		}else{
			stateStr.append("您");
		}
		if(type == 0){
			stateStr.append("未处理");
		}else if(type == 1){
			stateStr.append("已同意");
		}else if(type == 2){
			stateStr.append("已拒绝");
		}
		state.setText(stateStr);
	}
	
	
	public class ViewHolder{
		TextView friend;
		TextView content;
		TextView time;
		TextView stateview;
		public String to;
		public String from;
		public int state;
	}
	

}
