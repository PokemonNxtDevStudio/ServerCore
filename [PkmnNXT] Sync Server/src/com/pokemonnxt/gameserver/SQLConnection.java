package com.pokemonnxt.gameserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.Statement;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class SQLConnection {
	
	String url = "jdbc:mysql://localhost:3306/";
	String driver = "com.mysql.jdbc.Driver"; 
	static String gameDBName = "NXT_GAME";
	static String baseDBName = "NXT_BASE";
	String gameDBUserName = "NXT_GAME-SYNC";
	String gameDBPassword = "AqDErPDjjuUBzjcW";
	public Connection SQL;
	Connection BaseSQL;
	private Connection Temp;
	SQLRenewTask SQLRenew;
	Timer time = new Timer(); // Instantiate Timer Object
	
	public void ReconnectDatabases(){
		try {
		Class.forName(driver).newInstance(); 
	  	SQL.close();
	  	SQL =  DriverManager.getConnection(url+gameDBName,gameDBUserName,gameDBPassword); 
	  	BaseSQL.close();
	  	BaseSQL = DriverManager.getConnection(url+baseDBName,gameDBUserName,gameDBPassword);
	  	} catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
	  		GlobalExceptionHandler GEH = new GlobalExceptionHandler();
			GEH.uncaughtException(Thread.currentThread(), (Throwable) e);
			e.printStackTrace();
		} 
	}
	public void ConnectToSQLServer(){
		try { 
			System.out.println("[" + ConsoleColors.ANSI_GREEN + " WORKING " + ConsoleColors.ANSI_RESET + "]	Connecting SQL Server");
		  	Class.forName(driver).newInstance(); 
		  	SQL = DriverManager.getConnection(url+gameDBName,gameDBUserName,gameDBPassword); 
		  	BaseSQL = DriverManager.getConnection(url+baseDBName,gameDBUserName,gameDBPassword); 
		  	
		  	SQLRenew = new SQLRenewTask(); // Instantiate SheduledTask class
			time.schedule(SQLRenew, 50000, 600000); // Create Repetitively task for every 1 secs
		  	System.out.println("SQL SERVER CONNECTION						[" + ConsoleColors.ANSI_GREEN + "   OK   " + ConsoleColors.ANSI_RESET + "]");
	  	} catch (Exception e) { 
	  		GlobalExceptionHandler GEH = new GlobalExceptionHandler();
			GEH.uncaughtException(Thread.currentThread(), (Throwable) e);
	  		System.out.println("SQL SERVER CONNECTION						[" + ConsoleColors.ANSI_YELLOW + "  FAIL  " + ConsoleColors.ANSI_RESET + "]");
	  	} 
	         
}
	public int Update(String query){
		Statement st;
		int RS;
		try {
			st = SQL.createStatement();
			RS = st.executeUpdate(query);
		} catch (SQLException e) {
			GlobalExceptionHandler GEH = new GlobalExceptionHandler();
			GEH.uncaughtException(Thread.currentThread(), (Throwable) e,"Error whilst attempting to run the following query: " + query);
			RS = -1;
		}
		
return RS;
	}
	public ResultSet Query(String query){
		Statement st;
		ResultSet RS;
		try {
			st = SQL.createStatement();
			RS = st.executeQuery(query);
		} catch (SQLException e) {
			GlobalExceptionHandler GEH = new GlobalExceptionHandler();
			GEH.uncaughtException(Thread.currentThread(), (Throwable) e, "Error whilst attempting to run the following query: " + query);
			RS = null;
		}
		
return RS;
	}
	  public int getInt(String query) {
		  ResultSet RS;
		     
		    try {
		    	PreparedStatement pstmt = SQL.prepareStatement(query);
		      RS = pstmt.executeQuery();
		      if (RS.next()) {
		        return RS.getInt(1);
		      } else {
		        return -1;
		      }
		    } catch (Exception e) {
				GlobalExceptionHandler GEH = new GlobalExceptionHandler();
				GEH.uncaughtException(Thread.currentThread(), (Throwable) e,"Error whilst attempting to run the following query: " + query);
		      return -1;
		    }
		  }
	public ResultSet QueryBase(String query){
		Statement st;
		ResultSet RS;
		try {
			st = BaseSQL.createStatement();
			RS = st.executeQuery(query);
		} catch (SQLException e) {
			GlobalExceptionHandler GEH = new GlobalExceptionHandler();
			GEH.uncaughtException(Thread.currentThread(), (Throwable) e,"Error whilst attempting to run the following query: " + query);
			RS = null;
		}
		
return RS;
	}

public void DisconnectSQLServer(){
	System.out.println("Disconnecting from SQL server...");
	try {
		SQLRenew.cancel();
		System.out.println("[" + ConsoleColors.ANSI_GREEN + " WORKING " + ConsoleColors.ANSI_RESET + "]	Disconnecting SQL Server");
		SQL.close();
		BaseSQL.close();
		System.out.println("SQL SERVER DISCONNECTION								[" + ConsoleColors.ANSI_GREEN + "   OK   " + ConsoleColors.ANSI_RESET + "]");
	} catch (SQLException e) {
		System.out.println("SQL SERVER DISCONNECTION								[" + ConsoleColors.ANSI_YELLOW + "  FAIL  " + ConsoleColors.ANSI_RESET + "]	");
	} 
	
}

//Create a class extends with TimerTask
public class SQLRenewTask extends TimerTask {


	// Add your task here
	public void run() {
		System.out.println("Renewing server connection...");
		ReconnectDatabases();
		System.out.println("Server connection renewed.");
	}
}

}
