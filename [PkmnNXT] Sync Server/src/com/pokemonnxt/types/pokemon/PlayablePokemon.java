package com.pokemonnxt.types.pokemon;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.pokemonnxt.gameserver.GlobalExceptionHandler;
import com.pokemonnxt.gameserver.Logger;
import com.pokemonnxt.gameserver.Main;
import com.pokemonnxt.gameserver.ServerAssets;
import com.pokemonnxt.types.Location;
import com.pokemonnxt.types.pokemon.Pokemon;
import com.pokemonnxt.types.trainer.PlayableTrainer;

public class PlayablePokemon extends Pokemon  implements AutoCloseable{

	public PlayableTrainer owner;

	@Expose public String Name;
	@Expose public boolean inBall;
	
	public PlayablePokemon (int ID){
	GPID = ID;
			loadPokemon();
		
	}


	public PlayablePokemon(short lDEX, int lLevel, int lEXP, String lName, Pokemon.Stats lCurrentStats, Pokemon.Stats lBaseStats, PlayableTrainer trainer){
		Logger.log_server(Logger.LOG_VERB_LOW, "Saving GPID" + GPID + " from packet:");
		 PreparedStatement insertPokemon = null;
		int boxloc = 1;
		for(PlayablePokemon p : trainer.Party){
		/* Box number generator. 
			if (p.BOXLoc >= boxloc){
				boxloc = p.BOXLoc+1;
			}
			*/
		}

		try {
			insertPokemon = Main.SQL.SQL.prepareStatement("INSERT INTO `NXT_GAME`.`POKEMON` (`GTID`, `DEX`, `LOC`, `Name`, `Level`, `EXP`) VALUES (?, ?, ?, ?, ?, ?)",Statement.RETURN_GENERATED_KEYS);
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
				owner = trainer;
				Level = lLevel;
				EXP = lEXP;
				PreparedStatement currentStatUpdate = Main.SQL.SQL.prepareStatement("INSERT INTO `NXT_GAME`.`POKEMON_STATS` (`GPID`, `TYPE`, `Attack`, `SpAttack`, `Defense`, `SpDefense`, `Accuracy`, `Speed`, `Evasion`, `HP`) VALUES (?,?,?,?,?,?,?,?,?,?)");
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
				PreparedStatement baseStatUpdate = Main.SQL.SQL.prepareStatement("INSERT INTO `NXT_GAME`.`POKEMON_STATS` (`GPID`, `TYPE`, `Attack`, `SpAttack`, `Defense`, `SpDefense`, `Accuracy`, `Speed`, `Evasion`, `HP`) VALUES (?,?,?,?,?,?,?,?,?,?)");
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
				ServerAssets.AddPokemon(this);
			}else{
				Logger.log_server(Logger.LOG_ERROR, "Pokemon ID " + GPID + " WAS UNABLE TO BE SAVED :/");
			}
		} catch (SQLException e) {
			GlobalExceptionHandler GEH = new GlobalExceptionHandler();
			GEH.uncaughtException(Thread.currentThread(), (Throwable) e, "SQL Exception encountered whilst saving GPID" + GPID);
		}
		ServerAssets.AddPokemon(this);
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
		location.Move(lLOC);
		return true;
	}
	
	
	
	/*
	public boolean BoxMove(int NewLoc){
		
		Logger.log_server(Logger.LOG_VERB_LOW, "Moving GPID" + GPID + " to " + NewLoc);
			int OldLoc = BOXLoc;
			ResultSet OccupyingPokemon = Main.SQL.Query("SELECT * FROM `POKEMON` WHERE `GTID`='" + owner.GTID  + "' AND `LOC`='" + NewLoc + "'");
			try {
				if(OccupyingPokemon.next() == true){ // if there's a pokemon in the new location
					int OccupyingPokemonGPID = OccupyingPokemon.getInt("GPID"); // move it to the old one
					int MoveOne = Main.SQL.Update("UPDATE `POKEMON` SET `LOC`='" + OldLoc + "' WHERE `GPID`='" + OccupyingPokemonGPID  + "'");
					if(MoveOne > 0){
						Logger.log_server(Logger.LOG_PROGRESS, "Moved occupying pokemon GPID" + OccupyingPokemonGPID + " to " + OldLoc);
					}else{
						Logger.log_server(Logger.LOG_ERROR, "Unable to move occupying pokemon GPID" + OccupyingPokemonGPID + " to " + OldLoc);
						return false;
					}
				}
				int MoveTwo = Main.SQL.Update("UPDATE `POKEMON` SET `LOC`='" + NewLoc + "' WHERE `GPID`='" + GPID  + "'");
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
	*/
	public void CommitToDB(){
		Logger.log_server(Logger.LOG_PROGRESS, "Commiting GPID" + GPID + " to the database...");
		PreparedStatement UpdatePokemon = null;
		PreparedStatement UpdateStatsA = null;
		PreparedStatement UpdateStatsB = null;
		try {
			UpdatePokemon = Main.SQL.SQL.prepareStatement("UPDATE `NXT_GAME`.`POKEMON` SET `GTID`=? " +
					"`Name`=? `Level`=? `EXP`=? WHERE GPID='" + GPID + "'");
			UpdatePokemon.setInt(1, owner.GTID);
			UpdatePokemon.setString(2, Name);
			UpdatePokemon.setInt(3, Level);
			UpdatePokemon.setInt(4, EXP);
			UpdatePokemon.execute();
			UpdateStatsA = Main.SQL.SQL.prepareStatement("UPDATE `NXT_GAME`.`POKEMON_STATS` SET " +
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
			UpdateStatsB = Main.SQL.SQL.prepareStatement("UPDATE `NXT_GAME`.`POKEMON_STATS` SET " +
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
		ServerAssets.RemovePokemon(GPID);
	}
}
