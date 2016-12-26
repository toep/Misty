package com.misty.engine.network;

public interface ClientListener {

	public void disconnectedFromServer();
	
	public void receiveDataToClient(Packet p);

	public void onConnection(boolean b, String string);
	
}
