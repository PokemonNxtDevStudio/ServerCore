package com.pokemonnxt.sync;

import com.pokemonnxt.node.Nodes;




/**
 * @author 	TheModerator
 * @version 0.1.12
 * 
 * ########## SECURITY NOTICE #############
 * # THIS FILE CONTAINS INFORMATION WHICH #
 * # IS CLASSIFIED SECRET. DISCLOSURE TO  #
 * # UNAUTHORISED PERSONS IS STRICTLY     #
 * # PROHIBITED BY LAW. DO NOT DISCLOSE,  #
 * # TRANSMIT, STORE OR OTHERWISE SHARE   #
 * # (IN WHOLE OR IN PART) THE CONTENTS   #
 * # OF THIS FILE TO ANY UNAUTHORISED     #
 * # PERSONS                              #
 * ########################################
 * 
 * STRICTLY ALPHA: NOT TO BE IMPLEMENTED IN ANY PUBLIC SERVER SOLUTIONS
 * 
 */
public class Main {
	
	static String version = "0.1.13";
	
	
	public static SQLConnection SQL;
	public static MainServer Server;
	public static ControlServer Master;
	public static Nodes ServerNodes;
	public void UserQuit(){
		System.out.println(" ");
		System.out.println(">>>> Press ENTER to quit <<<<");
		System.console().readLine();
		System.exit(0);
	}
	public static  void initiateServerStartup(){
		System.out.println("Loading ROESTUDIOS POKEMON NXT SYNC SERVER... ");
		System.out.println("		Version " + version );
		System.out.println("		Time at Startup: " + System.currentTimeMillis());
		System.out.println("[" + ConsoleColors.ANSI_RED + "WARNING" + ConsoleColors.ANSI_RESET + "]	This server is in ALPHA and should not be connected to by public clients.");
		System.out.println("SYSTEM STARTUP							[" + ConsoleColors.ANSI_GREEN + "   OK   " + ConsoleColors.ANSI_RESET + "]");
	}

	public static void ShutdownServer(){
		Server.Shutdown();
		Master.Shutdown();
		SQL.DisconnectSQLServer();
		System.out.println("SERVER STATUS:		[" + ConsoleColors.ANSI_GREEN + " CLOSED " + ConsoleColors.ANSI_RESET + "]");
		System.out.println(">>>> Press ENTER to quit <<<<");
		System.console().readLine();
		System.exit(0);
	}
	public static void main(String[] args) {
		GlobalExceptionHandler handler = new GlobalExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(handler);

		Functions.initGeoIP();
		SQL = new SQLConnection();
		initiateServerStartup();
		SQL.ConnectToSQLServer();
		Cache.updateCache();
		Server = new MainServer();
		Server.start();
		Master = new ControlServer();
		Master.start();
		ServerNodes = new Nodes();
		System.out.println("SERVER STATUS:							[" + ConsoleColors.ANSI_GREEN + "   OK   " + ConsoleColors.ANSI_RESET + "]");
		while(true){
			System.out.print("> ");
			processCommand(System.console().readLine());
		}
		
	}
	
	public static void processCommand(String command){
	String args[] = command.split(" ");
	if (args[0].equalsIgnoreCase("reload")){
		if (args[1].equalsIgnoreCase("databases")){
			SQL.ReconnectDatabases();
			System.out.println("Done.");
		}
	}
	
	if (args[0].equalsIgnoreCase("Exit")){
		ShutdownServer();
	}
	if (args[0].equalsIgnoreCase("Info")){
		if (args.length < 2){
			
		}else{
			if (args[1].equalsIgnoreCase("Clients")){
				PrintClientsInfo();
			}
			if (args[1].equalsIgnoreCase("Client")){
				if (args.length < 3){
					String clientName = args[2];
					System.out.println("Retreiving data for " + clientName);
					
				}else{
					System.out.println("Client ID/IP/Port not specified");

				}
			}
		}
		
	}
	System.out.println("# Not yet supported");
	}
	
	public static void PrintClientsInfo(){
		System.out.println("---------- Connected Clients Information ----------");
		int CreatedCount = 0;
		int AliveCount = 0;
		int ConnectedCount = 0;
		int PlayerCount = 0;
		 for(Client CLI : Server.Clients){
			 if(CLI == null){
				 continue;
			 }
			 CreatedCount +=1;
			 if(CLI.isAlive()) AliveCount+=1;
			 if(CLI.player != null) PlayerCount+=1;
			 if(CLI.isConnected()) ConnectedCount+=1;
		 }
		 System.out.println("Threads Made: 		" + CreatedCount);
		 System.out.println("Threads Alive: 	" + AliveCount);
		 System.out.println("Connections: 		" + ConnectedCount);
		 System.out.println("Players made:		" + PlayerCount);
	}
	
}
