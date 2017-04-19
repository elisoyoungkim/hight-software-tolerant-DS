package com.server;

import java.util.List;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ServerImpl{
	private AccountTable accounts = new AccountTable();
	private static final int SOCKET_NA = 4445;
	private static final int SOCKET_EU = 4446;
	private static final int SOCKET_AS = 4447;
	public String location;
	private Logger logger;
	
	private void restoreData(int region) throws NumberFormatException, IOException{//read from file 
		BufferedReader br=initializeBR(region);
		String temp;
		while ((temp=br.readLine())!=null) {
			String[] t1=temp.split("-");
			accounts.put(new Account(t1[2], t1[3], Integer.parseInt(t1[5]), t1[0], t1[1], "132.0.0.0",t1[4]));
			
		}
		
	}
	public void backUpData(int region){//write to file
		Enumeration<List<Account>> e=accounts.table.elements();
		try {
			FileWriter fw=null;
			
			fw=new FileWriter("backup"+region +".txt",false);//true means it will
			//append the data
			PrintWriter print = new PrintWriter(fw);
			while (e.hasMoreElements()) {
				
				List<Account> list=(List<Account>)e.nextElement();
				
				for (int i = 0; i < list.size(); i++) {
					//temp+=list.get(i).username
					String newUserInfo=list.get(i).username+"-"+list.get(i).password+"-"+list.get(i).firstName+"-"+list.get(i).lastName+"-"+list.get(i).status+"-"+list.get(i).age;
					print.println(newUserInfo);
				}

			}
			
			print.close();
			fw.close();
		} catch (Exception fe) {
			fe.printStackTrace();
		}
	}
	
	private BufferedReader initializeBR(int region) throws FileNotFoundException{
		try {
			return new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream("backup"+region+".txt"))));
		}catch(FileNotFoundException fe){
			File file=new File("backup"+region+".txt");
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (Exception e) {
					fe.printStackTrace();				}
			}			return new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream("backup"+region+".txt"))));
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	private String getRegion(String ip){
		String prefix=ip.substring(0,ip.indexOf("."));
		if (prefix.equals("132")) {
			return "NA";
		}else if (prefix.equals("93")) {
			return "EU";
		}else if(prefix.equals("182")){
			return "AS";
		}
		return "";
	}
	
	public ServerImpl(int socket,int portinternal,int flag) throws NumberFormatException, IOException {
		
		initLogger();
		new GameServerReceiverThread(this, socket).start();
		//gsr.start();
		new UDPServerInternal(portinternal, this).start();
		if (flag==1) {
			int i;
			if (socket==1411) {
				i=1;
			}else if(socket==1412) {
				i=2;
			}else
				i=3;
			
			restoreData(i);
		}
		//new GameServerReceiverThread(this, socket).start();
	}
	private void initLogger() {
		logger = Logger.getLogger(location + "_server_logger");
		if (logger.getHandlers().length > 0)
			return;
		try {
			FileHandler fh;
			fh = new FileHandler(location + "_server.log", true);
			fh.setFormatter(new SimpleFormatter());
			logger.addHandler(fh);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public boolean connect(String ipAddress) {

		return false;
	}


	public String createPlayerAccount(String firstName, String lastName,
			int age, String username, String password, String ipAddress) {
		String successMessage = "Account creation successful.";
		String failUsernameUniqueMessage = "Account creation failed: username "
				+ username + " already exists.";
		String failUsernameLengthMessage = "Account creation failed: username "
				+ username + " must be between 6 and 15 characters.";
		String failPasswordLengthMessage = "Account creation failed: password "
				+ password + " must be more than 6 characters.";
		String message = successMessage;

		// create all conditions
		boolean isUsernameUnique = !accounts.contains(username);
		boolean isUsernameLengthOK = username.length() >= 6
				&& username.length() <= 15;
		boolean isPasswordLengthOK = password.length() >= 6;
		synchronized (accounts) {
			
		if (!isUsernameUnique)
			message = failUsernameUniqueMessage;
		else if (!isUsernameLengthOK)
			message = failUsernameLengthMessage;
		else if (!isPasswordLengthOK)
			message = failPasswordLengthMessage;
		else
			accounts.put(new Account(firstName, lastName, age, username,
					password, ipAddress,"false"));

		logger.info("IP: " + ipAddress + " attempted to create account.\n"+ message);
		}//synchronization ends
		boolean operationIsOK = isUsernameUnique && isUsernameLengthOK
				&& isPasswordLengthOK;
	
		if (operationIsOK) {
			return "true";
		}
		else
			return "false-0";
		
		}


	public String playerSignIn(String username, String password,
			String ipAddress) {
		username.trim();password.trim();ipAddress.trim();
		String successMessage = username + " successfully signed in.";
		String failUsernameMessage = "0 failed to sign in: username does not exist.";
		String failPasswordMessage = "2 failed to sign in: password is incorrect.";
		String failAlreadySignedMessage = "1 failed to sign in: already signed in.";

		String message = failUsernameMessage;
		boolean operationIsOK = false;
		
		Account account = accounts.get(username);
		synchronized (account) {
		if (account != null) {
			//operationIsOK=true;
			
			if (!account.password.equals(password)) {
				message = failPasswordMessage;
			} else if (account.isSignedIn()) {
				message = failAlreadySignedMessage;
			} else {
				account.signIn();
				operationIsOK = true;
				message = successMessage;
			}
		}
	}//synchronzation ends
		 logger.info("IP: " + ipAddress + " attempted to sign in .\n"+message);
		//return operationIsOK + "-" + message;
		 if (operationIsOK) {
			 
				return "true";
			}
			else{
				if(message.startsWith("0"))  {
					return "false-0";
				}else if (message.startsWith("1")) {
					return "false-1";
				}
				else {
					return "false-2";
				}
			}
	}

	public String showFailureTolerance(){
		return "true-failuretolerance";//this replica will give true reply to the leader
	}
	
	public String playerSignOut(String username, String ipAddress) {
		username.trim();
		String successMessage = username + " successfully signed out.";
		String failUsernameMessage = "0 failed to sign out: username does not exist.";
		String failAlreadySignedMessage = "1 failed to sign out: already signed out.";

		String message = failUsernameMessage;
		boolean operationIsOK = false;

		Account account = accounts.get(username);
		synchronized (account) {
		
		if (account != null) {
			if (!account.isSignedIn()) {
				message = failAlreadySignedMessage;
			} else {
				account.signOut();
				operationIsOK = true;
				message = successMessage;
			}
		}
	}//synchronization ends
		logger.info("IP: " + ipAddress + " attempted to sign out.\n"+ message);
		
		if (operationIsOK) {
			return "true";
		}
		else
			if (message.startsWith("0")) {
				return "false-0";
			}else
				return "false-1";
	}

	
	public String getPlayerStatus(String adminUsername, String adminPassword,
			String ipAddress) {
		if (!(adminUsername.equalsIgnoreCase("admin") && adminPassword.equalsIgnoreCase("admin"))) {
			return "false-- Wrong admin credentials.";
		} else {
			String resultEU=null,resultAS=null;
			String resultNA=null;
			DatagramSocket aSocket=null;
			
			if (ipAddress.startsWith("132")) {//na
				resultNA=getPlayerCount();
				resultEU=callUDP(aSocket, 1512, "call");
				resultAS=callUDP(aSocket, 1513, "call");
				
			}else if (ipAddress.startsWith("93")) {//eu
				resultEU=getPlayerCount();
				resultNA=callUDP(aSocket, 1511, "call");
				resultAS=callUDP(aSocket, 1513, "call");
				
			}else {//as
				resultAS=getPlayerCount();
				resultNA=callUDP(aSocket, 1511, "call");
				resultEU=callUDP(aSocket, 1512, "call");
				
			}
			
			return "getstatus-"+resultNA+"-"+resultEU+"-"+resultAS;
			
		}
		
	}
	public String getPlayerCount() {
		return accounts.getPlayerCount();
	}

	
	public String suspendAccount(String adminusername, String adminpassword,
			String ipaddress, String removeusername) {
		removeusername.trim();
		String successMessage = "Account creation successful.";
		String failUsernameUniqueMessage = "Account creation failed: username "
				+ removeusername + " already exists.";
		String failUsernameLengthMessage = "Account creation failed: username "
				+ removeusername + " must be between 6 and 15 characters.";
		String message = successMessage;
		
			
		// create all conditions
		boolean isUsernameUnique = accounts.contains(removeusername);
		boolean isUsernameLengthOK = removeusername.length() >= 6
				&& removeusername.length() <= 15;
		String value="";
		synchronized (accounts) {
		if (!isUsernameUnique)
			message = failUsernameUniqueMessage;
		else if (!isUsernameLengthOK)
			message = failUsernameLengthMessage;
		else
			value=accounts.remove(removeusername);
		}//synchronization ends
		logger.info("IP: " + ipaddress + " attempted to suspend account.\n"+message);

		boolean operationIsOK = isUsernameUnique && isUsernameLengthOK;
		
		//return value;
		if (operationIsOK) {
			return "true";
		}
		else
			return "false-0";
		
	}
	
	public String transferAccount(String userName, String password,
			String oldipApp, String newiAdd) {
		
		
		DatagramSocket socket=null;
		String fullUser=null;
		int port=0;
		if (newiAdd.startsWith("132")) {
			port=1511;
		}else if (newiAdd.startsWith("93")) {
			port=1512;
		}else {
			port=1513;
		}
		
		String oldRegion=getRegion(oldipApp);
		String newRegion=getRegion(newiAdd);
		if (oldRegion.endsWith(newRegion)) {
			return "false-0";
		}
		synchronized (accounts) {
			
		
		if (accounts.contains(userName)) {
			Account account = accounts.get(userName);
			
			
			
			fullUser=account.firstName+"_"+account.lastName+"_"+account.age+"_"+account.username+"_"+account.password;
			String retvalue=callUDP(socket, 1512, fullUser);
			if (retvalue.contains("true")) {
				String ans=suspendAccount("admin", "admin", oldipApp, userName);
				//return "transfer done!";
				return "true";
			}
			else
			{	//return "unm exists in new server so cant transfer";
				return "false-1";
			}
		
		}else 
		{	//return "wrong username";
			return "false-2";
		}
		}//syn ends

	}
	public String callUDP(DatagramSocket aSocket, int serverPort, String msg) {
		try {
			aSocket = new DatagramSocket();
			byte[] m=new byte[64000];
			m = msg.getBytes();
			InetAddress aHost = InetAddress.getByName("localhost");

			DatagramPacket request = new DatagramPacket(m, m.length, aHost,
					serverPort);
			aSocket.send(request);
			byte[] buffer = new byte[64000];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			aSocket.receive(reply);
				
			if (msg.equals("call")) {
				
				return new String(reply.getData(),0,22).trim();
			}
			return new String(reply.getData());
			
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
