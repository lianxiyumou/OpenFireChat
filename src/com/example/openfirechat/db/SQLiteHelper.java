package com.example.openfirechat.db;


import com.example.openfirechat.comm.ApplicationData;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper {

	private static final String TAG = "luopeng";
	
	private static final String DB_NAME = "openfirchat.db";
	
	/**
	 * V1 -> V2 : add voiceTS field for chat table
	 */
	private static final int DB_VERSION = 3;
	
	private static SQLiteHelper instance = null;
	
	private SQLiteHelper() {
		/*
		 * CursorFactory指定在执行查询时获得一个游标实例的工厂类 。 设置为null ， 则使用系统默认的工厂类 。
		 */
		super(ApplicationData.getInstance().getApplicationContext(), DB_NAME, null, DB_VERSION);
	}
	
	public static SQLiteHelper getInstance (){
		if(instance == null){
			synchronized (SQLiteHelper.class) {
				if(instance == null){
					instance = new SQLiteHelper();
				}
			}
		}
		return instance;
	}
	
    public static SQLiteDatabase getWriter() {
    	// Don't do anything.
		SQLiteHelper helper = SQLiteHelper.getInstance();
		
		try {
			SQLiteDatabase db = helper.getWritableDatabase();
			if (db == null) {
	    		Log.e(TAG, "Get writeable DB instance failed.");
	    		return null;
	    	}
			
			return db;
		} catch (Exception e) {
			return null;
		}
    }
    
    public static SQLiteDatabase getReader() {
    	SQLiteHelper helper = SQLiteHelper.getInstance();
    	try {
	    	SQLiteDatabase db = helper.getReadableDatabase();
	    	if (db == null) {
	    		Log.e(TAG, "Get writeable DB instance failed.");
	    		return null;
	    	}
	    	
	    	return db;
    	} catch (Exception e) {
    		return null;
    	}
    }	
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		Log.i(TAG, "SQLiteHelper onCreate");
		db.execSQL(ChatMsgDao.CHAT_SQL);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		if(oldVersion <= 1 && newVersion >= 2){
			db.execSQL(ChatMsgDao.CHAT_V1_TO_V2_VOICETS);
		}else if(oldVersion <= 2 && newVersion >= 3){
			db.execSQL(ChatMsgDao.CHAT_V2_TO_V3_STATE);
			db.execSQL(ChatMsgDao.CHAT_V2_TO_V3_READ);
		}
	}
	
}
