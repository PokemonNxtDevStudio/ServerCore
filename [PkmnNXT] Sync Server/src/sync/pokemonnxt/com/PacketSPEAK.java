package sync.pokemonnxt.com;

import com.google.gson.annotations.Expose;

public class PacketSPEAK{
		@Expose public PacketHeader header;
		@Expose public PACKETSPEAK payload;
		
		public class PACKETSPEAK {
			@Expose public String Message;
		 }

	

}
