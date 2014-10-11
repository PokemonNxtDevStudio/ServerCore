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
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.pokemonnxt.packets.Communications;
import com.pokemonnxt.packets.Communications.*;

public class Packet {


public boolean isReceiving = false;
public boolean isComplete = false;
public boolean Valid = false;
public String ReceivingIP;
private com.pokemonnxt.packets.Communications.Packet Packet;
private com.pokemonnxt.packets.Communications.Header Head;
private com.pokemonnxt.packets.Communications.Payload Payload;
private com.pokemonnxt.packets.Communications.PacketType Type;
public byte Data[];

	public com.google.protobuf.GeneratedMessage getPacket() throws ParseError, InvalidHeader{
		if(Packet != null) return Packet;
		Interpret();
		return Packet;
	}
	public com.pokemonnxt.packets.Communications.Payload getPayload() throws ParseError, InvalidHeader{
		if(Packet != null) return Payload;
		Interpret();
		return Payload;
	}
	public com.pokemonnxt.packets.Communications.Header getHeader() throws ParseError, InvalidHeader{
		if(Packet != null) return Head;
		Interpret();
		return Head;
	}
	public com.pokemonnxt.packets.Communications.PacketType getType() throws ParseError, InvalidHeader{
		if(Packet != null) return Type;
		Interpret();
		return Type;
	}
	
	private void Interpret() throws ParseError, InvalidHeader{
		try {
			Packet = Communications.Packet.parseFrom(Data);
			Head = Packet.getHeader();
			Type = Head.getType();
			Payload = Packet.getPayload();
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ParseError("PROTO PARSE ERROR");
		}
	}
	
	
	public void addPayloadByte(byte B) throws InvalidPacket, ParseError, InvalidHeader{
		if (CurrentPosition >= Data.length){
			throw new InvalidPacket("PACKET IS AT DECLARED LENGTH");
		}
		Data[CurrentPosition] = B;
		CurrentPosition +=1;
		if (CurrentPosition >= Data.length){
			isComplete = true;
			Interpret();
		}
	}
	short CurrentPosition = 0;
	public Packet(short length, String IP) throws InvalidHeader{ // Dis one is called when a packet is RECEIVED but theres still some of it to go.
		isReceiving = true;
		isComplete = false;
		ReceivingIP = IP;
		Data = new byte[length];
		Valid = true;
	}
	
	public Packet(Message M, String IP){ // Dis one is called when a packet is going to be SENT. It adds the header.
		isReceiving = false;
		isComplete = true;
		ReceivingIP = IP;
		com.pokemonnxt.packets.Communications.Header.Builder HeaderBuilder = com.pokemonnxt.packets.Communications.Header.newBuilder();
		com.pokemonnxt.packets.Communications.Packet.Builder PacketBuilder = com.pokemonnxt.packets.Communications.Packet.newBuilder();
		com.pokemonnxt.packets.Communications.Payload.Builder PayloadBuilder = com.pokemonnxt.packets.Communications.Payload.newBuilder();
		
		if(M instanceof PlayerDataPayload){ PayloadBuilder.setPlayerdatapayload((PlayerDataPayload) M); HeaderBuilder.setType(PacketType.PLAYER_DATA);}
		if(M instanceof LoginPayload){ PayloadBuilder.setLoginpayload((LoginPayload) M); HeaderBuilder.setType(PacketType.LOGIN);}
		if(M instanceof ChatMsgPayload){ PayloadBuilder.setChatmsgpayload((ChatMsgPayload) M); HeaderBuilder.setType(PacketType.CHAT);}
		if(M instanceof ActionFailedPayload){ PayloadBuilder.setActionfailedpayload((ActionFailedPayload) M); HeaderBuilder.setType(PacketType.ACTION_FAILED);}
		
		PacketBuilder.setPayload(PayloadBuilder.build());
		HeaderBuilder.setId(1);
		
		Packet = PacketBuilder.setHeader(HeaderBuilder.build())
				.setPayload(PayloadBuilder.build())
				.build();
		byte[] Dat = Packet.toByteArray();
		byte[] lengthPrefix = Functions.oneShortTwoBytes(Dat.length);
		 Logger.log_client(Logger.LOG_PROGRESS,IP, "DAT: " + Functions.bytesToHex(Dat) );
		 Logger.log_client(Logger.LOG_PROGRESS,IP, "LEN: " + Functions.bytesToHex(lengthPrefix) );
		 
		byte[] out = new byte[lengthPrefix.length + Dat.length];
		 System.arraycopy(lengthPrefix, 0, out, 0, lengthPrefix.length);
		 System.arraycopy(Dat, 0, out, lengthPrefix.length, Dat.length);
		 Logger.log_client(Logger.LOG_PROGRESS,IP, "OUT: " + Functions.bytesToHex(out) );

         // Concatenate the length prefix and the message
         Data = out;
		Valid = true;
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
	public class ParseError extends Throwable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1316999785573476045L;

		public String Invalid;
		public ParseError(String invalidPart){
			Invalid = invalidPart;
		}
	}
	
	
}
