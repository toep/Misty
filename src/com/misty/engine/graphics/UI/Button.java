package com.misty.engine.graphics.UI;

import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;

import com.misty.engine.Game;
import com.misty.engine.graphics.GameObject;
import com.misty.engine.graphics.Renderer;
import com.misty.engine.graphics.UI.listeners.ButtonListener;

public class Button extends GameObject implements Clickable{

	private String title;
	Rectangle rect;
	private boolean clickDown = false;
	private int color = 0xffaeaeae;
	private int normalColor = 0xffaeaeae;
	private int pressedColor = 0xffeeeeee;
	ArrayList<ButtonListener> listeners;
	public Button() {
		title = "";
		width = 10;
		height = 10;
		listeners = new ArrayList<ButtonListener>();
	}
	
	public Button(String str) {
		rect = new Rectangle(0, 0, 0, 0);
		setText(str);
		height = 16;
		
		listeners = new ArrayList<ButtonListener>();
	}
	public void setText(String str) {
		title = str;
		width = 10+str.length()*Game.getCurrent().getRenderer().getCurrentFont().getCharacterWidth();
		rect.width = width;
	}
	
	public void setPressedColor(int col) {
		pressedColor = col;
	}
	public void setColor(int col) {
		normalColor = col;
	}
	public void addButtonListener(ButtonListener bl) {
		listeners.add(bl);
	}
	public boolean isPressed() {
		return clickDown;
	}
	
	public void setPosition(float x, float y) {
		super.setPosition(x, y);
		rect.x = (int)x;
		rect.y = (int)y;
	}
	
	@Override
	public Shape getShape() {
		return rect;
	}

	@Override
	public void draw(Renderer r) {
		r.fillColoredRect(x, y, width, height, color);
		r.drawString(title, x+5, y+height/2-4);
		r.drawColoredRect(x, y, width, height, 0xffffffff);
	}

	@Override
	public void update() {
	}
	
	public void press() {
		clickDown = true;
		color = pressedColor;
	}
	
	public void release() {
		clickDown = false;
		color = normalColor;
	}

	@Override
	public boolean onClickPressed(int x, int y) {
		press();
		//listeners.forEach(e -> e.onPress());
		return true;
	}
	
	@Override
	public boolean onClickReleased(int x, int y) {
		release();
		listeners.forEach(e -> e.onPress());
		return true;
	}
	
	public void onclickReleasedOutside() {
		release();

	}

}
