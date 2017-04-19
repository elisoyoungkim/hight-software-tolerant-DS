package com.gamelib.leader;
import java.net.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.io.*;

public class UDPFIFOReceiver {

	/**
	 * @param args
	 */
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("===FIFO receiver started===");
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket(6789);

			while (true) {
				byte[] buffer = new byte[64000];
				DatagramPacket request = new DatagramPacket(buffer,
						buffer.length);
				aSocket.receive(request);

				String requestedString = new String(request.getData());
				
				System.out.println("request : " + requestedString);
				PrintWriter pw = new PrintWriter(new FileWriter("fifo.txt",true));
				pw.println(requestedString);
				pw.close();
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
