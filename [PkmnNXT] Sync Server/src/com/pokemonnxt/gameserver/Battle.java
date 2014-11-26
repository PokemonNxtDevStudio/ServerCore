package com.pokemonnxt.gameserver;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.annotations.Expose;
import com.pokemonnxt.types.trainer.Trainer;

public class Battle {

	@Expose public ArrayList<Trainer> TeamA = new ArrayList<Trainer>();
	@Expose public ArrayList<Trainer> TeamB = new ArrayList<Trainer>();
	
	public HashMap<Integer, Integer> States = new HashMap<Integer, Integer>();
	
	@Expose int GBID;
	@Expose int State;
	/*
	 * State Variable Explanation
	 * -1 = Invalid Battle Suggestion
	 * 0 = Battle Suggested
	 * 1 = Battle Agreed
	 * 2 = Battle Info Exchange in progress
	 * 3 = Battle Ready
	 * (Transition to 4 means start)
	 * 4 = Battle in progress
	 * (Transition to 5 means battle over)
	 * 5 = Battle Won
	 * 6 = Battle over
	 */
	
	public Battle(ArrayList<Trainer> Blue, ArrayList<Trainer> Red){
		// Blue team are the proposers, but send an invite to all anyways
		State = 0;
		
	}
}
