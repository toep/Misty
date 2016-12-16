package com.misty.engine;
import java.awt.Dimension;

import javax.swing.JFrame;

public class Frame extends JFrame {

	private static final long serialVersionUID = 1L;

	public Frame() {
		
	}
	
	public void pack() {
		this.setResizable(false);
		super.pack();
		this.setVisible(true);
		Dimension dim = getSize();
		this.setSize(dim);
		this.setPreferredSize(dim);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
}
