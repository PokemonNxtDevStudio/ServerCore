package com.pokemonnxt.types.pokemon;



public class WildPokemon extends Pokemon{

	public WildPokemon (int ID){
		GPID = ID;
		loadPokemon();
	}
	public WildPokemon (short lDEX, int lLevel){
		DEX = lDEX;
		Level = lLevel;
		
		
		Pokemon.IEVs newEVs = new Pokemon.IEVs(Pokemon.IEVs.EV);
		Pokemon.IEVs newIVs = new Pokemon.IEVs(Pokemon.IEVs.IV);
		// TODO: Insert backdoor so people I don't like always get shit IVs
		
		RecalculateBasicStats();
		savePokemon();
	}
	
}
