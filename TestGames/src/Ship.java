
import java.awt.Shape;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.misty.engine.graphics.Bitmap;
import com.misty.engine.graphics.GameObject;
import com.misty.engine.graphics.Renderer;
import com.misty.utils.Util;

public class Ship extends GameObject {
	private Bitmap img, shadow;

	public int playerID = -1;
	private final String imgURL = "res/ship.png";
	private final String shadowURL = "res/shipshadow.png";
	private float wobble = 0;
	private ConcurrentLinkedQueue<Bullet> bullets = new ConcurrentLinkedQueue<Bullet>();
	private int coolDownShot = 0;

	public Ship() {
		x = 100;
		y = 100;
		dx = 0;
		dy = 0;
		rotation = 0;
		setImages();
	}

	public void setImages() {
		try {
			img = new Bitmap(imgURL);
			shadow = new Bitmap(shadowURL);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Ship(int x, int y, float rot) {
		this.x = x;
		this.y = y;
		this.rotation = rot;
		this.dx = 0;
		this.dy = 0;
		setImages();
	}

	public void draw(Renderer graphics) {
		graphics.drawBitmap(img, (int) x, (int) (y + Util.sin(wobble)), rotation, 1f);
		Iterator<Bullet> it = bullets.iterator();
		while (it.hasNext()) {
			it.next().draw(graphics);
		}
		// graphics.drawString(playerID+"", (int)x, (int)y-5, 0xffffffff);
	}

	public void drawShadow(Renderer graphics) {
		graphics.drawBitmap(shadow, (int) (x + 5 - Util.sin(wobble)), (int) (y + 9 - 0), rotation, 1f);
	}

	public void update() {
		x += dx;
		y += dy;
		dx *= .95f;
		dy *= .95f;
		if (rotation > Math.PI * 2)
			rotation -= Math.PI * 2;
		if (rotation < -Math.PI * 2)
			rotation += Math.PI * 2;
		wobble += .05f;
		Iterator<Bullet> it = bullets.iterator();
		while (it.hasNext()) {
			Bullet b = it.next();
			if (b.isAlive())
				b.update();
			else
				it.remove();
		}
	}

	public void setRot(float rot) {
		this.rotation = rot;
	}

	public void thrust(float power) {
		dx += Util.cos((float) (rotation)) * power;
		dy += Util.sin((float) (rotation)) * power;
	}

	public void rot(float r) {
		rotation += r;
	}

	public int getX() {
		return (int) x;
	}

	public int getY() {
		return (int) y;
	}

	public float getWidth() {
		return img.getWidth();
	}

	public float getHeight() {
		return img.getHeight();
	}

	public void setPosition(int i, int j) {
		x = i;
		y = j;
	}

	public float getRot() {
		return rotation;
	}

	public boolean shoot() {
		if (coolDownShot-- < 0) {
			for (float r = 0; r <= 0 * Math.PI * 2; r += .2f)
				bullets.add(new Bullet(x + 7 + 7 * Util.cos((float) (rotation + r)),
						y + 7 + 7 * Util.sin((float) (rotation + r)), rotation + r));
			coolDownShot = 10;
			return true;
		}
		return false;
	}

	public void addShot(int x2, int y2) {
		bullets.add(new Bullet(x2 + 7 + 7 * Util.cos((float) (rotation)), y2 + 7 + 7 * Util.sin((float) (rotation)),
				rotation));

	}

	public ConcurrentLinkedQueue<Bullet> getBullets() {
		return bullets;
	}

	@Override
	public Shape getShape() {
		// TODO Auto-generated method stub
		return null;
	}
}
