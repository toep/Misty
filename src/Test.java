import java.io.IOException;

import com.misty.engine.Game;
import com.misty.engine.graphics.Bitmap;
import com.misty.engine.graphics.Color;
import com.misty.engine.graphics.Renderer;
import com.misty.engine.graphics.UI.Button;

public class Test extends Game {
	
	Bitmap tiles;
	Bitmap props;
	float time = 0;
	public Test(String name, int width, int height, int scale) {
		super(name, width, height, scale);
		setFPSLimit(false);
		setClearColor(0xfffe8243);
		try {
			tiles = new Bitmap("res/tiles.png");
			props = new Bitmap("res/props.png");
		} catch (IOException e) {
			e.printStackTrace();
		}
		Button b = new Button("Click me");
		
		b.addButtonListener(() -> {
			b.setText("clicked this yo");
		}); 
		b.setPosition(140, 140);
		Button b2 = new Button("Click me 22");
		b2.setPosition(760, 145);
		addObject(b);
		addObject(b2);
	}

	
	


	public static void main(String[] args) {
		new Test("FPS Test", 800, 450, 2).start();
	}
	
	@Override
	public void draw(Renderer g) {
		g.drawBitmap(tiles, 0, 0, 0, 1.5f);
		g.drawBitmap(props, 0, 0);
	}


	@Override
	public void update() {
		
	}
}
