package com.gamelib.helpers;


import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.gamelib.server.GameServer;

public class LogHelper {

	// createLog Method
	// it will store log file as per log type
	// Log Type : 0 = Server, 1 = AdminClient, 2 = Client
	public static void createLog(int logType,String filename, String logInfo) {
		
		
		if (logType == 0){
			filename = "log/server/" + filename;
		}else if (logType == 1){
			filename = "log/adminclient/" + filename;
		}else if (logType == 2){
			filename = "log/client/" + filename;
		}
		Logger logger = Logger.getLogger(GameServer.class.getName());
		
		logger.setUseParentHandlers(false); // true to print in console
		FileHandler fh = null;
		try {
			File f = new File(filename);
			if (f.exists()) { // append if file exists
				fh = new FileHandler(filename, true);
			} else {
				fh = new FileHandler(filename);
			}

			logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
			logger.info(logInfo + "\n");
		} catch (SecurityException e) {
			logger.info("SecurityException : " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.info("IOException : " + e.getMessage());
			e.printStackTrace();
		} finally {
			fh.close();
		}
	}

}
