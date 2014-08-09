package sync.pokemonnxt.com;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.annotations.Expose;

public class Battle {

	@Expose public ArrayList<Player> TeamA = new ArrayList<Player>();
	@Expose public ArrayList<Player> TeamB = new ArrayList<Player>();
	
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
	
	public Battle(ArrayList<Player> Blue, ArrayList<Player> Red){
		// Blue team are the proposers, but send an invite to all anyways
		State = 0;
		
	}
}
