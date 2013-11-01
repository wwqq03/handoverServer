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
			
			Response response = processMessage(message);
			if(response == null){
				response = new Response(Response.handoverResponse);
				response.setStatus("400");
				response.setMessage("Bad request!");
			}
			
			if(!response.isLegal()){
				response.setStatus("500");
				response.setMessage("Internal Server Error");
			}
			
			PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
			writer.println(response.toXML());
			writer.close();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private Response processMessage(String plainRequest){
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
		
		return null;
	}
	
	private Response processHandover(Request request){
		Response response = new Response(request.getRequestType());
		NurseCallPlan ncp = new NurseCallPlan();
		if(ncp.editCallPlan(request.getRoom(), request.getNurse())){
			response.setStatus("200");
		}
		else{
			response.setStatus("404");
			response.setMessage("Room does not exist");
		}
		return response;
	}
	
	private Response processLogin(Request request){
		Response response = new Response(request.getRequestType());
		Authenticator authenticator = new Authenticator();
		String auth = authenticator.authenticate(request.getName(), request.getPassword());
		if(!auth.equals(Authenticator.FAILED)){
			response.setStatus("200");
			response.setRole(auth);
		}
		else{
			response.setStatus("403");
			response.setMessage("Failed logging in");
		}
		return response;
	}

}
