package com.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class PlayerClient {
	public String location;
	public String ipAddress;
	private Logger logger;
	private Scanner scanner;
	
	
		public PlayerClient(String location, int prefix) {
			this.location = location;
			ipAddress = generateIp(prefix);
			scanner = new Scanner(System.in);
		}

		
		private String generateIp(int prefix) {
			String ip = "";
			if (prefix==1){
				ip = "132";
			}else if (prefix==2){
				ip = "93";
			}else if (prefix==3){
				ip = "182";
			}
			for (int i = 0; i < 3; i++) {
				ip = ip + "." + new Random().nextInt(255);
			}
			return ip.substring(0, ip.length()-1);
		}

}
