package com.misty.engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class MyListener implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener, WindowListener {

	public Game game;

	public MyListener(Game game) {
		this.game = game;
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
		if (game.isRunning())
			game.mouseEntered(e);
	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (game.isRunning())
			game.mousePressed(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (game.isRunning())
			game.mouseReleased(e);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (game.isRunning())
			game.keyPressed(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (game.isRunning())
			game.keyReleased(e);
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (game.isRunning())
			game.mouseDragged(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		
		if (game.isRunning())
			game.mouseMoved(e);
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
			game.mouseWheelMoved(e);

	}

}
