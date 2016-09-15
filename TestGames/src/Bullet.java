import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.io.IOException;

import com.misty.engine.graphics.Bitmap;
import com.misty.engine.graphics.Renderer;
import com.misty.engine.graphics.Sprite;
import com.misty.utils.Util;

public class Bullet extends Sprite {
	private final static String url = "res/bullet.png";
	private static Bitmap img;
	private float dx, dy;
	private int duration;
	private AffineTransform rotTransform;
	static {
		try {
			img = new Bitmap(url);
		} catch (IOException e) {
			e.printStackTrace();
			img = null;
		}
	}
	public Bullet(float x, float y, float dir) {
		super(img, (int)x, y);

		setDir(dir);
		duration = 230;
		rotTransform = new AffineTransform();
		rotTransform.rotate(dir, img.getWidth()/2, img.getHeight()/2);
		shape = new Ellipse2D.Float(0, 0, 5, 5);
		//makeCollidable();
	}
	
	public void draw(Renderer g) {
		g.draw(this, (int)x, (int)y, rotation, 1f);
	}
	
	public void setDir(float dir) {
		this.rotation = dir;
		dx = 3*Util.cos((float)(dir));
		dy = 3*Util.sin((float)(dir));
	}
	public boolean isAlive() {
		return duration > 0;
	}
	public void update() {
		x+=dx;
		y+=dy;
		duration--;
	}

	@Override
	public Shape getShape() {
		AffineTransform af = new AffineTransform();
		//Rectangle r = new Rectangle(img.width, img.height);
		af.translate(x, y);
		af.concatenate(rotTransform);
		return af.createTransformedShape(shape);
	}

	public boolean onScreen(int width, int height) {
		return x >= 0 && y >= 0 && x < width && y < height;
	}

	

	

	
}
