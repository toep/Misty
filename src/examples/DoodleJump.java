package examples;

import com.misty.engine.Game;
import com.misty.engine.graphics.*;
import com.misty.engine.graphics.UI.Label;
import com.misty.utils.Util;

import java.io.IOException;
import java.util.ArrayList;

public class DoodleJump extends Game {

    Doodle doodle;
    Sprite bg;
    Bitmap brick_bm;
    Group movingThings;
    Label score_label;
    int score = 0;
    private int curBrickyPosInc = 0;
    ArrayList<Brick> bricks = new ArrayList<Brick>();

    public DoodleJump(String name, int width, int height, int scale) throws IOException {
        super(name, width, height, scale);
        doodle = new Doodle("res/doodle_right.png");
        bg = new Sprite("res/doodle_bg.png");
        brick_bm = new Bitmap("res/brick.png");
        score_label = new Label("Score: " + score, 0, 10);
        score_label.setColor(Color.PURPLE);
        score_label.setScale(3);
        score_label.setZ(5);
        bg.setScale(1.21f);
        doodle.setZ(3);
        add(bg);
        add(score_label);
        movingThings = new Group();

        movingThings.add(doodle);
        //add(doodle);
        int yPos = height - 50;
        for (int i = 0; i < 30; i++)
            bricks.add(new Brick(brick_bm, Util.random(0, width - brick_bm.getWidth()), yPos -= Util.random(40, 120)));
        curBrickyPosInc = yPos;
        bricks.forEach(e -> movingThings.add(e));

        add(movingThings);

    }

    @Override
    public void draw(Renderer g) {
    }

    @Override
    public void update() {

        doodle.update(bricks);
        if (doodle.getY() < 400 && movingThings.getY() < -doodle.getY() + 400) {
            movingThings.setPosition(movingThings.getX(), -doodle.getY() + 400);
        }
        bricks.forEach(e -> {
            if (e.getY() + movingThings.getY() > Game.getCurrent().getHeight()) {
                e.setPosition(Util.random(0, width - brick_bm.getWidth()), curBrickyPosInc -= Util.random(40, 120));
            }
        });
        score_label.setText("Score: " + movingThings.getY() / 10);
    }


    public static void main(String[] args) throws IOException {
        new DoodleJump("examples.Doodle Jump", 640, 960, 1).start();
    }

    @Override
    public void setup() {
    }
}
