package com.example.openfirechat.util;

import java.io.InputStream;

import com.example.openfirechat.util.GifHelper.GifFrame;



public class GifUtil {
	/**
	 * 解码GIF图片
	 * 
	 * @param is
	 * @return
	 */
	public static GifFrame[] getGif(final InputStream is) {
		GifHelper gifHelper = new GifHelper();
		if (GifHelper.STATUS_OK == gifHelper.read(is)) {
			return gifHelper.getFrames();
		} else {
			return null;
		}

	}

	/**
	 * 判断图片是否为GIF格式
	 * 
	 * @param is
	 * @return
	 */
	public static boolean isGif(InputStream is) {
		GifHelper gifHelper = new GifHelper();
		return gifHelper.isGif(is);
	}
}
