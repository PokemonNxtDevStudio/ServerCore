package com.pokemonnxt.node;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.pokemonnxt.gameserver.GlobalExceptionHandler;

public class NodeCommunicator extends Thread implements AutoCloseable{

	private OuterNode Master;
	private Socket Sock;
	
	private DataInputStream is;
	private DataOutputStream os;
	
	public NodeCommunicator(Socket S) {
		  try {
			  Master = new OuterNode(this);
			Sock = S;
			is = new DataInputStream(Sock.getInputStream());
			os = new DataOutputStream(Sock.getOutputStream());
		} catch (IOException e) {
			GlobalExceptionHandler GEH = new GlobalExceptionHandler();
			GEH.uncaughtException(Thread.currentThread(), (Throwable) e, "Error attaching streams to node socket");
		}
	      
	}
	public NodeCommunicator(OuterNode ON, String S) {
		try {
			Master = ON;
			Socket So = new Socket(S,32232);
			Sock = So;
			is = new DataInputStream(Sock.getInputStream());
			os = new DataOutputStream(Sock.getOutputStream());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			GlobalExceptionHandler GEH = new GlobalExceptionHandler();
			GEH.uncaughtException(Thread.currentThread(), (Throwable) e, "Error whilst connecting to new node");
		}
		
		
	}
	
	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
