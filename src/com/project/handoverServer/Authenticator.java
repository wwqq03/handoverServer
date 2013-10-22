package com.project.handoverServer;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class Authenticator {
	
	private static final String USERSFILE = "users.xml";
	public static final String FAILED = "F";
	public static final String SUCCESS_CHIEF = "C";
	public static final String SUCCESS_NURSE = "N";
	
	private Authenticator () {};
	
	public static String authenticate(String name, String password){
		if(name == null || password == null)
			return FAILED;
		
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
