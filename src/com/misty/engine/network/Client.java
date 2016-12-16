package com.misty.engine.network;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class Client {

	// public String name;
	public String address;
	public int port;
	private int ID = -1;
	public int attempt = 0;
	private Socket socket;
	private Thread connect, send, receive;
	ClientListener listener;
	public static int timeout = 1000;
	public boolean attempingConnection;
	public boolean handShook = false;
	public LinkedBlockingDeque<byte[]> sendQueue = new LinkedBlockingDeque<byte[]>();
	private String handShakeCode = "hD0fGz4qGN";
	
	public void connect() {
		if (!(connect.getState() == Thread.State.NEW))
			createConnectThread();

		connect.start();

	}
	
	public void setHandshakeKey(String s) {
		handShakeCode = s;
	}

	public Client(String address, int port) {
		socket = new Socket();
		this.address = address;
		this.port = port;
		createConnectThread();
	}

	public void resetConnection() {
		socket = new Socket();
		// connected = false;
	}

	private void createConnectThread() {
		if (connect != null) {
			try {
				connect.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		connect = new Thread("connect") {
			@Override
			public void run() {
				try {
					attempingConnection = true;
					socket = new Socket();
					socket.connect(new InetSocketAddress(address, port), timeout);
					attempingConnection = false;
					startReceiving();
					startSendThread();
				} catch (IOException e) {
					System.err.println("Error connecting to " + address + ":" + port + ", " + e.getMessage());
					attempingConnection = false;

				}
			}
		};
	}



	protected void startSendThread() {
		send = new Thread("Sender [Client]") {
			@Override
			public void run() {
				while (isConnected()) {

					try {
						byte[] si = sendQueue.poll(200, TimeUnit.MILLISECONDS);
						if (si == null)
							continue;
						if(isConnected()) {
							OutputStream os = socket.getOutputStream();
							os.write(si);							
						}
					} catch (Exception e) {
						System.err.println("Unable to send data to server. we are probably not connected");
						resetConnection();
					}
				}
			}
		};

		send.start();
	}

	public void setListener(ClientListener listener) {
		this.listener = listener;
	}

	private void startReceiving() {
		receive = new Thread("Receiver [Client]") {
			public void run() {
				byte[] bytes = new byte[2097152];
				int numOfBytesRead = 2097152;
				while (true) {
					try {
						for(int i = 0; i < numOfBytesRead; i++) bytes[i] = (byte)(-1);
						numOfBytesRead = socket.getInputStream().read(bytes);
						if(numOfBytesRead == -1) numOfBytesRead = bytes.length;
						ByteBuffer bb = ByteBuffer.wrap(bytes, 0, numOfBytesRead);
						//System.out.println(numOfBytesRead);
						if (listener != null) {
							while (bb.hasRemaining()) {
								byte id = bb.get();
								if(id == -1) {
									break;
								}
								int size = bb.getInt();
								byte[] payload = new byte[size];
								bb.get(payload, 0, size);
								Packet p = new Packet(id, size, payload);
								
								if(handShook)
									listener.receiveDataToClient(p);
								else {
									handleHandshake(p);
								}
								//System.out.println("read " + read);
							}
							// listener.receiveDataToClient(ByteBuffer.wrap(bytes));
						} else {
							System.out.println("client received data, no listener set..");
						}

					} catch (IOException e) {
						if (listener != null) {
							listener.disconnectedFromServer();
						}
						break;
						// e.printStackTrace();
					}
				}
			}
		};
		receive.start();
	}

	protected void handleHandshake(Packet p) {
		//we're not ready yet
		if(p.id == 4) {
			p.toPayload();
			if(p.getString().equals("WTC")) {
				Packet codep = new Packet(5, handShakeCode.length() + 2);
				codep.putString(handShakeCode);
				sendDataHandshake(codep);
			}
		}else if(p.id == 5) {
			handShook = true;
			ID = p.getInt();
		}else if(p.id == 3) {
			//wrong handshakeKey
			System.err.println("The handshake key from the server is different from yours!");
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}

	public int getID() {
		return ID;
	}

	public void sendData(byte[] data) {
		if(isConnected() && handShook)
			sendQueue.add(data);
		else
			;
	}
	private void sendDataHandshake(byte[] data) {
		if(isConnected())
			sendQueue.add(data);
	}
	private void sendDataHandshake(Packet p) {
		sendDataHandshake(p.data);
	}

	public String getStatus() {
		if (attempingConnection) {
			return "Trying to connect.";
		} else if (socket.isConnected()) {
			return "Connected.";
		} else {
			return "Not connected.";
		}
	}

	public boolean isConnected() {
		return socket.isConnected();
	}

	public void sendData(Packet p) {
		sendData(p.data);
	}
}
