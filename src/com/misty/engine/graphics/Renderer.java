package com.misty.engine.graphics;

import com.misty.engine.Game;
import com.misty.engine.graphics.font.Font;
import com.misty.utils.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

public class Renderer extends JPanel {

    private static final long serialVersionUID = 1L;
    private BufferedImage image;
    private int[] pixels;
    private int[] clearPixels;
    private int width, height;
    private int scale;
    int tick = 0;

    private Graphics g;
    public static final int RENDERING_MODE_NORMAL = 0;
    public static final int RENDERING_MODE_MULTIPLY = 1;
    private int renderingMode = RENDERING_MODE_NORMAL;
    private int clearColor = 0xff000000;
    private Font font;

    private int xoffset = 0;
    private int yoffset = 0;

    private int clipx;
    private int clipy;
    private int clipwidth;
    private int clipheight;

    private ShapeRenderer shapeRenderer;

    public void setRenderingMode(int r) {
        renderingMode = r;
    }

    /**
     * sets the current font used by renderer
     *
     * @param a Font you wish to use
     */
    public void setFont(Font a) {
        font = a;
    }

    /**
     * @return the current font being used
     */
    public Font getCurrentFont() {
        return font;
    }

    /**
     * sets up the renderer with specified dimensions
     *
     * @param w     width in pixels
     * @param h     height in pixels
     * @param scale scale it should be drawn in. scale = 2 means each pixel will be a 2x2 box
     */
    public Renderer(int w, int h, int scale) {
        // setPreferredSize(preferredSize);
        setPreferredSize(new Dimension(w * scale, h * scale));
        this.scale = scale;
        this.width = w;
        this.height = h;
        clipx = 0;
        clipy = 0;
        clipwidth = w;
        clipheight = h;
        image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

        font = Font.defaultFont;
        clearPixels = new int[pixels.length];
        Arrays.fill(clearPixels, clearColor);

        shapeRenderer = new ShapeRenderer(this, w, h);
    }

    /**
     * sets up the renderer with specified dimensions with scale at 1
     *
     * @param w width in pixels
     * @param h height in pixels
     */
    public Renderer(int w, int h) {
        this(w, h, 1);
    }

    public void resetClip() {
        clipx = 0;
        clipy = 0;
        clipwidth = width;
        clipheight = height;
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
        g.drawImage(image, 0, 0, width * scale, height * scale, null);

        // paintImmediately(0, 0, width, height);
    }

    /**
     * draws a string to pixel array in a white color with scale 1
     *
     * @param str the string being drawn
     * @param x  x position
     * @param y  y position
     */
    public void drawString(String str, float x, float y) {
        drawString(str, x, y, Color.WHITE, 1.0f);
    }

    /**
     * draws a string to pixel array in specified color and scale
     *
     * @param str   string being drawn
     * @param x     pos
     * @param y     pos
     * @param color in 0xaarrggbb format
     * @param scale
     */
    public void drawString(String str, float x, float y, Color color, float scale) {
        drawString(str, x, y, color.rgb, scale);
    }

    /**
     * draws a string to pixel array in specified color and scale
     *
     * @param str   string being drawn
     * @param x     pos
     * @param y     pos
     * @param color in 0xaarrggbb format
     * @param scale
     */
    private void drawString(String str, float x, float y, int color, float scale) {
        int ix = (int) x;
        int iy = (int) y;
        int row = 0;
        int j = 0;
        ix += xoffset;
        iy += yoffset;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '\n') {
                row++;
                if (iy + row * 10 * scale > height)
                    break;

                j = 0;
            } else {
                if ((i + (8 * scale) * j) > width)
                    break;
                drawChar(str.charAt(i), (int) (ix + (font.getWidth() * scale) * j),
                        iy + (int) (row * (font.getHeight() + 2) * scale), color, scale);
                j++;
            }
        }
    }

    /**
     * draws a string @str at position @x and @x in pixels*gameScale with the
     * color @0xaarrggbb
     */
    private void drawString(String str, float x, float y, int color) {
        drawString(str, x, y, color, 1f);
    }

    /**
     * draws a string @str at position @x and @x in pixels*gameScale with the
     * color @0xaarrggbb
     */
    public void drawString(String str, float x, float y, Color color) {
        drawString(str, x, y, color.rgb, 1f);
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
        int height = bm.getHeight();
        boolean top = false;
        if (y < clipy) {
            height = height - clipy + y;
            y = clipy;
            top = true;
        }
        int i = 0;
        int j = 0;
        int maxi = (int) (bm.getWidth() * scale);
        if (x < 0)
            i = -x;
        if (x + bm.getWidth() * scale > width)
            maxi = width - x;
        if (y - clipy + height > clipheight) {
            height = clipheight - y + clipy;
        }
        int start = bm.getHeight() - height;
        int startPos = (y) * this.width + (x);

        int init = 0;
        int end = 0;
        if (top) {
            init = start;
            end = start;
        }
        for (j = init; j < (height + end) * scale; j++) {
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
        x += xoffset;
        y += yoffset;
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

        if (scale == 1) {
            drawBitmap(bm, x, y);
            return;
        }
        if (x + bm.getWidth() * scale < 0 || y + bm.getHeight() * scale < 0 || x > width)
            return;

        x += xoffset;
        y += yoffset;

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
        x += xoffset;
        y += yoffset;
        if (x == 0 && y == 0 && bm.getWidth() == Game.getCurrent().getWidth() && bm.getHeight() == Game.getCurrent().getHeight()) {
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
        if (y - clipy + height > clipheight) {
            height = clipheight - y + clipy;
        }
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

    public void draw(Sprite sprite, int x, int y, float rad, float scale) {
        if (sprite == null) return;
        Bitmap bm = sprite.getBitmap();
        if (rad == 0) {
            drawBitmap(bm, x, y, scale);
            return;
        }
        // if(scale == 1) {
        // drawBitmapRotated(bm, x, y, rad);
        // return;
        // }
        x += xoffset;
        y += yoffset;
        if (x + bm.getWidth() < 0 || y + bm.getHeight() < 0 || x - bm.getWidth() > width)
            return;
        float sin = Util.sin(rad);
        float cos = Util.cos(rad);

        float step = Math.max(Math.max(Math.abs(sin), Math.abs(cos)) - .2f, 0.7f);
        float px = sprite.getRotationPivotX();
        float py = sprite.getRotationPivotY();
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
        x += xoffset;
        y += yoffset;
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

    public void fillColoredRect(float x, float y, int w, int h, Color c) {
        fillColoredRect(x, y, w, h, c.rgb);
    }

    public void fillColoredOval(float x, float y, int w, int h, Color c) {
        shapeRenderer.fillColoredOval(x, y, w, h, c.rgb);
    }

    public void drawColoredOval(float x, float y, int w, int h, Color c) {
        shapeRenderer.drawColoredOval(x, y, w, h, c.rgb);
    }


    private void fillColoredRect(float xf, float yf, int width, int height, int color) {
        int x = (int) xf + xoffset;
        int y = (int) yf + yoffset;
        if (x + width < 0 || y + height < 0 || x > this.width)
            return;
        if (y < clipy) {
            height = height - clipy + y;
            y = clipy;
        }
        int i = 0;
        int j = 0;
        int maxi = width;
        if (x < 0)
            i = -x;
        if (x + width > this.width)
            maxi = this.width - x;
        int startPos = (y) * this.width + (x);
        if (y - clipy + height > clipheight) {
            height = clipheight - y + clipy;
        }
        //TODO clip width..
        for (j = 0; j < height; j++) {
            for (int ii = i; ii < maxi; ii++) {
                putPixel(startPos + ii, color);
            }
            startPos += this.width;
            if (startPos > pixels.length)
                return;
        }
    }

    public void drawColoredRect(float x, float y, int width, int height, Color c) {
        drawColoredRect(x, y, width, height, c.rgb);
    }

    private void drawColoredRect(float xf, float yf, int width, int height, int color) {
        int x = (int) xf + xoffset;
        int y = (int) yf + yoffset;
        if (x + width < 0 || y + height < 0 || x > this.width)
            return;
        if (y < clipy) {
            height = height - clipy + y;
            y = clipy;
        }

        int startPos = y * this.width + x;
        int ix = 0;
        int widthi = width;
        if (x < 0)
            ix = -x;
        if (x + width > this.width)
            widthi = this.width - x;
        if (y - clipy + height > clipheight) {
            height = clipheight - y + clipy;
        }

        //TODO clip width
        if (y < clipy + clipheight && y + height > clipy)
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

    public void drawPixel(int x, int y, Color color) {
        drawPixel(x, y, color.rgb);
    }

    void drawPixel(int x, int y, int color) {
        putPixel(x + y * width, color);
    }

    private void putPixel(int index, int color) {
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

    public void drawParticle(Particle p) {
        if (p.getX() < 0 || p.getY() < 0 || p.getX() >= width || p.getY() >= height)
            return;
        pixels[(int) p.getX() + (int) p.getY() * width] = p.getColor().rgb;
    }

    public void fill(Color c) {
        Util.intfill(pixels, c.rgb);
    }

    public void setClearColor(Color color) {
        Arrays.fill(clearPixels, color.rgb);
    }

    public void translate(float x, float y) {
        xoffset += x;
        yoffset += y;
    }

    public void setClip(int x, int y, int width, int height) {
        clipx = x;
        clipy = y;
        clipwidth = width;
        clipheight = height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }
}
