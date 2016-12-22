package com.misty.engine.graphics.UI;

import java.awt.Shape;

import com.misty.engine.Game;
import com.misty.engine.graphics.GameObject;
import com.misty.engine.graphics.Renderer;

public class Label extends GameObject {

	protected String str;
	protected int color = 0xffffffff;
	
	public Label(String str, int x, int y) {
		this.str = str;
		this.x = x;
		this.y = y;
		this.width = str.length()*Game.getCurrent().getRenderer().getCurrentFont().getCharacterWidth();
		this.height = Game.getCurrent().getRenderer().getCurrentFont().getCharacterHeight();
	}
	public void setText(String str) {
		this.str = str;
		this.width = str.length()*Game.getCurrent().getRenderer().getCurrentFont().getCharacterWidth();
	}
	public String getText() {
		return str;
	}
	public void setColor(int color) {
		this.color = color;
	}
	public Label() {
		this("", 0, 0);
	}
	public Label(String str) {
		this(str, 0, 0);
	}
	@Override
	public Shape getShape() {
		return null;
	}

	@Override
	public void draw(Renderer r) {
		r.drawString(str, (int)x, (int)y+2, color, scale);
	}

	@Override
	public void update() {
	}

	
}
