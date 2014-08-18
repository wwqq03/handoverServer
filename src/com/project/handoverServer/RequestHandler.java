package com.project.handoverServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.xml.transform.Templates;

import no.ntnu.item.nursecall.callplan.common.communication.Request;
import no.ntnu.item.nursecall.callplan.common.communication.Response;
import no.ntnu.item.nursecall.common.model.CallPlan;
import no.ntnu.item.nursecall.common.model.NurseList;

public class RequestHandler implements Runnable{
	
	private Socket clientSocket;
	private NurseCallPlan ncp = new NurseCallPlan();
	private Authenticator authenticator = new Authenticator();

	public RequestHandler(Socket clientSocket) {
		// TODO Auto-generated constructor stub
		this.clientSocket = clientSocket;
	}
	
	public void run() {
		boolean isGuiClient = true; 
		while (isGuiClient)
		{
			isGuiClient = false;
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
				
				if (response.isCallPlanReponse() || response.isCallPlanUpdateReponse() || response.isNurseListReponse())
					isGuiClient = true;
				
				PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
				writer.println(response.toXML());
				
				if (!isGuiClient)
					writer.close();
				else
					writer.flush();
			} catch(Exception e){
				e.printStackTrace();
				isGuiClient = false;
			}
		}
	}
	
	private Response processMessage(String plainRequest){
		if(plainRequest == null || plainRequest.isEmpty())
			return null;
		Request request = Request.createRequest(plainRequest);
		if(request == null || !request.isLegal())
			return null;
		
		System.out.println(request.toString());
		if(request.isHandoverRequest()){
			return processHandover(request);
		}
		else if(request.isLoginRequest()){
			return processLogin(request);
		}
		else if(request.isCallPlanRequest())
		{
			return processCallPlanRequest(request);
		}
		else if (request.isCallPlanUpdateRequest())
		{
			return processCallPlanUpdateRequest(request);
		}
		else if (request.isNurseListRequest())
		{
			return processNurseListRequest(request);
		}
		
		return null;
	}
	
	private Response processHandover(Request request){
		Response response = new Response(request.getRequestType());
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
	
	private Response processCallPlanUpdateRequest(Request request)
	{
		Response response = new Response(request.getRequestType());
		CallPlan cp = CallPlan.parseXML(request.getCallPlan());
		
		if (cp != null)
		{
			ncp.storeCallPlan(cp);
			response.setStatus("200");
		}
		else
			response.setStatus("500");
		return response;
	}
	
	private Response processCallPlanRequest(Request request)
	{
		Response response = new Response(request.getRequestType());
		CallPlan cp = ncp.retrieveCallPlan();
		if (cp != null)
		{
			response.setCallPlan(cp.createXML(false));
			response.setStatus("200");
		}
		else
			response.setStatus("500");
		
		return response;
	}
	
	private Response processNurseListRequest(Request request)
	{
		Response response = new Response(request.getRequestType());
		NurseList list = authenticator.retrieveUserList();
		if (list != null)
		{
			response.setNurseList(list.createXML(false));
			response.setStatus("200");
		}
		else
			response.setStatus("500");
		
		return response;
	}
	
}
