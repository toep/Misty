package examples;

import com.misty.engine.Game;
import com.misty.engine.graphics.*;
import com.misty.engine.graphics.UI.Button;
import com.misty.engine.graphics.font.Font;
import com.misty.engine.tilemap.TMXParser;
import com.misty.engine.tilemap.TileMap;
import com.misty.listeners.Keys;

import java.io.IOException;

public class TestDemo extends Game {

    String sample = "Welcome to my game";
    int yOffset = 100;
    int x, y;
    Animation ani;
    Sprite background;
    TileMap testMap;
    ParticleEmitter emitter;

    Stage mainMenu;

    public TestDemo(int width, int height, int scale) {
        super("TileMap examples.Demo", width, height, scale);
        setClearColor(new Color(0xff87CEEB));
        setCursorImage("res/cursor.png");
        setFPSLimit(false);
        try {
            //we create an animation with each frame being 32x32 pixels
            ani = new Animation("res/playerAni.png", 32, 32);
            //rock = new Sprite("res/rock_big.png", 200, 200);
            background = new Sprite("res/background.png", 0, 0);
            //this creates a tilemap that can be drawn just by adding it as addObject(testMap)
            testMap = TMXParser.createTileMap("res/polimap.tmx");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        //this specifies the drawing order of the objects, higher number gets drawn on top
        background.setZ(0);
        testMap.setZ(3);
        ani.setZ(4);
        ani.setPosition(300, 300);
        add(ani);
        add(testMap);
        add(background);

        mainMenu = new Stage();
        Button start = new Button("Click to start");
        start.setPosition(width / 2 - start.getWidth() / 2, height / 2 - start.getHeight() / 2);
        start.addButtonListener(() -> {
            setStage(null);
            clearParticles();
        });
        mainMenu.add(start);
        emitter = new ParticleEmitter(width / 2, height / 2);
        mainMenu.add(emitter);


        add(mainMenu);

        setStage(mainMenu);
    }


    public static void main(String[] args) {
        new TestDemo(576, 448, 2).start();
    }

    @Override
    public void draw(Renderer g) {

        //two fonts currently, this is how you set them
        g.setFont(Font.regularFont);

        g.drawString(sample, 5, yOffset, Color.WHITE, 1f);
        g.setFont(Font.defaultFont);

    }


    @Override
    public void update() {
        if (isKeyDown(Keys.RIGHT)) testMap.move(-4, 0);
        if (isKeyDown(Keys.LEFT)) testMap.move(4, 0);
        if (isKeyDown(Keys.UP)) testMap.move(0, 4);
        if (isKeyDown(Keys.DOWN)) testMap.move(0, -4);
        ani.setPosition(mouseX, mouseY);
        emitter.setPosition(mouseX, mouseY);
    }

    public void keyPressed(int key) {
        if (key == Keys.ESC) {
            setStage(mainMenu);
        }
    }

    @Override
    public void mouseWheelMoved(int wheelRotation) {
        yOffset -= wheelRotation * 5;
        ani.setScale(3f);
        ani.setRotation(yOffset / 50f);
    }


    @Override
    public void setup() {
    }
}
