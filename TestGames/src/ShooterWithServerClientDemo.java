
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.misty.engine.Game;
import com.misty.engine.Particle;
import com.misty.engine.Vector2;
import com.misty.engine.audio.Sound;
import com.misty.engine.graphics.Renderer;
import com.misty.engine.graphics.Sprite;
import com.misty.engine.network.Client;
import com.misty.engine.network.ClientListener;
import com.misty.engine.network.Packet;
import com.misty.engine.network.Server;
import com.misty.engine.network.Server.ClientSocket;
import com.misty.engine.network.ServerListener;
import com.misty.engine.physics.CollisionResult;
import com.misty.listeners.Keys;

//all games extends Game, and if you wanna implement server/client you implement ClientListener and/or ServerListener
public class ShooterWithServerClientDemo extends Game implements ClientListener, ServerListener {

	//your network objects
	Client client;
	Server server;

	Ship player;
	boolean mousePressed = false;
	int mouseX = 0;
	int mouseY = 0;
	File bw = new File("res/bow.wav");
	//client side list of ships
	public Map<Integer, Ship> players = new HashMap<Integer, Ship>();
	
	//server side list of ships
	public Map<Integer, Ship> clientShips = new HashMap<Integer, Ship>();
	
	Sprite backgroundSprite, bigRockSprite;
	
	public static void main(String[] args) {
		new ShooterWithServerClientDemo();
		
	}
	public ShooterWithServerClientDemo() {
		super("Space Shooter thingy", 576, 448, 2);
		//all colors are in the form 0xaarrggbb where a is the alpha, and rgb is red, green blue, 0xffffffff is white, 0xff000000 is black
		setClearColor(0xff000000);
		player = new Ship((int)(Math.random()*(width-40))+20, (int)(Math.random()*(height-40))+20, (float)(Math.random()*Math.PI*2));
		try {
			backgroundSprite = new Sprite("res/background.png", 0, 0);
			bigRockSprite = new Sprite("res/rock_big.png", 150, 150);
		} catch (IOException e) {
			e.printStackTrace();
		}
		bigRockSprite.makeCollidable();
		
		//replace localhost with your external ip if you want to join from other network :) and the port can be anything you'd like > 80 & < 65536
		client = new Client("localhost", 19132);
		client.setListener(this);
		//make sure the ports are the same!
		server = new Server(19132);
		server.setListener(this);

		//this is how you play sounds! works but not sure how well..
		Sound.playSound(new File("res/africa.wav"));
		
		//add objects for the engine to draw and update automatically..
		addObject(backgroundSprite);
		addObject(bigRockSprite);
		addObject(player);
		start();
	}

	@Override
	public void draw(Renderer g) {

		g.drawString("testing", 40, 40, 0xffae30fe, 2f);
		
		g.drawString("Ships: " + (players.size()+1), 2, 12, 0xffffffff);

		Iterator<Ship> shipIterator = players.values().iterator();
		
		while(shipIterator.hasNext()) {
			shipIterator.next().draw(g);
		}
		
		if (server.running()) {
			g.drawString("Server: " + server.clients.size() + " clients connected [" + clientShips.size() + " ships]", 2, 32, 0xffffffff);
		}
		if(client != null)
			g.drawString("Client: " + client.getStatus(), 2, 22, 0xffffffff);
		
	}

	@Override
	public void update() {
		super.update();

		if (isKeyDown(Keys.LEFT)) {
			player.rot(-0.03f);
		}
		if (isKeyDown(Keys.RIGHT)) {
			player.rot(0.03f);
		}
		if (isKeyDown(Keys.UP)) {
			player.thrust(.1f);
		}
		if (isKeyDown(Keys.DOWN)) {
			player.thrust(-.1f);
		}
		//this checks if the key is down and then sets the keyDown equal to the boolean
		if(isKeyDownAndSet(Keys.SHIFT, false)) {
			
				
		
		}

		if(client.isConnected()) {
			//a packet is created with the following parameters(byte id, short size(in bytes))
			//know int = 4 bytes = float
			//so when we wanna send 3 ints and 1 float we need 3*4 + 1*4 which is why we put 16 for the size of the packet.
			//the id you choose to identiy with the certain packet.
			Packet playerMovement = new Packet(4, 16);
			playerMovement.putInt(player.playerID);
			playerMovement.putInt(player.getX());
			playerMovement.putInt(player.getY());
			playerMovement.putFloat(player.getRot());
			client.sendData(playerMovement);
		}
		
		if (isKeyDownAndSet(Keys.S, false) && !server.running()) {
			server.start();//start the server
			client.connect();//connect the client to the server
		}
		if (isKeyDownAndSet(Keys.C, false) && !client.isConnected()) {
			client.connect();
		}
		if(isKeyDownAndSet(Keys.P, false) ) {
			sendDisconnectPacket();
		}
		if(isKeyDown(Keys.CONTROL)) {
			
			//if you wanna do something to a gameobject or similar from the server/client/mouselistener methods you wrap the code in this
			actionQueue.add(new Runnable() {
				@Override
				public void run() {
					//here you write the code :)
				}
			});
			actionQueue.add(new Runnable() {
				@Override
				public void run() {
					if(player.shoot()) {
						Sound.playSound(bw);
						Packet p = new Packet(6, 12);
						p.putInt(player.playerID);
						p.putInt(player.getX());
						p.putInt(player.getY());
						client.sendData(p);
					}
				}
			});
			
		}
		
		Iterator<Ship> it = players.values().iterator();
		while(it.hasNext()) {
			it.next().update();
		}
		

		Iterator<Bullet> bullets = player.getBullets().iterator();
		while(bullets.hasNext()) {
			Bullet b = bullets.next();
			if(b.onScreen(width, height) && b.possibleCollide(bigRockSprite)) {
				CollisionResult cr = b.intersectsWith(bigRockSprite);
				if(cr.intersects) {
					bigRockSprite.applyTorque(new Vector2(cr.intersectionPoint), b.getRotation(), .1f);
					bullets.remove();
				}
				
			}
			
		}
	}


	@Override
	public void mousePressed(int x, int y) {
		super.mousePressed(x, y);
		mouseX = x;
		mouseY = y;
		
		actionQueue.add(new Runnable() {
			@Override
			public void run() {
				addShot(mouseX, mouseY);
				Packet p = new Packet(3, 8);
				p.putInt(mouseX);
				p.putInt(mouseY);
				if(client.isConnected()) {
					client.sendData(p);
				}	
			}
		});
	}

	
	private void addShot(int x, int y) {
		for (int i = 0; i < 2000; i++) {
			//here you add particles to the screen, kinda funky but play around with it
			addParticle(new Particle(x, y, Particle.MOTION_RANDOM_GRAVITY_DOWN, 1, 120, 0xffff0000 * (i + 1)));
		}
	}

	

	@Override
	public void disconnectedFromServer() {
		System.out.println("looks like we disconnected from the server :(");
		client.resetConnection();
		players.clear();
	}

	

	@Override
	public void receiveDataToClient(Packet packet) {
		packet.toPayload();
		byte id = packet.id;
		
		if (id == 0) {
			//ping
		} else if (id == 1) {
			
			//first connect, get our playerID
			player.playerID = packet.getInt();
			Packet p = new Packet(1, 16);
			p.putInt(player.playerID);
			p.putInt(player.getX());
			p.putInt(player.getY());
			p.putFloat(player.getRot());
			client.sendData(p);
			//System.out.println("We connected! sending our data to the server");
			
		} else if (id == 2) {
			//add new entity
			int playerID = packet.getInt();
			int x = packet.getInt();
			int y = packet.getInt();
			float rot = packet.getFloat();
			//System.out.println("adding action to create new ship instance");
			
			actionQueue.add(new Runnable() {
				@Override
				public void run() {
					Ship s = new Ship(x, y, rot);
					s.playerID = playerID;
					players.put(playerID, s);
					//System.out.println("we added a new ship instance");

				}
			});
		} else if (id == 3) {
			//shot
			int x = packet.getInt();
			int y = packet.getInt();
			
			actionQueue.add(new Runnable() {
				public void run() {
					addShot(x, y);
				}
			});
			
		} else if (id == 4) {
			int playerID = packet.getInt();
			
				actionQueue.add(new Runnable() {
					@Override
					public void run() {
						if(players.containsKey(playerID)) {
							Ship s = players.get(playerID);
						s.setPosition(packet.getInt(), packet.getInt());
						s.setRot(packet.getFloat());
					}
						}
				});
				
			
		} else if (id == 5) {

		}
		else if(id == 6) {
			int pID = packet.getInt();
			int x = packet.getInt();
			int y = packet.getInt();
			players.get(pID).addShot(x, y);
		}
		else if(id == 7) {
			int disID = packet.getInt();
			int pID = player.playerID;
			actionQueue.add(new Runnable() {
				@Override
				public void run() {
					if(disID == pID) {
						// we disconnected
						client.resetConnection();
						players.clear();
					}
					else {
						players.remove(disID);				
					}	
				}
			});
			
		}
	}

	@Override
	public void receiveDataToServer(ClientSocket s, Packet packet) {
		packet.toPayload();
		byte id = packet.id;
		
		if(id == 1) {
			//new player info
			
			Iterator<Ship> ps = clientShips.values().iterator();
			while(ps.hasNext()) {
				//System.out.println("sending player data to new player");
				Ship ship = ps.next();
				Packet p = new Packet(2, 16);
				p.putInt(ship.playerID);
				p.putInt(ship.getX());
				p.putInt(ship.getY());
				p.putFloat(ship.getRot());
				server.sendData(p, s);
			}
			
			int playerID = packet.getInt();
			int px = packet.getInt();
			int py = packet.getInt();
			float pr = packet.getFloat();
			Ship sh = new Ship(px, py, pr);
			sh.playerID = playerID;
			clientShips.put(playerID, sh);
			
			Packet p = new Packet(2, 16);
			p.putInt(playerID);
			p.putInt(px);
			p.putInt(py);
			p.putFloat(pr);
			//System.out.println("server got player data, sending to all");
			server.sendToAllExcept(s, p);
			
			
			
		}
		else if(id == 3) {
			//a shot
			server.sendToAllExcept(s, packet);

		}
		else if(id == 4) {
			server.sendToAllExcept(s, packet);
		}
		else if(id == 6) {
			server.sendToAllExcept(s, packet);
		}
		else if(id == 7) {
			//client wants to disconnect
			int disconnectingID = s.id;
			Packet p = new Packet(7, 4);
			p.putInt(disconnectingID);
			server.sendToAll(p);
			server.userDisconnected(s);
		}
		
	}
	
	@Override
	public void clientHasConnected(ClientSocket s) {
		System.out.println("Client has connected!");
		Packet firstConnect = new Packet(1, 4);
		firstConnect.putInt(s.id);
		server.sendData(firstConnect, s);
		
		
	}
	
	@Override
	public void windowClosing(WindowEvent e) {
		sendDisconnectPacket();
		
	}

	
	private void sendDisconnectPacket() {
		Packet p = new Packet(7, 0);
		client.sendData(p);
	}

	@Override
	public void clientDisconnected(ClientSocket clientSocket) {
		clientShips.remove(clientSocket.id);
	}

	
	//this method is used if you have a server to update server logic like collision detection, decision making and such
	@Override
	public void serverUpdate() {
	}


}
