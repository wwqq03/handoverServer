package com.project.handoverServer;

import java.io.*;
import java.net.*;
import java.util.Properties;

import javax.crypto.spec.PSource;

public class Server {
	public static void main(String[] args){
		Server server = new Server();
		server.go();
	}
	
	public static String CALLPLANFILE;
	public static String USERSFILE;
	
	public void go() {
		  
		Properties p = new Properties();
		try{
			FileInputStream inputFile = new FileInputStream("HandoverServer.properties"); 
			p.load(inputFile);
			CALLPLANFILE = p.getProperty("callplan");
			USERSFILE = p.getProperty("users");
			if(CALLPLANFILE == null || CALLPLANFILE.isEmpty()){
				System.out.println("Failed to configure call plan file");
				return;
			}
			if(USERSFILE == null || USERSFILE.isEmpty()){
				System.out.println("Failed to configure users file");
				return;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		try {
			ServerSocket serverSocket = new ServerSocket(5000);
			System.out.println("System running on port 5000");
			
			while(true) {
				Socket clientSocket = serverSocket.accept();
				Thread requestHandler = new Thread(new RequestHandler(clientSocket));
				requestHandler.start();
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}
