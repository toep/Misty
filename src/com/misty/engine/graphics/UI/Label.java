package com.misty.engine.graphics.UI;

import com.misty.engine.Game;
import com.misty.engine.graphics.Color;
import com.misty.engine.graphics.GameObject;
import com.misty.engine.graphics.Renderer;

import java.awt.*;

public class Label extends GameObject {

    protected String str;
    protected Color color = Color.WHITE;

    public Label(String str, int x, int y) {
        this.x = x;
        this.y = y;
        setText(str);
        this.height = Game.getCurrent().getRenderer().getCurrentFont().getCharacterHeight() + 2;
    }

    public void setText(String str) {
        this.str = str;
        this.width = str.length() * Game.getCurrent().getRenderer().getCurrentFont().getCharacterWidth();
    }

    public String getText() {
        return str;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Label() {
        this("", 0, 0);
    }

    public Label(String str) {
        this(str, 0, 0);
    }

    @Override
    public Shape getShape() {
        return null;
    }

    @Override
    public void draw(Renderer r) {
        r.drawString(str, (int) x, (int) y + 2, color, scale);
    }

    @Override
    public void update() {
    }


}
