package com.misty.engine.network;

import java.io.IOException;
import java.io.OutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

import com.misty.utils.Util;

public class Server implements Runnable {

	public static final short PORT = 19132;
	public List<ClientSocket> clients = new ArrayList<ClientSocket>();
	private LinkedBlockingDeque<SendInfo> sendQueue = new LinkedBlockingDeque<SendInfo>();
	private ServerListener serverlistener;
	private ServerSocket socket;
	private int port;
	private boolean running = false;
	private Thread run, manage, send, waitForConnection;

	public class ClientSocket {
		public ClientSocket(int i, Socket s) {
			id = i;
			socket = s;
		}

		public int id;
		public Socket socket;

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
		manageClients();
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
						serverlistener.clientHasConnected(cs);
						receive(cs);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		};
		waitForConnection.start();
	}

	private void manageClients() {
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
						serverlistener.serverUpdate();
						deltaupdate--;
					}
				}
			}
		};
		manage.start();
	}

	public void setListener(ServerListener sl) {
		serverlistener = sl;
	}

	private void receive(ClientSocket s) {
		new Thread("Receiver " + s.id + " [Server]") {
			public void run() {
				while (s != null) {
					
					try {
					byte[] bytes = new byte[1024];
					Arrays.fill(bytes, (byte)-1);

					int numOfBytesRead = s.socket.getInputStream().read(bytes);
					if(numOfBytesRead == -1) {
						int i = bytes.length-1;
						for(; i >= 0; i--) {
							if(bytes[i] != -1) {
								i++;
								break;
							}
						}
						
						numOfBytesRead = i;
					}
					ByteBuffer bb = ByteBuffer.wrap(bytes, 0, numOfBytesRead);
					//Util.printBB(bb);
					
					if (serverlistener != null) {
						while (bb.hasRemaining()) {
							byte id = bb.get();
							if(id == -1) {
								break;
							}
							short size = bb.getShort();
							byte[] payload = new byte[size];
							bb.get(payload, 0, size);
							Packet p = new Packet(id, size, payload);
							serverlistener.receiveDataToServer(s, p);
						}
						// listener.receiveDataToClient(ByteBuffer.wrap(bytes));
					} else {
						System.out.println("client received data, no listener set..");
					}
					}
					catch(Exception e) {
						System.out.println("a client disconnected unexpectedly!");
						clients.remove(s);
						userDisconnected(s);
						return;
					}

				}
			}
		}.start();

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
		send(data, c);
	}

	// override this
	public void dataRecieved(String string) {

	}

	public String toHex(byte a) {
		return String.format("0x%02X", a);
	}

	public void userDisconnected(ClientSocket clientSocket) {
		if(clientSocket != null) {
			clients.remove(clientSocket);
			serverlistener.clientDisconnected(clientSocket);	
			clientSocket = null;
		}
	}

	public boolean running() {
		return socket != null && !socket.isClosed();
	}

	public void sendData(Packet p, ClientSocket s) {
		sendData(p.data, s);
	}

	@SuppressWarnings("unused")
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