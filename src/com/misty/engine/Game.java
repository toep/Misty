package com.misty.engine;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.misty.engine.graphics.GameObject;
import com.misty.engine.graphics.Renderer;
import com.misty.listeners.MyListener;

public class Game implements Runnable {
	public static double tick;
	public static int width;
	public static int height;
	
	protected int scale = 2;
	private int updaterate = 120;
	private int framerate = 60;
	private boolean fpsLimit = false;
	private Renderer graphics;
	private Frame frame;
	protected boolean running = false;
	protected boolean[] keys = new boolean[256];

	private Thread thread;
	private MyListener listener;
	private ArrayDeque<Particle> particles = new ArrayDeque<Particle>();
	private boolean shouldClear = true;
	public String name = "Unnamed";
	private int lastFPS = framerate;
	public boolean showFPS = true;
	public ArrayList<GameObject> gameObjects = new ArrayList<GameObject>();
	
	// used to run code in the update method if received from
		// network/eventlisteners
	public ConcurrentLinkedQueue<Runnable> actionQueue = new ConcurrentLinkedQueue<Runnable>();

	public Game(String name, int width, int height, int scale) {
		this.name = name;
		Game.width = width;
		Game.height = height;
		this.scale = scale;
		
		graphics = new Renderer(width, height);
		frame = new Frame(width * scale + 16, height * scale + 39);
		
		listener = new MyListener(this);
		frame.addWindowListener(listener);
		graphics.addMouseListener(listener);
		graphics.addKeyListener(listener);
		graphics.addMouseMotionListener(listener);
		graphics.addMouseWheelListener(listener);
		
		frame.add(graphics);
		frame.pack();
		frame.setTitle(name);
		drawLoadingScreen();
	}
	
	public void addObject(GameObject go) {
		gameObjects.add(go);
		Collections.sort(gameObjects);
	}

	public void setClearColor(int c) {
		graphics.setClearColor(c);
	}

	private void drawLoadingScreen() {
		graphics.fill(0xff000000);
		graphics.drawString("Loading assets", width / 2 - 56, height / 2, 2);
		graphics.render();
	}

	public void addParticle(Particle p) {
		particles.add(p);
	}

	public synchronized void start() {
		running = true;
		thread = new Thread(this, "Display");
		thread.start();
	}

	public synchronized void stop() {
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	protected void setClearEveryFrame(boolean a) {
		shouldClear = a;
	}
	
	/*this is frames per second, i.e. how many times the draw() method is called*/
	public void setTargetFPS(int fps) {
		framerate = fps;
	}

	/*this is updates per second, i.e. how many times the update() method is called*/
	public void setTargetUPS(int ups) {
		updaterate = ups;
	}
	
	public void setFPSLimit(boolean l) {
		fpsLimit = l;
	}
	
	public void setShowFPS(boolean s) {
		showFPS = s;
	}
	
	public void run() {
		long lastTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		final double nsUpdate = 1000000000.0 / updaterate;
		final double nsFrames = 1000000000.0 / framerate;

		double deltaupdate = 0;
		double deltaframes = 0;
		int frames = 0;
		
		@SuppressWarnings("unused")
		int updates = 0;

		// graphics.clear();
		while (running) {
			long now = System.nanoTime();
			deltaupdate += (now - lastTime) / nsUpdate;
			deltaframes += (now - lastTime) / nsFrames;
			lastTime = now;

			while (deltaupdate >= 1) {
				update();
				updateActions();
				updateParticles();
				updateGameObjects();
				deltaupdate--;
				updates++;
			}
			if (fpsLimit)
				while (deltaframes >= 1) {
					if (shouldClear)
						graphics.clear();
					graphics.setRenderingMode(Renderer.RENDERING_MODE_NORMAL);
					drawGameObjects();
					draw(graphics);
					if(showFPS) {
						graphics.drawString("FPS: " + lastFPS, 2, 2, 0xffffffff);
					}
					drawParticles();
					graphics.render();
					deltaframes--;
					frames++;
				}
			else {
				if (shouldClear)
					graphics.clear();
				graphics.setRenderingMode(Renderer.RENDERING_MODE_NORMAL);
				drawGameObjects();
				draw(graphics);
				drawParticles();
				if(showFPS) {
					graphics.drawString("FPS: " + lastFPS, 2, 2, 0xffffffff);
				}
				graphics.render();
				deltaframes--;
				frames++;
			}
			/*
			 * while (deltaupdate >= 1) { update(); updateParticles();
			 * deltaupdate--; updates++; } while (deltaframes >= 1) {
			 * graphics.clear(clearColor); draw(); drawParticles();
			 * graphics.render(); deltaframes--; frames++; }
			 */
			if (System.currentTimeMillis() - timer >= 1000) {
				lastFPS = frames;
				//frame.setTitle("Normal | " + ("FPS: " + frames + ", UPS: " + updates));
				frames = 0;
				updates = 0;
				timer = System.currentTimeMillis();
			}

			/*
			 * try { Thread.sleep(1); } catch (InterruptedException e) {
			 * e.printStackTrace(); }
			 */
		}
	}

	private void drawGameObjects() {
		Iterator<GameObject> gos = gameObjects.iterator();
		while(gos.hasNext()) {
			gos.next().draw(graphics);
		}
	}

	private void updateGameObjects() {
		Iterator<GameObject> gos = gameObjects.iterator();
		while(gos.hasNext()) {
			gos.next().update();
		}
	}

	public boolean isKeyDown(int keyCode) {
		return keys[keyCode];
	}

	public boolean isKeyDownAndSet(int keyCode, boolean a) {
		boolean b = keys[keyCode];
		setKeyDown(keyCode, a);
		return b;
	}

	public void setKeyDown(int keyCode, boolean a) {
		keys[keyCode] = a;
	}

	private void drawParticles() {
		Iterator<Particle> it = particles.iterator();
		while (it.hasNext()) {
			graphics.drawParticle(it.next());
		}
	}

	private void updateParticles() {

		Iterator<Particle> it = particles.iterator();
		while (it.hasNext()) {
			Particle p = it.next();
			if (p.getDuration() <= 0) {
				it.remove();
			} else {
				p.update();
			}
		}
	}

	public void draw(Renderer g) {

	}

	public void update() {

	}

	public void updateActions() {
		Runnable r;
		while (!actionQueue.isEmpty()) {

			r = actionQueue.poll();
			r.run();

		}
		tick += .01f;
		/*
		 * Iterator<Runnable> it = actionQueue.iterator(); while(it.hasNext()) {
		 * Runnable r = it.next(); r.run(); it.remove(); }
		 */
	}

	public void mousePressed(MouseEvent e) {
		int mouseX = e.getX() / scale;
		int mouseY = e.getY() / scale;
		mousePressed(mouseX, mouseY);
	}

	public void mousePressed(int x, int y) {

	}

	public void keyPressed(KeyEvent e) {
		keys[e.getKeyCode()] = true;
	}

	public void keyReleased(KeyEvent e) {
		keys[e.getKeyCode()] = false;

	}

	public void mouseDragged(MouseEvent e) {

	}

	public void mouseMoved(MouseEvent e) {

	}

	public void windowClosed(WindowEvent e) {

	}

	public void windowClosing(WindowEvent e) {

	}

	public void mouseWheelMoved(MouseWheelEvent e) {

	}

	public void mouseReleased(MouseEvent e) {

	}

	public void mouseEntered(MouseEvent e) {

	}
}
