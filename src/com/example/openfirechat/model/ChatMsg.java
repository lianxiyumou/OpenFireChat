package com.example.openfirechat.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.util.TimeUtils;

public class ChatMsg implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String cid;
	private int type;//类型: 1 文字, 2: 图片, 3: voice, 4: 加好友
	private String uname;//消息发起者用户名
	private String tname;//消息目标用户名	
	private String content = "";
	private String url;
	private long createTS;
	private int voiceTS;//s
	private int state;//0:未处理; 1:已同意; 2:已拒绝
	private int isread;//0:未读；1：已读
	
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public int getIsread() {
		return isread;
	}
	public void setIsread(int isread) {
		this.isread = isread;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	
	public String getUname() {
		return uname;
	}
	public void setUname(String uname) {
		this.uname = uname;
	}
	public String getTname() {
		return tname;
	}
	public void setTname(String tname) {
		this.tname = tname;
	}

	
	public int getVoiceTS() {
		return voiceTS;
	}
	public void setVoiceTS(int voiceTS) {
		this.voiceTS = voiceTS;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getCid() {
		return cid;
	}
	public void setCid(String cid) {
		this.cid = cid;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public long getCreateTS() {
		return createTS;
	}
	public void setCreateTS(long createTS) {
		this.createTS = createTS;
	}
	
	public String getTime(){
		Date date = new Date(this.createTS);
		return com.example.openfirechat.util.TimeUtils.friendly_time(date, false);
	}
	

}
