package com.pokemonnxt.loader;

import java.util.ArrayList;
import java.util.HashMap;

import com.pokemonnxt.gameserver.Client;
import com.pokemonnxt.gameserver.ServerVars;


public class LoadBalancer {
	
	public static HashMap<Byte,Node> Nodes = new HashMap<Byte,Node>();
	public static HashMap<Byte,Integer> NodeDensity = new HashMap<Byte,Integer>();
	public static HashMap<Integer,Byte> Connections = new HashMap<Integer,Byte>();
	final static Relay[] Relays = new Relay[ServerVars.MaxRelayConnections];
	
	public LoadBalancer() {
		// TODO Auto-generated constructor stub
		if (ServerVars.isGameServer) addNode(new Node("127.0.0.1",ServerVars.gameServerSocket));
	}
	
	
	
	
	
	
	public void addNode(Node n){
		byte NID = (byte) (Nodes.size() +1);
		Nodes.put(NID, n);
		NodeDensity.put(NID, 0);
	}
	
	public static int GenerateConnectionID(){
		int ID = 0;
		ID = (int) (Math.random() * 100000);
		 while(Connections.containsKey(ID)){
			  ID = (int) (Math.random() * 100000);
		  }
		 return ID;
	}
}
