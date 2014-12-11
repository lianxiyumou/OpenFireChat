package com.example.openfirechat.view;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import android.content.Context;
import android.widget.ImageView;

public class MyImageView extends ImageView {

	public float mRadius = 4;
	private String mScaleType = "";
       
    protected ImageLoader mImageLoader = ImageLoader.getInstance();	
	
	public MyImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	
	public void setImage(String url, int h, int w, final int def) {
    	if (url == null || url.length() <= 3) {
    		if (def > 0)
    			setImageResource(def);
    		return;
    	}

    	DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
    	if (def != 0) {
    		builder.showImageOnLoading(def)
				.showImageForEmptyUri(def)
				.showImageOnFail(def);
    	}
    	
    	if (mScaleType != null && mScaleType.equals("exactly")) {
    		builder.imageScaleType(ImageScaleType.EXACTLY);
    	} else {
    		builder.imageScaleType(ImageScaleType.EXACTLY_STRETCHED);
    	}
    	DisplayImageOptions options = builder.cacheInMemory(true)
			.cacheOnDisc(true)
			.considerExifParams(true)
			.displayer(new RoundedBitmapDisplayer((int)mRadius))
			.build();
    	
		mImageLoader.displayImage(url, this, options);
    }    	

}
