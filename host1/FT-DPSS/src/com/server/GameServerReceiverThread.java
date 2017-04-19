package com.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class GameServerReceiverThread extends Thread {
	// private DatagramSocket socket;
	private ServerImpl server;
	int port;

	public GameServerReceiverThread(ServerImpl server, int socketNumber) {
		this.server = server;
		this.port = socketNumber;
	}

	@Override
	public void run() {
		DatagramSocket socket = null;
		// System.out.println(Thread.currentThread().getName());
		try {
			socket = new DatagramSocket(port);
			socket.setReuseAddress(true);
			while (true) {
				
				byte[] buf = new byte[64000];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);

				String temp = new String(packet.getData(),0,packet.getLength());
				temp.trim();
				String[] values=temp.split("_");

				if (values[0].contains("create")) {
					//create_132.0.0.0_fnm_lnm_age_unm_pwd
					buf = server.createPlayerAccount(values[2], values[3], Integer.parseInt(values[4]),
							values[5], values[6], values[1]).getBytes();
				} else if (temp.contains("signin")) {
					//signin_132.0.0.0_unm_pwd
					buf=server.playerSignIn(values[2], values[3], values[1]).getBytes();
					
				} else if (temp.contains("signout")) {
					//signout_132.0.0.0_unm
					buf=server.playerSignOut(values[2], values[1]).getBytes();
					
				} else if (temp.contains("suspend")) {
					//suspend_132.0.0.0_adminunm_adminpwd_unmotsuspend
					buf=server.suspendAccount(values[2], values[3], values[1], values[4]).getBytes();
					
				} else if (temp.contains("getstatus")) {
					//getstatus_132.0.0.0_admin_admin
					buf=server.getPlayerStatus(values[2], values[3], values[1]).getBytes();
					
				} else if (temp.contains("transfer")) {
					//transfer_132.0.0.0_unm_pwd_93.3.3.3
					buf = server.transferAccount(values[2], values[3],
							values[1], values[4]).getBytes();
					                                                                                      
				} else if (temp.contains("failuretolerance")) {
					//failuretolerance_132.0.0.0_show
					buf=server.showFailureTolerance().getBytes();
					                                                                                      
				}
				else if (temp.contains("stop")) {//stop the servers
					socket.setReuseAddress(true);
					socket.close();
					socket.disconnect();
					break;
				} else if (temp.contains("restore")) {//stop with backing up the data
					//take data backup
					int i;
					if (port==1411) {
						i=1;
					}else if (port==1412) {
						i=2;
					}else
						i=3;
					
					server.backUpData(i);
					
					socket.setReuseAddress(true);
					socket.close();
					socket.disconnect();
					break;
				}
				else if (temp.contains("call")) {// that means call for get
													// player status
					buf = server.getPlayerCount().getBytes();
				}

				InetAddress address = packet.getAddress();
				int port = packet.getPort();

				packet = new DatagramPacket(buf, buf.length, address, port);
				socket.send(packet);
			}// while ends
		}catch (SocketException e) {
			System.out.println("SocketException in gsr : " + e.getMessage()+port);
		} catch (IOException e) {
			System.out.println("IOException : " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Exception : " + e.getMessage());
		}
		

	}
}
