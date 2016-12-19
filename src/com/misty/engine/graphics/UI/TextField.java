package com.misty.engine.graphics.UI;

import java.awt.Shape;
import java.awt.event.KeyEvent;

import com.misty.engine.graphics.Renderer;

public class TextField extends Label implements Clickable, Typeable {

	private boolean focus = false;
	private long time;
	private boolean blink = true;
	private boolean resizable = true;
	private int minSize = 8;//in characters
	private int maxSize = 14;
	public TextField(String string) {
		this(string, 0, 0);
	}
	
	public TextField(String string, int x, int y) {
		setText(string);
		this.x = x;
		this.y = y;
		this.height = 12;
		time = System.currentTimeMillis();
	}

	@Override
	public boolean isPressed() {
		
		return false;
	}

	@Override
	public boolean onClickPressed(int x, int y) {
		focus = true;
		time = System.currentTimeMillis();
		blink = true;
		return true;
	}

	@Override
	public boolean onClickReleased(int x, int y) {
		return true;
	}

	@Override
	public boolean onDragged(int x, int y) {
		return false;
	}

	@Override
	public void onHoverEnter() {
	}

	@Override
	public void onHoverExit() {
	}

	@Override
	public boolean isMouseOver() {
		return false;
	}

	@Override
	public void onclickReleasedOutside() {
	}

	@Override
	public void onClickOutside() {
		focus = false;
	}

	@Override
	public Shape getShape() {
		return null;
	}

	@Override
	public void draw(Renderer r) {
		r.fillColoredRect(x, y, width, height, 0xff323232);
		r.drawColoredRect(x, y, width, height, 0xff222222);

		r.drawString(str, (int)x+2, (int)y+2, color);
		if(focus && (blink || str.length() >= maxSize)) {
			r.drawString("|", x+str.length()*8-1, y+2);
		}
	}

	

	@Override
	public void update() {
		if(System.currentTimeMillis()-time > 300) {
			blink = !blink;
			time = System.currentTimeMillis();
		}
	}

	@Override
	public void onKey(KeyEvent e) {
		if(e.getKeyCode() >= 32 && e.getKeyCode() <= 122 && str.length() < maxSize)
			setText(str+e.getKeyChar());
		else if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
			if(str.length() != 0) {
				setText(str.substring(0, str.length()-1));
			}
		}
	}
	
	public void setText(String str) {
		this.str = str;
		if(str.length() < minSize) {
			this.width = minSize*8+4;
		} else if(str.length() > maxSize) {
			this.width = maxSize*8+4;
		}else 
		this.width = str.length()*8+4;
			
	}

	@Override
	public boolean hasFocus() {
		return focus;
	}

}
