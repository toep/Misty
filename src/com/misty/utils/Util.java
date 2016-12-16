package com.misty.utils;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Random;

import javax.imageio.ImageIO;

import com.misty.engine.graphics.Bitmap;

public class Util {

	private static float[] sin = new float[4196];
	private static float PI_2 = (float) (2 * Math.PI);
	private static float PIO2 = (float) (Math.PI / 2);
	private static int[] ids = new int[16384];
	private static int idsIndex = 0;
	static {
		for (int i = 0; i < sin.length; i++) {
			sin[i] = (float) Math.sin((double) (i * 2.0f * Math.PI / sin.length));
		}
		for (int i = 0; i < ids.length; i++) {
			ids[i] = i;
		}
		shuffleArray(ids);
	}

	private static void shuffleArray(int[] ar) {
		Random rnd = new Random();
		for (int i = ar.length - 1; i > 0; i--) {
			int index = rnd.nextInt(i + 1);
			// Simple swap
			int a = ar[index];
			ar[index] = ar[i];
			ar[i] = a;
		}
	}

	public static void bytefill(byte[] array, byte value) {
		int len = array.length;

		if (len > 0) {
			array[0] = value;
		}

		for (int i = 1; i < len; i += i) {
			System.arraycopy(array, 0, array, i, ((len - i) < i) ? (len - i) : i);
		}
	}

	public static void intfill(int[] array, int value) {
		int len = array.length;

		if (len > 0) {
			array[0] = value;
		}

		for (int i = 1; i < len; i += i) {
			System.arraycopy(array, 0, array, i, ((len - i) < i) ? (len - i) : i);
		}
	}

	public static float sin(float rad) {
		while (rad < 0)
			rad += PI_2;
		int index = (int) ((rad / PI_2) * (sin.length - 1));
		return sin[index % sin.length];
	}

	public static float cos(float rad) {
		return sin(rad + PIO2);
	}

	public static float tan(float rad) {
		return sin(rad) / cos(rad);
	}

	public static float sin(double rad) {
		return sin((float) rad);
	}

	public static float cos(double rad) {
		return sin((float) (rad + PIO2));
	}

	public static float tan(double rad) {
		return sin((float) rad) / cos(rad);
	}

	public static String getString(ByteBuffer bb, int length) {
		byte[] str = new byte[length];
		bb.get(str, 0, length);
		return new String(str);
	}

	public static Bitmap createBitmapFromFile(String filename) throws IOException {
		BufferedImage bi = ImageIO.read(new File(filename));
		Bitmap mp = new Bitmap(bi);
		return mp;
	}
	
	public static Bitmap createBitmapFromStream(InputStream in) throws IOException {
		BufferedImage bi = ImageIO.read(in);
		Bitmap mp = new Bitmap(bi);
		return mp;
	}
	
	public static BufferedImage getBufferedImageFromFile(String filename) throws IOException {
		return ImageIO.read(new File(filename));
	}

	public static Bitmap[] bitmapsFromSheet(Bitmap bm, int w, int h) {
		if (bm.getWidth() % w != 0 || bm.getHeight() % h != 0) {
			// the size doesnt fit the sheet
			return null;
		}
		int numInX = bm.getWidth() / w;
		int numInY = bm.getHeight() / h;
		Bitmap[] result = new Bitmap[numInX * numInY];
		Bitmap cur;
		for (int ii = 0; ii < result.length; ii++) {
			cur = new Bitmap(w, h);
			int[] pix = cur.pixels;
			int startX = (ii % numInX) * w;
			int startY = (ii / numInX) * h;
			int index = startX + startY * bm.getWidth();

			for (int j = 0; j < cur.getHeight(); j++) {
				for (int i = 0; i < cur.getWidth(); i++) {
					int pi = bm.pixels[index + i];
					pix[j * cur.getWidth() + i] = pi;
				}
				index += bm.getWidth();
			}
			result[ii] = cur;
			result[ii].determineTransparency();
		}

		return result;
	}

	public static int randomUniqueID() {
		return ids[idsIndex++];
	}

	public static void printBB(ByteBuffer bb) {
		System.out.print("[");
		for (int i = 0; i < bb.limit() - 1; i++) {
			System.out.print(bb.array()[i] + ", ");
		}
		System.out.println(bb.array()[bb.limit() - 1] + "]");
	}

	public static String splitStringAtWord(String sample, int space) {
		String res = "";
		int i;
		int len = space;
		for (i = 0; i + space + 1 < sample.length(); i += len) {
			len = sample.substring(i, i + space + 1).lastIndexOf(" ");
			if (len <= 0)
				len = space;
			if (len < space / 2) {
				len = space + 1;
				res += sample.substring(i, i + len).trim() + "-\n";
			} else {
				res += sample.substring(i, i + len).trim() + "\n";
			}
			// System.out.println(len + ", " + i);
		}
		res += sample.substring(i);
		return res;
	}

	public static Point[] makePoly(Bitmap bm, int d, int angle) {

		// creates an outline of a transparent image, points are stored in an
		// array
		// arg0 - BufferedImage source image
		// arg1 - Int detail (lower = better)
		// arg2 - Int angle threshold in degrees (will remove points with angle
		// differences below this level; 15 is a good value)
		// making this larger will make the body faster but less accurate;

		int w = bm.getWidth();
		int h = bm.getHeight();

		// increase array size from 255 if needed
		int[] vertex_x = new int[1024], vertex_y = new int[1024], vertex_k = new int[1024];

		int numPoints = 0, tx = 0, ty = 0, fy = -1, lx = 0, ly = 0;
		vertex_x[0] = 0;
		vertex_y[0] = 0;
		vertex_k[0] = 1;
		for (tx = 0; tx < w; tx += d)
			for (ty = 0; ty < h; ty += 1)
				if ((bm.getRGB(tx, ty) >> 24) != 0x00) {
					vertex_x[numPoints] = tx;
					vertex_y[numPoints] = h - ty;
					vertex_k[numPoints] = 1;
					numPoints++;
					if (fy < 0)
						fy = ty;
					lx = tx;
					ly = ty;
					break;
				}
		for (ty = 0; ty < h; ty += d)
			for (tx = w - 1; tx >= 0; tx -= 1)
				if ((bm.getRGB(tx, ty) >> 24) != 0x00 && ty > ly) {
					vertex_x[numPoints] = tx;
					vertex_y[numPoints] = h - ty;
					vertex_k[numPoints] = 1;
					numPoints++;
					lx = tx;
					ly = ty;
					break;
				}
		for (tx = w - 1; tx >= 0; tx -= d)
			for (ty = h - 1; ty >= 0; ty -= 1)
				if ((bm.getRGB(tx, ty) >> 24) != 0x00 && tx < lx) {
					vertex_x[numPoints] = tx;
					vertex_y[numPoints] = h - ty;
					vertex_k[numPoints] = 1;
					numPoints++;
					lx = tx;
					ly = ty;
					break;
				}
		for (ty = h - 1; ty >= 0; ty -= d)
			for (tx = 0; tx < w; tx += 1)
				if ((bm.getRGB(tx, ty) >> 24) != 0x00 && ty < ly && ty > fy) {
					vertex_x[numPoints] = tx;
					vertex_y[numPoints] = h - ty;
					vertex_k[numPoints] = 1;
					numPoints++;
					lx = tx;
					ly = ty;
					break;
				}
		double ang1, ang2;
		for (int i = 0; i < numPoints - 2; i++) {
			ang1 = PointDirection(vertex_x[i], vertex_y[i], vertex_x[i + 1], vertex_y[i + 1]);
			ang2 = PointDirection(vertex_x[i + 1], vertex_y[i + 1], vertex_x[i + 2], vertex_y[i + 2]);
			if (Math.abs(ang1 - ang2) <= angle)
				vertex_k[i + 1] = 0;
		}
		ang1 = PointDirection(vertex_x[numPoints - 2], vertex_y[numPoints - 2], vertex_x[numPoints - 1],
				vertex_y[numPoints - 1]);
		ang2 = PointDirection(vertex_x[numPoints - 1], vertex_y[numPoints - 1], vertex_x[0], vertex_y[0]);
		if (Math.abs(ang1 - ang2) <= angle)
			vertex_k[numPoints - 1] = 0;
		ang1 = PointDirection(vertex_x[numPoints - 1], vertex_y[numPoints - 1], vertex_x[0], vertex_y[0]);
		ang2 = PointDirection(vertex_x[0], vertex_y[0], vertex_x[1], vertex_y[1]);
		if (Math.abs(ang1 - ang2) <= angle)
			vertex_k[0] = 0;
		int n = 0;
		for (int i = 0; i < numPoints; i++)
			if (vertex_k[i] == 1)
				n++;
		Point[] poly = new Point[n];
		n = 0;
		for (int i = 0; i < numPoints; i++)
			if (vertex_k[i] == 1) {
				poly[n] = new Point();
				poly[n].x = vertex_x[i];
				poly[n].y = h - vertex_y[i];
				n++;
			}
		return poly;
	}

	private static double PointDirection(double xfrom, double yfrom, double xto, double yto) {
		return Math.atan2(yto - yfrom, xto - xfrom) * 180 / Math.PI;
	}

	

}
