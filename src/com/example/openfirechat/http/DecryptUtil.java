package com.example.openfirechat.http;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

//import com.castle.lib.Log;

public class DecryptUtil {
	
	private static final String MAGIC = "e=1&len=";
    private static final int MAGIC_LEN = MAGIC.length();
    private static final String DATA = "&d=";
    private static final int DATA_LEN = DATA.length();
    //private static final String TAG = "DecryptUtil";

    public static String decrytEntity(final HttpEntity entity) throws IOException {
    	String responseBody = null;
    	byte[] encrypted = EntityUtils.toByteArray(entity);
    	if (encrypted.length > 0) {
    		String entityStr = new String(encrypted, "UTF-8");
		    int magicPos = entityStr.indexOf(MAGIC);
		    int dPos = entityStr.indexOf(DATA);
		    if (magicPos == -1 || dPos == -1) {
		    	responseBody = entityStr;
		    } else {
		    	String dataLenStr = entityStr.substring(magicPos+MAGIC_LEN, dPos);
		    	int dataLen = Integer.parseInt(dataLenStr);
		    	responseBody = RC4.decryptionRC4(encrypted, RC4.KEY, dPos+DATA_LEN, dataLen);
		    }
		    // Log.d(TAG, "responseBody:"+responseBody);
    	}
	    return responseBody;
    }
}
