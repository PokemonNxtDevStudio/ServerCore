package com.pokemonnxt.node;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;


import com.pokemonnxt.sync.GlobalExceptionHandler;
import com.pokemonnxt.sync.Logger;
import com.pokemonnxt.sync.ServerVars;

public class NodeServer extends Thread{

	public NodeServer() {
		// TODO Auto-generated constructor stub
	}
	public void Shutdown(){
		shutdown = true;
		try {
			serverSocket.close();
		} catch (IOException e) {
			GlobalExceptionHandler GEH = new GlobalExceptionHandler();
			GEH.uncaughtException(Thread.currentThread(), (Throwable) e);
			e.printStackTrace();
		}
	}
	private boolean shutdown = false;
	
	  // The server socket.
	  private static ServerSocket serverSocket = null;
	  // The client socket.
	  private static Socket clientSocket = null;

	  
	   final static NodeCommunicator[] Clients = new NodeCommunicator[ServerVars.MaxNodes];
	   
	   
	  public void run() {
		  StartServer();
	  }
	  private void StartServer() {
		 
		
	    // The default port number.
	    int portNumber = 32232;

	    try {
	    	
	      serverSocket = new ServerSocket(portNumber);
	    } catch (IOException e) {
	      System.out.println(e);
	    }
	    

	    /*
	     * Create a client socket for each connection and pass it to a new client
	     * thread.
	     */
	    while (true) {
	      try {
	    	  if (shutdown) break;
	        clientSocket = serverSocket.accept();
	        if (shutdown) break;
	        Logger.log_server(Logger.LOG_VERB_HIGH, "NODE CONNECTED: " + clientSocket.getRemoteSocketAddress().toString());
	        int i = 0;
	        for (i = 0; i < ServerVars.MaxNodes; i++) {
	          if (Clients[i] == null) {
	            (Clients[i] = new NodeCommunicator(clientSocket)).start();
	            break;
	          }
	        }
	        if (shutdown) break;
	        if (i == ServerVars.MaxNodes) {
	        	 PrintStream os = new PrintStream(clientSocket.getOutputStream());
	          os.println("[BUSY]");
	          os.close();
	          clientSocket.close();
	          Logger.log_server(Logger.LOG_WARN, "Max node connections reached - client declined");
	        }
	      } catch (IOException e) {
	        System.out.println(e);
	      }
	    }
	  }
}
