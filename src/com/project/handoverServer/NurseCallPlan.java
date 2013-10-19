package com.project.handoverServer;

import java.util.Iterator;
import java.util.List;
import java.io.File;
import java.io.FileWriter;

import org.dom4j.io.*;
import org.dom4j.*;

public class NurseCallPlan {
	
	private static final String CALLPLANFILE = "callplan.xml";
	
	private NurseCallPlan() {};
	
	public static synchronized boolean editCallPlan(String room, String nurse){
		if(room == null || nurse == null)
			return false;
		try{
			SAXReader reader = new SAXReader();
			Document  document = reader.read(new File(CALLPLANFILE));
			
			Element callplan = document.getRootElement();
			List<Element> roomList = callplan.elements("room");
			
			for(Iterator<Element> itRoom = roomList.iterator(); itRoom.hasNext();){
				Element roomElement = itRoom.next();
				Attribute uriAttribute = roomElement.attribute("uri");
				if(uriAttribute == null)
					continue;
				
				String uriString = uriAttribute.getText();
				if(!room.equals(getNameFromUri(uriString)))
					continue;
				
				List<Element> nurseList = roomElement.elements("nurse");
				for(Iterator<Element> itNurse = nurseList.iterator(); itNurse.hasNext();){
					Element nurseElement = itNurse.next();
					Attribute rank = nurseElement.attribute("rank");
					
					if(rank == null)
						continue;
					if(!rank.getText().equals("1"))
						continue;
					
					String nurseUri = nurseElement.getText();
					String[] temp = nurseUri.split("@");
					if(temp.length < 2)
						return false;
					nurseElement.setText(nurse + "@" + temp[1]);
					
					XMLWriter writer = new XMLWriter(new FileWriter(CALLPLANFILE));
					writer.write(document);
					writer.close();
					
					return true;
				}
			}
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	private static String getNameFromUri(String uri) {
		if(uri == null)
			return null;
		if(!uri.contains("@"))
			return null;
		String[] names = uri.split("@");
		return names[0];
	}

}
