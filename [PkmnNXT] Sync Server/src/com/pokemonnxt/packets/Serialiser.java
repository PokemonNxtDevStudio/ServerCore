package com.pokemonnxt.packets;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.util.EncodingUtils;

import com.pokemonnxt.gameserver.ConsoleColors;
import com.pokemonnxt.gameserver.Functions;
import com.pokemonnxt.gameserver.Logger;

public class Serialiser {

	public Serialiser() {
		
	}
	public static boolean dbg = true;
	public static   void RunTests() throws SerialiserNotWorkingException{
		Logger.log_server(Logger.LOG_VERB_LOW,"---- INITIATING SERIALISER TEST...");
		
		
		Logger.log_server(Logger.LOG_VERB_LOW,"- INTEGER TEST:");
		int iinput = 21612;
		long intStart = System.nanoTime();
		byte iintermediate[] = IntToBytes(iinput);
		long intCompile = System.nanoTime();
		int ioutput = BytestoInt(iintermediate);
		long intDeCompile = System.nanoTime();
		if(iinput != ioutput){
			System.out.println("INTEGER PARSE TEST				[" + ConsoleColors.ANSI_RED + "  FAIL  " + ConsoleColors.ANSI_RESET + "]");
			throw new SerialiserNotWorkingException();
		}else{
			
		}
		System.out.println("INTEGER PARSE TEST				[" + ConsoleColors.ANSI_GREEN + "   OK   " + ConsoleColors.ANSI_RESET + "]");
		
		Logger.log_server(Logger.LOG_VERB_LOW,"- STRING TEST:");
		String sinput = "ABCCD_INPUT TEST_10";
		long strStart = System.nanoTime();
		byte sintermediate[] = StringToBytes(sinput);
		long strCompile = System.nanoTime();
		ByteBuffer BB = ByteBuffer.wrap(sintermediate);
		String soutput = BytesToString(BB,0);
		long strDeCompile = System.nanoTime();
		
		if(!sinput.equalsIgnoreCase(soutput)){
			System.out.println("STRING PARSE TEST				[" + ConsoleColors.ANSI_RED + "  FAIL  " + ConsoleColors.ANSI_RESET + "]");
			throw new SerialiserNotWorkingException();
		}
		System.out.println("STRING PARSE TEST				[" + ConsoleColors.ANSI_GREEN + "   OK   " + ConsoleColors.ANSI_RESET + "]");
		
		Logger.log_server(Logger.LOG_VERB_LOW,"- OBJECT LIST TEST:");
		long arrStart = System.nanoTime();
		List<Object> Objects = new ArrayList<Object>();
		Objects.add(true);
		Objects.add("TESTING");
		Objects.add(12367893);
		Objects.add(true);
		Objects.add(192.168);
		Objects.add((byte) 243);
		Objects.add(236423629365L);
		long arrCre = System.nanoTime();
		byte bytes[] = Serialise(Objects.toArray(),true,true);
		long arrCompile = System.nanoTime();
		List<Object> ObjectOut = DeSerialise(bytes);
		long arrDeCompile = System.nanoTime();
		System.out.println("ARRAY PARSE TEST				[" + ConsoleColors.ANSI_GREEN + "   OK   " + ConsoleColors.ANSI_RESET + "]");
		
		Logger.log_server(Logger.LOG_VERB_LOW,"  Timings (nanoseconds):");
		Logger.log_server(Logger.LOG_VERB_LOW,"     Int Total: " + (intDeCompile-intStart));
		Logger.log_server(Logger.LOG_VERB_LOW,"     Int Compile: " + (intCompile-intStart));
		Logger.log_server(Logger.LOG_VERB_LOW,"     Int Deompile: " + (intDeCompile-intCompile));
		
		Logger.log_server(Logger.LOG_VERB_LOW,"     Str Total: " + (strDeCompile-strStart));
		Logger.log_server(Logger.LOG_VERB_LOW,"     Str Compile: " + (strCompile-strStart));
		Logger.log_server(Logger.LOG_VERB_LOW,"     Str Deompile: " + (strDeCompile-strCompile));
		
		Logger.log_server(Logger.LOG_VERB_LOW,"     Arr Total: " + (arrDeCompile-arrStart));
		Logger.log_server(Logger.LOG_VERB_LOW,"     Arr Make: " + (arrCre-strStart));
		Logger.log_server(Logger.LOG_VERB_LOW,"     Arr Compile: " + (arrCompile-arrCre));
		Logger.log_server(Logger.LOG_VERB_LOW,"     Arr Deompile: " + (arrDeCompile-arrCompile));
		
	}
	
	public static class SerialiserNotWorkingException extends Throwable{
		private static final long serialVersionUID = 108242225646451139L;
	}
	public static class InsufficientTypeList extends Throwable{
		private static final long serialVersionUID = 108242225646451140L;
	}
	
	public static final List<Object> DeSerialise(ByteBuffer BB) {
		List<Object> Objects = new ArrayList<Object>();
		if(dbg) Logger.log_server(Logger.LOG_VERB_LOW,"  parsing " + Functions.bytesToHex(BB.array()));
		
		while(BB.position() < BB.capacity()){
			Objects.add(DecodeObject(BB.get(),BB));
		}
		return Objects;
	}
	public static final List<Object> DeSerialise(byte[] bytes) {
		ByteBuffer BB = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
		return DeSerialise(BB) ;
	}
	
	public static final String DeserialiseString(ByteBuffer BB){
		int length = (int) BB.get();
		int i = 1;
		if(dbg) Logger.log_server(Logger.LOG_VERB_LOW,"     StringLength: " + length);
		StringBuilder SB = new StringBuilder();
		while(i <= length){
			SB.append((char) BB.get());
			i+=1;
		}
		return SB.toString();
	}


	
	public static final List<Object> DeSerialise(ByteBuffer BB, byte[] typs) {
		List<Object> Objects = new ArrayList<Object>();
		if(dbg) Logger.log_server(Logger.LOG_VERB_LOW,"  parsing " + Functions.bytesToHex(BB.array()));
		int typ = 0;
		while(BB.position() < BB.capacity()){
			if (typ >= typs.length) break;
			if(dbg) Logger.log_server(Logger.LOG_VERB_LOW," TYPE " + typ + " is " + typs[typ]);
			/* if (BB.get() == 0 && (typ !=2 || typ !=4 || typ !=5 || typ !=6 || typ !=3 || typ !=8)){
				if(dbg) Logger.log_server(Logger.LOG_VERB_LOW," Nulled because null");
				Objects.add(null);
				continue;
			}else{
				BB.position(BB.position() - 1);
			} */
			Objects.add(DecodeObject(typs[typ],BB));
			typ+=1;
			
		}
		return Objects;
	}
	
	
	public static final Object DecodeObject(byte typ, ByteBuffer BB){
		switch(typ){
		case (byte) 0:
			return null;
		case (byte) 1:
			return DeserialiseString(BB);
		case (byte) 2:
			return BB.getInt();
		case (byte) 3:
			return BB.getShort();
		case (byte) 4:
			return BB.getDouble();
		case (byte) 5:
			return BB.getFloat();
		case (byte) 6:
			return BB.get() == (byte) 255;
		case (byte) 7:
			return BB.get();
		case (byte) 8:
			return BB.getLong();
		case (byte) 101:
			Packet.IntrinsicType.Location L = new Packet.IntrinsicType.Location(BB);
		return L;
		case (byte) 102:
			Packet.IntrinsicType.TrainerData TD = new Packet.IntrinsicType.TrainerData(BB);
		return TD;
		case (byte) 103:
			Packet.IntrinsicType.PokeData PD = new Packet.IntrinsicType.PokeData(BB);
		return PD;
		case (byte) 104:
			Packet.IntrinsicType.AssetData AD = new Packet.IntrinsicType.AssetData(BB);
		return AD;
		default:
			return null;
		}
	}
	
	public static final List<Object> DeSerialise(byte[] bytes, byte[] typs) {
		ByteBuffer BB = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
		return DeSerialise(BB,typs) ;
	}
	
	public static final byte[] Serialise(Object objs[], boolean prependLength,boolean prependTypes) {
		ByteBuffer BB = ByteBuffer.allocate(1024).order(ByteOrder.LITTLE_ENDIAN);
		
		for(Object O : objs){
			if(O == null){
				BB.put((byte) 0);
			}else if(O instanceof String){
				if(prependTypes) BB.put((byte)1);
				String S = (String) O;
				BB.put((byte) S.length());
				if(dbg) Logger.log_server(Logger.LOG_VERB_LOW,"     Setting String Length: " + (byte) S.length());
				int C = 0;
				try {
					BB.put(S.getBytes("US-ASCII"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if(O instanceof Integer){
				if(prependTypes) BB.put((byte)2);
				BB.putInt((int)O);
			}else if(O instanceof byte[]){
				BB.put((byte[])O);
			}else if(O instanceof Short){
				if(prependTypes) BB.put((byte)3);
				BB.putShort((short)O);
			}else if(O instanceof Double){
				if(prependTypes) BB.put((byte)4);
				BB.putDouble((double)O);
			}else if(O instanceof Float){
				if(prependTypes) BB.put((byte)5);
				BB.putFloat((float)O);
			}else if(O instanceof Boolean){
				if(prependTypes) BB.put((byte)6);
				byte B = (byte) 0;
				if (((boolean) O) == true) B = (byte) 255;
				BB.put((byte)B);
			}else if(O instanceof Byte){	
				if(prependTypes) BB.put((byte)7);
				BB.put((byte)O);
			}else if(O instanceof Long){	
				if(prependTypes) BB.put((byte)8);
				BB.putLong((long)O);
			}else{
				if(prependTypes) BB.put((byte)0);
				
			}
		}
		byte[] bytes = BB.array();
		int oldlength = BB.position();
		int newlength = BB.position();
		if(prependLength) newlength = oldlength+4;
		ByteBuffer BC = ByteBuffer.allocate(newlength).order(ByteOrder.LITTLE_ENDIAN);
		if(prependLength){
			BC.putInt(oldlength);
			BC.put(bytes, 0, oldlength);
		}else{
			BC.put(bytes, 0, oldlength);
		}
		return BC.array();
	}
	
	
	
	public static final byte[] Serialise(Object s) {
		
		if(s instanceof String){
			return SerialiseObject((String) s);
		}else if(s instanceof Integer){
			return SerialiseObject((int) s);
		}else if(s instanceof Double){
			return SerialiseObject((double) s);
		}else if(s instanceof Short){
			return SerialiseObject((short) s);
		}else if(s instanceof Byte){
			return SerialiseObject((byte) s);
		}else if(s instanceof Long){
			return SerialiseObject((long) s);
		}else if(s instanceof Boolean){	
			return SerialiseObject((boolean) s);
		}else if(s instanceof Float){	
			return SerialiseObject((float) s);
		}
		return null;
		
	}

	
	public static final byte[] SerialiseObject(byte o){
		ByteBuffer BB = ByteBuffer.allocate(2);
		BB.put(0, (byte) 7);
		BB.put(o);
		return BB.array();
	}
	public static final byte[] SerialiseObject(boolean o){
		byte bytes[] = BoolToBytes(o);
		ByteBuffer BB = ByteBuffer.allocate(bytes.length +1);
		BB.put(0, (byte) 6);
		BB.put(bytes);
		return BB.array();
	}
	public static final byte[] SerialiseObject(float o){
		byte bytes[] = FloatToBytes(o);
		ByteBuffer BB = ByteBuffer.allocate(bytes.length +1);
		BB.put(0, (byte) 5);
		BB.put(bytes);
		return BB.array();
	}
	public static final byte[] SerialiseObject(double o){
		byte bytes[] = DoubleToBytes(o);
		ByteBuffer BB = ByteBuffer.allocate(bytes.length +1);
		BB.put(0, (byte) 4);
		BB.put(bytes);
		return BB.array();
	}	
	public static final byte[] SerialiseObject(short o){
		byte bytes[] = ShortToBytes(o);
		ByteBuffer BB = ByteBuffer.allocate(bytes.length +1);
		BB.put(0, (byte) 3);
		BB.put(bytes);
		return BB.array();
	}	
	public static final byte[] SerialiseObject(String o){
		byte bytes[] = StringToBytes(o);
		ByteBuffer BB = ByteBuffer.allocate(bytes.length +1);
		BB.put(0, (byte) 2);
		BB.put(bytes);
		return BB.array();
	}	
	public static final byte[] SerialiseObject(int o){
		byte bytes[] = IntToBytes(o);
		ByteBuffer BB = ByteBuffer.allocate(bytes.length +1);
		BB.put(0, (byte) 2);
		BB.put(bytes);
		return BB.array();
	}
	
	
	public static final byte[] StringToBytes(String s) {
		byte[] sbyte;
		try {
			sbyte = s.getBytes("US-ASCII");
			
			ByteBuffer BB = ByteBuffer.allocate(sbyte.length+1);
			BB.put((byte) sbyte.length);
			return BB.put(sbyte).array();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	
	}
	
	public static final String BytesToString(ByteBuffer BB, int loc) {
		int length = (int) BB.get(loc);
		if(dbg) Logger.log_server(Logger.LOG_VERB_LOW,"String bytelength: " + length);
		byte input[] = new byte[length];
		int i = 0;
		BB.position(1);
		while(length > i){
			input[i] = BB.get();
			i+=1;
		}
		String str = new String(input);
		return str;
	}
	public static final byte[] IntToBytes(int value) {
	    return new byte[] {
	            (byte)(value),
	            (byte)(value >>> 8 ),
	            (byte)(value >>> 16),
	            (byte)(value >>> 24)};
	}
	public static final byte[] ShortToBytes(short value) {
	    return new byte[] {
	            (byte)(byte)(value & 0xff),
	            (byte)(byte)((value >> 8) & 0xff)};
	}
	public static byte[] DoubleToBytes(double value) {
	    byte[] bytes = new byte[9];
	    ByteBuffer.wrap(bytes).putDouble(value);
	    return bytes;
	}
	public static byte[] FloatToBytes(float value){
		int bits = Float.floatToIntBits(value);
		byte[] bytes = new byte[]{
				(byte)(bits & 0xff),
		(byte)((bits >> 8) & 0xff),
		(byte)((bits >> 16) & 0xff),
		(byte)((bits >> 24) & 0xff)};
		return bytes;
	}
	public static byte[] BoolToBytes(boolean value){
		byte var = (byte) 0;
		if(value){
			var = (byte) 255;
		}else{
			var = (byte) 0;
		}
		byte[] bytes = new byte[]{
				var};
		return bytes;
	}
	
	public static double ByteToDouble(ByteBuffer BB) {
	    return 0;//ByteBuffer.wrap(bytes).getDouble();
	}
	public static int BytestoInt(byte[] bytes) {

		  int ret = 0;
		  for (int i=0; i<4 && i+0<bytes.length; i++) {
		    ret <<= 8;
		    ret |= (int)bytes[i] & 0xFF;
		  }
		  return ret;
		}
	public static final byte[] concat(byte[] a, byte[] b) {
		   int aLen = a.length;
		   int bLen = b.length;
		   byte[] c= new byte[aLen+bLen];
		   System.arraycopy(a, 0, c, 0, aLen);
		   System.arraycopy(b, 0, c, aLen, bLen);
		   return c;
		}
}
