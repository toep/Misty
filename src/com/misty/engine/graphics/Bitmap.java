package com.misty.engine.graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;

import com.misty.utils.Util;

//this class contains the info for an image supports png and jpg
public class Bitmap {

	private int width, height;
	public boolean flippedX = false;
	public boolean flippedY = false;
	public int[] pixels;

	private boolean transparency = false;

	// creates an empty bitmap of width @w and height @h in pixels
	public Bitmap(int w, int h) {
		this.width = w;
		this.height = h;
		pixels = new int[width * height];
	}

	// creates a bitmap object from a bufferedImage
	public Bitmap(BufferedImage bi) {
		this.width = bi.getWidth();
		this.height = bi.getHeight();
		this.pixels = new int[bi.getWidth() * bi.getHeight()];
		bi.getRGB(0, 0, width, height, pixels, 0, width);
		determineTransparency();
	}
	public void determineTransparency() {
		for(int i = 0; i < pixels.length; i++) {
			if((pixels[i] & 0xff000000) != 0xff) {
				transparency = true;
				break;
			}
		}
	}

	public Bitmap(String string) throws IOException {
		this(Util.getBufferedImageFromFile(string));
	}

	public Bitmap getFlipY() {
		int[] newPixels = new int[pixels.length];
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				newPixels[j * width + i] = pixels[j * width + width - i - 1];
			}
		}
		return new Bitmap(width, height).withPixels(newPixels);
	}

	private Bitmap withPixels(int[] newPixels) {
		pixels = newPixels;
		determineTransparency();
		return this;
	}

	public Bitmap getFlipX() {
		int[] newPixels = new int[pixels.length];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				newPixels[j * width + i] = pixels[(height - j - 1) * width + i];
			}
		}
		return new Bitmap(width, height).withPixels(newPixels);
	}


	public boolean isTransparent() {
		return transparency;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getRGB(int tx, int ty) {
		if(tx < 0 || ty < 0 || tx > width || ty > height) return -1;
		return pixels[tx + ty * width];
	}
}
