package com.gamelib.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import org.omg.CORBA.StringHolder;

import com.gamelib.client.GameClient;
import com.gamelib.helpers.LogHelper;
import com.gamelib.helpers.MethodHelper;


public class GameServerImplementation {
	
	public Hashtable<String, ArrayList<GameClient>> hashData = new Hashtable<String, ArrayList<GameClient>>();
	
	GameClient _gClient;
	UDPServer _udpServer;
	
	public GameServerImplementation(int port){
		_udpServer = new UDPServer(port,this);
		_udpServer.start();
	}
	
	public void checkMethod(String param1, StringHolder reply) {
		// TODO Auto-generated method stub
		reply.value = "from check method";
	}
	
	// create account request
	
	public String createPlayerAccount(String firstname, String lastname, int age,
			String username, String password, String ipaddress) {

		boolean _flag = true;
		
		GameClient _gc = new GameClient();
		_gc._firstname = firstname;
		_gc._lastname = lastname;
		_gc._age = age;
		_gc._username = username;
		_gc._password = password;
		_gc._ipaddress = ipaddress;
		
		String _geolocation = new MethodHelper().fetchRegion(ipaddress);
		
		String _filename = _geolocation + "-server.log";
		
		String key = username.substring(0,1); // Check it user in registered or not
		if (hashData.containsKey(key)){
			if (hashData.get(key).size() > 0){
				for (int j = 0; j < hashData.get(key).size(); j++) {
					if (username.equalsIgnoreCase(hashData.get(key).get(j)._username)){
						_flag = false;
						break;
					}
				}
			}
		}
		
		ArrayList<GameClient> _list = new ArrayList<GameClient>();
		
		if (_flag){ // if not registered then add user
			LogHelper.createLog(0, _filename,
					"Create account is requested from " + _gc._ipaddress);
			_gc._signinstatus = false;
			synchronized (hashData) { // put synchronized block on hashData
				if (hashData.containsKey(key)){
					for (int j = 0; j < hashData.get(key).size(); j++) {
						_list.add(hashData.get(key).get(j));
					}
				}
				
				_list.add(_gc);
				hashData.put(key, _list);
				
				String clientfilename = _gc._username + "-" + _geolocation
						+ "-client.log";
				LogHelper.createLog(2, clientfilename,
						"Account successfully created. ");

				LogHelper.createLog(0, _filename,
						"Account successfully created with username : "
								+ _gc._username);
				
				return "true-User with username " + username + " is added !";

			}
		}else{ // if registered then return with proper error message
			LogHelper.createLog(0, _filename,
					"Create account is requested from " + _gc._ipaddress);
			LogHelper.createLog(0, _filename, "Account is not created");

			return "false-0-User not added";
		}
	}

	// playerSignIn request
	
	public String playerSignIn(String username, String password,
			String ipaddress) {
		// TODO Auto-generated method stub
		_gClient = new GameClient();
		String _geolocation = new MethodHelper().fetchRegion(ipaddress);
		
		String _filename = _geolocation + "-server.log";
		LogHelper.createLog(0, _filename, "Sign in request from " + ipaddress
				+ " with username : " + username);
		
		int _status = 0;
		String _key = username.substring(0,1);
		if (hashData.containsKey(_key)){
			for (int i = 0; i < hashData.get(_key).size(); i++) {
				GameClient tempGc= hashData.get(_key).get(i);
				if (username.equalsIgnoreCase(tempGc._username)){ // if username and password match with given username and password
					if (password.equalsIgnoreCase(tempGc._password)){
						_gClient = tempGc;
						synchronized (hashData) { // put synchronized block on hashData
							if (!tempGc._signinstatus) { //sign in - make sign in status true
								hashData.get(_key).get(i)._signinstatus = true;
								_status = 100;
								break;
							}else{ // already sign in if already sign in then give error message
								_status = 101;
								break;
							}
						}
					}else{ // if password is not valid then give error message
						_status = 102; // invalid password
					}	
				}else{ // if username is not valid then give error message
					_status = 0; //invalid username
				}
			}
		}
		
		// create log files based on status
		if (_status == 0) {
			LogHelper.createLog(0, _filename, "Username not found !");
			return "false-0-User not found !";
		} else if (_status == 100) {

			String clientfilename = _gClient._username + "-" + _geolocation
					+ "-client.log";
			LogHelper.createLog(2, clientfilename, "Sign in successfully");

			LogHelper.createLog(0, _filename, "Sign in successfully");

			return "true-You Are Signed In";
		} else if (_status == 101) {
			String clientfilename = _gClient._username + "-" + _geolocation
					+ "-client.log";
			LogHelper.createLog(2, clientfilename, "Already Sign In");

			LogHelper.createLog(0, _filename, "Already Sign In");

			return "false-1-Already Signed In !";
		} else if (_status == 102) {
			String clientfilename = _gClient._username + "-" + _geolocation
					+ "-client.log";
			LogHelper.createLog(2, clientfilename, "Invalid Password");

			LogHelper.createLog(0, _filename, "Invalid Password");

			return "false-2-Invalid Password";
		}
		return "false-404-Anonymous error sign in";
	}

	// playerSignOut request
	public String playerSignOut(String username, String ipaddress) {
		// TODO Auto-generated method stub
		String _geolocation = new MethodHelper().fetchRegion(ipaddress);
		
		String _filename = _geolocation + "-server.log";
		LogHelper.createLog(0, _filename, "Sign out request from " + ipaddress
				+ " with username : " + username);
		
		int _status = 0;
		String _key = username.substring(0,1);
		if (hashData.containsKey(_key)){
			for (int i = 0; i < hashData.get(_key).size(); i++) {
				GameClient tempGc= hashData.get(_key).get(i);
				if (username.equalsIgnoreCase(tempGc._username)){ // check if username found
						_gClient = tempGc;
						synchronized (hashData) { // put synchronized block on hashData
							if (tempGc._signinstatus) { //sign out // check if user is sign in then make it false
								hashData.get(_key).get(i)._signinstatus = false;
								_status = 100;
								break;
							}else{ // already sign out
								_status = 101;
								break;
							}
						}
					
				}else{
					_status = 103; //invalid username
				}
			}
		}
		
		//create log files based on status
		if (_status == 0) {
			LogHelper.createLog(0, _filename, "Username not found !");
			return "false-0-Invalid Username";
		} else if (_status == 100) {
			if (_gClient != null) {
				String clientfilename = _gClient._username + "-" + _geolocation
						+ "-client.log";
				LogHelper.createLog(2, clientfilename, "Sign Out successfully");

				LogHelper.createLog(0, _filename, "Sign Out successfully");

				return "true-You Are Signed Out";				
			}
		} else if (_status == 101) {
			if (_gClient != null) {
				String clientfilename = _gClient._username + "-" + _geolocation
						+ "-client.log";
				LogHelper.createLog(2, clientfilename, "Already Sign Out");

				LogHelper.createLog(0, _filename, "Already Sign Out");

				return "false-1-Already Signed Out !";				
			}
		} else if (_status == 103) {
			LogHelper.createLog(0, _filename, "Invalid Username !");
			return "false-2-Invalid Username !";
		}
		
		return "false-404-Anonymous error in sign out";
	}

	//getPlayerStatus request
	public String getPlayerStatus(String username,String password, String ipaddress) {
		// TODO Auto-generated method stub
		String _geolocation = new MethodHelper().fetchRegion(ipaddress);
		
		String _filename = _geolocation + "-server.log";
		LogHelper.createLog(0, _filename, "getPlayerStatus request from " + ipaddress
				+ " with username : " + username);
 		
		String _result = checkStatus(); // calculate local status
		
		// find region and based on that get status through UDP from particular server
		
		if (_geolocation.equalsIgnoreCase("na")) {
			String _resultEU = getReplyFromUDP("getplayerstatus",1102);
			String _resultAS = getReplyFromUDP("getplayerstatus",1103);
			_result = _result + _resultEU + _resultAS;
		}else if (_geolocation.equalsIgnoreCase("eu")) {
			String _resultAS = getReplyFromUDP("getplayerstatus",1103);
			String _resultNA = getReplyFromUDP("getplayerstatus",1101);
			_result = _resultNA +  _result + _resultAS ;
		}else if (_geolocation.equalsIgnoreCase("as")) {
			String _resultNA = getReplyFromUDP("getplayerstatus",1101);
			String _resultEU = getReplyFromUDP("getplayerstatus",1102);
			_result = _resultNA + _resultEU +_result;
		}	
		LogHelper.createLog(1, "admin.log", "From : " + _geolocation
				+ " region, result:" + _result);
		LogHelper.createLog(0, _filename, "result:" + _result);
		return "true-" + _result;
	}

	// method to calculate status from local hashtable
	public String checkStatus(){
		int online = 0, offline = 0;

		Enumeration<ArrayList<GameClient>> e = hashData.elements();

		while (e.hasMoreElements()) {
			ArrayList<GameClient> ar = (ArrayList<GameClient>) e
					.nextElement();
			for (int i = 0; i < ar.size(); i++) {
				if (ar.get(i)._signinstatus) {
					online++;
				} else {
					offline++;
				}
			}
		}

		return online + "-" + offline + "-";
	}
	
	// dynamic method to request another server through UDP

	public String getReplyFromUDP(String requestfor, int port){
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket();
			
			byte[] m = requestfor.getBytes();
			InetAddress aHost = InetAddress.getByName("localhost");
			int serverPort = port;

			DatagramPacket request = new DatagramPacket(m,
					requestfor.length(), aHost, serverPort);
			aSocket.setBroadcast(true);
			aSocket.setSendBufferSize(10000);
			aSocket.send(request);

			byte[] buffer = new byte[10000];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			aSocket.receive(reply);
			
			return new String(reply.getData(),0,reply.getLength());
		} catch (SocketException e) {
			// TODO: handle exception\
			System.out.println("Error in SocketException : " + e.getMessage());
		} catch (IOException e) {
			// TODO: handle exception
			System.out.println("Error in IOException : " + e.getMessage());
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error in Exception : " + e.getMessage());
		} finally {

			if (aSocket != null)
				aSocket.close();

		}
		
		return null;
	}

	// suspend account request
	public String suspendAccount(String adminusername, String adminpassword,
			String adminipaddress, String usernametosuspend) {
		// TODO Auto-generated method stub
		
		
		String _geolocation = new MethodHelper().fetchRegion(adminipaddress);
		
		String _filename = _geolocation + "-server.log";
		String _stringToLog = "suspendAccount request from " + adminipaddress
				+ " with username : " + adminusername + " for user with username : " + usernametosuspend;
		LogHelper.createLog(0, _filename, _stringToLog);
		
		int _status = 0;
		String _key = usernametosuspend.substring(0,1);
		if (hashData.containsKey(_key)){
			for (int i = 0; i < hashData.get(_key).size(); i++) {
				GameClient tempGc= hashData.get(_key).get(i);
				if (usernametosuspend.equalsIgnoreCase(tempGc._username)){ // if username is found then remove
						_gClient = tempGc;
						synchronized (hashData) { // put synchronized block on hashData
							_status = 100; //suspended 
							hashData.get(_key).remove(i);
							break;
						}
						
				}else{
					_status = 102; //invalid username
				}
			}
		}
		
		// return proper message based on status and create log for each status
		if (_status == 0) {
			LogHelper.createLog(1, "admin.log", "Username not found !");
			return "false-0-User not found !";
		}else if (_status == 100) {
			String clientfilename = _gClient._username + "-" + _geolocation
					+ "-client.log";
			LogHelper.createLog(2, clientfilename, "Account suspended with username : " + usernametosuspend);
			LogHelper.createLog(1, "admin.log", "Account suspended with username : " + usernametosuspend);
			LogHelper.createLog(0, _filename, "Account suspended with username : " + usernametosuspend);

			return "true-Account suspended";
		} else if (_status == 102) {
			LogHelper.createLog(1, "admin.log", "Username not found !");
			LogHelper.createLog(0, _filename, "Username not found !");

			return "false-1-Username not found !";
		}
		
		return "false-404-Anonymous error in suspend account";
	}

	// transferAccount request
	
	public String transferAccount(String username, String password,
			String oldipaddress, String newipaddress) {
		// TODO Auto-generated method stub
		String _geolocation = new MethodHelper().fetchRegion(oldipaddress);
		String _filename = _geolocation + "-server.log";
		LogHelper.createLog(0, _filename, "transferAccount request from " + _geolocation
				+ " ( " + oldipaddress + " ) with username : " + username);
	
		synchronized (username) { // put syncronizatino on username 
			if (isUserAvailable(username).equalsIgnoreCase("true")){ // go further if user is available on current region
				
				
				String _newgeolocation = new MethodHelper().fetchRegion(newipaddress);
				String clientfilename = username + "-" + _geolocation
						+ "-client.log";
				String LogMsg = "";
				String replyFromUDP = "";
				
				if (_newgeolocation.equalsIgnoreCase(_geolocation)) { // check if user wants to transfer in same region
					return "false-0-Invalid request ! You can't get transfer within same reqion";
				}else if (_newgeolocation.equalsIgnoreCase("eu")) { // transfer that user to another region via UDP, suspend and then create proper log
					String _strToPass = getAllUserDetailsInOneString(username, newipaddress,oldipaddress);
					
					replyFromUDP = getReplyFromUDP(_strToPass,1102);
					if (replyFromUDP.equalsIgnoreCase("can not transfer")){
						LogMsg = "Can't transfer because that server has same user with same username";
						
						LogHelper.createLog(0, _filename, LogMsg);
						LogHelper.createLog(2, clientfilename, LogMsg);
						return "false-1-Can't transfer because that server has same user with same username";
					}else{
						suspendAccount("admin", "admin", oldipaddress, username);
						LogMsg = "account transferred to " + _newgeolocation
								+ " ( " + newipaddress + " ) with username : " + username;
						
						LogHelper.createLog(0, _filename, LogMsg);
						LogHelper.createLog(2, clientfilename, LogMsg);
						
						return "true-" + LogMsg;
					}

				}else if (_newgeolocation.equalsIgnoreCase("as")) { // transfer that user to another region via UDP, suspend and then create proper log
					String _strToPass = getAllUserDetailsInOneString(username, newipaddress,oldipaddress);
					
					replyFromUDP = getReplyFromUDP(_strToPass,1103);
					if (replyFromUDP.equalsIgnoreCase("can not transfer")){
						LogMsg = "Can't transfer because that server has same user with same username";
						
						LogHelper.createLog(0, _filename, LogMsg);
						LogHelper.createLog(2, clientfilename, LogMsg);
						return "false-1-Can't transfer because that server has same user with same username";
					}else{
						suspendAccount("admin", "admin", oldipaddress, username);
						
						LogMsg = "account transferred to " + _newgeolocation
								+ " ( " + newipaddress + " ) with username : " + username;
						
						LogHelper.createLog(0, _filename, LogMsg);
						LogHelper.createLog(2, clientfilename, LogMsg);
						return "true-" + LogMsg;
					}

				}else if (_newgeolocation.equalsIgnoreCase("na")) { // transfer that user to another region via UDP, suspend and then create proper log
					String _strToPass = getAllUserDetailsInOneString(username, newipaddress,oldipaddress);
					
					replyFromUDP = getReplyFromUDP(_strToPass,1101);
					if (replyFromUDP.equalsIgnoreCase("can not transfer")){
						LogMsg = "Can't transfer because that server has same user with same username";
						
						LogHelper.createLog(0, _filename, LogMsg);
						LogHelper.createLog(2, clientfilename, LogMsg);
						return "false-1-Can't transfer because that server has same user with same username";
					}else{
						
						suspendAccount("admin", "admin", oldipaddress, username);
						
						LogMsg = "account transferred to " + _newgeolocation
								+ " ( " + newipaddress + " ) with username : " + username;
						
						LogHelper.createLog(0, _filename, LogMsg);
						LogHelper.createLog(2, clientfilename, LogMsg);
						
						return "true-" + LogMsg;
					}

				}
			}else{
				LogHelper.createLog(0, _filename, "Username not found !");
				return "false-1-Invalid Username";
			}
		}
		
		
		return "false-404-Anonymous error in transfer account";
	}

	// method to show failure tolerance
	public String showFailureTolerance() {
		return "true-failuretolerance";
	}
	
	// checks if user is available or not
	public String isUserAvailable(String username){
		
		String key = username.substring(0,1);
		if (hashData.containsKey(key)){
			if (hashData.get(key).size() > 0){
				for (int j = 0; j < hashData.get(key).size(); j++) {
					if (username.equalsIgnoreCase(hashData.get(key).get(j)._username)){
						return "true";
					}
				}
			}
		}
		return "false";
	}

	// give all user data in one string ( used for transferring account from one region to another )
	
	public String getAllUserDetailsInOneString(String username, String newipaddress,String oldipaddress){
		String _result = "";
		String key = username.substring(0,1);
		if (hashData.containsKey(key)){
			if (hashData.get(key).size() > 0){
				for (int j = 0; j < hashData.get(key).size(); j++) {
					if (username.equalsIgnoreCase(hashData.get(key).get(j)._username)){
						_result = _result + hashData.get(key).get(j)._firstname + "-";	//0
						_result = _result + hashData.get(key).get(j)._lastname + "-"; 	//1
						_result = _result + hashData.get(key).get(j)._age + "-";		//2
						_result =_result + hashData.get(key).get(j)._username + "-";	//3
						_result = _result + hashData.get(key).get(j)._password + "-";	//4
						_result = _result + newipaddress;								//6
						return _result;
					}
				}
			}
		}
		return "no detail found";
	}

}
