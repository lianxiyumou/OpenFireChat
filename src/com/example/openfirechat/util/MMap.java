package com.example.openfirechat.util;

import java.util.HashMap;

import com.example.openfirechat.comm.Logger;


public class MMap extends HashMap<String,Object> {

	private static String TAG = "luopeng";
	
	public int getInt(String key){
		int value = -1;
		if(!this.containsKey(key)){
			return value;
		}
		try{
			Object valueobj = this.get(key);
			Logger.d(TAG, "value:"+valueobj);
			if(valueobj instanceof Integer){
				value = (Integer) valueobj;
			}else if(valueobj instanceof Double){
				double tmp = (Double) valueobj;
				value = (int) tmp;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return value;
	}
	
}
