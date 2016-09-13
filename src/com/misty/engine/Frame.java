package com.misty.engine;
import java.awt.Dimension;

import javax.swing.JFrame;

public class Frame extends JFrame {

	private static final long serialVersionUID = 1L;

	public Frame(int w, int h) {
		Dimension dim = new Dimension(w, h);
		this.setSize(dim);
		this.setPreferredSize(dim);
		this.setVisible(true);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setTitle("Frame");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
}
