package com.pokemonnxt.types;

import com.pokemonnxt.gameserver.Logger;

public class Area {
	public Location BoundA;
	public Location BoundB;
	
	public double Width(){
		if(BoundA.X < BoundB.X){
			return BoundB.X - BoundA.X;
		}else{
			return BoundA.X - BoundB.X;
		}
	}
	public boolean isInside(Location LOC){
		if(BoundA.X < BoundB.X){
			if (LOC.X < BoundA.X) return false;
			if (LOC.X > BoundB.X) return false;
		}else{
			if (LOC.X > BoundA.X) return false;
			if (LOC.X < BoundB.X) return false;
		}
		if(BoundA.Z < BoundB.Z){
			if (LOC.Z < BoundA.Z) return false;
			if (LOC.Z > BoundB.Z) return false;
		}else{
			if (LOC.Z > BoundA.Z) return false;
			if (LOC.Z < BoundB.Z) return false;
		}
		return true;
	}
	public Area(Location A, Location B) {
		if (A.Y != 0 || B.Y != 0){
			Logger.log_server(Logger.LOG_ERROR, "Area initialised with non-zero vertical bounds");
			return;
		}
		BoundA = A;
		BoundB = B;
	}
	
}
