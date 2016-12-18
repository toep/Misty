package com.misty.engine.graphics;

import com.misty.utils.Util;

public class Particle {
	public static final int MOTION_RANDOM1 = 0;
	public static final int MOTION_RANDOM_GRAVITY_DOWN = 1;
	public static final int MOTION_RANDOM_RETURNING = 2;
	public static final int MOTION_RANDOM_ACCELERATING_OUT = 3;
	public static final int MOTION_DIRECTIONAL = 4;

	private float x, y;
	private float dx, dy, ddx = 0, ddy = 0;
	private int duration;
	private static float direction;
	private int color;
	
	public Particle(float x, float y, int motionType, float speed, int duration, int color) {
		this.x = x;
		this.y = y;
		float randomRad = motionType == MOTION_DIRECTIONAL? direction : (float) (Math.random()*Math.PI*2f);
		this.dx = (float) (Math.random()*Util.sin(randomRad)*speed);
		this.dy = (float) (Math.random()*Util.cos(randomRad)*speed);
		if(motionType == MOTION_RANDOM1) {
		
		}
		else if(motionType == MOTION_RANDOM_GRAVITY_DOWN) {
			this.ddy = 0.04f;//gravity
		}
		else if(motionType == MOTION_RANDOM_RETURNING) {
			this.ddx = -dx/50f;
			this.ddy = -dy/50f;
		}
		else if(motionType == MOTION_RANDOM_ACCELERATING_OUT) {
			this.ddx = dx/50f;
			this.ddy = dy/50f;
		}
	
		this.duration = (int) (duration-60 + Math.random()*80);
		this.color = color;
	}

	public void update() {
		x += dx;
		y += dy;
		dx += ddx;
		dy += ddy;
		duration--;
	}

	public int getDuration() {
		return duration;
	}

	public int getX() {
		return (int)x;
	}

	public int getY() {
		return (int)y;
	}

	public int getColor() {
		return color;
	}

	public static void setDirection(float f) {
		direction = f;
	}
	
	
}
