package com.misty.engine.graphics;

import com.misty.engine.Game;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class Group extends GameObject {

    protected ArrayList<GameObject> children = new ArrayList<GameObject>();
    private boolean contained = false;

    public boolean isContained() {
        return contained;
    }

    public void contain() {
        contained = true;
    }

    public Group() {

    }

    public Group(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void add(GameObject gameObject) {
        if (gameObject == this)
            return;
        if (gameObject instanceof Group) {
            if (((Group) gameObject).isContained())
                return;
            ((Group) gameObject).contain();
        }
        children.add(gameObject);
        Collections.sort(children);

        pack();
    }

    private void pack() {
        // int x2 = 0, y2 = 0;
        for (GameObject g : children) {
            width = Math.max(width, g.getX() + g.getWidth());
            height = Math.max(height, g.getY() + g.getHeight());
        }
        // width = x2;
        // height = y2;
    }

    public ArrayList<GameObject> getChildren() {
        return children;
    }


    @Override
    public Shape getShape() {
        return null;
    }

    @Override
    public void draw(Renderer r) {
        if (enabled) {
            Game.getCurrent().getRenderer().translate(x, y);
            children.forEach(e -> e.draw(r));
            Game.getCurrent().getRenderer().translate(-x, -y);
        }
    }

    @Override
    public void update() {
        if (enabled) {
            children.forEach(GameObject::update);
        }
    }
}
