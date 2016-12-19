package com.misty.engine.graphics.UI;

import java.awt.Color;
import java.awt.Shape;
import java.util.ArrayList;

import com.misty.engine.graphics.GameObject;
import com.misty.engine.graphics.Renderer;
import com.misty.engine.graphics.UI.listeners.SliderListener;

public class Slider extends GameObject implements Clickable {

	/**this goes between 0 and 1 */
	private float value;
	private boolean dragging = false;
	private boolean hovering = false;
	private int knobWidth = 5;
	private boolean highlighted = false;
	private int highlightColor = 0xffaefeae;
	private ArrayList<SliderListener> listeners = new ArrayList<SliderListener>();

	public Slider(int x, int y, int width, int height, float initialValue) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.value = initialValue;
	}
	
	public Slider(int x, int y, int width, int height) {
		this(x, y, width, height, 0.0f);
	}
	
	public void setHighlighted(boolean highlight) {
		highlighted = highlight;
	}
	
	public void setHighlightColor(int color) {
		highlightColor = color;
	}

	@Override
	public Shape getShape() {
		return null;
	}

	@Override
	public boolean isPressed() {
		return true;
	}

	@Override
	public boolean onClickPressed(int x, int y) {
		if(x >= this.x+value*(width-knobWidth) && x <= this.x+value*(width-knobWidth)+knobWidth)
			dragging = true;
		return true;
	}

	@Override
	public boolean onClickReleased(int x, int y) {
		dragging = false;
		return true;
	}

	@Override
	public void onHoverEnter() {
		hovering = true;
	}

	@Override
	public void onHoverExit() {
		hovering = false;
	}

	@Override
	public boolean isMouseOver() {
		return hovering;
	}

	@Override
	public void onclickReleasedOutside() {
		dragging = false;
	}

	@Override
	public void draw(Renderer r) {
		r.fillColoredRect(x, y, width, height, 0xffaeaeae);
		int knobColor = hovering?0xfff3f3f3:0xffe3e3e3;
		r.fillColoredRect(x+value*(width-knobWidth), y, knobWidth, height, knobColor);
		if(highlighted) {
			r.fillColoredRect(x, y, (int)(value*(width-knobWidth)), height, knobColor&highlightColor);
		}
		r.drawColoredRect(x, y, width, height, Color.BLACK.getRGB());

	}

	@Override
	public void update() {
		//System.out.println(dragging);
	}

	@Override
	public boolean onDragged(int x, int y) {
		if(dragging) {
			float xoff = (x-this.x)/(width);
			value = xoff;
			if(value < 0) value = 0;
			if(value > 1) value = 1;
			listeners.forEach(e -> e.valueChanged(value));
		}
		return false;
	}

	public void addSliderListener(SliderListener sliderListener) {
		listeners.add(sliderListener);
	}

	@Override
	public void onClickOutside() {
	}

}
