package com.misty.engine.physics;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public interface Collidable {

    default CollisionResult intersectsWith(Collidable other) {
        Area a = new Area(getShape());
        a.intersect(new Area(other.getShape()));
        CollisionResult r = new CollisionResult();
        r.intersects = !a.isEmpty();
        if (r.intersects) {
            Rectangle2D b = a.getBounds2D();
            r.intersectionPoint = new Point((int) b.getCenterX(), (int) b.getCenterY());
        }
        return r;
    }

    default Shape getShape() {
        return new Ellipse2D.Float(0, 0, 0, 0);
    }


}
