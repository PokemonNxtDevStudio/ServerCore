package com.pokemonnxt.gameserver;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;

import com.google.gson.annotations.Expose;
import com.pokemonnxt.node.OuterNode;
//GIT UPDATE
public class LoadMonitor extends Thread{
	Boolean shutdown = false;
	public static HashMap<Long,ThreadUsage> UsageMatrix = new HashMap<Long,ThreadUsage>();
	public void run(){
		 Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		while(shutdown == false){
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
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
