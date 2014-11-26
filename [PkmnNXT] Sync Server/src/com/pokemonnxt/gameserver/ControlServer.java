package com.pokemonnxt.gameserver;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ControlServer extends Thread{
	public static Gson gson = new GsonBuilder().create();
	public ControlServer(){
		
	}
	public void Shutdown(){
		shutdown = true;
		try {
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private boolean shutdown = false;
	
	  // The server socket.
	  private static ServerSocket serverSocket = null;
	  // The client socket.
	  private static Socket clientSocket = null;

	  
	  
	   public static final Commander[] Clients = new Commander[5];
	   
	   
	  public void run() {
		  StartServer();
	  }
	  public void StartServer() {

	    // The default port number.
	    int portNumber = 9999;

	    /*
	     * Open a server socket on the portNumber (default 2222). Note that we can
	     * not choose a port less than 1023 if we are not privileged users (root).
	     */
	    try {
	    	
	      serverSocket = new ServerSocket(portNumber);
	    } catch (IOException e) {
	    	GlobalExceptionHandler GEH = new GlobalExceptionHandler();
			GEH.uncaughtException(Thread.currentThread(), (Throwable) e, "Error Starting Control Server");
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
	        Logger.log_server(Logger.LOG_VERB_HIGH, "Control Server Connected to " + clientSocket.getRemoteSocketAddress().toString());
	        int i = 0;
	        for (i = 0; i < ServerVars.MaxConnections; i++) {
	          if (Clients[i] == null) {
	            (Clients[i] = new Commander(clientSocket, Clients)).start();
	            break;
	          }
	        }
	        if (shutdown) break;
	        if (i == ServerVars.MaxConnections) {
	        	 PrintStream os = new PrintStream(clientSocket.getOutputStream());
	          os.println("[BUSY]");
	          os.close();
	          clientSocket.close();
	          Logger.log_server(Logger.LOG_WARN, "Max control server connections reached - client declined");
	        }
	      } catch (IOException e) {
	    	  GlobalExceptionHandler GEH = new GlobalExceptionHandler();
				GEH.uncaughtException(Thread.currentThread(), (Throwable) e, "Error Running Control Server");
	      }
	    }
	  }
	}

	

