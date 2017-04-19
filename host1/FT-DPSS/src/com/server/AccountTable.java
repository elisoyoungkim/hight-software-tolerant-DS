package com.server;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class AccountTable {
	public Hashtable<String, List<Account>> table = new Hashtable<>();

	public boolean contains(String username) {
		String firstChar = Character.toString(username.charAt(0));
		List<Account> list = table.get(firstChar);

		if (list == null)
			return false;

		for (Account a : list)
			if (a.username.equals(username))
				return true;

		return false;
	}

	public Account get(String username) {
		String firstChar = Character.toString(username.charAt(0));
		List<Account> list = table.get(firstChar);
		if (list != null)
			for (Account a : list)
				if (a.username.equals(username))
					return a;
		return null;
	}
	public String remove(String username){
		String firstChar = Character.toString(username.charAt(0));
		List<Account> list = table.get(firstChar);
		table.get(firstChar).size();
		int retrunvalue=0;
		if (table.get(firstChar).size()>0) {
	
			for (int i = 0; i < table.get(firstChar).size(); i++) {
				if (username.equalsIgnoreCase(table.get(firstChar).get(i).username)) {
					list.remove(i);
					retrunvalue=1;
					break;
				}
			}
		}
		if (retrunvalue==0) {
			return "some error";
		}else
			return "removed successfully";
	}
	
	public void put(Account account) {
		String firstChar = Character.toString(account.username.charAt(0));
		List<Account> list = table.get(firstChar);

		if (list == null) {
			list = new ArrayList<>();
			table.put(firstChar, list);
		}

		list.add(account);
	}

	// return format is "online,offline"
	public String getPlayerCount() {
		int onlineCount = 0;
		int offlineCount = 0;

		for (List<Account> list : table.values())
			for (Account account : list)
				if (account.isSignedIn())
					onlineCount++;
				else
					offlineCount++;
		
		return Integer.toString(onlineCount) + "-" +Integer.toString(offlineCount);
	}
}
