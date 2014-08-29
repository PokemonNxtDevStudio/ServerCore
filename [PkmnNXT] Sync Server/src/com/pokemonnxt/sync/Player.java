package com.pokemonnxt.sync;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;



import com.google.gson.annotations.Expose;
import com.pokemonnxt.sync.PlayerLog.LOGTYPE;


public class Player implements AutoCloseable{

	public boolean isLoggedIn = false;
	public boolean isNew = false;
	public Client Connection;
	
	@Expose public String Username;
	@Expose public int GTID;
	@Expose public List<PlayerPokemon> Party = new ArrayList<PlayerPokemon>();
	@Expose public List<Integer> Items = new ArrayList<Integer>();
	@Expose public Location location = new Location();
	

	Player(String username, String password, String email, Client conn) throws  LoginFailed{
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
						createPlayer = Main.SQL.SQL.prepareStatement("INSERT INTO `NXT_USERS` (`Username`,`Password`, `Email`) VALUES (?, ?, ?)",Statement.RETURN_GENERATED_KEYS);
						createPlayer.setString(1, username);
						createPlayer.setString(2, Functions.encryptPassword(password));
						createPlayer.setString(3, email);
						GTID = createPlayer.executeUpdate();
						ResultSet insertResult = createPlayer.getGeneratedKeys();
						insertResult.first();
						GTID =  insertResult.getInt(1);
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
		// Now load their pokemon
		Connection = conn;
		ReloadPokemon();
		
		 Players.AddPlayer(this);
		PreparedStatement loginPlayer = null;
		try {
			loginPlayer = Main.SQL.SQL.prepareStatement("UPDATE `NXT_USERS` SET `Online`=? WHERE `GTID`=?",Statement.RETURN_GENERATED_KEYS);
			loginPlayer.setInt(1, 1);
			loginPlayer.setInt(2, GTID);
			loginPlayer.executeUpdate();
			PlayerLog.LogAction(LOGTYPE.LOGIN, GTID, Connection.IP);
		} catch (SQLException e) {
			GlobalExceptionHandler GEH = new GlobalExceptionHandler();
			GEH.uncaughtException(Thread.currentThread(), (Throwable) e);
		}
		
		Logger.log_server(Logger.LOG_PROGRESS, username + " Loaded!");
	}
	
	
	Player(int ID) throws PlayerNotFound{
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
	
	public void CommitToDB(){
		for(PlayerPokemon PP : Party){
			PP.CommitToDB();
		}
	}
	
	public void Kick(String message, String kicker){

			if(isLoggedIn){
				Connection.sendKick(message);
			}else{
				message = "[OFFLINE] " + message;
			}
			PlayerLog.LogAction(LOGTYPE.LOGIN, GTID, kicker, message);
	}
	
	public void sendMessage(Players.MESSAGE_TYPE Class, int Sender, String Message){
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
			PlayerLog.LogAction(LOGTYPE.LOGIN, GTID, Banner, message);
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
			PlayerLog.LogAction(LOGTYPE.LOGIN, GTID, UnBanner, message);
			return true;
		} catch (SQLException e) {
			GlobalExceptionHandler GEH = new GlobalExceptionHandler();
		GEH.uncaughtException(Thread.currentThread(), (Throwable) e, "Unable to unban player " + GTID);
			return false;
		}
	}
	public PlayerPokemon catchNew(int lDEX, int lLevel, int lEXP, String Name, PokemonStats CurrentStats, PokemonStats BaseStats){
		PlayerPokemon poke = new PlayerPokemon(lDEX, lLevel, lEXP, Name, CurrentStats, BaseStats, this);
		if(poke.GPID > 0){
		Party.add(poke);
		PreparedStatement loginPlayer = null;
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
		if(Connection != null){
			Players.SendLocationUpdate(GTID, l);
		}
		location = l;
	}
	public void Teleport(Location l){
		if(Connection != null){
			Players.SendLocationUpdate(GTID, l);
		}
		location = l;
	}
	public void ReloadPokemon(){
		Party.clear();
		ResultSet PokemonResult = Main.SQL.Query("SELECT `GPID` FROM `CAUGHT_POKEMON` WHERE `GTID`='" + GTID  + "'");
		try {
			while(PokemonResult.next()){
				Logger.log_player(Logger.LOG_VERB_HIGH, "Loading new pokemon from GPID: " + PokemonResult.getInt("GPID"),GTID);
				Party.add(new PlayerPokemon(PokemonResult.getInt("GPID")));
			}
		} catch (SQLException e) {
			GlobalExceptionHandler GEH = new GlobalExceptionHandler();
			GEH.uncaughtException(Thread.currentThread(), (Throwable) e);
		}
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
		 Players.RemovePlayer(this);
		 if(Party != null){
			 for(PlayerPokemon pp : Party) Players.RemovePokemon(pp.GPID);
		 }
		if(isLoggedIn) {
		Logger.log_player(Logger.LOG_VERB_HIGH, "Logging out player...", GTID);
		if (GTID == 0){
			Logger.log_player(Logger.LOG_ERROR, "Invalid GTID: Could not sign out.", GTID);
		}else{
			PlayerLog.LogAction(LOGTYPE.LOGOUT, GTID);
		}
		GTID = 0;
		isLoggedIn = false;
		}
	}
	
	public void ReloadPlayer(){
		
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
		
		String message = "Unspecified";
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
