package com.pokemonnxt.types;

import com.google.gson.annotations.Expose;
import com.pokemonnxt.gameserver.Zones;
import com.pokemonnxt.packets.Packet;
//GIT UPDATE
public class Location {
	@Expose public float X = -1;
	@Expose public float Y = -1;
	@Expose public float Z = -1;
	
	@Expose public short P = 0; //Pitch
	@Expose public short Ya = 0; //Yaw
	@Expose public short R = 0; //Roll
	
	public Zone zone;
	
public Location(){
	X = -1;
	Y = -1;
	Z = -1;
	P = 0;
	Ya = 0;
	R = 0;
	
}

public Location(float Xl, float Yl, float Zl, short Pl, short Yal, short Rl){
	X = Xl;
	Y = Yl;
	Z = Zl;
	P = Pl;
	Ya = Yal;
	R = Rl;
	
}
public void Move(float Xl, float Yl, float Zl, short Pl, short Yal, short Rl){
	X = Xl;
	Y = Yl;
	Z = Zl;
	P = Pl;
	Ya = Yal;
	R = Rl;
	if(false){
	if(!zone.inZone(this)){
		zone = Zones.getZone(this);
	}
	}
}
public void Move(Location NL){
	Move(NL.X,NL.Y,NL.Z,NL.P,NL.Ya,NL.R);
}
public Location(Packet.IntrinsicType.Location LO){
	X = LO.X;
	Y = LO.Y;
	Z = LO.Z;
	P = LO.Pitch;
	Ya = LO.Yaw;
	R = LO.Roll;
	
}
public Packet.IntrinsicType.Location toCommType(){
	Packet.IntrinsicType.Location PL = new Packet.IntrinsicType.Location(this);
	return PL;
}
public boolean isNear(Location LC2, int proximity){
	double xdist = Math.abs(LC2.X-X);
	double ydist = Math.abs(LC2.Y-Y);
	double zdist = Math.abs(LC2.Z-Z);
	if(xdist < proximity && ydist < proximity && zdist < proximity){
		return true;
	}else{
		return false;
	}
}
}
