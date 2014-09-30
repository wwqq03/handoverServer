package com.project.handoverServer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import no.ntnu.item.nursecall.common.callplan.model.CallPlan;
import no.ntnu.item.nursecall.common.callplan.model.NurseList;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class Authenticator {
	
	private String USERSFILE;
	public static final String FAILED = "F";
	public static final String SUCCESS_CHIEF = "C";
	public static final String SUCCESS_NURSE = "N";
	
	public Authenticator () {
		USERSFILE = Server.USERSFILE;
	};
	public synchronized NurseList retrieveUserList()
	{
		if(USERSFILE == null || USERSFILE.isEmpty())
			return null;
		
		try
		{
			String content = readFile(USERSFILE, Charset.defaultCharset());
			
			NurseList n = NurseList.parseXML(content);
			
			return n;
		}
		catch(Exception e) {e.printStackTrace(); return null;}
	}
	
	static String readFile(String path, Charset encoding) 
			  throws IOException 
	{
			  byte[] encoded = Files.readAllBytes(Paths.get(path));
			  return encoding.decode(ByteBuffer.wrap(encoded)).toString();
	}
	
	public String authenticate(String name, String password){
		if(name == null || password == null)
			return FAILED;
		if(USERSFILE == null || USERSFILE.isEmpty()){
			System.out.println("Error:USERFILE is empty");
			return FAILED;
		}
		
		try{
			SAXReader reader = new SAXReader();
			Document  document = reader.read(new File(USERSFILE));
			
			Element users = document.getRootElement();
			List<Element> userList = users.elements("user");
			
			for(Iterator<Element> it = userList.iterator(); it.hasNext();){
				Element userElement = it.next();
				if(userElement == null)
					continue;
				
				Element nameElement = userElement.element("name");
				if(nameElement == null || !nameElement.getText().equals(name))
					continue;
				
				Element passwordElement = userElement.element("password");
				if(passwordElement == null || !passwordElement.getText().equals(password)){
					return FAILED;
				}
				else{
					Element roleElement = userElement.element("role");
					if(roleElement != null && roleElement.getText().equals(SUCCESS_CHIEF)){
						return SUCCESS_CHIEF;
					}
					else{
						return SUCCESS_NURSE;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return FAILED;
	}

}
