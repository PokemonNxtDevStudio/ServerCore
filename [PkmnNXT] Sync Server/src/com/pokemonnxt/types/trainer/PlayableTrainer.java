package com.pokemonnxt.types.trainer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;




import com.google.gson.annotations.Expose;
import com.pokemonnxt.types.Location;
import com.pokemonnxt.types.pokemon.Pokemon.Stats;
import com.pokemonnxt.types.pokemon.PlayablePokemon;
import com.pokemonnxt.types.pokemon.Pokemon;
import com.pokemonnxt.gameserver.Client;
import com.pokemonnxt.gameserver.Functions;
import com.pokemonnxt.gameserver.GlobalExceptionHandler;
import com.pokemonnxt.gameserver.Logger;
import com.pokemonnxt.gameserver.Main;
import com.pokemonnxt.gameserver.PlayerLog;
import com.pokemonnxt.gameserver.Players;
import com.pokemonnxt.gameserver.ServerVars;
import com.pokemonnxt.gameserver.PlayerLog.LOGTYPE;
import com.pokemonnxt.packets.Communications.*;

public class PlayableTrainer extends Trainer implements AutoCloseable{

	public boolean isLoggedIn = false;
	public boolean isNew = false;
	public Client Connection;
	
	@Expose public String Username;
	@Expose public int GTID;
	@Expose public List<PlayablePokemon> Party = new ArrayList<PlayablePokemon>();
	@Expose public List<Integer> Items = new ArrayList<Integer>();

	public PlayableTrainer(String username, String password, String email, Client conn) throws  LoginFailed{
		Logger.log_server(Logger.LOG_PROGRESS, "Username " + username + " login requested");
		ResultSet PlayerResult = Main.SQL.Query("SELECT * FROM `NXT_USERS` WHERE `Username`='" + username  + "' AND `Password`='" + Functions.encryptPassword(password) + "'");
		if(!Functions.hasResults(PlayerResult)){ // If player username and password do not match
			Logger.log_server(Logger.LOG_PROGRESS, "Username and password do not match.");
			ResultSet UsernameResult = Main.SQL.Query("SELECT * FROM `NXT_USERS` WHERE `Username`='" + username  + "'");
			if(!Functions.hasResults(UsernameResult)){ // and the username is not in the database
				Logger.log_server(Logger.LOG_PROGRESS, "Username " + username + " Not in database");
				if(email != ""){ // but the email is present
					Logger.log_server(Logger.LOG_PROGRESS, "Email specified: " + email);
					ResultSet EmailResult = Main.SQL.Query("SELECT * FROM `NXT_USERS` WHERE `Email`='" + email  + "'");
					if(!Functions.hasResults(EmailResult)){ // and the email is not in the database
					try {
						Logger.log_server(Logger.LOG_VERB_HIGH, "Creating new player: " + username);
						PreparedStatement createPlayer = null;
						
						// Make the login details entry
						createPlayer = Main.SQL.SQL.prepareStatement("INSERT INTO `NXT_USERS` (`Username`,`Password`, `Email`) VALUES (?, ?, ?)",Statement.RETURN_GENERATED_KEYS);
						createPlayer.setString(1, username);
						createPlayer.setString(2, Functions.encryptPassword(password));
						createPlayer.setString(3, email);
						GTID = createPlayer.executeUpdate();
						ResultSet insertResult = createPlayer.getGeneratedKeys();
						insertResult.first();
						GTID =  insertResult.getInt(1);
						
						PreparedStatement InsertLocation = null;
						InsertLocation = Main.SQL.SQL.prepareStatement("INSERT INTO `NXT_GAME`.`PLAYER_LOCATION` (`GTID`, `X`, `Y`, `Z`, `Pitch`, `Yaw`, `Roll`, `ZONE`) VALUES (?, ?, ?, ?, ?, ?, ?,?)",Statement.RETURN_GENERATED_KEYS);
						InsertLocation.setInt(1, GTID);
						InsertLocation.setDouble(2, ServerVars.Spawn.X);
						InsertLocation.setDouble(3, ServerVars.Spawn.Y);
						InsertLocation.setDouble(4, ServerVars.Spawn.Z);
						InsertLocation.setDouble(5, ServerVars.Spawn.P);
						InsertLocation.setDouble(6, ServerVars.Spawn.Ya);
						InsertLocation.setDouble(7, ServerVars.Spawn.R);
						InsertLocation.setString(8, "SPAWN");
						InsertLocation.executeUpdate();
						
						
						
						Username = username;
						isLoggedIn = true;
						isNew = true;
					} catch (SQLException e) {
						GlobalExceptionHandler GEH = new GlobalExceptionHandler();
						GEH.uncaughtException(Thread.currentThread(), (Throwable) e);
						isLoggedIn = false;
						e.printStackTrace();
						return;
					}
					}else{
						Logger.log_server(Logger.LOG_PROGRESS, "Specified Email In Use");
						throw new LoginFailed(3, "Email in use!");
					}
				}else{
					Logger.log_server(Logger.LOG_PROGRESS, "Username/Password mismatch");
					throw new LoginFailed(1, "Username and password do not match!");
				}
			}else{
				Logger.log_server(Logger.LOG_PROGRESS, "Password was just damn wrong");
				throw new LoginFailed(1, "Incorrect Password!");
			}
		}
		
		try {
			 if(isNew)  {
			PlayerResult = Main.SQL.Query("SELECT * FROM `NXT_USERS` WHERE `Username`='" + username  + "' AND `Password`='" + Functions.encryptPassword(password) + "'");
			 if(! Functions.hasResults(PlayerResult)){
				 throw new LoginFailed(5, "Unable to make user.");
			 }
			 }
			String banned = PlayerResult.getString("Ban");
			if(banned != null){
				throw new LoginFailed(2, "Your account has been suspended: " + banned);
			}
			if(isLoggedIn == false){
				
			GTID = PlayerResult.getInt("GTID");
			Username = username;
			}
			isLoggedIn = true;
			
		} catch (SQLException e) {
			isLoggedIn = false;
			GlobalExceptionHandler GEH = new GlobalExceptionHandler();
			GEH.uncaughtException(Thread.currentThread(), (Throwable) e);
			throw new LoginFailed(5, "Internal Server Error occured during login: Please contact admins.");
		}
		// Now load their shit up
		Connection = conn;
		LoadFromDB();
		
		 Players.AddPlayer(this);
		 setOnline(true);
		
		Logger.log_server(Logger.LOG_PROGRESS, username + " Loaded!");
	}
	
	
	public PlayableTrainer(int ID) throws PlayerNotFound{
		ResultSet PlayerResult = Main.SQL.Query("SELECT * FROM `NXT_USERS` WHERE `GTID`=" + ID );
		if(!Functions.hasResults(PlayerResult)) throw new PlayerNotFound();
		try {
		GTID = PlayerResult.getInt("GTID");
		Username = PlayerResult.getString("Username");
		ReloadPokemon();
	} catch (SQLException e) {
		 throw new PlayerNotFound();
	}
	}
	
	public void setOnline(boolean on){
		PreparedStatement loginPlayer = null;
		try {
			loginPlayer = Main.SQL.SQL.prepareStatement("UPDATE `NXT_USERS` SET `Online`=? WHERE `GTID`=?",Statement.RETURN_GENERATED_KEYS);
			if (on){
				loginPlayer.setInt(1, 1);
				PlayerLog.LogAction(LOGTYPE.LOGIN, GTID);
			}else{
				loginPlayer.setInt(1, 0);
				PlayerLog.LogAction(LOGTYPE.LOGOUT, GTID);
			}
			
			loginPlayer.setInt(2, GTID);
			loginPlayer.executeUpdate();
		} catch (SQLException e) {
			GlobalExceptionHandler GEH = new GlobalExceptionHandler();
			GEH.uncaughtException(Thread.currentThread(), (Throwable) e);
		}
	}
	public void CommitToDB(){
		for(PlayablePokemon PP : Party){
			PP.CommitToDB();
		}
		CommitLocation();
	}
	public void CommitLocation(){
		PreparedStatement InsertLocation = null;
		try {
			InsertLocation = Main.SQL.SQL.prepareStatement("UPDATE  `NXT_GAME`.`PLAYER_LOCATION` SET  `X`=?,`Y`=?,`Z`=?,`Pitch`=?,`Yaw`=?,`Roll`=? WHERE  `PLAYER_LOCATION`.`GTID` =?;",Statement.RETURN_GENERATED_KEYS);
			InsertLocation.setDouble(1, ServerVars.Spawn.X);
			InsertLocation.setDouble(2, ServerVars.Spawn.Y);
			InsertLocation.setDouble(3, ServerVars.Spawn.Z);
			InsertLocation.setDouble(4, ServerVars.Spawn.P);
			InsertLocation.setDouble(5, ServerVars.Spawn.Ya);
			InsertLocation.setDouble(6, ServerVars.Spawn.R);
			InsertLocation.setInt(7, GTID);
			InsertLocation.executeUpdate();
		} catch (SQLException e) {
			GlobalExceptionHandler GEH = new GlobalExceptionHandler();
			GEH.uncaughtException(Thread.currentThread(), (Throwable) e);
		}
		
	}

	
	public void Kick(String message, String kicker){

			if(isLoggedIn){
				Connection.sendKick(message);
			}else{
				message = "[OFFLINE] " + message;
			}
			PlayerLog.LogAction(LOGTYPE.KICK, GTID, kicker, message);
	}
	
	public void sendMessage(ChatTypes Class, int Sender, String Message){
		Connection.sendChatUpdate(Class, Sender, Message);
	}
	
	public boolean Ban(String message, String Banner){
		PreparedStatement BanStatement = null;
		try {
			Kick(message, Banner);
			BanStatement = Main.SQL.SQL.prepareStatement("UPDATE `NXT_USERS` SET `Ban`=? WHERE `GTID`=?",Statement.RETURN_GENERATED_KEYS);
			BanStatement.setString(1, message);
			BanStatement.setInt(2, GTID);
			BanStatement.executeUpdate();
			BanStatement = null;
			PlayerLog.LogAction(LOGTYPE.BAN, GTID, Banner, message);
			return true;
		} catch (SQLException e) {
			GlobalExceptionHandler GEH = new GlobalExceptionHandler();
			GEH.uncaughtException(Thread.currentThread(), (Throwable) e, "Unable to ban player " + GTID);
			return false;
		}
	}
	public boolean UnBan(String message, String UnBanner){
		PreparedStatement BanStatement = null;
		try {
			BanStatement = Main.SQL.SQL.prepareStatement("UPDATE `NXT_USERS` SET `Ban`=? WHERE `GTID`=?",Statement.RETURN_GENERATED_KEYS);
			BanStatement.setString(1, "");
			BanStatement.setInt(2, GTID);
			BanStatement.executeUpdate();
			BanStatement = null;
			PlayerLog.LogAction(LOGTYPE.UNBAN, GTID, UnBanner, message);
			return true;
		} catch (SQLException e) {
			GlobalExceptionHandler GEH = new GlobalExceptionHandler();
		GEH.uncaughtException(Thread.currentThread(), (Throwable) e, "Unable to unban player " + GTID);
			return false;
		}
	}
	public Pokemon catchNew(int lDEX, int lLevel, int lEXP, String Name, Pokemon.Stats CurrentStats, Pokemon.Stats BaseStats){
		PlayablePokemon poke = new PlayablePokemon(lDEX, lLevel, lEXP, Name, CurrentStats, BaseStats, this);
		if(poke.GPID > 0){
		Party.add(poke);
		PlayerLog.LogAction(LOGTYPE.CATCH,GTID,Connection.IP,Integer.toString(poke.GPID));
		return poke;
		}
		poke = null;
		return poke;
	}
	
	public boolean useItem(int itemID){
		if (Items.contains(itemID)){
			// TODO Script to remove item from inventory
			return true;
		}else{
			return false;
		}
	}
	public void Move(Location l){
		location.Move(l);
		if(Connection != null){
			Players.SendLocationUpdate(this);
		}
	}
	public void Teleport(Location l){
		location.Move(l);
		if(Connection != null){
			Players.SendLocationUpdate(this);
		}
		CommitToDB();
	}
	
	public void LoadFromDB(){
		ReloadLocation();
		ReloadPokemon();
		ReloadItems();
	}
	
	private void ReloadPokemon(){
		Party.clear();
		ResultSet PokemonResult = Main.SQL.Query("SELECT `GPID` FROM `CAUGHT_POKEMON` WHERE `GTID`='" + GTID  + "'");
		try {
			while(PokemonResult.next()){
				Logger.log_player(Logger.LOG_VERB_HIGH, "Loading new pokemon from GPID: " + PokemonResult.getInt("GPID"),GTID);
				Party.add(new PlayablePokemon(PokemonResult.getInt("GPID")));
			}
		} catch (SQLException e) {
			GlobalExceptionHandler GEH = new GlobalExceptionHandler();
			GEH.uncaughtException(Thread.currentThread(), (Throwable) e);
		}
	}
	private void ReloadLocation(){
		ResultSet LocationResult = Main.SQL.Query("SELECT * FROM `PLAYER_LOCATION` WHERE `GTID`='" + GTID  + "'");
		try {
			while(LocationResult.next()){
				 location.X = LocationResult.getInt("X");
				 location.Y = LocationResult.getInt("Y");
				 location.Z = LocationResult.getInt("Z");
				 location.P = LocationResult.getInt("Pitch");
				 location.Ya = LocationResult.getInt("Yaw");
				 location.R = LocationResult.getInt("Roll");
			}
		} catch (SQLException e) {
			GlobalExceptionHandler GEH = new GlobalExceptionHandler();
			GEH.uncaughtException(Thread.currentThread(), (Throwable) e);
		}
		Teleport(location);
	}
	
	public void ReloadItems(){
		Items.clear();
		ResultSet ItemResult = Main.SQL.Query("SELECT `GIID` FROM `PLAYER_ITEMS` WHERE `GTID`='" + GTID  + "'");
		try {
			while(ItemResult.next()){
				Items.add(ItemResult.getInt("GIID"));
			}
		} catch (SQLException e) {
			GlobalExceptionHandler GEH = new GlobalExceptionHandler();
			GEH.uncaughtException(Thread.currentThread(), (Throwable) e);
		}
	}
	
	public void signOut(){
		Logger.log_player(Logger.LOG_VERB_HIGH, "Logging out player...", GTID);
		 Players.RemovePlayer(this);
		 if(Party != null){
			 for(PlayablePokemon pp : Party) Players.RemovePokemon(pp.GPID);
			 Party.clear();
		 }
		if(isLoggedIn) {
		
		if (GTID == 0){
			Logger.log_player(Logger.LOG_ERROR, "Invalid GTID: Could not sign out.", GTID);
		}else{
			
			setOnline(false);
		}
		}
		GTID = 0;
		isLoggedIn = false;
	}
	

	


	
	
	public  PlayerDataPayload toPayload(){
		PlayerDataPayload.Builder PDPB =
				PlayerDataPayload.newBuilder()
				.setGtid(GTID)
				.setLocation(location.toPayload())
				.setUsername(Username);
		for(PlayablePokemon PP : Party){
			PDPB.addParty(PP.generatePayload());
		}
		PlayerDataPayload PDP = PDPB.build();
		return PDP;
	}
	public  PlayerDataPayload toLocationUpdatePayload(){
		PlayerDataPayload.Builder PDPB =
				PlayerDataPayload.newBuilder()
				.setGtid(GTID)
				.setLocation(location.toPayload());
		return PDPB.build();
	}
	
	@Override
	public void close() throws Exception {
		Logger.log_player(Logger.LOG_VERB_LOW, "Player object facing destruction: Signing out...", GTID);
		signOut();
		Logger.log_player(Logger.LOG_PROGRESS, "Done... Destroying player.", GTID);
		
	}
	
	public class PlayerNotFound extends Throwable{
		/**
		 * 
		 */
		private static final long serialVersionUID = -3367932179014139737L;

		String message = "The player specified is not present on this NXT server";
	}
	
	public class LoginFailed extends Throwable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1306933785573476075L;
		
		public String message = "Unspecified";
		int Type = 0;
		
		/*
		 *  1 = Username/Password mismatch
		 *  2 = User Banned
		 *  3 = Email already in use
		 */
		public LoginFailed(int typ, String msg){
			Type = typ;
			message = msg;
		}
	}
	
	
	
}
