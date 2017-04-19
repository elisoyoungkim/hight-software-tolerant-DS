package com.server;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
//this class just sends the request to leader concurrently
public class UDPSenderforLeader extends Thread {
	String msg;
	String ipForLeader="132.205.93.59";
	public void run(){//new thread for each request
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket();
			byte[] m = msg.getBytes();
			InetAddress aHost = InetAddress.getByName(ipForLeader);//ip address of leader
			int serverPort = 6789;
			DatagramPacket request = new DatagramPacket(m, m.length,
					aHost, serverPort);
			aSocket.send(request);
			
		} catch (SocketException e) {
			System.out.println("Error in SocketException : " + e.getMessage());
		} catch (IOException e) {
			System.out.println("Error in IOException : " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Error in Exception : " + e.getMessage());
		}
	}	
}
