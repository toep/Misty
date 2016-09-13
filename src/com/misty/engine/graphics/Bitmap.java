package com.misty.engine.graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

import com.misty.utils.Util;

//this class contains the info for an image supports png and jpg
public class Bitmap {

	private int width, height;
	public boolean flippedX = false;
	public boolean flippedY = false;
	public int[] pixels;
	public int[][] preppedData;// only for bitmaps that aren't the size of the
								// screen and have no transparent pixels!

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
		prepDataArray();
	}

	public Bitmap(String string) throws IOException {
		this(Util.getBufferedImageFromFile(string));
	}

	public void flipX() {
		int[] newPixels = new int[pixels.length];
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				newPixels[j * width + i] = pixels[j * width + width - i - 1];
			}
		}
		pixels = newPixels;
	}

	public void flipY() {
		int[] newPixels = new int[pixels.length];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				newPixels[j * width + i] = pixels[(height - j - 1) * width + i];
			}
		}
		pixels = newPixels;
	}

	public void prepDataArray() {
		for (int i = 0; i < pixels.length; i++) {
			if ((pixels[i] & 0xff000000) == 0x00) {
				transparency = true;
				break;
			}
		}
		if (!transparency) {
			preppedData = new int[height][width];
			for (int line = 0; line < height; line++)
				preppedData[line] = Arrays.copyOfRange(pixels, width * line, width * line + width);

		}

	}

	public int[][] getPreppedData() {
		return preppedData;
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
		return pixels[tx + ty * width];
	}
}
