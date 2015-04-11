package com.pokemonnxt.types;

import java.util.HashMap;
import java.util.Map;

import com.pokemonnxt.gameserver.Client;
import com.pokemonnxt.gameserver.ServerAssets;
//GIT UPDATE
public abstract class Asset {
	public int AID = 0;
	public int owner = -1; // 0 = owned by server, anything else is THE APPROPRIATE CLIENT ID
	public VISIBILITY visibility = VISIBILITY.UNDEFINED;
	public Location location = new Location(-200F,-200F,-200F,(short)0,(short)0,(short)0);
	
	
	public static enum VISIBILITY {
		UNDEFINED(-1), SERVER_ONLY(0), NONPHYSICAL(1), CLIENT_ONLY(2), CLIENT_FRIENDS(3), LOCAL_AREA(4), SERVER(5), ALL(6);
        private  int value;
        
        private static Map<Integer, VISIBILITY> map = new HashMap<Integer, VISIBILITY>();

        static {
            for (VISIBILITY legEnum : VISIBILITY.values()) {
                map.put(legEnum.value, legEnum);
            }
        }

        

        public static VISIBILITY valueOf(int legNo) {
            return map.get(legNo);
        }
        private VISIBILITY(int value) {
                this.value = value;
        }
};  
	public Asset(){
		AID = ServerAssets.GenerateAssetID();
		ServerAssets.AddAsset(this);
	}
	
	public void TransferTo(Client C){
		ServerAssets.TransferAsset(this, C);
	}

}
