package com.project.handoverServer;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class Response {
	
	public static final String loginResponse  = "LOGIN";
	public static final String handoverResponse = "HANDOVER";
	
	private String responseType;
	private String status;
	private String message;
	private String role;
	
	public Response(String responseType){
		this.responseType = responseType;
	}

	public String getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}

	public String getRole() {
		return role;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setRole(String role) {
		this.role = role;
	}
	
	public boolean isLoginResponse(){
		return (responseType.equals(loginResponse));
	}
	
	public boolean isHandoverReponse(){
		return (responseType.equals(handoverResponse));
	}
	
	public boolean isLegal(){	
		if(status == null){
			return false;
		}
		
		if(status.equals("200")){
			return true;
		}
		else if(message != null){
			return true;
		}
		else{
			return false;
		}
	}
	
	public String toXML(){
		try{
			if(!isLegal())
				return null;
			Document document = DocumentHelper.createDocument();
            Element responseElement = document.addElement("response");
            responseElement.addAttribute("status", status);

            Element commandElement = responseElement.addElement("command");
            commandElement.setText(responseType);
            
            if(status.equals("200")){
            	if(isLoginResponse() && role != null){
            		Element roleElement = responseElement.addElement("role");
            		roleElement.setText(role);
            	}
            }
            else{
            	Element messageElement = responseElement.addElement("message");
            	messageElement.setText(message);
            }
            
            return responseElement.asXML();
		} catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

}
