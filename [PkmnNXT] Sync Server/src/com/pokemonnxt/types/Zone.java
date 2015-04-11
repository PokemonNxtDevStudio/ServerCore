package com.pokemonnxt.types;

import java.awt.Point;
import java.awt.Polygon;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.io.File;



import java.util.HashMap;
import java.util.Map;

import com.pokemonnxt.gameserver.Functions;
import com.pokemonnxt.gameserver.GlobalExceptionHandler;
import com.pokemonnxt.gameserver.Logger;
import com.pokemonnxt.types.pokemon.Pokemon.TYPE;
//GIT UPDATE
public class Zone {
	public static enum VISIBILITY {
		PRIVATE(1), FRIENDS(2),LOCAL(3),SERVER_LOCAL(4);
        private  int value;
        
        private static Map<Integer, VISIBILITY> map = new HashMap<Integer, VISIBILITY>();

        static {
            for (VISIBILITY legEnum : VISIBILITY.values()) {
                map.put(legEnum.value, legEnum);
            }
        }

        

        public static VISIBILITY valueOf(int legNo) {
            return map.get(legNo);
        }
        private VISIBILITY(int value) {
                this.value = value;
        }
	};   	

	public static enum TERRAIN {
		VOID(0), GRASS(1), GRAVEL(3), PLANE(4), SEA(5), LAKE(6), SKY(7), ROCK(8), ARENA(9), INSIDE(10), BALCONY(10);
        private  int value;
        
        private static Map<Integer, TERRAIN> map = new HashMap<Integer, TERRAIN>();

        static {
            for (TERRAIN legEnum : TERRAIN.values()) {
                map.put(legEnum.value, legEnum);
            }
        }

        

        public static TERRAIN valueOf(int legNo) {
            return map.get(legNo);
        }
        private TERRAIN(int value) {
                this.value = value;
        }
	}
	
	public Polygon Area;
	public int Lbound;
	public int Tbound;
	
	public VISIBILITY visibility;
	public String Name;
	public int ID;
	
	public String publicName;
	public int routeNumber;
	public int musicID;
	public String description;
	
	public boolean battlingPermitted;
	public int MaxPokemonSize;
	
	public boolean flySkillsPermitted;
	public boolean digSkillsPermitted;
	public boolean seaSkillsPermitted;
	public boolean earthSkillsPermitted;
	
	public boolean valid;
	
	public Zone(String file) {
		// TODO This is where we load the area data from the file
		try {
		File fXmlFile = new File(file);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
	 
		doc.getDocumentElement().normalize();
		if(!doc.getDocumentElement().getNodeName().equals("zone")){
			Logger.log_server(Logger.LOG_WARN, "Invalid Zone File at " + file  + " : "  + doc.getDocumentElement().getNodeName());
			return;
		}
		Logger.log_server(Logger.LOG_PROGRESS, "Loaded zone file " + file );
		Element infoNode = (Element) doc.getElementsByTagName("info").item(0);
		Element settingsNode = (Element) doc.getElementsByTagName("settings").item(0);
		Element securityNode = (Element) doc.getElementsByTagName("security").item(0);
		Element boundryNode = (Element) doc.getElementsByTagName("boundry").item(0);
		
		ID = Integer.parseInt(doc.getDocumentElement().getAttribute("id"));
		
				
		/*  Reading the basic <info> tag  */
		publicName = infoNode.getElementsByTagName("name").item(0).getTextContent();
		routeNumber = Integer.parseInt(infoNode.getElementsByTagName("route").item(0).getTextContent());
		description = infoNode.getElementsByTagName("description").item(0).getTextContent();
		
		
		/*  Reading the basic <settings> tag  */
		musicID = Integer.parseInt(settingsNode.getElementsByTagName("music").item(0).getTextContent());
		visibility = VISIBILITY.valueOf(settingsNode.getElementsByTagName("visibility").item(0).getTextContent());
		Element PokemonSettingsNode = (Element) settingsNode.getElementsByTagName("pokemon").item(0);
		battlingPermitted = Functions.toBoolean(PokemonSettingsNode.getAttribute("permitted"));
		MaxPokemonSize = Integer.parseInt(PokemonSettingsNode.getElementsByTagName("maxsize").item(0).getTextContent());
		
		Element PokemonSkillsSettingsNode = (Element) PokemonSettingsNode.getElementsByTagName("skills").item(0);
		flySkillsPermitted = Functions.toBoolean(((Element) PokemonSkillsSettingsNode.getElementsByTagName("fly").item(0)).getAttribute("permitted"));
		digSkillsPermitted = Functions.toBoolean(((Element) PokemonSkillsSettingsNode.getElementsByTagName("dig").item(0)).getAttribute("permitted"));
		seaSkillsPermitted = Functions.toBoolean(((Element) PokemonSkillsSettingsNode.getElementsByTagName("sea").item(0)).getAttribute("permitted"));
		earthSkillsPermitted = Functions.toBoolean(((Element) PokemonSkillsSettingsNode.getElementsByTagName("earth").item(0)).getAttribute("permitted"));
		
		
		
		} catch (Exception e) {
			GlobalExceptionHandler GEH = new GlobalExceptionHandler();
			GEH.uncaughtException(Thread.currentThread(), (Throwable) e);
			e.printStackTrace();
		}
	}
	
	
	public boolean inZone(Location L){
		if (L.Z > Tbound) return false;
		if (L.Z < Lbound) return false;
		Point P = new Point();
		P.x = (int) L.X;
		P.y = (int) L.Y;
		return Area.contains(P);
	}
	public boolean inZone(Point P){
		return Area.contains(P);
	}
	public boolean inZone(Location L, Point P){
		if (L.Z > Tbound) return false;
		if (L.Z < Lbound) return false;
		return Area.contains(P);
	}
}
