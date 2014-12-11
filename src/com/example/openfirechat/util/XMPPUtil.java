package com.example.openfirechat.util;

import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import com.example.openfirechat.model.User;

public class XMPPUtil {

	public static long getUserIdFromJID(String jid){
		
		String node =StringUtils.parseName(jid);
		try{
			return Long.valueOf(node);
		}catch(Exception e){
			e.printStackTrace();
		}
		return -1;
	}
	
	public static String getUserNameFromJID(String jid){
		
		return StringUtils.parseName(jid);
	}	
	
	public static User getUserFromRosterEntry(RosterEntry rosterEntry){
		User user = new User();
		user.setJid(rosterEntry.getUser());
		user.setUserName(getUserNameFromJID(rosterEntry.getUser()));
		user.setName(rosterEntry.getName());
		return user;
	}
	
	public static void initUserFromVCard(VCard vcard,User user){
		user.setName(vcard.getNickName());
	}
	
}
