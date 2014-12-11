package com.example.openfirechat.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;


import android.util.Log;


public class HttpClient {

    public String executePost() {
        String result = null;
        BufferedReader reader = null;
        try {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost();
            request.setURI(new URI("http://localhost:9090/plugins/offfile/uploadfile.do"));
            List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            ConcurrentHashMap<String, String> params = new ConcurrentHashMap<String, String>();
            params.put("token", "alexzhou");
            params.put("username", "luopeng3");
            params.put("pwd", "23456");
            String encodeParams = encode(params);
            Log.d("luopeng", "execute encodeParams:"+encodeParams);
            postParameters.add(new BasicNameValuePair("params", encodeParams));
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(
                    postParameters);
            request.setEntity(formEntity);
            
            HttpResponse response = client.execute(request);
            reader = new BufferedReader(new InputStreamReader(response
                    .getEntity().getContent()));
 
            StringBuffer strBuffer = new StringBuffer("");
            String line = null;
            while ((line = reader.readLine()) != null) {
                strBuffer.append(line);
            }
            result = strBuffer.toString();
            Log.d("luopeng", "executePost result:"+result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                    reader = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
 
        return result;
    } 	
    
    private String encode(ConcurrentHashMap<String, String> urlParams){
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
        Log.d("luopeng", "encode: "+sb.toString());
        return sb.toString();
    }
	
}
