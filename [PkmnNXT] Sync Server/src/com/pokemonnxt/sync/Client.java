package com.pokemonnxt.sync;




import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pokemonnxt.sync.Player.LoginFailed;
import com.pokemonnxt.sync.PlayerLog.LOGTYPE;
import com.pokemonnxt.sync.PlayerPokemon.STATUS;
import com.pokemonnxt.sync.Players.MESSAGE_TYPE;
import com.pokemonnxt.sync.ThreadMonitor.ThreadUsage;

import com.pokemonnxt.packets.Testing;
import com.pokemonnxt.packets.Testing.test;

public class Client extends Thread implements AutoCloseable {


	  public String IP = null;
	  private DataInputStream  is = null;
	  private PrintStream os = null;
	  public Socket clientSocket = null;
	  private final Client[] threads;
	  private int maxClientsCount;
	  public long startTime = 0;
	  public long lastRX = 0;
	  public ThreadUsage Performance = null;
	  
	  public Player player = null;
	  public boolean shutdown = false;
	  public List<Player> NearbyPlayers = new ArrayList<Player>();
	  
	  public Client(Socket clientSocket, Client[] threads) {
	    this.clientSocket = clientSocket;
	    this.threads = threads;
	    maxClientsCount = threads.length;
	    IP = clientSocket.getRemoteSocketAddress().toString();
	    IP = IP.substring(1,IP.indexOf(":"));
	  }
	  public boolean isConnected(){
		  return clientSocket.isConnected();
	  }
	  
	  final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	  public static String bytesToHex(byte[] bytes) {
	      char[] hexChars = new char[bytes.length * 2];
	      for ( int j = 0; j < bytes.length; j++ ) {
	          int v = bytes[j] & 0xFF;
	          hexChars[j * 2] = hexArray[v >>> 4];
	          hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	      }
	      return new String(hexChars);
	  }
	  
	  private boolean SendPacket(byte[] Packet){
		  
		  
		  return true;
	  }
	  
	  private byte[] ReceivePacket(){
		  Logger.log_client(Logger.LOG_PROGRESS,IP, "Waiting for packet..." );
				byte header[] = new byte[16];
				byte startbytes[] = new byte[2];
				
				// wait for packet start
				while (startbytes[0] != 0x00 && startbytes[1] != 0xFF && startbytes[2] != 0x00){
					startbytes[0] = is.readByte();
					startbytes[1] = is.readByte();
					startbytes[2] = is.readByte();
				}
				// packet received, get type
				byte TypeByte = is.readByte();
				byte sizeBytes[] = new byte[1];
				sizeBytes[0] = is.readByte();
				sizeBytes[1] = is.readByte();
				short PacketSize = Functions.twoBytesToShort(sizeBytes[0], sizeBytes[1]);
			
				com.pokemonnxt.packets.Testing.test.parseFrom(data);
			
			
			
			
				test t = test.parseFrom(data);
			
				
			 Logger.log_client(Logger.LOG_PROGRESS,IP, "Received : " + bytesToHex(data));
		 
		
	  }
	  
	  
	  public String GetNextPacket(){
		 try {
			 if (shutdown == true) return "";
			 int b = 1;
			 boolean f = true;
			 
			 while (f){
				// Logger.log_client(Logger.LOG_PROGRESS,IP, "LOGGING");
			 while (is.available() > 0){
				 byte data[] = new byte[is.available()];
				 Logger.log_client(Logger.LOG_PROGRESS,IP, "RECEIVING" + is.available());
			b= is.read(data);
			test t = test.parseFrom(data);
			
			 Logger.log_client(Logger.LOG_PROGRESS,IP, "Received : " + bytesToHex(data));
		 }
			// Logger.log_client(Logger.LOG_PROGRESS,IP, "EOS");
			 //Logger.log_client(Logger.LOG_PROGRESS,IP, "Received Packet: " + data.toString());
			 }
			 return "";
			/* if(data == null){
				shutdown = true;
				 data = "";
			 }
			 if(isConnected() == false){
				shutdown = true;
				 data = "";
			 }
			 lastRX = System.currentTimeMillis();
			return data;
			*/
		} catch (IOException e) {
			//shutdown = true;
			return "";
		}catch (NullPointerException npe){
			//shutdown = true;
			return "";
		}
		  
	  }
	  public void forceClose(){
		  
	      try {
	    	  shutdown = true;
	    	  clientSocket.close();
	    	  is.close();
		      os.close();
			
			if (player!=null) player.signOut();
		} catch (IOException e) {
			GlobalExceptionHandler GEH = new GlobalExceptionHandler();
			GEH.uncaughtException(Thread.currentThread(), (Throwable) e, "Error whilst force-closing client connection");
		}
	  }
	  

	private void SendPacket(String Packet){
		os.println(Packet);
		Logger.log_client(Logger.LOG_PROGRESS,IP,  "Packet Sent: " + Packet);
	}
	
	  public void run() {
		  ClientExceptionHandler handler = new ClientExceptionHandler();
			Thread.setDefaultUncaughtExceptionHandler(handler);
			lastRX = System.currentTimeMillis() + (ServerVars.Timeout*2);
			
			
	   // int maxClientsCount = this.maxClientsCount;
	    // Client[] threads = this.threads;
	    startTime = System.currentTimeMillis();
	    try {
			clientSocket.setKeepAlive(false);
		} catch (SocketException e1) {
			GlobalExceptionHandler GEH = new GlobalExceptionHandler();
			GEH.uncaughtException(Thread.currentThread(), (Throwable) e1, "Error setting socket keep-alive");
		}
	    try {
	      /*
	       * Create input and output streams for this client.
	       */
	      //is = new DataInputStream(new InputStreamReader(clientSocket.getInputStream()));
	      is = new DataInputStream(clientSocket.getInputStream());
	      os = new PrintStream(clientSocket.getOutputStream());
	      
	      for(IPPermission IPcheck : Cache.IPPermissions){
	    	  if(IPcheck != null){
				if(IPcheck.Permission != 1 && IP.startsWith(IPcheck.Mask)){
					if (IPcheck.Permission == 0){
						 SendPacket("{'header' { 'PTYPE' : 'IP_REJECTED', 'payload' { 'MESK' : " + IPcheck.Mask + ", 'MSG' : '" + IPcheck.Message + "'} } }");
						 shutdown = true;
						 PlayerLog.LogAction(PlayerLog.LOGTYPE.CONNECTION_REJECTED, 0, IP, "REJECTED ON MASK " + IPcheck.Mask + " MESSAGE " + IPcheck.Message);
						 Logger.log_client(Logger.LOG_VERB_HIGH, IP, "Player connection rejected on mask: " + IPcheck.Mask);
					}
				}
	    	  }
			}
	      
	      String AuthPacket;
	      while (true) {
	    	  if (shutdown== true) break;
	        SendPacket("<REQUESTING AUTH>");
	        SendPacket("{'header' { 'PTYPE' : 'SERVER_INFO', 'payload' { 'TIME' : " + System.currentTimeMillis() + ", 'Version' : '" + Main.version + "'} } }");
	        Logger.log_client(Logger.LOG_VERB_LOW, IP,"Requested Auth");
	        AuthPacket = GetNextPacket().trim();
	        SendPacket("[OK]");
	        if (shutdown== true) break;
	      
	        Gson gson = new GsonBuilder().create();
	        PACKEDUndefined Header = gson.fromJson(AuthPacket, PACKEDUndefined.class);
	        String packettype = Header.header.PTYPE.toUpperCase();
	        if (packettype.equalsIgnoreCase("LOGIN")){
	        
	        PacketLOGIN_REQUEST request = gson.fromJson(AuthPacket, PacketLOGIN_REQUEST.class);
	        try {
				player = new Player(request.payload.Username, request.payload.Password,request.payload.Email,this);
				break;
			} catch (LoginFailed e) {
				SendPacket("{'header' :{ 'PTYPE' : 'LOGIN_FAIL'}, 'payload': { 'Type' : '" + e.Type + "', 'Msg' : '" + e.message + "'} }");
				Logger.log_client(Logger.LOG_VERB_LOW, IP, "Login Failed");
			}
	        	
	        }else{
	        	SendPacket("{'header' :{ 'PTYPE' : 'COMMS_FAIL'}, 'payload': {'FLAG':'AUTH_FAIL', 'MESSAGE' : 'Not Logged In. Please log in before doing anything else'} }");
	        	Logger.log_client(Logger.LOG_VERB_LOW, IP, "Non-Login Packet Attempted in Login context");
	        }
	      }
	      if (shutdown== true){
	    	  Close();
	    	  return;
	      }
	    	  
	      SendPacket("{'header' :{ 'PTYPE' : 'LOGIN_SUCCESS'}, 'payload': { 'GTID' : '" + player.GTID + "', 'New' : " + player.isNew + "} }");
	     
	      while (true) {
	    	 if (shutdown== true) break;
	    	 Logger.log_client(Logger.LOG_PROGRESS, IP, "Waiting for next packet");
	        String newPacket = GetNextPacket();
	        
	        	Logger.log_client(Logger.LOG_VERB_LOW, IP, "Got Packet " + newPacket);
	        
	        if (shutdown== true) break;
	        if (newPacket.startsWith("[")){
	        if (newPacket.startsWith("[CLOSE]")) {
	        	SendPacket("[OK]");
	          break;
	        }
	        }
	        
	        if (newPacket.startsWith("{")){
	        	Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		        PACKEDUndefined Header = gson.fromJson(newPacket, PACKEDUndefined.class);
		        String packettype = Header.header.PTYPE.toUpperCase();
		        if(packettype.equalsIgnoreCase("PLUD")){
		        	PacketPLUD packet = gson.fromJson(newPacket, PacketPLUD.class);
		        	Players.SendLocationUpdate(player.GTID, packet.payload.LOC);
		        	player.location = packet.payload.LOC;
		        	SendPacket("[OK]");
		        }
		        if(packettype.equalsIgnoreCase("SAVE_REQUEST")){
		        	player.CommitToDB();
		        	SendPacket("[OK]");
		        }
		        
		        if(packettype.equalsIgnoreCase("DATA_REQUEST")){
		        	PacketDATA_REQUEST packet = gson.fromJson(newPacket, PacketDATA_REQUEST.class);
		        	if (packet.payload.Type.equalsIgnoreCase("ALL")){
		        		SendPacket("<FEATURE MOVED, PLEASE SEE 'PLAYER'>");
		        	}
		        	if (packet.payload.Type.equalsIgnoreCase("PLAYER")){
		        		SendPacket("{'header' :{ 'PTYPE' : 'PLAYER_DATA'}, 'payload': " + gson.toJson(player) + " }");
		        	}
		        	if (packet.payload.Type.equalsIgnoreCase("MOVES")){
		        		SendPacket("{'header' :{ 'PTYPE' : 'MOVE_LIST', 'SIZE' : " + Cache.moveListJSON.length() + " }, 'payload': " + Cache.moveListJSON + " }");
		        	}
		        	if (packet.payload.Type.equalsIgnoreCase("POKEMON")){
		        		SendPacket("{'header' :{ 'PTYPE' : 'POKEMON_LIST', 'SIZE' : " + Cache.pokemonListJSON.length() + " }, 'payload': " + Cache.pokemonListJSON + " }");
		        	}
		        }
		        if(packettype.equalsIgnoreCase("CAPTURE")){
		        	PacketCAPTURE packet = gson.fromJson(newPacket, PacketCAPTURE.class);
		        	PlayerPokemon newPoke = player.catchNew(packet.payload.DEX,packet.payload.Level,packet.payload.EXP, packet.payload.Name, packet.payload.CurrentStats,packet.payload.BaseStats);
		        
		        	SendPacket(gson.toJson(newPoke));
		        }
	        
		        
	        }
	        
	       
	      }
	      
	      /* if they reach here, they're exiting for whatever reason */
	      if(clientSocket.isConnected() ){
	    	  SendPacket("<CLOSING CONNECTION>");
	    	  Close();
	      }
	    } catch (IOException e) {
	    	GlobalExceptionHandler GEH = new GlobalExceptionHandler();
			GEH.uncaughtException(Thread.currentThread(), (Throwable) e);
	    	e.printStackTrace();
	    }
	    Logger.log_client(Logger.LOG_VERB_LOW, IP, "Client Finished.");
	  }
	  
	  public void Close(){
		  /*
	       * Clean up. Set the current thread variable to null so that a new client
	       * could be accepted by the server.
	       */
	      synchronized (this) {
	        for (int i = 0; i < maxClientsCount; i++) {
	          if (threads[i] == this) {
	            threads[i] = null;
	          }
	        }
	      }
	      Players.RemovePlayer(player);
	      if (player != null){
	    	  if (player.isLoggedIn){
	    		  player.signOut();
	    	  }
	      }
	    		  
	     
	      /*
	       * Close the output stream, close the input stream, close the socket.
	       */
	      try {
	    	  clientSocket.close();
	    	  is.close();
		      os.close();
		      shutdown = true;
			Logger.log_client(Logger.LOG_VERB_HIGH, IP, "Connection Closed.");
		} catch (IOException e) {
			GlobalExceptionHandler GEH = new GlobalExceptionHandler();
			GEH.uncaughtException(Thread.currentThread(), (Throwable) e);
			e.printStackTrace();
		}
	      
	  }
	  
	  public void timeOut(){
		  SendPacket("[TIMEOUT]");
	  }
	  public void sendKick(String Message){
		  SendPacket("{'header':{'PTYPE':'KICK'},'payload':{'MSG' : " + Message + "} }");
		  shutdown = true;
	  }
	  
	  public void sendStatusUpdate(int GPID, PlayerPokemon.STATUS State, boolean Value){
		  SendPacket("{'header':{'PTYPE':'STATUS_UPDATE'},'payload':{'GPID' : " + GPID + ",'Stat' : '" + State + "' ,'Value' : " + Value + "} }");
	  }
	  
	  public void sendLocationUpdate(Location LC, Integer PlayerGTID){
		  SendPacket("{'header':{'PTYPE':'PLUD'},'payload':{'GTID' : " + PlayerGTID + ",'LOC' : " + MainServer.gson.toJson(LC) + "} }");
	  }
	  
	  public void sendChatUpdate(Players.MESSAGE_TYPE CLASS, int sender, String Message){
		  SendPacket("{'header' :{ 'PTYPE' : 'CHAT_MESSAGE'}, 'payload': {'Class' : " + CLASS + ",'TX' : " + sender + ",'Msg' : '" + Message + "'} }");

	  }
	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		Close();
	}
	
	public class ClientExceptionHandler implements Thread.UncaughtExceptionHandler{
		 

		  public void uncaughtException(Thread t, Throwable e) {
			  String ID;
			  if(player==null){
				  if(IP==null){
					  ID = "UNDEFINED";
				  }else{
					  ID = IP;
				  }
			  }else{
				  if(player.Username == null){
					ID = Integer.toString(player.GTID);
				  }else{
					  ID = player.Username;
				  }
			  }
			try {
				PrintWriter writer = new PrintWriter("/etc/NXT_SERVER/ERRORS/CLIENT" + ID + "ERROR" + System.currentTimeMillis() + ".txt", "UTF-8");
				 writer.println("The following Error occured in thread " + t.toString() + " with player " + ID);
				    writer.println(e.getMessage());
				    writer.println(e.getLocalizedMessage());
				    e.printStackTrace(writer);
				    writer.println("A SAFE CLIENT SHUTDOWN WILL BE ATTEMPTED");
				    writer.close();
				    SendPacket("[ERR " + System.currentTimeMillis() + "]");
				    Close();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		   return;
		    
		  }
		}
	
	
}
