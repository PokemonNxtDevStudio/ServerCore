package sync.pokemonnxt.com;

import com.google.gson.annotations.Expose;

public class PacketCAPTURE {
	@Expose public PacketHeader header;
	@Expose public PACKETCapture payload;
	 
	public class PACKETCapture {
		@Expose public int DEX;
		@Expose public PokemonStats BaseStats;
		@Expose public PokemonStats CurrentStats;
		@Expose public String Name;
		@Expose public int Level;
		@Expose public int EXP;
	 }

}
