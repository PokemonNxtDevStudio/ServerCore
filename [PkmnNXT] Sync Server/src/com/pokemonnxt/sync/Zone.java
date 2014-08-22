package com.pokemonnxt.sync;

import java.awt.Point;
import java.awt.Polygon;

public class Zone {
	public Polygon Area;
	public int Lbound;
	public int Tbound;
	
	public Zone(String file) {
		// TODO This is where we load the area data from the file
	}
	public boolean inZone(Location L){
		if (L.Z > Tbound) return false;
		if (L.Z < Lbound) return false;
		Point P = new Point();
		P.x = (int) L.X;
		P.y = (int) L.Y;
		return Area.contains(P);
	}
	public boolean inZone(Point P){
		return Area.contains(P);
	}
	public boolean inZone(Location L, Point P){
		if (L.Z > Tbound) return false;
		if (L.Z < Lbound) return false;
		return Area.contains(P);
	}
}
