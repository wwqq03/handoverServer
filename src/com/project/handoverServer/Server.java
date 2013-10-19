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
				Thread requestHandler = new Thread(new RequestHandler(clientSocket));
				requestHandler.start();
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}
