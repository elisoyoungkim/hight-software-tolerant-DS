package com.gamelib.leader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


public class UDPSender {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}
	//"172.31.105.181" & 9876
	public void sendReply(String ipAdd, int port, String msg){
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket();
			byte[] m = msg.getBytes();
			InetAddress aHost = InetAddress.getByName(ipAdd);
			int serverPort = port;

			
			DatagramPacket request = new DatagramPacket(m, m.length,
					aHost, serverPort);
			aSocket.send(request);

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
