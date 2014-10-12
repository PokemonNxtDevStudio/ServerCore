package com.pokemonnxt.gameserver;




import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import com.pokemonnxt.types.Location;
import com.pokemonnxt.types.pokemon.Pokemon;
import com.pokemonnxt.types.trainer.PlayableTrainer;
import com.pokemonnxt.types.trainer.PlayableTrainer.LoginFailed;
import com.pokemonnxt.gameserver.Packet.InvalidHeader;
import com.pokemonnxt.gameserver.Packet.InvalidPacket;
import com.pokemonnxt.gameserver.Packet.ParseError;
import com.pokemonnxt.gameserver.ThreadMonitor.ThreadUsage;
import com.pokemonnxt.packets.ClassToPayload;
import com.pokemonnxt.packets.Communications.*;

public class Client extends Thread implements AutoCloseable {

	public static enum STATES {
		// Comments are from a server perspective
		UNKNOWN(-1), 	// I dunno
		INITIATED(0), 	// Thread is initialised but not started
		STARTED(1),		// Thread is started
		LOGGING_IN(2),	// User is being logged in
		WAITING(3),		// Waiting for an incoming packet
		RECEIVING(5),	// Receiving an incoming packet
		PARSING(6),		// Parsing the incoming packet
		WORKING(7),		// Doing work with the incoming packet
		PACKING(8),		// Packing an outgoing packet
		TRANSMITTING(9),// Sending an outgoing packet
		CLOSING(10),	// Closing the connection
		CLOSED(11)		// The socket is closed and this thread is effectively dead
		
        ;
		  
        private static Map<Integer, STATES> map = new HashMap<Integer, STATES>();
        private  int value;
        static {
            for (STATES legEnum : STATES.values()) {
                map.put(legEnum.value, legEnum);
            }
        }

        

        public static STATES valueOf(int legNo) {
            return map.get(legNo);
        }
        private STATES(int value) {
                this.value = value;
        }
};   

	  public String IP = null;
	  private DataInputStream  is = null;
	  private DataOutputStream os = null;
	  public Socket clientSocket = null;
	  private final Client[] threads;
	  private int maxClientsCount;
	  public long startTime = 0;
	  public long lastRX = 0;
	  public ThreadUsage Performance = null;
	  public STATES State;
	  
	  public PlayableTrainer player = null;
	  public boolean shutdown = false;
	  public List<PlayableTrainer> NearbyPlayers = new ArrayList<PlayableTrainer>();
	  
	  public Client(Socket clientSocket, Client[] threads) {
		  State = STATES.INITIATED;
	    this.clientSocket = clientSocket;
	    this.threads = threads;
	    maxClientsCount = threads.length;
	    IP = clientSocket.getRemoteSocketAddress().toString();
	    IP = IP.substring(1,IP.indexOf(":"));
	  }
	  public boolean isConnected(){
		  return clientSocket.isConnected();
	  }
	 
	  
	  private boolean SendPacket(Packet p){
		  
		  try {
			os.write(p.Data);
			Logger.log_client(Logger.LOG_PROGRESS,IP,  "Packet Sent: " + Functions.bytesToHex(p.Data));
			  return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
			return false;
	  }
	  
	  private Packet ReceivePacket(){
		  Logger.log_client(Logger.LOG_PROGRESS,IP, "Waiting for packet..." );
		  try {
				byte header[] = new byte[16];
				byte startbytes[] = new byte[2];
				State = STATES.WAITING;
				short i = 0;
				State = STATES.RECEIVING;
				startbytes[0] = is.readByte();
				startbytes[1] = is.readByte();
				Logger.log_client(Logger.LOG_PROGRESS,IP, "Length Bytes: " + Functions.bytesToHex(startbytes));
			Packet Incoming = new Packet(Functions.twoBytesToShort(startbytes[0], startbytes[1]),IP);
			Logger.log_client(Logger.LOG_PROGRESS,IP, "Starting receive of packet of length " + Functions.twoBytesToShort(startbytes[0], startbytes[1]));
			while(Incoming.isComplete == false){
				Incoming.addPayloadByte(is.readByte());
			}
			State = STATES.PARSING;
			Logger.log_client(Logger.LOG_PROGRESS,IP, "Finished Receiving Packet: ");
			Logger.log_client(Logger.LOG_PROGRESS,IP, "Packet: " + Functions.bytesToHex(Incoming.Data));
			Logger.log_client(Logger.LOG_PROGRESS,IP, "Packet Size: " + Incoming.Data.length);
			return Incoming;
		  	} catch (IOException e) {
			  State = STATES.UNKNOWN;
				e.printStackTrace();
			} catch (InvalidHeader e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			} catch (InvalidPacket e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		
	  }
	  
	  
	  private void ActionPacket(Packet p) throws ParseError, InvalidHeader{
		  State = STATES.WORKING;
		  
		  if (p.getPacket() == null || p.getType() == null){
			  Logger.log_client(Logger.LOG_ERROR, IP, "Blank Packet (Type " + p.getType() + ") Received.");
			  return;
		  }
		  switch(p.getType()){
		  	  case PLAYER_DATA:
		  		  PlayerDataPayload PlayerData = p.getPayload().getPlayerdatapayload();
		  		  if (PlayerData.getLocation() != null){
		  			  player.Move(new Location(PlayerData.getLocation()));
		  		  }
			  case LOGIN:
				  LoginPayload LoginPacket = p.getPayload().getLoginpayload();
				  
				 Logger.log_client(Logger.LOG_VERB_LOW, IP, "Received Login For User: " + LoginPacket.getUsername());
				 try {
						player = new PlayableTrainer(LoginPacket.getUsername(), LoginPacket.getPassword(),LoginPacket.getEmail(),this);
						PlayerDataPayload PDP = player.toPayload();
						Packet pac = new Packet(PDP,IP);
						SendPacket(pac);
						Logger.log_client(Logger.LOG_VERB_LOW, IP, "User Logged in: " + player.GTID);
					} catch (LoginFailed e) {
						ActionFailedPayload AFP = ClassToPayload.makeActionFailedPayload(ErrorTypes.LOGIN_INCORRECT,e.message);
						Packet pac = new Packet(AFP,IP);
						SendPacket(pac);
						Logger.log_client(Logger.LOG_VERB_LOW, IP, "Login Failed For User: " + LoginPacket.getUsername() + " Reason: " + e.message);
					}
				  break;
			  case CHAT:
				  ChatMsgPayload ChatMessagePacket = p.getPayload().getChatmsgpayload();
				  switch(ChatMessagePacket.getType()){
				case PRIVATE:
					break;
				case PUBLIC:
					Players.SendChat(ChatTypes.PUBLIC, player.GTID, ChatMessagePacket.getMsg());
					break;
				case SHOUT:
					break;
				default:
					break;
				  
				  }
				  break;
			  default:
				  break;
			  
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
		try {
			os.writeUTF(Packet);
			Logger.log_client(Logger.LOG_PROGRESS,IP,  "Packet Sent: " + Packet);
		} catch (IOException e) {
			GlobalExceptionHandler GEH = new GlobalExceptionHandler();
			GEH.uncaughtException(Thread.currentThread(), (Throwable) e, "Error sending string message");
		}
		
	}
	
	  public void run() {
		  ClientExceptionHandler handler = new ClientExceptionHandler();
			Thread.setDefaultUncaughtExceptionHandler(handler);
			lastRX = System.currentTimeMillis() + (ServerVars.Timeout*2);
			
			
	   // int maxClientsCount = this.maxClientsCount;
	    // Client[] threads = this.threads;
	    startTime = System.currentTimeMillis();
	    State = STATES.STARTED;
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
	      os = new DataOutputStream(clientSocket.getOutputStream());
	      
	      for(IPPermission IPcheck : Cache.IPPermissions){
	    	  if(IPcheck != null){
				if(IPcheck.Permission != 1 && IP.startsWith(IPcheck.Mask)){
					if (IPcheck.Permission == 0){
						 //TODO Add code to let the connection know it was rejected
						shutdown = true;
						 PlayerLog.LogAction(PlayerLog.LOGTYPE.CONNECTION_REJECTED, 0, IP, "REJECTED ON MASK " + IPcheck.Mask + " MESSAGE " + IPcheck.Message);
						 Logger.log_client(Logger.LOG_VERB_HIGH, IP, "Player connection rejected on mask: " + IPcheck.Mask);
					}
				}
	    	  }
			}
	      
	      while(!shutdown){
	    	  Packet p = ReceivePacket();
	    	try {
	    	  if (player == null && p.getType() != PacketType.LOGIN){
	    		  ActionFailedPayload AFP = ClassToPayload.makeActionFailedPayload(ErrorTypes.ACCESS_DENIED);
					Packet pac = new Packet(AFP,IP);
					SendPacket(pac);
					Logger.log_client(Logger.LOG_VERB_LOW, IP, "Attempt to do action without logging in.");
	    	  }
	    	  
				ActionPacket(p);
				
			} catch (InvalidHeader e) {
				GlobalExceptionHandler GEH = new GlobalExceptionHandler();
				GEH.uncaughtException(Thread.currentThread(), (Throwable) e, "Error actioning packet; Faulty header: " + e.Invalid);
			} catch (ParseError e) {
				GlobalExceptionHandler GEH = new GlobalExceptionHandler();
				GEH.uncaughtException(Thread.currentThread(), (Throwable) e, "Error actioning packet; Could not parse: " + e.Invalid);
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
	      if (player != null) player.signOut();
	      
	    		  
	     
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
		}
	      
	  }
	  
	  public void timeOut(){
		  SendPacket("[TIMEOUT]");
	  }
	  public void sendKick(String Message){
		  SendPacket("{'header':{'PTYPE':'KICK'},'payload':{'MSG' : " + Message + "} }");
		  shutdown = true;
	  }
	  

	  
	  
	  public void sendLocationUpdate( PlayerDataPayload AFP){
			Packet pac = new Packet(AFP,IP);
			SendPacket(pac);
			Logger.log_client(Logger.LOG_VERB_LOW, IP, "Sent Location Message");
	  }
	  public void sendLocationUpdate(PlayableTrainer p){
		  PlayerDataPayload AFP = p.toLocationUpdatePayload();
			Packet pac = new Packet(AFP,IP);
			SendPacket(pac);
			Logger.log_client(Logger.LOG_VERB_LOW, IP, "Sent Location Message");
	  }
	  
	  public void sendChatUpdate(ChatTypes CLASS, int sender, String Message){
		  ChatMsgPayload AFP = ClassToPayload.makeChatMsgPayload(CLASS,Message,sender);
			Packet pac = new Packet(AFP,IP);
			SendPacket(pac);
			Logger.log_client(Logger.LOG_VERB_LOW, IP, "Sent Chat Message");
		  
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
