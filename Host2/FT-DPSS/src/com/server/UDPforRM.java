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
													// so its initiated on every request
				DatagramPacket request = new DatagramPacket(buffer,
						buffer.length);
				aSocket.receive(request);
				String temp = new String(request.getData());
				temp = temp.trim();

				if (temp.contains("StartServers")) {
					buffer = startServers(0).getBytes();
				} 
				else if (temp.contains("stop")) {
					//refill hashtables
					restartServers("stop");
					Thread.sleep(1000);//so that it gives time to close the sockets occupied by servers
					
					buffer = startServers(0).getBytes();
				}else if (temp.contains("restore")) {
					//refill hashtables
					
					restartServers("restore");
					System.err.println("backup taken");
					Thread.sleep(1000);//so that it gives time to close the sockets occupied by servers
					
					buffer = startServers(1).getBytes();
					System.err.println("backup restored");
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
			System.out.println("Exception in udpforrm: " + e.getMessage());
		}
	}

	private String restartServers(String msg){
		DatagramSocket socket1=null;
		callUDP(socket1, 1411, msg);
		callUDP(socket1, 1511, msg);
	
		callUDP(socket1, 1412, msg);
		callUDP(socket1, 1512, msg);
		
		callUDP(socket1, 1413, msg);
		callUDP(socket1, 1513, msg);
		return "all servers stopped";
	}
	
	private String startServers(int flag) throws IOException {
		//if flag=0, dont restore, if 1 then restore
		new GameServerImpl(1411,1511,flag);
		new GameServerImpl(1412,1512,flag);
		new GameServerImpl(1413,1513,flag);
		return "all servers started for meet's replica";
	}

	public String callUDP(DatagramSocket aSocket, int serverPort, String msg) {
		try {
			aSocket = new DatagramSocket();
			byte[] m = msg.getBytes();
			InetAddress aHost = InetAddress.getByName("localhost");

			DatagramPacket request = new DatagramPacket(m, m.length, aHost,
					serverPort);
			aSocket.send(request);
			return "its for stop";

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
