package com.example.openfirechat.view.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.openfirechat.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * 聊天界面 更多控件的适配器
 * 
 * @author Administrator
 * 
 */
public class ChatMoreFunctionAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<HashMap<String, Object>> items;

	public ChatMoreFunctionAdapter(Context aContext,ArrayList<HashMap<String, Object>> aItems) {
		this.mContext = aContext;
		this.items = aItems;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder;
		if (convertView == null) {
			holder = new Holder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.chat_more_function_item ,null);
			holder.functionIconImageView = (ImageView) convertView.findViewById(R.id.function_icon);
			holder.functinInstructionsTextView = (TextView) convertView.findViewById(R.id.function_name);
			convertView.setTag(holder);
		}else{
			holder = (Holder) convertView.getTag();
		}
		HashMap<String, Object> map = items.get(position);
		
		holder.functionIconImageView.setImageResource((Integer) map.get("iconId"));
		holder.functinInstructionsTextView .setText((CharSequence) map.get("name"));
		return convertView;
	}
	
	class Holder{
		ImageView functionIconImageView;
		TextView functinInstructionsTextView;
	}
}
