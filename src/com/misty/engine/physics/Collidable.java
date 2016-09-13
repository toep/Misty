package com.misty.engine.physics;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

public interface Collidable {
	
	public default CollisionResult intersectsWith(Collidable other) {
		Area a = new Area(getShape());
		a.intersect(new Area(other.getShape()));
		CollisionResult r = new CollisionResult();
		r.intersects = !a.isEmpty();
		if(r.intersects) {
			Rectangle2D b = a.getBounds2D();
			r.intersectionPoint = new Point((int)b.getCenterX(), (int)b.getCenterY());
		}
		return r;
	}
	
	public Shape getShape();
	
	
}
