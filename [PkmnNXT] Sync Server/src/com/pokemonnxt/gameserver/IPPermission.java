package com.pokemonnxt.gameserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.annotations.Expose;

public class IPPermission{
	int TOPRULE_LEVEL = 4;
	
	@Expose public String Mask = "UNDEFINED";
	@Expose public String Name = "UNDEFINED";
	@Expose public String Authority = "UNDEFINED";
	@Expose public String Message = "NO MESSAGE SET";
	@Expose public int Permission = 1;
	/*
	 * Permission
	 * -1 DO NOT ACCEPT CONNECTION
	 *  0 Approve but reject afterwards
	 *  1 Approve and allow login
	 */
	public IPPermission(){
		
	}
	public void save(){
		File inputFile = new File("/etc/NXT_SERVER/CONF/IPs.csv");
		File tempFile = new File("/etc/NXT_SERVER/CONF/IPs.csv.tmp");
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(inputFile));
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

		String currentLine;
		 if(Mask == "") Mask = " ";
		    if(Name == "") Name = " ";
		    if(Authority == "") Authority = " ";
		    if(Message == "") Message = " ";
		    if(Mask.length() >= TOPRULE_LEVEL) writer.write(Mask + "," + Permission + "," + Name + "," + Authority + "," + Message + System.lineSeparator());
		  
		while((currentLine = reader.readLine()) != null) {
		    // trim newline when comparing with lineToRemove
		    String trimmedLine = currentLine.trim();
		    if(trimmedLine.startsWith(Mask + ",")) continue;
		    writer.write(currentLine + System.lineSeparator());
		}
		  if(Mask.length() < TOPRULE_LEVEL) writer.write(Mask + "," + Permission + "," + Name + "," + Authority + "," + Message + System.lineSeparator());
		  
		 tempFile.renameTo(inputFile);
		 writer.close();
		} catch (IOException e) {
			GlobalExceptionHandler GEH = new GlobalExceptionHandler();
			GEH.uncaughtException(Thread.currentThread(), (Throwable) e);
			e.printStackTrace();
		}
	}
	public void delete(){
		if(Mask.length() < TOPRULE_LEVEL){
			Permission = 1;
			save();
			return;
		}
		File inputFile = new File("/etc/NXT_SERVER/CONF/IPs.csv");
		File tempFile = new File("/etc/NXT_SERVER/CONF/IPs.csv.tmp");
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(inputFile));
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

		String currentLine;

		while((currentLine = reader.readLine()) != null) {
		    // trim newline when comparing with lineToRemove
		    String trimmedLine = currentLine.trim();
		    if(trimmedLine.startsWith(Mask + ",")) continue;
		    writer.write(currentLine + System.lineSeparator());
		}

		 tempFile.renameTo(inputFile);
		 writer.close();
		} catch (IOException e) {
			GlobalExceptionHandler GEH = new GlobalExceptionHandler();
			GEH.uncaughtException(Thread.currentThread(), (Throwable) e);
			e.printStackTrace();
		}
	}
}
