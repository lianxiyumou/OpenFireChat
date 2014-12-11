package com.example.openfirechat.db;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jivesoftware.smack.packet.Message;

import com.example.openfirechat.comm.Logger;
import com.example.openfirechat.constants.ConstantsApp;
import com.example.openfirechat.constants.ConstantsMsgType;
import com.example.openfirechat.model.ChatMsg;
import com.example.openfirechat.parse.ChatMsgParse;
import com.example.openfirechat.util.ComparatorChatMsg;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ChatMsgDao {

	private static final String TAG = "luopeng";
	
	private static ChatMsgDao chatMsgDao;
	public  static final String TABLE_CHAT = "chat";
	public static final String CHAT_SQL = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_CHAT + "("
			+ "id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ "cid char(8) NOT NULL, "
			+ "type INTEGER NOT NULL DEFAULT 0, "//1:txt;2:img;3:voice;4:add request
			+ "uname INTEGER NOT NULL, "//发送方用户ID
			+ "tname INTEGER NOT NULL, "//接收方用户ID
			+ "content TEXT NOT NULL, "
			+ "url TEXT, "
			+ "state INTEGER NOT NULL DEFAULT 0, "//for add request 0:未处理; 1:已同意; 2:已拒绝
			+ "isread INTEGER NOT NULL DEFAULT 0, "//0:not read; 1: readed
			+ "createTS INTEGER NOT NULL,"
			+ "voiceTS INTEGER DEFAULT 0"
			+ ")";
	
	public static final String CHAT_V1_TO_V2_VOICETS = 
			"ALTER table "+TABLE_CHAT+" ADD column voiceTS INTEGER DEFAULT 0";
	
	public static final String CHAT_V2_TO_V3_STATE = 
			"ALTER table "+TABLE_CHAT+" ADD column state INTEGER NOT NULL DEFAULT 0";
	
	public static final String CHAT_V2_TO_V3_READ = 
			"ALTER table "+TABLE_CHAT+" ADD column isread INTEGER NOT NULL DEFAULT 0";	
	
	public static ChatMsgDao getInstance(){
		Log.i("luopeng", "chatMsgDao getInstance");
		if(chatMsgDao == null){
			synchronized (ChatMsgDao.class) {
				if(chatMsgDao == null){
					chatMsgDao = new ChatMsgDao();
				}
			}
		}
		return chatMsgDao;
	}
	
	private ChatMsgDao(){
		Log.i(TAG, "create ChatMsgDao");
	}
	
	public long save(ChatMsg msg){
		if(msg == null){
			return -1;
		}
		SQLiteDatabase db = SQLiteHelper.getWriter();
		long row = -1;
		if(db == null){
			Log.e(TAG, "get db fail");
			return row;
		}
		if(check(msg.getCid())){
			Logger.i(TAG, "already exist");
			return row;
		}
		
		ContentValues values = new ContentValues();
		values.put("cid", msg.getCid());
		values.put("content", msg.getContent() == null ? "" : msg.getContent());
		values.put("url", msg.getUrl());
		values.put("createTS", msg.getCreateTS());
		values.put("tname", msg.getTname());
		values.put("type", msg.getType());
		values.put("uname", msg.getUname());
		values.put("voiceTS", msg.getVoiceTS());
		
		row =db.insert(TABLE_CHAT, null, values);
		if(row == -1){
			Log.e("luopeng", "insert chat msg fail");
		}else{
			Logger.i("luopeng", "insert chat msg success");
		}
		return row;
	}
	
	/**
	 * 
	 * @param tname : 消息接受者ID
	 * @param uname： 消息发送者ID
	 * @return
	 */
	public List<ChatMsg> findAllChatMsg(String uname,String tname,int count,long lastTime){
		Logger.d(TAG, "findAllChatMsg tname:"+tname+" uname:"+uname+" count:"+count);
		String sql = "select * from "+TABLE_CHAT +
					 " where ((tname = ? and uname = ?) or (uname = ? and tname = ?)) "+
					 " and createTS < ? and type in(?,?,?)"+
					 " order by createTS desc limit 0,"+count;
		Logger.d(TAG, "findAllChatMsg sql:"+sql);
		String[] selectionArgs = {tname,uname,tname,uname, 
								  String.valueOf(lastTime),
								  String.valueOf(ConstantsMsgType.MSG_CHAT_TXT),
								  String.valueOf(ConstantsMsgType.MSG_CHAT_IMG),
								  String.valueOf(ConstantsMsgType.MSG_CHAT_VOICE)};
		Cursor cursor = null;
		List<ChatMsg> chatList = new ArrayList<ChatMsg>();
		try{
			cursor = SQLiteHelper.getReader().rawQuery(sql, selectionArgs);
			cursor.moveToFirst();
			
			while(cursor.getPosition() != cursor.getCount()){
				ChatMsg chatMsg = new ChatMsg();
				chatMsg.setCid(cursor.getString(cursor.getColumnIndex("cid")));
				chatMsg.setType(cursor.getInt(cursor.getColumnIndex("type")));
				chatMsg.setUname(cursor.getString(cursor.getColumnIndex("uname")));
				chatMsg.setTname(cursor.getString(cursor.getColumnIndex("tname")));
				chatMsg.setContent(cursor.getString(cursor.getColumnIndex("content")));
				chatMsg.setUrl(cursor.getString(cursor.getColumnIndex("url")));
				chatMsg.setCreateTS(cursor.getLong(cursor.getColumnIndex("createTS")));
				chatMsg.setVoiceTS(cursor.getInt(cursor.getColumnIndex("voiceTS")));
				cursor.moveToNext();
				chatList.add(chatMsg);
			}
			Collections.reverse(chatList);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}finally{
			if(cursor != null)
				cursor.close();
		}
		return chatList;
	}

	/**
	 * 判断数据库中是否已有此数据
	 */
	public boolean check(String chatId) {
		Cursor cursor = null;
		boolean exists = false;
		try {
			String sql = "SELECT count(*) FROM " + TABLE_CHAT + " WHERE cid=?";
			String[] bindArgs = { chatId };
			cursor = SQLiteHelper.getReader().rawQuery(sql, bindArgs);
			cursor.moveToFirst();
			long num = cursor.getLong(0);
			if (num > 0) {
				exists = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return exists;
	}	
	
	public List<ChatMsg> getLastChatMsg(){
		List<ChatMsg> chatMsgs = new ArrayList<ChatMsg>();
		//addfriend request
		Logger.d(TAG, "getLastChatMsg");
		chatMsgs.addAll(getAddFriendRequest(1));
		chatMsgs.addAll(getFriendLastMsg());
		//last chatmsg
		Collections.sort(chatMsgs, new ComparatorChatMsg<ChatMsg>(true));
		Logger.d(TAG, "getLastChatMsg over");
		return chatMsgs;
	}
	
	public List<ChatMsg> getAddFriendRequest(int limit){
		String sql = "select * from (" +
				 "select tname as ixr,* from "+TABLE_CHAT+" where (uname = ?) and (tname <> ?) and type = ? "+
				 "union "+
				 "select uname as ixr,* from "+TABLE_CHAT+" where (uname <> ?) and (tname = ?) and type = ?"+
				 ")"+
				 " group by ixr order by max(createTS) desc limit "+limit;		
		Logger.d(TAG, "findAllChatMsg sql:"+sql);
		String[] selectionArgs = {ConstantsApp.USERNAME,
								  ConstantsApp.USERNAME,
								  String.valueOf(ConstantsMsgType.MSG_CHAT_REQUEST),
								  ConstantsApp.USERNAME,
								  ConstantsApp.USERNAME,
								  String.valueOf(ConstantsMsgType.MSG_CHAT_REQUEST)};
		Cursor cursor = null;
		List<ChatMsg> chatList = new ArrayList<ChatMsg>();
		try{
			cursor = SQLiteHelper.getReader().rawQuery(sql, selectionArgs);
			cursor.moveToFirst();
			
			while(cursor.getPosition() != cursor.getCount()){
				ChatMsg chatMsg = new ChatMsg();
				chatMsg.setCid(cursor.getString(cursor.getColumnIndex("cid")));
				chatMsg.setType(cursor.getInt(cursor.getColumnIndex("type")));
				chatMsg.setUname(cursor.getString(cursor.getColumnIndex("uname")));
				chatMsg.setTname(cursor.getString(cursor.getColumnIndex("tname")));
				chatMsg.setContent(cursor.getString(cursor.getColumnIndex("content")));
				chatMsg.setCreateTS(cursor.getLong(cursor.getColumnIndex("createTS")));
				chatMsg.setState(cursor.getInt(cursor.getColumnIndex("state")));
				chatMsg.setIsread(cursor.getInt(cursor.getColumnIndex("isread")));
				cursor.moveToNext();
				chatList.add(chatMsg);
			}
		}catch(Exception e){
			e.printStackTrace();
			return chatList;
		}finally{
			if(cursor != null)
				cursor.close();
		}
		return chatList;
	}

	private List<ChatMsg> getFriendLastMsg(){
		String sql = "select * from (" +
					 "select tname as ixr,* from "+TABLE_CHAT+" where (uname = ?) and (tname <> ?) and type in (?,?,?) "+
					 "union "+
					 "select uname as ixr,* from "+TABLE_CHAT+" where (uname <> ?) and (tname = ?) and type in (?,?,?)"+
					 ")"+
					 " group by ixr order by max(createTS) desc ";
		Logger.d(TAG, "findAllChatMsg sql:"+sql);
		String[] selectionArgs = {ConstantsApp.USERNAME,
								  ConstantsApp.USERNAME,
								  String.valueOf(ConstantsMsgType.MSG_CHAT_TXT),
								  String.valueOf(ConstantsMsgType.MSG_CHAT_IMG),
								  String.valueOf(ConstantsMsgType.MSG_CHAT_VOICE),
								  ConstantsApp.USERNAME,
								  ConstantsApp.USERNAME,
								  String.valueOf(ConstantsMsgType.MSG_CHAT_TXT),
								  String.valueOf(ConstantsMsgType.MSG_CHAT_IMG),
								  String.valueOf(ConstantsMsgType.MSG_CHAT_VOICE)};
		Cursor cursor = null;
		List<ChatMsg> chatList = new ArrayList<ChatMsg>();
		try{
			Logger.d(TAG, "getFriendLastMsg rawQuery");
			cursor = SQLiteHelper.getReader().rawQuery(sql, selectionArgs);
			Logger.d(TAG, "getFriendLastMsg rawQuery over");
			cursor.moveToFirst();
			
			while(cursor.getPosition() != cursor.getCount()){
				ChatMsg chatMsg = new ChatMsg();
				chatMsg.setCid(cursor.getString(cursor.getColumnIndex("cid")));
				chatMsg.setType(cursor.getInt(cursor.getColumnIndex("type")));
				chatMsg.setUname(cursor.getString(cursor.getColumnIndex("uname")));
				chatMsg.setTname(cursor.getString(cursor.getColumnIndex("tname")));
				chatMsg.setContent(cursor.getString(cursor.getColumnIndex("content")));
				chatMsg.setUrl(cursor.getString(cursor.getColumnIndex("url")));
				chatMsg.setCreateTS(cursor.getLong(cursor.getColumnIndex("createTS")));
				chatMsg.setVoiceTS(cursor.getInt(cursor.getColumnIndex("voiceTS")));
				cursor.moveToNext();
				chatList.add(chatMsg);
			}
			Logger.d(TAG, "getFriendLastMsg cursor over");
			Collections.reverse(chatList);
			Logger.d(TAG, "getFriendLastMsg reverse over");
		}catch(Exception e){
			e.printStackTrace();
			return chatList;
		}finally{
			if(cursor != null)
				cursor.close();
		}
		return chatList;
	}
	
	/**
	 * 
	 * @param state 0:未处理; 1:已同意; 2:已拒绝
	 * @param tname
	 */
	public void updateFriendRequestState(int state,String tname){
		String sql = "update "+ TABLE_CHAT+
					 " set state = ?"+
					 " where (tname = ? and uname = ?) ";
		Object[] strArgs = {state, tname,ConstantsApp.USERNAME};
		Logger.d(TAG, "updateFriendRequestState sql:"+sql);
		SQLiteHelper.getReader().execSQL(sql, strArgs);
		
	}
	
	/**
	 * 
	 * @param state 0:未处理; 1:已同意; 2:已拒绝
	 * @param tname
	 */
	public void updateMyRequestState(int state,String uname){
		String sql = "update "+ TABLE_CHAT+
					 " set state = ?"+
					 " where (tname = ? and uname = ?) ";
		Object[] strArgs = {state, ConstantsApp.USERNAME, uname};
		
		SQLiteHelper.getReader().execSQL(sql, strArgs);
		
	}	
	
}
