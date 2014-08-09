package sync.pokemonnxt.com;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class PlayerLog {
	public enum LOGTYPE {
		CONNECTION_REJECTED(-1), CONNECTED(0), LOGIN(1), LOGOUT(2), SIGNUP(3), CATCH(4), BAN(5), UNBAN(6), KICK(7);
        private int value;

        private LOGTYPE(int value) {
                this.value = value;
        }
};   

	public static void LogAction(LOGTYPE type, int GTID){
		try {
			PreparedStatement logInsert = null;
			logInsert = Main.SQL.SQL.prepareStatement("INSERT INTO `LOG_PLAYER` (`TIME`,`TYPE`,`GTID`) VALUES (?,?,?)",Statement.RETURN_GENERATED_KEYS);
			logInsert.setInt(1,(int) (System.currentTimeMillis() / 1000));
			logInsert.setInt(2, type.value);
			logInsert.setInt(3, GTID);
			logInsert.executeUpdate();
			} catch (SQLException e) {
				GlobalExceptionHandler GEH = new GlobalExceptionHandler();
				GEH.uncaughtException(Thread.currentThread(), (Throwable) e);
				e.printStackTrace();
			}
	}
	public static void LogAction(LOGTYPE type, int GTID, String ActioningIP){
		try {
			PreparedStatement logInsert = null;
			logInsert = Main.SQL.SQL.prepareStatement("INSERT INTO `LOG_PLAYER` (`TIME`,`TYPE`,`GTID`,`IP`,`GEO`) VALUES (?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
			logInsert.setInt(1,(int) (System.currentTimeMillis() / 1000));
			logInsert.setInt(2, type.value);
			logInsert.setInt(3, GTID);
			logInsert.setString(4, ActioningIP);
			logInsert.setString(5, Functions.getLocation(ActioningIP));
			logInsert.executeUpdate();
			} catch (SQLException e) {
				GlobalExceptionHandler GEH = new GlobalExceptionHandler();
				GEH.uncaughtException(Thread.currentThread(), (Throwable) e);
				e.printStackTrace();
			}
	}
	
	public static void LogAction(LOGTYPE type, int GTID, String ActioningIP, String ExtraData){
		try {
		PreparedStatement logInsert = null;
		logInsert = Main.SQL.SQL.prepareStatement("INSERT INTO `LOG_PLAYER` (`TIME`,`TYPE`,`GTID`,`EXTRA`,`IP`,`GEO`) VALUES (?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
		logInsert.setInt(1,(int) (System.currentTimeMillis() / 1000));
		logInsert.setInt(2, type.value);
		logInsert.setInt(3, GTID);
		logInsert.setString(4, ExtraData);
		logInsert.setString(5, ActioningIP);
		logInsert.setString(6, Functions.getLocation(ActioningIP));
		logInsert.executeUpdate();
		} catch (SQLException e) {
			GlobalExceptionHandler GEH = new GlobalExceptionHandler();
			GEH.uncaughtException(Thread.currentThread(), (Throwable) e);
			e.printStackTrace();
		}
	}
	
}
