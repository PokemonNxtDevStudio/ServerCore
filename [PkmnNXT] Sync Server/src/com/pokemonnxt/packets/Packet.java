package com.pokemonnxt.packets;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import com.pokemonnxt.gameserver.Functions;
import com.pokemonnxt.gameserver.Logger;
import com.pokemonnxt.packets.Packet.IntrinsicType.PokeData;
import com.pokemonnxt.types.Asset;
import com.pokemonnxt.types.Location;
import com.pokemonnxt.types.pokemon.Pokemon;
import com.pokemonnxt.types.trainer.Trainer;


public interface Packet {
	public static enum PACKET_TYPE {
		ERROR(-1), UNKNOWN(0), LOGIN(1), LOGIN_RESPONSE(2), TRAINER_DATA(3), CHAT(4), LOCATION(5), OWNERSHIP_UPDATE(6), DATA_REQUEST(7), REQUEST_FAILED(8), POKEMON_DATA(9), ASSET_DATA(10) ;
        private  int value;
        
        private static Map<Integer, PACKET_TYPE> map = new HashMap<Integer, PACKET_TYPE>();

        static {
            for (PACKET_TYPE legEnum : PACKET_TYPE.values()) {
                map.put(legEnum.value, legEnum);
            }
        }
        public static PACKET_TYPE valueOf(int legNo) {
            return map.get(legNo);
        }
        private PACKET_TYPE(int value) {
                this.value = value;
        }
	};
	
	
	byte[] Compile();
	PACKET_TYPE getType();
	
	// Compile must include headers.
	
 class Login implements Packet{
		
	 byte TypeOrder[] = {(byte)1,(byte)1,(byte)1};
	 public static byte tx_sessionBytes[] = {(byte)(2)};
		public static	 byte tx_presentationBytes[] = {(byte)(1)};
		
		public Login(String Usrname, String Pass){
			Username = Usrname;
			Password = Pass;
		}
		
		public Login(byte[] Packed){
			Object unpacked[] =  Serialiser.DeSerialise(Packed,TypeOrder).toArray();
			Logger.log_server(Logger.LOG_PROGRESS,  "Packet Sent: " + Functions.bytesToHex(Packed));
			Logger.log_server(Logger.LOG_PROGRESS,  "Has objects: " + unpacked.toString());
			Username = (String) unpacked[0];
			Password = (String) unpacked[1];
			Email = (String) unpacked[2];
		}
		
		public byte[] Compile(){
			Object Out[] = {(byte)1,(byte)1,Username,Password,Email};
			byte bytes[] = Serialiser.Serialise(Out,true,false);
			return bytes;
		}
		
		public String Username = "X";
		public String Password = "X";
		public String Email = "X";
		public PACKET_TYPE getType() {
			return PACKET_TYPE.LOGIN;
		}

		
		
		
	}
 
 class TrainerData implements Packet{
		
	 public static byte tx_sessionBytes[] = {(byte)(1)};
		public static	 byte tx_presentationBytes[] = {(byte)(4)};
		
	 	public TrainerData(Trainer[] trainers){
	 		
	 		Trainers = new IntrinsicType.TrainerData[trainers.length] ;
	 		int cnt = 0;
	 		for(Trainer t : trainers){
	 			Trainers[cnt] = new IntrinsicType.TrainerData(t);
	 			cnt +=1;
	 		}
		}
	 	
		public TrainerData(byte[] Packed){
			ByteBuffer BB = ByteBuffer.wrap(Packed);
			byte count = BB.get();
			byte b = 0;
			Trainers = new IntrinsicType.TrainerData[count - 1];
			while(b<count){
				Trainers[b] = new IntrinsicType.TrainerData(BB);
				b+=1;
			}
		}
		
		public byte[] Compile(){
			Object Out[] = {tx_sessionBytes,tx_presentationBytes,Build()};
			byte bytes[] = Serialiser.Serialise(Out,true,false);
			return bytes;
		}
		byte Packed[];
		
		public byte[] Build(){
			if (Packed == null){
				int len = 0;
				int cnt = 0;
				byte[][] bys = new byte[Trainers.length][];
				for(IntrinsicType.TrainerData tds : Trainers){
					bys[cnt] = tds.Build();
					len += bys[cnt].length;
					cnt+=1;
				}
				ByteBuffer BB =  ByteBuffer.allocate(len+1);
				BB.put((byte) Trainers.length);
				for (byte[] byb : bys){
					BB.put(byb);
				}
				Packed = BB.array();
			}
			
			return Packed;
		}
		
		public IntrinsicType.TrainerData[] Trainers;
		
		public PACKET_TYPE getType() {
			return PACKET_TYPE.TRAINER_DATA;
		}

		
		
		
	}
 class PokemonData implements Packet{
		
	 public static byte tx_sessionBytes[] = {(byte)(1)};
		public static	 byte tx_presentationBytes[] = {(byte)(5)};
		
	 	public PokemonData(Pokemon[] pokes){
	 		
	 		Pokemons = new IntrinsicType.PokeData[pokes.length] ;
	 		int cnt = 0;
	 		for(Pokemon p : pokes){
	 			Pokemons[cnt] = new IntrinsicType.PokeData(p);
	 			cnt +=1;
	 		}
		}
	 	
		public PokemonData(byte[] Packed){
			ByteBuffer BB = ByteBuffer.wrap(Packed);
			byte count = BB.get();
			byte b = 0;
			Pokemons = new IntrinsicType.PokeData[count - 1];
			while(b<count){
				Pokemons[b] = new IntrinsicType.PokeData(BB);
				b+=1;
			}
		}
		
		public byte[] Compile(){
			Object Out[] = {tx_sessionBytes,tx_presentationBytes,Build()};
			byte bytes[] = Serialiser.Serialise(Out,true,false);
			return bytes;
		}
		byte Packed[];
		
		public byte[] Build(){
			if (Packed == null){
				int len = 0;
				int cnt = 0;
				byte[][] bys = new byte[Pokemons.length][];
				for(IntrinsicType.PokeData tds : Pokemons){
					bys[cnt] = tds.Build();
					len += bys[cnt].length;
					cnt+=1;
				}
				ByteBuffer BB =  ByteBuffer.allocate(len+1);
				BB.put((byte) Pokemons.length);
				for (byte[] byb : bys){
					BB.put(byb);
				}
				Packed = BB.array();
			}
			
			return Packed;
		}
		
		public IntrinsicType.PokeData[] Pokemons;
		
		public PACKET_TYPE getType() {
			return PACKET_TYPE.POKEMON_DATA;
		}

		
		
		
	}
 
 class AssetData implements Packet{
		
	 public static byte tx_sessionBytes[] = {(byte)(1)};
		public static	 byte tx_presentationBytes[] = {(byte)(6)};
		
	 	public AssetData(Asset[] pokes){
	 		
	 		Assets = new IntrinsicType.AssetData[pokes.length] ;
	 		int cnt = 0;
	 		for(Asset p : pokes){
	 			Assets[cnt] = new IntrinsicType.AssetData(p);
	 			cnt +=1;
	 		}
		}
	 	
		public AssetData(byte[] Packed){
			ByteBuffer BB = ByteBuffer.wrap(Packed);
			byte count = BB.get();
			byte b = 0;
			Assets = new IntrinsicType.AssetData[count - 1];
			while(b<count){
				Assets[b] = new IntrinsicType.AssetData(BB);
				b+=1;
			}
		}
		
		public byte[] Compile(){
			Object Out[] = {tx_sessionBytes,tx_presentationBytes,Build()};
			byte bytes[] = Serialiser.Serialise(Out,true,false);
			return bytes;
		}
		byte Packed[];
		
		public byte[] Build(){
			if (Packed == null){
				int len = 0;
				int cnt = 0;
				byte[][] bys = new byte[Assets.length][];
				for(IntrinsicType.AssetData tds : Assets){
					bys[cnt] = tds.Build();
					len += bys[cnt].length;
					cnt+=1;
				}
				ByteBuffer BB =  ByteBuffer.allocate(len+1);
				BB.put((byte) Assets.length);
				for (byte[] byb : bys){
					BB.put(byb);
				}
				Packed = BB.array();
			}
			
			return Packed;
		}
		
		public IntrinsicType.AssetData[] Assets;
		
		public PACKET_TYPE getType() {
			return PACKET_TYPE.ASSET_DATA;
		}

		
		
		
	}
 
 class OwnershipUpdate implements Packet{
		
		public static byte tx_sessionBytes[] = {(byte)(1)};
		public static	 byte tx_presentationBytes[] = {(byte)(3)};
		
		byte TypeOrder[] = {(byte)7,(byte)7,(byte)2,(byte)1,(byte)2};
		
		public int AssetID = 0;
		public boolean Giving = false;
		
		public OwnershipUpdate(int lAID, boolean lgiving){
			Giving = lgiving;
			AssetID = lAID;
		}
		
		public OwnershipUpdate(Object[] unpacked){
			unpack(unpacked);
		}
		
		public OwnershipUpdate(byte[] Packed){
			Object unpacked[] =  Serialiser.DeSerialise(Packed,TypeOrder).toArray();
			unpack(unpacked);
		}
		
		
		private void unpack(Object[] unpacked){
			AssetID = (int) unpacked[0];
			Giving = (boolean) unpacked[1];
		}
		
		
		public byte[] Compile(){
			Object Out[] = {tx_sessionBytes,tx_presentationBytes,Build()};
			byte bytes[] = Serialiser.Serialise(Out,true,false);
			return bytes;
		}
		
		public byte[] Build(){
			Object Out[] = {AssetID,Giving};
			byte bytes[] = Serialiser.Serialise(Out,false,false);
			return bytes;
		}
		
		
		public PACKET_TYPE getType() {
			return PACKET_TYPE.OWNERSHIP_UPDATE;
		}

		
		
		
	}
 
 
 class DataRequest implements Packet{
		
		public static byte tx_sessionBytes[] = {(byte)(1)};
		public static	 byte tx_presentationBytes[] = {(byte)(3)};
		byte TypeOrder[] = {(byte)7,(byte)2,(byte)6};
		
		public REQUEST DAT = REQUEST.UNKNOWN;
		public int AssetID = 0;
		public boolean ReturnObject = false;
		
		public static enum REQUEST {
			UNKNOWN((byte) 0), SCENE((byte) 1), TRAINER((byte) 2), POKEMON((byte) 3), ASSET((byte) 4) ;
	        private  Byte value;
	        
	        private static Map<Byte, REQUEST> map = new HashMap<Byte, REQUEST>();

	        static {
	            for (REQUEST legEnum : REQUEST.values()) {
	                map.put(legEnum.value, legEnum);
	            }
	        }
	        public static REQUEST valueOf(byte legNo) {
	            return map.get(legNo);
	        }
	        private REQUEST(Byte value) {
	                this.value = value;
	        }
		};
		
		public DataRequest(REQUEST lDAT, int lAID, boolean lReturnObject){
			DAT = lDAT;
			AssetID = lAID;
			ReturnObject = lReturnObject;
			
		}
		
		public DataRequest(byte[] Packed){
			Object unpacked[] =  Serialiser.DeSerialise(Packed,TypeOrder).toArray();
			unpack(unpacked);
		}
		
		public DataRequest(Object[] unpacked){
			unpack(unpacked);
		}
		
		private void unpack(Object[] unpacked){
			if (unpacked.length ==3){
			DAT = REQUEST.valueOf((byte) unpacked[0]);
			AssetID = (int) unpacked[1];
			ReturnObject = (boolean) unpacked[2];
			}else{
				DAT = REQUEST.valueOf((byte) unpacked[0]);
				AssetID = (int) unpacked[1];
				ReturnObject = false;
			}
		}
		
		public byte[] Compile(){
			Object Out[] = {tx_sessionBytes,tx_presentationBytes,Build()};
			byte bytes[] = Serialiser.Serialise(Out,true,false);
			return bytes;
		}
		
		public byte[] Build(){
			Object Out[] = {DAT,AssetID,ReturnObject};
			byte bytes[] = Serialiser.Serialise(Out,false,false);
			return bytes;
		}

		
		public PACKET_TYPE getType() {
			return PACKET_TYPE.DATA_REQUEST;
		}

		
		
		
}
 
 class LoginResponse implements Packet{
	 public static enum LOGIN_RESPONSES {
			ERROR(-1), UNKNOWN(0), SUCCESSFUL(1), INVALID_USERNAME(2), EMAIL_EXISTS(3),INVALID_PASSWORD(4);
	        private  int value;
	        
	        private static Map<Integer, LOGIN_RESPONSES> map = new HashMap<Integer, LOGIN_RESPONSES>();

	        static {
	            for (LOGIN_RESPONSES legEnum : LOGIN_RESPONSES.values()) {
	                map.put(legEnum.value, legEnum);
	            }
	        }
	        public static LOGIN_RESPONSES valueOf(int legNo) {
	            return map.get(legNo);
	        }
	        private LOGIN_RESPONSES(int value) {
	                this.value = value;
	        }
		};
		
		public LoginResponse(LOGIN_RESPONSES Typ, String Msg, IntrinsicType.TrainerData TB){
			RES = Typ;
			Trainer = TB;
			Message = Msg;
		}
		
		public LoginResponse(Object[] unpacked){
			RES = LOGIN_RESPONSES.valueOf((int) unpacked[2]);
			//Password = (String) unpacked[3];
			//Email = (String) unpacked[4];
		}
		
		public static byte tx_sessionBytes[] = {(byte)(1)};
		public static	 byte tx_presentationBytes[] = {(byte)(1)};
		
		public byte[] Compile(){
			Object Out[];
			if(Trainer != null){
				Out =  new Object[]{tx_sessionBytes,tx_presentationBytes,(byte)RES.value,Message,Trainer.Build()};
			}else{
				Out = new Object[]{tx_sessionBytes,tx_presentationBytes,(byte)RES.value,Message, null};
			}
			
			byte bytes[] = Serialiser.Serialise(Out,true,false);
			return bytes;
			
		}
		
		public LOGIN_RESPONSES RES = LOGIN_RESPONSES.UNKNOWN;
		public String Message;
		public IntrinsicType.TrainerData Trainer;
		public PACKET_TYPE getType() {
			return PACKET_TYPE.LOGIN_RESPONSE;
		}

		
		
		
	}
 

 class RequestFailed implements Packet{
		
		public static byte tx_sessionBytes[] = {(byte)(1)};
		public static	 byte tx_presentationBytes[] = {(byte)(7)};
		byte TypeOrder[] = {(byte)7,(byte)7,(byte)7,(byte)1};
		
		public FAILURE FAL = FAILURE.ERROR;
		public String Message = "No Message Set";
		
		
		public RequestFailed(byte lFAL, String lMessage){
			FAL = FAILURE.valueOf(lFAL);
			Message = lMessage;
			
		}
		public RequestFailed(FAILURE lFAL, String lMessage){
			FAL = lFAL;
			Message = lMessage;
			
		}
		
		public static enum FAILURE {
			ERROR((byte) 0), INCORRECT_CREDENTIALS((byte) 1), INSUFFICIENT_PERMISSIONS((byte) 2), LOAD_REACHED((byte) 3), OVERLOADED((byte) 4),
			STUPID_REQUEST((byte) 5), MALFORMED_REQUEST((byte) 6), PARSE_ERROR((byte) 7), NOT_IMPLEMENTED((byte) 8), SERVER_FAULT((byte) 9), UNKNOWN_REQUEST((byte) 10),
			DATA_MISSING((byte) 10);
	        private  Byte value;
	        
	        private static Map<Byte, FAILURE> map = new HashMap<Byte, FAILURE>();

	        static {
	            for (FAILURE legEnum : FAILURE.values()) {
	                map.put(legEnum.value, legEnum);
	            }
	        }
	        public static FAILURE valueOf(byte legNo) {
	            return map.get(legNo);
	        }
	        private FAILURE(Byte value) {
	                this.value = value;
	        }
		};
		
		
		public RequestFailed(Object[] unpacked){
			FAL = FAILURE.valueOf((byte) unpacked[0]);
			Message = (String) unpacked[1];
		}
		
		public byte[] Compile(){
			Object Out[] = {tx_sessionBytes,tx_presentationBytes,Build()};
			byte bytes[] = Serialiser.Serialise(Out,true,false);
			return bytes;
		}
		
		public byte[] Build(){
			Object Out[] = {FAL,Message};
			byte bytes[] = Serialiser.Serialise(Out,false,false);
			return bytes;
		}

		
		public PACKET_TYPE getType() {
			return PACKET_TYPE.REQUEST_FAILED;
		}

		
		
		
}
 
 class ChatRX implements Packet{
		
		public ChatRX(String Msg){
			Message = Msg;
		}
		byte TypeOrder[] = {(byte)1};
		
		public ChatRX(byte[] Packed){
			Object unpacked[] =  Serialiser.DeSerialise(Packed,TypeOrder).toArray();
			Message = (String) unpacked[0];
		}
		
		
		public byte[] Compile(){
			Object Out[] = {Message};
			byte bytes[] = Serialiser.Serialise(Out,true,false);
			return bytes;
		}
		
		public String Message;
		
		public PACKET_TYPE getType() {
			return PACKET_TYPE.CHAT;
		}

		
		
		
	}
 class ChatTX implements Packet{
		
		public ChatTX(String Msg, int sender){
			Message = Msg;
			SenderGTID = sender;
		}
		byte TypeOrder[] = {(byte)2,(byte)1};
		public static byte tx_sessionBytes[] = {(byte)(1)};
		public static	 byte tx_presentationBytes[] = {(byte)(2)};
		public ChatTX(byte[] Packed){
			Object unpacked[] =  Serialiser.DeSerialise(Packed,TypeOrder).toArray();
			Message = (String) unpacked[1];
			SenderGTID = (int) unpacked[0];
		}
		
		public byte[] Compile(){
			Object Out[] = {tx_sessionBytes,tx_presentationBytes,SenderGTID, Message};
			byte bytes[] = Serialiser.Serialise(Out,true,false);
			return bytes;
		}
		
		public String Message;
		public int SenderGTID;
		public PACKET_TYPE getType() {
			return PACKET_TYPE.CHAT;
		}

		
		
		
	}
 
 class LocationUpdate implements Packet{
		
	public static byte tx_sessionBytes[] = {(byte)(2)};
	public static	 byte tx_presentationBytes[] = {(byte)(1)};
	
	public Location location;
		public LocationUpdate(int AID, float lX, float lY,float lZ, short lPitch,short lYaw,short lRoll){
			AssetID = AID;
			X = lX;
			Y = lY;
			Z = lZ;
			Pitch = lPitch;
			Yaw = lYaw;
			Roll = lRoll;
			location = new Location(X,Y,Z,Pitch,Yaw,Roll);
		}
		byte TypeOrder[] = {(byte)2,(byte)5,(byte)5,(byte)5,(byte)3,(byte)3,(byte)3};
		
		
		public LocationUpdate(byte[] Packed){
			Object unpacked[] =  Serialiser.DeSerialise(Packed,TypeOrder).toArray();
			AssetID = (int) unpacked[0];
			X = (float) unpacked[1];
			Y = (float) unpacked[2];
			Z = (float) unpacked[3];
			Pitch = (short) unpacked[4];
			Yaw = (short) unpacked[5];
			Roll = (short) unpacked[6];
			location = new Location(X,Y,Z,Pitch,Yaw,Roll);
			int plength = Packed.length + tx_sessionBytes.length + tx_presentationBytes.length;
			ByteBuffer BB = ByteBuffer.allocate(plength +4);
			BB.put(Serialiser.IntToBytes(plength));
			BB.put(tx_sessionBytes);
			BB.put(tx_presentationBytes);
			BB.put(Packed);
			Packaged = BB.array();
		}
		byte[] Packaged;
		public byte[] Compile(){
			return Packaged;
		}
		public int AssetID;
		public float X;
		public float Y;
		public float Z;
		public short Pitch;
		public short Yaw;
		public short Roll;
		
		public PACKET_TYPE getType() {
			return PACKET_TYPE.LOCATION;
		}

		
		
		
	}
 
 
 // Intrinsic types: Not to be packeted directly
 
 public interface IntrinsicType {
	 
		byte[] Build();
	 
		
 class TrainerData implements IntrinsicType{
			 	static byte TypeOrder[] = {(byte)2,(byte)3,(byte)2,(byte)3,(byte)101,(byte)7};

				public TrainerData(Trainer T){
					Username = T.Name;
					GTID = T.GTID;
					AssetID = T.AID;
					PrefabIndex = T.BaseModel;
					Loc = T.location.toCommType();
					byte b = 0;
					for(Pokemon P : T.Party){
						PartyPokemon[b] = new PokeData(P);
						b+=1;
					}
				}
				
				public TrainerData(ByteBuffer BB){
					Object[] unpacked = Serialiser.DeSerialise(BB,TypeOrder).toArray();
					GTID = (int) unpacked[0];
					PrefabIndex = (short) unpacked[1];
					AssetID = (int) unpacked[2];
					Username = (String) unpacked[3];
					Loc = (Location) unpacked[4];
					byte PPLength = (byte) unpacked[5];
					byte b = 0;
					while(b< PPLength){
						PartyPokemon[b] = new PokeData(BB);
					}
				}
				
				byte Packed[];
				public byte[] Build(){
					if(Packed == null){
						Logger.log_server(Logger.LOG_VERB_LOW, "Building Trainer packet");
					Object Out[] = {GTID,PrefabIndex,AssetID,Username,Loc.Build()};
					Packed = Serialiser.Serialise(Out,false,false);
					}
					Logger.log_server(Logger.LOG_VERB_LOW, "Trainer packet: " + Functions.bytesToHex(Packed));
					return Packed;
				}
				
				public int AssetID = 0;
				public int GTID = 0;
				public String Username = "X";
				public Location Loc;
				public short PrefabIndex;
				public PokeData[] PartyPokemon  = new PokeData[6];
				
			}
		 
		 
 class PokeData{
		
		byte TypeOrder[] = {(byte)2,(byte)2,(byte)3,(byte)1};
		
		public int GPID = (byte) 255;
		public int AssetID = 0;
		public short DEX = 0;
		public String NickName = "";
		public short PrefabIndex;
		byte Compiled[];
		
		public PokeData(Pokemon p){
			GPID = p.GPID;
			AssetID = p.AID;
			DEX = p.DEX;
			NickName = p.Name;
			PrefabIndex = p.Model;
		}
		public PokeData(ByteBuffer BB){
			Object[] unpacked = Serialiser.DeSerialise(BB,TypeOrder).toArray();
			GPID = (byte) unpacked[0];
			PrefabIndex = (short) unpacked[1];
			AssetID = (int) unpacked[2];
			DEX = (short) unpacked[3];
			NickName = (String) unpacked[4];
		}
		
		
		
		public byte[] Build(){
			if (Compiled == null){
				Object Out[] = {GPID,AssetID,DEX,NickName};
				Compiled = Serialiser.Serialise(Out,false,false);
			}
			return Compiled;
		}
		
}
 
 class AssetData{
		
		byte TypeOrder[] = {(byte)2,(byte)101};
		
		public int AssetID = 0;
		public Location location;
		
		byte Compiled[];
		
		public AssetData(Asset a){
			AssetID = a.AID;
			location = new Location(a.location);
		}
		public AssetData(ByteBuffer BB){
			Object[] unpacked = Serialiser.DeSerialise(BB,TypeOrder).toArray();
			AssetID = (int) unpacked[0];
			location = (Location) unpacked[1];
		}
		
		
		
		public byte[] Build(){
			if (Compiled == null){
				Object Out[] = {AssetID,location.Build()};
				Compiled = Serialiser.Serialise(Out,false,false);
			}
			return Compiled;
		}
		
}
 
 
 class Location implements Packet.IntrinsicType{
		
	 public float X;
		public float Y;
		public float Z;
		public short Pitch;
		public short Yaw;
		public short Roll;
		
	public com.pokemonnxt.types.Location location;
	
		public Location(com.pokemonnxt.types.Location L){
			location = L;
			X = location.X;
			Y = location.Y;
			Z = location.Z;
			Pitch = location.P;
			Yaw = location.Ya;
			Roll = location.R;
			
		}
		public Location(float lX, float lY,float lZ, short lPitch,short lYaw,short lRoll){
			X = lX;
			Y = lY;
			Z = lZ;
			Pitch = lPitch;
			Yaw = lYaw;
			Roll = lRoll;
			location = new com.pokemonnxt.types.Location(X,Y,Z,Pitch,Yaw,Roll);
			
		}
		byte TypeOrder[] = {(byte)5,(byte)5,(byte)5,(byte)3,(byte)3,(byte)3};
		
		public Location(ByteBuffer BB){
			Object unpacked[] =  Serialiser.DeSerialise(BB,TypeOrder).toArray();
			X = (float) unpacked[0];
			Y = (float) unpacked[1];
			Z = (float) unpacked[2];
			Pitch = (short) unpacked[3];
			Yaw = (short) unpacked[4];
			Roll = (short) unpacked[5];
			location = new com.pokemonnxt.types.Location(X,Y,Z,Pitch,Yaw,Roll);
		}
		
		public Location(byte[] Packed){
			Object unpacked[] =  Serialiser.DeSerialise(Packed,TypeOrder).toArray();
			X = (float) unpacked[0];
			Y = (float) unpacked[1];
			Z = (float) unpacked[2];
			Pitch = (short) unpacked[3];
			Yaw = (short) unpacked[4];
			Roll = (short) unpacked[5];
			location = new com.pokemonnxt.types.Location(X,Y,Z,Pitch,Yaw,Roll);
			Packaged = Packed;
		}
		
		byte[] Packaged;
		
		
		
		@Override
		public byte[] Build() {
			if (Packaged == null){
				Packaged =  Serialiser.Serialise(new Object[] {X,Y,Z,Pitch,Yaw,Roll}, false, false);
			}
			return Packaged;
		}

		
		
		
	}
 
 
 }
}

