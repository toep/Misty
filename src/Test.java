import java.awt.Color;
import java.io.IOException;

import com.misty.engine.Game;
import com.misty.engine.graphics.Bitmap;
import com.misty.engine.graphics.Group;
import com.misty.engine.graphics.Renderer;
import com.misty.engine.graphics.Stage;
import com.misty.engine.graphics.UI.Button;
import com.misty.engine.graphics.UI.CheckBox;
import com.misty.engine.graphics.UI.Label;
import com.misty.engine.graphics.UI.Slider;
import com.misty.engine.graphics.font.Font;


public class Test extends Game {

	Bitmap tiles;
	Bitmap props;
	float time = 0;
	CheckBox cb;
	Label testLabel;
	private int red, green, blue;
	
	public Test(String name, int width, int height, int scale) {
		super(name, width, height, scale);
		setClearColor(0xfffe8243);
		setCursorImage("res/Gold_Cursor.png");
		try {
			tiles = new Bitmap("res/tiles.png");
			props = new Bitmap("res/props.png");
		} catch (IOException e) {
			e.printStackTrace();
		}
		Button bb = new Button("Click me", 20);

		bb.addButtonListener(() -> {
			bb.setText("clicked this yo");
		});
		bb.setPosition(140, 140);

		Button b3 = new Button();
		b3.setColor(0xff4312fe);
		b3.setPressedColor(0xffaefe32);
		b3.setPosition(100, 100);
		b3.setText("This text is set after");
		cb = new CheckBox(140, 165);

		testLabel = new Label("Hello", 152, 168);
		
		add(testLabel);
		Stage menu = new Stage();

		cb.addCheckListener(checked -> {
			testLabel.setText("The checkbox is now " + (checked ? "on" : "off"));
		});
		Slider redSlider = new Slider(0, 0, 100, 5);
		redSlider.addSliderListener(e -> {
			red = (int) (e*255f);
		});
		Slider greenSlider = new Slider(0, 10, 100, 5);
		greenSlider.addSliderListener(e -> {
			green = (int) (e*255f);
		});
		Slider blueSlider = new Slider(0, 20, 100, 5);
		blueSlider.addSliderListener(e -> {
			blue = (int) (e*255f);
		});
		
		add(bb);
		add(b3);
		add(cb);
		Group g = new Group();
		g.setPosition(220, 250);
		g.add(redSlider);
		g.add(greenSlider);
		g.add(blueSlider);
		blueSlider.setHighlighted(true);
		//add(g);
		
		menu.add(g);
		
		Button menuGoBack = new Button("Done");
		menuGoBack.setPosition(100, 180);
		menu.add(menuGoBack);
		menuGoBack.addButtonListener(() -> {
			setStage(null);
		});
		add(menu);
		
		Button menuGo = new Button("Change color");
		menuGo.setPosition(100, 180);
		add(menuGo);
		menuGo.addButtonListener(() -> {
			setStage(menu);
		});
		
		//setStage(menu);
		
	}

	

	



	public static void main(String[] args) {
		new Test("FPS Test", 400, 450, 2).start();
	}

	@Override
	public void draw(Renderer g) {
		g.fillColoredRect(100, 200, 100, 100, getCol());
	}

	private int getCol() {
		//System.out.println(red + " " + green + " " + blue);
		return new Color(red, green, blue, 0xff).getRGB();
	}



	@Override
	public void update() {

	}
}
