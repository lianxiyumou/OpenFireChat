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

public class SessionAdapter extends BaseAdapter {
	private static final String TAG = "luopeng";
	private Context context;
	private List<ChatMsg> chatlist;
	
	public SessionAdapter(Context context,List<ChatMsg> chatlist){
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
			convertView = LayoutInflater.from(context).inflate(R.layout.session_item, null);
			vh.friend  = (TextView) convertView.findViewById(R.id.friend);
			vh.content = (TextView) convertView.findViewById(R.id.content);
			vh.time =  (TextView) convertView.findViewById(R.id.time);
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
			if(ConstantsApp.USERNAME.equals(chatMsg.getUname())){
				vh.friend.setText(chatMsg.getTname());
				vh.to = chatMsg.getTname();
			}else{
				vh.friend.setText(chatMsg.getUname());
				vh.to = chatMsg.getUname();
			}
			setContent(vh,chatMsg);
			vh.type = chatMsg.getType();
			vh.time.setText(TimeUtils.friendly_time(chatMsg.getCreateTS(), false));
		}
		
		return convertView;
	}
	
	private void setContent(ViewHolder vh,ChatMsg chatMsg){
		TextView content = vh.content;
		TextView title = vh.friend;
		switch(chatMsg.getType()){
		case ConstantsMsgType.MSG_CHAT_IMG:
			content.setText("[图片]");
			break;
		case ConstantsMsgType.MSG_CHAT_TXT:
			content.setText(chatMsg.getContent());
			break;
		case ConstantsMsgType.MSG_CHAT_VOICE:
			content.setText("[语音]");
			break;
		case ConstantsMsgType.MSG_CHAT_REQUEST:
//			content.setText(chatMsg.getContent());
			boolean isSelf = ConstantsApp.USERNAME.equals(chatMsg.getUname());
			if(isSelf){//is yourself request
				content.setText("向 "+chatMsg.getTname()+" 发送好友请求");
			}else{//your friend request
				content.setText("来自 "+chatMsg.getUname()+" 好友请求");
			}			
			title.setText("好友请求");
			break;
		}
	}
	
	
	public class ViewHolder{
		TextView friend;
		TextView content;
		TextView time;
		public String to;
		public int type;
	}
	

}
