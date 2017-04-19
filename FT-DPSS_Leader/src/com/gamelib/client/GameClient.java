package com.gamelib.client;

import java.io.IOException;
import java.util.Scanner;

import org.omg.CORBA.StringHolder;

import com.gamelib.helpers.MethodHelper;
import com.gamelib.server.GameServerImplementation;


public class GameClient {

	public String _username, _password, _firstname, _lastname, _ipaddress;
	public int _age;
	public boolean _signinstatus = false;

	GameServerImplementation iServer_NA, iServer_EU, iServer_AS;

	/**
	 * @param args
	 * @throws IOException
	 */
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		new GameClient().initServers();
		System.out.println("Game Server is running");
	}

	/*
	public String createPlayerAccount(String ipaddress, String[] params) {

		// TODO Auto-generated method stub
		this._firstname = params[0];
		this._lastname = params[1];
		this._age = Integer.parseInt(params[2]);
		this._username = params[3];
		this._password = params[4];
		this._ipaddress = ipaddress;

		System.out.println("found all data");
		
		MethodHelper mH = new MethodHelper();
		String region = mH.fetchRegion(ipaddress);
		
		String reply = "";
		
		if (region.equalsIgnoreCase("NA")) { // NA
			reply = iServer_NA.createPlayerAccount(this._firstname, this._lastname,
					this._age, this._username, this._password, this._ipaddress);
			
		} else if (region.equalsIgnoreCase("AS")) { // EU
			reply = iServer_EU.createPlayerAccount(this._firstname, this._lastname,
					this._age, this._username, this._password, this._ipaddress);
			
		} else if (region.equalsIgnoreCase("EU")) { // AS
			reply = iServer_AS.createPlayerAccount(this._firstname, this._lastname,
					this._age, this._username, this._password, this._ipaddress);
			
		}
		
		return reply;

	}

	private void singIn(int i) {
		// TODO Auto-generated method stub
		Scanner in = new Scanner(System.in);
		System.out.println("Enter Username :");
		String tempString = in.next();
		while (!(tempString.length() >= 6 && tempString.length() <= 15)) {
			System.out
					.println("Username is not proper. Enter Username (between 6 to 15 characters) :");
			tempString = in.next();
		}
		this._username = tempString;
		System.out.println("Enter Password :");
		tempString = in.next();
		while (!(tempString.length() >= 6)) {
			System.out
					.println("Password is not proper. Enter Password (atleast 6 characters) :");
			tempString = in.next();
		}
		this._password = tempString;

		String ipaddress = new MethodHelper().createIp(i);
		this._ipaddress = ipaddress;

		StringHolder reply = new StringHolder();
		if (i == 1) { // NA
			iServer_NA.playerSignIn(this._username, this._password,
					this._ipaddress);
			reply.value = "NA :- " + reply.value;
		} else if (i == 2) { // EU
			iServer_EU.playerSignIn(this._username, this._password,
					this._ipaddress, reply);
			reply.value = "EU :- " + reply.value;
		} else if (i == 3) { // AS
			iServer_AS.playerSignIn(this._username, this._password,
					this._ipaddress, reply);
			reply.value = "AS :- " + reply.value;
		}
		System.out.println(reply.value);
	}

	private void signOut(int i) {
		// TODO Auto-generated method stub
		Scanner in = new Scanner(System.in);
		System.out.println("Enter Username :");
		String tempString = in.next();
		while (!(tempString.length() >= 6 && tempString.length() <= 15)) {
			System.out
					.println("Username is not proper. Enter Username (between 6 to 15 characters) :");
			tempString = in.next();
		}
		this._username = tempString;

		String ipaddress = new MethodHelper().createIp(i);
		this._ipaddress = ipaddress;

		StringHolder reply = new StringHolder();
		if (i == 1) { // NA
			iServer_NA.playerSignOut(this._username, this._ipaddress, reply);
			reply.value = "NA :- " + reply.value;
		} else if (i == 2) { // EU
			iServer_EU.playerSignOut(this._username, this._ipaddress, reply);
			reply.value = "EU :- " + reply.value;
		} else if (i == 3) { // AS
			iServer_AS.playerSignOut(this._username, this._ipaddress, reply);
			reply.value = "AS :- " + reply.value;
		}
		System.out.println(reply.value);
	}

	private void transferAccount(int i) {

		// TODO Auto-generated method stub
		Scanner in = new Scanner(System.in);
		String oldipaddress = new MethodHelper().createIp(i);
		
		
		System.out.println("Enter Username (between 6 to 15 characters) :");
		String username = in.next();
		while (!(username.length() >= 6 && username.length() <= 15)) {
			System.out
					.println("Username is not proper. Enter Username (between 6 to 15 characters) :");
			username = in.next();
		}
		
		System.out.println("Enter Password (atleast 6 characters) :");
		String password = in.next();
		while (!(password.length() >= 6)) {
			System.out
					.println("Password is not proper. Enter Password (atleast 6 characters) :");
			password = in.next();
		}
		
		System.out.println("==== Choose region you want to transfer ====\n1. NA\n2. EU\n3. AS");
		int y = in.nextInt();
		String newipaddress = new MethodHelper().createIp(y);
		
		
		StringHolder reply = new StringHolder();
		if (i == 1) { // NA
			iServer_NA.transferAccount(username, password, oldipaddress, newipaddress, reply);
			reply.value = "NA :- " + reply.value;
		} else if (i == 2) { // EU
			iServer_EU.transferAccount(username, password, oldipaddress, newipaddress, reply);
			reply.value = "EU :- " + reply.value;
		} else if (i == 3) { // AS
			iServer_AS.transferAccount(username, password, oldipaddress, newipaddress, reply);
			reply.value = "AS :- " + reply.value;
		}
		System.out.println(reply.value);

	}

*/
	public void initServers(){
		iServer_NA = new GameServerImplementation(1101);
		iServer_EU = new GameServerImplementation(1102);
		iServer_AS = new GameServerImplementation(1103);
		
		System.out.println("Servers initialized");
	}
}
