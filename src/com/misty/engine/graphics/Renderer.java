package com.misty.engine.graphics;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

import com.misty.engine.Game;
import com.misty.engine.Particle;
import com.misty.engine.graphics.font.Font;
import com.misty.utils.Util;

public class Renderer extends Canvas {

	private static final long serialVersionUID = 1L;
	private BufferedImage image;
	private int[] pixels;
	private int[] clearPixels;
	private int width, height;
	int tick = 0;
//	private static String fontLegend = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"#$%&'()*+,-./:;<=>?[\\]^_|@ ";
	private Graphics g;
	public static final int RENDERING_MODE_NORMAL = 0;
	public static final int RENDERING_MODE_MULTIPLY = 1;
	private int renderingMode = RENDERING_MODE_NORMAL;
	private int clearColor = 0xff000000;
	private Font font;

	public void setRenderingMode(int r) {
		renderingMode = r;
	}
	public void setFont(Font a) {
		font = a;
	}

	public Renderer(int w, int h) {
		setSize(new Dimension(w, h));
		this.width = w;
		this.height = h;
		image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

		font = Font.regularFont;
		clearPixels = new int[pixels.length];
		Arrays.fill(clearPixels, clearColor);
	}

	public void setClearColor(int color) {
		Arrays.fill(clearPixels, color);
	}

	public void draw() {
		render();
	}

	public void drawString(String str, int x, int y) {
		drawString(str, x, y, 0xff000000, 1.0f);
	}

	public void drawString(String str, int x, int y, int color, float scale) {
		int row = 0;
		int j = 0;
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == '\n') {
				row++;
				if (y + row * 10 * scale > height)
					break;

				j = 0;
			} else {
				if ((x + (8 * scale) * j) > width)
					break;
				drawChar(str.charAt(i), (int) (x + (font.getWidth() * scale) * j), y + (int) (row * (font.getHeight()+2) * scale), color, scale);
				// drawBitmap(font[fontLegend.indexOf(str.charAt(i))], (int) (x
				// + (8 * scale) * j), y + row * 10, scale, color);
				j++;
			}
		}
	}

	/*
	 * draws a string @str at position @x and @x in pixels*gameScale with the
	 * color @0xaarrggbb
	 */
	public void drawString(String str, int x, int y, int color) {
		drawString(str, x, y, color, 1f);
	}

	private int mixPixel(int x, int y) {
		int xa = (x >> 24) & 0xFF;
		int ya = (y >> 24) & 0xFF;
		int a = Math.min(255, (xa + ya) / 2);
		float o2a = 1 / (2f * a);
		int r = (int) ((((x >> 16) & 0xFF) * xa + ((y >> 16) & 0xFF) * ya) * o2a);
		int g = (int) ((((x >> 8) & 0xFF) * xa + ((y >> 8) & 0xFF) * ya) * o2a);
		int b = (int) (((x & 0xFF) * xa + (y & 0xFF) * ya) * o2a);

		return (a << 24) | (r << 16) | (g << 8) | (b);
	}

	private void drawChar(char c, int x, int y, int color, float scale) {
		Bitmap bm = font.getFontChar(c);	
		if (bm == null || x + bm.getWidth() * scale < 0 || y + bm.getHeight() * scale < 0 || x > width || y > height)
			return;
		int i = 0;
		int j = 0;
		int maxi = (int) (bm.getWidth() * scale);
		if (x < 0)
			i = -x;
		if (x + bm.getWidth() * scale > width)
			maxi = width - x;
		int startPos = (y) * this.width + (x);
		for (j = 0; j < bm.getHeight() * scale; j++) {
			for (int ii = i; ii < maxi; ii++) {
				int pix = bm.pixels[(int) (j / scale) * bm.getWidth() + (int) (ii / scale)];
				if (pix == 0xff000000) {
					putPixel(startPos + ii, color);
				}
			}
			startPos += this.width;
		}
	}

	@Deprecated
	public void drawBitmap_slow(Bitmap bm, int x, int y) {
		if (x + bm.getWidth() < 0 || y + bm.getHeight() < 0 || x > width)
			return;
		int i = 0;
		int j = 0;
		int maxi = bm.getWidth();
		if (x < 0)
			i = -x;
		if (x + bm.getWidth() > width)
			maxi = width - x;
		int startPos = (y) * this.width + (x);
		for (j = 0; j < bm.getHeight(); j++) {
			for (int ii = i; ii < maxi; ii++) {
				putPixel(startPos + ii, bm.pixels[j * bm.getWidth() + ii]);
			}
			startPos += this.width;
		}
	}

	public void drawBitmap(Bitmap bm, int x, int y, float scale) {
		if(scale == 1) {
			drawBitmap(bm, x, y);
			return;
		}
		if (x + bm.getWidth() * scale < 0 || y + bm.getHeight() * scale < 0 || x > width)
			return;
		int i = 0;
		int j = 0;
		int maxi = (int) (bm.getWidth() * scale);
		if (x < 0)
			i = -x;
		if (x + bm.getWidth() * scale > width)
			maxi = width - x;
		int startPos = (y) * this.width + (x);
		for (j = 0; j < bm.getHeight() * scale; j++) {
			for (int ii = i; ii < maxi; ii++) {

				putPixel(startPos + ii, bm.pixels[(int) (j / scale) * bm.getWidth() + (int) (ii / scale)]);
			}
			startPos += this.width;
		}
	}

	public void drawBitmap(Bitmap bm, int x, int y) {
		if (bm.isTransparent())
			drawBitmap_slow(bm, x, y);
		else if(bm.getWidth() == Game.width && bm.getHeight() == Game.height) {
			System.arraycopy(bm.pixels, 0, pixels, 0, pixels.length);
		}
		else
			for (int line = 0; line < bm.getHeight(); line++) {
				copyToLine(bm.getPreppedData()[line], x, y + line, bm.getWidth());
			}
	}

	public void copyToLine(int[] bs, int x, int y, int width2) {
		if (y > height || x < -width || y < 0)
			return;
		int startIndex = 0;
		if (width2 + (x + y * width) > pixels.length) {
			width2 = pixels.length - (x + y * width);
			if (width2 < 0)
				return;
		}
		if (width2 + x > width) {
			width2 = width - x;
			if (width2 < 0)
				return;
		}
		if (x < 0) {
			width2 += x;
			startIndex = -x;
			x = 0;
			if (width2 < 0)
				return;
		}
		System.arraycopy(bs, startIndex, pixels, x + y * width, width2);
	}

	//public void drawBitmapRotated(Bitmap bm, int x, int y, float rad) {
		/*if (rad == 0) {
			drawBitmap(bm, x, y);
			return;
		}
		if (x + bm.getWidth() < 0 || y + bm.getHeight() < 0 || x - bm.getWidth() > width)
			return;
		float sin = Util.sin(rad);
		float cos = Util.cos(rad);
		float bmw2 = bm.getWidth() / 2f;
		float bmh2 = bm.getHeight() / 2f;
		float mx, mxx;
		float my, myx;
		for (float j = 0; j < bm.getHeight(); j += .5f) {
			for (float i = 0; i < bm.getWidth(); i += .5f) {
				mxx = i - bmw2;
				myx = j - bmh2;
				mx = (cos * mxx - sin * myx);
				my = (sin * mxx + cos * myx);
				mx += bmw2 + x;
				my += bmh2 + y;
				// mx += x;
				// my += y;
				if (mx < 0 || my < 0 || mx > width)
					continue;
				putPixel((((int) my) * this.width + ((int) mx)), bm.pixels[(int) j * bm.getWidth() + (int) i]);

			}
		}*/
		
	//}
	
	public void draw(GameObject go, int x, int y, float rad, float scale) {
		Bitmap bm = go.bm;
		if (rad == 0) {
			drawBitmap(bm, x, y, scale);
			return;
		}
		//if(scale == 1) {
		//	drawBitmapRotated(bm, x, y, rad);
		//	return;
		//}
		if (x + bm.getWidth() < 0 || y + bm.getHeight() < 0 || x - bm.getWidth() > width)
			return;
		float sin = Util.sin(rad);
		float cos = Util.cos(rad);
		
		float step = Math.max(Math.max(Math.abs(sin), Math.abs(cos))-.2f, 0.7f);
		float px = go!=null?go.rotationPivotX:.5f;
		float py = go!=null?go.rotationPivotX:.5f;
		float bmw2 = bm.getWidth()*scale * px;
		float bmh2 = bm.getHeight()*scale * py;
		float mx, mxx;
		float my, myx;
		for (float j = 0; j < bm.getHeight()*scale; j += step) {
			for (float i = 0; i < bm.getWidth()*scale; i += step) {
				mxx = i - bmw2;
				myx = j - bmh2;
				mx = (cos * mxx - sin * myx);
				mx += bmw2 + x;
				if(mx > width || mx < 0) continue;
				
				my = (sin * mxx + cos * myx);
				my += bmh2 + y;
				if(my > height || my < 0) continue;
				// mx += x;
				// my += y;

				
				putPixel((((int) my) * this.width + ((int) mx)), bm.pixels[(int) (j/scale) * bm.getWidth() + (int) (i/scale)]);

			}
		}
	}
	
	public void drawBitmap(Bitmap bm, int x, int y, float rad, float scale) {
		if (rad == 0) {
			drawBitmap(bm, x, y, scale);
			return;
		}
		//if(scale == 1) {
		//	drawBitmapRotated(bm, x, y, rad);
		//	return;
		//}
		if (x + bm.getWidth() < 0 || y + bm.getHeight() < 0 || x - bm.getWidth() > width)
			return;
		float sin = Util.sin(rad);
		float cos = Util.cos(rad);
		
		float step = Math.max(Math.max(Math.abs(sin), Math.abs(cos))-.2f, 0.7f);
		
		float bmw2 = bm.getWidth()*scale / 2f;
		float bmh2 = bm.getHeight()*scale / 2f;
		float mx, mxx;
		float my, myx;
		for (float j = 0; j < bm.getHeight()*scale; j += step) {
			for (float i = 0; i < bm.getWidth()*scale; i += step) {
				mxx = i - bmw2;
				myx = j - bmh2;
				mx = (cos * mxx - sin * myx);
				mx += bmw2 + x;
				if(mx > width || mx < 0) continue;
				
				my = (sin * mxx + cos * myx);
				my += bmh2 + y;
				if(my > height || my < 0) continue;
				// mx += x;
				// my += y;

				
				putPixel((((int) my) * this.width + ((int) mx)), bm.pixels[(int) (j/scale) * bm.getWidth() + (int) (i/scale)]);

			}
		}
	}

	public void fillColoredRect(int x, int y, int width, int height, int color) {

		if (x + width < 0 || y + height < 0 || x > this.width)
			return;
		int i = 0;
		int j = 0;
		int maxi = width;
		if (x < 0)
			i = -x;
		if (x + width > this.width)
			maxi = this.width - x;
		int startPos = (y) * this.width + (x);
		for (j = 0; j < height; j++) {
			for (int ii = i; ii < maxi; ii++) {
				putPixel(startPos + ii, color);
			}
			startPos += this.width;
			if (startPos > pixels.length)
				return;
		}
	}

	public void drawColoredRect(int x, int y, int width, int height, int color) {
		if (x + width < 0 || y + height < 0 || x > this.width)
			return;
		int startPos = y * this.width + x;
		int ix = 0;
		int widthi = width;
		if (x < 0)
			ix = -x;
		if (x + width > this.width)
			widthi = this.width - x;
		for (; ix < widthi; ix++) {
			int in = startPos + ix;
			putPixel(in, color);
			putPixel(in + (height - 1) * this.width, color);
		}

		if (x + width - 1 < this.width && x + width >= 0)
			for (int i = 1; i < height - 1; i++) {
				putPixel(startPos + width - 1 + i * this.width, color);
			}
		if (x >= 0 && x < this.width)
			for (int i = 1; i < height - 1; i++) {
				putPixel(startPos + i * this.width, color);
			}
	}

	public void putPixel(int index, int color) {
		// if (index < pixels.length && index >= 0 && (color & 0xff000000) !=
		// 0x00) {
		if ((color & 0xff000000) != 0x00 && !(index >= pixels.length || index < 0)) {
			if (renderingMode == RENDERING_MODE_NORMAL) {
				pixels[index] = color;
			} else if (renderingMode == RENDERING_MODE_MULTIPLY) {
				pixels[index] = mixPixel(pixels[index], color);
			}

		}
	}

	public void render() {
		
		if (g == null)
			g = getGraphics();
		g.drawImage(image, 0, 0, super.getWidth(), super.getHeight(), null);
	}

	public void clear() {
		System.arraycopy(clearPixels, 0, pixels, 0, pixels.length);
	}



	public void drawParticle(Particle p) {
		if (p.getX() < 0 || p.getY() < 0 || p.getX() >= width || p.getY() >= height)
			return;
		pixels[(int) p.getX() + (int) p.getY() * width] = p.getColor();
	}

	public void fill(int i) {
		Util.intfill(pixels, i);
	}

}
