package com.server;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;


public class MulticastReceiver {
	
	public static void main(String[] args) {
		System.out.println("--- Multicast Reciever ---");
	    MulticastSocket socket = null;
	    DatagramPacket inPacket = null;
	    
	    try {
	      //Prepare to join multicast group
	      socket = new MulticastSocket(1777);
	      InetAddress address = InetAddress.getByName("239.2.2.2");//multicast address
	      socket.joinGroup(address);
	 
	      while (true) {
	    	byte[] inBuf = new byte[64000];
	        inPacket = new DatagramPacket(inBuf, inBuf.length);
	        socket.receive(inPacket);
	        String msg = new String(inBuf, 0, inPacket.getLength());
	  
	        System.out.println("got via multicast"+msg);
	        new ProcessMulticastRequest().request(msg);//forward the request for further processing
	        
	      }
	    } catch (IOException ioe) {
	      System.out.println(ioe.getMessage());
	    }
	  }
}
