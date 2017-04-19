package com.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
//this class is internally used by servers for getplayer status & transfer methods
public class UDPServerInternal extends Thread {
	int port;
	GameServerImpl server;
	
	public UDPServerInternal(int port,GameServerImpl server){
		this.server=server;
		this.port=port;
	}
	
	public void run(){
		System.out.println(Thread.currentThread().getName()+port);
		DatagramSocket aSocket = null;
		try {
			aSocket=new DatagramSocket(port);
			aSocket.setReuseAddress(true);
			
			while(true){
					byte[] buffer = new byte[64000];	//kept inside the while loop so its initiated on every request
					DatagramPacket request = new DatagramPacket(buffer,buffer.length);
					aSocket.receive(request);
					String temp=new String(request.getData());
					temp=temp.trim();
					String ans="somevalue";
					
					//when server passes "call", it means call calculate status method
					if (temp.contains("call")) {
						buffer=server.calculateStatus().getBytes();
					}
					//to stop the server
					else if (temp.contains("stop")) {
						aSocket.setReuseAddress(true);
						aSocket.close();
						aSocket.disconnect();
						break;
						
					}else if (temp.contains("restore")) {//stop with data restore
						aSocket.setReuseAddress(true);
						aSocket.close();
						aSocket.disconnect();
						break;
					}
					else if(temp.contains("_")){	//this means request came from transferAccount method
						String[] userInfo=temp.split("_");
						
						//0-unm,1-pwd,2-newip,3-age,4-fnm,5-lnm
						buffer=server.createPlayerAccount(userInfo[4], userInfo[5], Integer.parseInt(userInfo[3]), userInfo[0], userInfo[1], userInfo[2]).getBytes();
					}else
					{	
						buffer="error somewhere".getBytes();
					}
					DatagramPacket reply = new DatagramPacket(buffer, buffer.length, request.getAddress(), request.getPort());
					aSocket.send(reply);
					
			}
		} catch (SocketException e) {
			System.out.println("SocketException : " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IOException : " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Exception in udpserverinternal: " + e.getMessage());
		}
	}
}
