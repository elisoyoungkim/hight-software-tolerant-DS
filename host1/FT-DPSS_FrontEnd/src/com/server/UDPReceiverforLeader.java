package com.server;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPReceiverforLeader {

	public static void main(String[] args) {
		PrintWriter print=null;
		DatagramSocket socket=null;
		System.out.println("ready to listen from leader");
		try {
			
			socket=new DatagramSocket(9876);
			while (true) {
				print=new PrintWriter(new FileWriter("output.txt"),true);
				byte[] buffer=new byte[64000];
				DatagramPacket receive=new DatagramPacket(buffer, buffer.length);
				socket.receive(receive);
				String replyFromLeader=new String(receive.getData(),0,receive.getLength());
				System.out.println("reply from the leader:"+replyFromLeader);
				//print the replies to the output file
				print.println(replyFromLeader);
				print.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
