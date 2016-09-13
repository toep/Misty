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
	public Animation(String string, int i, int j) throws IOException {
		this(Util.bitmapsFromSheet(Util.createBitmapFromFile(string), i, j));
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

	public void draw(Renderer g, int x, int y, float scale) {
		g.drawBitmap(bm, x, y, scale);
	}
	public void drawRotated(Renderer g, int x, int y, float rot) {
		g.draw(this, x, y, rot, 1f);
	}

	@Override
	public void draw(Renderer r) {
		r.drawBitmap(bm, (int)x, (int)y);
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
