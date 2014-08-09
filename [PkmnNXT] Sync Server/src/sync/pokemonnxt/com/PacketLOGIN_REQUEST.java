package sync.pokemonnxt.com;

import com.google.gson.annotations.Expose;

public class PacketLOGIN_REQUEST {
	@Expose public PacketHeader header;
	@Expose public PACKETLoginRequest payload;
	
	public class PACKETLoginRequest {
		@Expose public String Username;
		@Expose public String Password;
		@Expose public String Email = "";
	}
}
