package com.misty.engine.graphics;

import java.awt.Shape;

import com.misty.engine.Game;

public class ParticleEmitter extends GameObject {

	public ParticleEmitter(int x, int y) {
		this.x = x;
		this.y = y;
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
		Particle.setDirection((float) Game.tick*5);
		Game.getCurrent().addParticle(new Particle(x, y, Particle.MOTION_RANDOM_RETURNING, 1, 200, 0xff000000));
	}

}
