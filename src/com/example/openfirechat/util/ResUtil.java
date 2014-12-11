package com.example.openfirechat.util;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;

import com.example.openfirechat.R;
import com.example.openfirechat.comm.ApplicationData;
import com.example.openfirechat.model.Emotion;
import com.example.openfirechat.util.GifHelper.GifFrame;

import android.content.Context;

public class ResUtil {

	// 加载表情文中对应图片数组
		public static void initEmotionArr(Context context) {
			String[] faceArray = context.getResources().getStringArray(R.array.face_arr);
			if (ApplicationData.listEmotion.size() < 1) {
				for (int i = 0; i < faceArray.length; i++) {
					ApplicationData.listEmotion.add(new Emotion(faceArray[i], "face" + (i + 1)));
				}
			}
		}

		/**
		 * 
		 * 初始化静态表情数据
		 * 
		 * @param context
		 */
		public static void initEmojiData(Context context) {
			if (ApplicationData.emojiData.size() < 1) {
				String[] faceArray = context.getResources().getStringArray(R.array.face_arr);
				for (int i = 0; i < faceArray.length; i++) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					try {
						Field f = (Field) R.drawable.class.getDeclaredField("face" + (i + 1));
						map.put("imageItem", f.getInt(R.drawable.class));
					} catch (SecurityException e) {

						e.printStackTrace();
					} catch (NoSuchFieldException e) {

						e.printStackTrace();
					} catch (IllegalArgumentException e) {

						e.printStackTrace();
					} catch (IllegalAccessException e) {

						e.printStackTrace();
					}
					map.put("textItem", faceArray[i]);
					ApplicationData.emojiData.add(map);
				}
			}

		}

		/**
		 * 
		 * 初始化动态表情数据
		 * 
		 * @param context
		 */
		public static void initAnimationData(final Context context) {
			new Thread() {
				public void run() {
					if (ApplicationData.animationData.size() < 1) {
						String[] faceArray = context.getResources().getStringArray(R.array.xian_mi_animation_arr);
						for (int i = 0; i < faceArray.length; i++) {
							HashMap<String, Object> map = new HashMap<String, Object>();
							try {
								Field f = (Field) R.drawable.class.getDeclaredField("xiao_mi_" + (i + 1) + "_cover");
								map.put("imageItem", f.getInt(R.drawable.class));
								Field gifField = (Field) R.drawable.class.getDeclaredField("xiao_mi_" + (i + 1));
								map.put("gifItem", gifField.getInt(R.drawable.class));
								map.put("name", "xiao_mi_" + (i + 1));
								InputStream is = context.getResources().openRawResource(gifField.getInt(R.drawable.class));
								GifFrame[] frames = GifUtil.getGif(is);
								map.put("xiao_mi_" + (i + 1), frames);
							} catch (SecurityException e) {

								e.printStackTrace();
							} catch (NoSuchFieldException e) {

								e.printStackTrace();
							} catch (IllegalArgumentException e) {

								e.printStackTrace();
							} catch (IllegalAccessException e) {

								e.printStackTrace();
							}
							map.put("textItem", faceArray[i]);
							ApplicationData.animationData.add(map);
						}
					}
				};
			}.start();

		}	
	
}
