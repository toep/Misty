package com.misty.engine.graphics;

import com.misty.engine.graphics.listeners.AnimationListener;
import com.misty.utils.Util;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.ArrayList;

public class Animation extends Sprite {

    private Bitmap[] frames;
    private int index;
    private float fps = 15;
    private long startTime;
    private int startFrame = 0;
    private int endFrame;
    private boolean running = true;
    private boolean repeating = true;
    private ArrayList<AnimationListener> listeners;

    public Animation(Bitmap[] src) {
        super(src[0], 0, 0);//x=0,y=0
        frames = src;
        index = 0;
        endFrame = frames.length;
        startTime = System.currentTimeMillis();
        listeners = new ArrayList<AnimationListener>();
    }

    public void addAnimationListener(AnimationListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Currently only supports animation with static dimension frames
     *
     * @param string location of your image file
     * @param w      width of each frame in the animation
     * @param h      height of each frame in the animation
     * @throws IOException if file not found
     */
    public Animation(String string, int w, int h) throws IOException {
        this(Util.bitmapsFromSheet(Util.createBitmapFromFile(string), w, h));
        width = w;
        height = h;
    }

    /**
     * unused, used for testing with collision detection
     * Should probably be a utility class that takes in a bitmap
     */
    @Deprecated
    private void setupShape() {
        Point[] pts = Util.makePoly(bitmap, 10, 20);
        int[] xs = new int[pts.length];
        int[] ys = new int[pts.length];
        for (int i = 0; i < pts.length; i++) {
            xs[i] = pts[i].x;
            ys[i] = pts[i].y;
        }
        shape = new Polygon(xs, ys, pts.length);
    }

    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void setFPS(int a) {
        fps = a;
    }

    public void setStartFrame(int startFrame) {
        if (startFrame >= 0 && startFrame < frames.length) {
            this.startFrame = startFrame;
            index = startFrame;
        }
    }

    public void setEndFrame(int endFrame) {
        if (endFrame < frames.length)
            this.endFrame = endFrame;
    }

    public void update() {
        if (!running) return;

        long time = System.currentTimeMillis();
        long diff = time - startTime;
        if (diff > 1000 / fps) {
            nextFrame();
            startTime = System.currentTimeMillis();
        }
    }

    public Bitmap currentFrame() {
        return bitmap;
    }

    public void nextFrame() {
        index++;

        if (index >= endFrame) {
            if (repeating)
                index = startFrame;
            else {
                running = false;
                listeners.forEach(e -> e.onCompletion());
                return;
            }
        }

        bitmap = frames[index];
    }

    @Override
    public void draw(Renderer r) {
        r.draw(this, (int) x, (int) y, rotation, scale);
    }

    @Override
    public Shape getShape() {
        AffineTransform af = new AffineTransform();
        //Rectangle r = new Rectangle(img.width, img.height);
        af.translate(x, y);

        return af.createTransformedShape(shape);
    }

    public void reset() {
        running = true;
        index = startFrame;
    }

}
