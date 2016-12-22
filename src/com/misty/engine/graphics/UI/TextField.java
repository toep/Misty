package com.misty.engine.graphics.UI;

import java.awt.Shape;
import java.awt.event.KeyEvent;

import com.misty.engine.Game;
import com.misty.engine.graphics.Renderer;
import com.misty.listeners.Keys;

public class TextField extends Label implements Clickable, Typeable {

	private boolean focus = false;
	private long time;
	private boolean blink = true;
	private boolean resizable = true;
	private int maxSize = 14;
	private int caret = 0;
	
	public TextField(String string) {
		this(string, 0, 0);
	}
	
	public TextField(String string, int x, int y) {
		setText(string);
		this.x = x;
		this.y = y;
		this.height = 12;
		caret = string.length();

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
		int localX = (int) (x-this.x);
		caret = localX/Game.getCurrent().getRenderer().getCurrentFont().getCharacterWidth();
		if(caret < 0) caret = 0;
		if(caret > str.length()) caret = str.length();
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
		if(focus && (blink || (str.length() >= maxSize))) {
			r.drawString("|", x+caret*Game.getCurrent().getRenderer().getCurrentFont().getCharacterWidth()-1, y+2);
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
	public boolean onKey(KeyEvent e) {
		if((e.getKeyCode() == 32 || (e.getKeyCode() >= 42 && e.getKeyCode() <= 122)) && str.length() < maxSize) {
			String before = str.substring(0, caret);
			String after = str.substring(caret, str.length());
			setText(before + e.getKeyChar() + after);
			caret++;
			
		}
		else if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
			if(str.length() != 0 && caret != 0) {
				setText(str.substring(0, caret-1) + str.substring(caret, str.length()));
				caret--;
			}
		}
		else if(e.getKeyCode() == KeyEvent.VK_DELETE) {
			if(str.length() != 0 && caret != str.length()) {
				setText(str.substring(0, caret) + str.substring(caret+1, str.length()));
				//caret--;
			}
		}
		if(e.getKeyCode() == Keys.LEFT) {
			caret--;
			if(caret < 0) caret = 0;
		}
		else if(e.getKeyCode() == Keys.RIGHT) {
			caret++;
			if(caret > str.length()) caret = str.length();
		}
		return true;
	}
	
	public void setText(String str) {
		this.str = str;
		//if(str.length() < 1) {
			//this.width = 1*Game.getCurrent().getRenderer().getCurrentFont().getCharacterWidth()+4;
		//} else if(str.length() > maxSize) {
			this.width = maxSize*Game.getCurrent().getRenderer().getCurrentFont().getCharacterWidth()+4;
		//}else 
		//this.width = str.length()*Game.getCurrent().getRenderer().getCurrentFont().getCharacterWidth()+4;
			
	}

	@Override
	public boolean hasFocus() {
		return focus;
	}

}
