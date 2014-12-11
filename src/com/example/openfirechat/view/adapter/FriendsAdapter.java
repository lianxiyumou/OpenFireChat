package com.example.openfirechat.view.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jivesoftware.smack.RosterEntry;

import com.example.openfirechat.R;
import com.example.openfirechat.R.id;
import com.example.openfirechat.R.layout;
import com.example.openfirechat.model.User;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendsAdapter extends BaseAdapter {
	private static final String TAG = "luopeng";
	private Context context;
	
	public FriendsAdapter(Context context){
		this.context = context;
	}
	
	
	private List<User> friendlist;
	
	public List<User> getFriendlist() {
		return friendlist;
	}

	public void setFriendlist(List<User> friendlist) {
		this.friendlist = friendlist;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if( friendlist == null)
			return 0;
		return friendlist.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if(friendlist == null)
			return null;
		Object[] objs = friendlist.toArray();
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
			convertView = LayoutInflater.from(context).inflate(R.layout.friend_item, null);
			vh.friend  = (TextView) convertView.findViewById(R.id.friend);
			convertView.setTag(vh);
		}else{
			vh = (ViewHolder) convertView.getTag();
		}
		if(getItem(position) == null){
			Log.i(TAG, "getview is empty");
			return convertView;
		}
		User user = (User) getItem(position);
		if(user != null){
			vh.friend.setText(user.getUserName());
			vh.to = user.getUserName();
		}
		
		return convertView;
	}
	
	
	public class ViewHolder{
		TextView friend;
		public String to;
	}
	

}
