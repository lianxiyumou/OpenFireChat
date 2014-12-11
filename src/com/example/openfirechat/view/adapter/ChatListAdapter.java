package com.example.openfirechat.view.adapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.openfirechat.R;
import com.example.openfirechat.comm.Logger;
import com.example.openfirechat.constants.ConstantsMsgType;
import com.example.openfirechat.model.ChatMsg;
import com.example.openfirechat.util.TextAutoLink;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ChatListAdapter extends BaseAdapter {

	private List<ChatMsg> msglist;
	private LayoutInflater mInflater;
	private Context context;
	private String tName;
	public MediaPlayer mMediaPalyer;
	
	private static final String TAG = "luopeng";
	
	
	private ImageView preVoiceImg = null;
	
	public ChatListAdapter(List<ChatMsg> msglist,Context context,String tName){
		this.msglist = msglist;
		this.context = context;
		this.tName = tName;
		mMediaPalyer = new MediaPlayer();
	}
	
	
	public List<ChatMsg> getMsglist() {
		return msglist;
	}

	public void setMsglist(List<ChatMsg> msglist) {
		this.msglist = msglist;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(msglist == null)
			return 0;
		return msglist.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if(msglist != null && msglist.size() > position)
			return msglist.get(position);
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
    	if (mInflater == null)
    		mInflater = LayoutInflater.from(context);
    	ChatMsg chatMsg = msglist.get(position);
        View view = convertView;
        ViewHolder vh =  null;
        if(view != null)
        	vh = (ViewHolder) view.getTag();
        if(chatMsg == null)
        	return view;
        if(view == null || vh == null || ( !chatMsg.getUname().equals(vh.uname))
        	|| vh.type != chatMsg.getType()){
        	if(tName.equals(chatMsg.getUname()) ){
        		if(chatMsg.getType() == ConstantsMsgType.MSG_CHAT_TXT){
        			view = mInflater.inflate(R.layout.chat_item_txt_from, null);
        		}else if(chatMsg.getType() == ConstantsMsgType.MSG_CHAT_IMG){
        			view = mInflater.inflate(R.layout.chat_item_img_from, null);
        		}else if(chatMsg.getType() == ConstantsMsgType.MSG_CHAT_VOICE){
        			view = mInflater.inflate(R.layout.chat_item_voice_from, null);
        		}
        	}else{
        		if(chatMsg.getType() == ConstantsMsgType.MSG_CHAT_TXT){
        			view = mInflater.inflate(R.layout.chat_item_txt_to, null);
        		}else if(chatMsg.getType() == ConstantsMsgType.MSG_CHAT_IMG){
        			view = mInflater.inflate(R.layout.chat_item_img_to, null);
        		}else if(chatMsg.getType() == ConstantsMsgType.MSG_CHAT_VOICE){
        			view = mInflater.inflate(R.layout.chat_item_voice_to, null);
        		}
        	}
        	vh = new ViewHolder();
        	vh.chat_time = (TextView) view.findViewById(R.id.time);
        	if(chatMsg.getType() == ConstantsMsgType.MSG_CHAT_TXT){
        		vh.chat_txt = (TextView) view.findViewById(R.id.txt);
        	}else if(chatMsg.getType() == ConstantsMsgType.MSG_CHAT_IMG){
        		vh.img = (ImageView) view.findViewById(R.id.image);
        	}else if(chatMsg.getType() == ConstantsMsgType.MSG_CHAT_VOICE){
        		vh.voice_layout = (LinearLayout) view.findViewById(R.id.voice_layout);
        		vh.voice_time = (TextView) view.findViewById(R.id.voice_time);
        		vh.voice_anim = (ImageView)view.findViewById(R.id.voice_anim);
        	}
        	view.setTag(vh);
        }
        
//        Logger.d("luopeng", "vh.uid:"+vh.uid+" cuid:"+chatMsg.getUid()+" targetID:"+targetID);
        vh.uname = chatMsg.getUname();
        vh.type = chatMsg.getType();
        vh.chat_time.setText(chatMsg.getTime());
        if(chatMsg.getType() == ConstantsMsgType.MSG_CHAT_TXT){
        	TextAutoLink.addEmotionsSpanForChat(context, chatMsg.getContent(), vh.chat_txt);
        }else  if(chatMsg.getType() == ConstantsMsgType.MSG_CHAT_IMG){
        	showImage(vh.img,chatMsg.getUrl());
        }else  if(chatMsg.getType() == ConstantsMsgType.MSG_CHAT_VOICE){
        	setVoice(vh,chatMsg);
        }
        
		return view;
	}
	
	private void showImage(ImageView imgView , String url){
		try{
			ImageLoader mImageLoader = ImageLoader.getInstance();
			DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
			DisplayImageOptions options = builder.cacheInMemory(true)
					.cacheOnDisc(true)
					.considerExifParams(true)
					.displayer(new RoundedBitmapDisplayer((int)4))
					.build();
//			mImageLoader.loadImageSync(url, options);
//			Uri uri = Uri.fromFile(new File(url));
			mImageLoader.displayImage(url, imgView, options);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void setVoice(final ViewHolder vh, final ChatMsg chatMsg){
		final boolean isFriendMsg = chatMsg.getUname() == tName ;
		recoverPlaying(chatMsg,vh.voice_anim);
		if(vh.voice_time != null){
			vh.voice_time.setText(chatMsg.getVoiceTS()+" S");
		}else{
			Logger.d(TAG, "setVoice id"+chatMsg.getCid());
		}
		vh.voice_layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Logger.d("luopeng", "play url:"+ chatMsg.getUrl());
				playVoice(chatMsg.getUrl(),vh.voice_anim, isFriendMsg);
			}
		});
		Map<String,Object> info = new HashMap<String,Object>();
		info.put("cid", chatMsg.getCid());
		info.put("isfriend", isFriendMsg);
		vh.voice_anim.setTag(info);
	}
	
	private void recoverPlaying(ChatMsg chatMsg,ImageView voiceImg){
		if(preVoiceImg == null){
			return;
		}
		if(preVoiceImg.getTag() == null || !(preVoiceImg.getTag() instanceof Map)){
			return;
		}
		Map<String,Object> info = (Map<String, Object>) preVoiceImg.getTag();
		if(info != null && !info.isEmpty()){
			String preChatMsgId = info.get("cid").toString();
			if(mMediaPalyer.isPlaying() && chatMsg.getCid().equals(preChatMsgId)){
				boolean isFriendMsg = (boolean) info.get("isfriend");
				int startAnimResId = isFriendMsg ? R.anim.your_sound_anim : R.anim.my_sound_anim ;
				startVoiceAnim(voiceImg, startAnimResId);
			}
		}
	}
	
	private void playVoice(String url, final ImageView voicAnim, boolean isFriendMsg){
		boolean isLocal = true;
		File file =  new File(url);
		if(!file.exists()){
			isLocal = false;
		}else{
			isLocal = true;
		}
		try {
			final int startAnimResId = isFriendMsg ? R.anim.your_sound_anim : R.anim.my_sound_anim ;
			final int resetIconResId = isFriendMsg ? R.drawable.your_voice_3 : R.drawable.my_voice_3;
			
			stopPlay(resetIconResId);
			mMediaPalyer.reset();
			mMediaPalyer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			if(isLocal){
				mMediaPalyer.setDataSource((new FileInputStream(file)).getFD());
			}else{
				mMediaPalyer.setDataSource(url);
			}
			
			mMediaPalyer.prepareAsync();
			mMediaPalyer.setOnPreparedListener(new OnPreparedListener() {
				
				@Override
				public void onPrepared(MediaPlayer mp) {
					// TODO Auto-generated method stub
					mp.start();
					startVoiceAnim(voicAnim,startAnimResId);
				}
			});
			mMediaPalyer.setOnCompletionListener(new OnCompletionListener() {
				
				@Override
				public void onCompletion(MediaPlayer mp) {
					// TODO Auto-generated method stub
					Logger.d("luopeng", "playing voice is over");
					stopVoiceAnim(resetIconResId);
				}
			});
			
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	private void stopPlay(int resetIconResId){
		//stop pre voice
		if(mMediaPalyer.isPlaying()){
			mMediaPalyer.stop();
			Logger.d(TAG, "stopPlay");
			stopVoiceAnim(resetIconResId);
		}		
	}
	
	private void startVoiceAnim(ImageView voiceImg, int animResId){
		voiceImg.setBackgroundResource(animResId);
		AnimationDrawable draw = (AnimationDrawable) voiceImg.getBackground();
		preVoiceImg = voiceImg;		
		if(draw.isRunning()){
			Logger.d(TAG, "startVoiceAnim draw.isRunning");
			return;
		}
		draw.setOneShot(false);
		draw.start();
		Logger.d(TAG, "startVoiceAnim draw:"+draw);
	}
	
	private void stopVoiceAnim(int resetIconResId){
		if(preVoiceImg == null){
			return;
		}
		if(preVoiceImg.getBackground() instanceof AnimationDrawable){
			AnimationDrawable draw = (AnimationDrawable) preVoiceImg.getBackground();
			if(draw!=null && draw.isRunning()){
				draw.stop();
				preVoiceImg.setBackgroundResource(resetIconResId);
				Logger.d(TAG, "stopVoiceAnim draw:"+draw);
			}
		}
	}
	
	
	class ViewHolder{
		String uname;//from who 
		int type;
		String voiceUrl;
		LinearLayout voice_layout;
		ImageView item_bg;
		TextView chat_txt;
		TextView chat_time;
		ImageView img;
		TextView voice_time;
		ImageView voice_anim;
		
	}

}
