package com.misty.engine.graphics.UI;

import com.misty.engine.graphics.Color;
import com.misty.engine.graphics.Renderer;

public class VerticalSlider extends Slider {

    public VerticalSlider(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public VerticalSlider(int x, int y, int w, int h, float val) {
        super(x, y, w, h, val);
    }

    @Override
    public boolean onClickPressed(int x, int y) {
        if (y >= this.y + (1 - value) * (height - knobWidth) && y <= this.y + (1 - value) * (height - knobWidth) + knobWidth)
            dragging = true;
        return true;
    }

    @Override
    public boolean onDragged(int xpos, int ypos) {
        if (dragging) {
            float xoff = (ypos - knobWidth / 2 - this.y) / (height - knobWidth);
            value = 1 - xoff;
            if (value < 0) value = 0;
            if (value > 1) value = 1;
            listeners.forEach(e -> e.valueChanged(value));
        }
        return false;
    }

    @Override
    public void draw(Renderer r) {
        r.fillColoredRect(x, y, width, height, bgColor);
        Color knobColor = hovering ? hoveringKnob : unhoveringKnob;
        r.fillColoredRect(x, y + (1 - value) * (height - knobWidth - 2) + 1, width, knobWidth, knobColor);
        if (highlighted) {
            int hh = (int) ((value) * (height - knobWidth - 1));
            int startY = (int) (y + height * (1 - value) + (knobWidth + 1) * value);
            if (value < .5) {
                startY -= 1;
                hh += 1;
            }
            if (isMouseOver())
                r.fillColoredRect(x, startY, width, hh, highlightColorHover);
            else
                r.fillColoredRect(x, startY, width, hh, highlightColor);

        }
        r.drawColoredRect(x, y, width, height, Color.BLACK);

    }

}
