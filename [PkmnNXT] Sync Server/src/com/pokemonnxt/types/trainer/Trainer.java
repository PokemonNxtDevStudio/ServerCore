package com.pokemonnxt.types.trainer;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.pokemonnxt.types.Asset;
import com.pokemonnxt.types.Location;
import com.pokemonnxt.types.pokemon.Pokemon;
//GIT UPDATE
public abstract class Trainer extends Asset{
	@Expose public List<Pokemon> Party = new ArrayList<Pokemon>();
	@Expose public List<Integer> Items = new ArrayList<Integer>();
	
	public String Name;
	public int GTID;
	public short BaseModel;
}
