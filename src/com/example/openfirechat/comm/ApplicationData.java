package com.example.openfirechat.comm;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.openfirechat.constants.ConstantsApp;
import com.example.openfirechat.model.Emotion;
import com.example.openfirechat.util.ResUtil;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

public class ApplicationData extends Application {

	// 文字对应表情图片数组
		public static List<Emotion> listEmotion = new ArrayList<Emotion>();
		public static ArrayList<HashMap<String, Object>> emojiData = new ArrayList<HashMap<String, Object>>();
		public static ArrayList<HashMap<String, Object>> animationData = new ArrayList<HashMap<String, Object>>();

	
	private static ApplicationData instance = null;
	
	public static ApplicationData getInstance() {
		if (instance == null) {
			instance = new ApplicationData();
		}
		return instance;
	}	
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		instance = this;
		Log.i("luopeng", "ApplicationData onCreate");
		initEmoji();
		initFilePath();
		initImageLoader(getApplicationContext());
		connectOpenfire();
	}
	
	private void initEmoji(){
		// 加载表情列表
		ResUtil.initEmotionArr(this.getApplicationContext());
		ResUtil.initEmojiData(this);
		ResUtil.initAnimationData(getApplicationContext());		
	}
	
	public static void initImageLoader(Context context) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.discCacheSize(209715199)
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}	
	
	public void initFilePath(){
		
        File cache = getCacheDir();
        if(!cache.exists()) {
            if (cache.mkdirs()) {
            	ConstantsApp.CACHE_DIR = cache.getAbsolutePath();
            }
        } else {
        	ConstantsApp.CACHE_DIR = cache.getAbsolutePath();
        }		
		
		if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            ConstantsApp.CHAT_TEMP_FILE = Environment.getExternalStorageDirectory().getPath() +  
            		"/openfire/file/";
            createDirs(ConstantsApp.CHAT_TEMP_FILE);
		}else{
			ConstantsApp.CHAT_TEMP_FILE = ConstantsApp.CACHE_DIR+"/file/";
			createDirs(ConstantsApp.CHAT_TEMP_FILE);
		}
    	ConstantsApp.CHAT_TEMP_IMG = ConstantsApp.CHAT_TEMP_FILE + "image/";
    	ConstantsApp.CHAT_TEMP_VOICE = ConstantsApp.CHAT_TEMP_FILE + "voice/";	
    	ConstantsApp.CHAT_TEMP_OTHER = ConstantsApp.CHAT_TEMP_FILE + "other/";
    	createDirs(ConstantsApp.CHAT_TEMP_IMG );
    	createDirs(ConstantsApp.CHAT_TEMP_VOICE );
    	createDirs(ConstantsApp.CHAT_TEMP_OTHER );
	}
	
	private void createDirs(String url){
		try{
			File File = new File(url);
			if(!File.exists()) {
				File.mkdirs();
			}		
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void connectOpenfire(){
		Intent intent = new Intent(getApplicationContext(),ChatService.class);
		startService(intent);
	}
	
}
