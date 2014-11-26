package com.pokemonnxt.gameserver;

public class Random {

	/*
	 * Secure (relating to IDs) random numbers should be generated
	 * in accordance with RFC 4086 
	 * http://tools.ietf.org/html/rfc4086
	 * 
	 */
	
	static java.util.Random random = new java.util.Random();
	public Random() {
		// TODO Auto-generated constructor stub
	}
	public static double quickRand(int MIN, int MAX) {
		    //get the range, casting to long to avoid overflow problems
		    long range = (long)MIN - (long)MAX + 1;
		    // compute a fraction of the range, 0 <= frac < range
		    long fraction = (long)(range * random.nextDouble());
		    return (fraction + MAX);    
		    
	}

}
