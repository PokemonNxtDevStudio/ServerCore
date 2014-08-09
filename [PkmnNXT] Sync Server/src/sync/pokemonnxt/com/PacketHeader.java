package sync.pokemonnxt.com;

import com.google.gson.annotations.Expose;

public class PacketHeader {
	public PacketHeader(String TYPE){
		PTYPE = TYPE;
	}
	@Expose public String PTYPE;
}
