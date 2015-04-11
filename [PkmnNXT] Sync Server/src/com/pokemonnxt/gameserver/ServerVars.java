package com.pokemonnxt.gameserver;

import com.pokemonnxt.types.Location;

public class ServerVars {
	//GIT UPDATE

// isLoadBalancer
// Is this server acting as a load balancer for the other nodes?
public static boolean isLoadBalancer = false;
public static int loadBalancerSocket = 32232;
public static int MaxRelayConnections = 9001;

// isGameServer
// Does this server handle game data?
public static boolean isGameServer = true;
public static int gameServerSocket = 23323;
public static int MaxGameConnections = 9000;

// BalancedPackets
// Does this server handle packets from a load balancer?
public static boolean BalancedPackets = false;

// Spawn
// The location at which new players will spawn
public static Location Spawn = new Location(20L,20L,20L,(short)0,(short)0,(short)0);

// LocUpdateDist
// The distance at which location updates are made
public static int LocUpdateDist = 50;

// MaxDEXID
// Pokémon beyond this ID will not be loaded from the database in any way
public static int MaxDEXID = 50;


//MaxNodes
//The maximum amount of other servers this server can connect to
public static int MaxNodes = 64;

//Timeout
//The maximum amount of time a connection can go without inbound traffic (in milliseconds) before being kicked
public static int Timeout = 999999999;

//ServerID
//This server's ID
public static String ServerID = "X001";
}
