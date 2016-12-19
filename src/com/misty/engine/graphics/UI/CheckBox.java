package com.misty.engine.graphics.UI;

import java.awt.Shape;
import java.util.ArrayList;

import com.misty.engine.graphics.GameObject;
import com.misty.engine.graphics.Renderer;
import com.misty.engine.graphics.UI.listeners.CheckListener;

public class CheckBox extends GameObject implements Clickable {

	private boolean checked = false;
	private boolean clickDown = false;
	private boolean hovered = false;

	private ArrayList<CheckListener> listeners = new ArrayList<CheckListener>();
	public CheckBox(int x, int y) {
		this.x = x;
		this.y = y;
		width = 12;
		height = 12;
	}
	
	public boolean isChecked() {
		return checked;
	}
	@Override
	public Shape getShape() {
		return null;
	}

	@Override
	public boolean isPressed() {
		return clickDown;
	}

	@Override
	public boolean onClickPressed(int x, int y) {
		clickDown = true;
		return true;
	}

	@Override
	public boolean onClickReleased(int x, int y) {
		checked = !checked;
		clickDown = false;
		listeners.forEach(e -> e.checked(checked));
		return false;
	}

	@Override
	public void onHoverEnter() {
		hovered = true;
	}

	@Override
	public void onHoverExit() {
		hovered = false;
	}

	@Override
	public boolean isMouseOver() {
		return hovered;
	}

	@Override
	public void onclickReleasedOutside() {
	}

	@Override
	public void draw(Renderer r) {
		r.fillColoredRect(x, y, width, height, 0xffaeaeae);
		r.drawColoredRect(x, y, width, height, 0xff323232);
		if(hovered)
			r.drawColoredRect(x, y, width-1, height-1, 0xff323232);

		if(checked) {
			r.drawString("x", x+2, y+2);
		}
	}

	@Override
	public void update() {
	}

	public void addCheckListener(CheckListener e) {
		listeners.add(e);
	}

	@Override
	public boolean onDragged(int x, int y) {
		return false;
	}

	@Override
	public void onClickOutside() {
	}

}
