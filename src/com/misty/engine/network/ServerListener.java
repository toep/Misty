package com.misty.engine.network;

import com.misty.engine.network.Server.ClientSocket;

public interface ServerListener {

    void receiveDataToServer(ClientSocket s, Packet p);

    void clientHasConnected(ClientSocket cs);

    void clientDisconnected(ClientSocket clientSocket);

}
