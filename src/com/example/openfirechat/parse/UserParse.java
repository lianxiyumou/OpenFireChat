package com.example.openfirechat.parse;

import com.example.openfirechat.model.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

public class UserParse {

	public static User parseJson(String json){
		Gson gson = new Gson();
		
		JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
		User user = new User();
		if(jsonObject.has("username")){
			user.setUserName(jsonObject.get("username").getAsString());//用户昵称
			user.setName(jsonObject.get("username").getAsString());
		}
		if(jsonObject.has("id")){//用户id
			user.setAccount((long)jsonObject.get("id").getAsFloat());
		}
		
		if(jsonObject.has("password")){
			user.setPassword(jsonObject.get("password").getAsString());
		}
		
		return user;
	}
	
}
