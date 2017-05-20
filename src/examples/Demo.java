package examples;

import com.misty.engine.Game;
import com.misty.engine.graphics.Bitmap;
import com.misty.engine.graphics.Color;
import com.misty.engine.graphics.Particle;
import com.misty.engine.graphics.Renderer;
import com.misty.engine.graphics.UI.Button;
import com.misty.engine.graphics.UI.TextField;
import com.misty.utils.Util;

import java.io.IOException;

public class Demo extends Game {


    Bitmap mask;


    @Override
    public void setup() {
        try {
            mask = new Bitmap("res/circleMask.png");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Button b = new Button("Click me", 150, 150);
        TextField num = new TextField("30", 150, 138);
        b.addButtonListener(() -> {
            int count = Integer.valueOf(num.getText());
            for (int i = 0; i < count; i++) {
                actionQueue.add(() -> {
                    addParticle(new Particle(width / 2, height / 2, Particle.MOTION_RANDOM_RETURNING, 3f, 120, Color.ORANGE.darken().darken()));
                    addParticle(new Particle(width / 2, height / 2, Particle.MOTION_RANDOM_ACCELERATING_OUT, .3f, 140, Color.YELLOW));
                });
            }
            b.setText("Clicked");
        });
        add(b);

        add(num);
    }

    @Override
    public void draw(Renderer g) {
        //g.drawBitmap(mask, 0, 0);
        g.drawString(particles.size() + " Particles alive", width / 2 - 70, 10);
        int x = 200;
        int y = 200;
        int xo = 80;
        int yo = 30;
        for (int i = 0; i < 100; i++) {
            g.drawPixel(x + xo + (int) (x * Util.cos(i / (100 / (Math.PI * 2)))), y + yo + (int) (y * Util.sin(i / (100 / (Math.PI * 2)))), Color.random());
        }

        g.fillColoredOval(mouseX, mouseY, 80, 50, Color.BLUE);
        //g.drawString("Hello world", mouseX, mouseY, Color.BLUE);
        //g.drawColoredRect(mouseX+10*Util.cos(tick*15), mouseY+10*Util.sin(tick*15), 20, 20, Color.GREEN);
    }

    @Override
    public void update() {

    }


    public Demo(String name, int width, int height, int scale) {
        super(name, width, height, scale);
    }

    public static void main(String[] args) {
        int width = 565;
        int height = 437;
        int scale = 1;
        String title = "Title";
        new Demo(title, width, height, scale).start();
    }
}
