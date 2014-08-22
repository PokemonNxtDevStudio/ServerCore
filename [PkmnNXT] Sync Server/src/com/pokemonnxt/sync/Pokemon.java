package com.pokemonnxt.sync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;

public class Pokemon {
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

	@Expose public int DEX;
	@Expose public int Height;
	@Expose public int Weight;
	@Expose public int BaseEXP;
	@Expose public int habitat_id;
	@Expose public int capture_rate;
	@Expose public int base_happiness;
	@Expose public int gender_rate;
	@Expose public TYPE type1 = TYPE.UNDEFINED;
	@Expose public TYPE type2 = TYPE.UNDEFINED;
	@Expose public String Description;
	@Expose public String identifier;
	@Expose public String Name;
	@Expose public PokemonStats BaseStats;
	@Expose public List<AttackMatrixObject> Moves = new ArrayList<AttackMatrixObject>();
	
}
