package com.example.openfirechat.util;

import java.lang.reflect.Field;
import java.util.List;

import com.example.openfirechat.R;
import com.example.openfirechat.comm.ApplicationData;
import com.example.openfirechat.model.Emotion;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.widget.EditText;
import android.widget.TextView;


public class TextAutoLink {
	// 处理显示表情
	/**
	 * index 不需要变颜色的index值为1000
	 * */
	public static void addEmotionsSpan(Context context, String str, TextView textView, int begine, int index) {
		SpannableString ss = new SpannableString(str);
		try {
			String content = str;
			int len = 0;
			int starts = 0;
			int end = 0;
			while (len < content.length()) {
				if (content.indexOf("[", starts) != -1 && content.indexOf("]", end) != -1) {
					starts = content.indexOf("[", starts);
					end = content.indexOf("]", end);
					String phrase = content.substring(starts, end + 1);
					String imageName = "";
					List<Emotion> list = ApplicationData.listEmotion;
					for (Emotion emotions : list) {
						if (emotions.getPhrase().equals(phrase)) {
							imageName = emotions.getImageName();
						}
					}

					try {
						Field f = (Field) R.drawable.class.getDeclaredField(imageName);
						int i = f.getInt(R.drawable.class);
						Drawable drawable = context.getResources().getDrawable(i);
						if (drawable != null) {
							drawable.setBounds(0, 0, AndroidUtil.dip2px(context, 20), AndroidUtil.dip2px(context, 20));
							ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
							ss.setSpan(span, starts, end + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
						}
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (NoSuchFieldException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {

					}
					starts = end;
					len = end;
					end++;
				} else {
					starts++;
					end++;
					len = end;
				}
			}

			if (index == 1000) {//普通评论
				textView.setTextSize(14);
				textView.setTextColor(0xff333333);
				textView.setText(ss);
			} else {
				if (index == 1001) {//被删除的评论
					textView.setTextSize(14);
					textView.setTextColor(0xff999999);
					textView.setText(ss);
				} else {//评论者名字
					SpannableString temp = new SpannableString(ss);
					temp.setSpan(new ForegroundColorSpan(0xFF0099e5), begine, index, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					textView.setText(temp);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void addEmotionsSpanForChat(Context context, String str, TextView textView) {
		SpannableString ss = new SpannableString(str);
		try {
			String content = str;
			int len = 0;
			int starts = 0;
			int end = 0;
			while (len < content.length()) {
				if (content.indexOf("[", starts) != -1 && content.indexOf("]", end) != -1) {
					starts = content.indexOf("[", starts);
					end = content.indexOf("]", end);
					String phrase = content.substring(starts, end + 1);
					String imageName = "";
					List<Emotion> list = ApplicationData.listEmotion;
					for (Emotion emotions : list) {
						if (emotions.getPhrase().equals(phrase)) {
							imageName = emotions.getImageName();
						}
					}

					try {
						Field f = (Field) R.drawable.class.getDeclaredField(imageName);
						int i = f.getInt(R.drawable.class);
						Drawable drawable = context.getResources().getDrawable(i);
						if (drawable != null) {
							drawable.setBounds(0, 0, drawable.getIntrinsicWidth() - 10, drawable.getIntrinsicHeight() - 10);
							ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
							ss.setSpan(span, starts, end + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
						}
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (NoSuchFieldException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {

					}
					starts = end;
					len = end;
					end++;
				} else {
					starts++;
					end++;
					len = end;
				}
			}
			textView.setText(ss);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public static void addEmotionsSpanForInput(Context context, String str, EditText editText) {
		SpannableString ss = new SpannableString(str);
		try {
			String content = str;
			int len = 0;
			int starts = 0;
			int end = 0;
			while (len < content.length()) {
				if (content.indexOf("[", starts) != -1 && content.indexOf("]", end) != -1) {
					starts = content.indexOf("[", starts);
					end = content.indexOf("]", end);
					String phrase = content.substring(starts, end + 1);
					String imageName = "";
					List<Emotion> list = ApplicationData.listEmotion;
					for (Emotion emotions : list) {
						if (emotions.getPhrase().equals(phrase)) {
							imageName = emotions.getImageName();
						}
					}

					try {
						Field f = (Field) R.drawable.class.getDeclaredField(imageName);
						int i = f.getInt(R.drawable.class);
						Drawable drawable = context.getResources().getDrawable(i);
						if (drawable != null) {
							drawable.setBounds(0, 0, drawable.getIntrinsicWidth() - 10, drawable.getIntrinsicHeight() - 10);
							ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
							ss.setSpan(span, starts, end + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
						}
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (NoSuchFieldException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {

					}
					starts = end;
					len = end;
					end++;
				} else {
					starts++;
					end++;
					len = end;
				}
			}
			editText.setText(ss);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/***
	 * 文字转表情
	 * 
	 * @param context
	 * @param str
	 * @return
	 */
	public static SpannableString textToface(Context context, String str) {
		SpannableString ss = new SpannableString(str);
		try {
			String content = str;
			int len = 0;
			int starts = 0;
			int end = 0;
			while (len < content.length()) {
				if (content.indexOf("[", starts) != -1 && content.indexOf("]", end) != -1) {
					starts = content.indexOf("[", starts);
					end = content.indexOf("]", end);
					String phrase = content.substring(starts, end + 1);
					String imageName = "";
					List<Emotion> list = ApplicationData.listEmotion;
					for (Emotion emotions : list) {
						if (emotions.getPhrase().equals(phrase)) {
							imageName = emotions.getImageName();
						}
					}

					try {
						Field f = (Field) R.drawable.class.getDeclaredField(imageName);
						int i = f.getInt(R.drawable.class);
						Drawable drawable = context.getResources().getDrawable(i);
						if (drawable != null) {
							drawable.setBounds(0, 0, drawable.getIntrinsicWidth() - 10, drawable.getIntrinsicHeight() - 10);
							ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
							ss.setSpan(span, starts, end + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
						}
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (NoSuchFieldException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {

					}
					starts = end;
					len = end;
					end++;
				} else {
					starts++;
					end++;
					len = end;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ss;
	}

}