package sync.pokemonnxt.com;

import com.google.gson.annotations.Expose;

public class PacketPLAYER_MOVE {
	@Expose public PacketHeader header;
	@Expose public PACKETMove payload;
	
public class PACKETMove {
	@Expose public Location LOC;
}

}
