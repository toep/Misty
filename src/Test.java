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
import com.misty.engine.graphics.UI.Table;
import com.misty.engine.graphics.UI.TextField;
import com.misty.engine.graphics.font.Font;


public class Test extends Game {

	Bitmap tiles;
	Bitmap props;
	float time = 0;
	CheckBox cb;
	Label testLabel;
	Table table;
	private int red, green, blue;
	
	public Test(String name, int width, int height, int scale) {
		super(name, width, height, scale);
		setClearColor(0xff43fbde);
		setCursorImage("res/Gold_Cursor.png");
		setFont(Font.bold);
		try {
			tiles = new Bitmap("res/tiles.png");
			props = new Bitmap("res/props.png");
		} catch (IOException e) {
			e.printStackTrace();
		}
		Button bb = new Button("Click me", 20);
		bb.setTitleColor(0xff43fe43);
		bb.addButtonListener(() -> {
			bb.setText("clicked this yo");
		});
		bb.setPosition(140, 140);

		Button b3 = new Button();
		b3.setColor(0xff4312fe);
		b3.setPressedColor(0xffaefe32);
		b3.setPosition(100, 103);
		b3.setText("This text is set after");
		cb = new CheckBox(140, 165);

		testLabel = new Label("Hello", 152, 165);
		
		add(testLabel);
		Stage menu = new Stage();

		cb.addCheckListener(checked -> {
			testLabel.setText("The checkbox is now " + (checked ? "on" : "off"));
		});
		Slider redSlider = new Slider(0, 0, 100, 10);
		redSlider.addSliderListener(e -> {
			red = (int) (e*255f);
			redSlider.setHighlightColor(new Color(e, 0, 0).getRGB());

		});
		Slider greenSlider = new Slider(0, 11, 100, 10);
		greenSlider.addSliderListener(e -> {
			green = (int) (e*255f);
			greenSlider.setHighlightColor(new Color(0, e, 0).getRGB());

		});
		Slider blueSlider = new Slider(0, 22, 100, 10);
		blueSlider.addSliderListener(e -> {
			blue = (int) (e*255f);
			blueSlider.setHighlightColor(new Color(0, 0, e).getRGB());
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
		redSlider.setHighlighted(true);
		greenSlider.setHighlighted(true);
		//add(g);
		
		menu.add(g);
		
		Button menuGoBack = new Button("Done");
		menuGoBack.setPosition(100, 180);
		menu.add(menuGoBack);
		menuGoBack.addButtonListener(() -> {
			setStage(null);
		});
		addStage(menu);
		
		Button menuGo = new Button("Change color!");
		menuGo.setTitleColor(0xffba4354);
		menuGo.setPosition(100, 180);
		add(menuGo);
		menuGo.addButtonListener(() -> {
			setStage(menu);
		});
		
		
		table = new Table(220, 180);
		table.setFill(Table.FILL_STRETCH);
		add(table);
		table.setFixedHeight(97);
		table.setAllignment(Table.ALLIGN_CENTER);
		table.add(new CBWL("Send nudes"));
		table.add(new Button("row1 is longer"));
		table.add(new LabelNTextField("Name: ", new TextField("Hallo")));
		table.add(new Button("row2", 40));
		table.add(new Button("row3", 80));
		table.add(new LabelNTextField("sdf: ", new TextField("Hallo")));
		for(int i = 0; i < 200; i++) table.add(new Button("row " + (i+4)));
		//setStage(menu);
		
	}

	class CBWL extends Group {
		public CBWL(String str) {
			add(new CheckBox());
			add(new Label(str, 12, 0));
		}
	}
	class LabelNTextField extends Group {
		public LabelNTextField(String str, TextField tf) {
			Label label = new Label(str);
			add(label);
			tf.setPosition(label.getWidth(), 0);
			add(tf);
		}
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
