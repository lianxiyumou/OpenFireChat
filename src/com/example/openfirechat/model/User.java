package com.example.openfirechat.model;

import org.apache.commons.lang.StringUtils;

import com.example.openfirechat.util.XMLUtil;
import com.example.openfirechat.util.XMPPUtil;

public class User {
	
	private String id;
	private String jid;
	private String name;//昵称
	private String userName;
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	private String img;
	private String groupName;
	private String password;
	private long account;//用户ID
	
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public long getAccount() {
		if(account == 0){
			return XMPPUtil.getUserIdFromJID(jid);
		}
		return account;
	}
	public void setAccount(long account) {
		this.account = account;
	}
	public String getId() {
		return id;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getJid() {
		return jid;
	}
	public void setJid(String jid) {
		this.jid = jid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}

}
