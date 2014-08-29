package com.pokemonnxt.sync;

import com.google.protobuf.InvalidProtocolBufferException;
import com.pokemonnxt.packets.Communications;
import com.pokemonnxt.packets.Communications.LOGIN;

public class PacketInterpreter {

	public PacketInterpreter() {
		// TODO Auto-generated constructor stub
	}

	public static Communications.LOGIN GetLogin(byte Data[]){
		try {
			return Communications.LOGIN.parseFrom(Data);
		} catch (InvalidProtocolBufferException e) {
			GlobalExceptionHandler GEH = new GlobalExceptionHandler();
			GEH.uncaughtException(Thread.currentThread(), (Throwable) e);
			e.printStackTrace();
			return null;
		}
	}
}
