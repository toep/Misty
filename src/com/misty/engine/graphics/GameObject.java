package com.misty.engine.graphics;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import com.misty.engine.physics.Collidable;

public abstract class GameObject implements Comparable<GameObject>, Collidable {
	
	protected float x;
	protected float y;
	protected int z;//used for rendering order
	protected float dx, dy;
	protected Bitmap bm;
	protected Shape shape;//used for collision detection
	protected float rotation;
	protected float rotationPivotX = 0.5f, roationPivotY = 0.5f;//0-1, 0 being top/left and 1 being bottom/right
	public abstract void draw(Renderer r);
	
	public abstract void update();
	
	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void setZ(int z) {
		this.z = z;
	}
	
	@Override
	public int compareTo(GameObject o) {
			return z - o.z;
	}
	
	public boolean possibleCollide(GameObject other) {
		Rectangle2D m = shape.getBounds2D();
		Rectangle2D o = other.shape.getBounds2D();
		float mmx = (float) (x+m.getCenterX());
		float mmy = (float) (y+m.getCenterY());
		float omx = (float) (other.x+o.getCenterX());
		float omy = (float) (other.y+o.getCenterY());
		float dis = (mmx-omx)*(mmx-omx) + (mmy-omy)*(mmy-omy);
		float biggestR = (float)( Math.max(Math.max(m.getWidth(), m.getHeight()), Math.max(o.getWidth(), o.getHeight())));
		return dis <= biggestR * biggestR;
	}
}
