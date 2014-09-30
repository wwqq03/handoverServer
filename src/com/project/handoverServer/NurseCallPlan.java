package com.project.handoverServer;

import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import no.ntnu.item.nursecall.common.callplan.model.CallPlan;
import no.ntnu.item.nursecall.common.callplan.model.Room;

import org.dom4j.io.*;
import org.dom4j.*;

public class NurseCallPlan {
	
	private String CALLPLANFILE;
	
	public NurseCallPlan() {
		CALLPLANFILE = Server.CALLPLANFILE;
	};
	
	public synchronized CallPlan retrieveCallPlan()
	{
		if(CALLPLANFILE == null || CALLPLANFILE.isEmpty())
			return null;
		
		try
		{
			String content = readFile(CALLPLANFILE, Charset.defaultCharset());
			
			CallPlan cp = CallPlan.parseXML(content);
			
			return cp;
		}
		catch(Exception e) {e.printStackTrace(); return null;}
	}
	
	static String readFile(String path, Charset encoding) 
			  throws IOException 
	{
			  byte[] encoded = Files.readAllBytes(Paths.get(path));
			  return encoding.decode(ByteBuffer.wrap(encoded)).toString();
	}
	

	public synchronized boolean editCallPlan(String rooms, String nurse)
	{
		StringTokenizer st = new StringTokenizer(rooms, ";");
		CallPlan plan = retrieveCallPlan();
		boolean success = false;
		boolean update = false;
		while(st.hasMoreElements())
		{
			String room = st.nextToken();
			for (Room r : plan.rooms)
			{
				if (room.contains(r.getRoomNumber()))
				{
					if (r.locked == true)
					{
						success = true;
						break;
					}
					
					if (r.getNurseAt(0) != null)
					{
						if (r.getNurseAt(0).uri.contains(nurse))
						{
							success = true;
							break;
						}
						
						String primaryNurseUri = r.getNurseAt(0).uri;
						String[] temp = primaryNurseUri.split("@");
						if (temp.length < 2)
						{
							success = false;
							break;
						}
						else
						{
							r.getNurseAt(0).uri = nurse + "@" + temp[1];
							if (r.getNurseAt(1) != null)
							{
								r.getNurseAt(1).uri = primaryNurseUri;
							}
							success = true;
							update = true;
						}
						
					}
				}
			}
			if (success == false)
				break;
		}
		
		if (update == true)
		{
			storeCallPlan(plan);
		}
		
		return success;
	}
	
	/*
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
				Attribute lockedAttribute = roomElement.attribute("locked");
				if(uriAttribute == null)
					continue;
				
				String uriString = uriAttribute.getText();
				if(!room.equals(getNameFromUri(uriString)))
					continue;
				
				if (lockedAttribute.getText().equals("true"))
					return true;
					
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
	*/
	
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

	public void storeCallPlan(CallPlan cp) {
		File file = new File(CALLPLANFILE);
		FileWriter fooWriter;
		try {
			fooWriter = new FileWriter(file, false);
			fooWriter.write(cp.createXML(true));
			fooWriter.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // true to append
		                                                     // false to overwrite.
		

	}

}
