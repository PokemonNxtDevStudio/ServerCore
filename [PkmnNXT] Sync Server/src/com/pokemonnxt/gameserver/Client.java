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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;











import com.pokemonnxt.packets.*;
import com.pokemonnxt.types.Asset;
import com.pokemonnxt.types.Location;
import com.pokemonnxt.types.pokemon.Pokemon;
import com.pokemonnxt.types.trainer.PlayableTrainer;
import com.pokemonnxt.types.trainer.PlayableTrainer.LoginFailed;
import com.pokemonnxt.types.trainer.Trainer;
//import com.pokemonnxt.gameserver.Packet.InvalidHeader;
//import com.pokemonnxt.gameserver.Packet.InvalidPacket;
//import com.pokemonnxt.gameserver.Packet.ParseError;
import com.pokemonnxt.gameserver.ThreadMonitor.ThreadUsage;
import com.pokemonnxt.packets.Packet;
import com.pokemonnxt.packets.Packet.AssetData;
import com.pokemonnxt.packets.Packet.ChatRX;
import com.pokemonnxt.packets.Packet.ChatTX;
import com.pokemonnxt.packets.Packet.DataRequest;
import com.pokemonnxt.packets.Packet.LocationUpdate;
import com.pokemonnxt.packets.Packet.Login;
import com.pokemonnxt.packets.Packet.LoginResponse.LOGIN_RESPONSES;
import com.pokemonnxt.packets.Packet.PokemonData;
import com.pokemonnxt.packets.Packet.RequestFailed;
import com.pokemonnxt.packets.Packet.RequestFailed.FAILURE;
import com.pokemonnxt.packets.Packet.TrainerData;

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
	public int ID = 0;
	  public String IP = null;
	  private DataInputStream  is = null;
	  private DataOutputStream os = null;
	  public Socket clientSocket = null;
	  public long startTime = 0;
	  public long lastRX = 0;
	  public ThreadUsage Performance = null;
	  public STATES State;
	  
	  public PlayableTrainer player = null;
	  public boolean shutdown = false;
	  public List<PlayableTrainer> NearbyPlayers = new ArrayList<PlayableTrainer>();
	  public HashMap<Integer,Asset> Assets = new HashMap<Integer, Asset>();
	  
	  public Client(Socket clientSocket, Client[] threads) {
		  ID = ServerAssets.GenerateClientID();
		  State = STATES.INITIATED;
	    this.clientSocket = clientSocket;
	    IP = clientSocket.getRemoteSocketAddress().toString();
	    IP = IP.substring(1,IP.indexOf(":"));
	  }
	  
	  public boolean isConnected(){
		  return clientSocket.isConnected();
	  }
	 

	  
	  public void GiveOwnership(Asset A){
		  // TODO Dispense Ownership Given Packet
		  Assets.put(A.AID, A);
		  A.owner = this.ID;
	  }
	  
	  public boolean TakeOwnership(Asset A) {
		  if(!Assets.containsKey(A.AID)){
				return false;
		  }else{
			  // TODO Dispense Ownership Taken Packet
			  Assets.remove(A.AID);
			  A.owner = 0;
			  return true;
		  }
	  }
	  
	  public void SendFastPacket(byte TX[]){
		  
		  try {
			os.write(TX);
			os.flush();
		} catch (IOException e) {
			  Logger.log_client(Logger.LOG_ERROR, IP, " Error whilst sending packet: Closing connection");
				Close();
		}
	  }
	  
	  private boolean SendPacket(Packet p){
		  
		  try {
			  long starttime = System.nanoTime();
			  byte[] data = p.Compile();
			  long compiletime = System.nanoTime();
			os.write(data);
			os.flush();
			long endtime = System.nanoTime();
			//Logger.log_client(Logger.LOG_PROGRESS,IP,  "Packet Sent: " + Functions.bytesToHex(data));
			Logger.log_client(Logger.LOG_PROGRESS,IP,  "Sent " + p.getType().toString() + " packet  of length" + data.length + " in times " + (endtime-starttime) + "(" + (compiletime-starttime) + ")");
			
			  return true;
		} catch (IOException e) {
			  Logger.log_client(Logger.LOG_ERROR, IP, " Error whilst sending packet: Closing connection");
				Close();
		}
		  
			return false;
	  }
	  
	  
	  private byte rx_transportBytes[] = new byte[4];
	  private byte rx_sessionBytes[] = new byte[1];
	  private byte rx_presentationBytes[] = new byte[1];
	  private byte rx_applicationBytes[] = new byte[1024];

	  private Packet ReceivePacket(){
		  boolean dbg = false;
		  if(dbg) Logger.log_client(Logger.LOG_PROGRESS,IP, "Waiting for packet..." );
		  try {
			  	State = STATES.WAITING;
			  	int TransIn = 0;
			  	while(TransIn < rx_transportBytes.length){
			  		rx_transportBytes[TransIn] = is.readByte();
			  		TransIn +=1;
			  	}
			  	if(dbg) Logger.log_client(Logger.LOG_PROGRESS,IP, "Receiving size bytes..." );
				State = STATES.RECEIVING;
				if(dbg) Logger.log_client(Logger.LOG_PROGRESS,IP, "Size bytes received: " + Functions.bytesToHex(rx_transportBytes));
				int packetsize = ByteBuffer.wrap(rx_transportBytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
				TransIn = 0;
				while(TransIn < rx_sessionBytes.length){
					rx_sessionBytes[TransIn] = is.readByte();
			  		TransIn +=1;
			  		packetsize -=1;
			  	}
				TransIn = 0;
				while(TransIn < rx_presentationBytes.length){
					rx_presentationBytes[TransIn] = is.readByte();
			  		TransIn +=1;
			  		packetsize -=1;
			  	}
				
				if(dbg) Logger.log_client(Logger.LOG_PROGRESS,IP, "Starting receive of packet of length " + packetsize);
				
				int i = 0;
				rx_applicationBytes = new byte[packetsize];
				while(i < packetsize ){
					rx_applicationBytes[i] = is.readByte();
					i+=1;
				}
				//receivebuffer[i+1] = (byte) 200;
				State = STATES.PARSING;
				Main.packetsReceived +=1;
				if(dbg) Logger.log_client(Logger.LOG_PROGRESS,IP, "Finished Receiving Packet ");
				return PacketParser.getPacket(rx_sessionBytes,rx_presentationBytes,rx_applicationBytes);
		  	} catch (IOException e) {
			  State = STATES.UNKNOWN;
			  if(dbg)  Logger.log_client(Logger.LOG_ERROR, IP, " Error whilst communicating: Closing connection");
				Close();
			}
			return null;
		
	  }
	  public void SendFullUpdate(){
		  List<Trainer> Ts = ServerAssets.getNearbyPlayers(player.location);
		  
		  Trainer T[] = new Trainer[Ts.size()];
		  Ts.toArray(T);
		  SendTrainerData(T);
	  }
	  
	  public void SendTrainerData(Trainer t){
		  SendTrainerData(new Trainer[]{t});
	  }
	  public void SendTrainerData(Trainer[] t){
		  Packet.TrainerData RF2 = new TrainerData(t);
			SendPacket(RF2);
	  }
	  private void SendError(FAILURE F, String Message){
		  Logger.log_client(Logger.LOG_WARN, IP, "User Encountered Error: " + F.toString() + " - " + Message);
		  Packet.RequestFailed RF2 = new RequestFailed(F,Message);
			SendPacket(RF2);
	  }
	  private void ActionPacket(Packet p) /*throws ParseError, InvalidHeader*/{
		  State = STATES.WORKING;
		  if (p == null){
			  Logger.log_client(Logger.LOG_ERROR, IP, "Nothing received: Connection dead!");
			  return;
		  }
		  switch(p.getType()){
		  	  case LOCATION:
		  		Packet.LocationUpdate LC = (LocationUpdate) p;
		  		Logger.log_player(Logger.LOG_VERB_LOW, "Move packet received...", player.GTID);
		  		if (Assets.containsKey(LC.AssetID)){
		  			ServerAssets.SendLocationUpdate(LC);
		  			player.Move(LC.location);
		  		}else{
		  		  Logger.log_client(Logger.LOG_WARN, IP, "User attempted to move object which they do not own (" + LC.AssetID + ")");
		  		  SendError(FAILURE.INSUFFICIENT_PERMISSIONS,"You do not own AID" + LC.AssetID );
		  		}
		  		
			  break;
			  case LOGIN:
				  Packet.Login LoginPacket = (Login) p;
				  Logger.log_client(Logger.LOG_VERB_LOW, IP, "Received Login For User: " + LoginPacket.Username);
				  if (player!=null){
					  SendError(FAILURE.STUPID_REQUEST,"You're already logged in dumbass");
					  shutdown = true;
				  }
				 try {
						player = new PlayableTrainer(LoginPacket.Username, LoginPacket.Password,LoginPacket.Email,this);
						Packet.LoginResponse LR = new Packet.LoginResponse(LOGIN_RESPONSES.SUCCESSFUL, "Sardines shall rule the world", new Packet.IntrinsicType.TrainerData(player));
						SendPacket(LR);
						player.TransferTo(this);
						Logger.log_client(Logger.LOG_VERB_LOW, IP, "User Logged in: " + player.GTID);
						SendFullUpdate();
					} catch (LoginFailed e) {
						Packet.LoginResponse LR = new Packet.LoginResponse(LOGIN_RESPONSES.INVALID_PASSWORD, e.message, null);
						SendPacket(LR);
						Logger.log_client(Logger.LOG_VERB_LOW, IP, "Login Failed For User: " + LoginPacket.Username + " Reason: " + e.message);
					}
				  break;
			  case CHAT:
				  
				  Packet.ChatRX CRX = (ChatRX) p;
				  ServerAssets.SendChat(player, CRX.Message);
				 break;
				 
			  case DATA_REQUEST:
			  		Packet.DataRequest DR = (DataRequest) p;
			  		Logger.log_player(Logger.LOG_VERB_LOW, "Request for data received", player.GTID);

			  		switch(DR.DAT){
			  		case UNKNOWN: // Scene
			  			SendError(FAILURE.UNKNOWN_REQUEST,"Empty data request (DAT = 0) received!");
			  			break;
			  		case TRAINER: 
			  			PlayableTrainer t = ServerAssets.getPlayer(DR.AssetID);
			  			if (t==null){
			  				SendError(FAILURE.DATA_MISSING,"Could not find Trainer with GTID" + DR.AssetID);
			  			}else{
			  				SendTrainerData(t);
			  			}
			  			break;
			  		case ASSET: 
			  			Asset a = ServerAssets.getAsset(DR.AssetID);
			  			if (a==null){
			  				SendError(FAILURE.DATA_MISSING,"Could not find Asset with AID" + DR.AssetID);
			  			}else{
			  				if(DR.ReturnObject){
			  					if(a instanceof Trainer){
			  						Packet.TrainerData RF2 = new TrainerData(new Trainer[]{(Trainer) a});
						  			SendPacket(RF2);
			  					}else if (a instanceof Pokemon){
			  						Packet.PokemonData RF2 = new PokemonData(new Pokemon[]{(Pokemon) a});
						  			SendPacket(RF2);
			  					}else{
			  						Packet.AssetData RF2 = new AssetData(new Asset[]{ a});
						  			SendPacket(RF2);
			  					}
			  				}else{
			  					Packet.AssetData RF2 = new AssetData(new Asset[]{ a});
					  			SendPacket(RF2);
			  				}
			  			}
			  			break;
			  		case POKEMON: 
			  			Pokemon poke = ServerAssets.getPokemon(DR.AssetID);
			  			if (poke==null){
			  				SendError(FAILURE.DATA_MISSING,"Could not find Pokemon with GPID" + DR.AssetID);
			  			}else{
			  				Packet.PokemonData RF2 = new PokemonData(new Pokemon[]{poke});
				  			SendPacket(RF2);
			  			}
			  			break;
			  		case SCENE:
			  			SendError(FAILURE.NOT_IMPLEMENTED,"Scene data request not yet implemented");
			  			break;
			  		default:
			  			SendError(FAILURE.NOT_IMPLEMENTED,"Not sure what data you're requesting!");
			  			return;
			  		}
			  		
				  break;
		case ERROR:
			break;
		case LOGIN_RESPONSE:
			SendError(FAILURE.STUPID_REQUEST,"PROTOCOL VIOLATION: CLIENTS SHOULD NOT GENERATE LOGIN RESPONSES");
			break;
		case OWNERSHIP_UPDATE:
			SendError(FAILURE.NOT_IMPLEMENTED,"Ownership not implemented fully");
			break;
		case REQUEST_FAILED:
			SendError(FAILURE.NOT_IMPLEMENTED,"Clients not currently able to refuse server requests at this time");
			break;
		case TRAINER_DATA:
			SendError(FAILURE.NOT_IMPLEMENTED,"Not set up to receive Trainer Data packets");
			break;
		case UNKNOWN:
			SendError(FAILURE.UNKNOWN_REQUEST,"Empty packet type received?");
			break;
		default:
			SendError(FAILURE.NOT_IMPLEMENTED,"Unhadleable game packet type received!");
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
				ActionPacket(p);
		
	      }
	      
	      /* if they reach here, they're exiting for whatever reason */
	      if(clientSocket.isConnected() ){
	    	  Close();
	      }
	    } catch (IOException e) {
	    	GlobalExceptionHandler GEH = new GlobalExceptionHandler();
			GEH.uncaughtException(Thread.currentThread(), (Throwable) e);
	    }
	    Logger.log_client(Logger.LOG_VERB_LOW, IP, "Client Finished.");
	  }
	  
	  boolean closed;
	  
	  public void Close(){
		  if(closed)return;
		  closed = true;
		  /*
	       * Clean up. Set the current thread variable to null so that a new client
	       * could be accepted by the server.
	       */
	      synchronized (this) {
	        for (int i = 0; i < MainServer.Clients.length; i++) {
	          if (MainServer.Clients[i] == this) {
	        	  MainServer.Clients[i] = null;
	          }
	        }
	      }
	      ServerAssets.RemoveClient(this);
	      if (player != null) player.signOut();
	      
	    		  
	     
	      /*
	       * Close the output stream, close the input stream, close the socket.
	       */
	      try {
	    	  shutdown = true;
	    	  clientSocket.close();
	    	  is.close();
		      os.close();
			Logger.log_client(Logger.LOG_VERB_HIGH, IP, "Connection Closed.");
		} catch (IOException e) {
			GlobalExceptionHandler GEH = new GlobalExceptionHandler();
			GEH.uncaughtException(Thread.currentThread(), (Throwable) e,"Error whilst soft-closing connection! Attempting force close.");
			forceClose();
		}
	      
	  }
	  
	  
	  
  public void sendLocationUpdate(LocationUpdate LU){
	  Logger.log_client(Logger.LOG_PROGRESS,IP, "Transmitting location update to " + player.Name);
	  SendFastPacket(LU.Compile());
  }
	    
	public void sendChatUpdate(int sender, String Message){
		  Packet.ChatTX pac = new ChatTX(Message,sender);
			SendPacket(pac);
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
				  if(player.Name == null){
					ID = Integer.toString(player.GTID);
				  }else{
					  ID = player.Name;
				  }
			  }
			try {
				long EC = System.currentTimeMillis();
				Logger.log_client(Logger.LOG_FATAL, IP, "UNHANDLED CLIENT ERROR OCCURED: " + EC);
				
				PrintWriter writer = new PrintWriter("/etc/NXT_SERVER/ERRORS/CLIENT" + ID + "ERROR" + EC + ".txt", "UTF-8");
				 writer.println("The following Error occured in thread " + t.toString() + " with player " + ID);
				    writer.println(e.getMessage());
				    writer.println(e.getLocalizedMessage());
				    e.printStackTrace(writer);
				    writer.println("A SAFE CLIENT SHUTDOWN WILL BE ATTEMPTED");
				    writer.close();
				   if (clientSocket.isConnected()) SendError(FAILURE.SERVER_FAULT,"Server Exception Occured: [CLIENT" + ID + "ERROR" + EC + "]");
				    Logger.log_client(Logger.LOG_FATAL, IP, "Attempting safe shutdown...");
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
	
	public class ThatsNotMineException extends Throwable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1306933785573476832L;
		
		int AID = 0;
		public String message = "Client does not own asset " + AID;
		
		
		/*
		 *  1 = Username/Password mismatch
		 *  2 = User Banned
		 *  3 = Email already in use
		 */
		public ThatsNotMineException(int lAID){
			AID = lAID;
		}
	}

	public void timeOut() {
		// TODO Auto-generated method stub
		Close();
	}
	
}
