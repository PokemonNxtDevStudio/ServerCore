package com.pokemonnxt.node;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.pokemonnxt.types.trainer.PlayableTrainer;
//GIT UPDATE
public class Nodes {
	public List<OuterNode> Nodes = new ArrayList<OuterNode>();
	NodeServer nodeServer = new NodeServer();
	
	private void addNode(OuterNode ON){
		Nodes.add(ON);
	}
	public void TransferPlayerToNode(OuterNode N, PlayableTrainer P){
		
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
