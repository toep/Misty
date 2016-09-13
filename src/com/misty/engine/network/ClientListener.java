package com.misty.engine.network;

public interface ClientListener {

	public void disconnectedFromServer();
	
	public void receiveDataToClient(Packet p);
	
}
