package com.project.handoverServer;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import org.dom4j.io.*;
import org.dom4j.*;

public class NurseCallPlan {
	
	private String CALLPLANFILE;
	
	public NurseCallPlan() {
		CALLPLANFILE = Server.CALLPLANFILE;
	};
	
	public synchronized boolean editCallPlan(String room, String nurse){
		if(room == null || nurse == null)
			return false;
		if(CALLPLANFILE == null || CALLPLANFILE.isEmpty())
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
					
					printCallPlan(callplan);
					return true;
				}
			}
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	private String getNameFromUri(String uri) {
		if(uri == null)
			return null;
		if(!uri.contains("@"))
			return null;
		String[] names = uri.split("@");
		return names[0];
	}
	
	private void printCallPlan(Element callplan){
		if(callplan == null)
			return;
		System.out.println("----------------------------------------------------------------------");
		System.out.println("Updated call plan:");
		List<Element> roomList = callplan.elements("room");
		
		for(Iterator<Element> itRoom = roomList.iterator(); itRoom.hasNext();){
			String roomInfo = null;
			Element roomElement = itRoom.next();
			Attribute uriAttribute = roomElement.attribute("uri");
			if(uriAttribute == null)
				continue;
			
			String uriString = uriAttribute.getText();
			String room = getNameFromUri(uriString);
			if(room == null || room.isEmpty())
				continue;
			
			roomInfo = "Room: " + room + "\t";
			
			List<Element> nurseList = roomElement.elements("nurse");
			for(Iterator<Element> itNurse = nurseList.iterator(); itNurse.hasNext();){
				Element nurseElement = itNurse.next();
				Attribute rankAttribute = nurseElement.attribute("rank");
				
				if(rankAttribute == null)
					continue;
				
				String rank = rankAttribute.getText();
				if(rank == null || rank.isEmpty())
					continue;
				
				roomInfo = roomInfo + "Nurse" + rank + ": ";
				String nurseUri = nurseElement.getText();
				String nurse = getNameFromUri(nurseUri);
				roomInfo = roomInfo + nurse + "\t";
			}
			
			if(roomInfo != null && !roomInfo.isEmpty()){
				System.out.println(roomInfo);
			}
		}
		System.out.println("----------------------------------------------------------------------");
	}

}
