package com.example.openfirechat.util;

import java.io.File;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class FileUtil {

	public static File getFileFromUri(Uri uri,Context context){
		ContentResolver contentResolver = context.getContentResolver();  
		String[] proj = { MediaStore.Images.Media.DATA };  
		  
		Cursor actualimagecursor = contentResolver.query(uri,proj,null,null,null);  
		  
		int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);  
		  
		actualimagecursor.moveToFirst();  
		  
		String img_path = actualimagecursor.getString(actual_image_column_index);  
		  
		File file = new File(img_path); 
		if(file.exists()){
			return file;
		}else{
			return null;
		}
	}
	
}
