import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;

import com.misty.engine.Game;
import com.misty.engine.graphics.Bitmap;
import com.misty.engine.graphics.Renderer;
import com.misty.engine.graphics.Sprite;
import com.misty.listeners.Keys;

public class Doodle extends Sprite {

	private float gy = .25f;
	private Bitmap flipped, original;
	public Doodle(String string) throws IOException {
		super(string);
		x = Game.getCurrent().getWidth()/2;
		y = Game.getCurrent().getHeight()-height;
		flipped = bitmap.getFlipY();
		original = bitmap;
	
	}
	
	@Override
	public void draw(Renderer r) {
		super.draw(r);
		if(x < 0) {
			r.drawBitmap(bitmap, (int)(Game.getCurrent().getWidth()+x), (int)y);
		}
		if(x+width > Game.getCurrent().getWidth()) {
			r.drawBitmap(bitmap, (int)(x-Game.getCurrent().getWidth()), (int)y);

		}
	}
	
	@Override
	public void update() {
		dy+=gy;
		y+=dy;
		if(dy > 0 && y > Game.getCurrent().getHeight()-height) {
			bounce();
		}
		
		
		if(Game.getCurrent().isKeyDown(Keys.LEFT)) {
			dx-=.5f;
			bitmap = flipped;
		}
		if(Game.getCurrent().isKeyDown(Keys.RIGHT)) {
			dx+=.5f;
			bitmap = original;
		}
		
		x+=dx;
		dx*=.93f;
		if(x < -width) {
			x+=Game.getCurrent().getWidth();
		}
		if(x > Game.getCurrent().getWidth()) {
			x = 0;
		}
	}

	private void bounce() {
		dy = -10f;
	}
	
	public Rectangle collisionRectangle() {
		return new Rectangle((int)x+30, (int)y+65, 50, 15);
	}

	public void update(ArrayList<Brick> bricks) {
		if(dy > 0)
		bricks.forEach(e -> {
			if(intersects(e)) {
				bounce();
			}
		});
	}
	

}
