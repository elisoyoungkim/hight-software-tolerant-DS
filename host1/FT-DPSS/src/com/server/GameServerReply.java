package com.server;

import java.io.Serializable;

public class GameServerReply implements Serializable
{
	private static final long serialVersionUID = 224311734257599129L;
	public boolean isOK;
    public String message;
    
    public GameServerReply(boolean aIsOK, String aMessage)
    {
		isOK = aIsOK;
		message = aMessage;
    }
}
