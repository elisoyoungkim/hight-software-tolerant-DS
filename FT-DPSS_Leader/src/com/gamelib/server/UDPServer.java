package com.gamelib.server;

import java.net.*;
import java.io.*;

import org.omg.CORBA.StringHolder;


public class UDPServer extends Thread{

	int port;
	GameServerImplementation _gImp;
	
	public UDPServer(int port, GameServerImplementation gImp) {
		// TODO Auto-generated constructor stub
		this.port = port;
		this._gImp = gImp;
		
	}

	/**
	 * @param args
	 */
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		DatagramSocket aSocket = null;
		try {
			
			aSocket = new DatagramSocket(this.port);
			
			
			while(true){
				byte[] buffer = new byte[64000];
				DatagramPacket request = new DatagramPacket(buffer,buffer.length);
				aSocket.receive(request);
				
				String requestedString = new String(request.getData(),0,request.getLength());// buffer, 0, request.getLength());
				requestedString = requestedString.trim();
				
				if (requestedString.contains("getplayerstatus")){
					buffer = _gImp.checkStatus().getBytes();	
				}else{
					String _concatedClient = requestedString;
//					buffer = _concatedClient.getBytes();
					
					String[] _arrClient = _concatedClient.split("-");
					String reply = _gImp.isUserAvailable(_arrClient[3]);
					if (reply.equalsIgnoreCase("false")) { // not available
						
						_gImp.createPlayerAccount(_arrClient[0], _arrClient[1],Integer.parseInt( _arrClient[2]),
												_arrClient[3], _arrClient[4],
												_arrClient[5]);
						
						buffer = "User transferred".getBytes();						
					}else{
						buffer = "can not transfer".getBytes(); // because some user with same username
					}
					
				}
				
				
				DatagramPacket reply = new DatagramPacket(buffer, buffer.length, request.getAddress(), request.getPort());
				aSocket.send(reply);
			}
		} catch (SocketException e) {
			// TODO: handle exception\
			System.out.println("Error in SocketException : " + e.getMessage());
		} catch (IOException e) {
			// TODO: handle exception
			System.out.println("Error in IOException : " + e.getMessage());
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error in Exception : " + e.getMessage());
		}
		
	}

}
