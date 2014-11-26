package com.pokemonnxt.gameserver;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;

import com.google.gson.annotations.Expose;

public class ThreadMonitor extends Thread{
	Boolean shutdown = false;
	public static HashMap<Long,ThreadUsage> UsageMatrix = new HashMap<Long,ThreadUsage>();
	public void run(){
		 Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		ThreadMXBean tmxb = ManagementFactory.getThreadMXBean();
		while(shutdown == false){
			try {
				sleep(5000);
				 HashMap<Long,ThreadUsage> TUsageMatrix = new HashMap<Long,ThreadUsage>();
				
			for(Client c : MainServer.Clients){
				if(c == null) continue;
				if(System.currentTimeMillis() - c.lastRX > ServerVars.Timeout) {
					Logger.log_client(Logger.LOG_WARN,c.IP,  "[MON] Timing out IP ");
					c.timeOut();
					if (c.player != null) c.player.signOut();
					c.forceClose();
					
				}
				
				long TID = c.getId();
				ThreadUsage TU = null;
				if(TUsageMatrix.containsKey(TID)) TU = TUsageMatrix.get(TID);
				if(TU == null) TU = new ThreadUsage();
				TU.CPU =  (tmxb.getThreadCpuTime(TID) / 1000000)/(System.currentTimeMillis() - c.startTime);
				TU.TID = TID;
				Logger.log_client(Logger.LOG_PROGRESS, c.IP, "State: " + c.State);
				if(tmxb.getThreadInfo(TID)== null || tmxb.getThreadInfo(TID).getThreadState()== null){
					Logger.log_client(Logger.LOG_ERROR,c.IP,  "[MON] thread is in nullstate! Closing");
					if (c.player != null) c.player.signOut();
					c.forceClose();
				}else{
				TU.STATE = tmxb.getThreadInfo(TID).getThreadState();
				}
				TU.GTID = -1;
				if(c.player != null) TU.GTID = c.player.GTID;
				c.Performance = TU;
				if(!TUsageMatrix.containsKey(TID))  TUsageMatrix.put(TID,TU);
				
			}
			UsageMatrix = TUsageMatrix;
			} catch (Exception e) {
				GlobalExceptionHandler GEH = new GlobalExceptionHandler();
				GEH.uncaughtException(Thread.currentThread(), (Throwable) e);
				e.printStackTrace();
			}
		}
		
	}
	public class ThreadUsage{
		@Expose double TID = -1;
		@Expose int GTID = -1;
		@Expose long CPU = -1;
		@Expose State STATE = State.NEW;
	}
}
