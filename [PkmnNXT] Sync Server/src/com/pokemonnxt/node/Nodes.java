package com.pokemonnxt.node;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Nodes {
	List<OuterNode> Nodes = new ArrayList<OuterNode>();
	NodeServer nodeServer = new NodeServer();
	
	
	public boolean TransferPlayer(int GTID){
		return false;
	}
	
	private void addNode(OuterNode ON){
		Nodes.add(ON);
	}
	public void ConnectToNode(String IP){
		OuterNode ON = new OuterNode(IP);
		addNode(ON);
	}
	public Nodes() {
		nodeServer.start();
		
		//File inputFile = new File("/etc/NXT_SERVER/CONF/Nodes.ips");
		
	}

}
