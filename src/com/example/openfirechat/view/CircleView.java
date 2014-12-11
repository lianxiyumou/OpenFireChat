package com.example.openfirechat.view;

import com.example.openfirechat.R;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;


/**
 * 
 * 动态圆点控件
 * 
 * @author Zhengcw
 *
 */
public class CircleView extends LinearLayout {
	private Context mContext;

	public CircleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CircleView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		this.mContext = context;
		setOrientation(LinearLayout.HORIZONTAL);

	}

	public void initCircle(int size) {
		removeAllViews();
		for (int i = 0; i < size; i++) {
			ImageView imageView = new ImageView(mContext);
			imageView.setImageResource(R.drawable.emoji_circle_icon);
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.leftMargin = 6;
			params.rightMargin = 6;
			imageView.setLayoutParams(params);
			addView(imageView, i);
		}
	}

	public void chooseCircle(int index) {
		int size = getChildCount();
		for (int i = 0; i < size; i++) {
			if (i == index) {
				ImageView imageView = (ImageView) getChildAt(i);
				imageView.setImageResource(R.drawable.emoji_circle_icon_press);
			} else {
				ImageView imageView = (ImageView) getChildAt(i);
				imageView.setImageResource(R.drawable.emoji_circle_icon);

			}

		}
	}

}
