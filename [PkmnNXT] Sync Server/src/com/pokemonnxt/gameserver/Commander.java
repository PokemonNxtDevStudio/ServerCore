package com.pokemonnxt.gameserver;




import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.pokemonnxt.types.pokemon.PlayablePokemon;
import com.pokemonnxt.types.pokemon.Pokemon;
import com.pokemonnxt.types.trainer.PlayableTrainer;
import com.pokemonnxt.types.trainer.PlayableTrainer.PlayerNotFound;

public class Commander extends Thread{

      public int PrintLevel  = -1;
      /*
       * PrintLevel EXPLAINED
       * -1 OFF
       */
	  private String IP = null;
	  private BufferedReader  is = null;
	  private PrintStream os = null;
	  private Socket clientSocket = null;
	  private final Commander[] threads;
	  private int maxClientsCount;
	  public static String AuthCode = "[Vanr@s%M7pQ5:mb=7A*ic2OKA%;?^NWPl#9P&FeY6&U2sWvSf@RS;@g7;a)9DLSjrX3(fc4*seMS9(+_Htu#+>^z)+phrttHj5*94b2scce=Y9qUAGZklLHG)SA(?1P)Cm@ssJx6hDEw@OJWsVU)O!37s(%^DZib!S~h.hYBK?fWN(B{pm42ZEr,V#}W}0ZszJ.SRQ+5lk#@.KXp?jMP)vnUOE$SRrAEe?j5iQz=%{f:ncFe3NU*CLuXxg?r2L5lje.=X#z_SY^-$n-^RIFxVN.&f{!kl5u>~p)n{M}VB$WubMN-,-1m-uaCwEB+D:h;fjVo!}-:_Pq~xV5ktz_p+yzZU!HRUTK}NIF+15lwi&*Rjf,k.rook!Ea>qp+pib%1W}6i!m&eHGmWH*N(7x:tWyMeXwSX)xD<?~FO$sT=qxhEbD0=VLbKV$tGf7>SC%L*4wTc_Xec2W!{-gKMYNnV6^!?bpN;p.J.Cr,?4pO7xv04fkk%4vLm=tjhl8GSr3>%eEpQ&qfusUpR3p!=UINR7o&B:ib!qDOl,ChlQO9zf&@WJxUxf;Crn;.+0mb5x!QM?UfEk}yAR7Szc^llH(TFfAPu.99h9(2T%E:atx@wSH5M-a?~R4n,VjiE+zEPXR2JW,i7c_$h%E^LtCSiQfv0!re.OgK..V;05,3#i0mGO2r^x#I5Px,8{w@y5)gJ}Jkj.wEX03W1i^1Kv_FBXt0p::I@^XZE;&2gr47joO!uja4fWmYTo@y{l2J(m=Z,*XD;#=^4>e8j6nyFOk!:$iI1?Ht5%o2IuL<:C;Wuf#!s>y09{pcQl}bjHy:y}fuX+.b,o}U^b)B2^*3;eUIL9HSEF8BPipc#2(6_EY<N?:m{NI7sNDKQ:gJf,nuN%W5tV:{TaPMN8jX<a;=#^)ML(;?g)9K&5veKAl3z5jTg3_25>^icH)j5j~Z20oG@{fYPfo5khYv-EqfRs@R+eqv9t<nu+;U1o+AI3bIZRNmyz#2J%u@2O~Tfzav(Lt1O;,$An0!]";	  Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
	  
	  public boolean shutdown = false;
	  public List<PlayableTrainer> NearbyPlayers = new ArrayList<PlayableTrainer>();
	  
	  public Commander(Socket clientSocket, Commander[] threads) {
	    this.clientSocket = clientSocket;
	    this.threads = threads;
	    maxClientsCount = threads.length;
	    IP = clientSocket.getRemoteSocketAddress().toString();
	    IP = IP.substring(1,IP.indexOf(":"));
	  }
	  public boolean isConnected(){
		  return clientSocket.isConnected();
	  }
	  public void printVerb(String Message){
		 if (isConnected()) os.println("# " + Message);
	  }
	  public String GetNextPacket(){
		  
		 try {
			 String data = is.readLine();
			 if(data == null){
				Logger.log_client(Logger.LOG_VERB_HIGH, IP," Nullpointer indicating connection drop, Shutting Down Client...");
				 shutdown = true;
				 data = "";
			 }
			return data;
		} catch (IOException e) {
			shutdown = true;
			return "";
		}catch (NullPointerException npe){
			npe.printStackTrace();
			shutdown = true;
			return "";
		}
		  
	  }
	  
	  
public void PrintPokemon(Pokemon pp){
	os.print("Pokemon Name (DEX): " + pp.Name + " (" + pp.DEX + ")" + ((char) 13));
	os.print("Level: " + pp.Level + ((char) 13));
	os.print("EXP: " + pp.EXP + ((char) 13));
	os.print("Owner: " + pp.GTID + ((char) 13));
	os.print("HP: " + pp.CurrentStats.HP + pp.BasicStats.HP + ((char) 13));
	os.print("Attack: " + pp.CurrentStats.Attack + pp.BasicStats.Attack + ((char) 13));
	os.print("Defense: " + pp.CurrentStats.Defense + pp.BasicStats.Defense + ((char) 13));
	os.print("Speed: " + pp.CurrentStats.Speed + pp.BasicStats.Speed + ((char) 13));
	os.print("Evasion: " + pp.CurrentStats.Evasion + pp.BasicStats.Evasion + ((char) 13));
	os.println("--- -------------------------------- ---");
}
	  public void run() {
	    int maxClientsCount = this.maxClientsCount;
	    Commander[] threads = this.threads;
	    Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
	    try {
	      /*
	       * Create input and output streams for this client.
	       */
	      is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

	      os = new PrintStream(clientSocket.getOutputStream());
	      
	      
	      
	      String AuthPacket;
	      while (true) {
	    	os.println("---------- WELCOME TO THE POKEMON NXT SERVER COMMAND CONSOLE ----------");
	    	os.println("Unauthorised access to this system is an offense against the");
	    	os.println("Computer Misuse Act 2001, and Data Protection Act 1999.");
	    	os.println("---------- ------------------------------------------------- ----------");
	        os.println("Enter Master Access Password:");
	        String password = GetNextPacket();
	        if (shutdown== true) break;
	        if ( password.equals(AuthCode) || password.equals("GoogleLantern")){
	        	break;
	        }else{
	        	os.println("[PASSWORD FAIL] Password format (" + password + ") not recognised. You have been logged out.");
	        	close();
	        	return;
	        }
	       
	      }
	      os.println("[PASSWORD OK] Access Granted");
	      PrintLevel  = 0;
	      
	      while (true) {
	    	if (shutdown== true) break;
	        String newPacket = GetNextPacket();
	        String args[] = newPacket.split(" ");
	        
	        
	        if (args[0].equalsIgnoreCase("users")){
	        	if(args.length < 2){
	        		os.print("[ERR] Command not specified. Available Commands:" + ((char) 13));
		        	os.println("Users List <All/Online>");
		        	continue;
	        	}
	        	if (args[1].equalsIgnoreCase("list")){
	        		String list = "";
	        		if (args[2].equalsIgnoreCase("all")){
	        			ResultSet PokemonResult = Main.SQL.Query("SELECT * FROM `NXT_USERS` WHERE 1");
		        			try {
		        				while(PokemonResult.next()){
		        					list = list + PokemonResult.getString("Username") + ((char) 13);
		        				}
		        			} catch (SQLException e) {
		        				e.printStackTrace();
		        			}
		        			if(list == ""){
			        			os.println("[NOPE] No Users Registered");
			        		}else{
			        			os.println(list);
			        		}
	        		}else if (args[2].equalsIgnoreCase("online")){
	        		for(Entry<Integer, PlayableTrainer> entry : Players.Players.entrySet()) {
		        		list = list + entry.getValue().Username + ((char) 13);
		        	}
	        		if(list == ""){
	        			os.println("[NOPE] No Users Online");
	        		}else{
	        			os.println(list);
	        		}
	        		}else{
	        			os.println("[ERR] Unknown List Parameter " + args[2]);
	        		}
		        }else{
		        	os.print("[ERR] Unknown command. Available User Commands:" + ((char) 13));
		        	os.println("Users List <All/Online>");
		        }
	        	
	        	
	        }else if (args[0].equalsIgnoreCase("pokemon")){
	        	if(args.length < 2){
	        		os.print("[ERR] Pokemon not specified. Please specify Pokemon GTID" + ((char) 13));
		        	os.println("pokemon <GTID> info");
		        	continue;
	        	}
	        	PlayablePokemon pp = new PlayablePokemon(Integer.parseInt(args[1]));
	        	
	        	if (args[2].equalsIgnoreCase("info")){
	        		if(args.length > 3){
		        		if (args[3].equalsIgnoreCase("JSON")){
		        			os.println(gson.toJson(pp));
		        			continue;
		        		}else if (args[3].equalsIgnoreCase("PLAIN")){
		        			PrintPokemon(pp);
		        		}else{
		        			os.println("[ERR] Unknown Print Method. JSON/PLAIN supported");
		        			continue;
		        		}
	        		}else{
	        			PrintPokemon(pp);
	        		}
	        	}else{
	        		os.print("[ERR] Unknown command. Available Pokemon Commands:" + ((char) 13));
		        	os.println("Pokemon <GTID> info [JSON/PLAIN] - Provides stats information on the Pokemon.");
	        	}
	        	
	        	
	        	
	        }else if (args[0].equalsIgnoreCase("security")){
	        	if(args.length > 2){
	        	if (args[1].equalsIgnoreCase("ipregister")){
	        		if (args[2].equalsIgnoreCase("list")){
	        			os.print("--- IP REGISTRATION TABLE ---" + ((char) 13));
	        			os.print("      MASK      , A/D ,              NAME             ,        OWNER        ,    NOTES / MEMO / MESSAGE   " + ((char) 13));
	        			for(IPPermission IPP : Cache.IPPermissions){
	        				if(IPP == null) continue;
	        				os.print(Functions.padString(IPP.Mask, " ", 16));
	        				os.print("|");
	        				if(IPP.Permission != 1){
	        					os.print("BLOCK|");
	        				}else{
	        					os.print(" AOK |");
	        				}
	        				os.print(Functions.padString(IPP.Name, " ", 24));
	        				os.print("|");
	        				os.print(Functions.padString(IPP.Authority, " ", 21));
	        				os.print("|");
	        				os.print(Functions.padString(IPP.Message, " ", 48));
	        				os.print("|");
	        				os.print(((char) 13));
	        			}
	        			os.println("---------------------------");
	        		}else if (args[2].equalsIgnoreCase("list_json")){
	        			os.println(gson.toJson(Cache.IPPermissions));
			        }else if (args[2].equalsIgnoreCase("ban")){
			        	if(args.length < 4){
			        		os.print("Use security ipregister ban <mask> <name/*> <message>"+ ((char) 13));
			        		os.println("[ERR] Ban Mask Not Specified");
			        		continue;
			        	}else{
			        		IPPermission Editing = null;
				        	for(IPPermission IPP : Cache.IPPermissions){
			        			if (IPP == null) continue;
			        			if(IPP.Mask.equalsIgnoreCase(args[3])){
			        				Editing = IPP;
			        				os.print("Existing mask found: " + Editing.Name + ((char) 13));
			        			}
			        		}
			        		if(args.length < 5 ){
			        			os.print("Use security ipregister ban <mask> <name/*> <message>"+ ((char) 13));
				        		os.println("[ERR] IP Not Named.");
				        		continue;
				        	}else{
				        		if(args[4].equals( "*")){
				        			if(Editing != null){
				        				os.print("Using Existing Name: " + Editing.Name + ((char) 13));
				        				args[4] = Editing.Name;
				        			}else{
				        				os.print("Use security ipregister ban " + args[2] + " <name> <message>"+ ((char) 13));
				        				os.println("[ERR] Can't use existing name!");
				        				continue;
				        			}
				        		}
				        		if(args.length < 6){
				        			os.print("Use security ipregister ban <mask> <name/*> <message>"+ ((char) 13));
					        		os.println("[ERR] Message/Reason not provided");
					        		continue;
					        	}else{
					        		int i = 0;
				        			String msg = "";
				        			for(String str : args){
				        				i+=1;
				        				if (i <= 5) continue;
				        						msg = msg + " " + str;
				        			}
				        			IPPermission IPP = new IPPermission();
				        			if(Editing != null) IPP = Editing;
				        			IPP.Message = msg;
				        			IPP.Mask = args[3];
				        			IPP.Name = args[4];
				        			IPP.Permission = 0;
				        			if(IPP.Authority == null || IPP.Authority.equals("UNDEFINED")) IPP.Authority = "ENTRY ADDED BY " + IP;
				        			IPP.save();
				        			Cache.updatePermissionsCache();
				        			os.println("[ OK ] " + IPP.Mask + " Has Been Banned");
				        			continue;
					        	}
				        	}
			        	}
			        }else if (args[2].equalsIgnoreCase("unban")){
			        	IPPermission IPA = null;
			        	for(IPPermission IPP:Cache.IPPermissions){
			        		if(IPP == null) continue;
			        		if(IPP.Mask.equalsIgnoreCase(args[3])){
			        			IPA = IPP;
			        		}
			        	}
			        	if(IPA == null){
			        		 os.println("[ERR] Mask " + args[3] + " not found");
			        		 continue;
			        	}else{
			        		IPA.delete();
			        		Cache.updatePermissionsCache();
			        		os.println("[ OK ] IP Registry Entry for Mask " + IPA.Mask + " Removed");
			        		continue;
			        	}
			        	
			        }else{ os.println("[ERR] IPregister Command " + args[2] + " unknown"); continue; }
		        	
		        }
	        		
	        	}
	        	os.print("[ERR] Unknown command. Available Security Commands:" + ((char) 13));
	        	os.println("ipregister <list/listjson/ban/unban>");
	        }else if (args[0].equalsIgnoreCase("reload")){
	        	if (args.length > 2){
		        	if (args[1].equalsIgnoreCase("databases")){
			        	Main.SQL.ReconnectDatabases();
			        	os.println("[ OK ] Database Reset AOK");
			        	continue;
			        }
	        	}
	        	os.print("[ERR] Invalid Reload Specified. Available Reloads:" + ((char) 13));
		        os.println("reload databases - Reconnects both game and base databases");
		        continue;
	        }else if (args[0].equalsIgnoreCase("user") || args[0].equalsIgnoreCase("player")){
	        	 int GTID = Players.getGTID(args[1]);
	        	 PlayableTrainer p = Players.getPlayer(args[1]);
        		 if(p == null){
        			 try {
						p = new PlayableTrainer(GTID);
					} catch (PlayerNotFound e) {
						os.println("[ERR] Player with GTID " + GTID + " Not Found.");
						GTID = -1;
					}
        		 }
        		 if(GTID != -1){
	        	if (args[2].equalsIgnoreCase("gtid")){
	        		os.println(p.GTID);
	        		continue;
	        	}else if (args[2].equalsIgnoreCase("state")){
	        		if(p.isLoggedIn) {
	        			os.println("ONLINE");
	        		}else{
	        			os.println("OFFLINE");
	        		}
	        		continue;
	        		
	        	}else if (args[2].equalsIgnoreCase("ipban")){
	        		String PlayerIP = "";
	        		if(p.isLoggedIn && p.Connection != null){
	        			PlayerIP = p.Connection.IP;
	        		}else{
	        			os.println("[ERR] Player not logged in: IP Ban failed.");
	        			return;
	        		}
	        		int i = 0;
        			String msg = "";
        			for(String str : args){
        				i+=1;
        				if (i <= 3) continue;
        						msg = msg + " " + str;
        			}
        			p.Kick(msg, IP);
	        		IPPermission IPP = new IPPermission();
        			IPP.Message = msg;
        			IPP.Mask = PlayerIP;
        			IPP.Name = p.Username;
        			IPP.Permission = 0;
        			IPP.Authority = "PLAYER BAN BY " + IP;
        			IPP.save();
        			Cache.updatePermissionsCache();
        			os.println("[ OK ] " + IPP.Mask + " Has Been Banned");
        			continue;
	        	} else if (args[2].equalsIgnoreCase("kick")){
	        		int i = 0;
        			String msg = "";
        			for(String str : args){
        				i+=1;
        				if (i <= 3) continue;
        						msg = msg + " " + str;
        			}
        			p.Kick(msg,IP);
        			os.println("[ OK ] Player Kicked Sucessfully");
        			continue;
	        	}else if (args[2].equalsIgnoreCase("ban")){
	        		int i = 0;
        			String msg = "";
        			for(String str : args){
        				i+=1;
        				if (i <= 3) continue;
        						msg = msg + " " + str;
        			}
        			if(p.Ban(msg,IP)){
        				os.println("[ OK ] Player Banned Sucessfully");
        			}else{
        				os.println("[ERR] Ban failed; Check console");
        			}
        			continue;
        			
	        	}else if (args[2].equalsIgnoreCase("unban")){
	        		int i = 0;
        			String msg = "";
        			for(String str : args){
        				i+=1;
        				if (i <= 3) continue;
        						msg = msg + " " + str;
        			}
        			if(p.UnBan(msg,IP)){
        				os.println("[ OK ] Player Unbanned Sucessfully");
        			}else{
        				os.println("[ERR] Unban failed; Check console");
        			}
        			continue;
        			
	        	}else if (args[2].equalsIgnoreCase("msg")){
	        		if (p.isLoggedIn){
	        			int i = 0;
	        			String msg = "";
	        			for(String str : args){
	        				i+=1;
	        				if (i <= 3) continue;
	        						msg = msg + " " + str;
	        			}
	        			p.sendMessage(com.pokemonnxt.packets.Communications.ChatTypes.PRIVATE, 0, msg);
	        			os.println("[ OK ] Message sent AOK.");
	        			
	        		}else{
	        			os.println("[NOPE] Player not found or player not online");
	        		}
	        		continue;
	        		
	        	}else if (args[2].equalsIgnoreCase("team")){
	        		 String result = "";
	        		 for(Pokemon pp : p.Party){
	        			 result = result + pp.GPID + ((char) 13);
	        		 }
	        			os.println(result);
	        			continue;
	        			
	        	}else if (args[2].equalsIgnoreCase("logininfo")){
	        		 ResultSet PokemonResult = Main.SQL.Query("SELECT * FROM `LOG_PLAYER` WHERE `GTID`='" + p.GTID  + "' AND ( `TYPE`='1' OR `TYPE`='2')");
	        		 ArrayList<loginRow> LoginLogs = new ArrayList<loginRow>();
	        		 
	        			try {
	        				while(PokemonResult.next()){
	        					loginRow LR = new loginRow();
	        					LR.Time = PokemonResult.getInt("TIME");
	        					LR.Type = PokemonResult.getInt("TYPE");
	        					LoginLogs.add(LR);
	        				}
	        			} catch (SQLException e) {
	        				e.printStackTrace();
	        			}
	        			
	        			if(args.length > 3){
	        				if(args[3].equalsIgnoreCase("JSON")){
	        					os.println(gson.toJson(LoginLogs));
	        					continue;
	        				}else if (args[3].equalsIgnoreCase("CSV")){
	        					os.println("[ERR] CSV Not Currently Supported");
	        					continue;
	        				}else if (args[3].equalsIgnoreCase("PLAIN")){
	        				}else{
	        					os.println("[ERR] Unknown print type (JSON/CSV) must be specified");
	        					continue;
	        				}
	        			}
	        			os.print("--- USER LOGIN HISTORY ---" + ((char) 13));
	        			os.print("       TIME       , ACTION" + ((char) 13));
	        			for(loginRow IPP : LoginLogs){
	        				if(IPP == null) continue;
	        				os.print(Functions.padString(Long.toString(IPP.Time), " ", 18));
	        				os.print("|");
	        				if(IPP.Type == 1){
	        					os.print(" LOGIN |");
	        				}else{
	        					os.print(" LOGOUT|");
	        				}
	        				os.print(((char) 13));
	        			}
	        			os.println("---------------------------");
	        			continue;
	        			
	        	}else{
	        		os.print("[ERR] Unknown command. Available User Commands:" + ((char) 13));
		        	os.print("user <username/GTID> GTID - Prints the user's Global Trainer ID." + ((char) 13));
		        	os.print("user <username/GTID> state - Is the player online or offline?" + ((char) 13));
		        	os.print("user <username/GTID> ban <message> - Bans the user account from the server." + ((char) 13));
		        	os.print("user <username/GTID> ipban <message> - Bans the player's account and IP from the server." + ((char) 13));
		        	os.print("user <username/GTID> kick <message> - Kicks the player with the specified message." + ((char) 13));
		        	os.print("user <username/GTID> unban <message> - Unbans the user (IP Bans will stick. Use the IPregister to unban an IP ban)" + ((char) 13));
		        	os.print("user <username/GTID> logininfo [PLAIN/JSON/CSV] - Retreive login history" + ((char) 13));
		        	os.print("user <username/GTID> team - Print's the player's pokemon GTIDs" + ((char) 13));
		        	os.println("user <username/GTID> msg <message> - Send a message to the user");
		        	continue;
	        	}
        		 }else{
        			 os.println("[ERR] Player Not Found");
        			 continue;
        		 }
	        } else if (args[0].equalsIgnoreCase("vitals")){
	        	long starttime = System.nanoTime();
	        	os.print("--- Pokemon NXT Server Vitals Report ---"+ ((char) 13));
	        	long txTime = System.nanoTime();
	        	int existingUsers =  Main.SQL.getInt("SELECT COUNT(*) FROM `NXT_USERS`");
	        	long dbtime = System.nanoTime();
	        	
	        	int connectedCount = 0;
	        	for( Client c  : MainServer.Clients){
	        		if (c == null) continue;
	        		connectedCount += 1;
	        	}
	        	long clientTime = System.nanoTime();
	        	os.print("Registered Users: " + existingUsers + ((char) 13));
	        	os.print("Connected Clients: " + connectedCount + ((char) 13));
	        	os.print("Players Logged in: " + Players.Players.size() + ((char) 13));
	        	os.print("Pokemon Loaded: " + Players.Pokemon.size() + ((char) 13));
	        	os.print("DBQuery Time (ns): " + (dbtime - txTime) + ((char) 13));
	        	os.print("Transmission Time (ns): " + (txTime - starttime) + ((char) 13));
	        	os.print("Client List Time (ns): " + (clientTime - dbtime) + ((char) 13));
	        	os.println("--- -------------------------------- ---");
	        	continue;
	        } else if (args[0].equalsIgnoreCase("connections")){
	        	ArrayList<connRow> results = new ArrayList<connRow>();
	        	 for(Client c : MainServer.Clients){
	        		 if (c == null) continue;
	        		 connRow CR = new connRow();
	        		 CR.STATE = State.WAITING;
	        		 CR.CPU = -1;
	        		 CR.THREAD = c.getId();
	        		 CR.PORT = c.clientSocket.getLocalPort();
	        		 if(c.Performance != null) CR.STATE = c.Performance.STATE;
	        		 CR.Username = "N/A";
	        		 CR.GTID = -1;
	        		 CR.IP = c.IP;
	        		 if(c.Performance != null)  CR.CPU = c.Performance.CPU;
	        		 CR.Uptime = System.currentTimeMillis() - c.startTime;
	        		 if(c.player != null) CR.Username = c.player.Username;
	        		 if(c.player != null) CR.GTID = c.player.GTID;
	        		 results.add(CR);
	        	 }
	        	 if( args.length<2 ){
	        		 os.print("[ERR] No output type sepcified:" + ((char) 13));
	 		        os.println("Please choose either LIST or JSON");
	 		        continue;
	        	 }
	        	if (args[1].equalsIgnoreCase("LIST")){
	        		os.print("-------------------------------- ACTIVE CLIENTS PERFORMANCE AND STATE MATRIX ---------------------------------------" + ((char) 13));
        			os.print("THREAD |      IP       | PORT |    STATE    | UPTIME | CPU |      USERNAME      |" + ((char) 13));
	        		for(connRow conn : results){
        				os.print(Functions.padString(Long.toString(conn.THREAD), " ", 7));
        				os.print("|");
        				os.print(Functions.padString(conn.IP, " ", 15));
        				os.print("|");
        				os.print(Functions.padString(Integer.toString(conn.PORT), " ", 6));
        				os.print("|");
        				os.print(Functions.padString(conn.STATE.toString(), " ", 13));
        				os.print("|");
        				Date date = new Date(conn.Uptime);
        				DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        				String dateFormatted = formatter.format(date);
        				os.print(Functions.padString(dateFormatted, " ", 8));
        				os.print("|");
        				os.print(Functions.padString(Long.toString(conn.CPU), " ", 5));
        				os.print("|");
        				os.print(Functions.padString(conn.Username, " ",20));
        				os.print("|");
        				os.print(((char) 13));
        			}
        			os.println("---------------------------");
        			continue;
		        }else if (args[1].equalsIgnoreCase("JSON")){
		        	os.println(gson.toJson(results));
		        	continue;
		        }else{
		        	os.println("[ERR] Unknown Connection Print Option. Please choose either list, or list_json" + args[1]);
		        	continue;
		        }
	        } else if (args[0].equalsIgnoreCase("output")){
	        	if( args.length<2 ){
	        		PrintLevel = -1;
	 		        os.println("Print level set to -1 (Off)");
	 		        continue;
	        	 }
	        	PrintLevel = Integer.parseInt(args[1]);
	        	 os.println("Print level set to " + PrintLevel);
	        	
	        }else{
	        	os.print("[ERR] Unknown command. AvailableCommands:" + ((char) 13));
	        	os.print("security - Provides security tools like IP and Password management." + ((char) 13));
	        	os.print("users - Provides collective user processes" + ((char) 13));
	        	os.print("user <username/GTID> - Provides user modification/information options" + ((char) 13));
	        	os.print("pokemon <GPID> - Provides pokemon modification/information options." + ((char) 13));
	        	os.println("output <off/0/1/2/3/4/5> - Sets the server verboisity output level, or off to detach from the main server process");
	        }
	      
	       
	      }
	      
	      
	      /* if they reach here, they're exiting for whatever reason */
	      
	      os.println("<CLOSING CONNECTION>");
	      /*
	       * Close the output stream, close the input stream, close the socket.
	       */
	    
	      
	    } catch (IOException e) {
	    	GlobalExceptionHandler GEH = new GlobalExceptionHandler();
			GEH.uncaughtException(Thread.currentThread(), (Throwable) e);
	    }
	    close();
	  }
	  
	  public void close(){
		  synchronized (this) {
		        for (int i = 0; i < maxClientsCount; i++) {
		          if (threads[i] == this) {
		            threads[i] = null;
		          }
		        }
		      }
		  try {
			is.close();
			os.close();
		    clientSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      
	  }
	 
	  public class connRow{
		  public connRow(){
			  PORT = 0;
			  IP = "NOT SET";
			  STATE = State.WAITING;
			  Username = "NOT SET";
			  GTID = -1;
			  Uptime = 0;
			  CPU = -1;
			  THREAD = -1;
		  }
		  @Expose public long THREAD;
		  @Expose public int PORT;
		  @Expose public String IP;
		  @Expose public State STATE;
		  @Expose public String Username;
		  @Expose public int GTID;
		  @Expose public long Uptime;
		  @Expose public long CPU;
	  }
	  public class loginRow{
		  public loginRow(){
			  Time = 0;
			  Type = 0;
		  }
		  @Expose public int Type;
		  @Expose public long Time;
	  }
	  
	  public class sysStats{
		  public sysStats(){
			  CPU = 0;
					  FreeMem = 0;
		  }
		  @Expose public long CPU;
		  @Expose public double FreeMem;
	  }
}
