package com.misty.engine.network;

import com.misty.engine.network.Packet;
import com.misty.engine.network.Server.ClientSocket;

public interface ServerListener {

	public void receiveDataToServer(ClientSocket s, Packet p);

	public void clientHasConnected(ClientSocket cs);

	public void clientDisconnected(ClientSocket clientSocket);

	public void serverUpdate();

	
}
