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
import com.misty.engine.graphics.UI.Clickable;
import com.misty.listeners.MyListener;

public abstract class Game implements Runnable {
	public static double tick;
	public static int width;
	public static int height;

	protected int scale;
	private int updaterate = 60;
	private int framerate = 60;
	private boolean fpsLimit = true;
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
	private boolean showFPS = true;
	protected ArrayList<GameObject> gameObjects = new ArrayList<GameObject>();
	
	/** used for other game components to get a copy of the current game*/
	private static Game currentGame;

	/** used to run code in the update method if received from network/eventlisteners*/
	public ConcurrentLinkedQueue<Runnable> actionQueue = new ConcurrentLinkedQueue<Runnable>();

	public Game(String name, int width, int height, int scale) {
		this.name = name;
		Game.width = width;
		Game.height = height;
		this.scale = scale;

		graphics = new Renderer(width, height, scale);
		frame = new Frame();

		listener = new MyListener(this);
		frame.addWindowListener(listener);
		frame.addKeyListener(listener);
		graphics.addMouseListener(listener);
		graphics.addMouseMotionListener(listener);
		graphics.addMouseWheelListener(listener);

		frame.add(graphics);
		frame.pack();
		frame.setTitle(name);
		drawLoadingScreen();
		if (currentGame == null) {
			currentGame = this;
		}
	}
	
	public Game(String name, int width, int height) {
		this(name, width, height, 1);
	}

	/**
	 * Gives the game handle over drawing and updating
	 * @param go the GameObject you wish to pass
	 */
	public void addObject(GameObject go) {
		gameObjects.add(go);
		Collections.sort(gameObjects);
	}

	/**
	 * adds a particle to the engine
	 * @param p
	 */
	public void addParticle(Particle p) {
		particles.add(p);
	}

	/**
	 * sets the color the screen should clear with every frame
	 * @param c color in 0xaarrggbb
	 */
	public void setClearColor(int c) {
		graphics.setClearColor(c);
	}

	public Renderer getRenderer() {
		return graphics;
	}

	/**
	 * Starts the main game loop and begins game
	 */
	public synchronized void start() {
		running = true;
		thread = new Thread(this, "Main Loop");
		thread.start();
	}

	public void run() {
		long lastTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		final double nsUpdate = 1000000000.0 / updaterate;
		final double nsFrames = 1000000000.0 / framerate;
	
		double deltaupdate = 0;
		double deltaframes = 0;
		int frames = 0;
	
		int updates = 0;
	
		// graphics.clear();
		while (running) {
			long now = System.nanoTime();
			deltaupdate += (now - lastTime) / nsUpdate;
			deltaframes += (now - lastTime) / nsFrames;
			lastTime = now;
			if (deltaupdate > 2)
				deltaupdate = 2;
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
					if (showFPS) {
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
				if (showFPS) {
					graphics.drawString("FPS: " + lastFPS, 2, 2, 0xffffffff);
				}
				graphics.render();
				deltaframes--;
				frames++;
			}
	
			if (System.currentTimeMillis() - timer >= 1000) {
				lastFPS = frames;
				frame.setTitle("Normal | " + ("FPS: " + frames + ", UPS: " + updates));
				frames = 0;
				updates = 0;
				timer = System.currentTimeMillis();
			}
	
		}
	}

	/**
	 * gracefully tries to stop the main loop thread
	 */
	public synchronized void stop() {
		try {
			running = false;
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	protected void setClearEveryFrame(boolean a) {
		shouldClear = a;
	}

	/**
	 * this is frames per second, i.e. how many times the draw() method is
	 * called
	 */
	public void setTargetFPS(int fps) {
		framerate = fps;
	}

	/**
	 * this is updates per second, i.e. how many times the update() method is
	 * called
	 */
	public void setTargetUPS(int ups) {
		updaterate = ups;
	}

	public void setFPSLimit(boolean l) {
		fpsLimit = l;
	}

	public void setShowFPS(boolean s) {
		showFPS = s;
	}

	public void setKeyDown(int keyCode, boolean a) {
		keys[keyCode] = a;
	}

	public abstract void draw(Renderer g);

	public abstract void update();

	public void updateActions() {
		Runnable r;
		while (!actionQueue.isEmpty()) {

			r = actionQueue.poll();
			r.run();

		}
		tick += .01f;
	}

	public void mousePressed(MouseEvent e) {
		int mouseX = (e.getX()) / scale;
		int mouseY = (e.getY()) / scale;

		mousePressed(mouseX, mouseY);

		for (int i = gameObjects.size() - 1; i >= 0; i--) {
			if (gameObjects.get(i) instanceof Clickable) {
				if (gameObjects.get(i).containsPoint(mouseX, mouseY)) {
					if (((Clickable) gameObjects.get(i)).onClickPressed(mouseX, mouseY))
						break;

				}
			}
		}

	}

	public void mouseReleased(MouseEvent e) {
		int mouseX = (e.getX()) / scale;
		int mouseY = (e.getY()) / scale;
		mouseReleased(mouseX, mouseY);

		for (int i = gameObjects.size() - 1; i >= 0; i--) {
			if (gameObjects.get(i) instanceof Clickable) {
				Clickable cl = (Clickable) gameObjects.get(i);
				boolean inside = gameObjects.get(i).containsPoint(mouseX, mouseY);
				if (cl.isPressed() && inside) {
					if (cl.onClickReleased(mouseX, mouseY))
						break;
				} else if (cl.isPressed()) {
					cl.onclickReleasedOutside();
				}
			}
		}
	}

	public void mouseReleased(int x, int y) {

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

	public void mouseEntered(MouseEvent e) {

	}

	public boolean isKeyDownAndSet(int keyCode, boolean a) {
		boolean b = keys[keyCode];
		setKeyDown(keyCode, a);
		return b;
	}

	public boolean isKeyDown(int keyCode) {
		return keys[keyCode];
	}

	public boolean isRunning() {
		return running;
	}

	public static Game getCurrent() {
		return currentGame;
	}

	private void drawLoadingScreen() {
		graphics.fill(0xff000000);
		graphics.drawString("Loading assets", width / 2 - 56, height / 2, 2);
		graphics.render();
	}

	private void drawGameObjects() {
		Iterator<GameObject> gos = gameObjects.iterator();
		while (gos.hasNext()) {
			gos.next().draw(graphics);
		}
	}

	private void updateGameObjects() {
		Iterator<GameObject> gos = gameObjects.iterator();
		while (gos.hasNext()) {
			gos.next().update();
		}
	
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
}
