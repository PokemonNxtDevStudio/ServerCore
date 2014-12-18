package com.pokemonnxt.gameserver;

import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MainServer extends Thread{
	public static Gson gson = new GsonBuilder().create();
	
	
	
	
	public MainServer(){
		
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

	  static ThreadMonitor ThreadModerator = new ThreadMonitor();
	  
	   final static Client[] Clients = new Client[ServerVars.MaxConnections];
	   
	   
	  public void run() {
		  StartServer();
	  }
	  public void StartServer() {
		 
		
	    // The default port number.
	    int portNumber = 23323;

	    try {
	    	
	      serverSocket = new ServerSocket(portNumber);
	    } catch (IOException e) {
	      System.out.println(e);
	    }
	    // Startup Thread Monitor
	    ThreadModerator.start();
	    

	    /*
	     * Create a client socket for each connection and pass it to a new client
	     * thread.
	     */
	    while (true) {
	      try {
	    	  if (shutdown) break;
	        clientSocket = serverSocket.accept();
	        if (shutdown) break;
	        Logger.log_server(Logger.LOG_VERB_HIGH, "Connected to " + clientSocket.getRemoteSocketAddress().toString());
	        int i = 0;
	        for (i = 0; i < ServerVars.MaxConnections; i++) {
	          if (Clients[i] == null) {
	            (Clients[i] = new Client(clientSocket, Clients)).start();
	            break;
	          }
	        }
	        if (shutdown) break;
	        if (i == ServerVars.MaxConnections) {
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

	

