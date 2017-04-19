package com.server;

import Requests.requestInterfacePOA;
//this class will start new thread for each client request
public class RequestImpl extends requestInterfacePOA {

	public static int sequence = 1;//this will be the sequence no for each request
	private String udpmsg;

	//format each parameter & start new request thread for each request
	@Override
	public String createPlayerAccount(String firstName, String lastName,
			int age, String userName, String password, String ipAdd) {

		udpmsg = sequence++ + "-" + "create" + "-" + ipAdd + "-" + firstName
				+ "_" + lastName + "_" + age + "_" + userName + "_" + password;
		UDPSenderforLeader udp=new UDPSenderforLeader();
		udp.msg=udpmsg;
		udp.start();
		
		return "create";
	}

	@Override
	public String processSignIn(String userName, String password, String ipAdd) {
		udpmsg = sequence++ + "-" + "signin" + "-" + ipAdd + "-"
				+ userName + "_" + password;
		UDPSenderforLeader udp=new UDPSenderforLeader();
		udp.msg=udpmsg;
		udp.start();
		return "sign in";
	}

	@Override
	public String processSignOut(String userName, String ipAdd) {
		udpmsg = sequence++ + "-" + "signout" + "-" + ipAdd + "-"
				+ userName ;
		UDPSenderforLeader udp=new UDPSenderforLeader();
		udp.msg=udpmsg;
		udp.start();
		
		return "sign out";
	}

	@Override
	public String getPlayerStatus(String ipAdd) {		
		udpmsg=sequence++ +"-"+ "getstatus"+"-"+ ipAdd + "-" + "admin_admin";
		UDPSenderforLeader udp=new UDPSenderforLeader();
		udp.msg=udpmsg;
		udp.start();
		return "getstatus";
	}

	@Override
	public String suspendAccount(String adminUnm, String adminPwd,
			String ipAdd, String username) {
		udpmsg = sequence++ + "-" + "suspend" + "-" + ipAdd + "-"
				+ adminUnm+"_"+adminPwd+"_"+username ;
		UDPSenderforLeader udp=new UDPSenderforLeader();
		udp.msg=udpmsg;
		udp.start();
		
		return "suspend";
	}

	@Override
	public String transferAccount(String userName, String password,
			String oldipApp, String newiAdd) {
		udpmsg=sequence++ + "-" + "transfer" + "-" + oldipApp+"-"+userName+"_"+password+"_"+newiAdd;
		UDPSenderforLeader udp=new UDPSenderforLeader();
		udp.msg=udpmsg;
		udp.start();
		return "getstatus";
	}

	@Override
	public String showFailureTolerance() {
		udpmsg=sequence++ + "-" + "failuretolerance"+"-"+"132.0.0.0"+"-"+"show";
		UDPSenderforLeader udp=new UDPSenderforLeader();
		udp.msg=udpmsg;
		udp.start();
		return "";
	}	
}
