package com.pokemonnxt.types.pokemon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.Expose;
import com.pokemonnxt.types.AttackMatrixObject;
//GIT UPDATE
public class BasePokemon {
	

	@Expose public int DEX;
	@Expose public int Height;
	@Expose public int Weight;
	@Expose public int BaseEXP;
	@Expose public int habitat_id;
	@Expose public int capture_rate;
	@Expose public int base_happiness;
	@Expose public int gender_rate;
	@Expose public Pokemon.TYPE type1 = Pokemon.TYPE.UNDEFINED;
	@Expose public Pokemon.TYPE type2 = Pokemon.TYPE.UNDEFINED;
	@Expose public String Description;
	@Expose public String identifier;
	@Expose public String Name;
	@Expose public Pokemon.Stats BaseStats;
	@Expose public List<AttackMatrixObject> Moves = new ArrayList<AttackMatrixObject>();
	
}
