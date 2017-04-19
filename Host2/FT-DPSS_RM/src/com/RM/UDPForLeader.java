package com.RM;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;


public class UDPForLeader {
	
	static String ipForKim="132.205.93.61";
	
	public static void main(String[] args) {
		DatagramSocket aSocket = null;
		
		int counterMeet =0, counterKim = 0;
		try {
			aSocket=new DatagramSocket(9898);
			aSocket.setReuseAddress(true);
			System.out.println("Listening to leader if any replica gives bad result");
			while(true){
					byte[] buffer = new byte[64000];	//kept inside the while loop so its initiated on every request
					DatagramPacket request = new DatagramPacket(buffer,buffer.length);
					aSocket.receive(request);
					
					String temp=new String(request.getData(),0,request.getLength());
					temp = temp.trim();
					System.err.println(temp);
					if (temp.contains("meet")){
						counterMeet++;
						if (counterMeet == 3){//if counter reached 3, then restart the replica
							counterMeet = 0;
							DatagramSocket axSocket = null;
							//new UDPforReplicas().callUDP(axSocket, "localhost", 2222, "stop"); // ip of meet
							new UDPforReplicas().callUDP(axSocket, "localhost", 2222, "restore"); // ip of meet
							buffer="meet's replica restarted".getBytes();
						}else
							buffer="count havent reached yet for meet".getBytes();
					}
					if (temp.contains("kim")){
						counterKim++;
						if (counterKim ==3){//if counter reached 3, then restart the replica
							counterKim = 0;
							DatagramSocket axSocket = null;
							//ip of kim's replica
							//new UDPforReplicas().callUDP(axSocket,ipForKim , 2222, "stop"); // ip add of kim
							new UDPforReplicas().callUDP(axSocket,ipForKim, 2222, "restore"); // ip add of kim
							buffer="kim's replica restarted".getBytes();
						}else
							buffer="count havent reached yet for kim".getBytes();
					}
					
					DatagramPacket reply = new DatagramPacket(buffer, buffer.length, request.getAddress(), request.getPort());
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
}
