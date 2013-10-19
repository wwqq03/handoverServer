package com.project.handoverServer;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class Authenticator {
	
	private static final String USERSFILE = "users.xml";
	
	private Authenticator () {};
	
	public static boolean authenticate(String name, String password){
		if(name == null || password == null)
			return false;
		
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
				if(passwordElement == null || !passwordElement.getText().equals(password))
					return false;
				else
					return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
