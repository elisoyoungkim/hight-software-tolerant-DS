package com.RM;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;



public class UDPforReplicas {
	String ipForKim="132.205.93.61";
	
	public static void main(String[] args) {
		new UDPforReplicas().startServers();
	}
	public void startServers(){
		String ans;
		DatagramSocket aSocket = null;
		//calling meet's replica,so localhost
		ans = new UDPforReplicas().callUDP(aSocket,"localhost", 2222, "StartServers");
		System.out.println(ans);
		
		//calling kim's replica,enter kim's ipadd
		ans=new UDPforReplicas().callUDP(aSocket,ipForKim, 2222, "StartServers");
		System.out.println(ans);
		
	}
	
	public String callUDP(DatagramSocket aSocket,String ip, int serverPort, String msg) {
		try {
			aSocket = new DatagramSocket();
			byte[] m = msg.getBytes();
			InetAddress aHost = InetAddress.getByName(ip);
			
			DatagramPacket request = new DatagramPacket(m, m.length, aHost,
					serverPort);
			aSocket.send(request);
			if (msg.equals("stop")) {
				return "restarted!";	
			}
			byte[] buffer = new byte[64000];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			aSocket.receive(reply);

			return new String(reply.getData());

		} catch (SocketException e) {

			System.out.println("Error in SocketException : " + e.getMessage());
		} catch (IOException e) {

			System.out.println("Error in IOException : " + e.getMessage());
		} catch (Exception e) {

			System.out.println("Error in Exception : " + e.getMessage());
		} finally {

			if (aSocket != null)
				aSocket.close();

		}
		return "";
	}

}
