package com.pokemonnxt.packets;

import com.pokemonnxt.sync.Player;
import com.pokemonnxt.types.Location;
import com.pokemonnxt.types.PlayerPokemon;
import com.pokemonnxt.packets.Communications.*;

public class ClassToPayload {

	public ClassToPayload() {
		// TODO Auto-generated constructor stub
	}

	public static ActionFailedPayload makeActionFailedPayload(ErrorTypes E){
		ActionFailedPayload.Builder PDPB =
				ActionFailedPayload.newBuilder()
				.setError(E);
		ActionFailedPayload PDP = PDPB.build();
		return PDP;
	}
	
	public static ActionFailedPayload makeActionFailedPayload(ErrorTypes E, String msg){
		ActionFailedPayload.Builder PDPB =
				ActionFailedPayload.newBuilder()
				.setError(E)
				.setMessage(msg);
		ActionFailedPayload PDP = PDPB.build();
		return PDP;
	}
	public static ActionFailedPayload makeActionFailedPayload(ErrorTypes E, int errorID){
		ActionFailedPayload.Builder PDPB =
				ActionFailedPayload.newBuilder()
				.setError(E)
				.setErrorID(errorID);
		ActionFailedPayload PDP = PDPB.build();
		return PDP;
	}
	public static ActionFailedPayload makeActionFailedPayload(ErrorTypes E, int errorID, String msg){
		ActionFailedPayload.Builder PDPB =
				ActionFailedPayload.newBuilder()
				.setError(E)
				.setErrorID(errorID)
				.setMessage(msg);
		ActionFailedPayload PDP = PDPB.build();
		return PDP;
	}
	public static ActionFailedPayload makeActionFailedPayload(ErrorTypes E, String msg,int ID){
		ActionFailedPayload.Builder PDPB =
				ActionFailedPayload.newBuilder()
				.setError(E)
				.setId(ID)
				.setMessage(msg);
		ActionFailedPayload PDP = PDPB.build();
		return PDP;
	}
	public static ChatMsgPayload makeChatMsgPayload(ChatTypes C, String msg,int ID){
		ChatMsgPayload.Builder PDPB =
				ChatMsgPayload.newBuilder()
				.setType(C)
				.setGtid(ID)
				.setMsg(msg);
		ChatMsgPayload PDP = PDPB.build();
		return PDP;
	}
	public static ChatMsgPayload makeChatMsgPayload(ChatTypes C, String msg,String ID){
		ChatMsgPayload.Builder PDPB =
				ChatMsgPayload.newBuilder()
				.setType(C)
				.setUsername(ID)
				.setMsg(msg);
		ChatMsgPayload PDP = PDPB.build();
		return PDP;
	}
	public static ChatMsgPayload makeChatMsgPayload(ChatTypes C, String msg){
		ChatMsgPayload.Builder PDPB =
				ChatMsgPayload.newBuilder()
				.setType(C)
				.setMsg(msg);
		ChatMsgPayload PDP = PDPB.build();
		return PDP;
	}
	

}
