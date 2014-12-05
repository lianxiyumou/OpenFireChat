package com.example.openfirechat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransfer.Error;
import org.jivesoftware.smackx.filetransfer.FileTransfer.Status;

import com.example.openfirechat.comm.ApplicationData;
import com.example.openfirechat.comm.ConnectionManager;
import com.example.openfirechat.comm.Logger;
import com.example.openfirechat.constants.ConstantsApp;
import com.example.openfirechat.constants.ConstantsMsgType;
import com.example.openfirechat.db.ChatMsgDao;
import com.example.openfirechat.model.ChatMsg;
import com.example.openfirechat.model.EmojiInfo;
import com.example.openfirechat.parse.ChatMsgParse;
import com.example.openfirechat.util.AndroidUtil;
import com.example.openfirechat.util.ComparatorChatMsg;
import com.example.openfirechat.util.FileUtil;
import com.example.openfirechat.util.TextAutoLink;
import com.example.openfirechat.view.EmojiView;
import com.example.openfirechat.view.EmojiView.CallBack;
import com.example.openfirechat.view.RefreshListView;
import com.example.openfirechat.view.RefreshListView.RefreshListener;
import com.example.openfirechat.view.adapter.ChatListAdapter;
import com.example.openfirechat.view.adapter.ChatMoreFunctionAdapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;



public class ChatRoom extends Activity implements OnClickListener,OnItemClickListener,RefreshListener,CallBack {

	private EditText edit;
	private ImageButton btn_send,btn_voice,btn_keyboard;
	private Button btn_record;
	private GridView moreGrid;
	private EmojiView emojiView;
//	private String to_account;
//	private String to_name;
//	private long targetID;
//	private long myID;
	private String from_username;
	private String to_username;
	private Chat chat;
	private TextView title;
	private RefreshListView listView;
	private List<ChatMsg> chatMsgs = new ArrayList<ChatMsg>();
	private ChatListAdapter chatAdapter;
	
	private static final int UPDATE_CHAT = 1;
	private static final int UPDATE_ALLCHAT = 2;
	private static final int UPDATE_INPUT = 3;
	private static final int UPDATE_VOICE = 4;
	private static final int UPDATE_ITEMA = 5;
	private Uri mTakePhotoUri;
	private static final int REQ_TAKE_PHOTO = 1;
	private static final int REQ_PICK_PHOTO = 2;	
	
	
	private static final int count = 10;
	private long lastTime;
	
	private static final String  TAG = "luopeng";
	
	private int[] mRecordLocation = new int[2];
	
	private boolean isCancel = false,mFinished = false, mRecording = false;
	
	private String mFileName = null;
	
	private MediaRecorder mRecorder = null;
	private long   mStartTime;
	
	private static final int MIN_INTERVAL = 2000; // 2s
	
	private PopupWindow mMicPopupWindow;
	private View mMicView; // 麦克风
	private View mVoiceFail; // 录音时间太短
	private ImageView mMicImageView;
	private AnimationDrawable mMicAnimation;
	private ProgressBar mRecordPrepareProgressBar;
	private LinearLayout mMicLayout;	
	
	private BroadcastReceiver receiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Log.i("luopeng", "ChatRoom onReceive");
			Bundle bundle = intent.getExtras();
			if(bundle != null){
				if(bundle.containsKey("type")){
					String type = bundle.getString("type");
					if("txt".equals(type)){
						Toast.makeText(ChatRoom.this, bundle.getString("content"), Toast.LENGTH_LONG).show();
					}else if("image".equals(type)){
						Toast.makeText(ChatRoom.this, "image", Toast.LENGTH_LONG).show();
					}
					if(bundle.containsKey("chat")){
						ChatMsg chatMsg = (ChatMsg) bundle.getSerializable("chat");
						if(chatMsg.getCreateTS() == 0){
							chatMsg.setCreateTS(Calendar.getInstance().getTimeInMillis());
						}
						updateChatList(chatMsg);
					}
				}
			}
		}
		
	};
	
	private Handler hander = new Handler(){
		@Override
		public void handleMessage(android.os.Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what){
			case UPDATE_CHAT:
				if(msg.obj instanceof String){
					Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_LONG).show();
				}
				break;
			case UPDATE_ALLCHAT:
				if(msg.obj instanceof List){
					chatMsgs.clear();
					chatMsgs.addAll((List<ChatMsg>)msg.obj);
					chatAdapter.notifyDataSetChanged();	
					if(chatMsgs.size() != 0)
						listView.setSelection(chatMsgs.size() - 1);
				}
				break;
			case UPDATE_INPUT:
				edit.setText("");
				if(msg.obj instanceof ChatMsg){
					ChatMsg chatMsg = (ChatMsg)msg.obj;
					updateChatList(chatMsg);
				}
				break;
			case UPDATE_VOICE:
				if (mMicAnimation != null) {
					if(mMicAnimation.isRunning())
						mMicAnimation.stop();
					mMicAnimation.start();
				}				
				break;
			case UPDATE_ITEMA:
				if(msg.obj instanceof ChatMsg){
					ChatMsg chatMsg = (ChatMsg)msg.obj;
					updateChatList(chatMsg);
				}				
			}
		}
	};
	
	private void updateChatList(ChatMsg chatMsg){
		chatMsgs.add(chatMsg);
		Collections.sort(chatMsgs, new ComparatorChatMsg<ChatMsg>());
		chatAdapter.notifyDataSetChanged();	
		listView.setSelection(chatMsgs.size() -1);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Intent getIntent = getIntent();
		from_username = getIntent.getStringExtra("from");
		to_username = getIntent.getStringExtra("to");
		
		setContentView(R.layout.chat_room);
		initView();
		setListener();
		initData();
		
		Log.i("luopeng", "from:"+from_username+", to:"+to_username);
		startChat();
		
        IntentFilter myIntentFilter = new IntentFilter();  
        myIntentFilter.addAction("chat"); 
		registerReceiver(receiver, myIntentFilter);
		
		getHistoryMessage();
	}
	
	private void initView(){
		title = (TextView) findViewById(R.id.title);
		listView = (RefreshListView) findViewById(R.id.chatting_list);
		listView.removeFootView();
		edit = (EditText) findViewById(R.id.input);
		btn_send = (ImageButton) findViewById(R.id.send);
		moreGrid = (GridView) findViewById(R.id.more_grid);
		emojiView = (EmojiView) findViewById(R.id.emoji_view);
		btn_voice = (ImageButton) findViewById(R.id.chat_bottom_voice);
		btn_record = (Button) findViewById(R.id.record);
		btn_keyboard  = (ImageButton)findViewById(R.id.chat_bottom_keyboard);
		btn_record.getLocationOnScreen(mRecordLocation);
		initPopuVoice();
	}
	
	private void initPopuVoice(){
		mMicView = LayoutInflater.from(this).inflate(R.layout.chat_mic_layout, null);
		mRecordPrepareProgressBar = (ProgressBar) mMicView.findViewById(R.id.pb);
		mMicLayout = (LinearLayout) mMicView.findViewById(R.id.ll_mic);
		mMicImageView = (ImageView) mMicView.findViewById(R.id.iv_mic);
		mMicImageView.setBackgroundResource(R.anim.mic_anim);
		mMicAnimation = (AnimationDrawable) mMicImageView.getBackground();
		mMicAnimation.setOneShot(true);
		mMicPopupWindow = new PopupWindow(mMicView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mMicPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mMicPopupWindow.setOutsideTouchable(true);		
	}
	
	private void setListener(){
		btn_send.setOnClickListener(this);
		findViewById(R.id.btn_more).setOnClickListener(this);
		edit.setOnClickListener(this);
		edit.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				Logger.d(TAG, "OnTouchListener");
				return false;
			}
		});
		moreGrid.setOnItemClickListener(this);
		listView.setOnRefreshListener(this, false);
		emojiView.setListener(this);	
		btn_voice.setOnClickListener(this);
		btn_keyboard.setOnClickListener(this);
		
		btn_record.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				
				switch(event.getAction()){
				case MotionEvent.ACTION_DOWN:
					btn_record.setText("松开发送录音");
					startRecord();
					break;
				case MotionEvent.ACTION_UP:
					btn_record.setText("按住录音");
					if(isCancel){
						cancelRecrod();
					}else{
						finishRecord();
					}
					break;
				case MotionEvent.ACTION_MOVE:
					float y = event.getY();
					float x = event.getX();
					if(y < mRecordLocation[1] || y > mRecordLocation[1]+btn_record.getHeight() ||
					   x < mRecordLocation[0] || x >mRecordLocation[0]+btn_record.getWidth()){
						isCancel = true;
						btn_record.setText("松开取消录音");
					}else{
						isCancel = false;
						btn_record.setText("松开发送录音");
					}
					break;
				}
				return false;
			}
		});
	}
	
	private void startRecord(){
		if(mRecording)
			return;
		try {
			Logger.d(TAG, "startRecord createVoiceFile");
			if(!createVoiceFile()){
				return;
			}
			if (mMicPopupWindow != null) {
				mMicPopupWindow.showAtLocation(title, Gravity.CENTER, 0, 0);
			}			
			mRecorder = new MediaRecorder();
			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			// AMR_WB can be used Level 10
			if(android.os.Build.VERSION.SDK_INT >= 10) {
				mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
				mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
			} else {
				mRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
				mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);	
			}
			Logger.d(TAG, "startRecord setOutputFile");
			mRecorder.setOutputFile(mFileName);
			mRecorder.prepare();
			isCancel = false;
			mFinished = false;
			mRecording = true;
			mStartTime = System.currentTimeMillis();
			mRecorder.start();
			Logger.d(TAG, "startRecord mRecorder.start");
			mMicLayout.setVisibility(View.VISIBLE);
			mRecordPrepareProgressBar.setVisibility(View.GONE);
			new SoundThread().start();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	class SoundThread extends Thread{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				while(mRecording){
					int vuSize = 10 * mRecorder.getMaxAmplitude() / 32768;
					if (vuSize != 0) {
						android.os.Message msg = hander.obtainMessage();
						msg.what = UPDATE_VOICE;
						hander.sendMessage(msg);
					}
					sleep(300);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void cancelRecrod(){
		if(!stopRecord()){
			return;
		}
		File file = new File(mFileName);
		file.delete();	
		Toast.makeText(this, "取消录音 "+mFileName, Toast.LENGTH_LONG).show();
		mMicPopupWindow.dismiss();
	}
	
	private void finishRecord(){
		Logger.d(TAG, "finishRecord");
		//stopRecord
		if(!stopRecord()){
			return;
		}
		//time
		long intervalTime = System.currentTimeMillis() - mStartTime;
		if (intervalTime < MIN_INTERVAL) {
			Toast.makeText(this, "太短了"+mFileName, Toast.LENGTH_LONG).show();
			File file = new File(mFileName);
			file.delete();
			return;
		}		
		//save
		Toast.makeText(this, "发送"+mFileName, Toast.LENGTH_LONG).show();
		sendVoiceMsg(intervalTime);
		mMicPopupWindow.dismiss();
	}
	
	private void sendVoiceMsg(long intervalTime){
		File file = new File(mFileName);
		ChatMsg chatMsg = new ChatMsg();
		chatMsg.setTname(to_username);
		chatMsg.setUname(from_username);
		chatMsg.setUrl(file.getAbsolutePath());
		chatMsg.setType(ConstantsMsgType.MSG_CHAT_VOICE);
		chatMsg.setCreateTS(Calendar.getInstance().getTimeInMillis());
		int voiceTS = (int)(intervalTime/1000);
		chatMsg.setVoiceTS(voiceTS);
		Logger.d(TAG, "sendVoiceMsg");
		String msgId = ConnectionManager.getInstance(this).sendVoiceFile(file, to_username,voiceTS);
		if(msgId != null){
			chatMsg.setCid(msgId);
			saveFileMsg(chatMsg);
		}		
	}
	
	private void saveFileMsg(final ChatMsg chatMsg){
		new Thread(){
			public void run() {
				ChatMsgDao.getInstance().save(chatMsg);
				android.os.Message msg = hander.obtainMessage();
				msg.what = UPDATE_ITEMA;
				msg.obj = chatMsg;
				hander.sendMessage(msg);
			};
		}.start();

	}
	
	
	
	private boolean stopRecord(){
		if(mFinished)
			return false;
		try{
			if(mRecorder != null){
				mRecorder.stop();
				mRecorder.release();
				mRecording = false;
				mFinished = true;
				mRecorder = null;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return true;
		
	}
	
	private void initData(){
		title.setText(to_username);
		chatAdapter = new ChatListAdapter(chatMsgs, this, to_username);
		listView.setAdapter(chatAdapter);
		initMoreGrid();
		initEmoji();
	}
	
	private boolean createVoiceFile(){
		mFileName = ConstantsApp.CHAT_TEMP_VOICE //getCacheDir().getAbsolutePath()"/"
		+ System.currentTimeMillis() + ".amr";		
		File file = new File(mFileName);
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	
	private void initMoreGrid(){
		ArrayList<HashMap<String, Object>> mFunctions = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> expressionMap = new HashMap<String, Object>();
		expressionMap.put("iconId", R.drawable.chat_function_expression_icon);
		expressionMap.put("name", "表情");
		HashMap<String, Object> takePictureMap = new HashMap<String, Object>();
		takePictureMap.put("iconId", R.drawable.chat_function_take_picture_icon);
		takePictureMap.put("name", "拍照");
		HashMap<String, Object> selectPictureMap = new HashMap<String, Object>();
		selectPictureMap.put("iconId", R.drawable.chat_function_select_picture_icon);
		selectPictureMap.put("name", "图片");
		HashMap<String, Object> locationMap = new HashMap<String, Object>();
		locationMap.put("iconId", R.drawable.chat_function_location_icon);
		locationMap.put("name", "位置");
		mFunctions.add(expressionMap);
		mFunctions.add(takePictureMap);
		mFunctions.add(selectPictureMap);
		mFunctions.add(locationMap);	
		moreGrid.setAdapter(new ChatMoreFunctionAdapter(this, mFunctions));
	}
	
	private void initEmoji(){
		ArrayList<EmojiInfo> list = new ArrayList<EmojiInfo>();
		EmojiInfo faceEmojiInfo = new EmojiInfo();
		faceEmojiInfo.setEmojiColumns(7);
		faceEmojiInfo.setEmojiData(ApplicationData.emojiData);
		faceEmojiInfo.setEmojiLines(3);
		faceEmojiInfo.setEmojiType(1);
		faceEmojiInfo.setTabIconId(R.drawable.emoji_icon);
		list.add(faceEmojiInfo);

		EmojiInfo animationInfo = new EmojiInfo();
		animationInfo.setEmojiColumns(4);
		animationInfo.setEmojiData(ApplicationData.animationData);
		animationInfo.setEmojiLines(2);
		animationInfo.setEmojiType(2);
		animationInfo.setTabIconId(R.drawable.xiao_mi_icon);
		list.add(animationInfo);

		emojiView.setEmojiData(list);
			
	}
	
	
	private void getHistoryMessage(){
		new Thread(){
			public void run() {
				lastTime = Calendar.getInstance().getTimeInMillis();
				List<ChatMsg> tmplist = getMessageByPage();
				android.os.Message msg = new android.os.Message();
				msg.what = UPDATE_ALLCHAT;
				msg.obj = tmplist;
				hander.sendMessage(msg);
			};
		}.start();
	}
	
	private List<ChatMsg> getMessageByPage(){
		List<ChatMsg> tmplist = null;
		try{
			tmplist = ChatMsgDao.getInstance().findAllChatMsg(from_username,to_username, count,lastTime);
			if(tmplist != null && !tmplist.isEmpty()){
				for(ChatMsg msg : tmplist){
					Log.i("luopeng", "tname:"+msg.getTname()+" uname:"+msg.getUname()+" content:"+msg.getContent());
				}
				lastTime = tmplist.get(0).getCreateTS();
			}		
		}catch(Exception e){
			e.printStackTrace();
		}
		return tmplist;
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(receiver);
		destryRecord();
		super.onDestroy();
	}
	
	private void destryRecord(){
		mRecording = false;
		mFinished = false;
		isCancel = false;
		if(mRecorder != null){
			mRecorder.stop();
			mRecorder.release();
			mRecorder = null;
		}
	}
	
	private void startChat(){
		chat = ConnectionManager.getInstance(this).createChat(to_username, new MessageListener(){

			@Override
			public void processMessage(Chat chat, Message message) {
				// TODO Auto-generated method stub
				Log.i("luopeng", "msg: "+message.getBody());
				//Toast.makeText(getApplicationContext(), message.getBody(), Toast.LENGTH_LONG).show();
				android.os.Message msg = new android.os.Message();
				msg.obj = "from:"+message.getFrom()+" content:"+message.getBody();
				msg.what = UPDATE_CHAT;
				hander.sendMessage(msg);
			}
			
		
			
		});
	}
	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.submit:
			if(chat != null){
				Message message =  new Message();
				Log.i("luopeng", "from :"+from_username+" to:"+to_username +" content:"+edit.getEditableText().toString());
				message.setFrom(from_username);
				message.setTo(to_username);
				message.setBody(edit.getEditableText().toString());
				message.setType(Message.Type.chat);
				message.addSubject("ctype", "text");
				message.addSubject("timestamp", String.valueOf(Calendar.getInstance().getTimeInMillis()));
				try {
					chat.sendMessage(message);
					edit.setText("");
				} catch (NotConnectedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			break;
		case R.id.send:
			sendTxtMsg();
			break;
		case R.id.sendfile:
			sendFile();
			break;
		case R.id.btn_more:
			clickMore();
			break;
		case R.id.input:
			Logger.d("luopeng", "click");
			clickInput();
			break;
		case R.id.chat_bottom_voice:
			clickVoice();
			break;
		case R.id.record:
			
			break;
		case R.id.chat_bottom_keyboard:
			clickKeyBoard();
			break;
		default:
			break;
		}
	}
	
	private void clickMore(){
		if(moreGrid.getVisibility() == View.VISIBLE){
			moreGrid.setVisibility(View.GONE);
		}else{
			moreGrid.setVisibility(View.VISIBLE);
			AndroidUtil.hideSoftInput(this);
		}		
		emojiView.setVisibility(View.GONE);
	}
	
	private void clickInput(){
		Logger.d(TAG, "clickInput");
		moreGrid.setVisibility(View.GONE);
		emojiView.setVisibility(View.GONE);
		/*
		AndroidUtil.showSoftInput(this);
	*/}
	
	private void sendTxtMsg(){
		if(chat != null){
			final ChatMsg chatMsg = new ChatMsg();
			chatMsg.setTname(to_username);
			chatMsg.setUname(from_username);
			chatMsg.setContent(edit.getEditableText().toString());
			chatMsg.setType(ConstantsMsgType.MSG_CHAT_TXT);
			chatMsg.setCreateTS(Calendar.getInstance().getTimeInMillis());
			new Thread(){
				public void run() {
					Message message = ChatMsgParse.getXMPPMsg(chatMsg);
					Logger.d("luopeng", "sendTxtMsg: "+ message);
					try {
						chat.sendMessage(message);
						Logger.d(TAG, "messageId:"+message.getPacketID());
						chatMsg.setCid(message.getPacketID());
						long result = ChatMsgDao.getInstance().save(chatMsg);
						if(result != -1 ){
							android.os.Message handlerMsg = hander.obtainMessage();
							handlerMsg.what = UPDATE_INPUT;
							handlerMsg.obj = chatMsg;
							hander.sendMessage(handlerMsg);
						}
					} catch (NotConnectedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				};
			}.start();
		}		
	}
	
	
	@SuppressLint("SdCardPath")
	public void sendFile(){
		File file = new File("/sdcard/op.jpg");//videoEngine.log
		ConnectionManager.getInstance(getApplicationContext()).sendImgFile(file,to_username);
	}
	
	public void clickVoice(){
		AndroidUtil.hideSoftInput(this);
		emojiView.setVisibility(View.GONE);
		moreGrid.setVisibility(View.GONE);
		edit.setVisibility(View.INVISIBLE);
		btn_voice.setVisibility(View.INVISIBLE);
		btn_record.setVisibility(View.VISIBLE);
		btn_keyboard.setVisibility(View.VISIBLE);
	}
	
	public void clickKeyBoard(){
		edit.setVisibility(View.VISIBLE);
		btn_record.setVisibility(View.INVISIBLE);
		btn_keyboard.setVisibility(View.INVISIBLE);
		btn_voice.setVisibility(View.VISIBLE);
	}
	
	private OutputStream outputStream;
//	public synchronized void sendFile(final File file, final String description)
//			throws SmackException {
//		if (file == null || !file.exists() || !file.canRead()) {
//			throw new IllegalArgumentException("Could not read file");
//		} else {
////			setFileInfo(file.getAbsolutePath(), file.getName(), file.length());
//		}
//
//		Thread transferThread = new Thread(new Runnable() {
//			public void run() {
//				try {
//					outputStream = negotiateStream(file.getName(), file
//							.length(), description);
//				} catch (XMPPErrorException e) {
//					handleXMPPException(e);
//					return;
//				}
//                catch (Exception e) {
//                    setException(e);
//                }
//				if (outputStream == null) {
//					return;
//				}
//
//                if (!updateStatus(Status.negotiated, Status.in_progress)) {
//					return;
//				}
//
//				InputStream inputStream = null;
//				try {
//					inputStream = new FileInputStream(file);
//					writeToStream(inputStream, outputStream);
//				} catch (FileNotFoundException e) {
//					setStatus(FileTransfer.Status.error);
//					setError(Error.bad_file);
//					setException(e);
//				} catch (SmackException e) {
//					setStatus(FileTransfer.Status.error);
//					setException(e);
//				} finally {
//					try {
//						if (inputStream != null) {
//							inputStream.close();
//						}
//
//						outputStream.flush();
//						outputStream.close();
//					} catch (IOException e) {
//                        /* Do Nothing */
//					}
//				}
//                updateStatus(Status.in_progress, FileTransfer.Status.complete);
//				}
//
//		}, "File Transfer " + streamID);
//		transferThread.start();
//	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		switch(position){
		case 0:
			chooseEmoji();
			break;
		case 2:
			pickPhoto();
			break;
		}
	}
	
	private void chooseEmoji(){
		moreGrid.setVisibility(View.GONE);
		emojiView.setVisibility(View.VISIBLE);
		AndroidUtil.hideSoftInput(this);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQ_TAKE_PHOTO && mTakePhotoUri != null){		
			Logger.d("luopeng", "onActivityResult");
		}
		Uri uri = null;
		switch (requestCode) {
		case REQ_PICK_PHOTO:
			if (data != null) {
				uri = data.getData();
			} else if (mTakePhotoUri != null)
				uri = mTakePhotoUri;
			
			if (uri == null) {
				return;
			}
//			mTakePhotoUri = null;
			sendImg(uri, to_username);
//			sendFile();
			break;
		}
		
	}
	
	public void sendImg(final Uri uri, String username){
		File file = FileUtil.getFileFromUri(uri, this);
		if(file == null){
			return;
		}
		ChatMsg chatMsg = new ChatMsg();
		chatMsg.setTname(to_username);
		chatMsg.setUname(from_username);
		chatMsg.setUrl("file:///"+file.getAbsolutePath());
		chatMsg.setType(ConstantsMsgType.MSG_CHAT_IMG);
		chatMsg.setCreateTS(Calendar.getInstance().getTimeInMillis());			
		String msgId = ConnectionManager.getInstance(this).sendImgFile(file, username);
		if(msgId != null){
			chatMsg.setCid(msgId);
			saveFileMsg(chatMsg);
		}
	}
	
	
	
	private void takePhoto(){
		try {
			//拍照我们用Action为MediaStore.ACTION_IMAGE_CAPTURE，
			//有些人使用其他的Action但我发现在有些机子中会出问题，所以优先选择这个
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
			mTakePhotoUri = getTempURI();
			intent.putExtra(MediaStore.EXTRA_OUTPUT, mTakePhotoUri);
			//intent.putExtra("return-data", true);
			startActivityForResult(intent, REQ_TAKE_PHOTO);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Uri getTempURI() {		
	    // 创建媒体文件名
		// MUST check mSdcardUserDir
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()); 
	    File file = new File("/sdcard/luopeng/temp/" + 
	    		File.separator + "IMG_"+ timeStamp + ".tmp");
	      
	    Uri uri = Uri.fromFile(file);
	    Logger.v("luopeng", "Create URI: " + uri);
	    return uri; 
	}	
	
	// 请求Gallery程序
	private void doPickPhotoFromGallery() {
		try {
			final Intent intent = getPhotoPickIntent();
			startActivityForResult(intent, 2);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, "photoPickerNotFound", Toast.LENGTH_LONG).show();
		}
	}

	// // 封装请求Gallery的intent
	private static Intent getPhotoPickIntent() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");
		return intent;
	}	
	
    private void pickPhoto() {
		try {
			mTakePhotoUri = null;
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			intent.putExtra("return-data", true);
			startActivityForResult(intent, REQ_PICK_PHOTO);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	@Override
	public Object refreshing() {
		// TODO Auto-generated method stub
		Logger.d("luopeng", "refreshing");
		List<ChatMsg> chatMsg = getMessageByPage();
		return chatMsg;
	}

	@Override
	public void refreshed(Object obj) {
		// TODO Auto-generated method stub
		Logger.d("luopeng", "refreshed");
		try{
			if(obj instanceof List){
				synchronized (chatMsgs) {
					List<ChatMsg> newList = (List<ChatMsg>)obj;
					List<ChatMsg> tempList = new ArrayList<ChatMsg>();
					tempList.addAll(chatMsgs);
					chatMsgs.clear();
					chatMsgs.addAll(newList);
					chatMsgs.addAll(tempList);
					chatAdapter.notifyDataSetChanged();
					listView.setSelection(newList.size());
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void more() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void autoMore() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getEmojiContext(int MsgType, String MsgContext,
			String sourceName, int coverId) {
		// TODO Auto-generated method stub
		String precontent = edit.getEditableText().toString();
		String content = precontent+MsgContext;
//		edit.setText(context);
		TextAutoLink.addEmotionsSpanForInput(this, content, edit);
		edit.setSelection(content.length());
		Logger.d(TAG, "MsgType:"+MsgType+" MsgContext:"+MsgContext+" sourceName:"+sourceName+" coverId:"+coverId);
	}

	@Override
	public void deleteFuntion() {
		// TODO Auto-generated method stub
		
	} 
	
}
