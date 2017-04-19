package com.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ProcessMulticastRequest {
	String replicaID="kim";
	String ipForLeader="132.205.93.59";
	
	public void request(String msg){
		//arrange the data for further processing
		String[] reqArr = msg.split("-");
		int seqNumber = Integer.parseInt(reqArr[0]);
		String method = reqArr[1];
		String ipAddress = reqArr[2];
		String params=reqArr[3];
		
		DatagramSocket socket=null;
		int port=0;
		//extract the ip address & send directly to appropriate server
		if (ipAddress.startsWith("132")) {
			port=1411;
		}else if(ipAddress.startsWith("93")) {
			port=1412;
		}else if (ipAddress.startsWith("182")) {
			port=1413;
		}
		
		String temp=method+"_"+ipAddress+"_"+params;
		String ans=callUDP(socket, "localhost", port, temp);
		
		System.out.println(ans);
		//attach the replica-id & send reply back to the leader
		String passToLeader=replicaID+"-"+ans;
		
		
		callUDP(socket, ipForLeader, 1919, passToLeader);//ip of leader
	}
	public String callUDP(DatagramSocket aSocket,String ipadd, int serverPort, String msg) {
		try {
			aSocket = new DatagramSocket();
			byte[] m = msg.getBytes();
			InetAddress aHost = InetAddress.getByName(ipadd);
			//System.out.println("inside calludp:"+msg);
			DatagramPacket request = new DatagramPacket(m, m.length, aHost,
					serverPort);
			aSocket.send(request);
			if (ipadd.equals("localhost")) {
				byte[] buffer = new byte[64000];
				DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(reply);
				return new String(reply.getData());	
			}
			return "";

			

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
