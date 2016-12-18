package com.misty.engine.graphics;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

import javax.swing.JPanel;

import com.misty.engine.Game;
import com.misty.engine.graphics.font.Font;
import com.misty.utils.Util;

public class Renderer extends JPanel {

	private static final long serialVersionUID = 1L;
	private BufferedImage image;
	private int[] pixels;
	private int[] clearPixels;
	private int width, height;
	private int scale;
	int tick = 0;
	// private static String fontLegend =
	// "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"#$%&'()*+,-./:;<=>?[\\]^_|@
	// ";
	private Graphics g;
	public static final int RENDERING_MODE_NORMAL = 0;
	public static final int RENDERING_MODE_MULTIPLY = 1;
	private int renderingMode = RENDERING_MODE_NORMAL;
	private int clearColor = 0xff000000;
	private Font font;
	
	private int xoffset = 0;
	private int yoffset = 0;
	
	
	
	
	public void setRenderingMode(int r) {
		renderingMode = r;
	}

	/**
	 * sets the current font used by renderer
	 * @param a Font you wish to use
	 */
	public void setFont(Font a) {
		font = a;
	}
	/**
	 * 
	 * @return the current font being used
	 */
	public Font getCurrentFont() {
		return font;
	}

	/**
	 * sets up the renderer with specified dimensions
	 * @param w width in pixels
	 * @param h height in pixels
	 * @param scale scale it should be drawn in. scale = 2 means each pixel will be a 2x2 box
	 */
	public Renderer(int w, int h, int scale) {
		// setPreferredSize(preferredSize);
		setPreferredSize(new Dimension(w*scale, h*scale));
		this.scale = scale;
		this.width = w;
		this.height = h;
		image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

		font = Font.defaultFont;
		clearPixels = new int[pixels.length];
		Arrays.fill(clearPixels, clearColor);
	}
	/**
	 * sets up the renderer with specified dimensions with scale at 1
	 * @param w width in pixels
	 * @param h height in pixels
	 */
	public Renderer(int w, int h) {
		this(w, h, 1);
	}

	
	
	public void draw() {
		render();
	}

	/**
	 * renders the pixels to screen
	 */
	public void render() {

		if (g == null)
			g = getGraphics();
		g.drawImage(image, 0, 0, width*scale, height*scale, null);

		// paintImmediately(0, 0, width, height);
	}

	/**
	 * draws a string to pixel array in a black color with scale 1
	 * @param str the string being drawn
	 * @param xf x position
	 * @param yf y position
	 */
	public void drawString(String str, float xf, float yf) {
		int x = (int) xf;
		int y = (int) yf;
		drawString(str, x, y, 0xff000000, 1.0f);
	}

	/**
	 * draws a string to pixel array in specified color and scale
	 * @param str string being drawn
	 * @param x pos
	 * @param y pos
	 * @param color in 0xaarrggbb format
	 * @param scale
	 */
	public void drawString(String str, int x, int y, int color, float scale) {
		int row = 0;
		int j = 0;
		x+=xoffset;
		y+=yoffset;
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == '\n') {
				row++;
				if (y + row * 10 * scale > height)
					break;

				j = 0;
			} else {
				if ((x + (8 * scale) * j) > width)
					break;
				drawChar(str.charAt(i), (int) (x + (font.getWidth() * scale) * j),
						y + (int) (row * (font.getHeight() + 2) * scale), color, scale);
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
		x+=xoffset;
		y+=yoffset;
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
		
		if (scale == 1) {
			drawBitmap(bm, x, y);
			return;
		}
		if (x + bm.getWidth() * scale < 0 || y + bm.getHeight() * scale < 0 || x > width)
			return;
		
		x+=xoffset;
		y+=yoffset;
		
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
		
		if (bm.isTransparent()) {
			drawBitmap_slow(bm, x, y);
			return;
		}
		x+=xoffset;
		y+=yoffset;
		if (x == 0 && y == 0 && bm.getWidth() == Game.width && bm.getHeight() == Game.height) {
			System.arraycopy(bm.pixels, 0, pixels, 0, pixels.length);
		} else
			for (int line = 0; line < bm.getHeight(); line++) {
				copyToLine(bm.pixels, line, x, y + line, bm.getWidth());
			}
	}

	public void copyToLine(int[] bs, int height, int x, int y, int width2) {
		if (y > this.height || x < -width || y < 0)
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
		System.arraycopy(bs, startIndex + height * width2, pixels, x + y * width, width2);
	}

	// public void drawBitmapRotated(Bitmap bm, int x, int y, float rad) {
	/*
	 * if (rad == 0) { drawBitmap(bm, x, y); return; } if (x + bm.getWidth() < 0
	 * || y + bm.getHeight() < 0 || x - bm.getWidth() > width) return; float sin
	 * = Util.sin(rad); float cos = Util.cos(rad); float bmw2 = bm.getWidth() /
	 * 2f; float bmh2 = bm.getHeight() / 2f; float mx, mxx; float my, myx; for
	 * (float j = 0; j < bm.getHeight(); j += .5f) { for (float i = 0; i <
	 * bm.getWidth(); i += .5f) { mxx = i - bmw2; myx = j - bmh2; mx = (cos *
	 * mxx - sin * myx); my = (sin * mxx + cos * myx); mx += bmw2 + x; my +=
	 * bmh2 + y; // mx += x; // my += y; if (mx < 0 || my < 0 || mx > width)
	 * continue; putPixel((((int) my) * this.width + ((int) mx)),
	 * bm.pixels[(int) j * bm.getWidth() + (int) i]);
	 * 
	 * } }
	 */

	// }

	public void draw(GameObject go, int x, int y, float rad, float scale) {
		Bitmap bm = go.bm;
		if (rad == 0) {
			drawBitmap(bm, x, y, scale);
			return;
		}
		// if(scale == 1) {
		// drawBitmapRotated(bm, x, y, rad);
		// return;
		// }
		x+=xoffset;
		y+=yoffset;
		if (x + bm.getWidth() < 0 || y + bm.getHeight() < 0 || x - bm.getWidth() > width)
			return;
		float sin = Util.sin(rad);
		float cos = Util.cos(rad);

		float step = Math.max(Math.max(Math.abs(sin), Math.abs(cos)) - .2f, 0.7f);
		float px = go != null ? go.rotationPivotX : .5f;
		float py = go != null ? go.rotationPivotX : .5f;
		float bmw2 = bm.getWidth() * scale * px;
		float bmh2 = bm.getHeight() * scale * py;
		float mx, mxx;
		float my, myx;
		for (float j = 0; j < bm.getHeight() * scale; j += step) {
			for (float i = 0; i < bm.getWidth() * scale; i += step) {
				mxx = i - bmw2;
				myx = j - bmh2;
				mx = (cos * mxx - sin * myx);
				mx += bmw2 + x;
				if (mx > width || mx < 0)
					continue;

				my = (sin * mxx + cos * myx);
				my += bmh2 + y;
				if (my > height || my < 0)
					continue;
				// mx += x;
				// my += y;

				putPixel((((int) my) * this.width + ((int) mx)),
						bm.pixels[(int) (j / scale) * bm.getWidth() + (int) (i / scale)]);

			}
		}
	}

	public void drawBitmap(Bitmap bm, int x, int y, float rad, float scale) {
		if (rad == 0) {
			drawBitmap(bm, x, y, scale);
			return;
		}
		x+=xoffset;
		y+=yoffset;
		// if(scale == 1) {
		// drawBitmapRotated(bm, x, y, rad);
		// return;
		// }
		if (x + bm.getWidth() < 0 || y + bm.getHeight() < 0 || x - bm.getWidth() > width)
			return;
		float sin = Util.sin(rad);
		float cos = Util.cos(rad);

		float step = Math.max(Math.max(Math.abs(sin), Math.abs(cos)) - .2f, 0.7f);

		float bmw2 = bm.getWidth() * scale / 2f;
		float bmh2 = bm.getHeight() * scale / 2f;
		float mx, mxx;
		float my, myx;
		for (float j = 0; j < bm.getHeight() * scale; j += step) {
			for (float i = 0; i < bm.getWidth() * scale; i += step) {
				mxx = i - bmw2;
				myx = j - bmh2;
				mx = (cos * mxx - sin * myx);
				mx += bmw2 + x;
				if (mx > width || mx < 0)
					continue;

				my = (sin * mxx + cos * myx);
				my += bmh2 + y;
				if (my > height || my < 0)
					continue;
				// mx += x;
				// my += y;

				putPixel((((int) my) * this.width + ((int) mx)),
						bm.pixels[(int) (j / scale) * bm.getWidth() + (int) (i / scale)]);

			}
		}
	}

	public void fillColoredRect(float xf, float yf, int width, int height, int color) {
		int x = (int) xf + xoffset;
		int y = (int) yf + yoffset;
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

	public void drawColoredRect(float xf, float yf, int width, int height, int color) {
		int x = (int) xf + xoffset;
		int y = (int) yf + yoffset;
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

	public void clear() {
		System.arraycopy(clearPixels, 0, pixels, 0, pixels.length);
	}

	/*
	 * @Override protected void paintComponent(Graphics g) {
	 * super.paintComponent(g); g.drawImage(image, 0, 0, super.getWidth(),
	 * super.getHeight(), null); }
	 */

	public void drawParticle(Particle p) {
		if (p.getX() < 0 || p.getY() < 0 || p.getX() >= width || p.getY() >= height)
			return;
		pixels[(int) p.getX() + (int) p.getY() * width] = p.getColor();
	}

	public void fill(int i) {
		Util.intfill(pixels, i);
	}

	public void setClearColor(int color) {
		Arrays.fill(clearPixels, color);
	}

	public void translate(float x, float y) {
		xoffset+=x;
		yoffset+=y;
	}

}
