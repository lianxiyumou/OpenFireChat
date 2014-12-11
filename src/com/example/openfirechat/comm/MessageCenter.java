package com.example.openfirechat.comm;


import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;

import com.example.openfirechat.constants.ConstantsAction;
import com.example.openfirechat.constants.ConstantsApp;
import com.example.openfirechat.constants.ConstantsMsgType;
import com.example.openfirechat.db.ChatMsgDao;
import com.example.openfirechat.model.ChatMsg;
import com.example.openfirechat.parse.ChatMsgParse;
import com.example.openfirechat.util.XMPPUtil;

import android.content.Intent;
import android.os.Bundle;

/**
 * 消息转发中心
 * 对消息进行解析，存储，通知
 * @author Administrator
 *
 */
public class MessageCenter {

	private static final String TAG = "MessageCenter";
	
	public static void messagecCategory(Message message){
		ChatMsg chatMsg = ChatMsgParse.parseXMPPMsg(message);
		if(chatMsg == null){
			return;
		}
		if(chatMsg.getType() == ConstantsMsgType.MSG_CHAT_TXT){
			updateChatMsg(chatMsg,"txt");
		}else if(chatMsg.getType() == ConstantsMsgType.MSG_CHAT_IMG){
			updateChatMsg(chatMsg,"image");
		}else if(chatMsg.getType() == ConstantsMsgType.MSG_CHAT_VOICE){
			updateChatMsg(chatMsg,"voice");
		}
	}
	
	
	
	public static void messagecCategory( Map<String,ArrayList<Message>> offlineMsgs){
		
	}
	
	public static void messagecCategory(ChatMsg chatMsg,double progress){
		Bundle bundle = new Bundle();
		if(chatMsg.getType() == ConstantsMsgType.MSG_CHAT_IMG){
			bundle.putString("type", "image");
		}else if(chatMsg.getType() == ConstantsMsgType.MSG_CHAT_VOICE){
			bundle.putString("type", "voice");
		}else{
			bundle.putString("type", "other");
		}
		chatMsg.setUname(ConstantsApp.USERNAME);
		chatMsg.setCreateTS(Calendar.getInstance().getTimeInMillis());
		Logger.d("luopeng", "messagecCategory path:"+chatMsg.getUrl());
		bundle.putSerializable("chat", chatMsg);
		long result = ChatMsgDao.getInstance().save(chatMsg);
		if(result != -1)
			sentNotification("chat",bundle);
	}	
	
	private static void updateChatMsg(ChatMsg chatMsg,String type){
		long result = ChatMsgDao.getInstance().save(chatMsg);
		if(result != -1){
			Logger.d("luopeng", "updateChatMsg save chatMsg");
			Bundle bundle = new Bundle();
			bundle.putString("type", type);
			bundle.putString("content", chatMsg.getContent());
			bundle.putSerializable("chat", chatMsg);
			sentNotification(ConstantsAction.ACTION_CHAT_MSG,bundle);
		}
	}
	
	

	public static void sentNotification(String notifyType, Bundle bundle) {
		Intent intentPush = new Intent();
		intentPush.setAction(notifyType);
		if (bundle != null) {
			intentPush.putExtras(bundle);
		}
		ApplicationData.getInstance().sendBroadcast(intentPush);
	}	
	
	public static void messagecCategory(Presence presence){
		Type type = presence.getType();
		if(type == Type.subscribe){
			Logger.d(TAG, "recevier a request to be friend");
			receiveFriendRequest(presence);
		}else if(type == Type.subscribed){
//			receiveAgreement(presence);
			Logger.d(TAG, "your friend agree to add you");
		}else if(type == Type.unsubscribe){
			Logger.d(TAG, "you are removed by your friend");
		}else if(type == Type.unsubscribed){
			Logger.d(TAG, "your friend refuse to add you");
		}else if(type == Type.error){
			Logger.e(TAG, "The presence packet contains an error message");
		}
	}
	
	private static void receiveFriendRequest(Presence presence){
		Logger.d(TAG, "receiveFriendRequest status:"+presence.getStatus());
		if("agree".equals(presence.getStatus())){//同意并关注
			receiveAgreement(presence);
		}else{//单向关注与
			ChatMsg chatMsg = new ChatMsg();
			chatMsg.setCid(presence.getPacketID());
//			chatMsg.setContent("来自"+presence.getFrom()+"的好友请求");
			chatMsg.setUname(XMPPUtil.getUserNameFromJID(presence.getFrom()));
			chatMsg.setTname(ConstantsApp.USERNAME);
			chatMsg.setCreateTS(System.currentTimeMillis());
			chatMsg.setType(ConstantsMsgType.MSG_CHAT_REQUEST);
			long result = ChatMsgDao.getInstance().save(chatMsg);
			if(result != -1){
				Bundle bundle = new Bundle();
				bundle.putSerializable("chat", chatMsg);
				sentNotification(ConstantsAction.ACTION_ADD_FRIEND,bundle);
			}
		}
	}	
	
	private static void receiveAgreement(Presence presence){
		Logger.d(TAG, "receiveAgreement tuname:"+presence.getFrom());
		//update
		String tuname = XMPPUtil.getUserNameFromJID(presence.getFrom());
		ChatMsgDao.getInstance().updateFriendRequestState(1, tuname);
		//sentNotification
		Bundle bundle = new Bundle();
		bundle.putString("friendName", tuname);
		sentNotification(ConstantsAction.ACTION_ADD_FRIEND_AGREE, new Bundle());
	}
	
}
