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
	private boolean clickDown = false;
	private int color = 0xffaeaeae;
	private int normalColor = 0xffaeaeae;
	private int pressedColor = 0xffeeeeee;
	private int borderColor = 0xff333333;
	private boolean drawBorder = true;
	private boolean mouseOnButton = false;

	ArrayList<ButtonListener> listeners;
	public Button() {
		title = "";
		width = 20;
		height = 12;
		listeners = new ArrayList<ButtonListener>();
	}
	
	public Button(String str) {
		setText(str);
		setHeight(16);
		
		listeners = new ArrayList<ButtonListener>();
	}
	/**
	 * creates a button with set text and height
	 * @param string
	 * @param height
	 */
	public Button(String string, int height) {
		this(string);
		setHeight(height);
	}
	
	public void setHeight(int h) {
		this.height = h;
	}

	public void setBorderColor(int col) {
		borderColor = col;
	}
	
	public void setDrawBorder(boolean border) {
		drawBorder = border;
	}
	public void setText(String str) {
		title = str;
		width = 10+str.length()*Game.getCurrent().getRenderer().getCurrentFont().getCharacterWidth();
	}
	
	public void setPressedColor(int col) {
		pressedColor = col;
	}
	public void setColor(int col) {
		normalColor = col;
		color = normalColor;
	}
	public void addButtonListener(ButtonListener bl) {
		listeners.add(bl);
	}
	public boolean isPressed() {
		return clickDown;
	}
	
	public void setPosition(float x, float y) {
		super.setPosition(x, y);
	}
	
	@Override
	public Shape getShape() {
		return new Rectangle((int)x, (int)y, width, height);
	}

	@Override
	public void draw(Renderer r) {
		r.fillColoredRect(x, y, width, height, color);
		r.drawString(title, x+5, y+height/2-4);
		
		if(drawBorder)
			r.drawColoredRect(x, y, width, height, borderColor);
		if(mouseOnButton) {
			r.drawColoredRect(x, y, width-1, height-1, borderColor);
		}
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

	@Override
	public void onHoverEnter() {
		mouseOnButton = true;
	}

	@Override
	public void onHoverExit() {
		mouseOnButton = false;
	}
	
	@Override
	public boolean isMouseOver() {
		return mouseOnButton;
	}

	@Override
	public boolean onDragged(int x, int y) {
		return false;
	}

	@Override
	public void onClickOutside() {
	}

}
