package com.pokemonnxt.types;

import com.google.gson.annotations.Expose;
import com.pokemonnxt.gameserver.Zones;
import com.pokemonnxt.packets.Communications;
import com.pokemonnxt.packets.Communications.LOCATION;

public class Location {
	@Expose public double X = -1;
	@Expose public double Y = -1;
	@Expose public double Z = -1;
	
	@Expose public double P = 0; //Pitch
	@Expose public double Ya = 0; //Yaw
	@Expose public double R = 0; //Roll
	
	public Zone zone;
	
public Location(){
	X = -1;
	Y = -1;
	Z = -1;
	P = 0;
	Ya = 0;
	R = 0;
	
}

public Location(double Xl, double Yl, double Zl, double Pl, double Yal, double Rl){
	X = Xl;
	Y = Yl;
	Z = Zl;
	P = Pl;
	Ya = Yal;
	R = Rl;
	
}
public void Move(double Xl, double Yl, double Zl, double Pl, double Yal, double Rl){
	X = Xl;
	Y = Yl;
	Z = Zl;
	P = Pl;
	Ya = Yal;
	R = Rl;
	if(!zone.inZone(this)){
		zone = Zones.getZone(this);
	}
}
public void Move(Location NL){
	Move(NL.X,NL.Y,NL.Z,NL.P,NL.Ya,NL.R);
}
public Location(Communications.LOCATION LO){
	X = LO.getX();
	Y = LO.getY();
	Z = LO.getZ();
	P = LO.getPitch();
	Ya = LO.getYaw();
	R = LO.getRoll();
	
}
public  LOCATION toPayload(){
	LOCATION payload =
			LOCATION.newBuilder()
			.setX(X)
			.setY(Y)
			.setZ(Z)
			.setPitch(P)
			.setYaw(Ya)
			.setRoll(R)
			.build();
	return payload;
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
