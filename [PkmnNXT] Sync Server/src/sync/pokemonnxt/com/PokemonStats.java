package sync.pokemonnxt.com;

import com.google.gson.annotations.Expose;

public class PokemonStats {
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
	
PokemonStats(int A, int SA, int D, int SD, int S, int ACC, int E, int H){
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
