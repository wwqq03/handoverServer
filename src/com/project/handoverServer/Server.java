package com.project.handoverServer;

import java.io.*;
import java.net.*;

public class Server {
	public static void main(String[] args){
		Server server = new Server();
		server.go();
	}
	
	public void go() {
		try {
			ServerSocket serverSocket = new ServerSocket(5000);
			System.out.println("System running on port 5000");
			
			while(true) {
				Socket clientSocket = serverSocket.accept();
				
				InputStreamReader streamReader = new InputStreamReader(clientSocket.getInputStream());
				BufferedReader reader = new BufferedReader(streamReader);
				PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
				
				String message = reader.readLine();
				System.out.println("New request: " + message);
				
				String[] persons = message.split(", ");
				
				writer.println(persons[0] + " handed over!");
				writer.close();
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}
