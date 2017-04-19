package com.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import org.omg.CORBA.StringHolder;

public class UDPServer extends Thread{
	int port;
	GameServerImpl server;
	
	public UDPServer(int port,GameServerImpl server){
		this.server=server;
		this.port=port;
	}
	//3 servers, 1411, 1412, 1413
	public void run(){
		System.out.println(Thread.currentThread().getName()+port);
		DatagramSocket aSocket = null;
		try {
			aSocket=new DatagramSocket(port);
			aSocket.setReuseAddress(true);
			
			while(true){
					byte[] buffer = new byte[64000];	//kept inside the while loop so its initiated on every request
					DatagramPacket request = new DatagramPacket(buffer,buffer.length);
					aSocket.receive(request);
					String temp=new String(request.getData(),0,request.getLength());
					temp=temp.trim();
					String[] values=temp.split("_");
					
					if (values[0].contains("create")) {	
						//format is create_132.2.2.2_fnm_lnm_age_unm_pwd
						buffer=server.createPlayerAccount(values[2], values[3], Integer.parseInt(values[4]), values[5], values[6], values[1]).getBytes();
					}
					else if (values[0].contains("signin")) {
						//call sign in by extracting parameters
						//format is signin_132.2.2.2_unm_pwd
						buffer=server.processSignIn(values[2], values[3], values[1]).getBytes();
					}
					else if (values[0].contains("signout")) {
						//format is signout_132.2.2.2_unm
						buffer=server.processSignOut(values[2], values[1]).getBytes();
					
					}else if (values[0].contains("failuretolerance")) {
						//failuretolerance_132.0.0.0_show
						buffer=server.showFailureTolerance().getBytes();
					}
					else if (values[0].contains("suspend")) {
						//suspend_132.2.2.2_adminunm_adminpwd_unmtosuspend
						buffer=server.suspendAccount(values[2], values[3], values[1], values[4]).getBytes();
					}
					else if (temp.contains("transfer")) {
						//transfer_132.2.2.2_m123_mpass_93.3.3.3
						buffer=server.transferAccount(values[2], values[3], values[1], values[4]).getBytes();
					}
					else if (temp.contains("getstatus")) {
						
						//getstatus_132.3.3.3_admin_admin
						buffer=server.getPlayerStatus(values[1]).getBytes();
					}
					
					else if (temp.contains("stop")) {
						
						aSocket.setReuseAddress(true);
						aSocket.close();
						aSocket.disconnect();
						break;
					}else if (temp.contains("restore")) {
						//call method to write hash data to file
						int i;
						if (port==1411) {
							i=1;
						}else if (port==1412) {
							i=2;
						}else {
							i=3;
						}
						server.backUpData(i);
						aSocket.setReuseAddress(true);
						aSocket.close();
						aSocket.disconnect();
						break;
					}
					else if (temp.contains("call")) {
						buffer=server.calculateStatus().getBytes();
					}
					else
						buffer="error sumwer".getBytes();
					
					
					DatagramPacket reply = new DatagramPacket(buffer, buffer.length, request.getAddress(), request.getPort());
					aSocket.send(reply);
					
			}
		} catch (SocketException e) {
			System.out.println("SocketException : " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IOException : " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Exception in udpserver class: " + e.getMessage());
		}
	}
}
