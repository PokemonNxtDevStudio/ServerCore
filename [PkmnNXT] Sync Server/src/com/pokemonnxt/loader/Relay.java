package com.pokemonnxt.loader;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.Map.Entry;

public class Relay{

	int ConnectionID;
	Node EndNode;
	Socket playerSocket;
	Socket serverSocket;
	
	  private DataInputStream  PlayerIS = null;
	  private DataOutputStream PlayerOS = null;
	  
	  private DataInputStream  ServerIS = null;
	  private DataOutputStream ServerOS = null;
	  
	public Relay(Socket in) {
		ConnectionID = LoadBalancer.GenerateConnectionID();
		LoadBalancer.Connections.put(ConnectionID, (byte) 0);
		playerSocket = in;
		
	}
	public void Initiate(){
		Entry<Byte,Integer> Smallest = null;
		for(Entry<Byte,Integer> Node : LoadBalancer.NodeDensity.entrySet()){
			if(Smallest == null) Smallest = Node;
			if(Node.getValue() < Smallest.getValue()){
				Smallest = Node;
			}
		}
		EndNode = LoadBalancer.Nodes.get(Smallest.getKey());
		LoadBalancer.Connections.put(ConnectionID, Smallest.getKey());
		
		try {
			PlayerIS = new DataInputStream(playerSocket.getInputStream());
			PlayerOS = new DataOutputStream(playerSocket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		TransferTo(EndNode);
	}
	
	public void TransferTo(Node newNode){
		if(serverSocket != null){
			
		}
		
		stop();
		
		try {
			serverSocket = new Socket(newNode.IP,newNode.Port);
			ServerIS = new DataInputStream(serverSocket.getInputStream());
			ServerOS = new DataOutputStream(serverSocket.getOutputStream());
			
			EndNode = newNode;
			start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	IOMirror Incoming;
	IOMirror Outgoing;
	
	public void stop(){
		if (Incoming != null) Incoming.shutdown = true;
		if (Outgoing != null) Outgoing.shutdown = true;
		if (Incoming != null) Incoming = null;
		if (Outgoing != null) Outgoing = null;
	}
	
	public void start(){
		Incoming = new IOMirror(PlayerIS,ServerOS);
		Outgoing = new IOMirror(ServerIS,PlayerOS);
		Outgoing.start();
		Incoming.start();
	}
	//GIT UPDATE
	public boolean isRunning(){
		if (!Incoming.isAlive() || Incoming.shutdown || !Outgoing.isAlive() || Outgoing.shutdown){
			Outgoing.shutdown = true;
			Incoming.shutdown = true;
			return false;
		}else{
			return true;
		}
	}
	
	
	public class IOMirror extends Thread{
		DataInputStream IN;
		DataOutputStream OUT;
		public IOMirror(DataInputStream lIN, DataOutputStream lOUT){
			IN = lIN;
			OUT = lOUT;
		}
		private boolean shutdown;
		public void run(){
			
			try {
				while(!shutdown){
				OUT.writeByte(IN.readByte());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			shutdown = true;
		}
	}
}
