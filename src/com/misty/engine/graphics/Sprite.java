package com.misty.engine.graphics;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.io.IOException;

import com.misty.utils.Util;
import com.misty.utils.Vector2;

public class Sprite extends GameObject {

	protected float rotationDelta = 0f;
	public Sprite(Bitmap bm, float x, float y) {
		this.bm = bm;
		this.x = x;
		this.y = y;
		this.width = bm.getWidth();
		this.height = bm.getHeight();
	}
	
	/**
	 * 
	 * @param s the name of the image file
	 * @param x initial x position
	 * @param y initial y position
	 * @throws IOException file not found
	 */
	public Sprite(String s, float x, float y) throws IOException {
		this(new Bitmap(s), x, y);
		
	}
	
	public void makeCollidable() {
		setupShape();
	}
	
	private void setupShape() {
		Point[] pts = Util.makePoly(bm, 1, 10);
		int[] xs = new int[pts.length];
		int[] ys = new int[pts.length];
		for(int i = 0; i < pts.length; i++) {
			xs[i] = pts[i].x;
			ys[i] = pts[i].y;
		}
		shape = new Polygon(xs, ys, pts.length);
	}
	
	@Override
	public void draw(Renderer r) {
		r.draw(this, (int)x, (int)y, rotation, scale);
	}

	@Override
	public void update() {
		rotation += rotationDelta;
		if(rotation > Math.PI*2) rotation-=Math.PI*2;
		if(rotation < -Math.PI*2) rotation+=Math.PI*2;
		//rotationDelta*=.98f;
		x+=dx;
		y+=dy;
		//dx*=.98f;
		//dy*=.98f;
	}
	
	
	@Override
	public Shape getShape() {
		AffineTransform af = new AffineTransform();
		//Rectangle r = new Rectangle(img.width, img.height);
		af.translate(x, y);
		af.rotate(rotation, width/2, height/2);
		return af.createTransformedShape(shape);
	}
	public void applyTorque(Vector2 pt, float dir, float i) {
		Vector2 v1 = new Vector2(Util.cos(dir), Util.sin(dir));
		v1 = v1.mul(i/20000f);
		int centerX = (int) (x+width/2);
		int centerY = (int) (y+height/2);
		Vector2 momentArm = new Vector2(pt.x-centerX, centerY-pt.y);
		float totRot = 0;
		totRot+=v1.y*momentArm.x;
	
		totRot+=v1.x*momentArm.y;
		rotationDelta+=totRot;
		dx-=v1.x;
		dy-=v1.y;
	}

}
