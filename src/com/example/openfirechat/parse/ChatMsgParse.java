package com.example.openfirechat.parse;

import java.util.Calendar;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Message.Type;

import android.util.Log;

import com.example.openfirechat.comm.Logger;
import com.example.openfirechat.constants.ConstantsMsgType;
import com.example.openfirechat.model.ChatMsg;
import com.example.openfirechat.util.XMPPUtil;

public class ChatMsgParse {

	private static final String TAG = "luopeng";
	
	
	private static int getChatMsgType(Type type,String sub){
		Logger.d("luopeng", "type:"+type+" sub:"+sub);
		if(type == Message.Type.chat){
			if("text".equals(sub))
				return ConstantsMsgType.MSG_CHAT_TXT;
			else if("img".equals(sub)){
				return ConstantsMsgType.MSG_CHAT_IMG;
			}else if("voice".equals(sub)){
				return ConstantsMsgType.MSG_CHAT_VOICE;
			}
		}
		return ConstantsMsgType.MSG_CHAT_UNKOWN;
	}
	
	public static ChatMsg parseXMPPMsg(Message message){
		Logger.d(TAG, "parseXMPPMsg:  "+message);
		ChatMsg chatMsg = new ChatMsg();
		try{
			chatMsg.setType(getChatMsgType(message.getType(),message.getSubject("ctype")));//
			chatMsg.setUname(XMPPUtil.getUserNameFromJID(message.getFrom()));
			chatMsg.setTname(XMPPUtil.getUserNameFromJID(message.getTo()));
			if(message.getSubject("timestamp") != null)
				chatMsg.setCreateTS(Long.valueOf(message.getSubject("timestamp")));
			else
				chatMsg.setCreateTS(Long.valueOf(Calendar.getInstance().getTimeInMillis()));
			if(chatMsg.getType() == ConstantsMsgType.MSG_CHAT_TXT){
				chatMsg.setContent(message.getBody());
			}else if(chatMsg.getType() == ConstantsMsgType.MSG_CHAT_IMG){
				chatMsg.setUrl(message.getBody());
			}else if(chatMsg.getType() == ConstantsMsgType.MSG_CHAT_VOICE){
				chatMsg.setUrl(message.getBody());
				chatMsg.setVoiceTS(Integer.valueOf(message.getSubject("voiceTS")));
			}
			chatMsg.setCid(message.getPacketID());
		}catch(Exception e){
			Logger.e(TAG, "parseXMPPMsg error");
			e.printStackTrace();
			return null;
		}
		return chatMsg;
	}	
	
	
	public static Message getXMPPMsg(ChatMsg chatMsg){
		Message message =  new Message();
		message.setFrom(chatMsg.getUname());
		message.setTo(chatMsg.getTname());
		message.setBody(chatMsg.getContent());
		message.setType(Message.Type.chat);
		message.addSubject("ctype", "text");
		message.addSubject("timestamp", String.valueOf(chatMsg.getCreateTS()));
		return message;
	}
	
	
}
