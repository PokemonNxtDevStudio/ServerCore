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
import com.pokemonnxt.packets.Communications;
import com.pokemonnxt.packets.Communications.LOCATION;
import com.pokemonnxt.sync.Attack;
import com.pokemonnxt.sync.GlobalExceptionHandler;
import com.pokemonnxt.sync.Logger;
import com.pokemonnxt.sync.Main;
import com.pokemonnxt.sync.Players;
import com.pokemonnxt.types.Asset;
import com.pokemonnxt.types.Location;
import com.pokemonnxt.types.trainer.PlayableTrainer;
import com.pokemonnxt.types.trainer.Trainer;


public abstract class Pokemon extends Asset{
	

	@Expose public STATUS STATE;
	@Expose public int DEX;
	@Expose public int GPID;
	@Expose public String Name;
	@Expose public int BOXLoc;
	@Expose public int Level;
	@Expose public int EXP;
	@Expose public int GTID;
	@Expose public boolean inBall;
	@Expose public List<Attack> Attacks = new ArrayList<Attack>();
	@Expose public Pokemon.Stats BasicStats ;
	/*
	 * Basic stats will contain the the normal values.
	 */
	@Expose public Pokemon.Stats CurrentStats ;
	/*
	 * Basic stats will contain the current values, live in battle.
	 */

	private Trainer owner;
	
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

}
