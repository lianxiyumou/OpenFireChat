package com.example.openfirechat.model;

import java.util.ArrayList;
import java.util.HashMap;

/***
 * 
 * 
 * @author Zhengcw
 * 
 */
public class EmojiInfo {
	private int emojiType;
	private ArrayList<HashMap<String, Object>> emojiData;
	private int emojiColumns;
	private int emojiLines;
	private int tabIconId;

	public int getEmojiType() {
		return emojiType;
	}

	public void setEmojiType(int emojiType) {
		this.emojiType = emojiType;
	}

	public ArrayList<HashMap<String, Object>> getEmojiData() {
		return emojiData;
	}

	public void setEmojiData(ArrayList<HashMap<String, Object>> emojiData) {
		this.emojiData = emojiData;
	}

	public int getEmojiColumns() {
		return emojiColumns;
	}

	public void setEmojiColumns(int emojiColumns) {
		this.emojiColumns = emojiColumns;
	}

	public int getEmojiLines() {
		return emojiLines;
	}

	public void setEmojiLines(int emojiLines) {
		this.emojiLines = emojiLines;
	}

	public int getTabIconId() {
		return tabIconId;
	}

	public void setTabIconId(int tabIconId) {
		this.tabIconId = tabIconId;
	}

}
