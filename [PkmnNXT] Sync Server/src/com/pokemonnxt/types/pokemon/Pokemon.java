package com.pokemonnxt.types.pokemon;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.PreparedStatement;










import com.google.gson.annotations.Expose;
import com.pokemonnxt.gameserver.Cache;
import com.pokemonnxt.gameserver.GlobalExceptionHandler;
import com.pokemonnxt.gameserver.Logger;
import com.pokemonnxt.gameserver.Main;
import com.pokemonnxt.gameserver.Players;
import com.pokemonnxt.gameserver.Random;
import com.pokemonnxt.packets.CommTypes.POKEMON;
import com.pokemonnxt.types.Asset;
import com.pokemonnxt.types.Attack;
import com.pokemonnxt.types.Location;
import com.pokemonnxt.types.trainer.PlayableTrainer;
import com.pokemonnxt.types.trainer.Trainer;


public abstract class Pokemon extends Asset{
	
	@Expose public KIND Kind;
	@Expose public STATUS STATE;
	@Expose public int DEX;
	@Expose public int GPID;
	@Expose public int Level;
	@Expose public int EXP;
	@Expose public int GTID;
	@Expose public String Name;
	@Expose public List<Attack> Attacks = new ArrayList<Attack>();
	@Expose public Pokemon.Stats BasicStats ;
	/*
	 * Basic stats will contain the the normal values.
	 */
	@Expose public Pokemon.Stats CurrentStats ;
	/*
	 * Basic stats will contain the current values, live in battle.
	 */
	@Expose public Pokemon.IEVs IVs ;
	@Expose public Pokemon.IEVs EVs ;
	/*
	 * Fucking shit for fucking smogon fanboys. SMD.
	 */
	
	public static Pokemon getPokemon(int GPID){
		if (Players.Pokemon.containsKey(GPID)) return Players.Pokemon.get(GPID);
		KIND Kind = getKind(GPID);
		if(Kind == KIND.WILD){
			return new WildPokemon(GPID);
		}else if(Kind == KIND.PLAYER){
			return new PlayablePokemon(GPID);
		}else{
			return new NPCPokemon(GPID);
		}
	}
	
	public void loadPokemon(){
		Logger.log_server(Logger.LOG_PROGRESS, "Loading GPID" + GPID + "...");
		
		try {
			ResultSet BasicInfo = Main.SQL.Query("SELECT * FROM `POKEMON` WHERE `GPID`='" + GPID  + "'");
			BasicInfo.next();
			GPID = BasicInfo.getInt("GPID");
			STATE = STATUS.valueOf(BasicInfo.getInt("STATE"));
			GTID = BasicInfo.getInt("GTID");
			getKind();
			DEX = BasicInfo.getInt("DEX");
			Name = BasicInfo.getString("Name");
			EXP = BasicInfo.getInt("EXP"); 
			Level = BasicInfo.getInt("Level"); 
			
			ResultSet RBasicStats = Main.SQL.Query("SELECT * FROM `POKEMON_STATS` WHERE `GPID`='" + GPID  + "' AND `TYPE`='0'");
			RBasicStats.next();
			BasicStats = new Pokemon.Stats(RBasicStats.getInt("Attack"), RBasicStats.getInt("SpAttack") ,RBasicStats.getInt("Defense"),RBasicStats.getInt("SpDefense"),RBasicStats.getInt("Speed"),RBasicStats.getInt("Accuracy"),RBasicStats.getInt("Evasion"),RBasicStats.getInt("HP"));
			
			ResultSet RCurrentStats = Main.SQL.Query("SELECT * FROM `POKEMON_STATS` WHERE `GPID`='" + GPID  + "' AND `TYPE`='1'");
			RCurrentStats.next();
			CurrentStats = new Pokemon.Stats(RCurrentStats.getInt("Attack"), RCurrentStats.getInt("SpAttack") ,RCurrentStats.getInt("Defense"),RCurrentStats.getInt("SpDefense"),RCurrentStats.getInt("Speed"),RCurrentStats.getInt("Accuracy"),RCurrentStats.getInt("Evasion"),RCurrentStats.getInt("HP"));
			
			ResultSet IVResultSet = Main.SQL.Query("SELECT * FROM `POKEMON_IEVS` WHERE `GPID`='" + GPID  + "' AND `TYPE`='" + IEVs.IV + "'");
			IVResultSet.next();
			IVs = new Pokemon.IEVs(IVResultSet.getInt("Attack"), IVResultSet.getInt("SpAttack") ,IVResultSet.getInt("Defense"),IVResultSet.getInt("SpDefense"),IVResultSet.getInt("Speed"),IVResultSet.getInt("HP"));

			ResultSet EVResultSet = Main.SQL.Query("SELECT * FROM `POKEMON_IEVS` WHERE `GPID`='" + GPID  + "' AND `TYPE`='" + IEVs.EV + "'");
			EVResultSet.next();
			IVs = new Pokemon.IEVs(EVResultSet.getInt("Attack"), EVResultSet.getInt("SpAttack") ,EVResultSet.getInt("Defense"),EVResultSet.getInt("SpDefense"),EVResultSet.getInt("Speed"),EVResultSet.getInt("HP"));

			Players.AddPokemon(this);
			
		} catch (SQLException e) {
			GlobalExceptionHandler GEH = new GlobalExceptionHandler();
			GEH.uncaughtException(Thread.currentThread(), (Throwable) e,"SQL Exception encountered whilst loading GPID" + GPID + ". Pokemon Load Aborted.");
			unloadPokemon();
		}
	}
	
	public void savePokemon(){
		
	}
	
	public void unloadPokemon(){
		
		
	}
	
	
	public void RecalculateBasicStats(){
		// We're using GEN 3 logic here because gen 3 is the best gen ever
		BasicStats.HP = (((IVs.HP+(2*Cache.Pokedex[DEX].BaseStats.HP)+(EVs.HP/4)+100)*Level)/(100)) + 10;
		
		int Nature = 1;
		BasicStats.Attack = ((((IVs.Attack+(2*Cache.Pokedex[DEX].BaseStats.Attack)+(EVs.Attack/4))*Level)/100)+5)* Nature;
		BasicStats.Defense = ((((IVs.Defense+(2*Cache.Pokedex[DEX].BaseStats.Defense)+(EVs.Defense/4))*Level)/100)+5)* Nature;
		BasicStats.SpAttack= ((((IVs.SpAttack+(2*Cache.Pokedex[DEX].BaseStats.SpAttack)+(EVs.SpAttack/4))*Level)/100)+5)* Nature;
		BasicStats.SpDefense = ((((IVs.SpDefense+(2*Cache.Pokedex[DEX].BaseStats.SpDefense)+(EVs.SpDefense/4))*Level)/100)+5)* Nature;
		BasicStats.Speed = ((((IVs.Speed+(2*Cache.Pokedex[DEX].BaseStats.Speed)+(EVs.Speed/4))*Level)/100)+5)* Nature;
		
		// http://www.gamefaqs.com/boards/696959-pokemon-x/68539129
	}
	
	
	
	public POKEMON generatePayload(){
		 POKEMON payload =
				 POKEMON.newBuilder()
		.setDex(DEX)
		.setId(GPID)
		.build();
	return payload;
	}
	
	
	public KIND getKind(){
				if (GTID == 0){
					Kind = KIND.WILD;
				}else if (GTID > 0 ){
					Kind = KIND.PLAYER;
				}else{
					Kind = KIND.NPC;
				}
		return Kind;
	}
	public static KIND getKind(int GTID){
		if (GTID == 0){
			return KIND.WILD;
		}else if (GTID > 0 ){
			return KIND.PLAYER;
		}else{
			return KIND.NPC;
		}
	}
	
	public static enum KIND {
		ERROR(-1), PLAYER(0), WILD(1), NPC(2);
        private  int value;
        
        private static Map<Integer, KIND> map = new HashMap<Integer, KIND>();

        static {
            for (KIND legEnum : KIND.values()) {
                map.put(legEnum.value, legEnum);
            }
        }

        

        public static KIND valueOf(int legNo) {
            return map.get(legNo);
        }
        private KIND(int value) {
                this.value = value;
        }
	};
	public static enum TYPE {
		ERROR(-1), UNDEFINED(0), NORMAL(1), FIRE(10), WATER(11), GRASS(12), ELECTRIC(13), ICE(15), FIGHTING(2),
        POISON(4), GROUND(5), FLYING(3), PSYCHIC(14), BUG(7), ROCK(6),
        GHOST(8), DRAGON(16), DARK(17), FAIRY(18),STEEL(9);
        private  int value;
        
        private static Map<Integer, TYPE> map = new HashMap<Integer, TYPE>();

        static {
            for (TYPE legEnum : TYPE.values()) {
                map.put(legEnum.value, legEnum);
            }
        }

        

        public static TYPE valueOf(int legNo) {
            return map.get(legNo);
        }
        private TYPE(int value) {
                this.value = value;
        }
	};   
	public static enum STATUS {
		ERROR(-1), NORMAL(0), FAINTED(1), BURNT(2), PARALYSED(3), SLEEPING(4);
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
	public static class Stats {
	@Expose public int Attack;
	@Expose public int Defense;
	@Expose public int Speed;
	@Expose public int Accuracy;
	@Expose public int SpAttack;
	@Expose public int SpDefense;
	@Expose public int Evasion;
	@Expose public int HP;
	
	public static final  int TYPE_DEFAULT = 0;
	public static final  int TYPE_CURRENT = 1;
	
public Stats(int A, int SA, int D, int SD, int S, int ACC, int E, int H){
	Attack = A;
	SpAttack = SA;
	Defense = D;
	SpDefense = SD;
	Speed = S;
	Accuracy = ACC;
	Evasion = E;
	HP = H;
}
}
	public static class IEVs {
	@Expose public int Attack;
	@Expose public int Defense;
	@Expose public int Speed;
	@Expose public int SpAttack;
	@Expose public int SpDefense;
	@Expose public int HP;
	
	public static final  int IV = 0;
	public static final  int EV = 1;
	
	public IEVs(int A, int SA, int D, int SD, int S, int H){
		Attack = A;
		SpAttack = SA;
		Defense = D;
		SpDefense = SD;
		Speed = S;
		HP = H;
	}
	public IEVs(int MODE){
		if(MODE == IV){
			Attack = (int) Random.quickRand(0, 31);
			SpAttack = (int) Random.quickRand(0, 31);
			Defense = (int) Random.quickRand(0, 31);
			SpDefense = (int) Random.quickRand(0, 31);
			Speed = (int) Random.quickRand(0, 31);
			HP = (int) Random.quickRand(0, 31);
		}else{
			Attack = 0; 	// Fuck
			SpAttack = 0; 	// your
			Defense = 0;	// fucking
			SpDefense = 0;	// uber-training
			Speed = 0;		// IV
			HP = 0;			// Shit
		}
	}
}
	
	
	
}
