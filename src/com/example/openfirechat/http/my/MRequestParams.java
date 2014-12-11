package com.example.openfirechat.http.my;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import com.example.openfirechat.comm.Logger;
import com.example.openfirechat.http.RC4;
import com.example.openfirechat.http.RequestParams;
import com.example.openfirechat.http.SimpleMultipartEntity;


public class MRequestParams extends RequestParams {

	
    public HttpEntity getEntity() {
        HttpEntity entity = null;

        if(!fileParams.isEmpty()) { /* passsing files */
            SimpleMultipartEntity multipartEntity = new SimpleMultipartEntity();

            // Add string params
//            for(ConcurrentHashMap.Entry<String, String> entry : urlParams.entrySet()) {
//                multipartEntity.addPart(entry.getKey(), entry.getValue());
//            }
            multipartEntity.addPart("ENCRYPTED", "ENCRYPTED", getEncryptedStream(), false);

            // Add file params
            int currentIndex = 0;
            int lastIndex = fileParams.entrySet().size() - 1;
            for(ConcurrentHashMap.Entry<String, FileWrapper> entry : fileParams.entrySet()) {
                FileWrapper file = entry.getValue();
                if(file.inputStream != null) {
                	boolean isLast = currentIndex == lastIndex;
                    if(file.contentType != null) {
                        multipartEntity.addPart(entry.getKey(), file.getFileName(), file.inputStream, file.contentType, isLast);
                    } else {
                        multipartEntity.addPart(entry.getKey(), file.getFileName(), file.inputStream, isLast);
                    }
                }
                currentIndex++;
            }

            entity = multipartEntity;
        } else {
        	Logger.d("luopeng", "MRequsetParams getEntity");
                try {
					entity = new UrlEncodedFormEntity(getDecodeParamsList(), ENCODING);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//            SimpleMultipartEntity multipartEntity = new SimpleMultipartEntity();
//
//            multipartEntity.addPart("ENCRYPTED", "ENCRYPTED", getEncryptedStream(), true);
//
//            entity = multipartEntity;
        }

        return entity;
    }	
    
    protected List<BasicNameValuePair> getDecodeParamsList() {
        List<BasicNameValuePair> lparams = new LinkedList<BasicNameValuePair>();

    	StringBuilder sb = new StringBuilder();
        int size = urlParams.size();

        for(ConcurrentHashMap.Entry<String, String> entry : urlParams.entrySet()) {
        	sb.append(entry.getKey());
        	sb.append("=");
        	sb.append(entry.getValue());
        	if ((--size) > 0) {
        		sb.append("&");
        	}
        }
        String deParams = RC4.encryptionRC4String(sb.toString(), RC4.KEY);
        Logger.d("luopeng", "MRequestParams :"+deParams);
        lparams.add(new BasicNameValuePair("params", deParams));
        return lparams;
    }    
    
    
    
    
	
}
