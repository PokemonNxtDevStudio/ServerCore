package com.pokemonnxt.gameserver;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;



import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pokemonnxt.types.AttackMatrixObject;
import com.pokemonnxt.types.Move;
import com.pokemonnxt.types.pokemon.Pokemon.Stats;
import com.pokemonnxt.types.pokemon.BasePokemon;
import com.pokemonnxt.types.pokemon.Pokemon;

public class Cache {
	
public static String moveListJSON;
public static String pokemonListJSON;
public static BasePokemon[] Pokedex = new BasePokemon[512]; 
public static Move[] Movedex = new Move[1024]; 
public static IPPermission[] IPPermissions = new IPPermission[1024]; 


public static void updateMoveList(){
	ResultSet Results = Main.SQL.QueryBase("SELECT * FROM `moves`");
	ArrayList<Move> Moves = new ArrayList<Move>();
	Logger.log_server(Logger.LOG_VERB_HIGH, "Updating Moves Cache...");
	try {
		while(Results.next()){
			Move newMove = new Move();
			newMove.Name = Results.getString("identifier");
			newMove.MID = Results.getInt("id");
			newMove.Power = Results.getInt("power");
			newMove.PP = Results.getInt("pp");
			newMove.Accuracy = Results.getInt("accuracy");
			int type = Results.getInt("type_id");
			if (type>18) type = -1;
			newMove.Type = Pokemon.TYPE.valueOf(type);
			newMove.Target_ID = Results.getInt("target_id");
			ResultSet Desc = Main.SQL.QueryBase("SELECT * FROM `move_flavor_text` WHERE `move_id` = '" + newMove.MID + "'");
			if(Desc.next()){
			newMove.Description = Desc.getString("flavor_text");
			}else{
				newMove.Description = "!!! MOVE DESCRIPTION MISSING FROM DATABASE !!!";
			}
			Moves.add(newMove);
			if(newMove.MID<Movedex.length-1)  Movedex[newMove.MID] = newMove;
		}
		Gson gson = new GsonBuilder().create();
		moveListJSON = gson.toJson(Moves);
		Logger.log_server(Logger.LOG_VERB_HIGH, "Moves Cache Updated");
	} catch (SQLException e) {
		GlobalExceptionHandler GEH = new GlobalExceptionHandler();
		GEH.uncaughtException(Thread.currentThread(), (Throwable) e,"Error whilst updaing moves cache: ");
	}
}

public static void updatePokemonList(){
	ResultSet Results = Main.SQL.QueryBase("SELECT * FROM `pokemon` where `ID`<" + ServerVars.MaxDEXID);
	ArrayList<BasePokemon> Pokemons = new ArrayList<BasePokemon>();
	Logger.log_server(Logger.LOG_VERB_HIGH, "Updating Pokemon Cache...");
	try {
		while(Results.next()){
			BasePokemon newPokemon = new BasePokemon();
			newPokemon.DEX = Results.getInt("id");
			Logger.log_server(Logger.LOG_PROGRESS, "Loading Pokemon DEX" + newPokemon.DEX);
			newPokemon.identifier = Results.getString("identifier");
			newPokemon.BaseEXP = Results.getInt("base_experience");
			newPokemon.Height = Results.getInt("height");
			newPokemon.Weight = Results.getInt("weight");
			ResultSet BaseStatResults = Main.SQL.QueryBase("SELECT * FROM `pokemon_stats`");
			Pokemon.Stats BaseStats = new Pokemon.Stats(0,0,0,0,0,0,0,0);
			
			BaseStatResults.next();
			BaseStats.HP = BaseStatResults.getInt("base_stat");
			BaseStatResults.next();
			BaseStats.Attack = BaseStatResults.getInt("base_stat");
			BaseStatResults.next();
			BaseStats.Defense = BaseStatResults.getInt("base_stat");
			BaseStatResults.next();
			BaseStats.SpAttack = BaseStatResults.getInt("base_stat");
			BaseStatResults.next();
			BaseStats.SpDefense = BaseStatResults.getInt("base_stat");
			BaseStatResults.next();
			BaseStats.Speed = BaseStatResults.getInt("base_stat");
			BaseStatResults.next();
			BaseStats.Accuracy = BaseStatResults.getInt("base_stat");
			BaseStatResults.next();
			BaseStats.Evasion = BaseStatResults.getInt("base_stat");
			newPokemon.BaseStats = BaseStats;
			
			ResultSet Types = Main.SQL.QueryBase("SELECT * FROM `pokemon_types` WHERE `pokemon_id` = '" + newPokemon.DEX + "'");
			while(Types.next()){
				if(Types.getInt("slot") == 2){
					int type = Types.getInt("type_id");
					if (type>18) type = -1;
					newPokemon.type2 = Pokemon.TYPE.valueOf(type);
				}else{
					int type = Types.getInt("type_id");
					if (type>18) type = -1;
					newPokemon.type1 = Pokemon.TYPE.valueOf(type);
				}
			}
			
			ResultSet Desc = Main.SQL.QueryBase("SELECT * FROM `pokemon_species_flavor_text` WHERE `species_id` = '" + newPokemon.DEX + "' AND `language_id` = '9' ORDER BY `version_id` DESC LIMIT 1");
			if(Desc.next()){
			newPokemon.Description = Desc.getString("flavor_text");
			}else{
				newPokemon.Description = "!!! ENGLISH POKEDEX DESCRIPTION MISSING FROM DATABASE !!!";
			}
			ResultSet RealName = Main.SQL.QueryBase("SELECT * FROM `pokemon_species_names` WHERE `pokemon_species_id` = '" + newPokemon.DEX + "' AND `local_language_id` = '9' LIMIT 1");
			if(RealName.next()){
				newPokemon.Name = RealName.getString("name");
				}else{
					newPokemon.Name = "! " + newPokemon.identifier;
				}
			ResultSet Moves = Main.SQL.QueryBase("SELECT * FROM `pokemon_moves` WHERE `pokemon_id` = '" + newPokemon.DEX + "'");
			while(Moves.next()){
				AttackMatrixObject MMO = new AttackMatrixObject();
				MMO.Level = Moves.getInt("level");
				MMO.Method = Moves.getInt("pokemon_move_method_id");
				MMO.MID = Moves.getInt("move_id");
				newPokemon.Moves.add(MMO);
			}
			Pokemons.add(newPokemon);
			
			if(newPokemon.DEX<Pokedex.length-1) Pokedex[newPokemon.DEX] = newPokemon;
		}
		Gson gson = new GsonBuilder().create();
		pokemonListJSON = gson.toJson(Pokemons);
		Logger.log_server(Logger.LOG_VERB_HIGH, "Pokemon Cache Updated");
	} catch (SQLException e) {
		GlobalExceptionHandler GEH = new GlobalExceptionHandler();
		GEH.uncaughtException(Thread.currentThread(), (Throwable) e, "Error Updating Pokemon Cache");

	}
}

public static void updatePermissionsCache(){
	String everything = "";
	try(BufferedReader br = new BufferedReader(new FileReader("/etc/NXT_SERVER/CONF/IPs.csv"))) {
        StringBuilder sb = new StringBuilder();
        String line = br.readLine();

        while (line != null) {
            sb.append(line);
            sb.append(System.lineSeparator());
            line = br.readLine();
        }
       everything = sb.toString();
    } catch (IOException e) {
    	GlobalExceptionHandler GEH = new GlobalExceptionHandler();
		GEH.uncaughtException(Thread.currentThread(), (Throwable) e, "Unable to update permissions cache");
		return;
	}
	
	String[] Lines = everything.split(System.lineSeparator());
	int i = 0;
	while(i < IPPermissions.length){
		IPPermission IP = null;
		IPPermissions[i] = IP;
		i +=1;
	}
	 i = 0;
	for(String line : Lines){
		String[] cells = line.split(",");
		Logger.log_server(Logger.LOG_VERB_LOW,"Checking IP Perms for " +  cells[0]);
		IPPermission IP = new IPPermission();
		IP.Mask = cells[0];
		IP.Permission = Integer.parseInt(cells[1]);
		IP.Name = cells[2];
		IP.Authority = cells[3];
		IP.Message = cells[4];
		
		IPPermissions[i] = IP;
		i +=1;
	}
}


public static void updateCache(){
	Logger.log_server(Logger.LOG_PROGRESS, "Updating Cache");
	try {
		if (Main.SQL.BaseSQL.isClosed() == true){
			Logger.log_server(Logger.LOG_WARN, "Unable to update cache, connection closed.");
		}else{
			updateMoveList();
			updatePokemonList();
			updatePermissionsCache();
		}
	} catch (SQLException e) {
		GlobalExceptionHandler GEH = new GlobalExceptionHandler();
		GEH.uncaughtException(Thread.currentThread(), (Throwable) e,"Error whilst updaing cache ");
	}
}


}
