package com.misty.engine.tilemap;

import java.awt.Shape;

import com.misty.engine.Game;
import com.misty.engine.graphics.GameObject;
import com.misty.engine.graphics.Renderer;

public class TileMap extends GameObject {
	private Tileset tileset;
	private int[][] data;
	@SuppressWarnings("unused")
	private int width, height;
	
	public TileMap() {
		
	}

	public TileMap(Tileset[] tss, int[][] data2, int width, int height) {
		Tileset ts = Tileset.concat(tss);
		tileset = ts;
		this.width = width;
		this.height = height;
		data = data2;
	}

	@Override
	public Shape getShape() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void draw(Renderer r) {
		Tileset ts = tileset;
		for(int d = 0; d < data.length; d++) {
			int startI = (int) Math.max(((-x/ts.width)-Math.min(0, (int)(y/ts.height))*width), 0);
			int wCount = 0;
			int hCount = 0;
			for(int i = startI; i < data[d].length; i++) {
				if(data[d][i] != 0)
					r.drawBitmap(ts.bms[data[d][i]-1], (int)x+((i%width)*ts.width), (int)y+(i/width)*ts.height);
				wCount++;
				if(wCount > Game.getCurrent().getWidth()/ts.width+1) {
					i+=width-wCount;
					wCount = 0;
					hCount++;
					
					if(hCount > Game.getCurrent().getHeight()/ts.height+1)
						break;
				}
				
			}
		}
		
	}

	@Override
	public void update() {
		
	}

	public void move(int i, int j) {
		x+=i;
		y+=j;
	}
}
