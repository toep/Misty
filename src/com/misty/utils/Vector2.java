package com.misty.utils;

import java.awt.Point;

public class Vector2 {
	public float x, y;
	
	public Vector2(float a, float b) {
		this.x = a;
		this.y = b;
	}
	
	public Vector2(Point p) {
		this.x = p.x;
		this.y = p.y;
	}
	
	public void normalize() {
		float mag = magnitude();
		x/=mag;
		y/=mag;
	}
	
	public Vector2 sub(Vector2 o) {
		return new Vector2(x-o.x, y-o.y);
	}

	public float magnitude() {
		return (float) Math.sqrt(x*x + y*y);
	}

	public float dot(Vector2 v) {
		return x*v.x + y*v.y;
	}

	public Vector2 mul(float i) {
		return new Vector2(x*i, y*i);
	}
	
	public String toString() {
		
		return "<" + x + ", " + y + ">";
	}
}
