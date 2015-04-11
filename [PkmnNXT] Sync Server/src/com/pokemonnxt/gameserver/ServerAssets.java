package com.pokemonnxt.gameserver;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.pokemonnxt.types.Asset;
import com.pokemonnxt.types.Location;
import com.pokemonnxt.types.pokemon.PlayablePokemon;
import com.pokemonnxt.types.pokemon.Pokemon;
import com.pokemonnxt.types.trainer.PlayableTrainer;
import com.pokemonnxt.types.trainer.Trainer;
import com.pokemonnxt.packets.Packet;


public class ServerAssets {
	
private static HashMap<Integer,PlayableTrainer> Players = new HashMap<Integer,PlayableTrainer>();
private static HashMap<Integer,Client> Clients = new HashMap<Integer,Client>();
private static HashMap<Integer,Pokemon> Pokemon = new HashMap<Integer,Pokemon>();
private static HashMap<Integer,Asset> Assets = new HashMap<Integer,Asset>();
private static HashMap<String,Integer> Usernames = new HashMap<String,Integer>();
private static HashMap<String,Integer> LoginTokens = new HashMap<String,Integer>();

public static int GenerateAssetID(){
	int ID = 0;
	ID = (int) (Math.random() * 100000);
	 while(ServerAssets.Assets.containsKey(ID)){
		  ID = (int) (Math.random() * 100000);
	  }
	 return ID;
}
public static void AddAsset(Asset A){
	Assets.put(A.AID, A);
}
public static void DestroyAsset(Asset A){
	for(Client c : Clients.values()){
		if (c.Assets.containsKey(A.AID)){
			c.TakeOwnership(A);
		}
	}
	Assets.remove(A.AID);
	A = null;
	cleanDatabanks();
}
public static void TransferAsset(Asset A, Client C){
	if (A.owner == 0){
		
	}else{
	for(Client c : Clients.values()){
		if (c.Assets.containsKey(A.AID)){
			c.TakeOwnership(A);
		}
	}
	}
	C.GiveOwnership(A);
}

public static void cleanDatabanks(){
	Iterator<Entry<Integer, Client>> it = Clients.entrySet().iterator();
    while (it.hasNext()) {
        Map.Entry<Integer,Client> pairs = (Map.Entry<Integer,Client>)it.next();
        if(pairs.getValue() == null){
        	Logger.log_server(Logger.LOG_VERB_HIGH, "Removed null client ID " + pairs.getKey());
        	it.remove();
        }
    }
    
    Iterator<Entry<Integer, Pokemon>> it1 = Pokemon.entrySet().iterator();
    while (it1.hasNext()) {
        Map.Entry<Integer, Pokemon> pairs = (Map.Entry<Integer, Pokemon>) it1.next();
        if(pairs.getValue() == null){
        	Logger.log_server(Logger.LOG_VERB_HIGH, "Removed null pokemon GPID" + pairs.getKey());
        	it1.remove();
        }
    }
    
    Iterator<Entry<Integer, PlayableTrainer>> it2 = Players.entrySet().iterator();
    while (it2.hasNext()) {
        Map.Entry<Integer, PlayableTrainer> pairs = (Map.Entry<Integer, PlayableTrainer>) it2.next();
        if(pairs.getValue() == null){
        	Logger.log_server(Logger.LOG_VERB_HIGH, "Removed null trainer GTID" + pairs.getKey());
        	it2.remove();
        }
    }
    
    
}
//GIT UPDATE
public static int GenerateClientID(){
	int ID = 0;
	ID = (int) (Math.random() * 100000);
	 while(ServerAssets.Clients.containsKey(ID)){
		  ID = (int) (Math.random() * 100000);
	  }
	 return ID;
}

public static void AddClient(Client c){
	
}
public static void RemoveClient(Client c){
	
}

public static void AddPlayer(PlayableTrainer p){
	if(p != null){
	if (p.isLoggedIn){
		if (!Usernames.containsKey(p.Name)) Usernames.put(p.Name.toLowerCase(), p.GTID);
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

public static Asset getAsset(int AssetID){
	if (Assets.containsKey(AssetID)){
		return Assets.get(AssetID);
	}
	return null;
}
public static PlayableTrainer getPlayer(int GTID){
	if (Players.containsKey(GTID)){
		return Players.get(GTID);
	}
	return null;
}
public static Pokemon getPokemon(int GPID){
	if (Pokemon.containsKey(GPID)){
		return Pokemon.get(GPID);
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
	if (Players.containsKey(GTID)) return Players.get(GTID).Name;
	
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




public static void SendLocationUpdate(Packet.LocationUpdate LU){
	for(Entry<Integer, PlayableTrainer> entry : Players.entrySet()) {
	    PlayableTrainer player = entry.getValue();
	    if(player.location.isNear(LU.location, ServerVars.LocUpdateDist)){
	    	player.Connection.sendLocationUpdate(LU);
	    }
	}
}

public static void SendChat( PlayableTrainer Sender, String Message){
	Logger.log_server(Logger.LOG_VERB_LOW, "[CHAT] " +  Sender + ": " + Message);
	SendMessageToLocals(Sender,Message);
}

private static void SendMessageToLocals(PlayableTrainer Sender,  String Message){
	for(Entry<Integer, PlayableTrainer> entry : Players.entrySet()) {
	    PlayableTrainer player = entry.getValue();
	    if(player.location.isNear(Sender.location, ServerVars.LocUpdateDist)){
	    	player.sendMessage(Sender.GTID, Message);
	    }
	}
}

public static List<Trainer> getNearbyPlayers(Location L){
	ArrayList<Trainer> OUT = new ArrayList<Trainer>();
	for(Entry<Integer, PlayableTrainer> entry : Players.entrySet()) {
	    PlayableTrainer player = entry.getValue();
	    if(player.location.isNear(L, ServerVars.LocUpdateDist)) OUT.add((Trainer) player);
	}
	return OUT;
}

public static void SendChat(int Sender, int Recipient, String Message) throws PlayerOffline{
	if(isOnline(Recipient)){
		getPlayer(Recipient).sendMessage(Sender, Message);
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
