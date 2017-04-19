package com.gamelib.leader;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.gamelib.client.GameClient;


public class FIFOOperations {

	/**
	 * @param args
	 */
	
	static String FIFO_FILENAME = "fifo.txt";
	static String REPLY_IP = "192.168.2.13";
	static int REPLY_PORT = 9876;
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String reqString = getPriorString();
		
		// 3-create-132.65.163.13-fname_lname_7_username_password
		UDPSender udp = new UDPSender();
		udp.sendReply(REPLY_IP, REPLY_PORT,"msg");
	}
	
	private static void multicastRequest(String request) throws IOException {
		 DatagramSocket socket = null;
		    DatagramPacket outPacket = null;
		    
		    final int PORT = 1111;
		 
		    try {
		      socket = new DatagramSocket();
		      long counter = 0;
		      byte[] outBuf = request.getBytes();
		 
		        //Send to multicast IP address and port
		        InetAddress address = InetAddress.getByName("224.2.2.3");
		        outPacket = new DatagramPacket(outBuf, outBuf.length, address, PORT);
		 
		        socket.send(outPacket);

		    } catch (IOException ioe) {
		      System.out.println(ioe);
		    }
	}
	private static String getPriorString() throws IOException {
		String strToReturn = "";
		
		if (new File(FIFO_FILENAME).exists()){
			BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(FIFO_FILENAME))));
			int min = 0;
			
			String str = "";
			
			if ((str = br.readLine()) != null){
				min= Integer.parseInt(str.split("-")[0]);
				strToReturn = str;
			}
			
			while ((str = br.readLine()) != null) {
				if (Integer.parseInt(str.split("-")[0]) < min){
					min = Integer.parseInt(str.split("-")[0]);
					strToReturn = str;
				}
			}
		}else{
			strToReturn = "FIFO not found";
		}

		
		
		if (strToReturn == null) strToReturn = "no request found";
		return strToReturn;
	}

}
