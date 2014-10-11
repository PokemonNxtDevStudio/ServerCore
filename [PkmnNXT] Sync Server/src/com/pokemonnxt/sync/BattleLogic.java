package com.pokemonnxt.sync;

import com.pokemonnxt.types.Move;
import com.pokemonnxt.types.pokemon.BasePokemon;
import com.pokemonnxt.types.pokemon.Pokemon;
import com.pokemonnxt.types.pokemon.Pokemon.TYPE;

public class BattleLogic {
	

	
	public static double CalculateDamage(Pokemon Attacking, Pokemon Defending, Attack Executed){
	double totalDamage = 0;
	Move move = Cache.Movedex[Executed.MID];
	BasePokemon AttackingPokemon = Cache.Pokedex[Attacking.DEX];
	BasePokemon DefendingPokemon = Cache.Pokedex[Defending.DEX];
	// First, calculate modifier
		double STAB = 1;
		double TYPE = 1;
		double CRITICAL = 1;
		double OTHER = 1; // TODO Implement the fuck out of this shit
		double RAND = 1; // TODO Also, Make this shit random. Between 0.85 and 1.
		
		if(move.Type == AttackingPokemon.type1 || move.Type == AttackingPokemon.type2) STAB = 1.5;
		TYPE = GetTotalTypeEffectiveness(move.Type,DefendingPokemon);
	double modifier = STAB * TYPE * CRITICAL * OTHER * RAND;
				
	// Now do the actual damage
	// Equation taken from http://cdn.bulbagarden.net/upload/4/47/DamageCalc.png
		
		totalDamage = (((2 * Attacking.Level + 10)/(250)) * (Attacking.CurrentStats.Attack/Defending.CurrentStats.Defense) * Executed.Power)*modifier;
	return totalDamage;
	}
	
	public static double GetTotalTypeEffectiveness(Pokemon.TYPE attack, BasePokemon Defending){
		return GetTypeEffectiveness(attack, Defending.type1) * GetTypeEffectiveness(attack, Defending.type2);
	}
	

	
	private static double GetTypeEffectiveness(Pokemon.TYPE Attack, Pokemon.TYPE Defending){
		// Type effectiveness chart provided by
		// http://files.enjin.com.s3.amazonaws.com/520778/modules/forum/attachments/typechart_zpsed5878fa_1385342587.png
		if (Defending == Pokemon.TYPE.UNDEFINED) return 1;
		switch(Defending){
		case NORMAL:
			switch(Attack){
			case FIGHTING: 
				return 2;
			case GHOST: 
				return 0;
			default:
				return 1;
			}
		case FIRE:
			switch(Attack){
			case GRASS: case ELECTRIC:
				return 2;
			case FIRE: case WATER: case ICE: case STEEL:
				return 0.5;
			default:
				return 1;
			}
		case WATER:
			switch(Attack){
			case GRASS: case ELECTRIC:
				return 2;
			case FIRE: case WATER: case ICE: case STEEL:
				return 0.5;
			default:
				return 1;
			}
		case GRASS:
			switch(Attack){
			case FIRE: case ICE: case POISON: case FLYING: case BUG:
				return 2;
			case ELECTRIC: case WATER: case GRASS: case GROUND:
				return 0.5;
			default:
				return 1;
			}
		case ELECTRIC:
			switch(Attack){
			case GROUND:
				return 2;
			case ELECTRIC: case FLYING:
				return 0.5;
			default:
				return 1;
			}
		case ICE:
			switch(Attack){
			case FLYING: case PSYCHIC:
				return 2;
			case BUG: case ROCK: case DARK:
				return 0.5;
			default:
				return 1;
			}
		case FIGHTING:
			switch(Attack){
			case FLYING: case PSYCHIC:
				return 2;
			case BUG: case ROCK: case DARK:
				return 0.5;
			default:
				return 1;
			}
		case POISON:
			switch(Attack){
			case PSYCHIC: case GROUND:
				return 2;
			case GRASS: case FIGHTING: case POISON: case BUG:
				return 0.5;
			default:
				return 1;
			}
		case GROUND:
			switch(Attack){
			case WATER: case GRASS: case ICE:
				return 2;
			case POISON: case ROCK:
				return 0.5;
			case ELECTRIC:
				return 0;
			default:
				return 1;
			}
		case FLYING:
			switch(Attack){
			case ELECTRIC: case ICE:
				return 2;
			case GRASS: case FIGHTING: case BUG:
				return 0.5;
			case GROUND:
				return 0;
			default:
				return 1;
			}
		case PSYCHIC:
			switch(Attack){
			case BUG: case GHOST: case DARK:
				return 2;
			case FIGHTING: case PSYCHIC:
				return 0.5;
			default:
				return 1;
			}
		case BUG:
			switch(Attack){
			case FIRE: case FLYING: case ROCK:
				return 2;
			case GRASS: case FIGHTING: case GROUND:
				return 0.5;
			default:
				return 1;
			}
		case ROCK:
			switch(Attack){
			case WATER: case GRASS: case FIGHTING: case GROUND: case STEEL:
				return 2;
			case NORMAL: case FIRE: case FLYING:
				return 0.5;
			default:
				return 1;
			}
		case GHOST:
			switch(Attack){
			case GHOST: case DARK:
				return 2;
			case POISON: case BUG:
				return 0.5;
			case NORMAL: case FIGHTING:
				return 0;
			default:
				return 1;
			}
		case DRAGON:
			switch(Attack){
			case ICE: case DRAGON:
				return 2;
			case ELECTRIC: case GRASS: case WATER: case FIRE:
				return 0.5;
			default:
				return 1;
			}
		case DARK:
			switch(Attack){
			case BUG: case FIGHTING:
				return 2;
			case GHOST: case DARK:
				return 0.5;
			case PSYCHIC:
				return 0;
			default:
				return 1;
			}
		case STEEL:
				switch(Attack){
				case FIRE: case FIGHTING: case GROUND:
					return 2;
				case ELECTRIC: case WATER:
					return 1;
				case POISON:
					return 0;
				default:
					return 0.5;
				}
		default:
			return 1;
		}
	
	}
	
	
}
