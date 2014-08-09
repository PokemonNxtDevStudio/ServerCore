package sync.pokemonnxt.com;

import com.google.gson.annotations.Expose;

public class PacketDATA_REQUEST {
	@Expose public PacketHeader header;
	@Expose public PACKETDATA_REQUEST payload;

	public class PACKETDATA_REQUEST {
		@Expose public String Type;
	
	}

}
