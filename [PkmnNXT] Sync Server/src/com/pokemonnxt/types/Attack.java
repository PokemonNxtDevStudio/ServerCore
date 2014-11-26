package com.pokemonnxt.types;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.google.gson.annotations.Expose;
import com.pokemonnxt.gameserver.GlobalExceptionHandler;
import com.pokemonnxt.gameserver.Main;

public class Attack {

@Expose public int GAID;
@Expose public int MID;
@Expose public int Power;
@Expose public int Defense;
@Expose public int PP;

	Attack(int ID){
		ResultSet BasicInfo = Main.SQL.Query("SELECT * FROM `TAUGHT_ATTACKS` WHERE `GAID`='" + ID  + "'");
		
		try {
			BasicInfo.next();
			GAID = BasicInfo.getInt("GAID");
			MID = BasicInfo.getInt("MID");
			Power = BasicInfo.getInt("Power");
			Defense = BasicInfo.getInt("Defense");
			PP = BasicInfo.getInt("PP");
		} catch (SQLException e) {
			GlobalExceptionHandler GEH = new GlobalExceptionHandler();
			GEH.uncaughtException(Thread.currentThread(), (Throwable) e);
			e.printStackTrace();
		}
		
	}
}
