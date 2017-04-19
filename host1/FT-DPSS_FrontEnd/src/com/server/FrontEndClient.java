package com.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

import org.omg.CORBA.ORB;

import Requests.requestInterface;
import Requests.requestInterfaceHelper;

public class FrontEndClient {
	
	public String username,password,firstName,lastName,ipAdd;
	public int age;
	public boolean status=false;
	
	
	private static void showMenu() {
		System.out.println("1. Create a new account");
		System.out.println("2. Sign in");
		System.out.println("3. Sign out");
		System.out.println("4. Transfer to other server");
		System.out.println("5. Suspend");
		System.out.println("6. Get Player Status");
		System.out.println("7. Multithreading");
		System.out.println("8. Show failure tolerance");
		System.out.println("9. Exit");
		
	}

	public static void main(String[] args) throws IOException {
		//get the corba remote object from ior file
		ORB orb=ORB.init(args,null);
		BufferedReader br=new BufferedReader(new FileReader("ior.txt"));
		String ior=br.readLine();
		br.close();

		org.omg.CORBA.Object obj= orb.string_to_object(ior);
		requestInterface server=requestInterfaceHelper.narrow(obj);
		Scanner keyboard=new Scanner(System.in);
		
		String get="y";
		while(get.equalsIgnoreCase("y")){
		System.out.println("Enter your region\n1-NorthAmerica\n2-Europe\n3-Asia");
		int region=keyboard.nextInt();
		
		showMenu();
		
		int i=keyboard.nextInt();
		if (i==1) {
			new FrontEndClient().createPlayerAccount(server,region);
			
		}else if (i==2) {
			new FrontEndClient().signIn(server,region);
			
		}else if (i==3) {
			new FrontEndClient().signOut(server,region);
		
		}else if (i==4) {
			new FrontEndClient().transerAccount(server, region);
		
		}
		else if (i==5) {
			new FrontEndClient().suspendAccount(server,region);
		}
		else if (i==6) {
			new FrontEndClient().getPlayerStatus(server,region);
		}else if (i==7) {
			new FrontEndClient().doMulti(server,region);
		}else if (i==8) {
			new FrontEndClient().showFailureTolerance(server,region);
		}
		
		else break;
		
		System.out.println("Do you wish to continue ? y/n");
		get = keyboard.next();
		
		}
	}
	
	//this method is for showing failure tolerance mechanism
	private void showFailureTolerance(requestInterface server, int region) {
		server.showFailureTolerance();
	}

	//passing static data to show simultaneous multiple requests
	private void doMulti(requestInterface server, int region) {
		server.createPlayerAccount("soyoung", "kim", 12, "soyoung123", "soyoung", "132.2.2.2");
		server.processSignOut("soyoung123", "132.0.5.0");
		server.processSignIn("soyoung123", "soyoung", "132.0.0.0");
		server.processSignOut("soyoung123", "132.0.5.0");
		server.createPlayerAccount("soyoung", "kim", 12, "soyoung123", "soyoung", "182.2.2.2");
		server.processSignOut("soyoung123", "182.0.5.0");
	}

	private void getPlayerStatus(requestInterface server,int region) {
		System.out.println("Enter admin Username");
		Scanner keyboard=new Scanner(System.in);
		String admunm=keyboard.next();
		while (!(admunm.equals("admin"))) {
			System.out.println("wrong username,enter again");
			admunm=keyboard.next();
		}
		System.out.println("Enter admin Password");
		String admpwd=keyboard.next();
		while (!(admpwd.equals("admin"))) {
			System.out.println("wrong password,enter again");
			admpwd=keyboard.next();
		}
		String ans=server.getPlayerStatus(generateIp(region));
		
	}

	private void suspendAccount(requestInterface server,int region) {
		System.out.println("Enter admin Username");
		Scanner keyboard=new Scanner(System.in);
		String admunm=keyboard.next();
		System.out.println("Enter admin Password");
		String admpwd=keyboard.next();
		System.out.println("Enter username to suspend");
		String unmtosus=keyboard.next();
		
		String ans=server.suspendAccount(admunm, admpwd, generateIp(region), unmtosus);
	}

	private void transerAccount(requestInterface server, int region) {
		System.out.println("Enter ur Username");
		Scanner keyboard=new Scanner(System.in);
		String unm=keyboard.next();
		System.out.println("Enter ur Password");
		String pwd=keyboard.next();
		System.out.println("To which region u wanna move??\n1-NorthAmerica\n2-Europe\n3-Asia");
		int newRegion=keyboard.nextInt();
		String replyFromServer=server.transferAccount(unm, pwd, generateIp(region), generateIp(newRegion));
	}
	
	private void signOut(requestInterface server,int region) {
		Scanner keyboard=new Scanner(System.in);
		System.out.println("enter ur username");
		String userName = keyboard.next();
		
		String replyFromServer=server.processSignOut(userName, generateIp(region));
		
	}
	private void signIn(requestInterface server,int region) {
		Scanner keyboard=new Scanner(System.in);
		System.out.println("enter ur username");
		String userName = keyboard.next();
		System.out.println("enter ur password");
		String password = keyboard.next();
		String replyFromServer;
		replyFromServer=server.processSignIn(userName, password, generateIp(region));
		
	}
	private void createPlayerAccount(requestInterface server,int region){
		Scanner keyboard=new Scanner(System.in);
		System.out.println("enter ur username between 6 to 15 characters");
		String unm = keyboard.next();
		while (!(unm.length() >= 6 && unm.length() <= 15)) {
			System.out
					.println("Username should be between 6 to 15 characters");
			unm = keyboard.next();
		}
		this.username = unm;
		
		System.out.println("Enter password at least 6 characters long");
		String pwd = keyboard.next();
		while (!(pwd.length() >= 6)) {
			System.out.println("Password should be at least 6 characters long");
			pwd = keyboard.next();
		}
		this.password = pwd;
		System.out.println("Enter ur age");
		this.age=keyboard.nextInt();
		
		System.out.println("Enter firstname");
		this.firstName=keyboard.next();
		System.out.println("Enter lastname");
		this.lastName=keyboard.next();
		
		String replyFromServer;
		
		replyFromServer=server.createPlayerAccount(firstName, lastName, age, username, password, generateIp(region));
	}

	private String generateIp(int region){
		String ip = "";
		if (region==1){
			ip = "132";
		}else if (region==2){
			ip = "93";
		}else if (region==3){
			ip = "182";
		}
		for (int i = 0; i < 3; i++) {
			ip = ip + "." + new Random().nextInt(255);
		}
		return ip.substring(0, ip.length()-1);
		
	}
	
}
