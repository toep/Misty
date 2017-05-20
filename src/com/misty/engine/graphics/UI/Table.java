package com.misty.engine.graphics.UI;

import com.misty.engine.Game;
import com.misty.engine.graphics.Color;
import com.misty.engine.graphics.GameObject;
import com.misty.engine.graphics.Group;
import com.misty.engine.graphics.Renderer;
import com.misty.utils.Util;

import java.util.Iterator;

public class Table extends Group implements Scrollable, Clickable {

    private int nextY = 0;
    private Color backgroundColor = new Color(0xff212121);
    public final static int ALLIGN_LEFT = 2;
    public final static int ALLIGN_RIGHT = 3;
    public final static int ALLIGN_CENTER = 4;
    public final static int FILL_REGULAR = 2;
    public final static int FILL_STRETCH = 3;
    private int allignment = ALLIGN_LEFT;
    private int fill = FILL_REGULAR;
    private boolean fixedHeight = false;
    private int yScroll = 0;
    private boolean dragging = false;
    private boolean hovering = false;
    private final int sliderWidth = 10;
    private Color fillColor = new Color(0xff212121);
    private Color sliderBarColor = new Color(0xff434332);
    private boolean doFill = true;
    private float scrollIntensity = 4f;
    private boolean showSidebar = true;

    public Table(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setAllignment(int allignment) {
        this.allignment = allignment;
    }

    public void setFill(int fill) {
        this.fill = fill;
    }

    public void setFillBackground(boolean fillBack) {
        doFill = fillBack;
    }

    public void setShowSidebar(boolean showSidebar) {
        this.showSidebar = showSidebar;
    }

    /**
     * this decides how fast you can scroll through the table. default value is
     * 4. Higher is faster, negative reverses the scrolling order, 0 means no
     * scroll
     *
     * @param intensity
     */
    public void setScrollIntensity(float intensity) {
        this.scrollIntensity = intensity;
    }

    @Override
    public void add(GameObject gameObject) {
        int fh = height;
        super.add(gameObject);

        gameObject.setPosition(0, nextY - yScroll);
        nextY += gameObject.getHeight();
        width = Math.max(width, gameObject.getWidth() + sliderWidth);
        if (!fixedHeight)
            height = nextY;
        else
            height = fh;
        if (fill == FILL_STRETCH) {
            children.forEach(e -> e.setWidth(width - sliderWidth));
        }
        // gameObject.setWidth(width);
        fixAllignment();
    }

    //TODO
    private void insertAt(GameObject go, int index) {

    }

    public void scrollToBottom() {
        //int ty = 0 - yScroll + nextY - height;
        yScroll = nextY - height;

//					for (GameObject e : children)
//			e.setPosition(e.getX(), e.getY() - ty);
    }

    public void setFixedHeight(int height) {
        fixedHeight = true;
        this.height = height;
    }

    private void fixAllignment() {
        if (allignment == ALLIGN_LEFT) {
            children.forEach(e -> e.setPosition(0, e.getY()));
        } else if (allignment == ALLIGN_RIGHT) {
            children.forEach(e -> {
                e.setPosition(width - sliderWidth - e.getWidth(), e.getY());
            });
        } else if (allignment == ALLIGN_CENTER) {
            int cx = (width - sliderWidth) / 2;
            children.forEach(e -> {
                e.setPosition(cx - e.getWidth() / 2, e.getY());
            });
        }
    }

    @Override
    public void draw(Renderer r) {
        r.setClip((int) x, (int) y, width, height);
        if (doFill)
            r.fillColoredRect(x, y, width, height, fillColor);
        // System.out.println(children.get(0).getY());
        Game.getCurrent().getRenderer().translate(x, y);
        Iterator<GameObject> it = children.iterator();
        while (it.hasNext()) {
            it.next().draw(r);
        }

        Game.getCurrent().getRenderer().translate(-x, -y);
        // r.drawColoredRect(x, y, width, height, 0xff434332);
        r.resetClip();
        if (showSidebar)
            r.fillColoredRect(x + width - sliderWidth, y, 10, height, sliderBarColor);
        if (shouldScroll()) {
            int yPos = Util.map(yScroll, 0, nextY - height, 0, height - 10);
            r.fillColoredRect(x + width - sliderWidth, y + yPos, 10, 10, Color.WHITE);
            r.drawColoredRect(x + width - sliderWidth + 1, y + yPos + 1, 10 - 2, 10 - 2, Color.GRAY);

        }

    }

    private boolean shouldScroll() {
        return nextY > height && nextY != 0;
    }

    @Override
    public void onScroll(int ty) {
        if (shouldScroll())

            ty *= scrollIntensity;
        if (!hovering)
            return;

        yScroll += ty;
        if (yScroll < 0) {
            ty = ty - yScroll;
            yScroll = 0;
        }
        if (yScroll > nextY - height) {
            ty = ty - yScroll + nextY - height;
            yScroll = nextY - height;
        }
        for (GameObject e : children)
            e.setPosition(e.getX(), e.getY() - ty);


    }

    @Override
    public boolean isPressed() {
        return dragging;
    }

    @Override
    public boolean onClickPressed(int x, int y) {
        if (x > this.x + width - sliderWidth && nextY - height != 0) {
            int yPos = Util.map(yScroll, 0, nextY - height, 0, height - 10);
            if (y >= yPos + this.y && y <= yPos + this.y + sliderWidth) {
                dragging = true;
            }
        }
        return false;
    }

    @Override
    public boolean onClickReleased(int x, int y) {
        dragging = false;
        return false;
    }

    @Override
    public boolean onDragged(int x, int y) {
        if (!dragging)
            return false;
        if (shouldScroll()) {
            int nYs = (int) (y - this.y) - sliderWidth / 2;
            if (nYs < 0)
                nYs = 0;
            if (nYs > height - sliderWidth)
                nYs = height - sliderWidth;
            int oldYscroll = yScroll;
            yScroll = Util.map(nYs, 0, height - 10, 0, nextY - height);
            for (GameObject g : children) {
                g.setPosition(g.getX(), g.getY() + oldYscroll - yScroll);
            }
        }
        return false;
    }

    @Override
    public void onHoverEnter() {
        hovering = true;
    }

    @Override
    public void onHoverExit() {
        hovering = false;

    }

    @Override
    public boolean isMouseOver() {
        return hovering;
    }

    @Override
    public void onclickReleasedOutside() {
        dragging = false;
    }

    @Override
    public void onClickOutside() {
    }

}
