package com.example.openfirechat.comm;

import java.util.Map;
import java.util.Set;

import org.jivesoftware.smack.packet.IQ;


public class JabberRPC extends IQ {

    private final String xml; 

    public JabberRPC(final String xml) { 
            this.xml = "<query xmlns='jabber:iq:rpc'>\n" + xml + "\n</query>"; 
    } 
    
    public JabberRPC(Map<String,Object> map){
    	this.xml = "<query xmlns='jabber:iq:rpc'>\n" + setMap(map) + "\n</query>";
    }
    
    
    public String setMap(Map<String,Object> map){
    	StringBuffer sb = new StringBuffer();
    	Set<String> keys = map.keySet();
    	for(String key : keys){
    		sb.append("<"+key+">\n");
    		sb.append(map.get(key));
    		sb.append("\n</"+key+">");
    	}
    	return sb.toString();
    }
    

    public String getChildElementXML() { 
            return this.xml; 
    } 

}
