package com.pokemonnxt.loader;

import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pokemonnxt.gameserver.GlobalExceptionHandler;
import com.pokemonnxt.gameserver.Logger;
import com.pokemonnxt.gameserver.ServerVars;

public class Server extends Thread{
	public static Gson gson = new GsonBuilder().create();
	
	
	
	
	public Server(){
		
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
	   
	   
	  public void run() {
		  StartServer();
	  }
	  public void StartServer() {
		 
		
	    // The default port number.
	    int portNumber = ServerVars.loadBalancerSocket;

	    try {
	    	
	      serverSocket = new ServerSocket(portNumber);
	    } catch (IOException e) {
	      System.out.println(e);
	    }
	    // Startup Thread Monitor
	    
	  //GIT UPDATE
	    /*
	     * Create a client socket for each connection and pass it to a new client
	     * thread.
	     */
	    while (true) {
	      try {
	    	  if (shutdown) break;
	        clientSocket = serverSocket.accept();
	        if (shutdown) break;
	        Logger.log_server(Logger.LOG_VERB_HIGH, "[LOAD BALANCER] Connected to " + clientSocket.getRemoteSocketAddress().toString());
	        int i = 0;
	        for (i = 0; i < ServerVars.MaxRelayConnections; i++) {
	          if (LoadBalancer.Relays[i] == null) {
	            (LoadBalancer.Relays[i] = new Relay(clientSocket)).Initiate();
	            break;
	          }
	        }
	        if (shutdown) break;
	        if (i == ServerVars.MaxRelayConnections) {
	        	 PrintStream os = new PrintStream(clientSocket.getOutputStream());
	          os.println("[BUSY]");
	          os.close();
	          clientSocket.close();
	          Logger.log_server(Logger.LOG_WARN, "Max server connections reached - client declined");
	        }
	      } catch (IOException e) {
	        System.out.println(e);
	      }
	    }
	  }
	}

	

