package com.pokemonnxt.sync;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import com.google.api.client.util.DateTime;
import com.pokemonnxt.packets.Communications;

public class Packet {

	public static enum TYPES {
		// Comments are from a server perspective
		UNKNOWN(-3), // [ERR] The received packet had an unknown type
		INVALID_CHECKSUM(-2), // [ERR] The checksum was invalid
		UNKNOWN_ERROR(-1), // [ERR] Some other unknown error
		SPECIAL(0), // [NOP] Like Karl in high-school. He's special. It might be useful later, but for now, just avoid it
		LOGIN(1), // [IN] The user requested to login
		LOGIN_ACEPTED(2), // [OUT] The server accepted the login
		LOGIN_REJECTED(3), // [OUT] The server rejected the login
		PLAYER_LOCATION(4), // [BI] A player location update
		POKEMON_LOCATION(5), // [BI] A pokemon location update
		SPEAK(6), // [IN] A player has entered something in the chat box
		LISTEN(7), // [OUT] A player should display something in the chat box
		PLAYER_STAT_UPDATE(8), // [BI] A player's stats have changed
		POKEMON_STAT_UPDATE(9), // [BI] A pokemon's stats have changed
		POKEMON_ATTACK(10), // [BI] A pokemon has been given the command to attack (and the server approved it)
		DENIED(11), // [OUT] An action has been denied (Details about what was denied are included)
		SPAWN_MATRIX(12), // [OUT] The client has a new spawn matrix to obay
		BATTLE_REQUEST(13), // [BI] IN: client is requesting a battle | OUT: The server is asking a client to battle
		BATTLE_START(14),  // [OUT] A battle commences on this packet's arrival 
		OWNERSHIP_CHANGE(15), // [OUT] The client is now either responsible or not responsible for a server resource
        ACTION(255) // Misc thing, like door opening, an object's state changing or a player playing an animation
        ;
		  
        private static Map<Integer, TYPES> map = new HashMap<Integer, TYPES>();
        private  int value;
        static {
            for (TYPES legEnum : TYPES.values()) {
                map.put(legEnum.value, legEnum);
            }
        }

        

        public static TYPES valueOf(int legNo) {
            return map.get(legNo);
        }
        private TYPES(int value) {
                this.value = value;
        }
};   

public boolean isReceiving = false;
public boolean isComplete = false;
public boolean Valid = false;
public String ReceivingIP;
public Header Head;
public TYPES Type;
public byte Content[];
public com.google.protobuf.GeneratedMessage Packet;

	public Packet(byte header[], byte payload[], String IP) throws InvalidHeader{ // Dis one is called when a packet is RECEIVED
		isReceiving = true;
		isComplete = false;
		Head = new Header(header);
		Content = payload;
		isReceiving = false;
		if(!CheckSum(Head.Checksum,Content,IP)){
			Valid = false;
			throw new InvalidHeader("BAD CHECKSUM");
		}
		Valid = true;
		isComplete = true;
	}
	
	public void addPayloadByte(byte B) throws InvalidPacket{
		if (CurrentPosition >= Content.length){
			throw new InvalidPacket("PACKET IS AT DECLARED LENGTH");
		}
		Content[CurrentPosition] = B;
		CurrentPosition +=1;
		if (CurrentPosition >= Content.length){
			isComplete = true;
		}
	}
	short CurrentPosition = 0;
	public Packet(byte header[], String IP) throws InvalidHeader{ // Dis one is called when a packet is RECEIVED
		isReceiving = true;
		isComplete = false;
		Head = new Header(header);
		Content = new byte[Head.PacketSize];
		Valid = true;
	}
	
	
	public Packet(byte header[], byte payload[], String IP) throws InvalidHeader{ // Dis one is called when we want to make a packet
		Head = new Header(header);
		
	}
	
	MessageDigest m;
	private byte[] GenerateCheckSum(byte[] Payload, String IP){
		byte IPBYTES[] = IP.getBytes();
		byte result[] = new byte[Payload.length];
		result = Payload.clone();
		Date date = new Date();   // given date
		Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
		calendar.setTime(date);   // assigns calendar to given date 
		int seplength = result.length / (IPBYTES.length + calendar.get(Calendar.HOUR_OF_DAY) + 1);
		int i = 1;
		for(byte b : IPBYTES){
			result[i*seplength] = b;
			i += 1;
		}
		
		try {
			m = MessageDigest.getInstance("MD5");
			 byte[] digest = m.digest(result);
			 return digest;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			GlobalExceptionHandler GEH = new GlobalExceptionHandler();
			GEH.uncaughtException(Thread.currentThread(), (Throwable) e);
			e.printStackTrace();
		}
		return new byte[0];
	}
	
	private boolean CheckSum(byte[] Checksum, byte[] Payload, String IP){
		
       if (GenerateCheckSum(Payload,IP).hashCode() == Checksum.hashCode() ){
    	   return true;
       }
       return false;
	}
	
	
	public class Header{
		public TYPES Type;
		public short PacketSize;
		public short TimeSent;
		public byte Checksum[] = new byte[8];
		
		public Header(byte header[]) throws InvalidHeader{
			if (header.length != 16) throw new InvalidHeader("Length");
			if (header[0] != 0x00 && header[1] != 0xFF && header[2] != 0x00) throw new InvalidHeader("Start");
			// packet received, get type
			byte TypeByte = is.readByte();
			byte sizeBytes[] = new byte[1];
			sizeBytes[0] = is.readByte();
			sizeBytes[1] = is.readByte();
			short PacketSize = Functions.twoBytesToShort(sizeBytes[0], sizeBytes[1]);
			
		}
	
		
	}
	public class InvalidHeader extends Throwable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1306933788573476045L;
		public String Invalid;
		public InvalidHeader(String invalidPart){
			Invalid = invalidPart;
		}
	}
	public class InvalidPacket extends Throwable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1316999785573476045L;

		public String Invalid;
		public InvalidPacket(String invalidPart){
			Invalid = invalidPart;
		}
	}
	
}
