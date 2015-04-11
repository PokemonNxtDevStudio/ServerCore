package com.pokemonnxt.types.pokemon;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.pokemonnxt.gameserver.GlobalExceptionHandler;
import com.pokemonnxt.gameserver.Logger;
import com.pokemonnxt.gameserver.Main;
import com.pokemonnxt.types.pokemon.Pokemon.STATUS;
//GIT UPDATE
public class NPCPokemon extends Pokemon{

	public NPCPokemon (int ID){
		GPID = ID;
		loadPokemon();
	}
	
}
