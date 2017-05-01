import java.io.IOException;
import java.text.NumberFormat;

import com.misty.engine.Game;
import com.misty.engine.graphics.Animation;
import com.misty.engine.graphics.Bitmap;
import com.misty.engine.graphics.Color;
import com.misty.engine.graphics.Group;
import com.misty.engine.graphics.Particle;
import com.misty.engine.graphics.ParticleEmitter;
import com.misty.engine.graphics.Renderer;
import com.misty.engine.graphics.UI.Button;
import com.misty.engine.graphics.UI.CheckBox;
import com.misty.engine.graphics.UI.Label;
import com.misty.engine.graphics.UI.Slider;
import com.misty.engine.graphics.UI.Table;
import com.misty.engine.graphics.UI.TextField;
import com.misty.engine.graphics.UI.VerticalSlider;
import com.misty.engine.graphics.UI.builders.SliderBuilder;
import com.misty.engine.graphics.font.Font;
import com.misty.utils.Util;


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
	Animation ani;
	
	public static void main(String[] args) {
		new Test("FPS Test", 400, 450, 2).start();
	}
	
	@Override
	public void mousePressed(int x, int y) {
		super.mousePressed(x, y);
	}
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
		bb.setTitleColor(new Color(0xff43fe43));
		bb.addButtonListener(() -> {
			bb.setText("clicked this yo");
		});
		bb.setPosition(90, 140);

		
		cb = new CheckBox(140, 165);

		testLabel = new Label("Hello", 155, 165);
		testLabel.setColor(Color.MAGENTA);
		
		add(testLabel);
		cb.addCheckListener(checked -> {
			testLabel.setText("The checkbox is now " + (checked ? "on" : "off"));
		});
		Slider redSlider = new SliderBuilder().setDimensions(100, 10).setPosition(0, 0).setHighlightColor(Color.RED).setStartValue(.23f).createHorizontal();
		redSlider.addSliderListener(e -> {
			red = (int) (e*255f);
			redSlider.setHighlightColor(new Color(0xff, red, 0, 0));

		});
		Slider greenSlider = new SliderBuilder().setDimensions(100, 10).setPosition(0, 11).setHighlightColor(Color.GREEN).setStartValue(.53f).createHorizontal();
		greenSlider.addSliderListener(e -> {
			green = (int) (e*255f);
			greenSlider.setHighlightColor(new Color(0xff, 0, green, 0));

		});
		Slider blueSlider = new SliderBuilder().setDimensions(100, 10).setPosition(0, 22).setHighlightColor(Color.BLUE).setStartValue(.73f).createHorizontal();
		blueSlider.addSliderListener(e -> {
			blue = (int) (e*255f);
			blueSlider.setHighlightColor(new Color(0xff, 0, 0, blue));
		});
		red = (int) (redSlider.getValue()*255);
		green = (int) (greenSlider.getValue()*255);
		blue = (int) (blueSlider.getValue()*255);

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
		
		
		
		ParticleEmitter pe = new ParticleEmitter(120, 250);

		Button menuGo = new Button("Change color!");
		menuGo.setTitleColor(new Color(0xffba4354));
		menuGo.setPosition(222, 340);
		add(menuGo);
		menuGo.addButtonListener(() -> {
			//setStage(menu);
			pe.setColor(getCol());
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
			table.add(b);
		}
		//setStage(menu);
		try {
			ani = new Animation("res/simple_player_anim.png", 125, 125);
			ani.setPosition(260, 20);
			ani.setFPS(24);
			Slider aniSlider = new SliderBuilder().setDimensions(140, 20).setPosition(250, 139).setHighlightColor(Color.GOLD).setStartValue(.24f).create();
			Label aniLabel = new Label("24 fps");
			ParticleEmitter peAni = new ParticleEmitter(320,  25);
			aniSlider.addSliderListener(e -> {
				ani.setFPS((int)(e*100));
				aniLabel.setText((int)(e*100) + " fps");
				peAni.setSpeed(e*5f);
			});
			peAni.setParticleType(Particle.MOTION_DIRECTIONAL);
			Particle.setDirection(0);
			add(peAni);
			aniLabel.setColor(Color.BLACK);
			aniLabel.setPosition(285,  145);
			
			add(aniSlider);
			add(aniLabel);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		add(ani);
		
		VerticalSlider vs = new VerticalSlider(30, 220, 20, 130);
		Label sliderLabel = new Label("0", 32, 355);
		sliderLabel.setColor(Color.BLACK);
		pe.setParticleType(Particle.MOTION_RANDOM1);
		pe.setParticleLifetime(40);
		pe.setSpeed(1.4f);
		sliderLabel.setZ(2);
		vs.setHighlightColor(Color.BLUE);
		vs.addSliderListener(e -> {
			pe.setParticleCount((int)(e*1000));
			sliderLabel.setText(((int)(e*100))+"");
		});
		add(sliderLabel);
		add(vs);
		add(pe);
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



	

	@Override
	public void draw(Renderer g) {
		g.fillColoredRect(330, 300, 58, 33, getCol());
		g.drawColoredRect(330, 300, 58, 33, Color.BLACK);

		int cw = (int) (10*Util.cos(tick*12f));
		int ch = (int) (10*Util.sin(tick*12f));
		int w = (int) (20);
		int h = (int) (20);
		int ovalx = 140;
		int ovaly = 340;
		g.fillColoredOval(ovalx-w/2+cw*2, ovaly-h/2+ch*2, w, h, Color.MAROON);
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
