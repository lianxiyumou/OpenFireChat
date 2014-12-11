package com.example.openfirechat.util;

import java.util.Comparator;

import com.example.openfirechat.model.ChatMsg;

public class ComparatorChatMsg<ChatMsg> implements Comparator<ChatMsg> {
	
	private boolean desc = false;
	
	public ComparatorChatMsg(boolean desc){
		this.desc = desc;
	}
	
	public ComparatorChatMsg(){
		
	}
	
	@Override
	public int compare(ChatMsg lhs, ChatMsg rhs) {
		long t1 = ((com.example.openfirechat.model.ChatMsg) lhs).getCreateTS();
		long t2 = ((com.example.openfirechat.model.ChatMsg) rhs).getCreateTS();
//		if(t1 > t2){
//			return 1;
//		}else if(t1 < t2){
//			return -1;
//		}
		if(desc){
			return descCompared(t1,t2);
		}else{
			return ascCompared(t1,t2);
		}
	}
	
	private int descCompared(long t1, long t2){
		if(t1 < t2){
			return 1;
		}else if(t1 > t2){
			return -1;
		}
		return 0;
	}
	
	private int ascCompared(long t1, long t2){
		if(t1 > t2){
			return 1;
		}else if(t1 < t2){
			return -1;
		}
		return 0;
	}
	

}
