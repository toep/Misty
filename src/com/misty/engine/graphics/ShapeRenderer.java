package com.misty.engine.graphics;

class ShapeRenderer {

	private Renderer renderer;
	private int width;
	private int height;

	public ShapeRenderer(Renderer renderer, int width, int height) {
		this.renderer = renderer;
		this.width = width;
		this.height = height;
	}

	public void fillColoredOval(float x, float y, int w, int h, int c) {
		int h2 = h / 2;
		int w2 = w / 2;
		float fh2 = h / 2f;
		float fw2 = w / 2f;
		for (int i = 0; i <= h2; i++) {
			float p = i / fh2;
			float specialVal = (float) Math.sqrt(1.0 - p * p);
			int xpos = (int) (x + w - fw2 * (specialVal));
			int xpos2 = (int) (w - xpos + 2f * x - 1);
		
			int end = (int) (xpos2 + w2);
			if (end >= width)
				end = width - 1;
			int start = (int) (xpos - w2);
			if (start < 0)
				start = 0;
			int lower = (int) (y + i + h2);
			int upper = (int) (h + y - i - h2 - 1);

			if (upper >= 0 && upper < height)
				for (int xx = start; xx <= end; xx++) {
					renderer.drawPixel(xx, upper, c);
				}
			if (lower >= 0 && lower < height)
				for (int xx = start; xx <= end; xx++) {

					renderer.drawPixel(xx, lower, c);
				}

			
		}

	}

	public void drawColoredOval(float x, float y, int w, int h, int c) {
		int h2 = h / 2;
		int w2 = w / 2;
		float fh2 = h / 2f;
		float fw2 = w / 2f;
		for (int i = 0; i <= h2; i++) {
			float p = i / fh2;
			float specialVal = (float) Math.sqrt(1.0 - p * p);
			
			int xpos = (int) (x + w - fw2 * (specialVal));
			int xpos2 = (int) (w - xpos + 2f * x - 1);
	
			int end = (int) (xpos2 + w2);
			if (end >= width)
				end = width - 1;
			int start = (int) (xpos - w2);
			if (start < 0)
				start = 0;
			int lower = (int) (y + i + h2);
			int upper = (int) (h + y - i - h2 - 1);

			renderer.drawPixel(start, upper, c);
			renderer.drawPixel(end, upper, c);
			renderer.drawPixel(start, lower, c);
			renderer.drawPixel(end, lower, c);

		}

	}

}
