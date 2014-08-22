package com.pokemonnxt.sync;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.sql.PreparedStatement;


import com.google.gson.annotations.Expose;
import com.pokemonnxt.sync.Pokemon.TYPE;


public class PlayerPokemon implements AutoCloseable{
	public static enum STATUS {
		ERROR(-1), NORMAL(0), SLEEP(1), BURNT(2), PARALYSED(3);
        private  int value;
        
        private static Map<Integer, STATUS> map = new HashMap<Integer, STATUS>();

        static {
            for (STATUS legEnum : STATUS.values()) {
                map.put(legEnum.value, legEnum);
            }
        }

        

        public static STATUS valueOf(int legNo) {
            return map.get(legNo);
        }
        private STATUS(int value) {
                this.value = value;
        }
};   
	
@Expose public STATUS STATE;
@Expose public int DEX;
@Expose public int GPID;
@Expose public String Name;
@Expose public int BOXLoc;
@Expose public int Level;
@Expose public int EXP;
@Expose public int GTID;
@Expose public boolean inBall;
@Expose public Location LOC;
@Expose public List<Attack> Attacks = new ArrayList<Attack>();
@Expose public PokemonStats BasicStats ;
/*
 * Basic stats will contain the the normal values.
 */
@Expose public PokemonStats CurrentStats ;
/*
 * Basic stats will contain the current values, live in battle.
 */

private Player owner;

PlayerPokemon(int ID){
	Logger.log_server(Logger.LOG_PROGRESS, "Loading GPID" + ID + "...");
	ResultSet BasicInfo = Main.SQL.Query("SELECT * FROM `CAUGHT_POKEMON` WHERE `GPID`='" + ID  + "'");
	try {
		BasicInfo.next();
		STATE = STATUS.valueOf(BasicInfo.getInt("STATE"));
		GTID = BasicInfo.getInt("GTID");
		DEX = BasicInfo.getInt("DEX");
		Name = BasicInfo.getString("Name");
		BOXLoc = BasicInfo.getInt("LOC"); 
		EXP = BasicInfo.getInt("EXP"); 
		Level = BasicInfo.getInt("Level"); 
		ResultSet RBasicStats = Main.SQL.Query("SELECT * FROM `CAUGHT_POKEMON_STATS` WHERE `GPID`='" + ID  + "' AND `TYPE`='0'");
		RBasicStats.next();
		BasicStats = new PokemonStats(RBasicStats.getInt("Attack"), RBasicStats.getInt("SpAttack") ,RBasicStats.getInt("Defense"),RBasicStats.getInt("SpDefense"),RBasicStats.getInt("Speed"),RBasicStats.getInt("Accuracy"),RBasicStats.getInt("Evasion"),RBasicStats.getInt("HP"));
		ResultSet RCurrentStats = Main.SQL.Query("SELECT * FROM `CAUGHT_POKEMON_STATS` WHERE `GPID`='" + ID  + "' AND `TYPE`='1'");
		RCurrentStats.next();
		CurrentStats = new PokemonStats(RCurrentStats.getInt("Attack"), RCurrentStats.getInt("SpAttack") ,RCurrentStats.getInt("Defense"),RCurrentStats.getInt("SpDefense"),RCurrentStats.getInt("Speed"),RCurrentStats.getInt("Accuracy"),RCurrentStats.getInt("Evasion"),RCurrentStats.getInt("HP"));
		GPID = BasicInfo.getInt("GPID");
		Players.AddPokemon(this);
		
	} catch (SQLException e) {
		GlobalExceptionHandler GEH = new GlobalExceptionHandler();
		GEH.uncaughtException(Thread.currentThread(), (Throwable) e,"SQL Exception encountered whilst loading GPID" + GPID);
	}
	Players.AddPokemon(this);
	
}


PlayerPokemon(int lDEX, int lLevel, int lEXP, String lName, PokemonStats lCurrentStats, PokemonStats lBaseStats, Player trainer){
	Logger.log_server(Logger.LOG_VERB_LOW, "Saving GPID" + GPID + " from packet:");
	 PreparedStatement insertPokemon = null;
	int boxloc = 1;
	for(PlayerPokemon p : trainer.Pokemon){
		if (p.BOXLoc >= boxloc){
			boxloc = p.BOXLoc+1;
		}
	}

	try {
		insertPokemon = Main.SQL.SQL.prepareStatement("INSERT INTO `NXT_GAME`.`CAUGHT_POKEMON` (`GTID`, `DEX`, `LOC`, `Name`, `Level`, `EXP`) VALUES (?, ?, ?, ?, ?, ?)",Statement.RETURN_GENERATED_KEYS);
		insertPokemon.setInt(1, trainer.GTID);
		insertPokemon.setInt(2, lDEX);
		insertPokemon.setInt(3, boxloc);
		insertPokemon.setString(4, lName);
		insertPokemon.setInt(5, lLevel);
		insertPokemon.setInt(6, lEXP);
		GPID = insertPokemon.executeUpdate();
		ResultSet insertResult = insertPokemon.getGeneratedKeys();
		
		insertResult.first();
		GPID =  insertResult.getInt(1);
		GTID = trainer.GTID;
		if(GPID > 0){
			Name = lName;
			DEX = lDEX;
			// Stats = stats;
			BOXLoc = boxloc;
			owner = trainer;
			Level = lLevel;
			EXP = lEXP;
			PreparedStatement currentStatUpdate = Main.SQL.SQL.prepareStatement("INSERT INTO `NXT_GAME`.`CAUGHT_POKEMON_STATS` (`GPID`, `Type`, `Attack`, `SpAttack`, `Defense`, `SpDefense`, `Accuracy`, `Speed`, `Evasion`, `HP`) VALUES (?,?,?,?,?,?,?,?,?,?)");
			currentStatUpdate.setInt(1, GPID);
			currentStatUpdate.setInt(2, 1);
			currentStatUpdate.setInt(3, lBaseStats.Attack);
			currentStatUpdate.setInt(4, lBaseStats.SpAttack);
			currentStatUpdate.setInt(5, lBaseStats.Defense);
			currentStatUpdate.setInt(6, lBaseStats.SpDefense);
			currentStatUpdate.setInt(7, lBaseStats.Accuracy);
			currentStatUpdate.setInt(8, lBaseStats.Speed);
			currentStatUpdate.setInt(9, lBaseStats.Evasion);
			currentStatUpdate.setInt(10, lBaseStats.HP);
			currentStatUpdate.executeUpdate();
			BasicStats = lBaseStats;
			PreparedStatement baseStatUpdate = Main.SQL.SQL.prepareStatement("INSERT INTO `NXT_GAME`.`CAUGHT_POKEMON_STATS` (`GPID`, `Type`, `Attack`, `SpAttack`, `Defense`, `SpDefense`, `Accuracy`, `Speed`, `Evasion`, `HP`) VALUES (?,?,?,?,?,?,?,?,?,?)");
			baseStatUpdate.setInt(1, GPID);
			baseStatUpdate.setInt(2, 0);
			baseStatUpdate.setInt(3, lCurrentStats.Attack);
			baseStatUpdate.setInt(4, lCurrentStats.SpAttack);
			baseStatUpdate.setInt(5, lCurrentStats.Defense);
			baseStatUpdate.setInt(6, lCurrentStats.SpDefense);
			baseStatUpdate.setInt(7, lCurrentStats.Accuracy);
			baseStatUpdate.setInt(8, lCurrentStats.Speed);
			baseStatUpdate.setInt(9, lCurrentStats.Evasion);
			baseStatUpdate.setInt(10, lCurrentStats.HP);
			baseStatUpdate.executeUpdate();
			CurrentStats = lCurrentStats;
			Players.AddPokemon(this);
		}else{
			Logger.log_server(Logger.LOG_ERROR, "Pokemon ID " + GPID + " WAS UNABLE TO BE SAVED :/");
		}
	} catch (SQLException e) {
		GlobalExceptionHandler GEH = new GlobalExceptionHandler();
		GEH.uncaughtException(Thread.currentThread(), (Throwable) e, "SQL Exception encountered whilst saving GPID" + GPID);
	}
	Players.AddPokemon(this);
}


public void makeBurned(){
	if(owner != null){
		if(owner.Connection != null){
			owner.Connection.sendStatusUpdate(GPID, STATUS.BURNT, true);
		}
	}
}

public void ChangeName(String NewName){
	Name = NewName;
	CommitToDB();
}

public boolean Release(Location lLOC){
	if (inBall == false) return true;
	// TODO Broadcast Pokémon's Release
	
	inBall = false;
	return false;
}
public boolean Retreive(){
	if (inBall == true) return true;
	// TODO Broadcast Pokémon's Retrieval
	
	inBall = true;
	return false;
}
public boolean Move(Location lLOC){
	if(inBall) return false;
	
	// TODO Broadcast Position Change
	LOC = lLOC;
	return true;
}

public boolean BoxMove(int NewLoc){
	Logger.log_server(Logger.LOG_VERB_LOW, "Moving GPID" + GPID + " to " + NewLoc);
		int OldLoc = BOXLoc;
		ResultSet OccupyingPokemon = Main.SQL.Query("SELECT * FROM `CAUGHT_POKEMON` WHERE `GTID`='" + owner.GTID  + "' AND `LOC`='" + NewLoc + "'");
		try {
			if(OccupyingPokemon.next() == true){ // if there's a pokemon in the new location
				int OccupyingPokemonGPID = OccupyingPokemon.getInt("GPID"); // move it to the old one
				int MoveOne = Main.SQL.Update("UPDATE `CAUGHT_POKEMON` SET `LOC`='" + OldLoc + "' WHERE `GPID`='" + OccupyingPokemonGPID  + "'");
				if(MoveOne > 0){
					Logger.log_server(Logger.LOG_PROGRESS, "Moved occupying pokemon GPID" + OccupyingPokemonGPID + " to " + OldLoc);
				}else{
					Logger.log_server(Logger.LOG_ERROR, "Unable to move occupying pokemon GPID" + OccupyingPokemonGPID + " to " + OldLoc);
					return false;
				}
			}
			int MoveTwo = Main.SQL.Update("UPDATE `CAUGHT_POKEMON` SET `LOC`='" + NewLoc + "' WHERE `GPID`='" + GPID  + "'");
			if(MoveTwo > 0){
				Logger.log_server(Logger.LOG_PROGRESS, "Moved pokemon GPID" + GPID + " to " + NewLoc);
				BOXLoc = NewLoc;
			}else{
				Logger.log_server(Logger.LOG_ERROR, "Unable to move pokemon GPID" + GPID + " to " + NewLoc);
				return false;
			}
			return true;
		} catch (SQLException e) {
			GlobalExceptionHandler GEH = new GlobalExceptionHandler();
			GEH.uncaughtException(Thread.currentThread(), (Throwable) e, "Error moving GPID" + GPID);
			
		}
		
		
		return true;
	
}
public void CommitToDB(){
	Logger.log_server(Logger.LOG_PROGRESS, "Commiting GPID" + GPID + " to the database...");
	PreparedStatement UpdatePokemon = null;
	PreparedStatement UpdateStatsA = null;
	PreparedStatement UpdateStatsB = null;
	try {
		UpdatePokemon = Main.SQL.SQL.prepareStatement("UPDATE `NXT_GAME`.`CAUGHT_POKEMON` SET `GTID`=? " +
				"`LOC`=? `Name`=? `Level`=? `EXP`=? WHERE GPID='" + GPID + "'");
		UpdatePokemon.setInt(1, owner.GTID);
		UpdatePokemon.setInt(2, BOXLoc);
		UpdatePokemon.setString(3, Name);
		UpdatePokemon.setInt(4, Level);
		UpdatePokemon.setInt(5, EXP);
		UpdatePokemon.execute();
		UpdateStatsA = Main.SQL.SQL.prepareStatement("UPDATE `NXT_GAME`.`CAUGHT_POKEMON` SET `GTID`=? " +
				"`Attack`=? `SpAttack`=? `Defense`=? `SpDefense`=? `Accuracy`=? `Speed`=? `Evasion`=? `HP`=? WHERE GPID='" + GPID + "' AND `TYPE`='0'");
		UpdateStatsA.setInt(1, BasicStats.Attack);
		UpdateStatsA.setInt(2, BasicStats.SpAttack);
		UpdateStatsA.setInt(3, BasicStats.Defense);
		UpdateStatsA.setInt(4, BasicStats.SpDefense);
		UpdateStatsA.setInt(5, BasicStats.Accuracy);
		UpdateStatsA.setInt(6, BasicStats.Speed);
		UpdateStatsA.setInt(7, BasicStats.Evasion);
		UpdateStatsA.setInt(8, BasicStats.HP);
		UpdateStatsA.execute();
		UpdateStatsB = Main.SQL.SQL.prepareStatement("UPDATE `NXT_GAME`.`CAUGHT_POKEMON` SET `GTID`=? " +
				"`Attack`=? `SpAttack`=? `Defense`=? `SpDefense`=? `Accuracy`=? `Speed`=? `Evasion`=? `HP`=? WHERE GPID='" + GPID + "' AND `TYPE`='1'");
		UpdateStatsB.setInt(1, CurrentStats.Attack);
		UpdateStatsB.setInt(2, CurrentStats.SpAttack);
		UpdateStatsB.setInt(3, CurrentStats.Defense);
		UpdateStatsB.setInt(4, CurrentStats.SpDefense);
		UpdateStatsB.setInt(5, CurrentStats.Accuracy);
		UpdateStatsB.setInt(6, CurrentStats.Speed);
		UpdateStatsB.setInt(7, CurrentStats.Evasion);
		UpdateStatsB.setInt(8, CurrentStats.HP);
		UpdateStatsB.execute();
		Logger.log_server(Logger.LOG_VERB_LOW, "GPID" + GPID + " committed to DB.");
	} catch (SQLException e) {
		GlobalExceptionHandler GEH = new GlobalExceptionHandler();
		GEH.uncaughtException(Thread.currentThread(), (Throwable) e,"Error committing GPID" + GPID + " to the Database...");
	}

}


@Override
public void close() throws Exception {
	// TODO Auto-generated method stub
	Players.RemovePokemon(GPID);
}

}
