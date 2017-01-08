import java.io.IOException;

import com.misty.engine.Game;
import com.misty.engine.graphics.Bitmap;
import com.misty.engine.graphics.Color;
import com.misty.engine.graphics.Group;
import com.misty.engine.graphics.Renderer;
import com.misty.engine.graphics.UI.Button;
import com.misty.engine.graphics.UI.CheckBox;
import com.misty.engine.graphics.UI.Label;
import com.misty.engine.graphics.UI.Slider;
import com.misty.engine.graphics.UI.Table;
import com.misty.engine.graphics.UI.TextField;
import com.misty.engine.graphics.UI.builders.SliderBuilder;
import com.misty.engine.graphics.font.Font;


public class Test extends Game {

	Bitmap tiles;
	Bitmap props;
	float time = 0;
	CheckBox cb;
	Label testLabel;
	Table table;
	private int red, green, blue;
	private int bx, by;
	int val = 0;

	public Test(String name, int width, int height, int scale) {
		super(name, width, height, scale);
		setClearColor(new Color(0xff43fbde));
		setCursorImage("res/Gold_Cursor.png");
		setFont(Font.bold);
		try {
			tiles = new Bitmap("res/tiles.png");
			props = new Bitmap("res/props.png");
		} catch (IOException e) {
			e.printStackTrace();
		}
		Button bb = new Button("Click me");
		bb.setTitleColor(0xff43fe43);
		bb.addButtonListener(() -> {
			bb.setText("clicked this yo");
		});
		bb.setPosition(140, 140);

		
		cb = new CheckBox(140, 165);

		testLabel = new Label("Hello", 152, 165);
		
		add(testLabel);

		cb.addCheckListener(checked -> {
			testLabel.setText("The checkbox is now " + (checked ? "on" : "off"));
		});
		Slider redSlider = new SliderBuilder().setDimensions(100, 10).setPosition(0, 0).setHighlightColor(Color.RED).create();
		redSlider.addSliderListener(e -> {
			red = (int) (e*255f);
			redSlider.setHighlightColor(new Color(0xff, red, 0, 0));

		});
		Slider greenSlider = new Slider(0, 11, 100, 10);
		greenSlider.addSliderListener(e -> {
			green = (int) (e*255f);
			greenSlider.setHighlightColor(new Color(0xff, 0, green, 0));

		});
		Slider blueSlider = new Slider(0, 22, 100, 10);
		blueSlider.addSliderListener(e -> {
			blue = (int) (e*255f);
			blueSlider.setHighlightColor(new Color(0xff, 0, 0, blue));
		});
		
		add(bb);
		add(cb);
		Group g = new Group();
		g.setPosition(220, 300);
		g.add(redSlider);
		g.add(greenSlider);
		g.add(blueSlider);
		blueSlider.setHighlighted(true);
		redSlider.setHighlighted(true);
		greenSlider.setHighlighted(true);
		//add(g);
		
		add(g);
		
		
		
		
		Button menuGo = new Button("Change color!");
		menuGo.setTitleColor(0xffba4354);
		menuGo.setPosition(100, 180);
		add(menuGo);
		menuGo.addButtonListener(() -> {
			menuGo.setText("clicked " + ++val);
			//setStage(menu);
		});
		
		
		table = new Table(220, 180);
		table.setFill(Table.FILL_STRETCH);
		add(table);
		table.setFixedHeight(97);
		table.setAllignment(Table.ALLIGN_CENTER);
		table.add(new CBWL("<- checkbox"));
		table.add(new Button("row1 is longer"));
		table.add(new LabelNTextField("Name: ", new TextField("John")));
		table.add(new Button("row2"));
		table.add(new Button("row3"));
		table.add(new LabelNTextField("sdf: ", new TextField("Hallo")));
		for(int i = 0; i < 200; i++) {
			Button b = new Button("row " + (i+4));
			int ind = i;
			b.addButtonListener(() -> menuGo.setText(ind + " is clicked"));
			table.add(b);
		}
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
	

	@Override
	public void mouseMoved(int mouseX, int mouseY) {
		// TODO Auto-generated method stub
		super.mouseMoved(mouseX, mouseY);
		//bx = mouseX;
		//by = mouseY;
	}



	public static void main(String[] args) {
		new Test("FPS Test", 400, 450, 1).start();
	}

	@Override
	public void draw(Renderer g) {
		g.fillColoredRect(bx, by, 100, 100, getCol());
	}

	private Color getCol() {
		//System.out.println(red + " " + green + " " + blue);
		return new Color(0xff, red, green, blue);
	}



	@Override
	public void update() {

	}

	@Override
	public void setup() {
	}
}
