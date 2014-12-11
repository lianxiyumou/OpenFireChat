package com.example.openfirechat.util;

import java.util.Date;
import java.util.List;

import com.example.openfirechat.R;
import com.example.openfirechat.view.dialog.DialogItem;
import com.example.openfirechat.view.dialog.OnItemClick;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/***
 * 
 * 显示通用的对话框的工具类
 * 
 * @author Luzj
 * 
 */
public class UtilDialog {
	public static Dialog createBottomDialog(Context context, List<DialogItem> items, int style) {
		LinearLayout dialogView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.custom_bottom_dialog_layout,
				null);
		final Dialog customDialog = new Dialog(context, style);
		LinearLayout itemView;
		TextView textView;
		for (DialogItem item : items) {
			itemView = (LinearLayout) LayoutInflater.from(context).inflate(item.getViewId(), null);
			textView = (TextView) itemView.findViewById(R.id.popup_text);
			textView.setText(item.getTextId());
			textView.setOnClickListener(new OnItemClick(item, customDialog));
			dialogView.addView(itemView);
		}

		WindowManager.LayoutParams localLayoutParams = customDialog.getWindow().getAttributes();
		localLayoutParams.x = 0;
		localLayoutParams.y = -1000;
		localLayoutParams.gravity = 80;
		dialogView.setMinimumWidth(10000);

		customDialog.onWindowAttributesChanged(localLayoutParams);
		customDialog.setCanceledOnTouchOutside(true);
		customDialog.setCancelable(true);
		customDialog.setContentView(dialogView);

		if (context instanceof Activity) {
			Activity activity = (Activity) context;
			if (!activity.isFinishing()) {
				customDialog.show();
			}
		}

		return customDialog;
	}	
}
