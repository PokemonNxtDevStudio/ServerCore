package sync.pokemonnxt.com;

import com.google.gson.annotations.Expose;

public class PacketPLUD {
		@Expose public PacketHeader header;
		@Expose public PACKETPLUD payload;
		
		public class PACKETPLUD {
			@Expose public int GTID;
			@Expose public Location LOC = new Location();
		 }

	

}
