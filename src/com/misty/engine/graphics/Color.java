package com.misty.engine.graphics;

public class Color {

	public int rgb;

	public Color(int rgb) {
		this.rgb = rgb;
	}

	public Color(int a, int r, int g, int b) {
		this(((a << 24)) + ((r << 16)) + ((g << 8)) + (b));
	}

	public final static Color BLACK = new Color(0xff000000);
	public final static Color WHITE = new Color(0xffffffff);
	public final static Color RED = new Color(0xffFF0000);
	public final static Color LIME = new Color(0xff00FF00);
	public final static Color LIME_LIGHT = new Color(0xffaefeae);
	public final static Color BLUE = new Color(0xff0000FF);
	public final static Color YELLOW = new Color(0xffFFFF00);
	public final static Color CYAN = new Color(0xff00FFFF);
	public final static Color MAGENTA = new Color(0xffFF00FF);
	public final static Color GRAY = new Color(0xff808080);
	public final static Color SILVER = new Color(0xffC0C0C0);
	public final static Color GOLD = new Color(0xffFFD700);
	public final static Color MAROON = new Color(0xff800000);
	public final static Color OLIVE = new Color(0xff808000);
	public final static Color GREEN = new Color(0xff008000);
	public final static Color PURPLE = new Color(0xff800080);
	public final static Color TEAL = new Color(0xff008080);
	public final static Color NAVY = new Color(0xff000080);
	private static Color temp = new Color(0xff000000);
	
	
	public static Color create(int i) {
		temp.rgb = i;
		return temp;
	}
}
