package com.misty.engine.graphics.font;

import java.io.IOException;
import java.io.InputStream;

import com.misty.engine.graphics.Bitmap;
import com.misty.utils.Util;

public class Font {

	private static String fontLegend = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"#$%&'()*+,-./:;<=>?[\\]^_|@ ";
	private Bitmap[] font;
	private int w, h;
	
	public static final Font defaultFont = new Font(Font.class.getClassLoader().getResourceAsStream("font.png"), 8, 8);
	public static final Font regularFont = new Font(Font.class.getClassLoader().getResourceAsStream("pixel_std_regular.png"), 7, 9);

	
	public Font(String res, int w, int h) {
		this.w = w;
		this.h = h;
		try {
			font = Util.bitmapsFromSheet(Util.createBitmapFromFile(res), w, h);
		} catch (IOException e) {
			System.out.println("we couldn't load the font! make sure it's located as " + res);
			e.printStackTrace();
		}
	}
	
	public Font(InputStream in, int w, int h) {
		this.w = w;
		this.h = h;
		try {
			font = Util.bitmapsFromSheet(Util.createBitmapFromStream(in), w, h);
		} catch (IOException e) {
			System.out.println("we couldn't load the font! check to make sure the jar contains the file font.png ");
			e.printStackTrace();
		}
	}

	public Bitmap getFontChar(char c) {
		int i = fontLegend.indexOf(c);
		return (i!=-1)?font[i]:null;
	}

	public float getWidth() {
		return w;
	}

	public int getHeight() {
		return h;
	}

	public int getCharacterWidth() {
		return font[0].getWidth();
	}
}
