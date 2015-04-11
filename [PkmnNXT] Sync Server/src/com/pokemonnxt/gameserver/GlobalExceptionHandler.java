package com.pokemonnxt.gameserver;

//GIT UPDATE
public class GlobalExceptionHandler implements Thread.UncaughtExceptionHandler{
	 
	public GlobalExceptionHandler(){
		
	}

	  public void uncaughtException(Thread t, Throwable e) {
	
		  Logger.log_exception("Context not specified", e);
			    Main.SQL.ReconnectDatabases();

	   return;
	    
	  }
	  
	  public void uncaughtException(Thread t, Throwable e, String Action) {
			
		
				
				    Logger.log_exception(Action, e);
				    Main.SQL.ReconnectDatabases();
			
		   return;
		    
		  }
	}

	
	


