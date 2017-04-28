package com.misty.engine.graphics.UI;

import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;

import com.misty.engine.Game;
import com.misty.engine.graphics.Color;
import com.misty.engine.graphics.GameObject;
import com.misty.engine.graphics.Renderer;
import com.misty.engine.graphics.UI.listeners.ButtonListener;

public class Button extends GameObject implements Clickable{

	private String title;
	private boolean clickDown = false;
	private Color color = new Color(0xffaeaeae);
	private Color normalColor = new Color(0xffaeaeae);
	private Color pressedColor = new Color(0xffeeeeee);
	private Color borderColor = new Color(0xff333333);
	private boolean drawBorder = true;
	private boolean mouseOnButton = false;

	ArrayList<ButtonListener> listeners;
	
	private int disabledMask = 0xff888888;
	private Color titleColor = new Color(0xffffffff);
	private Color titleColorDisabled = new Color(titleColor.rgb&disabledMask);
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
	 * creates a button with set text and position
	 * @param string
	 * @param x
	 * @param y
	 */
	public Button(String string, int x, int y) {
		this(string);
		setHeight(16);
		setPosition(x, y);
	}
	
	public void setHeight(int h) {
		this.height = h;
	}

	public void setBorderColor(Color col) {
		borderColor = col;
	}
	
	public void setDrawBorder(boolean border) {
		drawBorder = border;
	}
	public void setText(String str) {
		title = str;
		width = 10+str.length()*Game.getCurrent().getRenderer().getCurrentFont().getCharacterWidth();
	}
	
	public void setPressedColor(Color col) {
		pressedColor = col;
	}
	public void setColor(Color col) {
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
		if(!enabled) {
			r.drawString(title, x+width/2-title.length()*Game.getCurrent().getRenderer().getCurrentFont().getCharacterWidth()/2, y+height/2-4, titleColorDisabled);
		}else
		r.drawString(title, x+width/2-title.length()*Game.getCurrent().getRenderer().getCurrentFont().getCharacterWidth()/2, y+height/2-4, titleColor);
		
		if(drawBorder) {
			if(enabled) {
			r.drawColoredRect(x, y, width, height, borderColor);
			}else {
				//r.drawColoredRect(x, y, width, height, Color.temp(borderColor.rgb^0xff000000));
			}
		}
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

	public void setTitleColor(Color color) {
		titleColor  = color;
		titleColorDisabled = new Color(color.rgb&disabledMask);
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		mouseOnButton = false;
	}

}
