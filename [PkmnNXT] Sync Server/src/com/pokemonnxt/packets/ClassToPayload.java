package com.pokemonnxt.packets;

import com.pokemonnxt.types.Location;
import com.pokemonnxt.packets.ClientComms.ActionFailedPayload;
import com.pokemonnxt.packets.ClientComms.ChatMsgPayload;
import com.pokemonnxt.packets.CommTypes.CHAT_TYPES;
import com.pokemonnxt.packets.CommTypes.ERROR_TYPES;

public class ClassToPayload {

	public ClassToPayload() {
		// TODO Auto-generated constructor stub
	}

	public static ActionFailedPayload makeActionFailedPayload(ERROR_TYPES E){
		ActionFailedPayload.Builder PDPB =
				ActionFailedPayload.newBuilder()
				.setError(E);
		ActionFailedPayload PDP = PDPB.build();
		return PDP;
	}
	
	public static ActionFailedPayload makeActionFailedPayload(ERROR_TYPES E, String msg){
		ActionFailedPayload.Builder PDPB =
				ActionFailedPayload.newBuilder()
				.setError(E)
				.setMessage(msg);
		ActionFailedPayload PDP = PDPB.build();
		return PDP;
	}
	public static ActionFailedPayload makeActionFailedPayload(ERROR_TYPES E, int errorID){
		ActionFailedPayload.Builder PDPB =
				ActionFailedPayload.newBuilder()
				.setError(E)
				.setErrorID(errorID);
		ActionFailedPayload PDP = PDPB.build();
		return PDP;
	}
	public static ActionFailedPayload makeActionFailedPayload(ERROR_TYPES E, int errorID, String msg){
		ActionFailedPayload.Builder PDPB =
				ActionFailedPayload.newBuilder()
				.setError(E)
				.setErrorID(errorID)
				.setMessage(msg);
		ActionFailedPayload PDP = PDPB.build();
		return PDP;
	}
	public static ActionFailedPayload makeActionFailedPayload(ERROR_TYPES E, String msg,int ID){
		ActionFailedPayload.Builder PDPB =
				ActionFailedPayload.newBuilder()
				.setError(E)
				.setId(ID)
				.setMessage(msg);
		ActionFailedPayload PDP = PDPB.build();
		return PDP;
	}
	public static ChatMsgPayload makeChatMsgPayload(CHAT_TYPES C, String msg,int ID){
		ChatMsgPayload.Builder PDPB =
				ChatMsgPayload.newBuilder()
				.setType(C)
				.setGtid(ID)
				.setMsg(msg);
		ChatMsgPayload PDP = PDPB.build();
		return PDP;
	}
	public static ChatMsgPayload makeChatMsgPayload(CHAT_TYPES C, String msg,String ID){
		ChatMsgPayload.Builder PDPB =
				ChatMsgPayload.newBuilder()
				.setType(C)
				.setUsername(ID)
				.setMsg(msg);
		ChatMsgPayload PDP = PDPB.build();
		return PDP;
	}
	public static ChatMsgPayload makeChatMsgPayload(CHAT_TYPES C, String msg){
		ChatMsgPayload.Builder PDPB =
				ChatMsgPayload.newBuilder()
				.setType(C)
				.setMsg(msg);
		ChatMsgPayload PDP = PDPB.build();
		return PDP;
	}
	

}
