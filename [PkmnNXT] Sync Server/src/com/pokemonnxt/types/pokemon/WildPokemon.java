package com.pokemonnxt.types.pokemon;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.pokemonnxt.gameserver.GlobalExceptionHandler;
import com.pokemonnxt.gameserver.Logger;
import com.pokemonnxt.gameserver.Main;
import com.pokemonnxt.gameserver.Players;
import com.pokemonnxt.types.pokemon.Pokemon.STATUS;

public class WildPokemon extends Pokemon{

	public WildPokemon (int ID){
		GPID = ID;
		loadPokemon();
	}
	public WildPokemon (int lDEX, int lLevel){
		DEX = lDEX;
		Level = lLevel;
		
		
		Pokemon.IEVs newEVs = new Pokemon.IEVs(Pokemon.IEVs.EV);
		Pokemon.IEVs newIVs = new Pokemon.IEVs(Pokemon.IEVs.IV);
		// TODO: Insert backdoor so people I don't like always get shit IVs
		
		RecalculateBasicStats();
		savePokemon();
	}
	
}
