package com.project.handoverServer;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class Request {
	
	public static final String loginRequest  = "LOGIN";
	public static final String handoverRequest = "HANDOVER";
	
	private String requestType;
	private String nurse;
	private String room;
	private String name;
	private String password;
	
	public Request(String plainRequest){
		if(plainRequest == null)
			return;
		try{
			Document document = DocumentHelper.parseText(plainRequest);
			Element request = document.getRootElement();
			if(request == null)
				return;
			
			Element command = request.element("command");
			if(command == null)
				return;
			requestType = command.getText();
			
			if(requestType.equals(loginRequest)){
				name = request.elementText("name");
				password = request.elementText("password");
			}
			
			else if(requestType.equals(handoverRequest)){
				nurse = request.elementText("nurse");
				room = request.elementText("room");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getRequestType() {
		return requestType;
	}

	public String getNurse() {
		return nurse;
	}

	public String getRoom() {
		return room;
	}

	public String getName() {
		return name;
	}

	public String getPassword() {
		return password;
	}
	
	public boolean isLoginRequest(){
		if(requestType == null)
			return false;
		return requestType.equals(loginRequest);
	}
	
	public boolean isHandoverRequest(){
		if(requestType == null)
			return false;
		return requestType.equals(handoverRequest);
	}
	
	public boolean isLegal(){
		if(requestType == null)
			return false;
		if(requestType.equals(handoverRequest)){
			if(nurse != null && room != null)
				return true;
		}
		else if(requestType.equals(loginRequest)){
			if(name != null && password != null){
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		if(requestType == null)
			return null;
		
		String s = ", ";
		if(requestType.equals(handoverRequest)){
			s = s + "nurse=" + nurse + ", room=" + room;
		}
		else if(requestType.equals(loginRequest)){
			s = s + "name=" + name + ", password=" + password;
		}
		else {
			return null;
		}
		return "Request [requestType=" + requestType + s + "]";
	}
	

}
