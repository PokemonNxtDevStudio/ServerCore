package com.pokemonnxt.gameserver;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class Logger {
	
	public static final  int LOG_FATAL = 0;
	public static final  int LOG_ERROR = 1;
	public static final  int LOG_WARN = 2;
	public static final  int LOG_VERB_HIGH = 3;
	public static final  int LOG_VERB_LOW = 4;
	public static final  int LOG_PROGRESS = 5;
	public static void log_player(int severity, String Message, int GTID){
		Calendar cal = Calendar.getInstance();
    	cal.getTime();
    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd-MM-YY");
		switch(severity){
		case LOG_FATAL:
			distributeMessage( sdf.format(cal.getTime()) + " - [" + ConsoleColors.ANSI_YELLOW + " FATAL " + ConsoleColors.ANSI_RESET + "] GTID:" + GTID + " - " + Message,LOG_FATAL);
		break;
		case LOG_ERROR:
			distributeMessage( sdf.format(cal.getTime()) + " - [" + ConsoleColors.ANSI_RED + " ERROR " + ConsoleColors.ANSI_RESET + "]  GTID:" + GTID + " - " + Message,LOG_ERROR);
		break;
		case LOG_WARN:
			distributeMessage( sdf.format(cal.getTime()) + " - [" + ConsoleColors.ANSI_PURPLE + " ERROR " + ConsoleColors.ANSI_RESET + "]  GTID:" + GTID + " - " + Message,LOG_WARN);
		break;
		case LOG_VERB_HIGH:
			distributeMessage( sdf.format(cal.getTime()) + " - GTID:" + GTID + " - " + Message,LOG_VERB_HIGH);

		break;
		case LOG_VERB_LOW:
			distributeMessage( sdf.format(cal.getTime()) + " - " + Message,LOG_VERB_LOW);
			break;
		case LOG_PROGRESS:
			distributeMessage( sdf.format(cal.getTime()) + " " + Message,LOG_PROGRESS);

		break;
		}
	}
	public static void log_exception(String Message, Throwable e){
		e.printStackTrace();
		Calendar cal = Calendar.getInstance();
    	cal.getTime();
    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd-MM-YY");
    	try {
    		PrintWriter writer;
			writer = new PrintWriter("/etc/NXT_SERVER/ERRORS/ERROR" + System.currentTimeMillis() + ".txt", "UTF-8");
			writer.println("The following Error occured at " +  sdf.format(cal.getTime()));
			writer.println("---------------------------------------------------------------");
			writer.println("Context: ");
			writer.println(Message);
			writer.println("---------------------------------------------------------------");
			writer.println(e.getMessage());
			writer.println(e.getLocalizedMessage());
			writer.println("---------------------------------------------------------------");
			e.printStackTrace(writer);
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	//GIT UPDATE
    	StackTraceElement[] StackTrace =  e.getStackTrace();
    	distributeMessage(" -------------------------------------------------------------------------------------- ",LOG_ERROR);
		distributeMessage( sdf.format(cal.getTime()) + " - [" + ConsoleColors.ANSI_RED + " ERROR " + ConsoleColors.ANSI_RESET + "]  AN ERROR OCCURED" ,LOG_ERROR);
		 distributeMessage("Error Messages:",LOG_ERROR);
		 distributeMessage("   " + e.getMessage(),LOG_ERROR);
		 distributeMessage("   " + e.getLocalizedMessage(),LOG_ERROR);
		 distributeMessage("Stack Trace:",LOG_ERROR);
		 for(StackTraceElement Trace: StackTrace){
			 distributeMessage("   " + Trace.getClassName() + ":" + Trace.getLineNumber() + " (" + Trace.getMethodName() + ") ",LOG_ERROR);
		 }
		 distributeMessage("----",LOG_ERROR);
		distributeMessage( sdf.format(cal.getTime()) + " - [" + ConsoleColors.ANSI_RED + " ERROR " + ConsoleColors.ANSI_RESET + "]  AN ERROR OCCURED" ,LOG_ERROR);
		distributeMessage(" -------------------------------------------------------------------------------------- ",LOG_ERROR);
	}
	private static void distributeMessage(String Message, int Level){
		System.out.println(Message);
		
		for(Commander c : ControlServer.Clients){
			if (c==null) continue;
			if (Level <= c.PrintLevel){
				c.printVerb(Message);
			}
		}
	}
	public static void log_server(int severity, String Message){
		Calendar cal = Calendar.getInstance();
    	cal.getTime();
    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd-MM-YY");
		switch(severity){
		case LOG_FATAL:
			distributeMessage( sdf.format(cal.getTime()) + " - [" + ConsoleColors.ANSI_YELLOW + " FATAL " + ConsoleColors.ANSI_RESET + "] " + Message,LOG_FATAL);
		break;
		case LOG_ERROR:
			distributeMessage( sdf.format(cal.getTime()) + " - [" + ConsoleColors.ANSI_RED + " ERROR " + ConsoleColors.ANSI_RESET + "] " + Message,LOG_ERROR);
		break;
		case LOG_WARN:
			distributeMessage( sdf.format(cal.getTime()) + " - [" + ConsoleColors.ANSI_PURPLE + " WARN " + ConsoleColors.ANSI_RESET + "] " + Message,LOG_WARN);
		break;
		case LOG_VERB_HIGH:
			distributeMessage( sdf.format(cal.getTime()) + " - " + Message,LOG_VERB_HIGH);
		break;
		case LOG_VERB_LOW:
			distributeMessage( sdf.format(cal.getTime()) + " - " + Message,LOG_VERB_LOW);
			break;
		case LOG_PROGRESS:
			distributeMessage( sdf.format(cal.getTime()) + " " + Message,LOG_PROGRESS);

		break;
		}
	}
	public static void log_client(int severity, String IP, String Message){
		Calendar cal = Calendar.getInstance();
    	cal.getTime();
    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd-MM-YY");
		switch(severity){
		case LOG_FATAL:
			distributeMessage( sdf.format(cal.getTime()) + " " + IP + " - [" + ConsoleColors.ANSI_RED + " FATAL " + ConsoleColors.ANSI_RESET + "] " + Message,LOG_FATAL);
		break;
		case LOG_ERROR:
			distributeMessage( sdf.format(cal.getTime()) + " " + IP + " - [" + ConsoleColors.ANSI_RED + " ERROR " + ConsoleColors.ANSI_RESET + "] " + Message,LOG_ERROR);

		break;
		case LOG_WARN:
			distributeMessage( sdf.format(cal.getTime()) + " " + IP + " - [" + ConsoleColors.ANSI_PURPLE + " WARN " + ConsoleColors.ANSI_RESET + "] " + Message,LOG_WARN);

		break;
		case LOG_VERB_HIGH:
			distributeMessage( sdf.format(cal.getTime()) + " " + IP + " - " + Message,LOG_VERB_HIGH);

		break;
		case LOG_VERB_LOW:
			distributeMessage( sdf.format(cal.getTime()) + " " + IP + " - " + Message,LOG_VERB_LOW);

		break;
		case LOG_PROGRESS:
			distributeMessage( sdf.format(cal.getTime()) + " " + IP + " " + Message,LOG_PROGRESS);

		break;
		}
	}
}
