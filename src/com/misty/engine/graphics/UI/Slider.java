package com.misty.engine.graphics.UI;

import java.awt.Shape;
import java.util.ArrayList;

import com.misty.engine.graphics.Color;
import com.misty.engine.graphics.GameObject;
import com.misty.engine.graphics.Renderer;
import com.misty.engine.graphics.UI.listeners.SliderListener;

public class Slider extends GameObject implements Clickable {

	protected float value;
	protected boolean dragging = false;
	protected boolean hovering = false;
	protected int knobWidth = 5;
	protected boolean highlighted = false;
	protected Color highlightColor = new Color(0xffaefeae);
	protected Color highlightColorHover = highlightColor.lighten();
	protected Color bgColor = new Color(0xffaeaeae);
	protected Color hoveringKnob = new Color(0xfff3f3f3);
	protected Color unhoveringKnob = new Color(0xffe3e3e3);
	protected ArrayList<SliderListener> listeners;

	public Slider(int x, int y, int width, int height, float initialValue) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.value = initialValue;
		 listeners = new ArrayList<SliderListener>();
	}
	
	public Slider(int x, int y, int width, int height) {
		this(x, y, width, height, 0.0f);
	}
	
	
	
	public void setHighlighted(boolean highlight) {
		highlighted = highlight;
	}
	
	public void setHighlightColor(Color color) {
		highlightColor = color;
		highlightColorHover = color.lighten();
		highlighted = true;
	}

	@Override
	public Shape getShape() {
		return null;
	}

	@Override
	public boolean isPressed() {
		return dragging;
	}
	
	/**
	 * 
	 * @return value between 0.0 and 1.0
	 */
	public float getValue() {
		return value;
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
		return false;
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
		r.fillColoredRect(x, y, width, height, bgColor);
		Color knobColor = hovering?hoveringKnob:unhoveringKnob;
		r.fillColoredRect(x+value*(width-knobWidth-2)+1, y, knobWidth, height, knobColor);
		if(highlighted) {
			if(isMouseOver())
				r.fillColoredRect(x, y, (int)(value*(width-knobWidth-2))+1, height, highlightColorHover);
			else
				r.fillColoredRect(x, y, (int)(value*(width-knobWidth-2))+1, height, highlightColor);
				
		}
		r.drawColoredRect(x, y, width, height, Color.BLACK);

	}

	@Override
	public void update() {
		//System.out.println(dragging);
	}

	@Override
	public boolean onDragged(int x, int y) {
		if(dragging) {
			float xoff = (x-knobWidth/2-this.x)/(width-knobWidth);
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
