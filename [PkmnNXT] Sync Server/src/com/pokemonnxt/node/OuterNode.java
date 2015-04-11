package com.pokemonnxt.node;


import java.util.ArrayList;
import java.util.List;
//GIT UPDATE
public class OuterNode {

	NodeCommunicator Connection;
	boolean Valid;
	
	public List<Integer> Users = new ArrayList<Integer>();
	
	
	public OuterNode(NodeCommunicator NC) {
		Connection = NC;
	}
	public OuterNode(String IP){
		Connection = new NodeCommunicator(this,IP);
		
	}
	
	public void submitTransfer(){
		
	}

}
