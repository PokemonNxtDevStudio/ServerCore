package sync.pokemonnxt.com;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Formatter;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;

public class Functions {
	static DatabaseReader reader;
	
	  
	public static void initGeoIP(){
		// A File object pointing to your GeoIP2 or GeoLite2 database
		File database = new File("/etc/NXT_SERVER/LIBDATA/GeoIP2-City.mmdb");

		// This creates the DatabaseReader object, which should be reused across
		// lookups.
		try {
			 reader = new DatabaseReader.Builder(database).build();
		} catch (IOException e) {
			GlobalExceptionHandler GEH = new GlobalExceptionHandler();
			GEH.uncaughtException(Thread.currentThread(), (Throwable) e);
		}
	}
	public static String getLocation(String IP){
		Logger.log_server(Logger.LOG_VERB_LOW,"CHECKING IP: " +  IP);
		if (reader==null){
			initGeoIP();
		}
		try {
			CityResponse response = reader.city(InetAddress.getByName(IP));
		String ans = response.getPostal().getCode();
		String CO = response.getCountry().getIsoCode();
		String ST = response.getMostSpecificSubdivision().getName();
		String CI = response.getCity().getName();
		String PO = response.getPostal().getCode();
		if(CO == "" || CO == null) CO = " ";
		if(ST == "" || ST == null) ST = " ";
		if(CI == "" || CI == null) CI = " ";
		if(PO == "" || PO == null) PO = " ";
		
		ResultSet RS = Main.SQL.Query("SELECT * FROM `GEO_LOCATIONS` WHERE `COUNTRY`='" + CO + "' AND `STATE`='" + ST + "' AND `CITY`='" + CI + "' AND `POST`='" + PO + "'");
		if (hasResults(RS)){
			try {
				ans = Integer.toString(RS.getInt("LID"));
			} catch (SQLException e) {
				GlobalExceptionHandler GEH = new GlobalExceptionHandler();
				GEH.uncaughtException(Thread.currentThread(), (Throwable) e);
				e.printStackTrace();
			}
		}else{
			try {
			PreparedStatement createPlayer = Main.SQL.SQL.prepareStatement("INSERT INTO `GEO_LOCATIONS` (`COUNTRY`,`STATE`, `CITY`,`POST`) VALUES (?, ?, ?,?)",Statement.RETURN_GENERATED_KEYS);
			createPlayer.setString(1, CO);
			createPlayer.setString(2, ST);
			createPlayer.setString(3, CI);
			createPlayer.setString(4, PO);
			createPlayer.executeUpdate();
			ResultSet insertResult = createPlayer.getGeneratedKeys();
			insertResult.first();
			ans = Integer.toString(insertResult.getInt(1));
			} catch (SQLException e) {
				GlobalExceptionHandler GEH = new GlobalExceptionHandler();
				GEH.uncaughtException(Thread.currentThread(), (Throwable) e);
				e.printStackTrace();
			}
		}
		return ans;
		} catch (IOException | GeoIp2Exception e) {
			return e.getMessage();
		}
	}
	public static boolean isInteger(String s) {
	    return isInteger(s,10);
	}

	public static boolean isInteger(String s, int radix) {
	    if(s.isEmpty()) return false;
	    for(int i = 0; i < s.length(); i++) {
	        if(i == 0 && s.charAt(i) == '-') {
	            if(s.length() == 1) return false;
	            else continue;
	        }
	        if(Character.digit(s.charAt(i),radix) < 0) return false;
	    }
	    return true;
	}
	
	public static String padString(String toPad, String padder, int Length){
		while(toPad.length() < Length){
			toPad = toPad + padder;
		}
		return toPad;
	}
	public static String encryptPassword(String password)
	{
	    String sha1 = "";
	    try
	    {
	        MessageDigest crypt = MessageDigest.getInstance("SHA-1");
	        crypt.reset();
	        crypt.update(password.getBytes("UTF-8"));
	        sha1 = byteToHex(crypt.digest());
	    }
	    catch(NoSuchAlgorithmException e)
	    {
	    	GlobalExceptionHandler GEH = new GlobalExceptionHandler();
			GEH.uncaughtException(Thread.currentThread(), (Throwable) e);
	        e.printStackTrace();
	    }
	    catch(UnsupportedEncodingException e)
	    {
	    	GlobalExceptionHandler GEH = new GlobalExceptionHandler();
			GEH.uncaughtException(Thread.currentThread(), (Throwable) e);
	        e.printStackTrace();
	    }
	    return sha1;
	}
public static boolean hasResults(ResultSet RS){
	try {
		if (!RS.next()){
			return false;
		}
	} catch (SQLException e) {
		GlobalExceptionHandler GEH = new GlobalExceptionHandler();
		GEH.uncaughtException(Thread.currentThread(), (Throwable) e);
		System.out.println("[" + ConsoleColors.ANSI_RED + "  WARN  " + ConsoleColors.ANSI_RESET + "]	Error in hasresults function:");
		e.printStackTrace();
	}
	return true;
}
	private static String byteToHex(final byte[] hash)
	{
	    Formatter formatter = new Formatter();
	    for (byte b : hash)
	    {
	        formatter.format("%02x", b);
	    }
	    String result = formatter.toString();
	    formatter.close();
	    return result;
	}
}
