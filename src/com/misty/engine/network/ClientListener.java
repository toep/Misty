package com.misty.engine.network;

public interface ClientListener {

    void disconnectedFromServer();

    void receiveDataFromServer(Packet p);

    void onConnection(boolean b, String string);

}
