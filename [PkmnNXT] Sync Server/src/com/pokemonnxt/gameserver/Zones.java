package com.pokemonnxt.gameserver;

import java.io.File;

import com.pokemonnxt.types.Location;
import com.pokemonnxt.types.Zone;



public class Zones {
	public static Zone[] Zones = new Zone[1024];
	
	public static void reloadZones(){
		Logger.log_server(Logger.LOG_PROGRESS, "Loading Zones...");
		Zone[] Zones = new Zone[1024];
		File folder = new File("/etc/NXT_SERVER/ZONES");
		File[] files = folder.listFiles();
		
		for(File fileEntry : files) {
	        if (!fileEntry.isDirectory()) {
	        	Zone z = new Zone(fileEntry.getAbsolutePath());
	        	Zones[z.ID] = z;
	        }
	    }
	}
	
	public static Zone getZone(Location L){
		for(Zone z : Zones){
			if (z == null) continue;
		if(z.inZone(L)) return z;
		}
		return null;
	}
}
