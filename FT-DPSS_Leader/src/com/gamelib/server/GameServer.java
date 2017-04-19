package com.gamelib.server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;


import com.gamelib.client.GameClient;
import com.gamelib.helpers.MethodHelper;
import com.gamelib.leader.UDPSender;

public class GameServer {


	
	/**
	 * @param args
	 * @throws InvalidName 
	 * @throws WrongPolicy 
	 * @throws ServantAlreadyActive 
	 * @throws ObjectNotActive 
	 * @throws FileNotFoundException 
	 */
	public String _username, _password, _firstname, _lastname, _ipaddress;
	public int _age;
	public boolean _signinstatus = false;
	
	GameServerImplementation iServer_NA, iServer_EU, iServer_AS;

	static String FIFO_FILENAME = "fifo.txt";
	static String FE_IP = "132.205.93.61";
	static String RM_IP = "132.205.93.60";
	static String MULTICAST_IP = "239.2.2.2";
	

	static String REP_ID_1 = "meet";
	static String REP_ID_2 = "kim";
	
	static int FE_PORT = 9876;
	static int RM_PORT = 9898;
	static int MULTICAST_PORT = 1777;
	static int FIFO_PORT = 6789;
	static int REPLY_RECEIVER_PORT = 1919;
	
	public static String reply_1 = "", reply_2 = "", reply = null;
	
	static Thread replyReceiverThread, FIFOReceiverThread, requestProcessorThread, replyFromRMRecieverThread;
	
	ArrayList<String> arrFifoForRequests = null;
	
	static GameServer gClient;
	
	public static void main(String[] args) throws IOException, InterruptedException{
		
		System.out.println("===Leader started===");
		
		//new GameServer().startLeader(new GameServer());
		gClient = new GameServer();
		gClient.arrFifoForRequests = new ArrayList<String>();
		gClient.initServers();
		gClient.initThreads();

	}
	
	public void startLeader(){
		gClient = new GameServer();
		gClient.initServers();
		gClient.initThreads();
	}
	// method to create player account
	// it finds region from ipaddress and send request to particular server
	
	public String createPlayerAccount(String ipaddress, String[] params) {

		// TODO Auto-generated method stub
		this._firstname = params[0];
		this._lastname = params[1];
		this._age = Integer.parseInt(params[2]);
		this._username = params[3];
		this._password = params[4];
		this._ipaddress = ipaddress;

		MethodHelper mH = new MethodHelper();
		String region = mH.fetchRegion(ipaddress);
		
		String reply = "";
		
		if (region.equalsIgnoreCase("NA")) { // NA
			reply = iServer_NA.createPlayerAccount(this._firstname, this._lastname,
					this._age, this._username, this._password, this._ipaddress);
			
		} else if (region.equalsIgnoreCase("EU")) { // EU
			reply = iServer_EU.createPlayerAccount(this._firstname, this._lastname,
					this._age, this._username, this._password, this._ipaddress);
			
		} else if (region.equalsIgnoreCase("AS")) { // AS
			reply = iServer_AS.createPlayerAccount(this._firstname, this._lastname,
					this._age, this._username, this._password, this._ipaddress);
			
		}
		
		return reply;

	}
	
	// method to singIn
	// it finds region from ipaddress and send request to particular server
	
	public String singIn(String ipaddress, String[] params) {
		// TODO Auto-generated method stub
		
		this._username = params[0];
		this._password = params[1];

		this._ipaddress = ipaddress;

		MethodHelper mH = new MethodHelper();
		String region = mH.fetchRegion(ipaddress);
		
		
		String reply = "";
		if (region.equalsIgnoreCase("NA")) { // NA
			reply = iServer_NA.playerSignIn(this._username, this._password,
					this._ipaddress);
		}else if (region.equalsIgnoreCase("EU")) { // NA
			reply = iServer_EU.playerSignIn(this._username, this._password,
					this._ipaddress);
			
		}else if (region.equalsIgnoreCase("AS")) { // NA
			reply = iServer_AS.playerSignIn(this._username, this._password,
					this._ipaddress);
		}
		
		return reply;
	}
	
	// method to signOut
	// it finds region from ipaddress and send request to particular server
	
	public String signOut(String ipaddress, String[] params){
		// TODO Auto-generated method stub
		
		this._username = params[0];
		this._ipaddress = ipaddress;
		
		MethodHelper mH = new MethodHelper();
		String region = mH.fetchRegion(ipaddress);
		
		String reply = "";
		if (region.equalsIgnoreCase("NA")) { // NA
			reply = iServer_NA.playerSignOut(this._username, this._ipaddress);
		} else if (region.equalsIgnoreCase("EU")) { // NA
			reply = iServer_EU.playerSignOut(this._username, this._ipaddress);
		} else if (region.equalsIgnoreCase("AS")) { // NA
			reply = iServer_AS.playerSignOut(this._username, this._ipaddress);
		}
		
		return reply;
	}
	
	// method to suspendAccount
	// it finds region from ipaddress and send request to particular server
	
	public String suspendAccount(String ipaddress, String[] params){
		// TODO Auto-generated method stub
		
		MethodHelper mH = new MethodHelper();
		String region = mH.fetchRegion(ipaddress);
		
		String reply = "";
		System.out.println("region : " + region);
		if (region.equalsIgnoreCase("NA")) { // NA
			reply = iServer_NA.suspendAccount(params[0], params[1], ipaddress, params[2]);
		} else if (region.equalsIgnoreCase("EU")) { // EU
			reply = iServer_EU.suspendAccount(params[0], params[1], ipaddress, params[2]);
		} else if (region.equalsIgnoreCase("AS")) { // AS
			reply = iServer_AS.suspendAccount(params[0], params[1], ipaddress, params[2]);
		}
		
		return reply;
	}      
	
	// method to getStatus
	// it finds region from ipaddress and send request to particular server
	
	public String getStatus(String ipaddress, String[] params) {
		// TODO Auto-generated method stub
		
		MethodHelper mH = new MethodHelper();
		String region = mH.fetchRegion(ipaddress);
		
		String reply = "";
		if (region.equalsIgnoreCase("NA")) { // NA
			reply = iServer_NA.getPlayerStatus(params[0],params[1], ipaddress);
		} else if (region.equalsIgnoreCase("EU")) { // NA
			reply = iServer_EU.getPlayerStatus(params[0],params[1], ipaddress);
		} else if (region.equalsIgnoreCase("AS")) { // NA
			reply = iServer_AS.getPlayerStatus(params[0],params[1], ipaddress);
		}
		
		return reply;
	}

	// method to transferAccount
	// it finds region from ipaddress and send request to particular server
	
	public String transferAccount(String ipaddress, String[] params) {

		// TODO Auto-generated method stub
		String username = params[0];
		String password = params[1];
		String newipaddress = params[2];
		String oldipaddress = ipaddress;
		
		MethodHelper mH = new MethodHelper();
		String region = mH.fetchRegion(ipaddress);
		
		String reply = "";
		if (region.equalsIgnoreCase("NA")) { // NA
			reply = iServer_NA.transferAccount(username, password, oldipaddress, newipaddress);
		} else if (region.equalsIgnoreCase("EU")) { // EU
			reply = iServer_EU.transferAccount(username, password, oldipaddress, newipaddress);
		} else if (region.equalsIgnoreCase("AS")) { // AS
			reply = iServer_AS.transferAccount(username, password, oldipaddress, newipaddress);
		}

		return reply;

	}
	
	// method to show failure tolerance
	// it finds region from ipaddress and send request to particular server
	
	public String showFailureTolerance(String ipaddress) {

		// TODO Auto-generated method stub
		
		MethodHelper mH = new MethodHelper();
		String region = mH.fetchRegion(ipaddress);
		
		String reply = "";
		if (region.equalsIgnoreCase("NA")) { // NA
			reply = iServer_NA.showFailureTolerance();
		} else if (region.equalsIgnoreCase("EU")) { // EU
			reply = iServer_EU.showFailureTolerance();
		} else if (region.equalsIgnoreCase("AS")) { // AS
			reply = iServer_AS.showFailureTolerance();
		}

		return reply;

	}
	
	// method to multicast request to replicas
	// it multicasts request on specific ip address and specific port
	
	private static void multicastRequest(String request) throws IOException {
		 DatagramSocket socket = null;
		    DatagramPacket outPacket = null;
		    
		    final int PORT = MULTICAST_PORT;//1777;
		 
		    try {
		      socket = new DatagramSocket();
		      
		      byte[] outBuf = request.getBytes();
		 
		        //Send to multicast IP address and port
		        InetAddress address = InetAddress.getByName(MULTICAST_IP); //InetAddress.getByName("239.2.2.2");
		        outPacket = new DatagramPacket(outBuf, outBuf.length, address, PORT);
		 
		        socket.send(outPacket);
		    } catch (IOException ioe) {
		      System.out.println(ioe);
		    }
	}
	
	// method to get request as per priority ( sequence number )
	// it fetches request with lowest sequence number from FIFO
	
	public String getPriorRequest() throws NumberFormatException, IOException{
		String strToReturn = "not found";

		/* FIFO with file */
		 
		if (new File(FIFO_FILENAME).exists()){
			BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(FIFO_FILENAME))));
			int min = 0;
			
			String str = null;
			
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
		
		
		/*
		int min = 0;
		for (int i = 0;i<arrFifoForRequests.size();i++) {
			if(i==0){
				min = Integer.parseInt(arrFifoForRequests.get(i).split("-")[0]);
				strToReturn = arrFifoForRequests.get(i);
			}
			
			if (Integer.parseInt(arrFifoForRequests.get(i).split("-")[0]) < min){
				min = Integer.parseInt(arrFifoForRequests.get(i).split("-")[0]);
				strToReturn = arrFifoForRequests.get(i);
				
			}
			
		}
		*/
		return strToReturn;
	}
	
	// method to delete processed request from FIFO

	
	private void deleteRequestFromFIFO(String reqString) throws IOException {
		// TODO Auto-generated method stub
		/*
		for (int i = 0;i<arrFifoForRequests.size();i++) {
			
			if (arrFifoForRequests.get(i).equalsIgnoreCase(reqString)){
				arrFifoForRequests.remove(i);
			}
			
		}
		 Fifo with file */
		DataInputStream in = new DataInputStream(new FileInputStream(FIFO_FILENAME));
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		String strline;
		String input = "";
		while ((strline = br.readLine()) != null) {
			
			if (strline.equalsIgnoreCase(reqString)) { 
			} else {
				input += strline + "\n";
			}
		}

		br.close();
		FileOutputStream file = new FileOutputStream(FIFO_FILENAME);
		file.write(input.getBytes()); // write modified data to file
		file.close();
		
		
	}

	// method to initialize servers region wise
	
	private void initServers(){
		iServer_NA = new GameServerImplementation(1101);
		iServer_EU = new GameServerImplementation(1102);
		iServer_AS = new GameServerImplementation(1103);
	}

	// method to initialize necessary threads
	
	private void initThreads() {
		// TODO Auto-generated method stub
		
		// Thread to recieve replies from replica 1 and replica 2. It checks continuesly for reply. if it found reply from
		// both replicas then it would compare outputs of all replicas and check if any fault found or not.
		// if it any kind of fault it would inform RM 
		
		replyReceiverThread = new Thread() {
		    public void run() {
		        System.out.println("===Reply Receiver Thread started===");
				DatagramSocket aSocket = null;
				try {
					aSocket = new DatagramSocket(REPLY_RECEIVER_PORT);

					while (true) {
						byte[] buffer = new byte[64000];
						DatagramPacket request = new DatagramPacket(buffer,
								buffer.length);
						aSocket.receive(request);

						String replyString = new String(request.getData(),0,request.getLength());
						replyString = replyString.trim();
						
						if(replyString.contains("meet")){
							reply_1 = replyString;	
							System.out.println("got response from meet :" + replyString);
						}
						if(replyString.contains("kim")){
							reply_2 = replyString;
							System.out.println("got response from kim :" + replyString);	
						}
						
						System.err.println("got reply1 : " + reply_1);
						System.err.println("got reply2 : " + reply_2);
						
						if(!reply_1.equalsIgnoreCase("") && !reply_2.equalsIgnoreCase("")){
							
							String rplForRM = "";
							
							if (reply_1.contains("getstatus") && reply_2.contains("getstatus")){
								String[] arrReply = reply.split("-");
								String[] arrReply1 = reply_1.split("-");
								String[] arrReply2 = reply_2.split("-");
								
								int NA_Online = Integer.parseInt(arrReply[1]);
								int NA_Offline = Integer.parseInt(arrReply[2]);
								int EU_Online = Integer.parseInt(arrReply[3]);
								int EU_Offline = Integer.parseInt(arrReply[4]);
								int AS_Online = Integer.parseInt(arrReply[5]);
								int AS_Offline = Integer.parseInt(arrReply[6]);
								
								
								if (NA_Online == Integer.parseInt(arrReply1[2]) && NA_Offline== Integer.parseInt(arrReply1[3]) &&
									EU_Online== Integer.parseInt(arrReply1[4]) && EU_Offline== Integer.parseInt(arrReply1[5]) &&
									AS_Online== Integer.parseInt(arrReply1[6]) && AS_Offline== Integer.parseInt(arrReply1[7])){
									
									if(NA_Online == Integer.parseInt(arrReply2[2]) && NA_Offline== Integer.parseInt(arrReply2[3]) &&
											EU_Online== Integer.parseInt(arrReply2[4]) && EU_Offline== Integer.parseInt(arrReply2[5]) &&
											AS_Online== Integer.parseInt(arrReply2[6]) && AS_Offline== Integer.parseInt(arrReply2[7])){
//										rplForRM = "Correct for both replica";
									}else{
										rplForRM = "fault in " + REP_ID_2;
										 System.out.println("rplForRM : " + rplForRM);
										 sendUDPmessage(RM_IP, RM_PORT, rplForRM);
										 System.out.println("sent to RM !");
									}
									
								}else{
									rplForRM = "fault in " + REP_ID_1;
									 System.out.println("rplForRM : " + rplForRM);
									 sendUDPmessage(RM_IP, RM_PORT, rplForRM);
									 System.out.println("sent to RM !");
								}
							}else if (reply.contains("true") && reply_1.contains("true") && reply_2.contains("true")){
//								rplForRM = "Correct for both replica";
							}else if (reply.contains("false") && reply_1.contains("false") && reply_2.contains("false")){
//								rplForRM = "Correct for both replica";
							}else if (reply.contains("failuretolerance")){
								if (!reply_1.contains("true")){
									rplForRM = "fault in " + REP_ID_1;
									 System.out.println("rplForRM : " + rplForRM);
									 sendUDPmessage(RM_IP, RM_PORT, rplForRM);
									 System.out.println("sent to RM !");
								}else if (!reply_2.contains("true")){
									rplForRM = "fault in " + REP_ID_2;
									 System.out.println("rplForRM : " + rplForRM);
									 sendUDPmessage(RM_IP, RM_PORT, rplForRM);
									 System.out.println("sent to RM !");
								}
							}else {
								System.out.println("Find fault");
								String[] arrReply1 = reply_1.split("-");
								String flag1 = arrReply1[1];

								
								if (reply.split("-")[0].equalsIgnoreCase(flag1)){
									if (arrReply1.length > 2){
										String status_code = arrReply1[2];								
										if (!reply.split("-")[1].equalsIgnoreCase(status_code)){
											rplForRM = "fault in " + REP_ID_1;
											 System.out.println("rplForRM : " + rplForRM);
											 sendUDPmessage(RM_IP, RM_PORT, rplForRM);
											 System.out.println("sent to RM !");
										}
									}
								}
								else{
									rplForRM = "fault in " + REP_ID_1;
									 System.out.println("rplForRM : " + rplForRM);
									 sendUDPmessage(RM_IP, RM_PORT, rplForRM);
									 System.out.println("sent to RM !");
								}
								
								String[] arrReply2 = reply_2.split("-");
								String flag2 = arrReply2[1];

								if (reply.split("-")[0].equalsIgnoreCase(flag2)){
									if (arrReply2.length > 2){
										String status_code = arrReply2[2];								
										if (!reply.split("-")[1].equalsIgnoreCase(status_code)){
											rplForRM = "fault in " + REP_ID_2;
											 System.out.println("rplForRM : " + rplForRM);
											 sendUDPmessage(RM_IP, RM_PORT, rplForRM);
											 System.out.println("sent to RM !");
										}
									}
								}
								else{
									rplForRM = "fault in " + REP_ID_2;
									 System.out.println("rplForRM : " + rplForRM);
									 sendUDPmessage(RM_IP, RM_PORT, rplForRM);
									 System.out.println("sent to RM !");
								}
								
							} 
							
							String rplForFE = "";
							 if (reply.contains("true")){
								 rplForFE = reply.split("-")[1];
							 }else if (reply.contains("false")){
								 rplForFE = reply.split("-")[2];
							 }
							 
							 if (reply_1.contains("getstatus") || reply_2.contains("getstatus")) {
								 	String[] arrReply = reply.split("-");
									
									int NA_Online = Integer.parseInt(arrReply[1]);
									int NA_Offline = Integer.parseInt(arrReply[2]);
									int EU_Online = Integer.parseInt(arrReply[3]);
									int EU_Offline = Integer.parseInt(arrReply[4]);
									int AS_Online = Integer.parseInt(arrReply[5]);
									int AS_Offline = Integer.parseInt(arrReply[6]);
									
									rplForFE = "\nNA : " + NA_Online + " Online " + NA_Offline + " Offline";
									rplForFE += "\nEU : " + EU_Online + " Online " + EU_Offline + " Offline";
									rplForFE += "\nAS : " + AS_Online + " Online " + AS_Offline + " Offline";
							 }
							
							
							 System.out.println("rplForFE : " + rplForFE);
							 sendUDPmessage(FE_IP, FE_PORT,rplForFE);
							 System.out.println("send to FE !");
							
							
						}
						
						 
						
						
						
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
		};

		replyReceiverThread.start();
		
		// Thread to fetch request from FIFO and process it.
		// it processes request. Store output of that request and multicast that request to other two replicas.
		// Then after role of replyReceiverThread starts.
		requestProcessorThread = new Thread(){
			 public void run() {
				 System.out.println("===Request Processor Receiver Thread started===");
				 while (true) {
					 
					 
					try {
						 
						String reqString = getPriorRequest();
						
						if (!reqString.equalsIgnoreCase("not found")){

							reply_1 = "";
							reply_2 = "";	
							
							String[] reqArr = reqString.split("-");
							int seqNumber = Integer.parseInt(reqArr[0]);
							String method = reqArr[1];
							String ipAddress = reqArr[2];
							String[] params = reqArr[3].split("_");	
							
							
							if (method.equalsIgnoreCase("create")){
								reply = gClient.createPlayerAccount(ipAddress, params);
							}else if (method.equalsIgnoreCase("signin")){
								reply = gClient.singIn(ipAddress, params);
							}else if (method.equalsIgnoreCase("signout")){
								reply = gClient.signOut(ipAddress, params);
							}else if (method.equalsIgnoreCase("suspend")){
								reply = gClient.suspendAccount(ipAddress, params);
							}else if (method.equalsIgnoreCase("getstatus")){
								reply = gClient.getStatus(ipAddress, params);
							}else if (method.equalsIgnoreCase("transfer")){
								reply = gClient.transferAccount(ipAddress, params);
							}else if (method.equalsIgnoreCase("failuretolerance")){
								reply = gClient.showFailureTolerance(ipAddress);
							}
							
							System.out.println("befor multicast-- own reply: " + reply);
							GameServer.multicastRequest(reqString);
							deleteRequestFromFIFO(reqString);
							Thread.sleep(2000);
							//break; // don't forget to remove
						}
					} catch (IOException | InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
						
						// 3-create-132.65.163.13-fname_lname_7_username_password
				 }
				 
					
					
			 }
		};
		requestProcessorThread.start();
		
		// Thread to recieve requests and store them into FIFO.
		// it is maintained by file. This is expensive operation for server but if leader crashes and restarts, requests
		// should also be processed. Thats the actual concept of implementing FIFO by using file
				
		FIFOReceiverThread = new Thread() {
		    public void run() {
		        System.out.println("===Fifo Receiver Thread started===");
		        DatagramSocket aSocket = null;
				try {
					aSocket = new DatagramSocket(FIFO_PORT);

					while (true) {
						byte[] buffer = new byte[64000];
						DatagramPacket request = new DatagramPacket(buffer,
								buffer.length);
						aSocket.receive(request);

						String requestedString = new String(request.getData(),0,request.getLength());
						requestedString = requestedString.trim();
						System.out.println("request : " + requestedString);
						arrFifoForRequests.add(requestedString);
						
						/* FIFO with file */
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
		};

		FIFOReceiverThread.start();
		
		// Thread to recieve reply from Replica Manager
		// If any kind of fault would be found then replyReceiverThread would give message to RM with id of faulty replica
		// It receivers reply from RM whether operation of restart replica is done or not 
		replyFromRMRecieverThread = new Thread(){
			public void run() {
		        System.out.println("===RM Reply Receiver Thread started===");
		        DatagramSocket aSocket = null;
				try {
					aSocket = new DatagramSocket(RM_PORT);

					while (true) {
						byte[] buffer = new byte[64000];
						DatagramPacket request = new DatagramPacket(buffer,
								buffer.length);
						aSocket.receive(request);

						String requestedString = new String(request.getData(),0,request.getLength());
						requestedString = requestedString.trim();
						System.out.println("Reply From RM : " + requestedString);
						
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
		};
		
		replyFromRMRecieverThread.start();
	}

	// method to send udp message on passed ip address and port
	
	public void sendUDPmessage(String ipAdd, int port, String msg){
		
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket();
			byte[] m = msg.getBytes();
			InetAddress aHost = InetAddress.getByName(ipAdd);
			int serverPort = port;

			
			DatagramPacket request = new DatagramPacket(m, m.length,
					aHost, serverPort);
			aSocket.send(request);

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
