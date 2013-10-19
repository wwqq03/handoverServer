package com.project.handoverServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.xml.transform.Templates;

public class RequestHandler implements Runnable{
	
	private Socket clientSocket;
	
	public RequestHandler(Socket clientSocket) {
		// TODO Auto-generated constructor stub
		this.clientSocket = clientSocket;
	}
	
	public void run() {
		try{
			InputStreamReader streamReader = new InputStreamReader(clientSocket.getInputStream());
			BufferedReader reader = new BufferedReader(streamReader);			
			String message = reader.readLine();
			
			String response = processMessage(message);
			if(response == null){
				response = "Bad request!";
			}
			
			PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
			writer.println(response);
			writer.close();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private String processMessage(String plainRequest){
		if(plainRequest == null || plainRequest.isEmpty())
			return null;
		Request request = new Request(plainRequest);
		if(request == null || !request.isLegal())
			return null;
		
		System.out.println(request.toString());
		if(request.isHandoverRequest()){
			return processHandover(request);
		}
		else if(request.isLoginRequest()){
			return processLogin(request);
		}
		
		return "Illegal request!";
	}
	
	private String processHandover(Request request){
		if(NurseCallPlan.editCallPlan(request.getRoom(), request.getNurse())){
			return "Success for " + request.getRoom();
		}
		else{
			return "Failed for " + request.getRoom();
		}
	}
	
	private String processLogin(Request request){
		if(Authenticator.authenticate(request.getName(), request.getPassword())){
			return "200";
		}
		else{
			return "403";
		}
	}

}
