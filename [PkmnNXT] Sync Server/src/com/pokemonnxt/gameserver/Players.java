package com.pokemonnxt.gameserver;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map.Entry;

import com.pokemonnxt.types.Asset;
import com.pokemonnxt.types.Location;
import com.pokemonnxt.types.pokemon.PlayablePokemon;
import com.pokemonnxt.types.pokemon.Pokemon;
import com.pokemonnxt.types.trainer.PlayableTrainer;
import com.pokemonnxt.packets.ClientComms.AssetDataPayload;
import com.pokemonnxt.packets.CommTypes.CHAT_TYPES;


public class Players {
public static HashMap<Integer,PlayableTrainer> Players = new HashMap<Integer,PlayableTrainer>();
public static HashMap<Integer,Pokemon> Pokemon = new HashMap<Integer,Pokemon>();
public static HashMap<String,Integer> Usernames = new HashMap<String,Integer>();
public static HashMap<String,Integer> LoginTokens = new HashMap<String,Integer>();

/*
	public static enum MESSAGE_TYPE {
		ERROR(-1), ADMIN_CONSOLE(0), ADMIN_INGAME(1), WEBSITE(2), WHISPER(3), LOCAL(4), LOCAL_SCHEDULED(5), GLOBAL_SCHEDULED(6), GLOBAL_ADMIN(7);
        private  int value;
        
        private static Map<Integer, MESSAGE_TYPE> map = new HashMap<Integer, MESSAGE_TYPE>();

        static {
            for (MESSAGE_TYPE legEnum : MESSAGE_TYPE.values()) {
                map.put(legEnum.value, legEnum);
            }
        }

        

        public static MESSAGE_TYPE valueOf(int legNo) {
            return map.get(legNo);
        }
        private MESSAGE_TYPE(int value) {
                this.value = value;
        }
};   */

public static void AddPlayer(PlayableTrainer p){
	if(p != null){
	if (p.isLoggedIn){
		if (!Usernames.containsKey(p.Username)) Usernames.put(p.Username.toLowerCase(), p.GTID);
		Players.put(p.GTID,p);
	}
	}
}
public static void RemovePlayer(PlayableTrainer p){
	if(p != null){
	if (Players.containsKey(p.GTID)){
		Players.remove(p.GTID);
	}
	}
}
public static PlayableTrainer getPlayer(int GTID){
	if (Players.containsKey(GTID)){
		return Players.get(GTID);
	}
	return null;
}
public static PlayableTrainer getPlayer(String Username){
	if (Usernames.containsKey(Username)){
		int GTID = Usernames.get(Username);
		if (Players.containsKey(GTID)){
			return Players.get(GTID);
		}
	}
	
	return null;
}


public static boolean isOnline(int GTID){
	if (Players.containsKey(GTID)) return Players.get(GTID).isLoggedIn;
	return false;
	
}

public static String getUsername(int GTID){
	if (Players.containsKey(GTID)) return Players.get(GTID).Username;
	
	ResultSet PokemonResult = Main.SQL.Query("SELECT `Username` FROM `NXT_USERS` WHERE `GTID`='" + GTID  + "'");
	try {
		while(PokemonResult.next()){
			return PokemonResult.getString("Username");
		}
	} catch (SQLException e) {
		GlobalExceptionHandler GEH = new GlobalExceptionHandler();
		GEH.uncaughtException(Thread.currentThread(), (Throwable) e);
		e.printStackTrace();
	}
	
	return "[NOT FOUND]";
	
}
public static int getGTID(String username){
	
	if (Functions.isInteger(username)) return Integer.parseInt(username);

	if (Usernames.containsKey(username.toLowerCase())) return Usernames.get(username);
	
	
	ResultSet PokemonResult = Main.SQL.Query("SELECT `GTID` FROM `NXT_USERS` WHERE `GTID`='" + username  + "' OR `Username`='" + username  + "'");
	try {
		while(PokemonResult.next()){
			return PokemonResult.getInt("GTID");
		}
	} catch (SQLException e) {
		GlobalExceptionHandler GEH = new GlobalExceptionHandler();
		GEH.uncaughtException(Thread.currentThread(), (Throwable) e);
		e.printStackTrace();
	}
	
	return -1;
	
}




public static void SendLocationUpdate(Asset moved){
	AssetDataPayload PDP = moved.toAssetPayload();
	for(Entry<Integer, PlayableTrainer> entry : Players.entrySet()) {
	    Integer GTID = entry.getKey();
	    PlayableTrainer player = entry.getValue();
	    Logger.log_server(Logger.LOG_PROGRESS, "LOC X: " + moved.location);
	    Logger.log_server(Logger.LOG_PROGRESS, "PL X: " + player.location.X);
	    if(player.location.isNear(moved.location, ServerVars.LocUpdateDist)){
	    	player.Connection.sendLocationUpdate(PDP);
	    }
	}
}



public static void SendChat(CHAT_TYPES Type, int Sender, String Message){
	SendMessageToLocals(Type,Sender,getPlayer(Sender).location,Message);
}

private static void SendMessageToLocals(CHAT_TYPES Type,int Sender, Location LOC, String Message){
	for(Entry<Integer, PlayableTrainer> entry : Players.entrySet()) {
	    Integer GTID = entry.getKey();
	    PlayableTrainer player = entry.getValue();
	    if(player.location.isNear(LOC, ServerVars.LocUpdateDist)){
	    	player.sendMessage(Type, Sender, Message);
	    }
	}
}

public static void SendChat(CHAT_TYPES Type,int Sender, int Recipient, String Message) throws PlayerOffline{
	if(isOnline(Recipient)){
		getPlayer(Recipient).sendMessage(Type, Sender, Message);
	}else{
		throw new PlayerOffline();
	}
	    
}

public static void AddPokemon(Pokemon p){
	if (p != null){
		Pokemon.put(p.GPID,p);
	}
}

public static void RemovePokemon(int GPID){
	if (Pokemon.containsKey(GPID)){
		Pokemon.remove(GPID);
	}
}
public static class PlayerOffline extends Throwable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -386049869785713101L;
	
	String message = "The player specified is offline. You specified an action that required being online.";
}


}
