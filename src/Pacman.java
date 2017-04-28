import java.io.IOException;

import com.misty.engine.Game;
import com.misty.engine.graphics.Animation;
import com.misty.engine.graphics.Bitmap;
import com.misty.engine.graphics.Renderer;
import com.misty.engine.graphics.Sprite;
import com.misty.engine.graphics.Stage;
import com.misty.engine.graphics.UI.Button;
import com.misty.engine.graphics.UI.Label;
import com.misty.engine.graphics.font.Font;

public class Pacman extends Game {

	private Stage menuStage, gameStage;
	private Animation pacAni;
	private PacPerson pac;
	public Pacman(String name, int width, int height, int scale) {
		super(name, width, height, scale);
		setFont(Font.regularFont);
		menuStage = createMenuStage();
		gameStage = createGameStage();
		
		add(menuStage);
		add(gameStage);
		setStage(menuStage);
	}

	private Stage createGameStage() {
		Stage st = new Stage();
		st.setPosition(0, 24);
		Sprite map;
		try {
			map = new Sprite("res/map.png");
			map.setPosition(0, 0);
			st.add(map);
			pac = new PacPerson();
			TopBar bar = new TopBar();
			pac.setOnPointGot(() -> bar.incScore());
			st.add(bar);
			st.add(pac);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return st;
	}

	private Stage createMenuStage() {
		Stage st = new Stage();
		Button startButton = new Button("Start");
		startButton.setPosition(width/2-startButton.getWidth()/2, height/2);
		startButton.addButtonListener(() -> {
			setStage(gameStage);
		});
		
		Label welcome = new Label("Pacman", width/2-4*8, height/4);
		st.add(welcome);
		
		Animation ani;
		try {
			ani = new Animation("res/pacman_animation.png", 128, 64);
			ani.setPosition(50, 75);
			ani.setFPS(20);
			st.add(ani);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		st.add(startButton);
		
		return st;
	}
	
	@Override
	public void keyPressed(int keyCode) {
		super.keyPressed(keyCode);
		pac.keyPressed(keyCode);
	}

	@Override
	public void draw(Renderer g) {
	}

	@Override
	public void update() {
	}

	
	
	public static void main(String[] args) {
		new Pacman("Pacman", 224, 248+24, 3).start();
	}

	@Override
	public void setup() {
	}
}
