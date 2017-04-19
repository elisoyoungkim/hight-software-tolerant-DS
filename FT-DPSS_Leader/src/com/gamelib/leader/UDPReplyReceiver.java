package com.gamelib.leader;
import java.net.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.io.*;

import com.gamelib.server.GameServer;

public class UDPReplyReceiver {

	/**
	 * @param args
	 */
	
	static String REPLICA_1_ID="", REPLICA_2_ID = "";
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("===Reply Receiver started===");
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket(1919);

			while (true) {
				byte[] buffer = new byte[64000];
				DatagramPacket request = new DatagramPacket(buffer,
						buffer.length);
				aSocket.receive(request);

				String replyString = new String(request.getData());
				
				GameServer.reply_1 = replyString;
//				if (replyString.contains(REPLICA_1_ID)){
//					GameServer.reply_1 = replyString;
//				}else if (replyString.contains(REPLICA_2_ID)){
//					GameServer.reply_2 = replyString;
//				}
				System.out.println("got response :" + replyString);
				
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
