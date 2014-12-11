package com.example.openfirechat.util;

import java.io.StringWriter;

import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import com.example.openfirechat.model.User;

public class XMLUtil {

	public static String getUserVCardXML(User user){
		StringWriter stringWriter = new StringWriter();
		try{
            // 获取XmlSerializer对象  
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();  
            XmlSerializer xmlSerializer = factory.newSerializer(); 
            // 设置输出流对象  
            xmlSerializer.setOutput(stringWriter);   
            xmlSerializer.startDocument("utf-8", true);
            xmlSerializer.startTag(null, "VCARD");
            
            xmlSerializer.startTag(null, "NICKNAME");  
            xmlSerializer.text(user.getName());  
            xmlSerializer.endTag(null, "NICKNAME"); 
            
            xmlSerializer.startTag(null, "ORGNAME");  
            xmlSerializer.text(user.getGroupName());  
            xmlSerializer.endTag(null, "ORGNAME");    
            
            xmlSerializer.endTag(null, "VCARD");
            xmlSerializer.endDocument(); 
		}catch(Exception e){
			
		}
		return stringWriter.toString();
	}
	
}
