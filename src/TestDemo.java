
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.misty.engine.Game;
import com.misty.engine.graphics.Animation;
import com.misty.engine.graphics.Renderer;
import com.misty.engine.graphics.Sprite;
import com.misty.engine.graphics.font.Font;
import com.misty.engine.tilemap.TMXParser;
import com.misty.engine.tilemap.TileMap;
import com.misty.listeners.Keys;

public class TestDemo extends Game {

	String sample = "Welcome to my game";
	int yOffset = 10;
	int x, y;
	Animation ani;
	Sprite background;
	TileMap testMap;
	public TestDemo(int width, int height, int scale) {
		super("TileMap Demo", width, height, scale);
		setClearColor(0xff87CEEB);
		try {
			//we create an animation with each frame being 32x32 pixels
			ani = new Animation("res/playerAni.png", 32, 32);
			//rock = new Sprite("res/rock_big.png", 200, 200);
			background = new Sprite("res/background.png", 0, 0);
			//this creates a tilemap that can be drawn just by adding it as addObject(testMap)
			testMap = TMXParser.createTileMap("res/polimap.tmx");
		} catch (IOException | ParserConfigurationException | SAXException e) {
			e.printStackTrace();
			System.exit(0);
		}
		//this specifies the drawing order of the objects, higher number gets drawn on top
		background.setZ(0);
		testMap.setZ(3);
		ani.setZ(3);
		ani.setPosition(300, 300);
		addObject(ani);
		addObject(testMap);
		addObject(background);
		start();
	}

	public static void main(String[] args) {
		new TestDemo(576, 448, 2);
	}
	
	@Override
	public void draw(Renderer g) {
		
		//two fonts currently, this is how you set them
		g.setFont(Font.regularFont);

		g.drawString(sample, 5, yOffset, 0xffffffff, 1f);
		g.setFont(Font.defaultFont);

	}
	
	@Override
	public void update() {
		//tst.update();
		if(isKeyDown(Keys.RIGHT)) testMap.move(-4, 0);
		if(isKeyDown(Keys.LEFT)) testMap.move(4, 0);
		if(isKeyDown(Keys.UP)) testMap.move(0, 4);
		if(isKeyDown(Keys.DOWN)) testMap.move(0, -4);
		super.update();
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		yOffset -= e.getWheelRotation()*5;
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		//this is how I get the animation to follow the mouse
		ani.setPosition(e.getX()/scale, e.getY()/scale);
		
	}
}
