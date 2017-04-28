import java.awt.Shape;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.misty.engine.Game;
import com.misty.engine.graphics.Animation;
import com.misty.engine.graphics.Bitmap;
import com.misty.engine.graphics.Color;
import com.misty.engine.graphics.GameObject;
import com.misty.engine.graphics.Renderer;
import com.misty.listeners.Keys;

public class PacPerson extends GameObject {

	private Animation pac;
	private Bitmap path;
	private Direction direction = Direction.RIGHT;
	private Direction nextDirection = direction;
	private int curx = 1, cury = 1;
	private int targetx, targety;
	private boolean[][] pellets;
	private Runnable onPoint;
	
	private Color peletColor = new Color(0xffaeaeae);
	private enum Direction {
		UP, DOWN, LEFT, RIGHT;
	}

	public PacPerson() {

		
		try {

			path = new Bitmap("res/path.png");
			pellets = new boolean[path.getWidth()][path.getHeight()];
			for(int i = 0; i < path.getWidth(); i++) {
				for(int j = 0; j < path.getHeight(); j++) {
					if(path.getRGB(i, j) == 0xff4800ff) {
						pellets[i][j] = true;
					}
				}
			}
			pac = new Animation("res/pac_sides.png", 13, 13);
			setCoord(1, 1);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void setCoord(int x, int y) {
		this.x = x * 8 - 2;
		this.y = y * 8 - 2;
		pac.setPosition(this.x, this.y);

	}

	@Override
	public Shape getShape() {
		return null;
	}

	@Override
	public void draw(Renderer r) {
		if(direction == Direction.LEFT) {
			pac.setRotation((float)Math.PI);
		}
		if(direction == Direction.RIGHT) {
			pac.setRotation(0);
		}
		if(direction == Direction.DOWN) {
			pac.setRotation((float)(Math.PI/2f));
		}
		if(direction == Direction.UP) {
			pac.setRotation((float)(3f*Math.PI/2f));
		}
		drawPellets(r);
		pac.draw(r);
	}

	private void drawPellets(Renderer r) {
		for(int i = 0; i < path.getWidth(); i++) {
			for(int j = 0; j < path.getHeight(); j++) {
				if(pellets[i][j]) {
					r.drawColoredRect(i*8+3, j*8+3, 3, 3, peletColor);
				}
			}
		}
	}

	public void eat(int x, int y) {
		if(pellets[x][y] == true) {
			onPointGot(onPoint);
			pellets[x][y] = false;
		}
	}
	@Override
	public void update() {
		pac.update();
		if (direction == Direction.RIGHT) {
			if (path.getRGB(curx + 1, cury) != 0xff000000) {
				setTarget(curx + 1, cury);
				// setCoord(curx+1, cury);
			}
			if (targetx * 8 != (int) x + 2) {
				x += 1;

			} else {
				curx = targetx;
				eat(curx, cury);
				if(canMoveInNextDir())
					direction = nextDirection;
			}
		} else if (direction == Direction.LEFT) {
			if (path.getRGB(curx - 1, cury) != 0xff000000) {
				setTarget(curx - 1, cury);
				// setCoord(curx+1, cury);
			}
			if (targetx * 8 != (int) x + 2) {
				x -= 1;

			} else {
				curx = targetx;
				eat(curx, cury);
				if(canMoveInNextDir())
					direction = nextDirection;
			}
		} else if (direction == Direction.UP) {
			if (path.getRGB(curx , cury-1) != 0xff000000) {
				setTarget(curx, cury-1);
				// setCoord(curx+1, cury);
			}
			if (targety * 8 != (int) y + 2) {
				y -= 1;

			} else {
				cury = targety;
				eat(curx, cury);

				if(canMoveInNextDir())
					direction = nextDirection;

			}
		} else if (direction == Direction.DOWN) {
			if (path.getRGB(curx, cury+1) != 0xff000000) {
				setTarget(curx, cury+1);
				// setCoord(curx+1, cury);
			}
			if (targety * 8 != (int) y + 2) {
				y += 1;

			} else {
				cury = targety;
				eat(curx, cury);

				if(canMoveInNextDir())
					direction = nextDirection;

			}
		}

		if(direction == Direction.LEFT && x < 0) {
			x = Game.getCurrent().getWidth()-2;
			
			setTarget((int)x/8, cury);
			curx = targetx;
		}
		else if(direction == Direction.RIGHT && x >= Game.getCurrent().getWidth()-pac.getWidth()-3) {
			
			x = -13;
			setTarget((int)x/8, cury);
			curx = targetx;

		}
		
		pac.setPosition(x, y);

	}

	private void setTarget(int x, int y) {
		targetx = x;
		targety = y;
	}

	public boolean canMoveInNextDir() {
		if(nextDirection == Direction.RIGHT)
			return path.getRGB(targetx+1, targety) != 0xff000000;
		else if(nextDirection == Direction.LEFT)
			return path.getRGB(targetx-1, targety) != 0xff000000;
		else if(nextDirection == Direction.UP)
			return path.getRGB(targetx, targety-1) != 0xff000000;
		else if(nextDirection == Direction.DOWN)
			return path.getRGB(targetx, targety+1) != 0xff000000;
		return false;
	}
	public void keyPressed(int key) {
		if (key == Keys.LEFT && path.getRGB(targetx-1, targety) != 0xff000000)
			nextDirection = Direction.LEFT;
		else if (key == Keys.RIGHT && path.getRGB(targetx+1, targety) != 0xff000000)
			nextDirection = Direction.RIGHT;
		else if (key == Keys.UP && path.getRGB(targetx, targety-1) != 0xff000000)
			nextDirection = Direction.UP;
		else if (key == Keys.DOWN && path.getRGB(targetx, targety+1) != 0xff000000)
			nextDirection = Direction.DOWN;

	}

	public void onPointGot(Runnable c) {
		if(c != null)
			c.run();
	}

	public void setOnPointGot(Runnable r) {
		onPoint = r;
	}

}
