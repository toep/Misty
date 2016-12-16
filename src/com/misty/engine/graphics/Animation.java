package com.misty.engine.graphics;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.io.IOException;

import com.misty.engine.Game;
import com.misty.utils.Util;

public class Animation extends GameObject {
	
	private Bitmap[] frames;
	private int index;
	private float fps = 15;
	private long startTime;
	private int startFrame = 0;
	private int endFrame;
	public Animation(Bitmap[] src) {
		frames = src;
		bm = frames[0];
		index = 0;
		endFrame = frames.length;
		startTime = System.currentTimeMillis();
		setupShape();
	}
	/**
	 * 
	 * @param string location of your image file
	 * @param w width of each frame in the animation
	 * @param h height of each frame in the animation
	 * @throws IOException if file not found
	 */
	public Animation(String string, int w, int h) throws IOException {
		this(Util.bitmapsFromSheet(Util.createBitmapFromFile(string), w, h));
	}
	private void setupShape() {
		Point[] pts = Util.makePoly(bm, 10, 20);
		int[] xs = new int[pts.length];
		int[] ys = new int[pts.length];
		for(int i = 0; i < pts.length; i++) {
			xs[i] = pts[i].x;
			ys[i] = pts[i].y;
		}
		shape = new Polygon(xs, ys, pts.length);
	}
	public void setFPS(int a) {
		fps = a;
	}
	public void setStartFrame(int startFrame) {
		if(startFrame >= 0 && startFrame < frames.length) {
			this.startFrame = startFrame;
			index = startFrame;			
		}
	}

	public void setEndFrame(int endFrame) {
		if(endFrame < frames.length)
			this.endFrame = endFrame;
	}
	
	public void update() {
		long time = System.currentTimeMillis();
		long diff = time - startTime;
		if(diff > 1000/fps) {
			nextFrame();
			startTime = System.currentTimeMillis();
		}
	}
	
	public Bitmap currentFrame() {
		return bm;
	}
	
	public void nextFrame() {
		index++;
		if(index >= endFrame) index = startFrame;
		bm = frames[index];
	}

	@Override
	public void draw(Renderer r) {
		r.draw(this, (int)x, (int)y, rotation, scale);
	}

	@Override
	public Shape getShape() {
		AffineTransform af = new AffineTransform();
		//Rectangle r = new Rectangle(img.width, img.height);
		af.translate(x, y);
		
		af.rotate(Game.tick/2, bm.getWidth()/2, bm.getHeight()/2);
		return af.createTransformedShape(shape);
	}
	
}
