package com.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPforRM {

	public static void main(String[] args) {
		new UDPforRM().start();
	}

	private void start() {
		System.out.println("UDPforRM ready to listen");
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket(2222);

			while (true) {
				byte[] buffer = new byte[64000]; // kept inside the while loop
													// so its initiated on every
													// request
				DatagramPacket request = new DatagramPacket(buffer,
						buffer.length);
				aSocket.receive(request);
				String temp = new String(request.getData());
				temp = temp.trim();
				
				if (temp.contains("StartServers")) {
					buffer = startServers(0).getBytes();

				} 
				//to stop the servers
				else if (temp.contains("stop")) {
					restartServers("stop");
					Thread.sleep(1000);
					buffer = startServers(0).getBytes();
					
				}
				//stop the servers with backing up the data
				else if (temp.contains("restore")) {
					restartServers("restore");
					Thread.sleep(1000);//so that it gives time to disconnect the sockets properly
					buffer = startServers(1).getBytes();
				}
				else
					buffer = "error sumwer in udpforrm".getBytes();

				DatagramPacket reply = new DatagramPacket(buffer,
						buffer.length, request.getAddress(), request.getPort());
				aSocket.send(reply);

			}
		} catch (SocketException e) {
			System.out.println("SocketException : " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IOException : " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Exception : " + e.getMessage());
		}
	}

	private String restartServers(String msg){
		DatagramSocket socket1=null;
		String ans=callUDP(socket1, 1411, msg);
		String ans1=callUDP(socket1, 1511, msg);
		
		ans=callUDP(socket1, 1412, msg);
		ans1=callUDP(socket1, 1512, msg);
		
		ans=callUDP(socket1, 1413, msg);
		ans1=callUDP(socket1, 1513, msg);
		
		return "all servers stopped";
	}
	
	private String startServers(int flag) throws NumberFormatException, IOException {
		//0-dont restore,1-restore
		new ServerImpl(1411,1511,flag);
		new ServerImpl(1412,1512,flag);
		new ServerImpl(1413,1513,flag);
		System.out.println("all server's started");
		return "all servers started for kim's replica";
	}

	public String callUDP(DatagramSocket aSocket, int serverPort, String msg) {
		try {
			aSocket = new DatagramSocket();
			byte[] m = msg.getBytes();
			InetAddress aHost = InetAddress.getByName("localhost");

			DatagramPacket request = new DatagramPacket(m, m.length, aHost,
					serverPort);
			aSocket.send(request);
			
			return "its from stop";

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


