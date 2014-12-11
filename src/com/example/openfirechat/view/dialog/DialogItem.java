package com.example.openfirechat.view.dialog;

public class DialogItem {
	private int mTextId;
	private int mViewId;
	
	public  DialogItem(int textId, int viewId){
		mTextId = textId;
		mViewId = viewId;
	}
	//点击事件
	public void onClick(){
	}

	public int getTextId() {
		return mTextId;
	}

	public void setTextId(int mTextId) {
		this.mTextId = mTextId;
	}

	public int getViewId() {
		return mViewId;
	}

	public void setViewId(int mViewId) {
		this.mViewId = mViewId;
	}

	
	
		
	
}

