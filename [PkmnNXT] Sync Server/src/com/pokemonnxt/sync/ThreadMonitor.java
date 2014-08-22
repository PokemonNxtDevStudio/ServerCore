package com.pokemonnxt.sync;

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
			for(Client c : MainServer.Clients){
				if(c == null) continue;
				if(System.currentTimeMillis() - c.lastRX > ServerVars.Timeout) {
					c.timeOut();
					if (c.player != null) c.player.signOut();
					c.forceClose();
					
				}
				
				long TID = c.getId();
				ThreadUsage TU = null;
				if(UsageMatrix.containsKey(TID)) TU = UsageMatrix.get(TID);
				if(TU == null) TU = new ThreadUsage();
				TU.CPU =  (tmxb.getThreadCpuTime(TID) / 1000000)/(System.currentTimeMillis() - c.startTime);
				TU.TID = TID;
				TU.STATE = tmxb.getThreadInfo(TID).getThreadState();
				TU.GTID = -1;
				if(c.player != null) TU.GTID = c.player.GTID;
				c.Performance = TU;
				if(!UsageMatrix.containsKey(TID))  UsageMatrix.put(TID,TU);
				
			}
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
