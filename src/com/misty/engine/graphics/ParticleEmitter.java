package com.misty.engine.graphics;

import java.awt.Shape;

import com.misty.engine.Game;

public class ParticleEmitter extends GameObject {

	private int type = Particle.MOTION_RANDOM1;
	private Color color = Color.BLACK;
	private int count = 1;
	private int lifetime = 50;
	private float speed = 1f;
	public ParticleEmitter(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void setParticleType(int type) {
		this.type = type;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}

	@Override
	public Shape getShape() {
		return null;
	}

	@Override
	public void draw(Renderer r) {
	}

	@Override
	public void update() {
		for(int i = 0; i < count; i++) {
			//Particle.setDirection((float) Game.getCurrent().getTick()*5);
			Game.getCurrent().addParticle(new Particle(x, y, type, speed, lifetime, color));	
		}
	}

	public void setParticleCount(int f) {
		count = f;
	}

	public void setParticleLifetime(int i) {
		lifetime = i;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}
}
