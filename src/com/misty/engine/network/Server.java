package com.misty.engine.network;

import com.misty.engine.Game;
import com.misty.utils.Util;

import java.io.IOException;
import java.io.OutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

public class Server implements Runnable {

    public List<ClientSocket> clients = new ArrayList<ClientSocket>();
    private LinkedBlockingDeque<SendInfo> sendQueue = new LinkedBlockingDeque<SendInfo>();
    private ArrayList<ServerListener> serverlisteners = new ArrayList<ServerListener>();
    private ServerSocket socket;
    private int port;
    private boolean running = false;
    private Thread run, manage, send, waitForConnection;
    private String handShakeCode = "hD0fGz4qGN";
    private boolean firstConnection = false;

    public class ClientSocket {
        public ClientSocket(int i, Socket s) {
            id = i;
            socket = s;
        }

        public int id;
        public String name;
        public Socket socket;
        public boolean handShook = false;

    }

    public class SendInfo {
        public SendInfo(byte[] d, Socket s) {
            data = d;
            socket = s;
        }

        public byte[] data;
        public Socket socket;
    }

    public ClientSocket getClientSocket(Socket s) {
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).socket == s)
                return clients.get(i);
        }

        return null;
    }

    public ClientSocket getClientSocket(int id) {
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).id == id)
                return clients.get(i);
        }
        return null;
    }

    public void setHandshakeKey(String s) {
        handShakeCode = s;
    }

    public Server(int port) {
        this.port = port;
        run = new Thread(this, "Server");
    }

    public void start() {
        try {
            socket = new ServerSocket(port);
            run.start();
        } catch (BindException e) {
            System.err.println("We were unable to bind to port " + port + ". Maybe you're already running a server on that port?");
            //e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        running = true;
        System.out.println("Server started on port " + port);

        send();
        waitForConnection();
    }

    private void send() {
        send = new Thread("Sender [Server]") {
            public void run() {
                while (running) {
                    SendInfo si = null;
                    try {
                        si = sendQueue.take();
                        OutputStream os = si.socket.getOutputStream();
                        os.write(si.data);
                        // }
                    } catch (IOException e) {
                        System.err.println("Unable to send data to client. removing from list");
                        userDisconnected(getClientSocket(si.socket));
                        si = null;
                        // clients.remove();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        };
        send.start();
    }

    protected boolean clientConnected(Socket s) {
        ClientSocket cs = getClientSocket(s);
        return clients.contains(cs);
    }

    private void waitForConnection() {
        waitForConnection = new Thread("Client Reciever [Server]") {
            public void run() {
                while (running) {
                    try {
                        Socket s = socket.accept();
                        System.out.println("a client has connected " + s.getInetAddress());
                        ClientSocket cs = new ClientSocket(Util.randomUniqueID(), s);
                        clients.add(cs);

                        //if(firstConnection()) {
                        //	manageClients();
                        //}
                        askForCode(cs);
                        receive(cs);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        waitForConnection.start();
    }

    protected boolean firstConnection() {
        return firstConnection;
    }

    protected void askForCode(ClientSocket cs) {
        Packet p = new Packet(Packet.PACKET_ID_HANDSHAKE_REQ, 3 + 2);
        p.putString("WTC");
        sendDataHandshake(p, cs);
    }

	/*private void manageClients() {
        firstConnection = false;
		manage = new Thread("Manage Clients [Server]") {
			public void run() {
				long lastTime = System.nanoTime();
				//long timer = System.currentTimeMillis();
				int updaterate = 60;
				double deltaupdate = 0;
				final double nsUpdate = 1000000000.0 / updaterate;
				while (running) {
					long now = System.nanoTime();
					deltaupdate += (now - lastTime) / nsUpdate;
					lastTime = now;
					while(deltaupdate >= 1) {
						//here we update logic server side
						serverlisteners.forEach(e -> e.serverUpdate());
						deltaupdate--;
					}
				}
			}
		};
		manage.start();
	}*/

    public void addListener(ServerListener sl) {
        serverlisteners.add(sl);
    }

    private void receive(ClientSocket s) {
        new Thread("Receiver " + s.id + " [Server]") {
            public void run() throws BufferUnderflowException {
                byte[] bytes = new byte[2097152];//2^21
                int numOfBytesRead = 2097152;

                ByteBuffer inbuffer = ByteBuffer.wrap(bytes, 0, numOfBytesRead);
                ByteBufferBackedOutputStream os = new ByteBufferBackedOutputStream(inbuffer);
                while (s != null) {
                    os.clearBuffer();
                    try {
                        Arrays.fill(bytes, (byte) -1);
                        for (int i = 0; i < numOfBytesRead; i++) bytes[i] = (byte) (-1);
                        int sizeOfPacket;
                        byte[] buf = new byte[65536];
                        ByteBuffer headerbb = ByteBuffer.wrap(buf);
                        s.socket.getInputStream().read(buf, 0, 5);
                        byte idd = headerbb.get();//id
                        sizeOfPacket = headerbb.getInt();
                        //System.out.println("expected size of packet: " + sizeOfPacket);
                        //os.buf.position(0);
                        os.write(idd);
                        os.write(sizeOfPacket);
                        int bytesRead = 0;
                        numOfBytesRead = 5;
                        while ((bytesRead = s.socket.getInputStream().read(buf)) > 0) {
                            os.write(buf, 0, bytesRead);
                            numOfBytesRead += bytesRead;
                            Thread.sleep(5);
                            // System.out.println("we've read: " + numOfBytesRead + " bytes");
                            if (numOfBytesRead >= sizeOfPacket) break;
                        }
                        //numOfBytesRead+=5;//for the header
                        //numOfBytesRead = //s.socket.getInputStream().read(bytes);
                        //s.socket.getInputStream().read(bytes, 0, 2097152);
                        //System.out.println("socket read bytes: " + numOfBytesRead);

                        if (numOfBytesRead == -1) {
                            int i = bytes.length - 1;
                            for (; i >= 0; i--) {
                                if (bytes[i] != -1) {
                                    i++;
                                    break;
                                }
                            }

                            numOfBytesRead = i;
                        }
                        //System.out.println("creating bb with size " + numOfBytesRead);
                        ByteBuffer bb = ByteBuffer.wrap(bytes, 0, numOfBytesRead);
                        //Util.printBB(bb);

                        if (serverlisteners.size() != 0) {
                            while (bb.hasRemaining()) {
                                byte id = bb.get();
                                //if (id <= 0) {
                                //    break;
                                //}
                                int size = bb.getInt();
                                //System.out.println("we have a package("+id+") with size " + size);
                                byte[] payload = new byte[size];
                                bb.get(payload, 0, size);
                                Packet p = new Packet(id, size, payload);
                                if (s.handShook && p.id >= 0)
                                    Game.getCurrent().actionQueue.add(() -> serverlisteners.forEach(e -> e.receiveDataFromClient(s, p)));
                                else {
                                    handleHandshake(s, p);
                                }
                            }
                            // listener.receiveDataToClient(ByteBuffer.wrap(bytes));
                        } else {
                            System.out.println("client received data, no listener set..");
                        }
                    } catch (Exception e) {
                        System.out.println("a client disconnected unexpectedly!");
                       // e.printStackTrace();
                        userDisconnected(s);

                        return;
                    }

                }
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }


    protected void handleHandshake(ClientSocket s, Packet p) {
        System.out.println("server got pid: " + p.id);
        p.toPayload();
        if (p.id == Packet.PACKET_ID_HANDSHAKE_RES) {
            if (p.getString().equals(handShakeCode)) {
                //matching handshake
                s.handShook = true;
                Packet idp = new Packet(Packet.PACKET_ID_HANDSHAKE_OK, 4);
                idp.putInt(s.id);
                System.out.println("handshake done, sending accept packet to client");
                sendDataHandshake(idp, s);
            } else {
                Packet w = new Packet(Packet.PACKET_ID_HANDSHAKE_INVALID, 0);
                sendDataHandshake(w, s);
                userDisconnected(s);
            }
        }
        else if(p.id == Packet.PACKET_ID_NAME_PACKET) {
            s.name = p.getString();
            Game.getCurrent().actionQueue.add(() -> serverlisteners.forEach(e -> e.clientHasConnected(s)));
        }
    }

    public void sendToAllExcept(ClientSocket except, final byte[] data) {
        for (ClientSocket s : clients) {
            if (s != except) {
                send(data, s);
            }
        }
    }

    public void sendToAll(final byte[] data) {
        for (ClientSocket s : clients) {
            send(data, s);
        }
        //System.out.println("sending data to all" + clients.size());
    }

    private void send(final byte[] data, ClientSocket client) {
        SendInfo si = new SendInfo(data, client.socket);
        //Executors.newSingleThreadScheduledExecutor().schedule(new Runnable(){public void run() {sendQueue.add(si);}}, 50, TimeUnit.MILLISECONDS);
        sendQueue.add(si);
    }


    public void sendData(final byte[] data, ClientSocket c) {
        if (c.handShook)
            send(data, c);
    }


    private void sendDataHandshake(Packet p, ClientSocket s) {
        send(p.data, s);
    }

    // override this
    public void dataRecieved(String string) {

    }

    public String toHex(byte a) {
        return String.format("0x%02X", a);
    }

    public void userDisconnected(ClientSocket clientSocket) {
        if (clientSocket != null) {
            clients.remove(clientSocket);
            Game.getCurrent().actionQueue.add(() -> serverlisteners.forEach(e -> e.clientDisconnected(clientSocket)));
        }
        //clientSocket = null;
    }

    public boolean running() {
        return socket != null && !socket.isClosed();
    }

    public void sendData(Packet p, ClientSocket s) {
        sendData(p.data, s);
    }

    private void sendToAllExcept(ClientSocket s, ByteBuffer data) {
        sendToAllExcept(s, data.array());
    }

    public void sendToAllExcept(ClientSocket s, Packet p) {
        sendToAllExcept(s, p.data);
    }

    public void sendToAll(Packet p) {
        sendToAll(p.data);
    }

}