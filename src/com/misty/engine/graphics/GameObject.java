package com.misty.engine.graphics;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import com.misty.engine.physics.Collidable;

public abstract class GameObject implements Comparable<GameObject>, Collidable {
	
	protected float x;
	protected float y;
	protected int z;//used for rendering order
	protected float dx, dy;
	protected int width, height;
	protected Shape shape;//used for collision detection
	protected float rotation = 0;
	protected float scale = 1;
	protected float rotationPivotX = 0.5f, roationPivotY = 0.5f;//0-1, 0 being top/left and 1 being bottom/right
	protected boolean enabled = true;
	public abstract void draw(Renderer r);
	
	public abstract void update();
	
	/**
	 * Sets the position of the gameobject
	 * @param x
	 * @param y
	 */
	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	/**
	 * Sets the pivot of rotation of the gameobject
	 * @param rpx (0,1), 0 being left, 1 being right
	 * @param rpy (0,1), 0 being top, 1 being bottom
	 */
	public void setPivot(float rpx, float rpy) {
		this.rotationPivotX = rpx;
		this.roationPivotY = rpy;
	}
	
	public float getRotationPivotX() {
		return rotationPivotX;
	}
	
	public float getRotationPivotY() {
		return roationPivotY;
	}
	
	/**
	 * sets the z position, for rendering order
	 * @param z
	 */
	public void setZ(int z) {
		this.z = z;
	}
	
	public void setRotation(float rot) {
		rotation  = rot;
	}
	public void setScale(float s) {
		scale  = s;
	}
	public float getScale() {
		return scale;
	}

	public float getRotation() {
		return rotation;
	}
	
	public int getX() {
		return (int) x;
	}
	
	public int getY() {
		return (int) y;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
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

	/**
	 * checks if a point in contained in this object
	 * @param x
	 * @param y
	 * @return true if it contains the point x, y
	 */
	public boolean containsPoint(int x, int y) {
		return x >= this.x && x < this.x + width && y >= this.y && y < this.y + height;
	}
	
	public boolean intersects(GameObject other) {
		Rectangle t = collisionRectangle();
		Rectangle o = other.collisionRectangle();
		return t.intersects(o);
	}
	
	public Rectangle collisionRectangle() {
		return new Rectangle((int)x, (int)y, width, height);
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public boolean isEnabled() {
		return enabled;
	}
}
