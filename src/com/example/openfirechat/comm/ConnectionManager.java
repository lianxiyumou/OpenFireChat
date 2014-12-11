package com.example.openfirechat.comm;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.harmony.javax.security.sasl.SaslException;
import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.SmackAndroid;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.SmackException.NotLoggedInException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Type;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.packet.RosterPacket.ItemStatus;
import org.jivesoftware.smack.packet.RosterPacket.ItemType;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferNegotiator;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.offline.OfflineMessageManager;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.ReportedData.Row;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.smackx.vcardtemp.provider.VCardProvider;
import org.jivesoftware.smackx.xdata.Form;

import com.example.openfirechat.constants.ConstantsAction;
import com.example.openfirechat.constants.ConstantsApp;
import com.example.openfirechat.constants.ConstantsMsgType;
import com.example.openfirechat.db.ChatMsgDao;
import com.example.openfirechat.http.BitmapUtil;
import com.example.openfirechat.http.HttpParamUtil;
import com.example.openfirechat.http.RequestParams;
import com.example.openfirechat.http.my.MyHttpClient;
import com.example.openfirechat.http.my.MyHttpResponseHandler;
import com.example.openfirechat.model.ChatMsg;
import com.example.openfirechat.model.User;
import com.example.openfirechat.util.FileUtil;
import com.example.openfirechat.util.MMap;
import com.example.openfirechat.util.XMLUtil;
import com.example.openfirechat.util.XMPPUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


public class ConnectionManager {

	
	private static XMPPConnection connection;
	private static ConnectionManager connectionManager;
	private static AccountManager accountManager;
	private static ChatManager chatManager;
	private FileTransferManager transfer;
	private Context context;
	
	private String hostName = "120.24.58.41";//"pc-luopeng";//
	private String hostIP = "120.24.58.41";//"192.168.80.109";//
	private static final String TAG = "luopeng";
	private ConnectionManager(){
	}
	
	public static ConnectionManager getInstance(Context context){
		if(connectionManager == null){
			connectionManager = new ConnectionManager();
			connectionManager.init(context);
		}
		return connectionManager;
	}
	
	public String sendImgFile(final File file, String username){
		return sendFile(file,username,0,ConstantsMsgType.MSG_CHAT_IMG);
	}
	
	public String sendVoiceFile(final File file, String username, int intervalTime){
		return sendFile(file,username,intervalTime,ConstantsMsgType.MSG_CHAT_VOICE);
	}	
	
	public String sendFile(final File file, String username, final int intervalTime, final int type){
		if(!connection.isConnected()){
			Logger.d("luopeng", "sendFile connection is closed");
			return null;
		}
		if(transfer == null)
			return null;
		final String userJID = username+"@"+hostName+"/Smack";
		Log.i("luopeng", "sendFile to "+userJID);
		final OutgoingFileTransfer out = transfer.createOutgoingFileTransfer(userJID);
		if (file == null || !file.exists() || !file.canRead()){
			return null;
		}
		try {
			out.sendFile(file, String.valueOf(intervalTime));
			new Thread(){
				public void run() {
					while(!out.isDone()){
						if(!connection.isConnected()){
							Logger.d("luopeng", "connetion is closed");
							break;
						}
//						Logger.i("luopeng", "send file:"+out.getStatus()+"  progress:"+out.getProgress());
//						Logger.d("luopeng", " isConnected"+connection.isConnected());
						if(out.getStatus() == FileTransfer.Status.error){
							Log.i("luopeng", "error :"+out.getError());
							Log.i("luopeng", "exception :"+out.getException());
							if(out.getException() != null){
								Logger.d(TAG, out.getException().getMessage());
								if("service-unavailable".equals(out.getException().getMessage())){
									uploadOffLine(userJID,file,type,intervalTime);
									Log.i("luopeng", "friend is offline ");
								}
							}
							break;
						}
					}
					Logger.d(TAG, "status:"+out.getStatus() );
					if(out.getStatus() == FileTransfer.Status.complete){
						Log.i("luopeng", "send file is done");
					}else{
						Log.i("luopeng", "send file is failed");
					}
					
				};
			}.start();
			Toast.makeText(context, "send "+connection.isConnected(), Toast.LENGTH_LONG).show();
		} catch (SmackException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return out.getStreamID();
		
	}
	
	public void sendUploadFileUrl(String userJID,String fileurl,int type,int intervalTime){
		Message msg = new Message();
		msg.setTo(userJID);
		msg.setBody(fileurl);
		msg.setType(Message.Type.chat);
		if(type == ConstantsMsgType.MSG_CHAT_IMG){
			msg.addSubject("ctype", "img");
		}else{
			msg.addSubject("ctype", "voice");
			msg.addSubject("voiceTS", String.valueOf(intervalTime));
		}
		msg.addSubject("timestamp", String.valueOf(System.currentTimeMillis()));
		Log.i("luopeng", "sendUploadFileUrl msg:"+msg);
		try {
			connection.sendPacket(msg);
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
	
	
	public void uploadOffLine(final String userJID,File file,final int type, final int intervalTime){
		Logger.d(TAG, "ConnectionManager uploadOffLine name:"+file.getName()+" UsableSpace:"+file.getUsableSpace()+" totalsapce:"+file.getTotalSpace()+" freesapce:"+file.getFreeSpace());
    	MyHttpClient httpClient = new MyHttpClient();
    	RequestParams params = HttpParamUtil.getInstance();
    	Uri uri = Uri.fromFile(file);
    	Logger.d(TAG, "Start DecodeStream");
    	InputStream is = null;
    	if(type == ConstantsMsgType.MSG_CHAT_IMG){
    		is = BitmapUtil.decodeStreamFromUri(context, uri, 480, 800);
    	}else if(type ==  ConstantsMsgType.MSG_CHAT_VOICE){
    		try {
				is = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	if(is == null)
    		return;
    	Logger.d(TAG, "End DecodeStream");
    	Logger.d(TAG, "fileName: "+file.getName());
    	params.put("IMAGES", is,file.getName());
    	httpClient.post("chat/uploadFile.do",params, new MyHttpResponseHandler(context,true){
    		
    		@Override
    		public void onSuccessGson(MMap content, Gson gson) {
    			// TODO Auto-generated method stub
    			Logger.d(TAG, "query end");
    			try{
    				Logger.d(TAG, "onSuccessJson error:"+content.getInt("error"));
    				Logger.d(TAG, "onSuccessJson url:"+content.get("url"));
    				if(content.getInt("error") == 0){
    					sendUploadFileUrl(userJID,content.get("url").toString(),type,intervalTime);
    				}else{
    					Logger.d(TAG, "onSuccessJson upload fail");
    				}
    			}catch(Exception e){
    				e.printStackTrace();
    			}    			
    		}
    		
    		@Override
    		public void onFailure(Throwable error, String content) {
    			// TODO Auto-generated method stub
    			super.onFailure(error, content);
    			Logger.d("luopeng", "request fail:"+error.toString()+" content:"+content);
    		}
    		
    	});
    	
    }
	
	public void sendSelfIQ(){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("name", "luopeng");
		map.put("url", "fastdfs");
        JabberRPC jp = new JabberRPC(map); 
        jp.setType(org.jivesoftware.smack.packet.IQ.Type.SET); 
        jp.setTo("pc-luopeng"); 
        try {
			connection.sendPacket(jp);
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public void getGroup(){
		Roster roster = connection.getRoster();
		if(roster == null){
			return;
		}
		Log.i("luopeng", "groupcount: "+roster.getGroupCount());
		Collection<RosterGroup> groups = roster.getGroups();
		Collection<RosterEntry> entries = roster.getEntries();
		Log.i("luopeng", "friendsize: "+entries.size());
	}
	
	public boolean addFriend(String username, String friendName){
		if(connection == null)
			return false;
		try {
			String userJid = friendName+"@"+hostName+"/Smack";
			Log.i("luopeng", "connect addFriend  username:"+username+" friendName:"+friendName);
			connection.getRoster().createEntry(userJid, friendName, null);
			saveAddFriendRequest(friendName);
			return true;
		} catch (NotLoggedInException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMPPErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	private void saveAddFriendRequest(String tname){
		ChatMsg chatMsg = new ChatMsg();
		chatMsg.setCid(Packet.nextID());
//		chatMsg.setContent("向 "+tname+" 发送好友请求");
		chatMsg.setUname(ConstantsApp.USERNAME);
		chatMsg.setTname(tname);
		chatMsg.setCreateTS(System.currentTimeMillis());
		chatMsg.setType(ConstantsMsgType.MSG_CHAT_REQUEST);
		long result = ChatMsgDao.getInstance().save(chatMsg);	
//		if(result != -1){
//			Bundle bundle = new Bundle();
//			bundle.putSerializable("chat", chatMsg);
//			MessageCenter.sentNotification(ConstantsAction.ACTION_ADD_FRIEND,bundle);
//		}
	}
	
	public Collection<RosterEntry> getAllFriend(){
		Collection<RosterEntry> allRoster = connection.getRoster().getEntries();
		Iterator<RosterEntry> iter =  allRoster.iterator();
		Logger.d(TAG, "AllfriendSize: "+allRoster.size());
		List<User> friends = new ArrayList<User>();
		List<User> cares = new ArrayList<User>();
		List<User> follows = new ArrayList<User>();
		while(iter.hasNext()){
			RosterEntry rosterEntry = iter.next();
			User user = XMPPUtil.getUserFromRosterEntry(rosterEntry);
			Logger.d(TAG, "user:"+rosterEntry.getUser()+" name:"+rosterEntry.getName()+" type:"+rosterEntry.getType()+" status:"+rosterEntry.getStatus());
			if(ItemType.both == rosterEntry.getType()){
				VCard vcard = getUserVCard(user.getJid());
				XMPPUtil.initUserFromVCard(vcard, user);
				friends.add(user);
			}else if(ItemType.from == rosterEntry.getType()){
				cares.add(user);
			}else if(ItemType.none == rosterEntry.getType() && ItemStatus.subscribe == rosterEntry.getStatus()){
				follows.add(user);
			}
		}
		Logger.d(TAG, "friends:"+friends.size());
		Logger.d(TAG, "cares:"+cares.size());
		Logger.d(TAG, "follows:"+follows.size());
		return allRoster;
	}
	
	public Map<String,List<User>> getRelationships(){
		Map<String,List<User>> relationships = new HashMap<String,List<User>>();
		try{
			Collection<RosterEntry> allRoster = connection.getRoster().getEntries();
			Iterator<RosterEntry> iter =  allRoster.iterator();
			Logger.d(TAG, "aLLfriendSize: "+allRoster.size());
			List<User> friends = new ArrayList<User>();
			List<User> cares = new ArrayList<User>();
			List<User> follows = new ArrayList<User>();
			while(iter.hasNext()){
				RosterEntry rosterEntry = iter.next();
				User user = XMPPUtil.getUserFromRosterEntry(rosterEntry);
				Logger.d(TAG, "user:"+rosterEntry.getUser()+" name:"+rosterEntry.getName()+" type:"+rosterEntry.getType()+" status:"+rosterEntry.getStatus());
				if(ItemType.both == rosterEntry.getType()){
					VCard vcard = getUserVCard(user.getJid());
					XMPPUtil.initUserFromVCard(vcard, user);
					friends.add(user);
					relationships.put("friend", friends);
				}else if(ItemType.to == rosterEntry.getType()){
					VCard vcard = getUserVCard(user.getJid());
					XMPPUtil.initUserFromVCard(vcard, user);				
					cares.add(user);
					relationships.put("care", cares);
				}else if(ItemType.from == rosterEntry.getType()){
					VCard vcard = getUserVCard(user.getJid());
					XMPPUtil.initUserFromVCard(vcard, user);				
					follows.add(user);
					relationships.put("follow", follows);
				}
			}
			Logger.d(TAG, "friends:"+friends.size());
			Logger.d(TAG, "cares:"+cares.size());
			Logger.d(TAG, "follows:"+follows.size());
		}catch(Exception e){
			e.printStackTrace();
		}
		return relationships;
	}	
	
	public List<User> getFriends(){
		Map<String,List<User>> relations =  getRelationships();
		return relations.get("friend");
	}
	
	public void setChatMsg(ChatManagerListener listener){
		chatManager.addChatListener(listener);
	}
	
	public XMPPConnection init(Context context){
		if(connection == null){
			Logger.d(TAG, "XMPPConnection init");
			this.context = context;
			SmackAndroid.init(context);
			ConnectionConfiguration config = new ConnectionConfiguration(hostIP,5222);
			config.setReconnectionAllowed(true);
			config.setSendPresence(false);
			config.setSecurityMode(SecurityMode.disabled);
			connection = new org.jivesoftware.smack.tcp.XMPPTCPConnection(config);
		}
		return connection;
	}
	
	public void connect(){
		if(connection == null){
			Logger.e(TAG, "connection is null in connect()");
			return;
		}
		try {
			synchronized (connection) {
				if(connection.isConnected()){
					Logger.d(TAG, "already connect");
					return;
				}
				connection.connect();
				accountManager = AccountManager.getInstance(connection);
				chatManager = ChatManager.getInstanceFor(connection);
				transfer = new FileTransferManager(connection);
				org.jivesoftware.smack.provider.ProviderManager.addIQProvider("query", "jabber:iq:rpc", new JabberRPCProvider()); 			
				addFileListener();
				addChatListener();
				addRosterListener();
				addPacketListener();
				addPacketSendingListner();
				Log.i("luopeng", "connect openfire success");
			}
		} catch (SmackException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void getOfflineMsgs(){
		Logger.d(TAG, "getOfflineMsgs start");
		OfflineMessageManager offlineManager = new OfflineMessageManager(connection); 
		List<String> offFiles = new ArrayList<String>();  
        try {  
            List<Message> list = offlineManager.getMessages();  
            Logger.d(TAG, "getOfflineMsgs end");
            Log.i("luopeng", "offmsg count: " + offlineManager.getMessageCount());
  
            Map<String,ArrayList<Message>> offlineMsgs = new HashMap<String,ArrayList<Message>>();  
            for(int i=0; i< list.size(); i++){  
                org.jivesoftware.smack.packet.Message message = list.get(i);  
                Log.i("luopeng", "Received chat offline from:" + message.getFrom()  
                        + ", messageBody: " + message.getBody());
                Log.i("luopeng", "offline msg type:"+message.getType()+" sub:"+message.getSubject());
                if("file".equals(message.getSubject())){
                	offFiles.add(message.getBody());
                }
                
                String fromUser = message.getFrom().split("/")[0];  
  
                if(offlineMsgs.containsKey(fromUser))  
                {  
                    offlineMsgs.get(fromUser).add(message);  
                }else{  
                    ArrayList<Message> temp = new ArrayList<Message>();  
                    temp.add(message);  
                    offlineMsgs.put(fromUser, temp);  
                }  
            }
            Logger.d(TAG, "deleteMessages start");  
            offlineManager.deleteMessages();
            Logger.d(TAG, "deleteMessages end");
//            deleteOffFile(offFiles);
            setPresence(0);
        } catch (Exception e) {  
            e.printStackTrace();  
        }  		
	}
	
	private void deleteOffFile(List<String> offFiles){
		for(int i=0; i<offFiles.size(); i++){
			MyHttpClient client = new MyHttpClient();
			RequestParams params = HttpParamUtil.getInstance();
			params.put("url", offFiles.get(i));
			Logger.d(TAG, "deleteOffFile url:"+offFiles.get(i));
			client.post("chat/deleteFile.do", params,new MyHttpResponseHandler(context, true){
				@Override
				protected void onSuccessGson(MMap content, Gson gson) {
					// TODO Auto-generated method stub
					super.onSuccessGson(content, gson);
    				Logger.d(TAG, "onSuccessJson error:"+content.getInt("error"));
				}
				
				@Override
				public void onFailure(Throwable error, String content) {
					// TODO Auto-generated method stub
					super.onFailure(error, content);
					Logger.d(TAG, "chat/deleteFile.do failed");
				}
				
				@Override
				public void onFailure(Throwable error) {
					// TODO Auto-generated method stub
					super.onFailure(error);
					Logger.d(TAG, "chat/deleteFile.do failed2");
				}
				
			});
		}
	}
	
	private void addPacketListener(){
		connection.addPacketListener(new PacketListener() {
			@Override
			public void processPacket(Packet packet)
					throws NotConnectedException {
				if (packet instanceof org.jivesoftware.smack.packet.IQ) { 
                    org.jivesoftware.smack.packet.IQ iq = (org.jivesoftware.smack.packet.IQ) packet;
                    if(iq != null && iq.getChildElementXML() != null){
                    	Logger.d(TAG, "addPacketListener iq:"+iq.getChildElementXML().toString());
                    }
				}else if(packet instanceof Presence){
					Presence presence = (Presence) packet;
					Logger.d(TAG, "presence: "+presence+" from:"+presence.getFrom()+" type:"+presence.getType()+" status:"+presence.getStatus()+" mode:"+presence.getMode());
					MessageCenter.messagecCategory(presence);
//					Type type = presence.getType();
//					if(type == Type.subscribe){
//						Logger.d(TAG, "recevier a request to be friend");
////						argree(presence.getFrom());
//					}else if(type == Type.subscribed){
//						Logger.d(TAG, "your friend agree to add you");
//					}else if(type == Type.unsubscribe){
//						Logger.d(TAG, "you are removed by your friend");
//					}else if(type == Type.unsubscribed){
//						Logger.d(TAG, "your friend refuse to add you");
//					}else if(type == Type.error){
//						Logger.e(TAG, "The presence packet contains an error message");
//					}
				}
			}
			
			
		}, 
			new org.jivesoftware.smack.filter.PacketFilter() { 
		        public boolean accept(Packet arg0) { 
		                return true; 
		        } 
			}
		); 
	}
	

	
	
	private void addPacketSendingListner(){
		connection.addPacketSendingListener(new PacketListener() {
			
			@Override
			public void processPacket(Packet packet) throws NotConnectedException {
				// TODO Auto-generated method stub
				Logger.d(TAG, "PacketSending");
				if(packet instanceof Message){
					Message msg = (Message)packet;
//					msg.get
				}else if (packet instanceof org.jivesoftware.smack.packet.IQ) { 
                    org.jivesoftware.smack.packet.IQ iq = (org.jivesoftware.smack.packet.IQ) packet;
                    if(iq != null && iq.getChildElementXML() != null){
                    	Logger.d(TAG, "addPacketSendingListner iq:"+iq.getChildElementXML().toString());
                    }
				}
			}
		}, 			
		new org.jivesoftware.smack.filter.PacketFilter() { 
	        public boolean accept(Packet arg0) { 
                return true; 
	        }
		});	
	}
	
	/**
	 * 
	 * @param to: tname
	 */
	public void agree(String to){
        Presence presencePacket = new Presence(Presence.Type.subscribe);
        presencePacket.setStatus("agree");
        presencePacket.setTo(to+"@"+hostName+"/Smack");
        try {
			connection.sendPacket(presencePacket);
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public void unsubscribe(String to){
		Presence presence = new Presence(Type.subscribed);
		presence.setTo(to);
		try {
			connection.sendPacket(presence);
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
	private void addFileListener(){
		Logger.d(TAG, "addFileListener");
		FileTransferNegotiator.setServiceEnabled(connection, true);
		transfer.addFileTransferListener(new FileTransferListener (){

			@Override
			public void fileTransferRequest(final FileTransferRequest request) {
				// TODO Auto-generated method stub
				Log.i("luopeng", "receive file");
				final IncomingFileTransfer inTransfer = request.accept();
				final String fileName = request.getFileName();  
				long length = request.getFileSize();	
				final String fromUser = request.getRequestor().split("/")[0];  
				String id = request.getStreamID();
				Log.i("luopeng", "type:"+request.getMimeType()+" name:"+fileName+" fromUser:"+fromUser+" id:"+id+" length:"+length);
				StringBuffer dir = new StringBuffer();
				String type = request.getMimeType();
				Logger.d(TAG, "description:"+request.getDescription());
				ChatMsg chatMsg = new ChatMsg();
				String pre = "";
				if(type.indexOf("image") == 0){
					pre = "file:///";
					dir.append(ConstantsApp.CHAT_TEMP_IMG);
					chatMsg.setType(ConstantsMsgType.MSG_CHAT_IMG);
				}else if(type.indexOf("audio") == 0){
					dir.append(ConstantsApp.CHAT_TEMP_VOICE);
					chatMsg.setType(ConstantsMsgType.MSG_CHAT_VOICE);
					chatMsg.setVoiceTS(Integer.valueOf(request.getDescription()));
				}else{
					dir.append(ConstantsApp.CHAT_TEMP_OTHER);
					chatMsg.setType(ConstantsMsgType.MSG_CHAT_UNKOWN);
				}
				try {
					Logger.d(TAG, "file dir:"+dir.append(fileName));
					File file = new File(dir.append(fileName).toString());
					inTransfer.recieveFile(file);
					
					chatMsg.setCid(id);
					chatMsg.setUname(XMPPUtil.getUserNameFromJID(fromUser));
					chatMsg.setUrl(pre+file.getAbsolutePath());
					acceptFileProgress(inTransfer,chatMsg);
					Log.i("luopeng", "receiver over");
				} catch (SmackException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  		         
			}
			
			
		
		});
		
	}
	
	private void acceptFileProgress(final IncomingFileTransfer inTransfer,final ChatMsg chatMsg){
		if(!connection.isConnected()){
			return;
		}
		new Thread(){
			public void run() {
				double progress = 0;
				Logger.d(TAG, "isDone:"+inTransfer.isDone());
				while(!inTransfer.isDone()){
					if(!connection.isConnected()){
						Logger.d("luopeng", "connetion is closed");
						break;
					}
					progress = inTransfer.getProgress();
//					Logger.i("luopeng", "accpt file:"+inTransfer.getStatus()+"  progress:"+inTransfer.getProgress());
					if(!connection.isConnected()){
						break;
					}
					if(inTransfer.getStatus() == FileTransfer.Status.error){
						Log.i("luopeng", "error :"+inTransfer.getError());
						Log.i("luopeng", "exception :"+inTransfer.getException());
						if("service-unavailable".equals(inTransfer.getException())){
							Log.i("luopeng", "friend is offline ");
						}
						break;
					}
				}
				Logger.d(TAG, "inTransfer status:"+inTransfer.getStatus());
				if(inTransfer.getStatus() == FileTransfer.Status.complete){
					Log.i("luopeng", "accept done");
					MessageCenter.messagecCategory(chatMsg, progress);
				}
			};
		}.start();		
	}
	
	private void addChatListener(){
		chatManager.addChatListener(new ChatManagerListener(){

			@Override
			public void chatCreated(Chat chat, boolean createdLocally) {
				Log.i("luopeng", "ChatRoom chatCreated");
				chat.addMessageListener(new MessageListener(){

					@Override
					public void processMessage(Chat chat, Message message) {
						// TODO Auto-generated method stub
						Log.i("luopeng", "ChatRoom chatCreated processMessage");
						Log.i("luopeng", "Received chat "+message.getBody());
						android.os.Message msg = new android.os.Message();
						String content = "from:"+message.getFrom()+" content:"+message.getBody();
						msg.obj = "from:"+message.getFrom()+" content:"+message.getBody();
						Log.i("luopeng", "ctype:"+message.getSubject("ctype")+" ts:"+message.getSubject("timestamp"));
						MessageCenter.messagecCategory(message);
					}
					
				});
				
			}
			
			
			
		});
	}	
	
	private void addRosterListener(){
		
		
		connection.getRoster().addRosterListener(new RosterListener(){

			@Override
			public void entriesAdded(Collection<String> addresses) {
				// TODO Auto-generated method stub
				Logger.d(TAG, "entriesAdded");
			}

			@Override
			public void entriesUpdated(Collection<String> addresses) {
				// TODO Auto-generated method stub
				Logger.d(TAG, "entriesUpdated");
			}

			@Override
			public void entriesDeleted(Collection<String> addresses) {
				// TODO Auto-generated method stub
				Logger.d(TAG, "entriesDeleted");
			}

			@Override
			public void presenceChanged(Presence presence) {
				// TODO Auto-generated method stub
				Logger.d(TAG, "presenceChanged");
			}
			
		});
	}
	
	public void register(String username, String pwd){
		if(accountManager == null){
			Log.i("luopeng", "accountManager is null");
			return;
		}//[email, password, username, name]
		try {
			accountManager.createAccount(username,pwd);
			Log.i("luopeng", "register success");
		} catch (NoResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMPPErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String regist(String account, String password, String name) {  
	    if (connection == null)  
	        return "0";  
	    Registration reg = new Registration();  
	    reg.setType(IQ.Type.SET);  
	    reg.setTo(connection.getServiceName());  
	    Map<String,String > attributes = new HashMap<String,String>();
	    attributes.put("username", account);
	    attributes.put("password", password);
	    attributes.put("name", name);
	    attributes.put("android", "geolo_createUser_android");
	    reg.setAttributes(attributes);
	    PacketFilter filter = new AndFilter(new PacketIDFilter(  
	            reg.getPacketID()), new PacketTypeFilter(IQ.class));  
	    PacketCollector collector = connection.createPacketCollector(filter);  
	    try {
			connection.sendPacket(reg);
			IQ result = (IQ) collector.nextResult(SmackConfiguration.getDefaultPacketReplyTimeout());  
			// Stop queuing results  
			collector.cancel();// ֹͣ����results���Ƿ�ɹ��Ľ��  
			if (result == null) {  
				Log.e("RegistActivity", "No response from server.");  
				return "0";  
			} else if (result.getType() == IQ.Type.RESULT) {  
				return "1";  
			} else { // if (result.getType() == IQ.Type.ERROR)  
				if (result.getError().toString().equalsIgnoreCase("conflict")) {  
					Log.e("RegistActivity", "IQ.Type.ERROR: "  
							+ result.getError().toString());  
					return "2";  
				} else {  
					Log.e("RegistActivity", "IQ.Type.ERROR: "  
							+ result.getError().toString());  
					return "3";  
				}  
			}  
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	    
	    return "0";
	}  	
	
	
	
	public boolean login(String username, String pwd){
		
		if(connection == null)
			return false;
		try {
			Logger.d(TAG, "login start");
			connection.login(username, pwd);
			Logger.d(TAG, "login end");
			new Thread(){
				public void run() {
					getOfflineMsgs();
				};
			}.start();
			Log.i("luopeng", "login success");
			return true;
		} catch (SaslException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SmackException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	
	/** 
	 * 设置用户状态
	 */  
	public void setPresence(int code) {  
	    if (connection == null)  
	        return;  
	    Presence presence;  
	    try {
	    	 switch (code) {  
		        case 0:  
		            presence = new Presence(Presence.Type.available);  
					connection.sendPacket(presence);
		            Log.v("luopeng", "设置在线");  
		            break;  
		        case 1:  
		            presence = new Presence(Presence.Type.available);  
		            presence.setMode(Presence.Mode.chat);  
		            connection.sendPacket(presence);  
		            Log.v("luopeng", "设置Q我吧");  
		            System.out.println(presence.toXML());  
		            break;  
		        case 2:  
		            presence = new Presence(Presence.Type.available);  
		            presence.setMode(Presence.Mode.dnd);  
		            connection.sendPacket(presence);  
		            Log.v("luopeng", "设置忙碌");  
		            System.out.println(presence.toXML());  
		            break;  
		        case 3:  
		            presence = new Presence(Presence.Type.available);  
		            presence.setMode(Presence.Mode.away);  
		            connection.sendPacket(presence);  
		            Log.v("luopeng", "设置离开");  
		            System.out.println(presence.toXML());  
		            break;  
		        case 4:  
		            Roster roster = connection.getRoster();  
		            Collection<RosterEntry> entries = roster.getEntries();  
		            for (RosterEntry entry : entries) {  
		                presence = new Presence(Presence.Type.unavailable);  
		                presence.setPacketID(Packet.ID_NOT_AVAILABLE);  
		                presence.setFrom(connection.getUser());  
		                presence.setTo(entry.getUser());  
		                connection.sendPacket(presence);  
		                System.out.println(presence.toXML());  
		            }  
		            //  向同一用户的其他客户端发送隐身状态  
		            presence = new Presence(Presence.Type.unavailable);  
		            presence.setPacketID(Packet.ID_NOT_AVAILABLE);  
		            presence.setFrom(connection.getUser());  
		            presence.setTo(StringUtils.parseBareAddress(connection.getUser()));  
		            connection.sendPacket(presence);  
		            Log.v("luopeng", "设置隐身");  
		            break;  
		        case 5:  
		            presence = new Presence(Presence.Type.unavailable);  
		            connection.sendPacket(presence);  
		            Log.v("luopeng", "设置离线");  
		            break;  
		        default:  
		            break;  
	        }  
	    } catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	   
	}
	
	
	public void createChat(String username){
		String userJID = username+"@"+hostName+"/Smack";
		Chat chat = chatManager.createChat(userJID, new MessageListener() {
			@Override
			public void processMessage(Chat chat, Message message) {
				// TODO Auto-generated method stub
//				message.get
//				chat
			}
		});
	}
	
	public Chat createChat(String username,MessageListener messageListner){
		if(chatManager == null)
			return null;
		String userJID = username+"@"+hostName+"/Smack";
		return chatManager.createChat(userJID, messageListner);
	}
	
	   /** 
     * 查询用户 
     *  
     * @param userName 
     * @return 
     * @throws XMPPException 
     */ 
    public List<HashMap<String, String>> searchUsers(String userName) {  
        if (connection == null)  
            return null;  
        HashMap<String, String> user = null;  
        List<HashMap<String, String>> results = new ArrayList<HashMap<String, String>>();  
        try {  
//            new ServiceDiscoveryManager(connection);  
   
            UserSearchManager usm = new UserSearchManager(connection);  
   
            Form searchForm = usm.getSearchForm(connection  
                    .getServiceName());  
            Form answerForm = searchForm.createAnswerForm();  
            answerForm.setAnswer("userAccount", true);  
            answerForm.setAnswer("userPhote", userName);  
            ReportedData data = usm.getSearchResults(answerForm, "search" 
                    + connection.getServiceName());  
            List<Row> list = data.getRows();
            Iterator<Row> it = list.iterator();  
            Row row = null;  
            while (it.hasNext()) {  
                user = new HashMap<String, String>();  
                row = it.next();  
//                user.put("userAccount", row.getValues("userAccount"));  
//                user.put("userPhote", row.getValues("userPhote");  
                results.add(user);  
                // 若存在，则有返回,UserName一定非空，其他两个若是有设，一定非空  
            }  
        } catch (XMPPException e) {  
            e.printStackTrace();  
        } catch (NoResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        return results;  
    } 	
    
    /** 
     * 获取用户VCard信息 
     *  
     * @param connection 
     * @param user 
     * @return 
     * @throws XMPPException 
     */ 
    public VCard getUserVCard(String userJID) {  
        if (connection == null)  
            return null;  
        VCard vcard = new VCard();  
        try {  
        	vcard.load(connection, userJID);  
        	Logger.d(TAG, "vcard.nickName:"+vcard.getNickName()+" vcard.jid:"+vcard.getJabberId());
        } catch (XMPPException e) {  
            e.printStackTrace();  
        } catch (NoResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        return vcard;  
    }  
   
    /** 
     * 获取用户头像信息 
     *  
     * @param connection 
     * @param user 
     * @return 
     */ 
    public Drawable getUserImage(String user) {  
        if (connection == null)  
            return null;  
        ByteArrayInputStream bais = null;  
        try {  
            VCard vcard = new VCard();  
            // 加入这句代码，解决No VCard for  
            ProviderManager.addIQProvider("vCard", "vcard-temp", new VCardProvider());
            if (user == "" || user == null || user.trim().length() <= 0) {  
                return null;  
            }  
            vcard.load(connection, user + "@" 
                    + connection.getServiceName());  
   
            if (vcard == null || vcard.getAvatar() == null)  
                return null;  
            bais = new ByteArrayInputStream(vcard.getAvatar());  
        } catch (Exception e) {  
            e.printStackTrace();  
            return null;  
        }  
        return null;//FormatTools.getInstance().InputStream2Drawable(bais);  
    }     
    
	public void registVCard(String userName){
		VCardProvider vProvider = new VCardProvider();
		ProviderManager.addIQProvider("vCard", "vcard-temp", new VCardProvider());
		User user = new User();
		user.setName(userName);
		user.setGroupName("luopeng1");
		String userXML = XMLUtil.getUserVCardXML(user);
		try {
			VCard vCard = vProvider.createVCardFromXML(userXML);
//			vCard.load(connection);
			vCard.save(connection);
			Logger.d(TAG, "nickName:"+vCard.getNickName()+" groupName:"+vCard.getOrganization());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void updateVCard(User user){
		VCard vCard = new VCard();
		vCard.setNickName(user.getName());
		vCard.setJabberId(connection.getUser());
		try {
			vCard.save(connection);
			Logger.d(TAG, "updateVCard vCard.nickName:"+vCard.getNickName());
		} catch (NoResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMPPErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
