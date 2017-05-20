package com.misty.engine;

import java.awt.event.*;

public class MyListener implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener, WindowListener {

    public Game game;

    MyListener(Game game) {
        this.game = game;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

        if (game.isRunning())
            game.addEvent(new MInputEvent(e, MInputEvent.MOUSE_ENTER));
        //game.mouseEntered(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (game.isRunning())
            game.addEvent(new MInputEvent(e, MInputEvent.MOUSE_EXIT));
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (game.isRunning())
            game.addEvent(new MInputEvent(e, MInputEvent.MOUSE_PRESS));
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (game.isRunning())
            game.addEvent(new MInputEvent(e, MInputEvent.MOUSE_RELEASE));
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (game.isRunning())
            game.addEvent(new MInputEvent(e, MInputEvent.KEY_PRESS));
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (game.isRunning())
            game.addEvent(new MInputEvent(e, MInputEvent.KEY_RELEASE));
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (game.isRunning())
            game.addEvent(new MInputEvent(e, MInputEvent.MOUSE_DRAG));
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (game.isRunning())
            game.addEvent(new MInputEvent(e, MInputEvent.MOUSE_MOVE));
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        game.windowClosing(e);
    }

    @Override
    public void windowClosed(WindowEvent e) {
        game.windowClosed(e);
    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (game.isRunning())
            game.addEvent(new MInputEvent(e, MInputEvent.MOUSE_WHEEL));

    }

    class MInputEvent {
        public static final int MOUSE_PRESS = 1;
        public static final int MOUSE_RELEASE = 2;
        public static final int MOUSE_DRAG = 4;
        public static final int MOUSE_MOVE = 5;
        public static final int MOUSE_ENTER = 6;
        public static final int MOUSE_EXIT = 7;
        public static final int MOUSE_WHEEL = 8;

        public static final int KEY_PRESS = 9;
        public static final int KEY_RELEASE = 10;

        public InputEvent event;
        public int type;

        public MInputEvent(InputEvent event, int type) {
            this.event = event;
            this.type = type;
        }
    }

}
