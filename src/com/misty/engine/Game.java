package com.misty.engine;

import com.misty.engine.graphics.Color;
import com.misty.engine.graphics.*;
import com.misty.engine.graphics.UI.Clickable;
import com.misty.engine.graphics.UI.Scrollable;
import com.misty.engine.graphics.UI.Typeable;
import com.misty.engine.graphics.font.Font;
import com.misty.utils.Util;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Name ideas: Gloomy
 *
 * @author Thomas
 */
public abstract class Game implements Runnable {
    protected float tick;
    protected int width;
    protected int height;
    protected int mouseX;
    protected int mouseY;
    private int scale;
    private boolean fpsLimit = true;
    private Renderer graphics;
    private Frame frame;
    protected boolean running = false;
    private boolean[] keys = new boolean[256];
    private boolean debug = false, showFPS = false;
    private final int KEY_SHOW_FPS = KeyEvent.VK_F3;

    private Thread thread;
    private MyListener listener;
    protected ArrayDeque<Particle> particles = new ArrayDeque<Particle>();
    private boolean shouldClear = true;
    private String name;
    private ArrayList<Stage> stages = new ArrayList<Stage>();

    protected Stage gameStage;
    /**
     * used for other game components to get a copy of the current game
     */
    private static Game currentGame;

    // used for offsetting mouse position, for some reason mac offsets by a few
    // pixels.. this is a simple workaround
    private int mouseXOffset = 0;
    private int mouseYOffset = 0;

    private double deltaupdate = 0;
    private double deltaframes = 0;
    private int frames = 0;
    private int updates = 0;

    private float frameRateTarget = 60;
    private long frameRatePeriod;
    private long frameRateLastNanos;
    private float frameRate = 10;

    private long timer;
    /**
     * used to run code in the update method on main thread if received from
     * network/eventlisteners
     */
    public ConcurrentLinkedQueue<Runnable> actionQueue = new ConcurrentLinkedQueue<>();
    private Stage currentStage;
    private List<Integer> memoryUsedList = new LinkedList<>();
    private ArrayList<MyListener.MInputEvent> events = new ArrayList<>();

    public Game(String name, int width, int height, int scale) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.scale = scale;
        setFrameRate(60);
        String os = System.getProperty("os.name");
        if (os.toLowerCase().contains("mac")) {
            mouseXOffset = -1;
            mouseYOffset = -3;
        }
        graphics = new Renderer(width, height, scale);
        frame = new Frame();

        setupListener();

        frame.add(graphics);
        frame.pack();

        frame.setTitle(name);

        drawLoadingScreen();

        if (currentGame == null) {
            currentGame = this;
        }
        gameStage = new Stage();
        currentStage = gameStage;

        setup();
    }

    public void setDebugMode(boolean debug) {
        this.debug = debug;
    }

    private void setupListener() {
        listener = new MyListener(this);
        frame.addWindowListener(listener);
        frame.addKeyListener(listener);
        graphics.addMouseListener(listener);
        graphics.addMouseMotionListener(listener);
        graphics.addMouseWheelListener(listener);
    }

    public abstract void setup();

    //use this if you need to change functinality of the JFrame object.
    public Frame getFrame() {
        return frame;
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
     * Starts the main game loop and begins game
     */
    public synchronized void start() {
        running = true;
        thread = new Thread(this, "Main Loop");
        thread.start();
    }

    /**
     * gracefully tries to stop the main loop thread
     */
    public synchronized void stop() {
        try {
            thread.interrupt();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    //old gameloop
    /*public void run() {
		long lastTime = System.nanoTime();
		timer = System.currentTimeMillis();
		final double nsUpdate = 1000000000.0 / updaterate;
		final double nsFrames = 1000000000.0 / framerate;
		float lastRenderTime = 0;
		float lastUpdateTime = 0;
		long now;
		// graphics.clear();
		while (running) {
			if(Thread.currentThread().isInterrupted()) {
				System.out.println("Interrupted request..");
				break;
			}
			now = System.nanoTime();
			deltaupdate += (now - lastTime) / nsUpdate;
			deltaframes += (now - lastTime) / nsFrames;
			lastTime = now;

			if (deltaupdate > 3)
				deltaupdate = 3;
			while (deltaupdate >= 1) {
				doUpdate();
			}
			if (fpsLimit)
				while (deltaframes >= 1) {
					doRender();
					lastRenderTime = now;
				}
			else {
				doRender();
			}


			if (System.currentTimeMillis() - timer >= 1000) {
				resetFPSValues();
				doMemoryLogging();
			}

		}
		System.exit(0);
	}*/

    @Override
    public void run() {  // not good to make this synchronized, locks things up
        long beforeTime = System.nanoTime();
        long overSleepTime = 0L;

        int noDelays = 0;
        // Number of frames with a delay of 0 ms before the
        // animation thread yields to other running threads.
        final int NO_DELAYS_PER_YIELD = 15;

        while ((Thread.currentThread() == thread) && running) {

            doUpdate();
            doRender();

            long afterTime = System.nanoTime();
            long timeDiff = afterTime - beforeTime;
            long sleepTime = (frameRatePeriod - timeDiff) - overSleepTime;

            if (sleepTime > 0) {  // some time left in this cycle
                try {
                    Thread.sleep(sleepTime / 1000000L, (int) (sleepTime % 1000000L));
                    noDelays = 0;  // Got some sleep, not delaying anymore
                } catch (InterruptedException ex) {
                }

                overSleepTime = (System.nanoTime() - afterTime) - sleepTime;

            } else {    // sleepTime <= 0; the frame took longer than the period
                overSleepTime = 0L;
                noDelays++;

                if (noDelays > NO_DELAYS_PER_YIELD) {
                    Thread.yield();   // give another thread a chance to run
                    noDelays = 0;
                }
            }

            beforeTime = System.nanoTime();
        }

    }

    private void doUpdate() {
        update();
        updateActions();
        updateEvents();
        updateParticles();
        updateGameObjects();
        deltaupdate--;
        updates++;
        tick += .01f;
    }

    private void updateEvents() {
        synchronized (events) {
            for (MyListener.MInputEvent e : events) {
                if (e.event instanceof MouseEvent) {
                    MouseEvent me = (MouseEvent) e.event;
                    switch (e.type) {
                        case MyListener.MInputEvent.MOUSE_PRESS:
                            mousePressed(me);
                            break;
                        case MyListener.MInputEvent.MOUSE_RELEASE:
                            mouseReleased(me);
                            break;
                        case MyListener.MInputEvent.MOUSE_DRAG:
                            mouseDragged(me);
                            break;
                        case MyListener.MInputEvent.MOUSE_MOVE:
                            mouseMoved(me);
                            break;
                        case MyListener.MInputEvent.MOUSE_ENTER:
                            mouseEntered(me);
                            break;
                        case MyListener.MInputEvent.MOUSE_EXIT:
                            mouseExited(me);
                            break;
                        case MyListener.MInputEvent.MOUSE_WHEEL:
                            mouseWheelMoved((MouseWheelEvent) me);
                            break;
                    }
                } else if (e.event instanceof KeyEvent) {
                    KeyEvent ke = (KeyEvent) e.event;
                    switch (e.type) {
                        case MyListener.MInputEvent.KEY_PRESS:
                            keyPressed(ke);
                            break;
                        case MyListener.MInputEvent.KEY_RELEASE:
                            keyReleased(ke);
                            break;
                    }
                }
            }
            events.clear();
        }
    }


    private void doRender() {
        long now = System.nanoTime();

        double rate = 1000000.0 / ((now - frameRateLastNanos) / 1000000.0);
        float instantaneousRate = (float) (rate / 1000.0);
        frameRate = (frameRate * 0.9f) + (instantaneousRate * 0.1f);

        if (shouldClear)
            graphics.clear();
        graphics.setRenderingMode(Renderer.RENDERING_MODE_NORMAL);
        drawGameObjects();
        drawParticles();
        draw(graphics);
        if (showFPS) {
            drawFPS();
        }
        graphics.render();
        deltaframes--;
        frames++;
        frameRateLastNanos = now;

    }

    private void drawFPS() {
        String fps = "FPS: " + (int) frameRate;
        graphics.fillColoredRect(0, 0, fps.length() * getRenderer().getCurrentFont().getCharacterWidth(), 13,
                Color.BLACK);
        graphics.drawString(fps, 0, 2, Color.WHITE);
        if (debug) {
            if (memoryUsedList.size() > 0) {
                float wid = 100f / memoryUsedList.size();
                float maxMem = memoryUsedList.stream().max(Integer::compare).get() / 40f;
                for (int i = 0; i < memoryUsedList.size(); i++) {
                    int h = (int) (memoryUsedList.get(i) / maxMem);
                    graphics.fillColoredRect((int) (i * wid), 55 - h, Math.round(wid), h, Color.GREEN);
                }
                graphics.drawString("" + String.format("%.2f mb", memoryUsedList.get(memoryUsedList.size() - 1) / 1024f), 5, 45);
                graphics.drawColoredRect(0, 14, 100, 42, Color.BLACK);
            }
        }
    }

    private void doMemoryLogging() {
        Runtime runtime = Runtime.getRuntime();

        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();

        long memorySpent = allocatedMemory - freeMemory;

        memoryUsedList.add((int) (memorySpent / 1024));
        if (memoryUsedList.size() > 60) {
            memoryUsedList.remove(0);
        }
    }


    private void updateActions() {
        Runnable r;
        while (!actionQueue.isEmpty()) {
            r = actionQueue.poll();
            r.run();
        }
    }

    /**
     * sets the color the screen should clear with every frame
     *
     * @param c color
     */
    public void setClearColor(Color c) {
        graphics.setClearColor(c);
    }

    public void setFont(Font font) {
        graphics.setFont(font);
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
            System.err.println("Unable to load cursor image " + name + ". Make sure it's in the correct folder!");
        }
    }

    protected void setClearEveryFrame(boolean a) {
        shouldClear = a;
    }

    /**
     * this is frames per second, i.e. how many times the draw() method is
     * called
     */
    public void setFrameRate(int fps) {
        frameRateTarget = fps;
        frameRatePeriod = (long) (1000000000.0 / frameRateTarget);
    }

    public void setFPSLimit(boolean l) {
        fpsLimit = l;
    }

    public void setKeyDown(int keyCode, boolean a) {
        keys[keyCode] = a;
    }

    public void setStage(Stage stage) {
        currentStage = stage;
        if (currentStage == null) {
            currentStage = gameStage;
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getName() {
        return name;
    }

    public float getTick() {
        return tick;
    }

    public static Game getCurrent() {
        return currentGame;
    }

    public Renderer getRenderer() {
        return graphics;
    }

    public abstract void draw(Renderer g);

    public abstract void update();

    public void clearParticles() {
        particles.clear();
    }

    private void mousePressed(int mouseX, int mouseY, Group group, boolean in) {
        ArrayList<GameObject> gos = group.getChildren();
        for (int i = gos.size() - 1; i >= 0; i--) {
            if (gos.get(i).isEnabled()) {
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
    }

    private void mouseReleased(int mouseX, int mouseY, Group group, boolean in) {
        ArrayList<GameObject> gos = group.getChildren();
        for (int i = gos.size() - 1; i >= 0; i--) {
            if (gos.get(i).isEnabled()) {
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
    }

    private boolean notifyKeyPressedToNodes(KeyEvent e, ArrayList<GameObject> currentGameObjects) {
        boolean checked = false;
        for (int i = currentGameObjects.size() - 1; i >= 0; i--) {
            if (currentGameObjects.get(i).isEnabled()) {
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
        }
        return checked;
    }

    private void mouseDragged(int mouseX, int mouseY, Group group) {
        ArrayList<GameObject> gos = group.getChildren();
        for (int i = gos.size() - 1; i >= 0; i--) {
            if (gos.get(i).isEnabled()) {
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
    }

    private void mouseMoved(int mouseX, int mouseY, Group group, boolean in) {
        ArrayList<GameObject> gos = group.getChildren();
        for (int i = gos.size() - 1; i >= 0; i--) {
            if (gos.get(i).isEnabled()) {
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
    }

    private void mouseWheelMoved(MouseWheelEvent e, ArrayList<GameObject> currentGameObjects) {
        for (int i = currentGameObjects.size() - 1; i >= 0; i--) {
            if (currentGameObjects.get(i).isEnabled()) {
                if (currentGameObjects.get(i) instanceof Scrollable) {
                    Scrollable cl = (Scrollable) currentGameObjects.get(i);
                    cl.onScroll(e.getScrollAmount() * e.getWheelRotation());
                }
                if (currentGameObjects.get(i) instanceof Group) {
                    Group g = (Group) currentGameObjects.get(i);
                    mouseWheelMoved(e, g.getChildren());
                }
            }
        }
    }

	/*
	 *protected mouse/key event methods 
	 */

    protected void mouseDragged(int x, int y) {

    }

    protected void onWindowClosed() {

    }

    protected void mouseEntered(int x, int y) {

    }

    protected void mouseReleased(int x, int y) {

    }

    protected void mousePressed(int x, int y) {

    }

    protected void keyPressed(int keyCode) {

    }

    protected void keyReleased(int keyCode) {

    }

    protected void mouseMoved(int mouseX, int mouseY) {

    }

    protected void mouseWheelMoved(int wheelRotation) {

    }

    protected void onWindownClosing() {

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

    private void drawLoadingScreen() {
        graphics.fill(Color.BLACK);
        graphics.drawString("Loading assets", width / 2 - 56, height / 2, Color.WHITE);
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

    void mousePressed(MouseEvent e) {
        int mouseX = (e.getX() + mouseXOffset) / scale;
        int mouseY = (e.getY() + mouseYOffset) / scale;

        mousePressed(mouseX, mouseY);

        mouseX -= currentStage.getX();
        mouseY -= currentStage.getY();
        if (e.getButton() == MouseEvent.BUTTON1)
            mousePressed(mouseX - currentStage.getX(), mouseY - currentStage.getY(), currentStage, true);

    }

    void mouseReleased(MouseEvent e) {
        int mouseX = (e.getX() + mouseXOffset) / scale;
        int mouseY = (e.getY() + mouseYOffset) / scale;
        mouseReleased(mouseX, mouseY);

        mouseX -= currentStage.getX();
        mouseY -= currentStage.getY();

        mouseReleased(mouseX - currentStage.getX(), mouseY - currentStage.getY(), currentStage, true);
    }

    void keyPressed(KeyEvent e) {
        if (e.getKeyCode() > 256)
            return;
        if (!notifyKeyPressedToNodes(e, currentStage.getChildren())) {
            keys[e.getKeyCode()] = true;
            keyPressed(e.getKeyCode());
        }
        if (e.getKeyCode() == KEY_SHOW_FPS) {
            showFPS = !showFPS;
        }
    }

    void keyReleased(KeyEvent e) {
        if (e.getKeyCode() > 256)
            return;
        keys[e.getKeyCode()] = false;
        keyReleased(e.getKeyCode());

    }

    void mouseDragged(MouseEvent e) {
        int mouseX = (e.getX() + mouseXOffset) / scale;
        int mouseY = (e.getY() + mouseYOffset) / scale;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        mouseDragged(mouseX, mouseY);
        mouseX -= currentStage.getX();
        mouseY -= currentStage.getY();
        mouseDragged(mouseX - currentStage.getX(), mouseY - currentStage.getY(), currentStage);

    }

    void mouseMoved(MouseEvent e) {
        int mouseX = (e.getX() + mouseXOffset) / scale;
        int mouseY = (e.getY() + mouseYOffset) / scale;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        mouseMoved(mouseX, mouseY);

        mouseX -= currentStage.getX();
        mouseY -= currentStage.getY();

        mouseMoved(mouseX - currentStage.getX(), mouseY - currentStage.getY(), currentStage, true);
    }

    private void mouseExited(MouseEvent e) {

    }

    void windowClosed(WindowEvent e) {
        onWindowClosed();
    }

    void windowClosing(WindowEvent e) {
        onWindownClosing();
    }

    void mouseWheelMoved(MouseWheelEvent e) {
        mouseWheelMoved(e, currentStage.getChildren());
        mouseWheelMoved(e.getWheelRotation());
    }

    void mouseEntered(MouseEvent e) {
        int mouseX = (e.getX() + mouseXOffset) / scale;
        int mouseY = (e.getY() + mouseYOffset) / scale;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        mouseEntered(mouseX, mouseY);
    }

    public void addEvent(MyListener.MInputEvent e) {
        synchronized (events) {
            events.add(e);
        }
    }
}
