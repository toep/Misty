package com.misty.engine.graphics.UI.builders;

import com.misty.engine.graphics.Color;
import com.misty.engine.graphics.UI.Slider;

public class SliderBuilder {

	private int x, y, w, h;
	private float val;
	private boolean highlighted;
	private Color highlightColor;
	
	public SliderBuilder() {
		
	}
	
	public SliderBuilder setPosition(int x, int y) {
		this.x = x;
		this.y = y;
		return this;
	}
	
	public SliderBuilder setDimensions(int w, int h) {
		this.w = w;
		this.h = h;
		return this;
	}
	
	public SliderBuilder setStartValue(float val) {
		this.val = val;
		return this;
	}
	public SliderBuilder setHighlightColor(Color highlightColor) {
		this.highlightColor = highlightColor;
		this.highlighted = true;
		return this;
	}
	
	public Slider create() {
		Slider slider = new Slider(x, y, w, h, val);
		slider.setHighlighted(highlighted);
		slider.setHighlightColor(highlightColor);
		return slider;
	}
}
