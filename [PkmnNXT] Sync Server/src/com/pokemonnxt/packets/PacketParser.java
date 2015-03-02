package com.pokemonnxt.packets;

import java.util.List;

import com.pokemonnxt.gameserver.Logger;
import com.pokemonnxt.packets.Packet.Login;

public class PacketParser {

	public PacketParser() {
		// TODO Auto-generated constructor stub
	}
	public static void RunTest(){
		/*
		Logger.log_server(Logger.LOG_VERB_LOW,"---- INITIATING PACKET TEST...");
		String Usr = "TestUser";
		String UsrP = "TestPassword";
		long starttime = System.nanoTime();
		Login LP = new Packet.Login(Usr,UsrP);
		long newlogintime = System.nanoTime();
		byte compiled[] = LP.Compile();
		byte compiled2[] = new byte[compiled.length - 4];
		int i = 0;
		for (byte b : compiled){
			if (i< 4){
			i+=1;
			continue;
			}else{
				compiled2[i-4] = b;
				i+=1;
			}
			
		}
		long compiletime =  System.nanoTime();
		byte b[] = {(byte)1};
		Packet P = getPacket(b,b,compiled2);
		long newpacktime = System.nanoTime();
		if(P instanceof Packet.Login){
			long interpretTime = System.nanoTime();
			Packet.Login L = (Packet.Login) P;
			Logger.log_server(Logger.LOG_VERB_LOW,"Parsed username: " + L.Username);	
			Logger.log_server(Logger.LOG_VERB_LOW,"  Timings (nanoseconds):");
			Logger.log_server(Logger.LOG_VERB_LOW,"     Total: " + (interpretTime-starttime));
			Logger.log_server(Logger.LOG_VERB_LOW,"     Newlogin: " + (newlogintime-starttime));
			Logger.log_server(Logger.LOG_VERB_LOW,"     Compile: " + (compiletime-newlogintime));
			Logger.log_server(Logger.LOG_VERB_LOW,"     NewPacket: " + (newpacktime-compiletime));
			Logger.log_server(Logger.LOG_VERB_LOW,"     Interpret: " + (interpretTime-newpacktime));
		}
		*/
	}
	
	public static Packet getPacket(byte[] Session,byte[] Presentation, byte[] Pack){
			switch(Session[0]){
			case (byte) 1:  
				switch(Presentation[0]){
				case (byte) 1: // SPECIAL packet
					return new Packet.Login(Pack);
				case (byte) 2: // CHAT packet
					return new Packet.ChatRX(Pack);
				case (byte) 3: // DATA_REQUEST packet
					return new Packet.DataRequest(Pack);
				default:
					return null;
				}
			case (byte) 2:  
				switch(Presentation[0]){
				case (byte) 1: // MOVE packet
					return new Packet.LocationUpdate(Pack);
				default:
					return null;
				}
			default: return null;
			}
		}




}
