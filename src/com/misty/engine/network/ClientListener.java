package com.misty.engine.network;

public interface ClientListener {

    void disconnectedFromServer();

    void receiveDataToClient(Packet p);

    void onConnection(boolean b, String string);

}
