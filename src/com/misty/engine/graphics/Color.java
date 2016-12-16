package com.misty.engine.graphics;

public class Color {
	byte alpha;
	byte red;
	byte green;
	byte blue;
	
	public Color(byte r, byte g, byte b) {
		red = r;
		green = g;
		blue = b;
		alpha = (byte)0xff;
	}
	public Color(byte a, byte r, byte g, byte b) {
		red = r;
		green = g;
		blue = b;
		alpha = a;
	}
}
