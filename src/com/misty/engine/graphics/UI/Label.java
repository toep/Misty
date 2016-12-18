package com.misty.engine.graphics.UI;

import java.awt.Shape;

import com.misty.engine.graphics.GameObject;
import com.misty.engine.graphics.Renderer;

public class Label extends GameObject {

	private String str;
	private int color = 0xffffffff;
	
	public Label(String str, int x, int y) {
		this.str = str;
		this.x = x;
		this.y = y;
	}
	public void setText(String str) {
		this.str = str;
	}
	public void setColor(int color) {
		this.color = color;
	}
	public Label() {
		this("", 0, 0);
	}
	@Override
	public Shape getShape() {
		return null;
	}

	@Override
	public void draw(Renderer r) {
		r.drawString(str, (int)x, (int)y, color);
	}

	@Override
	public void update() {
	}

	
}
