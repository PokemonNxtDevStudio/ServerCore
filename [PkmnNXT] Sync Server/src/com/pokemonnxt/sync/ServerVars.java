package com.pokemonnxt.sync;

public class ServerVars {

// LocUpdateDist
// The distance at which location updates are made
public static int LocUpdateDist = 50;

// MaxDEXID
// Pokémon beyond this ID will not be loaded from the database in any way
public static int MaxDEXID = 50;

// MaxConnections
// The maximum amount of connections that can be made at once
public static int MaxConnections = 9000;

//MaxNodes
//The maximum amount of other servers this server can connect to
public static int MaxNodes = 64;

//Timeout
//The maximum amount of time a connection can go without inbound traffic (in milliseconds) before being kicked
public static int Timeout = 20000;

}
