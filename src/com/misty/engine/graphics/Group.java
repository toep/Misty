package com.misty.engine.graphics;

import java.awt.Shape;
import java.util.ArrayList;

import com.misty.engine.Game;

public class Group extends GameObject {

	protected ArrayList<GameObject> children = new ArrayList<GameObject>();
	
	public Group() {
		
	}
	public Group(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public void add(GameObject gameObject) {
		children.add(gameObject);
	}

	public ArrayList<GameObject> getChildren() {
		return children;
	}
	public void setbackgroundColor(int i) {
	}
	
	@Override
	public Shape getShape() {
		return null;
	}

	@Override
	public void draw(Renderer r) {
		Game.getCurrent().getRenderer().translate(x, y);
		children.forEach(e -> e.draw(r));
		Game.getCurrent().getRenderer().translate(-x, -y);
	}

	@Override
	public void update() {
		children.forEach(e -> e.update());

	}
}
