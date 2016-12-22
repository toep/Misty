package com.misty.engine;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.misty.engine.graphics.GameObject;
import com.misty.engine.graphics.Group;
import com.misty.engine.graphics.Particle;
import com.misty.engine.graphics.Renderer;
import com.misty.engine.graphics.Stage;
import com.misty.engine.graphics.UI.Clickable;
import com.misty.engine.graphics.UI.Scrollable;
import com.misty.engine.graphics.UI.Typeable;
import com.misty.engine.graphics.font.Font;
import com.misty.listeners.MyListener;
import com.misty.utils.Util;

/**
 * 
 * Name ideas: Gloomy
 * 
 * @author Thomas
 *
 */
public abstract class Game implements Runnable {
	public double tick;
	public int width;
	public int height;

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
	protected ArrayList<Stage> stages = new ArrayList<Stage>();

	protected Stage gameStage;
	/** used for other game components to get a copy of the current game */
	private static Game currentGame;

	/**
	 * used to run code in the update method on main thread if received from
	 * network/eventlisteners
	 */
	public ConcurrentLinkedQueue<Runnable> actionQueue = new ConcurrentLinkedQueue<Runnable>();
	private Stage currentStage;

	public Game(String name, int width, int height, int scale) {
		this.name = name;
		this.width = width;
		this.height = height;
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
		gameStage = new Stage();
		currentStage = gameStage;
	}

	public Game(String name, int width, int height) {
		this(name, width, height, 1);
	}

	/**
	 * Gives the game handle over drawing and updating
	 * 
	 * @param go the GameObject you wish to pass
	 */
	public void add(GameObject go) {
		if (go == null)
			return;
		gameStage.add(go);		
	}

	public void addStage(Stage stage) {
		stages.add(stage);
	}

	/**
	 * adds a particle to the engine
	 * 
	 * @param p
	 */
	public void addParticle(Particle p) {
		particles.add(p);
	}

	/**
	 * sets the color the screen should clear with every frame
	 * 
	 * @param c color in 0xaarrggbb
	 */
	public void setClearColor(int c) {
		graphics.setClearColor(c);
	}

	public Renderer getRenderer() {
		return graphics;
	}

	public void setFont(Font font) {
		graphics.setFont(font);
	}

	/**
	 * Starts the main game loop and begins game
	 */
	public synchronized void start() {
		running = true;
		thread = new Thread(this, "Main Loop");
		thread.start();
	}

	/**
	 * set's the cursor to visible or invisible
	 * 
	 * @param b
	 */
	public void setMouseVisible(boolean b) {

		if (!b) {
			BufferedImage cursorImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
			// Create a new blank cursor.
			Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0),
					"blank cursor");
			graphics.setCursor(blankCursor);
		} else {
			graphics.setCursor(Cursor.getDefaultCursor());
		}
	}

	public void setCursorImage(String name) {
		BufferedImage img;
		try {
			img = Util.getBufferedImageFromFile(name);
			graphics.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(img, new Point(0, 0), name));
		} catch (IOException e) {

			e.printStackTrace();
		}
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
				frame.setTitle(name + " | " + ("FPS: " + frames + ", UPS: " + updates));
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

	public void setStage(Stage stage) {
		currentStage = stage;
		if(currentStage == null) {
			currentStage = gameStage;
		}
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

		mouseX -= currentStage.getX();
		mouseY -= currentStage.getY();

		mousePressed(mouseX - currentStage.getX(), mouseY - currentStage.getY(), currentStage, true);

	}

	private void mousePressed(int mouseX, int mouseY, Group group, boolean in) {
		ArrayList<GameObject> gos = group.getChildren();
		for (int i = gos.size() - 1; i >= 0; i--) {
			if (gos.get(i) instanceof Clickable) {
				if (gos.get(i).containsPoint(mouseX, mouseY) && in) {
					if (((Clickable) gos.get(i)).onClickPressed(mouseX, mouseY))
						in = false;
				} else {
					((Clickable) gos.get(i)).onClickOutside();
				}
			}
			if (gos.get(i) instanceof Group) {
				Group g = (Group) gos.get(i);
				mousePressed(mouseX - g.getX(), mouseY - g.getY(), g, g.containsPoint(mouseX, mouseY) && in);
			}
		}
	}

	public void mouseReleased(MouseEvent e) {
		int mouseX = (e.getX()) / scale;
		int mouseY = (e.getY()) / scale;
		mouseReleased(mouseX, mouseY);

		mouseX -= currentStage.getX();
		mouseY -= currentStage.getY();

		mouseReleased(mouseX - currentStage.getX(), mouseY - currentStage.getY(), currentStage, true);
	}

	private void mouseReleased(int mouseX, int mouseY, Group group, boolean in) {
		ArrayList<GameObject> gos = group.getChildren();
		for (int i = gos.size() - 1; i >= 0; i--) {
			if (gos.get(i) instanceof Clickable) {
				Clickable cl = (Clickable) gos.get(i);
				boolean inside = gos.get(i).containsPoint(mouseX, mouseY);
				if (cl.isPressed() && inside && in) {
					if (cl.onClickReleased(mouseX, mouseY))
						break;
				} else if (cl.isPressed()) {
					cl.onclickReleasedOutside();
				}
			}
			if (gos.get(i) instanceof Group) {
				Group g = (Group) gos.get(i);
				mouseReleased(mouseX - g.getX(), mouseY - g.getY(), g, g.containsPoint(mouseX, mouseY) && in);
			}
		}
	}

	public void mouseReleased(int x, int y) {

	}

	public void mousePressed(int x, int y) {

	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() > 256)
			return;
		if (!notifyKeyPressedToNodes(e, currentStage.getChildren())) {
			keys[e.getKeyCode()] = true;
			keyPressed(e.getKeyCode());
		}
	}

	private boolean notifyKeyPressedToNodes(KeyEvent e, ArrayList<GameObject> currentGameObjects) {
		boolean checked = false;
		for (int i = currentGameObjects.size() - 1; i >= 0; i--) {
			if (currentGameObjects.get(i) instanceof Typeable) {
				Typeable tp = (Typeable) currentGameObjects.get(i);
				if (tp.hasFocus()) {

					boolean yes = tp.onKey(e);
					if (yes) {
						checked = true;
						break;
					}
				}
			}
			if (currentGameObjects.get(i) instanceof Group) {
				Group g = (Group) currentGameObjects.get(i);
				checked = notifyKeyPressedToNodes(e, g.getChildren());
			}
		}
		return checked;
	}

	public void keyPressed(int keyCode) {

	}

	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() > 256)
			return;
		keys[e.getKeyCode()] = false;
		keyReleased(e.getKeyCode());

	}

	public void keyReleased(int keyCode) {

	}

	public void mouseDragged(MouseEvent e) {
		int mouseX = (e.getX()) / scale;
		int mouseY = (e.getY()) / scale;

		mouseX -= currentStage.getX();
		mouseY -= currentStage.getY();
		mouseDragged(mouseX - currentStage.getX(), mouseY - currentStage.getY(), currentStage);
			
	}

	private void mouseDragged(int mouseX, int mouseY, Group group) {
		ArrayList<GameObject> gos = group.getChildren();
		for (int i = gos.size() - 1; i >= 0; i--) {
			if (gos.get(i) instanceof Clickable) {
				Clickable cl = (Clickable) gos.get(i);
				cl.onDragged(mouseX, mouseY);
			}
			if (gos.get(i) instanceof Group) {
				Group g = (Group) gos.get(i);
				mouseDragged(mouseX - g.getX(), mouseY - g.getY(), g);
			}
		}
	}
	
	public void mouseMoved(int mouseX, int mouseY) {

	}
	
	public void mouseMoved(MouseEvent e) {
		int mouseX = (e.getX()) / scale;
		int mouseY = (e.getY()) / scale;
		mouseMoved(mouseX, mouseY);

		mouseX -= currentStage.getX();
		mouseY -= currentStage.getY();

		mouseMoved(mouseX - currentStage.getX(), mouseY - currentStage.getY(), currentStage, true);
	}

	

	private void mouseMoved(int mouseX, int mouseY, Group group, boolean in) {
		ArrayList<GameObject> gos = group.getChildren();
		for (int i = gos.size() - 1; i >= 0; i--) {
			if (gos.get(i) instanceof Clickable) {
				Clickable cl = (Clickable) gos.get(i);
				boolean inside = gos.get(i).containsPoint(mouseX, mouseY);
				if (inside && !cl.isMouseOver() && in) {
					cl.onHoverEnter();
				}
				if (!inside && cl.isMouseOver()) {
					cl.onHoverExit();
				}

			}
			if (gos.get(i) instanceof Group) {
				Group g = (Group) gos.get(i);
				mouseMoved(mouseX - g.getX(), mouseY - g.getY(), g, g.containsPoint(mouseX, mouseY) && in);
			}
		}
	}

	public void windowClosed(WindowEvent e) {

	}

	public void windowClosing(WindowEvent e) {

	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		mouseWheelMoved(e, currentStage.getChildren());
	}

	private void mouseWheelMoved(MouseWheelEvent e, ArrayList<GameObject> currentGameObjects) {
		for (int i = currentGameObjects.size() - 1; i >= 0; i--) {
			if (currentGameObjects.get(i) instanceof Scrollable) {
				Scrollable cl = (Scrollable) currentGameObjects.get(i);
				cl.onScroll(e.getScrollAmount()*e.getWheelRotation());
			}
			if (currentGameObjects.get(i) instanceof Group) {
				Group g = (Group) currentGameObjects.get(i);
				mouseWheelMoved(e, g.getChildren());
			}
		}
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
		currentStage.draw(graphics);
	}

	private void updateGameObjects() {
		currentStage.update();
	}

	private void drawParticles() {
		Iterator<Particle> it = particles.iterator();
		while (it.hasNext()) {
			graphics.drawParticle(it.next());
		}
	}

	public void clearParticles() {
		particles.clear();
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

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}
}
